package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.graphics.Color;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;

public class ChatMessage implements UI {
    private final String text;
    private final Color color;
    private float timeLeft;

    public ChatMessage(String text) {
        this(text, Color.WHITE, 5f);
    }

    public ChatMessage(String text, Color color) {
        this(text, color, 5f);
    }
    public ChatMessage(String text, Color color, float timeLeft) {
        this.text = text;
        this.color = color;
        this.timeLeft = timeLeft;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(float timeLeft) {
        this.timeLeft = timeLeft;
    }
}
