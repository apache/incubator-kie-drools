package org.drools.compiler.integrationtests.notms;

import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class NoTmsTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public NoTmsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testUnsupportedTms() {
        String drl =
                "package org.drools.test; \n" +
                "" +
                "rule A when\n" +
                " $x : Integer() \n" +
                "then\n" +
                " insertLogical( \"\" + $x ); \n" +
                "end\n" +
                "" +
                "rule B when\n" +
                " $x : String() \n" +
                "then\n" +
                "end";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).getText().contains("drools-tms"));
    }
}
