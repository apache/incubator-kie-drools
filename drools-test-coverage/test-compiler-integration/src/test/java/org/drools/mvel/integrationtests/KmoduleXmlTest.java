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

import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;

import static org.assertj.core.api.Assertions.assertThat;

public class KmoduleXmlTest {

    enum Element {
        KBASE,
        KSESSION
    }
    
    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void kbaseEmptyName(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        List<Message> errors = buildKmoduleWithEmptyValue(kieBaseTestConfiguration, "name", Element.KBASE);

        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getText()).contains("kbase name is empty in kmodule.xml");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void kbaseEmptyIncludes(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        List<Message> errors = buildKmoduleWithEmptyValue(kieBaseTestConfiguration, "includes", Element.KBASE);

        assertThat(errors).as("Empty includes is fine. It's ignored")
                          .isEmpty();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void kbaseEmptyPackages(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        List<Message> errors = buildKmoduleWithEmptyValue(kieBaseTestConfiguration, "packages", Element.KBASE);

        assertThat(errors).as("Empty packages is fine. It means the default package")
                          .isEmpty();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void ksessionEmptyName(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        List<Message> errors = buildKmoduleWithEmptyValue(kieBaseTestConfiguration, "name", Element.KSESSION);

        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getText()).contains("ksession name is empty in kmodule.xml");
    }

    private List<Message> buildKmoduleWithEmptyValue(KieBaseTestConfiguration kieBaseTestConfiguration, String emptyAttribute, Element element) throws Exception {
        String drl =
                "package org.example\n" +
                     "rule R1 when\n" +
                     "then\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/org/example/r1.drl", drl);
        kfs.write("src/main/resources/META-INF/kmodule.xml", getKmoduleString(emptyAttribute, element));
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        return kieBuilder.getResults().getMessages(Message.Level.ERROR);
    }

    private String getKmoduleString(String emptyAttribute, Element element) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
        if (element == Element.KBASE) {
            // if kbase name is omitted, UUID is given to its name
            sb.append("<kbase " + emptyAttribute + "=\"\" default=\"true\">\n");
            sb.append("  <ksession name=\"myKsession\" default=\"true\"/>\n");
        } else if (element == Element.KSESSION) {
            sb.append("<kbase name=\"myKbase\" default=\"true\">\n");
            // if you test an attribute other than "name" in ksession, you need to add "name" attribute as it's required
            sb.append("  <ksession " + emptyAttribute + "=\"\" default=\"true\"/>\n");
        } else {
            throw new IllegalArgumentException("Unsupported element : " + element);
        }
        sb.append("</kbase>\n");
        sb.append("</kmodule>\n");
        return sb.toString();
    }
}
