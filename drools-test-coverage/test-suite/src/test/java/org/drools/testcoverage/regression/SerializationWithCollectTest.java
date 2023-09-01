package org.drools.testcoverage.regression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieSession;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.fail;

/**
 * Simple reproducer for BZ 1193600 - serialization of rules with collect.
 */
public class SerializationWithCollectTest {

    private static final String DRL =
            "import java.util.Collection\n"
            + "rule R1 when\n"
            + " Collection(empty==false) from collect( Integer() )\n"
            + " Collection() from collect( String() )\n"
            + "then\n"
            + "end\n"
            + "rule R2 when then end\n";

    private KieBase kbase;
    private KieSession ksession;

    @Before
    public void setup() {
        this.kbase = new KieHelper().addContent(DRL, ResourceType.DRL).build();
        this.ksession = kbase.newKieSession();
    }

    @After
    public void cleanup() {
        if (this.ksession != null) {
            this.ksession.dispose();
        }
    }

    /**
     * BZ 1193600
     */
    @Test
    public void test() throws Exception {
        Marshaller marshaller = MarshallerFactory.newMarshaller(kbase);
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            marshaller.marshall(baos, ksession);
            try (final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                marshaller = MarshallerFactory.newMarshaller(kbase);
                ksession = marshaller.unmarshall(bais);
            }
        } catch (NullPointerException e) {
            fail("Consider reopening BZ 1193600!", e);
        }
    }
}
