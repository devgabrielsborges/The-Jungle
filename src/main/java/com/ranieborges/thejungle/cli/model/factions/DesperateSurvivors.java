package com.ranieborges.thejungle.cli.model.factions;

import com.ranieborges.thejungle.cli.model.Faction;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.factions.utils.FactionDisposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DesperateSurvivors extends Faction {
    public static final String FACTION_ID = "survivors_desperate";

    public DesperateSurvivors() {
        super(
            FACTION_ID,
            "Sobreviventes Desesperados",
            "Indivíduos levados ao limite, que podem recorrer a medidas extremas para sobreviver.",
            FactionDisposition.GUARDED,
            Collections.emptyList(),
            defineDesiredItems()
        );
    }

    private static List<MaterialType> defineDesiredItems() {
        List<MaterialType> desired = new ArrayList<>();
        desired.add(MaterialType.WOOD);
        desired.add(MaterialType.FIBER);
        return desired;
    }
}
