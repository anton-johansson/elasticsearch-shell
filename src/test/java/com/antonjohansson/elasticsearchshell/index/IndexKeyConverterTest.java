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

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.shell.core.Completion;

import com.antonjohansson.elasticsearchshell.client.Client;
import com.antonjohansson.elasticsearchshell.client.ClientFactory;
import com.antonjohansson.elasticsearchshell.domain.IndexMappings;

/**
 * Unit tests of {@link IndexKeyConverter}.
 */
public class IndexKeyConverterTest extends Assert
{
    private @Mock ClientFactory clientFactory;
    private @Mock Client client;
    private IndexKeyConverter converter;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        converter = new IndexKeyConverter(clientFactory);

        Map<String, IndexMappings> mappings = new HashMap<>();
        mappings.put("my-index", null);
        mappings.put("my-other-index", null);
        mappings.put("data", null);

        when(clientFactory.getClient()).thenReturn(client);
        when(client.getMappings()).thenReturn(mappings);
    }

    @Test
    public void test_supports()
    {
        assertTrue(converter.supports(IndexKey.class, null));
        assertFalse(converter.supports(String.class, null));
        assertFalse(converter.supports(Object.class, null));
    }

    @Test
    public void test_convertFromText()
    {
        IndexKey actual = converter.convertFromText("my-index", null, null);
        IndexKey expected = new IndexKey();
        expected.setName("my-index");

        assertEquals(expected, actual);
    }

    @Test
    public void test_getAllPossibleValues()
    {
        List<Completion> expected = asList(new Completion("my-index"), new Completion("my-other-index"));
        List<Completion> actual = new ArrayList<>();
        boolean result = converter.getAllPossibleValues(actual, null, "my", null, null);

        assertTrue(result);
        assertEquals(expected, actual);
    }
}
