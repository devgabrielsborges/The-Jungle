package io.github.com.ranie_borges.thejungle.model.entity.itens;

import com.badlogic.gdx.utils.Array;
import io.github.com.ranie_borges.thejungle.model.entity.Item;

import java.util.List;

public class Inventory {
    private Array<Item> items = new Array<>();

    public Array<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

}
