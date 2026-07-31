package com.kikitraiteur.api_kikitraiteur.Client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeDevisRequestDto {
    @NotBlank(message = "Le nom du client est obligatoire")
    private String clientName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @jakarta.validation.constraints.Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Format d'email invalide"
    )
    private String clientEmail;

    @NotBlank(message = "Le téléphone est obligatoire")
    @jakarta.validation.constraints.Pattern(
        regexp = "^(\\+221|00221)?\\s*(7[05678]|33)\\s*(\\d\\s*){7}$",
        message = "Le numéro de téléphone doit être conforme au standard sénégalais (ex: +221 77 777 77 77)"
    )
    private String clientPhone;

    private String clientType;

    private String organization;

    private String prestationId;

    private String prestationTitle;

    private String date;

    private String time;

    private Integer guests;

    private Boolean isInstitution;

    private String location;

    private String cuisine;

    private String message;
}
