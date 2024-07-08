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
package org.kie.dmn.core.alphanetwork;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.compiler.AlphaNetworkOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.classloader.DMNClassloaderTest.getPom;

public class AlphaNetworkSupportInLargeDecisionTableTest {

    public static Object[] params() {
        return new Object[]{true, false};
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void evaluateDecisionTable(final boolean useAlphaNetwork) {
        System.setProperty(AlphaNetworkOption.PROPERTY_NAME, Boolean.toString(useAlphaNetwork));
        KieServices kieServices = KieServices.get();

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId = ks.newReleaseId("org.kie.dmn.core.alphanetwork", "alphaNetworkSupportInLargeDecisionTable", UUID.randomUUID().toString());

        final KieFileSystem kfs = ks.newKieFileSystem();
        int numberOfDecisionTableRules = 1000;
        Resource dmnResource = kieServices.getResources()
                .newReaderResource(new StringReader(createDMN(numberOfDecisionTableRules)))
                .setResourceType(ResourceType.DMN)
                .setSourcePath("dmnFile.dmn");

        kfs.write(dmnResource);
        kfs.writePomXML(getPom(releaseId));

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertThat(kieBuilder.getResults().getMessages()).as(kieBuilder.getResults().getMessages().toString()).isEmpty();

        final KieContainer container = ks.newKieContainer(releaseId);
        DMNRuntime dmnRuntime = KieRuntimeFactory.of(container.getKieBase()).get(DMNRuntime.class);

        DMNModel dmnModel = dmnRuntime.getModel("https://github.com/kiegroup/kie-dmn", "decision-table-name");

        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Age", BigDecimal.valueOf(18));
        dmnContext.set("RiskCategory", "Medium");
        dmnContext.set("isAffordable", true);

        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getDecisionResultById("decision-table").getResult()).isEqualTo("Declined");
    }

    public String createDMN(int numberOfTableRules) {
        final StringBuilder dmnBuilder = new StringBuilder();

        dmnBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        dmnBuilder.append("<definitions id=\"decision-table-id\" name=\"decision-table-name\"\n");
        dmnBuilder.append("             namespace=\"https://github.com/kiegroup/kie-dmn\"\n");
        dmnBuilder.append("             xmlns=\"http://www.omg.org/spec/DMN/20151101/dmn.xsd\"\n");
        dmnBuilder.append("             xmlns:feel=\"http://www.omg.org/spec/FEEL/20140401\"\n");
        dmnBuilder.append("             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        dmnBuilder.append("             xsi:schemaLocation=\"http://www.omg.org/spec/DMN/20151101/dmn.xsd\">\n");

        dmnBuilder.append("  <decision id=\"decision-table\" name=\"decision-table\">\n");
        dmnBuilder.append("    <variable name=\"Approval Status\" typeRef=\"feel:string\"/>\n");
        dmnBuilder.append("    <informationRequirement>\n");
        dmnBuilder.append("      <requiredInput href=\"#_Age\"/>\n");
        dmnBuilder.append("    </informationRequirement>\n");
        dmnBuilder.append("    <informationRequirement>\n");
        dmnBuilder.append("      <requiredInput href=\"#_RiskCategory\"/>\n");
        dmnBuilder.append("    </informationRequirement>\n");
        dmnBuilder.append("    <informationRequirement>\n");
        dmnBuilder.append("      <requiredInput href=\"#_isAffordable\"/>\n");
        dmnBuilder.append("    </informationRequirement>\n");
        dmnBuilder.append("    <decisionTable hitPolicy=\"FIRST\" outputLabel=\"Approval Status\" preferredOrientation=\"Rule-as-Row\">\n");
        dmnBuilder.append("      <input id=\"_iAge\" label=\"Age\">\n");
        dmnBuilder.append("        <inputExpression typeRef=\"feel:number\">\n");
        dmnBuilder.append("          <text>Age</text>\n");
        dmnBuilder.append("        </inputExpression>\n");
        dmnBuilder.append("      </input>\n");
        dmnBuilder.append("      <input id=\"_iRiskCategory\" label=\"RiskCategory\">\n");
        dmnBuilder.append("        <inputExpression typeRef=\"feel:string\">\n");
        dmnBuilder.append("          <text>RiskCategory</text>\n");
        dmnBuilder.append("        </inputExpression>\n");
        dmnBuilder.append("        <inputValues>\n");
        dmnBuilder.append("          <text>\"High\", \"Low\", \"Medium\"</text>\n");
        dmnBuilder.append("        </inputValues>\n");
        dmnBuilder.append("      </input>\n");
        dmnBuilder.append("      <input id=\"_iIsAffordable\" label=\"isAffordable\">\n");
        dmnBuilder.append("        <inputExpression typeRef=\"feel:boolean\">\n");
        dmnBuilder.append("          <text>isAffordable</text>");
        dmnBuilder.append("        </inputExpression>\n");
        dmnBuilder.append("      </input>\n");
        dmnBuilder.append("      <output id=\"_oApprovalStatus\">\n");
        dmnBuilder.append("        <outputValues>\n");
        dmnBuilder.append("          <text>\"Approved\", \"Declined\"</text>\n");
        dmnBuilder.append("        </outputValues>\n");
        dmnBuilder.append("      </output>\n");

        for (int i = 0; i < numberOfTableRules; i++) {
            dmnBuilder.append(createRuleEntry(i));
        }

        dmnBuilder.append("    </decisionTable>\n");
        dmnBuilder.append("  </decision>\n");

        dmnBuilder.append("  <inputData id=\"_Age\" name=\"Age\">\n");
        dmnBuilder.append("    <variable name=\"Age\" typeRef=\"feel:number\"/>\n");
        dmnBuilder.append("  </inputData>\n");
        dmnBuilder.append("  <inputData id=\"_RiskCategory\" name=\"RiskCategory\">\n");
        dmnBuilder.append("    <variable name=\"RiskCategory\" typeRef=\"feel:string\"/>\n");
        dmnBuilder.append("  </inputData>\n");
        dmnBuilder.append("  <inputData id=\"_isAffordable\" name=\"isAffordable\">\n");
        dmnBuilder.append("    <variable name=\"isAffordable\" typeRef=\"feel:boolean\"/>\n");
        dmnBuilder.append("  </inputData>\n");
        dmnBuilder.append("</definitions>");

        return dmnBuilder.toString();
    }

    private String createRuleEntry(final int index) {
        final StringBuilder ruleBuilder = new StringBuilder();
        ruleBuilder.append("      <rule id=\"rule" + index + "\">\n");
        ruleBuilder.append("        <inputEntry id=\"inputEntry" + index + "-1\">\n");
        ruleBuilder.append("          <text>>=" + index + "</text>\n");
        ruleBuilder.append("        </inputEntry>\n");
        ruleBuilder.append("        <inputEntry id=\"inputEntry" + index + "-2\">\n");
        ruleBuilder.append("          <text>\"Medium\",\"Low\"</text>\n");
        ruleBuilder.append("        </inputEntry>\n");
        ruleBuilder.append("        <inputEntry id=\"inputEntry" + index + "-3\">\n");
        ruleBuilder.append("          <text>true</text>\n");
        ruleBuilder.append("        </inputEntry>\n");
        ruleBuilder.append("        <outputEntry id=\"outputEntry" + index + "\">\n");
        if (index % 3 == 0) {
            ruleBuilder.append("          <text>\"Declined\"</text>");
        } else {
            ruleBuilder.append("          <text>\"Approved\"</text>");
        }
        ruleBuilder.append("        </outputEntry>\n");
        ruleBuilder.append("      </rule>\n");
        return ruleBuilder.toString();
    }
}
