package org.drools.core.reteoo.sequencing;

import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;

public class LogicGateOutputSignalProcessor extends SignalProcessor {
    private SignalIndex[] gates;

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

    public void propagate(SignalStatus signalStatus, SequencerMemory memory) {
        switch (gates.length) {
            case 4:
                gate4.receive(index4, signalStatus, memory);
            case 3:
                gate3.receive(index3, signalStatus, memory);
            case 2:
                gate2.receive(index2, signalStatus, memory);
            case 1:
                gate1.receive(index1, signalStatus, memory);
                break;
            default:
                for (int i = gates.length - 1; i >= 0; i--) {
                    gates[i].getGate().receive(gates[i].getBitIndex(), signalStatus, memory);
                }
        }
    }

    @Override
    public void receive(int signalBitIndex, SignalStatus signalStatus, SequencerMemory memory) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void reset(SequencerMemory memory) {
        // Do nothing
    }
}
