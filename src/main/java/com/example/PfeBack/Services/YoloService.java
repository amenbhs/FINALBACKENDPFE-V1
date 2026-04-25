package com.example.PfeBack.Services;

import com.example.PfeBack.models.DetectionResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class YoloService {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<DetectionResult> detect(MultipartFile file, String modelType) throws Exception {
        // Sauvegarde image temporaire
        File tempFile = File.createTempFile("upload-", ".jpg");
        file.transferTo(tempFile);

        String scriptPath = "src/main/resources/python/detector.py";

        // Exécution : python detector.py <modelType> <imagePath>
        ProcessBuilder pb = new ProcessBuilder("python", scriptPath, modelType, tempFile.getAbsolutePath());
        pb.redirectErrorStream(true);

        Process process = pb.start();
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();

        tempFile.delete();

        if (exitCode != 0) {
            throw new IOException("Python detection failed: " + output);
        }

        return mapper.readValue(output, new TypeReference<List<DetectionResult>>() {});
    }
}