package io.github.com.ranie_borges.thejungle.model.world;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.world.interfaces.IAmbients;

import java.util.*;

public abstract class Ambient implements IAmbients {
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private float difficult;

    @Expose
    private Set<AmbientAttribute> attributes;
    @Expose
    private Set<Item> resources;
    @Expose
    private Map<Event, Double> possibleEvents;
    @Expose
    private Set<Clime> climes;
    private transient final Texture floorTexture;
    private transient final Texture wallTexture;
    private transient final Texture sidebarTexture;
    @Expose
    private final float wallDensity;
    @Expose
    private final float itemDensity;

    @Expose
    private int timesVisited = 0;

    @Expose
    private float darknessFactor = 1.0f;

    public static final int MAX_AMBIENT_USES = 3;

    protected Ambient(
            String name,
            String description,
            float difficult,
            Set<AmbientAttribute> attributes,
            Texture floorTexture,
            Texture wallTexture,
            Texture sidebarTexture,
            float wallDensity,
            float itemDensity) {
        setName(name);
        setDescription(description);
        setDifficult(difficult);
        setAttributes(attributes != null ? new HashSet<>(attributes) : new HashSet<>());
        this.resources = new HashSet<>();
        this.possibleEvents = new HashMap<>();
        this.climes = new HashSet<>();
        this.floorTexture = floorTexture;
        this.wallTexture = wallTexture;
        this.sidebarTexture = sidebarTexture;
        this.wallDensity = wallDensity;
        this.itemDensity = itemDensity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDifficult() {
        return difficult;
    }

    public void setDifficult(float difficult) {
        this.difficult = difficult;
    }

    public Set<Item> getResources() {
        return Collections.unmodifiableSet(resources);
    }

    public void addResource(Item resource) {
        this.resources.add(resource);
    }

    public void setResources(Set<Item> resources) {
        this.resources = new HashSet<>(resources);
    }

    public Map<Event, Double> getPossibleEvents() {
        return Collections.unmodifiableMap(possibleEvents);
    }

    public void setPossibleEvents(Map<Event, Double> possibleEvents) {
        this.possibleEvents = possibleEvents;
    }

    public Set<Clime> getClimes() {
        return Collections.unmodifiableSet(climes);
    }

    public void addClime(Clime clime) {
        this.climes.add(clime);
    }

    public void setClimes(Set<Clime> climes) {
        this.climes = new HashSet<>(climes);
    }

    public Set<AmbientAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<AmbientAttribute> attributes) {
        this.attributes = attributes;
    }

    public Texture getFloorTexture() {
        return floorTexture;
    }

    public Texture getWallTexture() {
        return wallTexture;
    }

    public Texture getSidebarTexture() {
        return sidebarTexture;
    }

    public float getItemDensity() {
        return itemDensity;
    }

    public float getWallDensity() {
        return wallDensity;
    }

    protected void addDoors(int[][] map, int mapWidth, int mapHeight, Random rand) {
        int numDoors = 2 + (rand.nextFloat() < itemDensity ? 1 : 0);
        java.util.List<int[]> borderPositions = new java.util.ArrayList<>();

        // Collect possible door positions along borders
        for (int x = 2; x < mapWidth - 2; x++) {
            borderPositions.add(new int[] { 0, x });
            borderPositions.add(new int[] { mapHeight - 1, x });
        }
        for (int y = 2; y < mapHeight - 2; y++) {
            borderPositions.add(new int[] { y, 0 });
            borderPositions.add(new int[] { y, mapWidth - 1 });
        }

        // Place doors
        java.util.Collections.shuffle(borderPositions);
        for (int i = 0; i < Math.min(numDoors, borderPositions.size()); i++) {
            int[] pos = borderPositions.get(i);
            int y = pos[0], x = pos[1];
            map[y][x] = 2;

            // Ensure door connects to walkable space
            if (x == 0)
                map[y][1] = 0;
            else if (x == mapWidth - 1)
                map[y][mapWidth - 2] = 0;
            else if (y == 0)
                map[1][x] = 0;
            else if (y == mapHeight - 1)
                map[mapHeight - 2][x] = 0;
        }
    }

    /**
     * Get the darkness factor of this ambient
     *
     * @return The darkness factor (higher is darker)
     */
    public float getDarknessFactor() {
        return darknessFactor;
    }

    /**
     * Set the darkness factor of this ambient
     *
     * @param darknessFactor The darkness factor (higher is darker)
     */
    public void setDarknessFactor(float darknessFactor) {
        this.darknessFactor = darknessFactor;
    }

    /**
     * Get the number of times this ambient has been visited
     *
     * @return The visit count
     */
    public int getTimesVisited() {
        return timesVisited;
    }

    /**
     * Increment the visit counter for this ambient
     */
    public void incrementVisitCount() {
        this.timesVisited++;
    }

    public float getDifficulty() {
        return this.difficult;
    }
}
