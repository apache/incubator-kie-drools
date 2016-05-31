/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.functional;

import org.assertj.core.api.Assertions;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.ResourceUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableInputType;

import java.io.StringReader;

/**
 * Tests loading of different types of resources (DRL, DSL, DRF, BPMN2, DTABLE).
 * Packages are loaded and built using KnowledgeBuilder.
 */
public class ResourcesTest {

    @Test
    public void testDRL() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), true, "aggregation.drl");

        // since 6.2.x java.lang is also returned as a package
        Assertions.assertThat((long) kbase.getKiePackages().size()).as("Unexpected number of KiePackages").isEqualTo((long) 3);
        verifyPackageWithRules(kbase, TestConstants.PACKAGE_FUNCTIONAL, 4);
        verifyPackageWithImports(kbase, TestConstants.PACKAGE_TESTCOVERAGE_MODEL);
    }

    @Test
    public void testDSL() {
        // DSL must go before rules otherwise error is thrown during building
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), true, "sample.dsl", "sample.dslr");

        Assertions.assertThat((long) kbase.getKiePackages().size()).as("Unexpected number of KiePackages").isEqualTo((long) 1);
        verifyPackageWithRules(kbase, TestConstants.PACKAGE_FUNCTIONAL, 1);
    }

    @Test
    public void testXLS() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), true, "sample.xls");

        Assertions.assertThat((long) kbase.getKiePackages().size()).as("Unexpected number of packages in kbase").isEqualTo((long) 2);

        verifyPackageWithRules(kbase, TestConstants.PACKAGE_FUNCTIONAL, 3);
        verifyPackageWithImports(kbase, TestConstants.PACKAGE_TESTCOVERAGE_MODEL);
    }

    @Test
    public void testCSV() {
        final Resource decisionTable =
                ResourceUtil.getDecisionTableResourceFromClasspath("sample.csv", getClass(), DecisionTableInputType.CSV);
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);

        Assertions.assertThat((long) kbase.getKiePackages().size()).as("Unexpected number of packages in kbase").isEqualTo((long) 2);

        verifyPackageWithRules(kbase, TestConstants.PACKAGE_FUNCTIONAL, 3);
        verifyPackageWithImports(kbase, TestConstants.PACKAGE_TESTCOVERAGE_MODEL);
    }

    @Test
    public void testRuleTemplate() {
        // first we compile the decision table into a whole lot of rules.
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();

        // the data we are interested in starts at row 2, column 2 (e.g. B2)
        final String drl = converter.compile(getClass().getResourceAsStream("sample_cheese.xls"), getClass()
                .getResourceAsStream("sample_cheese.drt"), 2, 2);

        // compile the drl
        final Resource res = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drl));
        res.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, res);

        Assertions.assertThat((long) kbase.getKiePackages().size()).as("Unexpected number of packages in kbase").isEqualTo((long) 2);

        verifyPackageWithRules(kbase, TestConstants.PACKAGE_FUNCTIONAL, 2);
        verifyPackageWithImports(kbase, TestConstants.PACKAGE_TESTCOVERAGE_MODEL);
    }

    @Test
    public void testWrongExtension() {
        final String drl = "package org.drools.testcoverage.functional\n" +
                "import org.drools.testcoverage.common.model.Message\n" +
                "rule sampleRule\n" +
                "    when\n" +
                "        $m : Message( )\n" +
                "    then\n" +
                "end\n";

        final Resource res = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drl))
                  .setResourceType(ResourceType.DRL).setSourcePath("src/main/resources/r1.txt");
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, res);

        verifyPackageWithRules(kbase, "org.drools.testcoverage.functional", 1);
    }

    private void verifyPackageWithRules(final KieBase kbase, final String packageName, final int expectedRules) {

        final KiePackage pack = kbase.getKiePackage(packageName);

        Assertions.assertThat(pack).as("KiePackage with given name not found in KieBase").isNotNull();
        Assertions.assertThat(pack.getName()).as("Unexpected package name").isEqualTo(packageName);
        Assertions.assertThat((long) pack.getRules().size()).as("Unexpected number of rules").isEqualTo((long) expectedRules);
    }

    private void verifyPackageWithImports(final KieBase kbase, final String packageName) {

        final KiePackage pack = kbase.getKiePackage(packageName);

        Assertions.assertThat(pack).as("KiePackage with given name not found in KieBase").isNotNull();
        Assertions.assertThat(pack.getName()).as("Unexpected package name").isEqualTo(packageName);
        Assertions.assertThat((long) pack.getRules().size()).as("Package with import should contain no rules").isEqualTo((long) 0);
    }
}
