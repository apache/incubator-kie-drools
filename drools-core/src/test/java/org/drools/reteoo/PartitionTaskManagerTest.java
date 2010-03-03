/*
 * Copyright 2008 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.reteoo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.common.InternalWorkingMemory;
import org.drools.concurrent.ExternalExecutorService;
import org.junit.Ignore;

import static org.mockito.Mockito.*;

/**
 * Test case for PartitionTaskManager
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 */
public class PartitionTaskManagerTest extends TestCase {
    private PartitionManager manager; 
    private PartitionTaskManager taskManager;
    private InternalWorkingMemory workingMemory;

    @Override
    public void setUp() {
        RuleBase rulebase = RuleBaseFactory.newRuleBase();
        workingMemory = (InternalWorkingMemory) rulebase.newStatefulSession();
        manager = new PartitionManager(workingMemory);
        taskManager = new PartitionTaskManager( manager, workingMemory );
    }

    @Override
    protected void tearDown() throws Exception {

    }

    @Ignore
    public void testEnqueueBeforeSettingExecutor() throws InterruptedException {
        final PartitionTaskManager.Action action = mock( PartitionTaskManager.Action.class );

        taskManager.enqueue( action );

        ExternalExecutorService service = new ExternalExecutorService( Executors.newSingleThreadExecutor() );
        // set the pool
        manager.setPool( service );  

        service.waitUntilEmpty();
        
        // check expectations
        verify( action ).execute(workingMemory);
    }

    @Ignore
    public void testFireCorrectly() throws InterruptedException {
        // creates a mock action
        final PartitionTaskManager.Action action = mock( PartitionTaskManager.Action.class );
        
        ExternalExecutorService service = new ExternalExecutorService( Executors.newSingleThreadExecutor() );
        // set the pool
        manager.setPool( service ); 
        
        // fire scenario
        taskManager.enqueue( action );
        
        // executes all pending actions using current thread
        service.waitUntilEmpty();
        
        // check expectations
        verify( action ).execute(workingMemory);
    }

    @Ignore
    public void FIXME_testActionCallbacks() throws InterruptedException {
        // creates a mock action
        final PartitionTaskManager.Action action = mock( PartitionTaskManager.Action.class );
        
        // enqueue before pool
        taskManager.enqueue( action );
        taskManager.enqueue( action );

        // TODO: implement a deterministic executor service for testing..
        ExecutorService pool = Executors.newSingleThreadExecutor();
        ExternalExecutorService service = new ExternalExecutorService( pool );
        // set the pool
        manager.setPool( service ); 
        
        // enqueue after setting the pool
        taskManager.enqueue( action );
        taskManager.enqueue( action );
        taskManager.enqueue( action );
        
        // executes all pending actions using current thread
        service.waitUntilEmpty();
        pool.shutdown();
        
        // check expectations
        verify( action, times(5) ).execute(workingMemory);
    }

}
