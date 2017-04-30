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
package com.antonjohansson.elasticsearchshell.domain;

import static java.util.Objects.hash;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains data about a clusters health.
 */
public class ClusterHealth
{
    private @JsonProperty("cluster_name") String clusterName = "";
    private @JsonProperty("status") String status = "";
    private @JsonProperty("number_of_nodes") int numberOfNodes;
    private @JsonProperty("number_of_data_nodes") int numberOfDataNodes;

    public String getClusterName()
    {
        return clusterName;
    }

    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getNumberOfNodes()
    {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes)
    {
        this.numberOfNodes = numberOfNodes;
    }

    public int getNumberOfDataNodes()
    {
        return numberOfDataNodes;
    }

    public void setNumberOfDataNodes(int numberOfDataNodes)
    {
        this.numberOfDataNodes = numberOfDataNodes;
    }

    @Override
    public int hashCode()
    {
        return hash(clusterName);
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

        ClusterHealth that = (ClusterHealth) obj;
        return new EqualsBuilder()
                .append(this.clusterName, that.clusterName)
                .append(this.status, that.status)
                .append(this.numberOfNodes, that.numberOfNodes)
                .append(this.numberOfDataNodes, that.numberOfDataNodes)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
