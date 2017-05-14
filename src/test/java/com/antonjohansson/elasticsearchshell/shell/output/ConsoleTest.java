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

import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.RED;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.WHITE;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.shell.core.JLineShell;

import jline.console.ConsoleReader;

/**
 * Unit tests of {@link Console}.
 */
public class ConsoleTest extends Assert
{
    private Console console;
    private @Mock ConsoleReader reader;
    private @Mock JLineShell shell;
    private @Mock Logger log;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        setConsoleReader();
        console = new Console(shell, log);

        when(reader.readLine(WHITE.format("Hello, Anton!"))).thenReturn("Hi!");
    }

    @Test
    public void test_writeLine_with_color()
    {
        console.writeLine("The number is %d", RED, 5);

        verify(log).info("\u001B[31mThe number is 5\u001B[0m");
    }

    @Test
    public void test_writeLine()
    {
        console.writeLine("The number is %d", 1);

        verify(log).info("\u001B[0mThe number is 1");
    }

    @Test
    public void test_readLine()
    {
        String actual = console.readLine("Hello, %s!", WHITE, "Anton");
        String expected = "Hi!";

        assertEquals(expected, actual);
    }

    @Test(expected = RuntimeException.class)
    public void test_readLine_throws_exception() throws IOException
    {
        doThrow(IOException.class).when(reader).readLine(WHITE.format("Hi!"));

        console.readLine("Hi!", WHITE);
    }

    private void setConsoleReader()
    {
        Field field = FieldUtils.getField(JLineShell.class, "reader", true);
        try
        {
            field.set(shell, reader);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException("Could not set the 'reader' field on the shell");
        }
    }
}
