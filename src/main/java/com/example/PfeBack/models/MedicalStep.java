package com.example.PfeBack.models;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "medical_steps")
public class MedicalStep {
    @Id
    private String id;
    private String action;
    private LocalDate date;
    private String veterinarian;
    private String notes;

    public MedicalStep() {}

    public MedicalStep(String action, LocalDate date, String veterinarian, String notes) {
        this.action = action;
        this.date = date;
        this.veterinarian = veterinarian;
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getVeterinarian() { return veterinarian; }
    public void setVeterinarian(String veterinarian) { this.veterinarian = veterinarian; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
