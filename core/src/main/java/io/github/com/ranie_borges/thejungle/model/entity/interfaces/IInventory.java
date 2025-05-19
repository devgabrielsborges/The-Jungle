package io.github.com.ranie_borges.thejungle.model.entity.interfaces;

import com.badlogic.gdx.utils.Array;
import io.github.com.ranie_borges.thejungle.model.entity.Item;

/**
 * Interface for inventory management capabilities
 */
public interface IInventory {
    /**
     * Gets the inventory items
     * 
     * @return Array of items in the inventory
     */
    Array<Item> getInventory();

    /**
     * Adds an item to the inventory
     * 
     * @param item The item to add
     */
    void insertItemInInventory(Item item);

    /**
     * Gets an item from the inventory by index
     * 
     * @param index The index of the item
     * @return The item at the specified index
     */
    Item getItem(int index);

    /**
     * Removes an item from the inventory
     * 
     * @param index The index of the item to remove
     */
    void dropItem(int index);

    /**
     * Drops a specific item from inventory
     * 
     * @param item The item to drop
     */
    void dropItem(Item item);

    /**
     * Clears all items from the inventory
     */
    void emptyInventory();

    /**
     * Checks if an item can be added to the inventory based on weight
     * 
     * @param itemWeight Weight of the item to check
     * @return True if the item can be carried, false otherwise
     */
    boolean canCarryMore(float itemWeight);

    /**
     * Increases the inventory capacity
     * 
     * @param newCapacity The new capacity value
     */
    void increaseInventoryCapacity(int newCapacity);

    /**
     * Checks if a new inventory capacity is valid
     * 
     * @param newCapacity The capacity to check
     * @return True if the capacity is valid, false otherwise
     */
    boolean isNewInventoryCapacityOk(int newCapacity);

    /**
     * Checks if the inventory is full
     * 
     * @return True if the inventory is full, false otherwise
     */
    boolean isInventoryFull();

    /**
     * Checks if an inventory index is valid
     * 
     * @param index The index to check
     * @return True if the index is valid, false otherwise
     */
    boolean isInventoryIndexOk(int index);

    /**
     * Checks if an inventory slot is empty
     * 
     * @param index The index to check
     * @return True if the slot is empty, false otherwise
     */
    boolean isInventoryIndexFree(int index);

    /**
     * Checks if the inventory is empty
     * 
     * @return True if the inventory is empty, false otherwise
     */
    boolean isInventoryEmpty();

    /**
     * Gets the number of items in the inventory
     * 
     * @return The inventory size
     */
    int getInventorySize();

    /**
     * Gets the maximum inventory capacity
     * 
     * @return The maximum inventory capacity
     */
    int getMaxInventoryCapacity();

    /**
     * Gets the initial inventory capacity
     * 
     * @return The initial inventory capacity
     */
    int getInventoryInitialCapacity();
}
