package org.drools.spi;

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

import org.drools.common.ActivationGroupNode;
import org.drools.common.LogicalDependency;
import org.drools.common.RuleFlowGroupNode;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.util.LinkedList;

/**
 * When a <code>Tuple</code> fully matches a rule it is added to the <code>Agenda</code>
 * As an <code>Activation</code>. Each <code>Activation</code> is assigned a number, this 
 * number is determined by the <code>WorkingMemory</code> all <code>Activations</code> created 
 * from a single insert, update, retract are assgigned the same Activation number.
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public interface Activation
    extends
    Serializable {
    /**
     * Retrieve the <code>Rule</code> that was activated.
     * 
     * @return The rule.
     */
    Rule getRule();
    
    int getSalience();

    /**
     * Retrieve the subrule that was activated.
     * 
     * @return
     */
    GroupElement getSubRule();

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
     * Retrieve the activation number.
     * 
     * @return The activation number
     */
    long getActivationNumber();

    /**
     * Cancel the <code>Activation</code> by removing it from the <code>Agenda</code>. 
     */
    void remove();

    public void addLogicalDependency(LogicalDependency node);

    public LinkedList getLogicalDependencies();
    
    public void setLogicalDependencies(LinkedList justified);

    public boolean isActivated();

    public void setActivated(boolean activated);

    public AgendaGroup getAgendaGroup();

    public ActivationGroupNode getActivationGroupNode();

    public void setActivationGroupNode(ActivationGroupNode activationGroupNode);

    public RuleFlowGroupNode getRuleFlowGroupNode();

    public void setRuleFlowGroupNode(RuleFlowGroupNode ruleFlowGroupNode);
    
}