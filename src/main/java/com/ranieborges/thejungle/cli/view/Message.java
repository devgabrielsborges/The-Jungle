package com.ranieborges.thejungle.cli.view;

import lombok.NonNull;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

public abstract class Message implements Art{
    public static void displayOnScreen(@NonNull String message) {
        System.out.println(message);
    }

    public static void displayStyledOnScreen(@NonNull String message, @NonNull String... styleCodes) {
        System.out.println(TerminalStyler.style(message, styleCodes));
    }

    public void displayOnScreen(@NonNull String message, @NonNull String... args) {
        System.out.println(String.format(message, args));
    }

    public void displayOnScreen(@NonNull String message, @NonNull Object... args) {
        System.out.println(String.format(message, args));
    }

    public static void displayWithDelay(String message, long delayMillis) {
        try {
            Thread.sleep(delayMillis);
            System.out.println(message);
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted while sleeping: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static void displayWithDelay(String message) {
        displayWithDelay(message, 1000);
    }

    public static void displayCharByCharWithDelay(String message, long charDelayMillis) {
        try {
            for (char c : message.toCharArray()) {
                System.out.print(c);
                System.out.flush(); // Ensure the character is printed immediately
                Thread.sleep(charDelayMillis);
            }
            System.out.println(); // Move to the next line after the message
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted during character-by-character print: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static void displayCharByCharWithDelay(String message, long charDelayMillis, String... styleCodes) {
        try {
            for (char c : message.toCharArray()) {
                System.out.print(TerminalStyler.style(String.valueOf(c), styleCodes));
                System.out.flush(); // Ensure the character is printed immediately
                Thread.sleep(charDelayMillis);
            }
            System.out.println(); // Move to the next line after the message
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted during character-by-character print: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
