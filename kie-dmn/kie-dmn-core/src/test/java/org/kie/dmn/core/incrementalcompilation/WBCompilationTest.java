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
package org.kie.dmn.core.incrementalcompilation;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class WBCompilationTest {

    static final String DMN_1 = "<?xml version=\"1.0\" ?>\n" +
                                "<dmn:definitions xmlns:dmn=\"https://www.omg.org/spec/DMN/20230324/MODEL/\" xmlns=\"https://github.com/kiegroup/drools/kie-dmn/_E084990F-18BF-4D46-8617-56CF6D8B86B6\" xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\" xmlns:kie=\"http://www.drools.org/kie/dmn/1.5\" xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" xmlns:dmndi=\"http://www.omg.org/spec/DMN/20180521/DMNDI/\" xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\" id=\"_F325E2BC-2565-45DD-A8E9-77A0D37F0875\" name=\"dmn1\" expressionLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" typeLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" namespace=\"https://github.com/kiegroup/drools/kie-dmn/_E084990F-18BF-4D46-8617-56CF6D8B86B6\">\n" +
                                "  <dmn:extensionElements></dmn:extensionElements>\n" +
                                "  <dmn:decision id=\"_8DDC5332-1141-4338-B413-D6F217B8F937\" name=\"Import me\">\n" +
                                "    <dmn:variable id=\"_323CB438-6468-456A-8235-3EBF2F3D7100\" name=\"Import me\"></dmn:variable>\n" +
                                "    <dmn:literalExpression id=\"_ED4FF7B2-7A47-4814-995E-B8903BB8C013\">\n" +
                                "      <dmn:text>1</dmn:text>\n" +
                                "    </dmn:literalExpression>\n" +
                                "  </dmn:decision>\n" +
                                "  <dmndi:DMNDI>\n" +
                                "    <dmndi:DMNDiagram>\n" +
                                "      <di:extension>\n" +
                                "        <kie:ComponentsWidthsExtension>\n" +
                                "          <kie:ComponentWidths dmnElementRef=\"_ED4FF7B2-7A47-4814-995E-B8903BB8C013\">\n" +
                                "            <kie:width>130.0</kie:width>\n" +
                                "          </kie:ComponentWidths>\n" +
                                "        </kie:ComponentsWidthsExtension>\n" +
                                "      </di:extension>\n" +
                                "      <dmndi:DMNShape id=\"dmnshape-_8DDC5332-1141-4338-B413-D6F217B8F937\" dmnElementRef=\"_8DDC5332-1141-4338-B413-D6F217B8F937\" isCollapsed=\"false\">\n" +
                                "        <dmndi:DMNStyle>\n" +
                                "          <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"></dmndi:FillColor>\n" +
                                "          <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"></dmndi:StrokeColor>\n" +
                                "          <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"></dmndi:FontColor>\n" +
                                "        </dmndi:DMNStyle>\n" +
                                "        <dc:Bounds x=\"254\" y=\"146\" width=\"100\" height=\"50\"></dc:Bounds>\n" +
                                "        <dmndi:DMNLabel></dmndi:DMNLabel>\n" +
                                "      </dmndi:DMNShape>\n" +
                                "    </dmndi:DMNDiagram>\n" +
                                "  </dmndi:DMNDI>\n" +
                                "</dmn:definitions>";

    static final String DMN_2 = "<?xml version=\"1.0\" ?>\n" +
                                "<dmn:definitions xmlns:dmn=\"https://www.omg.org/spec/DMN/20230324/MODEL/\" xmlns=\"https://github.com/kiegroup/drools/kie-dmn/_A496D41E-074B-415A-98CB-08833A7A7A7B\" xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\" xmlns:kie=\"http://www.drools.org/kie/dmn/1.5\" xmlns:imported=\"https://github.com/kiegroup/drools/kie-dmn/_E084990F-18BF-4D46-8617-56CF6D8B86B6\" xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" xmlns:dmndi=\"http://www.omg.org/spec/DMN/20180521/DMNDI/\" xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\" id=\"_D127BE9B-D616-4850-A67A-F5E4F875ECDA\" name=\"dmn2\" expressionLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" typeLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" namespace=\"https://github.com/kiegroup/drools/kie-dmn/_A496D41E-074B-415A-98CB-08833A7A7A7B\">\n" +
                                "  <dmn:extensionElements></dmn:extensionElements>\n" +
                                "  <dmn:import id=\"_26A5DCEC-FCC0-4859-8EDB-80F3E4CE6710\" name=\"imported\" namespace=\"https://github.com/kiegroup/drools/kie-dmn/_E084990F-18BF-4D46-8617-56CF6D8B86B6\" locationURI=\"default://DEFAULT_BRANCH@MySpace/example-Mortgages/src/main/resources/mortgages/mortgages/dmn1.dmn\" importType=\"https://www.omg.org/spec/DMN/20230324/MODEL/\"></dmn:import>\n" +
                                "  <dmndi:DMNDI>\n" +
                                "    <dmndi:DMNDiagram>\n" +
                                "      <di:extension>\n" +
                                "        <kie:ComponentsWidthsExtension></kie:ComponentsWidthsExtension>\n" +
                                "      </di:extension>\n" +
                                "    </dmndi:DMNDiagram>\n" +
                                "  </dmndi:DMNDI>\n" +
                                "</dmn:definitions>";

    @Test
    void steppedCompilation() {
        KieServices ks = KieServices.Factory.get();

        ReleaseId id = ks.newReleaseId("org.test", "foo", "1.0-SNAPSHOT");
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(id);

        kfs.write("src/main/resources/org/kie/scanner/dmn1.dmn", DMN_1);
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(DrlProject.class);
        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR)).hasSize(0);

        kfs.write("src/main/resources/org/kie/scanner/dmn2.dmn", DMN_2);
        IncrementalResults addResults = ((InternalKieBuilder) kieBuilder).createFileSet("src/main/resources/org/kie/scanner/dmn2.dmn").build();
        assertThat(addResults.getAddedMessages()).hasSize(0);
        assertThat(addResults.getRemovedMessages()).hasSize(0);

        KieContainer kieContainer = ks.newKieContainer(id);
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertThat(runtime.getModels()).hasSize(2);
    }

    @Test
    void steppedCompilationFromEmptyKbuilder() {
        // DROOLS-5584
        KieServices ks = KieServices.Factory.get();

        ReleaseId id = ks.newReleaseId("org.test", "foo", "1.0-SNAPSHOT");
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(id);

        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(DrlProject.class);
        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR)).hasSize(0);

        kfs.write("src/main/resources/org/kie/scanner/dmn1.dmn", DMN_1);
        IncrementalResults addResults = ((InternalKieBuilder) kieBuilder).createFileSet("src/main/resources/org/kie/scanner/dmn1.dmn").build();
        assertThat(addResults.getAddedMessages()).hasSize(0);
        assertThat(addResults.getRemovedMessages()).hasSize(0);

        kfs.write("src/main/resources/org/kie/scanner/dmn2.dmn", DMN_2);
        addResults = ((InternalKieBuilder) kieBuilder).createFileSet("src/main/resources/org/kie/scanner/dmn2.dmn").build();
        assertThat(addResults.getAddedMessages()).hasSize(0);
        assertThat(addResults.getRemovedMessages()).hasSize(0);

        KieContainer kieContainer = ks.newKieContainer(id);
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertThat(runtime.getModels()).hasSize(2);
    }
}
