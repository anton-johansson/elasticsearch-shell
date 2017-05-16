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

import static com.antonjohansson.elasticsearchshell.client.ClientTestData.ACTUAL_ALL_MAPPINGS;
import static com.antonjohansson.elasticsearchshell.domain.TestDataUtils.createItem;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.RED;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.WHITE;
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
import com.antonjohansson.elasticsearchshell.domain.Index;
import com.antonjohansson.elasticsearchshell.domain.IndexSettings;
import com.antonjohansson.elasticsearchshell.domain.index.IndexStatsContainer;
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
        when(client.createIndex("new-index", index())).thenReturn(true);
        when(client.getIndexStats("test-index")).thenReturn(Optional.of(createItem(IndexStatsContainer.class, 1)));
        when(client.deleteIndex("test-index")).thenReturn(true);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getOptionalConnection()).thenReturn(Optional.of(new Connection()));
        when(console.readLine("Enter the name of the index to confirm deletion: ", WHITE)).thenReturn("test-index");
    }

    private Index index()
    {
        IndexSettings settings = new IndexSettings();
        settings.setNumberOfReplicas(1);
        settings.setNumberOfShards(2);

        Index index = new Index();
        index.setSettings(settings);
        return index;
    }

    @Test
    public void test_use()
    {
        CommandResult result = shell().executeCommand("use my-index");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(client, clientFactory, console, session, sessionManager);
        inOrder.verify(clientFactory).getClient();
        inOrder.verify(client).getMappings();
        inOrder.verify(console).writeLine("Now using '%s'. Index has %d types.", WHITE, new IndexKey("my-index"), 2);
        inOrder.verify(sessionManager).getCurrentSession();
        inOrder.verify(session).setCurrentIndex(new IndexKey("my-index"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_use_when_not_connected()
    {
        when(session.getOptionalConnection()).thenReturn(Optional.empty());

        CommandResult result = shell().executeCommand("use use-index");
        assertFalse(result.isSuccess());
    }

    @Test
    public void test_use_non_existing()
    {
        CommandResult result = shell().executeCommand("use non-existing-index");
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

    @Test
    public void test_create()
    {
        CommandResult result = shell().executeCommand("create-index --name new-index --shards 2 --replicas 1");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(console, client);
        inOrder.verify(client).createIndex("new-index", index());
        inOrder.verify(console).writeLine("Created index '%s'", WHITE, "new-index");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_existing_create()
    {
        CommandResult result = shell().executeCommand("create-index --name existing-index --shards 2 --replicas 1");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(console, client);
        inOrder.verify(client).createIndex("existing-index", index());
        inOrder.verify(console).writeLine("Could not create index 'existing-index'", RED);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_deleteIndex()
    {
        when(session.getCurrentIndex()).thenReturn(new IndexKey("test-index"));

        CommandResult result = shell().executeCommand("delete-index");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(console, client);
        inOrder.verify(client).getIndexStats("test-index");
        inOrder.verify(console).writeLine("The current index has %d documents", WHITE, 1);
        inOrder.verify(console).readLine("Enter the name of the index to confirm deletion: ", WHITE);
        inOrder.verify(client).deleteIndex("test-index");
        inOrder.verify(console).writeLine("Successfully removed index '%s'", WHITE, "test-index");
        verifyNoMoreInteractions(console, client);
    }

    @Test
    public void test_deleteIndex_missing_index()
    {
        when(session.getCurrentIndex()).thenReturn(new IndexKey("missing-index"));

        CommandResult result = shell().executeCommand("delete-index");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(console, client);
        inOrder.verify(client).getIndexStats("missing-index");
        inOrder.verify(console).writeLine("Index with name 'missing-index' was not found", RED);
        verifyNoMoreInteractions(console, client);
    }

    @Test
    public void test_deleteIndex_bad_confirmation()
    {
        when(session.getCurrentIndex()).thenReturn(new IndexKey("test-index"));
        when(console.readLine("Enter the name of the index to confirm deletion: ", WHITE)).thenReturn("not-the-same-index-name");

        CommandResult result = shell().executeCommand("delete-index");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(console, client);
        inOrder.verify(client).getIndexStats("test-index");
        inOrder.verify(console).writeLine("The current index has %d documents", WHITE, 1);
        inOrder.verify(console).readLine("Enter the name of the index to confirm deletion: ", WHITE);
        inOrder.verify(console).writeLine("The entered index name does not match the current index name", RED);
        verifyNoMoreInteractions(console, client);
    }

    @Test
    public void test_deleteIndex_could_not_delete()
    {
        when(session.getCurrentIndex()).thenReturn(new IndexKey("test-index"));
        when(client.deleteIndex("test-index")).thenReturn(false);

        CommandResult result = shell().executeCommand("delete-index");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(console, client);
        inOrder.verify(client).getIndexStats("test-index");
        inOrder.verify(console).writeLine("The current index has %d documents", WHITE, 1);
        inOrder.verify(console).readLine("Enter the name of the index to confirm deletion: ", WHITE);
        inOrder.verify(client).deleteIndex("test-index");
        inOrder.verify(console).writeLine("Could not remove index 'test-index'", RED);
        verifyNoMoreInteractions(console, client);
    }
}
