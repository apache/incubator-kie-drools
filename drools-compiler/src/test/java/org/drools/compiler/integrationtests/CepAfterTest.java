package org.drools.compiler.integrationtests;

import java.io.Serializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

/**
 * Test for temporal operator After with realtime clock.
 */
public class CepAfterTest {
    private static final String EVENT_A = "EventA";
    private static final String EVENT_B = "EventB";

    private KieSession ksession;

    private List<String> result;

    @Before
    public void init() {
        KieServices ks = KieServices.Factory.get();

        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel defaultBase = module.newKieBaseModel("defaultKBase")
                                         .setDefault(true)
                                         .addPackage("*")
                                         .setEventProcessingMode(EventProcessingOption.STREAM);
        defaultBase.newKieSessionModel("defaultKSession")
                   .setDefault(true);
                    // uncomment setting the pseudo clock type and the test will pass
                   //.setClockType(ClockTypeOption.get("pseudo"));

        Resource drl = ks.getResources().newClassPathResource("test_CEP_AfterEP.drl", CepAfterTest.class);
        KieFileSystem kfs = ks.newKieFileSystem()
                              .write("src/main/resources/r1.drl", drl);
        kfs.writeKModuleXML(module.toXML());
        ks.newKieBuilder(kfs).buildAll();

        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId())
                     .newKieSession();

        result = new ArrayList<String>();
        ksession.setGlobal("results", result);
    }

    @After
    public void cleanup() {
        ksession.dispose();
    }
    
    @Test
    @Ignore
    public void testAfterMultipleAEventsByCalendar() throws InterruptedException {
        EntryPoint entryPoint = ksession.getEntryPoint("AfterMultipleAEventsStream");

        Calendar cal = Calendar.getInstance();
        cal.set(2012, 07, 29, 14, 42, 0);

        entryPoint.insert(new MetadataEvent(EVENT_B, cal.getTime(), 3000L));

        // EventA inserted, duration 1000 ms => proper zone when B
        // should start for the rule to fire is + 6070-7070 ms
        entryPoint.insert(new MetadataEvent(EVENT_A, new Date(cal.getTimeInMillis() + 70), 1000L));

        // should not trigger the rule
        entryPoint.insert(new MetadataEvent(EVENT_B, new Date(cal.getTimeInMillis() + 150), 1000L));

        // should not trigger the rule
        entryPoint.insert(new MetadataEvent(EVENT_B, new Date(cal.getTimeInMillis() + 6000), 1000L));

        // nothing should fire yet
        ksession.fireAllRules();
        assertTrue(result.isEmpty());

        // this should trigger the rule
        entryPoint.insert(new MetadataEvent(EVENT_B, new Date(cal.getTimeInMillis() + 7000), 1000L));

        // fire
        ksession.fireAllRules();
        assertEquals(1, result.size());
    }
   
    public static class MetadataEvent implements Serializable {

        private static final long serialVersionUID = 6827172457832354239L;
        private Date metadataTimestamp;
        private Long metadataDuration;
        private String name;

        public MetadataEvent() {
        }

        public MetadataEvent(Date timestamp, Long duration) {
            metadataTimestamp = timestamp;
            metadataDuration = duration;
        }

        public MetadataEvent(String name, Date timestamp, Long duration) {
            this.name = name;
            metadataTimestamp = timestamp;
            metadataDuration = duration;
        }

        public Date getMetadataTimestamp() {
            return metadataTimestamp != null ? (Date) metadataTimestamp.clone() : null;
        }

        public void setMetadataTimestamp(Date metadataTimestamp) {
            this.metadataTimestamp = metadataTimestamp != null
                                     ? (Date) metadataTimestamp.clone() : null;
        }

        public Long getMetadataDuration() {
            return metadataDuration;
        }

        public void setMetadataDuration(Long metadataDuration) {
            this.metadataDuration = metadataDuration;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("MetadataEvent[name='%s' timestamp='%s', duration='%s']", name, metadataTimestamp, metadataDuration);
        }
    }    
}