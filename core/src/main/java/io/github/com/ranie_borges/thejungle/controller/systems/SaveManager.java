package io.github.com.ranie_borges.thejungle.controller.systems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.com.ranie_borges.thejungle.controller.exceptions.save.SaveManagerException;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class SaveManager {
    private static final Logger logger = LoggerFactory.getLogger(SaveManager.class);
    private static final String SAVE_DIRECTORY = "saves/";
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
                    String dateStr = in.nextString();
                    return dateStr == null ? null : OffsetDateTime.parse(dateStr);
                }
            };

            this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(OffsetDateTime.class, offsetDateTimeAdapter)
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
            if (!Files.exists(Paths.get(SAVE_DIRECTORY))) {
                Files.createDirectories(Paths.get(SAVE_DIRECTORY));
                logger.info("Created save directory at: {}", SAVE_DIRECTORY);
            }
        } catch (IOException e) {
            logger.error("Failed to create save directory: {}", e.getMessage());
            throw new SaveManagerException("Failed to create save directory", e);
        }
    }

    public boolean saveGame(GameState gameState, String saveName) {
        try {
            if (gameState == null) {
                logger.error("Cannot save game: game state is null");
                return false;
            }

            String filename = saveName.isEmpty() ?
                "save_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json" :
                saveName + ".json";

            try (Writer writer = new FileWriter(SAVE_DIRECTORY + filename)) {
                gson.toJson(gameState, writer);
                logger.info("Game saved successfully to: {}", filename);
                return true;
            } catch (IOException e) {
                logger.error("Failed to save game: {}", e.getMessage());
                return false;
            }
        } catch (Exception e) {
            logger.error("Unexpected error saving game: {}", e.getMessage());
            return false;
        }
    }

    public GameState loadGame(String filename) {
        try {
            String filePath = SAVE_DIRECTORY + filename;
            if (!Files.exists(Paths.get(filePath))) {
                logger.error("Save file not found: {}", filePath);
                return null;
            }

            try (Reader reader = new FileReader(filePath)) {
                GameState gameState = gson.fromJson(reader, GameState.class);
                logger.info("Game loaded successfully from: {}", filename);
                return gameState;
            } catch (IOException e) {
                logger.error("Failed to load game: {}", e.getMessage());
                return null;
            }
        } catch (Exception e) {
            logger.error("Unexpected error loading game: {}", e.getMessage());
            return null;
        }
    }

    public String[] getSaveFiles() {
        try {
            File saveDir = new File(SAVE_DIRECTORY);
            if (!saveDir.exists() || !saveDir.isDirectory()) {
                logger.warn("Save directory does not exist or is not a directory");
                return new String[0];
            }

            return saveDir.list((dir, name) -> name.toLowerCase().endsWith(".json"));
        } catch (Exception e) {
            logger.error("Failed to get save files: {}", e.getMessage());
            return new String[0];
        }
    }

    public boolean deleteSave(String filename) {
        try {
            String filePath = SAVE_DIRECTORY + filename;
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
        } catch (Exception e) {
            logger.error("Unexpected error deleting save file: {}", e.getMessage());
            return false;
        }
    }
}
