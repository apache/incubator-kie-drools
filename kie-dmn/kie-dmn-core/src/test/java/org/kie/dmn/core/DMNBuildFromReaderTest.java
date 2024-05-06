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
package org.kie.dmn.core;

import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

class DMNBuildFromReaderTest {

    @Test
    void test() {
        // DROOLS-5127
        String dmn =
                "<definitions id=\"business-knowledge-model\" name=\"business-knowledge-model\"\n" +
                "             namespace=\"https://github.com/kiegroup/kie-dmn\"\n" +
                "             xmlns=\"http://www.omg.org/spec/DMN/20151101/dmn.xsd\"\n" +
                "             xmlns:feel=\"http://www.omg.org/spec/FEEL/20140401\">\n" +
                "  <decision id=\"decision0\" name=\"decision0\">\n" +
                "    <variable name=\"decisionVariable\" typeRef=\"feel:number\"/>\n" +
                "    <knowledgeRequirement>\n" +
                "      <requiredKnowledge href=\"#bkm0\"/>\n" +
                "    </knowledgeRequirement>\n" +
                "    <literalExpression>\n" +
                "      <text>bkm0()</text>\n" +
                "    </literalExpression>\n" +
                "  </decision>\n" +
                "  <businessKnowledgeModel id=\"bkm0\" name=\"bkm0\">\n" +
                "    <encapsulatedLogic>\n" +
                "      <literalExpression expressionLanguage=\"http://www.omg.org/spec/FEEL/20140401\">\n" +
                "        <text>0</text>\n" +
                "      </literalExpression>\n" +
                "    </encapsulatedLogic>\n" +
                "    <variable name=\"bkm0\" typeRef=\"feel:number\"/>\n" +
                "  </businessKnowledgeModel>\n" +
                "</definitions>";

        Resource dmnResource = KieServices.get().getResources()
                .newReaderResource(new StringReader(dmn))
                .setResourceType( ResourceType.DMN)
                .setSourcePath("dmnFile.dmn");

        KieHelper kieHelper = new KieHelper();
        kieHelper.addResource(dmnResource);

        KieBase kieBase = kieHelper.build();
        assertThat(kieBase).isNotNull();
    }
}
