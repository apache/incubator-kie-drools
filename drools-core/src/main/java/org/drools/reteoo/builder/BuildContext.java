/*
 * Copyright 2010 JBoss Inc
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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import org.drools.common.BaseNode;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ReteooBuilder;
import org.drools.rule.Behavior;
import org.drools.rule.EntryPoint;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.RuleComponent;
import org.drools.time.TemporalDependencyMatrix;

/**
 * A build context for Reteoo Builder
 * 
 * @author etirelli
 */
public class BuildContext {

    // tuple source to attach next node to
    private LeftTupleSource                  tupleSource;

    // object source to attach next node to
    private ObjectSource                     objectSource;

    // object type cache to check for cross products
    private LinkedList                       objectType;

    // offset of the pattern
    private int                              currentPatternOffset;

    // rule base to add rules to
    private InternalRuleBase                 rulebase;
    
    // rule being added at this moment
    private Rule                             rule;
    
    // the rule component being processed at the moment
    private Stack<RuleComponent>             ruleComponent;

    // working memories attached to the given rulebase
    private InternalWorkingMemory[]          workingMemories;

    // id generator
    private ReteooBuilder.IdGenerator        idGenerator;

    // a build stack to track nested elements
    private LinkedList<RuleConditionElement> buildstack;

    // beta constraints from the last pattern attached
    private List                             betaconstraints;

    // alpha constraints from the last pattern attached
    private List                             alphaConstraints;

    // behaviors from the last pattern attached
    private List<Behavior>                   behaviors;

    // the current entry point
    private EntryPoint                       currentEntryPoint;

    private boolean                          tupleMemoryEnabled;

    private boolean                          objectTypeNodeMemoryEnabled;

    private boolean                          terminalNodeMemoryEnabled;

    /** This one is slightly different as alphaMemory can be adaptive, only turning on for new rule attachments */
    private boolean                          alphaNodeMemoryAllowed;
    
    private boolean                          query;

    /** Stores the list of nodes being added that require partitionIds */
    private List<BaseNode>                   nodes;

    /** Stores the id of the partition this rule will be added to */
    private RuleBasePartitionId              partitionId;

    /** the calculate temporal distance matrix */
    private TemporalDependencyMatrix         temporal;

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

        this.nodes = new LinkedList<BaseNode>();

        this.partitionId = null;
        
        this.ruleComponent = new Stack<RuleComponent>();
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
        if ( this.objectType == null ) {
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
        if ( this.objectType == null ) {
            this.objectType = new LinkedList();
        }
        return this.objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(final LinkedList objectType) {
        if ( this.objectType == null ) {
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
            this.workingMemories = this.rulebase.getWorkingMemories();
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
    public void releaseId(int id) {
        this.idGenerator.releaseId( id );
    }

    /**
     * Adds the rce to the build stack
     * @param rce
     */
    public void push(final RuleConditionElement rce) {
        if ( this.buildstack == null ) {
            this.buildstack = new LinkedList<RuleConditionElement>();
        }
        this.buildstack.addLast( rce );
    }

    /**
     * Removes the top stack element
     * @return
     */
    public RuleConditionElement pop() {
        if ( this.buildstack == null ) {
            this.buildstack = new LinkedList<RuleConditionElement>();
        }
        return this.buildstack.removeLast();
    }

    /**
     * Returns the top stack element without removing it
     * @return
     */
    public RuleConditionElement peek() {
        if ( this.buildstack == null ) {
            this.buildstack = new LinkedList<RuleConditionElement>();
        }
        return this.buildstack.getLast();
    }

    /**
     * Returns a list iterator to iterate over the stacked elements
     * @return
     */
    public ListIterator<RuleConditionElement> stackIterator() {
        if ( this.buildstack == null ) {
            this.buildstack = new LinkedList<RuleConditionElement>();
        }
        return this.buildstack.listIterator( this.buildstack.size() );
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

        Integer seq = (Integer) this.rulebase.getAgendaGroupRuleTotals().get( groupName );
        if ( seq == null ) {
            seq = new Integer( 0 );
        }
        Integer newSeq = new Integer( seq.intValue() + 1 );
        this.rulebase.getAgendaGroupRuleTotals().put( groupName,
                                                      newSeq );

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
    
    

    public boolean isQuery() {
		return query;
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

    /**
     * @return the behaviours
     */
    public List<Behavior> getBehaviors() {
        return behaviors;
    }

    /**
     * @param behaviors the behaviours to set
     */
    public void setBehaviors(List<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    /**
     * @return the nodes
     */
    public List<BaseNode> getNodes() {
        return nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(List<BaseNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the partitionId
     */
    public RuleBasePartitionId getPartitionId() {
        return partitionId;
    }

    /**
     * @param partitionId the partitionId to set
     */
    public void setPartitionId(RuleBasePartitionId partitionId) {
        this.partitionId = partitionId;
    }

    public void setTemporalDistance(TemporalDependencyMatrix temporal) {
        this.temporal = temporal;
    }

    public TemporalDependencyMatrix getTemporalDistance() {
        return this.temporal;
    }

    public LinkedList<RuleConditionElement> getBuildStack() {
        return this.buildstack;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
        if ( rule instanceof Query) {
        	this.query = true;
        }
    }

    /**
     * Removes the top element from the rule component stack.
     * The rule component stack is used to add trackability to
     * the ReteOO nodes so that they can be linked to the rule
     * components that originated them.
     *  
     * @return
     */
    public RuleComponent popRuleComponent() {
        return this.ruleComponent.pop();
    }
    
    /**
     * Peeks at the top element from the rule component stack.
     * The rule component stack is used to add trackability to
     * the ReteOO nodes so that they can be linked to the rule
     * components that originated them.
     *  
     * @return
     */
    public RuleComponent peekRuleComponent() { 
        return this.ruleComponent.isEmpty() ? null : this.ruleComponent.peek();
    }
    
    /**
     * Adds the ruleComponent to the top of the rule component stack.
     * The rule component stack is used to add trackability to
     * the ReteOO nodes so that they can be linked to the rule
     * components that originated them.
     *  
     * @return
     */
    public void pushRuleComponent( RuleComponent ruleComponent ) {
        this.ruleComponent.push( ruleComponent );
    }

}
