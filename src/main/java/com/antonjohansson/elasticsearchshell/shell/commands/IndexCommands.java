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

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.client.Client;
import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.domain.IndexMappings;
import com.antonjohansson.elasticsearchshell.index.IndexKey;
import com.antonjohansson.elasticsearchshell.session.SessionManager;

/**
 * Provides commands for managing types in Elasticsearch.
 */
@Component
public class IndexCommands extends AbstractCommand
{
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

    @CliAvailabilityIndicator({"index"})
    public boolean isConnected()
    {
        return sessionManager.getCurrentSession().getOptionalConnection().isPresent();
    }

    @CliAvailabilityIndicator({"current-index"})
    public boolean isIndexChosen()
    {
        return sessionManager.getCurrentSession().getCurrentIndex() != null;
    }

    /**
     * Selects an index to work with.
     */
    @CliCommand(value = "index", help = "Selects an index to work with")
    public void index(@CliOption(key = {"", "name"}, mandatory = true, help = "The name of the index to work with") IndexKey key)
    {
        command(() ->
        {
            IndexMappings indexMappings = Optional.of(clientFactory.getClient())
                    .map(Client::getMappings)
                    .map(mappings -> mappings.get(key.getName()))
                    .orElseThrow(() -> new CommandException("No index named '%s' was found", key));

            console().writeLine("Now using '%s'. Index has %d types.", key, indexMappings.getMappings().size());

            sessionManager.getCurrentSession().setCurrentIndex(key);
        });
    }

    /**
     * Shows the current index.
     */
    @CliCommand(value = "current-index", help = "Shows the current index")
    public void currentIndex()
    {
        command(() ->
        {
            IndexKey currentIndex = sessionManager.getCurrentSession().getCurrentIndex();
            console().writeLine("Currently on index '%s'", currentIndex);
        });
    }
}
