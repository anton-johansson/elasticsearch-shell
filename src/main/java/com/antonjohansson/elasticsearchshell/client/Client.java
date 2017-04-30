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

import javax.ws.rs.WebApplicationException;

import org.apache.cxf.jaxrs.client.WebClient;

import com.antonjohansson.elasticsearchshell.common.ElasticsearchException;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.connection.PasswordEncrypter;
import com.antonjohansson.elasticsearchshell.domain.ClusterInfo;
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

    Client(Connection connection)
    {
        this.connection = connection;
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
            String decryptedPassword = PasswordEncrypter.decrypt(connection.getUsername(), connection.getPassword());
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
