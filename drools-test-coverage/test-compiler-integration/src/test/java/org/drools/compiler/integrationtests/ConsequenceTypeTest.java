package org.drools.compiler.integrationtests;

import java.util.Collection;

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
public class ConsequenceTypeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ConsequenceTypeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void test() {
        // KIE-1133

        int ruleNr = 100;
        StringBuilder drl = new StringBuilder();
        drl.append("package org.rules;\n");
        drl.append("import org.drools.compiler.integrationtests.domainfirst.*\n");
        drl.append("import org.drools.compiler.integrationtests.domainsecond.*\n");
        drl.append("dialect \"mvel\"\n");
        for (int i = 0; i < ruleNr; i++) {
            drl.append(generatedRule(i));
        }

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl.toString());
        final KieSession wm = kbase.newKieSession();

        wm.insert("test");
        assertThat(wm.fireAllRules()).isEqualTo(ruleNr);
    }

    private String generatedRule(int seed) {
        return "rule R" + seed + " when $s : String() then\n" +
                "org.drools.compiler.integrationtests.domainfirst.Pojo $pojo = new org.drools.compiler.integrationtests.domainfirst.Pojo();\n" +
                "org.drools.compiler.integrationtests.domainsecond.Pojo $pojo_No2 = new org.drools.compiler.integrationtests.domainsecond.Pojo();\n" +
                "$pojo.setId($s.length() + " + seed + ");\n" +
                "$pojo_No2.setId($s.length() + " + seed + ");\n" +
                "insert($pojo);\n" +
                "insert($pojo_No2);\n" +
                "end\n";
    }
}
