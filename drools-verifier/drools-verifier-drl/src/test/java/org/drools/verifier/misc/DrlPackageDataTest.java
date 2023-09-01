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
package org.drools.verifier.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class DrlPackageDataTest {
    @Test
    void testHandleDrl() throws ParseException {

        String drl = "";
        drl += "package org.drools.test\n";
        drl += "global java.util.List list\n";
        drl += "rule rule1\n";
        drl += "when\n";
        drl += "then\n";
        drl += "list.add( drools.getRule().getName() );\n";
        drl += "end\n";
        drl += "rule rule2\n";
        drl += "when\n";
        drl += "then\n";
        drl += "list.add( drools.getRule().getName() );\n";
        drl += "end\n";

        DrlPackageParser s = DrlPackageParser.findPackageDataFromDrl(drl);

        assertThat(s.getName()).isEqualTo("org.drools.test");
        assertThat(s.getRules().size()).isEqualTo(2);
        assertThat(s.getDescription()).isEqualTo("");

    }

    @Test
    void testHandleDrl2() throws IOException,
            ParseException {
        BufferedReader in = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream("DrlPackageTestData.drl") ) );
        String rule = "";
        String str;
        while ((str = in.readLine()) != null) {
            rule += str;
            rule += "\n";
        }
        in.close();

        DrlPackageParser s = DrlPackageParser.findPackageDataFromDrl(rule);

        assertThat(s).isNotNull();

        assertThat(s.getName()).isEqualTo("org.drools.test");
        assertThat(s.getRules().size()).isEqualTo(5);
        assertThat(s.getDescription()).isEqualTo("");

    }

    @Test
    void testHandleDrlNoPackageData() {

        String drl = "";
        drl += "rule rule1\n";
        drl += "    when\n";
        drl += "    then\n";
        drl += "        list.add( drools.getRule().getName() );\n";
        drl += "end\n";

        boolean exception = false;
        try {
            DrlPackageParser s = DrlPackageParser.findPackageDataFromDrl(drl);
        } catch (ParseException e) {
            // Test works
            exception = true;
        }

        if (!exception) {
            fail("Should have thrown a ParseException.");
        }
    }

    @Test
    void testHandleDrlWithComments() throws ParseException {

        String drl = "";
        drl += "# important information\n";
        drl += "# about this package\n";
        drl += "# it contains some rules\n";
        drl += "package org.drools.test\n";
        drl += "global java.util.List list\n";
        drl += "rule rule1\n";
        drl += "	when\n";
        drl += "	then\n";
        drl += "		list.add( drools.getRule().getName() );\n";
        drl += "end\n";
        drl += "rule rule2\n";
        drl += "	when\n";
        drl += "	then\n";
        drl += "		list.add( drools.getRule().getName() );\n";
        drl += "end\n";

        DrlPackageParser data = DrlPackageParser.findPackageDataFromDrl(drl);

        assertThat(data.getName()).isEqualTo("org.drools.test");
        assertThat(data.getRules().size()).isEqualTo(2);
        assertThat(data.getGlobals().size()).isEqualTo(1);
        assertThat(data.getGlobals().get(0)).isEqualTo("java.util.List list");
        assertThat(data.getDescription()).isEqualTo("important information\nabout this package\nit contains some rules\n");

        DrlRuleParser rd1 = data.getRules().get(0);
        assertThat(rd1.getName()).isEqualTo("rule1");
        assertThat(rd1.getDescription()).isEqualTo("");

        DrlRuleParser rd2 = data.getRules().get(1);
        assertThat(rd2.getName()).isEqualTo("rule2");
        assertThat(rd2.getDescription()).isEqualTo("");
    }

    @Test
    void testfindGlobals() {

        String header = "global LoanApplication gg";

        List<String> globals = DrlPackageParser.findGlobals(header);

        assertThat(globals.size()).isEqualTo(1);
        assertThat(globals.get(0)).isEqualTo("LoanApplication gg");
    }

}
