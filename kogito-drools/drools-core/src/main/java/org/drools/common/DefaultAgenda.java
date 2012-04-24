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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.SequentialKnowledgeHelper;
import org.drools.common.RuleFlowGroupImpl.DeactivateCallback;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.Entry;
import org.drools.core.util.LinkedListNode;
import org.drools.event.rule.ActivationCancelledCause;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.runtime.process.ProcessInstance;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ConsequenceException;
import org.drools.spi.ConsequenceExceptionHandler;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;
import org.drools.time.impl.DefaultJobHandle;
import org.drools.time.impl.ExpressionIntervalTimer;
import org.drools.time.impl.Timer;

/**
 * Rule-firing Agenda.
 * 
 * <p>
 * Since many rules may be matched by a single assertObject(...) all scheduled
 * actions are placed into the <code>Agenda</code>.
 * </p>
 * 
 * <p>
 * While processing a scheduled action, it may update or retract objects in
 * other scheduled actions, which must then be removed from the agenda.
 * Non-invalidated actions are left on the agenda, and are executed in turn.
 * </p>
 */
public class DefaultAgenda
    implements
    Externalizable,
    InternalAgenda {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long                                   serialVersionUID = 510l;

    /** Working memory of this Agenda. */
    private InternalWorkingMemory                               workingMemory;

    private org.drools.core.util.LinkedList                     scheduledActivations;

    /** Items time-delayed. */

    private Map<String, InternalAgendaGroup>                    agendaGroups;

    private Map<String, ActivationGroup>                        activationGroups;

    private Map<String, RuleFlowGroup>                          ruleFlowGroups;

    private LinkedList<AgendaGroup>                             focusStack;

    private InternalAgendaGroup                                 currentModule;

    private InternalAgendaGroup                                 main;

    private AgendaGroupFactory                                  agendaGroupFactory;

    private KnowledgeHelper                                     knowledgeHelper;

    public int                                                  activeActivations;

    public int                                                  dormantActivations;

    private ConsequenceExceptionHandler                         legacyConsequenceExceptionHandler;

    private org.drools.runtime.rule.ConsequenceExceptionHandler consequenceExceptionHandler;

    protected volatile AtomicBoolean                            halt             = new AtomicBoolean( false );

    private int                                                 activationCounter;
    
    private boolean                                             declarativeAgenda;
    
    private ObjectTypeConf                                      activationObjectTypeConf;
    
    private ActivationsFilter                                   activationsFilter;

    private volatile boolean                                    isFiringActivation = false;

    private volatile boolean                                    mustNotifyHalt     = false;                          

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public DefaultAgenda() {
    }

    /**
     * Construct.
     * 
     * @param rb
     *            The <code>InternalRuleBase</code> of this agenda.
     */
    public DefaultAgenda(InternalRuleBase rb) {
        this( rb,
              true );
    }

    /**
     * Construct.
     * 
     * @param rb
     *            The <code>InternalRuleBase</code> of this agenda.
     * @param initMain
     *            Flag to initialize the MAIN agenda group
     */
    public DefaultAgenda(InternalRuleBase rb,
                         boolean initMain) {

        this.agendaGroups = new HashMap<String, InternalAgendaGroup>();
        this.activationGroups = new HashMap<String, ActivationGroup>();
        this.ruleFlowGroups = new HashMap<String, RuleFlowGroup>();
        this.focusStack = new LinkedList<AgendaGroup>();
        this.scheduledActivations = new org.drools.core.util.LinkedList();
        this.agendaGroupFactory = rb.getConfiguration().getAgendaGroupFactory();

        if ( initMain ) {
            // MAIN should always be the first AgendaGroup and can never be
            // removed
            this.main = agendaGroupFactory.createAgendaGroup( AgendaGroup.MAIN,
                                                              rb );

            this.agendaGroups.put( AgendaGroup.MAIN,
                                   this.main );

            this.focusStack.add( this.main );
        }
        Object object = ClassUtils.instantiateObject( rb.getConfiguration().getConsequenceExceptionHandler(),
                                                      rb.getConfiguration().getClassLoader() );
        if ( object instanceof ConsequenceExceptionHandler ) {
            this.legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) object;
        } else {
            this.consequenceExceptionHandler = (org.drools.runtime.rule.ConsequenceExceptionHandler) object;
        }
        
        this.declarativeAgenda =  rb.getConfiguration().isDeclarativeAgenda();
    }

    public AgendaItem createAgendaItem(final LeftTuple tuple,
                                       final int salience,
                                       final PropagationContext context,
                                       final RuleTerminalNode rtn) {
        return new AgendaItem( activationCounter++,
                               tuple,
                               salience,
                               context,
                               rtn );
    }

    public ScheduledAgendaItem createScheduledAgendaItem(final LeftTuple tuple,
                                                         final PropagationContext context,
                                                         final RuleTerminalNode rtn) {
        return new ScheduledAgendaItem( activationCounter++,
                                        tuple,
                                        this,
                                        context,
                                        rtn );
    }

    public void setWorkingMemory(final InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        if ( ((InternalRuleBase) this.workingMemory.getRuleBase()).getConfiguration().isSequential() ) {
            this.knowledgeHelper = new SequentialKnowledgeHelper( this.workingMemory );
        } else {
            this.knowledgeHelper = new DefaultKnowledgeHelper( this.workingMemory );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        workingMemory = (InternalWorkingMemory) in.readObject();
        scheduledActivations = (org.drools.core.util.LinkedList) in.readObject();
        agendaGroups = (Map) in.readObject();
        activationGroups = (Map) in.readObject();
        ruleFlowGroups = (Map) in.readObject();
        focusStack = (LinkedList) in.readObject();
        currentModule = (InternalAgendaGroup) in.readObject();
        main = (InternalAgendaGroup) in.readObject();
        agendaGroupFactory = (AgendaGroupFactory) in.readObject();
        knowledgeHelper = (KnowledgeHelper) in.readObject();
        activeActivations = in.readInt();
        dormantActivations = in.readInt();
        legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) in.readObject();
        declarativeAgenda = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( workingMemory );
        out.writeObject( scheduledActivations );
        out.writeObject( agendaGroups );
        out.writeObject( activationGroups );
        out.writeObject( ruleFlowGroups );
        out.writeObject( focusStack );
        out.writeObject( currentModule );
        out.writeObject( main );
        out.writeObject( agendaGroupFactory );
        out.writeObject( knowledgeHelper );
        out.writeInt( activeActivations );
        out.writeInt( dormantActivations );
        out.writeObject( legacyConsequenceExceptionHandler );
        out.writeBoolean( declarativeAgenda );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getWorkingMemory()
     */
    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    /**
     * Schedule an agenda item for delayed firing.
     * 
     * @param item
     *            The item to schedule.
     */
    public void scheduleItem(final ScheduledAgendaItem item, final InternalWorkingMemory wm) {
        if( item.getPropagationContext().getReaderContext() == null ) {
            // this is not a serialization propagation, so schedule it
            // otherwise the timer will be correlated with this activation later during the 
            // deserialization of timers
            Scheduler.scheduleAgendaItem( item,
                                          this,
                                          wm );
        }
        this.scheduledActivations.add( item );
        item.setEnqueued( true );
    }

    
    // @TODO make serialisation work
    private ActivationGroup stagedActivations;
    /**
     * If the item belongs to an activation group, add it
     * 
     * @param item
     */
    private void addItemToActivationGroup(final AgendaItem item) {
        String group = item.getRule().getActivationGroup();
        if ( group != null && group.length() > 0 ) {
            this.getActivationGroup( group ).addActivation( item );
        }
    }
    
    public ActivationGroup getStageActivationsGroup() {
        if ( stagedActivations == null ) {
            stagedActivations = new ActivationGroupImpl( "staged activations");
        }
        return stagedActivations;
    }

    
    
    public boolean addActivation(final AgendaItem activation) { 
        if ( declarativeAgenda ) {
            if (  activationObjectTypeConf == null ) {
                EntryPoint ep = workingMemory.getEntryPoint();
                activationObjectTypeConf =  ((InternalWorkingMemoryEntryPoint) workingMemory.getWorkingMemoryEntryPoint( ep.getEntryPointId() )).getObjectTypeConfigurationRegistry().getObjectTypeConf( ep,
                                                                                                                                                                                                         activation );
            }
            
            InternalFactHandle factHandle = workingMemory.getFactHandleFactory().newFactHandle( activation, activationObjectTypeConf, workingMemory, workingMemory );
            workingMemory.getEntryPointNode().assertActivation( factHandle, activation.getPropagationContext(), workingMemory );
            activation.setFactHandle( factHandle );
            
            if ( !activation.isCanceled() && ( activation.getBlockers() == null || activation.getBlockers().isEmpty() ) ) {
                // All activations started off staged, they are unstaged if they are blocked or 
                // allowed to move onto the actual agenda for firing.
                getStageActivationsGroup().addActivation( activation );
            }
               
            return true;
        } else {
            addActivation( activation, true );
            return true;
        }
    } 
    
    public void setActiveActivations(int activeActivations) {
        this.activeActivations = activeActivations;
    }

    public void setDormantActivations(int dormantActivations) {
        this.dormantActivations = dormantActivations;
    }

    public boolean isDeclarativeAgenda() {
        return declarativeAgenda;
    }
    
    public void removeActivation(final AgendaItem activation) {
        if ( declarativeAgenda ) {
            workingMemory.getEntryPointNode().retractActivation( activation.getFactHandle(), activation.getPropagationContext(), workingMemory );

            if ( activation.getActivationGroupNode() != null ) {
                activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
            }
        }
        if ( activation instanceof ScheduledAgendaItem ) {
            removeScheduleItem( (ScheduledAgendaItem) activation );
        }
    }
    
    public void modifyActivation(final AgendaItem activation, boolean previouslyActive) {        
        if ( declarativeAgenda ) {
            InternalFactHandle factHandle = activation.getFactHandle();
            workingMemory.getEntryPointNode().modifyActivation( factHandle, activation.getPropagationContext(), workingMemory );
            
            if  (previouslyActive) {
                // already activated
                return;
            }
            
            if ( activation.isCanceled() || ( activation.getBlockers() != null && activation.getBlockers().size() > 0 ) ) {
                // it's blocked so do nothing
                return;
            }
            
            // All activations started off staged, they are unstaged if they are blocked or 
            // allowed to move onto the actual agenda for firing.
            ActivationGroup activationGroup = getStageActivationsGroup();  
            if ( activation.getActivationGroupNode() != null && activation.getActivationGroupNode().getActivationGroup() == activationGroup ) {
                // already staged, so return
                return;
            }
          
            activationGroup.addActivation( activation );
        } else {
            if ( !previouslyActive ) {
                addActivation( activation, true );
            } else {
                Timer timer = activation.getRule().getTimer();
                if ( timer != null && timer instanceof ExpressionIntervalTimer ) {
                    ScheduledAgendaItem schItem = ( ScheduledAgendaItem ) activation;                    
                    removeScheduleItem( schItem );
                    
                    scheduleItem( schItem,
                                  workingMemory );
                }
            }
        }
    }     
    
    public void clearAndCancelStagedActivations() {
        if ( getStageActivationsGroup().isEmpty() ) {
            return;
        }        
        org.drools.core.util.LinkedList list = getStageActivationsGroup().getList();

        final EventSupport eventsupport = (EventSupport) this.workingMemory;
        for ( Entry entry = list.removeFirst(); entry != null; entry = list.removeFirst() ) {
            ActivationGroupNode node = (ActivationGroupNode) entry;
            AgendaItem item = ( AgendaItem ) node.getActivation();  
            // This must be set to false otherwise modify won't work properly
            item.setActivated( false );            
            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                          this.workingMemory,
                                                                          ActivationCancelledCause.CLEAR );            
        }
    }
    
    public void unstageActivations() {
        if ( !declarativeAgenda || getStageActivationsGroup().isEmpty() ) {
            return;
        }        
        org.drools.core.util.LinkedList list = getStageActivationsGroup().getList();

        for ( Entry entry = list.removeFirst(); entry != null; entry = list.removeFirst() ) {
            ActivationGroupNode node = (ActivationGroupNode) entry;
            AgendaItem item = ( AgendaItem ) node.getActivation();
            item.setActivationGroupNode( null );

            addActivation( item, false );
        }
        
        notifyHalt();
    }
    
    private void addActivation(AgendaItem item, boolean notify) {
        Rule rule = item.getRule();
        
        // set the focus if rule autoFocus is true
        if ( rule.getAutoFocus() ) {
            this.setFocus( item.getPropagationContext(),
                           rule.getAgendaGroup() );
        }              
                               
        // adds item to activation group if appropriate
        addItemToActivationGroup(  item );            
        
        final Timer timer = rule.getTimer();
        if ( timer != null && item instanceof ScheduledAgendaItem ) {
            scheduleItem( (ScheduledAgendaItem) item,
                          workingMemory );
        } else {                
            InternalAgendaGroup agendaGroup = (InternalAgendaGroup) this.getAgendaGroup( rule.getAgendaGroup() );

            if ( item.getRule().getRuleFlowGroup() == null ) {
                agendaGroup.add( item );
            } else {
                // There is a RuleFlowNode so add it there, instead of the Agenda
                InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) this.getRuleFlowGroup( rule.getRuleFlowGroup() );
                rfg.addActivation( item );
            }
        }   
        if ( notify ) {
            // if an activation is currently firing allows to completely fire it before to send the notify
            if (isFiringActivation) {
                mustNotifyHalt = true;
            } else {
                notifyHalt();
            }
        }
    }
    
    public void removeScheduleItem(final ScheduledAgendaItem item) {
        if ( item.isEnqueued() ) {
            this.scheduledActivations.remove( item );
            item.setEnqueued( false );
            Scheduler.removeAgendaItem( item,
                                        this );
        }
    }

    public void addAgendaGroup(final AgendaGroup agendaGroup) {
        this.agendaGroups.put( agendaGroup.getName(),
                               (InternalAgendaGroup) agendaGroup );
    }

    public boolean createActivation(final LeftTuple tuple,
                                    final PropagationContext context,
                                    final InternalWorkingMemory workingMemory,
                                    final RuleTerminalNode rtn,
                                    final boolean reuseActivation ) {
        // First process control rules
        // Control rules do increase ActivationCountForEvent and agenda ActivateActivations, they do not currently fire events
        // ControlRules for now re-use the same PropagationContext
        if ( rtn.isFireDirect() ) {    
            // Fire RunLevel == 0 straight away. agenda-groups, rule-flow groups, salience are ignored
            AgendaItem item;
            if ( reuseActivation ) {
                item = ( AgendaItem ) tuple.getObject();
                item.setPropagationContext( context );
            } else {
                item = createAgendaItem( tuple,
                                         0,
                                         context,
                                         rtn);
            }
            tuple.setObject( item );            
            if( activationsFilter != null && !activationsFilter.accept( item, 
                                                                        context, 
                                                                        workingMemory,
                                                                        rtn ) ) {
                return false;
            }
            
            item.setActivated( true );
            tuple.increaseActivationCountForEvents();  
            increaseActiveActivations();
            fireActivation( item );  // Control rules fire straight away.       
            return true;
        }

        final Rule rule = rtn.getRule();
        AgendaItem item;
        final Timer timer = rule.getTimer();
        if ( timer != null ) {
            if ( reuseActivation ) {
                item = ( AgendaItem ) tuple.getObject();   
                item.setPropagationContext( context );
            } else {
                item = createScheduledAgendaItem( tuple,
                                                  context,
                                                  rtn );                
            }            
        } else {
            if ( rule.getCalendars() != null ) {
                // for normal activations check for Calendar inclusion here, scheduled activations check on each trigger point
                long timestamp = workingMemory.getSessionClock().getCurrentTime();
                for ( String cal : rule.getCalendars() ) {
                    if ( !workingMemory.getCalendars().get( cal ).isTimeIncluded( timestamp ) ) {
                        return false;
                    }
                }
            }                                
            
            InternalAgendaGroup agendaGroup = (InternalAgendaGroup) getAgendaGroup( rule.getAgendaGroup() );            
            if ( rule.getRuleFlowGroup() == null ) {
                // No RuleFlowNode so add it directly to the Agenda
                // do not add the activation if the rule is "lock-on-active" and the
                // AgendaGroup is active
                if ( rule.isLockOnActive() && agendaGroup.isActive() && agendaGroup.getAutoFocusActivator() != context) {
                	if ( tuple.getObject() == null ) {
                		tuple.setObject( Boolean.TRUE ); // this is so we can do a check with a bit more intent than a null check on modify
                	}
                    return false;
                }
            } else {
                // There is a RuleFlowNode so add it there, instead of the Agenda
                InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) getRuleFlowGroup( rule.getRuleFlowGroup() );

                // do not add the activation if the rule is "lock-on-active" and the
                // RuleFlowGroup is active
                if ( rule.isLockOnActive() && rfg.isActive() && agendaGroup.getAutoFocusActivator() != context) {
                	if ( tuple.getObject() == null ) {
                		tuple.setObject( Boolean.TRUE ); // this is so we can do a check with a bit more intent than a null check on modify
                	}
                    return false;
                }
            }            
            
            if ( reuseActivation ) {
                item = ( AgendaItem ) tuple.getObject();
                item.setSalience( rule.getSalience().getValue( new DefaultKnowledgeHelper( item, workingMemory ),
                                                               rule,
                                                               workingMemory ) );
                item.setPropagationContext( context );                                
            } else {
                item = createAgendaItem( tuple,
                                         0,
                                         context,
                                         rtn);
                item.setSalience( rule.getSalience().getValue( new DefaultKnowledgeHelper( item, workingMemory ),
                                                               rule,
                                                               workingMemory ) );
            }              
            
            item.setAgendaGroup( agendaGroup );   
        }
        

        tuple.setObject( item );
        
        if( activationsFilter != null && !activationsFilter.accept( item, 
                                                                    context, 
                                                                    workingMemory,
                                                                    rtn ) ) {
            increaseDormantActivations();
            return false;
        }
        item.setActivated( true );
        tuple.increaseActivationCountForEvents();  
        increaseActiveActivations();        
        item.setSequenence( rtn.getSequence() );                
        
        ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCreated( item,
                                                                                      workingMemory ); 
        return true;
    }

    public boolean createPostponedActivation(final LeftTuple tuple,
                                             final PropagationContext context,
                                             final InternalWorkingMemory workingMemory,
                                             final RuleTerminalNode rtn ) {

        final Rule rule = rtn.getRule();
        AgendaItem item;
        if ( rule.getCalendars() != null ) {
            // for normal activations check for Calendar inclusion here, scheduled activations check on each trigger point
            long timestamp = workingMemory.getSessionClock().getCurrentTime();
            for ( String cal : rule.getCalendars() ) {
                if ( !workingMemory.getCalendars().get( cal ).isTimeIncluded( timestamp ) ) {
                    return false;
                }
            }
        }

        InternalAgendaGroup agendaGroup = (InternalAgendaGroup) getAgendaGroup( rule.getAgendaGroup() );
        if ( rule.getRuleFlowGroup() == null ) {
            // No RuleFlowNode so add it directly to the Agenda
            // do not add the activation if the rule is "lock-on-active" and the
            // AgendaGroup is active
            if ( rule.isLockOnActive() && agendaGroup.isActive() && agendaGroup.getAutoFocusActivator() != context) {
                return false;
            }
        } else {
            // There is a RuleFlowNode so add it there, instead of the Agenda
            InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) getRuleFlowGroup( rule.getRuleFlowGroup() );

            // do not add the activation if the rule is "lock-on-active" and the
            // RuleFlowGroup is active
            if ( rule.isLockOnActive() && rfg.isActive() && agendaGroup.getAutoFocusActivator() != context) {
                return false;
            }
        }


        item = createAgendaItem( tuple,
                                 0,
                                 context,
                                 rtn);
        item.setSalience( rule.getSalience().getValue( new DefaultKnowledgeHelper( item, workingMemory ),
                                                       rule,
                                                       workingMemory ) );

        item.setAgendaGroup( agendaGroup );

        tuple.setObject( item );

        if( activationsFilter != null && !activationsFilter.accept( item,
                                                                    context,
                                                                    workingMemory,
                                                                    rtn ) ) {
            increaseDormantActivations();
            return false;
        }
        item.setActivated( true );
        tuple.increaseActivationCountForEvents();
        increaseActiveActivations();
        item.setSequenence( rtn.getSequence() );

        ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCreated( item,
                                                                                      workingMemory );
        return true;
    }


    public void cancelActivation(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory,
                                 final Activation activation,
                                 final RuleTerminalNode rtn ) {
        AgendaItem item = (AgendaItem) activation;
        item.removeAllBlockersAndBlocked( this );               

        if ( isDeclarativeAgenda() && activation.getFactHandle() == null ) {
            // This a control rule activation, nothing to do except update counters. As control rules are not in agenda-groups etc.
            decreaseDormantActivations(); // because we know ControlRules fire straight away and then become dormant
            return;
        } else {
            // we are retracting an actual Activation, so also remove it and it's handle from the WM. 
            removeActivation( item );
        }

        if ( activation.isActivated() ) {
            // on fact expiration, we don't remove the activation, but let it fire
            if ( context.getType() == PropagationContext.EXPIRATION && context.getFactHandleOrigin() != null ) {
            } else {
                activation.remove();

                if ( activation.getActivationGroupNode() != null ) {
                    activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
                }

                if ( activation.getActivationNode() != null ) {
                    final InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) activation.getActivationNode().getParentContainer();
                    ruleFlowGroup.removeActivation( activation );
                }
                leftTuple.decreaseActivationCountForEvents();

                ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCancelled( activation,
                                                                                                workingMemory,
                                                                                                ActivationCancelledCause.WME_MODIFY );
                decreaseActiveActivations();
            }
        } else {
            decreaseDormantActivations();
        }
        
        if ( item.getActivationUnMatchListener() != null ) {
            item.getActivationUnMatchListener().unMatch( workingMemory.getKnowledgeRuntime(), item );
        }

        workingMemory.getTruthMaintenanceSystem().removeLogicalDependencies( activation,
                                                                             context,
                                                                             rtn.getRule() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#setFocus(org.drools.spi.AgendaGroup)
     */
    public boolean setFocus(final AgendaGroup agendaGroup) {
        // Set the focus to the agendaGroup if it doesn't already have the focus
        if ( this.focusStack.getLast() != agendaGroup ) {
            ((InternalAgendaGroup) this.focusStack.getLast()).setActive( false );
            this.focusStack.add( agendaGroup );
            ((InternalAgendaGroup) agendaGroup).setActive( true );
            final EventSupport eventsupport = (EventSupport) this.workingMemory;
            eventsupport.getAgendaEventSupport().fireAgendaGroupPushed( agendaGroup,
                                                                        this.workingMemory );
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#setFocus(java.lang.String)
     */
    public void setFocus(final String name) {
        setFocus( null, name );
    }
    
    public void setFocus(final PropagationContext ctx, 
                         final String name) {
        AgendaGroup agendaGroup = getAgendaGroup( name );
        agendaGroup.setAutoFocusActivator( ctx );
        setFocus( agendaGroup );
    }    

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getFocus()
     */
    public AgendaGroup getFocus() {
        return this.focusStack.getLast();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getNextFocus()
     */
    public AgendaGroup getNextFocus() {
        InternalAgendaGroup agendaGroup;
        // Iterate until we find a populate AgendaModule or we reach the MAIN,
        // default, AgendaGroup
        while ( true ) {
            agendaGroup = (InternalAgendaGroup) this.focusStack.getLast();

            final boolean empty = agendaGroup.isEmpty();

            // No populated queues found so pop the focusStack and repeat
            if ( empty && (this.focusStack.size() > 1) ) {
                agendaGroup.setActive( false );
                this.focusStack.removeLast();
                final EventSupport eventsupport = (EventSupport) this.workingMemory;
                eventsupport.getAgendaEventSupport().fireAgendaGroupPopped( agendaGroup,
                                                                            this.workingMemory );
            } else {
                agendaGroup = (empty) ? null : agendaGroup;
                break;
            }
        }

        if ( agendaGroup != null ) {
            agendaGroup.setActive( true );
        }
        return agendaGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#setCurrentAgendaGroup(org.drools.spi.AgendaGroup)
     */
    public void setCurrentAgendaGroup(final InternalAgendaGroup agendaGroup) {
        this.currentModule = agendaGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getCurrentAgendaGroup()
     */
    public AgendaGroup getCurrentAgendaGroup() {
        return this.currentModule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getAgendaGroup(java.lang.String)
     */
    public AgendaGroup getAgendaGroup(final String name) {
        String groupName = (name == null || name.length() == 0) ? AgendaGroup.MAIN : name;

        AgendaGroup agendaGroup = this.agendaGroups.get( groupName );
        if ( agendaGroup == null ) {
            // The AgendaGroup is defined but not yet added to the
            // Agenda, so create the AgendaGroup and add to the Agenda.
            agendaGroup = agendaGroupFactory.createAgendaGroup( name,
                                                                ((InternalRuleBase) this.workingMemory.getRuleBase()) );
            addAgendaGroup( agendaGroup );
        }
        return agendaGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getAgendaGroups()
     */
    public AgendaGroup[] getAgendaGroups() {
        return (AgendaGroup[]) this.agendaGroups.values().toArray( new AgendaGroup[this.agendaGroups.size()] );
    }

    public Map<String, InternalAgendaGroup> getAgendaGroupsMap() {
        return this.agendaGroups;
    }

    public InternalAgendaGroup getMainAgendaGroup() {
        if ( this.main == null ) {
            this.main = (InternalAgendaGroup) getAgendaGroup( AgendaGroup.MAIN );
        }

        return this.main;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getStack()
     */
    public AgendaGroup[] getStack() {
        return this.focusStack.toArray( new AgendaGroup[this.focusStack.size()] );
    }

    public LinkedList<AgendaGroup> getStackList() {
        return this.focusStack;
    }

    public Map<String, RuleFlowGroup> getRuleFlowGroupsMap() {
        return this.ruleFlowGroups;
    }

    public Map<String, ActivationGroup> getActivationGroupsMap() {
        return this.activationGroups;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getActivationGroup(java.lang.String)
     */
    public ActivationGroup getActivationGroup(final String name) {
        ActivationGroupImpl activationGroup = (ActivationGroupImpl) this.activationGroups.get( name );
        if ( activationGroup == null ) {
            activationGroup = new ActivationGroupImpl( name );
            this.activationGroups.put( name,
                                       activationGroup );
        }
        return activationGroup;
    }

    public RuleFlowGroup getRuleFlowGroup(final String name) {
        RuleFlowGroup ruleFlowGroup = this.ruleFlowGroups.get( name );
        if ( ruleFlowGroup == null ) {
            ruleFlowGroup = new RuleFlowGroupImpl( name );
            ((InternalRuleFlowGroup) ruleFlowGroup).setWorkingMemory( (InternalWorkingMemory) getWorkingMemory() );
            this.ruleFlowGroups.put( name,
                                     ruleFlowGroup );
        }
        return ruleFlowGroup;
    }

    public void activateRuleFlowGroup(final String name) {
        ((InternalRuleFlowGroup) getRuleFlowGroup( name )).setActive( true );
    }

    public void activateRuleFlowGroup(final String name, long processInstanceId, String nodeInstanceId) {
        InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) getRuleFlowGroup( name );
        ruleFlowGroup.addNodeInstance(processInstanceId, nodeInstanceId);
        ruleFlowGroup.setActive( true );
    }

    public void deactivateRuleFlowGroup(final String name) {
        ((InternalRuleFlowGroup) getRuleFlowGroup( name )).setActive( false );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#focusStackSize()
     */
    public int focusStackSize() {
        int size = 0;
        for (final AgendaGroup group : this.focusStack) {
            size += group.size();
        }
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#agendaSize()
     */
    public int agendaSize() {
        int size = 0;
        for (InternalAgendaGroup internalAgendaGroup : this.agendaGroups.values()) {
            size += internalAgendaGroup.size();
        }
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getActivations()
     */
    public Activation[] getActivations() {
        final List<Activation> list = new ArrayList<Activation>();
        for ( final java.util.Iterator it = this.agendaGroups.values().iterator(); it.hasNext(); ) {
            final AgendaGroup group = (AgendaGroup) it.next();
            for ( org.drools.runtime.rule.Activation activation : group.getActivations() ) {
                list.add((Activation) activation);
            }
        }
        return list.toArray( new Activation[list.size()] );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getScheduledActivations()
     */
    public Activation[] getScheduledActivations() {
        Activation[] scheduledActivations = new Activation[this.scheduledActivations.size()];
        int i = 0;
        for ( LinkedListNode node = this.scheduledActivations.getFirst(); node != null; node = node.getNext() ) {
            scheduledActivations[i++] = (Activation) node;
        }
        return scheduledActivations;
    }

    public org.drools.core.util.LinkedList getScheduledActivationsLinkedList() {
        return this.scheduledActivations;
    }

    public void clear() {
        // reset focus stack
        this.focusStack.clear();
        this.focusStack.add( getMainAgendaGroup() );

        // reset scheduled activations
        if ( !this.scheduledActivations.isEmpty() ) {
            for ( ScheduledAgendaItem item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst(); item != null; item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst() ) {
                item.setEnqueued( false );
                Scheduler.removeAgendaItem( item,
                                            this );
            }
        }
        
        // reset staged activations
        getStageActivationsGroup().clear();
        

        //reset all agenda groups
        for ( InternalAgendaGroup group : this.agendaGroups.values() ) {
            group.clear();
        }

        // reset all ruleflows
        for ( RuleFlowGroup group : this.ruleFlowGroups.values() ) {
            group.clear();
        }

        // reset all activation groups.
        for ( ActivationGroup group : this.activationGroups.values() ) {
            group.clear();
        }
    }

    /** (non-Javadoc)
     * @see org.drools.common.AgendaI#clearAgenda()
     */
    public void clearAndCancel() {
        // Cancel all items and fire a Cancelled event for each Activation
        for (InternalAgendaGroup internalAgendaGroup : this.agendaGroups.values()) {
            clearAndCancelAgendaGroup(internalAgendaGroup);
        }

        final EventSupport eventsupport = (EventSupport) this.workingMemory;
        if ( !this.scheduledActivations.isEmpty() ) {
            for ( ScheduledAgendaItem item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst(); item != null; item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst() ) {
                item.setEnqueued( false );
                Scheduler.removeAgendaItem( item,
                                            this );
                eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                              this.workingMemory,
                                                                              ActivationCancelledCause.CLEAR );
            }
        }

        // cancel all staged activations
        clearAndCancelStagedActivations();
        
        // cancel all ruleflows
        for ( RuleFlowGroup group : this.ruleFlowGroups.values() ) {
            clearAndCancelAndCancel( group );
        }

        // cancel all activation groups.
        for ( ActivationGroup group : this.activationGroups.values() ) {
            clearAndCancelActivationGroup( group );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#clearAgendaGroup(java.lang.String)
     */
    public void clearAndCancelAgendaGroup(final String name) {
        final AgendaGroup agendaGroup = this.agendaGroups.get( name );
        if ( agendaGroup != null ) {
            clearAndCancelAgendaGroup( agendaGroup );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#clearAgendaGroup(org.drools.common.AgendaGroupImpl)
     */
    public void clearAndCancelAgendaGroup(final AgendaGroup agendaGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        // this is thread safe for BinaryHeapQueue
        // Binary Heap locks while it returns the array and reset's it's own internal array. Lock is released afer getAndClear()
        for (Activation aQueueable : ((InternalAgendaGroup) agendaGroup).getAndClear()) {
            final AgendaItem item = (AgendaItem) aQueueable;
            if (item == null) {
                continue;
            }

            // this must be set false before removal from the activationGroup.
            // Otherwise the activationGroup will also try to cancel the Actvation
            // Also modify won't work properly
            item.setActivated(false);

            if (item.getActivationGroupNode() != null) {
                item.getActivationGroupNode().getActivationGroup().removeActivation(item);
            }

            if (item.getActivationNode() != null) {
                final InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) item.getActivationNode().getParentContainer();
                ruleFlowGroup.removeActivation(item);
            }

            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                          this.workingMemory,
                                                                          ActivationCancelledCause.CLEAR );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#clearActivationGroup(java.lang.String)
     */
    public void clearAndCancelActivationGroup(final String name) {
        final ActivationGroup activationGroup = this.activationGroups.get( name );
        if ( activationGroup != null ) {
            clearAndCancelActivationGroup( activationGroup );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#clearActivationGroup(org.drools.spi.ActivationGroup)
     */
    public void clearAndCancelActivationGroup(final ActivationGroup activationGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        for ( final Iterator it = activationGroup.iterator(); it.hasNext(); ) {
            final ActivationGroupNode node = (ActivationGroupNode) it.next();
            final Activation activation = node.getActivation();
            activation.setActivationGroupNode( null );

            if ( activation.isActivated() ) {
                activation.setActivated( false );
                activation.remove();

                if ( activation.getActivationNode() != null ) {
                    final InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) activation.getActivationNode().getParentContainer();
                    ruleFlowGroup.removeActivation( activation );
                }

                eventsupport.getAgendaEventSupport().fireActivationCancelled( activation,
                                                                              this.workingMemory,
                                                                              ActivationCancelledCause.CLEAR );
            }
        }
        activationGroup.clear();
    }

    public void clearAndCancelRuleFlowGroup(final String name) {
        final RuleFlowGroup ruleFlowGrlup = this.ruleFlowGroups.get( name );
        if ( ruleFlowGrlup != null ) {
            clearAndCancelAndCancel( ruleFlowGrlup );
        }
    }

    public void clearAndCancelAndCancel(final RuleFlowGroup ruleFlowGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        for (ActivationNode node : ruleFlowGroup) {
            AgendaItem item = (AgendaItem) node.getActivation();
            if (item != null) {
                item.setActivated(false);
                item.remove();

                if (item.getActivationGroupNode() != null) {
                    item.getActivationGroupNode().getActivationGroup().removeActivation(item);
                }
            }

            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                          this.workingMemory,
                                                                          ActivationCancelledCause.CLEAR );
        }

        ruleFlowGroup.clear();

        if ( ruleFlowGroup.isActive() && ruleFlowGroup.isAutoDeactivate() ) {
            // deactivate callback
            WorkingMemoryAction action = new DeactivateCallback( (InternalRuleFlowGroup) ruleFlowGroup );
            this.workingMemory.queueWorkingMemoryAction( action );
        }
    }

    /**
     * Fire the next scheduled <code>Agenda</code> item, skipping items
     * that are not allowed by the agenda filter.
     * 
     * @return true if an activation was fired. false if no more activations 
     *              to fire
     * 
     * @throws ConsequenceException
     *             If an error occurs while firing an agenda item.
     */
    public boolean fireNextItem(final AgendaFilter filter) throws ConsequenceException {
        boolean tryagain, result;
        try {
            do {
                this.workingMemory.prepareToFireActivation();
                tryagain = result = false;
                final InternalAgendaGroup group = (InternalAgendaGroup) getNextFocus();
                // if there is a group with focus
                if ( group != null ) {
                    final AgendaItem item = (AgendaItem) group.getNext();
                    // if there is an item to fire from that group
                    if ( item != null ) {
                        //if the rule will be fired or not, it is necessary the routine bellow to clean up ruleflow activations
                        if ( item.getActivationNode() != null ) {
                            InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) item.getActivationNode().getParentContainer();
                            // it is possible that the ruleflow group is no longer active if it was
                            // cleared during execution of this activation
                            ruleFlowGroup.removeActivation( item );
                            ruleFlowGroup.deactivateIfEmpty();
                        }

                        // if that item is allowed to fire
                        if ( filter == null || filter.accept( item ) ) {
                            // fire it
                            fireActivation( item );
                            result = true;
                        } else {
                            // otherwise cancel it and try the next

                            //necessary to perfom queued actions like signal to a next node in a ruleflow/jbpm process
                            this.workingMemory.executeQueuedActions();

                            final EventSupport eventsupport = (EventSupport) this.workingMemory;
                            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                                          this.workingMemory,
                                                                                          ActivationCancelledCause.FILTER );
                            tryagain = true;
                        }
                    }
                }
            } while ( tryagain );
        } finally {
            this.workingMemory.activationFired();
        }
        return result;
    }

    /**
     * Fire this item.
     * 
     * @param activation
     *            The activation to fire
     * 
     * @throws ConsequenceException
     *             If an error occurs while attempting to fire the consequence.
     */
    public synchronized void fireActivation(final Activation activation) throws ConsequenceException {
        // We do this first as if a node modifies a fact that causes a recursion
        // on an empty pattern
        // we need to make sure it re-activates
        this.workingMemory.startOperation();
        isFiringActivation = true;
        try {
            increaseDormantActivations();
            decreaseActiveActivations();

            final EventSupport eventsupport = (EventSupport) this.workingMemory;

            eventsupport.getAgendaEventSupport().fireBeforeActivationFired( activation,
                                                                            this.workingMemory );

            if ( activation.getActivationGroupNode() != null ) {
                // We know that this rule will cancel all other activations in the group
                // so lets remove the information now, before the consequence fires
                final ActivationGroup activationGroup = activation.getActivationGroupNode().getActivationGroup();
                activationGroup.removeActivation( activation );
                clearAndCancelActivationGroup( activationGroup );
            }
            activation.setActivated( false );

            try {
                               
                this.knowledgeHelper.setActivation( activation );
                activation.getRule().getConsequence().evaluate( this.knowledgeHelper,
                                                                this.workingMemory );
                this.knowledgeHelper.cancelRemainingPreviousLogicalDependencies();
                this.knowledgeHelper.reset();
            } catch ( final Exception e ) {
                if ( this.legacyConsequenceExceptionHandler != null ) {
                    this.legacyConsequenceExceptionHandler.handleException( activation,
                                                                            this.workingMemory,
                                                                            e );
                } else if ( this.consequenceExceptionHandler != null ) {
                    this.consequenceExceptionHandler.handleException( activation, this.workingMemory.getKnowledgeRuntime(),
                                                                      e );
                } else {
                    throw new RuntimeException( e );
                }
            } finally {
                if ( activation.getFactHandle() != null ) {
                    // update the Activation in the WM
                    InternalFactHandle factHandle = activation.getFactHandle();
                    workingMemory.getEntryPointNode().modifyActivation( factHandle, activation.getPropagationContext(), workingMemory );
                    activation.getPropagationContext().evaluateActionQueue( workingMemory );
                }
                // if the tuple contains expired events 
                for ( LeftTuple tuple = activation.getTuple(); tuple != null; tuple = tuple.getParent() ) {
                    if ( tuple.getLastHandle().isEvent() ) {
                        EventFactHandle handle = (EventFactHandle) tuple.getLastHandle();
                        // decrease the activation count for the event
                        handle.decreaseActivationsCount();
                        // handles "expire" only in stream mode.
                        if ( handle.isExpired() ) {
                            if ( handle.getActivationsCount() <= 0 ) {
                                // and if no more activations, retract the handle
                                handle.getEntryPoint().retract( handle );
                            }
                        }
                    }
                }
            }

            eventsupport.getAgendaEventSupport().fireAfterActivationFired( activation,
                                                                           this.workingMemory );
            
            unstageActivations();
        } finally {
            isFiringActivation = false;
            if (mustNotifyHalt) {
                mustNotifyHalt = false;
                notifyHalt();
            }
            this.workingMemory.endOperation();
        }
    }


    public synchronized boolean fireTimedActivation( final Activation activation, boolean saveForLater ) throws ConsequenceException {
        //TODO : "save for later" : put activation in queue if halted, then dispatch again on next fire
        if ( ! this.halt.get() ) {
            fireActivation( activation );
            return ! this.halt.get();
        } else {
            return false;
        }
    }

    public void increaseActiveActivations() {
        this.activeActivations++;
    }

    public void decreaseActiveActivations() {
        this.activeActivations--;
    }

    public void increaseDormantActivations() {
        this.dormantActivations++;
    }

    public void decreaseDormantActivations() {
        this.dormantActivations--;
    }

    public int getActiveActivations() {
        return this.activeActivations;
    }

    public int getDormantActivations() {
        return this.dormantActivations;
    }

    /**
     * @inheritDoc
     */
    public boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName,
                                               String ruleName,
                                               long processInstanceId) {

        RuleFlowGroup systemRuleFlowGroup = this.getRuleFlowGroup( ruleflowGroupName );

        for (ActivationNode aSystemRuleFlowGroup : systemRuleFlowGroup) {
            Activation activation = aSystemRuleFlowGroup.getActivation();
            if (ruleName.equals(activation.getRule().getName())) {
                if (checkProcessInstance(activation, processInstanceId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkProcessInstance(Activation activation,
                                         long processInstanceId) {
        final Map<String, Declaration> declarations = activation.getSubRule().getOuterDeclarations();
        for ( Declaration declaration : declarations.values() ) {
            if ( "processInstance".equals( declaration.getIdentifier() ) ) {
                Object value = declaration.getValue( workingMemory,
                                                     activation.getTuple().get( declaration ).getObject() );
                if ( value instanceof ProcessInstance ) {
                    return ((ProcessInstance) value).getId() == processInstanceId;
                }
            }
        }
        return true;
    }

    public void addRuleFlowGroupListener(String ruleFlowGroup,
                                         RuleFlowGroupListener listener) {
        InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) this.getRuleFlowGroup( ruleFlowGroup );
        rfg.addRuleFlowGroupListener( listener );
    }

    public void removeRuleFlowGroupListener(String ruleFlowGroup,
                                            RuleFlowGroupListener listener) {
        InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) this.getRuleFlowGroup( ruleFlowGroup );
        rfg.removeRuleFlowGroupListener( listener );
    }

    public String getFocusName() {
        return this.getFocus().getName();
    }

    public void fireUntilHalt() {
        fireUntilHalt( null );
    }

    public void fireUntilHalt(final AgendaFilter agendaFilter) {
        unstageActivations();
        this.halt.set( false );
        while ( continueFiring( -1 ) ) {
            boolean fired = fireNextItem( agendaFilter );
            fired = fired || !((AbstractWorkingMemory) this.workingMemory).getActionQueue().isEmpty();
            this.workingMemory.executeQueuedActions();
            if ( !fired ) {
                try {
                    synchronized ( this.halt ) {
                        if( !this.halt.get() ) this.halt.wait();
                    }
                } catch ( InterruptedException e ) {
                    this.halt.set( true );
                }
            } else {
                this.workingMemory.executeQueuedActions();
            }
        }
    }

    public int fireAllRules(AgendaFilter agendaFilter,
                            int fireLimit) {
        unstageActivations();
        this.halt.set( false );
        int fireCount = 0;
        while ( continueFiring( fireLimit ) && fireNextItem( agendaFilter ) ) {
            fireCount++;
            fireLimit = updateFireLimit( fireLimit );
            this.workingMemory.executeQueuedActions();
        }
        if ( this.focusStack.size() == 1 && getMainAgendaGroup().isEmpty() ) {
            // the root MAIN agenda group is empty, reset active to false, so it can receive more activations.
            getMainAgendaGroup().setActive( false );
        }
        return fireCount;
    }

    private boolean continueFiring(final int fireLimit) {
        return (!halt.get()) && (fireLimit != 0);
    }

    private int updateFireLimit(final int fireLimit) {
        return fireLimit > 0 ? fireLimit - 1 : fireLimit;
    }

    public void notifyHalt() {
        synchronized ( this.halt ) {
            this.halt.notifyAll();
        }
    }

    public void halt() {
        this.halt.set( true );
        notifyHalt();
    }

    public ConsequenceExceptionHandler getConsequenceExceptionHandler() {
        return this.legacyConsequenceExceptionHandler;
    }

    public void setActivationsFilter(ActivationsFilter filter) {
        this.activationsFilter = filter;
    }

    public ActivationsFilter getActivationsFilter() {
        return this.activationsFilter;
    }
}
