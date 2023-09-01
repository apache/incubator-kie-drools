package org.drools.testcoverage.regression.mvel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests handling a collision of identifer and dynamic package import (BZ 1321281).
 * The test is modified not to depend on specific platforms (Windows, MacOS) not preserving case in file names.
 */
public class MvelLinkageErrorTest {

    private static final String TEST_FACT = "test";

    private static final String DRL = "package " + MvelLinkageErrorTest.class.getPackage().getName() + " \n"
            + "dialect \"mvel\"\n"
            + "import " + MvelLinkageErrorTest.class.getPackage().getName() + ".* \n"
            + "global java.util.List output \n"
            + "rule rule1 \n"
            + "  when \n"
            + "    String(NotLoadableClass: length) \n"
            + "  then \n"
            + "    output.add(NotLoadableClass); \n"
            + "end\n";

    @Test
    public void testMvelLinkageError() {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", DRL);
        assertThatCode(() -> ks.newKieBuilder(kfs).buildAll()).doesNotThrowAnyException();

        final KieSession kieSession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        try {
            final List result = new ArrayList<>();
            kieSession.setGlobal("output", result);

            kieSession.insert(TEST_FACT);
            final int fired = kieSession.fireAllRules();
            assertThat(fired).as("Unexpected number of rules fired.").isEqualTo(1);
        } finally {
            kieSession.dispose();
        }
    }
}