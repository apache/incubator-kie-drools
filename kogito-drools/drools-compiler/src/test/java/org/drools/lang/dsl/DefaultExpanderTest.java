package org.drools.lang.dsl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.lang.ExpanderException;

public class DefaultExpanderTest {
    private static final String NL = System.getProperty( "line.separator" );

    private DSLMappingFile          file          = null;
    private DSLTokenizedMappingFile tokenizedFile = null;
    private DefaultExpander         expander      = null;

    @Before
    public void setUp() throws Exception {
        final String filename = "test_metainfo.dsl";
        final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        this.file = new DSLTokenizedMappingFile();
        this.tokenizedFile = new DSLTokenizedMappingFile();
        this.file.parseAndLoad( reader );
        reader.close();

        final Reader reader2 = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        this.tokenizedFile.parseAndLoad( reader2 );
        reader2.close();

        this.expander = new DefaultExpander();
    }

    @Test
    public void testAddDSLMapping() {
        this.expander.addDSLMapping( this.file.getMapping() );
        // should not raise any exception
    }

    @Test
    public void testANTLRAddDSLMapping() {
        this.expander.addDSLMapping( this.tokenizedFile.getMapping() );
        // should not raise any exception
    }

    @Test
    public void testRegexp() throws Exception {
        this.expander.addDSLMapping( this.file.getMapping() );
        final Reader rules = new InputStreamReader( this.getClass().getResourceAsStream( "test_expansion.dslr" ) );
        final String result = this.expander.expand( rules );
    }

    @Test
    public void testANTLRRegexp() throws Exception {
        this.expander.addDSLMapping( this.tokenizedFile.getMapping() );
        final Reader rules = new InputStreamReader( this.getClass().getResourceAsStream( "test_expansion.dslr" ) );
        final String result = this.expander.expand( rules );
    }

    @Test
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

    @Test
    public void testExpandKeyword() throws Exception {
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[keyword]key {param}=Foo( attr=={param} )";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                      file.getErrors().size() );
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );

        String source = "rule x\nwhen\n key 1 \n key 2 \nthen\nend";
        String drl = ex.expand( source );
        System.out.println( drl );

        assertTrue( drl.contains( "attr==1" ) );
        assertTrue( drl.contains( "attr==2" ) );
        //System.err.println(ex.expand( "rule 'x' \n when \n foo \n then \n end" ));
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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
        equalsIgnoreWhiteSpace( expected,
                                drl );

    }

    @Test
    public void testANTLRExpandWithKeywordClashes() throws Exception {

        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]Invoke rule executor=ruleExec: RuleExecutor()\n" + "[then]Execute rule \"{id}\"=ruleExec.ExecuteSubRule( new Long({id}));";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                      file.getErrors().size() );

        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "package something;\n\nrule \"1\"\nwhen\n    Invoke rule executor\nthen\n    Execute rule \"5\"\nend";
        String expected = "package something;\n\nrule \"1\"\nwhen\n    ruleExec: RuleExecutor()\nthen\n    ruleExec.ExecuteSubRule( new Long(5));\nend";
        String drl = ex.expand( source );
        //        System.out.println("["+drl+"]" );
        //        System.out.println("["+expected+"]" );
        assertFalse( ex.hasErrors() );
        assertEquals( expected,
                      drl );
    }

    @Test
    public void testLineNumberError() throws Exception {
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]foo=Foo()" + NL + "[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );

        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "rule 'q'" + NL + "agenda-group 'x'" + NL + "when" + NL + "    __  " + NL +
                "then" + NL + "    bar 42" + NL + "\tgoober" + NL + "end";
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

    @Test
    public void testANTLRLineNumberError() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]foo=Foo()" + NL + "[then]bar {num}=baz({num});";
        file.parseAndLoad( new StringReader( dsl ) );

        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "rule 'q'" + NL + "agenda-group 'x'" + NL + "when" + NL + "    __  " + NL +
                "then" + NL + "    bar 42" + NL + "\tgoober" + NL + "end";
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

    @Test
    public void testANTLREnumExpand() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]When the credit rating is {rating:ENUM:Applicant.creditRating} = applicant:Applicant(credit=={rating})";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                      file.getErrors().size() );
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );
        String source = "rule \"TestNewDslSetup\"\ndialect \"mvel\"\nwhen\nWhen the credit rating is AA\nthen \nend";

        //        String source="rule \"TestNewDslSetup\"\n"+
        //                        "dialect \"mvel\"\n"+
        //                        "when\n"+
        //                            "When the credit rating is OK\n"+
        //                        "then\n"+
        //                        "end\n";

        String drl = ex.expand( source );

        String expected = "rule \"TestNewDslSetup\"\n" +
                          "dialect \"mvel\"\n" +
                          "when\n" +
                          "applicant:Applicant(credit==AA)\n" +
                          "then  \nend";

        assertFalse( ex.getErrors().toString(),
                     ex.hasErrors() );
        assertEquals( expected,
                      drl );

        //System.err.println(ex.expand( "rule 'x' \n when \n foo \n then \n end" ));
    }

    private boolean equalsIgnoreWhiteSpace(String expected,
                                           String actual) {
        String patternStr = expected.replaceAll( "\\s+",
                                                 "(\\\\s|\\\\n|\\\\r)*" );//.replaceAll( "\\n", "\\s*\\$" );
        Pattern pattern = Pattern.compile( patternStr,
                                           Pattern.DOTALL );
        Matcher m = pattern.matcher( actual );
        return m.matches();
    }

    @Test
    public void testExpandComplex() throws Exception {
        String source = "rule \"R\"\n"
                        + "dialect \"mvel\"\n"
                        + "when\n"
                        + "There is an TestObject\n"
                        + "-startDate is before 01-Jul-2011\n"
                        + "-endDate is after 01-Jul-2011\n"
                        + "then\n"
                        + "end\n";

        String expected = "rule \"R\"\n"
                          + "dialect \"mvel\"\n"
                          + "when\n"
                          + "TestObject(startDate>DateUtils.parseDate(\"01-Jul-2011\"), endDate>DateUtils.parseDate(\"01-Jul-2011\"))\n"
                          + "then\n"
                          + "end\n";

        checkExpansion(source, expected);
    }

    @Test
    public void testDontExpandCommentedLines() throws Exception {
        String source = "rule \"R\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "// There is an TestObject\n"
                + "// -startDate is before 01-Jul-2011\n"
                + "// -endDate is after 01-Jul-2011\n"
                + "then\n"
                + "end\n";

        String expected = "rule \"R\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "// There is an TestObject\n"
                + "// -startDate is before 01-Jul-2011\n"
                + "// -endDate is after 01-Jul-2011\n"
                + "then\n"
                + "end\n";

        checkExpansion(source, expected);
    }

    @Test
    public void testDontExpandCommentedBlocks() throws Exception {
        String source = "rule \"R\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "/*\n"
                + "There is an TestObject\n"
                + "-startDate is before 01-Jul-2011\n"
                + "-endDate is after 01-Jul-2011\n"
                + "*/\n"
                + "then\n"
                + "end\n";

        String expected = "rule \"R\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "\n"
                + "then\n"
                + "end\n";

        checkExpansion(source, expected);
    }

    private void checkExpansion(String source, String expected) throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]There is an TestObject=TestObject()\n"
                + "[when]-startDate is before {date}=startDate>DateUtils.parseDate(\"{date}\")\n"
                + "[when]-endDate is after {date}=endDate>DateUtils.parseDate(\"{date}\")";
        file.parseAndLoad( new StringReader( dsl ) );
        assertEquals( 0,
                file.getErrors().size() );

        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( file.getMapping() );

        String drl = ex.expand( source );
        assertFalse( ex.hasErrors() );

        assertEquals( expected, drl );
    }
}
