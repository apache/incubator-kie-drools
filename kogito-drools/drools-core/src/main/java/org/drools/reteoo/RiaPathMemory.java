package org.drools.reteoo;

import org.drools.common.InternalWorkingMemory;

import java.util.ArrayList;
import java.util.List;

public class RiaPathMemory extends PathMemory {

    private RightInputAdapterNode riaNode;

    private String terminalNodes;
    
    public RiaPathMemory(RightInputAdapterNode riaNode) {
        super( null );
        this.riaNode = riaNode;
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
            for ( Object childSink : ((RightInputAdapterNode)ltsink).getSinkPropagator().getSinks() )  {
                findAndAddTN( ( LeftTupleSink ) childSink, terminalNodeNames);
            }
        } else {
            for ( LeftTupleSink childLtSink : ((BetaNode)ltsink).getSinkPropagator().getSinks() )  {
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
