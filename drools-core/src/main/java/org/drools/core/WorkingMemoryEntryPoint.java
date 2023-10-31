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
package org.drools.core;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.EntryPointId;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

/**
 * An interface for instances that allow handling of entry-point-scoped
 * facts
 */
public interface WorkingMemoryEntryPoint extends EntryPoint {

    /**
     * Insert a fact registering JavaBean <code>PropertyChangeListeners</code>
     * on the Object to automatically trigger <code>update</code> calls
     * if <code>dynamic</code> is <code>true</code>.
     * 
     * @param object
     *            The fact object.
     * @param dynamic
     *            true if Drools should add JavaBean
     *            <code>PropertyChangeListeners</code> to the object.
     * 
     * @return The new fact-handle associated with the object.
     */
    FactHandle insert(Object object,
                      boolean dynamic);

    /**
     * Internal method called by the engine when the session is being disposed, so that the entry point
     * can proceed with the necessary clean ups.
     */
    void dispose();

    ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();

    InternalRuleBase getKnowledgeBase();

    void delete(FactHandle factHandle,
                RuleImpl rule,
                TerminalNode terminalNode );

    void delete(FactHandle factHandle,
                RuleImpl rule,
                TerminalNode terminalNode,
                FactHandle.State fhState);

    void update(FactHandle handle,
                Object object,
                BitMask mask,
                Class<?> modifiedClass,
                InternalMatch internalMatch);

    EntryPointId getEntryPoint();
    ReteEvaluator getReteEvaluator();

    void reset();

    ObjectStore getObjectStore();

    FactHandleFactory getHandleFactory();

    EntryPointNode getEntryPointNode();

    default Object getRuleUnit() {
        return null;
    }

    default void setRuleUnit(Object ruleUnit) {

    }
}
