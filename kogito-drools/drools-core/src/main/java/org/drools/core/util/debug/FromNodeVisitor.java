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

package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.reteoo.FromNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.FromNode.FromMemory;

public class FromNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final FromNodeVisitor INSTANCE = new FromNodeVisitor();
    
    protected FromNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        FromNode fn = (FromNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        final FromMemory memory = (FromMemory) info.getSession().getNodeMemory( fn );
        
        ni.setMemoryEnabled( true );
        
        if( fn.isLeftTupleMemoryEnabled() ) {
            ni.setTupleMemorySize( memory.betaMemory.getLeftTupleMemory().size() );

            long handles = 0;
            org.drools.core.util.Iterator it = memory.betaMemory.getLeftTupleMemory().iterator();
            for ( LeftTuple leftTuple = (LeftTuple) it.next(); leftTuple != null; leftTuple = (LeftTuple) it.next() ) {
                LeftTuple child = leftTuple.firstChild;
                while( child != null ) {
                    handles++;
                    child = child.getLeftParentNext();
                }
            }                
            ni.setCreatedFactHandles( handles );
        } else {
            info.warn( "The left memory for this node is disabled, making it impossible to calculate the number of created handles" );
        }

    }

}
