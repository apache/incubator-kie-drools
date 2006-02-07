package org.drools.reteoo;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TimerTask;

import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ConflictResolver;
import org.drools.spi.ConsequenceException;

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
class Agenda
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Working memory of this Agenda. */
    private final WorkingMemoryImpl workingMemory; 
    
    private org.drools.util.LinkedList scheduledActivations;
    
    /** Items time-delayed. */

    private final Map               agendaGroups;

    private final LinkedList        focusStack;

    private AgendaGroupImpl         currentModule;
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
    public Agenda(WorkingMemoryImpl workingMemory ) {        
        this.workingMemory = workingMemory;
        this.agendaGroups = new HashMap();
        this.focusStack = new LinkedList();

        // MAIN should always be the first AgendaGroup and can never be removed
        AgendaGroupImpl main = new AgendaGroupImpl( AgendaGroup.MAIN );
        this.agendaGroups.put( AgendaGroup.MAIN,
                               main );

        this.focusStack.add( main );

    }

    /**
     * Clears all Activations from the Agenda
     * 
     */
    void clearAgenda() {       
        // Cancel all items and fire a Cancelled event for each Activation
        for ( Iterator agendaGroupIterator = this.agendaGroups.values().iterator();agendaGroupIterator.hasNext(); ) {
            AgendaGroupImpl group = (AgendaGroupImpl) agendaGroupIterator.next();
            for ( Iterator queueIterator = group.getPriorityQueue().iterator(); queueIterator.hasNext(); ) {
                AgendaItem item = (AgendaItem) queueIterator.next();                
                item.remove( );
                this.workingMemory.getAgendaEventSupport().fireActivationCancelled( item );
            }
        }

        for (ScheduledAgendaItem item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst();item != null ; item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst()) {
            item.remove();
            this.workingMemory.getAgendaEventSupport().fireActivationCancelled( item );            
        }
   }

    /**
     * Schedule an agenda item for delayed firing.
     * 
     * @param item
     *            The item to schedule.
     */
    void scheduleItem(ScheduledAgendaItem item) {
        Scheduler.getInstance().scheduleAgendaItem( item );
        
        if (this.scheduledActivations == null ) {
            this.scheduledActivations = new org.drools.util.LinkedList();
        }
        
        this.scheduledActivations.add( item );       
    }
    
    void removeScheduleItem(ScheduledAgendaItem item) {    
        this.scheduledActivations.remove( item );
        item.cancel();
    }    

    
    org.drools.util.LinkedList getScheduledItems() {
        return this.scheduledActivations;
    }
    
    public void addAgendaGroup(AgendaGroup agendaGroup) {
        this.agendaGroups.put( agendaGroup.getName(),
                               agendaGroup );
    }

    public void setFocus(AgendaGroup agendaGroup) {
        // remove the object from the stack, before we add it to the end
        this.focusStack.add( agendaGroup );
    }

    public void setFocus(String name) {
        setFocus( (AgendaGroup) this.agendaGroups.get( name ) );
    }

    public AgendaGroup getFocus() {
        return (AgendaGroup) this.focusStack.getLast();
    }

    /**
     * Iterates the stack untill it finds either a module with items or reaches
     * MAIN
     * 
     * @return
     */
    public AgendaGroup getNextFocus() {
        AgendaGroupImpl module = null;
        boolean iterate = true;
        while ( iterate ) {
            module = (AgendaGroupImpl) this.focusStack.getLast();
            if ( module.getPriorityQueue().isEmpty() && !module.getName().equals( AgendaGroup.MAIN ) ) {
                this.focusStack.removeLast();
            } else {
                iterate = false;
            }
        }
        return module;
    }

    public void setCurrentAgendaGroup(AgendaGroup agendaGroup) {
        this.currentModule = (AgendaGroupImpl) agendaGroup;
    }

    public AgendaGroup getCurrentAgendaGroup() {
        return this.currentModule;
    }

    public AgendaGroup getAgendaGroup(String name) {
        return (AgendaGroup) this.agendaGroups.get( name );
    }

    public int focusSize() {
        return ((AgendaGroupImpl) getFocus()).getPriorityQueue().size();
    }

    public AgendaGroup[] getAgendaGroups() {
        return (AgendaGroup[]) this.agendaGroups.values().toArray( new AgendaGroup[this.agendaGroups.size()] );
    }

    public AgendaGroup[] getStack() {
        return (AgendaGroup[]) this.focusStack.toArray( new AgendaGroup[this.focusStack.size()] );
    }

    public int totalStackSize() {
        AgendaGroupImpl module;        

        int size = 0;
        for ( Iterator iterator = this.focusStack.iterator(); iterator.hasNext(); ) {
            module = (AgendaGroupImpl) iterator.next();
            size += module.getPriorityQueue().size();
        }
        return size;
    }

    public int totalAgendaSize() {
        AgendaGroupImpl module;
        Iterator iterator = this.agendaGroups.values().iterator();

        int size = 0;
        while ( iterator.hasNext() ) {
            module = (AgendaGroupImpl) iterator.next();
            size += module.getPriorityQueue().size();
        }
        return size;
    }

    /**
     * Fire the next scheduled <code>Agenda</code> item.
     * 
     * @throws ConsequenceException
     *             If an error occurs while firing an agenda item.
     */
    public boolean fireNextItem(AgendaFilter filter) throws ConsequenceException {
        AgendaGroupImpl group = (AgendaGroupImpl) getNextFocus();
        
        /* return if there are no Activations to fire */
        if ( group.getPriorityQueue().isEmpty() ) {
            return false;
        }


        ActivationQueue queue = (ActivationQueue) group.getPriorityQueue().get();
        while ( !group.getPriorityQueue().isEmpty() && queue.isEmpty() ) {
            queue = (ActivationQueue) group.getPriorityQueue().remove();
        }
        
        boolean fired = false;
        if ( ! queue.isEmpty() ) {        
            Activation item = queue.remove();        
            
            if ( filter == null || filter.accept( item ) ) {
                fireActivation( item );                
            }
    
            fired = true;
        }

        return fired;
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
    public synchronized void fireActivation(Activation activation)  throws ConsequenceException  {
        this.workingMemory.getAgendaEventSupport().fireBeforeActivationFired( activation );
        
        activation.getRule().getConsequence().invoke( activation, this.workingMemory );

        this.workingMemory.getAgendaEventSupport().fireAfterActivationFired( activation );        
    }

}
