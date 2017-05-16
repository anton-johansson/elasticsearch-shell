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
import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.domain.node.Node;
import com.antonjohansson.elasticsearchshell.session.SessionManager;

/**
 * Provides commands for various node-related queries.
 */
@Component
public class NodeCommands extends AbstractCommand
{
    private static final BigDecimal KILO = new BigDecimal("1000.00");

    private ClientFactory clientFactory;
    private SessionManager sessionManager;

    @Autowired
    void setClientFactory(ClientFactory clientFactory)
    {
        this.clientFactory = clientFactory;
    }

    @Autowired
    void setSessionManager(SessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    @CliAvailabilityIndicator({"node"})
    public boolean isConnected()
    {
        return sessionManager.getCurrentSession().getOptionalConnection().isPresent();
    }

    /**
     * Gets information and statistics about a node in the Elasticsearch cluster.
     */
    @CliCommand(value = "node", help = "Gets information and statistics about a node in the Elasticsearch cluster")
    public void node(@CliOption(key = {"", "name"}, help = "The name of the node", mandatory = true) String name)
    {
        command(() ->
        {
            Node node = clientFactory.getClient().getNodeInfo(name);
            String usedMemory = getHumanReadableBytes(node.getOperatingSystem().getMemory().getUsedInBytes());
            String freeMemory = getHumanReadableBytes(node.getOperatingSystem().getMemory().getFreeInBytes());

            console().writeLine("CPU usage: %d%%", WHITE, node.getOperatingSystem().getCpu().getPercentage());
            console().writeLine("Memory usage: %d%%", WHITE, node.getOperatingSystem().getMemory().getUsedPercentage());
            console().writeLine("Memory used: %s", WHITE, usedMemory);
            console().writeLine("Memory free: %s", WHITE, freeMemory);
        });
    }

    private String getHumanReadableBytes(BigInteger bytes)
    {
        BigDecimal kilobytes = new BigDecimal(bytes).divide(KILO);
        if (kilobytes.compareTo(ONE) < 0)
        {
            return bytes + " B";
        }
        BigDecimal megabytes = kilobytes.divide(KILO);
        if (megabytes.compareTo(ONE) < 0)
        {
            return kilobytes.setScale(2, HALF_UP).stripTrailingZeros().toPlainString() + " kB";
        }
        BigDecimal gigabytes = megabytes.divide(KILO);
        if (gigabytes.compareTo(ONE) < 0)
        {
            return megabytes.setScale(2, HALF_UP).stripTrailingZeros().toPlainString() + " MB";
        }
        return gigabytes.setScale(2, HALF_UP).stripTrailingZeros().toPlainString() + " GB";
    }
}
