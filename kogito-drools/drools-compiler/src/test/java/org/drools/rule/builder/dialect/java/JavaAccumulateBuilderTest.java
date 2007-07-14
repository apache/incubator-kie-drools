package org.drools.rule.builder.dialect.java;

import java.util.Set;

import junit.framework.TestCase;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.DescrFactory;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Accumulate;
import org.drools.rule.builder.RuleBuildContext;

public class JavaAccumulateBuilderTest extends TestCase {

    private JavaAccumulateBuilder builder;

    protected void setUp() throws Exception {
        super.setUp();
        builder = new JavaAccumulateBuilder();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBuildRuleBuildContextBaseDescr() {
        // $total : Integer() from accumulate( Cheese( $price : price ) init( int x = 0; ) action( x += $price ) result( new Integer( x ) ) ) 
        AccumulateDescr accumDescr = new DescrFactory().createAccumulate();
        
        FieldBindingDescr price = new FieldBindingDescr( "price", "$price" );
        PatternDescr cheeseDescr = new PatternDescr( "org.drools.Cheese" );
        cheeseDescr.addConstraint( price );
        accumDescr.setInputPattern( cheeseDescr );
        
        accumDescr.setInitCode( "int x = 0;" );
        accumDescr.setActionCode( "x += $price;" );
        accumDescr.setResultCode( "new Integer( x )" );
        
        org.drools.rule.Package pkg = new org.drools.rule.Package( "org.drools" );
        final PackageBuilder pkgBulider = new PackageBuilder(pkg);
        final PackageBuilderConfiguration conf = pkgBulider.getPackageBuilderConfiguration();
        Dialect dialect = pkgBulider.getPackageBuilderConfiguration().getDialectRegistry().getDialectConfiguration( "java" ).getDialect();
        
        
        RuleDescr ruleDescr = new RuleDescr("test rule");
        RuleBuildContext context = new RuleBuildContext( conf, pkg, ruleDescr, conf.getDialectRegistry(), dialect);
        
        Accumulate accumulate = (Accumulate) builder.build( context, accumDescr );
        
        assertTrue( context.getErrors().toString(), context.getErrors().isEmpty() );
//        System.out.println( context.getInvokers() );
//        System.out.println( context.getMethods() );
    }
    
    public void testFixInitCode() throws Exception {
        JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
        JavaAccumulateBuilder builder = new JavaAccumulateBuilder();
        
        String code = "int x = 0;";
        String expected = "x = 0;";
        JavaAnalysisResult analysis = analyzer.analyzeBlock( code, new Set[0]);
        String result = builder.fixInitCode( analysis, code );
        assertEquals( expected, result );
        
        code = "$anExternalVar.method(); \nint aVar = 0, anotherVar=10    ;Integer bla = new Integer( 25);functionCall();\n";
        expected = "$anExternalVar.method(); \naVar = 0;anotherVar=10;bla = new Integer( 25);functionCall();\n";;
        analysis = analyzer.analyzeBlock( code, new Set[0]);
        result = builder.fixInitCode( analysis, code );
        assertEquals( expected, result );
        
        code = "$anExternalVar.method(); String[] aVar = new String[] { \"a\", \"b\" }, anotherVar=new String[] { someStringVar }  ;final Integer bla = new Integer( 25);functionCall();\n";
        expected = "$anExternalVar.method(); aVar = new String[] { \"a\", \"b\" };anotherVar=new String[] { someStringVar };bla = new Integer( 25);functionCall();\n";
        analysis = analyzer.analyzeBlock( code, new Set[0]);
        result = builder.fixInitCode( analysis, code );
        assertEquals( expected, result );

    }

}
