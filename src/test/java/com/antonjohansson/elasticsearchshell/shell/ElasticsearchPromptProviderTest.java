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

import static com.antonjohansson.elasticsearchshell.shell.ElasticsearchPromptProvider.PROMPT_CHARACTER;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests of {@link ElasticsearchPromptProvider}.
 */
public class ElasticsearchPromptProviderTest extends Assert
{
    private @Mock PromptState state;
    private ElasticsearchPromptProvider provider;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        provider = new ElasticsearchPromptProvider(state);
    }

    @Test
    public void test_getProviderName()
    {
        assertEquals("elasticsearch-shell", provider.getProviderName());
    }

    @Test
    public void test_getPromptWhenLastExecutionWasOK()
    {
        when(state.isLastSuccess()).thenReturn(true);

        assertEquals("\u001B[32m" + PROMPT_CHARACTER + " \u001B[0m", provider.getPrompt());
    }

    @Test
    public void test_getPromptWhenLastExecutionWasError()
    {
        when(state.isLastSuccess()).thenReturn(false);

        assertEquals("\u001B[31m" + PROMPT_CHARACTER + " \u001B[0m", provider.getPrompt());
    }
}
