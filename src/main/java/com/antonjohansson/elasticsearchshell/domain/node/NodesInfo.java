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
package com.antonjohansson.elasticsearchshell.domain.node;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.antonjohansson.elasticsearchshell.common.ElasticsearchException;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains information about nodes.
 */
public class NodesInfo
{
    private @JsonProperty("cluster_name") String clusterName = "";
    private @JsonProperty("nodes") Map<String, Node> nodes = emptyMap();

    public String getClusterName()
    {
        return clusterName;
    }

    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }

    public Map<String, Node> getNodes()
    {
        return nodes;
    }

    public void setNodes(Map<String, Node> nodes)
    {
        this.nodes = nodes;
    }

    /**
     * Gets a {@link Node node} by its name.
     *
     * @param name The name of the node to get.
     * @return Returns the node, if it was found.
     * @throws ElasticsearchException Thrown if no node was found with the given {@code name}.
     */
    public Node getNodeByName(String name)
    {
        requireNonNull(name, "'name' cannot be null");
        return nodes.values()
                .stream()
                .filter(node -> name.equals(node.getName()))
                .findAny()
                .orElseThrow(() -> new ElasticsearchException("No node with name '" + name + "' was found"));
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(clusterName)
                .append(nodes)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || obj.getClass() != getClass())
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        NodesInfo that = (NodesInfo) obj;
        return new EqualsBuilder()
                .append(this.clusterName, that.clusterName)
                .append(this.nodes, that.nodes)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
