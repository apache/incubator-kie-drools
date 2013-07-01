/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.instance.node;

import junit.framework.TestCase;

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory;

public class ProcessNodeInstanceFactoryTest extends TestCase {
    
    public void testDefaultEntries() throws Exception {
        Node node = new ActionNode();
        assertEquals( CreateNewNodeFactory.class, NodeInstanceFactoryRegistry.getInstance(null).getProcessNodeInstanceFactory( node ).getClass() );       
    }
    
    public void testDiscoveredEntry() {
    	NodeInstanceFactoryRegistry.getInstance(null).register(MockNode.class, new MockNodeInstanceFactory(  new MockNodeInstance(  new MockNode() ) ));
        assertEquals( MockNodeInstanceFactory.class, NodeInstanceFactoryRegistry.getInstance(null).getProcessNodeInstanceFactory( new MockNode() ).getClass() );
    }
}
