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
package com.antonjohansson.elasticsearchshell.client;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.client.WebClient;

import com.antonjohansson.elasticsearchshell.common.ElasticsearchException;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.domain.Acknowledgement;
import com.antonjohansson.elasticsearchshell.domain.ClusterHealth;
import com.antonjohansson.elasticsearchshell.domain.ClusterInfo;
import com.antonjohansson.elasticsearchshell.domain.Index;
import com.antonjohansson.elasticsearchshell.domain.IndexMappings;
import com.antonjohansson.elasticsearchshell.domain.index.IndexStatsContainer;
import com.antonjohansson.elasticsearchshell.domain.index.IndexStatsResult;
import com.antonjohansson.elasticsearchshell.domain.node.Node;
import com.antonjohansson.elasticsearchshell.domain.node.NodesInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Function;

/**
 * Defines a client that can communicate with an Elasticsearch server.
 */
public class Client
{
    private static final ObjectMapper MAPPER = getMapper();
    private static final int UNAUTHORIZED = 401;
    private final Connection connection;
    private final PasswordEncrypter passwordEncrypter;

    Client(Connection connection, PasswordEncrypter passwordEncrypter)
    {
        this.connection = connection;
        this.passwordEncrypter = passwordEncrypter;
    }

    private static ObjectMapper getMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    Connection getConnection()
    {
        return connection;
    }

    private WebClient client()
    {
        String baseURL = connection.getURL();
        JacksonJsonProvider provider = new JacksonJsonProvider(MAPPER);

        WebClient client = WebClient.create(baseURL, asList(provider))
                .accept(APPLICATION_JSON_TYPE)
                .type(APPLICATION_JSON_TYPE);

        if (!isBlank(connection.getUsername()))
        {
            String decryptedPassword = passwordEncrypter.decrypt(connection.getUsername(), connection.getPassword());
            String authorizationString = connection.getUsername() + ":" + decryptedPassword;
            String authorization = "Basic " + Base64.getEncoder().encodeToString(authorizationString.getBytes());
            client.header("Authorization", authorization);
        }

        return client;
    }

    /**
     * Gets information about the Elasticsearch cluster.
     *
     * @return Returns the information about the cluster.
     */
    public ClusterInfo getClusterInfo()
    {
        return execute(client -> client.get(ClusterInfo.class));
    }

    /**
     * Gets the health of the cluster.
     *
     * @return Returns the cluster health.
     */
    public ClusterHealth getClusterHealth()
    {
        return execute(client -> client.path("/_cluster/health").get(ClusterHealth.class));
    }

    /**
     * Gets all indices and their mappings.
     *
     * @return Returns the mappings.
     */
    public Map<String, IndexMappings> getMappings()
    {
        GenericType<Map<String, IndexMappings>> responseType = new GenericType<Map<String, IndexMappings>>()
        {
        };

        return execute(client -> client.path("/_mappings").get(responseType));
    }

    /**
     * Creates a new index with the given settings.
     *
     * @param name The name of the index to create.
     * @param index The definition of the index.
     * @return Returns whether or not the index could be created.
     */
    public boolean createIndex(String name, Index index)
    {
        Acknowledgement acknowledgement = execute(client ->
        {
            Response response = client()
                    .path("/{indexName}", name)
                    .put(index);

            if (response.getStatus() == Status.OK.getStatusCode())
            {
                return response.readEntity(Acknowledgement.class);
            }

            return new Acknowledgement();
        });

        return acknowledgement.isAcknowledged();
    }

    /**
     * Deletes the index with the given name.
     *
     * @param name The name of the index to delete.
     * @return Returns whether or not the index could be deleted.
     */
    public boolean deleteIndex(String name)
    {
        Acknowledgement acknowledgement = execute(client ->
        {
            Response response = client
                .path("/{indexName}", name)
                .delete();

            if (response.getStatus() == Status.OK.getStatusCode())
            {
                return response.readEntity(Acknowledgement.class);
            }

            return new Acknowledgement();
        });

        return acknowledgement.isAcknowledged();
    }

    /**
     * Gets the statistics of a specific index, with the given name.
     *
     * @param indexName The name of the index to get statistics for.
     * @return Returns the index statistics.
     */
    public Optional<IndexStatsContainer> getIndexStats(String indexName)
    {
        IndexStatsResult result = execute(client -> client.path("/{indexName}/_stats", indexName).get(IndexStatsResult.class));
        IndexStatsContainer stats = result.getIndices().get(indexName);
        return Optional.ofNullable(stats);
    }

    /**
     * Gets information about a specific node.
     *
     * @param name The name of the node to get information about.
     * @return Returns the {@link Node node}.
     */
    public Node getNodeInfo(String name)
    {
        NodesInfo info = execute(client -> client.path("/_nodes/stats").get(NodesInfo.class));
        return info.getNodeByName(name);
    }

    private <T> T execute(Function<WebClient, T> mapper)
    {
        try
        {
            WebClient client = client();
            return mapper.apply(client);
        }
        catch (WebApplicationException e)
        {
            if (e.getResponse().getStatus() == UNAUTHORIZED)
            {
                throw new ElasticsearchException("Bad credentials");
            }
            throw new ElasticsearchException("Unknown error received from the server");
        }
    }
}
