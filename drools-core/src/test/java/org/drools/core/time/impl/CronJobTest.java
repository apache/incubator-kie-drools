package org.drools.core.time.impl;

import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.time.impl.JDKTimerServiceTest.HelloWorldJob;
import org.drools.core.time.impl.JDKTimerServiceTest.HelloWorldJobContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CronJobTest {
    @Test
    public void testCronTriggerJob() throws Exception {
        SessionConfiguration config = RuleBaseFactory.newKnowledgeSessionConfiguration().as(SessionConfiguration.KEY);
        config.setClockType(ClockType.PSEUDO_CLOCK);
        PseudoClockScheduler timeService = (PseudoClockScheduler) config.createTimerService();

        timeService.advanceTime( 0,
                                 TimeUnit.MILLISECONDS );

        CronTrigger trigger = new CronTrigger( 0,
                                               null,
                                               null,
                                               -1,
                                               "15 * * * * ?",
                                               null,
                                               null );

        HelloWorldJobContext ctx = new HelloWorldJobContext( "hello world",
                                                             timeService );
        timeService.scheduleJob( new HelloWorldJob(),
                                 ctx,
                                 trigger );

        assertThat(ctx.getList()).hasSize(0);

        timeService.advanceTime( 10,
                                 TimeUnit.SECONDS );
        assertThat(ctx.getList()).hasSize(0);

        timeService.advanceTime( 10,
                                 TimeUnit.SECONDS );
        assertThat(ctx.getList()).hasSize(1);

        timeService.advanceTime( 30,
                                 TimeUnit.SECONDS );
        assertThat(ctx.getList()).hasSize(1);

        timeService.advanceTime( 30,
                                 TimeUnit.SECONDS );
        assertThat(ctx.getList()).hasSize(2);
    }
}
