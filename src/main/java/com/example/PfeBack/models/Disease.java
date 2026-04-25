package com.example.PfeBack.models;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.util.List;

@Document(collection = "diseases")
public class Disease {
    @Id
    private String id;
    private String name;
    private String description;
    private List<String> symptoms;
    private List<String> treatments;
    private Article article;

    public Disease() {}

    public Disease(String id, String name, String description, List<String> symptoms, List<String> treatments, Article article) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.symptoms = symptoms;
        this.treatments = treatments;
        this.article = article;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public List<String> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<String> treatments) {
        this.treatments = treatments;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}


