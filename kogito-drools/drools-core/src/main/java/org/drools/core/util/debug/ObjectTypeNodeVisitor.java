/*
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

package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.core.util.ObjectHashSet;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;

public class ObjectTypeNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final ObjectTypeNodeVisitor INSTANCE = new ObjectTypeNodeVisitor();
    
    protected ObjectTypeNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        ObjectTypeNode otn = (ObjectTypeNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        ni.setMemoryEnabled( otn.isObjectMemoryEnabled() );
        
        if( otn.isObjectMemoryEnabled() ) {
            final ObjectHashSet memory = ((ObjectTypeNodeMemory) info.getSession().getNodeMemory( otn )).memory;
            ni.setFactMemorySize( memory.size() );
        }

    }

}
