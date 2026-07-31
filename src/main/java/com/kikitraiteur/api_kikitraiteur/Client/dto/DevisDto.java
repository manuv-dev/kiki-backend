package com.kikitraiteur.api_kikitraiteur.Client.dto;

import com.kikitraiteur.api_kikitraiteur.Client.model.DevisItem;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevisDto {
    private Long id;
    private String devisRef;
    private Long demandeId;
    private String clientName;
    private String clientEmail;
    private String prestationId;
    private String signatureGastronomique;
    private String dateCreated;
    private Double tvaRate;
    private Double discount;
    private String status;
    private List<DevisItem> items;
}
