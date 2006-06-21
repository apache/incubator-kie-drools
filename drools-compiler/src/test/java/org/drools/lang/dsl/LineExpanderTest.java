package org.drools.lang.dsl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.drools.lang.Expander;

import junit.framework.Assert;
import junit.framework.TestCase;

public class LineExpanderTest extends TestCase {


    private String readFile(String name) throws Exception {
        
        final InputStream in = getClass().getResourceAsStream( name );

        final InputStreamReader reader = new InputStreamReader( in );

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
    
    public void testFile() throws Exception {
        Expander exp = new Expander() {

            public String expand(String scope,
                                 String pattern) {
                if (scope.equals( "when" )) {
                    if (!(
                            (pattern.trim().equals( "line 1" ))
                            ||
                            (pattern.trim().equals( "line 2" ))
                       )) {
                        Assert.fail( "expected line 1 or line 2 only." );
                        
                    }
                }    
                if (scope.equals("then")) {
                    Assert.assertEquals( "line 3", pattern.trim() );
                }
                return "expanded: " + pattern.trim();
                
            }
            
        };        
        LineBasedExpander ex = new LineBasedExpander(readFile("rule1.drl"), exp);
        String result = ex.expand();
        assertNotNull(result);
        System.out.println(result);
        assertTrue(result.indexOf( "expanded: line 1" ) > result.indexOf( "when" ));
        assertTrue(result.indexOf( "expanded: line 2" ) > result.indexOf( "expanded: line 1" ));
        assertTrue(result.indexOf( "then" ) > result.indexOf( "expanded: line 2" ));
        assertTrue(result.indexOf( "expanded: line 3" ) > result.indexOf( "then" ));
        
        
    }
    
    public void testMatchingStart() {        
        LineBasedExpander exp = new LineBasedExpander("blah", null);
        String when = "when";
        String end = "end";
        assertTrue(exp.matchesKeyword( when, "  when".trim() ));
        assertTrue(exp.matchesKeyword( when, "\twhen#foo".trim() ));
        assertFalse(exp.matchesKeyword( when, "\tlwhen#foo".trim() ));
        assertTrue(exp.matchesKeyword( when, "when".trim() ));
        assertTrue(exp.matchesKeyword( end, "end".trim() ));
        
        assertTrue(exp.matchesKeyword( end, "\nend //comment".trim() ));
        assertTrue(exp.matchesKeyword( end, "end//comment".trim() ));
        
        assertFalse(exp.matchesKeyword( end, "\n\"end\" //comment".trim() ));
        assertFalse(exp.matchesKeyword( end, "\nkend//comment".trim() ));
        assertFalse(exp.matchesKeyword( when, "\nend".trim() ));
        assertFalse(exp.matchesKeyword( end, "\nkend//comment".trim() ));        
    }
    
    public void testNormaliseSpaces() {
        String test = " this  has more spaces,  then is  \t necessary";
        String res  = "this has more spaces, then is necessary";
        
        LineBasedExpander ex = new LineBasedExpander("ignore", null);
        assertEquals(res, ex.normaliseSpaces( test ));

        assertEquals(">yeah", ex.normaliseSpaces( ">yeah" ));
        assertEquals("yeah man", ex.normaliseSpaces( "yeah man" ));
        
        assertEquals("'with  ' some \"\tquotes\"", 
                     ex.normaliseSpaces( "'with  '  some \"\tquotes\" " ));
        
    }
    
}
