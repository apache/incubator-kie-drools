package org.drools.compiler;

import java.io.StringReader;

import junit.framework.TestCase;

public class DrlParserTest extends TestCase {

    public void testExpandDRL() throws Exception {
        String dsl = "[condition]Something=Something()\n[then]another=another();";
        String drl = "rule 'foo' \n when \n Something \n then \n another \nend";
        
        DrlParser parser = new DrlParser();
        String result = parser.getExpandedDRL( drl, new StringReader(dsl));
        assertEqualsIgnoreWhitespace( "rule 'foo' \n when \n Something() \n then \n another(); \nend", result );
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
}
