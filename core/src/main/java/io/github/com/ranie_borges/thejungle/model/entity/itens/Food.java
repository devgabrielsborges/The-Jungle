package io.github.com.ranie_borges.thejungle.model.entity.itens;

import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import java.util.Random;

/**
 * Represents food that can be consumed to restore hunger
 * but may cause food poisoning if spoiled
 */
public class Food extends Item {
    @Expose
    private final int nutritionalValue;
    @Expose
    private final String type;
    @Expose
    private int shelfLife; // prazo de validade em turnos
    @Expose
    private boolean spoiled;

    private static final Random random = new Random();

    public Food(String name, float weight, float durability, int nutritionalValue, String type, int shelfLife) {
        super(name, weight, durability);
        this.nutritionalValue = Math.max(0, nutritionalValue);
        this.type = type;
        this.shelfLife = shelfLife;
        this.spoiled = false;
    }

    public static Food createBerry() {
        return new Food("Berry", 0.2f, 100f, 15, "Fruit", 5);
    }

    /**
     * Consumes the food, restoring hunger but potentially causing food poisoning if
     * spoiled
     */
    @Override
    public void useItem() {
        if (shelfLife <= 0) {
            spoiled = true;
        }

        if (spoiled || random.nextInt(100) < 10) { // 10% de chance de intoxicar mesmo se não estragado
            System.out.println("Você comeu " + getName() + " e ficou intoxicado!");
            // Aqui você poderia aplicar penalidade de vida ou sanidade
        } else {
            System.out.println("Você comeu " + getName() + " e recuperou " + nutritionalValue + " pontos de fome!");
            // Aqui você poderia aumentar o valor de fome do personagem
        }
    }

    /**
     * Drops the food item
     */
    @Override
    public void dropItem() {
        System.out.println("Você deixou cair " + getName() + " no chão.");
    }

    // Atualiza o prazo de validade a cada turno
    public void decreaseShelfLife() {
        if (shelfLife > 0) {
            shelfLife--;
        }
    }

    /**
     * Gets the nutritional value of this food
     * 
     * @return The nutritional value
     */
    public int getNutritionalValue() {
        return nutritionalValue;
    }

    /**
     * Checks if the food is spoiled
     * 
     * @return True if the food is spoiled, false otherwise
     */
    public boolean isSpoiled() {
        return spoiled;
    }

    /**
     * Gets the shelf life of this food
     * 
     * @return The shelf life in turns
     */
    public int getShelfLife() {
        return shelfLife;
    }

    /**
     * Gets the type of this food
     * 
     * @return The food type
     */
    public String getFoodType() {
        return type;
    }
}
