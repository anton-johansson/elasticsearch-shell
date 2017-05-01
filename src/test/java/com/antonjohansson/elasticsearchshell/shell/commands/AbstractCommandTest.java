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

import java.lang.reflect.ParameterizedType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.Shell;

/**
 * Skeleton for tests for commands.
 *
 * @param <C> The type of the command to test.
 */
public abstract class AbstractCommandTest<C extends AbstractCommand> extends Assert
{
    private final Class<C> commandClass;
    private JLineShellComponent shell;
    private C command;

    @SuppressWarnings("unchecked")
    protected AbstractCommandTest()
    {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        this.commandClass = (Class<C>) type.getActualTypeArguments()[0];
    }

    @Before
    public final void setUpCore() throws Exception
    {
        String[] arguments = {"--disableInternalCommands"};
        Bootstrap bootstrap = new Bootstrap(arguments);
        shell = bootstrap.getJLineShellComponent();
        command = bootstrap.getApplicationContext().getBean(commandClass);

        MockitoAnnotations.initMocks(this);
        initMocks();
    }

    /**
     * Initializes mocks.
     */
    protected void initMocks()
    {
    }

    @After
    public final void tearDownCore()
    {
        shell.stop();
    }

    /**
     * Gets the {@link Shell}.
     *
     * @return Returns the shell.
     */
    protected JLineShellComponent shell()
    {
        return shell;
    }

    /**
     * Gets the command.
     *
     * @return Returns the command.
     */
    protected C command()
    {
        return command;
    }
}
