package org.drools.decisiontable;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarTimerResourcesTest {

    private KieSession ksession;

    private SessionPseudoClock clock;

    @Before
    public void init() {

        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase")
                                        .setDefault(true)
                                        .setEventProcessingMode(EventProcessingOption.STREAM);
        baseModel.newKieSessionModel("defaultKSession")
                 .setDefault(true)
                 .setClockType(ClockTypeOption.PSEUDO);

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write(ks.getResources().newClassPathResource("calendar_timer.drl.xls", this.getClass())); // README when path is set then test works
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        
        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR)).isEmpty();

        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        clock = ksession.getSessionClock();
    }

    @After
    public void tearDown() {

        if (ksession != null) {
            ksession.dispose();
        }
    }

    @Test
    public void test() {
        ksession.getCalendars().set("tuesday", TUESDAY);
        clock.advanceTime(4, TimeUnit.DAYS); // README now it is set to monday (test fails with NPE), when is set to tuesday (rule should fire) then test works
        
        ksession.fireAllRules();
    }


    private static final org.kie.api.time.Calendar TUESDAY = new org.kie.api.time.Calendar() {

        @Override
        public boolean isTimeIncluded(long timestamp) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timestamp);

            int day = c.get(Calendar.DAY_OF_WEEK);
            return day == Calendar.TUESDAY;
        }
    };
}
