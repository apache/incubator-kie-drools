package org.drools.testcoverage.regression;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Tests generics in RHS with modify - BZ 1142886.
 */
@RunWith(Parameterized.class)
public class GenericsWithModifyTest {

    private static final String DRL =
            "package defaultKBase;\n" +
            "import java.util.Map;\n" +
            "import java.util.HashMap;\n" +
            "rule R no-loop when\n" +
            " $s : String( )\n" +
            "then\n" +
            " Map<String,String> a = new HashMap<String,String>();\n" +
            " modify( $s ) { };\n" +
            "end\n";

    private KieSession kieSession;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public GenericsWithModifyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Before
    public void initialize()  {
        final KieBuilder kbuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, DRL);

        final KieContainer kieContainer = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId());
        this.kieSession = kieContainer.newKieSession();
    }

    @After
    public void dispose() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }

    /**
     * Tests generics on rule's RHS with modify.
     */
    @Test
    public void testModifyWithGenericsOnRHS() {
        this.kieSession.insert("1");
        this.kieSession.fireAllRules();
    }
}
