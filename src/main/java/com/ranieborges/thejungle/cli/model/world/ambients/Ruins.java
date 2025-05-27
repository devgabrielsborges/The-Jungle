package com.ranieborges.thejungle.cli.model.world.ambients;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.creatures.Wolf; // Example, could be scavengers
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Hostility;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

import java.util.*;

public class Ruins extends Ambient {

    public Ruins() {
        super("Abandoned Ruins",
                "The crumbling remains of an ancient structure. It might hold valuable supplies or hidden dangers.",
                1.1f,
                "Dusty and Still");
    }

    @Override
    public String explore(Character character) {
        String exploreMsg = character.getName() + " cautiously explores the dilapidated ruins.";
        Message.displayOnScreen(exploreMsg);
        character.changeEnergy(-(6 * getExplorationDifficulty()));

        int outcome = random.nextInt(100);

        if (outcome < 35) {

            String[] possibleFinds = {"Old Tools", "Canned Food", "Tattered Map Fragment", "Scrap Metal"};
            String found = possibleFinds[random.nextInt(possibleFinds.length)];
            exploreMsg += " Found some " + found + ".";
            Message.displayOnScreen("Sifting through the debris, you find some " + found + "!");
            exploreMsg += " Found some " + found + ".";
            Message.displayOnScreen("Sifting through the debris, you find some " + found + "!");


            if (found.equals("Canned Food")) {
                character.getInventory().addItem(new Food("Comida Enlatada", "Alimento preservado encontrado nas ruínas.", 0.5f, 40f, FoodType.CANNED, 50, 0.05f, 0.0f));
            } else if (found.equals("Ancient Medicine")) {
                character.getInventory().addItem(new Food("Medicina Antiga", "Um frasco com remédios antigos, ainda utilizáveis.", 0.3f, 5f, FoodType.OTHER, 20, 0.1f, 0.0f));
            } else if (found.equals("Metal Scraps")) {
                character.getInventory().addItem(new Material("Sucata Metálica", "Pedaços de metal que podem ser usados para criar ferramentas.", 0.7f, MaterialType.METAL_ORE, 8));
            } else if (found.equals("Broken Tools")) {
                character.getInventory().addItem(new Material("Ferramentas Quebradas", "Partes de ferramentas antigas que podem ser recicladas.", 0.6f, MaterialType.METAL_ORE, 5));
            } else if (found.equals("Artifact")) {
                character.getInventory().addItem(new Material("Artefato Antigo", "Um objeto misterioso de uma civilização perdida.", 0.4f, MaterialType.OTHER, 3));
            }
        } else if (outcome < 55) {
            exploreMsg += " A section of the floor creaks ominously... it might be a trap or something lurking below!";
            Message.displayOnScreen("You hear scurrying sounds from the shadows. Something might be living here.");
            Creature ruinsCreature;
            int creatureRoll = random.nextInt(100);
            String creatureType;

            if (creatureRoll < 40) { // 40% chance - Giant Spider
                creatureType = "Giant Spider";
                ruinsCreature = new Creature(creatureType, 50, 15, 8, Hostility.HOSTILE) {
                    @Override
                    public void attack(Character target) {
                        float damage = getAttackDamage() * (random.nextFloat() * 0.3f + 0.7f);
                        target.changeHealth(-damage);
                        Message.displayOnScreen(TerminalStyler.warning(getName() + " lunges at " + target.getName() + " with venomous fangs!"));
                        target.changeSanity(-5);
                        Message.displayOnScreen("The spider's horrifying appearance shakes your resolve! (-5 sanity)");
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
                        if (random.nextFloat() < 0.6f) {
                            loot.add(new Material("Spider Silk", "Strong and flexible fibers", 0.2f, MaterialType.FIBER, 3));
                        }
                        if (random.nextFloat() < 0.3f) {
                            loot.add(new Material("Spider Fang", "Sharp venomous fang", 0.1f, MaterialType.OTHER, 1));
                        }
                        return loot;
                    }
                };


                Message.displayOnScreen("A " + ruinsCreature.getName() + " descends from the ceiling!");
                ruinsCreature.displayStatus();


                ruinsCreature.attack(character);

                // Sanity loss from the encounter
                character.changeSanity(-3);
            } else if (creatureRoll < 70) { // 30% chance - Hostile Scavenger
                creatureType = "Human Scavenger";
                boolean isHostile = random.nextFloat() < 0.7f; // 70% chance to be hostile

                ruinsCreature = new Creature(creatureType, 60, 12, 10, isHostile ? Hostility.HOSTILE : Hostility.NEUTRAL) {
                    @Override
                    public void attack(Character target) {
                        float damage = getAttackDamage() * (random.nextFloat() * 0.4f + 0.6f);
                        target.changeHealth(-damage);
                        Message.displayOnScreen(TerminalStyler.warning(getName() + " attacks " + target.getName() + " with a makeshift weapon!"));
                    }

                    @Override
                    public void act(Character player) {
                        if (isAlive()) {
                            if (getHostility() == Hostility.HOSTILE) {
                                attack(player);
                            } else {
                                Message.displayOnScreen(getName() + " watches you cautiously but doesn't attack.");
                            }
                        }
                    }

                    @Override
                    public List<Item> dropLoot() {
                        List<Item> loot = new ArrayList<>();
                        if (random.nextFloat() < 0.5f) {
                            loot.add(new Food("Stale Ration", "A bland but edible ration", 0.3f, 15f, FoodType.CANNED, 10, 0.1f, 0.0f));
                        }
                        if (random.nextFloat() < 0.4f) {
                            loot.add(new Material("Scrap Metal", "Salvaged metal pieces", 0.5f, MaterialType.METAL_ORE, 2));
                        }
                        return loot;
                    }
                };

                // Announce the encounter
                if (isHostile) {
                    Message.displayOnScreen("A desperate " + ruinsCreature.getName() + " jumps out from behind debris, ready to attack!");
                } else {
                    Message.displayOnScreen("A cautious " + ruinsCreature.getName() + " emerges from the shadows, watching you warily.");
                }
                ruinsCreature.displayStatus();

                // If hostile, the scavenger attacks the character
                if (isHostile) {
                    ruinsCreature.attack(character);
                }

                // Sanity effect - lower impact than spider
                character.changeSanity(-2);
            } else { // 30% chance - Trap
                creatureType = "Trap";
                Message.displayOnScreen(TerminalStyler.error("You trigger a hidden trap in the ruins!"));

                int trapType = random.nextInt(3);
                if (trapType == 0) {
                    // Pit trap
                    Message.displayOnScreen("The floor gives way beneath you - it's a pit trap!");
                    float damage = 10 + random.nextInt(10);
                    character.changeHealth(-damage);
                    Message.displayOnScreen(TerminalStyler.error("You fall and take " + damage + " damage!"));
                    character.changeSanity(-5);
                    Message.displayOnScreen("The sudden fall leaves you disoriented and fearful. (-5 sanity)");
                } else if (trapType == 1) {
                    // Dart trap
                    Message.displayOnScreen("You hear a click, followed by a hissing sound - poison darts shoot from the walls!");
                    float damage = 5 + random.nextInt(10);
                    character.changeHealth(-damage);
                    Message.displayOnScreen(TerminalStyler.error("The darts hit you for " + damage + " damage!"));
                    character.changeSanity(-8);
                    Message.displayOnScreen("The poison makes your mind foggy and paranoid. (-8 sanity)");
                } else {
                    // Collapsing ceiling
                    Message.displayOnScreen("The ceiling above begins to crack and crumble!");
                    float damage = 15 + random.nextInt(10);
                    character.changeHealth(-damage);
                    Message.displayOnScreen(TerminalStyler.error("Falling debris hits you for " + damage + " damage!"));
                    character.changeSanity(-6);
                    Message.displayOnScreen("The near-death experience leaves you shaken. (-6 sanity)");
                }
            }            // Example: if (random.nextBoolean()) new GiantSpider(); else triggerTrap();
        } else if (outcome < 75) { // 20% Minor Discovery (hidden passage, inscription)
            exploreMsg += " Discovered a faded inscription on a wall.";
            Message.displayOnScreen("You find a barely legible inscription on a crumbling wall, hinting at the past.");
        } else { // 25% Nothing significant or structural instability
            exploreMsg += " The ruins are unstable, and a section collapses nearby!";
            Message.displayOnScreen("Dust and debris fall as a nearby section of the ruins collapses. You find nothing useful this time.");
            character.changeSanity(-3); // Minor stress
        }
        return exploreMsg;
    }

    @Override
    public List<String> getAvailableResourceTypes() {
        return Arrays.asList("Scrap Metal", "Old Fabric", "Broken Tools", "Canned Goods (Rare)", "Ancient Artifacts (Very Rare)");
    }

    @Override
    public List<Class<? extends Creature>> getTypicalCreatures() {
        // Could be rats, spiders, desperate survivors, or even automated defenses if sci-fi.
        return Arrays.asList(Wolf.class); // Placeholder, add GiantSpider, ScavengerNPC etc.
    }

    @Override
    public String changeWeather() {
        // "Baixo risco climático: Normalmente oferecem abrigo contra o clima."
        // So, weather changes might be less impactful or less frequent here.
        String oldWeather = getCurrentWeather();
        if (random.nextInt(5) == 0) { // Less frequent changes
            setCurrentWeather("Eerily Calm");
        } else {
            setCurrentWeather("Dusty and Still");
        }
        if (!oldWeather.equals(getCurrentWeather())) {
            return "The atmosphere in the " + getName() + " shifted from " + oldWeather + " to " + getCurrentWeather() + ".";
        }
        return "The air in the " + getName() + " remains " + getCurrentWeather() + ".";
    }

    @Override
    public Map<String, Double> getEventProbabilities() {
        // "Encontrar um grupo de sobreviventes (podem ser aliados ou hostis)."
        // "Armadilhas deixadas por antigos ocupantes."
        // "Descoberta de uma passagem secreta para outra área."
        Map<String, Double> eventProbs = new HashMap<>();
        eventProbs.put("EncounterSurvivorsNPC", 0.10);
        eventProbs.put("TriggerTrap", 0.15);
        eventProbs.put("FindSecretPassage", 0.08);
        eventProbs.put("StructuralCollapse", 0.05); // Minor collapse, not necessarily damaging
        return eventProbs;
    }
}
