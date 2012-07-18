/*
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

import java.io.Externalizable;
import java.util.List;

import org.drools.RuleBaseConfiguration;
import org.drools.core.util.LinkedList;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.spi.BetaNodeFieldConstraint;

public interface BetaConstraints
    extends
    Externalizable {

    ContextEntry[] createContext();

    void updateFromTuple(ContextEntry[] context,
                                InternalWorkingMemory workingMemory,
                                LeftTuple tuple);

    void updateFromFactHandle(ContextEntry[] context,
                                     InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle);

    boolean isAllowedCachedLeft(ContextEntry[] context,
                                       InternalFactHandle handle);

    boolean isAllowedCachedRight(ContextEntry[] context,
                                        LeftTuple tuple);

    BetaNodeFieldConstraint[] getConstraints();

    BetaConstraints getOriginalConstraint();
    
    boolean isIndexed();

    int getIndexCount();

    boolean isEmpty();

    BetaMemory createBetaMemory(final RuleBaseConfiguration config,
                                final short nodeType );

    void resetTuple(final ContextEntry[] context);

    void resetFactHandle(final ContextEntry[] context);

    long getListenedPropertyMask(List<String> settableProperties);

    void init(BuildContext context, BetaNode betaNode);
    void initIndexes(int depth, short betaNodeType);
}
