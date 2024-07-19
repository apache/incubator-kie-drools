package org.drools.core.reteoo.sequencing;

import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;
import org.drools.base.time.impl.Timer;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.SequenceNode.DynamicFilter;
import org.drools.core.reteoo.SequenceNode.SequenceNodeMemory;
import org.drools.core.reteoo.SequenceNode.SignalAdapter;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;
import org.drools.core.reteoo.sequencing.Step.LogicCircuitStep;
import org.drools.core.reteoo.sequencing.Step.StepFactory;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Sequence {
    private int sequenceIndex;

    private Step[] steps;

    private LogicGate[] gates;

    private SequenceController controller;

    private Consumer<SequenceMemory> onStart;
    private Consumer<SequenceMemory> onEnd;

    private int outputSize;

    public Sequence(int sequenceIndex, LogicCircuit... circuits) {
        this.steps = new Step[circuits.length];
        for ( int i = 0; i < circuits.length; i++ ) {
            this.steps[i] = new LogicCircuitStep(this, circuits[i]);
        }
        this.sequenceIndex = sequenceIndex;
        populateLogicGates();
        this.controller = new DefaultController();
        this.outputSize = 0;
    }

    public Sequence(int sequenceIndex, Step... steps) {
        this.steps = steps;
        this.sequenceIndex = sequenceIndex;
        populateLogicGates();
        controller = new DefaultController();
    }

    public Sequence(int sequenceIndex, StepFactory... stepFactories) {
        this.steps = new Step[stepFactories.length];
        for ( int i = 0; i < steps.length; i++ ) {
            steps[i] = stepFactories[i].createStep(this);
        }
        this.sequenceIndex = sequenceIndex;
        populateLogicGates();
        controller = new DefaultController();
    }

    public int getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(int outputSize) {
        this.outputSize = outputSize;
    }

    public Consumer<SequenceMemory> getOnStart() {
        return onStart;
    }

    public void setOnStart(Consumer<SequenceMemory> onStart) {
        this.onStart = onStart;
    }

    public Consumer<SequenceMemory> getOnEnd() {
        return onEnd;
    }

    public void setOnEnd(Consumer<SequenceMemory> onEnd) {
        this.onEnd = onEnd;
    }

    public void populateLogicGates() {
        List<LogicGate> list = new ArrayList<>();
        for (Step step : getSteps()) {
            if ( step instanceof LogicCircuitStep) {
                Arrays.stream(((LogicCircuitStep) step).getCircuit().getGates()).forEach( g -> list.add(g));
            }
        }

        gates = list.toArray(new LogicGate[0]);
    }



    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public Step[] getSteps() {
        return steps;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }

    public LogicGate[] getGates() {
        return gates;
    }


    public SequenceController getController() {
        return controller;
    }

    public void setController(SequenceController controller) {
        this.controller = controller;
    }

    public void start(SequencerMemory memory, ReteEvaluator reteEvaluator) {
        SequenceMemory sequenceMemory = memory.getSequenceMemory(this);
        memory.pushSequence(sequenceMemory);
        sequenceMemory.setStep(0);
        getSteps()[0].activate(sequenceMemory, reteEvaluator);
        if(onStart != null) {
            onStart.accept(sequenceMemory);
        }
        controller.start(sequenceMemory, reteEvaluator);
    }

    private void restart(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator) {
        sequenceMemory.setStep(0);
        sequenceMemory.getSequencerMemory().getEvents().resetHeadByOffset(sequenceMemory.getSequencerMemory().getEvents().size() - sequenceMemory.getEventsStartPosition());
        getSteps()[0].activate(sequenceMemory, reteEvaluator);
    }

    public void next(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator) {
        int step = sequenceMemory.getStep();

        sequenceMemory.getSequence().getSteps()[step].deactivate(sequenceMemory, reteEvaluator);
        step = sequenceMemory.incrementStep();

        if (step < sequenceMemory.getSequence().getSteps().length) {
            sequenceMemory.getSequence().getSteps()[step].activate(sequenceMemory, reteEvaluator);
        } else {
            if(onEnd != null) {
                onEnd.accept(sequenceMemory);
            }
            controller.end(sequenceMemory, reteEvaluator);
        }
    }

    public interface SequenceController {
        default void start(SequenceMemory memory, ReteEvaluator reteEvaluator) {

        }

        default void restart(SequenceMemory memory, ReteEvaluator reteEvaluator) {

        }

        void end(SequenceMemory memory, ReteEvaluator reteEvaluator);
    }

    public static class DefaultController implements SequenceController {
        private static final DefaultController INSTANCE = new DefaultController();

        public static DefaultController getINSTANCE() {
            return INSTANCE;
        }

        @Override
        public void end(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator)  {
            SequencerMemory sequencerMemory = sequenceMemory.getSequencerMemory();
            sequencerMemory.popSequence(); // pop is here, but the push was in the start step
            sequencerMemory.getNode().getSequencer().next(sequencerMemory, reteEvaluator);
        }

        @Override
        public String toString() {
            return "DefaultController{}";
        }
    }

    public static class LoopController implements SequenceController {
        private Predicate<SequenceMemory> predicate;

        public LoopController(Predicate<SequenceMemory> predicate) {
            this.predicate = predicate;
        }

        @Override
        public void end(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator)  {
            int counts = sequenceMemory.getCount();
            boolean restart = predicate.test(sequenceMemory);
            sequenceMemory.setCount(counts+1);
            SequencerMemory sequencerMemory = sequenceMemory.getSequencerMemory();
            if (restart) {
                sequenceMemory.getSequence().restart(sequenceMemory, reteEvaluator);
            } else {
                sequencerMemory.popSequence(); // pop is here, but the push was in the start step
                sequencerMemory.getNode().getSequencer().next(sequencerMemory, reteEvaluator);
            }
        }

        @Override
        public String toString() {
            return "LoopController{}";
        }
    }

    public static class TimoutController implements SequenceController {
        private Sequence sequence;
        private Timer     timer;
        private DefaultController defaultController = DefaultController.getINSTANCE();

        public TimoutController(Sequence sequence, Timer timer) {
            this.sequence = sequence;
            this.timer    = timer;
        }

        @Override
        public void start(SequenceMemory memory, ReteEvaluator reteEvaluator) {
            defaultController.start(memory, reteEvaluator);
            Trigger                 trigger   = timer.createTrigger(reteEvaluator.getTimerService().getCurrentTime(), null, null);
            SequenceTimerJobContext ctx       = new SequenceTimerJobContext(SequenceTimerJobContext.TIMEOUT, trigger, reteEvaluator, memory);

            JobHandle               jobHandle = reteEvaluator.getTimerService().scheduleJob(SequenceJob.getINSTANCE(), ctx, trigger);
            memory.setJobHandle(jobHandle);
            System.out.println("handle created");
        }

        @Override
        public void restart(SequenceMemory memory, ReteEvaluator reteEvaluator) {
            defaultController.restart(memory, reteEvaluator);
            reteEvaluator.getTimerService().removeJob(memory.getJobHandle());
        }

        @Override
        public void end(SequenceMemory memory, ReteEvaluator reteEvaluator)  {
            defaultController.end(memory, reteEvaluator);
            reteEvaluator.getTimerService().removeJob(memory.getJobHandle());
            memory.setJobHandle(null);
        }

        @Override
        public String toString() {
            return "TimoutController{" +
                   "timer=" + timer +
                   '}';
        }
    }

    public static class CompositeController implements SequenceController {
        private  SequenceController[] controllers;

        public CompositeController(SequenceController... controllers) {
            this.controllers = controllers;
        }

        @Override
        public void start(SequenceMemory memory, ReteEvaluator reteEvaluator) {
            for ( SequenceController controller : controllers ) {
                controller.start(memory, reteEvaluator);
            }
        }

        @Override
        public void restart(SequenceMemory memory, ReteEvaluator reteEvaluator) {
            for ( SequenceController controller : controllers ) {
                controller.restart(memory, reteEvaluator);
            }
        }

        @Override
        public void end(SequenceMemory memory, ReteEvaluator reteEvaluator)  {
            for ( SequenceController controller : controllers ) {
                controller.end(memory, reteEvaluator);
            }
        }
    }


    public static class SequenceJob
            implements
            Job {
        private static SequenceJob INSTANCE = new SequenceJob();

        public static SequenceJob getINSTANCE() {
            return INSTANCE;
        }

        public void execute(JobContext ctx) {
            SequenceTimerJobContext timerJobCtx   = (SequenceTimerJobContext) ctx;
            ReteEvaluator           reteEvaluator = timerJobCtx.getReteEvaluator();
            System.out.println("add propagation");
            reteEvaluator.addPropagation( new SequenceTimerAction(timerJobCtx ));
        }
    }

    public static class SequenceTimerJobContext
            implements
            JobContext {
        private static final int DELAY   = 0;
        private static final int TIMEOUT = 1;

        private       JobHandle     jobHandle;
        private final Trigger       trigger;
        private final ReteEvaluator reteEvaluator;

        private final SequenceMemory sequenceMemory;

        private final int actionType;

        public SequenceTimerJobContext(int actionType, Trigger trigger, ReteEvaluator reteEvaluator, SequenceMemory sequenceMemory) {
            this.trigger         = trigger;
            this.reteEvaluator   = reteEvaluator;
            this.sequenceMemory = sequenceMemory;
            this.actionType = actionType;
        }

        public JobHandle getJobHandle() {
            return this.jobHandle;
        }

        @Override
        public ReteEvaluator getReteEvaluator() {
            return reteEvaluator;
        }

        public void setJobHandle(JobHandle jobHandle) {
            this.jobHandle = jobHandle;
        }

        public Trigger getTrigger() {
            return trigger;
        }

        public SequenceMemory getSequenceMemory() {
            return sequenceMemory;
        }

        public int getActionType() {
            return actionType;
        }
    }

    public static class SequenceTimerAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {

        private final SequenceTimerJobContext jobCtx;

        private SequenceTimerAction( SequenceTimerJobContext jobCtx) {
            this.jobCtx = jobCtx;
        }

        @Override
        public boolean requiresImmediateFlushing() {
            return jobCtx.getReteEvaluator().getRuleSessionConfiguration().getTimedRuleExecutionFilter() != null;
        }

        @Override
        public void internalExecute(final ReteEvaluator reteEvaluator) {
            execute( reteEvaluator, false );
        }

        public void execute( final ReteEvaluator reteEvaluator, boolean needEvaluation ) {
            SequenceMemory sequenceMemory = jobCtx.getSequenceMemory();

            switch (jobCtx.getActionType()) {
                case SequenceTimerJobContext.DELAY:
//                    if (status == SignalStatus.MATCHED) {
//                        // transition
//                        gate.propapate(sequencerMemory, reteEvaluator, status);
//                        System.out.println("1");
//                    } else {
//                        // fail
////                        sequencerMemory.getNode().getSequencer().fail(sequencerMemory);
//                        System.out.println("2");
//                    }
                        System.out.println("1");
                    break;
                case SequenceTimerJobContext.TIMEOUT:
                    // fail, if not already transitioned
//                    if (status != SignalStatus.MATCHED) {
//                        // fail
//                        sequencerMemory.getNode().getSequencer().fail(sequencerMemory);
                    sequenceMemory.getSequence().fail(sequenceMemory, reteEvaluator);
                        System.out.println("2");
//                    }
                    break;
            }
        }
    }

    private void fail(SequenceMemory sequenceMemory, ReteEvaluator reteEvaluator) {
        int step = sequenceMemory.getStep();

        sequenceMemory.getSequence().getSteps()[step].deactivate(sequenceMemory, reteEvaluator);

        SequencerMemory sequencerMemory = sequenceMemory.getSequencerMemory();
        sequencerMemory.popSequence();
        System.out.println("fail timeout");
    }

    public static class SequenceMemory {
        private Sequence sequence;

        private int step;

        private int count;

        private SequencerMemory sequencerMemory;

        private SignalAdapter[] signalAdapters;

        private SignalAdapter[] activeSignalAdapters;

        private long[] gateMemory;

        private long[] counterMemories;

        private JobHandle[] jobHandles;

        private JobHandle jobHandle;

        private SignalStatus[] signalStatuses;

        private SequenceNodeMemory nodeMemory;

        private int eventsStartPosition;

        public SequenceMemory(SequencerMemory sequencerMemory, Sequence sequence,
                              SignalAdapter[] signalAdapters, SignalAdapter[] activeSignalAdapters,
                              long[] gateMemory, long[] counterMemories, SequenceNodeMemory nodeMemory) {
            this.sequencerMemory      = sequencerMemory;
            this.sequence             = sequence;
            this.signalAdapters       = signalAdapters;
            this.activeSignalAdapters = activeSignalAdapters;
            this.gateMemory           = gateMemory;
            this.counterMemories      = counterMemories;
            this.nodeMemory           = nodeMemory;
            this.signalStatuses       = new SignalStatus[gateMemory.length + counterMemories.length];
        }


        public Sequence getSequence() {
            return sequence;
        }

        public SequencerMemory getSequencerMemory() {
            return sequencerMemory;
        }


        public SignalStatus getCounterSignalStatus(int index) {
            return signalStatuses[gateMemory.length + index];
        }

        public void setCounterSignalStatus(int index, SignalStatus status) {
            signalStatuses[gateMemory.length + index] = status;
        }


        public SignalStatus getLogicGateSignalStatus(int index) {
            return signalStatuses[index];
        }

        public SignalStatus[] getLogicGateSignalStatus() {
            return signalStatuses;
        }

        public void setLogicGateSignalStatus(int index, SignalStatus status) {
            signalStatuses[index] = status;
        }

        public SignalAdapter[] getSignalAdapters() {
            return signalAdapters;
        }

        public SignalAdapter[] getActiveSignalAdapters() {
            return activeSignalAdapters;
        }

        public long[] getLogicGateMemory() {
            return gateMemory;
        }

        public long[] getCounterMemories() {
            return counterMemories;
        }

        public JobHandle[] getJobHandles() {
            return jobHandles;
        }

        public JobHandle getJobHandle() {
            return jobHandle;
        }

        public void setJobHandle(JobHandle jobHandle) {
            this.jobHandle = jobHandle;
        }

        public SignalStatus[] getSignalStatuses() {
            return signalStatuses;
        }

        public SequenceNodeMemory getNodeMemory() {
            return nodeMemory;
        }

        public int incrementStep() {
            return ++step;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getEventsStartPosition() {
            return eventsStartPosition;
        }

        public void setEventsStartPosition(int eventsStartPosition) {
            this.eventsStartPosition = eventsStartPosition;
        }

        public SignalAdapter activateSignalAdapter(int filterIndex, LogicGate gate, int signalAdapterIndex, int signalBitIndex) {
            if (activeSignalAdapters[signalAdapterIndex] != null) {
                throw new RuntimeException("Defensive coding, this should not be re-entrant");
            }

            SignalAdapter signalAdapter = signalAdapters[signalAdapterIndex];

            if (signalAdapter == null) {
                ConditionalSignalCounter counter = null;
                for ( ConditionalSignalCounter c : gate.getInputSignalCounters()) {
                    if ( c.getSignalIndex() == signalAdapterIndex) {
                        counter = c;
                        break;
                    }
                }
                signalAdapter = new SignalAdapter(counter == null ? gate : counter, signalBitIndex, this);
                signalAdapters[signalAdapterIndex] = signalAdapter;
            }

            activeSignalAdapters[signalAdapterIndex] = signalAdapter;

            DynamicFilter filter = nodeMemory.getActiveDynamicFilter(filterIndex);
            filter.addSignalAdapter(signalAdapter);

            return signalAdapter;
        }

        public void setJobHandle(int index, JobHandle handle) {
            if (jobHandles == null) {
                // lazily create
                jobHandles = new JobHandle[gateMemory.length]; // each gate can potentially have a job handle
            }
            jobHandles[index] = handle;
        }


        public JobHandle getJobHandle(int index) {
            return jobHandles != null ? jobHandles[index] : null;
        }

        public void deactivateSignalAdapter(int filterIndex, LogicGate gate, int signalAdapterIndex) {
            SignalAdapter signalAdapter = activeSignalAdapters[signalAdapterIndex];
            activeSignalAdapters[signalAdapterIndex] = null;

            DynamicFilter filter = nodeMemory.getActiveDynamicFilter(filterIndex);
            filter.removeSignalAdapter(signalAdapter);

            if (filter.getSignalAdapters().isEmpty()) {
                nodeMemory.removeActiveFilter(filter);
            }
        }

        public void resetLogicGateMemory(int gateIndex, ReteEvaluator reteEvaluator) {
            gateMemory[gateIndex]     = 0;
            signalStatuses[gateIndex] = null;
        }

        public void resetSignalCounterMemory(int counterIndex) {
            signalStatuses[gateMemory.length + counterIndex] = null;
            counterMemories[counterIndex]                    = 0;
        }

        public void cancelJobHandle(int gateIndex, ReteEvaluator reteEvaluator) {
            if (jobHandles != null) {
                JobHandle handle = jobHandles[gateIndex];
                reteEvaluator.getTimerService().removeJob(handle);
                jobHandles[gateIndex] = null;
                System.out.println("Job handle cancelled: " + handle);
            }
        }

        public void clearJobHandle(int gateIndex, ReteEvaluator reteEvaluator) {
            jobHandles[gateIndex] = null;
            System.out.println("Job handle cleared: ");
        }

        @Override
        public String toString() {
            return "SequenceMemory{" +
                   "sequence=" + sequence.getSequenceIndex() +
                   ", step=" + step +
                   ", count=" + count +
                   '}';
        }
    }

    @Override
    public String toString() {
        return "Sequence{" +
               "sequenceIndex=" + sequenceIndex +
               ", steps=" + Arrays.toString(steps) +
               ", controller=" + controller +
               '}';
    }
}
