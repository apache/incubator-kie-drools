package org.drools.lang.dsl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DSLMappingFileTest {
    private DSLMappingFile file     = null;
    private final String   filename = "test_metainfo.dsl";

    @Test
    public void testParseFile() {
        try {
            final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( this.filename ) );
            this.file = new DSLTokenizedMappingFile();

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

    @Test
    public void testParseFileWithEscaptedBrackets() {
        String file = "[when][]ATTRIBUTE \"{attr}\" IS IN \\[{list}\\]=Attribute( {attr} in ({list}) )";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertTrue( this.file.getErrors().toString(),
                        parsingResult );
            assertTrue( this.file.getErrors().isEmpty() );

            assertEquals( 1,
                          this.file.getMapping().getEntries().size() );

            DSLMappingEntry entry = (DSLMappingEntry) this.file.getMapping().getEntries().get( 0 );

            assertEquals( DSLMappingEntry.CONDITION,
                          entry.getSection() );
            assertEquals( DSLMappingEntry.EMPTY_METADATA,
                          entry.getMetaData() );
            assertEquals( "ATTRIBUTE \"{attr}\" IS IN \\[{list}\\]",
                          entry.getMappingKey() );
            assertEquals( "Attribute( {attr} in ({list}) )",
                          entry.getMappingValue() );

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

    @Test
    public void testParseFileWithEscaptedCurlyBrackets() {
        String file = "[consequence][$policy]Add surcharge {surcharge} to Policy=modify(policy) \\{price = {surcharge}\\}";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertTrue( this.file.getErrors().toString(),
                        parsingResult );
            assertTrue( this.file.getErrors().isEmpty() );

            assertEquals( 1,
                          this.file.getMapping().getEntries().size() );

            DSLMappingEntry entry = (DSLMappingEntry) this.file.getMapping().getEntries().get( 0 );

            assertEquals( DSLMappingEntry.CONSEQUENCE,
                          entry.getSection() );
            assertEquals( "$policy",
                          entry.getMetaData().toString() );
            assertEquals( "Add surcharge {surcharge} to Policy",
                          entry.getMappingKey() );
            assertEquals( "modify(policy) \\{price = {surcharge}\\}",
                          entry.getMappingValue() );
            
            String input = "rule x\nwhen\nthen\nAdd surcharge 300 to Policy\nend\n";
            String expected = "rule x\nwhen\nthen\nmodify(policy) {price = 300}\nend\n"; 
            
            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping( this.file.getMapping() );

            final String result = de.expand( input );
            
//            String result = entry.getKeyPattern().matcher( input ).replaceAll( entry.getValuePattern() );
            
            assertEquals( expected, 
                          result );

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }
    /**
     * Right now this test fails because there is no RHS for the rule. It connects the "then" and "end" to "thenend".
     */
    @Test
    public void testNoRHS() {
        String file = "[then]TEST=System.out.println(\"DO_SOMETHING\");\n" +
        "[when]code {code1} occurs and sum of all digit not equal \\( {code2} \\+ {code3} \\)=AAAA( cd1 == {code1}, cd2 != ( {code2} + {code3} ))\n"
                      + "[when]code {code1} occurs=BBBB\n";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertTrue( this.file.getErrors().toString(),
                        parsingResult );
            assertTrue( this.file.getErrors().isEmpty() );

            final String LHS = "code 1041 occurs and sum of all digit not equal ( 1034 + 1035 )";
            final String rule = "rule \"x\"\nwhen\n" + LHS + "\nthen\nend";

            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping( this.file.getMapping() );

            final String ruleAfterExpansion = de.expand( rule );

            final String expected = "rule \"x\"\nwhen\nAAAA( cd1 == 1041, cd2 != ( 1034 + 1035 ))\nthen\nend";

            assertEquals( expected,
                          ruleAfterExpansion );

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

    public void FIXME_testParseFileWithEscapes() {
        String file = "[then]TEST=System.out.println(\"DO_SOMETHING\");\n" + "[when]code {code1} occurs and sum of all digit not equal \\( {code2} \\+ {code3} \\)=AAAA( cd1 == {code1}, cd2 != ( {code2} + {code3} ))\n"
                      + "[when]code {code1} occurs=BBBB\n";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertTrue( this.file.getErrors().toString(),
                        parsingResult );
            assertTrue( this.file.getErrors().isEmpty() );

            final String LHS = "code 1041 occurs and sum of all digit not equal ( 1034 + 1035 )";
            final String rule = "rule \"x\"\nwhen\n" + LHS + "\nthen\nTEST\nend";

            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping( this.file.getMapping() );

            final String ruleAfterExpansion = de.expand( rule );

            final String expected = "rule \"x\"\nwhen\nAAAA( cd1 == 1041, cd2 != ( 1034 + 1035 ))\nthen\nSystem.out.println(\"DO_SOMETHING\");\nend\n";

            assertEquals( expected,
                          ruleAfterExpansion );

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

    public void FIXME_testParseFileWithEscaptedEquals() {
        String file = "[when][]something:\\={value}=Attribute( something == \"{value}\" )";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertTrue( this.file.getErrors().toString(),
                        parsingResult );
            assertTrue( this.file.getErrors().isEmpty() );

            assertEquals( 1,
                          this.file.getMapping().getEntries().size() );

            DSLMappingEntry entry = (DSLMappingEntry) this.file.getMapping().getEntries().get( 0 );

            assertEquals( DSLMappingEntry.CONDITION,
                          entry.getSection() );
            assertEquals( DSLMappingEntry.EMPTY_METADATA,
                          entry.getMetaData() );
            assertEquals( "something:={value}",
                          entry.getMappingKey() );
            assertEquals( "Attribute( something == \"{value}\" )",
                          entry.getMappingValue() );

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

    public void FIXME_testEnum() {
        String file = "[when][]ATTRIBUTE {attr:ENUM:Attribute.value} in {list}=Attribute( {attr} in ({list}) )";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertTrue( this.file.getErrors().toString(),
                        parsingResult );
            assertTrue( this.file.getErrors().isEmpty() );

            assertEquals( 1,
                          this.file.getMapping().getEntries().size() );

            DSLMappingEntry entry = (DSLMappingEntry) this.file.getMapping().getEntries().get( 0 );

            assertEquals( DSLMappingEntry.CONDITION,
                          entry.getSection() );
            assertEquals( DSLMappingEntry.EMPTY_METADATA,
                          entry.getMetaData() );
            System.out.println( entry.getValuePattern() );
            System.out.println( entry.getVariables() );
            assertEquals( "ATTRIBUTE {attr:ENUM:Attribute.value} in {list}",
                          entry.getMappingKey() );
            assertEquals( "Attribute( {attr} in ({list}) )",
                          entry.getMappingValue() );

            assertEquals( "(\\W|^)ATTRIBUTE\\s+(.*?)\\s+in\\s+(.*?)$",
                          entry.getKeyPattern().toString() );

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }
}
