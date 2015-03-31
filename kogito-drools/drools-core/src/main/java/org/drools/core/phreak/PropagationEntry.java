package org.drools.core.phreak;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.spi.PropagationContext;

public interface PropagationEntry {

    void execute(InternalWorkingMemory wm);

    PropagationEntry getNext();
    void setNext(PropagationEntry next);

    boolean isMarshallable();

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
