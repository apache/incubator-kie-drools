package org.drools.mvel.integrationtests;

import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class SwitchOverStringTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SwitchOverStringTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    private static final String FUNCTION_WITH_SWITCH_OVER_STRING = "function void theTest(String input) {\n" +
            "  switch(input) {\n" +
            "    case \"Hello World\" :" +
            "      System.out.println(\"yep\");\n" +
            "      break;\n" +
            "    default :\n" +
            "      System.out.println(\"uh\");\n" +
            "      break;\n" +
            "  }\n" +
            "}";

    @After
    public void cleanUp() {
        System.clearProperty("drools.dialect.java.compiler.lnglevel");
    }

    @Test
    public void testCompileSwitchOverStringWithLngLevel17() {
        double javaVersion = Double.valueOf(System.getProperty("java.specification.version"));
        Assume.assumeTrue("Test only makes sense on Java 7+.", javaVersion >= 1.7);
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.7");
        try {
            KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, FUNCTION_WITH_SWITCH_OVER_STRING);
            List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
            assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            System.clearProperty("drools.dialect.java.compiler.lnglevel");
        }
    }

    @Test
    public void testShouldFailToCompileSwitchOverStringWithLngLevel16() {
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.6");
        try {
            KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, FUNCTION_WITH_SWITCH_OVER_STRING);
            List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
            assertFalse("Should have an error", errors.isEmpty());
            
        } finally {
            System.clearProperty("drools.dialect.java.compiler.lnglevel");
        }
    }
}
