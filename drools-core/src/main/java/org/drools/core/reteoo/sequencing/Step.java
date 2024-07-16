package org.drools.core.reteoo.sequencing;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.core.util.CircularArrayList;

import java.util.function.Consumer;

public interface Step {
    void activate(SequenceMemory memory, ReteEvaluator reteEvaluator);

    void deactivate(SequenceMemory memory, ReteEvaluator reteEvaluator);

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
        LOGIC_CIRCUIT, SEQUENCE, AGGREGATOR, ACTION;
    }

    class StepFactory {
        private StepFactoryType type;
        private LogicCircuit circuit;
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

        public Step createStep(Sequence parentSequence) {
            switch (type) {
                case LOGIC_CIRCUIT:
                    return new LogicCircuitStep(parentSequence, circuit);
                case AGGREGATOR:
                    return new AggregatorStep(parentSequence, sequence, aggregator);
                case SEQUENCE:
                    return new SequenceStep(parentSequence, sequence);
                case ACTION:
                    return new ActionStep(parentSequence, action);
            }
            throw new IllegalArgumentException("Unsupported step type: " + type);
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
        protected Sequence parentSequence;
        protected Sequence sequence;

        public SequenceStep(Sequence parentSequence, Sequence sequence) {
            this.parentSequence = parentSequence;
            this.sequence = sequence;
        }

        @Override
        public Sequence getParentSequence() {
            return parentSequence;
        }


        public Sequence getSequence() {
            return sequence;
        }

        @Override
        public void activate(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator) {
            if (parentSequence != null) {
                // reserved for any context or return data
                // Also in the future it could be used to optional collect and hold nested array of subevents for later reference.
                CircularArrayList<Object> events = sequenceMemory.getSequencerMemory().getEvents();

                SequenceMemory subSequenceMemory = sequenceMemory.getSequencerMemory().getSequenceMemory(sequence);
                events.addEmpty(subSequenceMemory.getSequence().getOutputSize());
                subSequenceMemory.setEventsStartPosition(sequenceMemory.getSequencerMemory().getEvents().size());
            }
            sequence.start(sequenceMemory.getSequencerMemory(), reteEvaluator);
        }

        @Override
        public void deactivate(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator) {
            if (parentSequence != null) {
                SequenceMemory subSequenceMemory = sequenceMemory.getSequencerMemory().getSequenceMemory(sequence);
                CircularArrayList<Object> events = sequenceMemory.getSequencerMemory().getEvents();
                events.resetHeadByOffset(events.size() - subSequenceMemory.getEventsStartPosition());
            }
        }
    }

    class AggregatorStep extends SequenceStep implements Step {
        public AggregatorStep(Sequence parentSequence, Sequence sequence, Consumer<SequenceMemory> consumer) {
            super(parentSequence, sequence);
            sequence.setOnEnd(consumer);
        }
    }

    class ActionStep implements Step {
        protected Sequence parentSequence;
        private Consumer<SequenceMemory> consumer;

        public ActionStep(Sequence parentSequence, Consumer<SequenceMemory> consumer) {
            this.parentSequence = parentSequence;
            this.consumer       = consumer;
        }

        @Override
        public void activate(SequenceMemory memory, ReteEvaluator reteEvaluator) {
            consumer.accept(memory);
            memory.getSequence().next(memory, reteEvaluator); // transitions as soon as the action is fired
        }

        @Override
        public void deactivate(SequenceMemory memory, ReteEvaluator reteEvaluator) {
        }

        @Override
        public Sequence getParentSequence() {
            return parentSequence;
        }
    }
}
