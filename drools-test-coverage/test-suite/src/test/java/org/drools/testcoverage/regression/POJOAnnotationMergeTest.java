package org.drools.testcoverage.regression;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.definition.type.Position;

/**
 * Tests merging the POJO annotations (e.g. @Position) with fact declaration in
 * DRL.
 */
@RunWith(Parameterized.class)
public class POJOAnnotationMergeTest {

    private static final String EVENT_CLASS_NAME = PositionAnnotatedEvent.class.getCanonicalName();

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public POJOAnnotationMergeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    // should add metadata to metadata already defined in POJO
    private static final String DRL =
            "package org.test\n" +
            "declare " + EVENT_CLASS_NAME + "\n" +
            "    @role(event)\n" +
            "end \n" +
            "rule 'sample rule' \n" +
            "when \n" +
            "  " + EVENT_CLASS_NAME + "( 'value1', 'value2'; )\n" +
            "then\n" +
            "end\n";

    /**
     * Tests adding metadata in DRL to the metadata already declared in a POJO.
     */
    @Test
    public void testPositionFromPOJOIgnored() {
        KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,true, DRL);
    }

    /**
     * Sample event annotated with @Position metadata.
     */
    public static class PositionAnnotatedEvent {
        @Position(1)
        private String arg1;

        @Position(0)
        private String arg0;

        public String getArg1() {
            return arg1;
        }

        public void setArg1(final String arg1) {
            this.arg1 = arg1;
        }

        public String getArg0() {
            return arg0;
        }

        public void setArg0(final String arg0) {
            this.arg0 = arg0;
        }

    }
}
