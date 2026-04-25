package com.example.PfeBack.models;

import java.util.HashMap;

public class WaterTank extends Tank {
    public WaterTank() {
        this.quantity = 0.0;
        this.usageByDate = new HashMap<>();
    }
    public WaterTank(double quantity) {
        this.quantity = quantity;
        this.usageByDate = new HashMap<>();
    }
}
