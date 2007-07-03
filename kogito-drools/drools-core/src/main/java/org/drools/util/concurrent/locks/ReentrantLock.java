/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package org.drools.util.concurrent.locks;

import java.io.Serializable;
import java.util.Collection;

/**
 *  This is a stripped down version of jdk1.5 ReentrantLock. 
 *  All the condition and wait stuff has been removed
 *
 * @since 1.5
 * @author Doug Lea
 * @author Dawid Kurzyniec
 */
public class ReentrantLock
    implements
    Lock,
    java.io.Serializable {
    private static final long serialVersionUID = 400L;

    private final NonfairSync sync;

    final static class NonfairSync
        implements
        Serializable {
        private static final long  serialVersionUID = 400L;

        protected transient Thread owner_           = null;
        protected transient int    holds_           = 0;

        final void incHolds() {
            final int nextHolds = ++this.holds_;
            if ( nextHolds < 0 ) {
                throw new Error( "Maximum lock count exceeded" );
            }
            this.holds_ = nextHolds;
        }

        public boolean tryLock() {
            final Thread caller = Thread.currentThread();
            synchronized ( this ) {
                if ( this.owner_ == null ) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return true;
                } else if ( caller == this.owner_ ) {
                    incHolds();
                    return true;
                }
            }
            return false;
        }

        public synchronized int getHoldCount() {
            return isHeldByCurrentThread() ? this.holds_ : 0;
        }

        public synchronized boolean isHeldByCurrentThread() {
            return this.holds_ > 0 && Thread.currentThread() == this.owner_;
        }

        public synchronized boolean isLocked() {
            return this.owner_ != null;
        }

        protected synchronized Thread getOwner() {
            return this.owner_;
        }

        public boolean hasQueuedThreads() {
            throw new UnsupportedOperationException( "Use FAIR version" );
        }

        public int getQueueLength() {
            throw new UnsupportedOperationException( "Use FAIR version" );
        }

        public Collection getQueuedThreads() {
            throw new UnsupportedOperationException( "Use FAIR version" );
        }

        public boolean isQueued(final Thread thread) {
            throw new UnsupportedOperationException( "Use FAIR version" );
        }

        public void lock() {
            final Thread caller = Thread.currentThread();
            synchronized ( this ) {
                if ( this.owner_ == null ) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return;
                } else if ( caller == this.owner_ ) {
                    incHolds();
                    return;
                } else {
                    boolean wasInterrupted = Thread.interrupted();
                    try {
                        while ( true ) {
                            try {
                                wait();
                            } catch ( final InterruptedException e ) {
                                wasInterrupted = true;
                                // no need to notify; if we were signalled, we
                                // will act as signalled, ignoring the
                                // interruption
                            }
                            if ( this.owner_ == null ) {
                                this.owner_ = caller;
                                this.holds_ = 1;
                                return;
                            }
                        }
                    } finally {
                        if ( wasInterrupted ) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }

        public void lockInterruptibly() throws InterruptedException {
            if ( Thread.interrupted() ) {
                throw new InterruptedException();
            }
            final Thread caller = Thread.currentThread();
            synchronized ( this ) {
                if ( this.owner_ == null ) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return;
                } else if ( caller == this.owner_ ) {
                    incHolds();
                    return;
                } else {
                    try {
                        do {
                            wait();
                        } while ( this.owner_ != null );
                        this.owner_ = caller;
                        this.holds_ = 1;
                        return;
                    } catch ( final InterruptedException ex ) {
                        if ( this.owner_ == null ) {
                            notify();
                        }
                        throw ex;
                    }
                }
            }
        }

        public synchronized void unlock() {
            if ( Thread.currentThread() != this.owner_ ) {
                throw new IllegalMonitorStateException( "Not owner" );
            }

            if ( --this.holds_ == 0 ) {
                this.owner_ = null;
                notify();
            }
        }
    }

    /**
     * Creates an instance of <tt>ReentrantLock</tt>.
     * This is equivalent to using <tt>ReentrantLock(false)</tt>.
     */
    public ReentrantLock() {
        this.sync = new NonfairSync();
    }

    /**
     * Acquires the lock.
     *
     * <p>Acquires the lock if it is not held by another thread and returns
     * immediately, setting the lock hold count to one.
     *
     * <p>If the current thread
     * already holds the lock then the hold count is incremented by one and
     * the method returns immediately.
     *
     * <p>If the lock is held by another thread then the
     * current thread becomes disabled for thread scheduling
     * purposes and lies dormant until the lock has been acquired,
     * at which time the lock hold count is set to one.
     */
    public void lock() {
        this.sync.lock();
    }

    /**
     * Acquires the lock unless the current thread is
     * {@link Thread#interrupt interrupted}.
     *
     * <p>Acquires the lock if it is not held by another thread and returns
     * immediately, setting the lock hold count to one.
     *
     * <p>If the current thread already holds this lock then the hold count
     * is incremented by one and the method returns immediately.
     *
     * <p>If the lock is held by another thread then the
     * current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of two things happens:
     *
     * <ul>
     *
     * <li>The lock is acquired by the current thread; or
     *
     * <li>Some other thread {@link Thread#interrupt interrupts} the current
     * thread.
     *
     * </ul>
     *
     * <p>If the lock is acquired by the current thread then the lock hold
     * count is set to one.
     *
     * <p>If the current thread:
     *
     * <ul>
     *
     * <li>has its interrupted status set on entry to this method; or
     *
     * <li>is {@link Thread#interrupt interrupted} while acquiring
     * the lock,
     *
     * </ul>
     *
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * <p>In this implementation, as this method is an explicit interruption
     * point, preference is
     * given to responding to the interrupt over normal or reentrant
     * acquisition of the lock.
     *
     * @throws InterruptedException if the current thread is interrupted
     */
    public void lockInterruptibly() throws InterruptedException {
        this.sync.lockInterruptibly();
    }

    /**
     * Acquires the lock only if it is not held by another thread at the time
     * of invocation.
     *
     * <p>Acquires the lock if it is not held by another thread and
     * returns immediately with the value <tt>true</tt>, setting the
     * lock hold count to one. Even when this lock has been set to use a
     * fair ordering policy, a call to <tt>tryLock()</tt> <em>will</em>
     * immediately acquire the lock if it is available, whether or not
     * other threads are currently waiting for the lock.
     * This &quot;barging&quot; behavior can be useful in certain
     * circumstances, even though it breaks fairness. If you want to honor
     * the fairness setting for this lock, then use
     * {@link #tryLock(long, TimeUnit) tryLock(0, TimeUnit.SECONDS) }
     * which is almost equivalent (it also detects interruption).
     *
     * <p> If the current thread
     * already holds this lock then the hold count is incremented by one and
     * the method returns <tt>true</tt>.
     *
     * <p>If the lock is held by another thread then this method will return
     * immediately with the value <tt>false</tt>.
     *
     * @return <tt>true</tt> if the lock was free and was acquired by the
     * current thread, or the lock was already held by the current thread; and
     * <tt>false</tt> otherwise.
     */
    public boolean tryLock() {
        return this.sync.tryLock();
    }

    /**
     * Attempts to release this lock.
     *
     * <p>If the current thread is the
     * holder of this lock then the hold count is decremented. If the
     * hold count is now zero then the lock is released.  If the
     * current thread is not the holder of this lock then {@link
     * IllegalMonitorStateException} is thrown.
     * @throws IllegalMonitorStateException if the current thread does not
     * hold this lock.
     */
    public void unlock() {
        this.sync.unlock();
    }

    /**
     * Queries the number of holds on this lock by the current thread.
     *
     * <p>A thread has a hold on a lock for each lock action that is not
     * matched by an unlock action.
     *
     * <p>The hold count information is typically only used for testing and
     * debugging purposes. For example, if a certain section of code should
     * not be entered with the lock already held then we can assert that
     * fact:
     *
     * <pre>
     * class X {
     *   ReentrantLock lock = new ReentrantLock();
     *   // ...
     *   public void m() {
     *     assert lock.getHoldCount() == 0;
     *     lock.lock();
     *     try {
     *       // ... method body
     *     } finally {
     *       lock.unlock();
     *     }
     *   }
     * }
     * </pre>
     *
     * @return the number of holds on this lock by the current thread,
     * or zero if this lock is not held by the current thread.
     */
    public int getHoldCount() {
        return this.sync.getHoldCount();
    }

    /**
     * Queries if this lock is held by the current thread.
     *
     * <p>Analogous to the {@link Thread#holdsLock} method for built-in
     * monitor locks, this method is typically used for debugging and
     * testing. For example, a method that should only be called while
     * a lock is held can assert that this is the case:
     *
     * <pre>
     * class X {
     *   ReentrantLock lock = new ReentrantLock();
     *   // ...
     *
     *   public void m() {
     *       assert lock.isHeldByCurrentThread();
     *       // ... method body
     *   }
     * }
     * </pre>
     *
     * <p>It can also be used to ensure that a reentrant lock is used
     * in a non-reentrant manner, for example:
     *
     * <pre>
     * class X {
     *   ReentrantLock lock = new ReentrantLock();
     *   // ...
     *
     *   public void m() {
     *       assert !lock.isHeldByCurrentThread();
     *       lock.lock();
     *       try {
     *           // ... method body
     *       } finally {
     *           lock.unlock();
     *       }
     *   }
     * }
     * </pre>
     * @return <tt>true</tt> if current thread holds this lock and
     * <tt>false</tt> otherwise.
     */
    public boolean isHeldByCurrentThread() {
        return this.sync.isHeldByCurrentThread();
    }

    /**
     * Queries if this lock is held by any thread. This method is
     * designed for use in monitoring of the system state,
     * not for synchronization control.
     * @return <tt>true</tt> if any thread holds this lock and
     * <tt>false</tt> otherwise.
     */
    public boolean isLocked() {
        return this.sync.isLocked();
    }

    /**
     * <tt>null</tt> if not owned. When this method is called by a
     * thread that is not the owner, the return value reflects a
     * best-effort approximation of current lock status. For example,
     * the owner may be momentarily <tt>null</tt> even if there are
     * threads trying to acquire the lock but have not yet done so.
     * This method is designed to facilitate construction of
     * subclasses that provide more extensive lock monitoring
     * facilities.
     *
     * @return the owner, or <tt>null</tt> if not owned
     */
    protected Thread getOwner() {
        return this.sync.getOwner();
    }

    /**
     * Queries whether any threads are waiting to acquire this lock. Note that
     * because cancellations may occur at any time, a <tt>true</tt>
     * return does not guarantee that any other thread will ever
     * acquire this lock.  This method is designed primarily for use in
     * monitoring of the system state.
     *
     * @return true if there may be other threads waiting to acquire
     * the lock.
     */
    public final boolean hasQueuedThreads() {
        return this.sync.hasQueuedThreads();
    }

    /**
     * Queries whether the given thread is waiting to acquire this
     * lock. Note that because cancellations may occur at any time, a
     * <tt>true</tt> return does not guarantee that this thread
     * will ever acquire this lock.  This method is designed primarily for use
     * in monitoring of the system state.
     *
     * @param thread the thread
     * @return true if the given thread is queued waiting for this lock.
     * @throws NullPointerException if thread is null
     */
    public final boolean hasQueuedThread(final Thread thread) {
        return this.sync.isQueued( thread );
    }

    /**
     * Returns an estimate of the number of threads waiting to
     * acquire this lock.  The value is only an estimate because the number of
     * threads may change dynamically while this method traverses
     * internal data structures.  This method is designed for use in
     * monitoring of the system state, not for synchronization
     * control.
     * @return the estimated number of threads waiting for this lock
     */
    public final int getQueueLength() {
        return this.sync.getQueueLength();
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire this lock.  Because the actual set of threads may change
     * dynamically while constructing this result, the returned
     * collection is only a best-effort estimate.  The elements of the
     * returned collection are in no particular order.  This method is
     * designed to facilitate construction of subclasses that provide
     * more extensive monitoring facilities.
     * @return the collection of threads
     */
    protected Collection getQueuedThreads() {
        return this.sync.getQueuedThreads();
    }

    /**
     * Returns a string identifying this lock, as well as its lock
     * state.  The state, in brackets, includes either the String
     * &quot;Unlocked&quot; or the String &quot;Locked by&quot;
     * followed by the {@link Thread#getName} of the owning thread.
     * @return a string identifying this lock, as well as its lock state.
     */
    public String toString() {
        final Thread o = getOwner();
        return super.toString() + ((o == null) ? "[Unlocked]" : "[Locked by thread " + o.getName() + "]");
    }
}
