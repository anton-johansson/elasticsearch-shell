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

import static java.util.Objects.hash;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.antonjohansson.elasticsearchshell.connection.Connection;

/**
 * Defines a session.
 */
public class Session
{
    private String name = "";
    private Connection connection;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    public Optional<Connection> getOptionalConnection()
    {
        return Optional.ofNullable(connection);
    }

    @Override
    public int hashCode()
    {
        return hash(name);
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

        Session that = (Session) obj;
        return new EqualsBuilder()
                .append(this.name, that.name)
                .append(this.connection, that.connection)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
