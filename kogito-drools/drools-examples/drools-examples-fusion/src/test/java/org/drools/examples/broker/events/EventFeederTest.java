package org.drools.examples.broker.events;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.drools.time.impl.PseudoClockScheduler;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class EventFeederTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testFeedPseudoClock() {
        // create the events for the test
        final Event< ? >[] events = new Event[]{new EventImpl<String>( 1000,
                                                                       "one" ), new EventImpl<String>( 1100,
                                                                                                       "two" ), new EventImpl<String>( 1120,
                                                                                                                                       "three" ), new EventImpl<String>( 1300,
                                                                                                                                                                         "four" ), new EventImpl<String>( 1550,
                                                                                                                                                                                                          "five" ), new EventImpl<String>( 1700,
                                                                                                                                                                                                                                           "six" ),};

        final Iterator<Event< ? >> it = Arrays.asList( events ).iterator();

        // create mock objects for the source and the receiver
        final EventSource source = mock( EventSource.class );
        final EventReceiver receiver = mock( EventReceiver.class );

        when( source.hasNext() ).thenAnswer( new Answer<Boolean>() {
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return it.hasNext();
            }
        } );
        when( source.getNext() ).thenAnswer( new Answer<Event<?>>() {
            public Event< ? > answer(InvocationOnMock invocation) throws Throwable {
                return it.next();
            }
        });
        
        // create the scheduler used by drools and the feeder to be tested
        PseudoClockScheduler clock = new PseudoClockScheduler();
        EventFeeder feeder = new EventFeeder( clock,
                                              source,
                                              receiver );

        feeder.feed();
        // advancing time step by step, so that we can test state between advances
        for( Event<?> event : events ) {
            clock.advanceTime( event.getTimestamp() - clock.getCurrentTime(),
                               TimeUnit.MILLISECONDS );
        }

        verify( source, times(7) ).hasNext();
        verify( source, times(6) ).getNext();
    }

}
