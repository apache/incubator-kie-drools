/*
 * Copyright 2015 JBoss by Red Hat
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

package org.drools.workbench.models.guided.dtable.backend;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;

import java.util.Collections;

public class GuidedDTableParsingIntegrationTest {

    @Test
    public void testGuidedDTableWithDSLSuccessfullyCompiled() {
        String personDrl = "package org.drools.test\n" +
                "declare Person\n" +
                "  adult : boolean\n" +
                "end";
        String personDsl = "[when]When the person is adult = person:org.drools.test.Person(adult==\"true\")";

        String guidedDTable = "<decision-table52>\n" +
                "  <tableName>test</tableName>\n" +
                "  <rowNumberCol>\n" +
                "    <hideColumn>false</hideColumn>\n" +
                "    <width>-1</width>\n" +
                "  </rowNumberCol>\n" +
                "  <descriptionCol>\n" +
                "    <hideColumn>false</hideColumn>\n" +
                "    <width>-1</width>\n" +
                "  </descriptionCol>\n" +
                "  <metadataCols/>\n" +
                "  <attributeCols/>\n" +
                "  <conditionPatterns>\n" +
                "    <org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn>\n" +
                "      <hideColumn>false</hideColumn>\n" +
                "      <width>-1</width>\n" +
                "      <header>test</header>\n" +
                "      <constraintValueType>1</constraintValueType>\n" +
                "      <parameters/>\n" +
                "      <definition>\n" +
                "        <org.drools.workbench.models.datamodel.rule.DSLSentence>\n" +
                "          <drl>person:org.drools.test.Person(adult==\"true\")</drl>\n" +
                "          <definition>When the person is adult</definition>\n" +
                "          <values>\n" +
                "            <org.drools.workbench.models.datamodel.rule.DSLVariableValue>\n" +
                "              <value>false</value>\n" +
                "            </org.drools.workbench.models.datamodel.rule.DSLVariableValue>\n" +
                "          </values>\n" +
                "        </org.drools.workbench.models.datamodel.rule.DSLSentence>\n" +
                "      </definition>\n" +
                "      <childColumns>\n" +
                "        <org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn>\n" +
                "          <hideColumn>false</hideColumn>\n" +
                "          <width>-1</width>\n" +
                "          <header>test</header>\n" +
                "          <constraintValueType>1</constraintValueType>\n" +
                "          <fieldType>Boolean</fieldType>\n" +
                "          <parameters/>\n" +
                "          <varName></varName>\n" +
                "        </org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn>\n" +
                "      </childColumns>\n" +
                "    </org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn>\n" +
                "  </conditionPatterns>\n" +
                "  <actionCols/>\n" +
                "  <auditLog>\n" +
                "    <filter class=\"org.drools.guvnor.client.modeldriven.dt52.auditlog.DecisionTableAuditLogFilter\">\n" +
                "      <acceptedTypes>\n" +
                "        <entry>\n" +
                "          <string>INSERT_ROW</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>INSERT_COLUMN</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>DELETE_ROW</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>DELETE_COLUMN</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>UPDATE_COLUMN</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "      </acceptedTypes>\n" +
                "    </filter>\n" +
                "    <entries/>\n" +
                "  </auditLog>\n" +
                "  <packageName>org.drools.test</packageName>\n" +
                "  <tableFormat>EXTENDED_ENTRY</tableFormat>\n" +
                "  <data>\n" +
                "    <list>\n" +
                "      <value>\n" +
                "        <valueNumeric class=\"int\">1</valueNumeric>\n" +
                "        <valueString></valueString>\n" +
                "        <dataType>NUMERIC_INTEGER</dataType>\n" +
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "      <value>\n" +
                "        <valueString></valueString>\n" +
                "        <dataType>STRING</dataType>\n" +
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "      <value>\n" +
                "        <valueBoolean>false</valueBoolean>\n" +
                "        <valueString></valueString>\n" +
                "        <dataType>BOOLEAN</dataType>\n" +
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "    </list>\n" +
                "    <list>\n" +
                "      <value>\n" +
                "        <valueNumeric class=\"int\">2</valueNumeric>\n" +
                "        <valueString></valueString>\n" +
                "        <dataType>NUMERIC_INTEGER</dataType>\n" +
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "      <value>\n" +
                "        <valueString></valueString>\n" +
                "        <dataType>STRING</dataType>\n" +
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "      <value>\n" +
                "        <valueBoolean>true</valueBoolean>\n" +
                "        <valueString></valueString>\n" +
                "        <dataType>BOOLEAN</dataType>\n" +
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "    </list>\n" +
                "  </data>\n" +
                "</decision-table52>";

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(personDrl, ResourceType.DRL);
        kieHelper.addContent(personDsl, ResourceType.DSL);
        kieHelper.addContent(guidedDTable, ResourceType.GDST);

        Results buildResults = kieHelper.verify();
        Assert.assertEquals("No build failures", Collections.emptyList(), buildResults.getMessages(Message.Level.ERROR));
        // make sure the dtable was actually processed and knowledge packages created
        KieBase kieBase = kieHelper.build();
        Assert.assertNotNull("Rule generated from guided dtable not found!", kieBase.getRule("org.drools.test", "Row 1 test"));
    }

    @Test
    public void testGuidedDTableWithNoDSLSuccessfullyCompiled() {
        String personDrl = "package org.drools.test\n" +
                "declare Person\n" +
                "  adult : boolean\n" +
                "end";
        String guidedDTable = "<decision-table52>\n" +
                "  <tableName>test1</tableName>\n" +
                "  <rowNumberCol>\n" +
                "    <hideColumn>false</hideColumn>\n" +
                "    <width>-1</width>\n" +
                "  </rowNumberCol>\n" +
                "  <descriptionCol>\n" +
                "    <hideColumn>false</hideColumn>\n" +
                "    <width>-1</width>\n" +
                "  </descriptionCol>\n" +
                "  <metadataCols/>\n" +
                "  <attributeCols/>\n" +
                "  <conditionPatterns>\n" +
                "    <Pattern52>\n" +
                "      <factType>org.drools.test.Person</factType>\n" +
                "      <boundName>person</boundName>\n" +
                "      <isNegated>false</isNegated>\n" +
                "      <conditions>\n" +
                "        <condition-column52>\n" +
                "          <typedDefaultValue>\n" +
                "            <valueBoolean>false</valueBoolean>\n" +
                "            <valueString></valueString>\n" +
                "            <dataType>BOOLEAN</dataType>\n" +
                "            <isOtherwise>false</isOtherwise>\n" +
                "          </typedDefaultValue>\n" +
                "          <hideColumn>false</hideColumn>\n" +
                "          <width>-1</width>\n" +
                "          <header>Header</header>\n" +
                "          <constraintValueType>1</constraintValueType>\n" +
                "          <factField>adult</factField>\n" +
                "          <fieldType>Boolean</fieldType>\n" +
                "          <operator>==</operator>\n" +
                "          <parameters/>\n" +
                "        </condition-column52>\n" +
                "      </conditions>\n" +
                "      <window>\n" +
                "        <parameters/>\n" +
                "      </window>\n" +
                "    </Pattern52>\n" +
                "  </conditionPatterns>\n" +
                "  <actionCols/>\n" +
                "  <auditLog>\n" +
                "    <filter class=\"org.drools.guvnor.client.modeldriven.dt52.auditlog.DecisionTableAuditLogFilter\">\n" +
                "      <acceptedTypes>\n" +
                "        <entry>\n" +
                "          <string>INSERT_ROW</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>INSERT_COLUMN</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>DELETE_ROW</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>DELETE_COLUMN</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>UPDATE_COLUMN</string>\n" +
                "          <boolean>false</boolean>\n" +
                "        </entry>\n" +
                "      </acceptedTypes>\n" +
                "    </filter>\n" +
                "    <entries/>\n" +
                "  </auditLog>\n" +
                "  <imports>\n" +
                "    <imports/>\n" +
                "  </imports>\n" +
                "  <packageName>org.drools.test</packageName>\n" +
                "  <tableFormat>EXTENDED_ENTRY</tableFormat>\n" +
                "  <data>\n" +
                "    <list>\n" +
                "      <value>\n" +
                "        <valueNumeric class=\"int\">1</valueNumeric>\n" +
                "        <valueString></valueString>\n" +
                "        <dataType>NUMERIC_INTEGER</dataType>\n" +
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "      <value>\n" +
                "        <valueString></valueString>\n" +
                "        <dataType>STRING</dataType>\n" +
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "      <value>\n" +
                "        <valueBoolean>true</valueBoolean>\n" +
                "        <valueString></valueString>\n" +
                "        <dataType>BOOLEAN</dataType>\n" +
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "    </list>\n" +
                "  </data>\n" +
                "</decision-table52>";

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(personDrl, ResourceType.DRL);
        kieHelper.addContent(guidedDTable, ResourceType.GDST);

        Results buildResults = kieHelper.verify();
        Assert.assertEquals("No build failures", Collections.emptyList(), buildResults.getMessages(Message.Level.ERROR));
        // make sure the dtable was actually processed and knowledge packages created
        KieBase kieBase = kieHelper.build();
        Assert.assertNotNull("Rule generated from guided dtable not found!", kieBase.getRule("org.drools.test", "Row 1 test1"));
    }

}
