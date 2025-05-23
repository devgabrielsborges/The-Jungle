package io.github.com.ranie_borges.thejungle.view.interfaces;

import com.badlogic.gdx.Gdx;

public interface UI {
    int TILE_SIZE = 32;
    int MAP_WIDTH = Gdx.graphics.getWidth() / TILE_SIZE - 20;
    int MAP_HEIGHT = Gdx.graphics.getHeight() / TILE_SIZE;
    int SIDEBAR_WIDTH = 300;

    int TILE_GRASS = 0;
    int TILE_WALL = 1;
    int TILE_DOOR = 2;
    int TILE_CAVE = 3;
    int TILE_WATER = 4;
    int TILE_WETGRASS = 5;
}
