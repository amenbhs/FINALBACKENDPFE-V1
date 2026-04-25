package com.example.PfeBack.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.PfeBack.repository.PlantRepository;
import com.example.PfeBack.models.Farm;
import com.example.PfeBack.models.Plant;
import com.example.PfeBack.models.WaterTank;
import com.example.PfeBack.repository.FarmRepository;


@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final FarmRepository farmRepository;

    @Autowired
    public PlantService(PlantRepository plantRepository, FarmRepository farmRepository) {
        this.plantRepository = plantRepository;
        this.farmRepository = farmRepository;
    }

    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }

    public Plant getPlantById(String id) {
        return plantRepository.findById(id).orElse(null);
    }

    public Plant savePlant(Plant plant) {
        return plantRepository.save(plant);
    }

    public void deletePlant(String id) {
        plantRepository.deleteById(id);
    }

    // Watering related simple calculations (placeholder logic)
    public int calculateTotalWaterUsedToday(String farmId) {
        // Placeholder: sum (100 - soilMoisture) as proxy
        return plantRepository.findByFarmId(farmId).stream()
                .filter(p -> p.getSoilMoisture() != null)
                .mapToInt(p -> Math.max(0, 100 - p.getSoilMoisture()))
                .sum();
    }

    public int getTankLevel(String farmId) {
        // Placeholder fixed value or simple function of plants
        int base = 1000;
        int used = calculateTotalWaterUsedToday(farmId);
        return Math.max(0, base - used);
    }

    public java.util.Map<String,Object> getAiSuggestion(String farmId) {
        java.util.Optional<Plant> driest = plantRepository.findByFarmId(farmId).stream()
                .filter(p -> p.getSoilMoisture() != null)
                .min(java.util.Comparator.comparingInt(p -> p.getSoilMoisture()));
        java.util.Map<String,Object> map = new java.util.HashMap<>();
        if (driest.isPresent()) {
            Plant p = driest.get();
            map.put("plant", p.getName());
            map.put("soilMoisture", p.getSoilMoisture());
            map.put("suggested", (100 - p.getSoilMoisture()) / 5.0); // liters placeholder
        }
        return map;
    }

    public boolean getRainForecast(String farmId) {
        // Placeholder random false
        return false;
    }

    public int getTodayIntake(String farmId) {
        return calculateTotalWaterUsedToday(farmId);
    }

    public java.util.Map<String,Object> getSchedule(String farmId) {
        java.util.Map<String,Object> map = new java.util.HashMap<>();
        java.util.List<java.util.Map<String,String>> schedule = new java.util.ArrayList<>();
        schedule.add(java.util.Map.of("time","08:00","status","done"));
        schedule.add(java.util.Map.of("time","12:00","status","next"));
        schedule.add(java.util.Map.of("time","16:00","status","pending"));
        map.put("schedule", schedule);
        map.put("next", java.time.ZonedDateTime.now().plusHours(2).toString());
        return map;
    }

    // --- Mutations for watering dashboard ---
  public Plant waterPlant(String plantId, Integer addedLiters, Integer soilMoisture) {
    Plant updatedPlant = plantRepository.findById(plantId).map(p -> {
      
        if (soilMoisture != null) {
            p.setSoilMoisture(soilMoisture);
        } else {
            int current = p.getSoilMoisture() != null ? p.getSoilMoisture() : 50;
            int inc = addedLiters != null ? Math.min(addedLiters * 5, 100) : 10;
            p.setSoilMoisture(Math.min(100, current + inc));
        }
        p.setLastWatered(java.time.ZonedDateTime.now().toString());
        return plantRepository.save(p);
    }).orElseThrow(() -> new RuntimeException("Plant not found: " + plantId));

        // Decrease tank quantity and record usage
        if (updatedPlant.getFarmId() != null) {
            Farm farm = farmRepository.findById(updatedPlant.getFarmId()).orElse(null);
            if (farm != null && farm.getWaterTank() != null && addedLiters != null && addedLiters > 0) {
                WaterTank tank = farm.getWaterTank();
                double newQ = Math.max(0, tank.getQuantity() - addedLiters);
                tank.setQuantity(newQ);
                // Record usage for today
                String today = java.time.LocalDate.now().toString();
                java.util.Map<String, Double> usage = tank.getUsageByDate();
                if (usage == null) {
                    usage = new java.util.HashMap<>();
                    tank.setUsageByDate(usage);
                }
                usage.put(today, usage.getOrDefault(today, 0.0) + addedLiters);
                farmRepository.save(farm);
            }
        }
        return updatedPlant;
    }

    public Plant updateSoilMoisture(String plantId, Integer moisture) {
        if (moisture == null || moisture < 0 || moisture > 100) {
            throw new IllegalArgumentException("Moisture must be 0-100");
        }
        return plantRepository.findById(plantId).map(p -> {
            p.setSoilMoisture(moisture);
            return plantRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Plant not found: " + plantId));
    }


    public java.util.List<Plant> bulkUpdateMoisture(java.util.Map<String,Integer> moistureMap) {
        java.util.List<Plant> updated = new java.util.ArrayList<>();
        for (var entry : moistureMap.entrySet()) {
            String id = entry.getKey();
            Integer m = entry.getValue();
            plantRepository.findById(id).ifPresent(p -> {
                if (m != null && m >=0 && m <=100) {
                    p.setSoilMoisture(m);
                    updated.add(p);
                }
            });
        }
        if (!updated.isEmpty()) {
            plantRepository.saveAll(updated);
        }
        return updated;
    }
}
