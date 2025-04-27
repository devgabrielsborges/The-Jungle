package io.github.com.ranie_borges.thejungle.model.entity.itens;

import io.github.com.ranie_borges.thejungle.model.entity.Item;

/**
 * Represents a medicine item used for healing.
 */
public class Medicine extends Item {
    private double healRatio;

    public Medicine(String name, float weight, float durability, double healRatio) {
        super(name, weight, durability);
        setHealRatio(healRatio);
    }

    @Override
    public void useItem() {
        System.out.println("Você usou o medicamento: " + getName() + ", restaurando " + healRatio + "% da vida ou sanidade!");
        setDurability(getDurability() - 1); // Usar o remédio reduz a durabilidade (pode ser removido depois)
    }

    @Override
    public void dropItem() {
        System.out.println("Você deixou cair o medicamento: " + getName() + ".");
    }

    public double getHealRatio() {
        return healRatio;
    }

    public void setHealRatio(double healRatio) {
        this.healRatio = Math.max(0, healRatio);
    }

    // ====== ADICIONADO: Factory Methods para criar remédios padrão ======

    /**
     * Creates a standard Bandage.
     */
    public static Medicine createBandage() {
        return new Medicine("Bandage", 0.3f, 1.0f, 20.0);
    }

    /**
     * Creates a standard Medicinal Ointment.
     */
    public static Medicine createMedicinalOintment() {
        return new Medicine("Medicinal Ointment", 0.5f, 1.0f, 30.0);
    }

    /**
     * Creates a standard Antibiotic.
     */
    public static Medicine createAntibiotic() {
        return new Medicine("Antibiotic", 0.4f, 1.0f, 50.0);
    }
}
