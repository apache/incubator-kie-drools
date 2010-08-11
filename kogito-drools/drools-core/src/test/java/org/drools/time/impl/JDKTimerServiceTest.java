/**
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

package org.drools.time.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import junit.framework.TestCase;

import org.drools.ClockType;
import org.drools.SessionConfiguration;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.TimerService;
import org.drools.time.TimerServiceFactory;
import org.drools.time.Trigger;

public class JDKTimerServiceTest extends TestCase {
    
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
        
    
	public void testRepeatedExecutionJobWithRemove() throws Exception {
    	SessionConfiguration config = new SessionConfiguration();
    	config.setClockType(ClockType.REALTIME_CLOCK);
        TimerService timeService = TimerServiceFactory.getTimerService( config ); 
		Trigger trigger = new DelayedTrigger( new long[] { 100, 100, 100, 100, 100 } );
		HelloWorldJobContext ctx = new HelloWorldJobContext( "hello world", timeService);
		ctx.setLimit( 3 );
		timeService.scheduleJob( new HelloWorldJob(), ctx,  trigger);		
		Thread.sleep( 1000 );
        timeService.shutdown();
		assertEquals( 4, ctx.getList().size() );
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
	    
	    
	}
	
	public static class DelayedTrigger implements Trigger {
	    private Stack<Date> stack;
	    
	    public DelayedTrigger(long delay) {
	        this( new long[] { delay } );
	    }
	    
        public DelayedTrigger(long[] delay) {
            this.stack = new Stack<Date>();
            for( int i = delay.length-1; i >= 0; i-- ) {
                this.stack.push( new Date( delay[i] ) );
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
