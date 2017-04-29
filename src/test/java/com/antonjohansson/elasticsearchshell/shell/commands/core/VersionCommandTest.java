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
package com.antonjohansson.elasticsearchshell.shell.commands.core;

import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.WHITE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.shell.core.CommandResult;

import com.antonjohansson.elasticsearchshell.shell.output.Console;

/**
 * Unit tests of {@link VersionCommand}.
 */
public class VersionCommandTest extends AbstractCommandTest<VersionCommand>
{
    private @Mock Console console;

    @Override
    protected void initMocks()
    {
        command().setConsole(console);
    }

    @Test
    public void test_version()
    {
        CommandResult result = shell().executeCommand("version");
        assertTrue(result.isSuccess());

        verify(console).writeLine("development", WHITE);
        verifyNoMoreInteractions(console);
    }
}
