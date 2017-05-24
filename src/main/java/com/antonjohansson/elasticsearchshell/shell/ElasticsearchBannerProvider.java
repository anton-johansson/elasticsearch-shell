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
package com.antonjohansson.elasticsearchshell.shell;

import static java.lang.System.lineSeparator;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.common.ShellVersion;

/**
 * Provides the welcome banner.
 */
@Component
@Order(HIGHEST_PRECEDENCE)
public class ElasticsearchBannerProvider implements BannerProvider
{
    private static final String BANNER = (""
        + "       _           _   _                              _                    _          _ _ ?"
        + "      | |         | | (_)                            | |                  | |        | | |?"
        + "   ___| | __ _ ___| |_ _  ___ ___  ___  __ _ _ __ ___| |__    ______   ___| |__   ___| | |?"
        + "  / _ # |/ _` / __| __| |/ __/ __|# _ #/ _` | '__/ __| '_ #  |______| / __| '_ # / _ # | |?"
        + " |  __/ | (_| #__ # |_| | (__#__ #  __/ (_| | | | (__| | | |          #__ # | | |  __/ | |?"
        + "  #___|_|#__,_|___/#__|_|#___|___/#___|#__,_|_|  #___|_| |_|          |___/_| |_|#___|_|_|?")

                .replace("#", "\\")
                .replace("?", lineSeparator());

    private final ShellVersion version;

    @Autowired
    ElasticsearchBannerProvider(ShellVersion version)
    {
        this.version = version;
    }

    @Override
    public String getBanner()
    {
        return BANNER;
    }

    @Override
    public String getWelcomeMessage()
    {
        return null;
    }

    @Override
    public String getVersion()
    {
        return version.get();
    }

    @Override
    public String getProviderName()
    {
        return "elasticsearch-shell";
    }
}
