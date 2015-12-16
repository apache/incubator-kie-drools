/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.guided.template.backend;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.Collections;

public class TemplateWithDSLIntegrationTest {

    @Test
    public void testCompileTemplateWithDSL() {
        String template = "<rule>\n" +
                "  <name>guided-template-with-dsl</name>\n" +
                "  <modelVersion>1.0</modelVersion>\n" +
                "  <attributes/>\n" +
                "  <metadataList/>\n" +
                "  <lhs>\n" +
                "    <dslSentence>\n" +
                "      <drl>applicant:" + Applicant.class.getCanonicalName() + "(approved=={bool})</drl>\n" +
                "      <definition>When the applicant approval is {bool:BOOLEAN:checked}</definition>\n" +
                "      <values>\n" +
                "        <org.drools.workbench.models.datamodel.rule.DSLVariableValue>\n" +
                "          <value>false</value>\n" +
                "        </org.drools.workbench.models.datamodel.rule.DSLVariableValue>\n" +
                "        <org.drools.workbench.models.datamodel.rule.DSLComplexVariableValue>\n" +
                "          <value>bool</value>\n" +
                "          <id>BOOLEAN:checked</id>\n" +
                "        </org.drools.workbench.models.datamodel.rule.DSLComplexVariableValue>\n" +
                "      </values>\n" +
                "    </dslSentence>\n" +
                "  </lhs>\n" +
                "  <rhs>\n" +
                "    <dslSentence>\n" +
                "      <drl>applicant.setApproved(true)</drl>\n" +
                "      <definition>Approve the loan</definition>\n" +
                "      <values/>\n" +
                "    </dslSentence>\n" +
                "  </rhs>\n" +
                "  <imports>\n" +
                "    <imports/>\n" +
                "  </imports>\n" +
                "  <packageName>org.mortgages</packageName>\n" +
                "  <isNegated>false</isNegated>\n" +
                "  <table>\n" +
                "    <entry>\n" +
                "      <string>__ID_KOL_NAME__</string>\n" +
                "      <list>\n" +
                "        <string>1</string>\n" +
                "      </list>\n" +
                "    </entry>\n" +
                "  </table>\n" +
                "  <idCol>1</idCol>\n" +
                "  <rowsCount>1</rowsCount>\n" +
                "</rule>";

        String dsl = "[when]When the applicant approval is {bool:BOOLEAN:checked} = applicant:" + Applicant.class.getCanonicalName() + "(approved=={bool})\n" +
                "[then]Approve the loan = applicant.setApproved(true)";

        KieHelper kieHelper = new KieHelper();
        KieSession kieSession = kieHelper
                .addContent(template, ResourceType.TEMPLATE)
                .addContent(dsl, ResourceType.DSL)
                .build()
                .newKieSession();

        Applicant applicant = new Applicant();
        applicant.setApproved(false);
        kieSession.insert(applicant);
        int rulesFired = kieSession.fireAllRules();
        Assert.assertEquals("Incorrect number of rules fired!", 1, rulesFired);
        Assert.assertEquals("Rule RHS wasn't triggered!", true, applicant.isApproved());
    }

    public static class Applicant {
        private boolean approved;

        public boolean isApproved() {
            return approved;
        }

        public void setApproved(boolean approved) {
            this.approved = approved;
        }
    }

}
