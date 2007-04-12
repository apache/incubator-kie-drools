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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ReteooBuilder;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.TupleSource;
import org.drools.rule.RuleConditionElement;

/**
 * A build context for Reteoo Builder
 * 
 * @author etirelli
 */
public class BuildContext {

    // tuple source to attach next node to
    private TupleSource               tupleSource;

    // object source to attach next node to
    private ObjectSource              objectSource;

    // object type cache to check for cross products
    private Map                       objectType;

    // offset of the column
    private int                       currentColumnOffset;

    // attached nodes cache
    private Map                       attachedNodes;

    // rule base to add rules to
    private ReteooRuleBase            rulebase;

    // working memories attached to the given rulebase
    private ReteooWorkingMemory[]     workingMemories;

    // id generator
    private ReteooBuilder.IdGenerator idGenerator;

    // a build stack to track nested elements
    private LinkedList                buildstack;

    // beta constraints from the last column attached
    BetaConstraints                   betaconstraints;

    public BuildContext(final ReteooRuleBase rulebase,
                        final Map attachedNodes,
                        final ReteooBuilder.IdGenerator idGenerator) {
        this.rulebase = rulebase;
        this.workingMemories = (ReteooWorkingMemory[]) this.rulebase.getWorkingMemories().toArray( new ReteooWorkingMemory[this.rulebase.getWorkingMemories().size()] );
        this.attachedNodes = attachedNodes;
        this.idGenerator = idGenerator;

        this.objectType = new LinkedHashMap();
        this.buildstack = new LinkedList();

        this.tupleSource = null;
        this.objectSource = null;

        this.currentColumnOffset = 0;
    }

    /**
     * @return the currentColumnOffset
     */
    public int getCurrentColumnOffset() {
        return this.currentColumnOffset;
    }

    /**
     * @param currentColumnOffset the currentColumnOffset to set
     */
    public void setCurrentColumnOffset(final int currentColumnIndex) {
        this.currentColumnOffset = currentColumnIndex;
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
    public Map getObjectType() {
        return this.objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(final Map objectType) {
        this.objectType = objectType;
    }

    /**
     * @return the tupleSource
     */
    public TupleSource getTupleSource() {
        return this.tupleSource;
    }

    /**
     * @param tupleSource the tupleSource to set
     */
    public void setTupleSource(final TupleSource tupleSource) {
        this.tupleSource = tupleSource;
    }

    public void incrementCurrentColumnOffset() {
        this.currentColumnOffset++;
    }

    public void decrementCurrentColumnOffset() {
        this.currentColumnOffset--;
    }

    /**
     * Checks if the given candidate node is in cache, and if it is returns it. 
     * Returns null otherwise.
     *
     * @param candidate
     * @return
     */
    public BaseNode getNodeFromCache(final BaseNode candidate) {
        return (BaseNode) this.attachedNodes.get( candidate );
    }

    /**
     * Adds given node to node cache
     * @param candidate
     */
    public void addNodeToCache(final BaseNode node) {
        this.attachedNodes.put( node,
                                node );
    }

    /**
     * Returns context rulebase
     * @return
     */
    public ReteooRuleBase getRuleBase() {
        return this.rulebase;
    }

    /**
     * Return the array of working memories associated with the given
     * rulebase.
     * 
     * @return
     */
    public ReteooWorkingMemory[] getWorkingMemories() {
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
    public void releaseLastId() {
        this.idGenerator.releaseLastId();
    }

    /**
     * Adds the rce to the build stack
     * @param rce
     */
    public void push(final RuleConditionElement rce) {
        this.buildstack.addLast( rce );
    }

    /**
     * Removes the top stack element
     * @return
     */
    public RuleConditionElement pop() {
        return (RuleConditionElement) this.buildstack.removeLast();
    }

    /**
     * Returns the top stack element without removing it
     * @return
     */
    public RuleConditionElement peek() {
        return (RuleConditionElement) this.buildstack.getLast();
    }

    /**
     * Returns a list iterator to iterate over the stacked elements
     * @return
     */
    public ListIterator stackIterator() {
        return this.buildstack.listIterator();
    }

    /**
     * @return the betaconstraints
     */
    public BetaConstraints getBetaconstraints() {
        return this.betaconstraints;
    }

    /**
     * @param betaconstraints the betaconstraints to set
     */
    public void setBetaconstraints(final BetaConstraints betaconstraints) {
        this.betaconstraints = betaconstraints;
    }

}
