package io.github.com.ranie_borges.thejungle.game.itens;

import io.github.com.ranie_borges.thejungle.model.Item;
import io.github.com.ranie_borges.thejungle.model.enums.ItemType;

public class Drinkable extends Item {
    public ItemType itemType = ItemType.DRINKABLE;

    protected Drinkable(ItemType type, String name, float weight, float durability) {
        super(type, name, weight, durability);
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
