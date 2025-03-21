package io.github.com.ranie_borges.thejungle.game.itens;

import io.github.com.ranie_borges.thejungle.model.Item;

public class Medicine extends Item {
    private double healRatio;

    protected Medicine(String name, float weight, float durability, double healRatio) {
        super(name, weight, durability);
        setHealRatio(healRatio);
    }

    /**
     *
     */
    @Override
    public void useItem() {

    }

    /**
     *
     */
    @Override
    public void dropItem() {

    }

    public double getHealRatio() {
        return healRatio;
    }

    public void setHealRatio(double healRatio) {
        this.healRatio = healRatio;
    }
}
