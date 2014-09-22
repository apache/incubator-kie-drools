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

package org.drools.core.spi;

import java.io.Serializable;

import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.LogicalDependency;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.GroupElement;
import org.kie.api.runtime.rule.AgendaGroup;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.runtime.beliefs.Mode;

/**
 * When a <code>Tuple</code> fully matches a rule it is added to the <code>Agenda</code>
 * As an <code>Activation</code>. Each <code>Activation</code> is assigned a number, this 
 * number is determined by the <code>WorkingMemory</code> all <code>Activations</code> created 
 * from a single insert, update, retract are assgigned the same Activation number.
 */
public interface Activation<T extends Mode>
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
    LeftTuple getTuple();

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
    
    public void addBlocked(final LogicalDependency<SimpleMode> node);
    
    public LinkedList<LogicalDependency<SimpleMode>> getBlocked();

    public void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified);
    
    public LinkedList<SimpleMode> getBlockers();
    
    public void addLogicalDependency(LogicalDependency<T> node);

    public LinkedList<LogicalDependency<T>> getLogicalDependencies();

    public void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified);

    public void setQueued(boolean activated);
    
    public boolean isQueued();

    public InternalAgendaGroup getAgendaGroup();

    public ActivationGroupNode getActivationGroupNode();

    public void setActivationGroupNode(ActivationGroupNode activationGroupNode);

    public ActivationNode getActivationNode();

    public void setActivationNode(ActivationNode ruleFlowGroupNode);
    
    InternalFactHandle getFactHandle();   

    public boolean isMatched();

    public void setMatched(boolean matched);    

    public boolean isActive();

    public void setActive(boolean active);

    public boolean isRuleAgendaItem();


    void setQueueIndex(int index);

    int getQueueIndex();

    void dequeue();
}
