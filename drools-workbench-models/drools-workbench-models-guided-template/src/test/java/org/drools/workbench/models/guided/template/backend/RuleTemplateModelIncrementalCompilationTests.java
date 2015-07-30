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

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static org.junit.Assert.*;

/**
 * Tests for incremental compilation of templates
 */
public class RuleTemplateModelIncrementalCompilationTests {

    @Test
    public void testRuleTemplateInvalidFullBuild() throws Exception {

        //Smurf is unknown
        //  package org.mortgages;
        //    rule "t1_0"
        //  when
        //    Applicant( age == 22 )
        //    Smurf()
        //  then
        //  end
        String drl = "<rule>\n" +
                "  <name>t1</name>\n" +
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
                "            </expression>\n" +
                "            <parameters/>\n" +
                "            <factType>Applicant</factType>\n" +
                "            <fieldName>age</fieldName>\n" +
                "            <fieldType>Integer</fieldType>\n" +
                "          </fieldConstraint>\n" +
                "        </constraints>\n" +
                "      </constraintList>\n" +
                "      <factType>Applicant</factType>\n" +
                "      <isNegated>false</isNegated>\n" +
                "      <window>\n" +
                "        <parameters/>\n" +
                "      </window>\n" +
                "    </fact>\n" +
                "    <freeForm>\n" +
                "      <text>Smurf()</text>\n" +
                "    </freeForm>\n" +
                "  </lhs>\n" +
                "  <rhs/>\n" +
                "  <imports>\n" +
                "    <imports/>\n" +
                "  </imports>\n" +
                "  <packageName>org.drools.workbench.models.guided.template.backend</packageName>\n" +
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
                "        <string>22</string>\n" +
                "      </list>\n" +
                "    </entry>\n" +
                "  </table>\n" +
                "  <idCol>1</idCol>\n" +
                "  <rowsCount>1</rowsCount>\n" +
                "</rule>";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.template", drl );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        List<Message> messages = kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR );
        assertEquals( 1,
                      messages.size() );
    }

    @Test
    public void testRuleTemplateIncrementalCompilationAddInvalidUpdateWithValid() throws Exception {
        //Smurf is unknown
        //  package org.mortgages;
        //    rule "t1_0"
        //  when
        //    Applicant( age == 22 )
        //    Smurf()
        //  then
        //  end
        String drl1_1 = "<rule>\n" +
                "  <name>t1</name>\n" +
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
                "            </expression>\n" +
                "            <parameters/>\n" +
                "            <factType>Applicant</factType>\n" +
                "            <fieldName>age</fieldName>\n" +
                "            <fieldType>Integer</fieldType>\n" +
                "          </fieldConstraint>\n" +
                "        </constraints>\n" +
                "      </constraintList>\n" +
                "      <factType>Applicant</factType>\n" +
                "      <isNegated>false</isNegated>\n" +
                "      <window>\n" +
                "        <parameters/>\n" +
                "      </window>\n" +
                "    </fact>\n" +
                "    <freeForm>\n" +
                "      <text>Smurf()</text>\n" +
                "    </freeForm>\n" +
                "  </lhs>\n" +
                "  <rhs/>\n" +
                "  <imports>\n" +
                "    <imports/>\n" +
                "  </imports>\n" +
                "  <packageName>org.drools.workbench.models.guided.template.backend</packageName>\n" +
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
                "        <string>22</string>\n" +
                "      </list>\n" +
                "    </entry>\n" +
                "  </table>\n" +
                "  <idCol>1</idCol>\n" +
                "  <rowsCount>1</rowsCount>\n" +
                "</rule>";

        //Valid
        //  package org.mortgages;
        //    rule "t1_0"
        //  when
        //    Applicant( age == 22 )
        //  then
        //  end
        String drl1_2 = "<rule>\n" +
                "  <name>t1</name>\n" +
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
                "            </expression>\n" +
                "            <parameters/>\n" +
                "            <factType>Applicant</factType>\n" +
                "            <fieldName>age</fieldName>\n" +
                "            <fieldType>Integer</fieldType>\n" +
                "          </fieldConstraint>\n" +
                "        </constraints>\n" +
                "      </constraintList>\n" +
                "      <factType>Applicant</factType>\n" +
                "      <isNegated>false</isNegated>\n" +
                "      <window>\n" +
                "        <parameters/>\n" +
                "      </window>\n" +
                "    </fact>\n" +
                "  </lhs>\n" +
                "  <rhs/>\n" +
                "  <imports>\n" +
                "    <imports/>\n" +
                "  </imports>\n" +
                "  <packageName>org.drools.workbench.models.guided.template.backend</packageName>\n" +
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
                "        <string>22</string>\n" +
                "      </list>\n" +
                "    </entry>\n" +
                "  </table>\n" +
                "  <idCol>1</idCol>\n" +
                "  <rowsCount>1</rowsCount>\n" +
                "</rule>";

        KieServices ks = KieServices.Factory.get();

        //Add invalid definition
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.template",
                                                         drl1_1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 1,
                      kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        //Update file with valid definition - expect 1 "removed" error message
        kfs.write( "src/main/resources/r1.template", drl1_2 );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r1.template" ).build();

        assertEquals( 0,
                      addResults.getAddedMessages().size() );
        assertEquals( 1,
                      addResults.getRemovedMessages().size() );
    }

    @Test
    public void testRuleTemplateIncrementalCompilationAddValidUpdateWithInvalid() throws Exception {
        // DROOLS-360

        //Valid
        //  package org.mortgages;
        //    rule "t1_0"
        //  when
        //    Applicant( age == 22 )
        //  then
        //  end
        String drl1_1 = "<rule>\n" +
                "  <name>t1</name>\n" +
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
                "            </expression>\n" +
                "            <parameters/>\n" +
                "            <factType>Applicant</factType>\n" +
                "            <fieldName>age</fieldName>\n" +
                "            <fieldType>Integer</fieldType>\n" +
                "          </fieldConstraint>\n" +
                "        </constraints>\n" +
                "      </constraintList>\n" +
                "      <factType>Applicant</factType>\n" +
                "      <isNegated>false</isNegated>\n" +
                "      <window>\n" +
                "        <parameters/>\n" +
                "      </window>\n" +
                "    </fact>\n" +
                "  </lhs>\n" +
                "  <rhs/>\n" +
                "  <imports>\n" +
                "    <imports/>\n" +
                "  </imports>\n" +
                "  <packageName>org.drools.workbench.models.guided.template.backend</packageName>\n" +
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
                "        <string>22</string>\n" +
                "      </list>\n" +
                "    </entry>\n" +
                "  </table>\n" +
                "  <idCol>1</idCol>\n" +
                "  <rowsCount>1</rowsCount>\n" +
                "</rule>";

        //Smurf is unknown
        //  package org.mortgages;
        //    rule "t1_0"
        //  when
        //    Applicant( age == 22 )
        //    Smurf()
        //  then
        //  end
        String drl1_2 = "<rule>\n" +
                "  <name>t1</name>\n" +
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
                "            </expression>\n" +
                "            <parameters/>\n" +
                "            <factType>Applicant</factType>\n" +
                "            <fieldName>age</fieldName>\n" +
                "            <fieldType>Integer</fieldType>\n" +
                "          </fieldConstraint>\n" +
                "        </constraints>\n" +
                "      </constraintList>\n" +
                "      <factType>Applicant</factType>\n" +
                "      <isNegated>false</isNegated>\n" +
                "      <window>\n" +
                "        <parameters/>\n" +
                "      </window>\n" +
                "    </fact>\n" +
                "    <freeForm>\n" +
                "      <text>Smurf()</text>\n" +
                "    </freeForm>\n" +
                "  </lhs>\n" +
                "  <rhs/>\n" +
                "  <imports>\n" +
                "    <imports/>\n" +
                "  </imports>\n" +
                "  <packageName>org.drools.workbench.models.guided.template.backend</packageName>\n" +
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
                "        <string>22</string>\n" +
                "      </list>\n" +
                "    </entry>\n" +
                "  </table>\n" +
                "  <idCol>1</idCol>\n" +
                "  <rowsCount>1</rowsCount>\n" +
                "</rule>";

        KieServices ks = KieServices.Factory.get();

        //Add valid definition
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.template", drl1_1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        List<Message> messages = kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR );
        assertEquals( 0,
                      messages.size() );

        //Update with invalid definition - expect 1 "added" error message
        kfs.write( "src/main/resources/r1.template", drl1_2 );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r1.template" ).build();

        assertEquals( 1,
                      addResults.getAddedMessages().size() );
        assertEquals( 0,
                      addResults.getRemovedMessages().size() );
    }

}
