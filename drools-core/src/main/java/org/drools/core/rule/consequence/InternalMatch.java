/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.rule.consequence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.base.InitialFact;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.consequence.Consequence;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.util.Queue.QueueEntry;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

/**
 * When a <code>Tuple</code> fully matches a rule it is added to the <code>Agenda</code>
 * As an <code>Activation</code>. Each <code>Activation</code> is assigned a number, this 
 * number is determined by the <code>WorkingMemory</code> all <code>Activations</code> created 
 * from a single insert, update, retract are assigned the same Activation number.
 */
public interface InternalMatch extends Serializable, QueueEntry, Match {
    
    /**
     * 
     * @return
     *     The rule that was activated.
     */
    RuleImpl getRule();

    Consequence getConsequence();
    
    /**
     * Each PropagationContext is assigned an id from a counter for the WorkingMemory action it
     * represents. All Activations return this id as the ActivationNumber, thus all Activations
     * created from the same PropagationContext will return the same long for this method.
     *  
     * @return 
     *     The activation number
     */
    long getActivationNumber();

    Runnable getCallback();
    void setCallback(Runnable callback);

    default List<Object> getObjectsDeep() {
        return Collections.emptyList();
    }

    /**
     * Retrieve the <code>Tuple</code> that was activated.
     * 
     * @return The tuple.
     */
    TupleImpl getTuple();

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

    void setActivationFactHandle(InternalFactHandle factHandle);

    RuleAgendaItem getRuleAgendaItem();

    TerminalNode getTerminalNode();

    String toExternalForm();

    default List<FactHandle> getFactHandles(Tuple tuple) {
        FactHandle[] factHandles = tuple.toFactHandles();
        List<FactHandle> list = new ArrayList<>(factHandles.length);
        for (FactHandle factHandle : factHandles) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            if (!(o instanceof QueryElementFactHandle)) {
                list.add(factHandle);
            }
        }
        return Collections.unmodifiableList( list );
    }

    default List<Object> getObjectsDeep(TupleImpl entry) {
        List<Object> list = new ArrayList<>();
        while ( entry != null ) {
            if ( entry.getFactHandle() != null ) {
                Object o = entry.getFactHandle().getObject();
                if (!(o instanceof QueryElementFactHandle || o instanceof InitialFact)) {
                    list.add(o);
                    list.addAll( entry.getAccumulatedObjects() );
                }
            }
            entry = entry.getParent();
        }
        return list;
    }

    default List<Object> getObjects(Tuple tuple) {
        FactHandle[] factHandles = tuple.toFactHandles();
        List<Object> list = new ArrayList<>(factHandles.length);
        for (FactHandle factHandle : factHandles) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            if (!(o instanceof QueryElementFactHandle)) {
                list.add(o);
            }
        }
        return Collections.unmodifiableList(list);
    }

    default boolean checkProcessInstance(ReteEvaluator workingMemory, String processInstanceId) {
        final Map<String, Declaration> declarations = getTerminalNode().getSubRule().getOuterDeclarations();
        for ( Declaration declaration : declarations.values() ) {
            if ( "processInstance".equals( declaration.getIdentifier() )
                    || "org.kie.api.runtime.process.WorkflowProcessInstance".equals(declaration.getTypeName())) {
                Object value = declaration.getValue(workingMemory, getTuple());
                if ( value instanceof ProcessInstance) {
                    return processInstanceId.equals( (( ProcessInstance ) value).getId() );
                }
            }
        }
        return true;
    }
}
