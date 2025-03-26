package io.github.com.ranie_borges.thejungle.controller.systems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();

        // Create save directory if it doesn't exist
        createSaveDirectory();
    }

    private void createSaveDirectory() {
        try {
            Files.createDirectories(Paths.get(SAVE_DIRECTORY));
            logger.info("Save directory created at: {}", SAVE_DIRECTORY);
        } catch (IOException e) {
            logger.error("Failed to create save directory: {}", e.getMessage(), e);
        }
    }

    public boolean saveGame(GameState gameState, String saveName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = saveName.isEmpty() ?
            "save_" + timestamp + ".json" :
            saveName + ".json";

        try (Writer writer = new FileWriter(SAVE_DIRECTORY + filename)) {
            gson.toJson(gameState, writer);
            logger.info("Game saved successfully to: {}", filename);
            return true;
        } catch (IOException e) {
            logger.error("Failed to save game: {}", e.getMessage(), e);
            return false;
        }
    }

    public GameState loadGame(String filename) {
        try (Reader reader = new FileReader(SAVE_DIRECTORY + filename)) {
            GameState state = gson.fromJson(reader, GameState.class);
            logger.info("Game loaded successfully from: {}", filename);
            return state;
        } catch (IOException e) {
            logger.error("Failed to load game: {}", e.getMessage(), e);
            return null;
        }
    }

    public String[] getSaveFiles() {
        File saveDir = new File(SAVE_DIRECTORY);
        String[] files = saveDir.list((dir, name) -> name.endsWith(".json"));
        logger.debug("Found {} save files", files != null ? files.length : 0);
        return files;
    }
}
