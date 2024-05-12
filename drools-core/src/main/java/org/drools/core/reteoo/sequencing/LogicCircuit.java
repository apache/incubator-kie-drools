package org.drools.core.reteoo.sequencing;

import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.MultiInputNode;
import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;

import java.util.function.Consumer;
import java.util.function.LongPredicate;

public class LogicCircuit {

    private MultiInputNode node;

    private LeftTupleSinkNode sink;

    private LogicGate[] gates;

    public LogicCircuit(MultiInputNode node, LogicGate... gates) {
        this.node = node;
        this.gates = gates;
    }

    public LogicGate[] getGates() {
        return gates;
    }

    public void activate(SequencerMemory sequenceMemory) {
        for (LogicGate gate : gates) {
            gate.activate(sequenceMemory);
        }
    }

    public void deactivate(SequencerMemory sequenceMemory) {
        for (LogicGate gate : gates) {
            gate.deactivate(sequenceMemory);
        }
    }

//    @Override
    public void receive(SequenceIndexedRightTuple rightTuple, SequencerMemory circuitMemory) {
//        for ( int i = 0 ; i < gates.size() ; i++ ) {
//            gates[i].receive(rightTuple.getSequenceIndex(), SignalStatus.MATCHED, circuitMemory);
//        }
    }



    public static interface Loop {

        SignalStatus newMatch();

        SignalStatus status();
    }

    public static class Repetitions {
        public static Loop create(IntPredicate p) { return new LambdaCounterRepetition(p);}
        public static Loop one        = new LambdaCounterRepetition((int a) -> a == 1);
        public static Loop zeroOrMore = new LambdaCounterRepetition((int a) -> true);
        public static Loop oneOrMore  = new LambdaCounterRepetition((int a) -> a >= 1);
        public static Loop zeroOrOne  = new LambdaCounterRepetition((int a) -> a <= 1);
    }

    public static class UntilLoop implements Loop {

        @Override
        public SignalStatus newMatch() {
            return null;
        }

        @Override
        public SignalStatus status() {
            return null;
        }
    }

    public static class LambdaCounterRepetition implements Loop {
        private SignalStatus status = SignalStatus.UNMATCHED;
        private int count;

        private IntPredicate predicate;

        public LambdaCounterRepetition(IntPredicate predicate) {
            this.predicate = predicate;
            status = predicate.test(count) ? SignalStatus.MATCHED: SignalStatus.UNMATCHED;
        }

        public SignalStatus newMatch() {
            if (status == SignalStatus.UNMATCHED) {
                status = predicate.test(++count) ? SignalStatus.MATCHED : SignalStatus.UNMATCHED;
            } else if (status == SignalStatus.MATCHED) {
                status = predicate.test(++count) ? SignalStatus.MATCHED : SignalStatus.FAILED;
            }
            return status();
        }

        @Override
        public SignalStatus status() {
            return  status;
        }
    }

    public interface LongBiPredicate {
        boolean test(long a, long b);
    }

    public interface IntPredicate {
        boolean test(int a);
    }

}
