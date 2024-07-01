package org.drools.core.reteoo.sequencing;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

public interface Step {
    void activate(SequenceMemory memory, ReteEvaluator reteEvaluator);

    void deactivate(SequenceMemory memory, ReteEvaluator reteEvaluator);

    static StepFactory of(LogicCircuit circuit) {
        return new StepFactory(circuit, null);
    }

    static StepFactory of(Sequence sequence) {
        return new StepFactory(null, sequence);
    }

    Sequence getParentSequence();

    class StepFactory {
        LogicCircuit circuit;
        Sequence sequence;

        private StepFactory(LogicCircuit circuit, Sequence sequence) {
            this.circuit  = circuit;
            this.sequence = sequence;
        }

        public Step createStep(Sequence parentSequence) {
            if (circuit != null) {
                return new LogicCircuitStep(parentSequence, circuit);
            }  else {
                return new SequenceStep(parentSequence, sequence);
            }
        }
    }

    class LogicCircuitStep implements Step {
        private Sequence     parentSequence;
        private LogicCircuit circuit;

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

        @Override
        public void activate(SequenceMemory memory, ReteEvaluator reteEvaluator) {
            circuit.activate(memory, reteEvaluator);
        }

        @Override
        public void deactivate(SequenceMemory memory, ReteEvaluator reteEvaluator) {
            circuit.deactivate(memory, reteEvaluator);
        }
    }

    class SequenceStep implements Step {
        private Sequence parentSequence;
        private Sequence sequence;

        public SequenceStep(Sequence parentSequence, Sequence sequence) {
            this.parentSequence = parentSequence;
            this.sequence = sequence;
        }
        public Sequence getParentSequence() {
            return parentSequence;
        }

        public Sequence getSequence() {
            return sequence;
        }

        @Override
        public void activate(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator) {
            sequence.start(sequenceMemory.getSequencerMemory(), reteEvaluator);
        }

        @Override
        public void deactivate(SequenceMemory memory, ReteEvaluator reteEvaluator) {
        }
    }
}
