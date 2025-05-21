package io.github.com.ranie_borges.thejungle.controller.systems;

import com.google.gson.*;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.itens.*;

import java.lang.reflect.Type;

/**
 * GSON TypeAdapter for serializing and deserializing Item objects and their
 * subtypes.
 * This adapter handles the polymorphic nature of items in the game.
 */
public class ItemAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
    private static final String TYPE_FIELD = "itemType";
    private static final String DATA_FIELD = "itemData";

    @Override
    public JsonElement serialize(Item item, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        String itemType;
        if (item instanceof Weapon) {
            itemType = "Weapon";
        } else if (item instanceof Tool) {
            itemType = "Tool";
        } else if (item instanceof Medicine) {
            itemType = "Medicine";
        } else if (item instanceof Drinkable) {
            itemType = "Drinkable";
        } else if (item instanceof Food) {
            itemType = "Food";
        } else if (item instanceof Material) {
            itemType = "Material";
        } else {
            // Default fallback
            itemType = item.getClass().getSimpleName();
        }

        result.addProperty(TYPE_FIELD, itemType);
        JsonElement itemData = context.serialize(item);
        result.add(DATA_FIELD, itemData);

        return result;
    }

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has(TYPE_FIELD)) {
            throw new JsonParseException("Item type discriminator not found.");
        }

        String itemType = jsonObject.get(TYPE_FIELD).getAsString();
        JsonElement itemData = jsonObject.get(DATA_FIELD);

        try {
            switch (itemType) {
                case "Weapon":
                    return deserializeWeapon(itemData, context);
                case "Tool":
                    return deserializeTool(itemData, context);
                case "Medicine":
                    return deserializeMedicine(itemData, context);
                case "Drinkable":
                    return deserializeDrinkable(itemData, context);
                case "Food":
                    return deserializeFood(itemData, context);
                case "Material":
                    return deserializeMaterial(itemData, context);
                default:
                    throw new JsonParseException("Unknown item type: " + itemType);
            }
        } catch (Exception e) {
            throw new JsonParseException("Error deserializing item: " + e.getMessage());
        }
    }

    private Weapon deserializeWeapon(JsonElement data, JsonDeserializationContext context) {
        JsonObject obj = data.getAsJsonObject();
        String name = obj.has("name") ? obj.get("name").getAsString() : "Unknown Weapon";
        float weight = obj.has("weight") ? obj.get("weight").getAsFloat() : 1.0f;
        float durability = obj.has("durability") ? obj.get("durability").getAsFloat() : 100f;
        float damage = obj.has("damage") ? obj.get("damage").getAsFloat() : 10f;
        float attackSpeed = obj.has("attackSpeed") ? obj.get("attackSpeed").getAsFloat() : 1.0f;

        Weapon weapon = new Weapon(name, weight, durability, damage, attackSpeed);

        if (obj.has("quantity")) {
            weapon.setQuantity(obj.get("quantity").getAsInt());
        }

        return weapon;
    }

    private Tool deserializeTool(JsonElement data, JsonDeserializationContext context) {
        JsonObject obj = data.getAsJsonObject();
        String name = obj.has("name") ? obj.get("name").getAsString() : "Unknown Tool";
        float weight = obj.has("weight") ? obj.get("weight").getAsFloat() : 1.0f;
        float durability = obj.has("durability") ? obj.get("durability").getAsFloat() : 100f;
        float workPower = obj.has("workPower") ? obj.get("workPower").getAsFloat() : 1.0f;
        float usageSpeed = obj.has("usageSpeed") ? obj.get("usageSpeed").getAsFloat() : 1.0f;

        Tool tool = new Tool(name, weight, durability, workPower, usageSpeed);

        if (obj.has("quantity")) {
            tool.setQuantity(obj.get("quantity").getAsInt());
        }

        return tool;
    }

    private Medicine deserializeMedicine(JsonElement data, JsonDeserializationContext context) {
        JsonObject obj = data.getAsJsonObject();
        String name = obj.has("name") ? obj.get("name").getAsString() : "Unknown Medicine";
        float weight = obj.has("weight") ? obj.get("weight").getAsFloat() : 0.5f;
        float durability = obj.has("durability") ? obj.get("durability").getAsFloat() : 100f;
        double healRatio = obj.has("healRatio") ? obj.get("healRatio").getAsDouble() : 25.0;

        Medicine medicine = new Medicine(name, weight, durability, healRatio);

        if (obj.has("quantity")) {
            medicine.setQuantity(obj.get("quantity").getAsInt());
        }

        return medicine;
    }

    private Drinkable deserializeDrinkable(JsonElement data, JsonDeserializationContext context) {
        JsonObject obj = data.getAsJsonObject();
        String name = obj.has("name") ? obj.get("name").getAsString() : "Unknown Drinkable";
        float weight = obj.has("weight") ? obj.get("weight").getAsFloat() : 0.5f;
        float durability = obj.has("durability") ? obj.get("durability").getAsFloat() : 100f;
        boolean potable = obj.has("potable") && obj.get("potable").getAsBoolean();
        float volume = obj.has("volume") ? obj.get("volume").getAsFloat() : 1.0f;

        Drinkable drinkable = new Drinkable(name, weight, durability, potable, volume);

        if (obj.has("quantity")) {
            drinkable.setQuantity(obj.get("quantity").getAsInt());
        }

        return drinkable;
    }

    private Material deserializeMaterial(JsonElement data, JsonDeserializationContext context) {
        JsonObject obj = data.getAsJsonObject();
        String name = obj.has("name") ? obj.get("name").getAsString() : "Unknown Material";
        float weight = obj.has("weight") ? obj.get("weight").getAsFloat() : 0.5f;
        float durability = obj.has("durability") ? obj.get("durability").getAsFloat() : 100f;
        String type = obj.has("type") ? obj.get("type").getAsString() : "";
        float resistance = obj.has("resistance") ? obj.get("resistance").getAsFloat() : 0.5f;

        Material material = new Material(name, weight, durability, type, resistance);

        if (obj.has("quantity")) {
            material.setQuantity(obj.get("quantity").getAsInt());
        }

        return material;
    }

    private Food deserializeFood(JsonElement data, JsonDeserializationContext context) {
        JsonObject obj = data.getAsJsonObject();
        String name = obj.has("name") ? obj.get("name").getAsString() : "Unknown Food";
        float weight = obj.has("weight") ? obj.get("weight").getAsFloat() : 0.5f;
        float durability = obj.has("durability") ? obj.get("durability").getAsFloat() : 100f;
        int nutritionalValue = obj.has("nutritionalValue") ? obj.get("nutritionalValue").getAsInt() : 10;
        String type = obj.has("type") ? obj.get("type").getAsString() : "Generic";
        int shelfLife = obj.has("shelfLife") ? obj.get("shelfLife").getAsInt() : 5;

        Food food = new Food(name, weight, durability, nutritionalValue, type, shelfLife);

        if (obj.has("quantity")) {
            food.setQuantity(obj.get("quantity").getAsInt());
        }

        if (obj.has("spoiled")) {
            // We need to set the spoiled field using reflection as it's a private field
            try {
                java.lang.reflect.Field spoiledField = Food.class.getDeclaredField("spoiled");
                spoiledField.setAccessible(true);
                spoiledField.set(food, obj.get("spoiled").getAsBoolean());
            } catch (Exception e) {
                throw new JsonParseException("Could not set spoiled field: " + e.getMessage());
            }
        }

        return food;
    }
}
