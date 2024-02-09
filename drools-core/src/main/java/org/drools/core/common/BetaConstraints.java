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

import java.io.Externalizable;
import java.util.List;

import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public interface BetaConstraints<C>
    extends
    Externalizable {

    C createContext();

    void updateFromTuple(C context,
                         ValueResolver valueResolver,
                         Tuple tuple);

    void updateFromFactHandle(C context,
                              ValueResolver valueResolver,
                              FactHandle handle);

    boolean isAllowedCachedLeft(C context,
                                FactHandle handle);

    boolean isAllowedCachedRight(BaseTuple tuple, C context);

    BetaConstraint[] getConstraints();

    BetaConstraints getOriginalConstraint();
    
    boolean isIndexed();

    int getIndexCount();

    boolean isEmpty();

    BetaMemory createBetaMemory(final RuleBaseConfiguration config,
                                final int nodeType);

    void resetTuple(final C context);

    void resetFactHandle(final C context);

    BitMask getListenedPropertyMask(Pattern pattern, ObjectType modifiedType, List<String> settableProperties);

    void init(BuildContext context, int betaNodeType);
    void initIndexes(int depth, int betaNodeType, RuleBaseConfiguration config);

    <T> T cloneIfInUse();

    boolean isLeftUpdateOptimizationAllowed();

    void registerEvaluationContext(BuildContext buildContext);
}
