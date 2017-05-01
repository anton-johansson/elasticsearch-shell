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
package com.antonjohansson.elasticsearchshell.shell.commands;

import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.RED;

import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.shell.core.CommandMarker;

import com.antonjohansson.elasticsearchshell.common.ElasticsearchException;
import com.antonjohansson.elasticsearchshell.shell.PromptState;
import com.antonjohansson.elasticsearchshell.shell.output.Console;

/**
 * Skeleton for commands.
 */
public abstract class AbstractCommand implements CommandMarker, ApplicationContextAware
{
    private ApplicationContext context;
    private Console console;
    private PromptState promptState;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.context = context;
    }

    @Autowired
    public void setConsole(Console console)
    {
        this.console = console;
    }

    @Autowired
    public void setPromptState(PromptState promptState)
    {
        this.promptState = promptState;
    }

    /**
     * Gets the Spring application context.
     *
     * @return Returns the application context.
     */
    protected ApplicationContext context()
    {
        return context;
    }

    /**
     * Gets the console.
     *
     * @return Returns the console.
     */
    public Console console()
    {
        return console;
    }

    /**
     * Runs a command.
     *
     * @param command The command to run.
     */
    protected void command(Command command)
    {
        command(command, null);
    }

    /**
     * Runs a command.
     *
     * @param command The command to run.
     * @param onError The command to run when errors occur.
     */
    protected void command(Command command, Runnable onError)
    {
        try
        {
            command.command();
            promptState.setLastSuccess(true);
        }
        catch (CommandException | ElasticsearchException e)
        {
            console.writeLine(e.getMessage(), RED);
            promptState.setLastSuccess(false);
            Optional.ofNullable(onError).ifPresent(Runnable::run);
        }
    }
}
