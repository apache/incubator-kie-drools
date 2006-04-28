package org.drools.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.drools.compiler.DrlParser;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;

/**
 * Test the dump/convert format utilities.
 * @author Michael Neale
 */
public class DumperTest extends TestCase {

    public void testRoundTripXml() throws Exception {

        XmlPackageReader xmlPackageReader = new XmlPackageReader();
        xmlPackageReader.read( new InputStreamReader( getClass().getResourceAsStream( "test_ParseRule.xml" ) ) );
        PackageDescr pkgOriginal = xmlPackageReader.getPackageDescr();

        XmlDumper dumper = new XmlDumper();
        String result = dumper.dump( pkgOriginal );
        assertNotNull( result );

        //now lest slurp it back up
        xmlPackageReader = new XmlPackageReader();
        PackageDescr pkgDumped = xmlPackageReader.read( new StringReader( result ) );

        assertEquals( pkgOriginal.getName(),
                      pkgDumped.getName() );
        assertEquals( pkgOriginal.getFunctions().size(),
                      pkgDumped.getFunctions().size() );
        assertEquals( pkgOriginal.getRules().size(),
                      pkgDumped.getRules().size() );
        assertEquals( pkgOriginal.getGlobals().size(),
                      pkgDumped.getGlobals().size() );

        RuleDescr ruleOriginal = (RuleDescr) pkgOriginal.getRules().get( 0 );
        RuleDescr ruleDumped = (RuleDescr) pkgDumped.getRules().get( 0 );

        assertEquals( ruleOriginal.getName(),
                      ruleDumped.getName() );

        assertEquals( ruleOriginal.getLhs().getDescrs().size(),
                      ruleDumped.getLhs().getDescrs().size() );
        assertEquals( ruleOriginal.getConsequence(),
                      ruleDumped.getConsequence() );

    }

    public void testRoundTripDrl() throws Exception {

        DrlParser parser = new DrlParser();
        PackageDescr pkgOriginal = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "test_Dump.drl" ) ));
        DrlDumper dumper = new DrlDumper();
        String result = dumper.dump( pkgOriginal );
        assertNotNull(result);
        
        parser = new DrlParser();
        PackageDescr pkgDumped = parser.parse( new StringReader(result) );
        
        assertEquals( pkgOriginal.getName(),
                      pkgDumped.getName() );
        assertEquals( pkgOriginal.getFunctions().size(),
                      pkgDumped.getFunctions().size() );
        assertEquals( pkgOriginal.getRules().size(),
                      pkgDumped.getRules().size() );
        assertEquals( pkgOriginal.getGlobals().size(),
                      pkgDumped.getGlobals().size() );

        RuleDescr ruleOriginal = (RuleDescr) pkgOriginal.getRules().get( 0 );
        RuleDescr ruleDumped = (RuleDescr) pkgDumped.getRules().get( 0 );

        assertEquals( ruleOriginal.getName(),
                      ruleDumped.getName() );

        assertEquals( ruleOriginal.getLhs().getDescrs().size(),
                      ruleDumped.getLhs().getDescrs().size() );
        assertEquals( ruleOriginal.getConsequence(),
                      ruleDumped.getConsequence() );        
        

    }

    private void assertEqualsIgnoreWhitespace(String expected,
                                              String actual) {
        String cleanExpected = expected.replaceAll( "\\s+",
                                                    "" );
        String cleanActual = actual.replaceAll( "\\s+",
                                                "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

    private String readFile(String file) throws IOException {
        InputStreamReader reader = new InputStreamReader( getClass().getResourceAsStream( file ) );

        StringBuffer text = new StringBuffer();

        char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text.toString();
    }

}
