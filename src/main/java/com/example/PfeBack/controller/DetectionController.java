package com.example.PfeBack.controller;

import com.example.PfeBack.models.DetectionResult;
import com.example.PfeBack.Services.YoloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/detect")
public class DetectionController {

    @Autowired
    private YoloService yoloService;

    @PostMapping
    public ResponseEntity<List<DetectionResult>> detect(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "model", defaultValue = "plant") String model
    ) throws Exception {
        List<DetectionResult> results = yoloService.detect(file, model);
        return ResponseEntity.ok(results);
    }
}