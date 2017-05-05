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
package com.antonjohansson.elasticsearchshell.client;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link PasswordEncrypter}.
 */
public class PasswordEncrypterTest extends Assert
{
    private final PasswordEncrypter encrypter = new PasswordEncrypter();

    @Test
    public void test_encrypt_and_decrypt()
    {
        String password = "some-test-password";
        String username = "anton-johansson-very-long-username";

        String encryptedPassword = encrypter.encrypt(username, password);
        String decryptedPassword = encrypter.decrypt(username, encryptedPassword);

        assertEquals(password, decryptedPassword);
    }

    @Test
    public void test_encrypt()
    {
        String expected = "KRHrY0A/X9l9l8yILBMvpdZhHOqhLyLgrfolGyWgGUI=";
        String actual = encrypter.encrypt("anton-johansson", "some-test-password");

        assertEquals(expected, actual);
    }

    @Test
    public void test_decrypt()
    {
        String expected = "some-test-password";
        String actual = encrypter.decrypt("anton-johansson", "KRHrY0A/X9l9l8yILBMvpdZhHOqhLyLgrfolGyWgGUI=");

        assertEquals(expected, actual);
    }

    @Test(expected = RuntimeException.class)
    public void test_decrypt_bad_format()
    {
        encrypter.decrypt("anton-johansson", "KRHrY0A/X9l9l8yILBMvpdZhHOqhLyLgrfolGyWgGUI=X");
    }

    @Test(expected = RuntimeException.class)
    public void test_decrypt_bad_algorithm()
    {
        encrypter.setAlgorithm("unknown-algorithm");
        encrypter.encrypt("anton-johansson", "some-test-password");
    }
}
