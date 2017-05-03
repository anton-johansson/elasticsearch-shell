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
package com.antonjohansson.elasticsearchshell.session;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Defines a session key.
 */
public class SessionKey
{
    private String name = "";

    public SessionKey()
    {
    }

    public SessionKey(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
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

        SessionKey that = (SessionKey) obj;
        return new EqualsBuilder()
                .append(this.name, that.name)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return name;
    }
}
