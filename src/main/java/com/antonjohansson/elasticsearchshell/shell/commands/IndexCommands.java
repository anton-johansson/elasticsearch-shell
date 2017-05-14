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

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.client.Client;
import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.domain.Index;
import com.antonjohansson.elasticsearchshell.domain.IndexMappings;
import com.antonjohansson.elasticsearchshell.domain.IndexSettings;
import com.antonjohansson.elasticsearchshell.domain.index.IndexStatsContainer;
import com.antonjohansson.elasticsearchshell.index.IndexKey;
import com.antonjohansson.elasticsearchshell.session.Session;
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

    @CliAvailabilityIndicator({"current-index", "delete-index"})
    public boolean isIndexChosen()
    {
        return sessionManager.getCurrentSession().getCurrentIndex() != null;
    }

    /**
     * Selects an index to work with.
     */
    @CliCommand(value = "use", help = "Selects an index to work with")
    public void use(@CliOption(key = {"", "name"}, mandatory = true, help = "The name of the index to work with") IndexKey key)
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
     * Creates a new index.
     *
     * @param name The name of the index to create.
     * @param numberOfShards The number of shards of the new index.
     * @param numberOfReplicas The number of replicas of the new index.
     */
    @CliCommand(value = "create-index", help = "Creates a new index")
    public void create(
            @CliOption(key = "name", mandatory = true, help = "The name of the index to create") String name,
            @CliOption(key = "shards", unspecifiedDefaultValue = "1", help = "The number of shards of the index") int numberOfShards,
            @CliOption(key = "replicas", unspecifiedDefaultValue = "0", help = "The number of replicas of the index") int numberOfReplicas)
    {
        command(() ->
        {
            IndexSettings settings = new IndexSettings();
            settings.setNumberOfShards(numberOfShards);
            settings.setNumberOfReplicas(numberOfReplicas);

            Index index = new Index();
            index.setSettings(settings);

            boolean result = clientFactory.getClient()
                    .createIndex(name, index);

            if (!result)
            {
                throw new CommandException("Could not create index '%s'", name);
            }

            console().writeLine("Created index '%s'", WHITE, name);
        });
    }

    /**
     * Deletes the current index.
     */
    @CliCommand(value = "delete-index", help = "Deletes the currently used index")
    public void delete()
    {
        command(() ->
        {
            Session currentSession = sessionManager.getCurrentSession();
            String indexName = currentSession.getCurrentIndex().getName();

            IndexStatsContainer container = clientFactory.getClient()
                    .getIndexStats(indexName)
                    .orElseThrow(() -> new CommandException("Index with name '%s' was not found", indexName));

            int documentCount = container.getTotal().getDocuments().getCount();
            console().writeLine("The current index has %d documents", WHITE, documentCount);
            String input = console().readLine("Enter the name of the index to confirm deletion: ", WHITE);
            if (!input.equals(indexName))
            {
                throw new CommandException("The entered index name does not match the current index name");
            }

            boolean result = clientFactory.getClient().deleteIndex(indexName);
            if (result)
            {
                console().writeLine("Successfully removed index '%s'", WHITE, indexName);
                currentSession.setCurrentIndex(null);
            }
            else
            {
                throw new CommandException("Could not remove index '%s'", indexName);
            }
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
