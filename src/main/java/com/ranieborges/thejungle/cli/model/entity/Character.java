package com.ranieborges.thejungle.cli.model.entity;

import com.ranieborges.thejungle.cli.model.Inventory;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;
import com.ranieborges.thejungle.cli.model.Faction;
import com.ranieborges.thejungle.cli.model.factions.utils.FactionReputationLevel;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.model.world.ambients.Jungle;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public abstract class Character {

    @Setter private String name;
    private float health;
    private float hunger;
    private float thirst;
    private float energy;
    private float sanity;

    private final Inventory inventory;
    @Setter private float attackDamage;
    @Setter private float speed;

    @Setter private Set<Trait> traits;
    @Setter private Ambient currentAmbient;

    private Map<String, Integer> factionReputationPoints;
    private Map<String, FactionReputationLevel> factionReputationLevels;


    protected final float characterDefaultMaxHealth = 100f;
    protected final float characterDefaultMaxStat = 100f;

    public Character(
        String name,
        float initialHealth,
        float initialSanity,
        float attackDamage,
        float speed,
        float maxCarryWeight,
        Set<Trait> traits
    ) {
        this.name = name;
        this.setHealth(initialHealth);
        this.setHunger(100f);
        this.setThirst(100f);
        this.setEnergy(100f);
        this.setSanity(initialSanity);

        this.attackDamage = Math.max(0f, attackDamage);
        this.speed = Math.max(0f, speed);

        this.inventory = new Inventory(Math.max(10.0f, maxCarryWeight));
        this.traits = traits;
        if (this.currentAmbient == null) {
            this.currentAmbient = new Jungle();
        }

        this.factionReputationPoints = new HashMap<>();
        this.factionReputationLevels = new HashMap<>();
    }

    public int getReputationPoints(Faction faction) {
        return factionReputationPoints.getOrDefault(faction.getId(), 0); // Default to 0 points (Neutral range)
    }

    public FactionReputationLevel getReputationLevel(Faction faction) {
        return factionReputationLevels.getOrDefault(faction.getId(), FactionReputationLevel.NEUTRAL);
    }

    public void changeReputation(Faction faction, int pointsChange) {
        if (faction == null) return;
        String factionId = faction.getId();
        int currentPoints = getReputationPoints(faction);
        int newPoints = currentPoints + pointsChange;

        factionReputationPoints.put(factionId, newPoints);
        FactionReputationLevel oldLevel = getReputationLevel(faction);
        FactionReputationLevel newLevel = FactionReputationLevel.fromPoints(newPoints);
        factionReputationLevels.put(factionId, newLevel);

        if (newLevel != oldLevel) {
            Message.displayOnScreen(TerminalStyler.style(
                String.format("Your reputation with %s changed from %s to %s.",
                    faction.getName(), oldLevel.getDisplayName(), newLevel.getDisplayName()),
                (pointsChange > 0) ? TerminalStyler.BRIGHT_GREEN : TerminalStyler.BRIGHT_RED
            ));
        } else if (pointsChange != 0) {
            Message.displayOnScreen(TerminalStyler.style(
                String.format("Reputation with %s changed by %d points (Current: %d, Level: %s).",
                    faction.getName(), pointsChange, newPoints, newLevel.getDisplayName()),
                TerminalStyler.BRIGHT_BLACK
            ));
        }
    }

    public void initializeFactionReputations(List<Faction> allFactions) {
        for (Faction faction : allFactions) {
            if (!factionReputationPoints.containsKey(faction.getId())) {
                factionReputationPoints.put(faction.getId(), 0); // Start at 0 points
                factionReputationLevels.put(faction.getId(), FactionReputationLevel.fromPoints(0));
            }
        }
    }


    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(health, characterDefaultMaxHealth));
        if (this.health == 0) {
            Message.displayOnScreen(TerminalStyler.error(this.name + " has run out of health and perished!"));
        }
    }

    public void setHunger(float hunger) {
        this.hunger = Math.max(0, Math.min(hunger, characterDefaultMaxStat));
        if (this.hunger == 0) {
            Message.displayOnScreen(TerminalStyler.warning(this.name + " is starving!"));
        }
    }

    public void setThirst(float thirst) {
        this.thirst = Math.max(0, Math.min(thirst, characterDefaultMaxStat));
        if (this.thirst == 0) {
            Message.displayOnScreen(TerminalStyler.warning(this.name + " is dying of thirst!"));
        }
    }

    public void setEnergy(float energy) {
        this.energy = Math.max(0, Math.min(energy, characterDefaultMaxStat));
        if (this.energy == 0) {
            Message.displayOnScreen(TerminalStyler.warning(this.name + " has no energy left!"));
        }
    }

    public void setSanity(float sanity) {
        this.sanity = Math.max(0, Math.min(sanity, characterDefaultMaxStat));
        if (this.sanity == 0) {
            Message.displayOnScreen(TerminalStyler.warning(this.name + " has lost all sanity! The world blurs..."));
        }
    }

    public abstract void useSpecialAbility();
    public abstract String getDescription();

    public void changeHealth(float amount) {
        float oldHealth = this.health;
        setHealth(this.health + amount);
        if (this.health < oldHealth && amount < 0) {
            Message.displayOnScreen(TerminalStyler.style(String.format("%s takes %.0f damage.", this.name, -amount), TerminalStyler.RED));
        } else if (this.health > oldHealth && amount > 0) {
            Message.displayOnScreen(TerminalStyler.style(String.format("%s heals for %.0f health.", this.name, amount), TerminalStyler.GREEN));
        }
    }

    public void changeHunger(float amount) {
        setHunger(this.hunger + amount);
    }

    public void changeThirst(float amount) {
        setThirst(this.thirst + amount);
    }

    public void changeEnergy(float amount) {
        setEnergy(this.energy + amount);
    }

    public void changeSanity(float amount) {
        float oldSanity = this.sanity;
        setSanity(this.sanity + amount);
        if (this.sanity < oldSanity && amount < 0) {
            Message.displayOnScreen(TerminalStyler.style(String.format("%s feels more uneasy. Sanity %.0f.", this.name, -amount), TerminalStyler.MAGENTA));
        } else if (this.sanity > oldSanity && amount > 0) {
            Message.displayOnScreen(TerminalStyler.style(String.format("%s feels more composed. Sanity +%.0f.", this.name, amount), TerminalStyler.CYAN));
        }
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public void displayStatus() {
        Message.displayOnScreen(TerminalStyler.title("--- Character Status: " + getName() + " ---"));

        String healthColor = (getHealth() <= characterDefaultMaxHealth * 0.25) ? TerminalStyler.BRIGHT_RED :
            (getHealth() <= characterDefaultMaxHealth * 0.5) ? TerminalStyler.YELLOW : TerminalStyler.GREEN;
        Message.displayOnScreen(String.format("Health: %s%.1f%s/%.1f", healthColor, getHealth(), TerminalStyler.RESET, characterDefaultMaxHealth));

        String hungerColor = (getHunger() <= characterDefaultMaxStat * 0.25) ? TerminalStyler.RED : TerminalStyler.YELLOW;
        Message.displayOnScreen(String.format("Hunger: %s%.1f%s/%.1f", getHunger() > characterDefaultMaxStat * 0.25 ? TerminalStyler.WHITE : hungerColor, getHunger(), TerminalStyler.RESET, characterDefaultMaxStat));

        String thirstColor = (getThirst() <= characterDefaultMaxStat * 0.25) ? TerminalStyler.RED : TerminalStyler.YELLOW;
        Message.displayOnScreen(String.format("Thirst: %s%.1f%s/%.1f", getThirst() > characterDefaultMaxStat * 0.25 ? TerminalStyler.WHITE : thirstColor, getThirst(), TerminalStyler.RESET, characterDefaultMaxStat));

        String energyColor = (getEnergy() <= characterDefaultMaxStat * 0.25) ? TerminalStyler.RED : TerminalStyler.CYAN;
        Message.displayOnScreen(String.format("Energy: %s%.1f%s/%.1f", getEnergy() > characterDefaultMaxStat * 0.25 ? TerminalStyler.WHITE : energyColor, getEnergy(), TerminalStyler.RESET, characterDefaultMaxStat));

        String sanityColor = (getSanity() <= characterDefaultMaxStat * 0.25) ? TerminalStyler.BRIGHT_RED :
            (getSanity() <= characterDefaultMaxStat * 0.5) ? TerminalStyler.MAGENTA : TerminalStyler.WHITE;
        Message.displayOnScreen(String.format("Sanity: %s%.1f%s/%.1f", sanityColor, getSanity(), TerminalStyler.RESET, characterDefaultMaxStat));

        Message.displayOnScreen(String.format("Attack Damage: %s%.1f%s", TerminalStyler.BRIGHT_BLACK, getAttackDamage(), TerminalStyler.RESET));
        Message.displayOnScreen(String.format("Speed: %s%.1f%s", TerminalStyler.BRIGHT_BLACK, getSpeed(), TerminalStyler.RESET));
        Message.displayOnScreen(String.format("Inventory: %.1f/%.1f %s(Weight)%s",
            getInventory().getCurrentWeight(),
            getInventory().getMaxWeightCapacity(),
            TerminalStyler.BRIGHT_BLACK, TerminalStyler.RESET));
        Message.displayOnScreen("Location: " + TerminalStyler.style((getCurrentAmbient() != null ? getCurrentAmbient().getName() : "Unknown"), TerminalStyler.BLUE));

        if (getTraits() != null && !getTraits().isEmpty()) {
            String traitsString = getTraits().stream().map(Trait::getDisplayName).collect(Collectors.joining(", "));
            Message.displayOnScreen("Traits: " + TerminalStyler.style(traitsString, TerminalStyler.CYAN));
        } else {
            Message.displayOnScreen("Traits: None");
        }

        if (factionReputationLevels != null && !factionReputationLevels.isEmpty()) {
            Message.displayOnScreen(TerminalStyler.style("Faction Reputations:", TerminalStyler.YELLOW));
            factionReputationLevels.forEach((factionId, level) -> {
                Message.displayOnScreen(String.format(" - %s: %s (%d pts)", factionId, level.getDisplayName(), factionReputationPoints.getOrDefault(factionId, 0)));
            });
        }

        Message.displayOnScreen(TerminalStyler.style("------------------------", TerminalStyler.MAGENTA));
    }
}
