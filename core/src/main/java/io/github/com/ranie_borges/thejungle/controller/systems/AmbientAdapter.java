package io.github.com.ranie_borges.thejungle.controller.systems;

import com.google.gson.*;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Custom adapter for serializing and deserializing Ambient objects
 */
public class AmbientAdapter implements JsonSerializer<Ambient>, JsonDeserializer<Ambient> {
    private static final Logger logger = LoggerFactory.getLogger(AmbientAdapter.class);

    @Override
    public JsonElement serialize(Ambient src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null)
            return JsonNull.INSTANCE;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getClass().getSimpleName());
        jsonObject.addProperty("name", src.getName());
        jsonObject.add("climes", context.serialize(src.getClimes()));
        jsonObject.add("resources", context.serialize(src.getResources()));
        jsonObject.addProperty("difficulty", src.getDifficulty());
        jsonObject.addProperty("darknessFactor", src.getDarknessFactor());

        return jsonObject;
    }

    @Override
    public Ambient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull())
            return null;

        try {
            JsonObject jsonObject = json.getAsJsonObject();

            String ambientType = jsonObject.has("type") ? jsonObject.get("type").getAsString() : "Jungle"; // Default


            Ambient ambient;
            switch (ambientType) {
                case "Cave":
                    ambient = new Cave();
                    break;
                case "Jungle":
                    ambient = new Jungle();
                    break;
                case "LakeRiver":
                    ambient = new LakeRiver();
                    break;
                case "Mountain":
                    ambient = new Mountain();
                    break;
                case "Ruins":
                    ambient = new Ruins();
                    break;
                default:
                    logger.warn("Unknown ambient type: {}, defaulting to Jungle", ambientType);
                    ambient = new Jungle();
            }

            return ambient;
        } catch (Exception e) {
            logger.error("Error deserializing Ambient: {}", e.getMessage());
            return new Jungle(); // Default to Jungle on error
        }
    }
}
