package com.pragmasphere.oika.automator.commands.identifiants;

import com.pragmasphere.oika.automator.commands.auth.Auth;
import com.pragmasphere.oika.automator.fluentlenium.ReunionScript;
import com.pragmasphere.oika.automator.fluentlenium.data.FicheClient;
import com.pragmasphere.oika.automator.fluentlenium.data.Reunion;
import com.pragmasphere.oika.automator.persistence.PersistenceBackend;
import com.pragmasphere.oika.automator.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.table.Table;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.pragmasphere.oika.automator.custom.TableUtils.buildTable;

@Component
@Slf4j
public class IdentifiantsCommand implements CommandMarker {

    @Autowired
    private PersistenceBackend backend;

    @Autowired
    private SecurityService security;

    @CliCommand(value = "identifiants", help = "Génère le fichier Word des identifiants clients")
    public Table accesFromRegroupement(
            @CliOption(key = "", mandatory = true, help = "Hôte(sse) de la réunion ou ID Technique de la réunion (affiché dans "
                    + "la barre d'adresse)")
            final String[] hotesOrReunionIds) throws InvalidFormatException, IOException {

        final Auth auth = backend.get(Auth.class, null);
        if (auth == null) {
            throw new IllegalStateException("[!] Login/Password non définis. Utiliser la commande <config auth>");
        }
        auth.setPassword(security.decrypt(auth.getPassword()));

        Identifiants identifiants = backend.get(Identifiants.class, null);
        if (identifiants == null) {
            identifiants = new Identifiants();
        }

        final File templateFile = new File(identifiants.getTemplate());
        if (!templateFile.exists()) {
            log.info("[*] Le fichier modèle {} n'existe pas", templateFile);

            final InputStream inputStream = getClass().getResourceAsStream("modele-identifiants.docx");
            try {
                FileUtils.copyInputStreamToFile(inputStream, templateFile);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            backend.persist(identifiants);
            backend.flush();

            log.info("[*] Création du fichier modèle par défaut {}", templateFile);
        }
        log.info("[*] Vous pouvez modifier le fichier modèle {} pour personnaliser les fiches identifiants", templateFile);
        final XWPFDocument templateDoc = new XWPFDocument(OPCPackage.open(templateFile));

        final File outputFile = new File(identifiants.getOutput());

        // On a besoin de passer par un fichier Temporaire car POI plante lorsque ou écrit sur le meme fichier que le fichier
        // ouvert
        final File tmpOutputFile = File.createTempFile(outputFile.getName(), ".tmp");
        tmpOutputFile.delete();

        if (!outputFile.exists()) {
            log.info("[*] Les fiches identifiants seront ajoutées dans le fichier {}", outputFile);
            FileUtils.copyFile(templateFile, outputFile);
        } else {
            log.info("[*] Les fiches identifiants seront ajoutées à la suite des fiches existantes dans le fichier {}",
                    outputFile);
        }
        XWPFDocument doc = new XWPFDocument(OPCPackage.open(outputFile));

        final List<Reunion> reunions = new ArrayList<>();
        for (final String hoteOrReunionId : hotesOrReunionIds) {
            final ReunionScript clientsReunionScript = new ReunionScript(auth, hoteOrReunionId);
            clientsReunionScript.run();
            final Reunion reunion = clientsReunionScript.getReunionData();
            reunions.add(reunion);
        }

        final LinkedHashSet<FicheClient> clients = reunions.stream().flatMap(r -> r.getRegroupements().stream())
                .flatMap(r -> r.getFactures().stream()).map(f -> f.getFicheClient()).filter(f -> f != null)
                .collect(Collectors.toCollection(() -> new LinkedHashSet<>()));

        List<FicheClient> clientsRestants = new ArrayList<>(clients);

        while (clientsRestants.size() > 0) {
            int count = IdentifiantsPoi.fillClient(doc, clientsRestants);
            if (count == 0) {
                IdentifiantsPoi.appendTemplate(doc, templateDoc);

                // Reload the workbook, workaround for bug 49940
                // https://issues.apache.org/bugzilla/show_bug.cgi?id=49940
                doc.write(new FileOutputStream(tmpOutputFile));
                doc.close();
                FileUtils.copyFile(tmpOutputFile, outputFile);
                doc = new XWPFDocument(OPCPackage.open(outputFile));

                count = IdentifiantsPoi.fillClient(doc, clientsRestants);
            }
            if (count == 0) {
                throw new IllegalStateException("Problème dans le modèle Word des identifiants");
            }
            clientsRestants = new ArrayList<>(clientsRestants.subList(count, clientsRestants.size()));

            doc.write(new FileOutputStream(tmpOutputFile));
            doc.close();
            FileUtils.copyFile(tmpOutputFile, outputFile);
            doc = new XWPFDocument(OPCPackage.open(outputFile));
        }

        doc.close();
        FileUtils.copyFile(tmpOutputFile, outputFile);
        log.info("[+] Ecriture des identifiants dans le fichier {}", outputFile);
        tmpOutputFile.delete();

        return buildTable(new ArrayList<>(clients), "nom", "prenom", "id", "password");
    }

    @CliCommand(value = "config identifiants", help = "Configure la génération du fichier Word des identifiants clients")
    public Table auth(
            @CliOption(key = "template", mandatory = true, help = "Chemin d'accès vers le modèle (DOCX)")
            final String template,
            @CliOption(key = "output", mandatory = true, help = "Chemin d'accès vers le fichier de sortie (DOCX)")
            final String output) {

        final Identifiants identifiants = new Identifiants(template, output);

        backend.persist(identifiants);
        backend.flush();

        return buildTable(Arrays.asList(identifiants), "template", "output");
    }
}