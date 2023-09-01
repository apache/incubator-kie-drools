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
package org.drools.decisiontable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.TemplateDataListener;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *         basic tests for converter utility. Note that some of this may
 *         still use the drools 2.x syntax, as it is not compiled, only tested
 *         that it generates DRL in the correct structure (not that the DRL
 *         itself is correct).
 */
public class ExternalSpreadsheetCompilerTest {
    
    private ExternalSpreadsheetCompiler converter;

    @Before
    public void setUp() {
        converter = new ExternalSpreadsheetCompiler();
    }
    
    @Test
    public void testLoadFromClassPath() {
        final String drl = converter.compile("/data/MultiSheetDST.drl.xls",
                                              "/templates/test_template1.drl",
                                              11,
                                              2);
        assertThat(drl).isNotNull();

        assertThat(drl.indexOf("rule \"How cool is Shaun 12\"") > 0).isTrue();
        assertThat(drl.indexOf("rule \"How cool is Kumar 11\"") > 0).isTrue();
        assertThat(drl).contains("import example.model.User;");
        assertThat(drl).contains("import example.model.Car;");
    }

    @Test
    public void testLoadSpecificWorksheet() {
        final String drl = converter.compile("/data/MultiSheetDST.drl.xls",
                                              "Another Sheet",
                                              "/templates/test_template1.drl",
                                              11,
                                              2);
        assertThat(drl).isNotNull();
    }

    @Test
    public void testLoadCsv() {
        final String drl = converter.compile("/data/ComplexWorkbook.drl.csv",
                                              "/templates/test_template2.drl",
                                              InputType.CSV,
                                              10,
                                              2);
        assertThat(drl).isNotNull();

        assertThat(drl.indexOf("myObject.setIsValid(1, 2)") > 0).isTrue();
        assertThat(drl.indexOf("myObject.size () > 2") > 0).isTrue();

        assertThat(drl.indexOf("Foo(myObject.getColour().equals(red),\n        myObject.size () > 1") > 0).isTrue();
    }

    @Test
    public void testLoadBasicWithMergedCells() {
        final String drl = converter.compile("/data/BasicWorkbook.drl.xls",
                                              "/templates/test_template3.drl",
                                              InputType.XLS,
                                              10,
                                              2);

        final String drl1 = converter.compile("/data/BasicWorkbook.drl.xls",
                                               "/templates/test_template3.drl",
                                               InputType.XLS,
                                               21,
                                               2);

        assertThat(drl).isNotNull();

        Pattern p = Pattern.compile(".*setIsValid\\(Y\\).*setIsValid\\(Y\\).*setIsValid\\(Y\\).*",
                                     Pattern.DOTALL | Pattern.MULTILINE);
        Matcher m = p.matcher(drl);
        assertThat(m.matches()).isTrue();

        assertThat(drl).contains("This is a function block");
        assertThat(drl).contains("global Class1 obj1;");
        assertThat(drl1).contains("myObject.setIsValid(10-Jul-1974)");
        assertThat(drl).contains("myObject.getColour().equals(blue)");
        assertThat(drl).contains("Foo(myObject.getColour().equals(red), myObject.size() > 12\")");

        assertThat(drl).contains("b: Bar()\n        eval(myObject.size() < 3)");
        assertThat(drl).contains("b: Bar()\n        eval(myObject.size() < 9)");

        assertThat(drl.indexOf("Foo(myObject.getColour().equals(red), myObject.size() > 1)") < drl.indexOf("b: Bar()\n        eval(myObject.size() < 3)")).isTrue();

    }

    @Test
    public void testLoadBasicWithExtraCells() {
        final String drl = converter.compile("/data/BasicWorkbook.drl.xls",
                                             "/templates/test_template4.drl",
                                             InputType.XLS,
                                             10,
                                             2);
        assertThat(drl).isNotNull();

        assertThat(drl).contains("This is a function block");
        assertThat(drl).contains("global Class1 obj1;");
        assertThat(drl).contains("myObject.getColour().equals(blue)");
        assertThat(drl).contains("Foo(myObject.getColour().equals(red), myObject.size() > 12\")");

        assertThat(drl).contains("b: Bar()\n        eval(myObject.size() < 3)");
        assertThat(drl).contains("b: Bar()\n        eval(myObject.size() < 9)");

        assertThat(drl.indexOf("Foo(myObject.getColour().equals(red), myObject.size() > 1)") < drl.indexOf("b: Bar()\n        eval(myObject.size() < 3)")).isTrue();
    }


    @Test
    public void testIntegration() throws Exception {
        final String drl = converter.compile("/data/IntegrationExampleTestForTemplates.drl.xls", "/templates/test_integration.drl", 18, 3);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        assertThat(kbuilder.hasErrors()).isFalse();

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession kSession = kbase.newKieSession();

        //ASSERT AND FIRE
        kSession.insert(new Cheese("stilton", 42));
        kSession.insert(new Person("michael", "stilton", 42));
        List<String> list = new ArrayList<>();
        kSession.setGlobal("list", list);

        kSession.fireAllRules();
        
        assertThat(list).hasSize(1);
    }

    @Test
    public void testPricing() throws Exception {
        final List<DataListener> listeners = new ArrayList<>();
        TemplateDataListener l1 = new TemplateDataListener(10, 3, "/templates/test_pricing1.drl");
        listeners.add(l1);
        TemplateDataListener l2 = new TemplateDataListener(30, 3, "/templates/test_pricing2.drl");
        listeners.add(l2);
        converter.compile("/data/ExamplePolicyPricing.drl.xls", InputType.XLS, listeners);

        //COMPILE
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(l1.renderDRL().getBytes()), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(l2.renderDRL().getBytes()), ResourceType.DRL);
        assertThat(kbuilder.hasErrors()).isFalse();

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession kSession = kbase.newKieSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        kSession.insert(driver);
        kSession.insert(policy);

        kSession.fireAllRules();

        int basePrice = policy.getBasePrice();
        
        assertThat(basePrice).isEqualTo(120);
    }

}
