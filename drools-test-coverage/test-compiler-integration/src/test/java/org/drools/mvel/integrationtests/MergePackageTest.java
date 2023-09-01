/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class MergePackageTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MergePackageTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testMergingDifferentPackages2() throws Exception {
        // using different builders
        try {
            Collection<KiePackage> kpkgs1 = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_RuleNameClashes1.drl").getKiePackages();
            KiePackage kpkg1 = kpkgs1.stream().filter( pkg -> pkg.getName().equals( "org.drools.package1" ) ).findFirst().get();
            assertThat(kpkg1.getRules().size()).isEqualTo(1);

            Collection<KiePackage> kpkgs2 = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_RuleNameClashes2.drl").getKiePackages();
            KiePackage kpkg2 = kpkgs2.stream().filter( pkg -> pkg.getName().equals( "org.drools.package2" ) ).findFirst().get();
            assertThat(kpkg2.getRules().size()).isEqualTo(1);

            InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
            kbase.addPackages(kpkgs1);
            kbase.addPackages(kpkgs2);
            KieSession ksession = kbase.newKieSession();

            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            ksession.insert(new Cheese("stilton", 10));
            ksession.insert(new Cheese("brie", 5));

            ksession.fireAllRules();

            assertThat(results.size()).as(results.toString()).isEqualTo(2);
            assertThat(results.contains("p1.r1")).isTrue();
            assertThat(results.contains("p2.r1")).isTrue();
        } catch (final Exception e) {
            e.printStackTrace();
            fail("unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testMergePackageWithSameRuleNames() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_MergePackageWithSameRuleNames1.drl");
        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_MergePackageWithSameRuleNames2.drl").getKiePackages();

        kbase.addPackages(kpkgs);

        KieSession ksession = kbase.newKieSession();

        final List results = new ArrayList();
        ksession.setGlobal("results", results);
        ksession.fireAllRules();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo("rule1 for the package2");
    }

    @Test
    public void testMergingDifferentPackages() throws Exception {
        // using the same builder
        try {
            Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_RuleNameClashes1.drl", "test_RuleNameClashes2.drl").getKiePackages();

            assertThat(kpkgs.size()).isEqualTo(3);
            for (final KiePackage kpkg : kpkgs) {
                if (kpkg.getName().equals("org.drools.package1")) {
                    assertThat(kpkg.getRules().iterator().next().getName()).isEqualTo("rule 1");
                }
            }
        } catch (final RuntimeException e) {
            e.printStackTrace();
            fail("unexpected exception: " + e.getMessage());
        }
    }
}
