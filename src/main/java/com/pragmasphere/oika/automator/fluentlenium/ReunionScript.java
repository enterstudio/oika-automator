package com.pragmasphere.oika.automator.fluentlenium;

import com.pragmasphere.oika.automator.commands.auth.Auth;
import com.pragmasphere.oika.automator.fluentlenium.configuration.FluentScript;
import com.pragmasphere.oika.automator.fluentlenium.data.FicheClient;
import com.pragmasphere.oika.automator.fluentlenium.data.Regroupement;
import com.pragmasphere.oika.automator.fluentlenium.data.Reunion;
import com.pragmasphere.oika.automator.fluentlenium.po.ClientPage;
import com.pragmasphere.oika.automator.fluentlenium.po.ListeReunionsPage;
import com.pragmasphere.oika.automator.fluentlenium.po.MonEspaceReserve;
import com.pragmasphere.oika.automator.fluentlenium.po.ReunionPage;
import com.pragmasphere.oika.automator.fluentlenium.po.TableauDeBordPage;
import lombok.extern.slf4j.Slf4j;
import org.fluentlenium.core.annotation.Page;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnhandledAlertException;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ReunionScript extends FluentScript {
    private final Auth auth;
    private final String hoteOrReunionId;

    @Page
    private MonEspaceReserve espaceReserve;

    @Page
    private TableauDeBordPage tableauDeBord;

    @Page
    private ListeReunionsPage listeReunions;

    @Page
    private ReunionPage reunion;

    @Page
    private ClientPage client;

    private Reunion reunionData;

    public ReunionScript(final Auth auth, final String hoteOrReunionId) {
        this.auth = auth;
        this.hoteOrReunionId = hoteOrReunionId;
    }

    @Override
    protected void doRun() {
        espaceReserve.go();

        log.info("[*] Connexion {}/********", auth.getLogin());
        espaceReserve.login(auth);

        tableauDeBord.isAt();

        try {
            Integer.parseInt(hoteOrReunionId);
            log.info("[*] Affichage de la réunion {}", hoteOrReunionId);
            goTo("/Reunion.php5?ID=" + hoteOrReunionId);
        } catch (final NumberFormatException e) {
            log.info("[*] Affichage de la liste des réunions");
            tableauDeBord.menu.reunions().click();

            log.info("[*] Recherche de la dernière réunion de l'hôte(sse) {}", hoteOrReunionId);
            listeReunions.lastReunion(hoteOrReunionId);
        }

        reunion.isAt();

        log.info("[*] Recherche des regroupements et factures associées à la réunion");
        final List<Regroupement> regroupements = reunion.getRegroupements();
        log.info("[+] {} factures trouvées dans {} regroupements",
                regroupements.stream().flatMap(r -> r.getFactures().stream()).count(), regroupements.size());

        final Reunion reunionData = new Reunion();
        reunionData.setId(reunion.getId());
        reunionData.setTechnicalId(reunion.getTechnicalId());
        reunionData.setRegroupements(regroupements);

        final LinkedHashSet<String> codeClients = regroupements.stream()
                .flatMap(regroupement -> regroupement.getFactures().stream().sequential()).map(facture -> facture.getClientId())
                .collect(Collectors.toCollection(() -> new LinkedHashSet<>()));

        log.info("[+] {} clients trouvés", codeClients.size());
        final Map<String, FicheClient> clients = new LinkedHashMap<>();
        for (final String codeClient : codeClients) {
            client.goToClient(codeClient);
            if (getDriver().getCurrentUrl().endsWith("Client.php5?REF=" + codeClient)) {
                FicheClient ficheClient;
                try {
                    ficheClient = client.getFicheClient();
                } catch (UnhandledAlertException | NullPointerException e) {
                    // Workaround à cause d'un bug du site oikaoika qui provoque une alerte.
                    alert().dismiss();
                    ficheClient = client.getFicheClient();
                }

                clients.put(codeClient, ficheClient);
                log.info("[+] {} {} ({}/{})", ficheClient.getNom(), ficheClient.getPrenom(), ficheClient.getId(),
                        ficheClient.getPassword());
            } else {
                log.info("[!] Compte non client ignoré ({})", codeClient);
            }

        }

        regroupements.stream().flatMap(r -> r.getFactures().stream())
                .forEach(f -> f.setFicheClient(clients.get(f.getClientId())));

        this.reunionData = reunionData;

        log.info("[*] Chargement terminé");
    }

    public Reunion getReunionData() {
        return reunionData;
    }
}
