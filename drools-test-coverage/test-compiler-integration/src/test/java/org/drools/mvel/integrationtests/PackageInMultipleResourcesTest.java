package org.drools.mvel.integrationtests;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PackageInMultipleResourcesTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PackageInMultipleResourcesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testSamePackageRulesInDRLAndRF() {
        // DROOLS-6785
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "rf_test_rules.drl", "rf_test_rueflow.rf");

        KiePackage kiePackage = kbase.getKiePackage("com.example.rules");
        List<String> ruleNames = kiePackage.getRules().stream().map(rule -> rule.getName()).collect(Collectors.toList());

        assertThat(ruleNames.size()).isEqualTo(3);
        assertThat(ruleNames).contains("RuleFlow-Split-example-xxx-DROOLS_DEFAULT", "Left Rule", "Right Rule");
    }

    @Test
    public void testDifferentPackagesRulesInDRLAndRF() {
        // DROOLS-6797
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "rf_test_rules_different_pkg.drl", "rf_test_rueflow.rf");

        KiePackage kiePackage = kbase.getKiePackage("com.example.rules");
        List<String> ruleNames = kiePackage.getRules().stream().map(rule -> rule.getName()).collect(Collectors.toList());

        assertThat(ruleNames.size()).isEqualTo(1);
        assertThat(ruleNames).contains("RuleFlow-Split-example-xxx-DROOLS_DEFAULT");

        KiePackage kiePackageDiffPkg = kbase.getKiePackage("com.example.rules.different.pkg");
        List<String> ruleNamesDiffPkg = kiePackageDiffPkg.getRules().stream().map(rule -> rule.getName()).collect(Collectors.toList());

        assertThat(ruleNamesDiffPkg.size()).isEqualTo(2);
        assertThat(ruleNamesDiffPkg).contains("Left Rule", "Right Rule");
    }
}
