package com.example.PfeBack.models;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "medical_processes")
public class MedicalProcess {
    @Id
    private String id;
    private String diagnosis;
    private LocalDate startDate;
    private String status;
    private List<MedicalStep> steps;

    public MedicalProcess() {}

    public MedicalProcess(String diagnosis, LocalDate startDate, String status, List<MedicalStep> steps) {
        this.diagnosis = diagnosis;
        this.startDate = startDate;
        this.status = status;
        this.steps = steps;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<MedicalStep> getSteps() { return steps; }
    public void setSteps(List<MedicalStep> steps) { this.steps = steps; }
}
