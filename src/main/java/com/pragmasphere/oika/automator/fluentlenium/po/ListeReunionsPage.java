package com.pragmasphere.oika.automator.fluentlenium.po;

import com.pragmasphere.oika.automator.fluentlenium.data.Regroupement;
import org.fluentlenium.core.annotation.Page;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

import static org.fluentlenium.core.filter.FilterConstructor.with;

@PageUrl("ListeReunions.php5")
public class ListeReunionsPage extends OikaFluentPage {

    @FindBy(css = ".Recherche input[name='NOMHOTE']")
    private FluentWebElement nomHote;

    @FindBy(css = ".Recherche input[type='SUBMIT']")
    private FluentWebElement submit;

    @Page
    private ReunionPage reunionPage;

    /**
     * Affiche la dernière réunion effective d'un hote.
     *
     * @param nom nom de l'hote.
     */
    public void lastReunion(final String nom) {
        nomHote.write(nom);
        submit.click();

        List<FluentWebElement> links = $("#TableListeReunions a", with("href").startsWith("Reunion.php5"));

        for (int i=0; i<links.size(); i++) {
            FluentWebElement link = links.get(i);
            link.click();
            List<Regroupement> regroupements = reunionPage.getRegroupements();
            if (regroupements.size() > 0) {
                return;
            }
            getDriver().navigate().back();
            links = $("#TableListeReunions a", with("href").startsWith("Reunion.php5"));
            //TODO: Corriger le StaleElementReferenceException dans FluentLenium pour éviter de rechercher à nouveau
        }

        throw new NoSuchElementException("Impossible de trouver une réunion effective.");
    }

}
