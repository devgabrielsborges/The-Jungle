package com.ranieborges.thejungle.cli.model.events.utils.enums;

import lombok.Getter;

@Getter
public enum ClimateType {
    BLIZZARD("Nevasca"),
    HEAVY_RAIN("Chuva Forte"),
    HEAT_WAVE("Onda de Calor"),
    THICK_FOG("Névoa Densa");

    private final String displayName;
    ClimateType(String displayName) { this.displayName = displayName; }
}
