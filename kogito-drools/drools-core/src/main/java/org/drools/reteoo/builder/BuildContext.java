/*
 * Copyright 2006 JBoss Inc
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

package org.drools.reteoo.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.drools.common.BetaConstraints;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ReteooBuilder;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.LeftTupleSource;
import org.drools.rule.EntryPoint;
import org.drools.rule.RuleConditionElement;

/**
 * A build context for Reteoo Builder
 * 
 * @author etirelli
 */
public class BuildContext {

    // tuple source to attach next node to
    private LeftTupleSource               tupleSource;

    // object source to attach next node to
    private ObjectSource              objectSource;

    // object type cache to check for cross products
    private LinkedList                objectType;

    // offset of the pattern
    private int                       currentPatternOffset;

    // rule base to add rules to
    private InternalRuleBase          rulebase;

    // working memories attached to the given rulebase
    private InternalWorkingMemory[]   workingMemories;

    // id generator
    private ReteooBuilder.IdGenerator idGenerator;

    // a build stack to track nested elements
    private LinkedList                buildstack;

    // beta constraints from the last pattern attached
    private List                      betaconstraints;

    // alpha constraints from the last pattern attached
    private List                      alphaConstraints;
    
    // the current entry point
    private EntryPoint                currentEntryPoint;
    
    private boolean                   tupleMemoryEnabled;
    
    private boolean                   objectTypeNodeMemoryEnabled;    
    
    private boolean                   terminalNodeMemoryEnabled;
    
    /** This one is slightly different as alphaMemory can be adaptive, only turning on for new rule attachments */
    private boolean                   alphaNodeMemoryAllowed;

    public BuildContext(final InternalRuleBase rulebase,
                        final ReteooBuilder.IdGenerator idGenerator) {
        this.rulebase = rulebase;

        this.idGenerator = idGenerator;
        
        this.workingMemories = null;

        this.objectType = null;
        this.buildstack = null;

        this.tupleSource = null;
        this.objectSource = null;

        this.currentPatternOffset = 0;
        
        this.tupleMemoryEnabled = true;
        
        this.objectTypeNodeMemoryEnabled = true;
        
        this.currentEntryPoint = EntryPoint.DEFAULT;
    }

    /**
     * @return the currentPatternOffset
     */
    public int getCurrentPatternOffset() {
        return this.currentPatternOffset;
    }

    /**
     * @param currentPatternOffset the currentPatternOffset to set
     */
    public void setCurrentPatternOffset(final int currentPatternIndex) {
        this.currentPatternOffset = currentPatternIndex;
        this.syncObjectTypesWithPatternOffset();
    }

    public void syncObjectTypesWithPatternOffset() {
        if (this.objectType == null ) {
            this.objectType = new LinkedList();
        }
        while ( this.objectType.size() > this.currentPatternOffset ) {
            this.objectType.removeLast();
        }
    }

    /**
     * @return the objectSource
     */
    public ObjectSource getObjectSource() {
        return this.objectSource;
    }

    /**
     * @param objectSource the objectSource to set
     */
    public void setObjectSource(final ObjectSource objectSource) {
        this.objectSource = objectSource;
    }

    /**
     * @return the objectType
     */
    public LinkedList getObjectType() {
        if (this.objectType == null ) {
            this.objectType = new LinkedList();
        }        
        return this.objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(final LinkedList objectType) {
        if (this.objectType == null ) {
            this.objectType = new LinkedList();
        }        
        this.objectType = objectType;
    }

    /**
     * @return the tupleSource
     */
    public LeftTupleSource getTupleSource() {
        return this.tupleSource;
    }

    /**
     * @param tupleSource the tupleSource to set
     */
    public void setTupleSource(final LeftTupleSource tupleSource) {
        this.tupleSource = tupleSource;
    }

    public void incrementCurrentPatternOffset() {
        this.currentPatternOffset++;
    }

    public void decrementCurrentPatternOffset() {
        this.currentPatternOffset--;
        this.syncObjectTypesWithPatternOffset();
    }

    /**
     * Returns context rulebase
     * @return
     */
    public InternalRuleBase getRuleBase() {
        return this.rulebase;
    }

    /**
     * Return the array of working memories associated with the given
     * rulebase.
     * 
     * @return
     */
    public InternalWorkingMemory[] getWorkingMemories() {
        if ( this.workingMemories == null ) {
            this.workingMemories = (InternalWorkingMemory[]) this.rulebase.getWorkingMemories();
        }
        return this.workingMemories;
    }

    /**
     * Returns an Id for the next node
     * @return
     */
    public int getNextId() {
        return this.idGenerator.getNextId();
    }

    /**
     * Method used to undo previous id assignment
     */
    public void releaseId( int id ) {
        this.idGenerator.releaseId( id );
    }

    /**
     * Adds the rce to the build stack
     * @param rce
     */
    public void push(final RuleConditionElement rce) {
        if ( this.buildstack == null ) {
            this.buildstack = new LinkedList();            
        }        
        this.buildstack.addLast( rce );
    }

    /**
     * Removes the top stack element
     * @return
     */
    public RuleConditionElement pop() {
        if ( this.buildstack == null ) {
            this.buildstack = new LinkedList();            
        }
        return (RuleConditionElement) this.buildstack.removeLast();
    }

    /**
     * Returns the top stack element without removing it
     * @return
     */
    public RuleConditionElement peek() {
        if ( this.buildstack == null ) {
            this.buildstack = new LinkedList();            
        }        
        return (RuleConditionElement) this.buildstack.getLast();
    }

    /**
     * Returns a list iterator to iterate over the stacked elements
     * @return
     */
    public ListIterator stackIterator() {
        if ( this.buildstack == null ) {
            this.buildstack = new LinkedList();            
        }        
        return this.buildstack.listIterator();
    }

    /**
     * @return the betaconstraints
     */
    public List getBetaconstraints() {
        return this.betaconstraints;
    }

    /**
     * @param betaconstraints the betaconstraints to set
     */
    public void setBetaconstraints(final List betaconstraints) {
        this.betaconstraints = betaconstraints;
    }   
    
    public int getNextSequence(String groupName) {
        //List list = new ArrayList();
        
        Integer seq = ( Integer ) this.rulebase.getAgendaGroupRuleTotals().get( groupName );
        if ( seq == null ) {
            seq = new Integer( 0 );            
        }
        Integer newSeq = new Integer( seq.intValue() + 1 );
        this.rulebase.getAgendaGroupRuleTotals().put( groupName, newSeq );
        
        return newSeq.intValue();
    }

    /**
     * @return
     */
    public List getAlphaConstraints() {
        return alphaConstraints;
    }

    public void setAlphaConstraints(List alphaConstraints) {
        this.alphaConstraints = alphaConstraints;
    }

    public boolean isTupleMemoryEnabled() {
        return this.tupleMemoryEnabled;
    }

    public void setTupleMemoryEnabled(boolean hasLeftMemory) {
        this.tupleMemoryEnabled = hasLeftMemory;
    }

    public boolean isObjectTypeNodeMemoryEnabled() {
        return objectTypeNodeMemoryEnabled;
    }

    public void setObjectTypeNodeMemoryEnabled(boolean hasObjectTypeMemory) {
        this.objectTypeNodeMemoryEnabled = hasObjectTypeMemory;
    }

    public boolean isTerminalNodeMemoryEnabled() {
        return terminalNodeMemoryEnabled;
    }

    public void setTerminalNodeMemoryEnabled(boolean hasTerminalNodeMemory) {
        this.terminalNodeMemoryEnabled = hasTerminalNodeMemory;
    }     
    
    public void setAlphaNodeMemoryAllowed(boolean alphaMemoryAllowed) {
        this.alphaNodeMemoryAllowed = alphaMemoryAllowed;
    }
    
    public boolean isAlphaMemoryAllowed() {
        return this.alphaNodeMemoryAllowed;
    }

    /**
     * @return the currentEntryPoint
     */
    public EntryPoint getCurrentEntryPoint() {
        return currentEntryPoint;
    }

    /**
     * @param currentEntryPoint the currentEntryPoint to set
     */
    public void setCurrentEntryPoint(EntryPoint currentEntryPoint) {
        this.currentEntryPoint = currentEntryPoint;
    }
        
}
