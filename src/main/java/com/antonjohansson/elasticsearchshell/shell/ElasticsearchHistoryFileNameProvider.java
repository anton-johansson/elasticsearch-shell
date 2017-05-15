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

import static com.antonjohansson.elasticsearchshell.utils.Constants.CONFIGURATION_FILE;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.HistoryFileNameProvider;
import org.springframework.stereotype.Component;

/**
 * Provides the history file name.
 */
@Component
@Order(HIGHEST_PRECEDENCE)
class ElasticsearchHistoryFileNameProvider implements HistoryFileNameProvider
{
    private final File configurationFile;

    @Autowired
    ElasticsearchHistoryFileNameProvider(@Qualifier(CONFIGURATION_FILE) File configurationFile)
    {
        this.configurationFile = configurationFile;
    }

    @Override
    public String getHistoryFileName()
    {
        File historyFile = new File(configurationFile, "history");
        return historyFile.getAbsolutePath();
    }

    @Override
    public String getProviderName()
    {
        return "elasticsearch-shell";
    }
}
