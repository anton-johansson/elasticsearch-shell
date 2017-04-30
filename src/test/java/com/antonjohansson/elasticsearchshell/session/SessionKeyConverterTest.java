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
package com.antonjohansson.elasticsearchshell.session;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.shell.core.Completion;

/**
 * Unit tests of {@link SessionKeyConverter}.
 */
public class SessionKeyConverterTest extends Assert
{
    private @Mock SessionManager manager;
    private SessionKeyConverter converter;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        converter = new SessionKeyConverter(manager);

        when(manager.getSessionNames()).thenReturn(asList("default", "temporary01", "temporary02"));
    }

    @Test
    public void test_supports()
    {
        assertTrue(converter.supports(SessionKey.class, null));
        assertFalse(converter.supports(String.class, null));
        assertFalse(converter.supports(Object.class, null));
    }

    @Test
    public void test_convertFromText()
    {
        SessionKey actual = converter.convertFromText("mySession", null, null);
        SessionKey expected = new SessionKey();
        expected.setName("mySession");

        assertEquals(expected, actual);
    }

    @Test
    public void test_getAllPossibleValues()
    {
        List<Completion> expected = asList(new Completion("temporary01"), new Completion("temporary02"));
        List<Completion> actual = new ArrayList<>();
        boolean result = converter.getAllPossibleValues(actual, null, "temp", null, null);

        assertTrue(result);
        assertEquals(expected, actual);
    }
}
