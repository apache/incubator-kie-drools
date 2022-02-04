/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.integrationtests;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.io.ResourceType;
import org.kie.internal.services.KieAssemblersImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

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
        KieAssemblersImpl assemblers = (KieAssemblersImpl) ServiceRegistry.getService(KieAssemblers.class);
        Map<ResourceType, KieAssemblerService> internalAssemblers = assemblers.getAssemblers();
        KieAssemblerService originalDRFAssemblerService = internalAssemblers.get(ResourceType.DRF);
        try {
            assemblers.accept(new FakeDRFAssemblerService());

            KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "rf_test_rules.drl", "rf_test_rueflow.rf");

            KiePackage kiePackage = kbase.getKiePackage("com.example.rules");
            List<String> ruleNames = kiePackage.getRules().stream().map(rule -> rule.getName()).collect(Collectors.toList());

            assertEquals(3, ruleNames.size());
            assertThat(ruleNames).contains("RuleFlow-Split-example-xxx-DROOLS_DEFAULT", "Left Rule", "Right Rule");
        } finally {
            if (originalDRFAssemblerService == null) {
                internalAssemblers.remove(ResourceType.DRF);
            } else {
                assemblers.accept(originalDRFAssemblerService);
            }
        }
    }

    @Test
    public void testDifferentPackagesRulesInDRLAndRF() {
        // DROOLS-6797
        KieAssemblersImpl assemblers = (KieAssemblersImpl) ServiceRegistry.getService(KieAssemblers.class);
        Map<ResourceType, KieAssemblerService> internalAssemblers = assemblers.getAssemblers();
        KieAssemblerService originalDRFAssemblerService = internalAssemblers.get(ResourceType.DRF);
        try {
            assemblers.accept(new FakeDRFAssemblerService());

            KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "rf_test_rules_different_pkg.drl", "rf_test_rueflow.rf");

            KiePackage kiePackage = kbase.getKiePackage("com.example.rules");
            List<String> ruleNames = kiePackage.getRules().stream().map(rule -> rule.getName()).collect(Collectors.toList());

            assertEquals(1, ruleNames.size());
            assertThat(ruleNames).contains("RuleFlow-Split-example-xxx-DROOLS_DEFAULT");

            KiePackage kiePackageDiffPkg = kbase.getKiePackage("com.example.rules.different.pkg");
            List<String> ruleNamesDiffPkg = kiePackageDiffPkg.getRules().stream().map(rule -> rule.getName()).collect(Collectors.toList());

            assertEquals(2, ruleNamesDiffPkg.size());
            assertThat(ruleNamesDiffPkg).contains("Left Rule", "Right Rule");
        } finally {
            if (originalDRFAssemblerService == null) {
                internalAssemblers.remove(ResourceType.DRF);
            } else {
                assemblers.accept(originalDRFAssemblerService);
            }
        }
    }
}
