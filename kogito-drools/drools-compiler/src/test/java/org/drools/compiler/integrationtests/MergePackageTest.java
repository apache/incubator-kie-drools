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

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

public class MergePackageTest extends CommonTestMethodBase {

    @Test
    public void testMergingDifferentPackages2() throws Exception {
        // using different builders
        try {
            final Collection<KiePackage> kpkgs1 = loadKnowledgePackages("test_RuleNameClashes1.drl");
            KiePackage kpkg1 = kpkgs1.stream().filter( pkg -> pkg.getName().equals( "org.drools.package1" ) ).findFirst().get();
            assertEquals(1, kpkg1.getRules().size());

            final Collection<KiePackage> kpkgs2 = loadKnowledgePackages("test_RuleNameClashes2.drl");
            KiePackage kpkg2 = kpkgs2.stream().filter( pkg -> pkg.getName().equals( "org.drools.package2" ) ).findFirst().get();
            assertEquals(1, kpkg2.getRules().size());

            InternalKnowledgeBase kbase = (InternalKnowledgeBase) loadKnowledgeBase();
            kbase.addPackages(kpkgs1);
            kbase.addPackages(kpkgs2);
            kbase = SerializationHelper.serializeObject(kbase);
            final KieSession ksession = createKnowledgeSession(kbase);

            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            ksession.insert(new Cheese("stilton", 10));
            ksession.insert(new Cheese("brie", 5));

            ksession.fireAllRules();

            assertEquals(results.toString(), 2, results.size());
            assertTrue(results.contains("p1.r1"));
            assertTrue(results.contains("p2.r1"));
        } catch (final KnowledgeBuilderImpl.PackageMergeException e) {
            fail("Should not raise exception when merging different packages into the same rulebase: " + e.getMessage());
        } catch (final Exception e) {
            e.printStackTrace();
            fail("unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testMergePackageWithSameRuleNames() throws Exception {
        final InternalKnowledgeBase kbase =
                (InternalKnowledgeBase) SerializationHelper.serializeObject(loadKnowledgeBase("test_MergePackageWithSameRuleNames1.drl"));
        final Collection<KiePackage> kpkgs =
                loadKnowledgePackages("test_MergePackageWithSameRuleNames2.drl");
        kbase.addPackages(kpkgs);

        final KieSession ksession = createKnowledgeSession(kbase);

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
            final Collection<KiePackage> kpkgs =
                    loadKnowledgePackages("test_RuleNameClashes1.drl", "test_RuleNameClashes2.drl");
            assertEquals(3, kpkgs.size());
            for (final KiePackage kpkg : kpkgs) {
                if (kpkg.getName().equals("org.drools.package1")) {
                    assertEquals("rule 1", kpkg.getRules().iterator().next().getName());
                }
            }
        } catch (final KnowledgeBuilderImpl.PackageMergeException e) {
            fail("unexpected exception: " + e.getMessage());
        } catch (final RuntimeException e) {
            e.printStackTrace();
            fail("unexpected exception: " + e.getMessage());
        }
    }
}
