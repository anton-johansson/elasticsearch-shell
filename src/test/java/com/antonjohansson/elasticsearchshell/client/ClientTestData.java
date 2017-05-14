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

import static java.util.stream.Collectors.toMap;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.domain.IndexMappings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Contains various Elasticsearch JSON bodies, etc, used for {@link ClientTest}.
 */
public class ClientTestData
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

    public static final String INDEX = json()
            .child("settings")
            .put("number_of_replicas", 1)
            .put("number_of_shards", 2)
            .parent
            .build();

    public static final String ACK_TRUE = json()
            .put("acknowledged", true)
            .build();

    public static final String ACK_FALSE = json()
            .put("acknowledged", false)
            .build();

    static final String ALL_INDICES_AND_MAPPINGS = "{\"my-index\":{\"mappings\":{\"User\":{\"dynamic\":\"strict\",\"_all\":{\"enabled\":false},\"properties\":{\"email\":{\"type\":\"keyword\",\"index\":false},\"firstName\":{\"type\":\"keyword\",\"index\":false},\"lastName\":{\"type\":\"keyword\",\"index\":false},\"password\":{\"type\":\"keyword\",\"index\":false},\"userName\":{\"type\":\"keyword\",\"index\":false}}},\"OtherType\":{\"dynamic\":\"strict\",\"_all\":{\"enabled\":false},\"properties\":{\"someString\":{\"type\":\"keyword\",\"index\":false},\"someInteger\":{\"type\":\"integer\"}}}}},\"my-other-index\":{\"mappings\":{\"OtherType\":{\"dynamic\":\"strict\",\"_all\":{\"enabled\":false},\"properties\":{\"someString\":{\"type\":\"keyword\",\"index\":false},\"someInteger\":{\"type\":\"integer\"}}}}}}";
    static final String NODE_STATS = "{\"cluster_name\":\"test-cluster\",\"nodes\":{\"YjwABNYGThechGoNdM0rBA\":{\"name\":\"node1\",\"os\":{\"cpu\":{\"percent\":3},\"mem\":{\"total_in_bytes\":8243830784,\"free_in_bytes\":1135935488,\"used_in_bytes\":7107895296,\"free_percent\":14,\"used_percent\":86}}}}}";
    static final String INDEX_STATS = "{\"indices\":{\"test-index\":{\"primaries\":{\"docs\":{\"count\":18,\"deleted\":1}},\"total\":{\"docs\":{\"count\":18,\"deleted\":1}}}}}";

    public static final Map<String, IndexMappings> ACTUAL_ALL_MAPPINGS = allMappings();

    static Connection connection()
    {
        Connection connection = new Connection();
        connection.setHost("localhost");
        connection.setPort(PORT);
        connection.setName("my-test-connection");
        return connection;
    }

    private static Map<String, IndexMappings> allMappings()
    {
        Map<String, IndexMappings> mappings = new HashMap<>();
        mappings.put("my-index", mapping(userMapping(), otherTypeMapping()));
        mappings.put("my-other-index", mapping(otherTypeMapping()));
        return mappings;
    }

    @SafeVarargs
    private static IndexMappings mapping(Entry<String, Map<String, Object>>... mappings)
    {
        IndexMappings mapping = new IndexMappings();
        mapping.setMappings(Stream.of(mappings).collect(toMap(Entry::getKey, Entry::getValue)));
        return mapping;
    }

    private static Entry<String, Map<String, Object>> otherTypeMapping()
    {
        Map<String, Object> mapping = toMapFromJson("{\"dynamic\":\"strict\",\"_all\":{\"enabled\":false},\"properties\":{\"someString\":{\"type\":\"keyword\",\"index\":false},\"someInteger\":{\"type\":\"integer\"}}}");
        return new AbstractMap.SimpleEntry<>("OtherType", mapping);
    }

    private static Entry<String, Map<String, Object>> userMapping()
    {
        Map<String, Object> mapping = toMapFromJson("{\"dynamic\":\"strict\",\"_all\":{\"enabled\":false},\"properties\":{\"email\":{\"type\":\"keyword\",\"index\":false},\"firstName\":{\"type\":\"keyword\",\"index\":false},\"lastName\":{\"type\":\"keyword\",\"index\":false},\"password\":{\"type\":\"keyword\",\"index\":false},\"userName\":{\"type\":\"keyword\",\"index\":false}}}");
        return new AbstractMap.SimpleEntry<>("User", mapping);
    }

    private static Builder json()
    {
        return new Builder();
    }

    private static Map<String, Object> toMapFromJson(String json)
    {
        try
        {
            return new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>()
            {
            });
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
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
