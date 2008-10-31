package org.drools.rule.builder.dialect.java;

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
        
        builder = new JavaConsequenceBuilder();

        Package pkg = new Package( "org.drools" );
        pkg.addImport( new ImportDeclaration( "org.drools.Cheese" ) );
        
        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        PackageBuilder pkgBuilder = new PackageBuilder( pkg, conf );

        String consequence = " System.out.println(\"this is a test\");\n " + " modify( $cheese ) { setPrice( 10 ), setAge( age ) }\n " + " System.out.println(\"we are done\");\n ";
        ruleDescr = new RuleDescr( "test modify block" );
        ruleDescr.setConsequence( consequence );
        
        Rule rule = new Rule( ruleDescr.getName() );
        rule.addPattern( new Pattern(0, new ClassObjectType(Cheese.class), "$cheese") );

        PackageRegistry pkgRegistry = pkgBuilder.getPackageRegistry( pkg.getName() );
        DialectCompiletimeRegistry reg = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        context = new RuleBuildContext( pkgBuilder,
                                        ruleDescr,
                                        reg,
                                        pkg,                                        
                                        reg.getDialect( pkgRegistry.getDialect() ) );
        context.getBuildStack().push( rule.getLhs() );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDummy() {}
    
    // this test requires mvel 1.2.19. Leaving it commented until mvel is released.
    public void testFixModifyBlocks() {

        try {
            JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
            JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                      new Set[]{} );

            String fixed = builder.fixModifyBlocks( context,
                                                    analysis,
                                                    (String) ruleDescr.getConsequence() );
            
            String expected = " System.out.println(\"this is a test\");\n"+
                              "{ org.drools.Cheese __obj__ = (org.drools.Cheese) ( $cheese );\n" +
                              "modifyRetract( __obj__ );\n"+
                              "__obj__.setPrice( 10 );\n"+
                              "__obj__.setAge( age );\n"+
                              "modifyInsert( __obj__ );}\n"+
                              "System.out.println(\"we are done\");\n";
            
            assertNotNull( context.getErrors().toString(), fixed );
            assertEqualsIgnoreSpaces( expected, fixed );
//            System.out.println( "=============================" );
//            System.out.println( ruleDescr.getConsequence() );
//            System.out.println( "=============================" );
//            System.out.println( fixed );

        } catch ( RecognitionException e ) {
            e.printStackTrace();
        }

    }

    private void assertEqualsIgnoreSpaces(String expected,
                                          String fixed) {
        assertEquals( expected.replaceAll( "\\s+", "" ), fixed.replaceAll( "\\s+", "" ) );
    }

}
