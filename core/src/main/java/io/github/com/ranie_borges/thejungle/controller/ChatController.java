package io.github.com.ranie_borges.thejungle.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.com.ranie_borges.thejungle.view.ChatMessage;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;

import java.util.ArrayList;
import java.util.List;

public class ChatController implements UI {
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private static final int maxMessages = 5;

    /**
     * Adds a message to the system event chat
     *
     * @param message The message to add
     */
    public void addMessage(String message) {
        chatMessages.add(0, new ChatMessage(message));
        if (chatMessages.size() > maxMessages) {
            chatMessages.remove(chatMessages.size() - 1);
        }
    }

    /**
     * Adds a message with a specified color
     *
     * @param message The message to add
     * @param color   The color for the message
     */
    public void addMessage(String message, Color color) {
        chatMessages.add(0, new ChatMessage(message, color));
        if (chatMessages.size() > maxMessages) {
            chatMessages.remove(chatMessages.size() - 1);
        }
    }

    /**
     * Removes a specific message by its content
     *
     * @param message The message content to remove
     */
    public void removeMessage(String message) {
        for (int i = 0; i < chatMessages.size(); i++) {
            if (chatMessages.get(i).getText().equals(message)) {
                chatMessages.remove(i);
                return;
            }
        }
    }

    public List<ChatMessage> getMessages() {
        return chatMessages;
    }

    /**
     * Clears all chat messages
     */
    public void clearMessages() {
        chatMessages.clear();
    }

    /**
     * Updates the chat messages (removes expired messages)
     *
     * @param delta Time elapsed since last frame
     */
    public void update(float delta) {
        for (int i = chatMessages.size() - 1; i >= 0; i--) {
            ChatMessage message = chatMessages.get(i);
            message.setTimeLeft(message.getTimeLeft() - delta);
            if (message.getTimeLeft() <= 0) {
                chatMessages.remove(i);
            }
        }
    }

    public void renderChatArea(SpriteBatch batch, ShapeRenderer shapeRenderer, int width, int height,
            float sidebarWidth) {
        float chatX = width - sidebarWidth + 10;
        float chatY = 350;
        float chatWidth = sidebarWidth - 20;
        float chatAreaHeight = 500f;
        float frameThickness = 12f;

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Outer dark brown frame
        shapeRenderer.setColor(new Color(0.4f, 0.2f, 0.1f, 1f));
        shapeRenderer.rect(chatX - frameThickness, chatY - frameThickness,
                chatWidth + frameThickness * 2, chatAreaHeight + frameThickness * 2);

        // Inner medium brown frame with "grain" effect
        shapeRenderer.setColor(new Color(0.6f, 0.3f, 0.1f, 1f));
        shapeRenderer.rect(chatX - frameThickness + 2, chatY - frameThickness + 2,
                chatWidth + frameThickness * 2 - 4, chatAreaHeight + frameThickness * 2 - 4);

        // Parchment background (slightly yellowish white)
        shapeRenderer.setColor(new Color(0.95f, 0.92f, 0.82f, 0.9f));
        shapeRenderer.rect(chatX, chatY, chatWidth, chatAreaHeight);
        shapeRenderer.end();

        // Draw "stains" on the parchment for an aged look
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.8f, 0.7f, 0.5f, 0.7f));
        float stainWidth = 1f;
        float stainHeight = 1.5f;
        for (float x = chatX; x < chatX + chatWidth; x += stainWidth * 2) {
            for (float y = chatY; y < chatY + chatAreaHeight; y += stainHeight * 2) {
                shapeRenderer.rect(x, y, stainWidth, stainHeight);
            }
        }
        shapeRenderer.end();
        batch.begin();
    }

    /**
     * Renders all chat messages within the chat area
     *
     * @param batch        The SpriteBatch to use for rendering
     * @param width        The screen width
     * @param sidebarWidth The width of the sidebar
     */
    public void renderChatMessages(SpriteBatch batch, int width, float sidebarWidth) {
        if (chatMessages.isEmpty()) {
            return;
        }

        float chatX = width - sidebarWidth + 20; // Start text a bit inside the frame
        float startY = 800; // Starting Y position for the first message
        float lineHeight = 30; // Height of each message line

        BitmapFont messageFont = new BitmapFont();
        messageFont.getData().setScale(0.8f);

        for (int i = 0; i < chatMessages.size(); i++) {
            ChatMessage message = chatMessages.get(i);
            messageFont.setColor(message.getColor());

            float alpha = Math.min(1.0f, message.getTimeLeft() / 2.0f);
            Color color = messageFont.getColor();
            messageFont.setColor(color.r, color.g, color.b, alpha);

            messageFont.draw(batch, message.getText(), chatX, startY - i * lineHeight);
        }
    }
}
