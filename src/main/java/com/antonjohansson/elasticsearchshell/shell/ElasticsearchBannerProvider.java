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
package com.antonjohansson.elasticsearchshell.shell;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;

/**
 * Provides the welcome banner.
 */
@Component
@Order(HIGHEST_PRECEDENCE)
public class ElasticsearchBannerProvider implements BannerProvider
{
    @Override
    public String getBanner()
    {
        return null;
    }

    @Override
    public String getWelcomeMessage()
    {
        return null;
    }

    @Override
    public String getVersion()
    {
        return "1.2.3";
    }

    @Override
    public String getProviderName()
    {
        return "elasticsearch-shell";
    }
}
