package org.drools.core.reteoo.sequencing;

import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;
import org.drools.base.time.impl.Timer;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.sequencing.LogicCircuit.LongBiPredicate;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;

public class LogicGate extends SignalProcessor {
    protected long allMatched;

    private SignalProcessor output;

    private LongBiPredicate predicate;

    private int gateIndex;

    private LogicGate[] inputGates = EMPTY_INPUT_GATES;

    private int[] filterIndexes;

    private int[] signalAdapterIndexes;

    private static final LogicGate[] EMPTY_INPUT_GATES = new LogicGate[0];

    private static final ConditionalSignalCounter[] EMPTY_CONDITIONAL_SIGNAL_COUNTERS = new ConditionalSignalCounter[0];

    private ConditionalSignalCounter[] inputSignalCounters = EMPTY_CONDITIONAL_SIGNAL_COUNTERS;

    private PropagationTimer propagationTimer;

    public LogicGate(LongBiPredicate predicate, int gateIndex, int[] filterIndexes, int[] signalAdapterIndexes, int nbrOfInputGates) {
        this.predicate = predicate;

        this.filterIndexes        = filterIndexes;
        this.signalAdapterIndexes = signalAdapterIndexes;

        for (int i = 0; i < (signalAdapterIndexes.length + nbrOfInputGates); i++) {
            allMatched = allMatched | (1 << i);
        }

        this.gateIndex = gateIndex;
    }

    public int getGateIndex() {
        return gateIndex;
    }

    public int[] getFilterIndexes() {
        return filterIndexes;
    }

    public PropagationTimer getPropagationTimer() {
        return propagationTimer;
    }

    public void setPropagationTimer(PropagationTimer propagationTimer) {
        this.propagationTimer = propagationTimer;
    }

    public int[] getSignalAdapterIndexes() {
        return signalAdapterIndexes;
    }

    public void setSignalAdapterIndexes(int[] signalAdapterIndexes) {
        this.signalAdapterIndexes = signalAdapterIndexes;
    }

    public LogicGate[] getInputGates() {
        return inputGates;
    }

    public void setInputGates(LogicGate... inputGates) {
        this.inputGates = inputGates;
    }

    public ConditionalSignalCounter[] getInputSignalCounters() {
        return inputSignalCounters;
    }

    public void setInputSignalCounters(ConditionalSignalCounter[] inputSignalCounters) {
        this.inputSignalCounters = inputSignalCounters;
    }

    public SignalProcessor getOutput() {
        return output;
    }

    public void setOutput(SignalProcessor output) {
        this.output = output;
    }

    @Override
    public void consume(SignalStatus signalStatus, SequenceMemory memory, ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void consume(int signalBitIndex, SignalStatus signalStatus, SequenceMemory memory, ReteEvaluator reteEvaluator) {
        SignalStatus status = memory.getLogicGateSignalStatus(gateIndex);

        if (status == SignalStatus.FAILED) {
            throw new RuntimeException("Defensive Programming: LogicGate " + gateIndex + " failed");
        }

        SignalStatus priorStatus = status;

        long currentMatched = memory.getLogicGateMemory()[gateIndex];

        switch (signalStatus) {
            case MATCHED:
                currentMatched = currentMatched | (1 << (signalBitIndex - 1)); // ensures position is on, if it wasn't before. If it was on before, it remains on.
                break;
            case UNMATCHED:
                currentMatched = currentMatched & ~(1 << (signalBitIndex - 1)); // ensures position is off, if it wasn't before. If it was off before, it remains off.
                break;
        }

        memory.getLogicGateMemory()[gateIndex] = currentMatched;

        boolean matched = predicate.test(currentMatched, allMatched);

        if (matched) {
            status = SignalStatus.MATCHED;//repetition.newMatch();
        }
//            else {
//                status = SignalStatus.UNMATCHED;
//            }

        memory.setLogicGateSignalStatus(gateIndex, status);
        if (priorStatus != status) {
            if (propagationTimer != null) {
                propagationTimer.matched(memory, reteEvaluator, status);
            } else {
                propapate(memory, reteEvaluator, status);
            }
        }
    }

    public void propapate(SequenceMemory memory, ReteEvaluator reteEvaluator, SignalStatus status) {
        resetPrior(memory, reteEvaluator);
        output.consume(status, memory, reteEvaluator);
    }

    public void resetPrior(SequenceMemory memory, ReteEvaluator reteEvaluator) {
        for (LogicGate gate : inputGates) {
            gate.reset(memory, reteEvaluator);
        }

        memory.resetLogicGateMemory(gateIndex, reteEvaluator);

        for (ConditionalSignalCounter counter : inputSignalCounters) {
            counter.reset(memory, reteEvaluator);
        }
    }

    public void reset(SequenceMemory memory, ReteEvaluator reteEvaluator) {
        resetPrior(memory, reteEvaluator);
        output.reset(memory, reteEvaluator);
    }

    public void activate(SequenceMemory memory, ReteEvaluator reteEvaluator) {
        if (memory.getLogicGateSignalStatus()[gateIndex] == null) {
            memory.getLogicGateSignalStatus()[gateIndex] = SignalStatus.UNMATCHED;
        }
        for (int i = 0; i < filterIndexes.length; i++) {
            memory.activateSignalAdapter(filterIndexes[i], this, signalAdapterIndexes[i], i + 1); // bit indexes start at 1
        }

        if (propagationTimer != null) {
            propagationTimer.activated(memory, reteEvaluator);
        }
//        if (timer != null && timerActionType == LogicGateTimerJobContext.PASS_ON_WAIT) {
//            Trigger trigger = timer.createTrigger(reteEvaluator.getTimerService().getCurrentTime(), null, null);
//            LogicGateTimerJobContext ctx = new LogicGateTimerJobContext(timerActionType, trigger, reteEvaluator, this, memory);
//            JobHandle jobHandle = reteEvaluator.getTimerService().scheduleJob(LogicGateJob.getINSTANCE(), ctx, trigger);
//            memory.setJobHandle(gateIndex, jobHandle);
//        }
    }

    public void deactivate(SequenceMemory memory, ReteEvaluator reteEvaluator) {
        for (int i = 0; i < filterIndexes.length; i++) {
            memory.deactivateSignalAdapter(filterIndexes[i], this, signalAdapterIndexes[i]);
        }

        memory.resetLogicGateMemory(gateIndex, reteEvaluator);
    }

    public interface PropagationTimer {
        default void activated(SequenceMemory memory, ReteEvaluator reteEvaluator)  {

        }

        default void matched(SequenceMemory memory, ReteEvaluator reteEvaluator, SignalStatus status)  {

        }

        default void failed(SequenceMemory memory, ReteEvaluator reteEvaluator)  {

        }
    }

    public static class TimeoutTimer implements PropagationTimer {
        private LogicGate gate;
        private Timer timer;

        public TimeoutTimer(LogicGate gate, Timer timer) {
            this.gate  = gate;
            this.timer = timer;
        }

        @Override
        public void activated(SequenceMemory memory, ReteEvaluator reteEvaluator)  {
            Trigger trigger = timer.createTrigger(reteEvaluator.getTimerService().getCurrentTime(), null, null);
            LogicGateTimerJobContext ctx = new LogicGateTimerJobContext(LogicGateTimerJobContext.TIMEOUT, trigger, reteEvaluator, gate, memory);
            JobHandle jobHandle = reteEvaluator.getTimerService().scheduleJob(LogicGateJob.getINSTANCE(), ctx, trigger);
            memory.setJobHandle(gate.getGateIndex(), jobHandle);
            System.out.println("handle created");
        }

        @Override
        public void matched(SequenceMemory memory, ReteEvaluator reteEvaluator, SignalStatus status)  {
            memory.cancelJobHandle(gate.getGateIndex(), reteEvaluator);
            gate.propapate(memory, reteEvaluator, status);
        }

        @Override
        public void failed(SequenceMemory memory, ReteEvaluator reteEvaluator)  {
            memory.cancelJobHandle(gate.getGateIndex(), reteEvaluator);
        }
    }

    public static class DelayFromActivatedTimer implements PropagationTimer  {
        private LogicGate gate;
        private Timer timer;

        public DelayFromActivatedTimer(LogicGate gate, Timer timer) {
            this.gate  = gate;
            this.timer = timer;
        }

        @Override
        public void activated(SequenceMemory memory, ReteEvaluator reteEvaluator)  {
            Trigger trigger = timer.createTrigger(reteEvaluator.getTimerService().getCurrentTime(), null, null);
            LogicGateTimerJobContext ctx = new LogicGateTimerJobContext(LogicGateTimerJobContext.TIMEOUT, trigger, reteEvaluator, gate, memory);
            JobHandle jobHandle = reteEvaluator.getTimerService().scheduleJob(LogicGateJob.getINSTANCE(), ctx, trigger);
            memory.setJobHandle(gate.getGateIndex(), jobHandle);
        }

        @Override
        public void matched(SequenceMemory memory, ReteEvaluator reteEvaluator, SignalStatus status)  {
            //gate.propapate(memory, reteEvaluator, status);
        }

        @Override
        public void failed(SequenceMemory memory, ReteEvaluator reteEvaluator)  {
            memory.cancelJobHandle(gate.getGateIndex(), reteEvaluator);
        }
    }

    public static class DelayFromMatchTimer implements PropagationTimer  {
        private LogicGate gate;
        private Timer timer;

        public DelayFromMatchTimer(LogicGate gate, Timer timer) {
            this.gate  = gate;
            this.timer = timer;
        }

        @Override
        public void activated(SequenceMemory memory, ReteEvaluator reteEvaluator)  {

        }

        @Override
        public void matched(SequenceMemory memory, ReteEvaluator reteEvaluator, SignalStatus status)  {
            Trigger trigger = timer.createTrigger(reteEvaluator.getTimerService().getCurrentTime(), null, null);
            LogicGateTimerJobContext ctx = new LogicGateTimerJobContext(LogicGateTimerJobContext.DELAY, trigger, reteEvaluator, gate, memory);
            JobHandle jobHandle = reteEvaluator.getTimerService().scheduleJob(LogicGateJob.getINSTANCE(), ctx, trigger);
            memory.setJobHandle(gate.getGateIndex(), jobHandle);
            System.out.println("delayed match");
        }

        @Override
        public void failed(SequenceMemory memory, ReteEvaluator reteEvaluator)  {
            memory.cancelJobHandle(gate.getGateIndex(), reteEvaluator);
        }
    }

    public static class LogicGateJob
            implements
            Job {
        private static LogicGateJob INSTANCE = new LogicGateJob();

        public static LogicGateJob getINSTANCE() {
            return INSTANCE;
        }

        public void execute(JobContext ctx) {
            LogicGateTimerJobContext timerJobCtx   = (LogicGateTimerJobContext) ctx;
            ReteEvaluator       reteEvaluator = timerJobCtx.getReteEvaluator();
            System.out.println("add propagation");
            reteEvaluator.addPropagation( new LogicGateTimerAction(timerJobCtx ));
        }
    }

    public static class LogicGateTimerJobContext
            implements
            JobContext {
        private static final int DELAY   = 0;
        private static final int TIMEOUT = 1;

        private       JobHandle jobHandle;
        private final Trigger   trigger;
        private final ReteEvaluator         reteEvaluator;

        private final LogicGate gate;
        private final SequenceMemory sequenceMemory;

        private final int actionType;

        public LogicGateTimerJobContext(int actionType, Trigger trigger, ReteEvaluator reteEvaluator, LogicGate gate, SequenceMemory sequenceMemory) {
            this.trigger         = trigger;
            this.reteEvaluator   = reteEvaluator;
            this.gate            = gate;
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

        public LogicGate getGate() {
            return gate;
        }

        public SequenceMemory getSequenceMemory() {
            return sequenceMemory;
        }

        public int getActionType() {
            return actionType;
        }
    }

    public static class LogicGateTimerAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {

        private final LogicGateTimerJobContext jobCtx;

        private LogicGateTimerAction( LogicGateTimerJobContext jobCtx) {
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
            LogicGate gate = jobCtx.getGate();
            SequenceMemory sequenceMemory = jobCtx.getSequenceMemory();

            SignalStatus status = sequenceMemory.getLogicGateSignalStatus(gate.getGateIndex());

            sequenceMemory.clearJobHandle(gate.getGateIndex(), reteEvaluator); // clear rather than cancel, as it's actually firing
            System.out.println("execute");

            switch (jobCtx.getActionType()) {
                case LogicGateTimerJobContext.DELAY:
                    if (status == SignalStatus.MATCHED) {
                        // transition
                        gate.propapate(sequenceMemory, reteEvaluator, status);
                        System.out.println("1");
                    } else {
                        // fail
                        sequenceMemory.getSequencerMemory().getNode().getSequencer().fail(sequenceMemory);
                        System.out.println("2");
                    }
                    break;
                case LogicGateTimerJobContext.TIMEOUT:
                    // fail, if not already transitioned
                    if (status != SignalStatus.MATCHED) {
                        // fail
                        sequenceMemory.getSequencerMemory().getNode().getSequencer().fail(sequenceMemory);
                        System.out.println("3");
                    }
                    break;
            }

            // Logic is satsified and waiting to transition
            // Logic is not satsified and has run out of time.
        }

        private void evaluateAndFireRule(PathMemory pmem, ActivationsManager activationsManager) {
//            RuleExecutor ruleExecutor = pmem.getRuleAgendaItem().getRuleExecutor();
//            ruleExecutor.evaluateNetworkIfDirty( activationsManager );
//            ruleExecutor.fire( activationsManager );
        }
    }
}
