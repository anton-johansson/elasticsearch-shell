package com.antonjohansson.elasticsearchshell.shell;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

/**
 * Provides the prompt itself.
 */
@Component
@Order(HIGHEST_PRECEDENCE)
public class ElasticsearchPromtProvider implements PromptProvider
{
    private static char PROMPT_CHARACTER = 0x276F;
    private static String PROMPT = String.valueOf(PROMPT_CHARACTER) + " ";

    @Override
    public String getPrompt()
    {
        ConsoleColor color = ConsoleColor.GREEN;
        return color.format(PROMPT);
    }

    @Override
    public String getProviderName()
    {
        return "elasticsearch-shell";
    }
}
