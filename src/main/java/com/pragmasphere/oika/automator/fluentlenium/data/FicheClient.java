package com.pragmasphere.oika.automator.fluentlenium.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class FicheClient {
    private String id;
    private String nom;
    private String prenom;
    private String password;

    public FicheClient(final String id) {
        this.id = id;
    }

    public FicheClient() {
    }
}
