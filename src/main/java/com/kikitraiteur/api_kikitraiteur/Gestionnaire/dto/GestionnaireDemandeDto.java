package com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GestionnaireDemandeDto {
    private Long id;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String clientType;
    private String clientOrganization;
    private String prestationId;
    private String evenementNature;
    private String date;
    private String time;
    private Integer guests;
    private String organization;
    private String locationType;
    private String locationDetails;
    private String message;
    private String status;
    private LocalDateTime dateSubmitted;
}
