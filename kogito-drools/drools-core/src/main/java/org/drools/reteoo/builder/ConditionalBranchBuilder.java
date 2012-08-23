package org.drools.reteoo.builder;

import org.drools.common.UpdateContext;
import org.drools.reteoo.ConditionalBranchEvaluator;
import org.drools.reteoo.ConditionalBranchNode;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.ConditionalBranch;
import org.drools.rule.RuleConditionElement;

import static org.drools.reteoo.builder.NamedConsequenceBuilder.buildTerminalNodeForNamedConsequence;

public class ConditionalBranchBuilder implements ReteooComponentBuilder {

    public void build( BuildContext context, BuildUtils utils, RuleConditionElement rce ) {
        ConditionalBranch conditionalBranch = (ConditionalBranch) rce;
        ConditionalBranchEvaluator branchEvaluator = buildConditionalBranchEvaluator( context, conditionalBranch );

        context.pushRuleComponent( rce );

        ConditionalBranchNode node = new ConditionalBranchNode( context.getNextId(),
                                                                context.getTupleSource(),
                                                                branchEvaluator,
                                                                context );

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
