package com.example.PfeBack.controller;

import com.example.PfeBack.Services.DiseaseService;
import com.example.PfeBack.models.Article;
import com.example.PfeBack.models.Disease;
import com.example.PfeBack.repository.DiseaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/diseases")
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;
    @Autowired
    private DiseaseRepository diseaseRepository;

    @GetMapping("/test")
    public String test() {
        return "Public endpoint works!";
    }

    @GetMapping
    public List<Disease> getAllDiseases() {
        return diseaseService.getAllDiseases();
    }

    @GetMapping("/{id}")
    public Disease getDiseaseById(@PathVariable String id) {
        return diseaseService.getDiseaseById(id);
    }

    @PostMapping
    public Disease createDisease(@RequestBody Disease disease) {
        return diseaseService.createDisease(disease);
    }

    @PutMapping("/{id}")
    public Disease updateDisease(@PathVariable String id, @RequestBody Disease disease) {
        return diseaseService.updateDisease(id, disease);
    }

    @DeleteMapping("/{id}")
    public void deleteDisease(@PathVariable String id) {
        diseaseService.deleteDisease(id);
    }

    @GetMapping("/search")
    public List<Disease> searchDiseases(@RequestParam String keyword) {
        return diseaseService.searchDiseases(keyword);
    }
    @PostMapping("/init")
    public ResponseEntity<String> initializeDiseases() {
        List<Disease> diseases = List.of(
                new Disease(
                        null,
                        "Rabies",
                        "Rabies is a fatal viral disease that affects the central nervous system of mammals, including dogs and humans. It is transmitted through the saliva of infected animals, usually via bites.",
                        List.of(
                                "Excessive drooling",
                                "Aggressive behavior",
                                "Fear of water (hydrophobia)",
                                "Paralysis",
                                "Seizures",
                                "Lethargy"
                        ),
                        List.of(
                                "Immediate cleansing of wounds",
                                "Post-exposure prophylaxis (PEP) vaccination",
                                "Supportive care for symptomatic animals",
                                "Euthanasia in advanced stages to prevent suffering"
                        ),
                        new Article(
                                "Rabies in Dogs: Causes, Symptoms, and Prevention",
                                "Rabies is a viral disease transmitted primarily through bites from infected animals. It attacks the central nervous system, leading to behavioral changes, aggression, and eventual paralysis.\n\nSymptoms include excessive drooling, aggression, fear of water, paralysis, and seizures.\n\nThere is no cure once symptoms appear; prevention through vaccination is crucial. Post-exposure prophylaxis is essential if exposure occurs.\n\nGood vaccination programs and responsible pet ownership are key preventive measures."
                        )
                ),
                new Disease(
                        null,
                        "Lyme Disease",
                        "Lyme Disease is a tick-borne bacterial infection caused by Borrelia burgdorferi. It affects both dogs and humans and can lead to joint pain and other systemic issues.",
                        List.of(
                                "Lameness",
                                "Swollen joints",
                                "Fever",
                                "Loss of appetite",
                                "Lethargy"
                        ),
                        List.of(
                                "Antibiotic treatment (usually doxycycline)",
                                "Pain management",
                                "Tick prevention methods (collars, spot-on treatments)",
                                "Regular tick checks"
                        ),
                        new Article(
                                "Lyme Disease in Dogs: Causes, Symptoms, and Treatments",
                                "Lyme Disease is caused by the bacterium Borrelia burgdorferi, transmitted through tick bites. It primarily affects dogs' joints and can cause fever, swollen joints, lameness, and lethargy.\n\nTreatment includes antibiotics, especially doxycycline, and supportive care like pain management.\n\nPrevention is vital and involves tick control products, vaccines, and routine tick checks after outdoor activities."
                        )
                ),
                new Disease(
                        null,
                        "Heartworm Disease",
                        "Heartworm Disease is a serious parasitic condition in dogs, caused by Dirofilaria immitis. It is transmitted by mosquito bites and can result in heart and lung damage if left untreated.",
                        List.of(
                                "Mild persistent cough",
                                "Fatigue after mild exercise",
                                "Weight loss",
                                "Swollen abdomen",
                                "Difficulty breathing"
                        ),
                        List.of(
                                "Administration of heartworm medications",
                                "Restricted physical activity during treatment",
                                "Surgical removal of adult heartworms in severe cases",
                                "Preventive monthly medications"
                        ),
                        new Article(
                                "Heartworm Disease in Dogs: Causes, Symptoms, and Treatments",
                                "Heartworm Disease is a life-threatening condition caused by parasitic worms transmitted by mosquitoes. It affects the heart, lungs, and blood vessels of dogs.\n\nSymptoms include coughing, fatigue, weight loss, and breathing difficulties.\n\nTreatment requires specific heartworm medications and restricting the dog's activity to avoid complications. In severe cases, surgical intervention may be needed.\n\nMonthly heartworm preventatives and mosquito control are essential preventive strategies."
                        )
                )
        );


        diseaseRepository.saveAll(diseases);

        return ResponseEntity.ok("Diseases successfully initialized and inserted into MongoDB.");
    }
}

