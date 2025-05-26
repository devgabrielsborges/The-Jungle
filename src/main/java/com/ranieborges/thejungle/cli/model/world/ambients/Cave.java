package com.ranieborges.thejungle.cli.model.world.ambients;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cave extends Ambient {

    public Cave() {
        super("Dark Cave",
                "A damp and dark cave system. The air is cold, and strange sounds echo from the depths.",
                1.3f, // Exploration needs light, can be disorienting
                "Cold and Damp"); // "Pouca luz"
    }

    @Override
    public String explore(Character character) {
        String exploreMsg = character.getName() + " ventures deeper into the dark cave.";
        Message.displayOnScreen(exploreMsg);
        // "Pouca luz: Exige lanterna ou tochas para exploração eficiente."
        // This implies a check for a light source. If no light, exploration might be harder or riskier.
        boolean hasLightSource = false; // TODO: Check character's inventory/equipped items for a light source
        if (!hasLightSource) {
            Message.displayOnScreen("It's pitch black! Exploring without a light source is dangerous.");
            character.changeEnergy(-(10 * getExplorationDifficulty())); // Higher cost without light
            character.changeSanity(-5);
            exploreMsg += " It was very dark, increasing stress.";
        } else {
            character.changeEnergy(-(7 * getExplorationDifficulty()));
        }


        int outcome = random.nextInt(100);
        if (outcome < 30) { // Find Resource
            // "Rochas e minérios raros."
            // "Pequenos lagos subterrâneos (algumas vezes contaminados)."
            // "Ossos e vestígios de exploradores antigos."
            String[] possibleFinds = {"Rare Minerals", "Cave Fungi (Edible?)", "Ancient Bones", "Underground Spring Water"};
            String found = possibleFinds[random.nextInt(possibleFinds.length)];
            exploreMsg += " Found " + found + ".";
            Message.displayOnScreen("In the dim light, you discover some " + found + "!");
            // TODO: Add item to inventory, potentially with purity checks for water
        } else if (outcome < 60) { // Encounter Creature
            exploreMsg += " A chilling sound echoes from a nearby passage... a Cave Creature!";
            Message.displayOnScreen("You are not alone in these depths! A creature attacks!");
            // TODO: Instantiate CaveSpider, GiantBat, etc.
        } else if (outcome < 80) { // Minor Discovery: Hidden Tunnel or Chamber
            exploreMsg += " Discovered a narrow passage leading to an unexplored chamber.";
            Message.displayOnScreen("You find a narrow passage that seems to lead deeper into the cave.");
            // TODO: Option to explore the new passage (could be another Cave ambient instance or a special sub-area)
        } else { // Environmental challenge or nothing
            exploreMsg += " A section of the cave ceiling looks unstable.";
            Message.displayOnScreen("Loose rocks clatter nearby. The cave feels oppressive.");
            character.changeSanity(-2);
        }
        return exploreMsg;
    }

    @Override
    public List<String> getAvailableResourceTypes() {
        return Arrays.asList("Iron Ore", "Coal", "Cave Mushrooms", "Guano", "Crystal Shards");
    }

    @Override
    public List<Class<? extends Creature>> getTypicalCreatures() {
        // "Presença de criaturas desconhecidas"
        // return Arrays.asList(GiantBat.class, CaveSpider.class);
        return List.of(); // Placeholder
    }

    @Override
    public String changeWeather() {
        // Weather inside a cave is generally stable.
        setCurrentWeather("Cold and Damp"); // Stays consistent
        return "The atmosphere inside the " + getName() + " remains " + getCurrentWeather() + ".";
    }

    @Override
    public Map<String, Double> getEventProbabilities() {
        // "Encontro com uma criatura hostil."
        // "Descoberta de um túnel oculto."
        // "Desmoronamento parcial, bloqueando saídas."
        Map<String, Double> eventProbs = new HashMap<>();
        eventProbs.put("CaveCreatureEncounter", 0.20);
        eventProbs.put("FindHiddenTunnel", 0.15);
        eventProbs.put("MinorCaveIn", 0.10); // Could block path or cause minor damage/stress
        eventProbs.put("LoseWay", 0.05); // Disorientation, sanity loss
        return eventProbs;
    }
}
