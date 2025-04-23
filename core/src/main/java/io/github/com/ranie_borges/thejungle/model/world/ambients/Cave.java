package io.github.com.ranie_borges.thejungle.model.world.ambients;

import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.events.events.Mob;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.HUMID_CLIMATE;
import static io.github.com.ranie_borges.thejungle.model.enums.Clime.CAVE;

public class Cave extends Ambient {

    protected Cave(String name, String description, float difficult, Set<AmbientAttribute> attributes) {
        super("Cave","A dark and damp cave, echoing with the sounds of dripping water and distant growls.", 3.5f, Set.of(HUMID_CLIMATE));
        setClimes(Set.of(CAVE));
        // setPossibleEvents();   //FIXME refactor setPossibleEvents method
        setResources(Set.of(new Drinkable("cave Water", 0.3f, 0.5f)));

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
