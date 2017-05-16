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
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.GREEN;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.RED;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.WHITE;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.shell.core.CommandResult;

import com.antonjohansson.elasticsearchshell.session.Session;
import com.antonjohansson.elasticsearchshell.session.SessionKey;
import com.antonjohansson.elasticsearchshell.session.SessionManager;
import com.antonjohansson.elasticsearchshell.shell.output.Console;

/**
 * Unit tests of {@link SessionCommands}.
 */
public class SessionCommandsTest extends AbstractCommandTest<SessionCommands>
{
    private @Mock Console console;
    private @Mock SessionManager manager;

    @Override
    protected void initMocks()
    {
        command().setConsole(console);
        command().setManager(manager);

        when(manager.getSessionNames()).thenReturn(asList("default", "name1", "session1", "session2"));
        when(manager.getCurrentSession()).thenReturn(createItem(Session.class, 1));
        when(manager.setCurrentSession(new SessionKey("default"))).thenReturn(true);
        when(manager.remove(new SessionKey("default"))).thenReturn(true);
        when(manager.newSession("new-session")).thenReturn(true);
    }

    @Test
    public void test_add()
    {
        CommandResult result = shell().executeCommand("session-add new-session");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder();
        inOrder.verify(manager).newSession("new-session");
        inOrder.verify(console).writeLine("Switched to session '%s'", WHITE, "name1");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_add_without_name()
    {
        CommandResult result = shell().executeCommand("session-add");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder();
        inOrder.verify(manager).newSession();
        inOrder.verify(console).writeLine("Switched to session '%s'", WHITE, "name1");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_add_with_name_conflict()
    {
        CommandResult result = shell().executeCommand("session-add default");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder();
        inOrder.verify(manager).newSession("default");
        inOrder.verify(console).writeLine("Session 'default' already exists", RED);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_remove()
    {
        CommandResult result = shell().executeCommand("session-remove default");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder();
        inOrder.verify(manager).remove(new SessionKey("default"));
        inOrder.verify(console).writeLine("Removed session '%s'", WHITE, new SessionKey("default"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_remove_to_non_existing_session()
    {
        CommandResult result = shell().executeCommand("session-remove non-existing");
        assertTrue(result.isSuccess());

        verify(console).writeLine("Session 'non-existing' does not exist", RED);
        verifyNoMoreInteractions(console);
    }

    @Test
    public void test_remove_to_current_session()
    {
        CommandResult result = shell().executeCommand("session-remove name1");
        assertTrue(result.isSuccess());

        verify(console).writeLine("You can't remove the session you are currently working with", RED);
        verifyNoMoreInteractions(console);
    }

    @Test
    public void test_change()
    {
        CommandResult result = shell().executeCommand("session default");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder();
        inOrder.verify(manager).setCurrentSession(new SessionKey("default"));
        inOrder.verify(console).writeLine("Switched to '%s'", WHITE, new SessionKey("default"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void test_change_to_non_existing_session()
    {
        CommandResult result = shell().executeCommand("session non-existing");
        assertTrue(result.isSuccess());

        verify(console).writeLine("Session 'non-existing' does not exist", RED);
        verifyNoMoreInteractions(console);
    }

    @Test
    public void test_change_to_current_session()
    {
        CommandResult result = shell().executeCommand("session name1");
        assertTrue(result.isSuccess());

        verify(console).writeLine("You are already on session 'name1'", RED);
        verifyNoMoreInteractions(console);
    }

    @Test
    public void test_list()
    {
        CommandResult result = shell().executeCommand("sessions");
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder();
        inOrder.verify(manager).getSessionNames();
        inOrder.verify(console).writeLine("default", WHITE);
        inOrder.verify(console).writeLine("name1", GREEN);
        inOrder.verify(console).writeLine("session1", WHITE);
        inOrder.verify(console).writeLine("session2", WHITE);
        inOrder.verifyNoMoreInteractions();
    }

    private InOrder inOrder()
    {
        return Mockito.inOrder(console, manager);
    }
}
