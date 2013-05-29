package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TimerNode.TimerNodeMemory;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.DefaultJobHandle;
import org.drools.core.time.impl.Timer;
import org.drools.core.util.index.LeftTupleList;
import org.kie.api.runtime.Calendars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhreakTimerNode {
    private static final Logger log = LoggerFactory.getLogger(PhreakTimerNode.class);

    public void doNode(TimerNode timerNode,
                       TimerNodeMemory tm,
                       PathMemory pmem,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       LeftTupleSets trgLeftTuples,
                       LeftTupleSets stagedLeftTuples) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(timerNode, tm, pmem, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(timerNode, tm, pmem, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(timerNode, tm, pmem, sink, wm, srcLeftTuples, trgLeftTuples);
        }

        doPropagateChildLeftTuples(timerNode, tm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(TimerNode timerNode,
                              TimerNodeMemory tm,
                              PathMemory pmem,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples) {
        Timer timer = timerNode.getTimer();
        TimerService timerService = wm.getTimerService();
        long timestamp = timerService.getCurrentTime();
        String[] calendarNames = timerNode.getCalendarNames();
        Calendars calendars = wm.getCalendars();

        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            scheduleLeftTuple(timerNode, tm, pmem, sink, wm, timer, timerService, timestamp, calendarNames, calendars, leftTuple, trgLeftTuples, null);

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(TimerNode timerNode,
                              TimerNodeMemory tm,
                              PathMemory pmem,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples) {
        Timer timer = timerNode.getTimer();

        // Variables may have changed for ExpressionIntervalTimer, so it must be rescheduled
        TimerService timerService = wm.getTimerService();
        long timestamp = timerService.getCurrentTime();
        String[] calendarNames = timerNode.getCalendarNames();
        Calendars calendars = wm.getCalendars();

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            DefaultJobHandle jobHandle = ( DefaultJobHandle ) leftTuple.getObject();
            timerService.removeJob( jobHandle );
            scheduleLeftTuple(timerNode, tm, pmem, sink, wm, timer, timerService, timestamp, calendarNames, calendars, leftTuple, trgLeftTuples, stagedLeftTuples);

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(TimerNode timerNode,
                              TimerNodeMemory tm,
                              PathMemory pmem,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples) {
        TimerService timerService = wm.getTimerService();


        LeftTupleList leftTuples = tm.getInsertOrUpdateLeftTuples();
        synchronized ( leftTuples ) {
            LeftTupleList deletes = tm.getDeleteLeftTuples();
            if ( !deletes.isEmpty() ) {
                for ( LeftTuple leftTuple = deletes.getFirst(); leftTuple != null;  ) {
                    LeftTuple next =  ( LeftTuple ) leftTuple.getNext();
                    srcLeftTuples.addDelete(leftTuple);
                    leftTuple.clear();
                    leftTuple = next;
                }
                deletes.clear();
            }
            for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                LeftTuple next = leftTuple.getStagedNext();


                DefaultJobHandle jobHandle = ( DefaultJobHandle ) leftTuple.getObject();
                timerService.removeJob( jobHandle );

                if ( leftTuple.getMemory() != null ) {
                    // a delete clashes with insert or update, allow it to propagate once, will handle the deletes the second time around
                    leftTuples.remove( leftTuple );
                    doPropagateChildLeftTuple(sink, trgLeftTuples, stagedLeftTuples, leftTuple);
                    tm.getDeleteLeftTuples().add(leftTuple);
                    pmem.doLinkRule(wm); // make sure it's dirty, so it'll evaluate again
                } else {
                    LeftTuple childLeftTuple = leftTuple.getFirstChild(); // only has one child

                    if ( childLeftTuple != null ) {
                        switch (childLeftTuple.getStagedType()) {
                            // handle clash with already staged entries
                            case LeftTuple.INSERT:
                                stagedLeftTuples.removeInsert(childLeftTuple);
                                break;
                            case LeftTuple.UPDATE:
                                stagedLeftTuples.removeUpdate(childLeftTuple);
                                break;
                        }

                        trgLeftTuples.addDelete( childLeftTuple );
                    }
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }
    }


    private void scheduleLeftTuple(TimerNode timerNode, TimerNodeMemory tm, PathMemory pmem,
                                   LeftTupleSink sink, InternalWorkingMemory wm,
                                   Timer timer, TimerService timerService, long timestamp, String[] calendarNames,
                                   Calendars calendars, LeftTuple leftTuple, LeftTupleSets trgLeftTuples, LeftTupleSets stagedLeftTuples) {
        DefaultJobHandle jobHandle = ( DefaultJobHandle ) leftTuple.getObject();
        Trigger trigger = timer.createTrigger(timestamp, leftTuple, jobHandle, calendarNames, calendars, timerNode.getDeclarations(), wm);

        if ( trigger.hasNextFireTime().getTime() <= timestamp ) {
            // first execution is straight away, so void Scheduling

            doPropagateChildLeftTuple(sink, trgLeftTuples, stagedLeftTuples, leftTuple);

            trigger.nextFireTime();

            if ( trigger.hasNextFireTime().getTime() <= timestamp ) {
                throw new IllegalStateException( "Trigger.nextFireTime is not increasing" );
            }
        }

        TimerNodeJob job = new TimerNodeJob();
        TimerNodeJobContext jobCtx = new TimerNodeJobContext(trigger, leftTuple, tm,  sink, pmem, wm);

        jobHandle = ( DefaultJobHandle ) timerService.scheduleJob(job, jobCtx, trigger);
        leftTuple.setObject( jobHandle );
    }

    public void doPropagateChildLeftTuples(TimerNode timerNode,
                                           TimerNodeMemory tm,
                                           LeftTupleSink sink,
                                           InternalWorkingMemory wm,
                                           LeftTupleSets srcLeftTuples,
                                           LeftTupleSets trgLeftTuples,
                                           LeftTupleSets stagedLeftTuples) {
        LeftTupleList leftTuples = tm.getInsertOrUpdateLeftTuples();
        synchronized ( leftTuples ) {
            for ( LeftTuple leftTuple = leftTuples.getFirst(); leftTuple != null; ) {
                LeftTuple next = ( LeftTuple ) leftTuple.getNext();

                doPropagateChildLeftTuple(sink, trgLeftTuples, stagedLeftTuples, leftTuple);

                leftTuple.clear();
                leftTuple = next;
            }
            // doLeftDeletes handles deletes, directly into the trgLeftTuples

            leftTuples.clear();
        }
    }

    private void doPropagateChildLeftTuple(LeftTupleSink sink, LeftTupleSets trgLeftTuples, LeftTupleSets stagedLeftTuples, LeftTuple leftTuple) {
        LeftTuple childLeftTuple = leftTuple.getFirstChild();
        if ( childLeftTuple == null ) {
            childLeftTuple = sink.createLeftTuple(leftTuple, sink, leftTuple.getPropagationContext(), true);
            trgLeftTuples.addInsert( childLeftTuple );
        } else {
            switch (childLeftTuple.getStagedType()) {
                // handle clash with already staged entries
                case LeftTuple.INSERT:
                    stagedLeftTuples.removeInsert(childLeftTuple);
                    break;
                case LeftTuple.UPDATE:
                    stagedLeftTuples.removeUpdate( childLeftTuple );
                    break;
            }
            trgLeftTuples.addUpdate( childLeftTuple );
        }
    }


    public static class TimerNodeJob implements Job {
        public void execute(JobContext ctx) {
            TimerNodeJobContext timerJobCtx = (TimerNodeJobContext)ctx;
            Trigger trigger = timerJobCtx.getTrigger();

            PathMemory pmem = timerJobCtx.getPathMemory();
            pmem.doLinkRule(timerJobCtx.getWorkingMemory());

            LeftTupleList leftTuples = timerJobCtx.getTimerNodeMemory().getInsertOrUpdateLeftTuples();
            LeftTuple lt = timerJobCtx.getLeftTuple();

            log.trace("Timer Executor {} {}", timerJobCtx.getTrigger(), lt);

            synchronized ( leftTuples ) {
                if ( lt.getMemory() == null ) {
                    // don't add it, if it's already added, which could happen with interval or cron timers
                    leftTuples.add( lt );
                }
            }

            pmem.queueRuleAgendaItem(timerJobCtx.getWorkingMemory());
        }
    }

    public static class TimerNodeJobContext implements JobContext {
        private JobHandle jobHandle;
        private Trigger   trigger;

        private LeftTuple       leftTuple;
        private TimerNodeMemory tm;

        private LeftTupleSink         sink;
        private PathMemory            pmem;
        private InternalWorkingMemory wm;


        public TimerNodeJobContext(Trigger trigger,
                                   LeftTuple leftTuple,
                                   TimerNodeMemory tm,
                                   LeftTupleSink sink,
                                   PathMemory pmem,
                                   InternalWorkingMemory wm) {
            this.trigger = trigger;
            this.leftTuple = leftTuple;
            this.sink = sink;
            this.pmem = pmem;
            this.tm = tm;
            this.wm = wm;
        }

        public JobHandle getJobHandle() {
            return this.jobHandle;
        }

        public void setJobHandle(JobHandle jobHandle) {
            this.jobHandle = jobHandle;
        }

        public LeftTupleSink getSink() {
            return sink;
        }

        public LeftTuple getLeftTuple() {
            return leftTuple;
        }

        public TimerNodeMemory getTimerNodeMemory() {
            return tm;
        }

        public PathMemory getPathMemory() {
            return pmem;
        }

        public InternalWorkingMemory getWorkingMemory() {
            return wm;
        }

        public Trigger getTrigger() {
            return trigger;
        }
    }

}
