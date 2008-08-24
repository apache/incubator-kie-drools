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

import java.util.concurrent.TimeUnit;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.common.InternalWorkingMemory;

/**
 * Test case for PartitionTaskManager
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 */
public class PartitionTaskManagerTest extends TestCase {
    private MockAction action;
    private PartitionTaskManager manager;

    @Override
    public void setUp() {
        RuleBase rulebase = RuleBaseFactory.newRuleBase();
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) rulebase.newStatefulSession();
        action = new MockAction();
        manager = new PartitionTaskManager( workingMemory );
    }

    @Override
    protected void tearDown() throws Exception {

    }

    public void testStartStopService() throws InterruptedException {
        assertFalse( manager.isRunning() );
        manager.startService();
        Thread.sleep( 1000 );
        assertTrue( manager.isRunning() );
        manager.stopService();
        Thread.sleep( 1000 );
        assertFalse( manager.isRunning() );
    }

    public void testNodeCallbacks() throws InterruptedException {
        // should be possible to enqueue before starting the service,
        // even if that should never happen
        manager.enqueue( action );
        manager.startService();
        manager.enqueue( action );
        // give the engine some time
        Thread.sleep( 1000 ); 
        assertTrue( manager.stopService( 10, TimeUnit.SECONDS ) );
        assertEquals( 2, action.getCallbackCounter() );
        // should be possible to enqueue after the stop,
        // but callback must not be executed
        manager.enqueue( action );
        manager.enqueue( action );
        manager.enqueue( action );
        // making sure the service is not processing the nodes
        Thread.sleep( 1000 );
        assertEquals( 2, action.getCallbackCounter() );
        // restarting service
        manager.startService();
        // making sure the service had time to process the nodes
        Thread.sleep( 1000 );
        assertTrue( manager.stopService( 10, TimeUnit.SECONDS ) );
        assertEquals( 5, action.getCallbackCounter() );
    }

    public static class MockAction implements PartitionTaskManager.Action {
        private volatile long callbackCounter = 0;

        public synchronized long getCallbackCounter() {
            return this.callbackCounter;
        }

        public void execute( InternalWorkingMemory workingMemory ) {
            synchronized( this ) {
                callbackCounter++;
            }
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
