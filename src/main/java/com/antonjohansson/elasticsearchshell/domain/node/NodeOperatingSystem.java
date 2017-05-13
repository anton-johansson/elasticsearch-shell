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
 * Contains information about the operating system of a node in an Elasticsearch cluster.
 */
public class NodeOperatingSystem
{
    private @JsonProperty("cpu") NodeCPU cpu = new NodeCPU();
    private @JsonProperty("mem") NodeMemory memory = new NodeMemory();

    public NodeCPU getCpu()
    {
        return cpu;
    }

    public void setCpu(NodeCPU cpu)
    {
        this.cpu = cpu;
    }

    public NodeMemory getMemory()
    {
        return memory;
    }

    public void setMemory(NodeMemory memory)
    {
        this.memory = memory;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(cpu)
                .append(memory)
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

        NodeOperatingSystem that = (NodeOperatingSystem) obj;
        return new EqualsBuilder()
                .append(this.cpu, that.cpu)
                .append(this.memory, that.memory)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
