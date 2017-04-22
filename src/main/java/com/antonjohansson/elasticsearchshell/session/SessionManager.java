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

import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

/**
 * Controls sessions.
 */
@Component
public class SessionManager
{
    private final Map<String, Session> sessions = new TreeMap<>();
    private String currentSessionName;
    private int autogenerationId = 1;

    SessionManager()
    {
        newSession("default");
    }

    /**
     * Gets the current session.
     *
     * @return Returns the current session.
     */
    public Session getCurrentSession()
    {
        return sessions.get(currentSessionName);
    }

    /**
     * Creates a new session with a generated name.
     */
    public void newSession()
    {
        String name = generateNewName();
        newSession(name);
    }

    /**
     * Creates a new session with the given name.
     *
     * @param name The name of the session to create.
     * @return Returns {@code true} if the name was available.
     */
    public boolean newSession(String name)
    {
        if (sessions.containsKey(name))
        {
            return false;
        }

        Session session = new Session();
        session.setName(name);
        sessions.put(name, session);
        currentSessionName = name;
        return true;
    }

    private String generateNewName()
    {
        String name;
        do
        {
            name = "session" + autogenerationId++;
        }
        while (sessions.containsKey(name));
        return name;
    }

    /**
     * Gets the names of the sessions.
     * 
     * @return Returns the names.
     */
    public Iterable<String> getSessionNames()
    {
        return sessions.keySet();
    }

    /**
     * Removes the session with the given name, if it exists.
     *
     * @param name The name of the session to remove.
     * @return Returns {@code true} if the session existed and is now removed; otherwise, {@code false}.
     */
    public boolean remove(String name)
    {
        return sessions.remove(name) != null;
    }

    /**
     * Sets the current session.
     *
     * @param name The name of the session to change to.
     * @return Returns {@code true} if the session existed and is now switched to, otherwise; {@code false}.
     */
    public boolean setCurrentSession(String name)
    {
        if (!sessions.containsKey(name))
        {
            return false;
        }
        currentSessionName = name;
        return true;
    }
}
