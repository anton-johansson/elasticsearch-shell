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

import static com.antonjohansson.elasticsearchshell.client.ClientTestData.ACTUAL_ALL_MAPPINGS;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.RED;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.shell.core.CommandResult;

import com.antonjohansson.elasticsearchshell.client.Client;
import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.index.IndexKey;
import com.antonjohansson.elasticsearchshell.session.Session;
import com.antonjohansson.elasticsearchshell.session.SessionManager;
import com.antonjohansson.elasticsearchshell.shell.output.Console;

/**
 * Unit tests of {@link IndexCommands}.
 */
public class IndexCommandsTest extends AbstractCommandTest<IndexCommands>
{
    private @Mock Client client;
    private @Mock ClientFactory clientFactory;
    private @Mock Console console;
    private @Mock Session session;
    private @Mock SessionManager sessionManager;

    @Override
    protected void initMocks()
    {
        command().setClientFactory(clientFactory);
        command().setConsole(console);
        command().setSessionManager(sessionManager);

        when(clientFactory.getClient()).thenReturn(client);
        when(client.getMappings()).thenReturn(ACTUAL_ALL_MAPPINGS);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getOptionalConnection()).thenReturn(Optional.of(new Connection()));
    }

    @Test
    public void test_index()
    {
        CommandResult result = shell().executeCommand("index my-index");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(client, clientFactory, console, session, sessionManager);
        inOrder.verify(clientFactory).getClient();
        inOrder.verify(client).getMappings();
        inOrder.verify(console).writeLine("Now using '%s'. Index has %d types.", new IndexKey("my-index"), 2);
        inOrder.verify(sessionManager).getCurrentSession();
        inOrder.verify(session).setCurrentIndex(new IndexKey("my-index"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_index_when_not_connected()
    {
        when(session.getOptionalConnection()).thenReturn(Optional.empty());

        CommandResult result = shell().executeCommand("index my-index");
        assertFalse(result.isSuccess());
    }

    @Test
    public void test_index_non_existing()
    {
        CommandResult result = shell().executeCommand("index non-existing-index");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(client, clientFactory, console, session, sessionManager);
        inOrder.verify(clientFactory).getClient();
        inOrder.verify(client).getMappings();
        inOrder.verify(console).writeLine("No index named 'non-existing-index' was found", RED);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_current_index()
    {
        when(session.getCurrentIndex()).thenReturn(new IndexKey("my-index"));

        CommandResult result = shell().executeCommand("current-index");
        assertTrue(result.isSuccess());

        verify(console).writeLine("Currently on index '%s'", new IndexKey("my-index"));
        verifyNoMoreInteractions(console);
    }

    @Test
    public void test_current_index_when_not_using_one()
    {
        CommandResult result = shell().executeCommand("current-index");
        assertFalse(result.isSuccess());
    }
}
