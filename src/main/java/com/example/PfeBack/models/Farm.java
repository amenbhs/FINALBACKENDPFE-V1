package com.example.PfeBack.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.PfeBack.models.WaterTank;

@Document(collection = "farms")
public class Farm {
    @Id
    private String id;
    private String name;
    private String location;
    private List<String> animalIds; // store animal IDs
    private List<String> plantIds;
    private String userId; // store user ID
    private WaterTank waterTank;
    private CowFoodTank cowFoodTank;
    private DogFoodTank dogFoodTank;
    private ChickenFoodTank chickenFoodTank;
    private SheepFoodTank sheepFoodTank;


    // Constructors, getters, and setters
    public Farm() {
        this.waterTank = new WaterTank();
        this.cowFoodTank = new CowFoodTank();
        this.dogFoodTank = new DogFoodTank();
        this.chickenFoodTank = new ChickenFoodTank();
        this.sheepFoodTank = new SheepFoodTank();
    }

    public Farm(String name, String location, List<String> animalIds, List<String> plantIds, String userId) {
        this.name = name;
        this.location = location;
        this.animalIds = animalIds;
        this.plantIds = plantIds;
        this.userId = userId;
        this.waterTank = new WaterTank();
        this.cowFoodTank = new CowFoodTank();
        this.dogFoodTank = new DogFoodTank();
        this.chickenFoodTank = new ChickenFoodTank();
        this.sheepFoodTank = new SheepFoodTank();
    }
    public CowFoodTank getCowFoodTank() { return cowFoodTank; }
    public void setCowFoodTank(CowFoodTank cowFoodTank) { this.cowFoodTank = cowFoodTank; }

    public DogFoodTank getDogFoodTank() { return dogFoodTank; }
    public void setDogFoodTank(DogFoodTank dogFoodTank) { this.dogFoodTank = dogFoodTank; }

    public ChickenFoodTank getChickenFoodTank() { return chickenFoodTank; }
    public void setChickenFoodTank(ChickenFoodTank chickenFoodTank) { this.chickenFoodTank = chickenFoodTank; }

    public SheepFoodTank getSheepFoodTank() { return sheepFoodTank; }
    public void setSheepFoodTank(SheepFoodTank sheepFoodTank) { this.sheepFoodTank = sheepFoodTank; }
    public WaterTank getWaterTank() {
        return waterTank;
    }

    public void setWaterTank(WaterTank waterTank) {
        this.waterTank = waterTank;
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

  public String getLocation() {
      return location;
  }

  public void setLocation(String location) {
      this.location = location;
  }

  public List<String> getAnimalIds() {
      return animalIds;
  }

  public void setAnimalIds(List<String> animalIds) {
      this.animalIds = animalIds;
  }

  public List<String> getPlantIds() {
      return plantIds;
  }

  public void setPlantIds(List<String> plantIds) {
      this.plantIds = plantIds;
  }

  public String getUserId() {
      return userId;
  }

  public void setUserId(String userId) {
      this.userId = userId;
  }
}
