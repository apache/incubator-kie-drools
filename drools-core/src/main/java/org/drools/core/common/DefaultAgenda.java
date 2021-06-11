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

package org.drools.core.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.concurrent.RuleEvaluator;
import org.drools.core.concurrent.SequentialRuleEvaluator;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.ExecutableEntry;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.PropagationList;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.phreak.SynchronizedBypassPropagationList;
import org.drools.core.phreak.SynchronizedPropagationList;
import org.drools.core.phreak.ThreadUnsafePropagationList;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.QueryImpl;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.ConsequenceException;
import org.drools.core.spi.ConsequenceExceptionHandler;
import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleFlowGroup;
import org.drools.core.spi.Tuple;
import org.drools.core.util.StringUtils;
import org.drools.core.util.index.TupleList;
import org.drools.reflective.ComponentsFactory;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Map<String, InternalActivationGroup>                 activationGroups;

    private final org.drools.core.util.LinkedList<RuleAgendaItem> eager = new org.drools.core.util.LinkedList<>();

    private final Map<QueryImpl, RuleAgendaItem>                 queries = new ConcurrentHashMap<>();

    private ConsequenceExceptionHandler                          legacyConsequenceExceptionHandler;

    private org.kie.api.runtime.rule.ConsequenceExceptionHandler consequenceExceptionHandler;

    protected int                                                activationCounter;

    private boolean                                              declarativeAgenda;
    private boolean                                              sequential;

    private ObjectTypeConf                                       activationObjectTypeConf;

    private ActivationsFilter                                    activationsFilter;

    private volatile List<PropagationContext>                    expirationContexts;

    private RuleEvaluator ruleEvaluator;

    private PropagationList propagationList;

    private ExecutionStateMachine executionStateMachine;

    private AgendaGroupsManager agendaGroupsManager;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public DefaultAgenda() { }

    public DefaultAgenda(InternalKnowledgeBase kBase) {
        this( kBase, true );
    }

    public DefaultAgenda(InternalKnowledgeBase kBase, boolean initMain) {
        this(kBase, initMain, new ConcurrentExecutionStateMachine());
    }

    DefaultAgenda(InternalKnowledgeBase kBase,
                  boolean initMain,
                  ExecutionStateMachine executionStateMachine) {
        this.agendaGroupsManager = AgendaGroupsManager.create(kBase, initMain);
        this.activationGroups = new HashMap<>();
        this.executionStateMachine = executionStateMachine;

        Object object = ComponentsFactory.createConsequenceExceptionHandler( kBase.getConfiguration().getConsequenceExceptionHandler(),
                                                                             kBase.getConfiguration().getClassLoader() );
        if ( object instanceof ConsequenceExceptionHandler ) {
            this.legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) object;
        } else {
            this.consequenceExceptionHandler = (org.kie.api.runtime.rule.ConsequenceExceptionHandler) object;
        }

        this.declarativeAgenda = kBase.getConfiguration().isDeclarativeAgenda();
        this.sequential = kBase.getConfiguration().isSequential();
        if (kBase.getConfiguration().getEventProcessingMode() == EventProcessingOption.STREAM) {
            expirationContexts = new ArrayList<>();
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        setWorkingMemory( (InternalWorkingMemory) in.readObject() );
        agendaGroupsManager = (AgendaGroupsManager) in.readObject();
        activationGroups = (Map) in.readObject();
        legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) in.readObject();
        declarativeAgenda = in.readBoolean();
        sequential = in.readBoolean();
        this.executionStateMachine = new ConcurrentExecutionStateMachine();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( workingMemory );
        out.writeObject( agendaGroupsManager );
        out.writeObject( activationGroups );
        out.writeObject( legacyConsequenceExceptionHandler );
        out.writeBoolean( declarativeAgenda );
        out.writeBoolean( sequential );
    }

    @Override
    public RuleAgendaItem createRuleAgendaItem(final int salience,
                                               final PathMemory rs,
                                               final TerminalNode rtn ) {
        String ruleFlowGroupName = rtn.getRule().getRuleFlowGroup();
        return new RuleAgendaItem( activationCounter++, null, salience, null, rs, rtn, isDeclarativeAgenda(),
                (InternalAgendaGroup) getAgendaGroup( !StringUtils.isEmpty(ruleFlowGroupName) ? ruleFlowGroupName : rtn.getRule().getAgendaGroup() ));
    }

    @Override
    public AgendaItem createAgendaItem(RuleTerminalNodeLeftTuple rtnLeftTuple,
                                       final int salience,
                                       final PropagationContext context,
                                       RuleAgendaItem ruleAgendaItem,
                                       InternalAgendaGroup agendaGroup) {
        rtnLeftTuple.init(activationCounter++,
                          salience,
                          context,
                          ruleAgendaItem, agendaGroup);
        return rtnLeftTuple;
    }

    @Override
    public void setWorkingMemory(final InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        this.agendaGroupsManager.setWorkingMemory( workingMemory );

        if ( !workingMemory.getSessionConfiguration().isThreadSafe() ) {
            executionStateMachine = new UnsafeExecutionStateMachine();
        }

        this.ruleEvaluator = new SequentialRuleEvaluator( this );
        this.propagationList = createPropagationList();
    }

    private PropagationList createPropagationList() {
        if (!workingMemory.getSessionConfiguration().isThreadSafe()) {
            return new ThreadUnsafePropagationList( workingMemory );
        }

        return workingMemory.getSessionConfiguration().hasForceEagerActivationFilter() ?
               new SynchronizedBypassPropagationList( workingMemory ) :
               new SynchronizedPropagationList( workingMemory );
    }

    @Override
    public PropagationList getPropagationList() {
        return propagationList;
    }

    @Override
    public InternalWorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    @Override
    public void addEagerRuleAgendaItem(RuleAgendaItem item) {
        if ( sequential ) {
            return;
        }

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
        queries.remove( (QueryImpl) item.getRule() );
        if ( log.isTraceEnabled() ) {
            log.trace("Removed {} from query evaluation list.", item.getRule().getName() );
        }
    }

    /**
     * If the item belongs to an activation group, add it
     *
     * @param item
     */
    @Override
    public void addItemToActivationGroup(final AgendaItem item) {
        if ( item.isRuleAgendaItem() ) {
            throw new UnsupportedOperationException("defensive programming, making sure this isn't called, before removing");
        }
        String group = item.getRule().getActivationGroup();
        if ( group != null && group.length() > 0 ) {
            InternalActivationGroup actgroup = getActivationGroup( group );

            // Don't allow lazy activations to activate, from before it's last trigger point
            if ( actgroup.getTriggeredForRecency() != 0 &&
                 actgroup.getTriggeredForRecency() >= item.getPropagationContext().getFactHandle().getRecency() ) {
                return;
            }

            actgroup.addActivation( item );
        }
    }

    @Override
    public void insertAndStageActivation(final AgendaItem activation) {
        if ( activationObjectTypeConf == null ) {
            EntryPointId ep = workingMemory.getEntryPoint();
            activationObjectTypeConf = workingMemory.getWorkingMemoryEntryPoint( ep.getEntryPointId() ).getObjectTypeConfigurationRegistry().getObjectTypeConf(activation );
        }

        InternalFactHandle factHandle = workingMemory.getFactHandleFactory().newFactHandle( activation, activationObjectTypeConf, workingMemory, workingMemory );
        workingMemory.getEntryPointNode().assertActivation( factHandle, activation.getPropagationContext(), workingMemory );
        activation.setActivationFactHandle( factHandle );
    }

    @Override
    public boolean isDeclarativeAgenda() {
        return declarativeAgenda;
    }

    @Override
    public void modifyActivation(final AgendaItem activation,
                                 boolean previouslyActive) {
        // in Phreak this is only called for declarative agenda, on rule instances
        InternalFactHandle factHandle = activation.getActivationFactHandle();
        if ( factHandle != null ) {
            // removes the declarative rule instance for the real rule instance
            workingMemory.getEntryPointNode().modifyActivation( factHandle, activation.getPropagationContext(), workingMemory );
        }
    }

    @Override
    public boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName, String ruleName, long processInstanceId) {
        return isRuleInstanceAgendaItem(ruleflowGroupName, ruleName, processInstanceId);
    }

    @Override
    public void cancelActivation(final Activation activation) {
        AgendaItem item = (AgendaItem) activation;
        item.removeAllBlockersAndBlocked( this );

        workingMemory.cancelActivation( activation, isDeclarativeAgenda() );

        if ( isDeclarativeAgenda() ) {
            if (activation.getActivationFactHandle() == null) {
                // This a control rule activation, nothing to do except update counters. As control rules are not in agenda-groups etc.
                return;
            }
            // we are cancelling an actual Activation, so also it's handle from the WM.
            if ( activation.getActivationGroupNode() != null ) {
                activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
            }
        }

        if ( activation.isQueued() ) {
            if ( activation.getActivationGroupNode() != null ) {
                activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
            }
            (( Tuple ) activation).decreaseActivationCountForEvents();

            workingMemory.getAgendaEventSupport().fireActivationCancelled( activation,
                                                                           workingMemory,
                                                                           MatchCancelledCause.WME_MODIFY );
        }

        if (item.getRuleAgendaItem() != null) {
            item.getRuleAgendaItem().getRuleExecutor().fireConsequenceEvent( this.workingMemory, this, item, ON_DELETE_MATCH_CONSEQUENCE_NAME );
        }

        workingMemory.getRuleEventSupport().onDeleteMatch( item );

        TruthMaintenanceSystemHelper.removeLogicalDependencies( activation, ( Tuple ) activation, activation.getRule() );
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#setFocus(org.kie.spi.AgendaGroup)
     */
    @Override
    public boolean setFocus(final AgendaGroup agendaGroup) {
        return this.agendaGroupsManager.setFocus((InternalAgendaGroup) agendaGroup);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#setFocus(java.lang.String)
     */
    @Override
    public void setFocus(final String name) {
        setFocus( null, name );
    }

    public void setFocus(final PropagationContext ctx,
                         final String name) {
        AgendaGroup agendaGroup = getAgendaGroup( name );
        agendaGroup.setAutoFocusActivator( ctx );
        setFocus( agendaGroup );
    }

    @Override
    public InternalAgendaGroup getNextFocus() {
        return agendaGroupsManager.getNextFocus();
    }

    @Override
    public RuleAgendaItem peekNextRule() {
        return agendaGroupsManager.peekNextRule();
    }

    @Override
    public AgendaGroup getAgendaGroup(final String name) {
        return agendaGroupsManager.getAgendaGroup( name );
    }

    @Override
    public void removeAgendaGroup(final String name) {
        agendaGroupsManager.removeGroup( agendaGroupsManager.getAgendaGroup( name ) );
    }

    @Override
    public AgendaGroup getAgendaGroup(String name, InternalKnowledgeBase kBase) {
        return agendaGroupsManager.getAgendaGroup(name, kBase);
    }

    @Override
    public AgendaGroup[] getAgendaGroups() {
        return agendaGroupsManager.getAgendaGroups();
    }

    @Override
    public Map<String, InternalAgendaGroup> getAgendaGroupsMap() {
        return agendaGroupsManager.getAgendaGroupsMap();
    }

    @Override
    public void putOnAgendaGroupsMap(String name, InternalAgendaGroup group) {
        agendaGroupsManager.putOnAgendaGroupsMap(name, group);
    }

    @Override
    public Collection<String> getGroupsName() {
        return agendaGroupsManager.getGroupsName();
    }

    @Override
    public void addAgendaGroupOnStack(AgendaGroup agendaGroup) {
        agendaGroupsManager.addAgendaGroupOnStack((InternalAgendaGroup) agendaGroup);
    }

    @Override
    public Map<String, InternalActivationGroup> getActivationGroupsMap() {
        return this.activationGroups;
    }

    @Override
    public InternalActivationGroup getActivationGroup(final String name) {
        return this.activationGroups.computeIfAbsent(name, k -> new ActivationGroupImpl( this, k ));
    }

    @Override
    public RuleFlowGroup getRuleFlowGroup(final String name) {
        return ( RuleFlowGroup ) getAgendaGroup(name);
    }

    @Override
    public void activateRuleFlowGroup(final String name) {
        InternalRuleFlowGroup group =  (InternalRuleFlowGroup) getRuleFlowGroup( name );
        activateRuleFlowGroup( group, -1, null );
    }

    @Override
    public void activateRuleFlowGroup(final String name,
                                      long processInstanceId,
                                      String nodeInstanceId) {
        InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) getRuleFlowGroup( name );
        activateRuleFlowGroup( ruleFlowGroup, processInstanceId, nodeInstanceId );
    }

    public void activateRuleFlowGroup(final InternalRuleFlowGroup group, Object processInstanceId, String nodeInstanceId) {
        this.workingMemory.getAgendaEventSupport().fireBeforeRuleFlowGroupActivated( group, this.workingMemory );
        group.setActive( true );
        group.hasRuleFlowListener(true);
        if ( !StringUtils.isEmpty( nodeInstanceId ) ) {
            group.addNodeInstance( processInstanceId, nodeInstanceId );
            group.setActive( true );
        }
        group.setFocus();
        this.workingMemory.getAgendaEventSupport().fireAfterRuleFlowGroupActivated( group, this.workingMemory );
        propagationList.notifyWaitOnRest();
    }

    @Override
    public void deactivateRuleFlowGroup(final String name) {
        agendaGroupsManager.deactivateRuleFlowGroup(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#focusStackSize()
     */
    @Override
    public int focusStackSize() {
        return agendaGroupsManager.focusStackSize();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#agendaSize()
     */
    @Override
    public int agendaSize() {
        return agendaGroupsManager.agendaSize();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getActivations()
     */
    @Override
    public Activation[] getActivations() {
        return agendaGroupsManager.getActivations();
    }

    @Override
    public void clear() {
        agendaGroupsManager.reset(true);

        // reset all activation groups.
        for ( InternalActivationGroup group : this.activationGroups.values() ) {
            group.setTriggeredForRecency(this.workingMemory.getFactHandleFactory().getRecency());
            group.reset();
        }
        propagationList.reset();
    }

    @Override
    public void reset() {
        agendaGroupsManager.reset(false);

        // reset all activation groups.
        for ( InternalActivationGroup group : this.activationGroups.values() ) {
            group.setTriggeredForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
            group.reset();
        }

        eager.clear();
        activationCounter = 0;
        executionStateMachine.reset();
        propagationList.reset();
    }

    @Override
    public void clearAndCancel() {
        // Cancel all items and fire a Cancelled event for each Activation
        agendaGroupsManager.clearAndCancel(this);

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
    @Override
    public void clearAndCancelAgendaGroup(final String name) {
        agendaGroupsManager.clearAndCancelAgendaGroup(name, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#clearActivationGroup(java.lang.String)
     */
    @Override
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
    @Override
    public void clearAndCancelActivationGroup(final InternalActivationGroup activationGroup) {
        final EventSupport eventsupport = this.workingMemory;

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

    @Override
    public void clearAndCancelRuleFlowGroup(final String name) {
        agendaGroupsManager.clearAndCancelAgendaGroup(name, this);
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
    @Override
    public int fireNextItem(final AgendaFilter filter,
                            int fireCount,
                            int fireLimit) {
        // Because rules can be on the agenda, but after network evaluation produce no full matches, the
        // engine uses tryAgain to drive a loop to find a rule that has matches, until there are no more rules left to try.
        // once rule with 1..n matches is found, it'll return back to the outer loop.
        boolean tryagain;
        int localFireCount = 0;
        do {
            tryagain = false;
            evaluateEagerList();
            final InternalAgendaGroup group = getNextFocus();
            // if there is a group with focus
            if ( group != null ) {
                localFireCount = ruleEvaluator.evaluateAndFire(filter, fireCount, fireLimit, group);

                // it produced no full matches, so drive the search to the next rule
                if ( localFireCount == 0 ) {
                    // nothing matched
                    tryagain = true;
                    propagationList.flush(); // There may actions to process, which create new rule matches
                }
            }
        } while ( tryagain );

        return localFireCount;
    }

    @Override
    public void evaluateEagerList() {
        while ( !eager.isEmpty() ) {
            RuleAgendaItem item = eager.removeFirst();
            if (item.isRuleInUse()) { // this rule could have been removed by an incremental compilation
                evaluateQueriesForRule( item );
                RuleExecutor ruleExecutor = item.getRuleExecutor();
                ruleExecutor.evaluateNetwork( this );
            }
        }
    }

    public void evaluateQueriesForRule(RuleAgendaItem item) {
        RuleImpl rule = item.getRule();
        if (!rule.isQuery()) {
            for (QueryImpl query : rule.getDependingQueries()) {
                RuleAgendaItem queryAgendaItem = queries.remove(query);
                if (queryAgendaItem != null) {
                    queryAgendaItem.getRuleExecutor().evaluateNetwork(this);
                }
            }
        }
    }

    @Override
    public int sizeOfRuleFlowGroup(String name) {
        return agendaGroupsManager.sizeOfRuleFlowGroup(name);
    }

    @Override
    public boolean isRuleInstanceAgendaItem(String ruleflowGroupName,
                                            String ruleName,
                                            long processInstanceId) {
        return isRuleInstanceAgendaItem(ruleflowGroupName, ruleName, (Object) processInstanceId);
    }

    protected boolean isRuleInstanceAgendaItem(String ruleflowGroupName,
                                            String ruleName,
                                            Object processInstanceId) {
        propagationList.flush();
        RuleFlowGroup systemRuleFlowGroup = this.getRuleFlowGroup( ruleflowGroupName );

        Match[] matches = ((InternalAgendaGroup)systemRuleFlowGroup).getActivations();
        for ( Match match : matches ) {
            Activation act = ( Activation ) match;
            if ( act.isRuleAgendaItem() ) {
                // The lazy RuleAgendaItem must be fully evaluated, to see if there is a rule match
                RuleExecutor ruleExecutor = ((RuleAgendaItem) act).getRuleExecutor();
                ruleExecutor.evaluateNetwork(this);
                TupleList list = ruleExecutor.getLeftTupleList();
                for (RuleTerminalNodeLeftTuple lt = (RuleTerminalNodeLeftTuple) list.getFirst(); lt != null; lt = (RuleTerminalNodeLeftTuple) lt.getNext()) {
                    if ( ruleName.equals( lt.getRule().getName() )
                            && ( checkProcessInstance( lt, processInstanceId ) )) {
                        return true;
                    }
                }

            }   else {
                if ( ruleName.equals( act.getRule().getName() )
                        && ( checkProcessInstance( act, processInstanceId ) )) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkProcessInstance(Activation activation,
                                         Object processInstanceId) {
        final Map<String, Declaration> declarations = activation.getSubRule().getOuterDeclarations();
        for ( Declaration declaration : declarations.values() ) {
            if ( "processInstance".equals( declaration.getIdentifier() )
                    || "org.kie.api.runtime.process.WorkflowProcessInstance".equals(declaration.getTypeName())) {
                Object value = declaration.getValue( workingMemory, activation.getTuple() );
                if ( value instanceof ProcessInstance ) {
                    return sameProcessInstance( processInstanceId, ( ProcessInstance ) value );
                }
            }
        }
        return true;
    }

    protected boolean sameProcessInstance( Object processInstanceId, ProcessInstance value ) {
        return processInstanceId.equals( value.getId());
    }

    @Override
    public String getFocusName() {
        return this.agendaGroupsManager.getFocusName();
    }

    @Override
    public void stageLeftTuple(RuleAgendaItem ruleAgendaItem, AgendaItem justified) {
        if (!ruleAgendaItem.isQueued()) {
            ruleAgendaItem.getRuleExecutor().getPathMemory().queueRuleAgendaItem(this);
        }
        ruleAgendaItem.getRuleExecutor().addLeftTuple( justified.getTuple() );
    }

    @Override
    public void fireUntilHalt() {
        fireUntilHalt( null );
    }

    @Override
    public void fireUntilHalt(final AgendaFilter agendaFilter) {
        if ( log.isTraceEnabled() ) {
            log.trace("Starting Fire Until Halt");
        }
        if (executionStateMachine.toFireUntilHalt()) {
            internalFireUntilHalt( agendaFilter, true );
        }
        if ( log.isTraceEnabled() ) {
            log.trace("Ending Fire Until Halt");
        }
    }

    void internalFireUntilHalt( AgendaFilter agendaFilter, boolean isInternalFire ) {
        propagationList.setFiringUntilHalt( true );
        try {
            fireLoop( agendaFilter, -1, RestHandler.FIRE_UNTIL_HALT, isInternalFire );
        } finally {
            propagationList.setFiringUntilHalt( false );
        }
    }

    @Override
    public int fireAllRules(AgendaFilter agendaFilter, int fireLimit) {
        if (!executionStateMachine.toFireAllRules()) {
            return 0;
        }
        if ( log.isTraceEnabled() ) {
            log.trace("Starting Fire All Rules");
        }
        int fireCount = internalFireAllRules( agendaFilter, fireLimit, true );
        if ( log.isTraceEnabled() ) {
            log.trace("Ending Fire All Rules");
        }
        return fireCount;
    }

    int internalFireAllRules( AgendaFilter agendaFilter, int fireLimit, boolean isInternalFire ) {
        return fireLoop( agendaFilter, fireLimit, RestHandler.FIRE_ALL_RULES, isInternalFire );
    }

    private int fireLoop(AgendaFilter agendaFilter, int fireLimit, RestHandler restHandler, boolean isInternalFire) {
        int fireCount = 0;
        try {
            PropagationEntry head = propagationList.takeAll();
            int returnedFireCount;

            boolean limitReached = fireLimit == 0; // -1 or > 0 will return false. No reason for user to give 0, just handled for completeness.

            // The engine comes to potential rest (inside the loop) when there are no propagations and no rule firings.
            // It's potentially at rest, because we cannot guarantee it is at rest.
            // This is because external async actions (timer rules) can populate the queue that must be executed immediately.
            // A final takeAll within the sync point determines if it can safely come to rest.
            // if takeAll returns null, the engine is now safely at rest. If it returns something
            // the engine is not at rest and the loop continues.
            //
            // When FireUntilHalt comes to a safe rest, the thread is put into a wait state,
            // when the queue is populated the thread is notified and the loop begins again.
            //
            // When FireAllRules comes to a safe rest it will put the engine into an INACTIVE state
            // and the loop can exit.
            //
            // When a halt() command is added to the propagation queue and that queue is flushed
            // the engine is put into a INACTIVE state. At this point isFiring returns false and
            // no more rules can fire. However the loop will continue until rest point has been safely
            // entered, i.e. the queue returns null within that sync point.
            //
            // The loop is susceptable to never return on extremely greedy behaviour.
            //
            // Note that if a halt() command is given, the engine is changed to INACTIVE,
            // and isFiring returns false allowing it to exit before all rules are fired.
            //
            while ( isFiring()  )  {
                if ( head != null ) {
                    // it is possible that there are no action propagations, but there are rules to fire.
                    propagationList.flush(head);
                    head = null;
                }

                // a halt may have occurred during the flushPropagations,
                // which changes the isFiring state. So a second isFiring guard is needed
                if (!isFiring()) {
                    break;
                }

                evaluateEagerList();
                InternalAgendaGroup group = getNextFocus();
                if ( group != null && !limitReached ) {
                    // only fire rules while the limit has not reached.
                    // if halt is called, then isFiring will be false.
                    // The while loop may continue to loop, to keep flushing the action propagation queue
                    returnedFireCount = ruleEvaluator.evaluateAndFire( agendaFilter, fireCount, fireLimit, group );
                    fireCount += returnedFireCount;

                    limitReached = ( fireLimit > 0 && fireCount >= fireLimit );
                    head = propagationList.takeAll();
                } else {
                    returnedFireCount = 0; // no rules fired this iteration, so we know this is 0
                    group = null; // set the group to null in case the fire limit has been reached
                }

                if ( returnedFireCount == 0 && head == null && ( group == null || ( group.isEmpty() && !group.isAutoDeactivate() ) ) && !flushExpirations() ) {
                    // if true, the engine is now considered potentially at rest
                    head = restHandler.handleRest( this, isInternalFire );
                    if (!isInternalFire && head == null) {
                        break;
                    }
                }
            }

            agendaGroupsManager.deactivateMainGroupWhenEmpty();
        } finally {
            // makes sure the engine is inactive, if an exception is thrown.
            // if it safely returns, then the engine should already be inactive
            if (isInternalFire) {
                executionStateMachine.immediateHalt(propagationList);
            }
        }
        return fireCount;
    }

    interface RestHandler {
        RestHandler FIRE_ALL_RULES = new FireAllRulesRestHandler();
        RestHandler FIRE_UNTIL_HALT = new FireUntilHaltRestHandler();

        PropagationEntry handleRest(DefaultAgenda agenda, boolean isInternalFire);

        class FireAllRulesRestHandler implements RestHandler {
            @Override
            public PropagationEntry handleRest(DefaultAgenda agenda, boolean isInternalFire) {
                synchronized (agenda.executionStateMachine.getStateMachineLock()) {
                    PropagationEntry head = agenda.propagationList.takeAll();
                    if (isInternalFire && head == null) {
                        agenda.internalHalt();
                    }
                    return head;
                }
            }
        }

        class FireUntilHaltRestHandler implements RestHandler {
            @Override
            public PropagationEntry handleRest(DefaultAgenda agenda, boolean isInternalFire) {
                boolean deactivated = false;
                if (isInternalFire && agenda.executionStateMachine.getCurrentState() == ExecutionStateMachine.ExecutionState.FIRING_UNTIL_HALT) {
                    agenda.executionStateMachine.inactiveOnFireUntilHalt();
                    deactivated = true;
                }

                PropagationEntry head;
                // this must use the same sync target as takeAllPropagations, to ensure this entire block is atomic, up to the point of wait
                synchronized (agenda.propagationList) {
                    head = agenda.propagationList.takeAll();

                    // if halt() has called, the thread should not be put into a wait state
                    // instead this is just a safe way to make sure the queue is flushed before exiting the loop
                    if (head == null && (
                            agenda.executionStateMachine.getCurrentState() == ExecutionStateMachine.ExecutionState.FIRING_UNTIL_HALT ||
                            agenda.executionStateMachine.getCurrentState() == ExecutionStateMachine.ExecutionState.INACTIVE_ON_FIRING_UNTIL_HALT )) {
                        agenda.propagationList.waitOnRest();
                        head = agenda.propagationList.takeAll();
                    }
                }

                if (deactivated) {
                    agenda.executionStateMachine.toFireUntilHalt();
                }

                return head;
            }
        }
    }

    @Override
    public boolean isFiring() {
        return executionStateMachine.isFiring();
    }

    @Override
    public void executeTask( ExecutableEntry executable ) {
        if ( !executionStateMachine.toExecuteTask( executable ) ) {
            return;
        }

        try {
            executable.execute();
        } finally {
            executionStateMachine.immediateHalt(propagationList);
        }
    }

    @Override
    public void executeFlush() {
        if (!executionStateMachine.toExecuteTaskState()) {
            return;
        }

        try {
            flushPropagations();
        } finally {
            executionStateMachine.immediateHalt(propagationList);
        }
    }

    @Override
    public void activate() {
        executionStateMachine.activate(this, propagationList);
    }

    @Override
    public void deactivate() {
        executionStateMachine.deactivate();
    }

    @Override
    public boolean tryDeactivate() {
        return executionStateMachine.tryDeactivate();
    }

    static class Halt extends PropagationEntry.AbstractPropagationEntry {

        private final ExecutionStateMachine executionStateMachine;

        protected Halt( ExecutionStateMachine executionStateMachine ) {
            this.executionStateMachine = executionStateMachine;
        }

        @Override
        public void execute( InternalWorkingMemory wm ) {
            executionStateMachine.internalHalt();
        }

        @Override
        public String toString() {
            return "Halt";
        }
    }

    @Override
    public synchronized void halt() {
        // only attempt halt an engine that is currently firing
        // This will place a halt command on the propagation queue
        // that will allow the engine to halt safely
        if ( isFiring() ) {
            propagationList.addEntry(new Halt(executionStateMachine));
        }
    }

    @Override
    public boolean dispose(InternalWorkingMemory wm) {
        propagationList.dispose();
        return executionStateMachine.dispose( wm );
    }

    @Override
    public boolean isAlive() {
        return executionStateMachine.isAlive();
    }

    public void internalHalt() {
        executionStateMachine.internalHalt();
    }

    @Override
    public void setActivationsFilter(ActivationsFilter filter) {
        this.activationsFilter = filter;
    }

    @Override
    public ActivationsFilter getActivationsFilter() {
        return this.activationsFilter;
    }

    @Override
    public void handleException(InternalWorkingMemory wm, Activation activation, Exception e) {
        if ( this.legacyConsequenceExceptionHandler != null ) {
            this.legacyConsequenceExceptionHandler.handleException( activation, wm, e );
        } else if ( this.consequenceExceptionHandler != null ) {
            this.consequenceExceptionHandler.handleException( activation, wm.getKnowledgeRuntime(), e );
        } else {
            throw new RuntimeException( e );
        }
    }

    @Override
    public KnowledgeHelper getKnowledgeHelper() {
        return ruleEvaluator.getKnowledgeHelper();
    }

    @Override
    public void addPropagation(PropagationEntry propagationEntry) {
        propagationList.addEntry( propagationEntry );
    }

    @Override
    public void flushPropagations() {
        propagationList.flush();
    }

    @Override
    public void notifyWaitOnRest() {
        propagationList.notifyWaitOnRest();
    }

    @Override
    public Iterator<PropagationEntry> getActionsIterator() {
        return propagationList.iterator();
    }

    @Override
    public boolean hasPendingPropagations() {
        return !propagationList.isEmpty();
    }

    interface ExecutionStateMachine {
        enum ExecutionState {         // fireAllRule | fireUntilHalt | executeTask <-- required action
            INACTIVE( false, true ),         // fire        | fire          | exec
            FIRING_ALL_RULES( true, true ),  // do nothing  | wait + fire   | enqueue
            FIRING_UNTIL_HALT( true, true ), // do nothing  | do nothing    | enqueue
            INACTIVE_ON_FIRING_UNTIL_HALT( true, true ),
            HALTING( false, true ),          // wait + fire | wait + fire   | enqueue
            EXECUTING_TASK( false, true ),   // wait + fire | wait + fire   | wait + exec
            DEACTIVATED( false, true ),      // wait + fire | wait + fire   | wait + exec
            DISPOSING( false, false ),       // no further action is allowed
            DISPOSED( false, false );        // no further action is allowed

            private final boolean firing;
            private final boolean alive;

            ExecutionState( boolean firing, boolean alive ) {
                this.firing = firing;
                this.alive = alive;
            }

            public boolean isFiring() {
                return firing;
            }

            public boolean isAlive() {
                return alive;
            }
        }

        boolean isFiring();

        void reset();

        boolean toFireAllRules();

        boolean toFireUntilHalt();

        boolean toExecuteTask( ExecutableEntry executable );

        boolean toExecuteTaskState();

        void activate(DefaultAgenda agenda, PropagationList propagationList);

        void deactivate();

        boolean tryDeactivate();

        void immediateHalt(PropagationList propagationList);

        void inactiveOnFireUntilHalt();

        void internalHalt();

        boolean dispose(InternalWorkingMemory workingMemory);

        boolean isAlive();

        ExecutionState getCurrentState();

        Object getStateMachineLock();
    }

    static class UnsafeExecutionStateMachine implements ExecutionStateMachine {

        private final Object stateMachineLock = new Object();

        private ExecutionState currentState = ExecutionState.INACTIVE;

        @Override
        public boolean isFiring() {
            return currentState.isFiring();
        }

        @Override
        public void reset() {
            currentState = ExecutionState.INACTIVE;
        }

        @Override
        public boolean toFireAllRules() {
            currentState = ExecutionState.FIRING_ALL_RULES;
            return true;
        }

        @Override
        public boolean toFireUntilHalt() {
            throw new UnsupportedOperationException( "Not permitted in non-thread-safe mode" );
        }

        @Override
        public boolean toExecuteTask( ExecutableEntry executable ) {
            throw new UnsupportedOperationException( "Not permitted in non-thread-safe mode" );
        }

        @Override
        public boolean toExecuteTaskState() {
            currentState = ExecutionState.EXECUTING_TASK;
            return true;
        }

        @Override
        public void activate( DefaultAgenda agenda, PropagationList propagationList ) {
        }

        @Override
        public void deactivate() {
            currentState = ExecutionState.DEACTIVATED;
        }

        @Override
        public boolean tryDeactivate() {
            currentState = ExecutionState.DEACTIVATED;
            return true;
        }

        @Override
        public void immediateHalt( PropagationList propagationList ) {
            currentState = ExecutionState.INACTIVE;
        }

        @Override
        public void inactiveOnFireUntilHalt() {
            throw new UnsupportedOperationException( "Not permitted in non-thread-safe mode" );
        }

        @Override
        public void internalHalt() {
            if (isFiring()) {
                currentState = ExecutionState.HALTING;
            }
        }

        @Override
        public boolean dispose( InternalWorkingMemory workingMemory ) {
            currentState = ExecutionState.DISPOSED;
            return true;
        }

        @Override
        public boolean isAlive() {
            return currentState.isAlive();
        }

        @Override
        public ExecutionState getCurrentState() {
            return currentState;
        }

        @Override
        public Object getStateMachineLock() {
            return stateMachineLock;
        }
    }

    static class ConcurrentExecutionStateMachine implements ExecutionStateMachine {
        private volatile ExecutionState currentState = ExecutionState.INACTIVE;
        private volatile boolean wasFiringUntilHalt = false;

        private final Object stateMachineLock = new Object();

        private long fireUntilHaltThreadId = -1;

        public boolean isFiring() {
            return currentState.isFiring();
        }

        public void reset() {
            currentState = ExecutionState.INACTIVE;
            wasFiringUntilHalt = false;
        }

        public boolean toFireAllRules() {
            synchronized (stateMachineLock) {
                if (currentState.isFiring() || !currentState.isAlive()) {
                    return false;
                }
                waitAndEnterExecutionState( ExecutionState.FIRING_ALL_RULES );
            }
            return true;
        }

        public boolean toFireUntilHalt() {
            synchronized (stateMachineLock) {
                if ( currentState == ExecutionState.FIRING_UNTIL_HALT || currentState == ExecutionState.HALTING ) {
                    return false;
                }
                waitAndEnterExecutionState( ExecutionState.FIRING_UNTIL_HALT );
            }
            return true;
        }

        public boolean toExecuteTask( ExecutableEntry executable ) {
            synchronized (stateMachineLock) {
                // state is never changed outside of a sync block, so this is safe.
                if (isFiring()) {
                    executable.enqueue();
                    return false;
                }

                if (currentState != ExecutionState.EXECUTING_TASK) {
                    waitAndEnterExecutionState( ExecutionState.EXECUTING_TASK );
                }
                return true;
            }
        }

        public boolean toExecuteTaskState() {
            synchronized (stateMachineLock) {
                // state is never changed outside of a sync block, so this is safe.
                if (!currentState.isAlive() || currentState.isFiring()) {
                    return false;
                }
                waitAndEnterExecutionState( ExecutionState.EXECUTING_TASK );
                return true;
            }
        }

        private void waitAndEnterExecutionState( ExecutionState newState ) {
            waitInactive();
            setCurrentState( newState );
        }

        private void waitInactive() {
            while ( currentState != ExecutionState.INACTIVE && currentState != ExecutionState.INACTIVE_ON_FIRING_UNTIL_HALT && currentState != ExecutionState.DISPOSED ) {
                try {
                    stateMachineLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException( e );
                }
            }
        }

        private void setCurrentState(ExecutionState state) {
            if ( log.isDebugEnabled() ) {
                log.debug("State was {} is now {}", currentState, state);
            }
            if (currentState != ExecutionState.DISPOSED) {
                currentState = state;
            }
        }

        public void activate(DefaultAgenda agenda, PropagationList propagationList) {
            if ( currentState.isAlive() ) {
                boolean restoreFireUntilHalt = wasFiringUntilHalt;
                wasFiringUntilHalt = false;
                boolean restoreFiringOnSameThread = restoreFireUntilHalt && fireUntilHaltThreadId == Thread.currentThread().getId();
                fireUntilHaltThreadId = -1L;

                immediateHalt( propagationList );

                if ( restoreFireUntilHalt ) {
                    // restoring a fire until halt after an incremental compilation should either happen on the same thread
                    // where the fireUntilHalt was running before the compilation or on a brand new thread
                    if (restoreFiringOnSameThread) {
                        agenda.fireUntilHalt();
                    } else {
                        new Thread(agenda::fireUntilHalt).start();
                    }
                }
            }
        }

        public void deactivate() {
            synchronized (stateMachineLock) {
                pauseFiringUntilHalt();
                if ( currentState != ExecutionState.DEACTIVATED && currentState.isAlive() ) {
                    waitAndEnterExecutionState( ExecutionState.DEACTIVATED );
                }
            }
        }

        public boolean tryDeactivate() {
            synchronized (stateMachineLock) {
                if ( !currentState.isAlive() ) {
                    return true;
                }
                pauseFiringUntilHalt();
                if ( currentState == ExecutionState.INACTIVE || currentState == ExecutionState.INACTIVE_ON_FIRING_UNTIL_HALT ) {
                    setCurrentState( ExecutionState.DEACTIVATED );
                    return true;
                }
            }
            return false;
        }

        private void pauseFiringUntilHalt() {
            if ( currentState == ExecutionState.FIRING_UNTIL_HALT) {
                wasFiringUntilHalt = true;
                setCurrentState( ExecutionState.HALTING );
                waitInactive();
            }
        }

        public void immediateHalt(PropagationList propagationList) {
            synchronized (stateMachineLock) {
                if (currentState != ExecutionState.INACTIVE) {
                    setCurrentState( ExecutionState.INACTIVE );
                    stateMachineLock.notifyAll();
                    propagationList.onEngineInactive();
                    if (wasFiringUntilHalt) {
                        // if it is halting a thread that was running a fireUntilHalt registers its id
                        fireUntilHaltThreadId = Thread.currentThread().getId();
                    }
                }
            }
        }

        public void inactiveOnFireUntilHalt() {
            synchronized (stateMachineLock) {
                if (currentState != ExecutionState.INACTIVE && currentState != ExecutionState.INACTIVE_ON_FIRING_UNTIL_HALT) {
                    setCurrentState( ExecutionState.INACTIVE_ON_FIRING_UNTIL_HALT );
                    stateMachineLock.notifyAll();
                }
            }
        }

        public void internalHalt() {
            synchronized (stateMachineLock) {
                if (isFiring()) {
                    setCurrentState( ExecutionState.HALTING );
                }
            }
        }

        public boolean dispose(InternalWorkingMemory workingMemory) {
            synchronized (stateMachineLock) {
                if (!currentState.isAlive()) {
                    return false;
                }
                if (currentState.isFiring() && currentState != ExecutionState.INACTIVE_ON_FIRING_UNTIL_HALT) {
                    setCurrentState( ExecutionState.DISPOSING );
                    workingMemory.notifyWaitOnRest();
                }
                waitAndEnterExecutionState( ExecutionState.DISPOSED );
                stateMachineLock.notifyAll();
                return true;
            }
        }

        public boolean isAlive() {
            synchronized (stateMachineLock) {
                return currentState.isAlive();
            }
        }

        public ExecutionState getCurrentState() {
            return currentState;
        }

        public Object getStateMachineLock() {
            return stateMachineLock;
        }
    }

    @Override
    public void registerExpiration(PropagationContext ectx) {
        // it is safe to add into the expirationContexts list without any synchronization because
        // the state machine already guarantees that only one thread at time can access it
        expirationContexts.add(ectx);
    }

    private boolean flushExpirations() {
        if (expirationContexts == null || expirationContexts.isEmpty() || propagationList.hasEntriesDeferringExpiration()) {
            return false;
        }
        for (PropagationContext ectx : expirationContexts) {
            doRetract( ectx );
        }
        expirationContexts.clear();
        return true;
    }

    protected void doRetract( PropagationContext ectx ) {
        InternalFactHandle factHandle = ectx.getFactHandle();
        ObjectTypeNode.retractLeftTuples( factHandle, ectx, workingMemory );
        ObjectTypeNode.retractRightTuples( factHandle, ectx, workingMemory );
        if ( factHandle.isPendingRemoveFromStore() ) {
            String epId = factHandle.getEntryPointName();
            ( (InternalWorkingMemoryEntryPoint) workingMemory.getEntryPoint( epId ) ).removeFromObjectStore( factHandle );
        }
    }

    @Override
    public boolean isParallelAgenda() {
        return false;
    }
}
