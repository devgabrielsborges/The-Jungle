package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

@Getter
public enum MedicineType {
    BANDAGE("Bandagem"),
    ANTIBIOTIC("Antibiótico"),
    ANALGESIC("Analgésico"), // Painkiller
    ANTIDOTE("Antídoto"),
    HERBAL_SALVE("Pomada Herbal"),
    SPLINT("Tala"), // For fractures
    PURIFICATION_TABLET("Pastilha Purificadora"), // For water
    STIMULANT("Estimulante"); // For energy or counteracting fatigue/drowsiness

    private final String displayName;

    MedicineType(String displayName) {
        this.displayName = displayName;
    }
}
