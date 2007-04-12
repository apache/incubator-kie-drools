package org.drools.lang.dsl;

import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

public class DefaultExpanderTest extends TestCase {
    private DSLMappingFile  file     = null;
    private DefaultExpander expander = null;

    protected void setUp() throws Exception {
        final String filename = "test_metainfo.dsl";
        final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        this.file = new DSLMappingFile();
        this.file.parseAndLoad( reader );
        reader.close();

        this.expander = new DefaultExpander();

        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddDSLMapping() {
        this.expander.addDSLMapping( this.file.getMapping() );
        // should not raise any exception
    }

    public void testRegexp() throws Exception {
        this.expander.addDSLMapping( this.file.getMapping() );
        final Reader rules = new InputStreamReader( this.getClass().getResourceAsStream( "test_expansion.drl" ) );
        final String result = this.expander.expand( rules );
    }
}
