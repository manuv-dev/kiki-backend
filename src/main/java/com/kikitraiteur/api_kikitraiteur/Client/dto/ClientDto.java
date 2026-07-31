package com.kikitraiteur.api_kikitraiteur.Client.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String type;
    private String organization;
    private LocalDateTime createdAt;
}
