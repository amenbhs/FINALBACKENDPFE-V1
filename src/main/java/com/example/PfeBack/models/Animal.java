package com.example.PfeBack.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "animals")
public class Animal {
  @Id
  private String id;
      private String name;
      private String imageUrl;
      private String species;
      private String breed;
      private String birthDate;
      private String sex;
      private String weight;
      private String healthStatus;
      private String feeding;
      private String activity;
      private String notes;
      private String vet;
      private String nextVisit;
      private String vaccinationDate;
      private List<MedicalProcess> medicalProcesses;
      private String farmId;
      // Water intake tracking
      private Double todayIntakeLiters; // accumulated for current day
      private Double recommendedIntakeLiters; // recommended daily amount
      private String intakeUpdatedAt; // ISO date (yyyy-MM-dd) when todayIntakeLiters last reset
    private Double fullness; // 0.0..1.0 how full the animal is relative to recommended intake
      private String userId;
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Animal() {}

  public String getFarmId() {
    return farmId;
  }

  public void setFarmId(String farmId) {
    this.farmId = farmId;
  }

    // Add a constructor with all fields if needed

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public String getFeeding() { return feeding; }
    public void setFeeding(String feeding) { this.feeding = feeding; }

    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getVet() { return vet; }
    public void setVet(String vet) { this.vet = vet; }

    public String getNextVisit() { return nextVisit; }
    public void setNextVisit(String nextVisit) { this.nextVisit = nextVisit; }

    public String getVaccinationDate() { return vaccinationDate; }
    public void setVaccinationDate(String vaccinationDate) { this.vaccinationDate = vaccinationDate; }

    public List<MedicalProcess> getMedicalProcesses() { return medicalProcesses; }
    public void setMedicalProcesses(List<MedicalProcess> medicalProcesses) { this.medicalProcesses = medicalProcesses; }

  public Double getTodayIntakeLiters() { return todayIntakeLiters; }
  public void setTodayIntakeLiters(Double todayIntakeLiters) { this.todayIntakeLiters = todayIntakeLiters; }

  public Double getFullness() { return fullness; }
  public void setFullness(Double fullness) { this.fullness = fullness; }

  public Double getRecommendedIntakeLiters() { return recommendedIntakeLiters; }
  public void setRecommendedIntakeLiters(Double recommendedIntakeLiters) { this.recommendedIntakeLiters = recommendedIntakeLiters; }

  public String getIntakeUpdatedAt() { return intakeUpdatedAt; }
  public void setIntakeUpdatedAt(String intakeUpdatedAt) { this.intakeUpdatedAt = intakeUpdatedAt; }
}
