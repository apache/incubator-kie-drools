package org.drools.compiler.compiler.xml.rules;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.xml.XmlDumper;
import org.drools.compiler.compiler.xml.XmlPackageReader;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.junit.Assert.*;

/**
 * Helper Class for both xml and drl Dump Tests
 */
public class DumperTestHelper {
    
    public static void XmlFile(String filename) throws Exception {
        KnowledgeBuilderConfigurationImpl conf = new KnowledgeBuilderConfigurationImpl();
        
        XmlPackageReader xmlPackageReader = new XmlPackageReader( conf.getSemanticModules() );
        xmlPackageReader.getParser().setClassLoader( DumperTestHelper.class.getClassLoader() );
        xmlPackageReader.read( new InputStreamReader( DumperTestHelper.class.getResourceAsStream( filename ) ) );
        final PackageDescr pkgOriginal = xmlPackageReader.getPackageDescr();

        final XmlDumper dumper = new XmlDumper();
        final String result = dumper.dump( pkgOriginal );        
        
        String buffer = readFile( filename );
        
        System.out.println(buffer);
        System.out.println(result);
        
        assertEqualsIgnoreWhitespace( buffer,
                                      result );
        assertNotNull( result );
    }

    public static void DrlFile(String filename) throws Exception {

        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        final PackageDescr pkgOriginal = parser.parse( new InputStreamReader( DumperTestHelper.class.getResourceAsStream( filename ) ) );
        final DrlDumper dumper = new DrlDumper();
        String result1 = dumper.dump( pkgOriginal );
        final PackageDescr pkgDerivated = parser.parse( new StringReader( result1 ) );
        String result2 = dumper.dump( pkgDerivated );
        System.out.println( result1 );

        assertEqualsIgnoreWhitespace( result1,
                                      result2 );
    }

    public static void assertEqualsIgnoreWhitespace(final String expected,
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
