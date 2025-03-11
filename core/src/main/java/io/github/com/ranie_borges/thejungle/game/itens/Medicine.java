package io.github.com.ranie_borges.thejungle.game.itens;

import io.github.com.ranie_borges.thejungle.model.Item;
import io.github.com.ranie_borges.thejungle.model.enums.ItemType;

public class Medicine extends Item {
    public ItemType itemType = ItemType.MEDICINE;
    private double healRatio;

    protected Medicine(ItemType type, String name, float weight, float durability) {
        super(type, name, weight, durability);
    }

    public double getHealRatio() {
        return healRatio;
    }

    public void setHealRatio(double healRatio) {
        this.healRatio = healRatio;
    }

    /**
     *
     */
    @Override
    public void useItem() {
        super.useItem();
    }

    /**
     *
     */
    @Override
    public void dropItem() {
        super.dropItem();
    }
}
