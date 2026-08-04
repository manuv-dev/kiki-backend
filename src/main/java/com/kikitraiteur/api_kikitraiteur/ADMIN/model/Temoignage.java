package com.kikitraiteur.api_kikitraiteur.ADMIN.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "temoignages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Temoignage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4000)
    private String temoignage;

    @Column(name = "nom_client", nullable = false)
    private String nomClient;

    @Column(name = "titre_fonction")
    private String titreFonction;

    @Column(nullable = false)
    @Builder.Default
    private Integer note = 5;

    @JsonProperty("content")
    public String getContent() {
        return temoignage;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        if (content != null && !content.trim().isEmpty()) {
            this.temoignage = content;
        }
    }

    @JsonProperty("clientName")
    public String getClientName() {
        return nomClient;
    }

    @JsonProperty("clientName")
    public void setClientName(String clientName) {
        if (clientName != null && !clientName.trim().isEmpty()) {
            this.nomClient = clientName;
        }
    }

    @JsonProperty("clientRole")
    public String getClientRole() {
        return titreFonction;
    }

    @JsonProperty("clientRole")
    public void setClientRole(String clientRole) {
        if (clientRole != null) {
            this.titreFonction = clientRole;
        }
    }

    @JsonProperty("rating")
    public Integer getRating() {
        return note;
    }

    @JsonProperty("rating")
    public void setRating(Integer rating) {
        if (rating != null) {
            this.note = rating;
        }
    }
}
