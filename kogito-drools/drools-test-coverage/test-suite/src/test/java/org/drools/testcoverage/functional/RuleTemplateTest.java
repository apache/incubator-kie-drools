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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Customer;
import org.drools.testcoverage.common.model.Message;
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
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        final Resource table = kieServices.getResources().newClassPathResource("sample_cheese.xls", getClass());
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

        Assertions.assertThat(pkgs.size()).isEqualTo(2);

        final ArrayList<String> names = new ArrayList<String>();

        for (KiePackage kp : pkgs) {
            names.add(kp.getName());
        }

        Assertions.assertThat(names.contains(TestConstants.PACKAGE_FUNCTIONAL)).isTrue();
        Assertions.assertThat(names.contains(TestConstants.PACKAGE_TESTCOVERAGE_MODEL)).isTrue();

        final KiePackage kiePackage = (KiePackage) pkgs.toArray()[names.indexOf(TestConstants.PACKAGE_FUNCTIONAL)];

        Assertions.assertThat(kiePackage.getRules().size()).isEqualTo(2);

        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Cheese("stilton", 42));
        ksession.insert(new Person("michael", "stilton", 42));
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        LOGGER.debug(list.toString());

        ksession.dispose();
    }

    @Test
    public void testGuidedRuleTemplate() throws Exception {
        final String resourceName = "cheese.template";
        final KieResources kieResources = KieServices.get().getResources();
        final Resource resource = kieResources.newClassPathResource(resourceName, RuleTemplateTest.class);
        resource.setResourceType(ResourceType.TEMPLATE);
        final KieBase kBase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, resource);

        final KieSession kSession = kBase.newKieSession();

        final Cheese cheese = new Cheese();
        cheese.setPrice(90);

        final Customer petr = new Customer(0, "Peter");
        final Customer john = new Customer(1, "John");

        kSession.insert(cheese);
        kSession.insert(petr);
        kSession.insert(john);

        Assertions.assertThat(kSession.fireAllRules()).as("One rule should be fired").isEqualTo(1);
        final Collection messages = kSession.getObjects(object -> object instanceof Message);
        Assertions.assertThat(messages).hasSize(1);
        Assertions.assertThat(messages).hasOnlyOneElementSatisfying(message -> ((Message) message).getMessage().compareTo("Peter satisfied"));
    }
}
