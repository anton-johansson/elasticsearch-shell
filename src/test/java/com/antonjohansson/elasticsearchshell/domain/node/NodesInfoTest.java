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
package com.antonjohansson.elasticsearchshell.domain.node;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.antonjohansson.elasticsearchshell.common.ElasticsearchException;
import com.antonjohansson.elasticsearchshell.domain.AbstractDomainTest;

/**
 * Unit tests of {@link NodesInfo}.
 */
public class NodesInfoTest extends AbstractDomainTest<NodesInfo>
{
    @Test
    public void test_getNodeByName() throws Exception
    {
        Node node = new Node();
        node.setName("node1");

        Map<String, Node> nodes = new HashMap<>();
        nodes.put("node1", node);

        NodesInfo info = new NodesInfo();
        info.setNodes(nodes);

        Node actual = info.getNodeByName("node1");

        assertEquals(node, actual);
    }

    @Test(expected = ElasticsearchException.class)
    public void test_getNodeByName_not_found() throws Exception
    {
        NodesInfo info = new NodesInfo();
        info.getNodeByName("node1");
    }
}
