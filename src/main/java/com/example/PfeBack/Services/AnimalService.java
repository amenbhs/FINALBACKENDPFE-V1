package com.example.PfeBack.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PfeBack.repository.AnimalRepository;
import com.example.PfeBack.Services.FarmService;
import com.example.PfeBack.models.Animal;
import com.example.PfeBack.models.Farm;

@Service
public class AnimalService {
	private final AnimalRepository repository;
	private final FarmService farmService;

	public AnimalService(AnimalRepository repository, FarmService farmService) {
		this.repository = repository;
		this.farmService = farmService;
	}

	public List<Animal> getAllAnimals() {
		return repository.findAll();
	}

	public Animal getAnimalById(String id) {
		return repository.findById(id).orElse(null);
	}

	public Animal saveAnimal(Animal animal) {
		return repository.save(animal);
	}

	public void deleteAnimal(String id) {
		repository.deleteById(id);
	}
	public List<Animal> getAnimalsByFarmId(String farmId) {
		return repository.findByFarmId(farmId);
	}

	// Water intake tracking methods
	public Animal addIntake(String animalId, double liters) {
		return repository.findById(animalId).map(a -> {
			System.out.println("[AnimalService] addIntake called for animalId=" + animalId + " liters=" + liters);
			String today = java.time.LocalDate.now().toString();
			if (a.getIntakeUpdatedAt() == null || !a.getIntakeUpdatedAt().equals(today)) {
				a.setTodayIntakeLiters(0.0);
				a.setIntakeUpdatedAt(today);
			}
			double current = a.getTodayIntakeLiters() != null ? a.getTodayIntakeLiters() : 0.0;
			a.setTodayIntakeLiters(current + liters);
      System.out.println("Updated intake for animal " + animalId + ": " + a.getTodayIntakeLiters() + " liters");
			// Decrement appropriate food tank on farm and record usage
			try {
				if (a.getFarmId() != null) {
					Farm farm = farmService.getFarmById(a.getFarmId());
					if (farm != null) {
						String species = a.getSpecies() != null ? a.getSpecies().toLowerCase() : "";
						System.out.println("[AnimalService] Feeding animalId=" + animalId + " species=" + species + " on farm=" + a.getFarmId());
						switch (species) {
							case "cow":
								if (farm.getCowFoodTank() != null) {
									double before = farm.getCowFoodTank().getQuantity();
									double newQ = before - liters;
									farm.getCowFoodTank().setQuantity(newQ);
									java.util.Map<String, Double> map = farm.getCowFoodTank().getUsageByDate();
									if (map == null) { map = new java.util.HashMap<>(); farm.getCowFoodTank().setUsageByDate(map); }
									map.put(today, map.getOrDefault(today, 0.0) + liters);
									System.out.println("[AnimalService] Cow tank before=" + before + " after=" + newQ + " usedTodayAdded=" + liters);
								}
								break;
							case "dog":
								if (farm.getDogFoodTank() != null) {
									double before = farm.getDogFoodTank().getQuantity();
									double newQ = before - liters;
									farm.getDogFoodTank().setQuantity(newQ);
									java.util.Map<String, Double> map = farm.getDogFoodTank().getUsageByDate();
									if (map == null) { map = new java.util.HashMap<>(); farm.getDogFoodTank().setUsageByDate(map); }
									map.put(today, map.getOrDefault(today, 0.0) + liters);
									System.out.println("[AnimalService] Dog tank before=" + before + " after=" + newQ + " usedTodayAdded=" + liters);
								}
								break;
							case "chicken":
								if (farm.getChickenFoodTank() != null) {
									double before = farm.getChickenFoodTank().getQuantity();
									double newQ = before - liters;
									farm.getChickenFoodTank().setQuantity(newQ);
									java.util.Map<String, Double> map = farm.getChickenFoodTank().getUsageByDate();
									if (map == null) { map = new java.util.HashMap<>(); farm.getChickenFoodTank().setUsageByDate(map); }
									map.put(today, map.getOrDefault(today, 0.0) + liters);
									System.out.println("[AnimalService] Chicken tank before=" + before + " after=" + newQ + " usedTodayAdded=" + liters);
								}
								break;
							case "sheep":
								if (farm.getSheepFoodTank() != null) {
									double before = farm.getSheepFoodTank().getQuantity();
									double newQ = before - liters;
									farm.getSheepFoodTank().setQuantity(newQ);
									java.util.Map<String, Double> map = farm.getSheepFoodTank().getUsageByDate();
									if (map == null) { map = new java.util.HashMap<>(); farm.getSheepFoodTank().setUsageByDate(map); }
									map.put(today, map.getOrDefault(today, 0.0) + liters);
									System.out.println("[AnimalService] Sheep tank before=" + before + " after=" + newQ + " usedTodayAdded=" + liters);
								}
								break;
							default:
								// Unknown species — no tank update
								break;
						}
						farmService.saveFarm(farm);
					}
				}
			} catch (Exception e) {
				// swallow to avoid breaking feeding call; log if desired
				System.err.println("Failed to update food tank: " + e.getMessage());
			}

			// recompute fullness
			if (a.getRecommendedIntakeLiters() != null && a.getRecommendedIntakeLiters() > 0) {
				double full = (a.getTodayIntakeLiters() != null ? a.getTodayIntakeLiters() : 0.0) / a.getRecommendedIntakeLiters();
				if (full > 1.0) full = 1.0;
				a.setFullness(full);
			}
			return repository.save(a);
		}).orElseThrow(() -> new RuntimeException("Animal not found: " + animalId));
	}

	public Animal setRecommendedIntake(String animalId, double liters) {
		return repository.findById(animalId).map(a -> {
			a.setRecommendedIntakeLiters(liters);
			// recompute fullness
			if (a.getTodayIntakeLiters() != null && a.getRecommendedIntakeLiters() != null && a.getRecommendedIntakeLiters() > 0) {
				double full = a.getTodayIntakeLiters() / a.getRecommendedIntakeLiters();
				if (full > 1.0) full = 1.0;
				a.setFullness(full);
			}
			return repository.save(a);
		}).orElseThrow(() -> new RuntimeException("Animal not found: " + animalId));
	}

	public Animal resetIntake(String animalId) {
		return repository.findById(animalId).map(a -> {
			a.setTodayIntakeLiters(0.0);
			a.setIntakeUpdatedAt(java.time.LocalDate.now().toString());
			// recompute fullness
			a.setFullness(0.0);
			return repository.save(a);
		}).orElseThrow(() -> new RuntimeException("Animal not found: " + animalId));
	}
  
}
