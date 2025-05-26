package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

@Getter
public enum ToolType {
    AXE("Machado"),
    KNIFE("Faca"),
    PICKAXE("Picareta"),
    SHOVEL("Pá"),
    HAMMER("Martelo"),
    FISHING_ROD("Vara de Pescar"),
    LIGHTER("Isqueiro"), // "Isqueiro"
    FLASHLIGHT("Lanterna"), // "Lanterna"
    POT("Panela"), // For cooking
    REPAIR_KIT("Kit de Reparo"),
    SEWING_KIT("Kit de Costura"),
    WATER_FILTER("Filtro de Água Portátil"),
    OTHER("Outra Ferramenta");

    private final String displayName;

    ToolType(String displayName) {
        this.displayName = displayName;
    }
}
