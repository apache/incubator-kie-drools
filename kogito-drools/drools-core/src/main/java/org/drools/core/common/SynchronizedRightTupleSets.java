package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;

/**
 * Wraps some methods, to make them thread safe
 */
public class SynchronizedRightTupleSets extends RightTupleSetsImpl implements RightTupleSets {
    private final Object lock = new Object();

    public int addInsert(RightTuple rightTuple) {
        synchronized (lock) {
            return super.addInsert(rightTuple);
        }
    }

    public int addDelete(RightTuple rightTuple) {
        synchronized (lock) {
            return super.addDelete(rightTuple);
        }
    }


    public int addUpdate(RightTuple rightTuple) {
        synchronized (lock) {
            return super.addUpdate(rightTuple);
        }
    }

    public int removeInsert(RightTuple rightTuple) {
        synchronized (lock) {
            return super.removeInsert(rightTuple);
        }
    }

    public int removeDelete(RightTuple rightTuple) {
        synchronized (lock) {
            return super.removeDelete(rightTuple);
        }
    }

    public int removeUpdate(RightTuple rightTuple) {
        synchronized (lock) {
            return super.removeUpdate(rightTuple);
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
