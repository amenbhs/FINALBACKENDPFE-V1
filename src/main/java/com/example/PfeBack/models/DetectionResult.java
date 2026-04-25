package com.example.PfeBack.models;

import java.util.List;

public class DetectionResult {
    private String label;
    private double confidence;
    private List<Double> bbox;

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public List<Double> getBbox() { return bbox; }
    public void setBbox(List<Double> bbox) { this.bbox = bbox; }
}