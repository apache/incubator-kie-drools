package org.drools.testcoverage.regression;

import org.assertj.core.api.Assertions;
import org.drools.core.ClockType;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.utils.KieHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Reproducer for BZ 1205666, BZ 1205671 (DROOLS-749).
 */
public class EventDeserializationInPastTest {

    @Test
    public void testSerializationWithEventInPastBZ1205666() {
        // DROOLS-749
        String drl =
                "import " + Event1.class.getCanonicalName() + "\n" +
                        "declare Event1\n" +
                        " @role( event )\n" +
                        " @timestamp( timestamp )\n" +
                        " @expires( 3h )\n" +
                        "end\n" +
                        "\n" +
                        "rule R\n" +
                        " when\n" +
                        " $evt: Event1()\n" +
                        " not Event1(this != $evt, this after[0, 1h] $evt)\n" +
                        " then\n" +
                        " System.out.println($evt.getCode());\n" +
                        "end";

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));
        KieHelper helper = new KieHelper();
        helper.addContent(drl, ResourceType.DRL);
        KieBase kbase = helper.build(EventProcessingOption.STREAM);
        KieSession ksession = kbase.newKieSession(sessionConfig, null);
        ksession.insert(new Event1("id1", 0));
        PseudoClockScheduler clock = ksession.getSessionClock();
        clock.advanceTime(2, TimeUnit.HOURS);
        ksession.fireAllRules();
        ksession = marshallAndUnmarshall(KieServices.Factory.get(), kbase, ksession, sessionConfig);
        ksession.insert(new Event1("id2", 0));
        ksession.fireAllRules();
    }

    private KieSession marshallAndUnmarshall(KieServices ks, KieBase kbase, KieSession ksession) {
        return marshallAndUnmarshall(ks, kbase, ksession, null);
    }

    private KieSession marshallAndUnmarshall(KieServices ks, KieBase kbase, KieSession ksession, KieSessionConfiguration sessionConfig) {
        // Serialize and Deserialize
        try {
            Marshaller marshaller = ks.getMarshallers().newMarshaller(kbase);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshall(baos, ksession);
            marshaller = MarshallerFactory.newMarshaller(kbase);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            baos.close();
            ksession = marshaller.unmarshall(bais, sessionConfig, null);
            bais.close();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("unexpected exception :" + e.getMessage());
        }
        return ksession;
    }

    public static class Event1 implements Serializable {
        private final String code;
        private final long timestamp;

        public Event1(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        public String getCode() {
            return code;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "Event1{" +
                    "code='" + code + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
