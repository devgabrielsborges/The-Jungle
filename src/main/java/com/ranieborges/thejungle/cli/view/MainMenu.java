package com.ranieborges.thejungle.cli.view;

import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

public abstract class MainMenu implements Art {
    public static void displayInitialMenu() {
        Message.displayWithDelay("");
        Message.displayCharByCharWithDelay(TerminalStyler.title("Welcome"), 100);
        Message.displayCharByCharWithDelay(TerminalStyler.style("\t\tTo", TerminalStyler.CYAN), 100);
        Message.displayCharByCharWithDelay(Art.THE_JUNGLE, 2);
        Message.displayCharByCharWithDelay(Art.PLAYER_BACK, 2);
    }

    public static void main(String[] args) {
        displayInitialMenu();
        Message.displayWithDelay("", 2000);
        displayMenuOptions();
    }

    public static void displayMenuOptions() {
        Message.displayOnScreen(TerminalStyler.style("\n--- Main Menu ---", TerminalStyler.BOLD, TerminalStyler.CYAN));
        Message.displayOnScreen(TerminalStyler.style("1. Start New Game", TerminalStyler.BRIGHT_GREEN));
        Message.displayOnScreen(TerminalStyler.style("2. Continue Last Game", TerminalStyler.BRIGHT_YELLOW)); // Changed from "Load Game"
        Message.displayOnScreen(TerminalStyler.style("3. Exit", TerminalStyler.BRIGHT_RED)); // Adjusted number
    }
}
