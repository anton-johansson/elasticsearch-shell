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

import static com.antonjohansson.elasticsearchshell.domain.TestDataUtils.createItem;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.RED;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.WHITE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.shell.core.CommandResult;

import com.antonjohansson.elasticsearchshell.client.Client;
import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.connection.ConnectionKey;
import com.antonjohansson.elasticsearchshell.connection.ConnectionManager;
import com.antonjohansson.elasticsearchshell.domain.ClusterInfo;
import com.antonjohansson.elasticsearchshell.session.Session;
import com.antonjohansson.elasticsearchshell.session.SessionManager;
import com.antonjohansson.elasticsearchshell.shell.output.Console;

/**
 * Unit tests of {@link ConnectionCommands}.
 */
public class ConnectionCommandsTest extends AbstractCommandTest<ConnectionCommands>
{
    private static final Session SESSION_WITHOUT_CONNECTION = new Session();
    private static final Session SESSION_WITH_CONNECTION = createItem(Session.class, 1);

    private @Mock Client client;
    private @Mock ClientFactory clientFactory;
    private @Mock Console console;
    private @Mock ConnectionManager connectionManager;
    private @Mock SessionManager sessionManager;

    @Override
    protected void initMocks()
    {
        command().setClientFactory(clientFactory);
        command().setConsole(console);
        command().setConnectionManager(connectionManager);
        command().setSessionManager(sessionManager);

        when(clientFactory.getClient()).thenReturn(client);
        when(client.getClusterInfo()).thenReturn(createItem(ClusterInfo.class, 1));
    }

    @Test
    public void test_current()
    {
        when(sessionManager.getCurrentSession()).thenReturn(SESSION_WITH_CONNECTION);

        CommandResult result = shell().executeCommand("connection");
        assertTrue(result.isSuccess());

        verify(console).writeLine("Connected to '%s' at '%s' (version %s)", WHITE, "clusterName1", "http://host1:1", "number1");
        verifyNoMoreInteractions(console);
    }

    @Test
    public void test_current_when_not_connected()
    {
        when(sessionManager.getCurrentSession()).thenReturn(SESSION_WITHOUT_CONNECTION);

        CommandResult result = shell().executeCommand("connection");
        assertTrue(result.isSuccess());

        verify(console).writeLine("No connection is set", WHITE);
        verifyNoMoreInteractions(console);
    }

    @Test
    public void test_disconnect()
    {
        Session session = createItem(Session.class, 1);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        CommandResult result = shell().executeCommand("disconnect");
        assertTrue(result.isSuccess());
        assertNull(session.getConnection());
    }

    @Test
    public void test_disconnect_when_not_connected()
    {
        when(sessionManager.getCurrentSession()).thenReturn(SESSION_WITHOUT_CONNECTION);

        CommandResult result = shell().executeCommand("disconnect");
        assertFalse(result.isSuccess());
    }

    @Test
    public void test_connect()
    {
        Session session = new Session();
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(connectionManager.get(new ConnectionKey("my-connection"))).thenReturn(Optional.of(createItem(Connection.class, 1)));

        CommandResult result = shell().executeCommand("connect my-connection");
        assertTrue(result.isSuccess());
        assertEquals(createItem(Connection.class, 1), session.getConnection());

        verify(console).writeLine("Connected to cluster '%s' (version %s)", WHITE, "clusterName1", "number1");
        verifyNoMoreInteractions(console);
    }

    @Test
    public void test_connect_to_connection_that_is_already_connected_to()
    {
        Session session = createItem(Session.class, 1);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(connectionManager.get(new ConnectionKey("my-connection"))).thenReturn(Optional.of(createItem(Connection.class, 1)));

        CommandResult result = shell().executeCommand("connect my-connection");
        assertTrue(result.isSuccess());

        verify(console).writeLine("Already on connection 'name1'", RED);
        verifyNoMoreInteractions(console);
    }

    @Test
    public void test_connect_to_non_existing_connection()
    {
        Session session = createItem(Session.class, 1);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        CommandResult result = shell().executeCommand("connect my-connection");
        assertTrue(result.isSuccess());

        verify(console).writeLine("Connection 'my-connection' does not exist", RED);
        verifyNoMoreInteractions(console);
    }
}
