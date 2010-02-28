package org.drools.rule.builder.dialect.java;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

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

public class JavaConsequenceBuilderTest extends TestCase {

    private JavaConsequenceBuilder builder;
    private RuleBuildContext       context;
    private RuleDescr              ruleDescr;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void setupTest(String consequence) {
        builder = new JavaConsequenceBuilder();

        Package pkg = new Package( "org.drools" );
        pkg.addImport( new ImportDeclaration( "org.drools.Cheese" ) );

        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        PackageBuilder pkgBuilder = new PackageBuilder( pkg,
                                                        conf );

        ruleDescr = new RuleDescr( "test consequence builder" );
        ruleDescr.setConsequence( consequence );

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
    }

    public void testFixExitPointsReferences() {
        String consequence = " System.out.println(\"this is a test\");\n " + " exitPoints[\"foo\"].insert( new Cheese() );\n " + " System.out.println(\"we are done with exitPoints\");\n ";
        setupTest( consequence );
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

    public void testFixEntryPointsReferences() {
        String consequence = " System.out.println(\"this is a test\");\n " + " entryPoints[\"foo\"].insert( new Cheese() );\n " + " System.out.println(\"we are done with entryPoints\");\n ";
        setupTest( consequence );
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

    public void testFixModifyBlocks() {
        String consequence = " System.out.println(\"this is a test\");\n " + " modify( $cheese ) { setPrice( 10 ), setAge( age ) }\n " + " System.out.println(\"we are done\");\n ";
        setupTest( consequence );
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
        setupTest( consequence );
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

    private void assertEqualsIgnoreSpaces(String expected,
                                          String fixed) {
        assertEquals( expected.replaceAll( "\\s+",
                                           "" ),
                      fixed.replaceAll( "\\s+",
                                        "" ) );
    }

}
