package org.drools.rule.builder.dialect.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.antlr.runtime.RecognitionException;
import org.drools.Cheese;
import org.drools.base.ClassObjectType;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageRegistry;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.Consequence;

public class JavaConsequenceBuilderTest {

    private JavaConsequenceBuilder builder;
    private RuleBuildContext       context;
    private RuleDescr              ruleDescr;

    private void setupTest(String consequence, Map<String, Object> namedConsequences) {
        builder = new JavaConsequenceBuilder();

        Package pkg = new Package( "org.drools" );
        pkg.addImport( new ImportDeclaration( "org.drools.Cheese" ) );

        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        PackageBuilder pkgBuilder = new PackageBuilder( pkg,
                                                        conf );

        ruleDescr = new RuleDescr( "test consequence builder" );
        ruleDescr.setConsequence( consequence );
        
        for ( Entry<String, Object> entry : namedConsequences.entrySet() ) {
            ruleDescr.getNamedConsequences().put( entry.getKey(), entry.getValue() );
        }

        Rule rule = new Rule( ruleDescr.getName() );
        rule.addPattern( new Pattern( 0,
                                      new ClassObjectType( Cheese.class ),
                                      "$cheese" ) );

        PackageRegistry pkgRegistry = pkgBuilder.getPackageRegistry( pkg.getName() );
        DialectCompiletimeRegistry reg = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        context = new RuleBuildContext( pkgBuilder,
                                        ruleDescr,
                                        reg,
                                        pkg,
                                        reg.getDialect( pkgRegistry.getDialect() ) );
        context.getBuildStack().push( rule.getLhs() );
        
        context.getDialect().getConsequenceBuilder().build( context, "default" );
        for ( String name : namedConsequences.keySet() ) {
            context.getDialect().getConsequenceBuilder().build( context, name );    
        }
        
        context.getDialect().addRule( context );
        pkgRegistry.getPackage().addRule( context.getRule() );
        pkgBuilder.compileAll();
        pkgBuilder.reloadAll();         
    }

    @Test
    public void testFixExitPointsReferences() {
        String consequence = " System.out.println(\"this is a test\");\n " + " exitPoints[\"foo\"].insert( new Cheese() );\n " + " System.out.println(\"we are done with exitPoints\");\n ";
        setupTest( consequence, new HashMap<String, Object>() );
        try {
            JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
            JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                      new Map[]{} );

            String fixed = builder.fixBlockDescr( context,
                                                  analysis,
                                                  (String) ruleDescr.getConsequence() );

            String expected = " System.out.println(\"this is a test\");\n " + 
                              " drools.getExitPoint(\"foo\").insert( new Cheese() );\n " + 
                              " System.out.println(\"we are done with exitPoints\");\n ";

//            System.out.println( "=============================" );
//            System.out.println( ruleDescr.getConsequence() );
//            System.out.println( "=============================" );
//            System.out.println( fixed );

            assertNotNull( context.getErrors().toString(),
                           fixed );
            assertEqualsIgnoreSpaces( expected,
                                      fixed );
        } catch ( RecognitionException e ) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFixEntryPointsReferences() {
        String consequence = " System.out.println(\"this is a test\");\n " + " entryPoints[\"foo\"].insert( new Cheese() );\n " + " System.out.println(\"we are done with entryPoints\");\n ";
        setupTest( consequence, new HashMap<String, Object>() );
        try {
            JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
            JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                      new Map[]{} );

            String fixed = builder.fixBlockDescr( context,
                                                  analysis,
                                                  (String) ruleDescr.getConsequence() );

            String expected = " System.out.println(\"this is a test\");\n " + 
                              " drools.getEntryPoint(\"foo\").insert( new Cheese() );\n " + 
                              " System.out.println(\"we are done with entryPoints\");\n ";

//            System.out.println( "=============================" );
//            System.out.println( ruleDescr.getConsequence() );
//            System.out.println( "=============================" );
//            System.out.println( fixed );

            assertNotNull( context.getErrors().toString(),
                           fixed );
            assertEqualsIgnoreSpaces( expected,
                                      fixed );
        } catch ( RecognitionException e ) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFixModifyBlocks() {
        String consequence = " System.out.println(\"this is a test\");\n " + " modify( $cheese ) { setPrice( 10 ), setAge( age ) }\n " + " System.out.println(\"we are done\");\n ";
        setupTest( consequence, new HashMap<String, Object>() );
        try {
            JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
            JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                      new Map[]{} );

            String fixed = builder.fixBlockDescr( context,
                                                  analysis,
                                                  (String) ruleDescr.getConsequence() );

            String expected = " System.out.println(\"this is a test\");\n" + 
                              "{ org.drools.Cheese __obj__ = (org.drools.Cheese) ( $cheese );\n" + 
                              "__obj__.setPrice( 10 );\n" + 
                              "__obj__.setAge( age );\n" + 
                              "update( __obj__ );}\n" + 
                              "System.out.println(\"we are done\");\n";

            assertNotNull( context.getErrors().toString(),
                           fixed );
            assertEqualsIgnoreSpaces( expected,
                                      fixed );
            //            System.out.println( "=============================" );
            //            System.out.println( ruleDescr.getConsequence() );
            //            System.out.println( "=============================" );
            //            System.out.println( fixed );

        } catch ( RecognitionException e ) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFixInsertCalls() {
        String consequence = " System.out.println(\"this is a test\");\n " + 
                             " insert( $cheese );\n " + 
                             " if( true ) { \n " +
                             "     insert($another); \n" +
                             " } else { \n"+
                             "     retract($oneMore); \n" +
                             " } \n" +
                             " // just in case, one more call: \n" +
                             " insert( $abc );\n"
                             ;
        setupTest( consequence, new HashMap<String, Object>() );
        try {
            JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
            JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                      new Map[]{} );

            String fixed = builder.fixBlockDescr( context,
                                                  analysis,
                                                  (String) ruleDescr.getConsequence() );
            fixed = new KnowledgeHelperFixer().fix( fixed );

            String expected = " System.out.println(\"this is a test\");\n " + 
                              " drools.insert( $cheese );\n " + 
                              " if( true ) { \n " +
                              "     drools.insert($another); \n" +
                              " } else { \n"+
                              "     drools.retract($oneMore); \n" +
                              " } \n" +
                              " // just in case, one more call: \n" +
                              " drools.insert( $abc );\n"
            ;

//                        System.out.println( "=============================" );
//                        System.out.println( ruleDescr.getConsequence() );
//                        System.out.println( "=============================" );
//                        System.out.println( fixed );
            assertNotNull( context.getErrors().toString(),
                           fixed );
            assertEqualsIgnoreSpaces( expected,
                                      fixed );

        } catch ( RecognitionException e ) {
            e.printStackTrace();
        }

    }
    
    @Test
    public void testDefaultConsequenceCompilation() {
        String consequence = " System.out.println(\"this is a test\");\n ";
        setupTest( consequence, new HashMap<String, Object>() );       
        assertNotNull( context.getRule().getConsequence() );
        assertTrue( context.getRule().getNamedConsequences().isEmpty() );
        assertTrue( context.getRule().getConsequence() instanceof CompiledInvoker );
        assertTrue( context.getRule().getConsequence() instanceof Consequence );
    }
    
    @Test
    public void testDefaultConsequenceWithSingleNamedConsequenceCompilation() {
        String defaultCon = " System.out.println(\"this is a test\");\n ";
        
        Map<String, Object> namedConsequences = new HashMap<String, Object>(); 
        String name1 =  " System.out.println(\"this is a test name1\");\n ";
        namedConsequences.put( "name1", name1 );
        
        setupTest( defaultCon, namedConsequences);       
        assertEquals( 1, context.getRule().getNamedConsequences().size() );
        
        assertTrue( context.getRule().getConsequence() instanceof CompiledInvoker );
        assertTrue( context.getRule().getConsequence() instanceof Consequence );        
        
        assertTrue( context.getRule().getNamedConsequences().get( "name1" ) instanceof CompiledInvoker );
        assertTrue( context.getRule().getNamedConsequences().get( "name1" ) instanceof Consequence );   
        
        assertNotSame( context.getRule().getConsequence(), context.getRule().getNamedConsequences().get( "name1" ) );
    }    
    
    @Test
    public void testDefaultConsequenceWithMultipleNamedConsequenceCompilation() {
        String defaultCon = " System.out.println(\"this is a test\");\n ";
        
        Map<String, Object> namedConsequences = new HashMap<String, Object>(); 
        String name1 =  " System.out.println(\"this is a test name1\");\n ";
        namedConsequences.put( "name1", name1 );
        String name2 =  " System.out.println(\"this is a test name2\");\n ";
        namedConsequences.put( "name2", name2 );        
        
        setupTest( defaultCon, namedConsequences);       
        assertEquals( 2, context.getRule().getNamedConsequences().size() );
        
        assertTrue( context.getRule().getConsequence() instanceof CompiledInvoker );
        assertTrue( context.getRule().getConsequence() instanceof Consequence );        
        
        assertTrue( context.getRule().getNamedConsequences().get( "name1" ) instanceof CompiledInvoker );
        assertTrue( context.getRule().getNamedConsequences().get( "name1" ) instanceof Consequence );   
        
        assertTrue( context.getRule().getNamedConsequences().get( "name2" ) instanceof CompiledInvoker );
        assertTrue( context.getRule().getNamedConsequences().get( "name2" ) instanceof Consequence );         
        
        assertNotSame( context.getRule().getConsequence(), context.getRule().getNamedConsequences().get( "name1" ) );
        assertNotSame( context.getRule().getConsequence(), context.getRule().getNamedConsequences().get( "name2" ) );
        assertNotSame(  context.getRule().getNamedConsequences().get( "name1"), context.getRule().getNamedConsequences().get( "name2" ) );
    }    

    private void assertEqualsIgnoreSpaces(String expected,
                                          String fixed) {
        assertEquals( expected.replaceAll( "\\s+",
                                           "" ),
                      fixed.replaceAll( "\\s+",
                                        "" ) );
    }

}
