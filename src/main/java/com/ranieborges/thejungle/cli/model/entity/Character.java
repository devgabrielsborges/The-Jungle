package com.ranieborges.thejungle.cli.model.entity;

import com.ranieborges.thejungle.cli.model.Inventory;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.model.world.ambients.Jungle;
import com.ranieborges.thejungle.cli.view.Message; // Added for styled messages
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler; // Added for styling

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
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

    public static final float CHARACTER_DEFAULT_MAX_HEALTH = 100f;
    public static final float CHARACTER_DEFAULT_MAX_STAT = 100f;

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
        this.setHealth(initialHealth); // Uses custom setter
        this.setHunger(100f);          // Uses custom setter
        this.setThirst(100f);          // Uses custom setter
        this.setEnergy(100f);          // Uses custom setter
        this.setSanity(initialSanity); // Uses custom setter

        this.attackDamage = Math.max(0f, attackDamage);
        this.speed = Math.max(0f, speed);

        this.inventory = new Inventory(Math.max(10.0f, maxCarryWeight));
        this.traits = traits;
        if (this.currentAmbient == null) {
            this.currentAmbient = new Jungle();
        }
    }

    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(health, CHARACTER_DEFAULT_MAX_HEALTH));
        if (this.health == 0) {
            Message.displayOnScreen(TerminalStyler.error(this.name + " has run out of health and perished!"));
        }
    }

    public void setHunger(float hunger) {
        this.hunger = Math.max(0, Math.min(hunger, CHARACTER_DEFAULT_MAX_STAT));
        if (this.hunger == 0) {
            Message.displayOnScreen(TerminalStyler.warning(this.name + " is starving!"));
        }
    }

    public void setThirst(float thirst) {
        this.thirst = Math.max(0, Math.min(thirst, CHARACTER_DEFAULT_MAX_STAT));
        if (this.thirst == 0) {
            Message.displayOnScreen(TerminalStyler.warning(this.name + " is dying of thirst!"));
        }
    }

    public void setEnergy(float energy) {
        this.energy = Math.max(0, Math.min(energy, CHARACTER_DEFAULT_MAX_STAT));
        if (this.energy == 0) {
            Message.displayOnScreen(TerminalStyler.warning(this.name + " has no energy left!"));
        }
    }

    public void setSanity(float sanity) {
        this.sanity = Math.max(0, Math.min(sanity, CHARACTER_DEFAULT_MAX_STAT));
        if (this.sanity == 0) {
            Message.displayOnScreen(TerminalStyler.warning(this.name + " has lost all sanity! The world blurs..."));
        }
    }

    public float getCurrentInventoryWeight() {
        return this.inventory.getCurrentWeight();
    }

    public float getMaxInventoryCapacity() {
        return this.inventory.getMaxWeightCapacity();
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

        String healthColor = (getHealth() <= CHARACTER_DEFAULT_MAX_HEALTH * 0.25) ? TerminalStyler.BRIGHT_RED :
                (getHealth() <= CHARACTER_DEFAULT_MAX_HEALTH * 0.5) ? TerminalStyler.YELLOW : TerminalStyler.GREEN;
        Message.displayOnScreen(String.format("Health: %s%.1f%s/%.1f", healthColor, getHealth(), TerminalStyler.RESET, CHARACTER_DEFAULT_MAX_HEALTH));

        String hungerColor = (getHunger() <= CHARACTER_DEFAULT_MAX_STAT * 0.25) ? TerminalStyler.RED : TerminalStyler.YELLOW;
        Message.displayOnScreen(String.format("Hunger: %s%.1f%s/%.1f", getHunger() > CHARACTER_DEFAULT_MAX_STAT * 0.25 ? TerminalStyler.WHITE : hungerColor, getHunger(), TerminalStyler.RESET, CHARACTER_DEFAULT_MAX_STAT));

        String thirstColor = (getThirst() <= CHARACTER_DEFAULT_MAX_STAT * 0.25) ? TerminalStyler.RED : TerminalStyler.YELLOW;
        Message.displayOnScreen(String.format("Thirst: %s%.1f%s/%.1f", getThirst() > CHARACTER_DEFAULT_MAX_STAT * 0.25 ? TerminalStyler.WHITE : thirstColor, getThirst(), TerminalStyler.RESET, CHARACTER_DEFAULT_MAX_STAT));

        String energyColor = (getEnergy() <= CHARACTER_DEFAULT_MAX_STAT * 0.25) ? TerminalStyler.RED : TerminalStyler.CYAN;
        Message.displayOnScreen(String.format("Energy: %s%.1f%s/%.1f", getEnergy() > CHARACTER_DEFAULT_MAX_STAT * 0.25 ? TerminalStyler.WHITE : energyColor, getEnergy(), TerminalStyler.RESET, CHARACTER_DEFAULT_MAX_STAT));

        String sanityColor = (getSanity() <= CHARACTER_DEFAULT_MAX_STAT * 0.25) ? TerminalStyler.BRIGHT_RED :
                (getSanity() <= CHARACTER_DEFAULT_MAX_STAT * 0.5) ? TerminalStyler.MAGENTA : TerminalStyler.WHITE;
        Message.displayOnScreen(String.format("Sanity: %s%.1f%s/%.1f", sanityColor, getSanity(), TerminalStyler.RESET, CHARACTER_DEFAULT_MAX_STAT));

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
        Message.displayOnScreen(TerminalStyler.style("------------------------", TerminalStyler.MAGENTA));
    }
}
