/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance.timer;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.marshalling.impl.InputMarshaller;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.OutputMarshaller;
import org.drools.marshalling.impl.PersisterEnums;
import org.drools.marshalling.impl.TimersInputMarshaller;
import org.drools.marshalling.impl.TimersOutputMarshaller;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.TimerService;
import org.drools.time.Trigger;
import org.drools.time.impl.IntervalTrigger;
import org.jbpm.marshalling.impl.ProcessMarshallerImpl;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class TimerManager {
    private long                     timerId    = 0;

    private InternalKnowledgeRuntime kruntime;
    private TimerService             timerService;
    private Map<Long, TimerInstance> timers     = new ConcurrentHashMap<Long, TimerInstance>();
    public static final  Job         processJob = new ProcessJob();

    public TimerManager(InternalKnowledgeRuntime kruntime,
                        TimerService timerService) {
        this.kruntime = kruntime;
        this.timerService = timerService;
    }

    public void registerTimer(final TimerInstance timer,
                              ProcessInstance processInstance) {
        timer.setId( ++timerId );
        timer.setProcessInstanceId( processInstance.getId() );
        timer.setActivated( new Date() );
        
        Trigger trigger = new IntervalTrigger( timerService.getCurrentTime(),
                                               null,
                                               null,
                                               -1,
                                               timer.getDelay(),
                                               timer.getPeriod(),
                                               null,
                                               null );

        ProcessJobContext ctx = new ProcessJobContext( timer,
                                                       trigger,
                                                       processInstance.getId(),
                                                       this.kruntime );

        JobHandle jobHandle = this.timerService.scheduleJob( processJob,
                                                             ctx,
                                                             trigger );
        
        timer.setJobHandle( jobHandle );
        timers.put( timer.getId(),
                    timer );
    }

    public void internalAddTimer(final TimerInstance timer) {
        long delay;
        Date lastTriggered = timer.getLastTriggered();
        if ( lastTriggered == null ) {
            Date activated = timer.getActivated();
            Date now = new Date();
            long timespan = now.getTime() - activated.getTime();
            delay = timer.getDelay() - timespan;
            if ( delay < 0 ) {
                delay = 0;
            }
        } else {
            Date now = new Date();
            long timespan = now.getTime() - lastTriggered.getTime();
            delay = timespan - timer.getPeriod();
            if ( delay < 0 ) {
                delay = 0;
            }
        }
        Trigger trigger = new IntervalTrigger( timerService.getCurrentTime(),
                                               null,
                                               null,
                                               -1,
                                               delay,
                                               timer.getPeriod(),
                                               null,
                                               null ) ;
        ProcessJobContext ctx = new ProcessJobContext( timer,
                                                       trigger,
                                                       timer.getProcessInstanceId(),
                                                       this.kruntime );
        
        JobHandle jobHandle = this.timerService.scheduleJob( processJob,
                                                             ctx,
                                                             trigger );
        timer.setJobHandle( jobHandle );
        timers.put( timer.getId(),
                    timer );
    }

    public void cancelTimer(long timerId) {
        TimerInstance timer = timers.remove( timerId );
        if ( timer != null ) {
            timerService.removeJob( timer.getJobHandle() );
        }
    }

    public void dispose() {
//        for ( TimerInstance timer : timers.values() ) {
//            System.out.println( timer );
//            timerService.removeJob( timer.getJobHandle() );
//        }
        int i = 0;
        for ( Iterator<TimerInstance> it = timers.values().iterator(); it.hasNext(); ) {
            TimerInstance timer = it.next();            
            timerService.removeJob( timer.getJobHandle() );
            it.remove();
        }
        timerService.shutdown();
    }

    public TimerService getTimerService() {
        return this.timerService;
    }

    public Collection<TimerInstance> getTimers() {
        return timers.values();
    }
    
    public Map<Long, TimerInstance> getTimerMap() {
        return this.timers;
    }

    public long internalGetTimerId() {
        return timerId;
    }

    public void internalSetTimerId(long timerId) {
        this.timerId = timerId;
    }

    public void setTimerService(TimerService timerService) {
        this.timerService = timerService;
    }
    
    public static class ProcessTimerOutputMarshaller implements TimersOutputMarshaller {
        public void write(JobContext ctx,  MarshallerWriteContext outCtx) throws IOException {
            outCtx.writeShort( PersisterEnums.PROCESS_TIMER );
            
            ProcessJobContext pctx = ( ProcessJobContext ) ctx;
            
            outCtx.writeLong( pctx.getProcessInstanceId() );
            
            OutputMarshaller.writeTrigger( pctx.getTrigger(), outCtx );
            
            ProcessMarshallerImpl.writeTimer( outCtx, pctx.getTimer() );
        }
    }
    
    public static class ProcessTimerInputMarshaller  implements TimersInputMarshaller {
        public void read(MarshallerReaderContext inCtx) throws IOException, ClassNotFoundException {
            TimerService ts = inCtx.wm.getTimerService();
 
            long processInstanceId = inCtx.readLong();           

            Trigger trigger = InputMarshaller.readTrigger( inCtx );
            
            TimerInstance timerInstance = ProcessMarshallerImpl.readTimer( inCtx );            

            ProcessJobContext pctx = new ProcessJobContext(timerInstance, trigger, processInstanceId, inCtx.wm.getKnowledgeRuntime());

            
            JobHandle jobHandle = ts.scheduleJob( processJob,
                                                  pctx,
                                                  trigger );
            timerInstance.setJobHandle( jobHandle );
            pctx.setJobHandle( jobHandle );   
            
            TimerManager tm = ((InternalProcessRuntime)inCtx.wm.getProcessRuntime()).getTimerManager();
            
            tm.getTimerMap().put( timerInstance.getId(),
                                  timerInstance );            
        }
    }    

    public static class ProcessJob
        implements
        Job {

        public void execute(JobContext c) {
            ProcessJobContext ctx = (ProcessJobContext) c;

            Long processInstanceId = ctx.getProcessInstanceId();
            InternalKnowledgeRuntime kruntime = ctx.getKnowledgeRuntime();

            if ( processInstanceId == null ) {
                throw new IllegalArgumentException( "Could not find process instance for timer " );
            }

            ctx.getTimer().setLastTriggered( new Date( ctx.getKnowledgeRuntime().getSessionClock().getCurrentTime() ) );

            ((InternalProcessRuntime) kruntime.getProcessRuntime())
            	.getSignalManager().signalEvent( processInstanceId,
                                                 "timerTriggered",
                                                  ctx.getTimer() );
            
            TimerManager tm = ((InternalProcessRuntime)ctx.getKnowledgeRuntime().getProcessRuntime()).getTimerManager();

            if ( ctx.getTimer().getPeriod() == 0 ) {
                tm.getTimerMap().remove( ctx.getTimer().getId() );
            }
        }

    }

    public static class ProcessJobContext
        implements
        JobContext {
        private Long                     processInstanceId;
        private InternalKnowledgeRuntime kruntime;
        private TimerInstance            timer;
        private Trigger                  trigger;

        private JobHandle                jobHandle;

        public ProcessJobContext(final TimerInstance timer,
                                 final Trigger trigger, 
                                 final Long processInstanceId,
                                 final InternalKnowledgeRuntime kruntime) {
            this.timer = timer;
            this.trigger = trigger;
            this.processInstanceId = processInstanceId;
            this.kruntime = kruntime;
        }

        public Long getProcessInstanceId() {
            return processInstanceId;
        }

        public InternalKnowledgeRuntime getKnowledgeRuntime() {
            return kruntime;
        }
        
        public Trigger getTrigger() {
            return trigger;
        }

        public JobHandle getJobHandle() {
            return this.jobHandle;
        }

        public void setJobHandle(JobHandle jobHandle) {
            this.jobHandle = jobHandle;
        }

        public TimerInstance getTimer() {
            return timer;
        }

    }

}
