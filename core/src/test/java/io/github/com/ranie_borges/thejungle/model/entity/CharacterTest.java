package io.github.com.ranie_borges.thejungle.model.entity;

import io.github.com.ranie_borges.thejungle.model.enums.Trait;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class CharacterTest {
    private TestCharacter character;

    @Before
    public void setUp() {
        character = new TestCharacter("Test Character");
    }

    @Test
    public void testInitialization() {
        assertEquals("Test Character", character.getName());
        assertEquals(100.0f, character.getLife(), 0.001f);
        assertEquals(0.0f, character.getHunger(), 0.001f);
        assertEquals(0.0f, character.getThirsty(), 0.001f);
        assertEquals(100.0f, character.getEnergy(), 0.001f);
        assertEquals(100.0f, character.getSanity(), 0.001f);
        assertTrue(character.isInventoryEmpty());
        assertEquals("TestCharacter", character.getCharacterType());
    }

    @Test
    public void testSetters() {
        character.setName("New Name");
        character.setLife(80.0f);
        character.setHunger(30.0f);
        character.setThirsty(20.0f);
        character.setEnergy(70.0f);
        character.setSanity(90.0f);

        assertEquals("New Name", character.getName());
        assertEquals(80.0f, character.getLife(), 0.001f);
        assertEquals(30.0f, character.getHunger(), 0.001f);
        assertEquals(20.0f, character.getThirsty(), 0.001f);
        assertEquals(70.0f, character.getEnergy(), 0.001f);
        assertEquals(90.0f, character.getSanity(), 0.001f);
    }

    @Test
    public void testInventoryManagement() {
        TestItem item = new TestItem("Sword");
        TestItem item2 = new TestItem("Shield");

        // Initially empty
        assertTrue(character.isInventoryEmpty());
        assertEquals(0, character.getInventorySize());

        // Insert item
        character.insertItemInInventory(item);
        assertFalse(character.isInventoryEmpty());
        assertEquals(1, character.getInventorySize());
        assertEquals(item, character.getItem(0));

        // Insert at specific index
        character.insertItemInInventory(item2, 3);
        assertEquals(4, character.getInventorySize());
        assertEquals(item2, character.getItem(3));

        // Drop item
        character.dropItem(0);
        assertNull(character.getItem(0));

        // Empty inventory
        character.emptyInventory();
        assertTrue(character.isInventoryEmpty());
    }

    @Test
    public void testInventoryCapacity() {
        assertEquals(15, character.getInventoryInitialCapacity());

        // Increase capacity
        character.increaseInventoryCapacity(20);
        assertEquals(20, character.getInventoryInitialCapacity());

        // Test inventory full logic
        for (int i = 0; i < 20; i++) {
            character.insertItemInInventory(new TestItem("Item" + i));
        }
        assertTrue(character.isInventoryFull());

        // Try adding when full
        TestItem extraItem = new TestItem("Extra");
        character.insertItemInInventory(extraItem);
        assertFalse(character.getInventory().contains(extraItem, true));
    }

    @Test
    public void testCombatStats() {
        assertEquals(0.0, character.getAttackDamage(), 0.001);
        assertEquals(0.0, character.getDefenseStatus(), 0.001);

        character.setAttackDamage(15.5);
        character.setDefenseStatus(10.0);

        assertEquals(15.5, character.getAttackDamage(), 0.001);
        assertEquals(10.0, character.getDefenseStatus(), 0.001);
    }

    @Test
    public void testTraits() {
        assertTrue(character.getTraits().isEmpty());

        List<Trait> traits = new ArrayList<>();
        traits.add(Trait.STRONG);
        traits.add(Trait.FAST);

        character.setTraits(traits);
        assertEquals(2, character.getTraits().size());
        assertTrue(character.getTraits().contains(Trait.STRONG));
        assertTrue(character.getTraits().contains(Trait.FAST));
    }

    @Test
    public void testLocalization() {
        double[] initialLoc = character.getLocalization();
        assertEquals(0.0, initialLoc[0], 0.001);
        assertEquals(0.0, initialLoc[1], 0.001);

        double[] newLoc = {10.5, 20.3};
        character.setLocalization(newLoc);

        double[] retrievedLoc = character.getLocalization();
        assertEquals(10.5, retrievedLoc[0], 0.001);
        assertEquals(20.3, retrievedLoc[1], 0.001);
    }

    private static class TestCharacter extends Character<TestItem> {
        public TestCharacter(String name) {
            super(name);
        }

        /**
         * @param item
         */
        @Override
        public void dropItem(Item item) {

        }

        /**
         *
         */
        @Override
        public void defend() {

        }

        /**
         * @param attackDamage The amount of damage to inflict
         * @param npc          The non-player character target
         * @return
         */
        @Override
        public boolean attack(double attackDamage, NonPlayerCharacter npc) {
            return false;
        }

        /**
         * @param hasTraitLucky Whether the character has the lucky trait
         * @return
         */
        @Override
        public boolean avoidFight(boolean hasTraitLucky) {
            return false;
        }

        /**
         * @param hasItemNear     Whether an item is available nearby
         * @param isInventoryFull Whether the character's inventory is full
         */
        @Override
        public void collectItem(boolean hasItemNear, boolean isInventoryFull) {

        }

        /**
         * @param hasDrinkableItem Whether the character has a drinkable item
         */
        @Override
        public void drink(boolean hasDrinkableItem) {

        }

        /**
         * @param item The item to use
         */
        @Override
        public void useItem(Item item) {

        }
    }

    private static class TestItem extends Item {
        public TestItem(String name) {
            super(name, 1.0f, 100.0f);
        }

        @Override
        public void useItem() {
            // Implementation for testing
        }

        @Override
        public void dropItem() {
            // Implementation for testing
        }
    }
}
