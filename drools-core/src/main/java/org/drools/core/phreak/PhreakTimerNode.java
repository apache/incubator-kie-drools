package org.drools.core.phreak;

import java.io.IOException;
import java.util.Date;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
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
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
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
import org.kie.api.runtime.rule.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhreakTimerNode {
    private static final Logger log = LoggerFactory.getLogger( PhreakTimerNode.class );

    public void doNode(TimerNode timerNode,
                       TimerNodeMemory tm,
                       PathMemory pmem,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       LeftTupleSets trgLeftTuples,
                       LeftTupleSets stagedLeftTuples) {

        if ( srcLeftTuples.getDeleteFirst() != null ) {
            doLeftDeletes( timerNode, tm, pmem, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
        }

        if ( srcLeftTuples.getUpdateFirst() != null ) {
            doLeftUpdates( timerNode, tm, pmem, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );
        }

        if ( srcLeftTuples.getInsertFirst() != null ) {
            doLeftInserts( timerNode, tm, pmem, sink, wm, srcLeftTuples, trgLeftTuples );
        }

        doPropagateChildLeftTuples( timerNode, tm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples );

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

        for ( LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            scheduleLeftTuple( timerNode, tm, pmem, sink, wm, timer, timerService, timestamp, calendarNames, calendars, leftTuple, trgLeftTuples, null );

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

        for ( LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            DefaultJobHandle jobHandle = (DefaultJobHandle) leftTuple.getObject();
            timerService.removeJob( jobHandle );
            scheduleLeftTuple( timerNode, tm, pmem, sink, wm, timer, timerService, timestamp, calendarNames, calendars, leftTuple, trgLeftTuples, stagedLeftTuples );

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
                for ( LeftTuple leftTuple = deletes.getFirst(); leftTuple != null; ) {
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

                DefaultJobHandle jobHandle = (DefaultJobHandle) leftTuple.getObject();
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
                        switch ( childLeftTuple.getStagedType() ) {
                        // handle clash with already staged entries
                            case LeftTuple.INSERT :
                                stagedLeftTuples.removeInsert( childLeftTuple );
                                break;
                            case LeftTuple.UPDATE :
                                stagedLeftTuples.removeUpdate( childLeftTuple );
                                break;
                        }

                        childLeftTuple.setPropagationContext( leftTuple.getPropagationContext() );
                        trgLeftTuples.addDelete( childLeftTuple );
                        if ( log.isTraceEnabled() ) {
                            log.trace( "Timer Delete {}", leftTuple );
                        }
                    }
                }

                leftTuple.clearStaged();
                leftTuple = next;
            }
        }
    }

    private void scheduleLeftTuple(final TimerNode timerNode,
                                   final TimerNodeMemory tm,
                                   final PathMemory pmem,
                                   final LeftTupleSink sink,
                                   final InternalWorkingMemory wm,
                                   final Timer timer,
                                   final TimerService timerService,
                                   final long timestamp,
                                   final String[] calendarNames,
                                   final Calendars calendars,
                                   final LeftTuple leftTuple,
                                   final LeftTupleSets trgLeftTuples,
                                   final LeftTupleSets stagedLeftTuples) {
        if( leftTuple.getPropagationContext().getReaderContext() == null ) {
            final Trigger trigger = createTrigger( timerNode, wm, timer, timestamp, calendarNames, calendars, leftTuple );

            // regular propagation
            scheduleTimer( timerNode, tm, pmem, sink, wm, timerService, timestamp, leftTuple, trgLeftTuples, stagedLeftTuples, trigger );
        } else {
            // de-serializing, so we need to correlate timers before scheduling them
            Scheduler scheduler = new Scheduler() {
                @Override
                public void schedule( Trigger t ) {
                    scheduleTimer( timerNode, tm, pmem, sink, wm, timerService, timestamp, leftTuple, trgLeftTuples, stagedLeftTuples, t );
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
        final DefaultJobHandle jobHandle = (DefaultJobHandle) leftTuple.getObject();
        final Trigger trigger = timer.createTrigger( timestamp, leftTuple, jobHandle, calendarNames, calendars, timerNode.getDeclarations(), wm );
        return trigger;
    }
    
    public static interface Scheduler {
        public void schedule( Trigger t );
        public Trigger getTrigger();
    }

    private void scheduleTimer(TimerNode timerNode,
                               TimerNodeMemory tm,
                               PathMemory pmem,
                               LeftTupleSink sink,
                               InternalWorkingMemory wm,
                               TimerService timerService,
                               long timestamp,
                               LeftTuple leftTuple,
                               LeftTupleSets trgLeftTuples,
                               LeftTupleSets stagedLeftTuples,
                               Trigger trigger) {
        DefaultJobHandle jobHandle;
        if ( trigger.hasNextFireTime().getTime() <= timestamp ) {
            // first execution is straight away, so void Scheduling
            if ( log.isTraceEnabled() ) {
                log.trace( "Timer Fire Now {}", leftTuple );
            }
            doPropagateChildLeftTuple( sink, trgLeftTuples, stagedLeftTuples, leftTuple );

            trigger.nextFireTime();

            Date nextFireTime = trigger.hasNextFireTime();
            if ( nextFireTime != null && nextFireTime.getTime() <= timestamp ) {
                throw new IllegalStateException( "Trigger.nextFireTime is not increasing" );
            }
        }

        if ( trigger.hasNextFireTime() != null ) {
            // can be null, if the system timestamp has surpassed when this was suppose to fire
            TimerNodeJob job = new TimerNodeJob();
            TimerNodeJobContext jobCtx = new TimerNodeJobContext( timerNode.getId(), trigger, leftTuple, tm, sink, pmem, wm );

            jobHandle = (DefaultJobHandle) timerService.scheduleJob( job, jobCtx, trigger );
            leftTuple.setObject( jobHandle );

            if ( log.isTraceEnabled() ) {
                log.trace( "Timer Scheduled {}", leftTuple );
            }
        }
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
                LeftTuple next = (LeftTuple) leftTuple.getNext();

                doPropagateChildLeftTuple( sink, trgLeftTuples, stagedLeftTuples, leftTuple );

                leftTuple.clear();
                leftTuple = next;
            }
            // doLeftDeletes handles deletes, directly into the trgLeftTuples

            leftTuples.clear();
        }
    }

    private void doPropagateChildLeftTuple(LeftTupleSink sink,
                                           LeftTupleSets trgLeftTuples,
                                           LeftTupleSets stagedLeftTuples,
                                           LeftTuple leftTuple) {
        LeftTuple childLeftTuple = leftTuple.getFirstChild();
        if ( childLeftTuple == null ) {
            childLeftTuple = sink.createLeftTuple( leftTuple, sink, leftTuple.getPropagationContext(), true );
            trgLeftTuples.addInsert( childLeftTuple );
            if ( log.isTraceEnabled() ) {
                log.trace( "Timer Insert {}", childLeftTuple );
            }
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
    }

    public static class TimerNodeJob
            implements
            Job {
        public void execute(JobContext ctx) {
            TimerNodeJobContext timerJobCtx = (TimerNodeJobContext) ctx;
            Trigger trigger = timerJobCtx.getTrigger();

            PathMemory pmem = timerJobCtx.getPathMemory();
            pmem.doLinkRule( timerJobCtx.getWorkingMemory() );

            LeftTupleList leftTuples = timerJobCtx.getTimerNodeMemory().getInsertOrUpdateLeftTuples();
            LeftTuple lt = timerJobCtx.getLeftTuple();

            if ( log.isTraceEnabled() ) {
                log.trace( "Timer Executor {} {}", timerJobCtx.getTrigger(), lt );
            }

            synchronized ( leftTuples ) {
                if ( lt.getMemory() == null ) {
                    // don't add it, if it's already added, which could happen with interval or cron timers
                    leftTuples.add( lt );
                }
            }

            pmem.queueRuleAgendaItem( timerJobCtx.getWorkingMemory() );
        }
    }

    public static class TimerNodeJobContext
            implements
            JobContext {
        private JobHandle             jobHandle;
        private Trigger               trigger;

        private LeftTuple             leftTuple;
        private int                   timerNodeId;
        private TimerNodeMemory       tm;

        private LeftTupleSink         sink;
        private PathMemory            pmem;
        private InternalWorkingMemory wm;

        public TimerNodeJobContext(int timerNodeId,
                                   Trigger trigger,
                                   LeftTuple leftTuple,
                                   TimerNodeMemory tm,
                                   LeftTupleSink sink,
                                   PathMemory pmem,
                                   InternalWorkingMemory wm) {
            this.timerNodeId = timerNodeId;
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
        
        public int getTimerNodeId() {
            return timerNodeId;
        }
    }

    public static class TimerNodeTimerOutputMarshaller
            implements
            TimersOutputMarshaller {

        public void write(JobContext jobCtx,
                          MarshallerWriteContext outputCtx) throws IOException {
            throw new UnsupportedOperationException( "This method should no longer be used and is due to removal." );
        }

        public ProtobufMessages.Timers.Timer serialize(JobContext jobCtx,
                                                       MarshallerWriteContext outputCtx) {
            // TimerNodeJobContext   
            TimerNodeJobContext tnJobCtx = (TimerNodeJobContext) jobCtx;

            return ProtobufMessages.Timers.Timer.newBuilder()
                    .setType( ProtobufMessages.Timers.TimerType.TIMER_NODE )
                    .setTimerNode( ProtobufMessages.Timers.TimerNodeTimer.newBuilder()
                            .setNodeId( tnJobCtx.getTimerNodeId() )
                            .setTuple( PersisterHelper.createTuple( tnJobCtx.getLeftTuple() ) )
                            .setTrigger( ProtobufOutputMarshaller.writeTrigger( tnJobCtx.getTrigger(), 
                                                                                outputCtx ) )
                            .build() )
                    .build();
        }
    }

    public static class TimerNodeTimerInputMarshaller
            implements
            TimersInputMarshaller {
        public void read(MarshallerReaderContext inCtx) throws IOException,
                                                       ClassNotFoundException {
            throw new UnsupportedOperationException( "This method should no longer be used and is due to removal." );
        }

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
