package org.drools.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 */
public class UpgradableReentrantReadWriteLock {

    private final boolean shouldTryAtomicUpgrade;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Map<Long, IntegerRef> readCounters = new ConcurrentHashMap<Long, IntegerRef>();
    private final Map<Long, IntegerRef> upgradedReadCounters = new ConcurrentHashMap<Long, IntegerRef>();

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
        if (increaseUpgradedReadCounter()) return;

        if (shouldTryAtomicUpgrade && tryingLockUpgrade.get() && readCounters.get(Thread.currentThread().getId()) == null) {
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
        increaseReadCounter();
    }

    public void readUnlock() {
        if (decreaseUpgradedReadCounter()) return;
        lock.readLock().unlock();
        decreaseReadCounters();
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

        IntegerRef readHoldCount = readCounters.get(Thread.currentThread().getId());

        // Check if it's upgrading a read lock
        if (readHoldCount != null) {
            upgradedReadCounters.put(Thread.currentThread().getId(), new IntegerRef(readHoldCount.get()));
            if (shouldTryAtomicUpgrade && tryingLockUpgrade.compareAndSet(false, true)) {
                atomicLockUpgrade(readHoldCount.get());
            } else {
                // this lock shouldn't work atomically or there's another thread trying to atomically upgrade
                // its lock, so release all the read locks of this thread before to acquire a write one
                for (int i = readHoldCount.get(); i > 0; i--) lock.readLock().unlock();
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
            IntegerRef upgradedReadCount = upgradedReadCounters.get(Thread.currentThread().getId());
            if (upgradedReadCount != null) {
                for (int i = upgradedReadCount.get(); i > 0; i--) lock.readLock().lock();
                upgradedReadCounters.remove(Thread.currentThread().getId());
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

    private void increaseReadCounter() {
        long threadId = Thread.currentThread().getId();
        IntegerRef readCount = readCounters.get(threadId);
        if (readCount != null) {
            readCount.inc();
        } else {
            readCounters.put(threadId, new IntegerRef());
        }

    }

    private boolean increaseUpgradedReadCounter() {
        // increase the read locks counter if it belongs to a thread that upgraded its lock
        long threadId = Thread.currentThread().getId();
        IntegerRef upgradedReadCount = upgradedReadCounters.get(threadId);
        if (upgradedReadCount == null) return false;
        upgradedReadCount.inc();
        return true;
    }

    private void decreaseReadCounters() {
        long threadId = Thread.currentThread().getId();
        IntegerRef readCount = readCounters.get(threadId);
        if (readCount != null) {
            if (readCount.get() < 2) {
                readCounters.remove(threadId);
            } else {
                readCount.dec();
            }
        }
    }

    private boolean decreaseUpgradedReadCounter() {
        // decrease the read locks counter if it belongs to a thread that upgraded its lock
        long threadId = Thread.currentThread().getId();
        IntegerRef upgradedReadCount = upgradedReadCounters.get(threadId);
        if (upgradedReadCount == null) return false;
        if (upgradedReadCount.get() < 2) {
            upgradedReadCounters.remove(threadId);
        } else {
            upgradedReadCount.dec();
        }
        // this read lock has been upgraded in a write one, so no need to unlock it
        return true;
    }

    private static class IntegerRef {
        private int i = 1;
        public IntegerRef() { }
        public IntegerRef(int i) { this.i = i; }
        private int get() { return i; }
        private void inc() { i++; }
        private void dec() { i--; }
    }
}
