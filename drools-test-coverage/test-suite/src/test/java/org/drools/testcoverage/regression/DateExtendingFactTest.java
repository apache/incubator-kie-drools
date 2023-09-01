package org.drools.testcoverage.regression;

import java.util.Collection;
import java.util.Date;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

import static org.assertj.core.api.Assertions.assertThat;

/**
* Tests compilation of facts extending java.util.Date (BZ 1072629).
*/
@RunWith(Parameterized.class)
public class DateExtendingFactTest {

    private static final String FACT_CLASS_NAME = MyDate.class.getCanonicalName();

    private static final String DRL =
            "package org.test\n" +
            "rule 'sample rule'\n" +
            "when\n" +
            "  $date:" + FACT_CLASS_NAME + "()\n" +
            "then\n" +
            "$date.setDescription(\"test\");\n" +
            "end\n";

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DateExtendingFactTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    /**
     * Tests compiling DRL with a fact extending java.util.Date.
     */
    @Test
    public void testDateExtendingFact() {
        final KieBuilder kbuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, DRL);
        assertThat(kbuilder.getResults().getMessages(Message.Level.ERROR)).isEmpty();
    }


    /**
     * Sample fact extending java.util.Date.
     */
    public static class MyDate extends Date {

        private String description;

        public MyDate() {
            super();
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(final String desc) {
            this.description = desc;
        }
    }
}
