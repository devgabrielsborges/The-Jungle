package io.github.com.ranie_borges.thejungle.controller.managers;

import com.google.gson.*;
import io.github.com.ranie_borges.thejungle.controller.exceptions.save.SaveManagerException;
import io.github.com.ranie_borges.thejungle.controller.adapters.AmbientAdapter;
import io.github.com.ranie_borges.thejungle.controller.adapters.CharacterAdapter;
import io.github.com.ranie_borges.thejungle.controller.adapters.ItemAdapter;
import io.github.com.ranie_borges.thejungle.controller.adapters.OffsetDateTimeAdapter;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Survivor;
import io.github.com.ranie_borges.thejungle.model.stats.AmbientData;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveManager {
    private static final Logger logger = LoggerFactory.getLogger(SaveManager.class);
    private static final String SAVE_DIRECTORY = "saves/";
    private static final String JSON = ".json";

    public SaveManager() {
        try {
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

            if (gameState.getCurrentAmbient() != null) {
                String ambientName = gameState.getCurrentAmbient().getName();
                logger.debug("Saving current ambient: {}", ambientName);

                if (gameState.getCurrentMap() == null) {
                    logger.warn(
                            "Current map is null, but we can't get it from the ambient because getMap() doesn't exist");

                    if (gameState.getVisitedAmbients() != null &&
                            gameState.getVisitedAmbients().containsKey(ambientName)) {
                        AmbientData ambientData = gameState.getVisitedAmbients().get(ambientName);
                        if (ambientData != null && ambientData.getMap() != null) {
                            logger.debug("Found map in visitedAmbients, using it as current map");
                            gameState.setCurrentMap(ambientData.getMap());
                        }
                    }
                }

                if (gameState.getCurrentMap() != null) {
                    logger.debug("Saving current map with dimensions {}x{}",
                            gameState.getCurrentMap().length,
                            gameState.getCurrentMap()[0].length);

                    // Create or update the ambient data in visitedAmbients
                    if (!gameState.getVisitedAmbients().containsKey(ambientName)) {
                        AmbientData ambientData = new AmbientData(gameState.getCurrentMap());

                        // Copy resources
                        if (gameState.getCurrentAmbient().getResources() != null) {
                            ambientData.setRemainingResources(
                                    new ArrayList<>(gameState.getCurrentAmbient().getResources()));
                        }

                        gameState.getVisitedAmbients().put(ambientName, ambientData);
                    } else {
                        // Update existing ambient data
                        AmbientData ambientData = gameState.getVisitedAmbients().get(ambientName);
                        ambientData.setMap(gameState.getCurrentMap());
                        ambientData.incrementVisitCount();

                        // Update resources
                        if (gameState.getCurrentAmbient().getResources() != null) {
                            ambientData.setRemainingResources(
                                    new ArrayList<>(gameState.getCurrentAmbient().getResources()));
                        }
                    }
                }
            }

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                    .registerTypeAdapter(Item.class, new ItemAdapter())
                    .registerTypeAdapter(Character.class, new CharacterAdapter())
                    .registerTypeAdapter(Ambient.class, new AmbientAdapter())
                    .create();

            gson.toJson(gameState, writer);
            logger.info("Game saved successfully to: {}", saveFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            logger.error("Error saving game: {}", e.getMessage());
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

        String filePath = filename.endsWith(JSON) ? filename : filename + JSON;
        Reader reader = null;

        try {
            File saveFile = new File(filePath);
            if (!saveFile.exists()) {
                logger.error("Save file does not exist: {}", filePath);
                throw new FileNotFoundException("Save file not found: " + filePath);
            }

            reader = new FileReader(saveFile);
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            GameState gameState = new GameState();

            if (jsonObject.has("playerCharacter")) {
                try {
                    JsonObject characterObj = jsonObject.getAsJsonObject("playerCharacter");

                    Character character = new GsonBuilder()
                            .registerTypeAdapter(Character.class, new CharacterAdapter())
                            .registerTypeAdapter(Item.class, new ItemAdapter())
                            .create()
                            .fromJson(characterObj, Character.class);

                    if (character != null) {
                        gameState.setPlayerCharacter(character);
                        logger.debug("Restored character: {}, type: {}",
                                character.getName(), character.getClass().getSimpleName());
                    } else {
                        // Create default character if deserialization failed
                        gameState.setPlayerCharacter(new Survivor("Default", 0, 0));
                        logger.warn("Failed to deserialize character, using default Survivor");
                    }
                } catch (Exception e) {
                    logger.error("Error deserializing character: {}", e.getMessage());
                    gameState.setPlayerCharacter(new Survivor("Error", 0, 0));
                }
            }

            if (jsonObject.has("currentAmbient")) {
                try {
                    JsonObject ambientObj = jsonObject.getAsJsonObject("currentAmbient");

                    Ambient ambient = new GsonBuilder()
                            .registerTypeAdapter(Ambient.class, new AmbientAdapter())
                            .create()
                            .fromJson(ambientObj, Ambient.class);

                    if (ambient != null) {
                        gameState.setCurrentAmbient(ambient);
                        logger.debug("Restored current ambient: {}", ambient.getName());
                    } else {
                        // Fallback to Jungle if deserialization failed
                        gameState.setCurrentAmbient(new Jungle());
                        logger.warn("Failed to deserialize ambient, using default Jungle");
                    }
                } catch (Exception e) {
                    logger.error("Error deserializing ambient: {}", e.getMessage());
                    gameState.setCurrentAmbient(new Jungle());
                }
            }

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
                gameState.setMapWidth(width);
                gameState.setMapHeight(height);
            }

            // Load visited
            if (jsonObject.has("visitedAmbients")) {
                try {
                    Map<String, AmbientData> visitedAmbients = new HashMap<>();
                    JsonObject visitedAmbientsObj = jsonObject.getAsJsonObject("visitedAmbients");

                    for (Map.Entry<String, JsonElement> entry : visitedAmbientsObj.entrySet()) {
                        String ambientName = entry.getKey();
                        JsonObject ambientDataObj = entry.getValue().getAsJsonObject();

                        JsonArray mapArray = ambientDataObj.has("map") ? ambientDataObj.getAsJsonArray("map")
                                : new JsonArray();

                        int height = mapArray.size();
                        int width = height > 0 ? mapArray.get(0).getAsJsonArray().size() : 0;

                        int[][] map = new int[height][width];
                        for (int y = 0; y < height; y++) {
                            JsonArray row = mapArray.get(y).getAsJsonArray();
                            for (int x = 0; x < width; x++) {
                                map[y][x] = row.get(x).getAsInt();
                            }
                        }

                        AmbientData ambientData = new AmbientData(map);

                        if (ambientDataObj.has("visitCount")) {
                            for (int i = 1; i < ambientDataObj.get("visitCount").getAsInt(); i++) {
                                ambientData.incrementVisitCount();
                            }
                        }

                        // Set resources if available
                        if (ambientDataObj.has("remainingResources")) {
                            JsonArray resourcesArray = ambientDataObj.getAsJsonArray("remainingResources");
                            List<Item> resources = new ArrayList<>();

                            for (int i = 0; i < resourcesArray.size(); i++) {
                                JsonElement itemElement = resourcesArray.get(i);
                                if (!itemElement.isJsonNull()) {
                                    try {
                                        Item item = new GsonBuilder()
                                                .registerTypeAdapter(Item.class, new ItemAdapter())
                                                .create()
                                                .fromJson(itemElement, Item.class);

                                        if (item != null) {
                                            resources.add(item);
                                        }
                                    } catch (Exception e) {
                                        logger.error("Failed to deserialize resource item: {}", e.getMessage());
                                    }
                                }
                            }

                            ambientData.setRemainingResources(resources);
                        }

                        visitedAmbients.put(ambientName, ambientData);
                    }

                    gameState.setVisitedAmbients(visitedAmbients);
                    logger.debug("Loaded {} visited ambients", visitedAmbients.size());
                } catch (Exception e) {
                    logger.error("Error loading visited ambients: {}", e.getMessage());
                    gameState.setVisitedAmbients(new HashMap<>());
                }
            }
            // For backward compatibility with older saves
            else if (jsonObject.has("visitedMaps")) {
                JsonObject visitedMapsObj = jsonObject.getAsJsonObject("visitedMaps");
                Map<String, AmbientData> visitedAmbients = new HashMap<>();

                for (Map.Entry<String, JsonElement> entry : visitedMapsObj.entrySet()) {
                    String ambientName = entry.getKey();
                    JsonArray mapArray = entry.getValue().getAsJsonArray();
                    int height = mapArray.size();
                    int width = height > 0 ? mapArray.get(0).getAsJsonArray().size() : 0;

                    int[][] map = new int[height][width];
                    for (int y = 0; y < height; y++) {
                        JsonArray row = mapArray.get(y).getAsJsonArray();
                        for (int x = 0; x < width; x++) {
                            map[y][x] = row.get(x).getAsInt();
                        }
                    }

                    visitedAmbients.put(ambientName, new AmbientData(map));
                }

                gameState.setVisitedAmbients(visitedAmbients);
                logger.info("Loaded {} visited maps from older save format", visitedAmbients.size());
            }

            // Load other game state data
            if (jsonObject.has("daysSurvived"))
                gameState.setDaysSurvived(jsonObject.get("daysSurvived").getAsInt());

            if (jsonObject.has("offsetDateTime")) {
                String dateStr = jsonObject.get("offsetDateTime").getAsString();
                try {
                    OffsetDateTime dateTime = OffsetDateTime.parse(dateStr);
                    gameState.setOffsetDateTime(dateTime);
                } catch (DateTimeParseException e) {
                    logger.error("Error parsing date: {}", e.getMessage());
                    gameState.setOffsetDateTime(OffsetDateTime.now());
                }
            }

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
     * Returns an array of save files in the save directory
     *
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
