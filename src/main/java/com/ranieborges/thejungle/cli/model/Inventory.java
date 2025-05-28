package com.ranieborges.thejungle.cli.model;

import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.itens.Ammunition; // Import Ammunition
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.entity.itens.Tool;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.AmmunitionType; // Import AmmunitionType
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.ToolType;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Inventory {

    private final List<Item> items;
    @Getter private float currentWeight;
    @Getter private final float maxWeightCapacity;

    public Inventory(float maxWeightCapacity) {
        this.items = new ArrayList<>();
        this.maxWeightCapacity = Math.max(0, maxWeightCapacity);
        this.currentWeight = 0f;
    }

    private void recalculateCurrentWeight() {
        this.currentWeight = 0f;
        for (Item item : this.items) {
            // If Ammunition's weight is dynamic based on quantity, this needs to be reflected.
            // Assuming Item.getWeight() returns the current correct weight.
            this.currentWeight += item.getWeight();
        }
    }

    public boolean addItem(Item itemToAdd) {
        if (itemToAdd == null) {
            Message.displayOnScreen(TerminalStyler.error("Cannot add a null item."));
            return false;
        }

        // Handle stacking for Ammunition
        if (itemToAdd instanceof Ammunition newAmmo) {
            for (Item existingItem : items) {
                if (existingItem instanceof Ammunition existingAmmo && existingAmmo.getAmmunitionType() == newAmmo.getAmmunitionType()) {
                    if (this.currentWeight + newAmmo.getWeight() <= this.maxWeightCapacity) { // Check if adding the new stack's weight is okay
                        existingAmmo.increaseQuantity(newAmmo.getQuantity());
                        // Weight of existingAmmo stack needs to be updated if its getWeight() is not dynamic
                        // For simplicity, assume Ammunition.getWeight() reflects its current quantity * unit weight
                        // or that recalculateCurrentWeight() handles it.
                        recalculateCurrentWeight(); // Recalculate total inventory weight
                        Message.displayOnScreen(TerminalStyler.success("Added " + newAmmo.getQuantity() + " " + newAmmo.getName() + " to existing stack. Total: " + existingAmmo.getQuantity()));
                        return true;
                    } else {
                        Message.displayOnScreen(TerminalStyler.warning("Cannot add more " + newAmmo.getName() + ". Inventory is full or item is too heavy."));
                        return false;
                    }
                }
            }
        }

        // If not stackable or no existing stack, add as new item
        if (this.currentWeight + itemToAdd.getWeight() <= this.maxWeightCapacity) {
            this.items.add(itemToAdd);
            recalculateCurrentWeight();
            Message.displayOnScreen(TerminalStyler.success(itemToAdd.getName() + " added to inventory."));
            return true;
        } else {
            Message.displayOnScreen(TerminalStyler.warning("Cannot add " + itemToAdd.getName() + ". Inventory is full or item is too heavy. " +
                    "Available capacity: " + String.format("%.1f", (this.maxWeightCapacity - this.currentWeight)) +
                    ", Item weight: " + String.format("%.1f", itemToAdd.getWeight())));
            return false;
        }
    }

    public void removeItem(Item itemInstance) {
        if (itemInstance != null && this.items.remove(itemInstance)) {
            recalculateCurrentWeight();
            Message.displayOnScreen(TerminalStyler.info(itemInstance.getName() + " removed from inventory."));
        }
    }


    public Optional<Item> findItemByName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return Optional.empty();
        }
        return this.items.stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemName))
                .findFirst();
    }

    public boolean useItemByName(String itemName, Character user) {
        Optional<Item> itemOptional = findItemByName(itemName);
        if (itemOptional.isPresent()) {
            Item itemToUse = itemOptional.get();
            Message.displayOnScreen(user.getName() + " attempts to use " + itemToUse.getName() + "...");

            boolean consumedOrBroke = itemToUse.use(user);

            if (consumedOrBroke) {
                Message.displayOnScreen(TerminalStyler.info(itemToUse.getName() + " was consumed or broke after use."));
                this.items.remove(itemToUse);
                recalculateCurrentWeight();
            }
            return true;
        } else {
            Message.displayOnScreen(TerminalStyler.warning("Cannot use '" + itemName + "'. Item not found in inventory."));
            return false;
        }
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    public void displayInventory() {
        Message.displayOnScreen(TerminalStyler.style("--- Inventory ---", TerminalStyler.MAGENTA, TerminalStyler.BOLD));
        Message.displayOnScreen(String.format("Capacity: %.1f / %.1f (kg)", getCurrentWeight(), getMaxWeightCapacity()));
        if (items.isEmpty()) {
            Message.displayOnScreen("Inventory is empty.");
        } else {
            Message.displayOnScreen("Items:");
            for (int i = 0; i < items.size(); i++) {
                Message.displayOnScreen(String.format(" %d. %s", i + 1, items.get(i).toString()));
            }
        }
        Message.displayOnScreen(TerminalStyler.style("-----------------", TerminalStyler.MAGENTA));
    }

    public int countItemsByMaterialType(MaterialType materialType) {
        int count = 0;
        for (Item item : items) {
            if (item instanceof Material && ((Material) item).getMaterialType() == materialType) {
                count++;
            }
        }
        return count;
    }

    public int countSpecificItemByName(String itemName) {
        int count = 0;
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                count++;
            }
        }
        return count;
    }

    public boolean hasToolType(ToolType toolType) {
        for (Item item : items) {
            if (item instanceof Tool tool && tool.getToolType() == toolType && tool.getDurability() > 0) {
                    return true;
                }

        }
        return false;
    }


    public int countAmmunitionByType(AmmunitionType ammoType) {
        int totalQuantity = 0;
        for (Item item : items) {
            if (item instanceof Ammunition ammoItem && ammoItem.getAmmunitionType() == ammoType) {
                totalQuantity += ammoItem.getQuantity();
            }
        }
        return totalQuantity;
    }

    public boolean removeAmmunitionByType(AmmunitionType ammoType, int quantityToRemove) {
        if (quantityToRemove <= 0) return true; // Nothing to remove

        int quantityActuallyRemoved = 0;
        List<Ammunition> ammoStacksOfType = items.stream()
                .filter(item -> item instanceof Ammunition && ((Ammunition) item).getAmmunitionType() == ammoType)
                .map(Ammunition.class::cast)
                .toList();

        for (Ammunition ammoStack : ammoStacksOfType) {
            if (quantityActuallyRemoved >= quantityToRemove) break;

            int neededFromThisStack = quantityToRemove - quantityActuallyRemoved;
            if (ammoStack.getQuantity() >= neededFromThisStack) {
                ammoStack.decreaseQuantity(neededFromThisStack);
                quantityActuallyRemoved += neededFromThisStack;
            } else { // Take all from this stack
                quantityActuallyRemoved += ammoStack.getQuantity();
                ammoStack.decreaseQuantity(ammoStack.getQuantity()); // Sets quantity to 0
            }

            if (ammoStack.getQuantity() <= 0) {
                items.remove(ammoStack); // Remove the depleted stack
            }
        }
        recalculateCurrentWeight();
        return quantityActuallyRemoved >= quantityToRemove;
    }
}
