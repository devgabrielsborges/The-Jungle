package io.github.com.ranie_borges.thejungle.game.itens;

import io.github.com.ranie_borges.thejungle.model.Item;
import io.github.com.ranie_borges.thejungle.model.enums.ItemType;

public class Material extends Item {
    protected Material(ItemType type, String name, float weight, float durability) {
        super(type, name, weight, durability);
    }

    /**
     *
     */
    @Override
    public void dropItem() {
        super.dropItem();
    }

    /**
     *
     */
    @Override
    public void useItem() {
        super.useItem();
    }
}
