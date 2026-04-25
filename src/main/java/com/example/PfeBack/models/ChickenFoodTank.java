package com.example.PfeBack.models;

import java.util.HashMap;

public class ChickenFoodTank extends Tank {
    public ChickenFoodTank() {
        this.quantity = 0.0;
        this.usageByDate = new HashMap<>();
    }
    public ChickenFoodTank(double quantity) {
        this.quantity = quantity;
        this.usageByDate = new HashMap<>();
    }
}
