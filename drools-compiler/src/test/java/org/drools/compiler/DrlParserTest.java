package org.drools.compiler;

import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuntimeDroolsException;
import org.drools.lang.Expander;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.lang.dsl.DefaultExpanderResolver;

public class DrlParserTest {

    @Test
    public void testExpandDRL() throws Exception {
        String dsl = "[condition]Something=Something()\n[then]another=another();";
        String drl = "rule 'foo' \n when \n Something \n then \n another \nend";
        
        DrlParser parser = new DrlParser();
        String result = parser.getExpandedDRL( drl, new StringReader(dsl));
        assertEqualsIgnoreWhitespace( "rule 'foo' \n when \n Something() \n then \n another(); \nend", result );
    }
    
    @Test
    public void testExpandDRLUsingInjectedExpander() throws Exception {
        String dsl = "[condition]Something=Something()\n[then]another=another();";
        String drl = "rule 'foo' \n when \n Something \n then \n another \nend";
        
        
        DefaultExpanderResolver resolver = new DefaultExpanderResolver(new StringReader(dsl));
        
        
        final DSLMappingFile file = new DSLTokenizedMappingFile();
        if ( file.parseAndLoad( new StringReader(dsl) ) ) {
            final Expander expander = new DefaultExpander();
            expander.addDSLMapping( file.getMapping() );
            resolver.addExpander("*", expander);
        } else {
            throw new RuntimeDroolsException( "Error parsing and loading DSL file." + file.getErrors() );
        }        

        DrlParser parser = new DrlParser();
        String result = parser.getExpandedDRL( drl, resolver);
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
