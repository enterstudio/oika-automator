package com.pragmasphere.oika.automator.fluentlenium.po;

import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static org.fluentlenium.core.filter.FilterConstructor.with;

@PageUrl("ListeReunions.php5")
public class ListeReunionsPage extends OikaFluentPage {

    @FindBy(css = ".Recherche input[name='NOMHOTE']")
    private FluentWebElement nomHote;

    @FindBy(css = ".Recherche input[type='SUBMIT']")
    private FluentWebElement submit;

    /**
     * Affiche la dernière réunion d'un hote.
     *
     * @param nom nom de l'hote.
     */
    public void lastReunion(final String nom) {
        nomHote.write(nom);
        submit.click();

        final FluentWebElement link = $("#TableListeReunions a", with("href").startsWith("Reunion.php5")).first();
        link.click();
    }

}
