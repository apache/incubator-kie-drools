package org.drools.lang.dsl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

public class DSLMappingFileTest extends TestCase {
    private DSLMappingFile file     = null;
    private final String   filename = "test_metainfo.dsl";

    protected void setUp() throws Exception {
        Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        file = new DSLMappingFile( filename,
                                   reader );

        super.setUp();
    }

    protected void tearDown() throws Exception {
        file.close();
        super.tearDown();
    }

    public void testGetDslFileName() {
        assertEquals( filename, file.getDslFileName() );
    }

    public void testClose() {
        try {
            assertFalse( file.isClosed() );
            file.close();
            assertTrue( file.isClosed() );
        } catch ( IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception ");
        }
    }
    
    public void testParseFile() {
        try {
            boolean parsingResult = file.parseFile();
            
            assertTrue( file.getErrors().toString(), parsingResult );
            assertTrue( file.getErrors().isEmpty() );
            
            assertEquals( 31, file.getEntries().size() );
        } catch ( IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception ");
        }
        
    }
    
}
