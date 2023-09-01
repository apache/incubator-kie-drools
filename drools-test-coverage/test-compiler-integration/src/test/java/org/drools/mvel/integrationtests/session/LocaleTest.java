package org.drools.mvel.integrationtests.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class LocaleTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public LocaleTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testLatinLocale() throws Exception {
        final Locale defaultLoc = Locale.getDefault();

        try {
            // setting a locale that uses COMMA as decimal separator
            Locale.setDefault(new Locale("pt", "BR"));

            KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_LatinLocale.drl");
            KieSession ksession = kbase.newKieSession();

            final List<String> results = new ArrayList<String>();
            ksession.setGlobal("results", results);

            final Cheese mycheese = new Cheese("cheddar", 4);
            final FactHandle handle = ksession.insert(mycheese);
            ksession.fireAllRules();

            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0)).isEqualTo("1");

            mycheese.setPrice(8);
            mycheese.setDoublePrice(8.50);

            ksession.update(handle, mycheese);
            ksession.fireAllRules();
            assertThat(results.size()).isEqualTo(2);
            assertThat(results.get(1)).isEqualTo("3");
        } finally {
            Locale.setDefault(defaultLoc);
        }
    }

}
