package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.compiler.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class KnowledgeContextTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KnowledgeContextTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testKnowledgeContextJava() {
        testKnowledgeContext("test_KnowledgeContextJava.drl");
    }

    @Test
    public void testKnowledgeContextMVEL() {
        testKnowledgeContext("test_KnowledgeContextMVEL.drl");
    }

    private void testKnowledgeContext(final String drlResourceName) {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, drlResourceName);
        KieSession ksession = kbase.newKieSession();
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.insert(new Message());
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Hello World");
    }
}
