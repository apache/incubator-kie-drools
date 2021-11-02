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

import java.util.concurrent.CountDownLatch;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
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

import static org.drools.core.reteoo.EntryPointNode.removeRightTuplesMatchingOTN;
import static org.drools.core.rule.TypeDeclaration.NEVER_EXPIRES;

public interface PropagationEntry {

    void execute(ReteEvaluator reteEvaluator);

    PropagationEntry getNext();
    void setNext(PropagationEntry next);

    boolean requiresImmediateFlushing();
    
    boolean isCalledFromRHS();

    boolean isPartitionSplittable();
    PropagationEntry getSplitForPartition(int partitionNr);

    boolean defersExpiration();

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
        public boolean isPartitionSplittable() {
            return false;
        }

        @Override
        public boolean defersExpiration() {
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

        protected boolean isMainPartition() {
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

        public Insert( InternalFactHandle handle, PropagationContext context, ReteEvaluator reteEvaluator, ObjectTypeConf objectTypeConf) {
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;

            if ( objectTypeConf.isEvent() ) {
                scheduleExpiration(reteEvaluator, handle, context, objectTypeConf, reteEvaluator.getTimerService().getCurrentTime());
            }
        }

        public static void execute( InternalFactHandle handle, PropagationContext context, ReteEvaluator reteEvaluator, ObjectTypeConf objectTypeConf) {
            if ( objectTypeConf.isEvent() ) {
                scheduleExpiration(reteEvaluator, handle, context, objectTypeConf, reteEvaluator.getTimerService().getCurrentTime());
            }
            propagate( handle, context, reteEvaluator, objectTypeConf );
        }

        private static void propagate( InternalFactHandle handle, PropagationContext context, ReteEvaluator reteEvaluator, ObjectTypeConf objectTypeConf ) {
            for ( ObjectTypeNode otn : objectTypeConf.getObjectTypeNodes() ) {
                otn.propagateAssert( handle, context, reteEvaluator );
            }
        }

        public void execute( ReteEvaluator reteEvaluator ) {
            propagate( handle, context, reteEvaluator, objectTypeConf );
        }

        private static void scheduleExpiration(ReteEvaluator reteEvaluator, InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf, long insertionTime) {
            for ( ObjectTypeNode otn : objectTypeConf.getObjectTypeNodes() ) {
                scheduleExpiration( reteEvaluator, handle, context, otn, insertionTime, otn.getExpirationOffset() );
            }
            if ( objectTypeConf.getConcreteObjectTypeNode() == null ) {
                scheduleExpiration( reteEvaluator, handle, context, null, insertionTime, ( (ClassObjectTypeConf) objectTypeConf ).getExpirationOffset() );
            }
        }

        private static void scheduleExpiration( ReteEvaluator reteEvaluator, InternalFactHandle handle, PropagationContext context, ObjectTypeNode otn, long insertionTime, long expirationOffset ) {
            if ( expirationOffset == NEVER_EXPIRES || expirationOffset == Long.MAX_VALUE || context.getReaderContext() != null ) {
                return;
            }

            // DROOLS-455 the calculation of the effectiveEnd may overflow and become negative
            EventFactHandle eventFactHandle = (EventFactHandle) handle;
            long nextTimestamp = getNextTimestamp( insertionTime, expirationOffset, eventFactHandle );

            WorkingMemoryReteExpireAction action = new WorkingMemoryReteExpireAction( (EventFactHandle) handle, otn );
            if (nextTimestamp <= reteEvaluator.getTimerService().getCurrentTime()) {
                reteEvaluator.addPropagation( action );
            } else {
                JobContext jobctx = new ObjectTypeNode.ExpireJobContext( action, reteEvaluator );
                JobHandle jobHandle = reteEvaluator.getTimerService()
                                        .scheduleJob( job,
                                                      jobctx,
                                                      PointInTimeTrigger.createPointInTimeTrigger( nextTimestamp, null ) );
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

        public void execute(ReteEvaluator reteEvaluator) {
            execute(handle, context, objectTypeConf, reteEvaluator);
        }

        public static void execute(InternalFactHandle handle, PropagationContext pctx, ObjectTypeConf objectTypeConf, ReteEvaluator reteEvaluator) {
            // make a reference to the previous tuples, then null then on the handle
            ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples( handle.detachLinkedTuples() );
            ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();
            for ( int i = 0, length = cachedNodes.length; i < length; i++ ) {
                cachedNodes[i].modifyObject( handle, modifyPreviousTuples, pctx, reteEvaluator );
                if (i < cachedNodes.length - 1) {
                    removeRightTuplesMatchingOTN( pctx, reteEvaluator, modifyPreviousTuples, cachedNodes[i], 0 );
                }
            }
            modifyPreviousTuples.retractTuples(pctx, reteEvaluator);
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

        public void execute(ReteEvaluator reteEvaluator) {
            ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples( handle.detachLinkedTuplesForPartition(partition) );
            ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();
            for ( int i = 0, length = cachedNodes.length; i < length; i++ ) {
                ObjectTypeNode otn = cachedNodes[i];
                ( (CompositePartitionAwareObjectSinkAdapter) otn.getObjectSinkPropagator() )
                        .propagateModifyObjectForPartition( handle, modifyPreviousTuples,
                                                            context.adaptModificationMaskForObjectType(otn.getObjectType(), reteEvaluator),
                                                            reteEvaluator, partition );
                if (i < cachedNodes.length - 1) {
                    removeRightTuplesMatchingOTN( context, reteEvaluator, modifyPreviousTuples, otn, partition );
                }
            }
            modifyPreviousTuples.retractTuples(context, reteEvaluator);
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

        public void execute(ReteEvaluator reteEvaluator) {
            epn.propagateRetract(handle, context, objectTypeConf, reteEvaluator);
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

        public void execute(ReteEvaluator reteEvaluator) {
            ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();

            if ( cachedNodes == null ) {
                // it is  possible that there are no ObjectTypeNodes for an  object being retracted
                return;
            }

            for ( ObjectTypeNode cachedNode : cachedNodes ) {
                cachedNode.retractObject( handle, context, reteEvaluator, partition );
            }

            if (handle.isEvent() && isMainPartition()) {
                ((EventFactHandle) handle).unscheduleAllJobs(reteEvaluator);
            }
        }

        @Override
        public String toString() {
            return "Delete of " + handle.getObject() + " for partition " + partition;
        }
    }
}
