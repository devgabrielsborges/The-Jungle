package io.github.com.ranie_borges.thejungle.model.world.ambients;

import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;

import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.HUMID_CLIMATE;

public class LakeRiver extends Ambient {

    protected LakeRiver(String name, String description, float difficult, Set<AmbientAttribute> attributes) {
        super("Lake River", "A tranquil body of water with clear surface, surrounded by vibrant vegetation and teeming with aquatic life.", 1.5f, Set.of(HUMID_CLIMATE));
        // setPossibleEvents();   //FIXME refactor setPossibleEvents method
        setResources(Set.of(
            new Drinkable("Fresh Water", 0.1f, 1.0f),
            new Food("Wild Berries", 0.5f, 1.2f),
            new Material( "Rope", 1.0f, 0.5f),
            new Material("Stick", 0.2f, 0.5f)
        ));

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
