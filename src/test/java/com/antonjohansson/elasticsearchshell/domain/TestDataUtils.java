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
package com.antonjohansson.elasticsearchshell.domain;

import static java.util.stream.Collectors.toList;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Provides utilities for test data.
 */
public class TestDataUtils
{
    /**
     * Creates a new instance of the given type.
     */
    public static <T> T createItem(Class<T> clazz, int id)
    {
        return createItem("", clazz, id);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createItem(String propertyName, Class<T> clazz, int id)
    {
        if (clazz.isAssignableFrom(Map.class))
        {
            Map<Object, Object> map = new HashMap<>();
            map.put("key", id);
            return (T) map;
        }
        if (clazz.isAssignableFrom(String.class))
        {
            return (T) (propertyName + id);
        }
        if (clazz.isAssignableFrom(int.class))
        {
            return (T) new Integer(id);
        }
        if (clazz.isAssignableFrom(boolean.class))
        {
            return (T) new Boolean(id % 2 == 1);
        }
        if (clazz.isAssignableFrom(BigInteger.class))
        {
            return (T) new BigInteger(String.valueOf(id));
        }

        T value = newInstance(clazz);
        populate(value, id);
        return value;
    }

    private static <B> B newInstance(Class<B> clazz)
    {
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void populate(Object value, int id)
    {
        try
        {
            for (PropertyDescriptor descriptor : getProperties(value.getClass()))
            {
                Object innerValue = createItem(descriptor.getName(), descriptor.getPropertyType(), id);
                descriptor.getWriteMethod().invoke(value, innerValue);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Collection<PropertyDescriptor> getProperties(Class<?> clazz) throws Exception
    {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        return Stream.of(beanInfo.getPropertyDescriptors())
                .filter(descriptor -> !"class".equals(descriptor.getName()))
                .filter(descriptor -> descriptor.getReadMethod() != null)
                .filter(descriptor -> descriptor.getWriteMethod() != null)
                .collect(toList());
    }
}
