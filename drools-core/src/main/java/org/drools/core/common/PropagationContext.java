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

package org.drools.core.common;

import java.io.Externalizable;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.base.ObjectType;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public interface PropagationContext extends Externalizable {

    enum Type {
        INSERTION, DELETION, MODIFICATION, RULE_ADDITION, RULE_REMOVAL, EXPIRATION
    }

    long getPropagationNumber();

    Type getType();

    RuleImpl getRuleOrigin();

    TerminalNode getTerminalNodeOrigin();

    /**
     * @return fact handle that was inserted, updated or retracted that created the PropagationContext
     */
    FactHandle getFactHandle();
    void setFactHandle(FactHandle factHandle);

    EntryPointId getEntryPoint();
    
    BitMask getModificationMask();
    PropagationContext adaptModificationMaskForObjectType(ObjectType type, ReteEvaluator reteEvaluator);
    void setModificationMask(BitMask mask);

    MarshallerReaderContext getReaderContext();

    void cleanReaderContext();

    void setEntryPoint(EntryPointId entryPoint);
}
