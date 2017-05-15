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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Provides utilities to work with properties files.
 */
public final class PropertiesUtils
{
    // Prevent instantiation
    private PropertiesUtils()
    {
    }

    /**
     * Reads properties from the given file.
     */
    public static Properties read(File file)
    {
        try (InputStream input = new FileInputStream(file))
        {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes the given properties to the given file.
     */
    public static void write(Properties properties, File file)
    {
        try (OutputStream output = new FileOutputStream(file))
        {
            properties.store(output, "");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
