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

import static com.antonjohansson.elasticsearchshell.client.ClientTestData.ALL_INDICES_AND_MAPPINGS;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.CLUSTER_HEALTH;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.CLUSTER_INFO;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.INDEX;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.INDEX_ACK;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.INDEX_NO_ACK;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.PORT;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.connection;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import java.util.Base64;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import com.antonjohansson.elasticsearchshell.common.ElasticsearchException;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.domain.ClusterHealth;
import com.antonjohansson.elasticsearchshell.domain.ClusterInfo;
import com.antonjohansson.elasticsearchshell.domain.ClusterInfo.Version;
import com.antonjohansson.elasticsearchshell.domain.Index;
import com.antonjohansson.elasticsearchshell.domain.IndexMappings;
import com.antonjohansson.elasticsearchshell.domain.IndexSettings;

/**
 * Unit tests of {@link Client}.
 */
public class ClientTest extends Assert
{
    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int SERVER_ERROR = 500;
    private static final String JSON = "application/json";

    private ClientAndServer server;
    private Client client;
    private @Mock PasswordEncrypter passwordEncrypter;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        client = new Client(connection(), passwordEncrypter);
        when(passwordEncrypter.decrypt("elastic", "encrypted-server-error")).thenReturn("server-error");
        when(passwordEncrypter.decrypt("elastic", "encrypted-bad-password")).thenReturn("bad-password");
        when(passwordEncrypter.decrypt("elastic", "encrypted-ok-password")).thenReturn("ok-password");

        server = startClientAndServer(PORT);
        server.when(request().withHeader("Authorization", authorization("server-error"))).respond(response(SERVER_ERROR));
        server.when(request().withHeader("Authorization", authorization("bad-password"))).respond(response(UNAUTHORIZED));
        server.when(request().withHeader("Authorization", authorization("ok-password"))).respond(response(OK).withBody("{}"));
        server.when(request().withMethod("GET").withPath("/_cluster/health")).respond(response(OK).withBody(CLUSTER_HEALTH));
        server.when(request().withMethod("GET").withPath("/_mappings")).respond(response(OK).withBody(ALL_INDICES_AND_MAPPINGS));
        server.when(request().withMethod("PUT").withPath("/my-new-index").withBody(INDEX)).respond(response(OK).withBody(INDEX_ACK));
        server.when(request().withMethod("PUT").withPath("/not-acknowledged-index")).respond(response(OK).withBody(INDEX_NO_ACK));
        server.when(request().withMethod("PUT").withPath("/existing-index")).respond(response(BAD_REQUEST));
        server.when(request().withMethod("GET")).respond(response(OK).withBody(CLUSTER_INFO));
    }

    private HttpRequest request()
    {
        return HttpRequest.request().withHeader("Accept", JSON).withHeader("Content-Type", JSON);
    }

    private HttpResponse response(int statusCode)
    {
        return HttpResponse.response().withStatusCode(statusCode).withHeader("Content-Type", JSON);
    }

    private String authorization(String password)
    {
        return "Basic " + Base64.getEncoder().encodeToString(("elastic:" + password).getBytes());
    }

    @After
    public void tearDown()
    {
        server.stop();
    }

    @Test
    public void test_getConnection()
    {
        assertEquals(connection(), client.getConnection());
    }

    @Test
    public void test_with_bad_credentials()
    {
        Connection connection = connection();
        connection.setUsername("elastic");
        connection.setPassword("encrypted-bad-password");

        Client client = new Client(connection, passwordEncrypter);

        try
        {
            client.getClusterInfo();
            fail("Exception expected");
        }
        catch (ElasticsearchException e)
        {
            assertEquals("Bad credentials", e.getMessage());
        }
    }

    @Test
    public void test_with_ok_credentials()
    {
        Connection connection = connection();
        connection.setUsername("elastic");
        connection.setPassword("encrypted-ok-password");

        Client client = new Client(connection, passwordEncrypter);

        ClusterInfo actual = client.getClusterInfo();
        ClusterInfo expected = new ClusterInfo();

        assertEquals(expected, actual);
    }

    @Test
    public void test_when_unknown_server_error()
    {
        Connection connection = connection();
        connection.setUsername("elastic");
        connection.setPassword("encrypted-server-error");

        Client client = new Client(connection, passwordEncrypter);

        try
        {
            client.getClusterInfo();
            fail("Exception expected");
        }
        catch (ElasticsearchException e)
        {
            assertEquals("Unknown error received from the server", e.getMessage());
        }
    }

    @Test
    public void test_getClusterInfo()
    {
        Version version = new Version();
        version.setNumber("5.3.0");

        ClusterInfo expected = new ClusterInfo();
        expected.setClusterName("my-test-cluster");
        expected.setVersion(version);

        ClusterInfo actual = client.getClusterInfo();

        assertEquals(expected, actual);
    }

    @Test
    public void test_getClusterHealth()
    {
        ClusterHealth expected = new ClusterHealth();
        expected.setClusterName("my-test-cluster");
        expected.setStatus("green");
        expected.setNumberOfNodes(1);
        expected.setNumberOfDataNodes(1);

        ClusterHealth actual = client.getClusterHealth();

        assertEquals(expected, actual);
    }

    @Test
    public void test_getMappings()
    {
        Map<String, IndexMappings> actual = client.getMappings();
        Map<String, IndexMappings> expected = ClientTestData.ACTUAL_ALL_MAPPINGS;

        assertEquals(expected, actual);
    }

    @Test
    public void test_createIndex()
    {
        IndexSettings settings = new IndexSettings();
        settings.setNumberOfReplicas(1);
        settings.setNumberOfShards(2);

        Index index = new Index();
        index.setSettings(settings);

        boolean result = client.createIndex("my-new-index", index);
        assertTrue(result);
    }

    @Test
    public void test_createIndex_not_acknowledged()
    {
        boolean result = client.createIndex("not-acknowledged-index", new Index());
        assertFalse(result);
    }

    @Test
    public void test_createIndex_with_error()
    {
        boolean result = client.createIndex("existing-index", new Index());
        assertFalse(result);
    }
}
