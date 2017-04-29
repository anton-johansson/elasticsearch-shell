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
package com.antonjohansson.elasticsearchshell.connection;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static org.apache.commons.lang3.StringUtils.rightPad;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypts and decrypts connection passwords.
 */
public class PasswordEncrypter
{
    private static final int KEY_SIZE = 16;

    // Prevent instantiation
    private PasswordEncrypter()
    {
    }

    /**
     * Encrypts the given password, using the username as salt.
     *
     * @param username The username, used for salt.
     * @param password The password to encrypt.
     * @return Returns the encrypted password.
     */
    public static String encrypt(String username, String password)
    {
        try
        {
            String key = rightPad(username, KEY_SIZE).substring(0, KEY_SIZE);
            Key spec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(ENCRYPT_MODE, spec);
            byte[] encrypted = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypts the given password, using the username as salt.
     *
     * @param username The username, used for salt.
     * @param password The password to decrypt.
     * @return The decrypted password.
     */
    public static String decrypt(String username, String password)
    {
        try
        {
            String key = rightPad(username, KEY_SIZE).substring(0, KEY_SIZE);
            Key spec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(DECRYPT_MODE, spec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(password));
            return new String(decrypted);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
