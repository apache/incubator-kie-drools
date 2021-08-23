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

package org.drools.mvel.compiler.rule.builder.dialect.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.runtime.RecognitionException;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.base.ClassObjectType;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.java.JavaAnalysisResult;
import org.drools.mvel.java.JavaExprAnalyzer;
import org.junit.Test;
import org.kie.internal.builder.conf.PropertySpecificOption;

import static org.drools.mvel.asm.AsmUtil.fixBlockDescr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class JavaConsequenceBuilderPRAlwaysTest {

    private RuleBuildContext context;
    private RuleDescr ruleDescr;

    private void setupTest(String consequence, Map<String, Object> namedConsequences) {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.drools" );
        pkg.addImport( new ImportDeclaration( "org.drools.mvel.compiler.Cheese" ) );

        KnowledgeBuilderConfigurationImpl conf = new KnowledgeBuilderConfigurationImpl();
        // Although it should be the default, for completeness we explicit this is the test cases how RHS should be rewritten as:
        conf.setOption(PropertySpecificOption.ALWAYS);
        KnowledgeBuilderImpl kBuilder = new KnowledgeBuilderImpl( pkg, conf );

        ruleDescr = new RuleDescr( "test consequence builder" );
        ruleDescr.setConsequence( consequence );
        
        for ( Entry<String, Object> entry : namedConsequences.entrySet() ) {
            ruleDescr.addNamedConsequences( entry.getKey(), entry.getValue() );
        }

        RuleImpl rule = ruleDescr.toRule();
        
        PackageRegistry pkgRegistry = kBuilder.getPackageRegistry( pkg.getName() );
        DialectCompiletimeRegistry reg = kBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        context = new RuleBuildContext( kBuilder,
                                        ruleDescr,
                                        reg,
                                        pkg,
                                        reg.getDialect( pkgRegistry.getDialect() ) );        
        
        rule.addPattern( new Pattern( 0,
                                      new ClassObjectType( Cheese.class ),
                                      "$cheese" ) );
        
        Pattern p = new Pattern( 1,
                               new ClassObjectType( Person.class ),
                               "$persone" );
        
        
        
        Declaration declr = p.addDeclaration( "age" );

        final InternalReadAccessor extractor = PatternBuilder.getFieldReadAccessor(context,
                                                                                   new BindingDescr("age", "age"),
                                                                                   p,
                                                                                   "age",
                                                                                   declr,
                                                                                   true);
        
        rule.addPattern( p );
        
        context.getDeclarationResolver().pushOnBuildStack(rule.getLhs());
        
        context.getDialect().getConsequenceBuilder().build(context, RuleImpl.DEFAULT_CONSEQUENCE_NAME);
        for ( String name : namedConsequences.keySet() ) {
            context.getDialect().getConsequenceBuilder().build( context, name );
        }
        
        context.getDialect().addRule( context );
        pkgRegistry.getPackage().addRule(context.getRule());
        kBuilder.compileAll();
        kBuilder.reloadAll();
    }

    @Test
    public void testFixExitPointsReferences() {
        String consequence = 
            " System.out.println(\"this is a test\");\n " + 
            " exitPoints[\"foo\"].insert( new Cheese() );\n " + 
            " System.out.println(\"we are done with exitPoints\");\n ";
        setupTest( consequence, new HashMap<String, Object>() );
        try {
            JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
            JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                      new BoundIdentifiers( new HashMap<String, Class<?>>(), null ) );

            String fixed = fixBlockDescr(context, analysis, new HashMap<String, Declaration>());

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
        String consequence = 
            " System.out.println(\"this is a test\");\n " + 
            " entryPoints[\"foo\"].insert( new Cheese() );\n " + 
            " System.out.println(\"we are done with entryPoints\");\n ";
        setupTest( "", new HashMap<String, Object>() );
        try {
            ruleDescr.setConsequence( consequence );
            JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
            JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                      new BoundIdentifiers( new HashMap<String, Class<?>>(), null ) );

            String fixed = fixBlockDescr( context, analysis, new HashMap<String,Declaration>() );

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
    public void testDefaultConsequenceCompilation() {
        String consequence = " System.out.println(\"this is a test\");\n ";
        setupTest( consequence, new HashMap<String, Object>() );
        assertNotNull( context.getRule().getConsequence() );
        assertFalse( context.getRule().hasNamedConsequences() );
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

        assertTrue( context.getRule().getConsequence() instanceof CompiledInvoker );
        assertTrue( context.getRule().getConsequence() instanceof Consequence );
        
        assertTrue( context.getRule().getNamedConsequence( "name1" ) instanceof CompiledInvoker );
        assertTrue( context.getRule().getNamedConsequence( "name1" ) instanceof Consequence );
        
        assertNotSame( context.getRule().getConsequence(), context.getRule().getNamedConsequence( "name1" ) );
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

        assertTrue( context.getRule().getConsequence() instanceof CompiledInvoker );
        assertTrue( context.getRule().getConsequence() instanceof Consequence );
        
        assertTrue( context.getRule().getNamedConsequence( "name1" ) instanceof CompiledInvoker );
        assertTrue( context.getRule().getNamedConsequence( "name1" ) instanceof Consequence );
        
        assertTrue( context.getRule().getNamedConsequence( "name2" ) instanceof CompiledInvoker );
        assertTrue( context.getRule().getNamedConsequence( "name2" ) instanceof Consequence );
        
        assertNotSame( context.getRule().getConsequence(), context.getRule().getNamedConsequence( "name1" ) );
        assertNotSame( context.getRule().getConsequence(), context.getRule().getNamedConsequence( "name2" ) );
        assertNotSame(  context.getRule().getNamedConsequence( "name1"), context.getRule().getNamedConsequence( "name2" ) );
    }

    private void assertEqualsIgnoreSpaces(String expected,
                                          String fixed) {
        assertEquals( expected.replaceAll( "\\s+",
                                           "" ),
                      fixed.replaceAll( "\\s+",
                                        "" ) );
    }

}
