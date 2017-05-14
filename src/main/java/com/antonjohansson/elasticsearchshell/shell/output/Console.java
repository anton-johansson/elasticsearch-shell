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
package com.antonjohansson.elasticsearchshell.shell.output;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.JLineShell;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.stereotype.Component;

import jline.console.ConsoleReader;

/**
 * Provides utilities to work with the console.
 */
@Component
public class Console
{
    private static final Logger LOG = HandlerUtils.getLogger(Console.class);

    private final JLineShell shell;

    @Autowired
    Console(JLineShell shell)
    {
        this.shell = shell;
    }

    /**
     * Writes a line of text to the console.
     *
     * @param text The text to write to the console.
     * @param params The parameters to the text.
     */
    public void writeLine(String text, Object... params)
    {
        LOG.info(ConsoleColor.RESET + String.format(text, params));
    }

    /**
     * Writes a line of colored text to the console.
     * 
     * @param text The text to write to the console.
     * @param params The parameters to the text.
     * @param color The color to use.
     */
    public void writeLine(String text, ConsoleColor color, Object... params)
    {
        LOG.info(color.format(String.format(text, params)));
    }

    /**
     * Reads a line from the console.
     *
     * @param prompt The prompt.
     * @param color The color of the prompt.
     * @return Returns the read line.
     */
    public String readLine(String prompt, ConsoleColor color, Object... params)
    {
        ConsoleReader reader = getConsoleReader();

        try
        {
            return reader.readLine(color.format(String.format(prompt, params)));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not read data from the prompt", e);
        }
    }

    private ConsoleReader getConsoleReader()
    {
        Field field = FieldUtils.getField(JLineShell.class, "reader", true);
        try
        {
            return (ConsoleReader) field.get(shell);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException("Could not get the 'reader' field from the shell");
        }
    }
}
