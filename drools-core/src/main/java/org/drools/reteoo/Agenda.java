package org.drools.reteoo;

/*
 * $Id: Agenda.java,v 1.7 2005/08/16 22:55:37 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.drools.FactException;
import org.drools.rule.Rule;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.ConflictResolver;
import org.drools.spi.ConsequenceException;
import org.drools.spi.Duration;
import org.drools.spi.Module;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

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

    /** Items time-delayed. */
    private final Map               scheduledItems;

    private final Map               modules;

    private final LinkedList        focusStack;

    private ModuleImpl              currentModule;

    private final Set               justifiedActivations;

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
    public Agenda(WorkingMemoryImpl workingMemory,
                  ConflictResolver conflictResolver) {
        this.workingMemory = workingMemory;
        this.scheduledItems = new HashMap();
        this.modules = new HashMap();
        this.focusStack = new LinkedList();

        /* MAIN should always be the first module and can never be removed */
        ModuleImpl main = new ModuleImpl( Module.MAIN,
                                          conflictResolver );
        this.modules.put( Module.MAIN,
                          main );

        this.focusStack.add( main );

        this.justifiedActivations = new HashSet();

    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Schedule a rule action invokation on this <code>Agenda</code>. Rules
     * specified with noNoop=true that are active should not be added to the
     * agenda 3
     * 
     * @param tuple
     *            The matching <code>Tuple</code>.
     * @param rule
     *            The rule to fire.
     */
    void addToAgenda(ReteTuple tuple,
                     PropagationContext context,
                     Rule rule) {
        /*
         * if the current Rule is no-loop and the origin rule is the same then
         * return
         */
        if ( rule.getNoLoop() && rule.equals( context.getRuleOrigin() ) ) {
            return;
        }

        Duration dur = rule.getDuration();

        AgendaItem item = new AgendaItem( tuple,
                                          context,
                                          rule );

        /* set the focus if rule autoFocus is true */
        if ( rule.getAutoFocus() ) {
            setFocus( rule.getModule() );
        }

        if ( dur != null && dur.getDuration( tuple ) > 0 ) {
            this.scheduledItems.put( item.getKey(),
                                     item );
            scheduleItem( item );
            this.workingMemory.getAgendaEventSupport().fireActivationCreated( rule,
                                                                              tuple );
        } else {
            ModuleImpl module = (ModuleImpl) this.modules.get( rule.getModule() );
            module.getActivationQueue().add( item );
            this.workingMemory.getAgendaEventSupport().fireActivationCreated( rule,
                                                                              tuple );
        }
    }

    /**
     * Remove a tuple from the agenda.
     * 
     * @param key
     *            The key to the tuple to be removed.
     * @param rule
     *            The rule to remove.
     */
    void removeFromAgenda(TupleKey key,
                          PropagationContext context,
                          Rule rule) throws FactException {
        ModuleImpl module = (ModuleImpl) this.modules.get( rule.getModule() );

        for ( Iterator it = module.getActivationQueue().iterator(); it.hasNext(); ) {
            AgendaItem eachItem = (AgendaItem) it.next();

            if ( eachItem.getRule() == rule && eachItem.getKey().containsAll( key ) ) {
                it.remove();
                // need to restart iterator as heap could place elements before
                // current iterator position
                it = module.getActivationQueue().iterator();

                this.workingMemory.getAgendaEventSupport().fireActivationCancelled( rule,
                                                                                    eachItem.getTuple() );
                this.workingMemory.removeLogicalAssertions( eachItem,
                                                            context,
                                                            rule );
            }
        }

        for ( Iterator it = this.scheduledItems.values().iterator(); it.hasNext(); ) {
            AgendaItem eachItem = (AgendaItem) it.next();

            if ( eachItem.getRule() == rule && eachItem.getKey().containsAll( key ) ) {
                Tuple tuple = eachItem.getTuple();

                cancelItem( eachItem );

                it.remove();

                this.workingMemory.getAgendaEventSupport().fireActivationCancelled( rule,
                                                                                    tuple );
                this.workingMemory.removeLogicalAssertions( eachItem,
                                                            context,
                                                            rule );
            }
        }

        this.workingMemory.removeLogicalAssertions( key,
                                                    context,
                                                    rule );
    }

    /**
     * Clears all Activations from the Agenda
     * 
     */
    void clearAgenda() {
        AgendaItem eachItem;

        ModuleImpl module;
        Iterator queueIterator;
        Iterator moduleIterator = this.modules.values().iterator();

        // Cancel all items and fire a Cancelled event for each Module
        while ( moduleIterator.hasNext() ) {
            module = (ModuleImpl) moduleIterator.next();
            queueIterator = module.getActivationQueue().iterator();
            while ( queueIterator.hasNext() ) {
                eachItem = (AgendaItem) queueIterator.next();

                queueIterator.remove();

                this.workingMemory.getAgendaEventSupport().fireActivationCancelled( eachItem.getRule(),
                                                                                    eachItem.getTuple() );
            }
        }

        Iterator iter = this.scheduledItems.values().iterator();

        // Cancel all items in the Schedule and fire a Cancelled event for each
        // activation
        while ( iter.hasNext() ) {
            eachItem = (AgendaItem) iter.next();

            cancelItem( eachItem );

            iter.remove();

            this.workingMemory.getAgendaEventSupport().fireActivationCancelled( eachItem.getRule(),
                                                                                eachItem.getTuple() );
        }
    }

    /**
     * Schedule an agenda item for delayed firing.
     * 
     * @param item
     *            The item to schedule.
     */
    void scheduleItem(AgendaItem item) {
        Scheduler.getInstance().scheduleAgendaItem( item,
                                                    this.workingMemory );
    }

    /**
     * Cancel a scheduled agenda item for delayed firing.
     * 
     * @param item
     *            The item to cancel.
     */
    void cancelItem(AgendaItem item) {
        Scheduler.getInstance().cancelAgendaItem( item );
    }

    public void addModule(Module module) {
        this.modules.put( module.getName(),
                          module );
    }

    public void setFocus(Module module) {
        /* remove the object from the stack, before we add it to the end */
        this.focusStack.add( module );
    }

    public void setFocus(String name) {
        setFocus( (Module) this.modules.get( name ) );
    }

    public Module getFocus() {
        return (Module) this.focusStack.getLast();
    }

    /**
     * Iterates the stack untill it finds either a module with items or reaches
     * MAIN
     * 
     * @return
     */
    public Module getNextFocus() {
        ModuleImpl module = null;
        boolean iterate = true;
        while ( iterate ) {
            module = (ModuleImpl) this.focusStack.getLast();
            if ( module.getActivationQueue().isEmpty() && !module.getName().equals( Module.MAIN ) ) {
                this.focusStack.removeLast();
            } else {
                iterate = false;
            }
        }
        return module;
    }

    public void setCurrentModule(Module module) {
        this.currentModule = (ModuleImpl) module;
    }

    public Module getCurrentModule() {
        return this.currentModule;
    }

    public Module getModule(String name) {
        return (Module) this.modules.get( name );
    }

    public int focusSize() {
        return ((ModuleImpl) getFocus()).getActivationQueue().size();
    }
    
    public Module[] getModules() {
        return (Module[]) this.modules.values().toArray( new Module[ this.modules.size() ] );
    }
    
    public Module[] getStack() {
        return (Module[]) this.focusStack.toArray( new Module[ this.focusStack.size() ] );
    }    

    public Map getScheduledItems() {
        return this.scheduledItems;
    }

    public int totalStackSize() {
        ModuleImpl module;
        Iterator iterator = this.focusStack.iterator();

        int size = 0;
        while ( iterator.hasNext() ) {
            module = (ModuleImpl) iterator.next();
            size += module.getActivationQueue().size();
        }
        return size;
    }

    public int totalAgendaSize() {
        ModuleImpl module;
        Iterator iterator = this.modules.values().iterator();

        int size = 0;
        while ( iterator.hasNext() ) {
            module = (ModuleImpl) iterator.next();
            size += module.getActivationQueue().size();
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
        ModuleImpl module = (ModuleImpl) getNextFocus();

        /* return if there are no Activations to fire */
        if ( module.getActivationQueue().isEmpty() ) {
            return false;
        }

        AgendaItem item = (AgendaItem) module.getActivationQueue().remove();

        if ( filter == null || filter.accept( item ) ) {
            item.fire( this.workingMemory );
        }

        return true;
    }

    /**
     * Sets the AsyncExceptionHandler to handle exceptions thrown by the Agenda
     * Scheduler used for duration rules.
     * 
     * @param handler
     */
    void setAsyncExceptionHandler(AsyncExceptionHandler handler) {
        Scheduler.getInstance().setAsyncExceptionHandler( handler );
    }

}
