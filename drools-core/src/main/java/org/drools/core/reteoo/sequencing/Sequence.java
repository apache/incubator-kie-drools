package org.drools.core.reteoo.sequencing;

import org.drools.core.reteoo.sequencing.Sequencer.SequenceMemory;
import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;

public class Sequence {
    Step[] steps;

    public Sequence(LogicCircuit... circuits) {
        steps = new Step[circuits.length];
        for ( int i = 0; i < circuits.length; i++ ) {
            steps[i] = new LogicCircuitStep(circuits[i]);
        }
    }

    public Sequence(Step... steps) {
        this.steps = steps;
    }

    public Step[] getSteps() {
        return steps;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }

    public interface Step {
        public void activate(SequencerMemory memory);
        public void deactivate(SequencerMemory memory);

        public static Step of(LogicCircuit circuit) {
            return new LogicCircuitStep(circuit);
        }

        public static Step of(Sequence sequence) {
            return new SequenceStep(sequence);
        }
    }

    public static class LogicCircuitStep implements Step {
        private LogicCircuit circuit;

        public LogicCircuitStep(LogicCircuit circuit) {
            this.circuit = circuit;
        }

        public LogicCircuit getCircuit() {
            return circuit;
        }

        @Override
        public void activate(SequencerMemory memory) {
            circuit.activate(memory);
        }

        @Override
        public void deactivate(SequencerMemory memory) {
            circuit.deactivate(memory);
        }
    }

    public static class SequenceStep implements Step {
        private Sequence sequence;

        public SequenceStep(Sequence sequence) {
            this.sequence = sequence;
        }

        public Sequence getSequence() {
            return sequence;
        }

        @Override
        public void activate(SequencerMemory memory) {
            memory.pushSequence(new SequenceMemory(sequence));
            sequence.getSteps()[0].activate(memory);
        }

        @Override
        public void deactivate(SequencerMemory memory) {
        }
    }
}
