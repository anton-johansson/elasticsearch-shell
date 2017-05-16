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
package com.antonjohansson.elasticsearchshell.connection;

import static com.antonjohansson.elasticsearchshell.utils.Constants.CONFIGURATION_FILE;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.utils.PropertiesUtils;

/**
 * Manages connections.
 */
@Component
public class ConnectionManager
{
    private final Map<String, Connection> connections = new TreeMap<>();
    private final File connectionsPath;

    @Autowired
    ConnectionManager(@Qualifier(CONFIGURATION_FILE) File configurationPath)
    {
        connectionsPath = getConnectionsPath(configurationPath);
    }

    @PostConstruct
    void initialize()
    {
        for (File file : connectionsPath.listFiles())
        {
            Connection connection = load(file);
            connections.put(connection.getName(), connection);
        }
    }

    private Connection load(File file)
    {
        Properties properties = PropertiesUtils.read(file);

        Connection connection = new Connection();
        connection.setName(file.getName());
        connection.setHost(properties.getProperty("host"));
        connection.setPort(toInt(properties.getProperty("port")));
        connection.setUsername(properties.getProperty("username", ""));
        connection.setPassword(properties.getProperty("password", ""));
        return connection;
    }

    private File getConnectionsPath(File configurationPath)
    {
        File connections = new File(configurationPath, "connections");
        connections.mkdirs();
        return connections;
    }

    /**
     * Adds a new connection to the list of connections.
     *
     * @param connection The connection to add.
     * @return Returns {@code true} if the connection did not already exists and is now added; otherwise, {@code false}.
     */
    public boolean add(Connection connection)
    {
        if (connections.containsKey(connection.getName()))
        {
            return false;
        }

        persist(connection);
        connections.put(connection.getName(), connection);
        return true;
    }

    private void persist(Connection connection)
    {
        Properties properties = new Properties();
        properties.setProperty("host", connection.getHost());
        properties.setProperty("port", String.valueOf(connection.getPort()));
        properties.setProperty("username", connection.getUsername());
        properties.setProperty("password", connection.getPassword());

        File file = new File(connectionsPath, connection.getName());
        PropertiesUtils.write(properties, file);
    }

    /**
     * Gets a connection from the list.
     *
     * @param key The key of the connection to get.
     * @return Returns the connection, if one is found.
     */
    public Optional<Connection> get(ConnectionKey key)
    {
        return Optional.ofNullable(connections.get(key.getName()));
    }

    /**
     * Gets the connection names.
     *
     * @return Returns the available connection names.
     */
    public Iterable<String> getConnectionNames()
    {
        return connections.keySet();
    }
}
