package org.drools.base.reteoo.sequencing.signalprocessors;

public class LogicCircuit {

    private final LogicGate[] gates;

    public LogicCircuit(LogicGate... gates) {
        this.gates = gates;
    }

    public LogicGate[] getGates() {
        return gates;
    }

    public interface LongBiPredicate {
        boolean test(long a, long b);
    }

}
