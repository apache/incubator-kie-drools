package org.drools.core.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.core.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.core.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

public class LogicCircuitStep implements Step {
    private final Sequence     parentSequence;
    private final LogicCircuit circuit;

    public LogicCircuitStep(Sequence parentSequence, LogicCircuit circuit) {
        this.parentSequence = parentSequence;
        this.circuit        = circuit;
    }

    @Override
    public Sequence getParentSequence() {
        return parentSequence;
    }

    public LogicCircuit getCircuit() {
        return circuit;
    }

    public void activate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        for (LogicGate gate : circuit.getGates()) {
            gate.activate(sequenceMemory, valueResolver);
        }
    }

    public void deactivate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        for (LogicGate gate : circuit.getGates()) {
            gate.deactivate(sequenceMemory, valueResolver);
        }
    }
}
