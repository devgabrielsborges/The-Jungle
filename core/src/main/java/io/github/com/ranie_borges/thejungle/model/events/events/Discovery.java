package io.github.com.ranie_borges.thejungle.model.events.events;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Discoveries;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.HashSet;
import java.util.Set;

public class Discovery extends Event {
    private Discoveries discoveryType;
    private Set<Item> itemsFound;
    private String condition; // Optional condition to access discovery
    private boolean requiresSkill;

    public Discovery(String name, String description, float probability, Discoveries discoveryType) {
        super(name, description, probability);
        this.discoveryType = discoveryType;
        this.itemsFound = new HashSet<>();
        this.requiresSkill = false;
    }

    public Discoveries getDiscoveryType() {
        return discoveryType;
    }

    public void setDiscoveryType(Discoveries discoveryType) {
        this.discoveryType = discoveryType;
    }

    public Set<Item> getItemsFound() {
        return itemsFound;
    }

    public void setItemsFound(Set<Item> itemsFound) {
        this.itemsFound = itemsFound;
    }

    public void addItem(Item item) {
        this.itemsFound.add(item);
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isRequiresSkill() {
        return requiresSkill;
    }

    public void setRequiresSkill(boolean requiresSkill) {
        this.requiresSkill = requiresSkill;
    }

    /**
     * @param character
     * @param ambient
     */
    @Override
    public void execute(Character character, Ambient ambient) {

    }
}
