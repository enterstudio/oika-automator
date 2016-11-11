package com.pragmasphere.oika.automator.fluentlenium.po;

import com.pragmasphere.oika.automator.fluentlenium.data.Facture;
import com.pragmasphere.oika.automator.fluentlenium.data.Regroupement;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@PageUrl("Reunion.php5")
public class Reunion extends OikaFluentPage {
    public List<Regroupement> getRegroupements() {
        final List<Regroupement> regroupements = new ArrayList<>();

        for (final FluentWebElement elRegroupement : $("table.LstCde tr.TableInfo.LignePrincipale")) {
            final Regroupement regroupement = createRegroupement(elRegroupement);
            regroupements.add(regroupement);

            final FluentWebElement following = elRegroupement.axes().followingSiblings().first();
            for (final FluentWebElement elDetail : following.$("table.LstCdeDetail tr.TableInfo.LigneDetail")) {
                final Facture facture = createFacture(elDetail);
                regroupement.addFacture(facture);
            }
        }

        return regroupements;
    }

    private Regroupement createRegroupement(final FluentWebElement elRegroupement) {
        final Regroupement regroupement = new Regroupement();

        final FluentWebElement elRegroupementInfo = elRegroupement.el("td.LstCdeColInfo");

        final FluentWebElement numCmdeNet = elRegroupementInfo.el(".NumCmdeNet");
        final FluentWebElement numCmdeMoka = elRegroupementInfo.el(".NumCmdeMoka");
        final FluentWebElement dateCmde = elRegroupementInfo.el(".DateCmde");

        regroupement.setNumero(numCmdeNet.textContent().substring(2)); // "N°20299"
        regroupement.setId(numCmdeMoka.textContent().substring(4, numCmdeMoka.textContent().length() - 1)); // "(ID 19633)"
        regroupement.setDate(
                LocalDate.parse(dateCmde.textContent().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))); // "05/11/2016"

        return regroupement;
    }

    private Facture createFacture(final FluentWebElement elDetail) {
        final Facture facture = new Facture();

        final FluentWebElement elDetailInfo = elDetail.el("td.LstCdeColInfo");

        final FluentWebElement numCmdeNet = elDetailInfo.el(".NumCmdeNet");
        final FluentWebElement numCmdeMoka = elDetailInfo.el(".NumCmdeMoka");
        final FluentWebElement clientId = elDetailInfo.$("span").last();

        facture.setNumero(numCmdeNet.textContent().substring(2)); // "N°20299"
        facture.setId(numCmdeMoka.textContent().substring(4, numCmdeNet.textContent().length() - 1)); // "(ID 19633)"
        facture.setClientId(clientId.textContent().substring(1, clientId.textContent().length() - 1));

        return facture;
    }
}
