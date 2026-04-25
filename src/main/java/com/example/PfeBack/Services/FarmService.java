
package com.example.PfeBack.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.PfeBack.Services.AnimalFactory;
import com.example.PfeBack.models.Animal;
import com.example.PfeBack.models.Farm;
import com.example.PfeBack.models.Plant;
import com.example.PfeBack.repository.AnimalRepository;
import com.example.PfeBack.repository.FarmRepository;
import com.example.PfeBack.repository.PlantRepository;
import com.example.PfeBack.repository.UserRepository;

@Service
public class FarmService {

    private final FarmRepository farmRepository;
    private final AnimalRepository animalRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;


@Autowired
public FarmService(FarmRepository farmRepository,
                   AnimalRepository animalRepository,
                   PlantRepository plantRepository,
                   UserRepository userRepository) {
    this.farmRepository = farmRepository;
    this.animalRepository = animalRepository;
    this.plantRepository = plantRepository;
    this.userRepository = userRepository;
}
      public Farm saveFarm(Farm farm) {
        return farmRepository.save(farm);
    }
public Farm createFarmForUser(String userId, Farm farm) {
    // Normalize / initialize lists
    if (farm.getAnimalIds() == null) farm.setAnimalIds(new java.util.ArrayList<>());
    if (farm.getPlantIds() == null) farm.setPlantIds(new java.util.ArrayList<>());
    farm.setUserId(userId);
    Farm saved = farmRepository.save(farm);

    userRepository.findById(userId).ifPresent(user -> {
        user.setFarm(saved);
        userRepository.save(user); // ensure persisted
    });

    return saved;
}

public void deleteFarm(String farmId) {
    farmRepository.deleteById(farmId);
}

public Farm getFarmById(String farmId) {
    return farmRepository.findById(farmId).orElse(null);
}

public Farm getFarmByUserId(String userId) {
    return farmRepository.findByUserId(userId).orElse(null);
}

public java.util.List<Animal> getAnimalsByFarm(String farmId) {
    return animalRepository.findByFarmId(farmId);
}

public Animal addAnimalToFarm(String farmId, Animal animal) {
    Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new RuntimeException("Farm not found: " + farmId));

    // Use factory to create concrete subclass when possible
    Animal toSave = AnimalFactory.createFrom(animal);
    // Set foreign key
    toSave.setFarmId(farmId);
    Animal savedAnimal = animalRepository.save(toSave);

    // Update farm’s animalIds
    if (farm.getAnimalIds() == null) {
        farm.setAnimalIds(new java.util.ArrayList<>());
    }
    farm.getAnimalIds().add(savedAnimal.getId());
    System.out.println("Updated farm's animalIds: " + farm.getAnimalIds());
    farmRepository.save(farm);

    return savedAnimal;
}

public java.util.List<Plant> getPlantsByFarm(String farmId) {
    return plantRepository.findByFarmId(farmId);
}

public Plant addPlantToFarm(String farmId, Plant plant) {
    Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new RuntimeException("Farm not found: " + farmId));

    plant.setFarmId(farmId);
    Plant savedPlant = plantRepository.save(plant);

    if (farm.getPlantIds() == null) {
        farm.setPlantIds(new java.util.ArrayList<>());
    }
    farm.getPlantIds().add(savedPlant.getId());
    farmRepository.save(farm);

    return savedPlant; 
  } 

    // Food tank helpers
    public double getCowFoodLevel(String farmId) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getCowFoodTank() == null) throw new RuntimeException("Farm or cow tank not found");
        return farm.getCowFoodTank().getQuantity();
    }

    public double getDogFoodLevel(String farmId) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getDogFoodTank() == null) throw new RuntimeException("Farm or dog tank not found");
        return farm.getDogFoodTank().getQuantity();
    }

    public double getChickenFoodLevel(String farmId) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getChickenFoodTank() == null) throw new RuntimeException("Farm or chicken tank not found");
        return farm.getChickenFoodTank().getQuantity();
    }

    public double getSheepFoodLevel(String farmId) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getSheepFoodTank() == null) throw new RuntimeException("Farm or sheep tank not found");
        return farm.getSheepFoodTank().getQuantity();
    }

    public void refillCowFoodTank(String farmId, double amount) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getCowFoodTank() == null) throw new RuntimeException("Farm or cow tank not found");
        farm.getCowFoodTank().setQuantity(farm.getCowFoodTank().getQuantity() + amount);
        saveFarm(farm);
    }

    public void refillDogFoodTank(String farmId, double amount) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getDogFoodTank() == null) throw new RuntimeException("Farm or dog tank not found");
        farm.getDogFoodTank().setQuantity(farm.getDogFoodTank().getQuantity() + amount);
        saveFarm(farm);
    }

    public void refillChickenFoodTank(String farmId, double amount) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getChickenFoodTank() == null) throw new RuntimeException("Farm or chicken tank not found");
        farm.getChickenFoodTank().setQuantity(farm.getChickenFoodTank().getQuantity() + amount);
        saveFarm(farm);
    }

    public void refillSheepFoodTank(String farmId, double amount) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getSheepFoodTank() == null) throw new RuntimeException("Farm or sheep tank not found");
        farm.getSheepFoodTank().setQuantity(farm.getSheepFoodTank().getQuantity() + amount);
        saveFarm(farm);
    }

    public double getCowFoodUsageTotal(String farmId) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getCowFoodTank() == null) throw new RuntimeException("Farm or cow tank not found");
        double total = 0.0;
        if (farm.getCowFoodTank().getUsageByDate() != null) for (double v : farm.getCowFoodTank().getUsageByDate().values()) total += v;
        return total;
    }

    public double getDogFoodUsageTotal(String farmId) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getDogFoodTank() == null) throw new RuntimeException("Farm or dog tank not found");
        double total = 0.0;
        if (farm.getDogFoodTank().getUsageByDate() != null) for (double v : farm.getDogFoodTank().getUsageByDate().values()) total += v;
        return total;
    }

    public double getChickenFoodUsageTotal(String farmId) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getChickenFoodTank() == null) throw new RuntimeException("Farm or chicken tank not found");
        double total = 0.0;
        if (farm.getChickenFoodTank().getUsageByDate() != null) for (double v : farm.getChickenFoodTank().getUsageByDate().values()) total += v;
        return total;
    }

    public double getSheepFoodUsageTotal(String farmId) {
        Farm farm = getFarmById(farmId);
        if (farm == null || farm.getSheepFoodTank() == null) throw new RuntimeException("Farm or sheep tank not found");
        double total = 0.0;
        if (farm.getSheepFoodTank().getUsageByDate() != null) for (double v : farm.getSheepFoodTank().getUsageByDate().values()) total += v;
        return total;
    }
}