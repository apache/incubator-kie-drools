package org.drools.testcoverage.regression.mvel;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests handling a collision of identifer and dynamic package import (BZ 1321281).
 * The test is modified not to depend on specific platforms (Windows, MacOS) not preserving case in file names.
 */
public class MvelLinkageErrorTest {

    private static final String TEST_FACT = "test";

    private static final String DRL = "package org.drools.testcoverage.regression.mvel \n"
            + "dialect \"mvel\"\n"
            + "import org.drools.testcoverage.regression.mvel.* \n"
            + "global java.util.List output \n"
            + "rule rule1 \n"
            + "  when \n"
            + "    String(NotLoadableClass: length) \n"
            + "  then \n"
            + "    output.add(NotLoadableClass); \n"
            + "end\n";

    private KieSession kieSession;

    @Before
    public void init() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", DRL);
        ks.newKieBuilder( kfs ).buildAll();

        kieSession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
    }

    @After
    public void cleanup() {
        if (kieSession != null) {
            kieSession.dispose();
        }
    }

    @Test
    public void testMvelLinkageError() throws Exception {
        final List<Integer> result = new ArrayList<Integer>();
        kieSession.setGlobal("output", result);

        kieSession.insert(TEST_FACT);
        try {
            int fired = kieSession.fireAllRules();
            Assertions.assertThat(fired).as("Unexpected number of rules fired.").isEqualTo(1);
            Assertions.assertThat(result).as("Rule produced unexpected result value.").containsExactly(TEST_FACT.length());
        } finally {
            kieSession.dispose();
        }
    }

}
