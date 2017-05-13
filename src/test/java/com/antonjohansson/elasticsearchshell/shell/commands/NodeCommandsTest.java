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

import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.WHITE;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.shell.core.CommandResult;

import com.antonjohansson.elasticsearchshell.client.Client;
import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.domain.node.Node;
import com.antonjohansson.elasticsearchshell.domain.node.NodeCPU;
import com.antonjohansson.elasticsearchshell.domain.node.NodeMemory;
import com.antonjohansson.elasticsearchshell.domain.node.NodeOperatingSystem;
import com.antonjohansson.elasticsearchshell.session.Session;
import com.antonjohansson.elasticsearchshell.session.SessionManager;
import com.antonjohansson.elasticsearchshell.shell.output.Console;

/**
 * Unit tests of {@link NodeCommands}.
 */
public class NodeCommandsTest extends AbstractCommandTest<NodeCommands>
{
    private @Mock Client client;
    private @Mock ClientFactory clientFactory;
    private @Mock Console console;
    private @Mock SessionManager sessionManager;

    @Override
    protected void initMocks()
    {
        command().setClientFactory(clientFactory);
        command().setConsole(console);
        command().setSessionManager(sessionManager);

        Session session = new Session();
        session.setConnection(new Connection());

        when(clientFactory.getClient()).thenReturn(client);
        when(sessionManager.getCurrentSession()).thenReturn(session);
    }

    private Node node(String multiplier)
    {
        NodeCPU cpu = new NodeCPU();
        cpu.setPercentage(55);

        NodeMemory memory = new NodeMemory();
        memory.setUsedPercentage(60);
        memory.setFreePercentage(40);
        memory.setTotalInBytes(new BigInteger("200").multiply(new BigInteger(multiplier)));
        memory.setUsedInBytes(new BigInteger("120").multiply(new BigInteger(multiplier)));
        memory.setFreeInBytes(new BigInteger("80").multiply(new BigInteger(multiplier)));

        NodeOperatingSystem operatingSystem = new NodeOperatingSystem();
        operatingSystem.setCpu(cpu);
        operatingSystem.setMemory(memory);

        Node node = new Node();
        node.setOperatingSystem(operatingSystem);
        return node;
    }

    @Test
    public void test_node_when_not_connected()
    {
        when(sessionManager.getCurrentSession()).thenReturn(null);

        CommandResult result = shell().executeCommand("node node1");
        assertFalse(result.isSuccess());

        verify(sessionManager).getCurrentSession();
        verifyNoMoreInteractions(client, clientFactory, console, sessionManager);
    }

    @Test
    public void test_node_bytes()
    {
        Node node = node("1");
        when(client.getNodeInfo("node1")).thenReturn(node);

        CommandResult result = shell().executeCommand("node node1");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(client, clientFactory, console, sessionManager);
        inOrder.verify(sessionManager).getCurrentSession();
        inOrder.verify(clientFactory).getClient();
        inOrder.verify(client).getNodeInfo("node1");
        inOrder.verify(console).writeLine("CPU usage: %d%%", WHITE, 55);
        inOrder.verify(console).writeLine("Memory usage: %d%%", WHITE, 60);
        inOrder.verify(console).writeLine("Memory used: %s", WHITE, "120 B");
        inOrder.verify(console).writeLine("Memory free: %s", WHITE, "80 B");
        verifyNoMoreInteractions(client, clientFactory, console, sessionManager);
    }

    @Test
    public void test_node_kilobytes()
    {
        Node node = node("1000");
        when(client.getNodeInfo("node1")).thenReturn(node);

        CommandResult result = shell().executeCommand("node node1");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(client, clientFactory, console, sessionManager);
        inOrder.verify(sessionManager).getCurrentSession();
        inOrder.verify(clientFactory).getClient();
        inOrder.verify(client).getNodeInfo("node1");
        inOrder.verify(console).writeLine("CPU usage: %d%%", WHITE, 55);
        inOrder.verify(console).writeLine("Memory usage: %d%%", WHITE, 60);
        inOrder.verify(console).writeLine("Memory used: %s", WHITE, "120 kB");
        inOrder.verify(console).writeLine("Memory free: %s", WHITE, "80 kB");
        verifyNoMoreInteractions(client, clientFactory, console, sessionManager);
    }

    @Test
    public void test_node_megabytes()
    {
        Node node = node("1000000");
        when(client.getNodeInfo("node1")).thenReturn(node);

        CommandResult result = shell().executeCommand("node node1");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(client, clientFactory, console, sessionManager);
        inOrder.verify(sessionManager).getCurrentSession();
        inOrder.verify(clientFactory).getClient();
        inOrder.verify(client).getNodeInfo("node1");
        inOrder.verify(console).writeLine("CPU usage: %d%%", WHITE, 55);
        inOrder.verify(console).writeLine("Memory usage: %d%%", WHITE, 60);
        inOrder.verify(console).writeLine("Memory used: %s", WHITE, "120 MB");
        inOrder.verify(console).writeLine("Memory free: %s", WHITE, "80 MB");
        verifyNoMoreInteractions(client, clientFactory, console, sessionManager);
    }

    @Test
    public void test_node_gigabytes()
    {
        Node node = node("1000000000");
        when(client.getNodeInfo("node1")).thenReturn(node);

        CommandResult result = shell().executeCommand("node node1");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(client, clientFactory, console, sessionManager);
        inOrder.verify(sessionManager).getCurrentSession();
        inOrder.verify(clientFactory).getClient();
        inOrder.verify(client).getNodeInfo("node1");
        inOrder.verify(console).writeLine("CPU usage: %d%%", WHITE, 55);
        inOrder.verify(console).writeLine("Memory usage: %d%%", WHITE, 60);
        inOrder.verify(console).writeLine("Memory used: %s", WHITE, "120 GB");
        inOrder.verify(console).writeLine("Memory free: %s", WHITE, "80 GB");
        verifyNoMoreInteractions(client, clientFactory, console, sessionManager);
    }
}
