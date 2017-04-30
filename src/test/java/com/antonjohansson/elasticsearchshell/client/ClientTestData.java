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

import java.util.HashMap;
import java.util.Map;

import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Contains various Elasticsearch JSON bodies, etc, used for {@link ClientTest}.
 */
class ClientTestData
{
    static final int PORT = 1337;

    static final String CLUSTER_INFO = json()
            .put("cluster_name", "my-test-cluster")
            .child("version")
            .put("number", "5.3.0")
            .parent()
            .build();

    static final String CLUSTER_HEALTH = json()
            .put("cluster_name", "my-test-cluster")
            .put("status", "green")
            .put("number_of_nodes", 1)
            .put("number_of_data_nodes", 1)
            .build();

    static Connection connection()
    {
        Connection connection = new Connection();
        connection.setHost("localhost");
        connection.setPort(PORT);
        connection.setName("my-test-connection");
        return connection;
    }

    private static Builder json()
    {
        return new Builder();
    }

    /**
     * Used to build JSON data.
     */
    private static class Builder
    {
        private static final ObjectMapper MAPPER = new ObjectMapper();
        protected final Map<String, Object> data = new HashMap<>();

        public Builder put(String key, Object value)
        {
            data.put(key, value);
            return this;
        }

        public ChildBuilder child(String key)
        {
            return new ChildBuilder(key, this);
        }

        public String build()
        {
            try
            {
                return MAPPER.writeValueAsString(data);
            }
            catch (JsonProcessingException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Used to build JSON data.
     */
    private static class ChildBuilder extends Builder
    {
        private final Builder parent;

        ChildBuilder(String key, Builder parent)
        {
            this.parent = parent;
            parent.put(key, data);
        }

        @Override
        public ChildBuilder put(String key, Object value)
        {
            return (ChildBuilder) super.put(key, value);
        }

        public Builder parent()
        {
            return parent;
        }
    }
}
