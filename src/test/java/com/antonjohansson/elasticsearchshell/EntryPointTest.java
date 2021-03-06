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
package com.antonjohansson.elasticsearchshell;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.shell.Bootstrap;

/**
 * Unit tests of {@link EntryPoint}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Bootstrap.class)
public class EntryPointTest extends Assert
{
    @Test
    public void test_new()
    {
        assertNotNull(new EntryPoint());
    }

    @Test
    public void test_main() throws IOException
    {
        PowerMockito.mockStatic(Bootstrap.class);
        EntryPoint.main(new String[] {"test1", "test2"});

        PowerMockito.verifyStatic();
        Bootstrap.main(new String[] {"--disableInternalCommands"});
    }
}
