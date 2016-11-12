package com.pragmasphere.oika.automator.fluentlenium.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Reunion {
    private String id;
    private List<Regroupement> regroupements;
}
