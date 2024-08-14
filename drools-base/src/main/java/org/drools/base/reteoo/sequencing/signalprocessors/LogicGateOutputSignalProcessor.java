package org.drools.base.reteoo.sequencing.signalprocessors;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;

public class LogicGateOutputSignalProcessor extends SignalProcessor {
    private final SignalIndex[] gates;

    private LogicGate gate1;
    private int       index1;
    private LogicGate gate2;
    private int       index2;
    private LogicGate gate3;
    private int       index3;
    private LogicGate gate4;
    private int       index4;


    public LogicGateOutputSignalProcessor(SignalIndex... gates) {
        this.gates       = gates;

        switch (gates.length) {
            case 4:
                gate4 = gates[3].getGate();
                index4 = gates[3].getBitIndex();
            case 3:
                gate3 = gates[2].getGate();
                index3 = gates[2].getBitIndex();
            case 2:
                gate2 = gates[1].getGate();
                index2 = gates[1].getBitIndex();
            case 1:
                gate1 = gates[0].getGate();
                index1 = gates[0].getBitIndex();
                break;
        }
    }

    public void consume(SignalStatus signalStatus, SequenceMemory memory, ValueResolver valueResolver) {
        switch (gates.length) {
            case 4:
                gate4.consume(index4, signalStatus, memory, valueResolver);
            case 3:
                gate3.consume(index3, signalStatus, memory, valueResolver);
            case 2:
                gate2.consume(index2, signalStatus, memory, valueResolver);
            case 1:
                gate1.consume(index1, signalStatus, memory, valueResolver);
                break;
            default:
                for (int i = gates.length - 1; i >= 0; i--) {
                    gates[i].getGate().consume(gates[i].getBitIndex(), signalStatus, memory, valueResolver);
                }
        }
    }

    @Override
    public void consume(int signalBitIndex, SignalStatus signalStatus, SequenceMemory memory, ValueResolver valueResolver) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void reset(SequenceMemory memory, ValueResolver valueResolver) {
        // Do nothing
    }
}
