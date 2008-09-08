package org.drools.common;

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

import org.drools.Agenda;
import org.drools.ClockType;
import org.drools.process.instance.timer.TimerManager.TimerTrigger;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.TimerService;
import org.drools.time.TimerServiceFactory;

/**
 * Scheduler for rules requiring truth duration.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
final class Scheduler {
    // ------------------------------------------------------------
    // Class members
    // ------------------------------------------------------------

    /** Singleton instance. */
    private static final Scheduler INSTANCE = new Scheduler();

    // ------------------------------------------------------------
    // Class methods
    // ------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     * 
     * @return The singleton instance.
     */
    static Scheduler getInstance() {
        return Scheduler.INSTANCE;
    }

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Alarm manager. */
    private final TimerService timerService;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    private Scheduler() {
        // FIXME: must use the session timer service
        this.timerService = TimerServiceFactory.getTimerService( ClockType.REAL_TIME );
    }

    /**
     * Schedule an agenda item.
     * 
     * @param item
     *            The item to schedule.
     * @param workingMemory
     *            The working memory session.
     */
    void scheduleAgendaItem(final ScheduledAgendaItem item, InternalAgenda agenda) {
        DuractionJob job = new DuractionJob();        
        DuractionJobContext ctx = new DuractionJobContext( item, agenda );
        TimerTrigger trigger = new TimerTrigger( item.getRule().getDuration().getDuration( item.getTuple() ), 0);
        
        
        JobHandle jobHandle = this.timerService.scheduleJob( job, ctx, trigger );
        item.setJobHandle( jobHandle );
    }
    
    public void removeAgendaItem(final ScheduledAgendaItem item) {
        this.timerService.removeJob( item.getJobHandle() );
    }    
    
    public static class DuractionJob implements Job {
        public void execute(JobContext ctx) {
            InternalAgenda agenda = ( InternalAgenda ) ((DuractionJobContext)ctx).getAgenda();
            ScheduledAgendaItem item  = ((DuractionJobContext)ctx).getScheduledAgendaItem();
            
            agenda.fireActivation( item );
            agenda.getScheduledActivationsLinkedList().remove( item );
            agenda.getWorkingMemory().fireAllRules();            
        }        
    }
    
    public static class DuractionJobContext implements JobContext {
        private JobHandle jobHandle;
        private ScheduledAgendaItem scheduledAgendaItem;
        private Agenda agenda;                
        
        public DuractionJobContext(ScheduledAgendaItem scheduledAgendaItem,
                                   Agenda agenda) {
            this.scheduledAgendaItem = scheduledAgendaItem;
            this.agenda = agenda;
        }

        public DuractionJobContext(ScheduledAgendaItem agendaItem) {
            this.scheduledAgendaItem = scheduledAgendaItem;
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
    }    
}
