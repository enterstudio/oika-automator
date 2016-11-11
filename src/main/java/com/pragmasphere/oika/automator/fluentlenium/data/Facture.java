package com.pragmasphere.oika.automator.fluentlenium.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Facture {
    private String id;
    private String numero;
    private String clientId;
}
