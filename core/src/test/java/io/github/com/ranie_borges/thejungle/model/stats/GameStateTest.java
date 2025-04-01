package io.github.com.ranie_borges.thejungle.model.stats;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.NonPlayerCharacter;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.junit.Before;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameStateTest {
    private GameState gameState;
    private TestCharacter testCharacter;
    private TestAmbient testAmbient;
    private TestEvent testEvent;

    @Before
    public void setUp() {
        gameState = new GameState();
        testCharacter = new TestCharacter("Test Player");
        testAmbient = new TestAmbient("Test Ambient");
        testEvent = new TestEvent("Test Event", "Test Description", 0.5f);
    }

    @Test
    public void testInitialization() {
        assertNotNull(gameState.getActiveEvents());
        assertTrue(gameState.getActiveEvents().isEmpty());
        assertNotNull(gameState.getOffsetDateTime());
        assertEquals(0, gameState.getDaysSurvived());
        assertNull(gameState.getPlayerCharacter());
        assertNull(gameState.getCurrentAmbient());
    }

    @Test
    public void testPlayerCharacter() {
        gameState.setPlayerCharacter(testCharacter);
        assertEquals(testCharacter, gameState.getPlayerCharacter());
    }

    @Test
    public void testCurrentAmbient() {
        gameState.setCurrentAmbient(testAmbient);
        assertEquals(testAmbient, gameState.getCurrentAmbient());
    }

    @Test
    public void testActiveEvents() {
        List<Event> events = new ArrayList<>();
        events.add(testEvent);

        gameState.setActiveEvents(events);
        assertEquals(1, gameState.getActiveEvents().size());
        assertEquals(testEvent, gameState.getActiveEvents().get(0));
    }

    @Test
    public void testDaysSurvived() {
        gameState.setDaysSurvived(5);
        assertEquals(5, gameState.getDaysSurvived());
    }

    @Test
    public void testOffsetDateTime() {
        OffsetDateTime newDateTime = OffsetDateTime.now().plusDays(1);
        gameState.setOffsetDateTime(newDateTime);
        assertEquals(newDateTime, gameState.getOffsetDateTime());
    }

    // Test classes needed for testing
    private static class TestCharacter extends Character<TestItem> {
        public TestCharacter(String name) {
            super(name);
        }


        public void attack(TestItem other) {}

        @Override
        public boolean attack(double attackDamage, NonPlayerCharacter npc) {
            return false;
        }

        @Override
        public boolean avoidFight(boolean hasTraitLucky) {
            return false;
        }

        @Override
        public void collectItem(boolean hasItemNear, boolean isInventoryFull) {}

        @Override
        public void drink(boolean hasDrinkableItem) {}

        @Override
        public void useItem(Item item) {}

        @Override
        public void dropItem(Item item) {}

        @Override
        public void defend() {}
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

    private static class TestAmbient extends Ambient {
        public TestAmbient(String name) {
            // Use default constructor instead
            super(name, "Test description", 1.0f);
        }

        @Override
        public void explore() {
            // Empty implementation or remove super.explore() if method is abstract
        }

        @Override
        public void generateEvent() {
            // Empty implementation or remove super.generateEvent() if method is abstract
        }

        @Override
        public void modifiesClime() {
            // Empty implementation or remove super.modifiesClime() if method is abstract
        }

        @Override
        public void disableEvent() {
            // Implementation required
        }
    }

    private static class TestEvent extends Event {
        public TestEvent(String name, String description, float probability) {
            super(name, description, probability);
        }

        @Override
        public <T extends Character> void execute(T character) {}
    }
}
