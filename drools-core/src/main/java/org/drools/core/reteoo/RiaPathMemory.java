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

import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.ActivationsManager;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.definitions.rule.impl.RuleImpl;

public class RiaPathMemory extends PathMemory {

    private List<RuleImpl> rules;
    
    public RiaPathMemory(RightInputAdapterNode riaNode, ReteEvaluator reteEvaluator) {
        super( riaNode, reteEvaluator );
    }

    @Override
    protected boolean initDataDriven( ReteEvaluator reteEvaluator ) {
        for (PathEndNode pnode : getPathEndNode().getPathEndNodes()) {
            if (pnode instanceof TerminalNode) {
                RuleImpl rule = ( (TerminalNode) pnode ).getRule();
                if ( isRuleDataDriven( reteEvaluator, rule ) ) {
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
    public void doLinkRule(ReteEvaluator reteEvaluator) {
        getRightInputAdapterNode().getObjectSinkPropagator().doLinkRiaNode( reteEvaluator );
    }

    @Override
    public void doLinkRule(ActivationsManager activationsManager ) {
        doLinkRule(activationsManager.getReteEvaluator());
    }

    @Override
    public void doUnlinkRule(ReteEvaluator reteEvaluator) {
        getRightInputAdapterNode().getObjectSinkPropagator().doUnlinkRiaNode( reteEvaluator );
    }

    @Override
    public short getNodeType() {
        return NodeTypeEnums.RightInputAdaterNode;
    }

    private void updateRuleTerminalNodes() {
        rules = new ArrayList<>();
        for ( ObjectSink osink : getRightInputAdapterNode().getObjectSinkPropagator().getSinks() ) {
            for ( LeftTupleSink ltsink : ((BetaNode)osink).getSinkPropagator().getSinks() )  {
                findAndAddTN(ltsink, rules );
            }
        }
    }

    private void findAndAddTN( LeftTupleSink ltsink, List<RuleImpl> terminalNodes) {
        if ( NodeTypeEnums.isTerminalNode(ltsink)) {
            terminalNodes.add( ((TerminalNode)ltsink).getRule() );
        } else if ( ltsink.getType() == NodeTypeEnums.RightInputAdaterNode ) {
            for ( Sink childSink : (( RightInputAdapterNode ) ltsink).getSinks() )  {
                findAndAddTN((LeftTupleSink)childSink, terminalNodes);
            }
        } else {
            for ( LeftTupleSink childLtSink : ((LeftTupleSource)ltsink).getSinkPropagator().getSinks() )  {
                findAndAddTN(childLtSink, terminalNodes);
            }
        }
    }

    public List<RuleImpl> getAssociatedRules() {
        if ( rules == null ) {
            updateRuleTerminalNodes();
        }
        return rules;
    }

    public String getRuleNames() {
        List<String> ruleNames = new ArrayList<>();
        for (RuleImpl rule : getAssociatedRules()) {
            ruleNames.add(rule.getName());
        }
        return ruleNames.toString();
    }

    public String toString() {
        return "[RiaMem " + getRuleNames() + "]";
    }
}
