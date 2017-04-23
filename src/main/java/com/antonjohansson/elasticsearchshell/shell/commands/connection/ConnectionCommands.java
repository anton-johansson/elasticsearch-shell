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
package com.antonjohansson.elasticsearchshell.shell.commands.connection;

import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.WHITE;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.connection.ConnectionKey;
import com.antonjohansson.elasticsearchshell.connection.ConnectionManager;
import com.antonjohansson.elasticsearchshell.session.SessionManager;
import com.antonjohansson.elasticsearchshell.shell.commands.core.AbstractCommand;
import com.antonjohansson.elasticsearchshell.shell.commands.core.CommandException;

/**
 * Provides commands for managing connections.
 */
@Component
class ConnectionCommands extends AbstractCommand
{
    private final ConnectionManager connectionManager;
    private final SessionManager sessionManager;

    @Autowired
    ConnectionCommands(ConnectionManager connectionManager, SessionManager sessionManager)
    {
        this.connectionManager = connectionManager;
        this.sessionManager = sessionManager;
    }

    @CliCommand(value = "connect", help = "Selects a connection for the current session")
    public void connect(@CliOption(key = {"", "name"}, mandatory = true, help = "The name of the connection to choose") ConnectionKey key)
    {
        command(() ->
        {
            Connection connection = connectionManager.get(key).orElseThrow(() -> new CommandException("Connection '%s' does not exist", key));
            String currentConnectionName = sessionManager.getCurrentSession().getConnection().map(Connection::getName).orElse("");
            if (currentConnectionName.equals(connection.getName()))
            {
                throw new CommandException("Already on connection '%s'", connection.getName());
            }
            sessionManager.getCurrentSession().setConnection(connection);
            console().writeLine("Connection set to '%s'", WHITE, connection.getURL());
        });
    }

    @CliCommand(value = "connection", help = "Displays the current connection")
    public void current()
    {
        command(() ->
        {
            Optional<Connection> currentConnection = sessionManager.getCurrentSession().getConnection();
            if (!currentConnection.isPresent())
            {
                console().writeLine("No connection is set",  WHITE);
            }
            else
            {
                console().writeLine("Connection is '%s'", WHITE, currentConnection.get().getURL());
            }
        });
    }
}
