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

package org.drools.core.reteoo.builder;

import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.ConditionalBranchEvaluator;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.ConditionalBranch;
import org.drools.core.rule.RuleConditionElement;

import static org.drools.core.reteoo.builder.NamedConsequenceBuilder.buildTerminalNodeForNamedConsequence;

public class ConditionalBranchBuilder implements ReteooComponentBuilder {

    public void build( BuildContext context, BuildUtils utils, RuleConditionElement rce ) {
        ConditionalBranch conditionalBranch = (ConditionalBranch) rce;
        ConditionalBranchEvaluator branchEvaluator = buildConditionalBranchEvaluator( context, conditionalBranch );

        context.pushRuleComponent( rce );



        ConditionalBranchNode node = context.getComponentFactory()
                                            .getNodeFactoryService()
                                            .buildConditionalBranchNode( context.getNextId(), context.getTupleSource(),
                                                                         branchEvaluator, context );

        context.setTupleSource((LeftTupleSource) utils.attachNode(context, node));

        context.popRuleComponent();
    }

    private ConditionalBranchEvaluator buildConditionalBranchEvaluator( BuildContext context, ConditionalBranch conditionalBranch ) {
        RuleTerminalNode terminalNode = buildTerminalNodeForNamedConsequence(context, conditionalBranch.getNamedConsequence());
        terminalNode.networkUpdated(new UpdateContext());
        // adds the terminal node to the list of nodes created/added by this sub-rule
        context.getNodes().add(terminalNode);

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
