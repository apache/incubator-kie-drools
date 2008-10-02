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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.common.InternalWorkingMemory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.DeterministicScheduler;

/**
 * Test case for PartitionTaskManager
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 */
public class PartitionTaskManagerTest extends TestCase {
    Mockery context = new Mockery();
    private PartitionTaskManager manager;
    private InternalWorkingMemory workingMemory;

    @Override
    public void setUp() {
        RuleBase rulebase = RuleBaseFactory.newRuleBase();
        workingMemory = (InternalWorkingMemory) rulebase.newStatefulSession();
        manager = new PartitionTaskManager( workingMemory );
    }

    @Override
    protected void tearDown() throws Exception {

    }

    public void testEnqueueBeforeSettingExecutor() throws InterruptedException {
        final PartitionTaskManager.Action action = context.mock( PartitionTaskManager.Action.class );
        // set expectations for the scenario
        context.checking( new Expectations() {{
            oneOf( action ).execute( workingMemory );
        }});

        manager.enqueue( action );

        // this is a jmock helper class that implements the ExecutorService interface
        DeterministicScheduler pool = new DeterministicScheduler();
        // set the pool
        manager.setPool( pool );

        // executes all pending actions using current thread
        pool.runUntilIdle();

        // check expectations
        context.assertIsSatisfied();
    }

    public void testFireCorrectly() throws InterruptedException {
        // creates a mock action
        final PartitionTaskManager.Action action = context.mock( PartitionTaskManager.Action.class );
        
        // this is a jmock helper class that implements the ExecutorService interface
        DeterministicScheduler pool = new DeterministicScheduler();
        // set the pool
        manager.setPool( pool );
        
        // set expectations for the scenario
        context.checking( new Expectations() {{
            oneOf( action ).execute( workingMemory );
        }});
        
        // fire scenario
        manager.enqueue( action );
        
        // executes all pending actions using current thread
        pool.runUntilIdle();
        
        // check expectations
        context.assertIsSatisfied();
    }

    public void testActionCallbacks() throws InterruptedException {
        // creates a mock action
        final PartitionTaskManager.Action action = context.mock( PartitionTaskManager.Action.class );
        // this is a jmock helper class that implements the ExecutorService interface
        DeterministicScheduler pool = new DeterministicScheduler();
        
        // set expectations for the scenario
        context.checking( new Expectations() {{
            exactly(5).of( action ).execute( workingMemory );
        }});
        
        // enqueue before pool
        manager.enqueue( action );
        manager.enqueue( action );

        // set the pool
        manager.setPool( pool );
        
        // enqueue after setting the pool
        manager.enqueue( action );
        manager.enqueue( action );
        manager.enqueue( action );
        
        // executes all pending actions using current thread
        pool.runUntilIdle();
        
        // check expectations
        context.assertIsSatisfied();
    }

}
