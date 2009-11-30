package org.drools.time.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.drools.ClockType;
import org.drools.time.TimerServiceFactory;
import org.drools.time.impl.JDKTimerServiceTest.HelloWorldJob;
import org.drools.time.impl.JDKTimerServiceTest.HelloWorldJobContext;


public class CronJobTest extends TestCase {
    public void testCronTriggerJob() throws Exception {
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) TimerServiceFactory.getTimerService( ClockType.PSEUDO_CLOCK ); 
        
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
        Date date = df.parse( "2009-01-01T00:00:00.000-0000" );
        System.out.println( "current time " + date );
        
        timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
        
        CronTrigger trigger = new CronTrigger(date.getTime(), null, null, "15 * * * * ?");
        
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
