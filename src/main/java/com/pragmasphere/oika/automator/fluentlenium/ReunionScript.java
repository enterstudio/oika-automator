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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    protected void runScript() {
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

        log.info("[*] Recherche des regroupements et factures associées à la réunion");
        final List<Regroupement> regroupements = reunion.getRegroupements();
        log.info("[+] {} factures trouvées dans {} regroupements",
                regroupements.stream().flatMap(r -> r.getFactures().stream()).count(), regroupements.size());

        final Reunion reunionData = new Reunion();
        reunionData.setId(reunion.getId());
        reunionData.setTechnicalId(reunion.getTechnicalId());
        reunionData.setRegroupements(regroupements);

        final Set<String> codeClients = regroupements.stream().flatMap(regroupement -> regroupement.getFactures().stream())
                .map(facture -> facture.getClientId()).collect(Collectors.toSet());

        log.info("[+] {} clients trouvés", codeClients.size());
        final Map<String, FicheClient> clients = new LinkedHashMap<>();
        for (final String codeClient : codeClients) {
            client.goToClient(codeClient);
            final FicheClient ficheClient = client.getFicheClient();
            clients.put(codeClient, ficheClient);
            log.info("[+] {} {} ({}/{})", ficheClient.getNom(), ficheClient.getPrenom(), ficheClient.getId(),
                    ficheClient.getPassword());
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
