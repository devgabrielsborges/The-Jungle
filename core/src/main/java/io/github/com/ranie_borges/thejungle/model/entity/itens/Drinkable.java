package io.github.com.ranie_borges.thejungle.model.entity.itens;

import io.github.com.ranie_borges.thejungle.model.entity.Item;
import java.util.Random;

/**
 * Represents a drinkable resource collected directly from the environment.
 * Drinking may restore thirst, but also risks contamination and health loss.
 */
public class Drinkable extends Item {
    private boolean potable;
    private float volume;
    private static final Random random = new Random();

    public Drinkable(String name, float weight, float durability, boolean potable, float volume) {
        super(name, weight, durability);
        this.potable = potable;
        this.volume = Math.max(0, volume);
    }

    @Override
    public void useItem() {
        if (volume <= 0) {
            System.out.println(getName() + " secou, não há mais nada para beber.");
            return;
        }

        int amountDrunk = random.nextInt(3) + 1;
        amountDrunk = Math.min(amountDrunk, (int) volume);

        System.out.println("Você bebeu " + amountDrunk + " unidades de " + getName() + ".");

        volume = Math.max(0, volume - amountDrunk);
        setDurability(getDurability() - amountDrunk);

        if (!potable || random.nextInt(100) < 20) {
            int healthLoss = random.nextInt(6) + 5;
            System.out.println("A água estava contaminada! Você perdeu " + healthLoss + " pontos de vida.");
            // Aplicar dano no personagem
        } else {
            System.out.println("A água estava limpa. Sede saciada!");
            // Recuperar sede do personagem
        }
    }

    @Override
    public void dropItem() {
        System.out.println("Você derramou " + getName() + " no chão.");
    }

    public boolean isPotable() {
        return potable;
    }

    public void setPotable(boolean potable) {
        this.potable = potable;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, volume);
    }
}
