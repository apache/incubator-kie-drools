package org.drools.verifier.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;

public class DrlPackageDataTest extends TestCase {
    public void testHandleDrl() {

        String rule1 = "";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";
        rule1 += "rule rule2\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";

        DrlPackageParser s = DrlPackageParser.findPackageDataFromDrl( rule1 );

        assertEquals( "org.drools.test",
                      s.getName() );
        assertEquals( 2,
                      s.getRules().size() );
        assertEquals( "",
                      s.getDescription() );

    }

    public void testHandleDrl2() throws IOException {
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

    public void testHandleDrlWithComments() {

        String rule1 = "";
        rule1 += "# important information\n";
        rule1 += "# about this package\n";
        rule1 += "# it contains some rules\n";
        rule1 += "package org.drools.test\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "	when\n";
        rule1 += "	then\n";
        rule1 += "		list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";
        rule1 += "rule rule2\n";
        rule1 += "	when\n";
        rule1 += "	then\n";
        rule1 += "		list.add( drools.getRule().getName() );\n";
        rule1 += "end\n";

        DrlPackageParser data = DrlPackageParser.findPackageDataFromDrl( rule1 );

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

    public void testfindGlobals() {

        String header = "global LoanApplication gg";

        List<String> globals = DrlPackageParser.findGlobals( header );

        assertEquals( 1,
                      globals.size() );
        assertEquals( "LoanApplication gg",
                      globals.get( 0 ) );
    }

}
