package io.github.com.ranie_borges.thejungle.model.stats;

import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.entity.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to store ambient-specific data including maps and visit history
 */
public class AmbientData {
    @Expose
    private int[][] map;

    @Expose
    private int visitCount;

    @Expose
    private List<Item> remainingResources;

    public AmbientData() {
        // Default constructor for Gson
        this.visitCount = 1;
        this.remainingResources = new ArrayList<>();
    }

    public AmbientData(int[][] map) {
        this.map = map;
        this.visitCount = 1;
        this.remainingResources = new ArrayList<>();
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int count) {
        this.visitCount = count;
    }

    public void incrementVisitCount() {
        this.visitCount++;
    }

    /**
     * Get the remaining resources in this ambient
     * 
     * @return List of remaining resources
     */
    public List<Item> getRemainingResources() {
        return remainingResources;
    }

    /**
     * Set the remaining resources for this ambient
     * 
     * @param resources List of resources to set
     */
    public void setRemainingResources(List<Item> resources) {
        this.remainingResources = resources;
    }
}
