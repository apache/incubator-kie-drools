/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.time.impl;

import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.TimerServiceFactory;
import org.drools.core.time.Trigger;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import static org.junit.Assert.assertEquals;

public class JDKTimerServiceTest {
    
    @Test
    public void testSingleExecutionJob() throws Exception {
        SessionConfiguration config = new SessionConfiguration();
        config.setClockType(ClockType.REALTIME_CLOCK);
        TimerService timeService = TimerServiceFactory.getTimerService( config );
        Trigger trigger = new DelayedTrigger( 100 );
        HelloWorldJobContext ctx = new HelloWorldJobContext( "hello world", timeService);
        timeService.scheduleJob( new HelloWorldJob(), ctx,  trigger);
        Thread.sleep( 500 );
        timeService.shutdown();
        assertEquals( 1, ctx.getList().size() );
    }
    
    @Test
    public void testRepeatedExecutionJob() throws Exception {
        SessionConfiguration config = new SessionConfiguration();
        config.setClockType(ClockType.REALTIME_CLOCK);
        TimerService timeService = TimerServiceFactory.getTimerService( config );
        Trigger trigger = new DelayedTrigger(  new long[] { 100, 100, 100} );
        HelloWorldJobContext ctx = new HelloWorldJobContext( "hello world", timeService);
        timeService.scheduleJob( new HelloWorldJob(), ctx,  trigger);
        Thread.sleep( 500 );
        timeService.shutdown();
        assertEquals( 3, ctx.getList().size() );
    }
        
    
    @Test
    public void testRepeatedExecutionJobWithRemove() throws Exception {
        SessionConfiguration config = new SessionConfiguration();
        config.setClockType(ClockType.REALTIME_CLOCK);
        TimerService timeService = TimerServiceFactory.getTimerService( config );
        Trigger trigger = new DelayedTrigger(  new long[] {100, 100, 100, 100, 100, 100, 100, 100} ); 
        HelloWorldJobContext ctx = new HelloWorldJobContext( "hello world", timeService);
        ctx.setLimit( 3 );
        timeService.scheduleJob( new HelloWorldJob(), ctx,  trigger);
        Thread.sleep( 1000 );
        timeService.shutdown();
        assertEquals( 5, ctx.getList().size() );
    }

    public static class HelloWorldJob implements Job {
        public void execute(JobContext c) {
            HelloWorldJobContext ctx = (HelloWorldJobContext) c;
            int counter = ctx.increaseCounter();
            if ( counter > 3 ) {
                ctx.timeService.removeJob( ctx.getJobHandle() );
            }
            ctx.getList().add( ((HelloWorldJobContext)ctx).getMessage() + " : " + counter);
        }
    }

    public static class HelloWorldJobContext implements JobContext {
        private String message;
        private  TimerService timeService;
        private JobHandle jobHandle;

        private List list;

        private int counter;
        private int limit;

        public HelloWorldJobContext(String message, TimerService timeService) {
            this.message = message;
            this.timeService = timeService;
            this.list = new ArrayList();
        }

        public String getMessage() {
            return this.message;
        }

        public int increaseCounter() {
            return this.counter++;
        }

        public JobHandle getJobHandle() {
            return this.jobHandle;
        }

        public void setJobHandle(JobHandle jobHandle) {
            this.jobHandle = jobHandle;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public List getList() {
            return list;
        }

        @Override
        public InternalWorkingMemory getWorkingMemory() {
            return (InternalWorkingMemory) Proxy.newProxyInstance( InternalWorkingMemory.class.getClassLoader(),
                                                                   new Class[]{InternalWorkingMemory.class},
                                                                   new InvocationHandler() {
                                                                       @Override
                                                                       public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
                                                                           if (method.getName().equals( "addPropagation" )) {
                                                                               ( (PropagationEntry) args[0] ).execute( (InternalWorkingMemory)null );
                                                                           }
                                                                           return null;
                                                                       }
                                                                   } );
        }
    }

    public static class DelayedTrigger implements Trigger {
        private Stack<Date> stack;

        public DelayedTrigger(long delay) {
            this( new long[] { delay } );
        }

        public DelayedTrigger(long[] delay) {
            this.stack = new Stack<Date>();
            for( int i = delay.length-1; i >= 0; i-- ) {
                this.stack.push( new Date( new Date().getTime() + delay[i] ) );
            }
        }

        public Date hasNextFireTime() {
            return this.stack.isEmpty() ? null : this.stack.peek();
        }
        
        public Date nextFireTime() {
            return this.stack.isEmpty() ? null : this.stack.pop();
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // FIXME : not safe, since timestamps will be wrong
            this.stack = (Stack<Date>) in.readObject();
            
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // FIXME : not safe, since timestamps will be wrong
            out.writeObject( stack );
        }

    }
}
