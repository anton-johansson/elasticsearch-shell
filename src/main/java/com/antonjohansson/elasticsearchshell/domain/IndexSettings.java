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

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines settings for an index.
 */
public class IndexSettings
{
    private @JsonProperty("number_of_shards") int numberOfShards;
    private @JsonProperty("number_of_replicas") int numberOfReplicas;

    public int getNumberOfShards()
    {
        return numberOfShards;
    }

    public void setNumberOfShards(int numberOfShards)
    {
        this.numberOfShards = numberOfShards;
    }

    public int getNumberOfReplicas()
    {
        return numberOfReplicas;
    }

    public void setNumberOfReplicas(int numberOfReplicas)
    {
        this.numberOfReplicas = numberOfReplicas;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(numberOfShards, numberOfReplicas);
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

        IndexSettings that = (IndexSettings) obj;
        return new EqualsBuilder()
                .append(this.numberOfShards, that.numberOfShards)
                .append(this.numberOfReplicas, that.numberOfReplicas)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
