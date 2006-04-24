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

import java.io.Serializable;
import java.util.Iterator;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.leaps.util.TableIterator;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;

/**
 * this object wears multiple hats - Tuple and being main element that wraps
 * fact handle on main leaps stack
 * 
 * @author Alexander Bagerman
 * 
 */
public class Token
    implements
    Tuple,
    Serializable {

    private static final long            serialVersionUID   = 1L;

    private WorkingMemoryImpl            workingMemory;

    private final FactHandleImpl         dominantFactHandle;

    private RuleHandle                   currentRuleHandle  = null;

    private FactHandleImpl[]             currentFactHandles = new FactHandleImpl[0];

    boolean                              resume             = false;

    private Iterator                     rules              = null;

    private final PropagationContextImpl propagationContext;

    /**
     * 
     */
    public Token(WorkingMemoryImpl workingMemory, FactHandleImpl factHandle,
            PropagationContextImpl propagationContext) {
        this.workingMemory = workingMemory;
        this.dominantFactHandle = factHandle;
        this.propagationContext = propagationContext;
    }

    private Iterator rulesIterator() {
        if (this.rules == null) {
            if (this.dominantFactHandle != null) {
                this.rules = this.workingMemory.getFactTable(
                        this.dominantFactHandle.getObject().getClass()).getRulesIterator();
            }
            else {
                this.rules = this.workingMemory.getNoRequiredColumnsLeapsRules();
            }
        }
        return this.rules;
    }

    public RuleHandle nextRuleHandle() {
        this.currentRuleHandle = (RuleHandle) this.rules.next();
        this.currentFactHandles = new FactHandleImpl[this.currentRuleHandle.getLeapsRule().getNumberOfColumns()];
        return this.currentRuleHandle;
    }

    /**
     * 
     * @param memory
     * @return indicator if there are more rules
     */

    public boolean hasNextRuleHandle() {
        boolean ret = false;
        if (this.rulesIterator() != null) {
            // starting with calling rulesIterator() to make sure that we picks
            // rules because fact can be asserted before rules added
            long levelId = this.workingMemory.getIdLastFireAllAt();
            if (this.dominantFactHandle == null
                    || this.dominantFactHandle.getId() >= levelId) {
                ret = this.rules.hasNext();
            }
            else {
                // then we need to skip rules that have id lower than
                // workingMemory.idLastFireAllAt
                boolean done = false;
                while (!done) {
                    if (this.rules.hasNext()) {
                        if (((RuleHandle) ((TableIterator) this.rules).peekNext()).getId() > levelId) {
                            ret = true;
                            done = true;
                        }
                        else {
                            this.rules.next();
                        }
                    }
                    else {
                        ret = false;
                        done = true;
                    }
                }
            }
        }
        
        return ret;
    }

    public int hashCode() {
        if (this.dominantFactHandle != null) {
            return this.dominantFactHandle.hashCode();
        }
        else {
            return 0;
        }
    }

    public void set(int idx, FactHandleImpl factHandle) {
        this.currentFactHandles[idx] = factHandle;
    }

    public FactHandleImpl getDominantFactHandle() {
        return this.dominantFactHandle;
    }

    public RuleHandle getCurrentRuleHandle() {
        return this.currentRuleHandle;
    }

    public boolean isResume() {
        return this.resume;
    }

    public void setResume(boolean resume) {
        this.resume = resume;
    }

    /**
     * We always have only one Tuple per fact handle hence match on handle id
     * 
     * @see Object
     */
    public boolean equals(Object that) {
        return this.dominantFactHandle.getId() == ((Token) that).dominantFactHandle.getId();
    }

    /**
     * Retrieve the value at position
     * 
     * @param position
     * @return The currently bound <code>Object</code> value.
     * @see org.drools.spi.Tuple
     */
    public InternalFactHandle get(int idx) {
        return this.currentFactHandles[idx];
    }

    /**
     * @see org.drools.spi.Tuple
     */
    public InternalFactHandle get(Declaration declaration) {
        return this.get( declaration.getColumn() );
    }

    /**
     * @see org.drools.spi.Tuple
     */
    public InternalFactHandle[] getFactHandles() {
        return this.currentFactHandles;
    }
    
    public long getRecency() {
        return 0;
    }

    /**
     * Returns a reference to the <code>WorkingMemory</code> associated with
     * this object.
     * 
     * @return WorkingMemory
     */
    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    /**
     * @see java.lang.Object
     */
    public String toString() {
        String ret = "TOKEN [" + this.dominantFactHandle + "]\n" + "\tRULE : " + this.currentRuleHandle + "\n";
        if ( this.currentFactHandles != null ) {
            for ( int i = 0, length = this.currentFactHandles.length; i < length; i++ ) {
                ret = ret + ((i == this.currentRuleHandle.getDominantPosition()) ? "***" : "") + "\t" + i + " -> " + this.currentFactHandles[i].getObject() + "\n";
            }
        }
        return ret;
    }

    /**
     * creates lightweight tuple suitable for agendaItem
     * 
     * @return LeapsTuple
     */
    LeapsTuple getTuple() {
        return new LeapsTuple(this.currentFactHandles, this.currentRuleHandle
                .getLeapsRule(), this.propagationContext);
    }

    /**
     * Determine if this tuple depends upon a specified object.
     * 
     * @param handle
     *            The object handle to test.
     * 
     * @return <code>true</code> if this tuple depends upon the specified
     *         object, otherwise <code>false</code>.
     */
    public boolean dependsOn(FactHandle handle) {
        for ( int i = 0, length = this.currentFactHandles.length; i < length; i++ ) {
            if ( this.currentFactHandles[i].equals( handle ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Do nothing because this tuple never gets to agendaItem stage. Another one -
     * LeapsTuple - is created to take part in agendaItem processing
     * 
     * @see getTuple()
     * @see org.drools.spi.Tuple
     */
    public void setActivation(Activation activation) {
        // do nothing
    }

    public PropagationContextImpl getPropagationContext() {
        return propagationContext;
    }
}