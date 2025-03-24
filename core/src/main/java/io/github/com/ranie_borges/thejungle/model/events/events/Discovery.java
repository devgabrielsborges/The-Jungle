package io.github.com.ranie_borges.thejungle.model.events.events;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Discoveries;
import io.github.com.ranie_borges.thejungle.model.events.Event;

import java.util.HashSet;
import java.util.Set;

public class Discovery<T extends Item> extends Event {
    private Discoveries discoveryType;
    private Set<T> items;
    private String condition; // Optional condition to access discovery
    private boolean requiresSkill;

    public Discovery(String name, String description, float probability, Discoveries discoveryType) {
        super(name, description, probability);
        this.discoveryType = discoveryType;
        this.items = new HashSet<>();
        this.requiresSkill = false;
    }

    public Discoveries getDiscoveryType() {
        return discoveryType;
    }

    public void setDiscoveryType(Discoveries discoveryType) {
        this.discoveryType = discoveryType;
    }

    public Set<T> getItems() {
        return items;
    }

    public void setItems(Set<T> items) {
        this.items = items;
    }

    public void addItem(T item) {
        this.items.add(item);
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
     * Execute the discovery event for a character
     *
     * @param character The character who experiences the discovery
     */
    @Override
    public <U extends Character> void execute(U character) {
        // Implementation for discovery:
        // 1. Show discovery description to player
        // 2. Check if character has required skills (if needed)
        // 3. Add discovered items to character inventory
        // 4. Apply any special effects from the discovery
    }
}
