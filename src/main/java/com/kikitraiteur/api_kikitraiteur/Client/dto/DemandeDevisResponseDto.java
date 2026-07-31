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
    private String prestationTitle;
    private String date;
    private String time;
    private Integer guests;
    private Boolean isInstitution;
    private String organization;
    private String location;
    private String cuisine;
    private String message;
    private String status;
    private LocalDateTime dateSubmitted;
}
