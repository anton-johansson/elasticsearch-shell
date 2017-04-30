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

import static com.antonjohansson.elasticsearchshell.client.ClientTestData.CLUSTER_INFO;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.PORT;
import static com.antonjohansson.elasticsearchshell.client.ClientTestData.connection;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.Base64;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;

import com.antonjohansson.elasticsearchshell.common.ElasticsearchException;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.connection.PasswordEncrypter;
import com.antonjohansson.elasticsearchshell.domain.ClusterInfo;
import com.antonjohansson.elasticsearchshell.domain.ClusterInfo.Version;

/**
 * Unit tests of {@link Client}.
 */
public class ClientTest extends Assert
{
    private static final int OK = 200;
    private static final int UNAUTHORIZED = 401;
    private static final int SERVER_ERROR = 500;
    private static final String JSON = "application/json";
    private final Client client = new Client(connection());
    private ClientAndServer server;

    @Before
    public void setUp()
    {
        server = startClientAndServer(PORT);
        server.when(request().withHeader("Authorization", authorization("server-error"))).respond(response().withStatusCode(SERVER_ERROR));
        server.when(request().withHeader("Authorization", authorization("bad-password"))).respond(response().withStatusCode(UNAUTHORIZED));
        server.when(request().withHeader("Authorization", authorization("ok-password"))).respond(response().withStatusCode(OK).withHeader("Content-Type", JSON).withBody("{}"));
        server.when(request().withHeader("Accept", JSON).withMethod("GET")).respond(response().withStatusCode(OK).withHeader("Content-Type", JSON).withBody(CLUSTER_INFO));
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
    public void test_with_bad_credentials()
    {
        Connection connection = connection();
        connection.setUsername("elastic");
        connection.setPassword(PasswordEncrypter.encrypt("elastic", "bad-password"));

        Client client = new Client(connection);

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
        connection.setPassword(PasswordEncrypter.encrypt("elastic", "ok-password"));

        Client client = new Client(connection);
        ClusterInfo clusterInfo = client.getClusterInfo();
        assertNotNull(clusterInfo);
    }

    @Test
    public void test_when_unknown_server_error()
    {
        Connection connection = connection();
        connection.setUsername("elastic");
        connection.setPassword(PasswordEncrypter.encrypt("elastic", "server-error"));

        Client client = new Client(connection);

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
}
