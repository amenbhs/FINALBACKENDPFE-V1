// CareTipService.java
package com.example.PfeBack.Services;

import com.example.PfeBack.models.CareTip;
import com.example.PfeBack.repository.CareTipRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CareTipService {

    @Autowired
    private CareTipRepository careTipRepository;

    public List<CareTip> getAllCareTips() {
        return careTipRepository.findAll();
    }

    public Optional<CareTip> getCareTipById(String id) {
        return careTipRepository.findById(id);
    }

    public CareTip createCareTip(CareTip careTip) {
        return careTipRepository.save(careTip);
    }

    public CareTip updateCareTip(String id, CareTip updatedCareTip) {
        return careTipRepository.findById(id)
                .map(careTip -> {
                    careTip.setTitle(updatedCareTip.getTitle());
                    careTip.setDescription(updatedCareTip.getDescription());
                    careTip.setArticle(updatedCareTip.getArticle());
                    return careTipRepository.save(careTip);
                })
                .orElseThrow(() -> new RuntimeException("CareTip not found with id: " + id));
    }

    public void deleteCareTip(String id) {
        careTipRepository.deleteById(id);
    }
}