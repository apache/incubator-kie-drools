/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DrlPackageDataTest {
    @Test
    public void testHandleDrl() throws ParseException {

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

        DrlPackageParser s = DrlPackageParser.findPackageDataFromDrl( drl );

        assertEquals( "org.drools.test",
                      s.getName() );
        assertEquals( 2,
                      s.getRules().size() );
        assertEquals( "",
                      s.getDescription() );

    }

    @Test
    public void testHandleDrl2() throws IOException,
                                ParseException {
        BufferedReader in = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream( "DrlPackageTestData.drl" ) ) );
        String rule = "";
        String str;
        while ( (str = in.readLine()) != null ) {
            rule += str;
            rule += "\n";
        }
        in.close();

        DrlPackageParser s = DrlPackageParser.findPackageDataFromDrl( rule );

        assertNotNull( s );

        assertEquals( "org.drools.test",
                      s.getName() );
        assertEquals( 5,
                      s.getRules().size() );
        assertEquals( "",
                      s.getDescription() );

    }

    @Test
    public void testHandleDrlNoPackageData() {

        String drl = "";
        drl += "rule rule1\n";
        drl += "    when\n";
        drl += "    then\n";
        drl += "        list.add( drools.getRule().getName() );\n";
        drl += "end\n";

        boolean exception = false;
        try {
            DrlPackageParser s = DrlPackageParser.findPackageDataFromDrl( drl );
        } catch ( ParseException e ) {
            // Test works
            exception = true;
        }

        if ( !exception ) {
            fail( "Should have thrown a ParseException." );
        }
    }

    @Test
    public void testHandleDrlWithComments() throws ParseException {

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

        DrlPackageParser data = DrlPackageParser.findPackageDataFromDrl( drl );

        assertEquals( "org.drools.test",
                      data.getName() );
        assertEquals( 2,
                      data.getRules().size() );
        assertEquals( 1,
                      data.getGlobals().size() );
        assertEquals( "java.util.List list",
                      data.getGlobals().get( 0 ) );
        assertEquals( "important information\nabout this package\nit contains some rules\n",
                      data.getDescription() );

        DrlRuleParser rd1 = data.getRules().get( 0 );
        assertEquals( "rule1",
                      rd1.getName() );
        assertEquals( "",
                      rd1.getDescription() );

        DrlRuleParser rd2 = data.getRules().get( 1 );
        assertEquals( "rule2",
                      rd2.getName() );
        assertEquals( "",
                      rd2.getDescription() );
    }

    @Test
    public void testfindGlobals() {

        String header = "global LoanApplication gg";

        List<String> globals = DrlPackageParser.findGlobals( header );

        assertEquals( 1,
                      globals.size() );
        assertEquals( "LoanApplication gg",
                      globals.get( 0 ) );
    }

}
