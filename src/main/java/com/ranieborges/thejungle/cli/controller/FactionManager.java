package com.ranieborges.thejungle.cli.controller;

import com.ranieborges.thejungle.cli.model.entity.Character; // For type hinting
import com.ranieborges.thejungle.cli.model.Faction;
import com.ranieborges.thejungle.cli.model.factions.FactionDisposition;
import com.ranieborges.thejungle.cli.model.factions.FactionReputationLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Manages all factions within the game, their states, and player's reputation with them.
 */
public class FactionManager {
    private final List<Faction> allFactions;
    // Player's reputation is stored in Character.java to keep it with the player state.
    public FactionManager() {
        this.allFactions = new ArrayList<>();
        initializeFactions();
    }

    private void initializeFactions() {
        allFactions.add(new Faction(
            "nomads_peaceful",
            "Nômades Pacíficos",
            "Um grupo errante que valoriza a cooperação e o comércio justo. Evitam conflitos.",
            FactionDisposition.FRIENDLY
            // TODO: Add tradeableItems and desiredItems if implementing trade
        ));

        allFactions.add(new Faction(
            "merchants_resource",
            "Mercadores de Recursos",
            "Comerciantes astutos que possuem suprimentos raros, mas cobram preços altos.",
            FactionDisposition.NEUTRAL
            // TODO: Add tradeableItems (rare) and desiredItems (valuable goods/materials)
        ));

        allFactions.add(new Faction(
            "hunters_brutal",
            "Caçadores Brutais",
            "Um bando territorial e agressivo que vê estranhos como presas ou ameaças.",
            FactionDisposition.HOSTILE
        ));

        allFactions.add(new Faction(
            "survivors_desperate",
            "Sobreviventes Desesperados",
            "Indivíduos levados ao limite, que podem recorrer a medidas extremas para sobreviver.",
            FactionDisposition.GUARDED // Start guarded, can become hostile or even temporarily neutral/friendly
        ));
    }

    public List<Faction> getAllFactions() {
        return Collections.unmodifiableList(allFactions);
    }

    public Optional<Faction> getFactionById(String factionId) {
        return allFactions.stream().filter(f -> f.getId().equals(factionId)).findFirst();
    }

    public Optional<Faction> getFactionByName(String factionName) {
        return allFactions.stream().filter(f -> f.getName().equalsIgnoreCase(factionName)).findFirst();
    }


    /**
     * Determines the current effective disposition of a faction towards a player,
     * considering the faction's initial disposition and the player's reputation.
     * @param faction The faction in question.
     * @param player The player character.
     * @return The effective FactionDisposition.
     */
    public FactionDisposition getEffectiveDisposition(Faction faction, Character player) {
        if (faction == null || player == null) {
            return FactionDisposition.NEUTRAL; // Default or error case
        }
        FactionReputationLevel repLevel = player.getReputationLevel(faction);

        if (repLevel == FactionReputationLevel.HATED || repLevel == FactionReputationLevel.DISLIKED) {
            return FactionDisposition.HOSTILE;
        }
        if (repLevel == FactionReputationLevel.ALLIED || repLevel == FactionReputationLevel.TRUSTED) {
            return FactionDisposition.FRIENDLY;
        }
        return faction.getDisposition();
    }
}
