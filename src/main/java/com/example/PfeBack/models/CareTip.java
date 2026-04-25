package com.example.PfeBack.models;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(collection = "care-tips")
public class CareTip {
    @Id
    private String id;
    private String title;
    private String description;
    private Article article;

    public CareTip() {}

    public CareTip(String id, String title, String description, Article article) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.article = article;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}