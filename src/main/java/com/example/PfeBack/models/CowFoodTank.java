package com.example.PfeBack.models;

import java.util.HashMap;

public class CowFoodTank extends Tank {
    public CowFoodTank() {
        this.quantity = 0.0;
        this.usageByDate = new HashMap<>();
    }
    public CowFoodTank(double quantity) {
        this.quantity = quantity;
        this.usageByDate = new HashMap<>();
    }
}
