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

import static com.antonjohansson.elasticsearchshell.client.ClientTestData.ACK_FALSE;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.ACK_TRUE;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.ALL_INDICES_AND_MAPPINGS;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.CLUSTER_HEALTH;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.CLUSTER_INFO;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.INDEX;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.INDEX_STATS;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.NODE_STATS;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.PORT;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.connection;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

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
import com.antonjohansson.elasticsearchshell.domain.index.IndexStats;
import com.antonjohansson.elasticsearchshell.domain.index.IndexStatsContainer;
import com.antonjohansson.elasticsearchshell.domain.node.Node;

/**
 * Unit tests of {@link Client}.
 */
public class ClientTest extends Assert
{
    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int NOT_FOUND = 404;
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

        // Create index
        server.when(request().withMethod("PUT").withPath("/my-new-index").withBody(INDEX)).respond(response(OK).withBody(ACK_TRUE));
        server.when(request().withMethod("PUT").withPath("/not-acknowledged-index")).respond(response(OK).withBody(ACK_FALSE));
        server.when(request().withMethod("PUT").withPath("/existing-index")).respond(response(BAD_REQUEST));

        // Delete index
        server.when(request().withMethod("DELETE").withPath("/my-index")).respond(response(OK).withBody(ACK_TRUE));
        server.when(request().withMethod("DELETE").withPath("/not-existing-index")).respond(response(NOT_FOUND));

        // Statistics
        server.when(request().withMethod("GET").withPath("/_nodes/stats")).respond(response(OK).withBody(NODE_STATS));
        server.when(request().withMethod("GET").withPath("/test-index/_stats")).respond(response(OK).withBody(INDEX_STATS));

        // Cluster
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

    @Test
    public void test_deleteIndex()
    {
        boolean result = client.deleteIndex("my-index");
        assertTrue(result);
    }

    @Test
    public void test_deleteIndex_non_existing()
    {
        boolean result = client.deleteIndex("non-existing-index");
        assertFalse(result);
    }

    @Test
    public void test_getIndexStats()
    {
        IndexStats stats = new IndexStats();
        stats.getDocuments().setCount(18);
        stats.getDocuments().setDeleted(1);

        IndexStatsContainer actual = client.getIndexStats("test-index").get();
        IndexStatsContainer expected = new IndexStatsContainer();
        expected.setPrimaries(stats);
        expected.setTotal(stats);

        assertEquals(expected, actual);
    }

    @Test
    public void test_getIndexStats_missing_index()
    {
        Optional<IndexStatsContainer> result = client.getIndexStats("non-existing-index");
        assertFalse(result.isPresent());
    }

    @Test
    public void test_getNodeInfo()
    {
        Node actual = client.getNodeInfo("node1");
        Node expected = new Node();
        expected.setName("node1");
        expected.getOperatingSystem().getCpu().setPercentage(3);
        expected.getOperatingSystem().getMemory().setUsedPercentage(86);
        expected.getOperatingSystem().getMemory().setFreePercentage(14);
        expected.getOperatingSystem().getMemory().setTotalInBytes(new BigInteger("8243830784"));
        expected.getOperatingSystem().getMemory().setUsedInBytes(new BigInteger("7107895296"));
        expected.getOperatingSystem().getMemory().setFreeInBytes(new BigInteger("1135935488"));

        assertEquals(expected, actual);
    }
}
