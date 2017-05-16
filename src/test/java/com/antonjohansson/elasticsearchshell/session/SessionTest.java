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

import com.antonjohansson.elasticsearchshell.connection.Connection;
import com.antonjohansson.elasticsearchshell.domain.AbstractDomainTest;
import com.antonjohansson.elasticsearchshell.domain.TestDataUtils;

/**
 * Unit tests of {@link Session}.
 */
public class SessionTest extends AbstractDomainTest<Session>
{
    @Test
    public void test_getOptionalConnection()
    {
        Connection expected = TestDataUtils.createItem(Connection.class, 1);
        Connection actual = TestDataUtils.createItem(Session.class, 1).getOptionalConnection().get();

        assertEquals(expected, actual);
    }

    @Test
    public void test_getOptionalConnection_without_a_connection()
    {
        assertFalse(new Session().getOptionalConnection().isPresent());
    }
}
