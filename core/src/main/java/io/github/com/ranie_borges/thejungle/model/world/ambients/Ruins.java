package io.github.com.ranie_borges.thejungle.model.world.ambients;

import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;

import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.DRY_CLIMATE;
import static io.github.com.ranie_borges.thejungle.model.enums.Clime.DESERT;

public class Ruins extends Ambient {

    protected Ruins(String name, String description, float difficult, Set<AmbientAttribute> attributes) {
        super("Ruins", "Ancient stone structures overtaken by time, with crumbling walls and hidden passages. Artifacts of a forgotten civilization may be found here.", 2.5f, Set.of(DRY_CLIMATE));
        setClimes(Set.of(DESERT));
        setResources(Set.of(
            new Drinkable("Stagnant Water", 0.2f, 0.3f)
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
