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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.spi.PropagationContext;

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
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }
    }

    class Insert extends AbstractPropagationEntry {
        private final ObjectTypeNode[] otns;
        private final InternalFactHandle handle;
        private final PropagationContext context;

        public Insert(ObjectTypeNode[] otns, InternalFactHandle handle, PropagationContext context) {
            this.otns = otns;
            this.handle = handle;
            this.context = context;
        }

        public void execute(InternalWorkingMemory wm) {
            for (int i = 0, length = otns.length; i < length; i++ ) {
                otns[i].propagateAssert(handle, context, wm);
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
