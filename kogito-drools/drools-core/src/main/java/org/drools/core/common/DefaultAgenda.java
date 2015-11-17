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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.ExecutableEntry;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.phreak.SynchronizedPropagationList;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.QueryImpl;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.ConsequenceException;
import org.drools.core.spi.ConsequenceExceptionHandler;
import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleFlowGroup;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.StringUtils;
import org.drools.core.util.index.LeftTupleList;
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

    public static final String ON_BEFORE_ALL_FIRES_CONSEQUENCE_NAME = "$onBeforeAllFire$";
    public static final String ON_AFTER_ALL_FIRES_CONSEQUENCE_NAME = "$onAfterAllFire$";
    public static final String ON_DELETE_MATCH_CONSEQUENCE_NAME = "$onDeleteMatch$";

    protected static final transient Logger                      log                = LoggerFactory.getLogger( DefaultAgenda.class );

    private static final long                                    serialVersionUID   = 510l;

    /** Working memory of this Agenda. */
    protected InternalWorkingMemory                              workingMemory;

    /** Items time-delayed. */

    private Map<String, InternalAgendaGroup>                     agendaGroups;

    private Map<String, InternalActivationGroup>                 activationGroups;

    private LinkedList<AgendaGroup>                              focusStack;

    private InternalAgendaGroup                                  main;

    private final org.drools.core.util.LinkedList<RuleAgendaItem> eager = new org.drools.core.util.LinkedList<RuleAgendaItem>();

    private final Map<QueryImpl, RuleAgendaItem>                 queries = new HashMap<QueryImpl, RuleAgendaItem>();

    private AgendaGroupFactory                                   agendaGroupFactory;

    protected KnowledgeHelper                                    knowledgeHelper;

    private ConsequenceExceptionHandler                          legacyConsequenceExceptionHandler;

    private org.kie.api.runtime.rule.ConsequenceExceptionHandler consequenceExceptionHandler;

    protected int                                                activationCounter;

    private boolean                                              declarativeAgenda;
    private boolean                                              sequential;

    private ObjectTypeConf                                       activationObjectTypeConf;

    private ActivationsFilter                                    activationsFilter;

    private volatile ExecutionState                              currentState = ExecutionState.INACTIVE;

    private enum ExecutionState {  // fireAllRule | fireUntilHalt | executeTask <-- required action
        INACTIVE( false ),         // fire        | fire          | exec
        FIRING_ALL_RULES( true ),  // do nothing  | wait + fire   | enqueue
        FIRING_UNTIL_HALT( true ), // do nothing  | do nothing    | enqueue
        REST_HALTING( false ),     // wait + fire | wait + fire   | enqueue
        FORCE_HALTING( false ),    // wait + fire | wait + fire   | wait + exec
        EXECUTING_TASK( false );   // wait + fire | wait + fire   | wait + exec

        private final boolean firing;

        ExecutionState( boolean firing ) {
            this.firing = firing;
        }

        public boolean isFiring() {
            return firing;
        }
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public DefaultAgenda() {
    }

    public DefaultAgenda(InternalKnowledgeBase kBase) {
        this( kBase,
              true );
    }

    public DefaultAgenda(InternalKnowledgeBase kBase,
                         boolean initMain) {

        this.agendaGroups = new HashMap<String, InternalAgendaGroup>();
        this.activationGroups = new HashMap<String, InternalActivationGroup>();
        this.focusStack = new LinkedList<AgendaGroup>();
        this.agendaGroupFactory = kBase.getConfiguration().getAgendaGroupFactory();

        if ( initMain ) {
            // MAIN should always be the first AgendaGroup and can never be
            // removed
            this.main = agendaGroupFactory.createAgendaGroup( AgendaGroup.MAIN,
                                                              kBase );

            this.agendaGroups.put( AgendaGroup.MAIN,
                                   this.main );

            this.focusStack.add( this.main );
        }

        Object object = ClassUtils.instantiateObject( kBase.getConfiguration().getConsequenceExceptionHandler(),
                                                      kBase.getConfiguration().getClassLoader() );
        if ( object instanceof ConsequenceExceptionHandler ) {
            this.legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) object;
        } else {
            this.consequenceExceptionHandler = (org.kie.api.runtime.rule.ConsequenceExceptionHandler) object;
        }

        this.declarativeAgenda = kBase.getConfiguration().isDeclarativeAgenda();
        this.sequential = kBase.getConfiguration().isSequential();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        workingMemory = (InternalWorkingMemory) in.readObject();
        agendaGroups = (Map) in.readObject();
        activationGroups = (Map) in.readObject();
        focusStack = (LinkedList) in.readObject();
        main = (InternalAgendaGroup) in.readObject();
        agendaGroupFactory = (AgendaGroupFactory) in.readObject();
        knowledgeHelper = (KnowledgeHelper) in.readObject();
        legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) in.readObject();
        declarativeAgenda = in.readBoolean();
        sequential = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( workingMemory );
        out.writeObject( agendaGroups );
        out.writeObject( activationGroups );
        out.writeObject( focusStack );
        out.writeObject( main );
        out.writeObject( agendaGroupFactory );
        out.writeObject( knowledgeHelper );
        out.writeObject( legacyConsequenceExceptionHandler );
        out.writeBoolean( declarativeAgenda );
        out.writeBoolean( sequential );
    }

    public RuleAgendaItem createRuleAgendaItem(final int salience,
                                               final PathMemory rs,
                                               final TerminalNode rtn ) {
        String agendaGroupName = rtn.getRule().getAgendaGroup();
        String ruleFlowGroupName = rtn.getRule().getRuleFlowGroup();

        RuleAgendaItem lazyAgendaItem;
        if ( !StringUtils.isEmpty(ruleFlowGroupName) ) {
            lazyAgendaItem = new RuleAgendaItem( activationCounter++, null, salience, null, rs, rtn, isDeclarativeAgenda(), (InternalAgendaGroup) getAgendaGroup( ruleFlowGroupName ));
        }  else {
            lazyAgendaItem = new RuleAgendaItem( activationCounter++, null, salience, null, rs, rtn, isDeclarativeAgenda(), (InternalAgendaGroup) getRuleFlowGroup( agendaGroupName ));
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
        rtnLeftTuple.setObject(rtnLeftTuple);
        return rtnLeftTuple;
    }

    public void setWorkingMemory(final InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        RuleBaseConfiguration rbc = this.workingMemory.getKnowledgeBase().getConfiguration();
//        if ( rbc.isSequential() ) {
//            this.knowledgeHelper = rbc.getComponentFactory().getKnowledgeHelperFactory().newSequentialKnowledgeHelper( this.workingMemory );
//        } else {
            this.knowledgeHelper = rbc.getComponentFactory().getKnowledgeHelperFactory().newStatefulKnowledgeHelper( this.workingMemory );
//        }
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
        if ( sequential ) {
            return;
        }

        if ( workingMemory.getSessionConfiguration().getForceEagerActivationFilter().accept(item.getRule()) ) {
            item.getRuleExecutor().evaluateNetwork(workingMemory);
            return;
        }

        if ( item.isInList() ) {
            return;
        }

        if ( log.isTraceEnabled() ) {
            log.trace("Added {} to eager evaluation list.", item.getRule().getName() );
        }
        eager.add( item );
    }

    @Override
    public void removeEagerRuleAgendaItem(RuleAgendaItem item) {
        if ( !item.isInList() ) {
            return;
        }

        if ( log.isTraceEnabled() ) {
            log.trace( "Removed {} from eager evaluation list.", item.getRule().getName() );
        }
        eager.remove( item );
    }

    @Override
    public void addQueryAgendaItem(RuleAgendaItem item) {
        queries.put( (QueryImpl) item.getRule(), item );
        if ( log.isTraceEnabled() ) {
            log.trace( "Added {} to query evaluation list.", item.getRule().getName() );
        }
    }

    @Override
    public void removeQueryAgendaItem(RuleAgendaItem item) {
        queries.remove( item.getRule() );
        if ( log.isTraceEnabled() ) {
            log.trace("Removed {} from query evaluation list.", item.getRule().getName() );
        }
    }

    public void scheduleItem(final ScheduledAgendaItem item,
                             final InternalWorkingMemory wm) {
        throw new UnsupportedOperationException("rete only");
    }

    /**
     * If the item belongs to an activation group, add it
     *
     * @param item
     */
    public void addItemToActivationGroup(final AgendaItem item) {
        if ( item.isRuleAgendaItem() ) {
            throw new UnsupportedOperationException("defensive programming, making sure this isn't called, before removing");
        }
        String group = item.getRule().getActivationGroup();
        if ( group != null && group.length() > 0 ) {
            InternalActivationGroup actgroup = getActivationGroup( group );

            // Don't allow lazy activations to activate, from before it's last trigger point
            if ( actgroup.getTriggeredForRecency() != 0 &&
                 actgroup.getTriggeredForRecency() >= ((InternalFactHandle) item.getPropagationContext().getFactHandle()).getRecency() ) {
                return;
            }

            actgroup.addActivation( item );
        }
    }

    public InternalActivationGroup getStageActivationsGroup() {
        throw new UnsupportedOperationException("rete only");
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
        activation.setFactHandle( factHandle );
    }

    public boolean addActivation(final AgendaItem activation) {
        throw new UnsupportedOperationException("Defensive, rete only");
    }

    public boolean isDeclarativeAgenda() {
        return declarativeAgenda;
    }

    public void removeActivation(final AgendaItem activation) {
        throw new UnsupportedOperationException("Defensive, rete only");
    }

    public void modifyActivation(final AgendaItem activation,
                                 boolean previouslyActive) {
        // in Phreak this is only called for declarative agenda, on rule instances
        InternalFactHandle factHandle = activation.getFactHandle();
        if ( factHandle != null ) {
            // removes the declarative rule instance for the real rule instance
            workingMemory.getEntryPointNode().modifyActivation( factHandle, activation.getPropagationContext(), workingMemory );
        }
    }

    public void clearAndCancelStagedActivations() {
        throw new UnsupportedOperationException("rete only");
    }

    public int unstageActivations() {
        // Not used by phreak, but still called by some generic code.
        return 0;
    }

    @Override
    public void addAgendaItemToGroup(AgendaItem item) {
        throw new UnsupportedOperationException("Defensive");
    }

    public void removeScheduleItem(final ScheduledAgendaItem item) {
        throw new UnsupportedOperationException("rete only");
    }

    public void addAgendaGroup(final AgendaGroup agendaGroup) {
        this.agendaGroups.put( agendaGroup.getName(),
                               (InternalAgendaGroup) agendaGroup );
    }

    public boolean createActivation(final LeftTuple tuple,
                                    final PropagationContext context,
                                    final InternalWorkingMemory workingMemory,
                                    final TerminalNode rtn) {
        throw new UnsupportedOperationException("defensive programming, making sure this isn't called, before removing");
    }

    public boolean createPostponedActivation(final LeftTuple tuple,
                                             final PropagationContext context,
                                             final InternalWorkingMemory workingMemory,
                                             final TerminalNode rtn) {
        throw new UnsupportedOperationException("rete only");
    }

    public boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName, String ruleName, long processInstanceId) {
        return isRuleInstanceAgendaItem(ruleflowGroupName, ruleName, processInstanceId);
    }

    public void cancelActivation(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory,
                                 final Activation activation,
                                 final TerminalNode rtn) {
        AgendaItem item = (AgendaItem) activation;
        item.removeAllBlockersAndBlocked( this );

        if ( isDeclarativeAgenda() && activation.getFactHandle() == null ) {
            // This a control rule activation, nothing to do except update counters. As control rules are not in agenda-groups etc.
            return;
        } else if (isDeclarativeAgenda()) {
            // we are cancelling an actual Activation, so also it's handle from the WM.
            workingMemory.getEntryPointNode().retractActivation( activation.getFactHandle(), activation.getPropagationContext(), workingMemory );

            if ( activation.getActivationGroupNode() != null ) {
                activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
            }
        }

        if ( activation.isQueued() ) {
            // on fact expiration, we don't remove the activation, but let it fire
            if ( context.getType() != PropagationContext.EXPIRATION || context.getFactHandleOrigin() == null ) {
                if ( activation.getActivationGroupNode() != null ) {
                    activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
                }
                leftTuple.decreaseActivationCountForEvents();

                ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCancelled( activation,
                                                                                                workingMemory,
                                                                                                MatchCancelledCause.WME_MODIFY );
            }
        }

        fireConsequenceEvent( item, ON_DELETE_MATCH_CONSEQUENCE_NAME );

        if ( item.getActivationUnMatchListener() != null ) {
            item.getActivationUnMatchListener().unMatch( workingMemory.getKnowledgeRuntime(), item );
        }

        TruthMaintenanceSystemHelper.removeLogicalDependencies( activation,
                                                                context,
                                                                rtn.getRule() );
        workingMemory.executeQueuedActionsForRete();
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
                removeLast();

                if ( agendaGroup.isAutoDeactivate() && !agendaGroup.getNodeInstances().isEmpty() ) {
                    ((EventSupport) this.workingMemory).getAgendaEventSupport().fireBeforeRuleFlowGroupDeactivated( (InternalRuleFlowGroup) agendaGroup,
                            this.workingMemory );

                    innerDeactiveRuleFlowGroup((InternalRuleFlowGroup) agendaGroup);

                    ((EventSupport) this.workingMemory).getAgendaEventSupport().fireAfterRuleFlowGroupDeactivated(( InternalRuleFlowGroup) agendaGroup,
                            this.workingMemory);
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

    public InternalAgendaGroup removeLast() {
        InternalAgendaGroup group = (InternalAgendaGroup) this.focusStack.removeLast();
        group.visited();
        return group;
    }

    public boolean removeGroup(InternalAgendaGroup group) {
        boolean existed = this.focusStack.remove( group );
        group.visited();

        return existed;
    }

    public void clearFocusStack() {
        InternalAgendaGroup[] groups = focusStack.toArray( new InternalAgendaGroup[focusStack.size()] );
        for ( InternalAgendaGroup group : groups ) {
            group.visited();
        }
        this.focusStack.clear();
    }

    public RuleAgendaItem peekNextRule() {
        return (RuleAgendaItem) ((InternalAgendaGroup) this.focusStack.peekLast()).peek();
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
        activateRuleFlowGroup( group, -1, null );
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
        setFocus( group );
        ((EventSupport) this.workingMemory).getAgendaEventSupport().fireAfterRuleFlowGroupActivated( group,
                                                                                                     this.workingMemory );
        this.workingMemory.notifyHalt();
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
        while ( removeGroup(group) ); // keep removing while group is on the stack
        group.setActive( false );
        innerDeactiveRuleFlowGroup( group );
        ((EventSupport) this.workingMemory).getAgendaEventSupport().fireAfterRuleFlowGroupDeactivated( group,
                                                                                                       this.workingMemory );
    }

    private void innerDeactiveRuleFlowGroup(InternalRuleFlowGroup group) {
        group.hasRuleFlowListener( false );
        group.getNodeInstances().clear();
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
        for (InternalAgendaGroup group : this.agendaGroups.values()) {
            for (Match activation : group.getActivations()) {
                list.add((Activation) activation);
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
        throw new UnsupportedOperationException("rete only");
    }

    public <M extends ModedAssertion<M>> org.drools.core.util.LinkedList<ScheduledAgendaItem<M>> getScheduledActivationsLinkedList() {
        throw new UnsupportedOperationException("rete only");
    }

    public void clear() {
        // reset focus stack
        clearFocusStack();
        this.focusStack.add(getMainAgendaGroup());

        //reset all agenda groups
        for ( InternalAgendaGroup group : this.agendaGroups.values() ) {
            // preserve lazy items.
            group.setClearedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
            group.reset();
        }

        // reset all activation groups.
        for ( InternalActivationGroup group : this.activationGroups.values() ) {
            group.setTriggeredForRecency(this.workingMemory.getFactHandleFactory().getRecency());
            group.reset();
        }
    }

    public void reset() {
        // reset focus stack
        clearFocusStack();
        this.focusStack.add( getMainAgendaGroup() );

        //reset all agenda groups
        for ( InternalAgendaGroup group : this.agendaGroups.values() ) {
            group.reset();
        }

        // reset all activation groups.
        for ( InternalActivationGroup group : this.activationGroups.values() ) {
            group.setTriggeredForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
            group.reset();
        }

        eager.clear();
        activationCounter = 0;
    }

    public void clearAndCancel() {
        // Cancel all items and fire a Cancelled event for each Activation
        for ( InternalAgendaGroup internalAgendaGroup : this.agendaGroups.values() ) {
            clearAndCancelAgendaGroup( internalAgendaGroup );
        }

        // cancel all staged activations
        clearAndCancelStagedActivations();

        // cancel all activation groups.
        for ( InternalActivationGroup group : this.activationGroups.values() ) {
            clearAndCancelActivationGroup( group );
        }
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
        // enforce materialization of all activations of this group before removing them
        for (Activation activation : agendaGroup.getActivations()) {
            ((RuleAgendaItem)activation).getRuleExecutor().reEvaluateNetwork( workingMemory );
        }

        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        agendaGroup.setClearedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );

        // this is thread safe for BinaryHeapQueue
        // Binary Heap locks while it returns the array and reset's it's own internal array. Lock is released afer getAndClear()
        List<RuleAgendaItem> lazyItems = new ArrayList<RuleAgendaItem>();
        for ( Activation aQueueable : agendaGroup.getAndClear() ) {
            final AgendaItem item = (AgendaItem) aQueueable;
            if ( item.isRuleAgendaItem() ) {
                lazyItems.add( (RuleAgendaItem) item );
                ((RuleAgendaItem) item).getRuleExecutor().cancel(workingMemory, eventsupport);
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
        // restore lazy items
        for ( RuleAgendaItem lazyItem : lazyItems ) {
            ((InternalAgendaGroup) agendaGroup ).add( lazyItem );
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
            clearAndCancelActivationGroup( activationGroup );
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

                RuleExecutor ruleExec = ((RuleTerminalNodeLeftTuple)activation).getRuleAgendaItem().getRuleExecutor();
                ruleExec.removeLeftTuple((LeftTuple) activation);
                eventsupport.getAgendaEventSupport().fireActivationCancelled( activation,
                                                                              this.workingMemory,
                                                                              MatchCancelledCause.CLEAR );
            }
        }
        activationGroup.reset();
    }

    public void clearAndCancelRuleFlowGroup(final String name) {
        clearAndCancelAgendaGroup( agendaGroups.get( name ) );
    }

    public void clearAndCancelAndCancel(final RuleFlowGroup ruleFlowGroup) {
        clearAndCancelAgendaGroup( (InternalAgendaGroup) ruleFlowGroup );
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
                final InternalAgendaGroup group = getNextFocus();
                // if there is a group with focus
                if ( group != null ) {
                    RuleAgendaItem item;
                    if ( workingMemory.getKnowledgeBase().getConfiguration().isSequential() ) {
                        item = (RuleAgendaItem) group.remove();
                        item.setBlocked(true);
                    }   else {
                        item = (RuleAgendaItem) group.peek();
                    }

                    if (item != null) {

                        evaluateQueriesForRule(item);
                        localFireCount = item.getRuleExecutor().evaluateNetworkAndFire(this.workingMemory, filter,
                                                                                       fireCount, fireLimit);
                        if ( localFireCount == 0 ) {
                            // nothing matched
                            tryagain = true; // will force the next Activation of the agenda, without going to outer loop which checks halt
                            this.workingMemory.flushPropagations(); // There may actions to process, which create new rule matches
                        }
                    }

                    if ( group.peek() == null || !((AgendaItem) group.peek()).getTerminalNode().isFireDirect() ) {
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
            if (item.isRuleInUse()) { // this rule could have been removed by an incremental compilation
                evaluateQueriesForRule( item );
                RuleExecutor ruleExecutor = item.getRuleExecutor();
                ruleExecutor.evaluateNetwork( this.workingMemory );
            }
        }
    }

    private void evaluateQueriesForRule(RuleAgendaItem item) {
        RuleImpl rule = item.getRule();
        if (!rule.isQuery()) {
            for (QueryImpl query : rule.getDependingQueries()) {
                RuleAgendaItem queryAgendaItem = queries.remove(query);
                if (queryAgendaItem != null) {
                    RuleExecutor ruleExecutor = queryAgendaItem.getRuleExecutor();
                    ruleExecutor.evaluateNetwork(this.workingMemory);
                }
            }
        }
    }

    public int sizeOfRuleFlowGroup(String name) {
        InternalAgendaGroup group = agendaGroups.get( name );
        if (group == null) {
            return 0;
        }
        int count = 0;
        for ( Activation item : group.getActivations() ) {
            if (!((RuleAgendaItem) item).getRuleExecutor().getLeftTupleList().isEmpty()) {
                count = count + ((RuleAgendaItem) item).getRuleExecutor().getLeftTupleList().size();
            }
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
     * @throws ConsequenceException
     *             If an error occurs while attempting to fire the consequence.
     */
    public void fireActivation(final Activation activation) throws ConsequenceException {
        // We do this first as if a node modifies a fact that causes a recursion
        // on an empty pattern
        // we need to make sure it re-activates
        this.workingMemory.startOperation();
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
                activation.setActive(false);
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
                    if ( tuple.getLastHandle() != null &&  tuple.getLastHandle().isEvent() ) {
                        // can be null for eval, not and exists that have no right input

                        EventFactHandle handle = (EventFactHandle) tuple.getLastHandle();
                        // decrease the activation count for the event
                        handle.decreaseActivationsCount();
                        // handles "expire" only in stream mode.
                        if ( handle.isExpired() ) {
                            if ( handle.getActivationsCount() <= 0 ) {
                                // and if no more activations, retract the handle
                                handle.getEntryPoint().delete( handle );
                            }
                        }
                    }
                }
            }

            eventsupport.getAgendaEventSupport().fireAfterActivationFired( activation,
                                                                           this.workingMemory );

            unstageActivations();
        } finally {
            this.workingMemory.endOperation();
        }
    }

    public void fireConsequenceEvent(Activation activation, String consequenceName) {
        Consequence consequence = activation.getRule().getNamedConsequence( consequenceName );
        if (consequence != null) {
            fireActivationEvent(activation, consequence);
        }
    }

    private void fireActivationEvent(Activation activation, Consequence event) throws ConsequenceException {
        this.workingMemory.startOperation();
        try {
            try {

                this.knowledgeHelper.setActivation( activation );
                if ( log.isTraceEnabled() ) {
                    log.trace( "Fire event {} for rule \"{}\" \n{}", event.getName(), activation.getRule().getName(), activation.getTuple() );
                }
                event.evaluate(this.knowledgeHelper,
                               this.workingMemory);
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
            }
        } finally {
            this.workingMemory.endOperation();
        }
    }

    public boolean fireTimedActivation(final Activation activation) throws ConsequenceException {
        throw new UnsupportedOperationException("rete only");
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
            if ( act.isRuleAgendaItem() ) {
                // The lazy RuleAgendaItem must be fully evaluated, to see if there is a rule match
                RuleAgendaItem ruleAgendaItem = (RuleAgendaItem) act;
                ruleAgendaItem.getRuleExecutor().evaluateNetwork(workingMemory);
                workingMemory.flushPropagations();
                LeftTupleList list = ruleAgendaItem.getRuleExecutor().getLeftTupleList();
                for (RuleTerminalNodeLeftTuple lt = (RuleTerminalNodeLeftTuple) list.getFirst(); lt != null; lt = (RuleTerminalNodeLeftTuple) lt.getNext()) {
                    if ( ruleName.equals( lt.getRule().getName() ) ) {
                        if ( checkProcessInstance( lt, processInstanceId ) ) {
                            return true;
                        }
                    }
                }

            }   else {
                if ( ruleName.equals( act.getRule().getName() ) ) {
                    if ( checkProcessInstance( act, processInstanceId ) ) {
                        return true;
                    }
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
        // this method name is incorrect for Rete, as it doesn't have staging like Rete did for declarative agenda.
        // so it just gets added directly.  It happens when a blocked LeftTuple becomes unblocked.
        if (!ruleAgendaItem.isQueued()) {
            ruleAgendaItem.getRuleExecutor().getPathMemory().queueRuleAgendaItem(workingMemory);
        }
        ruleAgendaItem.getRuleExecutor().addLeftTuple( justified.getTuple() );
    }

    public void fireUntilHalt() {
        fireUntilHalt( null );
    }

    public void fireUntilHalt(final AgendaFilter agendaFilter) {
        synchronized (this) {
            if (currentState == ExecutionState.FIRING_UNTIL_HALT) {
                return;
            }
            waitAndEnterExecutionState( ExecutionState.FIRING_UNTIL_HALT );
        }

        try {
            if ( log.isTraceEnabled() ) {
                log.trace("Starting fireUntilHalt");
            }

            this.workingMemory.flushPropagations();
            while ( isFiring() ) {
                workingMemory.flushPropagationsOnFireUntilHalt( fireNextItem( agendaFilter, 0, -1 ) > 0 );
            }

            PropagationEntry head = tryHalt();
            while (head != null) {
                workingMemory.flushPropagationsOnFireUntilHalt( fireNextItem( agendaFilter, 0, -1 ) > 0, head );
                head = workingMemory.takeAllPropagations();
            }

            if ( log.isTraceEnabled() ) {
                log.trace( "Ending fireUntilHalt" );
            }
        } finally {
            immediateHalt();
        }
    }

    public int fireAllRules(AgendaFilter agendaFilter,
                            int fireLimit) {
        unstageActivations();

        synchronized (this) {
            if (currentState.isFiring()) {
                return 0;
            }
            waitAndEnterExecutionState( ExecutionState.FIRING_ALL_RULES );
        }

        int fireCount = 0;
        try {
            this.workingMemory.flushPropagations();
            int returnedFireCount;
            do {
                returnedFireCount = fireNextItem( agendaFilter, fireCount, fireLimit );
                fireCount += returnedFireCount;
                this.workingMemory.flushPropagations();
            } while ( ( isFiring() && returnedFireCount != 0 && (fireLimit == -1 || fireCount < fireLimit) ) );

            PropagationEntry head = tryHalt();
            while (head != null) {
                fireCount += fireNextItem( agendaFilter, fireCount, fireLimit );
                SynchronizedPropagationList.flush(workingMemory, head);
                if (currentState == ExecutionState.FORCE_HALTING) {
                    break;
                }
                head = workingMemory.takeAllPropagations();
            }

            if ( this.focusStack.size() == 1 && getMainAgendaGroup().isEmpty() ) {
                // the root MAIN agenda group is empty, reset active to false, so it can receive more activations.
                getMainAgendaGroup().setActive( false );
            }
        } finally {
            immediateHalt();
        }
        return fireCount;
    }

    private void waitAndEnterExecutionState( ExecutionState newState ) {
        if (currentState != ExecutionState.INACTIVE) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException( e );
            }
        }
        currentState = newState;
    }

    @Override
    public boolean isFiring() {
        return currentState.isFiring();
    }

    @Override
    public void executeTask( ExecutableEntry executable ) {
        synchronized (this) {
            if (isFiring() || currentState == ExecutionState.REST_HALTING) {
                executable.enqueue();
                return;
            }
            if (currentState != ExecutionState.EXECUTING_TASK) {
                waitAndEnterExecutionState( ExecutionState.EXECUTING_TASK );
            }
        }

        try {
            executable.execute();
        } finally {
            immediateHalt();
        }
    }

    public void halt() {
        synchronized (this) {
            if (currentState.isFiring()) {
                currentState = ExecutionState.FORCE_HALTING;
            }
        }
    }

    private void immediateHalt() {
        synchronized (this) {
            if (currentState != ExecutionState.INACTIVE) {
                setEngineInactive();
            }
        }
    }

    private PropagationEntry tryHalt() {
        synchronized (this) {
            PropagationEntry head = workingMemory.takeAllPropagations();
            if (head == null) {
                setEngineInactive();
            } else if (currentState != ExecutionState.FORCE_HALTING) {
                currentState = ExecutionState.REST_HALTING;
            }
            return head;
        }
    }

    private void setEngineInactive() {
        currentState = ExecutionState.INACTIVE;
        notify();
        workingMemory.notifyEngineInactive();
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

    public KnowledgeHelper getKnowledgeHelper() {
        return knowledgeHelper;
    }
}
