package com.pragmasphere.oika.automator.fluentlenium.po;

import com.pragmasphere.oika.automator.fluentlenium.data.FicheClient;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static org.fluentlenium.core.filter.FilterConstructor.withText;

@PageUrl("Client.php5") // ?REF=CLVDI70475
public class ClientPage extends OikaFluentPage {

    @FindBy(css = "table#TableInfoClient input[name='nom']")
    public FluentWebElement nom;

    @FindBy(css = "table#TableInfoClient input[name='prenom']")
    public FluentWebElement prenom;

    public void goToClient(final String codeClient) {
        super.goTo("/" + getUrl() + "?REF=" + codeClient); // TODO: Bug goTo "/" a corriger dans FluentLenium.
    }

    public FluentWebElement tableInfoClient() {
        return el("table#TableInfoClient");
    }

    public FicheClient getFicheClient() {
        final FicheClient ficheClient = new FicheClient();

        final FluentWebElement tableInfoClient = tableInfoClient();

        final FluentWebElement id = el(".TableInfoTitre", withText("ID Compte Client")).axes().parent().$("td").index(1);
        final FluentWebElement nom = tableInfoClient.el("input[name='nom_vdi']");
        final FluentWebElement prenom = tableInfoClient.el("input[name='prenom_vdi']");
        final FluentWebElement password = el(".TableInfoTitre", withText("Mot de Passe Internet")).axes().parent().$("td")
                .index(1);

        ficheClient.setId(id.text());
        ficheClient.setNom(nom.value());
        ficheClient.setPrenom(prenom.value());
        ficheClient.setPassword(password.text());

        return ficheClient;
    }
}
