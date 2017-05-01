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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.domain.ClusterHealth;
import com.antonjohansson.elasticsearchshell.session.SessionManager;
import com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor;

/**
 * Provides commands for managing the cluster.
 */
@Component
public class ClusterCommands extends AbstractCommand
{
    private SessionManager sessionManager;
    private ClientFactory factory;

    @Autowired
    void setSessionManager(SessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    @Autowired
    void setFactory(ClientFactory factory)
    {
        this.factory = factory;
    }

    /** Indicates whether or not the current session is connected. */
    @CliAvailabilityIndicator("health")
    public boolean isConnected()
    {
        return sessionManager.getCurrentSession().getOptionalConnection().isPresent();
    }

    /** Prints the cluster health. */
    @CliCommand(value = "health", help = "Prints the health of the cluster that is currently connected to")
    public void health()
    {
        command(() ->
        {
            ClusterHealth health = factory.getClient().getClusterHealth();
            ConsoleColor colorOfStatus = getColorOfStatus(health.getStatus());
            String status = colorOfStatus.format(health.getStatus());
            console().writeLine("The cluster status is '%s' and it has %d nodes (where %d node(s) are data nodes)", status, health.getNumberOfNodes(), health.getNumberOfDataNodes());
        });
    }

    private ConsoleColor getColorOfStatus(String status)
    {
        switch (status)
        {
            case "green":
                return ConsoleColor.GREEN;
            case "yellow":
                return ConsoleColor.YELLOW;
            default:
                return ConsoleColor.RED;
        }
    }
}
