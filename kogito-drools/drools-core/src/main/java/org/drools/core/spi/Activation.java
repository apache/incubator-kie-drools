/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.spi;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.GroupElement;
import org.drools.core.util.LinkedList;
import org.kie.api.runtime.rule.Match;

/**
 * When a <code>Tuple</code> fully matches a rule it is added to the <code>Agenda</code>
 * As an <code>Activation</code>. Each <code>Activation</code> is assigned a number, this 
 * number is determined by the <code>WorkingMemory</code> all <code>Activations</code> created 
 * from a single insert, update, retract are assgigned the same Activation number.
 */
public interface Activation<T extends ModedAssertion<T>>
    extends
    Serializable,
    Match {
    
    /**
     * 
     * @return
     *     The rule that was activated.
     */
    RuleImpl getRule();

    Consequence getConsequence();
    
    int getSalience();

    /**
     * Retrieve the subrule that was activated.
     * 
     * @return
     */
    GroupElement getSubRule();
    
    /**
     * Each PropgationContext is assigned an id from a counter for the WorkingMemory action it 
     * represents. All Activations return this id as the ActivationNumber, thus all Activations
     * created from the same PropgationContext will return the same long for this method.
     *  
     * @return 
     *     The activation number
     */
    long getActivationNumber();

    /**
     * Retrieve the <code>Tuple</code> that was activated.
     * 
     * @return The tuple.
     */
    Tuple getTuple();

    /**
     * Retrieve the <code>PropagationContext</code> for the <code>Activation</code>
     * 
     * @return The propagation context
     */
    PropagationContext getPropagationContext();

    /**
     * Cancel the <code>Activation</code> by removing it from the <code>Agenda</code>. 
     */
    void remove();
    
    void addBlocked(final LogicalDependency<SimpleMode> node);
    
    LinkedList<LogicalDependency<SimpleMode>> getBlocked();

    void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified);
    
    LinkedList<SimpleMode> getBlockers();
    
    void addLogicalDependency(LogicalDependency<T> node);

    LinkedList<LogicalDependency<T>> getLogicalDependencies();

    void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified);

    void setQueued(boolean activated);
    
    boolean isQueued();

    InternalAgendaGroup getAgendaGroup();

    ActivationGroupNode getActivationGroupNode();

    void setActivationGroupNode(ActivationGroupNode activationGroupNode);

    ActivationNode getActivationNode();

    void setActivationNode(ActivationNode ruleFlowGroupNode);
    
    InternalFactHandle getActivationFactHandle();

    boolean isMatched();

    void setMatched(boolean matched);

    boolean isActive();

    void setActive(boolean active);

    boolean isRuleAgendaItem();

    void setQueueIndex(int index);

    int getQueueIndex();

    void dequeue();

    default List<Object> getObjectsDeep() {
        return Collections.emptyList();
    }
}
