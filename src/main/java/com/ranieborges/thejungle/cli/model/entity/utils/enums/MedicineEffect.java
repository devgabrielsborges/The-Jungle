package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

@Getter
public enum MedicineEffect {
    HEAL_WOUNDS("Cura Ferimentos"),      // Cures direct health damage (like from cuts)
    CURE_INFECTION("Cura Infecção"),   // Cures an "infected" status effect
    RELIEVE_PAIN("Alivia Dor"),     // Might temporarily boost sanity or negate pain penalties
    CURE_POISON("Cura Veneno"),      // Cures a "poisoned" status effect
    SOOTHE_BURNS("Alivia Queimaduras"),     // Specific type of healing
    STABILIZE_FRACTURE("Estabiliza Fratura"), // For use with splints
    PURIFY_WATER("Purifica Água"),     // Not directly applied to character, but to a water item
    BOOST_ENERGY("Aumenta Energia"),     // Temporarily increases energy
    BOOST_SANITY("Aumenta Sanidade"),     // Temporarily increases sanity
    REDUCE_SICKNESS("Reduz Doença");  // General sickness/nausea relief

    private final String displayName;

    MedicineEffect(String displayName) {
        this.displayName = displayName;
    }
}
