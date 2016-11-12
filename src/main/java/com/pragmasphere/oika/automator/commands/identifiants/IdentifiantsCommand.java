package com.pragmasphere.oika.automator.commands.identifiants;

import com.pragmasphere.oika.automator.commands.auth.Auth;
import com.pragmasphere.oika.automator.fluentlenium.ClientsReunionScript;
import com.pragmasphere.oika.automator.fluentlenium.data.FicheClient;
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
import java.util.List;

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
            @CliOption(key = "", mandatory = true, help = "Hôte(sse) de la réunion")
            final String hote) throws InvalidFormatException, IOException {

        final Auth auth = backend.get(Auth.class, null);
        if (auth == null) {
            throw new IllegalStateException("[!] Login/Password non définis. Utiliser la commande <config auth>");
        }

        Identifiants identifiants = backend.get(Identifiants.class, null);
        if (identifiants == null) {
            identifiants = new Identifiants("modele-identifiants.docx");
        }

        final File templateFile = new File(identifiants.getPath());
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

        auth.setPassword(security.decrypt(auth.getPassword()));

        final ClientsReunionScript clientsReunionScript = new ClientsReunionScript(auth, hote);
        clientsReunionScript.run();

        final List<FicheClient> clients = clientsReunionScript.getClients();
        List<FicheClient> clientsRestants = clientsReunionScript.getClients();
        int i = 0;

        while (clientsRestants.size() > 0) {
            i++;
            final XWPFDocument doc = new XWPFDocument(OPCPackage.open(identifiants.getPath()));
            final int count = IdentifiantsPoi.fillClient(doc, clientsRestants);
            clientsRestants = new ArrayList<>(clientsRestants.subList(count, clientsRestants.size()));
            doc.write(new FileOutputStream(hote + (i > 1 ? "_" + i : "") + ".docx"));
        }

        return buildTable(clients, "nom", "prenom", "id", "password");
    }

    @CliCommand(value = "config identifiants", help = "Configure la génération du fichier Word des identifiants clients")
    public Table auth(
            @CliOption(key = "path", mandatory = true, help = "Chemin d'accès vers le modèle (DOCX)")
            final String path) {

        final Identifiants identifiants = new Identifiants(path);

        backend.persist(identifiants);
        backend.flush();

        return buildTable(Arrays.asList(identifiants), "path");
    }
}