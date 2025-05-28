package com.ranieborges.thejungle.cli.view.utils;

public final class TerminalStyler {

    // ANSI Escape Codes
    public static final String RESET = "\u001B[0m";

    // Text Styles
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String ITALIC = "\u001B[3m";

    // Foreground Colors
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Bright Foreground Colors
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    // Background Colors
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_MAGENTA = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_WHITE = "\u001B[47m";

    // Bright Background Colors
    public static final String BG_BRIGHT_BLACK = "\u001B[100m";
    public static final String BG_BRIGHT_RED = "\u001B[101m";
    public static final String BG_BRIGHT_GREEN = "\u001B[102m";
    public static final String BG_BRIGHT_YELLOW = "\u001B[103m";
    public static final String BG_BRIGHT_BLUE = "\u001B[104m";
    public static final String BG_BRIGHT_MAGENTA = "\u001B[105m";
    public static final String BG_BRIGHT_CYAN = "\u001B[106m";
    public static final String BG_BRIGHT_WHITE = "\u001B[107m";

    /**
     * Applies a single style (color or text style) to a message.
     *
     * @param message The message string.
     * @param styleCode The ANSI style code (e.g., TerminalStyler.RED, TerminalStyler.BOLD).
     * @return The styled message string.
     */
    public static String style(String message, String styleCode) {
        if (message == null || styleCode == null) {
            return message;
        }
        return styleCode + message + RESET;
    }

    /**
     * Applies multiple style codes to a message.
     *
     * @param message The message string.
     * @param styleCodes Varargs of ANSI style codes.
     * @return The styled message string.
     */
    public static String style(String message, String... styleCodes) {
        if (message == null || styleCodes == null || styleCodes.length == 0) {
            return message;
        }
        StringBuilder sb = new StringBuilder();
        for (String code : styleCodes) {
            if (code != null) {
                sb.append(code);
            }
        }
        sb.append(message);
        sb.append(RESET);
        return sb.toString();
    }

    public static String error(String message) {
        return style(message, BRIGHT_RED, BOLD);
    }

    public static String warning(String message) {
        return style(message, BRIGHT_YELLOW);
    }

    public static String success(String message) {
        return style(message, BRIGHT_GREEN);
    }

    public static String info(String message) {
        return style(message, BRIGHT_BLUE);
    }

    public static String title(String message) {
        return style(message, BRIGHT_CYAN, BOLD, UNDERLINE);
    }

    public static String highlight(String message) {
        return style(message, BRIGHT_MAGENTA, BOLD);
    }

    public static String debug(String message) {
        return style(message, BRIGHT_BLACK); // Often gray
    }
}
