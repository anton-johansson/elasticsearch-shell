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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link ReflectionUtils}.
 */
public class ReflectionUtilsTest extends Assert
{
    private final TestClass testInstance = new TestClass();

    @Test
    public void test_constructor() throws Exception
    {
        int classModifiers = ReflectionUtils.class.getModifiers();
        assertTrue("Expected class to be final", Modifier.isFinal(classModifiers));

        Constructor<?>[] constructors = ReflectionUtils.class.getDeclaredConstructors();
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
    public void test_getFieldValue()
    {
        String actual = ReflectionUtils.getFieldValue(TestClass.class, testInstance, "data");
        String expected = "value";

        assertEquals(expected, actual);
    }

    @Test(expected = RuntimeException.class)
    public void test_getFieldValue_field_that_does_not_exist()
    {
        ReflectionUtils.getFieldValue(TestClass.class, testInstance, "nonExistingField");
    }

    /**
     * Defines a class, used for testing the reflection utilities.
     */
    private static class TestClass
    {
        @SuppressWarnings("unused")
        private final String data = "value";
    }
}
