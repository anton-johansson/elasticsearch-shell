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
package com.antonjohansson.elasticsearchshell.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link PropertiesUtils}.
 */
public class PropertiesUtilsTest extends Assert
{
    @Test
    public void test_constructor() throws Exception
    {
        int classModifiers = PropertiesUtils.class.getModifiers();
        assertTrue("Expected class to be final", Modifier.isFinal(classModifiers));

        Constructor<?>[] constructors = PropertiesUtils.class.getDeclaredConstructors();
        assertEquals("Expected class to have one single constructor", 1, constructors.length);

        Constructor<?> constructor = constructors[0];
        assertFalse("Expected constructor to be inaccessible", constructor.isAccessible());

        try
        {
            constructor.setAccessible(true);
            constructor.newInstance();
        }
        finally
        {
            constructor.setAccessible(false);
        }
    }

    @Test
    public void read()
    {
        String file = PropertiesUtilsTest.class.getResource("/test-file.txt").getFile();
        Properties properties = PropertiesUtils.read(new File(file));
        assertNotNull(properties);
    }

    @Test(expected = RuntimeException.class)
    public void read_bad_call()
    {
        PropertiesUtils.read(null);
    }

    @Test
    public void output()
    {
        String file = PropertiesUtilsTest.class.getResource("/test-file.txt").getFile();
        PropertiesUtils.write(new Properties(), new File(file));
    }

    @Test(expected = RuntimeException.class)
    public void output_bad_call()
    {
        PropertiesUtils.write(null, null);
    }
}
