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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.shell.core.CommandResult;

import com.antonjohansson.elasticsearchshell.client.Client;
import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.domain.ClusterHealth;
import com.antonjohansson.elasticsearchshell.session.Session;
import com.antonjohansson.elasticsearchshell.session.SessionManager;
import com.antonjohansson.elasticsearchshell.shell.output.Console;

/**
 * Unit tests of {@link ClusterCommands}.
 */
public class ClusterCommandsTest extends AbstractCommandTest<ClusterCommands>
{
    private final Session sessionWithConnection;
    private @Mock Client client;
    private @Mock ClientFactory factory;
    private @Mock Console console;
    private @Mock SessionManager sessionManager;

    public ClusterCommandsTest()
    {
        sessionWithConnection = new Session();
        sessionWithConnection.setConnection(new Connection());
    }

    @Override
    protected void initMocks()
    {
        command().setFactory(factory);
        command().setConsole(console);
        command().setSessionManager(sessionManager);

        when(factory.getClient()).thenReturn(client);
        when(sessionManager.getCurrentSession()).thenReturn(sessionWithConnection);
    }

    private ClusterHealth health(String status)
    {
        ClusterHealth health = new ClusterHealth();
        health.setStatus(status);
        health.setNumberOfNodes(2);
        health.setNumberOfDataNodes(1);
        return health;
    }

    @Test
    public void test_green_health()
    {
        when(client.getClusterHealth()).thenReturn(health("green"));

        CommandResult result = shell().executeCommand("health");
        assertTrue(result.isSuccess());

        verify(console).writeLine("The cluster status is '%s' and it has %d nodes (where %d node(s) are data nodes)", "\u001B[32mgreen\u001B[0m", 2, 1);
    }

    @Test
    public void test_yellow_health()
    {
        when(client.getClusterHealth()).thenReturn(health("yellow"));

        CommandResult result = shell().executeCommand("health");
        assertTrue(result.isSuccess());

        verify(console).writeLine("The cluster status is '%s' and it has %d nodes (where %d node(s) are data nodes)", "\u001B[33myellow\u001B[0m", 2, 1);
    }

    @Test
    public void test_red_health()
    {
        when(client.getClusterHealth()).thenReturn(health("red"));

        CommandResult result = shell().executeCommand("health");
        assertTrue(result.isSuccess());

        verify(console).writeLine("The cluster status is '%s' and it has %d nodes (where %d node(s) are data nodes)", "\u001B[31mred\u001B[0m", 2, 1);
    }
}
