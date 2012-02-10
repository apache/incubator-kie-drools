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

import org.drools.RuleBaseConfiguration;
import org.drools.core.util.LinkedList;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.ContextEntry;

public interface BetaConstraints
    extends
    Externalizable {

    public ContextEntry[] createContext();

    public void updateFromTuple(ContextEntry[] context,
                                InternalWorkingMemory workingMemory,
                                LeftTuple tuple);

    public void updateFromFactHandle(ContextEntry[] context,
                                     InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle);

    public boolean isAllowedCachedLeft(ContextEntry[] context,
                                       InternalFactHandle handle);

    public boolean isAllowedCachedRight(ContextEntry[] context,
                                        LeftTuple tuple);

    public LinkedList getConstraints();

    public BetaConstraints getOriginalConstraint();
    
    public boolean isIndexed();

    public int getIndexCount();

    public boolean isEmpty();

    BetaMemory createBetaMemory(final RuleBaseConfiguration config,
                                final short nodeType );

    public void resetTuple(final ContextEntry[] context);

    public void resetFactHandle(final ContextEntry[] context);

}
