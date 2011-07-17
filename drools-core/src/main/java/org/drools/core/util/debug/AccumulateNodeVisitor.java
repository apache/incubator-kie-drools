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
import org.drools.core.util.FastIterator;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.AccumulateNode.AccumulateContext;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftTuple;

public class AccumulateNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final AccumulateNodeVisitor INSTANCE = new AccumulateNodeVisitor();
    
    protected AccumulateNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        AccumulateNode an = (AccumulateNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        final AccumulateMemory memory = (AccumulateMemory) info.getSession().getNodeMemory( an );
        
        ni.setMemoryEnabled( true );
        
        if( an.isObjectMemoryEnabled() ) {
            ni.setFactMemorySize( memory.betaMemory.getRightTupleMemory().size() );
        }
        if( an.isLeftTupleMemoryEnabled() ) {
            ni.setTupleMemorySize( memory.betaMemory.getLeftTupleMemory().size() );
            FastIterator it =  memory.betaMemory.getLeftTupleMemory().fullFastIterator();
            
            int i = 0;
            for ( LeftTuple leftTuple = BetaNode.getFirstLeftTuple( memory.betaMemory.getLeftTupleMemory(), it ); leftTuple != null; leftTuple = ( LeftTuple) it.next( leftTuple  )) {
                AccumulateContext ctx = (AccumulateContext) leftTuple.getObject();
                if ( ctx != null && ctx.result != null ) {
                    i++;
                }
            }
             
            ni.setCreatedFactHandles( i );
        }

    }

}
