package com.example.PfeBack.models;

import java.util.HashMap;

public class DogFoodTank extends Tank {
    public DogFoodTank() {
        this.quantity = 0.0;
        this.usageByDate = new HashMap<>();
    }
    public DogFoodTank(double quantity) {
        this.quantity = quantity;
        this.usageByDate = new HashMap<>();
    }
}
