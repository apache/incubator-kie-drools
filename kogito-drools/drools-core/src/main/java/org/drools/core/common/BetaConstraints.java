/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Tuple;
import org.drools.core.util.bitmask.BitMask;

import java.io.Externalizable;
import java.util.List;

public interface BetaConstraints
    extends
    Externalizable {

    ContextEntry[] createContext();

    void updateFromTuple(ContextEntry[] context,
                         InternalWorkingMemory workingMemory,
                         Tuple tuple);

    void updateFromFactHandle(ContextEntry[] context,
                                     InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle);

    boolean isAllowedCachedLeft(ContextEntry[] context,
                                InternalFactHandle handle);

    boolean isAllowedCachedRight(ContextEntry[] context,
                                 Tuple tuple);

    BetaNodeFieldConstraint[] getConstraints();

    BetaConstraints getOriginalConstraint();
    
    boolean isIndexed();

    int getIndexCount();

    boolean isEmpty();

    BetaMemory createBetaMemory(final RuleBaseConfiguration config,
                                final short nodeType );

    void resetTuple(final ContextEntry[] context);

    void resetFactHandle(final ContextEntry[] context);

    BitMask getListenedPropertyMask(List<String> settableProperties);

    void init(BuildContext context, short betaNodeType);
    void initIndexes(int depth, short betaNodeType);

    BetaConstraints cloneIfInUse();

    boolean isLeftUpdateOptimizationAllowed();

    void registerEvaluationContext(BuildContext buildContext);
}
