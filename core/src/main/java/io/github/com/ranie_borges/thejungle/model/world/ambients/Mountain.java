package io.github.com.ranie_borges.thejungle.model.world.ambients;

import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;

import java.util.Set;
public class Mountain extends Ambient {

    protected Mountain(String name, String description, float difficult, Set<AmbientAttribute> attributes) {
        super(name, description, difficult, attributes);
    }

    /**
     *
     */
    @Override
    public void explore() {
        super.explore();
    }

    /**
     *
     */
    @Override
    public void generateEvent() {
        super.generateEvent();
    }

    /**
     *
     */
    @Override
    public void modifiesClime() {
        super.modifiesClime();
    }

    /**
     *
     */
    @Override
    public void disableEvent() {

    }
}
