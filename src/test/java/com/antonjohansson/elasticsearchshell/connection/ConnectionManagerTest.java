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
package com.antonjohansson.elasticsearchshell.connection;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.FileUtils.writeLines;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests of {@link ConnectionManager}.
 */
public class ConnectionManagerTest extends Assert
{
    private static final File CONFIGURATION_PATH = new File("target/configuration");
    private final ConnectionManager manager = new ConnectionManager(CONFIGURATION_PATH);

    @Before
    public void setUp() throws Exception
    {
        File connections = new File(CONFIGURATION_PATH, "connections");
        deleteDirectory(connections);
        forceMkdir(connections);

        writeLines(new File(connections, "stage01"), asList(
                "host=stage",
                "port=9200"));

        writeLines(new File(connections, "production01"), asList(
                "host=production",
                "port=9200",
                "username=elastic",
                "password=changeme"));

        manager.initialize();
    }

    @Test
    public void test_getConnectionNames()
    {
        List<String> actual = getConnectionNames();
        List<String> expected = asList("production01", "stage01");

        assertEquals(expected, actual);
    }

    @Test
    public void test_get()
    {
        Connection actual = manager.get(new ConnectionKey("production01")).get();
        Connection expected = new Connection();
        expected.setName("production01");
        expected.setHost("production");
        expected.setPort(9200);
        expected.setUsername("elastic");
        expected.setPassword("changeme");

        assertEquals(expected, actual);
    }

    @Test
    public void test_add() throws IOException
    {
        Connection connection = new Connection();
        connection.setName("new");
        connection.setHost("some-host");
        connection.setPort(9201);
        connection.setUsername("sa");
        connection.setPassword("secret");

        boolean result = manager.add(connection);
        assertTrue(result);

        File connections = new File(CONFIGURATION_PATH, "connections");
        File connectionFile = new File(connections, "new");

        assertTrue(connectionFile.exists());

        try (InputStream stream = new FileInputStream(connectionFile))
        {
            Properties properties = new Properties();
            properties.load(stream);

            assertEquals("some-host", properties.getProperty("host"));
            assertEquals("9201", properties.getProperty("port"));
            assertEquals("sa", properties.getProperty("username"));
            assertEquals("secret", properties.getProperty("password"));
        }
    }

    @Test
    public void test_add_existing_connection()
    {
        Connection connection = new Connection();
        connection.setName("production01");

        boolean result = manager.add(connection);
        assertFalse(result);
    }

    private List<String> getConnectionNames()
    {
        List<String> connectionNames = new ArrayList<>();
        manager.getConnectionNames().forEach(connectionNames::add);
        return connectionNames;
    }
}
