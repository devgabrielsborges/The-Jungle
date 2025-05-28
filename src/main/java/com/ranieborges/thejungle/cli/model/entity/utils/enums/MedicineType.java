package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

@Getter
public enum MedicineType {
    BANDAGE("Bandagem"),
    ANTIBIOTIC("Antibiótico"),
    ANALGESIC("Analgésico"),
    ANTIDOTE("Antídoto"),
    HERBAL_SALVE("Pomada Herbal"),
    SPLINT("Tala"),
    PURIFICATION_TABLET("Pastilha Purificadora"),
    STIMULANT("Estimulante");

    private final String displayName;

    MedicineType(String displayName) {
        this.displayName = displayName;
    }
}
