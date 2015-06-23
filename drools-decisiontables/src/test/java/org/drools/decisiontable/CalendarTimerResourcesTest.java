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

package org.drools.decisiontable;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;

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
                 .setClockType(ClockTypeOption.get("pseudo"));

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write(ks.getResources().newClassPathResource("calendar_timer.xls", this.getClass())); // README when path is set then test works
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();

        clock = ksession.getSessionClock();
    }

    @Test
    public void test() {

        ksession.getCalendars().set("tuesday", TUESDAY);
        clock.advanceTime(4, TimeUnit.DAYS); // README now it is set to monday (test fails with NPE), when is set to tuesday (rule should fire) then test works
        ksession.fireAllRules();
    }

    @After
    public void clear() {

        if (ksession != null) {
            ksession.dispose();
        }
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
