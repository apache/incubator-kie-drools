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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.h2.tools.Server;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.test.util.ExecutorTestUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.query.QueryContext;
import org.kie.test.util.db.PoolingDataSourceWrapper;


public class DBUnavilabilityExecutorTest{
    
    public static final Map<String, Object> cachedEntities = new HashMap<String, Object>();
    private ExecutorService executorService;
    
    
    private EntityManagerFactory emf = null;    
 
	private PoolingDataSourceWrapper pds;
	
	private static Server h2Server;
	
	@BeforeClass
    public static void createDBServer() throws Exception {
	    h2Server = Server.createTcpServer(new String[] { "-tcpPort", "9123" });
	    h2Server.start();
    }
	
	@AfterClass
	public static void stopDBServer() {
	    if (h2Server.isRunning(false)) {
	        h2Server.stop();
	    }
	}
	
    @Before
    public void setUp() {
        Properties dsProps = ExecutorTestUtil.getDatasourceProperties();
        dsProps.setProperty("url", "jdbc:h2:tcp://localhost:9123/target/jbpm-exec-test;MVCC=TRUE");
        pds = PersistenceUtil.setupPoolingDataSource(dsProps, "jdbc/jbpm-ds");        
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        
        executorService.setThreadPoolSize(1);
        executorService.setInterval(3);
        
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
    
    @Test(timeout=60000)
    public void reoccurringExecutionTest() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(2);
        
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());

        executorService.scheduleRequest("org.jbpm.executor.commands.ReoccurringPrintOutCommand", ctxCMD);
        // wait for the first two jobs to successfully run
        countDownListener.waitTillCompleted();
        // stop db
        h2Server.stop();
        // we need to use sleep here to allow the next job fail at connection to stopped db
        // listeners are not available until the job is actually fetched from db
        Thread.sleep(3000);
        //reset listeners and start db
        countDownListener.reset(2);
        h2Server.start();
        // wait for additional two jobs to run as they are reoccuring
        countDownListener.waitTillCompleted();        
        
        List<RequestInfo> rescheduled = executorService.getRequestsByBusinessKey((String)ctxCMD.getData("businessKey"), Arrays.asList(STATUS.QUEUED), new QueryContext());
        assertEquals(1, rescheduled.size());  
        // finally cancel reoccuring job so it won't cause false resuts
        executorService.cancelRequest(rescheduled.get(0).getId());
        // lastly check that there are exactly 4 jobs executed
        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(4, executedRequests.size());


    }
}