package com.kikitraiteur.api_kikitraiteur.Client.dto;

import com.kikitraiteur.api_kikitraiteur.Client.model.DevisItem;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectDevisRequestDto {
    private Long clientId;
    
    // Pour création nouveau client
    private String newClientName;
    private String newClientEmail;
    private String newClientPhone;
    private String newClientType; // particulier, entreprise
    private String newClientOrganization;

    // Champs du devis
    private String prestationId;
    private String signatureGastronomique;
    private Integer guests;
    private String location;
    private String date;
    private String time;
    private Double tvaRate;
    private Double discount;
    private List<DevisItem> items;
    private String gestionnaireName;
}
