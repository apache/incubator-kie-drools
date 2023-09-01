package org.drools.mvel.integrationtests.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.mvel.compiler.SpecialString;
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
public class CrossProductTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CrossProductTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testCrossProductRemovingIdentityEquals() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_CrossProductRemovingIdentityEquals.drl");
        KieSession session = kbase.newKieSession();

        final List list1 = new ArrayList();
        session.setGlobal("list1", list1);

        final SpecialString first42 = new SpecialString("42");
        final SpecialString second43 = new SpecialString("43");
        final SpecialString world = new SpecialString("World");
        session.insert(world);
        session.insert(first42);
        session.insert(second43);

        session.fireAllRules();

        assertThat(list1.size()).isEqualTo(6);

        final List list2 = Arrays.asList("42:43", "43:42", "World:42", "42:World", "World:43", "43:World");
        Collections.sort(list1);
        Collections.sort(list2);
        assertThat(list1).isEqualTo(list2);
    }

}
