package org.drools.lang.dsl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

public class DSLMappingFileTest extends TestCase {
    private DSLMappingFile file     = null;
    private final String   filename = "test_metainfo.dsl";

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseFile() {
        try {
            Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
            file = new DSLMappingFile( );

            boolean parsingResult = file.parseAndLoad( reader );
            reader.close();
            
            assertTrue( file.getErrors().toString(), parsingResult );
            assertTrue( file.getErrors().isEmpty() );
            
            assertEquals( 31, file.getMapping().getEntries().size() );
        } catch ( IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception ");
        }
        
    }
    
}
