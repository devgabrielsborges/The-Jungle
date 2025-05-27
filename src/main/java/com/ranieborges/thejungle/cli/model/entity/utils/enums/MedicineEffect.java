package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

@Getter
public enum MedicineEffect {
    HEAL_WOUNDS("Cura Ferimentos"),
    CURE_INFECTION("Cura Infecção"),
    RELIEVE_PAIN("Alivia Dor"),
    CURE_POISON("Cura Veneno"),
    SOOTHE_BURNS("Alivia Queimaduras"),
    STABILIZE_FRACTURE("Estabiliza Fratura"),
    PURIFY_WATER("Purifica Água"),
    BOOST_ENERGY("Aumenta Energia"),
    BOOST_SANITY("Aumenta Sanidade"),
    REDUCE_SICKNESS("Reduz Doença");

    private final String displayName;

    MedicineEffect(String displayName) {
        this.displayName = displayName;
    }
}
