
package com.example.PfeBack.controller;

import java.util.List;

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

import com.example.PfeBack.Services.AnimalService;
import com.example.PfeBack.models.Animal;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {
	private final AnimalService animalService;

	public AnimalController(AnimalService animalService) {
		this.animalService = animalService;
	}

	@GetMapping
	public List<Animal> getAllAnimals() {
		return animalService.getAllAnimals();
	}

	@GetMapping("/{id}")
	public Animal getAnimalById(@PathVariable String id) {
		return animalService.getAnimalById(id);
	}

	@GetMapping("/{id}/species")
	public String getAnimalSpecies(@PathVariable String id) {
		Animal animal = animalService.getAnimalById(id);
		return animal != null ? animal.getSpecies() : null;
	}

	@PostMapping
	public Animal addAnimal(@RequestBody Animal animal) {
		return animalService.saveAnimal(animal);
	}

	@PutMapping("/{id}")
	public Animal updateAnimal(@PathVariable String id, @RequestBody Animal animal) {
		animal.setId(id);
		return animalService.saveAnimal(animal);
	}

	@DeleteMapping("/{id}")
	public void deleteAnimal(@PathVariable String id) {
		animalService.deleteAnimal(id);
	}
  	// ...existing code...

	@GetMapping("/alerts/{userId}/{farmId}")
	public List<String> getAnimalAlerts(@PathVariable String userId, @PathVariable String farmId) {
		List<Animal> animals = animalService.getAllAnimals();
		List<String> alerts = new java.util.ArrayList<>();
		java.time.LocalDate today = java.time.LocalDate.now();
		for (Animal animal : animals) {
			// Only include animals that belong to the specified farm and user
			if (animal.getFarmId() != null && animal.getFarmId().equals(farmId)
				&& animal.getUserId() != null && animal.getUserId().equals(userId)) {
				// Check vaccinationDate
				if (animal.getVaccinationDate() != null && !animal.getVaccinationDate().isEmpty()) {
					try {
						java.time.LocalDate vaccDate = java.time.LocalDate.parse(animal.getVaccinationDate());
						if (!vaccDate.isBefore(today) && !vaccDate.isAfter(today.plusDays(7))) {
							alerts.add("Vaccination for " + animal.getName() + " is due on " + animal.getVaccinationDate());
						} else if (vaccDate.isBefore(today)) {
							alerts.add("Vaccination for " + animal.getName() + " is OVERDUE! Was scheduled for " + animal.getVaccinationDate());
						}
					} catch (Exception ignored) {}
				}
				// Check nextVisit
				if (animal.getNextVisit() != null && !animal.getNextVisit().isEmpty()) {
					try {
						java.time.LocalDate visitDate = java.time.LocalDate.parse(animal.getNextVisit());
						if (!visitDate.isBefore(today) && !visitDate.isAfter(today.plusDays(7))) {
							alerts.add("Vet visit for " + animal.getName() + " is scheduled on " + animal.getNextVisit());
						} else if (visitDate.isBefore(today)) {
							alerts.add("Vet visit for " + animal.getName() + " is OVERDUE! Was scheduled for " + animal.getNextVisit());
						}
					} catch (Exception ignored) {}
				}
			}
		}
		System.out.println("Animal alerts: " + alerts);
		return alerts;
	}

	// --- Water intake mutations ---
	@PatchMapping("/{id}/intake/add")
	public Animal addIntake(@PathVariable String id, @RequestParam double liters) {
		return animalService.addIntake(id, liters);
	}

	@PatchMapping("/{id}/intake/recommended")
	public Animal setRecommended(@PathVariable String id, @RequestParam double liters) {
		return animalService.setRecommendedIntake(id, liters);
	}

	@PatchMapping("/{id}/intake/reset")
	public Animal resetIntake(@PathVariable String id) {
		return animalService.resetIntake(id);
	}
}
