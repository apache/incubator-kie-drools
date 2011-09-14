/*
 * Copyright 2005 JBoss Inc
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

package org.drools.common;

import java.io.IOException;

import org.drools.Agenda;
import org.drools.marshalling.impl.InputMarshaller;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.OutputMarshaller;
import org.drools.marshalling.impl.PersisterEnums;
import org.drools.marshalling.impl.TimersInputMarshaller;
import org.drools.marshalling.impl.TimersOutputMarshaller;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.runtime.Calendars;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.Trigger;
import org.drools.time.impl.PointInTimeTrigger;

/**
 * Scheduler for rules requiring truth duration.
 */
public final class Scheduler {
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    /**
     * Construct.
     */
    private Scheduler() {
    }

    /**
     * Schedule an agenda item.
     * 
     * @param item
     *            The item to schedule.
     * @param wm 
     * @param workingMemory
     *            The working memory session.
     */
    public static void scheduleAgendaItem(final ScheduledAgendaItem item, InternalAgenda agenda, InternalWorkingMemory wm) {
        String[] calendarNames = item.getRule().getCalendars();
        Calendars calendars = wm.getCalendars();
        
        Trigger trigger = item.getRule().getTimer().createTrigger( ((InternalWorkingMemory)agenda.getWorkingMemory()).getTimerService().getCurrentTime(), calendarNames, calendars);
        
        ActivationTimerJob job = new ActivationTimerJob();
        ActivationTimerJobContext ctx = new ActivationTimerJobContext( trigger, item, agenda );
                
        JobHandle jobHandle = ((InternalWorkingMemory)agenda.getWorkingMemory()).getTimerService().scheduleJob( job, ctx, trigger );
        item.setJobHandle( jobHandle );
    }
    
    public static void removeAgendaItem(final ScheduledAgendaItem item, final InternalAgenda agenda) {
        ((InternalWorkingMemory)agenda.getWorkingMemory()).getTimerService().removeJob( item.getJobHandle() );
    }
    
    public static class ActivationTimerJob implements Job {
        public void execute(JobContext ctx) {
            InternalAgenda agenda = ( InternalAgenda ) ((ActivationTimerJobContext)ctx).getAgenda();
            ScheduledAgendaItem item  = ((ActivationTimerJobContext)ctx).getScheduledAgendaItem();
            
            agenda.fireActivation( item );
            if ( ((ActivationTimerJobContext)ctx).getTrigger().hasNextFireTime() == null ) {
                agenda.getScheduledActivationsLinkedList().remove( item );
            } else {
                // the activation has been rescheduled, the Agenda would have set it's activated to false
                // so reset the activated to true here
                item.setActivated( true );
            }
            agenda.getWorkingMemory().fireAllRules();
        }
    }
    
    public static class ActivationTimerJobContext implements JobContext {
        private JobHandle jobHandle;
        private ScheduledAgendaItem scheduledAgendaItem;
        private Agenda agenda;
        private Trigger trigger;
        
        public ActivationTimerJobContext(Trigger trigger,
                                         ScheduledAgendaItem scheduledAgendaItem,
                                         Agenda agenda) {
            this.trigger =               trigger;
            this.scheduledAgendaItem = scheduledAgendaItem;
            this.agenda = agenda;
        }
        
        public Agenda getAgenda() {
            return this.agenda;
        }
        
        public ScheduledAgendaItem getScheduledAgendaItem() {
            return this.scheduledAgendaItem;
        }

        public JobHandle getJobHandle() {
            return this.jobHandle;
        }

        public void setJobHandle(JobHandle jobHandle) {
            this.jobHandle = jobHandle;
        }
        
        public Trigger getTrigger() {
            return trigger;
        }

        public void setScheduledAgendaItem(ScheduledAgendaItem scheduledAgendaItem) {
            this.scheduledAgendaItem = scheduledAgendaItem;
        }

        public void setAgenda(Agenda agenda) {
            this.agenda = agenda;
        }

        public void setTrigger(Trigger trigger) {
            this.trigger = trigger;
        }               
    }
    
    
    public static class ActivationTimerOutputMarshaller  implements TimersOutputMarshaller {
        public void write(JobContext jobCtx,
                          MarshallerWriteContext outputCtx) throws IOException {     
            outputCtx.writeShort( PersisterEnums.ACTIVATION_TIMER );
            ActivationTimerJobContext ajobCtx = ( ActivationTimerJobContext ) jobCtx;
            int leftTupleId = outputCtx.terminalTupleMap.get( ajobCtx.getScheduledAgendaItem().getTuple() );
            outputCtx.writeInt( leftTupleId );
            
            OutputMarshaller.writeTrigger(ajobCtx.getTrigger(), outputCtx);
        }
    }
    
    public static class ActivationTimerInputMarshaller implements TimersInputMarshaller  {
        public void read(MarshallerReaderContext inCtx) throws IOException, ClassNotFoundException {                       
            int leftTupleId = inCtx.readInt();
            LeftTuple leftTuple = inCtx.terminalTupleMap.get( leftTupleId  );
            ScheduledAgendaItem item = ( ScheduledAgendaItem ) leftTuple.getObject();
            
            Trigger trigger = InputMarshaller.readTrigger( inCtx ); 
            
            DefaultAgenda agenda = ( DefaultAgenda ) inCtx.wm.getAgenda();
            ActivationTimerJob job = new ActivationTimerJob();
            ActivationTimerJobContext ctx = new ActivationTimerJobContext( trigger, item, agenda );
                    
            JobHandle jobHandle = ((InternalWorkingMemory)agenda.getWorkingMemory()).getTimerService().scheduleJob( job, ctx, trigger );
            item.setJobHandle( jobHandle );            
        }
    }    
        
}
