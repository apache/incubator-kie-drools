package org.drools.core.reteoo.builder;

import org.drools.core.common.BaseNode;
import org.drools.core.common.BetaConstraints;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.rule.From;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;

import java.util.List;

public class ReactiveFromBuilder implements ReteooComponentBuilder {

    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {
        final From from = (From) rce;
        context.pushRuleComponent( from );

        @SuppressWarnings("unchecked")
        BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context, (List<BetaNodeFieldConstraint>) context.getBetaconstraints(), true );

        AlphaNodeFieldConstraint[] alphaNodeFieldConstraints = context.getAlphaConstraints() != null ?
                                                               context.getAlphaConstraints().toArray( new AlphaNodeFieldConstraint[context.getAlphaConstraints().size()] ) :
                                                               new AlphaNodeFieldConstraint[0];

        BaseNode node = context.getComponentFactory().getNodeFactoryService()
                               .buildReactiveFromNode(context.getNextId(),
                                                      from.getDataProvider(),
                                                      context.getTupleSource(),
                                                      alphaNodeFieldConstraints,
                                                      betaConstraints,
                                                      context.isTupleMemoryEnabled(),
                                                      context,
                                                      from);

        context.setTupleSource( (LeftTupleSource) utils.attachNode( context, node ) );
    }

    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
