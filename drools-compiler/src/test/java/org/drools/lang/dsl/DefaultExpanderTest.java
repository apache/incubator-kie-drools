package org.drools.lang.dsl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.drools.lang.ExpanderException;

public class DefaultExpanderTest extends TestCase {
    private DSLMappingFile  file     = null;
    private DSLTokenizedMappingFile tokenizedFile = null;
    private DefaultExpander expander = null;

    protected void setUp() throws Exception {
        final String filename = "test_metainfo.dsl";
        final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        this.file = new DSLTokenizedMappingFile();
        this.tokenizedFile = new DSLTokenizedMappingFile();
        this.file.parseAndLoad( reader );
        reader.close();
        
        final Reader reader2 = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        this.tokenizedFile.parseAndLoad(reader2);
        reader2.close();
        
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
    
    public void testANTLRAddDSLMapping() {
        this.expander.addDSLMapping( this.tokenizedFile.getMapping() );
        // should not raise any exception
    }

    public void testRegexp() throws Exception {
        this.expander.addDSLMapping( this.file.getMapping() );
        final Reader rules = new InputStreamReader( this.getClass().getResourceAsStream( "test_expansion.dslr" ) );
        final String result = this.expander.expand( rules );
    }
    
    public void testANTLRRegexp() throws Exception {
        this.expander.addDSLMapping( this.tokenizedFile.getMapping() );
        final Reader rules = new InputStreamReader( this.getClass().getResourceAsStream( "test_expansion.dslr" ) );
        final String result = this.expander.expand( rules );
    }

    
    public void testExpandParts() throws Exception {
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]foo=Foo()\n[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                      file.getErrors().size() );
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        
        //System.err.println(ex.expand( "rule 'x' \n when \n foo \n then \n end" ));
    }
    
    public void testANTLRExpandParts() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]foo=Foo()\n[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                      file.getErrors().size() );
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        
        //System.err.println(ex.expand( "rule 'x' \n when \n foo \n then \n end" ));
    }
    
    public void testExpandFailure() throws Exception {

        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]foo=Foo()\n[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                      file.getErrors().size() );

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
        assertEquals( 1,
                      ex.getErrors().size() );
        //System.err.println(( (ExpanderException) ex.getErrors().get( 0 )).getMessage());
    }
    
    public void testANTLRExpandFailure() throws Exception {

        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]foo=Foo()\n[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                      file.getErrors().size() );

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
        assertEquals( 1,
                      ex.getErrors().size() );
        //System.err.println(( (ExpanderException) ex.getErrors().get( 0 )).getMessage());
    }

    public void testExpandWithKeywordClashes() throws Exception {

        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]Invoke rule executor=ruleExec: RuleExecutor()\n" + "[then]Execute rule \"{id}\"=ruleExec.ExecuteSubRule( new Long({id}));";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                      file.getErrors().size() );

        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "package something;\n\nrule \"1\"\nwhen\n    Invoke rule executor\nthen\n    Execute rule \"5\"\nend";
        String expected = "package something;\n\nrule \"1\"\nwhen\n   ruleExec: RuleExecutor()\nthen\n   ruleExec.ExecuteSubRule( new Long(5));\nend\n";
        String drl = ex.expand( source );
//        System.out.println("["+drl+"]" );
//        System.out.println("["+expected+"]" );
        assertFalse( ex.hasErrors() );
        equalsIgnoreWhiteSpace( expected, drl );

    }
    
    public void testANTLRExpandWithKeywordClashes() throws Exception {

        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]Invoke rule executor=ruleExec: RuleExecutor()\n" + "[then]Execute rule \"{id}\"=ruleExec.ExecuteSubRule( new Long({id}));";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                      file.getErrors().size() );

        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source =   "package something;\n\nrule \"1\"\nwhen\n    Invoke rule executor\nthen\n    Execute rule \"5\"\nend";
        String expected = "package something;\n\nrule \"1\"\nwhen\n    ruleExec: RuleExecutor()\nthen\n    ruleExec.ExecuteSubRule( new Long(5));\nend";
        String drl = ex.expand( source );
//        System.out.println("["+drl+"]" );
//        System.out.println("["+expected+"]" );
        assertFalse( ex.hasErrors() );
        assertEquals( expected, drl );
    }


    public void testLineNumberError() throws Exception {
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]foo=Foo()\n[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );

        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "rule 'q'\nagenda-group 'x'\nwhen\n    __  \nthen\n    bar 42\n\tgoober\nend";
        ex.expand( source );
        assertTrue( ex.hasErrors() );
        assertEquals( 2,
                      ex.getErrors().size() );
        ExpanderException err = (ExpanderException) ex.getErrors().get( 0 );
        assertEquals( 4,
                      err.getLine() );
        err = (ExpanderException) ex.getErrors().get( 1 );
        assertEquals( 7,
                      err.getLine() );

    }
    
    public void testANTLRLineNumberError() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]foo=Foo()\n[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );

        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "rule 'q'\nagenda-group 'x'\nwhen\n    __  \nthen\n    bar 42\n\tgoober\nend";
        ex.expand( source );
        assertTrue( ex.hasErrors() );
        assertEquals( 2,
                      ex.getErrors().size() );
        ExpanderException err = (ExpanderException) ex.getErrors().get( 0 );
        assertEquals( 4,
                      err.getLine() );
        err = (ExpanderException) ex.getErrors().get( 1 );
        assertEquals( 7,
                      err.getLine() );

    }
    
    public void FIXME_testANTLREnumExpand() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]When the credit rating is {rating:ENUM:Applicant.creditRating} = applicant:Applicant(credit=={rating})";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,file.getErrors().size() );
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "rule \"TestNewDslSetup\"\ndialect \"mvel\"\nwhen\nWhen the credit rating is AA\nthen \nend";
        
//        String source="rule \"TestNewDslSetup\"\n"+
//    					"dialect \"mvel\"\n"+
//						"when\n"+
//							"When the credit rating is OK\n"+
//						"then\n"+
//						"end\n";
    
        String drl = ex.expand(source);
        
        String expected = "rule \"TestNewDslSetup\"\n"+
        "dialect \"mvel\"\n"+
        "when\n"+
        "applicant:Applicant(credit==AA)\n"+ 
        "then \nend\n";
        
        assertFalse(ex.getErrors().toString(),ex.hasErrors());
        assertEquals( expected, drl );
        
        


        //System.err.println(ex.expand( "rule 'x' \n when \n foo \n then \n end" ));
    }
    
    private boolean equalsIgnoreWhiteSpace( String expected, String actual ) {
        String patternStr = expected.replaceAll( "\\s+", "(\\\\s|\\\\n|\\\\r)*" );//.replaceAll( "\\n", "\\s*\\$" );
        Pattern pattern = Pattern.compile( patternStr, Pattern.DOTALL );
        Matcher m = pattern.matcher( actual );
        return m.matches();
    }
}
