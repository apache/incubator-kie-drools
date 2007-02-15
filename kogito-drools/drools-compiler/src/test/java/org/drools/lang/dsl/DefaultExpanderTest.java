package org.drools.lang.dsl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

public class DefaultExpanderTest extends TestCase {
    private DSLMappingFile  file     = null;
    private DefaultExpander expander = null;

    protected void setUp() throws Exception {
        String filename = "test_metainfo.dsl";
        Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        file = new DSLMappingFile( filename,
                                   reader );
        file.parseFile();

        expander = new DefaultExpander();

        super.setUp();
    }

    protected void tearDown() throws Exception {
        file.close();
        super.tearDown();
    }

    public void xxxtestAddDSLMapping() {
        expander.addDSLMapping( file );
        // should not raise any exception
    }

    public void xxxtestExpand() {
        expander.addDSLMapping( file );
        Reader rules = new InputStreamReader( this.getClass().getResourceAsStream( ".drl" ) );

        try {
            String out = expander.expand( rules );
            System.out.println( out );
        } catch ( IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exceptions" );
        }
    }

    public void testRegexp() throws Exception {
//        String input = "regra \"average \\\"bob\\\"\" salience -10";
//
//        Pattern pat = Pattern.compile( "((\"[(\\\")|[^\"]]*\"\\s*)|([^\\s]+\\s*))" );
//        Matcher mat = pat.matcher( input );
//
//        while( mat.find()) {
//            System.out.println("Found: ["+mat.group().trim()+"]");
//        }
        
        expander.addDSLMapping( file );
        Reader rules = new InputStreamReader( this.getClass().getResourceAsStream( "test_expansion.drl" ) );
        String result = expander.expand( rules );
        System.out.println( result );
        
        
    }
}
