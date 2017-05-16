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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests of {@link SessionManager}.
 */
public class SessionManagerTest extends Assert
{
    private SessionManager manager;

    @Before
    public void setUp()
    {
        manager = new SessionManager();
    }

    @Test
    public void test_default_session()
    {
        Session actual = manager.getCurrentSession();
        Session expected = new Session();
        expected.setConnection(null);
        expected.setName("default");

        assertEquals(expected, actual);
    }

    @Test
    public void test_create_autogenerated_sessions()
    {
        manager.newSession();
        manager.newSession();

        List<String> actual = getSessionNames();
        List<String> expected = asList("default", "session1", "session2");

        assertEquals(expected, actual);
    }

    @Test
    public void test_create_autogenerated_sessions_with_colliding_names()
    {
        manager.newSession("session2");
        manager.newSession();
        manager.newSession();

        List<String> actual = getSessionNames();
        List<String> expected = asList("default", "session1", "session2", "session3");

        assertEquals(expected, actual);
    }

    @Test
    public void test_create_session_that_already_exists()
    {
        boolean result = manager.newSession("default");
        assertFalse(result);
        assertEquals(1, getSessionNames().size());
    }

    @Test
    public void test_removing_session()
    {
        manager.newSession("temporary");
        assertEquals(2, getSessionNames().size());

        SessionKey key = new SessionKey();
        key.setName("temporary");

        boolean result = manager.remove(key);
        assertTrue(result);
        assertEquals(1, getSessionNames().size());
    }

    @Test
    public void test_removing_non_existing_session()
    {
        SessionKey key = new SessionKey();
        key.setName("non-existing-session");

        boolean result = manager.remove(key);
        assertFalse(result);
        assertEquals(1, getSessionNames().size());
    }

    @Test
    public void test_setting_session()
    {
        SessionKey key = new SessionKey();
        key.setName("new-session");

        manager.newSession("new-session");
        boolean result = manager.setCurrentSession(key);
        assertTrue(result);

        Session actual = manager.getCurrentSession();
        Session expected = new Session();
        expected.setName("new-session");

        assertEquals(expected, actual);
    }

    @Test
    public void test_setting_non_existing_session()
    {
        SessionKey key = new SessionKey();
        key.setName("non-existing-session");

        boolean result = manager.setCurrentSession(key);
        assertFalse(result);

        Session actual = manager.getCurrentSession();
        Session expected = new Session();
        expected.setName("default");

        assertEquals(expected, actual);
    }

    private List<String> getSessionNames()
    {
        List<String> sessionNames = new ArrayList<>();
        manager.getSessionNames().forEach(sessionNames::add);
        return sessionNames;
    }
}
