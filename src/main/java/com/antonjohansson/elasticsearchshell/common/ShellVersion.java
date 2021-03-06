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
package com.antonjohansson.elasticsearchshell.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Holds the shell version.
 */
@Component
public class ShellVersion
{
    private static final String IMPLEMENTATION_VERSION = ShellVersion.class.getPackage().getImplementationVersion();
    private static final String VERSION = StringUtils.defaultIfBlank(IMPLEMENTATION_VERSION, "development");

    /**
     * Gets the version of the shell.
     *
     * @return Returns the version of the shell.
     */
    public String get()
    {
        return VERSION;
    }
}
