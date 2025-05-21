package io.github.com.ranie_borges.thejungle.model.stats;

import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.controller.EventController;
import io.github.com.ranie_borges.thejungle.controller.AmbientController;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the current state of the game that can be saved and loaded
 */
public class GameState {
    @Expose
    private Character playerCharacter;

    @Expose
    private Ambient currentAmbient;

    @Expose
    private int[][] currentMap;

    // Mapping from ambient name to ambient data (map, visit count, resources) for
    // visited ambients
    @Expose
    private Map<String, AmbientData> visitedAmbients;

    @Expose
    private List<Event> activeEvents;

    @Expose
    private int daysSurvived;

    @Expose
    private OffsetDateTime offsetDateTime;

    private EventController eventController;
    private AmbientController ambientController;

    @Expose
    private int mapWidth;

    @Expose
    private int mapHeight;

    public GameState() {
        this.activeEvents = new ArrayList<>();
        this.offsetDateTime = OffsetDateTime.now();
        this.eventController = new EventController(this);
        this.visitedAmbients = new HashMap<>();
    }

    public Character getPlayerCharacter() {
        return playerCharacter;
    }

    public void setPlayerCharacter(Character playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public Ambient getCurrentAmbient() {
        return currentAmbient;
    }

    public void setCurrentAmbient(Ambient currentAmbient) {
        this.currentAmbient = currentAmbient;
    }

    public List<Event> getActiveEvents() {
        return activeEvents;
    }

    public void setActiveEvents(List<Event> activeEvents) {
        this.activeEvents = activeEvents;
    }

    public int getDaysSurvived() {
        return daysSurvived;
    }

    public void setDaysSurvived(int daysSurvived) {
        this.daysSurvived = daysSurvived;
    }

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
    }

    public EventController getEventController() {
        return eventController;
    }

    public void setEventController(EventController eventController) {
        this.eventController = eventController;
    }

    public AmbientController getAmbientController() {
        return ambientController;
    }

    public void setAmbientController(AmbientController ambientController) {
        this.ambientController = ambientController;
    }

    public int[][] getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(int[][] currentMap) {
        this.currentMap = currentMap;
    }

    public Map<String, AmbientData> getVisitedAmbients() {
        return visitedAmbients;
    }

    public void setVisitedAmbients(Map<String, AmbientData> visitedAmbients) {
        this.visitedAmbients = visitedAmbients;
    }

    /**
     * Add a map to the visited ambients collection
     *
     * @param ambientName The name of the ambient
     * @param map         The map data
     */
    public void addVisitedMap(String ambientName, int[][] map) {
        if (ambientName == null || map == null) {
            return;
        }

        // First check if this ambient already exists
        if (visitedAmbients.containsKey(ambientName)) {
            AmbientData data = visitedAmbients.get(ambientName);
            data.incrementVisitCount();
            // Only update the map if necessary
            if (data.getMap() == null) {
                data.setMap(map);
            }
        } else {
            // Create a new ambient data entry
            AmbientData data = new AmbientData(map);
            visitedAmbients.put(ambientName, data);
        }
    }

    /**
     * Increment the visit count for a specific ambient
     *
     * @param ambientName The ambient name to increment
     */
    public void incrementAmbientVisitCount(String ambientName) {
        if (ambientName != null && visitedAmbients.containsKey(ambientName)) {
            visitedAmbients.get(ambientName).incrementVisitCount();
        }
    }

    /**
     * Set resources for a specific ambient
     *
     * @param ambientName The ambient name
     * @param resources   The resources to set
     */
    public void setAmbientResources(String ambientName, List<Item> resources) {
        if (ambientName == null || resources == null) {
            return;
        }

        if (!visitedAmbients.containsKey(ambientName)) {
            // If the ambient doesn't exist yet, create it with null map
            visitedAmbients.put(ambientName, new AmbientData(null));
        }

        visitedAmbients.get(ambientName).setRemainingResources(resources);
    }

    /**
     * Get map data for a specific ambient
     *
     * @param ambientName The ambient name
     * @return The map data or null if not found
     */
    public int[][] getVisitedMap(String ambientName) {
        if (ambientName != null && visitedAmbients.containsKey(ambientName)) {
            return visitedAmbients.get(ambientName).getMap();
        }
        return null;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public void setCharacter(Character character) {
        this.playerCharacter = character;
    }
}
