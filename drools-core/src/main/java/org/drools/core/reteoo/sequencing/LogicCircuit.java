package org.drools.core.reteoo.sequencing;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.SequenceNode;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

public class LogicCircuit {

    private SequenceNode node;

    private LeftTupleSinkNode sink;

    private LogicGate[] gates;

    public LogicCircuit(SequenceNode node, LogicGate... gates) {
        this.node = node;
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
