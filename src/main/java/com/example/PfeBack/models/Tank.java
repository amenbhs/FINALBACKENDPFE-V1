package com.example.PfeBack.models;

import java.util.Map;

public abstract class Tank {
    protected double quantity;
    protected Map<String, Double> usageByDate; // key: date (yyyy-MM-dd), value: quantity used

    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public Map<String, Double> getUsageByDate() {
        return usageByDate;
    }
    public void setUsageByDate(Map<String, Double> usageByDate) {
        this.usageByDate = usageByDate;
    }
}
