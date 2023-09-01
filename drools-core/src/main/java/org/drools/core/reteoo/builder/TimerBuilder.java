package org.drools.core.reteoo.builder;

import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.RuleConditionElement;
import org.drools.core.time.impl.BaseTimer;
import org.drools.base.time.impl.Timer;

public class TimerBuilder
    implements
    ReteooComponentBuilder {

    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {
        final Timer timer = (Timer) rce;
        context.pushRuleComponent( timer );

        Declaration[][] declrs = timer instanceof BaseTimer ?
                                 ((BaseTimer)timer).getTimerDeclarations(context.getSubRule().getOuterDeclarations()) :
                                 null;

        context.setTupleSource( utils.attachNode( context,
                CoreComponentFactory.get().getNodeFactoryService().buildTimerNode( context.getNextNodeId(),
                                                                                                      timer,
                                                                                                      context.getRule().getCalendars(),
                                                                                                      declrs,
                                                                                                      context.getTupleSource(),
                                                                                                      context  ) ) );

        context.setAlphaConstraints( null );
        context.setBetaconstraints( null );
        context.popRuleComponent();
    }

    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
