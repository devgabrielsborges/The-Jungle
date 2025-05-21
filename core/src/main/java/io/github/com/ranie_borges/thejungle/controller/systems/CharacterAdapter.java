package io.github.com.ranie_borges.thejungle.controller.systems;

import com.badlogic.gdx.utils.Array;
import com.google.gson.*;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Doctor;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Hunter;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Lumberjack;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Survivor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Custom adapter for serializing and deserializing Character objects
 */
public class CharacterAdapter implements JsonSerializer<Character>, JsonDeserializer<Character> {
    private static final Logger logger = LoggerFactory.getLogger(CharacterAdapter.class);

    @Override
    public JsonElement serialize(Character src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null)
            return JsonNull.INSTANCE;

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("characterType", src.getClass().getSimpleName());

        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("life", src.getLife());
        jsonObject.addProperty("hunger", src.getHunger());
        jsonObject.addProperty("thirsty", src.getThirsty());
        jsonObject.addProperty("energy", src.getEnergy());
        jsonObject.addProperty("sanity", src.getSanity());
        jsonObject.addProperty("maxCarryWeight", src.getMaxCarryWeight());
        jsonObject.addProperty("inventoryInitialCapacity", src.getInventoryInitialCapacity());
        jsonObject.addProperty("maxInventoryCapacity", src.getMaxInventoryCapacity());
        jsonObject.addProperty("attackDamage", src.getAttackDamage());

        // Serialize position
        JsonObject positionObj = new JsonObject();
        positionObj.addProperty("x", src.getPosition().x);
        positionObj.addProperty("y", src.getPosition().y);
        jsonObject.add("position", positionObj);

        // Serialize inventory - handle libGDX Array properly
        JsonArray inventoryArray = new JsonArray();
        if (src.getInventory() != null) {
            Array<Item> inventory = src.getInventory();
            for (int i = 0; i < inventory.size; i++) {
                Item item = inventory.get(i);
                if (item != null) {
                    // Use ItemAdapter for serialization
                    try {
                        JsonElement itemElement = context.serialize(item, Item.class);
                        inventoryArray.add(itemElement);
                    } catch (Exception e) {
                        logger.error("Failed to serialize inventory item: {}", e.getMessage());
                    }
                }
            }
        }
        jsonObject.add("inventory", inventoryArray);

        return jsonObject;
    }

    @Override
    public Character deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull())
            return null;

        try {
            JsonObject jsonObject = json.getAsJsonObject();
            String characterType = jsonObject.get("characterType").getAsString();

            float xPos = 100.0f; // Default position
            float yPos = 100.0f;

            if (jsonObject.has("position")) {
                JsonObject positionObj = jsonObject.getAsJsonObject("position");
                xPos = positionObj.get("x").getAsFloat();
                yPos = positionObj.get("y").getAsFloat();
            }

            Character character;
            switch (characterType) {
                case "Hunter":
                    character = new Hunter(
                            jsonObject.get("name").getAsString(),
                            xPos,
                            yPos);
                    break;
                case "Doctor":
                    character = new Doctor(
                            jsonObject.get("name").getAsString(),
                            xPos,
                            yPos);
                    break;
                case "Lumberjack":
                    character = new Lumberjack(
                            jsonObject.get("name").getAsString(),
                            xPos,
                            yPos);
                    break;
                case "Survivor":
                default:
                    character = new Survivor(
                            jsonObject.get("name").getAsString(),
                            xPos,
                            yPos);
                    break;
            }

            if (jsonObject.has("life")) {
                character.setLife(jsonObject.get("life").getAsFloat());
            }
            if (jsonObject.has("hunger")) {
                character.setHunger(jsonObject.get("hunger").getAsFloat());
            }
            if (jsonObject.has("thirsty")) {
                character.setThirsty(jsonObject.get("thirsty").getAsFloat());
            }
            if (jsonObject.has("energy")) {
                character.setEnergy(jsonObject.get("energy").getAsFloat());
            }
            if (jsonObject.has("sanity")) {
                character.setSanity(jsonObject.get("sanity").getAsFloat());
            }

            if (jsonObject.has("attackDamage")) {
                character.setAttackDamage(jsonObject.get("attackDamage").getAsDouble());
            }

            if (jsonObject.has("maxInventoryCapacity")) {
                character.setMaxInventoryCapacity(jsonObject.get("maxInventoryCapacity").getAsInt());
            }
            if (jsonObject.has("inventoryInitialCapacity")) {
                character.setInventoryInitialCapacity(jsonObject.get("inventoryInitialCapacity").getAsInt());
            }

            Array<Item> inventory = new Array<>();
            if (jsonObject.has("inventory") && !jsonObject.get("inventory").isJsonNull()) {
                JsonArray inventoryArray = jsonObject.getAsJsonArray("inventory");
                for (int i = 0; i < inventoryArray.size(); i++) {
                    JsonElement itemElement = inventoryArray.get(i);
                    if (itemElement != null && !itemElement.isJsonNull()) {
                        Item item = context.deserialize(itemElement, Item.class);
                        if (item != null) {
                            inventory.add(item);
                        }
                    }
                }
            }
            character.setInventory(inventory);

            return character;
        } catch (Exception e) {
            logger.error("Error deserializing Character: {}", e.getMessage(), e);
            // Create a fallback character with safe defaults
            return new Survivor("Error Character", 100.0f, 100.0f);
        }
    }
}
