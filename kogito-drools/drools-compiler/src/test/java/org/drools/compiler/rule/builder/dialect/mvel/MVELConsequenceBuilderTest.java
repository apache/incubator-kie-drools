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

package org.drools.compiler.rule.builder.dialect.mvel;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.drools.compiler.Cheese;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleBuilder;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DefaultKnowledgeHelper;
import org.drools.core.base.mvel.MVELConsequence;
import org.drools.core.base.mvel.MVELDebugHandler;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.AgendaItemImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.CompositeObjectSinkAdapterTest;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PatternExtractor;
import org.drools.core.spi.PropagationContext;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.debug.DebugTools;

import static org.junit.Assert.*;

public class MVELConsequenceBuilderTest {

    @Test
    public void testSimpleExpression() throws Exception {
        PackageDescr pkgDescr = new PackageDescr( "pkg1" );
        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl();
        pkgBuilder.addPackage( pkgDescr );

        InternalKnowledgePackage pkg = pkgBuilder.getPackageRegistry( "pkg1" ).getPackage();
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.setNamespace( "pkg1" );
        ruleDescr.setConsequence( "modify (cheese) {price = 5 };\nretract (cheese)" );

        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();

        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( "mvel" );

        final RuleBuildContext context = new RuleBuildContext( pkgBuilder,
                                                               ruleDescr,
                                                               dialectRegistry,
                                                               pkg,
                                                               mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();

        final ObjectType cheeseObjeectType = new ClassObjectType( Cheese.class );

        final Pattern pattern = new Pattern( 0,
                                             cheeseObjeectType,
                                             "cheese" );

        final GroupElement subrule = new GroupElement( GroupElement.AND );
        subrule.addChild( pattern );
        final Map<String, Declaration> map = new HashMap<String, Declaration>();
        map.put( "cheese",
                 pattern.getDeclaration() );
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        final MVELConsequenceBuilder builder = new MVELConsequenceBuilder();
        builder.build( context, RuleImpl.DEFAULT_CONSEQUENCE_NAME );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();

        PropagationContextFactory pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        kBase.addPackage(pkg);

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final Cheese cheddar = new Cheese( "cheddar", 10 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( cheddar );
        final LeftTupleImpl tuple = new LeftTupleImpl( f0, null, true );
        f0.removeLeftTuple(tuple);

        final AgendaItem item = new AgendaItemImpl( 0, tuple, 10,
                                                pctxFactory.createPropagationContext( 1, PropagationContext.Type.DELETION, null, tuple, null),
                                                new RuleTerminalNode(0, new CompositeObjectSinkAdapterTest.MockBetaNode(), context.getRule(), subrule, 0, new BuildContext( kBase )), null);
        final DefaultKnowledgeHelper kbHelper = new DefaultKnowledgeHelper( ksession );
        kbHelper.setActivation( item );
        ((MVELConsequence) context.getRule().getConsequence()).compile(  (MVELDialectRuntimeData) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ));
        context.getRule().getConsequence().evaluate( kbHelper,
                                                     ksession );

        assertEquals( 5,
                      cheddar.getPrice() );
    }

    @Test
    public void testImperativeCodeError() throws Exception {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.setConsequence( "if (cheese.price == 10) { cheese.price = 5; }" );

        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.default",
                                "mvel" );
        KnowledgeBuilderConfigurationImpl cfg1 = new KnowledgeBuilderConfigurationImpl( properties );

        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl( pkg, cfg1 );
        PackageRegistry pkgRegistry = pkgBuilder.getPackageRegistry( pkg.getName() );
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( pkgRegistry.getDialect() );

        final RuleBuildContext context = new RuleBuildContext( pkgBuilder,
                                                               ruleDescr,
                                                               dialectRegistry,
                                                               pkg,
                                                               mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();

        final ObjectType cheeseObjeectType = new ClassObjectType( Cheese.class );

        final Pattern pattern = new Pattern( 0,
                                             cheeseObjeectType );

        final PatternExtractor extractor = new PatternExtractor( cheeseObjeectType );

        final Declaration declaration = new Declaration( "cheese",
                                                         extractor,
                                                         pattern );
        final Map<String, Declaration> map = new HashMap<String, Declaration>();
        map.put( "cheese",
                 declaration );
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        final MVELConsequenceBuilder builder = new MVELConsequenceBuilder();
        builder.build( context, RuleImpl.DEFAULT_CONSEQUENCE_NAME );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( cheddar );
        final LeftTupleImpl tuple = new LeftTupleImpl( f0,
                                               null,
                                               true );

        final AgendaItem item = new AgendaItemImpl( 0,
                                                tuple,
                                                10,
                                                null, null, null);
        final DefaultKnowledgeHelper kbHelper = new DefaultKnowledgeHelper( ksession );
        kbHelper.setActivation( item );
        try {
            ((MVELConsequence) context.getRule().getConsequence()).compile( (MVELDialectRuntimeData) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ) );
            context.getRule().getConsequence().evaluate( kbHelper,
                                                         ksession );
            fail( "should throw an exception, as 'if' is not allowed" );
        } catch ( Exception e ) {
        }

        assertEquals( 10,
                      cheddar.getPrice() );
    }

    /**
     * Just like MVEL command line, we can allow expressions to span lines, with optional ";"
     * seperating expressions. If its needed a ";" can be thrown in, but if not, a new line is fine.
     *
     * However, when in the middle of unbalanced brackets, a new line means nothing.
     *
     * @throws Exception
     */
    @Test
    public void testLineSpanOptionalSemis() throws Exception {

        String simpleEx = "foo\nbar\nbaz";
        assertEquals( "foo;\nbar;\nbaz",
                      MVELConsequenceBuilder.delimitExpressions( simpleEx ) );

        String ex = "foo (\n bar \n)\nbar;\nyeah;\nman\nbaby";
        assertEquals( "foo ( bar );\n\n\nbar;\nyeah;\nman;\nbaby",
                      MVELConsequenceBuilder.delimitExpressions( ex ) );

        ex = "foo {\n bar \n}\nbar;   \nyeah;\nman\nbaby";
        assertEquals( "foo { bar };\n\n\nbar;   \nyeah;\nman;\nbaby",
                      MVELConsequenceBuilder.delimitExpressions( ex ) );

        ex = "foo [\n bar \n]\nbar;  x\nyeah();\nman[42]\nbaby;ca chiga;\nend";
        assertEquals( "foo [ bar ];\n\n\nbar;  x;\nyeah();\nman[42];\nbaby;ca chiga;\nend",
                      MVELConsequenceBuilder.delimitExpressions( ex ) );

        ex = "   \n\nfoo [\n bar \n]\n\n\nbar;  x\n  \nyeah();\nman[42]\nbaby;ca chiga;\nend";
        assertEquals( "   \n\nfoo [ bar ];\n\n\n\n\nbar;  x;\n  \nyeah();\nman[42];\nbaby;ca chiga;\nend",
                      MVELConsequenceBuilder.delimitExpressions( ex ) );

        ex = "   retract(f1) // some comment\n   retract(f2)\nend";
        assertEquals( "   retract(f1) ;// some comment\n   retract(f2);\nend",
                      MVELConsequenceBuilder.delimitExpressions( ex ) );

        ex = "   retract(f1 /* inline comment */) /* some\n comment\n*/   retract(f2)\nend";
        assertEquals( "   retract(f1 /* inline comment */) ;/* some\n comment\n*/   retract(f2);\nend",
                      MVELConsequenceBuilder.delimitExpressions( ex ) );

    }

    @Test
    public void testMVELDebugSymbols() throws DroolsParserException {

        MVELDebugHandler.setDebugMode( true );

        try {
            final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
            final PackageDescr pkgDescr = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "mvel_rule.drl" ) ) );

            // just checking there is no parsing errors
            assertFalse( parser.getErrors().toString(),
                                parser.hasErrors() );

            InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.drools" );

            final RuleDescr ruleDescr = pkgDescr.getRules().get( 0 );

            final KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl( pkg );
            DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
            Dialect dialect = dialectRegistry.getDialect( "mvel" );

            RuleBuildContext context = new RuleBuildContext( pkgBuilder,
                                                             ruleDescr,
                                                             dialectRegistry,
                                                             pkg,
                                                             dialect );

            RuleBuilder.build( context );

            assertTrue( context.getErrors().toString(),
                               context.getErrors().isEmpty() );

            final RuleImpl rule = context.getRule();

            MVELConsequence mvelCons = (MVELConsequence) rule.getConsequence();
            mvelCons.compile( (MVELDialectRuntimeData) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ) );
            String s = DebugTools.decompile( mvelCons.getCompExpr() );

            int fromIndex = 0;
            int count = 0;
            while ( (fromIndex = s.indexOf( "DEBUG_SYMBOL",
                                            fromIndex + 1 )) > -1 ) {
                count++;
            }
            assertEquals( 4,
                          count );
        } finally {
            MVELDebugHandler.setDebugMode( false );
        }

    }

    @Test
    public void testDebugSymbolCount() {
        String expr = "System.out.println( \"a1\" );\n" + "System.out.println( \"a2\" );\n" + "System.out.println( \"a3\" );\n" + "System.out.println( \"a4\" );\n";

        ParserContext context = new ParserContext();
        context.setDebugSymbols( true );
        context.addImport( "System",
                           System.class );
        context.setStrictTypeEnforcement( true );
        //context.setDebugSymbols( true );
        context.setSourceFile( "mysource" );

        ExpressionCompiler compiler = new ExpressionCompiler( expr, context );
        Serializable compiledExpression = compiler.compile();

        String s = DebugTools.decompile( compiledExpression );

        System.out.println( "s " + s );

        int fromIndex = 0;
        int count = 0;
        while ( (fromIndex = s.indexOf( "DEBUG_SYMBOL",
                                        fromIndex + 1 )) > -1 ) {
            count++;
        }
        assertEquals( 4,
                      count );

    }
    
    private RuleBuildContext        context;
    private RuleDescr               ruleDescr;
    private MVELConsequenceBuilder  builder;
    
    private void setupTest(String consequence, Map<String, Object> namedConsequences) {
        builder = new MVELConsequenceBuilder();

        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.drools.compiler.test" );
        pkg.addImport( new ImportDeclaration( Cheese.class.getCanonicalName() ) );

        KnowledgeBuilderConfigurationImpl conf = new KnowledgeBuilderConfigurationImpl();
        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl( pkg, conf );

        ruleDescr = new RuleDescr( "test consequence builder" );
        ruleDescr.setConsequence( consequence );
        ruleDescr.addAttribute( new AttributeDescr("dialect", "mvel") );
        
        for ( Entry<String, Object> entry : namedConsequences.entrySet() ) {
            ruleDescr.addNamedConsequences( entry.getKey(), entry.getValue() );
        }

        RuleImpl rule = new RuleImpl( ruleDescr.getName() );
        rule.addPattern( new Pattern( 0,
                                      new ClassObjectType( Cheese.class ),
                                      "$cheese" ) );
        
        rule.addPattern( new Pattern( 0,
                                      new ClassObjectType( Map.class ),
                                      "$map" ) );        

        PackageRegistry pkgRegistry = pkgBuilder.getPackageRegistry( pkg.getName() );
        DialectCompiletimeRegistry reg = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        context = new RuleBuildContext( pkgBuilder,
                                        ruleDescr,
                                        reg,
                                        pkg,
                                        reg.getDialect( pkgRegistry.getDialect() ) );
        context.getDeclarationResolver().pushOnBuildStack( rule.getLhs() );
        
        context.getDialect().getConsequenceBuilder().build( context, RuleImpl.DEFAULT_CONSEQUENCE_NAME );
        for ( String name : namedConsequences.keySet() ) {
            context.getDialect().getConsequenceBuilder().build( context, name );
        }
        
        context.getDialect().addRule( context );
        pkgRegistry.getPackage().addRule( context.getRule() );
        pkgBuilder.compileAll();
        pkgBuilder.reloadAll();
        if ( pkgBuilder.hasErrors() ) {
            fail(pkgBuilder.getErrors().toString());
        }
    }
    

    @Test
    public void testDefaultConsequenceCompilation() {
        String consequence = " System.out.println(\"this is a test:\" + $cheese);\n " +
        		"c1 = new Cheese().{ type = $cheese.type };" +
        		"c2 = new Cheese().{ type = $map[$cheese.type] };" +
        		"c3 = new Cheese().{ type = $map['key'] };";
        setupTest( consequence, new HashMap<String, Object>() );
         assertNotNull( context.getRule().getConsequence() );
        assertFalse( context.getRule().hasNamedConsequences() );
        assertTrue( context.getRule().getConsequence() instanceof MVELConsequence );
    }
    
    @Test
    public void testDefaultConsequenceWithSingleNamedConsequenceCompilation() {
        String defaultCon = " System.out.println(\"this is a test\" + $cheese);\n ";
        
        Map<String, Object> namedConsequences = new HashMap<String, Object>();
        String name1 =  " System.out.println(\"this is a test name1\" + $cheese);\n ";
        namedConsequences.put( "name1", name1 );
        
        setupTest( defaultCon, namedConsequences);
        assertEquals( 1, context.getRule().getNamedConsequences().size() );
        
        assertTrue( context.getRule().getConsequence() instanceof MVELConsequence );
        
        assertTrue( context.getRule().getNamedConsequences().get( "name1" ) instanceof MVELConsequence );
        
        assertNotSame( context.getRule().getConsequence(), context.getRule().getNamedConsequences().get( "name1" ) );
    }
    
    @Test
    public void testDefaultConsequenceWithMultipleNamedConsequenceCompilation() {
        String defaultCon = " System.out.println(\"this is a test\" + $cheese);\n ";
        
        Map<String, Object> namedConsequences = new HashMap<String, Object>();
        String name1 =  " System.out.println(\"this is a test name1\" + $cheese);\n ";
        namedConsequences.put( "name1", name1 );
        String name2 =  " System.out.println(\"this is a test name2\" + $cheese);\n ";
        namedConsequences.put( "name2", name2 );
        
        setupTest( defaultCon, namedConsequences);
        assertEquals( 2, context.getRule().getNamedConsequences().size() );
        
        assertTrue( context.getRule().getConsequence() instanceof MVELConsequence );
        
        assertTrue( context.getRule().getNamedConsequences().get( "name1" ) instanceof MVELConsequence );
        
        assertTrue( context.getRule().getNamedConsequences().get( "name2" ) instanceof MVELConsequence );
        
        assertNotSame( context.getRule().getConsequence(), context.getRule().getNamedConsequences().get( "name1" ) );
        assertNotSame( context.getRule().getConsequence(), context.getRule().getNamedConsequences().get( "name2" ) );
        assertNotSame(  context.getRule().getNamedConsequences().get( "name1"), context.getRule().getNamedConsequences().get( "name2" ) );
    }
}
