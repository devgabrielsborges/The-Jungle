package com.ranieborges.thejungle.cli.model.world.ambients;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.creatures.Fish;
import com.ranieborges.thejungle.cli.model.entity.itens.Drinkable;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Purity;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import java.util.Scanner;
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import java.util.ArrayList;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FishType;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Hostility;


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
            if (found.equals("Fresh Water Source")) {
                character.getInventory().addItem(new Drinkable("Lake Water", "Water from the lake.", 0.1f, 30f, Purity.UNKNOWN, 0.1f));
                Message.displayOnScreen("Você coletou água do lago para beber mais tarde.");
            } else if (found.equals("Edible Algae")) {
                Food algae = new Food("Algae", "Edible freshwater algae. Not tasty, but nutritious.", 0.2f,
                    15f, FoodType.VEGETABLE, 2, 0.05f, 0.0f);
                character.getInventory().addItem(algae);
                Message.displayOnScreen("Você coletou algumas algas comestíveis.");
            } else if (found.equals("Reeds for Crafting")) {
                Material reeds = new Material("River Reeds", "Flexible plant stalks good for weaving and crafting.",
                    0.3f, MaterialType.FIBER, 3);
                character.getInventory().addItem(reeds);
                Message.displayOnScreen("Você coletou juncos que podem ser úteis para criar cordas ou cestas.");
            } else if (found.equals("Small School of Fish")) {
                Message.displayOnScreen("Você avista um cardume de peixes! Tentar pescar? (y/n)");
                Scanner scanner = new Scanner(System.in);
                String choice = scanner.nextLine().trim().toLowerCase();

                if (choice.startsWith("y")) {
                    Message.displayOnScreen("Você improvisa uma vara de pesca com o que tem à mão...");
                    character.changeEnergy(-5); // Fishing takes energy

                    // Chance de sucesso baseada em fatores aleatórios
                    int fishingSuccess = random.nextInt(100);

                    if (fishingSuccess < 60) { // 60% chance de pegar um peixe
                        Fish caught = new Fish();
                        List<Item> fishLoot = caught.dropLoot();
                        for (Item loot : fishLoot) {
                            character.getInventory().addItem(loot);
                        }
                        Message.displayOnScreen(TerminalStyler.success("Você conseguiu pescar um " + caught.getName() + "!"));
                    } else {
                        Message.displayOnScreen("O peixe escapou! Talvez você precise de equipamento adequado para pescar.");
                    }
                } else {
                    Message.displayOnScreen("Você decide não pescar agora.");
                }
            }             if (found.equals("Fresh Water Source")) character.getInventory().addItem(new Drinkable("Lake Water", "Water from the lake.", 0.1f, 30f, Purity.UNKNOWN, 0.1f));
        } else if (outcome < 60) { // 20% Encounter Creature
            exploreMsg += " The water ripples suspiciously... a Water Creature!";
            Message.displayOnScreen("Something stirs in the water!");
        } else if (outcome < 60) { // 20% Encounter Creature
            exploreMsg += " The water ripples suspiciously... a Water Creature!";
            Message.displayOnScreen("Something stirs in the water!");

            Creature waterCreature;
            int creatureRoll = random.nextInt(100);
            String creatureType;

            if (creatureRoll < 40) { // 40% chance - Piranha
                creatureType = "Piranha";
                waterCreature = new Fish(FishType.PIRANHA);
            } else if (creatureRoll < 70) { // 30% chance - Jacaré
                creatureType = "Jacaré";
                waterCreature = new Creature(creatureType, 70, 20, 6, Hostility.HOSTILE) {
                    @Override
                    public void attack(Character target) {
                        float damage = getAttackDamage() * (random.nextFloat() * 0.4f + 0.8f);
                        target.changeHealth(-damage);
                        Message.displayOnScreen(TerminalStyler.warning(getName() + " avança violentamente das águas e morde " + target.getName() + "!"));
                        target.changeSanity(-5);
                        Message.displayOnScreen("O ataque repentino do jacaré te deixou em choque! (-5 sanidade)");
                    }

                    @Override
                    public void act(Character player) {
                        if (isAlive()) {
                            attack(player);
                        }
                    }

                    @Override
                    public List<Item> dropLoot() {
                        List<Item> loot = new ArrayList<>();
                        if (random.nextFloat() < 0.7f) {
                            loot.add(new Food("Carne de Jacaré", "Carne dura mas nutritiva", 1.0f,
                                30f, FoodType.MEAT_RAW, 2, 0.2f, 0.4f));
                        }
                        if (random.nextFloat() < 0.5f) {
                            loot.add(new Material("Escamas de Jacaré", "Escamas duras e resistentes", 0.5f, MaterialType.LEATHER, 5));
                        }
                        return loot;
                    }
                };
            } else { // 30% chance - Sanguessuga Gigante
                creatureType = "Sanguessuga Gigante";
                waterCreature = new Creature(creatureType, 30, 8, 5, Hostility.HOSTILE) {
                    @Override
                    public void attack(Character target) {
                        float damage = getAttackDamage() * (random.nextFloat() * 0.3f + 0.7f);
                        target.changeHealth(-damage);
                        Message.displayOnScreen(TerminalStyler.warning(getName() + " se prende em " + target.getName() + " e começa a sugar sangue!"));

                        // Efeito especial: drenar energia além de causar dano
                        target.changeEnergy(-5);
                        Message.displayOnScreen("Você sente sua energia sendo drenada junto com o sangue! (-5 energia)");
                    }

                    @Override
                    public void act(Character player) {
                        if (isAlive()) {
                            attack(player);
                        }
                    }

                    @Override
                    public List<Item> dropLoot() {
                        List<Item> loot = new ArrayList<>();
                        if (random.nextFloat() < 0.4f) {
                            loot.add(new Material("Sanguessuga Medicinal", "Pode ser usada para fins medicinais", 0.1f, MaterialType.OTHER, 1));
                        }
                        return loot;
                    }
                };
            }

            // Anunciar o encontro
            Message.displayOnScreen("Um " + waterCreature.getName() + " aparece nas águas!");
            waterCreature.displayStatus();

            // A criatura ataca o personagem
            waterCreature.attack(character);

            // Perda de sanidade pelo encontro
            character.changeSanity(-2);        } else if (outcome < 75) { // 15% Minor Discovery (abandoned boat, fishing spot)
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
