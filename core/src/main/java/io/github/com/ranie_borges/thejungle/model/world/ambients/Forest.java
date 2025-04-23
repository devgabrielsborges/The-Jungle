package io.github.com.ranie_borges.thejungle.model.world.ambients;

import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;

import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.ABUNDANT_FAUNA;
import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.DENSE_VEGETATION;
import static io.github.com.ranie_borges.thejungle.model.enums.Clime.FOREST;

public class Forest extends Ambient {

    protected Forest(String name, String description, float difficult, Set<AmbientAttribute> attributes) {
        super("Forest", "A dense forest with tall trees, lush vegetation, and sounds of wild animals.", 1f, Set.of(DENSE_VEGETATION, ABUNDANT_FAUNA));
        setClimes(Set.of(FOREST));
        setResources(Set.of(
            new Drinkable("Stream Water", 0.1f, 0.8f),
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
