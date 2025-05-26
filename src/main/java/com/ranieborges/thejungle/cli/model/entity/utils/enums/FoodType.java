package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

@Getter
public enum FoodType {
    FRUIT("Fruta"),
    VEGETABLE("Vegetal"),
    MEAT_RAW("Carne Crua"),
    MEAT_COOKED("Carne Cozida"),
    CANNED("Enlatado"),
    GRAIN("Grão"),
    MUSHROOM("Cogumelo"),
    OTHER("Outro");

    private final String displayName;

    FoodType(String displayName) {
        this.displayName = displayName;
    }
}