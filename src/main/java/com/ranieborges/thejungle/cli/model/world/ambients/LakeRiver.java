package com.ranieborges.thejungle.cli.model.world.ambients;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.creatures.Fish;
import com.ranieborges.thejungle.cli.model.entity.itens.Drinkable;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Purity;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LakeRiver extends Ambient {

    public LakeRiver() {
        super("Shimmering Lake/River",
                "A large body of fresh water, either a calm lake or a flowing river. Offers water and fish, but may hide dangers.",
                0.9f, // Easier to traverse along the bank, harder if swimming
                "Cool and Breezy");
    }

    @Override
    public String explore(Character character) {
        String exploreMsg = character.getName() + " explores the edge of the " + getName() + ".";
        Message.displayOnScreen(exploreMsg);
        character.changeEnergy(-(4 * getExplorationDifficulty()));

        int outcome = random.nextInt(100);

        if (outcome < 40) { // 40% Find Resource (water, fish, reeds)
            // "Peixes e algas comestíveis."
            // "Água doce (algumas vezes contaminada)."
            // "Vegetação ribeirinha útil para fabricação de cordas e armadilhas."
            String[] possibleFinds = {"Fresh Water Source", "Edible Algae", "Reeds for Crafting", "Small School of Fish"};
            String found = possibleFinds[random.nextInt(possibleFinds.length)];
            exploreMsg += " Found " + found + ".";
            Message.displayOnScreen("Near the water's edge, you spot some " + found + "!");
            // TODO: Add item or interaction (e.g., if "Fish", trigger fishing minigame/check)
             if (found.equals("Fresh Water Source")) character.getInventory().addItem(new Drinkable("Lake Water", "Water from the lake.", 0.1f, 30f, Purity.UNKNOWN, 0.1f));
        } else if (outcome < 60) { // 20% Encounter Creature
            exploreMsg += " The water ripples suspiciously... a Water Creature!";
            Message.displayOnScreen("Something stirs in the water!");
            // TODO: Instantiate Fish (Piranha variant?), Alligator, GiantLeech
            // Example: if (random.nextBoolean()) new Fish(Fish.FishType.PIRANHA); else new Alligator();
        } else if (outcome < 75) { // 15% Minor Discovery (abandoned boat, fishing spot)
            exploreMsg += " Discovered an old, half-submerged fishing boat.";
            Message.displayOnScreen("You find an old, dilapidated fishing boat beached on the shore. It might have some usable parts.");
        } else { // 25% Nothing or minor environmental detail
            exploreMsg += " The water looks calm and reflects the sky.";
            Message.displayOnScreen("The water is calm. You enjoy a moment of peace but find nothing else.");
        }
        return exploreMsg;
    }

    @Override
    public List<String> getAvailableResourceTypes() {
        return Arrays.asList("Fresh Water", "Fish", "River Stones", "Clay (from bank)", "Edible Water Plants", "Driftwood");
    }

    @Override
    public List<Class<? extends Creature>> getTypicalCreatures() {
        // "Ataque de criatura aquática (como piranhas ou jacarés)."
        return List.of(Fish.class); // Add Alligator.class, Piranha.class (as Fish variant)
    }

    @Override
    public String changeWeather() {
        String oldWeather = getCurrentWeather();
        int chance = random.nextInt(4);
        switch (chance) {
            case 0 -> setCurrentWeather("Sudden Downpour");
            case 1 -> setCurrentWeather("Misty Morning/Evening");
            case 2 -> setCurrentWeather("Sunny and Clear");
            default -> setCurrentWeather("Cool and Breezy");
        }
        return "The weather near the " + getName() + " changed from " + oldWeather + " to " + getCurrentWeather() + ".";
    }

    @Override
    public Map<String, Double> getEventProbabilities() {
        // "Ataque de criatura aquática (como piranhas ou jacarés)."
        // "Tempestade, aumentando o nível da água."
        // "Encontro de um barco abandonado."
        Map<String, Double> eventProbs = new HashMap<>();
        eventProbs.put("AquaticCreatureAttack", 0.15);
        eventProbs.put("SuddenStormFlood", 0.08); // Increases water level, makes crossing harder/dangerous
        eventProbs.put("FindAbandonedBoat", 0.10);
        eventProbs.put("GoodFishingSpot", 0.20); // Higher chance to catch fish
        return eventProbs;
    }
}
