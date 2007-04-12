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
            final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( this.filename ) );
            this.file = new DSLMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertTrue( this.file.getErrors().toString(),
                        parsingResult );
            assertTrue( this.file.getErrors().isEmpty() );

            assertEquals( 31,
                          this.file.getMapping().getEntries().size() );
        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

}
