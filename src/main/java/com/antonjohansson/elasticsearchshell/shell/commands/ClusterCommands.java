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

import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.GREEN;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.RED;
import static com.antonjohansson.elasticsearchshell.shell.output.ConsoleColor.YELLOW;
import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private static final Map<String, ConsoleColor> STATUS_TO_COLOR_MAP = getStatusToColorMap();

    private SessionManager sessionManager;
    private ClientFactory factory;

    private static Map<String, ConsoleColor> getStatusToColorMap()
    {
        Map<String, ConsoleColor> map = new HashMap<>();
        map.put("green", GREEN);
        map.put("yellow", YELLOW);
        map.put("red", RED);
        return unmodifiableMap(map);
    }

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
            ConsoleColor colorOfStatus = Optional.ofNullable(STATUS_TO_COLOR_MAP.get(health.getStatus())).orElse(RED);
            String status = colorOfStatus.format(health.getStatus());
            console().writeLine("The cluster status is '%s' and it has %d nodes (where %d node(s) are data nodes)", status, health.getNumberOfNodes(), health.getNumberOfDataNodes());
        });
    }
}
