/*
 * Copyright 2015 JBoss Inc
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

import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class KnowledgeBuilderTemplateTest {

    private FileManager fileManager;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager().setUp();
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }

    @Test
    public void testCompositeKnowledgeBuilder() throws Exception {

        String template = "<rule>\n" +
                "  <name>gg</name>\n" +
                "  <modelVersion>1.0</modelVersion>\n" +
                "  <attributes/>\n" +
                "  <metadataList/>\n" +
                "  <lhs>\n" +
                "    <fact>\n" +
                "      <constraintList>\n" +
                "        <constraints>\n" +
                "          <fieldConstraint>\n" +
                "            <value>$default</value>\n" +
                "            <operator>==</operator>\n" +
                "            <constraintValueType>7</constraintValueType>\n" +
                "            <expression>\n" +
                "              <parts/>\n" +
                "              <index>2147483647</index>\n" +
                "            </expression>\n" +
                "            <parameters/>\n" +
                "            <factType>Fact</factType>\n" +
                "            <fieldName>factField</fieldName>\n" +
                "            <fieldType>String</fieldType>\n" +
                "          </fieldConstraint>\n" +
                "        </constraints>\n" +
                "      </constraintList>\n" +
                "      <factType>Fact</factType>\n" +
                "      <isNegated>false</isNegated>\n" +
                "      <window>\n" +
                "        <parameters/>\n" +
                "      </window>\n" +
                "    </fact>\n" +
                "    <dslSentence>\n" +
                "      <drl>f : Fact()</drl>\n" +
                "      <definition>There is a Fact with</definition>\n" +
                "      <values/>\n" +
                "    </dslSentence>\n" +
                "  </lhs>\n" +
                "  <rhs/>\n" +
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
                "    <entry>\n" +
                "      <string>$default</string>\n" +
                "      <list>\n" +
                "        <string>gg</string>\n" +
                "      </list>\n" +
                "    </entry>\n" +
                "  </table>\n" +
                "  <idCol>1</idCol>\n" +
                "  <rowsCount>1</rowsCount>\n" +
                "</rule>";


        String declaration = "package org.mortgages\n" +
                "declare Fact\n" +
                "    factField: String\n" +
                "end\n";


        String dsl = "[when]There is a Fact with=f : Fact()";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(dsl.getBytes()), ResourceType.DSL);
        kbuilder.add(ResourceFactory.newByteArrayResource(declaration.getBytes()), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newByteArrayResource(template.getBytes()), ResourceType.TEMPLATE);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        assertFalse(kbuilder.hasErrors());
    }

}
