package org.drools.core.reteoo.builder;

import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.RuleConditionElement;

public class EvalBuilder
    implements
    ReteooComponentBuilder {

    /**
     * @inheritDoc
     */
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {

        final EvalCondition eval = (EvalCondition) rce;
        context.pushRuleComponent( rce );

        context.setTupleSource( utils.attachNode( context,
                CoreComponentFactory.get()
                                                         .getNodeFactoryService()
                                                         .buildEvalNode( context.getNextNodeId(),
                                                                         context.getTupleSource(),
                                                                         eval,
                                                                         context ) ) );
        context.popRuleComponent();

    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
