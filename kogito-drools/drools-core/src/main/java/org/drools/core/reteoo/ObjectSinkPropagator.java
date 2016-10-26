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

package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.spi.PropagationContext;

import java.io.Externalizable;

public interface ObjectSinkPropagator
    extends
    Externalizable {

    ObjectSinkPropagator addObjectSink(ObjectSink sink, int alphaNodeHashingThreshold);
    ObjectSinkPropagator removeObjectSink(ObjectSink sink);

    default void changeSinkPartition( ObjectSink sink, RuleBasePartitionId oldPartition, RuleBasePartitionId newPartition, int alphaNodeHashingThreshold ) { }

    void propagateAssertObject(InternalFactHandle factHandle,
                               PropagationContext context,
                               InternalWorkingMemory workingMemory);

    BaseNode getMatchingNode(BaseNode candidate);

    ObjectSink[] getSinks();

    int size();
    boolean isEmpty();

    void propagateModifyObject(InternalFactHandle factHandle,
                               ModifyPreviousTuples modifyPreviousTuples,
                               PropagationContext context,
                               InternalWorkingMemory workingMemory);
    
    void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                 final ModifyPreviousTuples modifyPreviousTuples,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory);
    
    void doLinkRiaNode(InternalWorkingMemory wm);

    void doUnlinkRiaNode(InternalWorkingMemory wm);

}
