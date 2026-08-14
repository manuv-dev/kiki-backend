package com.kikitraiteur.api_kikitraiteur.Gestionnaire.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusRequestDto {
    private String status;
    private List<Long> propositionIds;
}
