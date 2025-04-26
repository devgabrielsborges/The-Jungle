package io.github.com.ranie_borges.thejungle.controller.systems;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.com.ranie_borges.thejungle.controller.exceptions.save.SaveManagerException;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Doctor;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Hunter;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Lumberjack;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Survivor;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.model.world.ambients.LakeRiver;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Mountain;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Ruins;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Cave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SaveManager {
    private static final Logger logger = LoggerFactory.getLogger(SaveManager.class);
    private static final String SAVE_DIRECTORY = "saves/";
    private static final String JSON = ".json";
    private final Gson gson;

    public SaveManager() {
        try {
            // Create a custom TypeAdapter for OffsetDateTime
            TypeAdapter<OffsetDateTime> offsetDateTimeAdapter = new TypeAdapter<OffsetDateTime>() {
                @Override
                public void write(JsonWriter out, OffsetDateTime value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.toString());
                    }
                }

                @Override
                public OffsetDateTime read(JsonReader in) throws IOException {
                    try {
                        String dateStr = in.nextString();
                        return dateStr == null ? null : OffsetDateTime.parse(dateStr);
                    } catch (DateTimeParseException e) {
                        logger.error("Failed to parse OffsetDateTime: {}", e.getMessage());
                        return OffsetDateTime.now();
                    }
                }
            };

            // Configure Gson with runtime type information
            this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(OffsetDateTime.class, offsetDateTimeAdapter)
                .registerTypeHierarchyAdapter(Character.class, new CharacterAdapter())
                .serializeSpecialFloatingPointValues()
                .create();

            // Create save directory if it doesn't exist
            createSaveDirectory();
        } catch (Exception e) {
            logger.error("Failed to initialize SaveManager: {}", e.getMessage());
            throw new SaveManagerException("Failed to initialize SaveManager", e);
        }
    }

    private void createSaveDirectory() {
        try {
            Path path = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Created save directory at: {}", SAVE_DIRECTORY);
            }
        } catch (IOException e) {
            logger.error("Failed to create save directory: {}", e.getMessage());
            throw new SaveManagerException("Failed to create save directory", e);
        }
    }


    public boolean saveGame(GameState gameState, String saveName) {
        if (gameState == null) {
            logger.error("Cannot save game: game state is null");
            return false;
        }

        if (saveName == null || saveName.trim().isEmpty()) {
            logger.warn("Empty save name provided, using timestamp instead");
            saveName = "save_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        }

        String filename = saveName + (saveName.endsWith(JSON) ? "" : JSON);
        Writer writer = null;

        try {
            File saveDir = new File(SAVE_DIRECTORY);
            if (!saveDir.exists() && !saveDir.mkdirs()) {
                logger.error("Failed to create save directory: {}", SAVE_DIRECTORY);
                return false;
            }

            File saveFile = new File(SAVE_DIRECTORY + filename);
            writer = new FileWriter(saveFile);

            Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

            gson.toJson(gameState, writer);

            logger.info("Game saved successfully to: {}", saveFile.getAbsolutePath());
            return true;
        } catch (JsonIOException e) {
            logger.error("JSON error while saving game: {}", e.getMessage());
            return false;
        } catch (IOException e) {
            logger.error("I/O error while saving game: {}", e.getMessage());
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("Error closing writer: {}", e.getMessage());
                }
            }
        }
    }

    public GameState loadGame(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            logger.error("Cannot load game: filename is null or empty");
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        String filePath = SAVE_DIRECTORY + (filename.endsWith(JSON) ? filename : filename + JSON);
        Reader reader = null;

        try {
            File saveFile = new File(filePath);
            if (!saveFile.exists()) {
                logger.error("Save file does not exist: {}", filePath);
                throw new FileNotFoundException("Save file not found: " + filePath);
            }

            reader = new FileReader(saveFile);
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            // Create game state from JSON
            GameState gameState = new GameState();

            // Load character data
            if (jsonObject.has("character")) {
                JsonObject characterObj = jsonObject.getAsJsonObject("character");
                String name = characterObj.has("name") ? characterObj.get("name").getAsString() : "Unknown";
                String characterType = characterObj.has("characterType") ?
                    characterObj.get("characterType").getAsString() : "Survivor";

                // Get position from saved data or default
                float xPos = 0f, yPos = 0f;
                if (characterObj.has("position")) {
                    JsonObject posObj = characterObj.getAsJsonObject("position");
                    xPos = posObj.has("x") ? posObj.get("x").getAsFloat() : 0f;
                    yPos = posObj.has("y") ? posObj.get("y").getAsFloat() : 0f;
                }

                // Restore character based on type and position
                Character character = null;
                switch (characterType) {
                    case "Doctor":
                        character = new Doctor(name, xPos, yPos);
                        break;
                    case "Hunter":
                        character = new Hunter(name, xPos, yPos);
                        break;
                    case "Lumberjack":
                        character = new Lumberjack(name, xPos, yPos);
                        break;
                    case "Survivor":
                    default:
                        character = new Survivor(name, xPos, yPos);
                        break;
                }

                // Restore character stats
                if (characterObj.has("life")) character.setLife(characterObj.get("life").getAsFloat());
                if (characterObj.has("hunger")) character.setHunger(characterObj.get("hunger").getAsFloat());
                if (characterObj.has("thirsty")) character.setThirsty(characterObj.get("thirsty").getAsFloat());
                if (characterObj.has("energy")) character.setEnergy(characterObj.get("energy").getAsFloat());
                if (characterObj.has("sanity")) character.setSanity(characterObj.get("sanity").getAsFloat());

                gameState.setCharacter(character);
            }

            // Load ambient data
            if (jsonObject.has("ambientName")) {
                String ambientName = jsonObject.get("ambientName").getAsString();
                Ambient ambient;

                switch (ambientName) {
                    case "Jungle": ambient = new Jungle(); break;
                    case "Cave": ambient = new Cave(); break;
                    case "Lake River": ambient = new LakeRiver(); break;
                    case "Mountain": ambient = new Mountain(); break;
                    case "Ruins": ambient = new Ruins(); break;
                    default: ambient = new Jungle(); break;
                }

                gameState.setCurrentAmbient(ambient);
            }

            // Load map data
            if (jsonObject.has("currentMap")) {
                JsonArray mapArray = jsonObject.getAsJsonArray("currentMap");
                int height = mapArray.size();
                int width = height > 0 ? mapArray.get(0).getAsJsonArray().size() : 0;

                int[][] map = new int[height][width];
                for (int y = 0; y < height; y++) {
                    JsonArray row = mapArray.get(y).getAsJsonArray();
                    for (int x = 0; x < width; x++) {
                        map[y][x] = row.get(x).getAsInt();
                    }
                }

                gameState.setCurrentMap(map);
            }

            // Load other game state data
            if (jsonObject.has("daysSurvived"))
                gameState.setDaysSurvived(jsonObject.get("daysSurvived").getAsInt());

            logger.info("Game loaded successfully from: {}", filePath);
            return gameState;

        } catch (JsonSyntaxException e) {
            logger.error("JSON syntax error while loading game: {}", e.getMessage());
            throw new RuntimeException("Error parsing save file: " + e.getMessage(), e);
        } catch (JsonIOException e) {
            logger.error("JSON I/O error while loading game: {}", e.getMessage());
            throw new RuntimeException("Error reading save file: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("I/O error while loading game: {}", e.getMessage());
            throw new RuntimeException("Error accessing save file: " + e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("Error closing reader: {}", e.getMessage());
                }
            }
        }
    }

    public boolean deleteSave(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            logger.error("Cannot delete save: filename is null or empty");
            return false;
        }

        String filePath = SAVE_DIRECTORY + (filename.endsWith(JSON) ? filename : filename + JSON);
        try {
            File saveFile = new File(filePath);
            if (!saveFile.exists()) {
                logger.warn("Save file does not exist: {}", filePath);
                return false;
            }

            boolean deleted = saveFile.delete();
            if (deleted) {
                logger.info("Save file deleted: {}", filePath);
            } else {
                logger.error("Failed to delete save file: {}", filePath);
            }

            return deleted;
        } catch (SecurityException e) {
            logger.error("Security exception deleting save file: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Custom adapter to handle Character subclasses properly
     */
    private static class CharacterAdapter extends TypeAdapter<Character> {
        private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

        @Override
        public void write(JsonWriter out, Character character) throws IOException {
            if (character == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("type").value(character.getCharacterType());
            out.name("data").value(gson.toJson(character));
            out.endObject();
        }

        @Override
        public Character read(JsonReader in) throws IOException {
            in.beginObject();
            String type = null;
            String data = null;

            while (in.hasNext()) {
                String name = in.nextName();
                if (name.equals("type")) {
                    type = in.nextString();
                } else if (name.equals("data")) {
                    data = in.nextString();
                } else {
                    in.skipValue();
                }
            }

            in.endObject();

            if (type == null || data == null) {
                return null;
            }

            try {
                switch (type) {
                    case "Survivor":
                        return gson.fromJson(data, Survivor.class);
                    case "Hunter":
                        return gson.fromJson(data, Hunter.class);
                    case "Lumberjack":
                        return gson.fromJson(data, Lumberjack.class);
                    case "Doctor":
                        return gson.fromJson(data, Doctor.class);
                    default:
                        logger.error("Unknown character type: {}", type);
                        return null;
                }
            } catch (JsonSyntaxException e) {
                logger.error("Failed to deserialize character of type {}: {}", type, e.getMessage());
                return null;
            }
        }
    }

    /**
     * Returns an array of save files in the save directory
     * @return Array of save files or empty array if directory doesn't exist
     */
    public File[] getSaveFiles() {
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists() || !directory.isDirectory()) {
            createSaveDirectory();
            return new File[0];
        }
        return directory.listFiles((dir, name) -> name.toLowerCase().endsWith(JSON));
    }
}
