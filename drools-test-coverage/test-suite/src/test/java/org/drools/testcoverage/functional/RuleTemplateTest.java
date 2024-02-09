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
package org.drools.testcoverage.functional;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RuleTemplateTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleTemplateTest.class);

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RuleTemplateTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testSampleCheese() {
        // first we compile the decision table into a whole lot of rules.
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();

        final KieServices kieServices = KieServices.Factory.get();

        final Resource table = kieServices.getResources().newClassPathResource("sample_cheese.drl.xls", getClass());
        final Resource template = kieServices.getResources().newClassPathResource("sample_cheese.drt", getClass());

        String drl = null;
        try {
            // the data we are interested in starts at row 2, column 2 (i.e. B2)
            drl = converter.compile(table.getInputStream(), template.getInputStream(), 2, 2);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read spreadsheet or rules stream.", e);
        }

        // compile the drl
        final Resource drlResource = kieServices.getResources().newReaderResource(new StringReader(drl));
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);

        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, drlResource);
        final Collection<KiePackage> pkgs = kbase.getKiePackages();

        assertThat(pkgs.size()).isEqualTo(2);

        final ArrayList<String> names = new ArrayList<String>();

        for (KiePackage kp : pkgs) {
            names.add(kp.getName());
        }

        assertThat(names.contains(TestConstants.PACKAGE_FUNCTIONAL)).isTrue();
        assertThat(names.contains(TestConstants.PACKAGE_TESTCOVERAGE_MODEL)).isTrue();

        final KiePackage kiePackage = (KiePackage) pkgs.toArray()[names.indexOf(TestConstants.PACKAGE_FUNCTIONAL)];

        assertThat(kiePackage.getRules().size()).isEqualTo(2);

        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Cheese("stilton", 42));
        ksession.insert(new Person("michael", "stilton", 42));
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        LOGGER.debug(list.toString());

        ksession.dispose();
    }
}
