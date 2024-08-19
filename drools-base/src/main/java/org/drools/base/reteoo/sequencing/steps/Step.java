package org.drools.base.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface Step {
    void activate(SequenceMemory memory, ValueResolver valueResolver);

    void deactivate(SequenceMemory memory, ValueResolver valueResolver);

    void onFail(SequenceMemory memory, ValueResolver valueResolver);

    static StepFactory of(LogicCircuit circuit) {
        StepFactory factory = new StepFactory(StepFactoryType.LOGIC_CIRCUIT);
        factory.setCircuit(circuit);
        return factory;
    }

    static StepFactory of(Sequence sequence) {
        StepFactory factory = new StepFactory(StepFactoryType.SEQUENCE);
        factory.setSequence(sequence);
        return factory;
    }

    static StepFactory of(Sequence... sequences) {
        StepFactory factory = new StepFactory(StepFactoryType.PARALLEL);
        factory.setSequences(sequences);
        return factory;
    }

    static StepFactory of(Sequence sequence, Consumer<SequenceMemory> function) {
        StepFactory factory = new StepFactory(StepFactoryType.AGGREGATOR);
        factory.setAggregator(function);
        factory.setSequence(sequence);
        return factory;
    }

    static StepFactory of(Consumer<SequenceMemory> function) {
        StepFactory factory = new StepFactory(StepFactoryType.ACTION);
        factory.setAction(function);
        return factory;
    }

    Sequence getParentSequence();


    enum StepFactoryType {
        LOGIC_CIRCUIT, SEQUENCE, AGGREGATOR, ACTION, PARALLEL
    }

    class StepFactory {
        private final StepFactoryType type;
        private       LogicCircuit    circuit;
        private Sequence sequence;
        private Sequence[] sequences;
        private Consumer<SequenceMemory> aggregator;
        private Consumer<SequenceMemory> action;

        public StepFactory(StepFactoryType type) {
            this.type = type;
        }

        public StepFactoryType getType() {
            return type;
        }

        public LogicCircuit getCircuit() {
            return circuit;
        }

        public void setCircuit(LogicCircuit circuit) {
            this.circuit = circuit;
        }

        public Sequence getSequence() {
            return sequence;
        }

        public void setSequence(Sequence sequence) {
            this.sequence = sequence;
        }

        public Sequence[] getSequences() {
            return sequences;
        }

        public void setSequences(Sequence[] sequences) {
            this.sequences = sequences;
        }

        public Consumer<SequenceMemory> getAggregator() {
            return aggregator;
        }

        public void setAggregator(Consumer<SequenceMemory> aggregator) {
            this.aggregator = aggregator;
        }

        public Consumer<SequenceMemory> getAction() {
            return action;
        }

        public void setAction(Consumer<SequenceMemory> action) {
            this.action = action;
        }

        public Step createStep(int index, Sequence parentSequence) {
            switch (type) {
                case LOGIC_CIRCUIT:
                    return new LogicCircuitStep(index, parentSequence, circuit);
                case AGGREGATOR:
                    return new AggregatorStep(index, parentSequence, sequence, aggregator);
                case SEQUENCE:
                    return new SequenceStep(index, parentSequence, sequence);
                case ACTION:
                    return new ActionStep(index, parentSequence, action);
                case PARALLEL:
                    SequenceStep[] steps = new SequenceStep[sequences.length];
                    for ( int i = 0; i < steps.length; i++ ) {
                        steps[i] = (SequenceStep) Step.of(sequences[i]).createStep(i,parentSequence);
                    }
                    int outputSize = 0;
                    // Parallel output size must be the same size as the largest output.
                    for (int i = 0; i < sequences.length; i++ ) {
                        Sequence s = sequences[i];
                        s.setSubsequenceIndex(i);
                        if (outputSize < s.getOutputSize() ) {
                            outputSize = s.getOutputSize();
                        }
                    }
                    return new ParallelStep(index, outputSize, parentSequence, steps);
            }
            throw new IllegalArgumentException("Unsupported step type: " + type);
        }
    }

}
