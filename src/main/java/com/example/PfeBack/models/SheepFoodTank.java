package com.example.PfeBack.models;

import java.util.HashMap;

public class SheepFoodTank extends Tank {
    public SheepFoodTank() {
        this.quantity = 0.0;
        this.usageByDate = new HashMap<>();
    }
    public SheepFoodTank(double quantity) {
        this.quantity = quantity;
        this.usageByDate = new HashMap<>();
    }
}
