package org.jbpm.test.listener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jbpm.executor.AsynchronousJobEvent;
import org.jbpm.executor.AsynchronousJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CountDownAsyncJobListener implements AsynchronousJobListener {

    private static final Logger logger = LoggerFactory.getLogger(CountDownAsyncJobListener.class);
    private CountDownLatch latch;
    
    public CountDownAsyncJobListener(int threads) {
        this.latch = new CountDownLatch(threads);
    }
    
    public void waitTillCompleted() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all async jobs");
        }
    }
    
    public void waitTillCompleted(long timeOut) {
        try {
            latch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all async jobs");
        }
    }
    
    public void reset(int threads) {
        this.latch = new CountDownLatch(threads);
    }
    
    @Override
    public void beforeJobScheduled(AsynchronousJobEvent event) {

    }

    @Override
    public void afterJobScheduled(AsynchronousJobEvent event) {

    }

    @Override
    public void beforeJobExecuted(AsynchronousJobEvent event) {
    
    }

    @Override
    public void afterJobExecuted(AsynchronousJobEvent event) {
        latch.countDown();
    }

    @Override
    public void beforeJobCancelled(AsynchronousJobEvent event) {

    }

    @Override
    public void afterJobCancelled(AsynchronousJobEvent event) {
        latch.countDown();
    }

}
