package org.drools.core.phreak;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.reteoo.*;
import org.drools.reteoo.PathMemory;
import org.drools.core.spi.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleNetworkEvaluatorActivation extends AgendaItem {

    private static final Logger log = LoggerFactory.getLogger(RuleNetworkEvaluatorActivation.class);

    private PathMemory rmem;

    private static RuleNetworkEvaluator networkEvaluator = new RuleNetworkEvaluator();

    public RuleNetworkEvaluatorActivation() {

    }

    public RuleNetworkEvaluatorActivation(final long activationNumber,
                                          final LeftTuple tuple,
                                          final int salience,
                                          final PropagationContext context,
                                          final PathMemory rmem,
                                          final TerminalNode rtn) {
        super(activationNumber, tuple, salience, context, rtn);
        this.rmem = rmem;
    }

    public int evaluateNetwork(InternalWorkingMemory wm) {
        return this.networkEvaluator.evaluateNetwork(rmem, wm);
    }

    public boolean isRuleNetworkEvaluatorActivation() {
        return true;
    }

}
