package org.drools.core.reteoo.builder;

import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.QueryElement;
import org.drools.base.rule.RuleConditionElement;


public class QueryElementBuilder
    implements
    ReteooComponentBuilder {

    /**
     * @inheritDoc
     */
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {

        final QueryElement qe = (QueryElement) rce;
        context.pushRuleComponent( qe );

        Pattern   resultPattern = qe.getResultPattern();
        final int tupleIndex    = context.getTupleSource() == null ? 0 : context.getTupleSource().getPathIndex() + 1;

        resultPattern.setTupleIndex(tupleIndex);
        resultPattern.setObjectIndex((context.getTupleSource() != null) ? context.getTupleSource().getObjectCount() : 0);

        context.setTupleSource( utils.attachNode( context,
                CoreComponentFactory.get().getNodeFactoryService().buildQueryElementNode(
                                                                        context.getNextNodeId(),
                                                                        context.getTupleSource(),
                                                                        qe,
                                                                        context.isTupleMemoryEnabled(),
                                                                        qe.isOpenQuery(),
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
