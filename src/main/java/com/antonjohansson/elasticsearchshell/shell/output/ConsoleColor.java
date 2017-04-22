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
package com.antonjohansson.elasticsearchshell.shell.output;

/**
 * Provides console colors.
 */
public enum ConsoleColor
{
    GREEN("\u001B[32m"),
    RED("\u001B[31m"),
    WHITE("\u001B[37m");

    private static final String RESET = "\u001B[0m";

    private final String code;

    // Prevent instantiation
    ConsoleColor(String code)
    {
        this.code = code;
    }

    /**
     * Formats the given text with this color.
     *
     * @param text The text to format.
     * @return Returns the formatted text.
     */
    public String format(String text)
    {
        return new StringBuilder()
                .append(code)
                .append(text)
                .append(RESET)
                .toString();
    }
}
