package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class AnimatedBackground extends Actor {
    private final Animation<TextureRegion> animation;
    private float stateTime;
    private final Texture spriteSheet;

    /**
     * Construtor para criar o fundo animado utilizando uma spritesheet.
     *
     * @param spriteSheetPath Caminho para a spritesheet.
     * @param frameDuration   Duração de cada frame (em segundos).
     * @param frameWidth      Largura de cada frame.
     * @param frameHeight     Altura de cada frame.
     */
    public AnimatedBackground(String spriteSheetPath, float frameDuration, int frameWidth, int frameHeight) {
        // Carrega a imagem contendo todos os frames
        spriteSheet = new Texture(Gdx.files.internal(spriteSheetPath));

        // Divide a spritesheet em uma matriz de TextureRegions
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
        Array<TextureRegion> frames = new Array<>();

        // Seleciona os 5 primeiros frames da primeira linha (índice 0)
        if (tmp.length > 0) {
            int totalColumnsRow0 = tmp[0].length;
            int limitRow0 = Math.min(5, totalColumnsRow0);
            for (int j = 0; j < limitRow0; j++) {
                frames.add(tmp[0][j]);
            }
        }

        // Cria a animação com os frames selecionados, definindo o modo de loop
        animation = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
        stateTime = 0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Atualiza o tempo de estado para controlar a animação
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Obtém o frame atual da animação
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        // Desenha o frame atual; o fundo cobre toda a área do ator
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}

