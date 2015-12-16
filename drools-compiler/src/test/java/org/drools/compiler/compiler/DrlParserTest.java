/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;

import org.drools.compiler.lang.Expander;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.dsl.DSLMappingFile;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.drools.compiler.lang.dsl.DefaultExpanderResolver;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;

public class DrlParserTest {

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testExpandDRL() throws Exception {
        String dsl = "[condition]Something=Something()" + NL + "[then]another=another();";
        String drl = "rule 'foo' " + NL + " when " + NL + " Something " + NL + " then " + NL + " another " + NL + "end";
        
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        String result = parser.getExpandedDRL( drl, new StringReader(dsl));
        assertEqualsIgnoreWhitespace( "rule 'foo' " + NL + " when " + NL + " Something() " + NL + " then " + NL + " another(); " + NL + "end", result );
    }
    
    @Test
    public void testExpandDRLUsingInjectedExpander() throws Exception {
        String dsl = "[condition]Something=Something()" + NL + "[then]another=another();";
        String drl = "rule 'foo' " + NL + " when " + NL + " Something " + NL + " then " + NL + " another " + NL + "end";
        
        
        DefaultExpanderResolver resolver = new DefaultExpanderResolver(new StringReader(dsl));
        
        
        final DSLMappingFile file = new DSLTokenizedMappingFile();
        if ( file.parseAndLoad( new StringReader(dsl) ) ) {
            final Expander expander = new DefaultExpander();
            expander.addDSLMapping( file.getMapping() );
            resolver.addExpander("*", expander);
        } else {
            throw new RuntimeException( "Error parsing and loading DSL file." + file.getErrors() );
        }

        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        String result = parser.getExpandedDRL( drl, resolver);
        assertEqualsIgnoreWhitespace( "rule 'foo' " + NL + " when " + NL + " Something() " + NL + " then " + NL + " another(); " + NL + "end", result );
    }

    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }
    
    @Test
    public void testDeclaredSuperType() throws Exception {
        String drl = "package foo \n"
                     + "declare Bean1 \n"
                     + "age: int \n"
                     + "name : String \n"
                     + "end \n"
                     + "declare Bean2 extends Bean1\n"
                     + "cheese : String \n"
                     + "end";

        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr pkgDescr = parser.parse( null, drl );
        TypeDeclarationDescr bean1Type = pkgDescr.getTypeDeclarations().get( 0 );
        assertNull( bean1Type.getSuperTypeName() );

        TypeDeclarationDescr bean2Type = pkgDescr.getTypeDeclarations().get( 1 );
        assertEquals( "Bean1",
                      bean2Type.getSuperTypeName() );
    }
    
    @Test
    public void testBigDecimalWithZeroValue() throws Exception {
        String drl = "package foo \n"
                     + "declare Bean1 \n"
                     + "field1: java.math.BigDecimal \n"
                     + "end \n"
                     + "rule \"bigdecimal\" \n"
                     + "when \n"
                     + "Bean1( field1 == 0B ) \n"
                     + "then \n"
                     + "end";

        createKBuilderAddDrlAndAssertHasNoErrors( drl );
    }

    @Test
    public void testBigDecimalWithZeroDecimalPointValue() throws Exception {
        String drl = "package foo \n"
                     + "declare Bean1 \n"
                     + "field1: java.math.BigDecimal \n"
                     + "end \n"
                     + "rule \"bigdecimal\" \n"
                     + "when \n"
                     + "Bean1( field1 == 0.0B ) \n"
                     + "then \n"
                     + "end";

        createKBuilderAddDrlAndAssertHasNoErrors( drl );
    }

    @Test
    public void testBigDecimalWithNonZeroValue() throws Exception {
        String drl = "package foo \n"
                     + "declare Bean1 \n"
                     + "field1: java.math.BigDecimal \n"
                     + "end \n"
                     + "rule \"bigdecimal\" \n"
                     + "when \n"
                     + "Bean1( field1 == 1B ) \n"
                     + "then \n"
                     + "end";

        createKBuilderAddDrlAndAssertHasNoErrors( drl );
    }

    @Test
    public void testBigDecimalWithNonZeroDecimalPointValue() throws Exception {
        String drl = "package foo \n"
                     + "declare Bean1 \n"
                     + "field1: java.math.BigDecimal \n"
                     + "end \n"
                     + "rule \"bigdecimal\" \n"
                     + "when \n"
                     + "Bean1( field1 == 1.0B ) \n"
                     + "then \n"
                     + "end";

        createKBuilderAddDrlAndAssertHasNoErrors( drl );
    }

    @Test
    public void testBigIntegerWithZeroValue() throws Exception {
        String drl = "package foo \n"
                     + "declare Bean1 \n"
                     + "field1: java.math.BigInteger \n"
                     + "end \n"
                     + "rule \"bigdecimal\" \n"
                     + "when \n"
                     + "Bean1( field1 == 0I ) \n"
                     + "then \n"
                     + "end";

        createKBuilderAddDrlAndAssertHasNoErrors( drl );
    }

    @Test
    public void testBigIntegerWithNonZeroValue() throws Exception {
        String drl = "package foo \n"
                     + "declare Bean1 \n"
                     + "field1: java.math.BigInteger \n"
                     + "end \n"
                     + "rule \"bigdecimal\" \n"
                     + "when \n"
                     + "Bean1( field1 == 1I ) \n"
                     + "then \n"
                     + "end";

        createKBuilderAddDrlAndAssertHasNoErrors( drl );
    }

    @Test
    public void testParseConsequenceWithSingleQuoteInsideDoubleQuotesFollowedByUpdate() {
        String drl = "declare Person\n" +
                "    name: String\n" +
                "end\n" +
                "\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    $p: Person()\n" +
                "then\n" +
                "    $p.setName(\"Some name with' single quote inside\");\n" +
                "    update($p);\n" +
                "end";

        createKBuilderAddDrlAndAssertHasNoErrors( drl );
    }

    @Test
    public void testParseConsequenceWithEscapedDoubleQuoteInsideDoubleQuotesFollowedByUpdate() {
        String drl = "declare Person\n" +
                "    name: String\n" +
                "end\n" +
                "\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    $p: Person()\n" +
                "then\n" +
                "    $p.setName(\"Some name with\\\" escaped double quote inside double quotes\");\n" +
                "    update($p);\n" +
                "end";

        createKBuilderAddDrlAndAssertHasNoErrors( drl );
    }

    private void createKBuilderAddDrlAndAssertHasNoErrors(String drl) {
        KnowledgeBuilder kbuilder = createKBuilderWithSpecifiedDrl( drl );
        assertHasNoErrors( kbuilder );
    }

    private KnowledgeBuilder createKBuilderWithSpecifiedDrl( String drl ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader(drl) ),
                      ResourceType.DRL );
        return kbuilder;
    }

    private void assertHasNoErrors(KnowledgeBuilder kbuilder) {
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        assertEquals( "Expected no build errors, but got: " + errors.toString(),
                      0,
                      errors.size() );
    }

}
