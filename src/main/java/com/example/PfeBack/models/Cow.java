package com.example.PfeBack.models;

import org.springframework.data.annotation.TypeAlias;

@TypeAlias("cow")
public class Cow extends Animal {
    public Cow() {
        super();
        this.setSpecies("cow");
    }
}
