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
package com.antonjohansson.elasticsearchshell.shell;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.antonjohansson.elasticsearchshell.common.ShellVersion;

/**
 * Unit tests of {@link ElasticsearchBannerProvider}.
 */
public class ElasticsearchBannerProviderTest extends Assert
{
    private @Mock ShellVersion version;
    private ElasticsearchBannerProvider provider;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        provider = new ElasticsearchBannerProvider(version);

        when(version.get()).thenReturn("1.2.3");
    }

    @Test
    public void test_getProviderName()
    {
        assertEquals("elasticsearch-shell", provider.getProviderName());
    }

    @Test
    public void test_getBanner()
    {
        assertNull(provider.getBanner());
    }

    @Test
    public void test_getWelcomeMessage()
    {
        assertNull(provider.getWelcomeMessage());
    }

    @Test
    public void test_getVersion()
    {
        assertEquals("1.2.3", provider.getVersion());
    }
}
