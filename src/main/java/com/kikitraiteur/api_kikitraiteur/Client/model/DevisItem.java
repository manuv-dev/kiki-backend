package com.kikitraiteur.api_kikitraiteur.Client.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevisItem {
    @Column(name = "description")
    private String desc;
    private Integer qty;
    private Double unitPrice;
}
