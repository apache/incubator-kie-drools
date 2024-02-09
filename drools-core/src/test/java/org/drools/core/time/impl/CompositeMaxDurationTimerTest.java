/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.time.impl;

import java.util.Date;

import org.drools.base.time.Trigger;
import org.junit.Test;

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
