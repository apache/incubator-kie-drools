package org.drools.kiesession.debug;

import org.drools.base.common.NetworkNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftTuple;

import java.util.Collection;

public class FromNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final FromNodeVisitor INSTANCE = new FromNodeVisitor();
    
    protected FromNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Collection<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        FromNode fn = (FromNode) node;
        DefaultNodeInfo ni = info.getNodeInfo( node );
        final FromMemory memory = (FromMemory) info.getSession().getNodeMemory( fn );
        
        if( fn.isLeftTupleMemoryEnabled() ) {
            ni.setTupleMemorySize( memory.getBetaMemory().getLeftTupleMemory().size() );

            long handles = 0;
            org.drools.core.util.Iterator it = memory.getBetaMemory().getLeftTupleMemory().iterator();
            for ( LeftTuple leftTuple = (LeftTuple) it.next(); leftTuple != null; leftTuple = (LeftTuple) it.next() ) {
                LeftTuple child = leftTuple.getFirstChild();
                while( child != null ) {
                    handles++;
                    child = child.getHandleNext();
                }
            }
            ni.setCreatedFactHandles( handles );
        } else {
            info.warn( "The left memory for this node is disabled, making it impossible to calculate the number of created handles" );
        }

    }

}
