package org.drools.lang.dsl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DSLTokenizedMappingFileTest {

    // Due to a bug in JDK 5, a workaround for zero-widht lookbehind has to be used.
    // JDK works correctly with "(?<=^|\\W)"
    private static final String lookbehind = "(?:(?<=^)|(?<=\\W))";

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
    public void testParseFileWithBrackets() {
        String file = "[when]ATTRIBUTE \"{attr}\" IS IN [{list}]=Attribute( {attr} in ({list}) )";
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
            assertEquals( lookbehind + "ATTRIBUTE\\s+\"(.*?)\"\\s+IS\\s+IN\\s+[(.*?)](?=\\W|$)",
                          entry.getKeyPattern().toString() );
            //Attribute( {attr} in ({list}) )
            assertEquals( "Attribute( {attr} in ({list}) )",
                          entry.getValuePattern() );

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }
    }

    @Test
    public void testParseFileWithEscaptedBrackets() {
        String file = "[when]ATTRIBUTE \"{attr}\" IS IN \\[{list}\\]=Attribute( {attr} in ({list}) )";
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
            
            assertEquals( lookbehind + "ATTRIBUTE\\s+\"(.*?)\"\\s+IS\\s+IN\\s+\\[(.*?)\\](?=\\W|$)",
                          entry.getKeyPattern().toString() );
            //Attribute( {attr} in ({list}) )
            assertEquals( "Attribute( {attr} in ({list}) )",
                          entry.getValuePattern() );

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

    @Test
    public void testParseFileWithEscapes() {
        String file = "[then]TEST=System.out.println(\"DO_SOMETHING\");\n" + 
                      "[when]code {code1} occurs and sum of all digit not equal \\( {code2} \\+ {code3} \\)=AAAA( cd1 == {code1}, cd2 != ( {code2} + {code3} ))\n" + 
                      "[when]code {code1} occurs=BBBB\n";
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
            de.addDSLMapping(this.file.getMapping());
                    
            final String ruleAfterExpansion = de.expand(rule);
            
            final String expected = "rule \"x\"\nwhen\nAAAA( cd1 == 1041, cd2 != ( 1034 + 1035 ))\nthen\nSystem.out.println(\"DO_SOMETHING\");\nend";
            
            assertEquals( expected, ruleAfterExpansion );
            
        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

    @Test
    public void testParseFileWithEscaptedEquals() {
        String file = "[when]something:\\={value}=Attribute( something == \"{value}\" )";
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
            assertEquals( lookbehind + "something:\\=(.*?)$",
                          entry.getKeyPattern().toString() );
            assertEquals( "Attribute( something == \"{value}\" )",
                          entry.getValuePattern() );

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }
}
