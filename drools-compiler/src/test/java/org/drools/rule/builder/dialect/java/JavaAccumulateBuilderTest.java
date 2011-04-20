package org.drools.rule.builder.dialect.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.Dialect;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageRegistry;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Accumulate;
import org.drools.rule.Package;
import org.drools.rule.builder.RuleBuildContext;
import org.junit.Before;
import org.junit.Test;

public class JavaAccumulateBuilderTest {

    private JavaAccumulateBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new JavaAccumulateBuilder();
    }

    @Test
    public void testBuildRuleBuildContextBaseDescr() {
        // $total : Integer() from accumulate( Cheese( $price : price ) init( int x = 0; ) action( x += $price ) result( new Integer( x ) ) ) 
        AccumulateDescr accumDescr = new AccumulateDescr();
        
        BindingDescr price = new BindingDescr( "$price", "price" );
        PatternDescr cheeseDescr = new PatternDescr( "org.drools.Cheese" );
        cheeseDescr.addBinding( price );
        accumDescr.setInputPattern( cheeseDescr );
        
        accumDescr.setInitCode( "int x = 0; int y = 0;" );
        accumDescr.setActionCode( "x += $price;" );
        accumDescr.setResultCode( "new Integer( x )" );
        
        //org.drools.rule.Package pkg = new org.drools.rule.Package( "org.drools" );
        final PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage( new PackageDescr( "org.drools" ) );
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
        PackageRegistry pkgReg = pkgBuilder.getPackageRegistry( "org.drools" );
        Package pkg = pkgReg.getPackage();
        DialectCompiletimeRegistry dialectRegistry = pkgReg.getDialectCompiletimeRegistry();
        Dialect dialect = dialectRegistry.getDialect( "java" );
                
        RuleDescr ruleDescr = new RuleDescr("test rule");
        RuleBuildContext context = new RuleBuildContext( pkgBuilder, ruleDescr, dialectRegistry, pkg, dialect);
        
        Accumulate accumulate = (Accumulate) builder.build( context, accumDescr );
        String generatedCode = (String) context.getMethods().get( 0 );
        
        assertTrue( generatedCode.contains( "private int x;" ) );
        assertTrue( generatedCode.contains( "private int y;" ) );
        assertTrue( generatedCode.contains( "x = 0;y = 0;" ) );
        
//        System.out.println( context.getInvokers() );
//        System.out.println( context.getMethods() );
    }
    
    @Test
    public void testFixInitCode() throws Exception {
        JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
        JavaAccumulateBuilder builder = new JavaAccumulateBuilder();
        
        String code = "int x = 0;";
        String expected = "x = 0;";
        BoundIdentifiers bindings = new BoundIdentifiers( new HashMap<String, Class<?>>(), new HashMap<String, Class<?>>() );
        JavaAnalysisResult analysis = analyzer.analyzeBlock( code, bindings);
        String result = builder.fixInitCode( analysis, code );
        assertEquals( expected, result );
        
        code = "$anExternalVar.method(); \nint aVar = 0, anotherVar=10    ;Integer bla = new Integer( 25);functionCall();\n";
        expected = "$anExternalVar.method(); \naVar = 0;anotherVar=10;bla = new Integer( 25);functionCall();\n";;
        analysis = analyzer.analyzeBlock( code, bindings);
        result = builder.fixInitCode( analysis, code );
        assertEquals( expected, result );
        
        code = "$anExternalVar.method(); String[] aVar = new String[] { \"a\", \"b\" }, anotherVar=new String[] { someStringVar }  ;final Integer bla = new Integer( 25);functionCall();\n";
        expected = "$anExternalVar.method(); aVar = new String[] { \"a\", \"b\" };anotherVar=new String[] { someStringVar };bla = new Integer( 25);functionCall();\n";
        analysis = analyzer.analyzeBlock( code,bindings);
        result = builder.fixInitCode( analysis, code );
        assertEquals( expected, result );

    }

}
