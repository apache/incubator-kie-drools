/**
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

package org.drools.common;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.reteoo.EntryPointNode;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;


import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.Activation;

public interface InternalWorkingMemoryEntryPoint extends WorkingMemoryEntryPoint {
    ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();
    RuleBase getRuleBase();
    public void retract(final FactHandle factHandle,
                        final boolean removeLogical,
                        final boolean updateEqualsMap,
                        final Rule rule,
                        final Activation activation) throws FactException;
    public void update(org.drools.runtime.rule.FactHandle handle,
                       Object object,
                       Rule rule,
                       Activation activation) throws FactException;

    public EntryPoint getEntryPoint();
    public InternalWorkingMemory getInternalWorkingMemory();

    public FactHandle getFactHandleByIdentity(final Object object);
    
    void reset();
    
    ObjectStore getObjectStore();
    
    EntryPointNode getEntryPointNode();
}
