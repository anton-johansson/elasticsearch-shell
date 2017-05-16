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
package com.antonjohansson.elasticsearchshell.client;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.antonjohansson.elasticsearchshell.common.ElasticsearchException;
import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.session.Session;
import com.antonjohansson.elasticsearchshell.session.SessionManager;

/**
 * Unit tests of {@link ClientFactory}.
 */
public class ClientFactoryTest extends Assert
{
    private static final Connection CONNECTION = new Connection();

    private @Mock SessionManager sessionManager;
    private ClientFactory factory;
    private Session sessionWithoutConnection;
    private Session sessionWithConnection;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        factory = new ClientFactory(sessionManager);

        sessionWithoutConnection = new Session();
        sessionWithConnection = new Session();
        sessionWithConnection.setConnection(CONNECTION);
    }

    @Test
    public void test_get_client_for_session_without_connection()
    {
        when(sessionManager.getCurrentSession()).thenReturn(sessionWithoutConnection);

        try
        {
            factory.getClient();
            fail("Expected exception");
        }
        catch (ElasticsearchException e)
        {
            assertEquals("No connection", e.getMessage());
        }
    }

    @Test
    public void test_get_client()
    {
        when(sessionManager.getCurrentSession()).thenReturn(sessionWithConnection);

        Client client = factory.getClient();
        assertNotNull(client);
        assertSame(CONNECTION, client.getConnection());
    }
}
