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

import java.io.Externalizable;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.constraint.BetaNodeFieldConstraint;
import org.drools.core.base.ObjectType;
import org.drools.core.reteoo.Tuple;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public interface BetaConstraints
    extends
    Externalizable {

    ContextEntry[] createContext();

    void updateFromTuple(ContextEntry[] context,
                         ReteEvaluator reteEvaluator,
                         Tuple tuple);

    void updateFromFactHandle(ContextEntry[] context,
                              ReteEvaluator reteEvaluator,
                              FactHandle handle);

    boolean isAllowedCachedLeft(ContextEntry[] context,
                                FactHandle handle);

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

    BitMask getListenedPropertyMask(ObjectType modifiedType, List<String> settableProperties);

    void init(BuildContext context, short betaNodeType);
    void initIndexes(int depth, short betaNodeType, RuleBaseConfiguration config);

    BetaConstraints cloneIfInUse();

    boolean isLeftUpdateOptimizationAllowed();

    void registerEvaluationContext(BuildContext buildContext);
}
