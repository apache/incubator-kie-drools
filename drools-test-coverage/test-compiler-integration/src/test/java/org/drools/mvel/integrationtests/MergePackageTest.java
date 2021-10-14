/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
            assertEquals(1, kpkg1.getRules().size());

            Collection<KiePackage> kpkgs2 = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_RuleNameClashes2.drl").getKiePackages();
            KiePackage kpkg2 = kpkgs2.stream().filter( pkg -> pkg.getName().equals( "org.drools.package2" ) ).findFirst().get();
            assertEquals(1, kpkg2.getRules().size());

            InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration);
            kbase.addPackages(kpkgs1);
            kbase.addPackages(kpkgs2);
            KieSession ksession = kbase.newKieSession();

            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            ksession.insert(new Cheese("stilton", 10));
            ksession.insert(new Cheese("brie", 5));

            ksession.fireAllRules();

            assertEquals(results.toString(), 2, results.size());
            assertTrue(results.contains("p1.r1"));
            assertTrue(results.contains("p2.r1"));
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
        assertEquals(1, results.size());
        assertEquals("rule1 for the package2", results.get(0));
    }

    @Test
    public void testMergingDifferentPackages() throws Exception {
        // using the same builder
        try {
            Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromClasspathResources("tmp", getClass(), kieBaseTestConfiguration, "test_RuleNameClashes1.drl", "test_RuleNameClashes2.drl").getKiePackages();

            assertEquals(3, kpkgs.size());
            for (final KiePackage kpkg : kpkgs) {
                if (kpkg.getName().equals("org.drools.package1")) {
                    assertEquals("rule 1", kpkg.getRules().iterator().next().getName());
                }
            }
        } catch (final RuntimeException e) {
            e.printStackTrace();
            fail("unexpected exception: " + e.getMessage());
        }
    }
}
