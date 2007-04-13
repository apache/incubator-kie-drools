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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ConsequenceException;
import org.drools.spi.RuleFlowGroup;
import org.drools.util.LinkedListNode;
import org.drools.util.Queueable;

/**
 * Rule-firing Agenda.
 * 
 * <p>
 * Since many rules may be matched by a single assertObject(...) all scheduled
 * actions are placed into the <code>Agenda</code>.
 * </p>
 * 
 * <p>
 * While processing a scheduled action, it may modify or retract objects in
 * other scheduled actions, which must then be removed from the agenda.
 * Non-invalidated actions are left on the agenda, and are executed in turn.
 * </p>
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class DefaultAgenda
    implements
    Serializable,
    InternalAgenda {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long          serialVersionUID = 320L;

    /** Working memory of this Agenda. */
    private final InternalWorkingMemory workingMemory;

    private org.drools.util.LinkedList scheduledActivations;

    /** Items time-delayed. */

    private final Map                  agendaGroups;

    private final Map                  activationGroups;

    private final Map                  ruleFlowGroups;

    private final LinkedList           focusStack;

    private AgendaGroupImpl            currentModule;

    private AgendaGroup                main;

    private DefaultKnowledgeHelper     knowledgeHelper;

    public int                         activeActivations;
    public int                         dormantActivations;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param workingMemory
     *            The <code>WorkingMemory</code> of this agenda.
     * @param conflictResolver
     *            The conflict resolver.
     */
    public DefaultAgenda(final InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        this.knowledgeHelper = new DefaultKnowledgeHelper( this.workingMemory );
        this.agendaGroups = new HashMap();
        this.activationGroups = new HashMap();
        this.ruleFlowGroups = new HashMap();
        this.focusStack = new LinkedList();

        // MAIN should always be the first AgendaGroup and can never be removed
        this.main = new AgendaGroupImpl( AgendaGroup.MAIN );

        this.agendaGroups.put( AgendaGroup.MAIN,
                               this.main );

        this.focusStack.add( this.main );

    }

    /* (non-Javadoc)
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
    public void scheduleItem(final ScheduledAgendaItem item) {
        Scheduler.getInstance().scheduleAgendaItem( item );

        if ( this.scheduledActivations == null ) {
            this.scheduledActivations = new org.drools.util.LinkedList();
        }

        this.scheduledActivations.add( item );
    }

    public void removeScheduleItem(final ScheduledAgendaItem item) {
        this.scheduledActivations.remove( item );
        item.cancel();
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#getScheduledItems()
     */
    public org.drools.util.LinkedList getScheduledItems() {
        return this.scheduledActivations;
    }

    public void addAgendaGroup(final AgendaGroup agendaGroup) {
        this.agendaGroups.put( agendaGroup.getName(),
                               agendaGroup );
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#setFocus(org.drools.spi.AgendaGroup)
     */
    public boolean setFocus(final AgendaGroup agendaGroup) {
        // Set the focus to the agendaGroup if it doesn't already have the focus
        if ( this.focusStack.getLast() != agendaGroup ) {
            ((AgendaGroupImpl) this.focusStack.getLast()).setActive( false );
            this.focusStack.add( agendaGroup );
            ((AgendaGroupImpl) agendaGroup).setActive( true );
            final EventSupport eventsupport = (EventSupport) this.workingMemory;
            eventsupport.getAgendaEventSupport().fireAgendaGroupPushed( agendaGroup );
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#setFocus(java.lang.String)
     */
    public void setFocus(final String name) {
        AgendaGroup agendaGroup = (AgendaGroup) this.agendaGroups.get( name );

        // Agenda may not have been created yet, if not create it.
        if ( agendaGroup == null ) {
            agendaGroup = new AgendaGroupImpl( name );
            ((DefaultAgenda) this.workingMemory.getAgenda()).addAgendaGroup( agendaGroup );
        }
        setFocus( agendaGroup );
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#getFocus()
     */
    public AgendaGroup getFocus() {
        return (AgendaGroup) this.focusStack.getLast();
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#getNextFocus()
     */
    public AgendaGroup getNextFocus() {
        AgendaGroupImpl agendaGroup = null;
        // Iterate untill we find a populate AgendaModule or we reach the MAIN, default, AgendaGroup
        while ( true ) {
            agendaGroup = (AgendaGroupImpl) this.focusStack.getLast();

            final boolean empty = agendaGroup.isEmpty();

            // No populated queus found so pop the focusStack and repeat            
            if ( empty && (this.focusStack.size() > 1) ) {
                agendaGroup.setActive( false );
                this.focusStack.removeLast();
                final EventSupport eventsupport = (EventSupport) this.workingMemory;
                eventsupport.getAgendaEventSupport().fireAgendaGroupPopped( agendaGroup );
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

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#setCurrentAgendaGroup(org.drools.spi.AgendaGroup)
     */
    public void setCurrentAgendaGroup(final AgendaGroup agendaGroup) {
        this.currentModule = (AgendaGroupImpl) agendaGroup;
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#getCurrentAgendaGroup()
     */
    public AgendaGroup getCurrentAgendaGroup() {
        return this.currentModule;
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#getAgendaGroup(java.lang.String)
     */
    public AgendaGroup getAgendaGroup(final String name) {
        return (AgendaGroup) this.agendaGroups.get( name );
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#getAgendaGroups()
     */
    public AgendaGroup[] getAgendaGroups() {
        return (AgendaGroup[]) this.agendaGroups.values().toArray( new AgendaGroup[this.agendaGroups.size()] );
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#getStack()
     */
    public AgendaGroup[] getStack() {
        return (AgendaGroup[]) this.focusStack.toArray( new AgendaGroup[this.focusStack.size()] );
    }

    /* (non-Javadoc)
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
        RuleFlowGroup ruleFlowGroup = (RuleFlowGroup) this.ruleFlowGroups.get( name );
        if ( ruleFlowGroup == null ) {
            ruleFlowGroup = new RuleFlowGroupImpl( name );
            this.ruleFlowGroups.put( name,
                                     ruleFlowGroup );
        }
        return ruleFlowGroup;
    }

    public void activateRuleFlowGroup(final String name) {
        ((InternalRuleFlowGroup) getRuleFlowGroup( name )).setActive( true );
    }

    public void deactivateRuleFlowGroup(final String name) {
        ((InternalRuleFlowGroup) getRuleFlowGroup( name )).setActive( false );
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#focusStackSize()
     */
    public int focusStackSize() {
        int size = 0;
        for ( final java.util.Iterator iterator = this.focusStack.iterator(); iterator.hasNext(); ) {
            final AgendaGroup group = (AgendaGroupImpl) iterator.next();
            size += group.size();
        }
        return size;
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#agendaSize()
     */
    public int agendaSize() {
        int size = 0;
        for ( final java.util.Iterator iterator = this.agendaGroups.values().iterator(); iterator.hasNext(); ) {
            final AgendaGroup group = (AgendaGroupImpl) iterator.next();
            size += group.size();
        }
        return size;
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#getActivations()
     */
    public Activation[] getActivations() {
        final List list = new ArrayList();
        for ( final java.util.Iterator it = this.agendaGroups.values().iterator(); it.hasNext(); ) {
            final AgendaGroup group = (AgendaGroup) it.next();
            list.addAll( Arrays.asList( group.getActivations() ) );
        }
        return (Activation[]) list.toArray( new Activation[list.size()] );
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#getScheduledActivations()
     */
    public Activation[] getScheduledActivations() {
        final List list = new ArrayList( this.scheduledActivations.size() );
        for ( LinkedListNode node = this.scheduledActivations.getFirst(); node != null; node = node.getNext() ) {
            list.add( node );
        }
        return (Activation[]) list.toArray( new Activation[list.size()] );
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#clearAgenda()
     */
    public void clearAgenda() {
        // Cancel all items and fire a Cancelled event for each Activation
        for ( final java.util.Iterator agendaGroupIterator = this.agendaGroups.values().iterator(); agendaGroupIterator.hasNext(); ) {
            final AgendaGroupImpl group = (AgendaGroupImpl) agendaGroupIterator.next();
            clearAgendaGroup( group );
        }

        final EventSupport eventsupport = (EventSupport) this.workingMemory;
        if ( this.scheduledActivations != null && !this.scheduledActivations.isEmpty() ) {
            for ( ScheduledAgendaItem item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst(); item != null; item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst() ) {
                item.remove();
                eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                              this.workingMemory );
            }
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#clearAgendaGroup(java.lang.String)
     */
    public void clearAgendaGroup(final String name) {
        final AgendaGroupImpl agendaGroup = (AgendaGroupImpl) this.agendaGroups.get( name );
        if ( agendaGroup != null ) {
            clearAgendaGroup( agendaGroup );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#clearAgendaGroup(org.drools.common.AgendaGroupImpl)
     */
    public void clearAgendaGroup(final AgendaGroup agendaGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        final Queueable[] queueable = ((AgendaGroupImpl) agendaGroup).getQueueable();
        for ( int i = 0, length = queueable.length; i < length; i++ ) {
            final AgendaItem item = (AgendaItem) queueable[i];
            if ( item == null ) {
                continue;
            }

            // this must be set false before removal from the XorGroup. Otherwise the XorGroup will also try to cancel the Actvation
            item.setActivated( false );

            if ( item.getActivationGroupNode() != null ) {
                item.getActivationGroupNode().getActivationGroup().removeActivation( item );
            }

            if ( item.getRuleFlowGroupNode() != null ) {
                final InternalRuleFlowGroup ruleFlowGroup = item.getRuleFlowGroupNode().getRuleFlowGroup();
                ruleFlowGroup.removeActivation( item, this.workingMemory );
            }

            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                          this.workingMemory );
        }
        ((AgendaGroupImpl) agendaGroup).clear();
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#clearActivationGroup(java.lang.String)
     */
    public void clearActivationGroup(final String name) {
        final ActivationGroup activationGroup = (ActivationGroup) this.activationGroups.get( name );
        if ( activationGroup != null ) {
            clearActivationGroup( activationGroup );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.AgendaI#clearActivationGroup(org.drools.spi.ActivationGroup)
     */
    public void clearActivationGroup(final ActivationGroup activationGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        for ( final Iterator it = activationGroup.iterator(); it.hasNext(); ) {
            final ActivationGroupNode node = (ActivationGroupNode) it.next();
            final Activation activation = node.getActivation();
            activation.setActivationGroupNode( null );

            if ( activation.isActivated() ) {
                activation.setActivated( false );
                activation.remove();

                if ( activation.getRuleFlowGroupNode() != null ) {
                    final InternalRuleFlowGroup ruleFlowGroup = activation.getRuleFlowGroupNode().getRuleFlowGroup();
                    ruleFlowGroup.removeActivation( activation, this.workingMemory );
                }

                eventsupport.getAgendaEventSupport().fireActivationCancelled( activation,
                                                                              this.workingMemory );
            }
        }
        activationGroup.clear();
    }

    /**
     * Fire the next scheduled <code>Agenda</code> item.
     * 
     * @throws ConsequenceException
     *             If an error occurs while firing an agenda item.
     */
    public boolean fireNextItem(final AgendaFilter filter) throws ConsequenceException {
        final AgendaGroupImpl group = (AgendaGroupImpl) getNextFocus();

        // return if there are no Activations to fire
        if ( group == null ) {
            return false;
        }

        final AgendaItem item = (AgendaItem) group.getNext();
        if ( item == null ) {
            return false;
        }

        if ( filter == null || filter.accept( item ) ) {
            fireActivation( item );
        }

        return true;
    }

    /**
     * Fire this item.
     * 
     * @param workingMemory
     *            The working memory context.
     * 
     * @throws ConsequenceException
     *             If an error occurs while attempting to fire the consequence.
     */
    public synchronized void fireActivation(final Activation activation) throws ConsequenceException {
        // We do this first as if a node modifies a fact that causes a recursion on an empty pattern
        // we need to make sure it re-activates
        increaseDormantActivations();

        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        eventsupport.getAgendaEventSupport().fireBeforeActivationFired( activation,
                                                                        this.workingMemory );

        if ( activation.getActivationGroupNode() != null ) {
            // We know that this rule will cancel all other activatiosn in the group
            // so lets remove the information now, before the consequence fires
            final ActivationGroup activationGroup = activation.getActivationGroupNode().getActivationGroup();
            activationGroup.removeActivation( activation );
            clearActivationGroup( activationGroup );
        }
        activation.setActivated( false );

        try {
            this.knowledgeHelper.setActivation( activation );
            activation.getRule().getConsequence().evaluate( this.knowledgeHelper,
                                                            this.workingMemory );
        } catch ( final Exception e ) {
            e.printStackTrace();
            throw new ConsequenceException( e,
                                            activation.getRule() );
        }

        if ( activation.getRuleFlowGroupNode() != null ) {
            final InternalRuleFlowGroup ruleFlowGroup = activation.getRuleFlowGroupNode().getRuleFlowGroup();
            ruleFlowGroup.removeActivation( activation, this.workingMemory );
        }

        eventsupport.getAgendaEventSupport().fireAfterActivationFired( activation );
    }

    public void increaseActiveActivations() {
        this.activeActivations++;
    }

    public void decreaseActiveActivations() {
        this.activeActivations--;
    }

    public void increaseDormantActivations() {
        this.activeActivations--;
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

}
