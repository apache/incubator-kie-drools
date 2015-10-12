package org.jbpm.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CountDownProcessEventListener extends DefaultProcessEventListener {

    private static final Logger logger = LoggerFactory.getLogger(CountDownProcessEventListener.class);
    
    private String nodeName;
    private CountDownLatch latch;
    
    private boolean reactOnNodeTriggered = false;
    
    public CountDownProcessEventListener() {
        
    }
    
    public CountDownProcessEventListener(String nodeName, int threads) {
        this.nodeName = nodeName;
        this.latch = new CountDownLatch(threads);
    }
    
    public CountDownProcessEventListener(String nodeName, int threads, boolean reactOnNodeTriggered) {
        this.nodeName = nodeName;
        this.latch = new CountDownLatch(threads);
        this.reactOnNodeTriggered = reactOnNodeTriggered;
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        if (nodeName.equals(event.getNodeInstance().getNodeName())) {
            latch.countDown();
        }
    }
    
    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        if (reactOnNodeTriggered && nodeName.equals(event.getNodeInstance().getNodeName())) {
            latch.countDown();
        }
    }

    public void waitTillCompleted() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers for node {}", nodeName);
        }
    }
    
    public void waitTillCompleted(long timeOut) {
        try {
            latch.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.debug("Interrputed thread while waiting for all triggers for node {}", nodeName);
        }
    }
    
    public void reset(int threads) {
        this.latch = new CountDownLatch(threads);
    }
    
    public void reset(String nodeName, int threads) {
        this.nodeName = nodeName;
        this.latch = new CountDownLatch(threads);
    }
}
