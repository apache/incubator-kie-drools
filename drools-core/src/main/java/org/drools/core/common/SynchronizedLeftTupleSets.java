package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;

public class SynchronizedLeftTupleSets extends LeftTupleSetsImpl implements LeftTupleSets {
    private final Object lock = new Object();

    public Object getLock() {
        return lock;
    }

    public boolean addInsert(LeftTuple leftTuple) {
        synchronized (lock) {
            return super.addInsert(leftTuple);
        }
    }

    public boolean addDelete(LeftTuple leftTuple) {
        synchronized (lock) {
            return super.addDelete(leftTuple);
        }
    }


    public boolean addUpdate(LeftTuple leftTuple) {
        synchronized (lock) {
            return super.addUpdate(leftTuple);
        }
    }

    public void resetAll() {
        synchronized (lock) {
            super.resetAll();
        }
    }


    /**
     * The returned LeftTupleSet, is not thread safe.
     * @return
     */
    public LeftTupleSets takeAll() {
        synchronized (lock) {
            return super.takeAll();
        }
    }

}
