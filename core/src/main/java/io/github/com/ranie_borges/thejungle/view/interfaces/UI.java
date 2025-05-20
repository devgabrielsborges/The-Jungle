package io.github.com.ranie_borges.thejungle.view.interfaces;

import com.badlogic.gdx.Gdx;

public interface UI {
    static final int TILE_SIZE = 32;
    static final int MAP_WIDTH = Gdx.graphics.getWidth() / TILE_SIZE - 20;
    static final int MAP_HEIGHT = Gdx.graphics.getHeight() / TILE_SIZE;
    static final int SIDEBAR_WIDTH = 300;

    static final int TILE_GRASS = 0;
    static final int TILE_WALL = 1;
    static final int TILE_DOOR = 2;
    static final int TILE_CAVE = 3;
    static final int TILE_WATER = 4;
}
