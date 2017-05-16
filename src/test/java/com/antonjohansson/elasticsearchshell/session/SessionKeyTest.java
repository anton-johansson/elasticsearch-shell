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
package com.antonjohansson.elasticsearchshell.session;

import org.junit.Test;

import com.antonjohansson.elasticsearchshell.domain.AbstractDomainTest;

/**
 * Unit tests of {@link SessionKey}.
 */
public class SessionKeyTest extends AbstractDomainTest<SessionKey>
{
    @Test
    public void test_constructor_with_name()
    {
        SessionKey key = new SessionKey("some-name");
        assertEquals("some-name", key.getName());
    }

    @Override
    public void test_toString() throws Exception
    {
        SessionKey key = new SessionKey();
        key.setName("key1");

        assertEquals("key1", key.getName());
        assertEquals("key1", key.toString());
    }
}
