package io.github.com.ranie_borges.thejungle.game.ambients;

import io.github.com.ranie_borges.thejungle.model.Ambient;
import io.github.com.ranie_borges.thejungle.model.Event;
import io.github.com.ranie_borges.thejungle.model.Item;
import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;

import java.util.Set;

public class Forest extends Ambient {

    protected Forest(String name, String description, float difficult, Set<AmbientAttribute> attributes) {
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
