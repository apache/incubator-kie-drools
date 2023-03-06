/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.impl;

import java.io.IOException;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.common.PropagationContext;

import static org.drools.core.common.PhreakPropagationContextFactory.createPropagationContextForFact;

public class WorkingMemoryReteExpireAction
        extends PropagationEntry.AbstractPropagationEntry
        implements WorkingMemoryAction {

    protected EventFactHandle factHandle;
    protected ObjectTypeNode node;

    protected WorkingMemoryReteExpireAction() { }

    public WorkingMemoryReteExpireAction(final EventFactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public WorkingMemoryReteExpireAction(final EventFactHandle factHandle,
                                         final ObjectTypeNode node) {
        this(factHandle);
        this.node = node;
        factHandle.increaseOtnCount();
    }

    public EventFactHandle getFactHandle() {
        return factHandle;
    }

    public void setFactHandle(EventFactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public ObjectTypeNode getNode() {
        return node;
    }

    public void setNode(ObjectTypeNode node) {
        this.node = node;
    }

    public WorkingMemoryReteExpireAction(MarshallerReaderContext context) throws IOException {
        this.factHandle = (EventFactHandle)context.getHandles().get(context.readLong());
        final int nodeId = context.readInt();
        this.node = (ObjectTypeNode) context.getSinks().get(nodeId);
    }

    public void internalExecute(ReteEvaluator reteEvaluator) {
        if (!factHandle.isValid()) {
            return;
        }

        PropagationContext context = createPropagationContextForFact( reteEvaluator, factHandle, PropagationContext.Type.EXPIRATION );
        reteEvaluator.getActivationsManager().registerExpiration( context );

        factHandle.forEachLeftTuple( ObjectTypeNode::expireLeftTuple );
        factHandle.forEachRightTuple( rt -> {
            rt.setExpired(reteEvaluator, context);
            ObjectTypeNode.expireRightTuple(rt);
        } );

        expireFactHandle( reteEvaluator, factHandle );
    }

    private static void expireFactHandle( ReteEvaluator reteEvaluator, EventFactHandle factHandle ) {
        factHandle.decreaseOtnCount();
        if (factHandle.getOtnCount() == 0) {
            factHandle.setExpired( true );
            if (factHandle.getActivationsCount() == 0) {
                String epId = factHandle.getEntryPointName();
                ( (InternalWorkingMemoryEntryPoint) reteEvaluator.getEntryPoint( epId ) ).removeFromObjectStore( factHandle );
            } else {
                factHandle.setPendingRemoveFromStore( true );
            }
        }
    }

    @Override
    public boolean isPartitionSplittable() {
        return true;
    }

    @Override
    public PropagationEntry getSplitForPartition( int partitionNr ) {
        return new PartitionAwareWorkingMemoryReteExpireAction( factHandle, node, partitionNr );
    }

    @Override
    public String toString() {
        return "Expiration of " + factHandle.getObject();
    }

    public static class PartitionAwareWorkingMemoryReteExpireAction extends PropagationEntry.AbstractPartitionedPropagationEntry {
        private final EventFactHandle factHandle;
        private final ObjectTypeNode node;

        public PartitionAwareWorkingMemoryReteExpireAction(EventFactHandle factHandle, ObjectTypeNode node, int partition) {
            super( partition );
            this.factHandle = factHandle;
            this.node = node;
        }

        @Override
        public void internalExecute(ReteEvaluator reteEvaluator ) {
            if (!factHandle.isValid()) {
                return;
            }

            PropagationContext context = createPropagationContextForFact( reteEvaluator, factHandle, PropagationContext.Type.EXPIRATION );
            reteEvaluator.getActivationsManager().getPartitionedAgenda( partition ).registerExpiration( context );

            DefaultFactHandle.CompositeLinkedTuples linkedTuples = ( (DefaultFactHandle.CompositeLinkedTuples) factHandle.getLinkedTuples() );
            linkedTuples.forEachLeftTuple( partition, ObjectTypeNode::expireLeftTuple );
            linkedTuples.forEachRightTuple( partition, rt -> {
                rt.setExpired(reteEvaluator, context);
                ObjectTypeNode.expireRightTuple(rt);
            });

            if (isMainPartition()) {
                expireFactHandle( reteEvaluator, factHandle );
            }
        }

        @Override
        public String toString() {
            return "Expiration of " + factHandle.getObject() + " for partition " + partition;
        }
    }

}
