package com.example.PfeBack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PfeBack.Services.MedicalProcessService;
import com.example.PfeBack.models.MedicalProcess;

@RestController
@RequestMapping("/api/medical-process")
public class MedicalProcessController {
    private final MedicalProcessService service;

    public MedicalProcessController(MedicalProcessService service) {
        this.service = service;
    }

    // Get all medical processes (not used by frontend, but kept)
    @GetMapping
    public List<MedicalProcess> getAll() {
        return service.getAllProcesses();
    }

    // Get all medical processes for a specific animal
    @GetMapping("/{animalId}")
    public List<MedicalProcess> getByAnimalId(@PathVariable String animalId) {
        // You may need to implement this in the service/repository
        return service.getProcessesByAnimalId(animalId);
    }

    // Add a new medical process (not used by frontend, but kept)
    @PostMapping
    public MedicalProcess create(@RequestBody MedicalProcess process) {
        return service.saveProcess(process);
    }

    // Add a new medical step to a process (for a specific animal)
    @PostMapping("/{animalId}/steps")
    public MedicalProcess addStepToAnimal(@PathVariable String animalId, @RequestBody StepRequest stepRequest) {
        // You may need to implement this in the service
        return service.addStepToAnimal(animalId, stepRequest);
    }

    // Delete a medical process by id (not used by frontend, but kept)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteProcess(id);
    }

    // DTO for step request
    public static class StepRequest {
        public String type;
        public String description;
    }
}
