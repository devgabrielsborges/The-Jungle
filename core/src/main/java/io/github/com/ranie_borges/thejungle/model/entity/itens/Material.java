package io.github.com.ranie_borges.thejungle.model.entity.itens;

import io.github.com.ranie_borges.thejungle.model.entity.Item;

public class Material extends Item {

    protected Material(String name, float weight, float durability) {
        super(name, weight, durability);
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
}
