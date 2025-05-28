package com.ranieborges.thejungle.cli.model.events;

import com.ranieborges.thejungle.cli.controller.TurnController;
import com.ranieborges.thejungle.cli.model.Event;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.creatures.Wolf;
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Hostility;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Getter
public class DiscoveryEvent extends Event {

    public enum DiscoveryType {
        ABANDONED_SHELTER("Abrigo Abandonado"),
        WATER_SOURCE("Fonte de Água"),
        MYSTERIOUS_RUINS_DETAIL("Detalhe em Ruínas Misteriosas"), // More specific than just being in Ruins ambient
        RARE_RESOURCE_NODE("Nódulo de Recurso Raro"),
        HIDDEN_CACHE("Esconderijo Secreto");

        private final String displayName;
        DiscoveryType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    private final DiscoveryType discoveryType;
    // "Recursos encontrados" - could be a list of potential items or a direct item.
    private final List<Item> itemsFound; // For simplicity, let's say this event can grant specific items.

    public DiscoveryEvent(String name, String description, double baseProbability,
                          DiscoveryType discoveryType, List<Item> itemsFound) {
        super(name, description, baseProbability);
        this.discoveryType = discoveryType;
        this.itemsFound = (itemsFound != null) ? itemsFound : new ArrayList<>();
    }
    public DiscoveryEvent(String name, String description, double baseProbability,
                          DiscoveryType discoveryType) {
        this(name, description, baseProbability, discoveryType, new ArrayList<>());
    }


    @Override
    public String execute(Character player, Ambient ambient, TurnController turnController) {
        announceEvent();
        String outcomeSummary = "Discovered a " + discoveryType.getDisplayName() + ".";
        Message.displayOnScreen(TerminalStyler.success("You've made a discovery: " + discoveryType.getDisplayName() + "!"));

        if (!itemsFound.isEmpty()) {
            Message.displayOnScreen(TerminalStyler.info("You found the following items:"));
            for (Item item : itemsFound) {
                Message.displayOnScreen(" - " + item.getName() + " (" + item.getDescription() + ")");
                if (player.getInventory().addItem(item)) {
                    outcomeSummary += " Found " + item.getName() + ".";
                } else {
                    Message.displayOnScreen(TerminalStyler.warning("Could not pick up " + item.getName() + ", inventory full!"));
                    outcomeSummary += " Found " + item.getName() + " but couldn't carry it.";
                }
            }
        }


        switch (discoveryType) {
            case ABANDONED_SHELTER:
                Message.displayOnScreen("This shelter could offer temporary protection or contain more supplies.");
                if (random.nextDouble() < 0.2) {
                    Message.displayOnScreen(TerminalStyler.warning("But wait... you hear sounds from inside!"));

                    Message.displayOnScreen(TerminalStyler.warning("Ao entrar no abrigo, você percebe que não está sozinho!"));


                    int encounterType = random.nextInt(100);
                    if (encounterType < 60) {
                        Message.displayOnScreen("Um sobrevivente assustado se esconde nos fundos do abrigo.");


                        Creature survivor = new Creature("Sobrevivente Desnutrido", 40, 8, 10, Hostility.NEUTRAL) {
                            @Override
                            public void attack(Character target) {
                                float damage = getAttackDamage() * (random.nextFloat() * 0.3f + 0.7f);
                                target.changeHealth(-damage);
                                Message.displayOnScreen(TerminalStyler.warning(getName() + " ataca " + target.getName() + " com uma faca improvisada!"));
                            }

                            @Override
                            public void act(Character player) {
                                if (isAlive() && getHealth() < getMaxHealth() * 0.3) {
                                    setHostility(Hostility.FLEEING);
                                    Message.displayOnScreen(getName() + " tenta fugir desesperadamente!");
                                }
                            }

                            @Override
                            public List<Item> dropLoot() {
                                List<Item> loot = new ArrayList<>();
                                if (random.nextFloat() < 0.7f) {
                                    loot.add(new Food("Ração Velha", "Uma ração semi-estragada", 0.3f,
                                        10f, FoodType.CANNED, 3, 0.2f, 0.0f));
                                }
                                return loot;
                            }
                        };


                        Message.displayOnScreen("\nComo você deseja proceder?");
                        Message.displayOnScreen("1. Oferecer ajuda ao sobrevivente");
                        Message.displayOnScreen("2. Tentar intimidar e expulsar");
                        Message.displayOnScreen("3. Atacar primeiro");

                        Scanner scanner = new Scanner(System.in);
                        String choice = scanner.nextLine().trim();

                        if (choice.equals("1")) {
                            Message.displayOnScreen(TerminalStyler.success("Você oferece comida e se aproxima com cuidado."));
                            if (random.nextFloat() < 0.7f) {
                                Message.displayOnScreen("O sobrevivente aceita sua ajuda e compartilha informações úteis.");
                                player.changeSanity(5);
                                player.getInventory().addItem(new Material("Mapa Rasgado", "Mostra locais próximos", 0.1f, MaterialType.OTHER, 1));
                                outcomeSummary += " Você fez um aliado temporário.";
                            } else {
                                Message.displayOnScreen(TerminalStyler.warning("Era uma armadilha! O sobrevivente tenta te atacar!"));
                                survivor.attack(player);
                                outcomeSummary += " Você foi enganado pelo sobrevivente.";
                            }
                        } else if (choice.equals("2") || choice.equals("3")) {
                            Message.displayOnScreen(TerminalStyler.error("Você assume uma postura agressiva."));
                            survivor.setHostility(Hostility.HOSTILE);
                            if (choice.equals("3")) {
                                Message.displayOnScreen("Você ataca primeiro!");
                                survivor.takeDamage(player.getAttackDamage());
                            } else {
                                survivor.attack(player);
                            }
                            player.changeSanity(-3);
                            outcomeSummary += " Você entrou em conflito com o ocupante do abrigo.";
                        }
                    } else {
                        Message.displayOnScreen(TerminalStyler.warning("Um lobo ferido está usando o abrigo como toca!"));
                        Creature wolf = new Wolf();
                        wolf.takeDamage(20);
                        wolf.attack(player);
                        player.changeSanity(-5);
                        outcomeSummary += " Você confrontou um lobo ferido no abrigo.";
                    }                outcomeSummary += " The shelter seemed occupied!";
                }
                break;
            case WATER_SOURCE:
                Message.displayOnScreen("A clean source of water! You can drink or fill containers.");

                player.changeThirst(30);
                outcomeSummary += " Quenched some thirst at the source.";
                break;
            case MYSTERIOUS_RUINS_DETAIL:
                Message.displayOnScreen("You notice an unusual carving or artifact. It might be significant.");
                player.changeSanity(5);

                outcomeSummary += " Found an intriguing detail.";
                break;
            default:
                break;
        }
        return outcomeSummary;
    }

    @Override
    public boolean canOccur(Character player, Ambient ambient) {

        if (this.discoveryType == DiscoveryType.MYSTERIOUS_RUINS_DETAIL && !(ambient.getName().contains("Ruins"))) {
            return false;
        }
        // Water source more likely near Lake/River or in Jungle
        if (this.discoveryType == DiscoveryType.WATER_SOURCE && (ambient.getName().contains("Mountain") || ambient.getName().contains("Ruins"))) {
            return random.nextDouble() < 0.1; // Less likely in these ambients
        }
        return true;
    }
}
