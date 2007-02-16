package org.drools.lang.dsl;

import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

public class DefaultExpanderTest extends TestCase {
    private DSLMappingFile  file     = null;
    private DefaultExpander expander = null;

    protected void setUp() throws Exception {
        String filename = "test_metainfo.dsl";
        Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        file = new DSLMappingFile();
        file.parseAndLoad( reader );
        reader.close();

        expander = new DefaultExpander();

        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddDSLMapping() {
        expander.addDSLMapping( file.getMapping() );
        // should not raise any exception
    }

    public void testRegexp() throws Exception {
        expander.addDSLMapping( file.getMapping() );
        Reader rules = new InputStreamReader( this.getClass().getResourceAsStream( "test_expansion.drl" ) );
        String result = expander.expand( rules );
    }
}
