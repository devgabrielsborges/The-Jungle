package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

@Getter
public enum MaterialType {
    WOOD("Madeira"),
    STONE("Pedra"),
    METAL_ORE("Minério de Metal"),
    METAL_INGOT("Barra de Metal"),
    FIBER("Fibra Vegetal"),
    CLAY("Argila"),
    LEATHER("Couro"),
    BONE("Osso"),
    OTHER("Outro");

    private final String displayName;

    MaterialType(String displayName) {
        this.displayName = displayName;
    }
}
