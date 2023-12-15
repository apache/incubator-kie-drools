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
package org.drools.core.common;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.TraitHelper;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public interface InternalWorkingMemoryEntryPoint extends WorkingMemoryEntryPoint {

    TraitHelper getTraitHelper();

    PropagationContextFactory getPctxFactory();

    void insert(InternalFactHandle handle);

    FactHandle insert( Object object,
                       boolean dynamic,
                       RuleImpl rule,
                       TerminalNode terminalNode );

    void insert(InternalFactHandle handle,
                Object object,
                RuleImpl rule,
                TerminalNode terminalNode,
                ObjectTypeConf typeConf );

    void insert(InternalFactHandle handle,
                Object object,
                RuleImpl rule,
                ObjectTypeConf typeConf,
                PropagationContext pctx );

    FactHandle insertAsync(Object object);

    InternalFactHandle update(InternalFactHandle handle,
                              Object object,
                              BitMask mask,
                              Class<?> modifiedClass,
                              InternalMatch internalMatch);

    void update(InternalFactHandle handle,
                Object object,
                Object originalObject,
                ObjectTypeConf typeConf,
                PropagationContext propagationContext);

    PropagationContext delete(InternalFactHandle handle,
                              Object object,
                              ObjectTypeConf typeConf,
                              RuleImpl rule,
                              TerminalNode terminalNode);

    PropagationContext immediateDelete(InternalFactHandle handle,
                                       Object object,
                                       ObjectTypeConf typeConf,
                                       RuleImpl rule,
                                       TerminalNode terminalNode);

    void removeFromObjectStore(InternalFactHandle handle);
}
