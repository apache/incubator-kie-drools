package org.drools.testcoverage.regression;

import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.ResourceUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.io.Resource;

import java.io.StringReader;
import java.util.Date;

/**
* Tests compilation of facts extending java.util.Date (BZ 1072629).
*/
public class DateExtendingFactTest {

    private static final String FACT_CLASS_NAME = MyDate.class.getCanonicalName();

    private static final String DRL = "package org.test \n"
            + "rule 'sample rule' \n"
            + "when \n" + "  $date:" + FACT_CLASS_NAME + "() \n"
            + "then \n" + "$date.setDescription(\"test\"); \n" + "end \n";

    /**
     * Tests compiling DRL with a fact extending java.util.Date.
     */
    @Test
    public void testDateExtendingFact() {

        final Resource resource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(DRL));
        resource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        final KieBuilder kbuilder = KieBaseUtil.getKieBuilderFromResources(true, resource);
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
