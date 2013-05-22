package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;

public class SynchronizedLeftTupleSets extends LeftTupleSetsImpl implements LeftTupleSets {
    private final Object lock = new Object();

    public void addInsert(LeftTuple leftTuple) {
        synchronized (lock) {
            super.addInsert(leftTuple);
        }
    }

    public void addDelete(LeftTuple leftTuple) {
        synchronized (lock) {
            super.addDelete(leftTuple);
        }
    }


    public void addUpdate(LeftTuple leftTuple) {
        synchronized (lock) {
            super.addUpdate(leftTuple);
        }
    }

    public void removeInsert(LeftTuple leftTuple) {
        synchronized (lock) {
            super.removeInsert(leftTuple);
        }
    }

    public void removeDelete(LeftTuple leftTuple) {
        synchronized (lock) {
            super.removeDelete(leftTuple);
        }
    }

    public void removeUpdate(LeftTuple leftTuple) {
        synchronized (lock) {
            super.removeUpdate(leftTuple);
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
