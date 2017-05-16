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
package com.antonjohansson.elasticsearchshell.domain;

import static java.util.Objects.hash;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains information about an Elasticsearch cluster.
 */
public class ClusterInfo
{
    private @JsonProperty("cluster_name") String clusterName = "";
    private @JsonProperty("version") Version version;

    public String getClusterName()
    {
        return clusterName;
    }

    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }

    public Version getVersion()
    {
        return version;
    }

    public void setVersion(Version version)
    {
        this.version = version;
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

        ClusterInfo that = (ClusterInfo) obj;
        return new EqualsBuilder()
                .append(this.clusterName, that.clusterName)
                .append(this.version, that.version)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }

    /**
     * Describes the version of the cluster.
     */
    public static class Version
    {
        private @JsonProperty("number") String number = "";

        public String getNumber()
        {
            return number;
        }

        public void setNumber(String number)
        {
            this.number = number;
        }

        @Override
        public int hashCode()
        {
            return hash(number);
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

            Version that = (Version) obj;
            return new EqualsBuilder()
                    .append(this.number, that.number)
                    .isEquals();
        }

        @Override
        public String toString()
        {
            return reflectionToString(this, SHORT_PREFIX_STYLE);
        }
    }
}
