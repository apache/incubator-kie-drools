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
 *
 */

package org.drools.workbench.models.guided.template.backend;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TemplateIntegrationTest {

    private static final String template =
            "<rule>\n" +
            "  <name>RecTemplate</name>\n" +
            "  <modelVersion>1.0</modelVersion>\n" +
            "  <attributes/>\n" +
            "  <metadataList/>\n" +
            "  <lhs>\n" +
            "    <fact>\n" +
            "      <constraintList>\n" +
            "        <constraints>\n" +
            "          <fieldConstraint>\n" +
            "            <value>$companyId</value>\n" +
            "            <operator>==</operator>\n" +
            "            <constraintValueType>7</constraintValueType>\n" +
            "            <expression>\n" +
            "              <parts/>\n" +
            "              <index>2147483647</index>\n" +
            "            </expression>\n" +
            "            <parameters/>\n" +
            "            <factType>Company</factType>\n" +
            "            <fieldName>companyId</fieldName>\n" +
            "            <fieldType>Long</fieldType>\n" +
            "          </fieldConstraint>\n" +
            "        </constraints>\n" +
            "      </constraintList>\n" +
            "      <factType>Company</factType>\n" +
            "      <boundName>$c</boundName>\n" +
            "      <isNegated>false</isNegated>\n" +
            "      <window>\n" +
            "        <parameters/>\n" +
            "      </window>\n" +
            "    </fact>\n" +
            "  </lhs>\n" +
            "  <rhs>\n" +
            "    <freeForm>\n" +
            "      <text>System.out.println(&quot;Found ----&gt; &quot; + $c);\n$c.setFound(true);</text>\n" +
            "    </freeForm>\n" +
            "  </rhs>\n" +
            "  <imports>\n" +
            "    <imports>\n" +
            "      <org.drools.workbench.models.datamodel.imports.Import>\n" +
            "        <type>" + Company.class.getCanonicalName() + "</type>\n" +
            "      </org.drools.workbench.models.datamodel.imports.Import>\n" +
            "    </imports>\n" +
            "  </imports>\n" +
            "  <packageName>com.sample</packageName>\n" +
            "  <isNegated>false</isNegated>\n" +
            "  <table>\n" +
            "    <entry>\n" +
            "      <string>__ID_KOL_NAME__</string>\n" +
            "      <list>\n" +
            "        <string>1</string>\n" +
            "        <string>0</string>\n" +
            "        <string>0</string>\n" +
            "        <string>0</string>\n" +
            "      </list>\n" +
            "    </entry>\n" +
            "    <entry>\n" +
            "      <string>$companyId</string>\n" +
            "      <list>\n" +
            "        <string>321</string>\n" +
            "        <string>123</string>\n" +
            "        <string>12345</string>\n" +
            "        <string>54321</string>\n" +
            "      </list>\n" +
            "    </entry>\n" +
            "  </table>\n" +
            "  <idCol>1</idCol>\n" +
            "  <rowsCount>4</rowsCount>\n" +
            "</rule>\n";

    @Test
    public void test() {
        String drl = "global java.util.List list\n" +
                     "rule \"String detector\"\n" +
                     "    when\n" +
                     "        $s : String( )\n" +
                     "    then\n" +
                     "        list.add($s);\n" +
                     "end";

        final KieSession ksession = new KieHelper().addContent(template, ResourceType.TEMPLATE)
                                                   .build()
                                                   .newKieSession();

        Company myCompany = new Company( 123, "myCompany" );
        Company yourCompany = new Company( 456, "yourCompany" );

        ksession.insert( myCompany );
        ksession.insert( yourCompany );
        ksession.fireAllRules();

        assertTrue(myCompany.isFound());
        assertFalse(yourCompany.isFound());
    }

    public static class Company {
        private final int companyId;
        private final String companyName;

        private boolean found;

        public Company( int companyId, String companyName ) {
            this.companyId = companyId;
            this.companyName = companyName;
        }

        public int getCompanyId() {
            return companyId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public boolean isFound() {
            return found;
        }

        public void setFound( boolean found ) {
            this.found = found;
        }

        @Override
        public String toString() {
            return "Company id: " + getCompanyId() +  "; Name: " + getCompanyName();
        }
    }
}
