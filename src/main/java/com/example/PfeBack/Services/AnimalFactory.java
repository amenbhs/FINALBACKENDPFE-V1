package com.example.PfeBack.Services;

import com.example.PfeBack.models.Animal;
import com.example.PfeBack.models.Chicken;
import com.example.PfeBack.models.Cow;
import com.example.PfeBack.models.Dog;
import com.example.PfeBack.models.Sheep;

public class AnimalFactory {
    public static Animal createFrom(Animal input) {
        if (input == null) return null;
        String species = input.getSpecies();
        if (species == null) species = "";
        species = species.toLowerCase();
        Animal out;
        switch (species) {
            case "cow":
                out = new Cow();
                break;
            case "dog":
                out = new Dog();
                break;
            case "chicken":
            case "hen":
                out = new Chicken();
                break;
            case "sheep":
                out = new Sheep();
                break;
            default:
                // default to base Animal to avoid breaking existing data
                out = new Animal();
                out.setSpecies(input.getSpecies());
                break;
        }
        // copy common fields (simple shallow copy)
        out.setName(input.getName());
        out.setImageUrl(input.getImageUrl());
        out.setBreed(input.getBreed());
        out.setBirthDate(input.getBirthDate());
        out.setSex(input.getSex());
        out.setWeight(input.getWeight());
        out.setHealthStatus(input.getHealthStatus());
        out.setFeeding(input.getFeeding());
        out.setActivity(input.getActivity());
        out.setNotes(input.getNotes());
        out.setVet(input.getVet());
        out.setNextVisit(input.getNextVisit());
        out.setVaccinationDate(input.getVaccinationDate());
        out.setMedicalProcesses(input.getMedicalProcesses());
        out.setTodayIntakeLiters(input.getTodayIntakeLiters());
        out.setRecommendedIntakeLiters(input.getRecommendedIntakeLiters());
        out.setIntakeUpdatedAt(input.getIntakeUpdatedAt());
        out.setUserId(input.getUserId());
        // set or compute fullness: prefer explicit input.fullness, otherwise compute from intake/recommended
        out.setFullness(0.0);

        return out;
    }
}
