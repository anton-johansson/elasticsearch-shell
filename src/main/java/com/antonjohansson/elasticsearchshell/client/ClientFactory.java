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
package com.antonjohansson.elasticsearchshell.client;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.common.ElasticsearchException;
import com.antonjohansson.elasticsearchshell.session.SessionManager;

/**
 * Creates new {@link WebClient} that can be used to communicate with Elasticsearch.
 */
@Component
public class ClientFactory
{
    private final SessionManager sessionManager;

    @Autowired
    ClientFactory(SessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    /**
     * Gets a new {@link Client}.
     *
     * @return Returns the created {@link Client}.
     */
    public Client getClient()
    {
        return sessionManager.getCurrentSession()
                .getOptionalConnection()
                .map(Client::new)
                .orElseThrow(() -> new ElasticsearchException("No connection"));
    }
}
