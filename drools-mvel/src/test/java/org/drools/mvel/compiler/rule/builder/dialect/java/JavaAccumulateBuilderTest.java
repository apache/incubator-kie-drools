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
package org.drools.mvel.compiler.rule.builder.dialect.java;

import java.util.HashMap;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.Accumulate;
import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.mvel.java.JavaAccumulateBuilder;
import org.drools.mvel.java.JavaAnalysisResult;
import org.drools.mvel.java.JavaExprAnalyzer;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        PatternDescr cheeseDescr = new PatternDescr( "org.drools.mvel.compiler.Cheese" );
        cheeseDescr.addConstraint( price );
        accumDescr.setInputPattern( cheeseDescr );
        
        accumDescr.setInitCode( "int x = 0; int y = 0;" );
        accumDescr.setActionCode( "x += $price;" );
        accumDescr.setResultCode( "new Integer( x )" );
        
        //org.drools.core.rule.Package pkg = new org.kie.rule.Package( "org.kie" );
        final KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl();
        pkgBuilder.addPackage( new PackageDescr( "org.drools" ) );
        final KnowledgeBuilderConfigurationImpl conf = pkgBuilder.getBuilderConfiguration();
        PackageRegistry pkgReg = pkgBuilder.getPackageRegistry( "org.drools" );
        InternalKnowledgePackage pkg = pkgReg.getPackage();
        DialectCompiletimeRegistry dialectRegistry = pkgReg.getDialectCompiletimeRegistry();
        Dialect dialect = dialectRegistry.getDialect( "java" );
                
        RuleDescr ruleDescr = new RuleDescr("test rule");
        RuleBuildContext context = new RuleBuildContext( pkgBuilder, ruleDescr, dialectRegistry, pkg, dialect);
        
        Accumulate accumulate = (Accumulate) builder.build( context, accumDescr );
        String generatedCode = context.getMethods().get(0);

        assertThat(generatedCode.contains("private int x;")).isTrue();
        assertThat(generatedCode.contains("private int y;")).isTrue();
        assertThat(generatedCode.contains("x = 0;y = 0;")).isTrue();
        
//        System.out.println( context.getInvokers() );
//        System.out.println( context.getMethods() );
    }
    
    @Test
    public void testFixInitCode() throws Exception {
        JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
        JavaAccumulateBuilder builder = new JavaAccumulateBuilder();
        
        String code = "int x = 0;";
        String expected = "x = 0;";
        BoundIdentifiers bindings = new BoundIdentifiers( new HashMap<String, Class<?>>(), null );
        JavaAnalysisResult analysis = analyzer.analyzeBlock( code, bindings);
        String result = builder.fixInitCode( analysis, code );
        assertThat(result).isEqualTo(expected);
        
        code = "$anExternalVar.method(); \nint aVar = 0, anotherVar=10    ;Integer bla = new Integer( 25);functionCall();\n";
        expected = "$anExternalVar.method(); \naVar = 0;anotherVar=10;bla = new Integer( 25);functionCall();\n";;
        analysis = analyzer.analyzeBlock( code, bindings);
        result = builder.fixInitCode( analysis, code );
        assertThat(result).isEqualTo(expected);
        
        code = "$anExternalVar.method(); String[] aVar = new String[] { \"a\", \"b\" }, anotherVar=new String[] { someStringVar }  ;final Integer bla = new Integer( 25);functionCall();\n";
        expected = "$anExternalVar.method(); aVar = new String[] { \"a\", \"b\" };anotherVar=new String[] { someStringVar };bla = new Integer( 25);functionCall();\n";
        analysis = analyzer.analyzeBlock( code,bindings);
        result = builder.fixInitCode( analysis, code );
        assertThat(result).isEqualTo(expected);

    }

}
