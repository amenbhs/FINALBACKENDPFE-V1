package com.example.PfeBack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PfeBack.Services.PlantService;
import com.example.PfeBack.models.Plant;

@RestController
@RequestMapping("/api/plants")
public class PlantController {
    private final PlantService plantService;

    @Autowired
    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping
    public List<Plant> getAllPlants() {
        return plantService.getAllPlants();
    }

    @GetMapping("/{id}")
    public Plant getPlantById(@PathVariable String id) {
        return plantService.getPlantById(id);
    }

    @PostMapping
    public Plant addPlant(@RequestBody Plant plant) {
        return plantService.savePlant(plant);
    }

    @PutMapping("/{id}")
    public Plant updatePlant(@PathVariable String id, @RequestBody Plant plant) {
        plant.setId(id);
        return plantService.savePlant(plant);
    }

    @DeleteMapping("/{id}")
    public void deletePlant(@PathVariable String id) {
        plantService.deletePlant(id);
    }
        // ...existing code...

    @GetMapping("/alerts")
    public List<String> getPlantAlerts() {
        List<Plant> plants = plantService.getAllPlants();
        List<String> alerts = new java.util.ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        for (Plant plant : plants) {
            // Check nextTreatment
            if (plant.getNextTreatment() != null && !plant.getNextTreatment().isEmpty()) {
                try {
                    java.time.LocalDate treatDate = java.time.LocalDate.parse(plant.getNextTreatment());
                    if (!treatDate.isBefore(today) && !treatDate.isAfter(today.plusDays(7))) {
                        alerts.add("Treatment for " + plant.getName() + " is due on " + plant.getNextTreatment());
                    } else if (treatDate.isBefore(today)) {
                        alerts.add("Treatment for " + plant.getName() + " is OVERDUE! Was scheduled for " + plant.getNextTreatment());
                    }
                } catch (Exception ignored) {}
            }
            // Check expectedHarvestDate
            if (plant.getExpectedHarvestDate() != null && !plant.getExpectedHarvestDate().isEmpty()) {
                try {
                    java.time.LocalDate harvestDate = java.time.LocalDate.parse(plant.getExpectedHarvestDate());
                    if (!harvestDate.isBefore(today) && !harvestDate.isAfter(today.plusDays(7))) {
                        alerts.add("Harvest for " + plant.getName() + " is expected on " + plant.getExpectedHarvestDate());
                    } else if (harvestDate.isBefore(today)) {
                        alerts.add("Harvest for " + plant.getName() + " is OVERDUE! Was expected on " + plant.getExpectedHarvestDate());
                    }
                } catch (Exception ignored) {}
            }
        }
        System.out.println("Plant alerts: " + alerts);
        return alerts;
    }

    // --- Watering mutations ---
    @PostMapping("/{id}/water")
    public Plant waterPlant(@PathVariable String id, @RequestParam(required = false) Integer liters) {
        return plantService.waterPlant(id, liters, liters);
    }

    @PatchMapping("/{id}/moisture")
    public Plant updateSoilMoisture(@PathVariable String id, @RequestParam Integer value) {
        return plantService.updateSoilMoisture(id, value);
    }


    @PatchMapping("/bulk/moisture")
    public List<Plant> bulkMoisture(@RequestBody java.util.Map<String,Integer> moistureMap) {
        return plantService.bulkUpdateMoisture(moistureMap);
    }
}
