package com.example.PfeBack.Services;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.PfeBack.models.Animal;
import com.example.PfeBack.models.MedicalProcess;
import com.example.PfeBack.models.MedicalStep;
import com.example.PfeBack.repository.AnimalRepository;
import com.example.PfeBack.repository.MedicalProcessRepository;

@Service
public class MedicalProcessService {
    private final MedicalProcessRepository repository;
    private final AnimalRepository animalRepository;

    @Autowired
    public MedicalProcessService(MedicalProcessRepository repository, AnimalRepository animalRepository) {
        this.repository = repository;
        this.animalRepository = animalRepository;
    }

    public List<MedicalProcess> getAllProcesses() {
        return repository.findAll();
    }

    public MedicalProcess getProcessById(String id) {
        return repository.findById(id).orElse(null);
    }

    public MedicalProcess saveProcess(MedicalProcess process) {
        return repository.save(process);
    }

    public void deleteProcess(String id) {
        repository.deleteById(id);
    }

    // Get all medical processes for a specific animal
    public List<MedicalProcess> getProcessesByAnimalId(String animalId) {
        Animal animal = animalRepository.findById(animalId).orElse(null);
        if (animal != null && animal.getMedicalProcesses() != null) {
            return animal.getMedicalProcesses();
        }
        return new ArrayList<>();
    }

    // Add a new medical step to the latest process of an animal, or create a new process if none exists
    public MedicalProcess addStepToAnimal(String animalId, Object stepRequestObj) {
        Animal animal = animalRepository.findById(animalId).orElse(null);
        if (animal == null) return null;

        // StepRequest is an inner class of the controller, so use reflection or cast
        String type = null;
        String description = null;
        try {
            java.lang.reflect.Field typeField = stepRequestObj.getClass().getField("type");
            java.lang.reflect.Field descField = stepRequestObj.getClass().getField("description");
            type = (String) typeField.get(stepRequestObj);
            description = (String) descField.get(stepRequestObj);
        } catch (Exception e) {
            return null;
        }

        MedicalStep step = new MedicalStep();
        step.setAction(type);
        step.setNotes(description);
        step.setDate(java.time.LocalDate.now());

        List<MedicalProcess> processes = animal.getMedicalProcesses();
        if (processes == null) {
            processes = new ArrayList<>();
        }
        MedicalProcess process;
        if (processes.isEmpty()) {
            process = new MedicalProcess();
            process.setDiagnosis("");
            process.setStartDate(java.time.LocalDate.now());
            process.setStatus("En cours");
            process.setSteps(new ArrayList<>());
            processes.add(process);
        } else {
            process = processes.get(processes.size() - 1);
        }
        if (process.getSteps() == null) process.setSteps(new ArrayList<>());
        process.getSteps().add(step);
        animal.setMedicalProcesses(processes);
        animalRepository.save(animal);
        return process;
    }
}
