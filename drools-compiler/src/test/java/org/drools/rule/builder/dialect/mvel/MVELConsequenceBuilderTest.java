package org.drools.rule.builder.dialect.mvel;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StringConcrete;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.mvel.MVELConsequence;
import org.drools.base.mvel.MVELDebugHandler;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.conf.LanguageLevelOption;
import org.drools.common.AgendaItem;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.PropagationContextImpl;
import org.drools.compiler.Dialect;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageRegistry;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.CompositeObjectSinkAdapterTest;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.reteoo.MockLeftTupleSink;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleBuilder;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.spi.ObjectType;
import org.drools.spi.PatternExtractor;
import org.junit.Test;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.debug.DebugTools;


import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MVELConsequenceBuilderTest {

    @Test
    public void testSimpleExpression() throws Exception {
        PackageDescr pkgDescr = new PackageDescr( "pkg1" );
        PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage( pkgDescr );

        final Package pkg = pkgBuilder.getPackageRegistry( "pkg1" ).getPackage();
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.setNamespace( "pkg1" );
        ruleDescr.setConsequence( "modify (cheese) {price = 5 };\nretract (cheese)" );

        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();

        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( "mvel" );

        final InstrumentedBuildContent context = new InstrumentedBuildContent( pkgBuilder,
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
        builder.build( context, Rule.DEFAULT_CONSEQUENCE_NAME );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        MockLeftTupleSink sink = new MockLeftTupleSink();
        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle f0 = (InternalFactHandle) wm.insert( cheddar );
        final LeftTupleImpl tuple = new LeftTupleImpl( f0,
                                               sink,
                                               true );
        

        final AgendaItem item = new AgendaItem( 0,
                                                tuple,
                                                10,
                                                new PropagationContextImpl( 1,
                                                                            1,
                                                                            null,
                                                                            tuple,
                                                                            null ),
                                                new RuleTerminalNode(0, new CompositeObjectSinkAdapterTest.MockBetaNode(), context.getRule(), subrule, 0, new BuildContext( (InternalRuleBase) ruleBase, null ))  );
        final DefaultKnowledgeHelper kbHelper = new DefaultKnowledgeHelper( wm );
        kbHelper.setActivation( item );
        ((MVELConsequence) context.getRule().getConsequence()).compile(  (MVELDialectRuntimeData) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ));
        context.getRule().getConsequence().evaluate( kbHelper,
                                                     wm );

        assertEquals( 5,
                      cheddar.getPrice() );
    }

    @Test
    public void testImperativeCodeError() throws Exception {
        final Package pkg = new Package( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.setConsequence( "if (cheese.price == 10) { cheese.price = 5; }" );

        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.default",
                                "mvel" );
        PackageBuilderConfiguration cfg1 = new PackageBuilderConfiguration( properties );

        PackageBuilder pkgBuilder = new PackageBuilder( pkg,
                                                        cfg1 );
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
        PackageRegistry pkgRegistry = pkgBuilder.getPackageRegistry( pkg.getName() );
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( pkgRegistry.getDialect() );

        final InstrumentedBuildContent context = new InstrumentedBuildContent( pkgBuilder,
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
        builder.build( context, Rule.DEFAULT_CONSEQUENCE_NAME );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final WorkingMemory wm = ruleBase.newStatefulSession();

        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle f0 = (InternalFactHandle) wm.insert( cheddar );
        final LeftTupleImpl tuple = new LeftTupleImpl( f0,
                                               null,
                                               true );

        final AgendaItem item = new AgendaItem( 0,
                                                tuple,
                                                10,
                                                null,
                                                null );
        final DefaultKnowledgeHelper kbHelper = new DefaultKnowledgeHelper( wm );
        kbHelper.setActivation( item );
        try {
            ((MVELConsequence) context.getRule().getConsequence()).compile( (MVELDialectRuntimeData) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ) );
            context.getRule().getConsequence().evaluate( kbHelper,
                                                         wm );
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
        MVELConsequenceBuilder cons = new MVELConsequenceBuilder();
        assertEquals( "foo;\nbar;\nbaz",
                      MVELConsequenceBuilder.delimitExpressions( simpleEx ) );

        String ex = "foo (\n bar \n)\nbar;\nyeah;\nman\nbaby";
        assertEquals( "foo (\n bar \n);\nbar;\nyeah;\nman;\nbaby",
                      MVELConsequenceBuilder.delimitExpressions( ex ) );

        ex = "foo {\n bar \n}\nbar;   \nyeah;\nman\nbaby";
        assertEquals( "foo {\n bar \n};\nbar;   \nyeah;\nman;\nbaby",
                      MVELConsequenceBuilder.delimitExpressions( ex ) );

        ex = "foo [\n bar \n]\nbar;  x\nyeah();\nman[42]\nbaby;ca chiga;\nend";
        assertEquals( "foo [\n bar \n];\nbar;  x;\nyeah();\nman[42];\nbaby;ca chiga;\nend",
                      MVELConsequenceBuilder.delimitExpressions( ex ) );

        ex = "   \n\nfoo [\n bar \n]\n\n\nbar;  x\n  \nyeah();\nman[42]\nbaby;ca chiga;\nend";
        assertEquals( "   \n\nfoo [\n bar \n];\n\n\nbar;  x;\n  \nyeah();\nman[42];\nbaby;ca chiga;\nend",
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

            final Package pkg = new Package( "org.drools" );

            final RuleDescr ruleDescr = pkgDescr.getRules().get( 0 );

            final RuleBuilder builder = new RuleBuilder();

            final PackageBuilder pkgBuilder = new PackageBuilder( pkg );
            final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
            DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
            Dialect dialect = dialectRegistry.getDialect( "mvel" );

            RuleBuildContext context = new RuleBuildContext( pkgBuilder,
                                                             ruleDescr,
                                                             dialectRegistry,
                                                             pkg,
                                                             dialect );

            builder.build( context );

            assertTrue( context.getErrors().toString(),
                               context.getErrors().isEmpty() );

            final Rule rule = context.getRule();

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

        ExpressionCompiler compiler = new ExpressionCompiler( expr );

        ParserContext context = new ParserContext();
        context.setDebugSymbols( true );
        context.addImport( "System",
                           System.class );
        context.setStrictTypeEnforcement( true );
        //context.setDebugSymbols( true );
        context.setSourceFile( "mysource" );


        Serializable compiledExpression = compiler.compile( context );

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

        Package pkg = new Package( "org.drools" );
        pkg.addImport( new ImportDeclaration( "org.drools.Cheese" ) );

        PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
        PackageBuilder pkgBuilder = new PackageBuilder( pkg,
                                                        conf );

        ruleDescr = new RuleDescr( "test consequence builder" );
        ruleDescr.setConsequence( consequence );
        ruleDescr.addAttribute( new AttributeDescr("dialect", "mvel") );
        
        for ( Entry<String, Object> entry : namedConsequences.entrySet() ) {
            ruleDescr.addNamedConsequences( entry.getKey(), entry.getValue() );
        }

        Rule rule = new Rule( ruleDescr.getName() );
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
        context.getBuildStack().push( rule.getLhs() );
        
        context.getDialect().getConsequenceBuilder().build( context, Rule.DEFAULT_CONSEQUENCE_NAME );
        for ( String name : namedConsequences.keySet() ) {
            context.getDialect().getConsequenceBuilder().build( context, name );
        }
        
        context.getDialect().addRule( context );
        pkgRegistry.getPackage().addRule( context.getRule() );
        pkgBuilder.compileAll();
        pkgBuilder.reloadAll();
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
    
    
	@Test
	public void testConcreteClassWithGenericMember() {
		

		String javaDrl = 	"package foo \n" +
							"import org.drools.StringConcrete \n" +
							"rule java \n" + 
							"when \n" + 
							"$S : StringConcrete()  \n" +
							"then \n" +
							"System.out.println( $S.getFoo().concat(\"this works with java dialect\") ); \n" +
							"end \n";

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		kbuilder.add( ResourceFactory.newReaderResource( new StringReader( javaDrl ) ), ResourceType.DRL );

		assertFalse( kbuilder.hasErrors() );
		
		String mvelDrl = 	"package foo \n" +
							"import org.drools.StringConcrete \n" +
							"rule mvel \n" + 
							"dialect \"mvel\" \n" +
							"when \n" + 
							"$S : StringConcrete()  \n" +
							"then \n" +
							"System.out.println( $S.getFoo().concat(\"but not in mvel\") ); \n" +
							"end \n";

		kbuilder.add( ResourceFactory.newReaderResource( new StringReader( mvelDrl ) ), ResourceType.DRL );

		System.err.println( kbuilder.getErrors() );
		assertFalse( kbuilder.hasErrors() );
	}
}
