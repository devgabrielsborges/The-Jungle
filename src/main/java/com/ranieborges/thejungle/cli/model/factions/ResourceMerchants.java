package com.ranieborges.thejungle.cli.model.factions;

import com.ranieborges.thejungle.cli.model.Faction;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.entity.itens.Material;
import com.ranieborges.thejungle.cli.model.entity.itens.Medicine;
import com.ranieborges.thejungle.cli.model.entity.itens.Tool;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineEffect;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.ToolType;
import com.ranieborges.thejungle.cli.model.factions.utils.FactionDisposition;

import java.util.ArrayList;
import java.util.List;

public class ResourceMerchants extends Faction {
    public static final String FACTION_ID = "merchants_resource";

    public ResourceMerchants() {
        super(
            FACTION_ID,
            "Mercadores de Recursos",
            "Comerciantes astutos que possuem suprimentos raros, mas cobram preços altos.",
            FactionDisposition.NEUTRAL,
            defineTradeableItems(),
            defineDesiredItems()
        );
    }

    private static List<Item> defineTradeableItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Tool("Sturdy Pickaxe", "A well-crafted pickaxe, good for mining.", 2.0f, ToolType.PICKAXE, 80, 1.3f, 7f));
        items.add(new Medicine("Purification Tablets (x5)", "Tablets to purify questionable water.", 0.05f, MedicineType.PURIFICATION_TABLET, MedicineEffect.PURIFY_WATER, 1f, 5));
        items.add(new Material("Iron Ingot", "A bar of refined iron, ready for smithing.", 1.5f, MaterialType.METAL_INGOT, 60));
        items.add(new Food("Preserved Rations", "Dense, long-lasting food rations.", 0.5f, 50f, FoodType.CANNED, Integer.MAX_VALUE, 0f, 0f));
        return items;
    }

    private static List<MaterialType> defineDesiredItems() {
        List<MaterialType> desired = new ArrayList<>();
        desired.add(MaterialType.BONE);
        desired.add(MaterialType.STONE);
        return desired;
    }
}
