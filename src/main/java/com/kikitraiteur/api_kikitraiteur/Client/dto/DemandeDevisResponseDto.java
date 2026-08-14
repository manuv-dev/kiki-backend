package com.kikitraiteur.api_kikitraiteur.Client.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeDevisResponseDto {
    private Long id;
    private ClientDto client;
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
