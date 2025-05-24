package io.github.com.ranie_borges.thejungle.controller.managers;

import com.google.gson.*;
import io.github.com.ranie_borges.thejungle.controller.exceptions.save.SaveManagerException;
import io.github.com.ranie_borges.thejungle.controller.adapters.AmbientAdapter;
import io.github.com.ranie_borges.thejungle.controller.adapters.CharacterAdapter;
import io.github.com.ranie_borges.thejungle.controller.adapters.ItemAdapter;
import io.github.com.ranie_borges.thejungle.controller.adapters.OffsetDateTimeAdapter;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.stats.AmbientData;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;

public class SaveManager {
    private static final Logger logger = LoggerFactory.getLogger(SaveManager.class);
    private static final String SAVE_DIRECTORY = "saves/";
    private static final String JSON_EXTENSION = ".json";

    public SaveManager() {
        try {
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

        String effectiveSaveName = saveName;
        if (effectiveSaveName == null || effectiveSaveName.trim().isEmpty()) {
            logger.warn("Empty save name provided, using default 'autosave'");
            effectiveSaveName = "autosave";
        }
        // Ensure .json extension
        if (!effectiveSaveName.toLowerCase().endsWith(JSON_EXTENSION)) {
            effectiveSaveName += JSON_EXTENSION;
        }

        File saveFile = new File(SAVE_DIRECTORY + effectiveSaveName);
        try (Writer writer = new FileWriter(saveFile)) {
            // Consolidate map data into visitedAmbients before saving
            if (gameState.getCurrentAmbient() != null && gameState.getCurrentMap() != null) {
                String ambientName = gameState.getCurrentAmbient().getName();
                AmbientData currentAmbientData = gameState.getVisitedAmbients().computeIfAbsent(ambientName, k -> new AmbientData());
                currentAmbientData.setMap(gameState.getCurrentMap());
                // Resources and visit count should be managed by MapManager/GameState updates
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
            logger.error("Error saving game to {}: {}", effectiveSaveName, e.getMessage(), e);
            return false;
        }
    }

    public GameState loadGame(String filePathOrName) {
        if (filePathOrName == null || filePathOrName.trim().isEmpty()) {
            logger.error("Cannot load game: filename is null or empty");
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        File saveFile;
        if (filePathOrName.contains(File.separator) || filePathOrName.startsWith(SAVE_DIRECTORY)) { // Likely a full or relative path
            saveFile = new File(filePathOrName);
        } else { // Just a name, assume it's in SAVE_DIRECTORY
            String fileName = filePathOrName.toLowerCase().endsWith(JSON_EXTENSION) ? filePathOrName : filePathOrName + JSON_EXTENSION;
            saveFile = new File(SAVE_DIRECTORY + fileName);
        }


        if (!saveFile.exists()) {
            logger.error("Save file does not exist: {}", saveFile.getAbsolutePath());
            return null;
        }

        try (Reader reader = new FileReader(saveFile)) {
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                .registerTypeAdapter(Item.class, new ItemAdapter())
                .registerTypeAdapter(Character.class, new CharacterAdapter())
                .registerTypeAdapter(Ambient.class, new AmbientAdapter())
                .create();

            GameState gameState = gson.fromJson(reader, GameState.class);

            // Post-load initializations or validations if needed
            if (gameState != null) {
                if (gameState.getPlayerCharacter() == null) {
                    logger.warn("Loaded GameState has no player character. Load might be corrupted or incomplete.");
                    // Optionally create a default character or handle error
                }
                if (gameState.getCurrentAmbient() == null && gameState.getVisitedAmbients() != null && !gameState.getVisitedAmbients().isEmpty()) {
                    // Attempt to set currentAmbient if null but visitedAmbients has data (e.g. from older save structure)
                    // This is a fallback, ideally currentAmbient should be saved directly.
                    logger.warn("currentAmbient is null in loaded GameState. Attempting to infer from visitedAmbients.");
                    // This logic might need to be more sophisticated, e.g. finding the most recently visited
                } else if (gameState.getCurrentAmbient() == null) {
                    logger.warn("currentAmbient is null in loaded GameState and no visitedAmbients to infer from. Will default in AmbientController.");
                }


                logger.info("Game loaded successfully from: {}", saveFile.getAbsolutePath());
            } else {
                logger.error("Failed to deserialize GameState from: {}. Gson returned null.", saveFile.getAbsolutePath());
            }
            return gameState;

        } catch (JsonSyntaxException e) {
            logger.error("JSON syntax error while loading game from {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        } catch (JsonIOException e) {
            logger.error("JSON I/O error while loading game from {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        } catch (IOException e) {
            logger.error("I/O error while loading game from {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        }
        return null; // Return null if any error occurs during loading
    }

    public boolean deleteSave(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            logger.error("Cannot delete save: filename is null or empty");
            return false;
        }

        String effectiveFilename = filename;
        if (!effectiveFilename.toLowerCase().endsWith(JSON_EXTENSION)) {
            effectiveFilename += JSON_EXTENSION;
        }

        File saveFile = new File(SAVE_DIRECTORY + effectiveFilename);

        try {
            if (!saveFile.exists()) {
                logger.warn("Save file to delete does not exist: {}", saveFile.getAbsolutePath());
                return false; // Or true, if "not existing" means "successfully deleted" in context
            }

            boolean deleted = Files.deleteIfExists(saveFile.toPath());
            if (deleted) {
                logger.info("Save file deleted: {}", saveFile.getAbsolutePath());
            } else {
                // This might happen if the file is locked or due to permissions,
                // or if it was deleted by another process between exists() and delete()
                logger.error("Failed to delete save file (Files.deleteIfExists returned false): {}", saveFile.getAbsolutePath());
            }
            return deleted;
        } catch (SecurityException e) {
            logger.error("Security exception deleting save file {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        } catch (IOException e) {
            logger.error("IOException deleting save file {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        }
        return false;
    }

    public File[] getSaveFiles() {
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists() || !directory.isDirectory()) {
            createSaveDirectory(); // Attempt to create if missing
            return new File[0];
        }
        // Filter for .json files
        return directory.listFiles((dir, name) -> name.toLowerCase().endsWith(JSON_EXTENSION));
    }
}
