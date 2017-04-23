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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

/**
 * Converts {@link String strings} to {@link ConnectionKey connection keys}.
 */
@Component
class ConnectionKeyConverter implements Converter<ConnectionKey>
{
    private final ConnectionManager manager;

    @Autowired
    ConnectionKeyConverter(ConnectionManager manager)
    {
        this.manager = manager;
    }

    @Override
    public boolean supports(Class<?> type, String optionContext)
    {
        return type.isAssignableFrom(ConnectionKey.class);
    }

    @Override
    public ConnectionKey convertFromText(String value, Class<?> targetType, String optionContext)
    {
        ConnectionKey key = new ConnectionKey();
        key.setName(value);
        return key;
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData, String optionContext, MethodTarget target)
    {
        for (String name : manager.getConnectionNames())
        {
            if (name.startsWith(existingData))
            {
                completions.add(new Completion(name));
            }
        }
        return true;
    }
}
