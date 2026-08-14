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
    @NotBlank(message = "Le type de client est obligatoire")
    private String clientType;

    private String organization;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String phone;

    @NotBlank(message = "La prestation est obligatoire")
    private String prestationId;

    private String evenementNature;
    private String message;
    
    @NotNull(message = "Le nombre de convives est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins 1 convive")
    private Integer guests;
    
    @NotBlank(message = "La date est obligatoire")
    private String date;
    
    @NotBlank(message = "L'heure est obligatoire")
    private String time;
    
    private String locationType;
    private String locationDetails;
}
