package com.ranieborges.thejungle.cli.controller;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.Faction;
import com.ranieborges.thejungle.cli.model.factions.BrutalHunters;
import com.ranieborges.thejungle.cli.model.factions.DesperateSurvivors;
import com.ranieborges.thejungle.cli.model.factions.PeacefulNomads;
import com.ranieborges.thejungle.cli.model.factions.ResourceMerchants;
import com.ranieborges.thejungle.cli.model.factions.utils.FactionDisposition;
import com.ranieborges.thejungle.cli.model.factions.utils.FactionReputationLevel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FactionManager {
    private final List<Faction> allFactions;

    public FactionManager() {
        this.allFactions = new ArrayList<>();
        initializeFactions();
    }

    private void initializeFactions() {
        allFactions.add(new PeacefulNomads());
        allFactions.add(new ResourceMerchants());
        allFactions.add(new BrutalHunters());
        allFactions.add(new DesperateSurvivors());
    }

    public List<Faction> getAllFactions() {
        return Collections.unmodifiableList(allFactions);
    }

    public Optional<Faction> getFactionById(String factionId) {
        return allFactions.stream().filter(f -> f.getId().equals(factionId)).findFirst();
    }

    public FactionDisposition getEffectiveDisposition(Faction faction, Character player) {
        if (faction == null || player == null) {
            return FactionDisposition.NEUTRAL;
        }
        FactionReputationLevel repLevel = player.getReputationLevel(faction);

        if (repLevel == FactionReputationLevel.HATED || repLevel == FactionReputationLevel.DISLIKED) {
            return FactionDisposition.HOSTILE;
        }
        if (repLevel == FactionReputationLevel.ALLIED || repLevel == FactionReputationLevel.TRUSTED) {
            return FactionDisposition.FRIENDLY;
        }
        if (repLevel == FactionReputationLevel.ACCEPTED && faction.getDisposition() == FactionDisposition.GUARDED) {
            return FactionDisposition.NEUTRAL;
        }
        return faction.getDisposition();
    }
}
