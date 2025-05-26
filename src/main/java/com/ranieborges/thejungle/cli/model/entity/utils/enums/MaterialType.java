package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

@Getter
public enum MaterialType {
    WOOD("Madeira"),
    STONE("Pedra"),
    METAL_ORE("Minério de Metal"), // Raw ore
    METAL_INGOT("Barra de Metal"), // Processed metal
    FIBER("Fibra Vegetal"),      // For ropes, cloth
    CLAY("Argila"),
    LEATHER("Couro"),
    BONE("Osso"),
    OTHER("Outro");

    private final String displayName;

    MaterialType(String displayName) {
        this.displayName = displayName;
    }
}
