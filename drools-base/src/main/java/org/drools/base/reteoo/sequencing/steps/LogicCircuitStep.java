package org.drools.base.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;

public class LogicCircuitStep extends AbstractStep implements Step {
    private final LogicCircuit match;

    public LogicCircuitStep(int index, Sequence sequence, LogicCircuit circuit) {
        super(StepType.LOGIC_CIRCUIT, index, sequence);
        this.match = circuit;
    }

    public LogicCircuit getCircuit() {
        return match;
    }

    public void activate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        for (LogicGate gate : match.getGates()) {
            gate.activate(sequenceMemory, valueResolver);
        }
    }

    public void deactivate(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        for (LogicGate gate : match.getGates()) {
            gate.deactivate(sequenceMemory, valueResolver);
        }
    }
}
