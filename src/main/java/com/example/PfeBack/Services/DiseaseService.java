package com.example.PfeBack.Services;

import com.example.PfeBack.models.Disease;
import com.example.PfeBack.repository.DiseaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiseaseService {

    @Autowired
    private DiseaseRepository diseaseRepository;

    public List<Disease> getAllDiseases() {
        return diseaseRepository.findAll();
    }

    public Disease getDiseaseById(String id) {
        return diseaseRepository.findById(id).orElse(null);
    }

    public Disease createDisease(Disease disease) {
        return diseaseRepository.save(disease);
    }

    public Disease updateDisease(String id, Disease disease) {
        disease.setId(id);
        return diseaseRepository.save(disease);
    }

    public List<Disease> searchDiseases(String keyword) {
        return diseaseRepository.findByNameContaining(keyword);
    }

    public void deleteDisease(String id) {
        diseaseRepository.deleteById(id);
    }
}
