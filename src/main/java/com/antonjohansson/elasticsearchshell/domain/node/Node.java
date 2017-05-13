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

package com.antonjohansson.elasticsearchshell.domain.node;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains information about a specific node in an Elasticsearch cluster.
 */
public class Node
{
    private @JsonProperty("name") String name = "";
    private @JsonProperty("os") NodeOperatingSystem operatingSystem = new NodeOperatingSystem();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public NodeOperatingSystem getOperatingSystem()
    {
        return operatingSystem;
    }

    public void setOperatingSystem(NodeOperatingSystem operatingSystem)
    {
        this.operatingSystem = operatingSystem;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(name)
                .append(operatingSystem)
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

        Node that = (Node) obj;
        return new EqualsBuilder()
                .append(this.name, that.name)
                .append(this.operatingSystem, that.operatingSystem)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
