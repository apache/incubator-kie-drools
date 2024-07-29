package org.drools.core.reteoo.sequencing.signalprocessors;

public class SignalIndex {
    private final LogicGate gate;
    private final int       bitIndex;

    public static SignalIndex of(LogicGate gate, int bitIndex) {
        return new SignalIndex(gate, bitIndex);
    }

    public SignalIndex(LogicGate gate, int bitIndex) {
        this.gate     = gate;
        this.bitIndex = bitIndex;
    }

    public LogicGate getGate() {
        return gate;
    }

    public int getBitIndex() {
        return bitIndex;
    }
}
