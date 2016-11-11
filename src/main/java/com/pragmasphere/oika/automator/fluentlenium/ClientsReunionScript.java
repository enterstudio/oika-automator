package com.pragmasphere.oika.automator.fluentlenium;

import com.pragmasphere.oika.automator.commands.auth.Auth;
import com.pragmasphere.oika.automator.fluentlenium.configuration.FluentScript;
import com.pragmasphere.oika.automator.fluentlenium.data.FicheClient;
import com.pragmasphere.oika.automator.fluentlenium.data.Regroupement;
import com.pragmasphere.oika.automator.fluentlenium.po.Client;
import com.pragmasphere.oika.automator.fluentlenium.po.ListeReunions;
import com.pragmasphere.oika.automator.fluentlenium.po.MonEspaceReserve;
import com.pragmasphere.oika.automator.fluentlenium.po.Reunion;
import com.pragmasphere.oika.automator.fluentlenium.po.TableauDeBord;
import lombok.extern.slf4j.Slf4j;
import org.fluentlenium.core.annotation.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ClientsReunionScript extends FluentScript {
    private final Auth auth;
    private final String hote;

    @Page
    private MonEspaceReserve espaceReserve;

    @Page
    private TableauDeBord tableauDeBord;

    @Page
    private ListeReunions listeReunions;

    @Page
    private Reunion reunion;

    @Page
    private Client client;

    private List<FicheClient> clients;

    public ClientsReunionScript(final Auth auth, final String hote) {
        this.auth = auth;
        this.hote = hote;
    }

    @Override
    protected void runScript() {
        espaceReserve.go();

        log.info("[*] Connexion {}/********", auth.getLogin());
        espaceReserve.login(auth);

        tableauDeBord.isAt();

        log.info("[*] Affichage de la liste des réunions");
        tableauDeBord.menu.reunions().click();

        log.info("[*] Recherche de la dernière réunion de l'hôte(sse) {}", hote);
        listeReunions.lastReunion(hote);

        log.info("[*] Lecture des factures associées à la réunion");
        final List<Regroupement> regroupements = reunion.getRegroupements();

        final Set<String> codeClients = regroupements.stream().flatMap(regroupement -> regroupement.getFactures().stream())
                .map(facture -> facture.getClientId()).collect(Collectors.toSet());

        log.info("[+] {} factures ont été trouvées", codeClients.size());
        final List<FicheClient> clients = new ArrayList<>();
        for (final String codeClient : codeClients) {
            client.goToClient(codeClient);
            final FicheClient ficheClient = client.getFicheClient();
            clients.add(ficheClient);
            log.info("[+] {} {} ({}/{})", ficheClient.getNom(), ficheClient.getPrenom(), ficheClient.getId(),
                    ficheClient.getPassword());
        }

        this.clients = clients;

        log.info("[*] Chargement terminé");
    }

    public List<FicheClient> getClients() {
        return clients;
    }
}
