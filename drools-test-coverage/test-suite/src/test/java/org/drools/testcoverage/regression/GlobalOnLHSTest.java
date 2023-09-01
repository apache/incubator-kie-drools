package org.drools.testcoverage.regression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalOnLHSTest extends KieSessionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalOnLHSTest.class);

    private static final String DRL_FILE = "bz1019473.drl";

    public GlobalOnLHSTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                           final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Parameterized.Parameters(name = "{1}" + " (from " + "{0}" + ")")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseAndStatefulKieSessionConfigurations();
    }

    @Test
    public void testNPEOnMutableGlobal() throws Exception {

        KieSession ksession = session.getStateful();

        List<String> context = new ArrayList<String>();
        ksession.setGlobal("context", context);
        ksession.setGlobal("LOGGER", LOGGER);

        FactHandle b = ksession.insert( new Message( "b" ) );
        ksession.delete(b);
        int fired = ksession.fireAllRules(1);

        assertThat(fired).isEqualTo(0);
        ksession.dispose();
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL_FILE, GlobalOnLHSTest.class);
    }
}
