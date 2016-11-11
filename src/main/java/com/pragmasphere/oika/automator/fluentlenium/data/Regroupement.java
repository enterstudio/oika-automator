package com.pragmasphere.oika.automator.fluentlenium.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Regroupement {
    private String id;
    private String numero;
    private LocalDate date;
    private final List<Facture> factures = new ArrayList<>();

    public boolean addFacture(final Facture facture) {
        return factures.add(facture);
    }
}
