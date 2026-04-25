package com.example.PfeBack.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PfeBack.Services.AnimalService;
import com.example.PfeBack.Services.FarmService;
import com.example.PfeBack.Services.NotificationService;
import com.example.PfeBack.Services.PlantService;
import com.example.PfeBack.models.Animal;
import com.example.PfeBack.models.Farm;
import com.example.PfeBack.models.Notification;
import com.example.PfeBack.models.Plant;
import com.example.PfeBack.repository.NotificationRepository;

@RestController
@RequestMapping("/api/farms")
public class FarmController {
    
    @Autowired
    private FarmService farmService;
    @Autowired
    private PlantService plantService;
    @Autowired
    private AnimalService animalService;  
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;


    FarmController(FarmService farmService, PlantService plantService, AnimalService animalService, NotificationService notificationService, NotificationRepository notificationRepository) {
        this.farmService = farmService;
        this.plantService = plantService;
        this.animalService = animalService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    // ✅ Create farm for a user
    @PostMapping("/user/{userId}")
    public Farm createFarmForUser(@PathVariable String userId, @RequestBody Farm farm) {
        System.out.println("Creating farm for user: " + userId);
        System.err.println("Farm details: " + farm);
        return farmService.createFarmForUser(userId, farm);
    }

    // ✅ Get notifications for a farm (now supports farm-specific filtering)
    @GetMapping("/{farmId}/notifications")
    public List<Notification> getNotificationsForFarm(
            @PathVariable String farmId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "false") boolean unreadOnly) {
        
        // Use farm-specific queries from NotificationRepository
        if (unreadOnly) {
            return notificationRepository.findByFarmIdAndReadFalse(farmId);
        } else {
            return notificationRepository.findByFarmIdOrderByCreatedAtDesc(farmId);
        }
    }

    @PostMapping("/{farmId}/notifications/generate-dynamic")
    public ResponseEntity<String> generateDynamicNotificationsForFarm(@PathVariable String farmId) {
        // TODO: pass farmId to service when notifications are linked to farms
        notificationService.generateDynamicNotifications(farmId);
        return ResponseEntity.ok("Dynamic notifications generated for farm " + farmId);
    }

    @PutMapping("/{farmId}/notifications/read-all")
    public ResponseEntity<String> markAllFarmNotificationsAsRead(@PathVariable String farmId) {
        // TODO: only mark notifications for this farm when model supports farmId
        notificationService.markAllAsRead(farmId);
        return ResponseEntity.ok("All notifications marked as read for farm " + farmId);
    }

    // ✅ Delete farm
    @DeleteMapping("/{farmId}")
    public void deleteFarm(@PathVariable String farmId) {
        farmService.deleteFarm(farmId);
    }

    // ✅ Get farm details
    @GetMapping("/{farmId}")
    public Farm getFarmById(@PathVariable String farmId) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Farm not found: " + farmId);
        }
        return farm;
    }

    // ✅ Get farm by user id
    @GetMapping("/user/{userId}")
    public Farm getFarmByUserId(@PathVariable String userId) {
        return farmService.getFarmByUserId(userId);
    }

    // ✅ Get animals of a farm
    @GetMapping("/{farmId}/animals")
    public List<Animal> getAnimalsByFarm(@PathVariable String farmId) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Farm not found: " + farmId);
        }
        return farmService.getAnimalsByFarm(farmId);
    }

    // ✅ Add animal to farm
    @PostMapping("/{farmId}/animals")
    public Animal addAnimalToFarm(@PathVariable String farmId, @RequestBody Animal animal) {
        return farmService.addAnimalToFarm(farmId, animal);
    }

    // ✅ Get plants of a farm
    @GetMapping("/{farmId}/plants")
    public List<Plant> getPlantsByFarm(@PathVariable String farmId) {
        return farmService.getPlantsByFarm(farmId);
    }

    // ✅ Add plant to farm
    @PostMapping("/{farmId}/plants")
    public Plant addPlantToFarm(@PathVariable String farmId, @RequestBody Plant plant) {
        return farmService.addPlantToFarm(farmId, plant);
    }

    // Watering dashboard endpoints (placeholder logic)
    // --- WaterTank endpoints ---
    @GetMapping("/{farmId}/tank-level")
    public java.util.Map<String,Object> getTankLevel(@PathVariable String farmId) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null || farm.getWaterTank() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Farm or tank not found: " + farmId);
        }
        double level = farm.getWaterTank().getQuantity();
        boolean low = level < 200; // threshold
        java.util.Map<String,Object> map = new java.util.HashMap<>();
        map.put("level", level);
        map.put("low", low);
        return map;
    }

    // --- Cow food tank endpoints ---
    @GetMapping("/{farmId}/cow-tank-level")
    public java.util.Map<String,Object> getCowTankLevel(@PathVariable String farmId) {
        double level = farmService.getCowFoodLevel(farmId);
        boolean low = level < 50; // threshold for food
        return java.util.Map.of("level", level, "low", low);
    }

    @PostMapping("/{farmId}/cow-tank/refill")
    public java.util.Map<String,Object> refillCowTank(@PathVariable String farmId, @RequestParam double amount) {
        farmService.refillCowFoodTank(farmId, amount);
        double level = farmService.getCowFoodLevel(farmId);
        return java.util.Map.of("level", level);
    }

    @GetMapping("/{farmId}/cow-tank/usage-by-date")
    public java.util.Map<String,Double> getCowUsageByDate(@PathVariable String farmId, @RequestParam String date) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null || farm.getCowFoodTank() == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Farm or cow tank not found: " + farmId);
        Double used = 0.0;
        if (farm.getCowFoodTank().getUsageByDate() != null) used = farm.getCowFoodTank().getUsageByDate().getOrDefault(date, 0.0);
        return java.util.Map.of("used", used);
    }

    @GetMapping("/{farmId}/cow-tank/usage-total")
    public java.util.Map<String,Double> getCowUsageTotal(@PathVariable String farmId) {
        double total = farmService.getCowFoodUsageTotal(farmId);
        return java.util.Map.of("total", total);
    }

    // --- Dog food tank endpoints ---
    @GetMapping("/{farmId}/dog-tank-level")
    public java.util.Map<String,Object> getDogTankLevel(@PathVariable String farmId) {
        double level = farmService.getDogFoodLevel(farmId);
        boolean low = level < 20;
        return java.util.Map.of("level", level, "low", low);
    }

    @PostMapping("/{farmId}/dog-tank/refill")
    public java.util.Map<String,Object> refillDogTank(@PathVariable String farmId, @RequestParam double amount) {
        farmService.refillDogFoodTank(farmId, amount);
        double level = farmService.getDogFoodLevel(farmId);
        return java.util.Map.of("level", level);
    }

    @GetMapping("/{farmId}/dog-tank/usage-by-date")
    public java.util.Map<String,Double> getDogUsageByDate(@PathVariable String farmId, @RequestParam String date) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null || farm.getDogFoodTank() == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Farm or dog tank not found: " + farmId);
        Double used = 0.0;
        if (farm.getDogFoodTank().getUsageByDate() != null) used = farm.getDogFoodTank().getUsageByDate().getOrDefault(date, 0.0);
        return java.util.Map.of("used", used);
    }

    @GetMapping("/{farmId}/dog-tank/usage-total")
    public java.util.Map<String,Double> getDogUsageTotal(@PathVariable String farmId) {
        double total = farmService.getDogFoodUsageTotal(farmId);
        return java.util.Map.of("total", total);
    }

    // --- Chicken food tank endpoints ---
    @GetMapping("/{farmId}/chicken-tank-level")
    public java.util.Map<String,Object> getChickenTankLevel(@PathVariable String farmId) {
        double level = farmService.getChickenFoodLevel(farmId);
        boolean low = level < 20;
        return java.util.Map.of("level", level, "low", low);
    }

    @PostMapping("/{farmId}/chicken-tank/refill")
    public java.util.Map<String,Object> refillChickenTank(@PathVariable String farmId, @RequestParam double amount) {
        farmService.refillChickenFoodTank(farmId, amount);
        double level = farmService.getChickenFoodLevel(farmId);
        return java.util.Map.of("level", level);
    }

    @GetMapping("/{farmId}/chicken-tank/usage-by-date")
    public java.util.Map<String,Double> getChickenUsageByDate(@PathVariable String farmId, @RequestParam String date) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null || farm.getChickenFoodTank() == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Farm or chicken tank not found: " + farmId);
        Double used = 0.0;
        if (farm.getChickenFoodTank().getUsageByDate() != null) used = farm.getChickenFoodTank().getUsageByDate().getOrDefault(date, 0.0);
        return java.util.Map.of("used", used);
    }

    @GetMapping("/{farmId}/chicken-tank/usage-total")
    public java.util.Map<String,Double> getChickenUsageTotal(@PathVariable String farmId) {
        double total = farmService.getChickenFoodUsageTotal(farmId);
        return java.util.Map.of("total", total);
    }

    // --- Sheep food tank endpoints ---
    @GetMapping("/{farmId}/sheep-tank-level")
    public java.util.Map<String,Object> getSheepTankLevel(@PathVariable String farmId) {
        double level = farmService.getSheepFoodLevel(farmId);
        boolean low = level < 50;
        return java.util.Map.of("level", level, "low", low);
    }

    @PostMapping("/{farmId}/sheep-tank/refill")
    public java.util.Map<String,Object> refillSheepTank(@PathVariable String farmId, @RequestParam double amount) {
        farmService.refillSheepFoodTank(farmId, amount);
        double level = farmService.getSheepFoodLevel(farmId);
        return java.util.Map.of("level", level);
    }

    @GetMapping("/{farmId}/sheep-tank/usage-by-date")
    public java.util.Map<String,Double> getSheepUsageByDate(@PathVariable String farmId, @RequestParam String date) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null || farm.getSheepFoodTank() == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Farm or sheep tank not found: " + farmId);
        Double used = 0.0;
        if (farm.getSheepFoodTank().getUsageByDate() != null) used = farm.getSheepFoodTank().getUsageByDate().getOrDefault(date, 0.0);
        return java.util.Map.of("used", used);
    }

    @GetMapping("/{farmId}/sheep-tank/usage-total")
    public java.util.Map<String,Double> getSheepUsageTotal(@PathVariable String farmId) {
        double total = farmService.getSheepFoodUsageTotal(farmId);
        return java.util.Map.of("total", total);
    }

    @GetMapping("/{farmId}/water-usage")
    public java.util.Map<String,Double> getWaterUsage(@PathVariable String farmId) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null || farm.getWaterTank() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Farm or tank not found: " + farmId);
        }
        // Sum all usage
        double total = 0.0;
        if (farm.getWaterTank().getUsageByDate() != null) {
            for (double v : farm.getWaterTank().getUsageByDate().values()) total += v;
        }
        return java.util.Map.of("total", total);
    }

    @PostMapping("/{farmId}/tank/refill")
    public java.util.Map<String,Object> refillTank(@PathVariable String farmId, @RequestParam double amount) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null || farm.getWaterTank() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Farm or tank not found: " + farmId);
        }
        double newQ = farm.getWaterTank().getQuantity() + amount;
        farm.getWaterTank().setQuantity(newQ);
        farmService.saveFarm(farm);
        return java.util.Map.of("level", newQ);
    }

    @GetMapping("/{farmId}/tank/usage-by-date")
    public java.util.Map<String,Double> getUsageByDate(@PathVariable String farmId, @RequestParam String date) {
        Farm farm = farmService.getFarmById(farmId);
        if (farm == null || farm.getWaterTank() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Farm or tank not found: " + farmId);
        }
        Double used = 0.0;
        if (farm.getWaterTank().getUsageByDate() != null) {
            used = farm.getWaterTank().getUsageByDate().getOrDefault(date, 0.0);
        }
        return java.util.Map.of("used", used);
    }

    @GetMapping("/{farmId}/ai-suggestion")
    public java.util.Map<String,Object> getAiSuggestion(@PathVariable String farmId) {
        return plantService.getAiSuggestion(farmId);
    }

    @GetMapping("/{farmId}/rain-forecast")
    public java.util.Map<String,Boolean> getRainForecast(@PathVariable String farmId) {
        return java.util.Map.of("rain", plantService.getRainForecast(farmId));
    }

    @GetMapping("/{farmId}/today-intake")
    public java.util.Map<String,Integer> getTodayIntake(@PathVariable String farmId) {
        return java.util.Map.of("intake", plantService.getTodayIntake(farmId));
    }

    @GetMapping("/{farmId}/schedule")
    public java.util.Map<String,Object> getSchedule(@PathVariable String farmId) {
        return plantService.getSchedule(farmId);
    }

  @PostMapping("/{farmId}/plants/{plantId}/water")
      public Plant waterPlantAndDecreaseTank(
          @PathVariable String farmId,
          @PathVariable String plantId,
          @RequestParam int liters,
          @RequestBody(required = false) Map<String, Object> body // Accepts JSON body
      ) {
          Integer soilMoisture = null;
      if (body != null && body.containsKey("soilMoisture")) {
          Object val = body.get("soilMoisture");
          if (val instanceof Number) {
              soilMoisture = ((Number) val).intValue();
          }
      }
        
          return plantService.waterPlant(plantId, liters, soilMoisture);
      }

    // ✅ Get animals of a farm with treatment or vaccination today
    @GetMapping("/{farmId}/animals/today-schedule")
    public List<Animal> getAnimalsWithTodaySchedule(@PathVariable String farmId) {
        List<Animal> animals = farmService.getAnimalsByFarm(farmId);
        java.time.LocalDate today = java.time.LocalDate.now();
        List<Animal> result = new java.util.ArrayList<>();
        for (Animal animal : animals) {
            boolean match = false;
            if (animal.getNextVisit() != null && !animal.getNextVisit().isEmpty()) {
                try {
                    java.time.LocalDate visitDate = java.time.LocalDate.parse(animal.getNextVisit());
                    if (visitDate.equals(today)) match = true;
                } catch (Exception ignored) {}
            }
            if (animal.getVaccinationDate() != null && !animal.getVaccinationDate().isEmpty()) {
                try {
                    java.time.LocalDate vaccDate = java.time.LocalDate.parse(animal.getVaccinationDate());
                    if (vaccDate.equals(today)) match = true;
                } catch (Exception ignored) {}
            }
            if (match) result.add(animal);
        }
        return result;
    }

    // ✅ Get plants of a farm with treatment or harvest today
    @GetMapping("/{farmId}/plants/today-schedule")
    public List<Plant> getPlantsWithTodaySchedule(@PathVariable String farmId) {
        List<Plant> plants = farmService.getPlantsByFarm(farmId);
        java.time.LocalDate today = java.time.LocalDate.now();
        List<Plant> result = new java.util.ArrayList<>();
        for (Plant plant : plants) {
            boolean match = false;
            if (plant.getNextTreatment() != null && !plant.getNextTreatment().isEmpty()) {
                try {
                    java.time.LocalDate treatDate = java.time.LocalDate.parse(plant.getNextTreatment());
                    if (treatDate.equals(today)) match = true;
                } catch (Exception ignored) {}
            }
            if (plant.getExpectedHarvestDate() != null && !plant.getExpectedHarvestDate().isEmpty()) {
                try {
                    java.time.LocalDate harvestDate = java.time.LocalDate.parse(plant.getExpectedHarvestDate());
                    if (harvestDate.equals(today)) match = true;
                } catch (Exception ignored) {}
            }
            if (match) result.add(plant);
        }
        return result;
    }

    
        // Endpoint to feed an animal (add to todayIntakeLiters) — accepts optional JSON body and returns updated Animal
        @PostMapping("/{farmId}/animals/{animalId}/feed")
        public Animal feedAnimal(@PathVariable String farmId, @PathVariable String animalId, @RequestParam double liters, @RequestBody(required = false) Map<String, Object> body) {
            System.out.println("[FeedEndpoint] Received feed POST: farm="+farmId+" animal="+animalId+" liters="+liters+" body="+ (body!=null?body.toString():"null"));

            // Optionally parse additional fields from body (e.g., foodType)
            String foodType = null;
            if (body != null && body.containsKey("foodType")) {
                Object val = body.get("foodType");
                if (val instanceof String) foodType = (String) val;
            }
            if (foodType != null) {
                System.out.println("[FeedEndpoint] foodType=" + foodType);
            }

            // Update animal intake (this also updates the farm tanks in AnimalService)
            Animal updated = animalService.addIntake(animalId, liters);
            System.out.println("[FeedEndpoint] animalService.addIntake called for " + animalId + ", updated todayIntake=" + (updated!=null?updated.getTodayIntakeLiters():"null"));

            return updated;
        }

        // Simple admin/debug endpoint: set an animal's fullness to 1.0 by animalId only
        @PostMapping("/animals/{animalId}/set-fullness")
        public Animal setAnimalFullnessToOne(@PathVariable String animalId) {
            System.out.println("[Admin] set fullness=1.0 for animal=" + animalId);
            Animal a = animalService.getAnimalById(animalId);
            if (a == null) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Animal not found: " + animalId);
            }
            a.setFullness(1.0);
            return animalService.saveAnimal(a);
        }

        // Decrease fullness based on species (admin/debug). Dogs lose less fullness than cows/chickens.
        @PostMapping("/animals/{animalId}/decrease-fullness")
        public Animal decreaseAnimalFullnessBySpecies(@PathVariable String animalId) {
            System.out.println("[Admin] decrease fullness for animal=" + animalId);
            Animal a = animalService.getAnimalById(animalId);
            if (a == null) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Animal not found: " + animalId);
            }

            String species = a.getSpecies() != null ? a.getSpecies().toLowerCase() : "";
            double decrease;
            switch (species) {
                case "dog":
                    decrease = 0.005; // dogs get hungry slower
                    break;
                case "cow":
                    decrease = 0.025;
                    break;
                case "chicken":
                    decrease = 0.030;
                    break;
                case "sheep":
                    decrease = 0.015;
                    break;
                default:
                    decrease = 0.020;
            }

            double current = a.getFullness() != null ? a.getFullness() : 1.0;
            double updated = current - decrease;
            if (updated < 0.0) updated = 0.0;
            a.setFullness(updated);
            Animal saved = animalService.saveAnimal(a);
            System.out.println("[Admin] species=" + species + " fullness: " + current + " -> " + updated);
            return saved;
        }
}
