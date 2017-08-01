/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.drools.persistence.api.TransactionSynchronization;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.process.ProcessInstance;

public class TrackingProcessEventListener extends DefaultProcessEventListener {

    private final int numberOfCountDownsNeeded;
    private boolean transactional = true;
    
    public TrackingProcessEventListener(int involvedThreads) { 
        this.numberOfCountDownsNeeded = involvedThreads;
        
        this.processesAbortedLatch = new CountDownLatch(involvedThreads);
        this.processesStartedLatch = new CountDownLatch(involvedThreads);
        this.processesCompletedLatch = new CountDownLatch(involvedThreads);
    }
    
    public TrackingProcessEventListener() { 
       this(1);
    }
    
    public TrackingProcessEventListener(boolean transactional) { 
        this(1);
        this.transactional = transactional;
     }
   
    private final List<String> processesStarted = new ArrayList<String>();
    private CountDownLatch processesStartedLatch;
    
    private final List<String> processesCompleted = new ArrayList<String>();
    private CountDownLatch processesCompletedLatch;
    
    private final List<String> processesAborted = new ArrayList<String>();
    private CountDownLatch processesAbortedLatch;

    private final List<String> nodesTriggered = new ArrayList<String>();
    private final ConcurrentHashMap<String, CountDownLatch> nodeTriggeredLatchMap = new ConcurrentHashMap<String, CountDownLatch>();
    private final List<String> nodesLeft = new ArrayList<String>();
    private final ConcurrentHashMap<String, CountDownLatch> nodeLeftLatchMap = new ConcurrentHashMap<String, CountDownLatch>();

    private final List<String> variablesChanged = new ArrayList<String>();

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        String nodeName = event.getNodeInstance().getNodeName();
        CountDownLatch nodeLatch = getNodeTriggeredLatch(nodeName);
        nodesTriggered.add(nodeName);
        countDown(nodeLatch);        
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        String nodeName = event.getNodeInstance().getNodeName();
        CountDownLatch nodeLatch = getNodeLeftLatch(nodeName);
        nodesLeft.add(nodeName);
        countDown(nodeLatch);        
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        if( processesStartedLatch.getCount() == 0 ) { 
            processesStartedLatch = new CountDownLatch(numberOfCountDownsNeeded);
        }        
        processesStarted.add(event.getProcessInstance().getProcessId());
        countDown(processesStartedLatch);
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        if (event.getProcessInstance().getState() == ProcessInstance.STATE_ABORTED) {
            processesAborted.add(event.getProcessInstance().getProcessId());
            if( processesAbortedLatch.getCount() == 0 ) { 
                processesAbortedLatch = new CountDownLatch(numberOfCountDownsNeeded);
            }
            countDown(processesAbortedLatch);
        } else {
            processesCompleted.add(event.getProcessInstance().getProcessId());
            if( processesCompletedLatch.getCount() == 0 ) { 
                processesCompletedLatch = new CountDownLatch(numberOfCountDownsNeeded);
            }
            countDown(processesCompletedLatch);
        }
    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        variablesChanged.add(event.getVariableId());
    }

    public List<String> getNodesTriggered() {
        return Collections.unmodifiableList(nodesTriggered);
    }

    public List<String> getNodesLeft() {
        return Collections.unmodifiableList(nodesLeft);
    }

    public List<String> getProcessesStarted() {
        return Collections.unmodifiableList(processesStarted);
    }

    public List<String> getProcessesCompleted() {
        return Collections.unmodifiableList(processesCompleted);
    }

    public List<String> getProcessesAborted() {
        return Collections.unmodifiableList(processesAborted);
    }

    public List<String> getVariablesChanged() {
        return Collections.unmodifiableList(variablesChanged);
    }

    public boolean wasNodeTriggered(String nodeName) {
        return nodesTriggered.contains(nodeName);
    }

    public boolean wasNodeLeft(String nodeName) {
        return nodesLeft.contains(nodeName);
    }

    public boolean wasProcessStarted(String processName) {
        return processesStarted.contains(processName);
    }

    public boolean wasProcessCompleted(String processName) {
        return processesCompleted.contains(processName);
    }

    public boolean wasProcessAborted(String processName) {
        return processesAborted.contains(processName);
    }

    public boolean wasVariableChanged(String variableId) {
        return variablesChanged.contains(variableId);
    }

    public boolean waitForProcessToStart(long milliseconds) throws Exception {
        return processesStartedLatch.await(milliseconds, TimeUnit.MILLISECONDS);
    }

    public boolean waitForProcessToComplete(long milliseconds) throws Exception {
        return processesCompletedLatch.await(milliseconds, TimeUnit.MILLISECONDS);
    }

    public boolean waitForProcessToAbort(long milliseconds) throws Exception {
        return processesAbortedLatch.await(milliseconds, TimeUnit.MILLISECONDS);
    }
    
    public boolean waitForNodeTobeTriggered(String nodeName, long milliseconds) throws Exception {
        CountDownLatch nodeLatch = getNodeTriggeredLatch(nodeName);
        return nodeLatch.await(milliseconds, TimeUnit.MILLISECONDS);
    }
    
    public boolean waitForNodeToBeLeft(String nodeName, long milliseconds) throws Exception {
        CountDownLatch nodeLatch = getNodeLeftLatch(nodeName);
        return nodeLatch.await(milliseconds, TimeUnit.MILLISECONDS);
    }
    
    private CountDownLatch getNodeTriggeredLatch(String nodeName) { 
        return getNodeLatch(nodeTriggeredLatchMap, nodeName);
    }
    
    private CountDownLatch getNodeLeftLatch(String nodeName) { 
        return getNodeLatch(nodeLeftLatchMap, nodeName);
    }
    
    private CountDownLatch getNodeLatch(ConcurrentHashMap<String, CountDownLatch> nodeLatchMap, String nodeName) { 
        synchronized(nodeLatchMap) { 
            CountDownLatch nodeLatch = new CountDownLatch(numberOfCountDownsNeeded);
            CountDownLatch previousLatch = nodeLatchMap.putIfAbsent(nodeName,nodeLatch); 
            if( previousLatch != null ) {  
                return previousLatch;
            }
            return nodeLatch;
        }
    }

    public void clear() {
        nodesTriggered.clear();
        nodesLeft.clear();
        processesStarted.clear();
        processesCompleted.clear();
        processesAborted.clear();
        variablesChanged.clear();
        
        processesStartedLatch = new CountDownLatch(numberOfCountDownsNeeded);
        processesAbortedLatch = new CountDownLatch(numberOfCountDownsNeeded);
        processesCompletedLatch = new CountDownLatch(numberOfCountDownsNeeded);
        nodeTriggeredLatchMap.clear();
        nodeLeftLatchMap.clear();
    }
    
    protected void countDown(final CountDownLatch latch) {
        try {
            TransactionManager tm = TransactionManagerFactory.get().newTransactionManager();
            if (transactional && tm != null && tm.getStatus() != TransactionManager.STATUS_NO_TRANSACTION
                    && tm.getStatus() != TransactionManager.STATUS_ROLLEDBACK
                    && tm.getStatus() != TransactionManager.STATUS_COMMITTED) {
                tm.registerTransactionSynchronization(new TransactionSynchronization() {
                    
                    @Override
                    public void beforeCompletion() {        
                    }
                    
                    @Override
                    public void afterCompletion(int status) {
                        latch.countDown();
                    }
                });
            } else {            
                latch.countDown();
            }
        } catch (Exception e) {
            latch.countDown();
        }
    }

}
