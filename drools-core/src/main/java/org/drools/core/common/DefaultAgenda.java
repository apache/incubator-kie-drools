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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.phreak.StackEntry;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Rule;
import org.drools.core.spi.Activation;
import org.drools.core.spi.ActivationGroup;
import org.drools.core.spi.AgendaFilter;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.ConsequenceException;
import org.drools.core.spi.ConsequenceExceptionHandler;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleFlowGroup;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.StringUtils;
import org.drools.core.util.index.LeftTupleList;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.concurrent.ExecutorProviderFactory;
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

    protected static transient Logger                            log                = LoggerFactory.getLogger( DefaultAgenda.class );

    private static final long                                    serialVersionUID   = 510l;

    /** Working memory of this Agenda. */
    protected InternalWorkingMemory                              workingMemory;

    /** Items time-delayed. */

    private Map<String, InternalAgendaGroup>                     agendaGroups;

    private Map<String, ActivationGroup>                         activationGroups;

    private LinkedList<AgendaGroup>                              focusStack;

    private InternalAgendaGroup                                  currentModule;

    private InternalAgendaGroup                                  main;

    private LinkedList<RuleAgendaItem>                           eager;

    private AgendaGroupFactory                                   agendaGroupFactory;

    protected KnowledgeHelper                                    knowledgeHelper;

    private ConsequenceExceptionHandler                          legacyConsequenceExceptionHandler;

    private org.kie.api.runtime.rule.ConsequenceExceptionHandler consequenceExceptionHandler;

    protected AtomicBoolean                                      halt               = new AtomicBoolean( true );
    protected volatile boolean                                 fireUntilHalt      = false;

    protected int                                                activationCounter;

    private boolean                                              declarativeAgenda;

    private ObjectTypeConf                                       activationObjectTypeConf;

    private ActivationsFilter                                    activationsFilter;

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
        this.focusStack = new LinkedList<AgendaGroup>();
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
        eager = new LinkedList<RuleAgendaItem>();

        Object object = ClassUtils.instantiateObject( rb.getConfiguration().getConsequenceExceptionHandler(),
                                                      rb.getConfiguration().getClassLoader() );
        if ( object instanceof ConsequenceExceptionHandler ) {
            this.legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) object;
        } else {
            this.consequenceExceptionHandler = (org.kie.api.runtime.rule.ConsequenceExceptionHandler) object;
        }

        this.declarativeAgenda = rb.getConfiguration().isDeclarativeAgenda();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        workingMemory = (InternalWorkingMemory) in.readObject();
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

    @Override
    public long getNextActivationCounter() {
        return  activationCounter++;
    }

    public AgendaItem createAgendaItem(final LeftTuple tuple,
                                       final int salience,
                                       final PropagationContext context,
                                       final TerminalNode rtn,
                                       RuleAgendaItem ruleAgendaItem,
                                       InternalAgendaGroup agendaGroup) {
        RuleTerminalNodeLeftTuple rtnLeftTuple = (RuleTerminalNodeLeftTuple) tuple;
        rtnLeftTuple.init(activationCounter++,
                          salience,
                          context,
                          ruleAgendaItem, agendaGroup);
        rtnLeftTuple.setObject( rtnLeftTuple );
        return rtnLeftTuple;
    }

    public ScheduledAgendaItem createScheduledAgendaItem(final LeftTuple tuple,
                                                         final PropagationContext context,
                                                         final TerminalNode rtn,
                                                         InternalAgendaGroup agendaGroup) {
        throw new UnsupportedOperationException("rete only");
    }

    public void setWorkingMemory(final InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        RuleBaseConfiguration rbc = ((InternalRuleBase) this.workingMemory.getRuleBase()).getConfiguration();
        if ( rbc.isSequential() ) {
            this.knowledgeHelper = rbc.getComponentFactory().getKnowledgeHelperFactory().newSequentialKnowledgeHelper( this.workingMemory );
        } else {
            this.knowledgeHelper = rbc.getComponentFactory().getKnowledgeHelperFactory().newStatefulKnowledgeHelper( this.workingMemory );
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getWorkingMemory()
     */
    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    @Override
    public void addEagerRuleAgendaItem(final RuleAgendaItem item) {
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
        synchronized (eager) {
            eager.add( item );
        }
        notifyHalt();
    }

    @Override
    public void removeEagerRuleAgendaItem(RuleAgendaItem item) {
        if ( !item.isInList() ) {
            return;
        }

        if ( log.isTraceEnabled() ) {
            log.trace("Removed {} from eager evaluation list.", item.getRule().getName() );
        }
        synchronized (eager) {
            eager.remove(item);
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
            ActivationGroup actgroup = getActivationGroup( group );

            // Don't allow lazy activations to activate, from before it's last trigger point
            if ( actgroup.getTriggeredForRecency() != 0 &&
                 actgroup.getTriggeredForRecency() >= ((InternalFactHandle) item.getPropagationContext().getFactHandle()).getRecency() ) {
                return;
            }

            actgroup.addActivation( item );
        }
    }

    public ActivationGroup getStageActivationsGroup() {
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

    public void addActivation(AgendaItem item,
                              boolean notify) {
        throw new UnsupportedOperationException("Defensive, rete only");
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
            if ( context.getType() == PropagationContext.EXPIRATION && context.getFactHandleOrigin() != null ) {
            } else {
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

        TruthMaintenanceSystemHelper.removeLogicalDependencies( activation,
                                                                context,
                                                                rtn.getRule() );
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
        return (RuleAgendaItem) ((InternalAgendaGroup) this.focusStack.peekLast()).peek();
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
        return getAgendaGroup( name, workingMemory == null ? null : ((InternalRuleBase) workingMemory.getRuleBase()) );
    }

    public AgendaGroup getAgendaGroup(final String name,
                                      InternalRuleBase ruleBase) {
        String groupName = (name == null || name.length() == 0) ? AgendaGroup.MAIN : name;

        InternalAgendaGroup agendaGroup = this.agendaGroups.get( groupName );
        if ( agendaGroup == null ) {
            // The AgendaGroup is defined but not yet added to the
            // Agenda, so create the AgendaGroup and add to the Agenda.
            agendaGroup = agendaGroupFactory.createAgendaGroup( name,
                                                                ruleBase );
            addAgendaGroup( agendaGroup );
        }

        agendaGroup.setWorkingMemory( (InternalWorkingMemory) getWorkingMemory() );

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

    public Map<String, ActivationGroup> getActivationGroupsMap() {
        return this.activationGroups;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#getActivationGroup(java.lang.String)
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
        activateRuleFlowGroup(ruleFlowGroup, processInstanceId, nodeInstanceId);
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
        this.notifyHalt();
    }

    public void deactivateRuleFlowGroup(final String name) {
        deactivateRuleFlowGroup((InternalRuleFlowGroup ) getRuleFlowGroup(name));
    }

    public void deactivateRuleFlowGroup(final InternalRuleFlowGroup group) {
        if ( !group.isRuleFlowListener() ) {
            return;
        }
        ((EventSupport) this.workingMemory).getAgendaEventSupport().fireBeforeRuleFlowGroupDeactivated( group,
                                                                                                        this.workingMemory );
        while ( this.focusStack.remove( group ) ); // keep removing while group is on the stack
        group.setActive(false);
        innerDeactiveRuleFlowGroup(group);
    }

    private void innerDeactiveRuleFlowGroup(InternalRuleFlowGroup group) {
        group.hasRuleFlowListener(false);
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
        for ( final java.util.Iterator<InternalAgendaGroup> it = this.agendaGroups.values().iterator(); it.hasNext(); ) {
            final AgendaGroup group = it.next();
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
        throw new UnsupportedOperationException("rete only");
    }

    public org.drools.core.util.LinkedList<ScheduledAgendaItem> getScheduledActivationsLinkedList() {
        throw new UnsupportedOperationException("rete only");
    }

    public void clear() {
        // reset focus stack
        this.focusStack.clear();
        this.focusStack.add( getMainAgendaGroup() );

        //reset all agenda groups
        for ( InternalAgendaGroup group : this.agendaGroups.values() ) {
            // preserve lazy items.
            group.setClearedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
            for ( Match a : group.getActivations() ) {
                if ( ((Activation) a).isRuleAgendaItem() ) {
                    ((RuleAgendaItem) a).getRuleExecutor().reEvaluateNetwork( this.workingMemory, new org.drools.core.util.LinkedList<StackEntry>(), false );
                }
            }

            group.clear();
        }

        // reset all activation groups.
        for ( ActivationGroup group : this.activationGroups.values() ) {
            group.setTriggeredForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
            group.clear();

        }
    }

    public void clearAndCancel() {
        // Cancel all items and fire a Cancelled event for each Activation
        for ( InternalAgendaGroup internalAgendaGroup : this.agendaGroups.values() ) {
            clearAndCancelAgendaGroup( internalAgendaGroup );
        }

        // cancel all staged activations
        clearAndCancelStagedActivations();

        // cancel all activation groups.
        for ( ActivationGroup group : this.activationGroups.values() ) {
            clearAndCancelActivationGroup( group);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#clearAgendaGroup(java.lang.String)
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
     * @see org.kie.common.AgendaI#clearAgendaGroup(org.kie.common.AgendaGroupImpl)
     */
    public void clearAndCancelAgendaGroup(final AgendaGroup agendaGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        ((InternalAgendaGroup) agendaGroup).setClearedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );

        // this is thread safe for BinaryHeapQueue
        // Binary Heap locks while it returns the array and reset's it's own internal array. Lock is released afer getAndClear()
        List<RuleAgendaItem> lazyItems = new ArrayList<RuleAgendaItem>();
        for ( Activation aQueueable : ((InternalAgendaGroup) agendaGroup).getAndClear() ) {
            final AgendaItem item = (AgendaItem) aQueueable;
            if ( item == null ) {
                continue;
            }

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
            ((InternalAgendaGroup) agendaGroup).add( lazyItem );
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#clearActivationGroup(java.lang.String)
     */
    public void clearAndCancelActivationGroup(final String name) {
        final ActivationGroup activationGroup = this.activationGroups.get( name );
        if ( activationGroup != null ) {
            clearAndCancelActivationGroup( activationGroup);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.common.AgendaI#clearActivationGroup(org.kie.spi.ActivationGroup)
     */
    public void clearAndCancelActivationGroup(final ActivationGroup activationGroup) {
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
        activationGroup.clear();
    }

    public void clearAndCancelRuleFlowGroup(final String name) {
        clearAndCancelAgendaGroup( agendaGroups.get(name) );
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
                    if ( ((InternalRuleBase)workingMemory.getRuleBase()).getConfiguration().isSequential() ) {
                        item = (RuleAgendaItem) group.remove();
                        item.setBlocked(true);
                    }   else {
                        item = (RuleAgendaItem) group.peek();
                    }
                    if (item != null) {
                        localFireCount = item.getRuleExecutor().evaluateNetworkAndFire(this.workingMemory, filter,
                                                                                       fireCount, fireLimit);
                        if ( localFireCount == 0 ) {
                            // nothing matched
                            tryagain = true; // will force the next Activation of the agenda, without going to outer loop which checks halt
                            this.workingMemory.executeQueuedActions(); // There may actions to process, which create new rule matches
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
        synchronized (eager) {
            while ( !eager.isEmpty() ) {
                RuleAgendaItem item = eager.removeFirst();
                item.getRuleExecutor().evaluateNetwork(this.workingMemory);
            }
        }
    }

    public int sizeOfRuleFlowGroup(String name) {
        InternalAgendaGroup group = agendaGroups.get(name);
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
    public synchronized void fireActivation(final Activation activation) throws ConsequenceException {
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
                final ActivationGroup activationGroup = activation.getActivationGroupNode().getActivationGroup();
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
            this.workingMemory.endOperation();
        }
    }

    public synchronized boolean fireTimedActivation(final Activation activation,
                                                    boolean saveForLater) throws ConsequenceException {
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
            if ( act.isRuleAgendaItem() ) {
                // The lazy RuleAgendaItem must be fully evaluated, to see if there is a rule match
                RuleAgendaItem ruleAgendaItem = (RuleAgendaItem) act;
                ruleAgendaItem.getRuleExecutor().evaluateNetwork(workingMemory);
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
    public boolean isFireUntilHalt() {
        return fireUntilHalt;
    }

    @Override
    public void stageLeftTuple(RuleAgendaItem ruleAgendaItem, AgendaItem justified) {
        // this method name is incorrect for Rete, as it doesn't have staging like Rete did for declarative agenda.
        // so it just gets added directly.  It happens when a blocked LeftTuple becomes unblocked.
        if (!ruleAgendaItem.isQueued()) {
            ruleAgendaItem.getRuleExecutor().getPathMemory().queueRuleAgendaItem(workingMemory);
        }
        ruleAgendaItem.getRuleExecutor().addLeftTuple(justified.getTuple());
    }

    public void fireUntilHalt() {
        fireUntilHalt( null );
    }

    public void fireUntilHalt(final AgendaFilter agendaFilter) {
        if( this.halt.compareAndSet( true, false ) ) { // if this was false already means someone else is firing rules already
            fireUntilHalt = true;
            try {
                if ( log.isTraceEnabled() ) {
                    log.trace("Starting fireUntilHalt");
                }
                while ( continueFiring( -1 ) ) {
                    boolean fired = fireNextItem( agendaFilter, 0, -1 ) > 0 ||
                                    !((AbstractWorkingMemory) this.workingMemory).getActionQueue().isEmpty();
                    this.workingMemory.executeQueuedActions();
                    if ( !fired ) {
                        synchronized ( this.halt ) {
                            // has to check in here because a different thread might have set the halt flag already
                            if( ! this.halt.get() ) {
                                // need to check again the agenda is still empty as a new activation
                                // could have been created between the time it did not fire the last 
                                // one and the synchronized block started
                                InternalAgendaGroup nextFocus = getNextFocus();
                                if( nextFocus == null || nextFocus.isEmpty() ) {
                                    try {
                                        this.halt.wait();
                                    } catch (InterruptedException e) {
                                        // nothing to do
                                    }
                                }
                            }
                        }
                    }
                }
                if ( log.isTraceEnabled() ) {
                    log.trace("Ending fireUntilHalt");
                }
            } finally {
                fireUntilHalt = false;
                this.halt.set(true);
            }
        }
    }

    public int fireAllRules(AgendaFilter agendaFilter,
                            int fireLimit) {
        unstageActivations();
        int fireCount = 0;
        if( this.halt.compareAndSet( true, false ) ) { // if this was false already means someone else is firing rules already
            try {
                int returnedFireCount = 0;
                do {
                    returnedFireCount = fireNextItem( agendaFilter, fireCount, fireLimit );
                    fireCount += returnedFireCount;
                    this.workingMemory.executeQueuedActions();
                } while ( continueFiring( 0 ) && returnedFireCount != 0 && (fireLimit == -1 || (fireCount < fireLimit)) );
                if ( this.focusStack.size() == 1 && getMainAgendaGroup().isEmpty() ) {
                    // the root MAIN agenda group is empty, reset active to false, so it can receive more activations.
                    getMainAgendaGroup().setActive( false );
                }
            } finally {
                this.halt.set(true);
            }
        }
        return fireCount;
    }

    @Override
    public boolean continueFiring(final int fireLimit) {
        return !halt.get();
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

    public KnowledgeHelper getKnowledgeHelper() {
        return knowledgeHelper;
    }
}
