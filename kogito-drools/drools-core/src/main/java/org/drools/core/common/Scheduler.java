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

package org.drools.core.common;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers.ActivationTimer;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.core.marshalling.impl.ProtobufOutputMarshaller;
import org.drools.core.marshalling.impl.TimersInputMarshaller;
import org.drools.core.marshalling.impl.TimersOutputMarshaller;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;
import org.kie.api.runtime.rule.Agenda;

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

    public static class ActivationTimerJob<T extends ModedAssertion<T>> implements Job {
        public void execute(JobContext ctx) {
            InternalAgenda agenda = ( InternalAgenda ) ((ActivationTimerJobContext)ctx).getAgenda();
            ScheduledAgendaItem item  = ((ActivationTimerJobContext)ctx).getScheduledAgendaItem();

            boolean wasFired = agenda.fireTimedActivation( item );

            if ( ((ActivationTimerJobContext)ctx).getTrigger().hasNextFireTime() == null ) {

                if ( wasFired ) {
                    agenda.getWorkingMemory().fireAllRules();
                } else {
                    postpone(item, agenda);
                }

                if ( item.isEnqueued() ) {
                    org.drools.core.util.LinkedList<ScheduledAgendaItem<T>> schedules = agenda.getScheduledActivationsLinkedList();
                    schedules.remove( item );
                    item.setEnqueued( false );
                }
            } else {
                // the activation has been rescheduled, the Agenda would have set it's activated to false
                // so reset the activated to true here
                item.setQueued(true);
                if ( wasFired ) {
                    agenda.getWorkingMemory().fireAllRules();
                } else {
                    postpone(item, agenda);
                }
            }
        }

        private void postpone( ScheduledAgendaItem item, InternalAgenda agenda ) {

            LeftTuple postponedTuple;
            if ( item.getTuple().getParent() != null ) {
                LeftTuple lt = item.getTuple();
                if ( lt.getRightParent() != null ) {
                    postponedTuple = item.getTerminalNode().createLeftTuple( item.getTuple().getLeftParent(), item.getTuple().getRightParent(), null, null, item.getTuple().getSink(), true );                  
                } else {
                    // eval nodes have no right parent
                    postponedTuple = item.getTerminalNode().createLeftTuple( item.getTuple().getParent(), item.getTuple().getSink(), item.getTuple().getPropagationContext(), true);
                }
            } else {
                postponedTuple = item.getTerminalNode().createLeftTuple( item.getTuple().getHandle(), item.getTuple().getSink(), true );
            }

            agenda.createPostponedActivation( postponedTuple,
                                              item.getPropagationContext(),
                                              agenda.getWorkingMemory(),
                                              item.getTerminalNode() );
            agenda.addActivation( (AgendaItem) postponedTuple.getObject() );
            
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

        @Override
        public InternalWorkingMemory getWorkingMemory() {
            return ((InternalAgenda)agenda).getWorkingMemory();
        }
    }
    
    
    public static class ActivationTimerOutputMarshaller  implements TimersOutputMarshaller {
        public Timer serialize(JobContext jobCtx,
                               MarshallerWriteContext outputCtx) {
            ActivationTimerJobContext ajobCtx = ( ActivationTimerJobContext ) jobCtx;
            return ProtobufMessages.Timers.Timer.newBuilder()
                    .setType( ProtobufMessages.Timers.TimerType.ACTIVATION )
                    .setActivation( ProtobufMessages.Timers.ActivationTimer.newBuilder()
                                .setActivation( PersisterHelper.createActivation( ajobCtx.getScheduledAgendaItem().getRule().getPackageName(),
                                                                                  ajobCtx.getScheduledAgendaItem().getRule().getName(),
                                                                                  ajobCtx.getScheduledAgendaItem().getTuple() ) )
                                .setTrigger( ProtobufOutputMarshaller.writeTrigger(ajobCtx.getTrigger(), 
                                                                                   outputCtx) )
                                .build() )
                    .build();
        }
    }
    
    public static class ActivationTimerInputMarshaller implements TimersInputMarshaller  {
        public void deserialize(MarshallerReaderContext inCtx,
                                Timer _timer) throws ClassNotFoundException {
            ActivationTimer _activation = _timer.getActivation();

            LeftTuple leftTuple = inCtx.filter.getTuplesCache().get( PersisterHelper.createActivationKey( _activation.getActivation().getPackageName(), 
                                                                                                          _activation.getActivation().getRuleName(), 
                                                                                                          _activation.getActivation().getTuple() ) );
            if (leftTuple == null) {
                // if there's no leftTuple the session is being unmarshalled in a new kbase without the timer's activated rule
                return;
            }

            ScheduledAgendaItem item = (ScheduledAgendaItem) leftTuple.getObject();
            
            Trigger trigger = ProtobufInputMarshaller.readTrigger( inCtx,
                                                                   _activation.getTrigger() );

            ActivationTimerJobContext ctx = new ActivationTimerJobContext( trigger, item, inCtx.wm.getAgenda() );
            JobHandle jobHandle = inCtx.wm.getTimerService().scheduleJob( new ActivationTimerJob(), ctx, trigger );
            item.setJobHandle( jobHandle );            
        }
    }
}
