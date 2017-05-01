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
package com.antonjohansson.elasticsearchshell.domain;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Skeleton for domain object tests.
 *
 * @param <T> The type of the domain object.
 */
public abstract class AbstractDomainTest<T> extends Assert
{
    private final Class<T> domainClass;

    @SuppressWarnings("unchecked")
    protected AbstractDomainTest()
    {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        this.domainClass = (Class<T>) type.getActualTypeArguments()[0];
    }

    /**
     * Gets a list of properties that are excluded from the test that validates {@link #toString()}.
     */
    protected Collection<String> getPropertiesThatAreExcludedFromToString()
    {
        return emptyList();
    }

    @Test
    public void test_properties() throws Exception
    {
        T item = createEmptyItem();

        for (PropertyDescriptor descriptor : getProperties(domainClass))
        {
            Object value1 = generateValue(descriptor.getName(), descriptor.getPropertyType(), 1);
            setProperty(item, descriptor, value1);
            Object value2 = getProperty(item, descriptor);

            if (!value1.equals(value2))
            {
                throw new AssertionError(descriptor.getName() + " does not have a working getter or setter");
            }
        }
    }

    @Test
    public void test_toString() throws Exception
    {
        T item = createItem(1);
        String output = item.toString();

        for (PropertyDescriptor descriptor : getProperties(domainClass))
        {
            if (getPropertiesThatAreExcludedFromToString().contains(descriptor.getName()))
            {
                continue;
            }

            Object value1 = generateValue(descriptor.getName(), descriptor.getPropertyType(), 1);

            String data = descriptor.getName() + "=" + value1.toString();
            if (!output.contains(data))
            {
                throw new AssertionError(descriptor.getName() + " is not properly included in #toString()");
            }
        }
    }

    @Test
    public void test_hashCode() throws Exception
    {
        T item1 = createItem(1);
        T item2 = createItem(2);
        T item3 = createItem(1);

        assertEquals(item1.hashCode(), item3.hashCode());
        assertNotEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void test_equals() throws Exception
    {
        T item1 = createItem(1);
        T item2 = createItem(2);

        if (!item1.equals(item1))
        {
            throw new AssertionError("One instance is not equal to itself!");
        }

        if (item1.equals(new Object()))
        {
            throw new AssertionError("One instance is equal to an object of another type!");
        }

        if (item1.equals(null))
        {
            throw new AssertionError("One instance is equal to null!");
        }

        for (PropertyDescriptor descriptor : getProperties(domainClass))
        {
            if (item1.equals(item2))
            {
                throw new AssertionError("Two instances were eqaul before all properties were copied");
            }

            Object value = getProperty(item1, descriptor);
            setProperty(item2, descriptor, value);
        }

        if (!item1.equals(item2))
        {
            throw new AssertionError("Two instances were not equals after all properties were copied");
        }
    }

    private Object getProperty(T item, PropertyDescriptor descriptor)
    {
        try
        {
            return descriptor.getReadMethod().invoke(item);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void setProperty(T item, PropertyDescriptor descriptor, Object value)
    {
        try
        {
            descriptor.getWriteMethod().invoke(item, value);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Object generateValue(String propertyName, Class<?> clazz, int id) throws Exception
    {
        if (clazz.isAssignableFrom(String.class))
        {
            return propertyName + id;
        }
        if (clazz.isAssignableFrom(int.class))
        {
            return id;
        }

        Object value = newInstance(clazz);
        populate(value, id);
        return value;
    }

    private void populate(Object value, int id) throws Exception
    {
        for (PropertyDescriptor descriptor : getProperties(value.getClass()))
        {
            Object innerValue = generateValue(descriptor.getName(), descriptor.getPropertyType(), id);
            descriptor.getWriteMethod().invoke(value, innerValue);
        }
    }

    /**
     * Creates a new empty instance of the domain object.
     */
    protected T createEmptyItem()
    {
        return newInstance(domainClass);
    }

    /**
     * Creates a new, populated instance of the domain object.
     */
    protected T createItem(int id) throws Exception
    {
        T value = newInstance(domainClass);
        populate(value, id);
        return value;
    }

    private <B> B newInstance(Class<B> clazz)
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

    private Collection<PropertyDescriptor> getProperties(Class<?> clazz) throws Exception
    {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        return Stream.of(beanInfo.getPropertyDescriptors())
                .filter(descriptor -> !"class".equals(descriptor.getName()))
                .filter(descriptor -> descriptor.getReadMethod() != null)
                .filter(descriptor -> descriptor.getWriteMethod() != null)
                .collect(toList());
    }
}
