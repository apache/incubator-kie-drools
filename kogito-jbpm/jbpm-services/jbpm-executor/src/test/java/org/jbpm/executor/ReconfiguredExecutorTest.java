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
package org.jbpm.executor;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.runtime.query.QueryContext;


public class ReconfiguredExecutorTest {
    
	protected ExecutorService executorService;
    public static final Map<String, Object> cachedEntities = new HashMap<String, Object>();
    
	private PoolingDataSource pds;
	private EntityManagerFactory emf = null;
    
    @Before
    public void setUp() {
        pds = ExecutorTestUtil.setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.setThreadPoolSize(2);
        executorService.setInterval(3000);
        executorService.setTimeunit(TimeUnit.MILLISECONDS);
        
        executorService.init();
    }
    
    @After
    public void tearDown() {
    	executorService.clearAllRequests();
        executorService.clearAllErrors();
        
        System.clearProperty("org.kie.executor.msg.length");
    	System.clearProperty("org.kie.executor.stacktrace.length");
        executorService.destroy();
        if (emf != null) {
        	emf.close();
        }
        pds.close();
    }
    
    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        
        return countDownListener;
    }
   
    @Test
    public void simpleExcecutionTest() throws InterruptedException {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);

        countDownListener.waitTillCompleted();

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());


    }   
}