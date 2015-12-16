/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NetworkNode;
import org.drools.core.common.TupleSets;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller.TupleKey;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers.TimerNodeTimer;
import org.drools.core.marshalling.impl.ProtobufOutputMarshaller;
import org.drools.core.marshalling.impl.TimersInputMarshaller;
import org.drools.core.marshalling.impl.TimersOutputMarshaller;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TimerNode;
import org.drools.core.reteoo.TimerNode.TimerNodeMemory;
import org.drools.core.spi.Tuple;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.DefaultJobHandle;
import org.drools.core.time.impl.Timer;
import org.drools.core.util.LinkedList;
import org.drools.core.util.index.TupleList;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.conf.TimedRuleExecutionFilter;
import org.kie.api.runtime.rule.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class PhreakTimerNode {
    private static final Logger log = LoggerFactory.getLogger( PhreakTimerNode.class );

    public void doNode(TimerNode timerNode,
                       TimerNodeMemory tm,
                       PathMemory pmem,
                       SegmentMemory smem,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        if ( srcLeftTuples.getDeleteFirst() != null ) {
            doLeftDeletes( tm, pmem, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
        }

        if ( srcLeftTuples.getUpdateFirst() != null ) {
            doLeftUpdates( timerNode, tm, pmem, smem, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
        }

        if ( srcLeftTuples.getInsertFirst() != null ) {
            doLeftInserts( timerNode, tm, pmem, smem, sink, wm, srcLeftTuples, trgLeftTuples );
        }

        doPropagateChildLeftTuples( tm, sink, trgLeftTuples, stagedLeftTuples );

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(TimerNode timerNode,
                              TimerNodeMemory tm,
                              PathMemory pmem,
                              SegmentMemory smem,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples) {
        Timer timer = timerNode.getTimer();
        TimerService timerService = wm.getTimerService();
        long timestamp = timerService.getCurrentTime();
        String[] calendarNames = timerNode.getCalendarNames();
        Calendars calendars = wm.getCalendars();

        for ( LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            scheduleLeftTuple( timerNode, tm, pmem, smem, sink, wm, timer, timerService, timestamp, calendarNames, calendars, leftTuple, trgLeftTuples, null );

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(TimerNode timerNode,
                              TimerNodeMemory tm,
                              PathMemory pmem,
                              SegmentMemory smem,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples) {
        Timer timer = timerNode.getTimer();

        // Variables may have changed for ExpressionIntervalTimer, so it must be rescheduled
        TimerService timerService = wm.getTimerService();
        long timestamp = timerService.getCurrentTime();
        String[] calendarNames = timerNode.getCalendarNames();
        Calendars calendars = wm.getCalendars();

        for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            DefaultJobHandle jobHandle = (DefaultJobHandle) leftTuple.getContextObject();
            if ( jobHandle != null ) {
                // jobHandle can be null, if the time fired straight away, and never ended up scheduling a job
                timerService.removeJob(jobHandle);
            }
            scheduleLeftTuple( timerNode, tm, pmem, smem, sink, wm, timer, timerService, timestamp, calendarNames, calendars, leftTuple, trgLeftTuples, stagedLeftTuples );

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(TimerNodeMemory tm,
                              PathMemory pmem,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples) {
        TimerService timerService = wm.getTimerService();

        TupleList leftTuples = tm.getInsertOrUpdateLeftTuples();
        TupleList deletes = tm.getDeleteLeftTuples();
        if ( !deletes.isEmpty() ) {
            for ( LeftTuple leftTuple = (LeftTuple) deletes.getFirst(); leftTuple != null; ) {
                LeftTuple next = (LeftTuple) leftTuple.getNext();
                srcLeftTuples.addDelete( leftTuple );
                if ( log.isTraceEnabled() ) {
                    log.trace( "Timer Add Postponed Delete {}", leftTuple );
                }
                leftTuple.clear();
                leftTuple = next;
            }
            deletes.clear();
        }
        for ( LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            DefaultJobHandle jobHandle = (DefaultJobHandle) leftTuple.getContextObject();
            if ( jobHandle != null ) {
                // jobHandle can be null, if the time fired straight away, and never ended up scheduling a job
                timerService.removeJob( jobHandle );
            }

            org.drools.core.spi.PropagationContext pctx = leftTuple.getPropagationContext();
            pctx = RuleTerminalNode.findMostRecentPropagationContext( leftTuple, pctx );

            if ( leftTuple.getMemory() != null ) {
                leftTuples.remove( leftTuple ); // it gets removed either way.
                if ( pctx.getType() == PropagationContext.EXPIRATION ) {
                    // a expire clashes with insert or update, allow it to propagate once, will handle the expire the second time around
                    doPropagateChildLeftTuple( sink, trgLeftTuples, stagedLeftTuples, leftTuple );
                    tm.getDeleteLeftTuples().add( leftTuple );
                    pmem.doLinkRule( wm ); // make sure it's dirty, so it'll evaluate again
                    if ( log.isTraceEnabled() ) {
                        log.trace( "Timer Postponed Delete {}", leftTuple );
                    }
                }
            }

            if ( leftTuple.getMemory() == null ) {
                // if it's != null, then it's already been postponed, and the existing child propagated
                LeftTuple childLeftTuple = leftTuple.getFirstChild(); // only has one child
                if ( childLeftTuple != null ) {
                    childLeftTuple.setPropagationContext( leftTuple.getPropagationContext() );
                    RuleNetworkEvaluator.deleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    if ( log.isTraceEnabled() ) {
                        log.trace( "Timer Delete {}", leftTuple );
                    }
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    private void scheduleLeftTuple(final TimerNode timerNode,
                                   final TimerNodeMemory tm,
                                   final PathMemory pmem,
                                   final SegmentMemory smem,
                                   final LeftTupleSink sink,
                                   final InternalWorkingMemory wm,
                                   final Timer timer,
                                   final TimerService timerService,
                                   final long timestamp,
                                   final String[] calendarNames,
                                   final Calendars calendars,
                                   final LeftTuple leftTuple,
                                   final TupleSets<LeftTuple> trgLeftTuples,
                                   final TupleSets<LeftTuple> stagedLeftTuples) {
        if( leftTuple.getPropagationContext().getReaderContext() == null ) {
            final Trigger trigger = createTrigger( timerNode, wm, timer, timestamp, calendarNames, calendars, leftTuple );

            // regular propagation
            scheduleTimer( timerNode, tm, smem, sink, wm, timerService, timestamp, leftTuple, trgLeftTuples, stagedLeftTuples, trigger );
        } else {
            // de-serializing, so we need to correlate timers before scheduling them
            Scheduler scheduler = new Scheduler() {
                @Override
                public void schedule( Trigger t ) {
                    scheduleTimer( timerNode, tm, smem, sink, wm, timerService, timestamp, leftTuple, trgLeftTuples, stagedLeftTuples, t );
                    evaluate( pmem, wm, sink, tm, trgLeftTuples );
                }
                @Override
                public Trigger getTrigger() {
                    return createTrigger( timerNode, wm, timer, timestamp, calendarNames, calendars, leftTuple );
                }
            };
            leftTuple.getPropagationContext().getReaderContext().addTimerNodeScheduler( timerNode.getId(),
                                                                                        PersisterHelper.createTupleKey( leftTuple ),
                                                                                        scheduler );
        }
    }

    private Trigger createTrigger(final TimerNode timerNode,
                                  final InternalWorkingMemory wm,
                                  final Timer timer,
                                  final long timestamp,
                                  final String[] calendarNames,
                                  final Calendars calendars,
                                  final LeftTuple leftTuple) {
        final DefaultJobHandle jobHandle = (DefaultJobHandle) leftTuple.getContextObject();
        return timer.createTrigger( timestamp, leftTuple, jobHandle, calendarNames, calendars, timerNode.getDeclarations(), wm );
    }

    public interface Scheduler {
        void schedule( Trigger t );
        Trigger getTrigger();
    }

    private void scheduleTimer(TimerNode timerNode,
                               TimerNodeMemory tm,
                               SegmentMemory smem,
                               LeftTupleSink sink,
                               InternalWorkingMemory wm,
                               TimerService timerService,
                               long timestamp,
                               LeftTuple leftTuple,
                               TupleSets<LeftTuple> trgLeftTuples,
                               TupleSets<LeftTuple> stagedLeftTuples,
                               Trigger trigger) {
        if ( trigger.hasNextFireTime() == null ) {
            return;
        }

        if ( trigger.hasNextFireTime().getTime() <= timestamp ) {
            // first execution is straight away, so void Scheduling
            if ( log.isTraceEnabled() ) {
                log.trace( "Timer Fire Now {}", leftTuple );
            }

            LeftTuple childLeftTuple = doPropagateChildLeftTuple( sink, trgLeftTuples, stagedLeftTuples, leftTuple );
            if (childLeftTuple.getStagedType() != LeftTuple.NONE) {
                // Flag the newly created childLeftTuple to avoid a reevaluation in case it gets
                // rescheduled before the end of this doNode loop
                childLeftTuple.setContextObject( Boolean.TRUE );
            }

            trigger.nextFireTime();

            Date nextFireTime = trigger.hasNextFireTime();
            if ( nextFireTime != null && nextFireTime.getTime() <= timestamp ) {
                throw new IllegalStateException( "Trigger.nextFireTime is not increasing" );
            }
        }

        if ( trigger.hasNextFireTime() != null ) {
            // can be null, if the system timestamp has surpassed when this was suppose to fire
            TimerNodeJob job = new TimerNodeJob();
            TimerNodeJobContext jobCtx = new TimerNodeJobContext( timerNode.getId(), trigger, leftTuple, tm, sink, smem.getPathMemories(), wm );

            DefaultJobHandle jobHandle = (DefaultJobHandle) timerService.scheduleJob( job, jobCtx, trigger );
            leftTuple.setContextObject( jobHandle );

            if ( log.isTraceEnabled() ) {
                log.trace( "Timer Scheduled {}", leftTuple );
            }
        }
    }

    public static void doPropagateChildLeftTuples(TimerNodeMemory tm,
                                                  LeftTupleSink sink,
                                                  TupleSets<LeftTuple> trgLeftTuples,
                                                  TupleSets<LeftTuple> stagedLeftTuples) {
        TupleList leftTuples = tm.getInsertOrUpdateLeftTuples();
        for ( LeftTuple leftTuple = (LeftTuple) leftTuples.getFirst(); leftTuple != null; ) {
            LeftTuple next = (LeftTuple) leftTuple.getNext();

            doPropagateChildLeftTuple( sink, trgLeftTuples, stagedLeftTuples, leftTuple );

            leftTuple.clear();
            leftTuple = next;
        }
        // doLeftDeletes handles deletes, directly into the trgLeftTuples
        if ( tm.getDeleteLeftTuples().isEmpty() ) {
            // dirty bit can only be reset when there are no InsertOUdate LeftTuples and no Delete staged LeftTuples.
            tm.setNodeCleanWithoutNotify();
        }
        leftTuples.clear();
    }

    private static LeftTuple doPropagateChildLeftTuple(LeftTupleSink sink,
                                                       TupleSets<LeftTuple> trgLeftTuples,
                                                       TupleSets<LeftTuple> stagedLeftTuples,
                                                       LeftTuple leftTuple) {
        LeftTuple childLeftTuple = leftTuple.getFirstChild();
        if ( childLeftTuple == null ) {
            childLeftTuple = sink.createLeftTuple( leftTuple, sink, leftTuple.getPropagationContext(), true );
            trgLeftTuples.addInsert(childLeftTuple);
            if ( log.isTraceEnabled() ) {
                log.trace( "Timer Insert {}", childLeftTuple );
            }
        } else if (childLeftTuple.getContextObject() == Boolean.TRUE) {
            // This childLeftTuple has been created in this doNode loop, just skip it
            childLeftTuple.setContextObject( null );
        } else {
            switch ( childLeftTuple.getStagedType() ) {
                // handle clash with already staged entries
                case LeftTuple.INSERT :
                    stagedLeftTuples.removeInsert( childLeftTuple );
                    break;
                case LeftTuple.UPDATE :
                    stagedLeftTuples.removeUpdate( childLeftTuple );
                    break;
            }
            trgLeftTuples.addUpdate( childLeftTuple );
            if ( log.isTraceEnabled() ) {
                log.trace( "Timer Update {}", childLeftTuple );
            }
        }
        return childLeftTuple;
    }

    public static class TimerNodeJob
            implements
            Job {
        public void execute(JobContext ctx) {
            TimerNodeJobContext timerJobCtx = (TimerNodeJobContext) ctx;
            InternalWorkingMemory wm = timerJobCtx.getWorkingMemory();
            wm.addPropagation( new TimerAction( timerJobCtx ) );
        }
    }

    public static class TimerAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {

        private final TimerNodeJobContext timerJobCtx;

        private TimerAction( TimerNodeJobContext timerJobCtx ) {
            this.timerJobCtx = timerJobCtx;
        }

        @Override
        public ProtobufMessages.ActionQueue.Action serialize( MarshallerWriteContext context ) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean requiresImmediateFlushing() {
            return timerJobCtx.getWorkingMemory().getSessionConfiguration().getTimedRuleExecutionFilter() != null;
        }

        @Override
        public void execute( final InternalWorkingMemory wm ) {
            execute( wm, false );
        }

        public void execute( final InternalWorkingMemory wm, boolean needEvaluation ) {
            TupleList leftTuples = timerJobCtx.getTimerNodeMemory().getInsertOrUpdateLeftTuples();
            Tuple lt = timerJobCtx.getTuple();

            if ( log.isTraceEnabled() ) {
                log.trace( "Timer Executor {} {}", timerJobCtx.getTrigger(), lt );
            }

            if ( timerJobCtx.getJobHandle().isCancel() ) {
                // this is to force a sync point, as during update propagate it can cancel the FH
                // we cannot have an update processed at the same timer is firing
                return;
            }
            if ( lt.getMemory() == null ) {
                // don't add it, if it's already added, which could happen with interval or cron timers
                leftTuples.add( lt );
            }

            timerJobCtx.getTimerNodeMemory().setNodeDirtyWithoutNotify();

            TimedRuleExecutionFilter filter = wm.getSessionConfiguration().getTimedRuleExecutionFilter();
            needEvaluation &= filter != null;

            for (final PathMemory pmem : timerJobCtx.getPathMemories()) {
                pmem.doLinkRule( wm );

                if (needEvaluation && filter.accept(new Rule[]{pmem.getRule()})) {
                    evaluateAndFireRule( pmem, wm );
                }
            }
        }

        private void evaluateAndFireRule(PathMemory pmem, InternalWorkingMemory wm) {
            RuleExecutor ruleExecutor = pmem.getRuleAgendaItem().getRuleExecutor();
            ruleExecutor.reEvaluateNetwork( wm );
            ruleExecutor.fire(wm);
        }
    }

    private static LinkedList<StackEntry> evaluate(PathMemory pmem,
                                                   InternalWorkingMemory wm,
                                                   LeftTupleSink sink,
                                                   TimerNodeMemory tm,
                                                   TupleSets<LeftTuple> trgLeftTuples) {
        SegmentMemory[] smems = pmem.getSegmentMemories();
        LeftInputAdapterNode lian = ( LeftInputAdapterNode ) smems[0].getRootNode();

        SegmentMemory sm = tm.getSegmentMemory();
        int smemIndex = 0;
        for (SegmentMemory smem : smems) {
            if (smem == sm) {
                break;
            }
            smemIndex++;
        }

        long bit = 1;
        for (NetworkNode node = sm.getRootNode(); node != sink; node = ((LeftTupleSource)node).getSinkPropagator().getFirstLeftTupleSink() ) {
            //update the bit to the correct node position.
            bit = bit << 1;
        }

        RuleNetworkEvaluator rne = new RuleNetworkEvaluator();
        LinkedList<StackEntry> outerStack = new LinkedList<StackEntry>();

        rne.outerEval(lian, pmem, sink, bit, tm,
                      smems, smemIndex, trgLeftTuples,
                      wm, new LinkedList<StackEntry>(), true,
                      pmem.getRuleAgendaItem().getRuleExecutor());
        return outerStack;
    }

    public static class TimerNodeJobContext
            implements
            JobContext {
        private       JobHandle             jobHandle;
        private final Trigger               trigger;

        private final Tuple                 tuple;
        private final int                   timerNodeId;
        private final TimerNodeMemory       tm;

        private final LeftTupleSink         sink;
        private final List<PathMemory>      pmems;
        private final InternalWorkingMemory wm;

        public TimerNodeJobContext(int timerNodeId,
                                   Trigger trigger,
                                   Tuple tuple,
                                   TimerNodeMemory tm,
                                   LeftTupleSink sink,
                                   List<PathMemory> pmems,
                                   InternalWorkingMemory wm) {
            this.timerNodeId = timerNodeId;
            this.trigger = trigger;
            this.tuple = tuple;
            this.sink = sink;
            this.pmems = pmems;
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

        public Tuple getTuple() {
            return tuple;
        }

        public TimerNodeMemory getTimerNodeMemory() {
            return tm;
        }

        public List<PathMemory> getPathMemories() {
            return pmems;
        }

        public InternalWorkingMemory getWorkingMemory() {
            return wm;
        }

        public Trigger getTrigger() {
            return trigger;
        }

        public int getTimerNodeId() {
            return timerNodeId;
        }
    }

    public static class TimerNodeTimerOutputMarshaller
            implements
            TimersOutputMarshaller {

        public ProtobufMessages.Timers.Timer serialize(JobContext jobCtx,
                                                       MarshallerWriteContext outputCtx) {
            // TimerNodeJobContext   
            TimerNodeJobContext tnJobCtx = (TimerNodeJobContext) jobCtx;

            return ProtobufMessages.Timers.Timer.newBuilder()
                                          .setType( ProtobufMessages.Timers.TimerType.TIMER_NODE )
                                          .setTimerNode( ProtobufMessages.Timers.TimerNodeTimer.newBuilder()
                                                                                .setNodeId( tnJobCtx.getTimerNodeId() )
                                                                                .setTuple( PersisterHelper.createTuple( tnJobCtx.getTuple() ) )
                                                                                .setTrigger( ProtobufOutputMarshaller.writeTrigger( tnJobCtx.getTrigger(),
                                                                                                                                    outputCtx ) )
                                                                                .build() )
                                          .build();
        }
    }

    public static class TimerNodeTimerInputMarshaller
            implements
            TimersInputMarshaller {

        public void deserialize(MarshallerReaderContext inCtx,
                                ProtobufMessages.Timers.Timer _timer) throws ClassNotFoundException {
            TimerNodeTimer _tn = _timer.getTimerNode();

            int timerNodeId = _tn.getNodeId();
            TupleKey tuple = PersisterHelper.createTupleKey( _tn.getTuple() );
            Trigger trigger = ProtobufInputMarshaller.readTrigger( inCtx,
                                                                   _tn.getTrigger() );

            Scheduler scheduler = inCtx.removeTimerNodeScheduler( timerNodeId, tuple );
            if( scheduler != null ) {
                scheduler.schedule( trigger );
            }
        }
    }

}
