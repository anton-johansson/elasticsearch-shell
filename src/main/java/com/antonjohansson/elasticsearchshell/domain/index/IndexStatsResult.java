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
package com.antonjohansson.elasticsearchshell.domain.index;

import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Defines the result of looking up statistics of an Elasticsearch index.
 */
public class IndexStatsResult
{
    private Map<String, IndexStatsContainer> indices = emptyMap();

    public Map<String, IndexStatsContainer> getIndices()
    {
        return indices;
    }

    public void setIndices(Map<String, IndexStatsContainer> indices)
    {
        this.indices = indices;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(indices)
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

        IndexStatsResult that = (IndexStatsResult) obj;
        return new EqualsBuilder()
                .append(this.indices, that.indices)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
