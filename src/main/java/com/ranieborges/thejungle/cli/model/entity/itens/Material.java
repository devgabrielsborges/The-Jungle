package com.ranieborges.thejungle.cli.model.entity.itens;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.view.Message; // For output
import lombok.Getter;


@Getter
public class Material extends Item {

    private final MaterialType materialType;
    private final int resistance;

    public Material(String name, String description, float weight, MaterialType materialType, int resistance) {
        super(name, description, weight);
        if (resistance < 0) {
            throw new IllegalArgumentException("Material resistance cannot be negative.");
        }
        this.materialType = materialType;
        this.resistance = resistance;
    }

    @Override
    public boolean use(Character user) {
        Message.displayOnScreen(user.getName() + " examines " + getName() + ". It's a " + materialType.getDisplayName() + ".");
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s (Type: %s, Resistance: %d, Wt: %.1f)",
                getName(), materialType.getDisplayName(), resistance, getWeight());
    }
}
