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
package org.drools.mvel.compiler.lang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.lang.DRLParser;
import org.drools.drl.parser.lang.Expander;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.drl.parser.DRLFactory.buildParser;

public class ErrorsParserTest {

    @Test
    public void testNotBindindShouldBarf() throws Exception {
        final DRLParser parser = parseResource( "not_with_binding_error.drl" );
        parser.compilationUnit();
        assertThat(parser.hasErrors()).isTrue();
    }

    @Test
    public void testExpanderErrorsAfterExpansion() throws Exception {

        String name = "expander_post_errors.dslr";
        Expander expander = new DefaultExpander();
        String expanded = expander.expand( this.getReader( name ) );
        
        DRLParser parser = parse( name, expanded );
        parser.compilationUnit();
        assertThat(parser.hasErrors()).isTrue();

        assertThat(parser.getErrors().size()).isEqualTo(1);
        DroolsParserException err = parser.getErrors().get(0);
        assertThat(err.getLineNumber()).isEqualTo(6);
    }

    @Test
    public void testInvalidSyntax_Catches() throws Exception {
        DRLParser parser = parseResource("invalid_syntax.drl");
        parser.compilationUnit();
        assertThat(parser.hasErrors()).isTrue();
    }

    @Test
    public void testMultipleErrors() throws Exception {
        DRLParser parser = parseResource( "multiple_errors.drl" );
        parser.compilationUnit();
        assertThat(parser.hasErrors()).isTrue();
        assertThat(parser.getErrors().size()).isEqualTo(2);
    }

    @Test
    public void testPackageGarbage() throws Exception {
        DRLParser parser = parseResource( "package_garbage.drl" );
        parser.compilationUnit();
        assertThat(parser.hasErrors()).isTrue();
    }

    @Test
    public void testEvalWithSemicolon() throws Exception {
        DRLParser parser = parseResource( "eval_with_semicolon.drl" );
        parser.compilationUnit();
        assertThat(parser.hasErrors()).isTrue();
        assertThat(parser.getErrorMessages().size()).isEqualTo(1);
        assertThat(parser.getErrors().get(0).getErrorCode()).isEqualTo("ERR 102");
    }

    @Test
    public void testLexicalError() throws Exception {
        DRLParser parser = parseResource( "lex_error.drl" );
        parser.compilationUnit();
        assertThat(parser.hasErrors()).isTrue();
    }

    @Test
    public void testTempleteError() throws Exception {
        DRLParser parser = parseResource( "template_test_error.drl" );
        parser.compilationUnit();
        assertThat(parser.hasErrors()).isTrue();
    }

    @Test
    public void testErrorMessageForMisplacedParenthesis() throws Exception {
        final DRLParser parser = parseResource( "misplaced_parenthesis.drl" );
        parser.compilationUnit();

        assertThat(parser.hasErrors()).as("Parser should have raised errors").isTrue();

        assertThat(parser.getErrors().size()).isEqualTo(1);

        assertThat(parser.getErrors().get(0).getErrorCode()).isEqualTo("ERR 102");
    }

    @Test
    public void testNPEOnParser() throws Exception {
        final DRLParser parser = parseResource( "npe_on_parser.drl" );
        parser.compilationUnit();
        assertThat(parser.hasErrors()).as("Parser should have raised errors").isTrue();

        assertThat(parser.getErrors().size()).isEqualTo(1);

        assertThat(parser.getErrors().get(0).getErrorCode().equals("ERR 102")).isTrue();
    }

    @Test
    public void testCommaMisuse() throws Exception {
        final DRLParser parser = parseResource( "comma_misuse.drl" );
        try {
            parser.compilationUnit();
            assertThat(parser.hasErrors()).as("Parser should have raised errors").isTrue();
        } catch ( NullPointerException npe ) {
            fail( "Should not raise NPE" );
        }
    }

    private DRLParser parse(final String source,
                            final String text) throws Exception {
        return buildParser(text, LanguageLevelOption.DRL5);
    }

    private Reader getReader(final String name) throws Exception {
        final InputStream in = getClass().getResourceAsStream( name );

        return new InputStreamReader( in );
    }

    private DRLParser parseResource(final String name) throws Exception {

        final Reader reader = getReader(name);

        final StringBuilder text = new StringBuilder();

        final char[] buf = new char[1024];
        int len;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }

        return parse( name,
                      text.toString() );
    }
}
