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

package org.drools.time.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.ClockType;
import org.drools.SessionConfiguration;
import org.drools.time.TimerServiceFactory;
import org.drools.time.impl.JDKTimerServiceTest.HelloWorldJob;
import org.drools.time.impl.JDKTimerServiceTest.HelloWorldJobContext;


public class CronJobTest {
    @Test
    public void testCronTriggerJob() throws Exception {
        SessionConfiguration config = new SessionConfiguration();
        config.setClockType(ClockType.PSEUDO_CLOCK);
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) TimerServiceFactory.getTimerService( config );
        
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
        Date date = df.parse( "2009-01-01T00:00:00.000-0000" );
        
        timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
        
        CronTrigger trigger = new CronTrigger(date.getTime(), null, null, -1, "15 * * * * ?", null, null);
        
        HelloWorldJobContext ctx = new HelloWorldJobContext( "hello world", timeService);
        timeService.scheduleJob( new HelloWorldJob(), ctx,  trigger);

        assertEquals( 0, ctx.getList().size() );
                
        timeService.advanceTime( 10, TimeUnit.SECONDS );
        assertEquals( 0, ctx.getList().size() );
                 
        timeService.advanceTime( 10, TimeUnit.SECONDS );
        assertEquals( 1, ctx.getList().size() );
        
        timeService.advanceTime( 30, TimeUnit.SECONDS );
        assertEquals( 1, ctx.getList().size() );
        
        timeService.advanceTime( 30, TimeUnit.SECONDS );
        assertEquals( 2, ctx.getList().size() );
    }
}
