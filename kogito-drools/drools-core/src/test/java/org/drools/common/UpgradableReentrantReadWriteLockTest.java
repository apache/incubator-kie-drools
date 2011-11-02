package org.drools.common;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class UpgradableReentrantReadWriteLockTest {

    @Test(timeout=10000)
    public void testLock() {
        final UpgradableReentrantReadWriteLock lock = new UpgradableReentrantReadWriteLock(true);
        lock.readLock();
        new Thread(new Runnable() {
            public void run() {
                lock.readLock();
                lock.writeLock();
                sleep();
                sleep();
                lock.readUnlock();
                lock.writeUnlock();
            }
        }).start();
        sleep();
        lock.writeLock();
        sleep();
        sleep();
        lock.writeUnlock();
        lock.readUnlock();
    }

    @Test(timeout=10000)
    public void testLock2() {
        final UpgradableReentrantReadWriteLock lock = new UpgradableReentrantReadWriteLock(true);
        lock.readLock();
        new Thread(new Runnable() {
            public void run() {
                lock.readLock();
                sleep();
                sleep();
                sleep();
                lock.readUnlock();
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                lock.readLock();
                lock.readLock();
                sleep();
                lock.readUnlock();
                sleep();
                lock.readUnlock();
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                lock.readLock();
                lock.writeLock();
                sleep();
                sleep();
                lock.readUnlock();
                lock.writeUnlock();
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                lock.writeLock();
                sleep();
                lock.readLock();
                sleep();
                sleep();
                lock.readUnlock();
                sleep();
                lock.writeUnlock();
            }
        }).start();
        sleep();
        lock.writeLock();
        sleep();
        sleep();
        lock.writeUnlock();
        lock.readUnlock();
    }
    
    @Test(timeout=10000)
    @Ignore("Failing with atomic upgrade")
    public void testLock3() throws InterruptedException {
        final int THREADS=10;
        final UpgradableReentrantReadWriteLock lock = new UpgradableReentrantReadWriteLock(true);
        final CyclicBarrier sync = new CyclicBarrier( THREADS );
        final AtomicBoolean success = new AtomicBoolean( true );
        
        Runnable r1 = new Runnable() {
            public void run() {
                try {
                    lock.readLock();
                    sync.await();
                    lock.writeLock();
                    lock.writeUnlock();
                    lock.readUnlock();
                    System.out.println(Thread.currentThread().getName()+" succeeded!");
                } catch ( Exception e ) {
                    e.printStackTrace();
                    success.set( false );
                    System.out.println(Thread.currentThread().getName()+" failed!");
                }
            }
        };
        Runnable r2 = new Runnable() {
            public void run() {
                try {
                    sync.await();
                    lock.writeLock();
                    lock.writeUnlock();
                    System.out.println(Thread.currentThread().getName()+" succeeded!");
                } catch ( Exception e ) {
                    e.printStackTrace();
                    success.set( false );
                    System.out.println(Thread.currentThread().getName()+" failed!");
                }
            }
        };
        
        Thread[] threads = new Thread[THREADS];
        for( int i = 0; i < THREADS; i++ ) {
            threads[i] = new Thread( i % 2 == 0 ? r1 : r2 , "T-"+i );
            threads[i].start();
        }
        for( int i = 0; i < THREADS; i++ ) {
            threads[i].join();
        }
        Assert.assertTrue( success.get() );
    }

    private void sleep() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
