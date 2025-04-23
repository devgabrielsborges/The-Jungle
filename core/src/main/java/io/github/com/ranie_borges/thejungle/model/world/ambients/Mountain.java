package io.github.com.ranie_borges.thejungle.model.world.ambients;

import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;

import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.DRY_CLIMATE;
import static io.github.com.ranie_borges.thejungle.model.enums.Clime.SNOW;

public class Mountain extends Ambient {

    protected Mountain(String name, String description, float difficult, Set<AmbientAttribute> attributes) {
        super("Mountain", "Steep rocky peaks with thin air and panoramic views. The harsh terrain offers limited resources but valuable minerals.", 3.0f, Set.of(DRY_CLIMATE));
        setClimes(Set.of(SNOW));
        setResources(Set.of(
            new Drinkable("Mountain Spring Water", 0.1f, 1.0f),
            new Food("Wild Berries", 0.5f, 1.2f)
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
