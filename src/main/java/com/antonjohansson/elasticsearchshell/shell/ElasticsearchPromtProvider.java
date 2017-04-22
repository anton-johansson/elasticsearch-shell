/**
 *    Copyright 2017 Anton Johansson
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
    private static final char PROMPT_CHARACTER = 0x276F;
    private static final String PROMPT = String.valueOf(PROMPT_CHARACTER) + " ";

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
