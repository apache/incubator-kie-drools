package org.drools.tms;

import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.AgendaComponentFactory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.common.PropagationContext;
import org.drools.tms.agenda.TruthMaintenanceSystemRuleTerminalNodeLeftTuple;

public class TruthMaintenanceSystemAgendaComponentFactory implements AgendaComponentFactory {

    public TruthMaintenanceSystemAgendaComponentFactory() {
    }

    @Override
    public LeftTuple createTerminalTuple() {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple();
    }

    @Override
    public LeftTuple createTerminalTuple(InternalFactHandle factHandle,
                                         Sink sink,
                                         boolean leftTupleMemoryEnabled) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }

    @Override
    public LeftTuple createTerminalTuple(final InternalFactHandle factHandle,
                                         final LeftTuple leftTuple,
                                         final Sink sink) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(factHandle,leftTuple, sink );
    }

    @Override
    public LeftTuple createTerminalTuple(LeftTuple leftTuple,
                                         Sink sink,
                                         PropagationContext pctx,
                                         boolean leftTupleMemoryEnabled) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    @Override
    public LeftTuple createTerminalTuple(LeftTuple leftTuple,
                                         RightTuple rightTuple,
                                         Sink sink) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(leftTuple, rightTuple, sink );
    }

    @Override
    public LeftTuple createTerminalTuple(LeftTuple leftTuple,
                                         RightTuple rightTuple,
                                         LeftTuple currentLeftChild,
                                         LeftTuple currentRightChild,
                                         Sink sink,
                                         boolean leftTupleMemoryEnabled) {
        return new TruthMaintenanceSystemRuleTerminalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
    }

    @Override
    public RuleAgendaItem createAgendaItem(int salience, PathMemory pmem, TerminalNode rtn, boolean declarativeAgendaEnabled, InternalAgendaGroup agendaGroup) {
        return new RuleAgendaItem(salience, pmem, rtn, declarativeAgendaEnabled, agendaGroup);
    }
}
