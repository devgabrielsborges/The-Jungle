package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class ProceduralMapScreen implements Screen {

    private final int TILE_SIZE = 32;
    private final int MAP_WIDTH = 30;
    private final int MAP_HEIGHT = 20;

    private int[][] mapa = new int[MAP_HEIGHT][MAP_WIDTH];

    private Texture texChao;
    private Texture texParede;
    private Texture texPorta;
    private Texture texPlayer;
    private SpriteBatch batch;
    private Vector2 playerPos;

    @Override
    public void show() {
        texChao = new Texture("GameScreen/chao.png");
        texParede = new Texture("GameScreen/parede.png");
        texPorta = new Texture("GameScreen/chao.png"); // você pode trocar por imagem real de porta
        texPlayer = new Texture("sprites/character/personagem_luta.png");

        batch = new SpriteBatch();

        gerarMapa();
        posicionarPlayer();
    }

    private void gerarMapa() {
        Random rand = new Random();
        mapa = new int[MAP_HEIGHT][MAP_WIDTH];

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) {
                    mapa[y][x] = 1; // parede
                } else {
                    mapa[y][x] = rand.nextFloat() < 0.2f ? 1 : 0;
                }
            }
        }

        // Corrige centro com caminho livre (opcional)
        for (int x = 1; x < MAP_WIDTH - 1; x++) {
            mapa[MAP_HEIGHT / 2][x] = 0;
        }

        // CANTOS possíveis
        int[][] cantos = {
            {1, 1},
            {1, MAP_WIDTH - 2},
            {MAP_HEIGHT - 2, 1},
            {MAP_HEIGHT - 2, MAP_WIDTH - 2}
        };

        // Embaralha e escolhe 2 cantos diferentes
        java.util.List<int[]> lista = java.util.Arrays.asList(cantos);
        java.util.Collections.shuffle(lista);

        for (int i = 0; i < 2; i++) {
            int[] pos = lista.get(i);
            mapa[pos[0]][pos[1]] = 2; // Porta
        }
    }


    private void posicionarPlayer() {
        // Encontra uma posição livre (chão) para nascer
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            for (int x = 1; x < MAP_WIDTH - 1; x++) {
                if (mapa[y][x] == 0) {
                    playerPos = new Vector2(x * TILE_SIZE, y * TILE_SIZE);
                    return;
                }
            }
        }

        // fallback
        playerPos = new Vector2(TILE_SIZE, TILE_SIZE);
    }

    private void moverJogador(float delta) {
        float velocidade = 100f;
        float nextX = playerPos.x;
        float nextY = playerPos.y;

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) nextY += velocidade * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) nextY -= velocidade * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) nextX -= velocidade * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) nextX += velocidade * delta;

        int tileX = (int) ((nextX + 8) / TILE_SIZE);
        int tileY = (int) ((nextY + 8) / TILE_SIZE);

        if (tileX >= 0 && tileX < MAP_WIDTH && tileY >= 0 && tileY < MAP_HEIGHT) {
            int tipoTile = mapa[tileY][tileX];

            if (tipoTile != 1) { // Se não for parede
                playerPos.set(nextX, nextY);
            }

            if (tipoTile == 2) { // Porta
                gerarMapa();
                posicionarPlayer();
            }
        }
    }

    @Override
    public void render(float delta) {
        moverJogador(delta);

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Texture tex;
                if (mapa[y][x] == 1) tex = texParede;
                else if (mapa[y][x] == 2) tex = texPorta;
                else tex = texChao;

                batch.draw(tex, x * TILE_SIZE, y * TILE_SIZE);
            }
        }

        batch.draw(texPlayer, playerPos.x, playerPos.y, 20, 20);

        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        texChao.dispose();
        texParede.dispose();
        texPorta.dispose();
        texPlayer.dispose();
        batch.dispose();
    }
}
