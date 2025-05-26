package com.ranieborges.thejungle.cli.view.utils;

public final class TerminalUtils {
    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("Error clearing screen: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("This will be cleared in 2 seconds...");
        Thread.sleep(2000);
        TerminalUtils.clearScreen();
        System.out.println("Screen cleared!");
    }
}
