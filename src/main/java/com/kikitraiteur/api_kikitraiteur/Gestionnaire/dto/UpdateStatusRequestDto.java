package com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusRequestDto {
    private String status;
}
