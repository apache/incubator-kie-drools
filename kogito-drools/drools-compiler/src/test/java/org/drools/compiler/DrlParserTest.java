package org.drools.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;

import org.drools.RuntimeDroolsException;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.lang.Expander;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.lang.dsl.DefaultExpanderResolver;
import org.junit.Ignore;
import org.junit.Test;

public class DrlParserTest {

    @Test
    public void testExpandDRL() throws Exception {
        String dsl = "[condition]Something=Something()\n[then]another=another();";
        String drl = "rule 'foo' \n when \n Something \n then \n another \nend";
        
        DrlParser parser = new DrlParser();
        String result = parser.getExpandedDRL( drl, new StringReader(dsl));
        assertEqualsIgnoreWhitespace( "rule 'foo' \n when \n Something() \n then \n another(); \nend", result );
    }
    
    @Test
    public void testExpandDRLUsingInjectedExpander() throws Exception {
        String dsl = "[condition]Something=Something()\n[then]another=another();";
        String drl = "rule 'foo' \n when \n Something \n then \n another \nend";
        
        
        DefaultExpanderResolver resolver = new DefaultExpanderResolver(new StringReader(dsl));
        
        
        final DSLMappingFile file = new DSLTokenizedMappingFile();
        if ( file.parseAndLoad( new StringReader(dsl) ) ) {
            final Expander expander = new DefaultExpander();
            expander.addDSLMapping( file.getMapping() );
            resolver.addExpander("*", expander);
        } else {
            throw new RuntimeDroolsException( "Error parsing and loading DSL file." + file.getErrors() );
        }

        DrlParser parser = new DrlParser();
        String result = parser.getExpandedDRL( drl, resolver);
        assertEqualsIgnoreWhitespace( "rule 'foo' \n when \n Something() \n then \n another(); \nend", result );
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

        DrlParser parser = new DrlParser();
        PackageDescr pkgDescr = parser.parse( drl );
        TypeDeclarationDescr bean1Type = pkgDescr.getTypeDeclarations().get( 0 );
        assertNull( bean1Type.getSuperTypeName() );

        TypeDeclarationDescr bean2Type = pkgDescr.getTypeDeclarations().get( 1 );
        assertEquals( "Bean1",
                      bean2Type.getSuperTypeName() );
    }
    
    @Test
    @Ignore("See JBRULES-3425: KnowledgeBuilder fails to build rules with BigDecimal\\BigInteger constraints comparing to zero")
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        assertEquals( 0,
                      errors.size() );
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        assertEquals( 0,
                      errors.size() );
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        assertEquals( 0,
                      errors.size() );
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        assertEquals( 0,
                      errors.size() );
    }

    @Test
    @Ignore("See JBRULES-3425: KnowledgeBuilder fails to build rules with BigDecimal\\BigInteger constraints comparing to zero")
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        assertEquals( 0,
                      errors.size() );
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        assertEquals( 0,
                      errors.size() );
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
}
