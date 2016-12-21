package org.drools.testcoverage.regression;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for BZ 1150308.
 */
public class FixedPatternTest {

    private KieSession ksession;

    @After
    public void cleanup() {
        if (this.ksession != null) {
            this.ksession.dispose();
        }
    }

    /**
     * Tests fixed pattern without constraint in Decision table (BZ 1150308).
     */
    @Test
    public void testFixedPattern() {

        Resource resource = KieServices.Factory.get().getResources().newClassPathResource("fixedPattern.xls", getClass());
        final KieBuilder kbuilder = KieBaseUtil.getKieBuilderFromResources(true, resource);

        final KieContainer kcontainer = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId());
        ksession = kcontainer.newKieSession();

        List<Long> list = new ArrayList<Long>();
        ksession.setGlobal("list", list);

        ksession.insert(1L);
        ksession.insert(2);
        ksession.fireAllRules();

        Assertions.assertThat(list.size()).isEqualTo(1);
        Assertions.assertThat((long) list.get(0)).isEqualTo(1L);
    }
}
