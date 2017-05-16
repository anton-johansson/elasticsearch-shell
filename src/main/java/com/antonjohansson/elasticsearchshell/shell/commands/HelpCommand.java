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

import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.SimpleParser;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * Provides a help command for showing available commands and extended details about each command.
 */
@Component
class HelpCommand extends AbstractCommand
{
    @CliCommand(value = "help", help = "Lists all commands usage")
    public void help(@CliOption(key = {"", "command"}, help = "The command name to get help for") String buffer)
    {
        command(() ->
        {
            JLineShellComponent shell = context().getBean("shell", JLineShellComponent.class);
            SimpleParser parser = shell.getSimpleParser();
            parser.obtainHelp(buffer);
        });
    }
}
