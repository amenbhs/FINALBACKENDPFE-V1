package com.example.PfeBack.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PfeBack.Services.CareTipService;
import com.example.PfeBack.models.CareTip;
import com.example.PfeBack.repository.CareTipRepository;

@RestController
@RequestMapping("/api/care-tips")
public class CareTipController {

    @Autowired
    private CareTipService careTipService;
    @Autowired
    private CareTipRepository careTipRepository;

    @GetMapping
    public ResponseEntity<List<CareTip>> getAllCareTips() {
        List<CareTip> careTips = careTipService.getAllCareTips();
        return ResponseEntity.ok(careTips);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareTip> getCareTipById(@PathVariable String id) {
        Optional<CareTip> careTip = careTipService.getCareTipById(id);
        return careTip.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    @PostMapping
    public ResponseEntity<CareTip> createCareTip(@RequestBody CareTip careTip) {
        CareTip createdCareTip = careTipService.createCareTip(careTip);
        return ResponseEntity.ok(createdCareTip);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CareTip> updateCareTip(@PathVariable String id, @RequestBody CareTip updatedCareTip) {
        try {
            CareTip careTip = careTipService.updateCareTip(id, updatedCareTip);
            return ResponseEntity.ok(careTip);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareTip(@PathVariable String id) {
        careTipService.deleteCareTip(id);
        return ResponseEntity.ok().build();
    }


}