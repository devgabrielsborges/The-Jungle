package com.ranieborges.thejungle.cli.model.factions;

import com.ranieborges.thejungle.cli.model.Faction;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.entity.itens.Medicine;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineEffect;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineType;
import com.ranieborges.thejungle.cli.model.factions.utils.FactionDisposition;

import java.util.ArrayList;
import java.util.List;

public class PeacefulNomads extends Faction {
    public static final String FACTION_ID = "nomads_peaceful";

    public PeacefulNomads() {
        super(
            FACTION_ID,
            "Nômades Pacíficos",
            "Um grupo errante que valoriza a cooperação e o comércio justo. Evitam conflitos.",
            FactionDisposition.FRIENDLY,
            defineTradeableItems(),
            defineDesiredItems()
        );
    }

    private static List<Item> defineTradeableItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Food("Wild Berries", "A handful of sweet, wild berries.", 0.2f, 10f, FoodType.FRUIT, 2, 0.01f, 0f));
        items.add(new Material("Woven Cordage", "Strong cordage woven from plant fibers.", 0.1f, MaterialType.FIBER, 10));
        items.add(new Medicine("Herbal Salve", "A soothing salve for minor cuts and burns.", 0.1f, MedicineType.HERBAL_SALVE, MedicineEffect.HEAL_WOUNDS, 8f, 3));
        return items;
    }

    private static List<MaterialType> defineDesiredItems() {
        List<MaterialType> desired = new ArrayList<>();
        desired.add(MaterialType.METAL_ORE);
        desired.add(MaterialType.LEATHER);
        return desired;
    }
}
