/*
 * Copyright 2015 JBoss Inc
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

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.WorkingMemoryReteExpireAction;
import org.drools.core.reteoo.ClassObjectTypeConf;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.impl.PointInTimeTrigger;

public interface PropagationEntry {

    void execute(InternalWorkingMemory wm);
    void execute(InternalKnowledgeRuntime kruntime);

    PropagationEntry getNext();
    void setNext(PropagationEntry next);

    boolean isMarshallable();

    boolean requiresImmediateFlushing();

    abstract class AbstractPropagationEntry implements PropagationEntry {
        private PropagationEntry next;

        public void setNext(PropagationEntry next) {
            this.next = next;
        }

        public PropagationEntry getNext() {
            return next;
        }

        @Override
        public boolean isMarshallable() {
            return false;
        }

        @Override
        public boolean requiresImmediateFlushing() {
            return false;
        }

        @Override
        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((InternalWorkingMemoryEntryPoint) kruntime).getInternalWorkingMemory() );
        }
    }

    class Insert extends AbstractPropagationEntry {
        private static final transient ObjectTypeNode.ExpireJob job = new ObjectTypeNode.ExpireJob();

        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final InternalWorkingMemory workingMemory;
        private final ObjectTypeConf objectTypeConf;
        private final boolean isEvent;
        private final long insertionTime;

        public Insert( InternalFactHandle handle, PropagationContext context, InternalWorkingMemory workingMemory, ObjectTypeConf objectTypeConf) {
            this.handle = handle;
            this.context = context;
            this.workingMemory = workingMemory;
            this.objectTypeConf = objectTypeConf;
            this.isEvent = objectTypeConf.isEvent();
            this.insertionTime = isEvent ? workingMemory.getTimerService().getCurrentTime() : 0L;
        }

        public void execute(InternalWorkingMemory wm) {
            for ( ObjectTypeNode otn : objectTypeConf.getObjectTypeNodes() ) {
                otn.propagateAssert( handle, context, wm );
                if (isEvent) {
                    scheduleExpiration( otn, otn.getExpirationOffset() );
                }
            }

            if (isEvent && objectTypeConf.getConcreteObjectTypeNode() == null) {
                scheduleExpiration( null, ( (ClassObjectTypeConf) objectTypeConf ).getExpirationOffset() );
            }
        }

        private void scheduleExpiration( ObjectTypeNode otn, long expirationOffset ) {
            if ( expirationOffset < 0 || expirationOffset == Long.MAX_VALUE || context.getReaderContext() != null ) {
                return;
            }

            // DROOLS-455 the calculation of the effectiveEnd may overflow and become negative
            EventFactHandle eventFactHandle = (EventFactHandle) handle;
            long effectiveEnd = eventFactHandle.getEndTimestamp() + expirationOffset;
            long nextTimestamp = Math.max( insertionTime,
                                           effectiveEnd >= 0 ? effectiveEnd : Long.MAX_VALUE );
            JobContext jobctx = new ObjectTypeNode.ExpireJobContext( new WorkingMemoryReteExpireAction( (EventFactHandle) handle, otn ),
                                                                     workingMemory );

            if (nextTimestamp < workingMemory.getTimerService().getCurrentTime()) {
                job.execute( jobctx );
            } else {
                JobHandle jobHandle = workingMemory.getTimerService()
                                                   .scheduleJob( job,
                                                                 jobctx,
                                                                 new PointInTimeTrigger( nextTimestamp, null, null ) );
                jobctx.setJobHandle( jobHandle );
                eventFactHandle.addJob( jobHandle );
            }
        }

        @Override
        public String toString() {
            return "Insert of " + handle.getObject();
        }
    }

    class Update extends AbstractPropagationEntry {
        private final EntryPointNode epn;
        private final InternalFactHandle handle;
        private final PropagationContext context;
        private final ObjectTypeConf objectTypeConf;

        public Update(EntryPointNode epn, InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf) {
            this.epn = epn;
            this.handle = handle;
            this.context = context;
            this.objectTypeConf = objectTypeConf;
        }

        public void execute(InternalWorkingMemory wm) {
            epn.propagateModify(handle, context, objectTypeConf, wm);
        }

        @Override
        public String toString() {
            return "Update of " + handle.getObject();
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
        public String toString() {
            return "Delete of " + handle.getObject();
        }
    }
}
