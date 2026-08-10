package com.kikitraiteur.api_kikitraiteur.Client.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Devis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "devis_ref")
    private String devisRef;

    @Column(name = "demande_id", nullable = false)
    private Long demandeId;

    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String gestionnaireName;
    private String prestationId;
    private String signatureGastronomique;
    
    private Integer guests;
    private String location;
    private String date;
    private String time;

    private String dateCreated;
    private Double tvaRate;
    private Double discount;

    @Column(nullable = false)
    private String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "devis_items", joinColumns = @JoinColumn(name = "devis_id"))
    @Builder.Default
    private List<DevisItem> items = new ArrayList<>();
}
