package org.drools.core.time.impl;

import org.drools.base.time.Trigger;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class CompositeMaxDurationTimerTest {
    
    @Test
    public void testJustMaxDuration() {
        CompositeMaxDurationTimer timer = new CompositeMaxDurationTimer();
        timer.addDurationTimer( new DurationTimer( 25 ) );
        timer.addDurationTimer( new DurationTimer( 50 ) );
        timer.addDurationTimer( new DurationTimer( 70 ) );
        Date timestamp = new Date();
        Trigger trigger = timer.createTrigger( timestamp.getTime(), null, null, null, null, null, null );

        assertThat(trigger.hasNextFireTime()).isEqualTo(new Date( timestamp.getTime() + 70 ));
        assertThat(trigger.nextFireTime()).isNull();
        assertThat(trigger.hasNextFireTime()).isNull();
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
        assertThat(trigger.hasNextFireTime()).isEqualTo(new Date( timestamp.getTime() + 70 ));
        assertThat(trigger.nextFireTime()).isEqualTo(new Date( timestamp.getTime() + 90 ));
        assertThat(trigger.nextFireTime()).isEqualTo(new Date( timestamp.getTime() + 115 ));
        assertThat(trigger.nextFireTime()).isEqualTo(new Date( timestamp.getTime() + 140 ));
        assertThat(trigger.nextFireTime()).isNull();
        assertThat(trigger.hasNextFireTime()).isNull();        
    }    
}
