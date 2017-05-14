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
package com.antonjohansson.elasticsearchshell.utils;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Defines utilities for managing reflection, used across the application.
 */
public final class ReflectionUtils
{
    // Prevent instantiation.
    private ReflectionUtils()
    {
    }

    /**
     * Gets the value of a field of an object.
     *
     * @param clazz The class of the object to get field value from.
     * @param object The object to get field value from.
     * @param fieldName The name of the field to get value from.
     * @return Returns the field value.
     */
    @SuppressWarnings("unchecked")
    public static <V, T> V getFieldValue(Class<T> clazz, T object, String fieldName)
    {
        Field field = FieldUtils.getField(clazz, fieldName, true);
        try
        {
            return (V) field.get(object);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
