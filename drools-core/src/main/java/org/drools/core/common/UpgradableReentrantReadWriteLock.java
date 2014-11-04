package org.drools.core.common;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 */
public class UpgradableReentrantReadWriteLock {

    private final boolean shouldTryAtomicUpgrade;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // lockCounter[0] == readCounter;
    // lockCounter[1] == upgradedReadCounter;
    private final ThreadLocal<int[]> lockCounters = new ThreadLocal<int[]>() {
        protected int[] initialValue() {
            return new int[] {0, 0};
        }
    };

    private AtomicBoolean tryingLockUpgrade = new AtomicBoolean(false);

    // all threads except the (only) one who is trying an atomic lock upgrade must have low priority
    private final Integer lowPriotityMonitor = 42;
    private final Integer highPriorityMonitor = 43;

    public UpgradableReentrantReadWriteLock() {
        this(false);
    }

    public UpgradableReentrantReadWriteLock(boolean shouldTryAtomicUpgrade) {
        this.shouldTryAtomicUpgrade = shouldTryAtomicUpgrade;
    }

    public void readLock() {
        int[] lockCounter = lockCounters.get();

        if (increaseUpgradedReadCounter(lockCounter)) return;

        if (shouldTryAtomicUpgrade && tryingLockUpgrade.get() && lockCounter[0] == 0) {
            // if another thread is trying a lock upgrade and the current thread still doesn't hold a read one make it wait
            try {
                synchronized (lowPriotityMonitor) {
                    lowPriotityMonitor.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        lock.readLock().lock();
        increaseReadCounter(lockCounter);
    }

    public void readUnlock() {
        int[] lockCounter = lockCounters.get();
        if (decreaseUpgradedReadCounter(lockCounter)) return;
        lock.readLock().unlock();
        decreaseReadCounters(lockCounter);
        notifyUpgradingThread();
    }

    private void notifyUpgradingThread() {
        if (shouldTryAtomicUpgrade && tryingLockUpgrade.get()) {
            synchronized (highPriorityMonitor) {
                if (lock.getReadLockCount() < 2 && !lock.isWriteLocked()) {
                    // all the read locks, except the one of the thread that is trying to upgrade its lock, have been released
                    highPriorityMonitor.notifyAll();
                }
            }
        }
    }

    public void writeLock() {
        if (lock.isWriteLockedByCurrentThread()) {
            lock.writeLock().lock();
            return;
        }

        int[] lockCounter = lockCounters.get();

        // Check if it's upgrading a read lock
        if (lockCounter[0] > 0) {
            //lockCounter.upgradedReadCounter = lockCounter.readCounter;
            lockCounter[1] = lockCounter[0];
            if (shouldTryAtomicUpgrade && tryingLockUpgrade.compareAndSet(false, true)) {
                atomicLockUpgrade(lockCounter[0]);
            } else {
                // this lock shouldn't work atomically or there's another thread trying to atomically upgrade
                // its lock, so release all the read locks of this thread before to acquire a write one
                for (int i = lockCounter[0]; i > 0; i--) lock.readLock().unlock();
                notifyUpgradingThread();
                lowPriorityWriteLock();
            }
        } else {
            lowPriorityWriteLock();
        }
    }

    private void lowPriorityWriteLock() {
        if (shouldTryAtomicUpgrade && tryingLockUpgrade.get()) {
            synchronized (lowPriotityMonitor) {
                try {
                    lowPriotityMonitor.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        lock.writeLock().lock();
    }

    private void atomicLockUpgrade(int readHoldCount) {
        // release all the read locks of this thread but the last one
        for (int i = readHoldCount; i > 1; i--) lock.readLock().unlock();

        synchronized (highPriorityMonitor) {
            // if there are other read or write lock, wait until they aren't unlocked
            if (lock.getReadLockCount() > readHoldCount || lock.isWriteLocked()) {
                try {
                    highPriorityMonitor.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // there is no other thread allowed to acquire neither a read lock nor a write one
        // so I am sure this lock upgrade is done atomically
        lock.readLock().unlock();
        lock.writeLock().lock();

        tryingLockUpgrade.set(false);
        synchronized (lowPriotityMonitor) {
            lowPriotityMonitor.notifyAll();
        }
    }

    public void writeUnlock() {
        // Check if unlocking an upgraded read lock and if so downgrade it back
        if (lock.getWriteHoldCount() == 1) {
            int[] lockCounter = lockCounters.get();
            if (lockCounter[1] > 0) {
                for (int i = lockCounter[1]; i > 0; i--) {
                    lock.readLock().lock();
                }
                lockCounter[1] = 0;
            }
        }

        lock.writeLock().unlock();
        notifyUpgradingThread();
    }

    public boolean isWriteLockedByCurrentThread() {
        return lock.isWriteLockedByCurrentThread();
    }

    public int getWriteHoldCount() {
        return lock.getWriteHoldCount();
    }

    private void increaseReadCounter(int[] lockCounter) {
        lockCounter[0]++;
    }

    private boolean increaseUpgradedReadCounter(int[] lockCounter) {
        // increase the read locks counter if it belongs to a thread that upgraded its lock
        if (lockCounter[1] == 0) {
            return false;
        }
        lockCounter[1]++;
        return true;
    }

    private void decreaseReadCounters(int[] lockCounter) {
        lockCounter[0]--;
    }

    private boolean decreaseUpgradedReadCounter(int[] lockCounter) {
        // decrease the read locks counter if it belongs to a thread that upgraded its lock
        if (lockCounter[1] == 0) {
            return false;
        }
        lockCounter[1]--;
        // this read lock has been upgraded in a write one, so no need to unlock it
        return true;
    }
}
