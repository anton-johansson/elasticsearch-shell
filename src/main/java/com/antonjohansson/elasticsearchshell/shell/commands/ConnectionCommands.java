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

import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.WHITE;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.connection.ConnectionKey;
import com.antonjohansson.elasticsearchshell.connection.ConnectionManager;
import com.antonjohansson.elasticsearchshell.domain.ClusterInfo;
import com.antonjohansson.elasticsearchshell.index.IndexKey;
import com.antonjohansson.elasticsearchshell.session.SessionManager;

/**
 * Provides commands for managing connections.
 */
@Component
public class ConnectionCommands extends AbstractCommand
{
    private ConnectionManager connectionManager;
    private SessionManager sessionManager;
    private ClientFactory clientFactory;

    @Autowired
    void setConnectionManager(ConnectionManager connectionManager)
    {
        this.connectionManager = connectionManager;
    }

    @Autowired
    void setSessionManager(SessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    @Autowired
    void setClientFactory(ClientFactory clientFactory)
    {
        this.clientFactory = clientFactory;
    }

    /** Connects to a specific connection. */
    @CliCommand(value = "connect", help = "Selects a connection for the current session")
    public void connect(@CliOption(key = {"", "name"}, mandatory = true, help = "The name of the connection to choose") ConnectionKey key)
    {
        Connection previousConnection = sessionManager.getCurrentSession().getConnection();
        IndexKey previousIndex = sessionManager.getCurrentSession().getCurrentIndex();

        command(() ->
        {
            Connection connection = connectionManager.get(key).orElseThrow(() -> new CommandException("Connection '%s' does not exist", key));
            String currentConnectionName = sessionManager.getCurrentSession().getOptionalConnection().map(Connection::getName).orElse("");
            if (currentConnectionName.equals(connection.getName()))
            {
                throw new CommandException("Already on connection '%s'", connection.getName());
            }
            sessionManager.getCurrentSession().setConnection(connection);
            sessionManager.getCurrentSession().setCurrentIndex(null);
            ClusterInfo clusterInfo = clientFactory.getClient().getClusterInfo();
            console().writeLine("Connected to cluster '%s' (version %s)", WHITE, clusterInfo.getClusterName(), clusterInfo.getVersion().getNumber());
        }, () ->
        {
            sessionManager.getCurrentSession().setConnection(previousConnection);
            sessionManager.getCurrentSession().setCurrentIndex(previousIndex);
        });
    }

    /** Prints the current connection. */
    @CliCommand(value = "connection", help = "Displays the current connection")
    public void current()
    {
        command(() ->
        {
            Optional<Connection> currentConnection = sessionManager.getCurrentSession().getOptionalConnection();
            if (!currentConnection.isPresent())
            {
                console().writeLine("No connection is set",  WHITE);
            }
            else
            {
                ClusterInfo clusterInfo = clientFactory.getClient().getClusterInfo();
                console().writeLine("Connected to '%s' at '%s' (version %s)", WHITE, clusterInfo.getClusterName(), currentConnection.get().getURL(), clusterInfo.getVersion().getNumber());
            }
        });
    }

    /** Indicates whether or not the current session is connected. */
    @CliAvailabilityIndicator("disconnect")
    public boolean isConnected()
    {
        return sessionManager.getCurrentSession().getOptionalConnection().isPresent();
    }

    /** Disconnects from the current connection. */
    @CliCommand(value = "disconnect", help = "Disconnects from the current connection")
    public void disconnect()
    {
        command(() ->
        {
            Connection connection = sessionManager.getCurrentSession().getOptionalConnection().get();
            sessionManager.getCurrentSession().setConnection(null);
            sessionManager.getCurrentSession().setCurrentIndex(null);
            console().writeLine("Disconnected from '%s'", WHITE, connection.getURL());
        });
    }
}
