package io.github.com.ranie_borges.thejungle.model.events.events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class SnakeEventManager {
    private static boolean showAlert = false;
    private static boolean waitForSpace = false;
    private static float timer = 0f;
    private static final float DURATION = 3f;
    private static final Texture snakeBiteImage = new Texture(Gdx.files.internal("sprites/criaturas/snakeBite.png"));
    private static final Sound snakeHissSound = Gdx.audio.newSound(Gdx.files.internal("sounds/snakeHiss.mp3"));
    private static float biteCooldown = 0f;
    private static final float BITE_COOLDOWN_DURATION = 5f;

    public static void triggerSnakeBite() {
        if (biteCooldown > 0) return; // evita nova mordida durante cooldown

        showAlert = true;
        waitForSpace = true;
        timer = 0f;
        biteCooldown = BITE_COOLDOWN_DURATION;
        snakeHissSound.play(0.8f);
    }


    public static void update(float delta) {
        if (biteCooldown > 0) {
            biteCooldown -= delta;
            if (biteCooldown < 0) biteCooldown = 0;
        }

        if (showAlert && !waitForSpace) {
            timer += delta;
            if (timer >= DURATION) {
                showAlert = false;
            }
        }
    }


    public static void handleInput() {
        if (waitForSpace && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            waitForSpace = false;
            showAlert = false;
        }
    }

    public static boolean isAlertActive() {
        return showAlert;
    }

    public static boolean isWaitingForSpace() {
        return waitForSpace;
    }

    public static Texture getSnakeBiteImage() {
        return snakeBiteImage;
    }

    public static void dispose() {
        snakeBiteImage.dispose();
        snakeHissSound.dispose();
    }
}
