package org.drools.common;

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

    private void sleep() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
