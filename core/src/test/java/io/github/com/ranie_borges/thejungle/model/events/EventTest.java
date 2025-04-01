package io.github.com.ranie_borges.thejungle.model.events;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.NonPlayerCharacter;
import io.github.com.ranie_borges.thejungle.model.entity.enums.Attribute;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class EventTest {
    private TestEvent event;

    @Before
    public void setUp() {
        event = new TestEvent("Test Event", "A test event", 0.5f);
    }

    @Test
    public void testEventInitialization() {
        assertEquals("Test Event", event.getName());
        assertEquals("A test event", event.getDescription());
        assertEquals(0.5f, event.getProbability(), 0.001f);
        assertTrue(event.getImpacts().isEmpty());
        assertTrue(event.isPossible());
    }

    @Test
    public void testSetters() {
        event.setName("New Event");
        event.setDescription("New description");
        event.setProbability(0.8f);
        event.setPossible(false);

        assertEquals("New Event", event.getName());
        assertEquals("New description", event.getDescription());
        assertEquals(0.8f, event.getProbability(), 0.001f);
        assertFalse(event.isPossible());
    }

    @Test
    public void testProbabilityBounds() {
        // Test probability upper bound
        event.setProbability(1.5f);
        assertEquals(1.0f, event.getProbability(), 0.001f);

        // Test probability lower bound
        event.setProbability(-0.5f);
        assertEquals(0.0f, event.getProbability(), 0.001f);
    }

    @Test
    public void testImpactsManagement() {
        // Add single impact
        event.addImpact(Attribute.LIFE);
        assertEquals(1, event.getImpacts().size());
        assertTrue(event.getImpacts().contains(Attribute.LIFE));

        // Add another impact
        event.addImpact(Attribute.ENERGY);
        assertEquals(2, event.getImpacts().size());
        assertTrue(event.getImpacts().contains(Attribute.ENERGY));

        // Set impacts as a list
        List<Attribute> newImpacts = Arrays.asList(Attribute.HUNGER, Attribute.SANITY);
        event.setImpacts(newImpacts);
        assertEquals(2, event.getImpacts().size());
        assertTrue(event.getImpacts().contains(Attribute.HUNGER));
        assertTrue(event.getImpacts().contains(Attribute.SANITY));
        assertFalse(event.getImpacts().contains(Attribute.LIFE));
    }

    @Test
    public void testImpactsImmutability() {
        event.addImpact(Attribute.LIFE);

        List<Attribute> impacts = event.getImpacts();
        try {
            impacts.add(Attribute.ENERGY);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testExecute() {
        TestCharacter character = new TestCharacter("Test Character");
        event.execute(character);
        assertTrue(character.wasAffected());
    }

    // Test implementation of Event
    private static class TestEvent extends Event {
        public TestEvent(String name, String description, float probability) {
            super(name, description, probability);
        }

        @Override
        public <T extends Character> void execute(T character) {
            if (character instanceof TestCharacter) {
                ((TestCharacter) character).affect();
            }
        }
    }

    // Test implementation of Character
    private static class TestCharacter extends Character<TestItem> {
        private boolean affected;

        public TestCharacter(String name) {
            super(name);
            this.affected = false;
        }

        public void affect() {
            this.affected = true;
        }

        public boolean wasAffected() {
            return affected;
        }

        public void attack(TestItem other) {
            // Not needed for this test
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
        public void useItem(Item item) {
            // Not needed for this test
        }

        @Override
        public void dropItem(Item item) {
            // Not needed for this test
        }

        @Override
        public void defend() {
            // Not needed for this test
        }
    }

    // Test implementation of Item
    private static class TestItem extends Item {
        public TestItem(String name) {
            super(name, 1.0f, 100.0f);
        }

        @Override
        public void useItem() {
            // Not needed for this test
        }

        @Override
        public void dropItem() {
            // Not needed for this test
        }
    }
}
