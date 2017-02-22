/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.phreak;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.WorkingMemoryReteExpireAction;
import org.drools.core.reteoo.ClassObjectTypeConf;
import org.drools.core.reteoo.CompositePartitionAwareObjectSinkAdapter;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.impl.PointInTimeTrigger;

import java.util.concurrent.CountDownLatch;

public interface PropagationEntry {

    void execute(InternalWorkingMemory wm);
    void execute(InternalKnowledgeRuntime kruntime);

    PropagationEntry getNext();
    void setNext(PropagationEntry next);

    boolean requiresImmediateFlushing();
    
    boolean isCalledFromRHS();

    boolean isPartitionSplittable();
    PropagationEntry getSplitForPartition(int partitionNr);

    abstract class AbstractPropagationEntry implements PropagationEntry {
        private PropagationEntry next;

        public void setNext(PropagationEntry next) {
            this.next = next;
        }

        public PropagationEntry getNext() {
            return next;
        }

        @Override
        public boolean requiresImmediateFlushing() {
            return false;
        }
        
        @Override
        public boolean isCalledFromRHS() {
            return false;
        }

        @Override
        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((WorkingMemoryEntryPoint) kruntime).getInternalWorkingMemory() );
        }

        @Override
        public boolean isPartitionSplittable() {
            return false;
        }

        @Override
        public PropagationEntry getSplitForPartition(int partitionNr) {
            throw new UnsupportedOperationException();
        }
    }

    abstract class AbstractPartitionedPropagationEntry extends AbstractPropagationEntry {
        protected final int partition;

        protected AbstractPartitionedPropagationEntry( int partition ) {
            this.partition = partition;
        }

        protected boolean isMasterPartition() {
            return partition == 0;
        }
    }

    abstract class PropagationEntryWithResult<T> extends PropagationEntry.AbstractPropagationEntry {
        private final CountDownLatch done = new CountDownLatch( 1 );

        private T result;

        public final T getResult() {
            try {
                done.await();
            } catch (InterruptedException e) {
                throw new RuntimeException( e );
            }
            return result;
        }

        protected void done(T result) {
            this.result = result;
            done.countDown();
        }

        @Override
        public boolean requiresImmediateFlushing() {
            return true;
        }
    }

    class Insert extends AbstractPropagationEntry {
        private static final transient ObjectTypeNode.ExpireJob job = new ObjectTypeNode.ExpireJob();

        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final ObjectTypeConf objectTypeConf;

        public Insert( InternalFactHandle handle, PropagationContext context, InternalWorkingMemory workingMemory, ObjectTypeConf objectTypeConf) {
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;

            if ( objectTypeConf.isEvent() ) {
                scheduleExpiration(workingMemory, handle, context, objectTypeConf, workingMemory.getTimerService().getCurrentTime());
            }
        }

        public static void execute( InternalFactHandle handle, PropagationContext context, InternalWorkingMemory wm, ObjectTypeConf objectTypeConf) {
            if ( objectTypeConf.isEvent() ) {
                scheduleExpiration(wm, handle, context, objectTypeConf, wm.getTimerService().getCurrentTime());
            }
            propagate( handle, context, wm, objectTypeConf );
        }

        private static void propagate( InternalFactHandle handle, PropagationContext context, InternalWorkingMemory wm, ObjectTypeConf objectTypeConf ) {
            for ( ObjectTypeNode otn : objectTypeConf.getObjectTypeNodes() ) {
                otn.propagateAssert( handle, context, wm );
            }
        }

        public void execute( InternalWorkingMemory wm ) {
            propagate( handle, context, wm, objectTypeConf );
        }

        private static void scheduleExpiration(InternalWorkingMemory wm, InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf, long insertionTime) {
            for ( ObjectTypeNode otn : objectTypeConf.getObjectTypeNodes() ) {
                scheduleExpiration( wm, handle, context, otn, insertionTime, otn.getExpirationOffset() );
            }
            if ( objectTypeConf.getConcreteObjectTypeNode() == null ) {
                scheduleExpiration( wm, handle, context, null, insertionTime, ( (ClassObjectTypeConf) objectTypeConf ).getExpirationOffset() );
            }
        }

        private static void scheduleExpiration( InternalWorkingMemory wm, InternalFactHandle handle, PropagationContext context, ObjectTypeNode otn, long insertionTime, long expirationOffset ) {
            if ( expirationOffset < 0 || expirationOffset == Long.MAX_VALUE || context.getReaderContext() != null ) {
                return;
            }

            // DROOLS-455 the calculation of the effectiveEnd may overflow and become negative
            EventFactHandle eventFactHandle = (EventFactHandle) handle;
            long nextTimestamp = getNextTimestamp( insertionTime, expirationOffset, eventFactHandle );

            WorkingMemoryReteExpireAction action = new WorkingMemoryReteExpireAction( (EventFactHandle) handle, otn );
            if (nextTimestamp < wm.getTimerService().getCurrentTime()) {
                wm.addPropagation( action );
            } else {
                JobContext jobctx = new ObjectTypeNode.ExpireJobContext( action, wm );
                JobHandle jobHandle = wm.getTimerService()
                                        .scheduleJob( job,
                                                      jobctx,
                                                      new PointInTimeTrigger( nextTimestamp, null, null ) );
                jobctx.setJobHandle( jobHandle );
                eventFactHandle.addJob( jobHandle );
            }
        }

        private static long getNextTimestamp( long insertionTime, long expirationOffset, EventFactHandle eventFactHandle ) {
            long effectiveEnd = eventFactHandle.getEndTimestamp() + expirationOffset;
            return Math.max( insertionTime, effectiveEnd >= 0 ? effectiveEnd : Long.MAX_VALUE );
        }

        @Override
        public String toString() {
            return "Insert of " + handle.getObject();
        }
    }

    class Update extends AbstractPropagationEntry {
        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final ObjectTypeConf objectTypeConf;

        public Update(InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf) {
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;
        }

        public void execute(InternalWorkingMemory wm) {
            EntryPointNode.propagateModify(handle, context, objectTypeConf, wm);
        }

        @Override
        public boolean isPartitionSplittable() {
            return true;
        }

        @Override
        public PropagationEntry getSplitForPartition( int partitionNr ) {
            return new PartitionedUpdate( handle, context, objectTypeConf, partitionNr );
        }

        @Override
        public String toString() {
            return "Update of " + handle.getObject();
        }
    }

    class PartitionedUpdate extends AbstractPartitionedPropagationEntry {
        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final ObjectTypeConf objectTypeConf;

        PartitionedUpdate(InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf, int partition) {
            super( partition );
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;
        }

        public void execute(InternalWorkingMemory wm) {
            ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples( handle.detachLinkedTuplesForPartition(partition) );
            ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();
            for ( int i = 0, length = cachedNodes.length; i < length; i++ ) {
                ObjectTypeNode otn = cachedNodes[i];
                ( (CompositePartitionAwareObjectSinkAdapter) otn.getObjectSinkPropagator() )
                        .propagateModifyObjectForPartition( handle, modifyPreviousTuples,
                                                            context.adaptModificationMaskForObjectType(otn.getObjectType(), wm),
                                                            wm, partition );
                if (i < cachedNodes.length - 1) {
                    EntryPointNode.removeRightTuplesMatchingOTN( context, wm, modifyPreviousTuples, otn );
                }
            }
            modifyPreviousTuples.retractTuples(context, wm);
        }

        @Override
        public String toString() {
            return "Update of " + handle.getObject() + " for partition " + partition;
        }
    }

    class Delete extends AbstractPropagationEntry {
        private final EntryPointNode epn;
        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final ObjectTypeConf objectTypeConf;

        public Delete(EntryPointNode epn, InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf) {
            this.epn = epn;
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;
        }

        public void execute(InternalWorkingMemory wm) {
            epn.propagateRetract(handle, context, objectTypeConf, wm);
        }

        @Override
        public boolean isPartitionSplittable() {
            return true;
        }

        @Override
        public PropagationEntry getSplitForPartition( int partitionNr ) {
            return new PartitionedDelete( handle, context, objectTypeConf, partitionNr );
        }

        @Override
        public String toString() {
            return "Delete of " + handle.getObject();
        }
    }

    class PartitionedDelete extends AbstractPartitionedPropagationEntry {
        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final ObjectTypeConf objectTypeConf;

        PartitionedDelete(InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf, int partition) {
            super( partition );
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;
        }

        public void execute(InternalWorkingMemory wm) {
            ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();

            if ( cachedNodes == null ) {
                // it is  possible that there are no ObjectTypeNodes for an  object being retracted
                return;
            }

            for ( ObjectTypeNode cachedNode : cachedNodes ) {
                cachedNode.retractObject( handle, context, wm, partition );
            }

            if (handle.isEvent() && isMasterPartition()) {
                ((EventFactHandle) handle).unscheduleAllJobs(wm);
            }
        }

        @Override
        public String toString() {
            return "Delete of " + handle.getObject() + " for partition " + partition;
        }
    }
}
