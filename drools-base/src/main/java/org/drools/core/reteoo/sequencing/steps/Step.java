package org.drools.core.reteoo.sequencing.steps;

import org.drools.base.base.ValueResolver;
import org.drools.core.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;

import java.util.function.Consumer;

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
        LOGIC_CIRCUIT, SEQUENCE, AGGREGATOR, ACTION
    }

    class StepFactory {
        private final StepFactoryType type;
        private       LogicCircuit    circuit;
        private Sequence sequence;
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
            }
            throw new IllegalArgumentException("Unsupported step type: " + type);
        }
    }

}
