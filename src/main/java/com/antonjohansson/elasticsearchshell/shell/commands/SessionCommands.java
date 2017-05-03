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

import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.GREEN;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.WHITE;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.session.SessionKey;
import com.antonjohansson.elasticsearchshell.session.SessionManager;

/**
 * Provide commands for managing sessions.
 */
@Component
class SessionCommands extends AbstractCommand
{
    private SessionManager manager;

    @Autowired
    void setManager(SessionManager manager)
    {
        this.manager = manager;
    }

    @CliCommand(value = "session-add", help = "Adds a new session and switches to it")
    public void add(@CliOption(key = {"", "name"}, help = "The name of the session to add") String name)
    {
        command(() ->
        {
            if (isBlank(name))
            {
                manager.newSession();
            }
            else
            {
                boolean succeeded = manager.newSession(name);
                if (!succeeded)
                {
                    throw new CommandException("Session '%s' already exists", name);
                }
            }

            console().writeLine("Switched to session '%s'", WHITE, manager.getCurrentSession().getName());
        });
    }

    @CliCommand(value = "session-remove", help = "Removes a session")
    public void remove(@CliOption(key = {"", "name"}, mandatory = true, help = "The name of the session to remove") SessionKey key)
    {
        command(() ->
        {
            if (key.getName().equals(manager.getCurrentSession().getName()))
            {
                throw new CommandException("You can't remove the session you are currently working with");
            }

            if (!manager.remove(key))
            {
                throw new CommandException("Session '%s' does not exist", key);
            }

            console().writeLine("Removed session '%s'", WHITE, key);
        });
    }

    @CliCommand(value = "session", help = "Switches to another session")
    public void change(@CliOption(key = {"", "name"}, mandatory = true, help = "The name of the session to switch to") SessionKey key)
    {
        command(() ->
        {
            if (key.getName().equals(manager.getCurrentSession().getName()))
            {
                throw new CommandException("You are already on session '%s'", key);
            }

            if (!manager.setCurrentSession(key))
            {
                throw new CommandException("Session '%s' does not exist", key);
            }

            console().writeLine("Switched to '%s'", WHITE, key);
        });
    }

    @CliCommand(value = "sessions", help = "Lists all existing sessions")
    public void list()
    {
        command(() ->
        {
            for (String name : manager.getSessionNames())
            {
                if (name.equals(manager.getCurrentSession().getName()))
                {
                    console().writeLine(name, GREEN);
                }
                else
                {
                    console().writeLine(name, WHITE);
                }
            }
        });
    }
}
