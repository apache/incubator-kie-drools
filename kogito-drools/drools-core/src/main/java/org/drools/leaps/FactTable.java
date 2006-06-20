package org.drools.leaps;

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

import java.util.Iterator;
import java.util.LinkedList;

import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.leaps.util.Table;
import org.drools.spi.PropagationContext;

/**
 * Implementation of a container to store data elements used throughout the
 * leaps. Stores fact handles and companion information - relevant rules
 * 
 * @author Alexander Bagerman
 * 
 */
class FactTable extends Table {
    /**
     * 
     */
    private static final long serialVersionUID = 5964698708240814905L;

    /**
     * positive rules are not complete rules but rather its conditions that
     * relates by type
     */
    private final RuleTable   rules;

    /**
     * dynamic rule management support. used to push facts on stack again after
     * fireAllRules by working memory and adding of a new rule after that
     */
    private boolean           reseededStack    = false;

    /**
     * Tuples that are either already on agenda or are very close (missing
     * exists or have not facts matching)
     */
    private LinkedList        tuples;

    /**
     * initializes base LeapsTable with appropriate Comparator and positive and
     * negative rules repositories
     * 
     * @param factConflictResolver
     * @param ruleConflictResolver
     */
    public FactTable(final ConflictResolver conflictResolver) {
        super( conflictResolver.getFactConflictResolver( ) );
        this.rules = new RuleTable( conflictResolver.getRuleConflictResolver( ) );
        this.tuples = new LinkedList( );
    }

    /**
     * Add rule
     * 
     * @param workingMemory
     * @param ruleHandle
     */
    public void addRule( final LeapsWorkingMemory workingMemory,
                         final LeapsRuleHandle ruleHandle ) {
        if (!this.rules.contains( ruleHandle )) {
            this.rules.add( ruleHandle );
            // push facts back to stack if needed
            this.checkAndAddFactsToStack( workingMemory );
        }
    }

    /**
     * Remove rule
     * 
     * @param ruleHandle
     */
    public void removeRule( final LeapsWorkingMemory workingMemory,
                            final LeapsRuleHandle ruleHandle ) {
        this.rules.remove( ruleHandle );
        // remove tuples that are still there
        final LinkedList list = new LinkedList( );

        for (final Iterator it = this.getTuplesIterator( ); it.hasNext( );) {
            final LeapsTuple tuple = (LeapsTuple) it.next( );
            if (ruleHandle.getLeapsRule( ).getRule( ) != tuple.getLeapsRule( ).getRule( )) {
                list.add( tuple );
            }
        }
        this.tuples = list;
    }

    /**
     * checks if rule arrived after working memory fireAll event and if no rules
     * where added since then. Iterates through all facts asserted (and not
     * retracted, they are not here duh) and adds them to the stack.
     * 
     * @param working
     *            memory
     * 
     */
    private void checkAndAddFactsToStack( final LeapsWorkingMemory workingMemory ) {
        if (this.reseededStack) {
            this.setReseededStack( false );

            final PropagationContextImpl context = new PropagationContextImpl( workingMemory.nextPropagationIdCounter( ),
                                                                               PropagationContext.ASSERTION,
                                                                               null,
                                                                               null );

            // let's only add facts below waterline - added before rule is added
            // rest would be added to stack automatically
            final DefaultFactHandle startFactHandle = new DefaultFactHandle( workingMemory.getIdLastFireAllAt( ),
                                                                             new Object( ) );
            for (final Iterator it = this.iteratorFromPositionToTableStart( startFactHandle, startFactHandle ); it.hasNext( );) {
                final LeapsFactHandle handle = (LeapsFactHandle) it.next( );
                workingMemory.pushTokenOnStack( handle, new Token( workingMemory,
                                                                   handle,
                                                                   context ) );
            }
        }
    }

    /**
     * set indicator if rule was added already after fire all completed
     * 
     * @param new
     *            value
     */
    public void setReseededStack( final boolean reseeded ) {
        this.reseededStack = reseeded;
    }

    /**
     * returns an iterator of rule handles to the regular(positive) CEs portions
     * of rules were type matches this fact table underlying type
     * 
     * @return iterator of positive rule handles
     */
    public Iterator getRulesIterator() {
        return this.rules.iterator( );
    }

    /**
     * @see java.lang.Object
     */
    public String toString() {
        final StringBuffer ret = new StringBuffer( );

        for (final Iterator it = this.iterator( ); it.hasNext( );) {
            final LeapsFactHandle handle = (LeapsFactHandle) it.next( );
            ret.append( "\n" + handle + "[" + handle.getObject( ) + "]" );
        }

        ret.append( "\nTuples :" );

        for (final Iterator it = this.tuples.iterator( ); it.hasNext( );) {
            ret.append( "\n" + it.next( ) );
        }

        ret.append( "\nRules :" );

        for (final Iterator it = this.rules.iterator( ); it.hasNext( );) {
            final LeapsRuleHandle handle = (LeapsRuleHandle) it.next( );
            ret.append( "\n\t" + handle.getLeapsRule( ).getRule( ).getName( )
                    + "[dominant - " + handle.getDominantPosition( ) + "]" );
        }

        return ret.toString( );
    }

    protected Iterator getTuplesIterator() {
        return this.tuples.iterator( );
    }

    protected void addTuple( final LeapsTuple tuple ) {
        this.tuples.add( tuple );
    }
}
