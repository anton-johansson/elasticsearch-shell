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
package com.antonjohansson.elasticsearchshell.index;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import com.antonjohansson.elasticsearchshell.client.ClientFactory;

/**
 * Converts {@link String strings} to {@link IndexKey index keys}.
 */
@Component
class IndexKeyConverter implements Converter<IndexKey>
{
    private final ClientFactory clientFactory;

    @Autowired
    IndexKeyConverter(ClientFactory clientFactory)
    {
        this.clientFactory = clientFactory;
    }

    @Override
    public boolean supports(Class<?> type, String optionContext)
    {
        return IndexKey.class.isAssignableFrom(type);
    }

    @Override
    public IndexKey convertFromText(String value, Class<?> targetType, String optionContext)
    {
        return new IndexKey(value);
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData, String optionContext, MethodTarget target)
    {
        clientFactory.getClient()
                .getMappings()
                .keySet()
                .stream()
                .filter(name -> name.startsWith(existingData))
                .sorted()
                .forEach(name ->
                {
                    completions.add(new Completion(name));
                });

        return true;
    }
}
