package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;

/**
 * Wraps some methods, to make them thread safe
 */
public class SynchronizedRightTupleSets extends RightTupleSetsImpl implements RightTupleSets {
    private final Object lock = new Object();

    public Object getLock() {
        return lock;
    }

    public boolean addInsert(RightTuple rightTuple) {
        synchronized (lock) {
            return super.addInsert(rightTuple);
        }
    }

    public boolean addDelete(RightTuple rightTuple) {
        synchronized (lock) {
            return super.addDelete(rightTuple);
        }
    }


    public boolean addUpdate(RightTuple rightTuple) {
        synchronized (lock) {
            return super.addUpdate(rightTuple);
        }
    }

    public void resetAll() {
        synchronized (lock) {
            super.resetAll();
        }
    }


    /**
     * The returned RightTupleSet, is not thread safe.
     * @return
     */
    public RightTupleSets takeAll() {
        synchronized (lock) {
            return super.takeAll();
        }
    }


}
