package io.github.com.ranie_borges.thejungle.model.stats.events.entity;

import com.badlogic.gdx.utils.Array;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.NonPlayerCharacter;
import io.github.com.ranie_borges.thejungle.model.enums.Trait;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class NonPlayerCharacterTest {
    private TestNPC npc;

    @Before
    public void setUp() {
        npc = new TestNPC("Test NPC");
    }

    @Test
    public void testInitialization() {
        assertEquals("Test NPC", npc.getName());
        assertEquals(100.0f, npc.getLife(), 0.001f);
        assertEquals(0.0f, npc.getHunger(), 0.001f);
        assertEquals(0.0f, npc.getThirsty(), 0.001f);
        assertEquals(100.0f, npc.getEnergy(), 0.001f);
        assertEquals(100.0f, npc.getSanity(), 0.001f);
        assertEquals(0.0, npc.getAttackDamage(), 0.001);
        assertEquals(0.0, npc.getDefenseStatus(), 0.001);
        assertEquals(0, npc.getInventory().size);
        assertTrue(npc.getTraits().isEmpty());
        assertArrayEquals(new double[]{0.0, 0.0}, npc.getLocalization(), 0.001);
        assertFalse(npc.isHostile());
        assertTrue(npc.isAlive());
    }

    @Test
    public void testSetLife() {
        npc.setLife(50.0f);
        assertEquals(50.0f, npc.getLife(), 0.001f);

        npc.setLife(-10.0f);
        assertEquals(-10.0f, npc.getLife(), 0.001f);
        assertFalse(npc.isAlive());
    }

    @Test
    public void testSetStats() {
        npc.setHunger(50.0f);
        assertEquals(50.0f, npc.getHunger(), 0.001f);

        npc.setThirsty(50.0f);
        assertEquals(50.0f, npc.getThirsty(), 0.001f);

        npc.setEnergy(50.0f);
        assertEquals(50.0f, npc.getEnergy(), 0.001f);

        npc.setSanity(50.0f);
        assertEquals(50.0f, npc.getSanity(), 0.001f);
    }

    @Test
    public void testInventoryMethods() {
        Item item1 = new TestItem("Item 1");
        Item item2 = new TestItem("Item 2");

        // Test insert
        npc.insertItemInInventory(item1);
        assertEquals(1, npc.getInventory().size);
        assertEquals("Item 1", npc.getInventory().get(0).getName());

        npc.insertItemInInventory(item2);
        assertEquals(2, npc.getInventory().size);

        // Test drop
        npc.dropItem(0);
        assertEquals(1, npc.getInventory().size);
        assertEquals("Item 2", npc.getInventory().get(0).getName());

        // Test invalid index
        npc.dropItem(5);
        assertEquals(1, npc.getInventory().size);
    }

    @Test
    public void testAttack() {
        TestCharacter character = new TestCharacter("Test Character");
        character.setLife(100.0f);
        character.setDefenseStatus(20.0);

        // Test attack with no damage
        assertFalse(npc.attack(0.0, character));
        assertEquals(100.0f, character.getLife(), 0.001f);

        // Test attack with damage
        assertTrue(npc.attack(10.0, character));
        assertTrue(character.getLife() < 100.0f);

        // Test attack when NPC is dead
        npc.setLife(0.0f);
        assertFalse(npc.attack(10.0, character));

        // Test attack with null target
        assertFalse(npc.attack(10.0, null));
    }

    @Test
    public void testAbstractMethodImplementation() {
        TestCharacter character = new TestCharacter("Test Character");

        npc.interact(character);
        assertTrue(npc.hasInteracted());
        assertEquals(character, npc.getLastInteractedWith());

        npc.roam();
        assertTrue(npc.hasRoamed());
    }

    // Test classes
    private static class TestNPC extends NonPlayerCharacter<TestItem> {
        private boolean interacted;
        private Character<?> lastInteractedWith;
        private boolean roamed;

        public TestNPC(String name) {
            super(name);
        }

        /**
         * @param attackDamage The amount of damage to inflict
         * @param character    The character to attack
         * @return
         */
        @Override
        public boolean attack(double attackDamage, Character<?> character) {
            return false;
        }

        @Override
        public void interact(Character<?> character) {
            this.interacted = true;
            this.lastInteractedWith = character;
        }

        @Override
        public void roam() {
            this.roamed = true;
        }

        public boolean hasInteracted() {
            return interacted;
        }

        public Character<?> getLastInteractedWith() {
            return lastInteractedWith;
        }

        public boolean hasRoamed() {
            return roamed;
        }
    }

    private static class TestItem extends Item {
        public TestItem(String name) {
            super(name, 1.0f, 100.0f);
        }

        @Override
        public void useItem() {}

        @Override
        public void dropItem() {}
    }

    private static class TestCharacter extends Character<TestItem> {
        public TestCharacter(String name) {
            super(name);
        }

        public void attack(TestCharacter other) {
            // Implementation left empty for testing
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

        @Override
        public void useItem(Item item) {}

        @Override
        public void dropItem(Item item) {}

        @Override
        public void defend() {}
    }
}
