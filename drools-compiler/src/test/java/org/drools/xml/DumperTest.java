package org.drools.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;

/**
 * Test the dump/convert format utilities.
 * @author Michael Neale
 */
public class DumperTest extends TestCase {

    public void testRoundTripXml() throws Exception {

        XmlPackageReader xmlPackageReader = new XmlPackageReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseRule.xml" ) ) );
        final PackageDescr pkgOriginal = xmlPackageReader.getPackageDescr();

        final XmlDumper dumper = new XmlDumper();
        final String result = dumper.dump( pkgOriginal );
        assertNotNull( result );

        //now lest slurp it back up
        xmlPackageReader = new XmlPackageReader();
        final PackageDescr pkgDumped = xmlPackageReader.read( new StringReader( result ) );

        assertEquals( pkgOriginal.getName(),
                      pkgDumped.getName() );
        assertEquals( pkgOriginal.getFunctions().size(),
                      pkgDumped.getFunctions().size() );
        assertEquals( pkgOriginal.getRules().size(),
                      pkgDumped.getRules().size() );
        assertEquals( pkgOriginal.getGlobals().size(),
                      pkgDumped.getGlobals().size() );

        final RuleDescr ruleOriginal = (RuleDescr) pkgOriginal.getRules().get( 0 );
        final RuleDescr ruleDumped = (RuleDescr) pkgDumped.getRules().get( 0 );

        assertEquals( ruleOriginal.getName(),
                      ruleDumped.getName() );

        assertEquals( ruleOriginal.getLhs().getDescrs().size(),
                      ruleDumped.getLhs().getDescrs().size() );
        assertEquals( ruleOriginal.getConsequence(),
                      ruleDumped.getConsequence() );

    }

    public void testRoundTripDrl() throws Exception {

        DrlParser parser = new DrlParser();
        final PackageDescr pkgOriginal = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "test_Dump.drl" ) ) );
        final DrlDumper dumper = new DrlDumper();
        final String result = dumper.dump( pkgOriginal );
        assertNotNull( result );

        parser = new DrlParser();
        final PackageDescr pkgDumped = parser.parse( new StringReader( result ) );

        assertEquals( pkgOriginal.getName(),
                      pkgDumped.getName() );
        assertEquals( pkgOriginal.getFunctions().size(),
                      pkgDumped.getFunctions().size() );
        assertEquals( pkgOriginal.getRules().size(),
                      pkgDumped.getRules().size() );
        assertEquals( pkgOriginal.getGlobals().size(),
                      pkgDumped.getGlobals().size() );

        final RuleDescr ruleOriginal = (RuleDescr) pkgOriginal.getRules().get( 0 );
        final RuleDescr ruleDumped = (RuleDescr) pkgDumped.getRules().get( 0 );

        assertEquals( ruleOriginal.getName(),
                      ruleDumped.getName() );

        assertEquals( ruleOriginal.getLhs().getDescrs().size(),
                      ruleDumped.getLhs().getDescrs().size() );
        assertEquals( ruleOriginal.getConsequence(),
                      ruleDumped.getConsequence() );

        // Now double check the contents are the same
        BufferedReader reader = new BufferedReader( new InputStreamReader( getClass().getResourceAsStream( "test_Dump.drl" ) ) );
        StringBuffer buffer = new StringBuffer();
        String s = null;
        while ( (s = reader.readLine()) != null ) {
            buffer.append( s );
        }

        assertEqualsIgnoreWhitespace( buffer.toString(),
                                      result );

    }

    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

    private String readFile(final String file) throws IOException {
        final InputStreamReader reader = new InputStreamReader( getClass().getResourceAsStream( file ) );

        final StringBuffer text = new StringBuffer();

        final char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text.toString();
    }

}
