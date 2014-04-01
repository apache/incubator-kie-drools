package org.drools.core.reteoo;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleEntryQueue;

import java.util.ArrayList;
import java.util.List;

public class RiaPathMemory extends PathMemory {

    private RightInputAdapterNode riaNode;

    private String terminalNodes;
    
    public RiaPathMemory(RightInputAdapterNode riaNode) {
        super( riaNode );
        this.riaNode = riaNode;
    }

    public void initQueue() {
        throw new UnsupportedOperationException("Queues can only be created onthe outer Rule PathMemory");
    }

    public void setTupleQueue(TupleEntryQueue queue) {
        this.queue = queue;
    }

    public RightInputAdapterNode getRightInputAdapterNode() {
        return this.riaNode;
    }
    
    
    public void doLinkRule(InternalWorkingMemory wm) {
        riaNode.getSinkPropagator().doLinkRiaNode( wm );
    }
        
    public void doUnlinkRule(InternalWorkingMemory wm) {
        riaNode.getSinkPropagator().doUnlinkRiaNode( wm );
    }
    
    public short getNodeType() {
        return NodeTypeEnums.RightInputAdaterNode;
    }

    public void updateRuleTerminalNodes() {
        List<String> terminalNodeNames = new ArrayList<String>();
        for ( ObjectSink osink : riaNode.getSinkPropagator().getSinks() ) {
            for ( LeftTupleSink ltsink : ((BetaNode)osink).getSinkPropagator().getSinks() )  {
                findAndAddTN(ltsink, terminalNodeNames);
            }
        }

        StringBuilder sbuilder = new StringBuilder();
        boolean first = true;
        for ( String name : terminalNodeNames ) {
            if ( !first ) {
                sbuilder.append( ", " );
            }
            sbuilder.append( name );
            first = false;
        }

        terminalNodes = sbuilder.toString();
    }

    public void findAndAddTN( LeftTupleSink ltsink, List<String> terminalNodeNames) {
        if ( NodeTypeEnums.isTerminalNode(ltsink)) {
            terminalNodeNames.add( ((TerminalNode)ltsink).getRule().getName() );
        } else if ( ltsink.getType() == NodeTypeEnums.RightInputAdaterNode ) {
            // Do not traverse here, as we'll the other side of the target node anyway.
        } else {
            for ( LeftTupleSink childLtSink : ((LeftTupleSource)ltsink).getSinkPropagator().getSinks() )  {
                findAndAddTN(childLtSink, terminalNodeNames);
            }
        }
    }


    public String toString() {
        if ( terminalNodes == null ) {
            updateRuleTerminalNodes();
        }
        return "[RiaMem " + terminalNodes + "]";
    }
	
}
