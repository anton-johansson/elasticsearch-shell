package com.antonjohansson.elasticsearchshell.shell;

/**
 * Provides console colors.
 */
public enum ConsoleColor
{
    GREEN("\u001B[32m"),
    RED("\u001B[31m");

    private static final String RESET = "\u001B[0m";

    private final String code;

    // Prevent instantiation
    private ConsoleColor(String code)
    {
        this.code = code;
    }

    /**
     * Formats the given text with this color.
     *
     * @param text The text to format.
     * @return Returns the formatted text.
     */
    public String format(String text)
    {
        return new StringBuilder()
                .append(code)
                .append(text)
                .append(RESET)
                .toString();
    }
}
