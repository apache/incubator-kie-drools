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

package org.drools.compiler.rule.builder.dialect.java;

import org.antlr.runtime.RecognitionException;
import org.drools.compiler.Cheese;
import org.drools.compiler.Person;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaBlockDescr;
import org.drools.core.base.ClassObjectType;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.PropertySpecificUtil;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.bitmask.LongBitMask;
import org.junit.Test;
import org.kie.internal.builder.conf.PropertySpecificOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.drools.compiler.rule.builder.dialect.DialectUtil.fixBlockDescr;
import static org.drools.compiler.rule.builder.dialect.DialectUtil.setContainerBlockInputs;
import static org.junit.Assert.*;

public class JavaConsequenceBuilderPRAlwaysTest {

    private RuleBuildContext       context;
    private RuleDescr              ruleDescr;

    private void setupTest(String consequence, Map<String, Object> namedConsequences) {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.drools" );
        pkg.addImport( new ImportDeclaration( "org.drools.compiler.Cheese" ) );

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
    public void testFixThrows() {
        String consequence =
            " modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " + 
            " throw new java.lang.RuntimeException(\"xxx\");\n " +
            " Cheese c1 = $cheese;\n" +
            " modify( c1 ) { setPrice( 10 ), setOldPrice( age ) }\n ";
        setupTest( "", new HashMap<String, Object>() );
        try {
            ruleDescr.setConsequence( consequence );
            JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
            Map<String, Class<?>> declrCls = new HashMap<String, Class<?>>();
            declrCls.put( "$cheese", Cheese.class );
            
            JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                      new BoundIdentifiers(declrCls, null ) );
            
            BoundIdentifiers bindings = new BoundIdentifiers( new HashMap(), null );
            bindings.getDeclrClasses().put( "$cheese", Cheese.class );
            bindings.getDeclrClasses().put( "age", int.class );
            
            // Set the inputs for each container, this is needed for modifes when the target context is the result of an expression
            List<JavaBlockDescr> descrs = new ArrayList<JavaBlockDescr>();
            setContainerBlockInputs(context,
                                            descrs,
                                            analysis.getBlockDescrs(), 
                                            consequence,
                                            bindings,
                                            new HashMap(),
                                            0);

            context.getKnowledgeBuilder().getTypeDeclaration(Cheese.class).setPropertyReactive(true);
            String fixed = fixBlockDescr(context, analysis, context.getDeclarationResolver().getDeclarations( context.getRule() ) );

            String expected = 
                    " { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, new org.drools.core.util.bitmask.LongBitMask(12L), org.drools.compiler.Cheese.class ); }\r\n" +
            		"  throw new java.lang.RuntimeException(\"xxx\");\r\n" + 
            		"  Cheese c1 = $cheese;\r\n" + 
            		" { org.drools.compiler.Cheese __obj__ = ( c1 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, new org.drools.core.util.bitmask.LongBitMask(12L), org.drools.compiler.Cheese.class ); }\r\n" +
            		" \r\n" + 
            		"";

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
    public void testFixModifyBlocks() throws Exception {
        String consequence = 
            " System.out.println(\"this is a test\");\n " +
            " Cheese c1 = $cheese;\n" +            
            " try { \r\n" +
            "     modify( c1 ) { setPrice( 10 ), \n" +
            "                    setOldPrice( age ) }\n " +
            "     Cheese c4 = $cheese;\n" +            
            "     try { \n" +
            "         modify( c4 ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "     } catch (java.lang.Exception e) {\n" +            
            "         modify( c1 ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "     } finally {\n " +
            "         Cheese c3 = $cheese;\n" +            
            "         modify( c3 ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "    }\n" +            
            " } catch (java.lang.Exception e) {\n" +
            "     Cheese c2 = $cheese;\n" +            
            "     modify( c2 ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            " } finally {\n " +
            "     Cheese c3 = $cheese;\n" +            
            "     modify( c3 ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "}\n" +
            " modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " + 
            " System.out.println(\"we are done\");\n ";
        setupTest( "", new HashMap<String, Object>() );

        ruleDescr.setConsequence( consequence );
        JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
        Map<String, Class<?>> declrCls = new HashMap<String, Class<?>>();
        declrCls.put( "$cheese", Cheese.class );
        
        JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                  new BoundIdentifiers(declrCls, null ) );
        
        BoundIdentifiers bindings = new BoundIdentifiers( new HashMap(), null );
        bindings.getDeclrClasses().put( "$cheese", Cheese.class );
        bindings.getDeclrClasses().put( "age", int.class );
        
        // Set the inputs for each container, this is needed for modifes when the target context is the result of an expression
        List<JavaBlockDescr> descrs = new ArrayList<JavaBlockDescr>();
        setContainerBlockInputs(context,
                                        descrs,
                                        analysis.getBlockDescrs(), 
                                        consequence,
                                        bindings,
                                        new HashMap(),
                                        0);

        analysis.setBoundIdentifiers(bindings);

        context.getKnowledgeBuilder().getTypeDeclaration(Cheese.class).setPropertyReactive(true);
        String fixed = fixBlockDescr( context, analysis, context.getDeclarationResolver().getDeclarations(context.getRule()), descrs );

        String expected = 
                " System.out.println(\"this is a test\");\r\n" + 
                "  Cheese c1 = $cheese;\r\n" + 
                " try { \r\n" + 
                "     { org.drools.compiler.Cheese __obj__ = ( c1 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); \r\n" +
                "__obj__.setOldPrice( age ); drools.update( __obj____Handle2__, new org.drools.core.util.bitmask.LongBitMask(12L), org.drools.compiler.Cheese.class ); }\r\n" +
                "      Cheese c4 = $cheese;\r\n" + 
                "     try { \r\n" + 
                "         { org.drools.compiler.Cheese __obj__ = ( c4 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, new org.drools.core.util.bitmask.LongBitMask(12L), org.drools.compiler.Cheese.class ); }\r\n" +
                "      } catch (java.lang.Exception e) {\r\n" + 
                "         { org.drools.compiler.Cheese __obj__ = ( c1 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, new org.drools.core.util.bitmask.LongBitMask(12L), org.drools.compiler.Cheese.class ); }\r\n" +
                "      } finally {\r\n" + 
                "          Cheese c3 = $cheese;\r\n" + 
                "         { org.drools.compiler.Cheese __obj__ = ( c3 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, new org.drools.core.util.bitmask.LongBitMask(12L), org.drools.compiler.Cheese.class ); }\r\n" +
                "     }\r\n" + 
                " } catch (java.lang.Exception e) {\r\n" + 
                "     Cheese c2 = $cheese;\r\n" + 
                "     { org.drools.compiler.Cheese __obj__ = ( c2 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, new org.drools.core.util.bitmask.LongBitMask(12L), org.drools.compiler.Cheese.class ); }\r\n" +
                "  } finally {\r\n" + 
                "      Cheese c3 = $cheese;\r\n" + 
                "     { org.drools.compiler.Cheese __obj__ = ( c3 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, new org.drools.core.util.bitmask.LongBitMask(12L), org.drools.compiler.Cheese.class ); }\r\n" +
                " }\r\n" + 
                " { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, new org.drools.core.util.bitmask.LongBitMask(12L), org.drools.compiler.Cheese.class ); }\r\n" +
                "  System.out.println(\"we are done\");\r\n" + 
                " \r\n" + 
                "";

        assertNotNull( context.getErrors().toString(),
                       fixed );
        assertEqualsIgnoreSpaces( expected,
                                  fixed );
        //            System.out.println( "=============================" );
        //            System.out.println( ruleDescr.getConsequence() );
        //            System.out.println( "=============================" );
        //            System.out.println( fixed );        

    }
    
    @Test
    public void testIfElseBlocks() throws Exception {
        String consequence = 
            " System.out.println(\"this is a test\");\n " +
            " Cheese c1 = $cheese;\n" +            
            " if( c1 == $cheese )     { \r\n" +
            "     modify( c1 ) { setPrice( 10 ), \n" +
            "                    setOldPrice( age ) }\n " +
            "     Cheese c4 = $cheese;\n" +            
            "     if ( true )     { \n" +
            "         modify( c4 ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "     } else if (1==2) {\n" +            
            "         modify( c1 ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "     } else {\n " +
            "         Cheese c3 = $cheese;\n" +            
            "         modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "    }\n" +            
            " } else {\n " +
            "     Cheese c3 = $cheese;\n" +            
            "     modify( c3 ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "     if ( c4 ==  $cheese ) modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "     else modify( $cheese ) { setPrice( 12 ) }\n " +                 
            "}\n" +
            " modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " + 
            " System.out.println(\"we are done\");\n ";
        setupTest( "", new HashMap<String, Object>() );

        ruleDescr.setConsequence( consequence );
        JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
        Map<String, Class<?>> declrCls = new HashMap<String, Class<?>>();
        declrCls.put( "$cheese", Cheese.class );
        
        JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                  new BoundIdentifiers(declrCls, null ) );
        
        BoundIdentifiers bindings = new BoundIdentifiers( new HashMap(), null );
        bindings.getDeclrClasses().put( "$cheese", Cheese.class );
        bindings.getDeclrClasses().put( "age", int.class );
        
        // Set the inputs for each container, this is needed for modifes when the target context is the result of an expression
        List<JavaBlockDescr> descrs = new ArrayList<JavaBlockDescr>();
        setContainerBlockInputs(context,
                                        descrs,
                                        analysis.getBlockDescrs(), 
                                        consequence,
                                        bindings,
                                        new HashMap(),
                                        0);            
        
        String fixed = fixBlockDescr( context, analysis, context.getDeclarationResolver().getDeclarations( context.getRule() ) );

        List<String> cheeseAccessibleProperties = ClassUtils.getAccessibleProperties(Cheese.class);
        BitMask priceOldPrice = PropertySpecificUtil.calculatePositiveMask(Arrays.asList("price", "oldPrice"), cheeseAccessibleProperties);
        BitMask price = PropertySpecificUtil.calculatePositiveMask(Arrays.asList("price"), cheeseAccessibleProperties);
        
        String expected = 
                "  System.out.println(\"this is a test\");\r\n" + 
                "  Cheese c1 = $cheese;\r\n" + 
                " if( c1 == $cheese )     { \r\n" + 
                "     { org.drools.compiler.Cheese __obj__ = ( c1 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); \r\n" +
                "__obj__.setOldPrice( age ); drools.update( __obj____Handle2__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "      Cheese c4 = $cheese;\r\n" + 
                "     if ( true )     { \r\n" + 
                "         { org.drools.compiler.Cheese __obj__ = ( c4 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "      } else if (1==2) {\r\n" + 
                "         { org.drools.compiler.Cheese __obj__ = ( c1 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "      } else {\r\n" + 
                "          Cheese c3 = $cheese;\r\n" + 
                "         { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "     }\r\n" + 
                " } else {\r\n" + 
                "      Cheese c3 = $cheese;\r\n" + 
                "     { org.drools.compiler.Cheese __obj__ = ( c3 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "      if ( c4 ==  $cheese ) { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                // the following line please notice only price is actually modified.
                "      else { $cheese.setPrice( 12 ); drools.update( $cheese__Handle__, "+price.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                " }\r\n" + 
                " { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "  System.out.println(\"we are done\");\r\n" + 
                " \r\n";

        assertNotNull( context.getErrors().toString(),
                       fixed );
        assertEqualsIgnoreSpaces( expected,
                                  fixed );
        
        //            System.out.println( "=============================" );
        //            System.out.println( ruleDescr.getConsequence() );
        //            System.out.println( "=============================" );
        //            System.out.println( fixed );        

    }   
    
    @Test
    public void testWhileBlocks() throws Exception {
        String consequence = 
            " System.out.println(\"this is a test\");\n " +
            " Cheese c1 = $cheese;\n" +            
            " while ( c1 == $cheese )     { \r\n" +
            "     modify( c1 ) { setPrice( 10 ), \n" +
            "                    setOldPrice( age ) }\n " +
            "     Cheese c4 = $cheese;\n" +            
            "     while ( true )     { \n" +
            "         modify( c4 ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "     }"+          
            " } \n " +
            " Cheese c3 = $cheese;\n" +            
            " while ( c4 ==  $cheese ) modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " +             
            " modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " + 
            " System.out.println(\"we are done\");\n " +            
            " while (true) { System.out.println(1);}\n" +
            " modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            " System.out.println(\"we are done\");\n ";         
        setupTest( "", new HashMap<String, Object>() );

        ruleDescr.setConsequence( consequence );
        JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
        Map<String, Class<?>> declrCls = new HashMap<String, Class<?>>();
        declrCls.put( "$cheese", Cheese.class );
        
        JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                  new BoundIdentifiers(declrCls, null ) );
        
        BoundIdentifiers bindings = new BoundIdentifiers( new HashMap(), null );
        bindings.getDeclrClasses().put( "$cheese", Cheese.class );
        bindings.getDeclrClasses().put( "age", int.class );
        
        // Set the inputs for each container, this is needed for modifes when the target context is the result of an expression
        List<JavaBlockDescr> descrs = new ArrayList<JavaBlockDescr>();
        setContainerBlockInputs(context,
                                        descrs,
                                        analysis.getBlockDescrs(), 
                                        consequence,
                                        bindings,
                                        new HashMap(),
                                        0);            
        
        String fixed = fixBlockDescr( context, analysis, context.getDeclarationResolver().getDeclarations( context.getRule() ) );
        
        List<String> cheeseAccessibleProperties = ClassUtils.getAccessibleProperties(Cheese.class);
        BitMask priceOldPrice = PropertySpecificUtil.calculatePositiveMask(Arrays.asList("price", "oldPrice"), cheeseAccessibleProperties);

        String expected = 
                " System.out.println(\"this is a test\");\r\n" + 
                "  Cheese c1 = $cheese;\r\n" + 
                " while ( c1 == $cheese )     { \r\n" + 
                "     { org.drools.compiler.Cheese __obj__ = ( c1 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); \r\n" +
                "__obj__.setOldPrice( age ); drools.update( __obj____Handle2__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "      Cheese c4 = $cheese;\r\n" + 
                "     while ( true )     { \r\n" + 
                "         { org.drools.compiler.Cheese __obj__ = ( c4 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); __obj__.setOldPrice( age ); drools.update( __obj____Handle2__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "      } } \r\n" + 
                "  Cheese c3 = $cheese;\r\n" + 
                " while ( c4 ==  $cheese ) { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "  { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "  System.out.println(\"we are done\");\r\n" + 
                "  while (true) { System.out.println(1);}\r\n" + 
                " { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "  System.out.println(\"we are done\");\r\n" + 
                " \r\n" + 
                "";

        assertNotNull( context.getErrors().toString(),
                       fixed );
        assertEqualsIgnoreSpaces( expected,
                                  fixed );
    }     

    @Test
    public void testForBlocks() throws Exception {
        String consequence = 
            " System.out.println(\"this is a test\");\n " +         
            "int i = 0;\n" +
            " for ( Cheese c1 = $cheese; i < 10;i++ )     { \r\n" +
            "     modify( c1 ) { setPrice( 10 ), \n" +
            "                    setOldPrice( age ) }\n " +
            "     Cheese c4 = $cheese;\n" +            
            "     for ( Cheese item : new ArrayList<Cheese>() ) {" +
            "         modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            "      }\n" +
            " } \n " +        
            " for ( ; ; ) modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " + // <<< TODO in the original test here was a cast to (Cheese) $cheese,  but this ad-hoc test is missing the kBuilder to properly populate the CompositePackageDescr which contains the import declaration. 
                                                                                         //          the more correct way would be to change completely the ad-hoc build logic of this test contained in setupTest() and intercept the DialectUtils transformation on a *real* compilation process. 
            " for ( Cheese item : new ArrayList<Cheese>() ) modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " +                         
            " modify( $cheese ) { setPrice( 10 ), setOldPrice( age ) }\n " +
            " System.out.println(\"we are done\");\n ";         
        setupTest( "", new HashMap<String, Object>() );

        ruleDescr.setConsequence( consequence );

        JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
        Map<String, Class<?>> declrCls = new HashMap<String, Class<?>>();
        declrCls.put( "$cheese", Cheese.class );
        
        JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
                                                                                  new BoundIdentifiers(declrCls, null ) );
        
        BoundIdentifiers bindings = new BoundIdentifiers( new HashMap(), null );
        bindings.getDeclrClasses().put( "$cheese", Cheese.class );
        bindings.getDeclrClasses().put( "age", int.class );
        
        // Set the inputs for each container, this is needed for modifes when the target context is the result of an expression
        List<JavaBlockDescr> descrs = new ArrayList<JavaBlockDescr>();
        setContainerBlockInputs(context,
                                        descrs,
                                        analysis.getBlockDescrs(), 
                                        consequence,
                                        bindings,
                                        new HashMap(),
                                        0);            
        
        String fixed = fixBlockDescr( context, analysis, context.getDeclarationResolver().getDeclarations( context.getRule() ) );
        
        List<String> cheeseAccessibleProperties = ClassUtils.getAccessibleProperties(Cheese.class);
        BitMask priceOldPrice = PropertySpecificUtil.calculatePositiveMask(Arrays.asList("price", "oldPrice"), cheeseAccessibleProperties);
        
        String expected = 
                " System.out.println(\"this is a test\");\r\n" + 
                " int i = 0;\r\n" + 
                " for ( Cheese c1 = $cheese; i < 10;i++ )     { \r\n" + 
                "     { org.drools.compiler.Cheese __obj__ = ( c1 ); org.kie.api.runtime.rule.FactHandle __obj____Handle2__ = drools.getFactHandle(__obj__);__obj__.setPrice( 10 ); \r\n" +
                "__obj__.setOldPrice( age ); drools.update( __obj____Handle2__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "      Cheese c4 = $cheese;\r\n" + 
                "     for ( Cheese item : new ArrayList<Cheese>() ) {         { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "       }\r\n" + 
                " } \r\n" + 
                "  for ( ; ; ) { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "  for ( Cheese item : new ArrayList<Cheese>() ) { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "  { $cheese.setPrice( 10 ); $cheese.setOldPrice( age ); drools.update( $cheese__Handle__, "+priceOldPrice.getInstancingStatement()+", org.drools.compiler.Cheese.class ); }\r\n" +
                "  System.out.println(\"we are done\");\r\n" + 
                " \r\n" + 
                "";
        
        System.out.println(expected);
        System.out.println(fixed);

        assertNotNull( context.getErrors().toString(),
                       fixed );
        assertEqualsIgnoreSpaces( expected,
                                  fixed );
    }      
    
//    @Test
//    public void testFixInsertCalls() {
//        String consequence = " System.out.println(\"this is a test\");\n " + 
//                             " insert( $cheese );\n " + 
//                             " if( true ) { \n " +
//                             "     insert($another); \n" +
//                             " } else { \n"+
//                             "     retract($oneMore); \n" +
//                             " } \n" +
//                             " // just in case, one more call: \n" +
//                             " insert( $abc );\n"
//                             ;
//        setupTest( consequence, new HashMap<String, Object>() );
//        try {
//            JavaExprAnalyzer analyzer = new JavaExprAnalyzer();
//            JavaAnalysisResult analysis = (JavaAnalysisResult) analyzer.analyzeBlock( (String) ruleDescr.getConsequence(),
//                                                                                      new Map[]{} );
//
//            String fixed = builder.fixBlockDescr( context,
//                                                  analysis,
//                                                  (String) ruleDescr.getConsequence() );
//            fixed = new KnowledgeHelperFixer().fix( fixed );
//
//            String expected = " System.out.println(\"this is a test\");\n " + 
//                              " drools.insert( $cheese );\n " + 
//                              " if( true ) { \n " +
//                              "     drools.insert($another); \n" +
//                              " } else { \n"+
//                              "     drools.retract($oneMore); \n" +
//                              " } \n" +
//                              " // just in case, one more call: \n" +
//                              " drools.insert( $abc );\n"
//            ;
//
////                        System.out.println( "=============================" );
////                        System.out.println( ruleDescr.getConsequence() );
////                        System.out.println( "=============================" );
////                        System.out.println( fixed );
//            assertNotNull( context.getErrors().toString(),
//                           fixed );
//            assertEqualsIgnoreSpaces( expected,
//                                      fixed );
//
//        } catch ( RecognitionException e ) {
//            e.printStackTrace();
//        }
//
//    }
    
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
