package io.github.com.ranie_borges.thejungle.model.world;

public class TestAmbient extends Ambient {
    public TestAmbient(String name) {
        // Call the parent constructor with required parameters
        super(name, "Test description", 1.0f);
    }

    @Override
    public void explore() {
        // Empty implementation for testing
    }

    @Override
    public void generateEvent() {
        // Empty implementation for testing
    }

    @Override
    public void modifiesClime() {
        // Empty implementation for testing
    }

    @Override
    public void disableEvent() {
        // Empty implementation for testing
    }
}
