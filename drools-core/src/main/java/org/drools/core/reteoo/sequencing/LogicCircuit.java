package org.drools.core.reteoo.sequencing;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

public class LogicCircuit {

    private final LogicGate[] gates;

    public LogicCircuit(LogicGate... gates) {
        this.gates = gates;
    }

    public LogicGate[] getGates() {
        return gates;
    }

    public void activate(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator) {
        for (LogicGate gate : gates) {
            gate.activate(sequenceMemory, reteEvaluator);
        }
    }

    public void deactivate(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator) {
        for (LogicGate gate : gates) {
            gate.deactivate(sequenceMemory, reteEvaluator);
        }
    }

    public interface LongBiPredicate {
        boolean test(long a, long b);
    }

}
