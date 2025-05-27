package com.ranieborges.thejungle.cli.model.events.utils.enums;

import lombok.Getter;

@Getter
public enum AfflictionType {
    MINOR_CUT("Corte Leve"),
    SPRAINED_ANKLE("Tornozelo Torcido"),
    FOOD_POISONING("Intoxicação Alimentar"),
    INFECTION("Infecção (de ferida anterior)"),
    HYPOTHERMIA("Hipotermia"),
    DEHYDRATION_SPELL("Episódio de Desidratação Aguda"),
    FEVER("Febre Súbita"),
    FRACTURE("Fratura Simples");

    private final String displayName;
    AfflictionType(String displayName) { this.displayName = displayName; }
}
