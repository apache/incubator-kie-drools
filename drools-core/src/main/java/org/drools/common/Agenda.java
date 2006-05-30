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
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ConsequenceException;
import org.drools.spi.KnowledgeHelper;
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
public class Agenda
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long          serialVersionUID = -9112292414291983688L;

    /** Working memory of this Agenda. */
    private final WorkingMemory        workingMemory;

    private org.drools.util.LinkedList scheduledActivations;

    /** Items time-delayed. */

    private final Map                  agendaGroups;

    private final Map                  activationGroups;

    private final LinkedList           focusStack;

    private AgendaGroupImpl            currentModule;

    private AgendaGroup                main;

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
    public Agenda(final WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        this.agendaGroups = new HashMap();
        this.activationGroups = new HashMap();
        this.focusStack = new LinkedList();

        // MAIN should always be the first AgendaGroup and can never be removed
        this.main = new AgendaGroupImpl( AgendaGroup.MAIN );

        this.agendaGroups.put( AgendaGroup.MAIN,
                               this.main );

        this.focusStack.add( this.main );

    }

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

    public org.drools.util.LinkedList getScheduledItems() {
        return this.scheduledActivations;
    }

    public void addAgendaGroup(final AgendaGroup agendaGroup) {
        this.agendaGroups.put( agendaGroup.getName(),
                               agendaGroup );
    }

    public boolean setFocus(final AgendaGroup agendaGroup) {
        // Set the focus to the agendaGroup if it doesn't already have the focus
        if ( this.focusStack.getLast() != agendaGroup ) {
            this.focusStack.add( agendaGroup );
            return true;
        }
        return false;
    }

    public void setFocus(final String name) {
        AgendaGroup agendaGroup = (AgendaGroup) this.agendaGroups.get( name );

        // Agenda may not have been created yet, if not create it.
        if ( agendaGroup == null ) {
            agendaGroup = new AgendaGroupImpl( name );
            this.workingMemory.getAgenda().addAgendaGroup( agendaGroup );
        }
        setFocus( agendaGroup );
    }

    public AgendaGroup getFocus() {
        return (AgendaGroup) this.focusStack.getLast();
    }

    /**
     * Returns the next populated Agenda Group. If all groups are empty then return the MAIN, dfault, Agenda Group
     * 
     * @return
     */
    public AgendaGroup getNextFocus() {
        AgendaGroupImpl agendaGroup = null;
        // Iterate untill we find a populate AgendaModule or we reach the MAIN, default, AgendaGroup
        while ( true ) {
            agendaGroup = (AgendaGroupImpl) this.focusStack.getLast();

            final boolean empty = agendaGroup.isEmpty();

            // No populated queus found so pop the focusStack and repeat            
            if ( empty && (this.focusStack.size() > 1) ) {
                this.focusStack.removeLast();
            } else {
                agendaGroup = (empty) ? null : agendaGroup;
                break;
            }
        }

        return agendaGroup;
    }

    public void setCurrentAgendaGroup(final AgendaGroup agendaGroup) {
        this.currentModule = (AgendaGroupImpl) agendaGroup;
    }

    public AgendaGroup getCurrentAgendaGroup() {
        return this.currentModule;
    }

    public AgendaGroup getAgendaGroup(final String name) {
        return (AgendaGroup) this.agendaGroups.get( name );
    }

    public AgendaGroup[] getAgendaGroups() {
        return (AgendaGroup[]) this.agendaGroups.values().toArray( new AgendaGroup[this.agendaGroups.size()] );
    }

    public AgendaGroup[] getStack() {
        return (AgendaGroup[]) this.focusStack.toArray( new AgendaGroup[this.focusStack.size()] );
    }

    public ActivationGroup getActivationGroup(final String name) {
        ActivationGroupImpl activationGroup = (ActivationGroupImpl) this.activationGroups.get( name );
        if ( activationGroup == null ) {
            activationGroup = new ActivationGroupImpl( name );
            this.activationGroups.put( name,
                                       activationGroup );
        }
        return activationGroup;
    }

    /**
     * Iterates all the <code>AgendGroup<code>s in the focus stack returning the total number of <code>Activation</code>s
     * @return
     *      total number of <code>Activation</code>s on the focus stack
     */
    public int focusStackSize() {
        int size = 0;
        for ( final Iterator iterator = this.focusStack.iterator(); iterator.hasNext(); ) {
            final AgendaGroup group = (AgendaGroupImpl) iterator.next();
            size += group.size();
        }
        return size;
    }

    /**
     * Iterates all the modules in the focus stack returning the total number of <code>Activation</code>s
     * @return
     *      total number of activations on the focus stack
     */
    public int agendaSize() {
        int size = 0;
        for ( final Iterator iterator = this.agendaGroups.values().iterator(); iterator.hasNext(); ) {
            final AgendaGroup group = (AgendaGroupImpl) iterator.next();
            size += group.size();
        }
        return size;
    }

    public Activation[] getActivations() {
        final List list = new ArrayList();
        for ( final Iterator it = this.agendaGroups.values().iterator(); it.hasNext(); ) {
            final AgendaGroup group = (AgendaGroup) it.next();
            list.addAll( Arrays.asList( group.getActivations() ) );
        }
        return (Activation[]) list.toArray( new Activation[list.size()] );
    }

    public Activation[] getScheduledActivations() {
        final List list = new ArrayList( this.scheduledActivations.size() );
        for ( LinkedListNode node = this.scheduledActivations.getFirst(); node != null; node = node.getNext() ) {
            list.add( node );
        }
        return (Activation[]) list.toArray( new Activation[list.size()] );
    }

    /**
     * Clears all Activations from the Agenda
     * 
     */
    public void clearAgenda() {
        // Cancel all items and fire a Cancelled event for each Activation
        for ( final Iterator agendaGroupIterator = this.agendaGroups.values().iterator(); agendaGroupIterator.hasNext(); ) {
            final AgendaGroupImpl group = (AgendaGroupImpl) agendaGroupIterator.next();
            clearAgendaGroup( group );
        }

        final EventSupport eventsupport = (EventSupport) this.workingMemory;
        if ( this.scheduledActivations != null && !this.scheduledActivations.isEmpty() ) {
            for ( ScheduledAgendaItem item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst(); item != null; item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst() ) {
                item.remove();
                eventsupport.getAgendaEventSupport().fireActivationCancelled( item );
            }
        }
    }

    /**
     * Clears all Activations from an Agenda Group. Any Activations that are also in an Xor Group are removed the
     * the Xor Group.
     * 
     * @param agendaGroup
     */
    public void clearAgendaGroup(final String name) {
        final AgendaGroupImpl agendaGroup = (AgendaGroupImpl) this.agendaGroups.get( name );
        if ( agendaGroup != null ) {
            clearAgendaGroup( agendaGroup );
        }
    }

    /**
     * Clears all Activations from an Agenda Group. Any Activations that are also in an Xor Group are removed the
     * the Xor Group.
     * 
     * @param agendaGroup
     */
    public void clearAgendaGroup(final AgendaGroupImpl agendaGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        final Queueable[] queueable = agendaGroup.getQueueable();
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

            eventsupport.getAgendaEventSupport().fireActivationCancelled( item );
        }
        agendaGroup.clear();
    }

    /**
     * Clears all Activations from an Activation-Group. Any Activations that are also in an Agenda Group are removed
     * from the Agenda Group.
     * 
     * @param activationGroup
     */
    public void clearActivationGroup(final String name) {
        final ActivationGroup activationGroup = (ActivationGroup) this.activationGroups.get( name );
        if ( activationGroup != null ) {
            clearActivationGroup( activationGroup );
        }
    }

    /**
     * Clears all Activations from an Xor Group. Any Activations that are also in an Agenda Group are removed
     * from the Agenda Group.
     * 
     * @param activationGroup
     */
    public void clearActivationGroup(final ActivationGroup activationGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;
        for ( final Iterator it = activationGroup.iterator(); it.hasNext(); ) {
            final Activation activation = ((ActivationGroupNode) it.next()).getActivation();
            activation.setActivationGroupNode( null );

            if ( activation.isActivated() ) {
                activation.setActivated( false );
                activation.remove();
                eventsupport.getAgendaEventSupport().fireActivationCancelled( activation );
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
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        eventsupport.getAgendaEventSupport().fireBeforeActivationFired( activation );

        if ( activation.getActivationGroupNode() != null ) {
            final ActivationGroup activationGroup = activation.getActivationGroupNode().getActivationGroup();
            activationGroup.removeActivation( activation );
            clearActivationGroup( activationGroup );
        }
        activation.setActivated( false );

        try {
            final KnowledgeHelper knowledgeHelper = new org.drools.base.DefaultKnowledgeHelper( activation,
                                                                                                this.workingMemory );
            activation.getRule().getConsequence().evaluate( knowledgeHelper,
                                                            this.workingMemory );
        } catch ( final Exception e ) {
            e.printStackTrace();
            throw new ConsequenceException( e,
                                            activation.getRule() );
        }

        eventsupport.getAgendaEventSupport().fireAfterActivationFired( activation );
    }

}
