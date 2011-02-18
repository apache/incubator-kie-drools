package org.drools.compiler.xml.rules;

import java.io.IOException;
import java.io.InputStreamReader;

import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.xml.XmlDumper;
import org.drools.compiler.xml.XmlPackageReader;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.PackageDescr;

import static org.junit.Assert.*;

/**
 * Helper Class for both xml and drl Dump Tests
 */
public class DumperTestHelper {
    
    public static void XmlFile(String filename) throws Exception {
        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        
        XmlPackageReader xmlPackageReader = new XmlPackageReader( conf.getSemanticModules() );
        xmlPackageReader.read( new InputStreamReader( DumperTestHelper.class.getResourceAsStream( filename ) ) );
        final PackageDescr pkgOriginal = xmlPackageReader.getPackageDescr();

        final XmlDumper dumper = new XmlDumper();
        final String result = dumper.dump( pkgOriginal );
        
        String buffer = readFile( filename );
        
        assertEqualsIgnoreWhitespace( buffer,
                                      result );
        assertNotNull( result );
    }

    public static void DrlFile(String filename) throws Exception {

        DrlParser parser = new DrlParser();
        final PackageDescr pkgOriginal = parser.parse( new InputStreamReader( DumperTestHelper.class.getResourceAsStream( filename ) ) );
        final DrlDumper dumper = new DrlDumper();
        String result = dumper.dump( pkgOriginal );

        parser = new DrlParser();
        String buffer = readFile( filename );
        assertEqualsIgnoreWhitespace( buffer.toString(),
                                      result );
    }

    private static void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );
        assertEquals( cleanExpected,
                      cleanActual );
    }

    private static String readFile(final String file) throws IOException {
        final InputStreamReader reader = new InputStreamReader( DumperTestHelper.class.getResourceAsStream( file ) );
        final StringBuilder text = new StringBuilder();
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
