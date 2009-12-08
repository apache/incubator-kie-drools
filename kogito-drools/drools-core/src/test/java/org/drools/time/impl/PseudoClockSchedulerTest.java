package org.drools.time.impl;

import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.Trigger;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PseudoClockSchedulerTest {

    private Mockery context = new Mockery();

    private Job mockJob_1 = context.mock(Job.class, "mockJob_1");
    private JobContext mockContext_1 = context.mock(JobContext.class, "mockContext_1");
    private Trigger mockTrigger_1 = context.mock(Trigger.class, "mockTrigger_1");

    private Job mockJob_2 = context.mock(Job.class, "mockJob_2");
    private JobContext mockContext_2 = context.mock(JobContext.class, "mockContext_2");
    private Trigger mockTrigger_2 = context.mock(Trigger.class, "mockTrigger_2");

    private PseudoClockScheduler scheduler = new PseudoClockScheduler();

    @Test public void removeExistingJob() {
        final Date triggerTime = new Date(1000);
        context.checking(new Expectations() {{
            atLeast(1).of(mockTrigger_1).hasNextFireTime(); will(returnValue(triggerTime));
        }});

        JobHandle jobHandle = scheduler.scheduleJob(mockJob_1, this.mockContext_1, mockTrigger_1);
        assertThat(scheduler.getTimeToNextJob(), is(triggerTime.getTime()));

        scheduler.removeJob(jobHandle);
        assertThat(scheduler.getTimeToNextJob(), is(-1L));
    }


    @Test public void removeExistingJobWhenMultipleQueued() {
        final Date triggerTime_1 = new Date(1000);
        final Date triggerTime_2 = new Date(2000);
        context.checking(new Expectations() {{
            atLeast(1).of(mockTrigger_1).hasNextFireTime(); will(returnValue(triggerTime_1));
            atLeast(1).of(mockTrigger_2).hasNextFireTime(); will(returnValue(triggerTime_2));
        }});

        JobHandle jobHandle_1 = scheduler.scheduleJob(mockJob_1, this.mockContext_1, mockTrigger_1);
        JobHandle jobHandle_2 = scheduler.scheduleJob(mockJob_2, this.mockContext_2, mockTrigger_2);
        assertThat(scheduler.getTimeToNextJob(), is(triggerTime_1.getTime()));

        scheduler.removeJob(jobHandle_1);
        assertThat(scheduler.getTimeToNextJob(), is(triggerTime_2.getTime()));

        scheduler.removeJob(jobHandle_2);
        assertThat(scheduler.getTimeToNextJob(), is(-1L));
    }

    @Test public void timerIsSetToJobTriggerTimeForExecution() {
        final Date triggerTime = new Date(1000);
        context.checking(new Expectations() {{
            exactly(2).of(mockTrigger_1).hasNextFireTime(); will(returnValue(triggerTime));
            oneOf(mockTrigger_1).nextFireTime(); will(returnValue(triggerTime));
            allowing(mockTrigger_1).hasNextFireTime(); will(returnValue(null));
        }});
        Job job = new Job() {
            public void execute(JobContext ctx) {
                // Even though the clock has been advanced to 5000, the job should run
                // with the time set its trigger time.
                assertThat(scheduler.getCurrentTime(), is(1000L));
            }
        };

        scheduler.scheduleJob(job, this.mockContext_1, mockTrigger_1);

        scheduler.advanceTime(5000, TimeUnit.MILLISECONDS);

        // Now, after the job has been executed the time should be what it was advanced to
        assertThat(scheduler.getCurrentTime(), is(5000L));
    }

    @Test public void timerIsResetWhenJobThrowsExceptions() {
        final Date triggerTime = new Date(1000);
        context.checking(new Expectations() {{
            exactly(2).of(mockTrigger_1).hasNextFireTime(); will(returnValue(triggerTime));
            oneOf(mockTrigger_1).nextFireTime(); will(returnValue(triggerTime));
            allowing(mockTrigger_1).hasNextFireTime(); will(returnValue(null));
        }});
        Job job = new Job() {
            public void execute(JobContext ctx) {
                assertThat(scheduler.getCurrentTime(), is(1000L));
                throw new RuntimeException("for test");
            }
        };

        scheduler.scheduleJob(job, this.mockContext_1, mockTrigger_1);

        scheduler.advanceTime(5000, TimeUnit.MILLISECONDS);

        // The time must be advanced correctly even when the job throws an exception
        assertThat(scheduler.getCurrentTime(), is(5000L));
    }
}