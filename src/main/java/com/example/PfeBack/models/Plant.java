package com.example.PfeBack.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "plants")
public class Plant {
    @Id
    private String id;
    private String name; // Plant Name (e.g., Wheat, Olive Tree, Tomato)
    private String imageUrl;
    private String type; // (Crop, Tree, Medicinal, etc.)
    private String plantingDate;
    private String expectedHarvestDate;
    private String quantityOrArea; // Quantity / Area covered
    private String healthStatus;
    private String notes;
    private String nextTreatment;
    private String fertilizer;
    private String irrigation;
    private String diseaseHistory;
      private String farmId;
  // Water management fields
  private Integer soilMoisture; // percentage 0-100
  private String lastWatered; // ISO date-time string

  public Plant() {}

  public String getFarmId() {
    return farmId;
  }
  public void setFarmId(String farmId) {
    this.farmId = farmId;
  }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPlantingDate() { return plantingDate; }
    public void setPlantingDate(String plantingDate) { this.plantingDate = plantingDate; }

    public String getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(String expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }

    public String getQuantityOrArea() { return quantityOrArea; }
    public void setQuantityOrArea(String quantityOrArea) { this.quantityOrArea = quantityOrArea; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getNextTreatment() { return nextTreatment; }
    public void setNextTreatment(String nextTreatment) { this.nextTreatment = nextTreatment; }

    public String getFertilizer() { return fertilizer; }
    public void setFertilizer(String fertilizer) { this.fertilizer = fertilizer; }

    public String getIrrigation() { return irrigation; }
    public void setIrrigation(String irrigation) { this.irrigation = irrigation; }

    public String getDiseaseHistory() { return diseaseHistory; }
    public void setDiseaseHistory(String diseaseHistory) { this.diseaseHistory = diseaseHistory; }

  public Integer getSoilMoisture() { return soilMoisture; }
  public void setSoilMoisture(Integer soilMoisture) { this.soilMoisture = soilMoisture; }

  public String getLastWatered() { return lastWatered; }
  public void setLastWatered(String lastWatered) { this.lastWatered = lastWatered; }

}
