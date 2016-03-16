/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.reteoo.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.DefaultKnowledgeHelper;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.common.ActivationGroupImpl;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationsFilter;
import org.drools.core.common.AgendaGroupFactory;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ScheduledAgendaItem;
import org.drools.core.common.Scheduler;
import org.drools.core.common.TruthMaintenanceSystemHelper;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.ExecutableEntry;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.ConsequenceException;
import org.drools.core.spi.ConsequenceExceptionHandler;
import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleFlowGroup;
import org.drools.core.spi.Tuple;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.ExpressionIntervalTimer;
import org.drools.core.time.impl.Timer;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.StringUtils;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
public class ReteAgenda<M extends ModedAssertion<M>>
        implements
        Externalizable,
        InternalAgenda {

    protected static final transient Logger log = LoggerFactory.getLogger(ReteAgenda.class);

    private static final long serialVersionUID = 510l;

    /** Working memory of this Agenda. */
    protected InternalWorkingMemory workingMemory;

    private org.drools.core.util.LinkedList<ScheduledAgendaItem<M>> scheduledActivations;

    /** Items time-delayed. */

    private Map<String, InternalAgendaGroup> agendaGroups;

    private Map<String, InternalActivationGroup> activationGroups;

    private LinkedList<AgendaGroup> focusStack;

    private InternalAgendaGroup currentModule;

    private InternalAgendaGroup main;

    private org.drools.core.util.LinkedList<RuleAgendaItem> eager;

    private AgendaGroupFactory agendaGroupFactory;

    protected KnowledgeHelper knowledgeHelper;

    private ConsequenceExceptionHandler legacyConsequenceExceptionHandler;

    private org.kie.api.runtime.rule.ConsequenceExceptionHandler consequenceExceptionHandler;

    protected volatile AtomicBoolean halt = new AtomicBoolean(false);

    protected int activationCounter;

    private boolean declarativeAgenda;

    private ObjectTypeConf activationObjectTypeConf;

    private ActivationsFilter activationsFilter;

    private volatile boolean isFiringActivation = false;

    private volatile boolean mustNotifyHalt = false;

    // @TODO make serialisation work
    private InternalActivationGroup stagedActivations;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public ReteAgenda() {
    }

    /**
     * Construct.
     *
     * @param kBase
     *            The <code>InternalKnowledgeBase</code> of this agenda.
     */
    public ReteAgenda(InternalKnowledgeBase kBase) {
        this(kBase,
             true);
    }

    /**
     * Construct.
     *
     * @param kBase
     *            The <code>InternalKnowledgeBase</code> of this agenda.
     * @param initMain
     *            Flag to initialize the MAIN agenda group
     */
    public ReteAgenda(InternalKnowledgeBase kBase,
                      boolean initMain) {

        this.agendaGroups = new HashMap<String, InternalAgendaGroup>();
        this.activationGroups = new HashMap<String, InternalActivationGroup>();
        this.focusStack = new LinkedList<AgendaGroup>();
        this.scheduledActivations = new org.drools.core.util.LinkedList<ScheduledAgendaItem<M>>();
        this.agendaGroupFactory = kBase.getConfiguration().getAgendaGroupFactory();

        if (initMain) {
            // MAIN should always be the first AgendaGroup and can never be
            // removed
            this.main = agendaGroupFactory.createAgendaGroup(AgendaGroup.MAIN,
                                                             kBase);

            this.agendaGroups.put(AgendaGroup.MAIN,
                                  this.main);

            this.focusStack.add(this.main);
        }
        eager = new org.drools.core.util.LinkedList<RuleAgendaItem>();

        Object object = ClassUtils.instantiateObject(kBase.getConfiguration().getConsequenceExceptionHandler(),
                                                     kBase.getConfiguration().getClassLoader());
        if (object instanceof ConsequenceExceptionHandler) {
            this.legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) object;
        } else {
            this.consequenceExceptionHandler = (org.kie.api.runtime.rule.ConsequenceExceptionHandler) object;
        }

        this.declarativeAgenda = kBase.getConfiguration().isDeclarativeAgenda();
    }

    public RuleAgendaItem createRuleAgendaItem(final int salience,
                                               final PathMemory rs,
                                               final TerminalNode rtn) {
        String agendaGroupName = rtn.getRule().getAgendaGroup();
        String ruleFlowGroupName = rtn.getRule().getRuleFlowGroup();

        RuleAgendaItem lazyAgendaItem;
        if (!StringUtils.isEmpty(ruleFlowGroupName)) {
            lazyAgendaItem = new RuleAgendaItem(activationCounter++, null, salience, null, rs, rtn, isDeclarativeAgenda(), (InternalAgendaGroup) getAgendaGroup(ruleFlowGroupName));
        } else {
            lazyAgendaItem = new RuleAgendaItem(activationCounter++, null, salience, null, rs, rtn, isDeclarativeAgenda(), (InternalAgendaGroup) getRuleFlowGroup(agendaGroupName));
        }

        return lazyAgendaItem;
    }

    public AgendaItem createAgendaItem(RuleTerminalNodeLeftTuple rtnLeftTuple,
                                       final int salience,
                                       final PropagationContext context,
                                       RuleAgendaItem ruleAgendaItem,
                                       InternalAgendaGroup agendaGroup) {
        rtnLeftTuple.init(activationCounter++,
                          salience,
                          context,
                          ruleAgendaItem, agendaGroup);
        rtnLeftTuple.setContextObject( rtnLeftTuple );
        return rtnLeftTuple;
    }

    public ScheduledAgendaItem createScheduledAgendaItem(final Tuple tuple,
                                                         final PropagationContext context,
                                                         final TerminalNode rtn,
                                                         InternalAgendaGroup agendaGroup) {
        RuleTerminalNodeLeftTuple rtnLeftTuple = ( RuleTerminalNodeLeftTuple ) tuple;
        rtnLeftTuple.init(activationCounter++, 0, context, null, agendaGroup );
        ScheduledAgendaItem item =  new ScheduledAgendaItem( rtnLeftTuple, this );
        tuple.setContextObject( item );
        return item;
    }

    public void setWorkingMemory(final InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        RuleBaseConfiguration rbc = this.workingMemory.getKnowledgeBase().getConfiguration();
        if ( rbc.isSequential() ) {
            this.knowledgeHelper = rbc.getComponentFactory().getKnowledgeHelperFactory().newSequentialKnowledgeHelper( this.workingMemory );
        } else {
            this.knowledgeHelper = rbc.getComponentFactory().getKnowledgeHelperFactory().newStatefulKnowledgeHelper( this.workingMemory );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        workingMemory = (InternalWorkingMemory) in.readObject();
        scheduledActivations = (org.drools.core.util.LinkedList) in.readObject();
        agendaGroups = (Map) in.readObject();
        activationGroups = (Map) in.readObject();
        focusStack = (LinkedList) in.readObject();
        currentModule = (InternalAgendaGroup) in.readObject();
        main = (InternalAgendaGroup) in.readObject();
        agendaGroupFactory = (AgendaGroupFactory) in.readObject();
        knowledgeHelper = (KnowledgeHelper) in.readObject();
        legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) in.readObject();
        declarativeAgenda = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( workingMemory );
        out.writeObject( scheduledActivations );
        out.writeObject( agendaGroups );
        out.writeObject( activationGroups );
        out.writeObject( focusStack );
        out.writeObject( currentModule );
        out.writeObject( main );
        out.writeObject( agendaGroupFactory );
        out.writeObject( knowledgeHelper );
        out.writeObject( legacyConsequenceExceptionHandler );
        out.writeBoolean( declarativeAgenda );
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getWorkingMemory()
     */
    public InternalWorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    @Override
    public void addEagerRuleAgendaItem(RuleAgendaItem item) {
        if ( item.isInList(eager) ) {
            return;
        }

        if ( log.isTraceEnabled() ) {
            log.trace("Added {} to eager evaluation list.", item.getRule().getName() );
        }
        eager.add( item );
    }

    @Override
    public void removeEagerRuleAgendaItem(RuleAgendaItem item) {
        if ( !item.isInList(eager) ) {
            return;
        }

        if ( log.isTraceEnabled() ) {
            log.trace("Removed {} from eager evaluation list.", item.getRule().getName() );
        }
        eager.remove( item );
    }

    @Override
    public void addQueryAgendaItem(final RuleAgendaItem item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeQueryAgendaItem(RuleAgendaItem item) {
        throw new UnsupportedOperationException();
    }

    /**
     * Schedule an agenda item for delayed firing.
     *
     * @param item
     *            The item to schedule.
     */
    public void scheduleItem(final ScheduledAgendaItem item,
                             final InternalWorkingMemory wm) {
        this.scheduledActivations.add( item );
        item.setEnqueued( true );
        if ( item.getPropagationContext().getReaderContext() == null ) {
            // this is not a serialization propagation, so schedule it
            // otherwise the timer will be correlated with this activation later during the
            // deserialization of timers
            scheduleAgendaItem( item, wm );
        }
    }

    /**
     * If the item belongs to an activation group, add it
     *
     * @param item
     */
    public void addItemToActivationGroup(final AgendaItem item) {
        String group = item.getRule().getActivationGroup();
        if ( group != null && group.length() > 0 ) {
            InternalActivationGroup actgroup = getActivationGroup( group );

            actgroup.addActivation( item );
        }
    }

    public InternalActivationGroup getStageActivationsGroup() {
        if ( stagedActivations == null ) {
            stagedActivations = new ActivationGroupImpl( this, "staged activations" );
        }
        return stagedActivations;
    }

    @Override
    public void insertAndStageActivation(final AgendaItem activation) {
        if ( activationObjectTypeConf == null ) {
            EntryPointId ep = workingMemory.getEntryPoint();
            activationObjectTypeConf = ((InternalWorkingMemoryEntryPoint) workingMemory.getWorkingMemoryEntryPoint( ep.getEntryPointId() )).getObjectTypeConfigurationRegistry().getObjectTypeConf( ep,
                                                                                                                                                                                                    activation );
        }

        InternalFactHandle factHandle = workingMemory.getFactHandleFactory().newFactHandle( activation, activationObjectTypeConf, workingMemory, workingMemory );
        workingMemory.getEntryPointNode().assertActivation( factHandle, activation.getPropagationContext(), workingMemory );
        activation.setActivationFactHandle( factHandle );

        if ( !activation.isCanceled() && (activation.getBlockers() == null || activation.getBlockers().isEmpty()) ) {
            // All activations started off staged, they are unstaged if they are blocked or
            // allowed to move onto the actual agenda for firing.
            getStageActivationsGroup().addActivation( activation );
        }
    }

    public boolean addActivation(final AgendaItem activation) {
        if ( declarativeAgenda ) {
            insertAndStageActivation( activation );
            return true;
        } else {
            addActivation( activation, true );
            return true;
        }
    }

    public boolean isDeclarativeAgenda() {
        return declarativeAgenda;
    }

    public void removeActivation(final AgendaItem activation) {
        if ( declarativeAgenda ) {
            workingMemory.getEntryPointNode().retractActivation( activation.getActivationFactHandle(), activation.getPropagationContext(), workingMemory );

            if ( activation.getActivationGroupNode() != null ) {
                activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
            }
        }
        if ( activation instanceof ScheduledAgendaItem ) {
            removeScheduleItem( (ScheduledAgendaItem) activation );
        }
    }

    public void modifyActivation(final AgendaItem activation,
                                 boolean previouslyActive) {
        if ( declarativeAgenda ) {
            InternalFactHandle factHandle = activation.getActivationFactHandle();
            workingMemory.getEntryPointNode().modifyActivation( factHandle, activation.getPropagationContext(), workingMemory );

            if ( previouslyActive ) {
                // already activated
                return;
            }

            if ( activation.isCanceled() || (activation.getBlockers() != null && activation.getBlockers().size() > 0) ) {
                // it's blocked so do nothing
                return;
            }

            // All activations started off staged, they are unstaged if they are blocked or
            // allowed to move onto the actual agenda for firing.
            InternalActivationGroup activationGroup = getStageActivationsGroup();
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
                    ScheduledAgendaItem schItem = (ScheduledAgendaItem) activation;
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
        org.drools.core.util.LinkedList<ActivationGroupNode> list = getStageActivationsGroup().getList();

        final EventSupport eventsupport = (EventSupport) this.workingMemory;
        for ( ActivationGroupNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
            AgendaItem item = (AgendaItem) node.getActivation();
            // This must be set to false otherwise modify won't work properly
            item.setQueued( false );
            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                          this.workingMemory,
                                                                          MatchCancelledCause.CLEAR );
        }
    }

    public int unstageActivations() {
        if ( !declarativeAgenda || getStageActivationsGroup().isEmpty() ) {
            return 0;
        }
        org.drools.core.util.LinkedList<ActivationGroupNode> list = getStageActivationsGroup().getList();

        int i = 0;
        for ( ActivationGroupNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
            AgendaItem item = (AgendaItem) node.getActivation();
            item.setActivationGroupNode( null );

            addActivation( item, false );
            i++;
        }

        workingMemory.notifyWaitOnRest();

        return i;
    }

    public void addActivation(AgendaItem item,
                              boolean notify) {
        RuleImpl rule = item.getRule();
        item.setQueued( true );

        // set the focus if rule autoFocus is true
        if ( rule.getAutoFocus() ) {
            this.setFocus( item.getPropagationContext(),
                           rule.getAgendaGroup() );
        }

        // adds item to activation group if appropriate
        addItemToActivationGroup( item );

        final Timer timer = rule.getTimer();
        if ( timer != null && item instanceof ScheduledAgendaItem ) {
            ScheduledAgendaItem sitem = (ScheduledAgendaItem) item;
            if ( sitem.isEnqueued() ) {
                // it's about to be re-added to scheduled list, so remove first
                this.scheduledActivations.remove( sitem );
            }
            scheduleItem( sitem,
                          workingMemory );
        } else {
            addAgendaItemToGroup( item );
        }
        if ( notify ) {
            // if an activation is currently firing allows to completely fire it before to send the notify
            if ( isFiringActivation ) {
                mustNotifyHalt = true;
            } else {
                workingMemory.notifyWaitOnRest();
            }
        }
    }

    @Override
    public void addAgendaItemToGroup(AgendaItem item) {
        InternalAgendaGroup agendaGroup = (InternalAgendaGroup) this.getAgendaGroup(item.getRule().getAgendaGroup());
        agendaGroup.add( item );
    }

    public void removeScheduleItem(final ScheduledAgendaItem item) {
        if ( item.isEnqueued() ) {
            this.scheduledActivations.remove( item );
            item.setEnqueued( false );
            removeAgendaItem( item );
        }
    }

    public void addAgendaGroup(final AgendaGroup agendaGroup) {
        this.agendaGroups.put( agendaGroup.getName(),
                               (InternalAgendaGroup) agendaGroup );
    }

    public boolean createActivation(final Tuple tuple,
                                    final PropagationContext context,
                                    final InternalWorkingMemory workingMemory,
                                    final TerminalNode rtn) {
        // First process control rules
        // Control rules do increase ActivationCountForEvent and agenda ActivateActivations, they do not currently fire events
        // ControlRules for now re-use the same PropagationContext
        if ( rtn.isFireDirect() ) {
            // Fire RunLevel == 0 straight away. agenda-groups, rule-flow groups, salience are ignored
            AgendaItem item = createAgendaItem( (RuleTerminalNodeLeftTuple)tuple, 0, context, null, null );
            tuple.setContextObject( item );
            if ( activationsFilter != null && !activationsFilter.accept( item,
                                                                         workingMemory,
                                                                         rtn ) ) {
                return false;
            }

            item.setQueued(true);
            tuple.increaseActivationCountForEvents();
            fireActivation( item); // Control rules fire straight away.
            return true;
        }

        final RuleImpl rule = rtn.getRule();
        AgendaItem item;
        final Timer timer = rule.getTimer();
        InternalAgendaGroup agendaGroup = (InternalAgendaGroup) getAgendaGroup( rule.getAgendaGroup() );
        if ( timer != null ) {
            item = createScheduledAgendaItem( tuple,
                                              context,
                                              rtn,
                                              agendaGroup
                                            );
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

            if ( rule.isLockOnActive() && agendaGroup.isActive()  && agendaGroup.getAutoFocusActivator() != context ) {
                // do not add the activation if the rule is "lock-on-active" and the AgendaGroup is active
                if ( tuple.getContextObject() == null ) {
                    tuple.setContextObject( Boolean.TRUE ); // this is so we can do a check with a bit more intent than a null check on modify
                }
                return false;
            }

            item = createAgendaItem( (RuleTerminalNodeLeftTuple)tuple,
                                     0,
                                     context,
                                     null,
                                     agendaGroup
                                   );
            item.setSalience( rule.getSalience().getValue( new DefaultKnowledgeHelper( item, workingMemory ),
                                                           rule,
                                                           workingMemory ) );
        }

        if ( activationsFilter != null && !activationsFilter.accept( item,
                                                                     workingMemory,
                                                                     rtn ) ) {
            return false;
        }
        item.setQueued(true);
        tuple.increaseActivationCountForEvents();

        ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCreated( item,
                                                                                      workingMemory );
        return true;
    }

    public boolean createPostponedActivation(final LeftTuple tuple,
                                             final PropagationContext context,
                                             final InternalWorkingMemory workingMemory,
                                             final TerminalNode rtn) {

        final RuleImpl rule = rtn.getRule();
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
        // do not add the activation if the rule is "lock-on-active" and the AgendaGroup is active
        if ( rule.isLockOnActive() && agendaGroup.isActive() && agendaGroup.getAutoFocusActivator() != context ) {
            return false;
        }

        item = createAgendaItem( (RuleTerminalNodeLeftTuple)tuple,
                                 0,
                                 context,
                                 null,
                                 agendaGroup
                               );
        item.setSalience( rule.getSalience().getValue( new DefaultKnowledgeHelper( item, workingMemory ),
                                                       rule,
                                                       workingMemory ) );

        if ( activationsFilter != null && !activationsFilter.accept( item,
                                                                     workingMemory,
                                                                     rtn ) ) {
            return false;
        }
        item.setQueued(true);
        tuple.increaseActivationCountForEvents();

        ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCreated( item,
                                                                                      workingMemory );
        return true;
    }

    public boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName, String ruleName, long processInstanceId) {
        return isRuleInstanceAgendaItem(ruleflowGroupName, ruleName, processInstanceId);
    }

    public void cancelActivation(final Tuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory,
                                 final Activation activation,
                                 final TerminalNode rtn) {
        AgendaItem item = (AgendaItem) activation;
        item.removeAllBlockersAndBlocked( this );

        if ( isDeclarativeAgenda() && activation.getActivationFactHandle() == null ) {
            // This a control rule activation, nothing to do except update counters. As control rules are not in agenda-groups etc.
            return;
        } else {
            // we are retracting an actual Activation, so also remove it and it's handle from the WM.
            removeActivation( item );
        }

        if ( activation.isQueued() ) {
            // on fact expiration, we don't remove the activation, but let it fire
            if ( context.getType() != PropagationContext.EXPIRATION || context.getFactHandle() == null ) {
                activation.remove();

                if ( activation.getActivationGroupNode() != null ) {
                    activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
                }
                leftTuple.decreaseActivationCountForEvents();

                ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCancelled( activation,
                                                                                                workingMemory,
                                                                                                MatchCancelledCause.WME_MODIFY );
            }
        }

        if ( item.getActivationUnMatchListener() != null ) {
            item.getActivationUnMatchListener().unMatch( workingMemory.getKnowledgeRuntime(), item );
        }

        TruthMaintenanceSystemHelper.removeLogicalDependencies(activation,
                                                               context,
                                                               rtn.getRule());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#setFocus(org.kie.spi.AgendaGroup)
     */
    @Override
    public boolean setFocus(final AgendaGroup agendaGroup) {
        // Set the focus to the agendaGroup if it doesn't already have the focus
        if ( this.focusStack.getLast() != agendaGroup ) {
            ((InternalAgendaGroup) this.focusStack.getLast()).setActive( false );
            this.focusStack.add( agendaGroup );
            InternalAgendaGroup igroup = (InternalAgendaGroup) agendaGroup;
            igroup.setActive( true );
            igroup.setActivatedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
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
     * @see org.kie.common.AgendaI#setFocus(java.lang.String)
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
     * @see org.kie.common.AgendaI#getFocus()
     */
    public AgendaGroup getFocus() {
        return this.focusStack.getLast();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getNextFocus()
     */
    public InternalAgendaGroup getNextFocus() {
        InternalAgendaGroup agendaGroup;
        // Iterate until we find a populate AgendaModule or we reach the MAIN,
        // default, AgendaGroup
        while ( true ) {
            agendaGroup = (InternalAgendaGroup) this.focusStack.getLast();

            if ( !agendaGroup.isAutoDeactivate() ) {
                // does not automatically pop, when empty, so always return, even if empty
                break;
            }

            final boolean empty = agendaGroup.isEmpty();

            // No populated queues found so pop the focusStack and repeat
            if ( empty && (this.focusStack.size() > 1) ) {
                agendaGroup.setActive( false );
                this.focusStack.removeLast();
                if ( agendaGroup.isAutoDeactivate() && !agendaGroup.getNodeInstances().isEmpty() ) {
                    innerDeactiveRuleFlowGroup((InternalRuleFlowGroup) agendaGroup);
                }
                final EventSupport eventsupport = (EventSupport) this.workingMemory;
                eventsupport.getAgendaEventSupport().fireAgendaGroupPopped( agendaGroup,
                                                                            this.workingMemory );
            } else {
                agendaGroup = (empty) ? null : agendaGroup;
                break;
            }
        }

        if ( agendaGroup != null &&  !agendaGroup.isActive() ) {
                // only update recency, if not already active. It may be active already if the use called setFocus
                agendaGroup.setActivatedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
                agendaGroup.setActive( true );
        }
        return agendaGroup;
    }

    public RuleAgendaItem peekNextRule() {
        return (RuleAgendaItem) ((InternalAgendaGroup) this.focusStack.peek()).peek();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#setCurrentAgendaGroup(org.kie.spi.AgendaGroup)
     */
    public void setCurrentAgendaGroup(final InternalAgendaGroup agendaGroup) {
        this.currentModule = agendaGroup;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getCurrentAgendaGroup()
     */
    public AgendaGroup getCurrentAgendaGroup() {
        return this.currentModule;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getAgendaGroup(java.lang.String)
     */
    public AgendaGroup getAgendaGroup(final String name) {
        return getAgendaGroup( name, workingMemory == null ? null : workingMemory.getKnowledgeBase() );
    }

    public AgendaGroup getAgendaGroup(final String name,
                                      InternalKnowledgeBase kBase) {
        String groupName = (name == null || name.length() == 0) ? AgendaGroup.MAIN : name;

        InternalAgendaGroup agendaGroup = this.agendaGroups.get( groupName );
        if ( agendaGroup == null ) {
            // The AgendaGroup is defined but not yet added to the
            // Agenda, so create the AgendaGroup and add to the Agenda.
            agendaGroup = agendaGroupFactory.createAgendaGroup( name,
                                                                kBase );
            addAgendaGroup( agendaGroup );
        }

        agendaGroup.setWorkingMemory( getWorkingMemory() );

        return agendaGroup;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getAgendaGroups()
     */
    public AgendaGroup[] getAgendaGroups() {
        return this.agendaGroups.values().toArray( new AgendaGroup[this.agendaGroups.size()] );
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
     * @see org.kie.common.AgendaI#getStack()
     */
    public AgendaGroup[] getStack() {
        return this.focusStack.toArray( new AgendaGroup[this.focusStack.size()] );
    }

    public LinkedList<AgendaGroup> getStackList() {
        return this.focusStack;
    }

    public void addAgendaGroupOnStack(AgendaGroup agendaGroup) {
        if ( focusStack.isEmpty() || focusStack.getLast() != agendaGroup ) {
            focusStack.add( agendaGroup );
        }
    }

    public Map<String, InternalActivationGroup> getActivationGroupsMap() {
        return this.activationGroups;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getActivationGroup(java.lang.String)
     */
    public InternalActivationGroup getActivationGroup(final String name) {
        ActivationGroupImpl activationGroup = (ActivationGroupImpl) this.activationGroups.get( name );
        if ( activationGroup == null ) {
            activationGroup = new ActivationGroupImpl( this, name );
            this.activationGroups.put( name,
                                       activationGroup );
        }
        return activationGroup;
    }

    public RuleFlowGroup getRuleFlowGroup(final String name) {
        return ( RuleFlowGroup ) getAgendaGroup(name);
    }

    public void activateRuleFlowGroup(final String name) {
        InternalRuleFlowGroup group =  (InternalRuleFlowGroup) getRuleFlowGroup( name );
        activateRuleFlowGroup(group, -1, null);
    }

    public void activateRuleFlowGroup(final String name,
                                      long processInstanceId,
                                      String nodeInstanceId) {
        InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) getRuleFlowGroup( name );
        activateRuleFlowGroup( ruleFlowGroup, processInstanceId, nodeInstanceId );
    }

    public void activateRuleFlowGroup(final InternalRuleFlowGroup group, long processInstanceId, String nodeInstanceId) {
        ((EventSupport) this.workingMemory).getAgendaEventSupport().fireBeforeRuleFlowGroupActivated( group, this.workingMemory );
        group.setActive( true );
        group.hasRuleFlowListener(true);
        if ( !StringUtils.isEmpty( nodeInstanceId ) ) {
            group.addNodeInstance( processInstanceId, nodeInstanceId );
            group.setActive( true );
        }
        setFocus(group);
        ((EventSupport) this.workingMemory).getAgendaEventSupport().fireAfterRuleFlowGroupActivated( group,
                                                                                                     this.workingMemory );
    }

    public void deactivateRuleFlowGroup(final String name) {
        deactivateRuleFlowGroup( (InternalRuleFlowGroup) getRuleFlowGroup( name ) );
    }

    public void deactivateRuleFlowGroup(final InternalRuleFlowGroup group) {
        if ( !group.isRuleFlowListener() ) {
            return;
        }
        ((EventSupport) this.workingMemory).getAgendaEventSupport().fireBeforeRuleFlowGroupDeactivated( group,
                                                                                                        this.workingMemory );
        while ( this.focusStack.remove( group ) ); // keep removing while group is on the stack
        group.setActive( false );
        innerDeactiveRuleFlowGroup( group );
    }

    private void innerDeactiveRuleFlowGroup(InternalRuleFlowGroup group) {
        group.hasRuleFlowListener( false );
        group.getNodeInstances().clear();
        ((EventSupport) this.workingMemory).getAgendaEventSupport().fireAfterRuleFlowGroupDeactivated( group, this.workingMemory );
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#focusStackSize()
     */
    public int focusStackSize() {
        int size = 0;
        for ( final AgendaGroup group : this.focusStack ) {
            size += group.size();
        }
        return size;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#agendaSize()
     */
    public int agendaSize() {
        int size = 0;
        for ( InternalAgendaGroup internalAgendaGroup : this.agendaGroups.values() ) {
            size += internalAgendaGroup.size();
        }
        return size;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getActivations()
     */
    public Activation[] getActivations() {
        final List<Activation> list = new ArrayList<Activation>();
        for ( final InternalAgendaGroup group : this.agendaGroups.values() ) {
            for ( Match activation : group.getActivations() ) {
                list.add( (Activation) activation );
            }
        }
        return list.toArray( new Activation[list.size()] );
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getScheduledActivations()
     */
    public Activation[] getScheduledActivations() {
        Activation[] scheduledActivations = new Activation[this.scheduledActivations.size()];
        int i = 0;
        for ( ScheduledAgendaItem node = this.scheduledActivations.getFirst(); node != null; node = node.getNext() ) {
            scheduledActivations[i++] = node;
        }
        return scheduledActivations;
    }

    public org.drools.core.util.LinkedList<ScheduledAgendaItem<M>> getScheduledActivationsLinkedList() {
        return this.scheduledActivations;
    }

    public void clear() {
        // reset focus stack
        this.focusStack.clear();
        this.focusStack.add( getMainAgendaGroup() );

        // reset scheduled activations
        if ( !this.scheduledActivations.isEmpty() ) {
            for ( ScheduledAgendaItem item = this.scheduledActivations.removeFirst(); item != null; item = this.scheduledActivations.removeFirst() ) {
                item.setEnqueued( false );
                removeAgendaItem( item );
            }
        }

        // reset staged activations
        getStageActivationsGroup().clear();

        //reset all agenda groups
        for ( InternalAgendaGroup group : this.agendaGroups.values() ) {
            group.reset();
        }

        // reset all activation groups.
        for ( InternalActivationGroup group : this.activationGroups.values() ) {
            group.setTriggeredForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
            group.reset();

        }
    }

    public void clearAndCancel() {
        // Cancel all items and fire a Cancelled event for each Activation
        for ( InternalAgendaGroup internalAgendaGroup : this.agendaGroups.values() ) {
            clearAndCancelAgendaGroup( internalAgendaGroup );
        }

        final EventSupport eventsupport = (EventSupport) this.workingMemory;
        if ( !this.scheduledActivations.isEmpty() ) {
            for ( ScheduledAgendaItem item = this.scheduledActivations.removeFirst(); item != null; item = this.scheduledActivations.removeFirst() ) {
                item.setEnqueued( false );
                removeAgendaItem( item );
                eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                              this.workingMemory,
                                                                              MatchCancelledCause.CLEAR );
            }
        }

        // cancel all staged activations
        clearAndCancelStagedActivations();

        // cancel all activation groups.
        for ( InternalActivationGroup group : this.activationGroups.values() ) {
            clearAndCancelActivationGroup( group );
        }

    }

    private void scheduleAgendaItem(final ScheduledAgendaItem item, InternalWorkingMemory wm) {
        Trigger trigger = item.getRule().getTimer().createTrigger( item, wm );

        Scheduler.ActivationTimerJob job = new Scheduler.ActivationTimerJob();
        Scheduler.ActivationTimerJobContext ctx = new Scheduler.ActivationTimerJobContext( trigger, item, this );

        JobHandle jobHandle = getWorkingMemory().getTimerService().scheduleJob( job, ctx, trigger );
        item.setJobHandle( jobHandle );
    }

    private void removeAgendaItem(final ScheduledAgendaItem item) {
        getWorkingMemory().getTimerService().removeJob( item.getJobHandle() );
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#clearAgendaGroup(java.lang.String)
     */
    public void clearAndCancelAgendaGroup(final String name) {
        InternalAgendaGroup agendaGroup = this.agendaGroups.get( name );
        if ( agendaGroup != null ) {
            clearAndCancelAgendaGroup( agendaGroup );
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#clearAgendaGroup(org.kie.common.AgendaGroupImpl)
     */
    public void clearAndCancelAgendaGroup(InternalAgendaGroup agendaGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        ((InternalAgendaGroup) agendaGroup).setClearedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );

        // this is thread safe for BinaryHeapQueue
        // Binary Heap locks while it returns the array and reset's it's own internal array. Lock is released afer getAndClear()
        for ( Activation aQueueable : ((InternalAgendaGroup) agendaGroup).getAndClear() ) {
            final AgendaItem item = (AgendaItem) aQueueable;
            if ( item == null ) {
                continue;
            }

            // this must be set false before removal from the activationGroup.
            // Otherwise the activationGroup will also try to cancel the Actvation
            // Also modify won't work properly
            item.setQueued(false);

            if ( item.getActivationGroupNode() != null ) {
                item.getActivationGroupNode().getActivationGroup().removeActivation( item );
            }

            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                          this.workingMemory,
                                                                          MatchCancelledCause.CLEAR );
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#clearActivationGroup(java.lang.String)
     */
    public void clearAndCancelActivationGroup(final String name) {
        final InternalActivationGroup activationGroup = this.activationGroups.get( name );
        if ( activationGroup != null ) {
            clearAndCancelActivationGroup( activationGroup);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#clearActivationGroup(org.kie.spi.ActivationGroup)
     */
    public void clearAndCancelActivationGroup(final InternalActivationGroup activationGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        activationGroup.setTriggeredForRecency( this.workingMemory.getFactHandleFactory().getRecency() );

        for ( final Iterator it = activationGroup.iterator(); it.hasNext(); ) {
            final ActivationGroupNode node = (ActivationGroupNode) it.next();
            final Activation activation = node.getActivation();
            activation.setActivationGroupNode( null );

            if ( activation.isQueued() ) {
                activation.setQueued(false);
                activation.remove();

                eventsupport.getAgendaEventSupport().fireActivationCancelled( activation,
                                                                              this.workingMemory,
                                                                              MatchCancelledCause.CLEAR );
            }
        }
        activationGroup.reset();
    }

    public void clearAndCancelRuleFlowGroup(final String name) {
        clearAndCancelAgendaGroup( (InternalAgendaGroup) agendaGroups.get( name ) );
    }

    public void clearAndCancelAndCancel(final RuleFlowGroup ruleFlowGroup) {
        clearAndCancelAgendaGroup((InternalAgendaGroup)ruleFlowGroup);
    }

    /**
     * Fire the next scheduled <code>Agenda</code> item, skipping items
     * that are not allowed by the agenda filter.
     *
     * @return true if an activation was fired. false if no more activations
     *              to fire
     *
     * @throws org.drools.core.spi.ConsequenceException
     *             If an error occurs while firing an agenda item.
     */
    public int fireNextItem(final AgendaFilter filter,
                            int fireCount,
                            int fireLimit) throws ConsequenceException {
        boolean tryagain;
        int localFireCount = 0;
        try {
            do {
                evaluateEagerList();
                this.workingMemory.prepareToFireActivation();
                tryagain = false;
                final InternalAgendaGroup group = (InternalAgendaGroup) getNextFocus();
                // if there is a group with focus
                if ( group != null ) {
                    final AgendaItem item = (AgendaItem) group.remove();
                    // if there is an item to fire from that group
                    if ( item != null ) {
                        // if that item is allowed to fire
                        if ( filter == null || filter.accept( item ) ) {
                            // fire it
                            fireActivation( item);
                            localFireCount++;
                        } else {
                            // otherwise cancel it and try the next

                            //necessary to perfom queued actions like signal to a next node in a ruleflow/jbpm process
                            this.workingMemory.executeQueuedActionsForRete();

                            final EventSupport eventsupport = (EventSupport) this.workingMemory;
                            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                                          this.workingMemory,
                                                                                          MatchCancelledCause.FILTER );
                            tryagain = true;
                        }
                    }
                    if ( (AgendaItem) group.peek() == null || !((AgendaItem) group.peek()).getTerminalNode().isFireDirect() ) {
                        // make sure the "fireDirect" meta rules have all impacted first, before unstaging.
                        unstageActivations();
                    }
                }
            } while ( tryagain );
        } finally {
            this.workingMemory.activationFired();
        }
        return localFireCount;
    }

    public void evaluateEagerList() {
        while ( !eager.isEmpty() ) {
            RuleAgendaItem item = eager.removeFirst();
            item.getRuleExecutor().evaluateNetwork(this.workingMemory);
        }
    }

    public int sizeOfRuleFlowGroup(String name) {
        InternalAgendaGroup group = (InternalAgendaGroup) agendaGroups.get(name);
        if (group == null) {
            return 0;
        }
        int count = 0;
        for ( Activation item : group.getActivations() ) {
            count++;
        }
        return count;
    }

    /**
     * Fire this item.
     *
     *
     *
     * @param activation
     *            The activation to fire
     *
     * @throws org.drools.core.spi.ConsequenceException
     *             If an error occurs while attempting to fire the consequence.
     */
    public synchronized void fireActivation(final Activation activation) throws ConsequenceException {
        // We do this first as if a node modifies a fact that causes a recursion
        // on an empty pattern
        // we need to make sure it re-activates
        this.workingMemory.startOperation();
        isFiringActivation = true;
        try {
            final EventSupport eventsupport = (EventSupport) this.workingMemory;

            eventsupport.getAgendaEventSupport().fireBeforeActivationFired( activation,
                                                                            this.workingMemory );

            if ( activation.getActivationGroupNode() != null ) {
                // We know that this rule will cancel all other activations in the group
                // so lets remove the information now, before the consequence fires
                final InternalActivationGroup activationGroup = activation.getActivationGroupNode().getActivationGroup();
                activationGroup.removeActivation( activation );
                clearAndCancelActivationGroup( activationGroup);
            }
            activation.setQueued(false);

            try {

                this.knowledgeHelper.setActivation( activation );
                if ( log.isTraceEnabled() ) {
                    log.trace( "Fire \"{}\" \n{}", activation.getRule().getName(), activation.getTuple() );
                }
                activation.getConsequence().evaluate( this.knowledgeHelper,
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
                if ( activation.getActivationFactHandle() != null ) {
                    // update the Activation in the WM
                    InternalFactHandle factHandle = activation.getActivationFactHandle();
                    workingMemory.getEntryPointNode().modifyActivation( factHandle, activation.getPropagationContext(), workingMemory );
                    activation.getPropagationContext().evaluateActionQueue( workingMemory );
                }
                // if the tuple contains expired events
                for ( Tuple tuple = activation.getTuple(); tuple != null; tuple = tuple.getParent() ) {
                    if ( tuple.getFactHandle() == null ) {
                        // can be null for eval, not and exists that have no right input
                        continue;
                    }
                    if ( tuple.getFactHandle().isEvent() ) {
                        EventFactHandle handle = (EventFactHandle) tuple.getFactHandle();
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
            if ( mustNotifyHalt ) {
                mustNotifyHalt = false;
                workingMemory.notifyWaitOnRest();
            }
            this.workingMemory.endOperation();
        }
    }

    public synchronized void fireConsequenceEvent(Activation activation, String consequenceName) throws ConsequenceException {
        throw new UnsupportedOperationException("Cannot invoke fireActivationEvent on ReteAgenda");
    }

    public synchronized boolean fireTimedActivation(final Activation activation) throws ConsequenceException {
        //TODO : "save for later" : put activation in queue if halted, then dispatch again on next fire
        if ( !this.halt.get() ) {
            fireActivation( activation);
            return !this.halt.get();
        } else {
            return false;
        }
    }

    /**
     * @inheritDoc
     */
    public boolean isRuleInstanceAgendaItem(String ruleflowGroupName,
                                            String ruleName,
                                            long processInstanceId) {

        RuleFlowGroup systemRuleFlowGroup = this.getRuleFlowGroup( ruleflowGroupName );

        Match[] matches = ((InternalAgendaGroup)systemRuleFlowGroup).getActivations();
        for ( Match match : matches ) {
            Activation act = ( Activation ) match;
            if ( ruleName.equals( act.getRule().getName() ) ) {
                if ( checkProcessInstance( act, processInstanceId ) ) {
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

    public String getFocusName() {
        return this.getFocus().getName();
    }

    @Override
    public void stageLeftTuple(RuleAgendaItem ruleAgendaItem, AgendaItem justified) {
        getStageActivationsGroup().addActivation(justified);
    }

    public void fireUntilHalt() {
        fireUntilHalt( null );
    }

    public void fireUntilHalt(final AgendaFilter agendaFilter) {
        unstageActivations();
        this.halt.set( false );
        if ( log.isTraceEnabled() ) {
            log.trace("Starting fireUntilHalt");
        }
        while ( isFiring() ) {
            boolean fired = fireNextItem( agendaFilter, 0, -1 ) >= 0 ||
                            !((ReteWorkingMemory) this.workingMemory).getActionQueue().isEmpty();
            this.workingMemory.executeQueuedActionsForRete();
            if ( !fired ) {
                try {
                    synchronized ( this.halt ) {
                        if ( !this.halt.get() ) {
                            this.halt.wait();
                        }
                    }
                } catch ( InterruptedException e ) {
                    this.halt.set( true );
                }
            } else {
                this.workingMemory.executeQueuedActionsForRete();
            }
        }
        if ( log.isTraceEnabled() ) {
            log.trace( "Ending fireUntilHalt");
        }
    }

    public int fireAllRules(AgendaFilter agendaFilter,
                            int fireLimit) {
        unstageActivations();
        this.halt.set( false );
        int fireCount = 0;
        int returnedFireCount = 0;
        do {
            returnedFireCount = fireNextItem( agendaFilter, fireCount, fireLimit );
            fireCount += returnedFireCount;
            this.workingMemory.executeQueuedActionsForRete();
        } while ( isFiring() && returnedFireCount != 0 && (fireLimit == -1 || (fireCount < fireLimit)) );
        if ( this.focusStack.size() == 1 && getMainAgendaGroup().isEmpty() ) {
            // the root MAIN agenda group is empty, reset active to false, so it can receive more activations.
            getMainAgendaGroup().setActive( false );
        }
        return fireCount;
    }

    @Override
    public boolean isFiring() {
        return !halt.get();
    }

    @Override
    public void executeTask( ExecutableEntry executable ) {
        if( this.halt.compareAndSet( true, false ) ) {
            try {
                executable.execute();
            } finally {
                this.halt.set( true );
            }
        }
    }

    public void halt() {
        this.halt.set( true );
        workingMemory.notifyWaitOnRest();
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
