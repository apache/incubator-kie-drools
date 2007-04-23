package org.drools.lang.dsl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.drools.lang.ExpanderException;
import org.drools.lang.dsl.DSLMappingEntry.Section;

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

    public void testExpandFailure() throws Exception {

        DSLMappingFile file = new DSLMappingFile();
        String dsl = "[when]foo=Foo()\n[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0, file.getErrors().size() );

        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "rule 'q'\nagenda-group 'x'\nwhen\n    foo  \nthen\n    bar 42\nend";
        String drl = ex.expand( source );
        assertFalse( ex.hasErrors() );

        ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );

        source = "rule 'q' agenda-group 'x'\nwhen\n    foos \nthen\n    bar 42\n end";
        drl = ex.expand( source );
        //System.out.println( drl );
        assertTrue( ex.hasErrors() );
        assertEquals( 1, ex.getErrors().size() );
        //System.err.println(( (ExpanderException) ex.getErrors().get( 0 )).getMessage());

    }
    
    public void FIXME_testLineNumberError() throws Exception {
        DSLMappingFile file = new DSLMappingFile();
        String dsl = "[when]foo=Foo()\n[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );
        
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "rule 'q'\nagenda-group 'x'\nwhen\n    __  \nthen\n    bar 42\nend";
        ex.expand( source );
        assertTrue( ex.hasErrors() );
        assertEquals(1, ex.getErrors().size());
        ExpanderException err = (ExpanderException) ex.getErrors().get( 0 );
        assertEquals(4, err.getLine());
        
    }
}
