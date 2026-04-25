package com.example.PfeBack.models;

import org.springframework.data.annotation.TypeAlias;

@TypeAlias("dog")
public class Dog extends Animal {
    public Dog() {
        super();
        this.setSpecies("dog");
    }
}
