package org.drools.examples.broker.events;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.drools.time.impl.JDKTimerService;
import org.drools.time.impl.PseudoClockScheduler;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;

public class EventFeederTest extends TestCase {
    Mockery context = new Mockery();

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testFeedPseudoClock() {
        // create the events for the test
        final Event<String> event1 = new EventImpl<String>( 1000, "one" );
        final Event<String> event2 = new EventImpl<String>( 1100, "two" );
        final Event<String> event3 = new EventImpl<String>( 1120, "three" );
        final Event<String> event4 = new EventImpl<String>( 1300, "four" );
        final Event<String> event5 = new EventImpl<String>( 1550, "five" );
        final Event<String> event6 = new EventImpl<String>( 1700, "six" );
        
        // define a jmock sequence and state machine
        final Sequence seq = context.sequence( "call sequence" );
        final States ts = context.states( "timestamp" ).startsAs( String.valueOf( event1.getTimestamp() ) );
        
        // create mock objects for the source and the receiver
        final EventSource source = context.mock( EventSource.class );
        final EventReceiver receiver = context.mock( EventReceiver.class );
        
        // create the scheduler used by drools and the feeder to be tested
        PseudoClockScheduler clock = new PseudoClockScheduler();
        EventFeeder feeder = new EventFeeder( clock, source, receiver );
        
        // create the expectations
        context.checking( new Expectations() {{
            // there is an event1, so, read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) ); 
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event1 ) );  
            oneOf( receiver ).receive( event1 ); inSequence( seq ); when( ts.is( String.valueOf( event1.getTimestamp() ) ) ); 
            then( ts.is( String.valueOf( event2.getTimestamp() ) ) );
            
            // there is an event 2, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event2 ) );  
            oneOf( receiver ).receive( event2 ); inSequence( seq ); when( ts.is( String.valueOf( event2.getTimestamp() ) ) );
            then( ts.is( String.valueOf( event3.getTimestamp() ) ) );
            
            // there is an event 3, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event3 ) );  
            oneOf( receiver ).receive( event3 ); inSequence( seq ); when( ts.is( String.valueOf( event3.getTimestamp() ) ) );
            then( ts.is( String.valueOf( event4.getTimestamp() ) ) );
            
            // there is an event 4, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event4 ) );  
            oneOf( receiver ).receive( event4 ); inSequence( seq ); when( ts.is( String.valueOf( event4.getTimestamp() ) ) ); 
            then( ts.is( String.valueOf( event5.getTimestamp() ) ) );
            
            // there is an event 5, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event5 ) );  
            oneOf( receiver ).receive( event5 ); inSequence( seq ); when( ts.is( String.valueOf( event5.getTimestamp() ) ) ); 
            then( ts.is( String.valueOf( event6.getTimestamp() ) ) );
            
            // there is an event 6, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event6 ) );  
            oneOf( receiver ).receive( event6 ); inSequence( seq ); when( ts.is( String.valueOf( event6.getTimestamp() ) ) ); 
            
            // there are no more events
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( false ) );
        }});
        
        feeder.feed();
        // advancing time step by step, so that we can test state between advances
        clock.advanceTime( event1.getTimestamp() - clock.getCurrentTime(), TimeUnit.MILLISECONDS );
        clock.advanceTime( event2.getTimestamp() - clock.getCurrentTime(), TimeUnit.MILLISECONDS );
        clock.advanceTime( event3.getTimestamp() - clock.getCurrentTime(), TimeUnit.MILLISECONDS );
        clock.advanceTime( event4.getTimestamp() - clock.getCurrentTime(), TimeUnit.MILLISECONDS );
        clock.advanceTime( event5.getTimestamp() - clock.getCurrentTime(), TimeUnit.MILLISECONDS );
        clock.advanceTime( event6.getTimestamp() - clock.getCurrentTime(), TimeUnit.MILLISECONDS );
        
        context.assertIsSatisfied();
    }

    @SuppressWarnings("static-access")
    public void testFeedRealtimeClock() {
        // create the events for the test
        long startTime = System.currentTimeMillis() + 10000;
        final Event<String> event1 = new EventImpl<String>( startTime + 1000, "one" );
        final Event<String> event2 = new EventImpl<String>( startTime + 1100, "two" );
        final Event<String> event3 = new EventImpl<String>( startTime + 1120, "three" );
        final Event<String> event4 = new EventImpl<String>( startTime + 1300, "four" );
        final Event<String> event5 = new EventImpl<String>( startTime + 1550, "five" );
        final Event<String> event6 = new EventImpl<String>( startTime + 1700, "six" );
        
        // define a jmock sequence and state machine
        final Sequence seq = context.sequence( "call sequence" );
        
        // create mock objects for the source and the receiver
        final EventSource source = context.mock( EventSource.class );
        final EventReceiver receiver = context.mock( EventReceiver.class );
        
        // create the scheduler used by drools and the feeder to be tested
        JDKTimerService clock = new JDKTimerService(1);
        EventFeeder feeder = new EventFeeder( clock, source, receiver );
        
        // create the expectations
        context.checking( new Expectations() {{
            // there is an event1, so, read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) ); 
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event1 ) );  
            oneOf( receiver ).receive( event1 ); inSequence( seq ); 
            
            // there is an event 2, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event2 ) );  
            oneOf( receiver ).receive( event2 ); inSequence( seq ); 
            
            // there is an event 3, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event3 ) );  
            oneOf( receiver ).receive( event3 ); inSequence( seq ); 
            
            // there is an event 4, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event4 ) );  
            oneOf( receiver ).receive( event4 ); inSequence( seq ); 
            
            // there is an event 5, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event5 ) );  
            oneOf( receiver ).receive( event5 ); inSequence( seq ); 
            
            // there is an event 6, so read and feed
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( true ) );
            oneOf( source ).getNext(); inSequence( seq ); will( returnValue( event6 ) );  
            oneOf( receiver ).receive( event6 ); inSequence( seq );  
            
            // there are no more events
            oneOf( source ).hasNext(); inSequence( seq ); will( returnValue( false ) );
        }});
        
        feeder.feed();
        
        // give time for the test to run
        try {
            Thread.currentThread().sleep( 20000 );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
        
        context.assertIsSatisfied();
    }
}
