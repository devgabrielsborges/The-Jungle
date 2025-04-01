package io.github.com.ranie_borges.thejungle.model.world;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AmbientTest {
    private TestAmbient ambient;
    private static final String TEST_NAME = "Test Jungle";
    private static final String TEST_DESCRIPTION = "A test jungle environment";
    private static final float TEST_DIFFICULTY = 2.5f;

    @Before
    public void setUp() {
        ambient = new TestAmbient(TEST_NAME);
        ambient.setDescription(TEST_DESCRIPTION);
        ambient.setDifficult(TEST_DIFFICULTY);
    }

    @Test
    public void testConstructor() {
        assertEquals(TEST_NAME, ambient.getName());
        assertEquals(TEST_DESCRIPTION, ambient.getDescription());
        assertEquals(TEST_DIFFICULTY, ambient.getDifficult(), 0.001);
        assertNotNull(ambient.getAttributes());
        assertNotNull(ambient.getResources());
        assertNotNull(ambient.getPossibleEvents());
        assertNotNull(ambient.getClimes());
        assertTrue(ambient.getAttributes().isEmpty());
    }

    @Test
    public void testConstructorWithAttributes() {
        Set<AmbientAttribute> attributes = new HashSet<>();
        attributes.add(AmbientAttribute.DENSE_VEGETATION);
        attributes.add(AmbientAttribute.HUMID_CLIMATE);

        TestAmbient ambientWithAttributes = new TestAmbient(
            "Jungle with Attributes",
            "A jungle with predefined attributes",
            3.0f,
            attributes
        );

        assertEquals("Jungle with Attributes", ambientWithAttributes.getName());
        assertEquals("A jungle with predefined attributes", ambientWithAttributes.getDescription());
        assertEquals(3.0f, ambientWithAttributes.getDifficult(), 0.001);
        assertEquals(2, ambientWithAttributes.getAttributes().size());
        assertTrue(ambientWithAttributes.getAttributes().contains(AmbientAttribute.DENSE_VEGETATION));
        assertTrue(ambientWithAttributes.getAttributes().contains(AmbientAttribute.HUMID_CLIMATE));
    }

    @Test
    public void testSetName() {
        String newName = "New Ambient Name";
        ambient.setName(newName);
        assertEquals(newName, ambient.getName());
    }

    @Test
    public void testSetDescription() {
        String newDescription = "New Ambient Description";
        ambient.setDescription(newDescription);
        assertEquals(newDescription, ambient.getDescription());
    }

    @Test
    public void testSetDifficult() {
        float newDifficulty = 4.5f;
        ambient.setDifficult(newDifficulty);
        assertEquals(newDifficulty, ambient.getDifficult(), 0.001);
    }

    @Test
    public void testAddResource() {
        TestItem testItem = new TestItem("Test Item");
        ambient.addResource(testItem);

        assertTrue(ambient.getResources().contains(testItem));
        assertEquals(1, ambient.getResources().size());
    }

    @Test
    public void testSetResources() {
        Set<Item> resources = new HashSet<>();
        TestItem testItem1 = new TestItem("Test Item 1");
        TestItem testItem2 = new TestItem("Test Item 2");

        resources.add(testItem1);
        resources.add(testItem2);

        ambient.setResources(resources);

        assertEquals(2, ambient.getResources().size());
        assertTrue(ambient.getResources().contains(testItem1));
        assertTrue(ambient.getResources().contains(testItem2));
    }

    @Test
    public void testResourcesUnmodifiableView() {
        TestItem testItem = new TestItem("Test Item");
        ambient.addResource(testItem);

        Set<Item> resources = ambient.getResources();
        try {
            resources.add(new TestItem("New Item"));
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected exception
        }
    }

    @Test
    public void testSetPossibleEvents() {
        Map<Event, Double> events = new HashMap<>();
        TestEvent testEvent1 = new TestEvent("Test Event 1", 0.3f);
        TestEvent testEvent2 = new TestEvent("Test Event 2", 0.7f);

        events.put(testEvent1, 0.3);
        events.put(testEvent2, 0.7);

        ambient.setPossibleEvents(events);

        assertEquals(2, ambient.getPossibleEvents().size());
        assertTrue(ambient.getPossibleEvents().containsKey(testEvent1));
        assertTrue(ambient.getPossibleEvents().containsKey(testEvent2));
        assertEquals(0.3, ambient.getPossibleEvents().get(testEvent1), 0.001);
        assertEquals(0.7, ambient.getPossibleEvents().get(testEvent2), 0.001);
    }

    @Test
    public void testPossibleEventsUnmodifiableView() {
        Map<Event, Double> events = new HashMap<>();
        events.put(new TestEvent("Test Event", 0.5f), 0.5);
        ambient.setPossibleEvents(events);

        Map<Event, Double> possibleEvents = ambient.getPossibleEvents();
        try {
            possibleEvents.put(new TestEvent("New Event", 0.3f), 0.3);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected exception
        }
    }

    @Test
    public void testSetAttributes() {
        Set<AmbientAttribute> attributes = new HashSet<>();
        // Use all available enum values
        attributes.add(AmbientAttribute.DENSE_VEGETATION);
        attributes.add(AmbientAttribute.ABUNDANT_FAUNA);
        attributes.add(AmbientAttribute.HUMID_CLIMATE);

        ambient.setAttributes(attributes);

        assertEquals(3, ambient.getAttributes().size());
        assertTrue(ambient.getAttributes().contains(AmbientAttribute.DENSE_VEGETATION));
        assertTrue(ambient.getAttributes().contains(AmbientAttribute.ABUNDANT_FAUNA));
        assertTrue(ambient.getAttributes().contains(AmbientAttribute.HUMID_CLIMATE));
    }

    @Test
    public void testAttributesModifiable() {
        // Since attributes should be directly modifiable, verify this behavior
        Set<AmbientAttribute> initialAttributes = new HashSet<>();
        initialAttributes.add(AmbientAttribute.DENSE_VEGETATION);
        ambient.setAttributes(initialAttributes);

        // Get and modify the attributes directly
        Set<AmbientAttribute> attributes = ambient.getAttributes();
        attributes.add(AmbientAttribute.HUMID_CLIMATE);

        // Verify the changes reflect in ambient
        assertEquals(2, ambient.getAttributes().size());
        assertTrue(ambient.getAttributes().contains(AmbientAttribute.HUMID_CLIMATE));
    }

    @Test
    public void testAddClime() {
        Clime testClime = mock(Clime.class);
        ambient.addClime(testClime);
        assertTrue(ambient.getClimes().contains(testClime));
    }

    @Test
    public void testSetClimes() {
        Set<Clime> climes = new HashSet<>();
        Clime testClime1 = mock(Clime.class);
        Clime testClime2 = mock(Clime.class);

        climes.add(testClime1);
        climes.add(testClime2);

        ambient.setClimes(climes);

        assertEquals(2, ambient.getClimes().size());
        assertTrue(ambient.getClimes().contains(testClime1));
        assertTrue(ambient.getClimes().contains(testClime2));
    }

    @Test
    public void testClimesUnmodifiableView() {
        Clime testClime = mock(Clime.class);
        ambient.addClime(testClime);

        Set<Clime> climes = ambient.getClimes();
        try {
            climes.add(mock(Clime.class));
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Expected exception
        }
    }

    @Test
    public void testExplore() {
        TestAmbient spyAmbient = spy(ambient);
        doNothing().when(spyAmbient).generateEvent();

        spyAmbient.explore();
        verify(spyAmbient).generateEvent();
    }

    @Test
    public void testModifiesClimeAndDisableEvent() {
        TestAmbient spyAmbient = spy(ambient);

        spyAmbient.modifiesClime();
        verify(spyAmbient).modifiesClime();

        spyAmbient.disableEvent();
        verify(spyAmbient).disableEvent();
    }

    // Test implementations
    private static class TestAmbient extends Ambient {
        public TestAmbient(String name) {
            super(name, "Test description", 1.0f);
        }

        public TestAmbient(String name, String description, float difficult, Set<AmbientAttribute> attributes) {
            super(name, description, difficult, attributes);
        }

        @Override
        public void disableEvent() {
            // Implementation for testing
        }

        @Override
        public void explore() {
            generateEvent();
        }

        @Override
        public void generateEvent() {
            // Test implementation
        }

        @Override
        public void modifiesClime() {
            // Test implementation
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

    private static class TestEvent extends Event {
        public TestEvent(String name, float probability) {
            super(name, "Test description", probability);
        }

        @Override
        public <T extends Character> void execute(T character) {
            // Test implementation
        }
    }
}
