package org.drools.core.time.impl;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class PseudoClockSchedulerClockTest {

    @Test
    public void testClockAdvance() {
        final PseudoClockScheduler scheduler = new PseudoClockScheduler();
        Assert.assertEquals("Initial time is not 0!", 0L, scheduler.getCurrentTime());

        testTime(scheduler, 10000, 10, TimeUnit.SECONDS, null);
        testTime(scheduler, 0, -10, TimeUnit.SECONDS, null);
        testTime(scheduler, 60000, 1, TimeUnit.MINUTES, null);
    }

    @Test
    public void testClockAdvanceNumberOfTimes() {
        final PseudoClockScheduler scheduler = new PseudoClockScheduler();
        Assert.assertEquals("Initial time is not 0!", 0L, scheduler.getCurrentTime());

        testTime(scheduler, 10000, 10, TimeUnit.SECONDS, 1);
        testTime(scheduler, 0, -10, TimeUnit.SECONDS, 1);
        testTime(scheduler, 60000, 1, TimeUnit.MINUTES, 1);
        testTime(scheduler, 0, 1, TimeUnit.MINUTES, -1);

        testTime(scheduler, 50000, 10, TimeUnit.SECONDS, 5);
        testTime(scheduler, 0, -10, TimeUnit.SECONDS, 5);
        testTime(scheduler, 1200000, 1, TimeUnit.MINUTES, 20);
        testTime(scheduler, 0, 1, TimeUnit.MINUTES, -20);
        testTime(scheduler, -50000, 10, TimeUnit.SECONDS, -5);
    }

    private void testTime(final PseudoClockScheduler scheduler, final long expectedTime, final long amount,
            final TimeUnit timeUnit, final Integer steps) {
        if (steps == null) {
            scheduler.advanceTime(amount, timeUnit);
        } else {
            scheduler.advanceTime(amount, timeUnit, steps);
        }
        Assert.assertEquals("Time is not " + expectedTime + "!", expectedTime, scheduler.getCurrentTime());
    }
}
