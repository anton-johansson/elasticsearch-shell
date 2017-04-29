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
package com.antonjohansson.elasticsearchshell.connection;

import static java.util.Objects.hash;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Defines a connection that can be used to manage Elasticsearch nodes.
 */
public class Connection
{
    private String name = "";
    private String host = "";
    private int port;
    private String username = "";
    private String password = "";

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Gets the URL of this connection.
     *
     * @return Returns the connection URL.
     */
    public String getURL()
    {
        return new StringBuilder()
                .append("http://")
                .append(host)
                .append(":")
                .append(port)
                .toString();
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

        Connection that = (Connection) obj;
        return new EqualsBuilder()
                .append(this.name, that.name)
                .append(this.host, that.host)
                .append(this.port, that.port)
                .append(this.username, that.username)
                .append(this.password, that.password)
                .isEquals();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("host", host)
                .append("port", port)
                .append("username", username)
                .append("password", "*****")
                .toString();
    }
}
