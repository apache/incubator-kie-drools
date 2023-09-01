package org.drools.testcoverage.regression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.Promotion;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.testcoverage.common.util.KieUtil.getServices;

public class LogicalInsertionsSerializationTest extends KieSessionTest {

    private static final String DRL_FILE = "logical-insertion.drl";

    public LogicalInsertionsSerializationTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                              final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Rule
    public TestName name = new TestName();

    @Parameterized.Parameters(name = "{1}" + " (from " + "{0}" + ")")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseAndStatefulKieSessionConfigurations();
    }

    @Test
    public void testSerializeAndDeserializeSession() throws Exception {
        KieSession ksession = session.getStateful();
        File tempFile = File.createTempFile(name.getMethodName(), null);

        ksession.fireAllRules();

        try (OutputStream fos = new FileOutputStream(tempFile)) {
            Marshaller marshaller = getServices().getMarshallers().newMarshaller(getKbase());
            marshaller.marshall(fos, ksession);
        }

        try (InputStream fis = new FileInputStream(tempFile)) {
            Marshaller marshaller = getServices().getMarshallers().newMarshaller(getKbase());
            marshaller.unmarshall(fis, ksession);
        }

        ksession.insert(new Promotion("Claire", "Scientist"));
        int firedRules = ksession.fireAllRules();

        assertThat(firedRules).isEqualTo(1);
    }

    private KieBase getKbase() {
        return session.isStateful() ? session.getStateful().getKieBase() : session.getStateless().getKieBase();
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL_FILE, LogicalInsertionsSerializationTest.class);
    }
}
