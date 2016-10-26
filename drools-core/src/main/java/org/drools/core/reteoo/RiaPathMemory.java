/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.reteoo;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;

import java.util.ArrayList;
import java.util.List;

public class RiaPathMemory extends PathMemory {

    private List<String> terminalNodeNames;
    
    public RiaPathMemory(RightInputAdapterNode riaNode, InternalWorkingMemory wm) {
        super( riaNode, wm );
    }

    @Override
    protected boolean initDataDriven( InternalWorkingMemory wm ) {
        for (PathEndNode pnode : getPathEndNode().getPathEndNodes()) {
            if (pnode instanceof TerminalNode) {
                RuleImpl rule = ( (TerminalNode) pnode ).getRule();
                if ( isRuleDataDriven( wm, rule ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public RightInputAdapterNode getRightInputAdapterNode() {
        return (RightInputAdapterNode) getPathEndNode();
    }

    @Override
    public void doLinkRule(InternalWorkingMemory wm) {
        getRightInputAdapterNode().getObjectSinkPropagator().doLinkRiaNode( wm );
    }

    @Override
    public void doLinkRule(InternalAgenda agenda ) {
        doLinkRule(agenda.getWorkingMemory());
    }

    @Override
    public void doUnlinkRule(InternalWorkingMemory wm) {
        getRightInputAdapterNode().getObjectSinkPropagator().doUnlinkRiaNode( wm );
    }

    @Override
    public short getNodeType() {
        return NodeTypeEnums.RightInputAdaterNode;
    }

    private void updateRuleTerminalNodes() {
        terminalNodeNames = new ArrayList<String>();
        for ( ObjectSink osink : getRightInputAdapterNode().getObjectSinkPropagator().getSinks() ) {
            for ( LeftTupleSink ltsink : ((BetaNode)osink).getSinkPropagator().getSinks() )  {
                findAndAddTN(ltsink, terminalNodeNames);
            }
        }
    }

    private void findAndAddTN( LeftTupleSink ltsink, List<String> terminalNodeNames) {
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

    public List<String> getTerminalNodeNames() {
        if ( terminalNodeNames == null ) {
            updateRuleTerminalNodes();
        }
        return terminalNodeNames;
    }

    public String toString() {
        return "[RiaMem " + getTerminalNodeNames() + "]";
    }
}
