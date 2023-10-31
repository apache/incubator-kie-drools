/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo.builder;

import org.drools.base.rule.ConditionalBranch;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.RuleConditionElement;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.ConditionalBranchEvaluator;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.RuleTerminalNode;

public class ConditionalBranchBuilder implements ReteooComponentBuilder {

    public void build( BuildContext context, BuildUtils utils, RuleConditionElement rce ) {
        ConditionalBranch conditionalBranch = (ConditionalBranch) rce;
        ConditionalBranchEvaluator branchEvaluator = buildConditionalBranchEvaluator( context, conditionalBranch );

        context.pushRuleComponent( rce );



        ConditionalBranchNode node = CoreComponentFactory.get()
                                            .getNodeFactoryService()
                                            .buildConditionalBranchNode( context.getNextNodeId(), context.getTupleSource(),
                                                                         branchEvaluator, context );

        context.setTupleSource( utils.attachNode(context, node) );

        context.popRuleComponent();
    }

    private ConditionalBranchEvaluator buildConditionalBranchEvaluator( BuildContext context, ConditionalBranch conditionalBranch ) {
        // conditional branches are always inline, so set their subrule index to 0. Their equals will be differentiated by consequence anyway.
        RuleTerminalNode terminalNode = (RuleTerminalNode) ReteooRuleBuilder.buildTerminalNodeForConsequence(context, (GroupElement) context.peek(), 0,
                                                                                                             conditionalBranch.getNamedConsequence(), null, null);
        terminalNode.networkUpdated(new UpdateContext());

        return new ConditionalBranchEvaluator( conditionalBranch.getEvalCondition(),
                                               context.getTupleSource().getPartitionId(),
                                               terminalNode,
                                               conditionalBranch.getNamedConsequence().isBreaking(),
                                               conditionalBranch.getElseBranch() != null ? buildConditionalBranchEvaluator( context, conditionalBranch.getElseBranch() ) : null  );
    }

    public boolean requiresLeftActivation(BuildUtils utils, RuleConditionElement rce) {
        return true;
    }
}
