package com.ranieborges.thejungle.cli.model.events.utils.enums;

import lombok.Getter;

@Getter
public enum AfflictionType {
    MINOR_CUT("Corte Leve"),
    SPRAINED_ANKLE("Tornozelo Torcido"),
    FOOD_POISONING("Intoxicação Alimentar"),
    INFECTION("Infecção (de ferida anterior)"), // Would require tracking wounds
    HYPOTHERMIA("Hipotermia"),
    DEHYDRATION_SPELL("Episódio de Desidratação Aguda"), // Sudden worsening
    FEVER("Febre Súbita"),
    FRACTURE("Fratura Simples"); // More severe

    private final String displayName;
    AfflictionType(String displayName) { this.displayName = displayName; }
}
