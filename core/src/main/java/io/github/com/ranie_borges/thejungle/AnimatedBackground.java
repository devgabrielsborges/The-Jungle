package io.github.com.ranie_borges.thejungle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class AnimatedBackground extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime;
    private Texture spriteSheet;

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
        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Seleciona os 5 primeiros frames da primeira linha (índice 0)
        if (tmp.length > 0) {
            int totalColumnsRow0 = tmp[0].length;
            int limitRow0 = Math.min(5, totalColumnsRow0);
            for (int j = 0; j < limitRow0; j++) {
                frames.add(tmp[0][j]);
            }
        }

        // Seleciona os 3 últimos frames da segunda linha (índice 1), se disponíveis
//        if (tmp.length > 1) {
//            int totalColumnsRow1 = tmp[1].length;
//            int startIndex = Math.max(0, totalColumnsRow1 - 3);
//            for (int j = startIndex; j < totalColumnsRow1; j++) {
//                frames.add(tmp[1][j]);
//            }
//        }

        // Depuração: Imprime o total de frames adicionados (deve ser 8 se a spritesheet tiver frames suficientes)
        System.out.println("Total frames na animação: " + frames.size);

        // Cria a animação com os frames selecionados, definindo o modo de loop
        animation = new Animation<TextureRegion>(frameDuration, frames, Animation.PlayMode.LOOP);
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

    // Método para liberar os recursos utilizados
    public void dispose() {
        spriteSheet.dispose();
    }
}
