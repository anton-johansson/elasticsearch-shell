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

import java.math.BigInteger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains statistics about the memory of a node.
 */
public class NodeMemory
{
    private @JsonProperty("free_percent") int freePercentage;
    private @JsonProperty("used_percent") int usedPercentage;
    private @JsonProperty("total_in_bytes") BigInteger totalInBytes;
    private @JsonProperty("free_in_bytes") BigInteger freeInBytes;
    private @JsonProperty("used_in_bytes") BigInteger usedInBytes;

    public int getFreePercentage()
    {
        return freePercentage;
    }

    public void setFreePercentage(int freePercentage)
    {
        this.freePercentage = freePercentage;
    }

    public int getUsedPercentage()
    {
        return usedPercentage;
    }

    public void setUsedPercentage(int usedPercentage)
    {
        this.usedPercentage = usedPercentage;
    }

    public BigInteger getTotalInBytes()
    {
        return totalInBytes;
    }

    public void setTotalInBytes(BigInteger totalInBytes)
    {
        this.totalInBytes = totalInBytes;
    }

    public BigInteger getFreeInBytes()
    {
        return freeInBytes;
    }

    public void setFreeInBytes(BigInteger freeInBytes)
    {
        this.freeInBytes = freeInBytes;
    }

    public BigInteger getUsedInBytes()
    {
        return usedInBytes;
    }

    public void setUsedInBytes(BigInteger usedInBytes)
    {
        this.usedInBytes = usedInBytes;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(freePercentage)
                .append(usedPercentage)
                .append(totalInBytes)
                .append(freeInBytes)
                .append(usedInBytes)
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

        NodeMemory that = (NodeMemory) obj;
        return new EqualsBuilder()
                .append(this.freePercentage, that.freePercentage)
                .append(this.usedPercentage, that.usedPercentage)
                .append(this.totalInBytes, that.totalInBytes)
                .append(this.freeInBytes, that.freeInBytes)
                .append(this.usedInBytes, that.usedInBytes)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
