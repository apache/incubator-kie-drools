package org.drools.core.time.impl;

import org.drools.core.time.Trigger;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CompositeMaxDurationTimerTest {
    
    @Test
    public void testJustMaxDuration() {
        CompositeMaxDurationTimer timer = new CompositeMaxDurationTimer();
        timer.addDurationTimer( new DurationTimer( 25 ) );
        timer.addDurationTimer( new DurationTimer( 50 ) );
        timer.addDurationTimer( new DurationTimer( 70 ) );
        Date timestamp = new Date();
        Trigger trigger = timer.createTrigger( timestamp.getTime(), null, null, null, null, null, null );
        
        assertEquals( new Date( timestamp.getTime() + 70 ), trigger.hasNextFireTime() );
        assertNull( trigger.nextFireTime() );
        assertNull( trigger.hasNextFireTime() );
    }
    
    @Test
    public void testMixedDurationAndTimer() {
        CompositeMaxDurationTimer timer = new CompositeMaxDurationTimer();
        timer.addDurationTimer( new DurationTimer( 25 ) );
        timer.addDurationTimer( new DurationTimer( 50 ) );
        timer.addDurationTimer( new DurationTimer( 70 ) );
        
        timer.setTimer( new IntervalTimer(null, null, 6, 40, 25) );
        Date timestamp = new Date();        
        
        Trigger trigger = timer.createTrigger( timestamp.getTime(), null, null, null, null, null, null );
        
        // ignores the first interval timer at 65
        assertEquals( new Date( timestamp.getTime() + 70 ), trigger.hasNextFireTime() );
        assertEquals( new Date( timestamp.getTime() + 90 ), trigger.nextFireTime() );
        assertEquals( new Date( timestamp.getTime() + 115 ), trigger.nextFireTime() );
        assertEquals( new Date( timestamp.getTime() + 140 ), trigger.nextFireTime() );
        assertNull( trigger.nextFireTime() );
        assertNull( trigger.hasNextFireTime() );        
    }    
}
