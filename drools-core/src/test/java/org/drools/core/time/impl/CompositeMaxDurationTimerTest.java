/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
