package com.kikitraiteur.api_kikitraiteur.Client.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "telephone"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_prenom", nullable = false)
    private String name;

    @Column(name = "name", nullable = false)
    private String nameCol;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "telephone", nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String type; // ex: particulier, entreprise

    private String organization;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DemandeDevis> demandesDevis = new ArrayList<>();

    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.name != null && !this.name.isEmpty()) {
            this.nameCol = this.name;
        } else if (this.nameCol != null && !this.nameCol.isEmpty()) {
            this.name = this.nameCol;
        } else {
            this.name = "Client Kiki Traiteur";
            this.nameCol = "Client Kiki Traiteur";
        }

        if (this.phone != null && !this.phone.isEmpty()) {
            this.telephone = this.phone;
        } else if (this.telephone != null && !this.telephone.isEmpty()) {
            this.phone = this.telephone;
        } else {
            this.phone = "Non renseigné";
            this.telephone = "Non renseigné";
        }

        if (this.type == null || this.type.isEmpty()) {
            this.type = "particulier";
        }
    }
}
