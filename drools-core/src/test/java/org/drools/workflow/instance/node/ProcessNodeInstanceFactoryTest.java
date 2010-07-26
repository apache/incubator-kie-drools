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

package org.drools.workflow.instance.node;

import java.util.Properties;

import junit.framework.TestCase;

import org.drools.RuleBaseConfiguration;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.drools.workflow.instance.impl.factory.CreateNewNodeFactory;

public class ProcessNodeInstanceFactoryTest extends TestCase {
    
    public void testDefaultEntries() throws Exception {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        NodeInstanceFactoryRegistry registry = conf.getProcessNodeInstanceFactoryRegistry();
        Node node = new ActionNode();
        assertEquals( CreateNewNodeFactory.class, registry.getProcessNodeInstanceFactory( node ).getClass() );       
    }
    
    public void testDiscoveredEntry() {
        Properties properties = new Properties(); 
        properties.put( "processNodeInstanceFactoryRegistry", "mockProcessNodeInstanceFactory.conf" );        
        RuleBaseConfiguration conf = new RuleBaseConfiguration( properties );
        NodeInstanceFactoryRegistry registry = conf.getProcessNodeInstanceFactoryRegistry();
        assertEquals( MockNodeInstanceFactory.class, registry.getProcessNodeInstanceFactory( new MockNode() ).getClass() );
    }
}
