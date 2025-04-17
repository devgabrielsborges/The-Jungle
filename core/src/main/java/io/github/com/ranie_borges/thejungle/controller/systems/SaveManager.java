package io.github.com.ranie_borges.thejungle.controller.systems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
            this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
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
            Files.createDirectories(Paths.get(SAVE_DIRECTORY));
            logger.info("Save directory created at: {}", SAVE_DIRECTORY);
        } catch (IOException e) {
            logger.error("Failed to create save directory: {}", e.getMessage(), e);
            throw new SaveManagerException("Failed to create save directory", e);
        }
    }

    public boolean saveGame(GameState gameState, String saveName) {
        try {
            if (gameState == null) {
                logger.error("Cannot save game: game state is null");
                throw new SaveManagerException("Failed to save game", e);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = saveName.isEmpty() ?
                "save_" + timestamp + ".json" :
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
            if (filename == null || filename.isEmpty()) {
                logger.error("Cannot load game: filename is null or empty");
                return null;
            }

            File file = new File(SAVE_DIRECTORY + filename);
            if (!file.exists() || !file.isFile()) {
                logger.error("Save file does not exist: {}", filename);
                return null;
            }

            try (Reader reader = new FileReader(file)) {
                GameState state = gson.fromJson(reader, GameState.class);
                logger.info("Game loaded successfully from: {}", filename);
                return state;
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
                logger.warn("Save directory does not exist: {}", SAVE_DIRECTORY);
                return new String[0];
            }

            String[] files = saveDir.list((dir, name) -> name.endsWith(".json"));
            if (files == null) {
                files = new String[0];
            }

            logger.debug("Found {} save files", files.length);
            return files;
        } catch (Exception e) {
            logger.error("Failed to get save files: {}", e.getMessage());
            return new String[0];
        }
    }
}
