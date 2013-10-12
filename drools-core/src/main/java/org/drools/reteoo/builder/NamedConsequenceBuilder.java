package org.drools.reteoo.builder;

import org.drools.ActivationListenerFactory;
import org.drools.common.BaseNode;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.UpdateContext;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.TerminalNode;
import org.drools.rule.GroupElement;
import org.drools.rule.NamedConsequence;
import org.drools.rule.Rule;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.PropagationContext;

public class NamedConsequenceBuilder implements ReteooComponentBuilder {

    public void build(BuildContext context, BuildUtils utils, RuleConditionElement rce) {
        NamedConsequence namedConsequence = (NamedConsequence) rce;
        RuleTerminalNode terminalNode = buildTerminalNodeForNamedConsequence(context, namedConsequence);

        terminalNode.attach(context);

        terminalNode.networkUpdated(new UpdateContext());

        // adds the terminal node to the list of nodes created/added by this sub-rule
        context.addNode(terminalNode);

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            final PropagationContext propagationContext = new PropagationContextImpl(workingMemory.getNextPropagationIdCounter(),
                                                                                     PropagationContext.RULE_ADDITION,
                                                                                     null,
                                                                                     null,
                                                                                     null);

            for ( BaseNode node : context.getNodes() ) {
                node.updateSinkOnAttach( context, propagationContext, workingMemory );
            }

            propagationContext.evaluateActionQueue( workingMemory );
        }
    }

    public boolean requiresLeftActivation(BuildUtils utils, RuleConditionElement rce) {
        return false;
    }

    static RuleTerminalNode buildTerminalNodeForNamedConsequence(BuildContext context, NamedConsequence namedConsequence) {
        Rule rule = context.getRule();
        GroupElement subrule = (GroupElement) context.peek();

        ActivationListenerFactory factory = context.getRuleBase().getConfiguration().getActivationListenerFactory( rule.getActivationListener() );
        TerminalNode terminal = factory.createActivationListener( context.getNextId(),
                                                                  context.getTupleSource(),
                                                                  rule,
                                                                  subrule,
                                                                  0, // subruleIndex,
                                                                  context );

        RuleTerminalNode terminalNode = (RuleTerminalNode) terminal;
        ((RuleTerminalNode) terminal).setConsequenceName( namedConsequence.getConsequenceName() );
        return terminalNode;
    }
}
