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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class InMemorySimpleExecutorTest extends BasicExecutorBaseTest{
    
    
    @Before
    public void setUp() {

        executorService = ExecutorServiceFactory.newExecutorService();
        
        executorService.init();
        super.setUp();
    }
    
    @After
    public void tearDown() {
        super.tearDown();
        executorService.destroy();
    }
   
    @Override
    @Test
    @Ignore("It's only for JPA based as it removes data from db")
    public void cleanupLogExecutionTest() throws InterruptedException {
    	
    }
    
    @Override
    @Test
    @Ignore("It's only for JPA based as it removes data from db")
    public void reoccurringExecutionTest() throws InterruptedException {
    	
    }

    @Override
    @Test
    @Ignore("It's only for JPA based as it in memory does not care about delays")
    public void testCustomConstantRequestRetry() throws InterruptedException {
        super.testCustomConstantRequestRetry();
    }

    @Override
    @Test
    @Ignore("It's only for JPA based as it in memory does not care about delays")
    public void testCustomIncrementingRequestRetry() throws InterruptedException {
        super.testCustomIncrementingRequestRetry();
    }

    @Test
    @Ignore("It's only for JPA based as it in memory does not care about delays")
    public void testCustomIncrementingRequestRetrySpecialValues() throws InterruptedException {        
        super.testCustomIncrementingRequestRetrySpecialValues();
    }

    @Test
    @Ignore("It's only for JPA based as it in memory does not care about priorities")
    public void testPrioritizedJobsExecution() throws InterruptedException {
        super.testPrioritizedJobsExecution();
    }

    @Test
    @Ignore("It's only for JPA based as it in memory does not care about priorities")
    public void testPrioritizedJobsExecutionInvalidProrities() throws InterruptedException {
        super.testPrioritizedJobsExecutionInvalidProrities();
    }

    @Test
    @Ignore("It's only for JPA based as it in memory does not care about deployments")
    public void testProcessContextJobsExecution() throws InterruptedException {
        super.testProcessContextJobsExecution();
    }

    @Test
    @Ignore("It's only for JPA based as it in memory does not support retries")
    public void testUpdateRequestDataFromErrorState() throws InterruptedException {
        super.testUpdateRequestDataFromErrorState();
    }
    
}