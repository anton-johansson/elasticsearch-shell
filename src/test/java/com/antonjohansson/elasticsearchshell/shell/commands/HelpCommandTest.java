/**
 * Copyright 2017 Anton Johansson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.antonjohansson.elasticsearchshell.shell.commands;

import org.junit.Test;
import org.springframework.shell.core.CommandResult;

/**
 * Unit tests of {@link HelpCommand}.
 */
public class HelpCommandTest extends AbstractCommandTest<HelpCommand>
{
    @Test
    public void test()
    {
        CommandResult result = shell().executeCommand("help");
        assertTrue(result.isSuccess());
    }
}
