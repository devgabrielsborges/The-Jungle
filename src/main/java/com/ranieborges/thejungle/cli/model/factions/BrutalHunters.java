package com.ranieborges.thejungle.cli.model.factions;

import com.ranieborges.thejungle.cli.model.Faction;
import com.ranieborges.thejungle.cli.model.factions.utils.FactionDisposition;

import java.util.Collections;

public class BrutalHunters extends Faction {
    public static final String FACTION_ID = "hunters_brutal";

    public BrutalHunters() {
        super(
            FACTION_ID,
            "Caçadores Brutais",
            "Um bando territorial e agressivo que vê estranhos como presas ou ameaças.",
            FactionDisposition.HOSTILE,
            Collections.emptyList(),
            Collections.emptyList()
        );
    }
}
