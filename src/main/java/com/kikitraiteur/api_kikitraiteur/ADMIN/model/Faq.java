package com.kikitraiteur.api_kikitraiteur.ADMIN.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faqs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String question;

    @Column(nullable = false, length = 4000)
    private String reponse;

    @Column(length = 255)
    @Builder.Default
    private String categorie = "Général";

    @JsonProperty("answer")
    public String getAnswer() {
        return reponse;
    }

    @JsonProperty("answer")
    public void setAnswer(String answer) {
        if (answer != null && !answer.trim().isEmpty()) {
            this.reponse = answer;
        }
    }

    @JsonProperty("category")
    public String getCategory() {
        return categorie;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            this.categorie = category;
        }
    }
}
