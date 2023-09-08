/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.lang.dsl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.drools.drl.parser.lang.dsl.DSLMappingEntry;
import org.drools.drl.parser.lang.dsl.DSLMappingFile;
import org.drools.drl.parser.lang.dsl.DSLTokenizedMappingFile;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class DSLMappingFileTest {
    private DSLMappingFile file     = null;
    private final String   filename = "test_metainfo.dsl";
    private static final String NL = System.getProperty("line.separator");


    @Test
    public void testParseFile() {
        try {
            final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( this.filename ) );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertThat(parsingResult).as(this.file.getErrors().toString()).isTrue();
            assertThat(this.file.getErrors().isEmpty()).isTrue();

            assertThat(this.file.getMapping().getEntries().size()).isEqualTo(31);
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

            assertThat(parsingResult).as(this.file.getErrors().toString()).isTrue();
            assertThat(this.file.getErrors().isEmpty()).isTrue();

            assertThat(this.file.getMapping().getEntries().size()).isEqualTo(1);

            DSLMappingEntry entry = this.file.getMapping().getEntries().get(0);

            assertThat(entry.getSection()).isEqualTo(DSLMappingEntry.CONDITION);
            assertThat(entry.getMetaData()).isEqualTo(DSLMappingEntry.EMPTY_METADATA);
            assertThat(entry.getMappingKey()).isEqualTo("ATTRIBUTE \"{attr}\" IS IN \\[{list}\\]");
            assertThat(entry.getMappingValue()).isEqualTo("Attribute( {attr} in ({list}) )");

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

            assertThat(parsingResult).as(this.file.getErrors().toString()).isTrue();
            assertThat(this.file.getErrors().isEmpty()).isTrue();

            assertThat(this.file.getMapping().getEntries().size()).isEqualTo(1);

            DSLMappingEntry entry = this.file.getMapping().getEntries().get(0);

            assertThat(entry.getSection()).isEqualTo(DSLMappingEntry.CONSEQUENCE);
            assertThat(entry.getMetaData().toString()).isEqualTo("$policy");
            assertThat(entry.getMappingKey()).isEqualTo("Add surcharge {surcharge} to Policy");
            assertThat(entry.getMappingValue()).isEqualTo("modify(policy) \\{price = {surcharge}\\}");
            
            String input = "rule x" + NL + "when" + NL + "then" + NL + "Add surcharge 300 to Policy" + NL + "end" + NL + "";
            String expected = "rule x" + NL + "when" + NL + "then" + NL + "modify(policy) {price = 300}" + NL + "end" + NL + "";
            
            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping( this.file.getMapping() );

            final String result = de.expand( input );

//            String result = entry.getKeyPattern().matcher( input ).replaceAll( entry.getValuePattern() );
            
            assertThat(result).isEqualTo(expected);

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
        String file = "[then]TEST=System.out.println(\"DO_SOMETHING\");" + NL + "" +
        "[when]code {code1} occurs and sum of all digit not equal \\( {code2} \\+ {code3} \\)=AAAA( cd1 == {code1}, cd2 != ( {code2} + {code3} ))" + NL + ""
                      + "[when]code {code1} occurs=BBBB" + NL + "";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertThat(parsingResult).as(this.file.getErrors().toString()).isTrue();
            assertThat(this.file.getErrors().isEmpty()).isTrue();

            final String LHS = "code 1041 occurs and sum of all digit not equal ( 1034 + 1035 )";
            final String rule = "rule \"x\"" + NL + "when" + NL + "" + LHS + "" + NL + "then" + NL + "end";

            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping( this.file.getMapping() );

            final String ruleAfterExpansion = de.expand( rule );

            final String expected = "rule \"x\"" + NL + "when" + NL + "AAAA( cd1 == 1041, cd2 != ( 1034 + 1035 ))" + NL + "then" + NL + "end";

            assertThat(ruleAfterExpansion).isEqualTo(expected);

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

    @Test
    public void testParseFileWithEscapes() {
        String file = "[then]TEST=System.out.println(\"DO_SOMETHING\");" + NL + "" + "[when]code {code1} occurs and sum of all digit not equal \\( {code2} \\+ {code3} \\)=AAAA( cd1 == {code1}, cd2 != ( {code2} + {code3} ))" + NL + ""
                      + "[when]code {code1} occurs=BBBB" + NL + "";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertThat(parsingResult).as(this.file.getErrors().toString()).isTrue();
            assertThat(this.file.getErrors().isEmpty()).isTrue();

            final String LHS = "code 1041 occurs and sum of all digit not equal ( 1034 + 1035 )";
            final String rule = "rule \"x\"" + NL + "when" + NL + "" + LHS + "" + NL + "then" + NL + "TEST" + NL + "end";

            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping( this.file.getMapping() );

            final String ruleAfterExpansion = de.expand( rule );

            final String expected = "rule \"x\"" + NL + "when" + NL + "AAAA( cd1 == 1041, cd2 != ( 1034 + 1035 ))" + NL + "then" + NL + "System.out.println(\"DO_SOMETHING\");" + NL + "end";

            assertThat(ruleAfterExpansion).isEqualTo(expected);

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

    @Test @Ignore
    public void testParseFileWithEscaptedEquals() {
        String file = "[when][]something:\\={value}=Attribute( something == \"{value}\" )";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertThat(parsingResult).as(this.file.getErrors().toString()).isTrue();
            assertThat(this.file.getErrors().isEmpty()).isTrue();

            assertThat(this.file.getMapping().getEntries().size()).isEqualTo(1);

            DSLMappingEntry entry = this.file.getMapping().getEntries().get(0);

            assertThat(entry.getSection()).isEqualTo(DSLMappingEntry.CONDITION);
            assertThat(entry.getMetaData()).isEqualTo(DSLMappingEntry.EMPTY_METADATA);
            assertThat(entry.getMappingKey()).isEqualTo("something:={value}");
            assertThat(entry.getMappingValue()).isEqualTo("Attribute( something == \"{value}\" )");

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }

    @Test
    public void testEnum() {
        String file = "[when][]ATTRIBUTE {attr:ENUM:Attribute.value} in {list}=Attribute( {attr} in ({list}) )";
        try {
            final Reader reader = new StringReader( file );
            this.file = new DSLTokenizedMappingFile();

            final boolean parsingResult = this.file.parseAndLoad( reader );
            reader.close();

            assertThat(parsingResult).as(this.file.getErrors().toString()).isTrue();
            assertThat(this.file.getErrors().isEmpty()).isTrue();

            assertThat(this.file.getMapping().getEntries().size()).isEqualTo(1);

            DSLMappingEntry entry = this.file.getMapping().getEntries().get(0);

            assertThat(entry.getSection()).isEqualTo(DSLMappingEntry.CONDITION);
            assertThat(entry.getMetaData()).isEqualTo(DSLMappingEntry.EMPTY_METADATA);
            System.out.println( entry.getValuePattern() );
            System.out.println( entry.getVariables() );
            assertThat(entry.getMappingKey()).isEqualTo("ATTRIBUTE {attr:ENUM:Attribute.value} in {list}");
            assertThat(entry.getMappingValue()).isEqualTo("Attribute( {attr} in ({list}) )");

            assertThat(entry.getKeyPattern().toString()).isEqualTo("(?:(?<=^)|(?<=\\W))ATTRIBUTE\\s+(.*?)\\s+in\\s+(.*?)$");

        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Should not raise exception " );
        }

    }
}
