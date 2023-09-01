package org.drools.core.reteoo.builder;

import org.drools.core.common.BetaConstraints;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.base.rule.AsyncReceive;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;

public class AsyncReceiveBuilder implements ReteooComponentBuilder {

    @Override
    public void build( BuildContext context, BuildUtils utils, RuleConditionElement rce ) {
        final AsyncReceive receive = (AsyncReceive) rce;
        context.pushRuleComponent( receive );

        @SuppressWarnings("unchecked")
        BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context, context.getBetaconstraints(), true );

        AlphaNodeFieldConstraint[] alphaNodeFieldConstraints = context.getAlphaConstraints() != null ?
                context.getAlphaConstraints().toArray( new AlphaNodeFieldConstraint[context.getAlphaConstraints().size()] ) :
                new AlphaNodeFieldConstraint[0];

        context.setTupleSource( utils.attachNode( context,
                CoreComponentFactory.get().getNodeFactoryService().buildAsyncReceiveNode( context.getNextNodeId(),
                                                                                             receive,
                                                                                             context.getTupleSource(),
                                                                                             alphaNodeFieldConstraints,
                                                                                             betaConstraints,
                                                                                             context  ) ) );

        context.setAlphaConstraints( null );
        context.setBetaconstraints( null );
        context.popRuleComponent();
    }

    @Override
    public boolean requiresLeftActivation( BuildUtils utils, RuleConditionElement rce ) {
        return true;
    }
}
