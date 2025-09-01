package org.drools.base.reteoo.sequencing;

import org.drools.base.base.ValueResolver;
import org.drools.base.phreak.actions.AbstractPropagationEntry;
import org.drools.base.reteoo.DynamicFilter;
import org.drools.base.reteoo.sequencing.signalprocessors.ConditionalSignalCounter;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.reteoo.sequencing.signalprocessors.SignalStatus;
import org.drools.base.reteoo.sequencing.steps.LogicCircuitStep;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.reteoo.sequencing.steps.Step.StepFactory;
import org.drools.base.reteoo.sequencing.steps.SubsequenceStep;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;
import org.drools.base.time.Timer;
import org.drools.base.reteoo.SignalAdapter;
import org.drools.base.time.Job;
import org.drools.base.time.JobContext;
import org.drools.base.util.CircularArrayList;
import org.kie.api.runtime.rule.FactHandle;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Sequence implements RuleConditionElement {
    private final int sequenceIndex;

    private final SubsequenceStep parentStep;

    //private Step;
    private Parent parent;

    private Pattern[] filters;

    private Step[] steps;

    private LogicGate[] gates;

    private SequenceController controller;

    private Consumer<SequenceMemory> onStart;
    private Consumer<SequenceMemory> onEnd;

    private int outputSize;

    private int subsequenceIndex = -1; // -1 is for when this is not parallel

    public Sequence(int sequenceIndex, SubsequenceStep parentStep, StepFactory... stepFactories) {
        this.steps = new Step[stepFactories.length];
        this.sequenceIndex = sequenceIndex;
        this.parentStep = parentStep;
        this.controller = new DefaultController();

        for ( int i = 0; i < steps.length; i++ ) {
            steps[i] = stepFactories[i].createStep(i,this);
        }
        populateLogicGates();
    }

    public Sequence(int sequenceIndex, StepFactory... stepFactories) {
        this(sequenceIndex, null, stepFactories);
    }

    public Pattern[] getFilters() {
        return filters;
    }

    public void setFilters(Pattern[] filters) {
        this.filters = filters;
    }

    public int getOutputSize() {
        return outputSize;
    }

    public int getSubsequenceIndex() {
        return subsequenceIndex;
    }

    public void setSubsequenceIndex(int subsequenceIndex) {
        this.subsequenceIndex = subsequenceIndex;
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

    @Override
    public Map<String, Declaration> getInnerDeclarations() {
        return Map.of();
    }

    @Override
    public Map<String, Declaration> getOuterDeclarations() {
        return Map.of();
    }

    @Override
    public Declaration resolveDeclaration(String identifier) {
        return null;
    }

    @Override
    public RuleConditionElement clone() {
        return null;
    }

    @Override
    public List<? extends RuleConditionElement> getNestedElements() {
        return List.of();
    }

    @Override
    public boolean isPatternScopeDelimiter() {
        return false;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void start(SequenceMemory memory, ValueResolver valueResolver) {
        controller.start(memory, valueResolver);
    }

    private void restart(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        sequenceMemory.setStep(0);
        sequenceMemory.getData().resetHeadByOffset(sequenceMemory.getSequencerMemory().getData().size() - sequenceMemory.getEventsStartPosition());
        getSteps()[0].activate(sequenceMemory, valueResolver);
    }

    public void next(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
         controller.next(sequenceMemory, valueResolver);
    }

    public interface SequenceController {
        default void start(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
            sequenceMemory.setStep(0);
            sequenceMemory.sequence.steps[0].activate(sequenceMemory, valueResolver);
            if(sequenceMemory.sequence.onStart != null) {
                sequenceMemory.sequence.onStart.accept(sequenceMemory);
            }
        }

        default void restart(SequenceMemory memory, ValueResolver valueResolver) {

        }

        default void next(SequenceMemory memory, ValueResolver valueResolver) {

        }

        void end(SequenceMemory memory, ValueResolver valueResolver);
    }

    public static class DefaultController implements SequenceController {
        private static final DefaultController INSTANCE = new DefaultController();

        public static DefaultController getINSTANCE() {
            return INSTANCE;
        }

        public void next(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
            Sequence sequence = sequenceMemory.getSequence();
            int step = sequenceMemory.getStep();

            sequence.steps[step].deactivate(sequenceMemory, valueResolver);
            step = sequenceMemory.incrementStep();

            if (step < sequenceMemory.getSequence().getSteps().length) {
                sequence.steps[step].activate(sequenceMemory, valueResolver);
            } else {
                if(sequence.onEnd != null) {
                    sequence.onEnd.accept(sequenceMemory);
                }
                end(sequenceMemory, valueResolver);
            }
        }

        @Override
        public void end(SequenceMemory sequenceMemory, ValueResolver valueResolver)  {
            SequenceMemory parent = sequenceMemory.getParent();
            if (parent != null) {
                SequenceMemory parentSeqMemory = parent.getSequencerMemory().getSequenceMemory(parent.getSequence());
                parent.getSequence().next(parentSeqMemory, valueResolver);
            } else {
                sequenceMemory.getSequencerMemory().match(valueResolver);
            }
        }

        @Override
        public String toString() {
            return "DefaultController{}";
        }
    }

    public static class LoopController extends DefaultController implements SequenceController {
        private final Predicate<SequenceMemory> predicate;

        private final SequenceController defaultController = DefaultController.getINSTANCE();

        public LoopController(Predicate<SequenceMemory> predicate) {
            this.predicate = predicate;
        }


//        public void restart(SequenceMemory memory, ValueResolver valueResolver) {
//            defaultController.restart(memory, valueResolver);
//        }
//
//        public void next(SequenceMemory memory, ValueResolver valueResolver) {
//            defaultController.next(memory, valueResolver);
//        }

        @Override
        public void end(SequenceMemory sequenceMemory, ValueResolver valueResolver)  {
            int counts = sequenceMemory.getCount();
            boolean restart = predicate.test(sequenceMemory);
            sequenceMemory.setCount(counts+1);
            SequencerMemory sequencerMemory = sequenceMemory.getSequencerMemory();
            if (restart) {
                sequenceMemory.getSequence().restart(sequenceMemory, valueResolver);
            } else {
                defaultController.end(sequenceMemory, valueResolver);
            }
        }

        @Override
        public String toString() {
            return "LoopController{}";
        }
    }

    public static class TimoutController implements SequenceController {
        private final Timer    timer;
        private final DefaultController defaultController = DefaultController.getINSTANCE();

        public TimoutController(Timer timer) {
            this.timer    = timer;
        }

        @Override
        public void start(SequenceMemory memory, ValueResolver valueResolver) {
            defaultController.start(memory, valueResolver);
            Trigger                 trigger   = timer.createTrigger(valueResolver.getTimerService().getCurrentTime(), null, null);
            SequenceTimerJobContext ctx       = new SequenceTimerJobContext(SequenceTimerJobContext.TIMEOUT, trigger, valueResolver, memory);

            JobHandle               jobHandle = valueResolver.getTimerService().scheduleJob(SequenceJob.getINSTANCE(), ctx, trigger);
            memory.setJobHandle(jobHandle);
            System.out.println("handle created");
        }

        @Override
        public void restart(SequenceMemory memory, ValueResolver valueResolver) {
            defaultController.restart(memory, valueResolver);
            valueResolver.getTimerService().removeJob(memory.getJobHandle());
        }

        @Override
        public void end(SequenceMemory memory, ValueResolver valueResolver)  {
            defaultController.end(memory, valueResolver);
            valueResolver.getTimerService().removeJob(memory.getJobHandle());
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
        private final SequenceController[] controllers;

        public CompositeController(SequenceController... controllers) {
            this.controllers = controllers;
        }

        @Override
        public void start(SequenceMemory memory, ValueResolver valueResolver) {
            for ( SequenceController controller : controllers ) {
                controller.start(memory, valueResolver);
            }
        }

        @Override
        public void restart(SequenceMemory memory, ValueResolver valueResolver) {
            for ( SequenceController controller : controllers ) {
                controller.restart(memory, valueResolver);
            }
        }

        @Override
        public void end(SequenceMemory memory, ValueResolver valueResolver)  {
            for ( SequenceController controller : controllers ) {
                controller.end(memory, valueResolver);
            }
        }
    }


    public static class SequenceJob
            implements
            Job {
        private static final SequenceJob INSTANCE = new SequenceJob();

        public static SequenceJob getINSTANCE() {
            return INSTANCE;
        }

        public void execute(JobContext ctx) {
            SequenceTimerJobContext timerJobCtx   = (SequenceTimerJobContext) ctx;
            ValueResolver           valueResolver = timerJobCtx.getValueResolver();
            System.out.println("add propagation");
            valueResolver.addPropagation( new SequenceTimerAction(timerJobCtx ));
        }
    }

    public static class SequenceTimerJobContext
            implements
            JobContext {
        private static final int DELAY   = 0;
        private static final int TIMEOUT = 1;

        private       JobHandle     jobHandle;
        private final Trigger       trigger;
        private final ValueResolver valueResolver;

        private final SequenceMemory sequenceMemory;

        private final int actionType;

        public SequenceTimerJobContext(int actionType, Trigger trigger, ValueResolver valueResolver, SequenceMemory sequenceMemory) {
            this.trigger         = trigger;
            this.valueResolver   = valueResolver;
            this.sequenceMemory = sequenceMemory;
            this.actionType = actionType;
        }

        public JobHandle getJobHandle() {
            return this.jobHandle;
        }

        @Override
        public ValueResolver getValueResolver() {
            return valueResolver;
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

    public static class SequenceTimerAction extends AbstractPropagationEntry<ValueResolver> {

        private final SequenceTimerJobContext jobCtx;

        private SequenceTimerAction(SequenceTimerJobContext jobCtx) {
            this.jobCtx = jobCtx;
        }

        @Override
        public boolean requiresImmediateFlushing() {
            return true;
        }

        @Override
        public void internalExecute(final ValueResolver reteEvaluator) {
            execute( reteEvaluator, false );
        }

        public void execute( final ValueResolver reteEvaluator, boolean needEvaluation ) {
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

    public void fail(SequenceMemory sequenceMemory, ValueResolver valueResolver) {
        int index = sequenceMemory.getStep();
        Step step = sequenceMemory.getSequence().getSteps()[index];
        step.onFail(sequenceMemory, valueResolver);
    }

    public static class SequenceMemory {
        private SequenceMemory parent;

        private final Sequence sequence;

        private int step;

        private int count;

        private final SequencerMemory sequencerMemory;

        private final SignalAdapter[] signalAdapters;

        private final SignalAdapter[] activeSignalAdapters;

        private final long[] gateMemory;

        private final long[] counterMemories;

        private JobHandle[] jobHandles;

        private JobHandle jobHandle;

        private final SignalStatus[] signalStatuses;

        private int eventsStartPosition;

        private CircularArrayList<Object> data;

        public SequenceMemory(SequencerMemory sequencerMemory, Sequence sequence, CircularArrayList<Object> data,
                              SignalAdapter[] signalAdapters, SignalAdapter[] activeSignalAdapters,
                              long[] gateMemory, long[] counterMemories) {
            this(sequencerMemory, null, sequence, data, signalAdapters, activeSignalAdapters, gateMemory, counterMemories);
        }

        public SequenceMemory(SequencerMemory sequencerMemory, SequenceMemory parent, Sequence sequence, CircularArrayList<Object> data,
                              SignalAdapter[] signalAdapters, SignalAdapter[] activeSignalAdapters,
                              long[] gateMemory, long[] counterMemories) {
            this.sequencerMemory      = sequencerMemory;
            this.parent               = parent;
            this.data                 = data;
            this.sequence             = sequence;
            this.signalAdapters       = signalAdapters;
            this.activeSignalAdapters = activeSignalAdapters;
            this.gateMemory           = gateMemory;
            this.counterMemories      = counterMemories;
            this.signalStatuses       = new SignalStatus[gateMemory.length + counterMemories.length];
        }

        public SequenceMemory getParent() {
            return parent;
        }

        public void setParent(SequenceMemory parent) {
            this.parent = parent;
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

        public void addData(FactHandle handle) {
            data.add(handle);
        }

        public CircularArrayList<Object> getData() {
            return data;
        }

        public int getOutputStartPosition() {
            if (sequence.getSubsequenceIndex() == -1) {
                return eventsStartPosition - sequence.getOutputSize();
            } else {
                int i = eventsStartPosition - ((sequence.getSubsequenceIndex() + 1) * sequence.getOutputSize());
                System.out.println("eventsStartPosition = " + i);
                return i;
            }
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

            DynamicFilter filter = sequencerMemory.getActiveDynamicFilter(filterIndex);
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

            DynamicFilter filter = sequencerMemory.getActiveDynamicFilter(filterIndex);
            filter.removeSignalAdapter(signalAdapter);

            if (filter.getSignalAdapters().isEmpty()) {
                sequencerMemory.removeActiveFilter(filter);
            }
        }

        public void resetLogicGateMemory(int gateIndex, ValueResolver valueResolver) {
            gateMemory[gateIndex]     = 0;
            signalStatuses[gateIndex] = null;
        }

        public void resetSignalCounterMemory(int counterIndex) {
            signalStatuses[gateMemory.length + counterIndex] = null;
            counterMemories[counterIndex]                    = 0;
        }

        public void cancelJobHandle(int gateIndex, ValueResolver valueResolver) {
            if (jobHandles != null) {
                JobHandle handle = jobHandles[gateIndex];
                valueResolver.getTimerService().removeJob(handle);
                jobHandles[gateIndex] = null;
                System.out.println("Job handle cancelled: " + handle);
            }
        }

        public void clearJobHandle(int gateIndex, ValueResolver valueResolver) {
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
