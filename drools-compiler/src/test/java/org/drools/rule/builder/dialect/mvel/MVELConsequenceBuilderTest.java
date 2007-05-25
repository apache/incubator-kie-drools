package org.drools.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.base.ClassTypeResolver;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.common.AgendaItem;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.DialectRegistry;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Pattern;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.drools.spi.PatternExtractor;
import org.drools.spi.DeclarationScopeResolver;
import org.drools.spi.ObjectType;

public class MVELConsequenceBuilderTest extends TestCase {

    public void setUp() {
    }

    public void testSimpleExpression() throws Exception {
        final Package pkg = new Package( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.setConsequence( "cheese.setPrice( 5 );" );

        DialectRegistry registry = new DialectRegistry(); 
        registry.addDialect( "default",
                                  new JavaDialect( pkg,
                                                   new PackageBuilderConfiguration(),
                                                   new ClassTypeResolver(),
                                                   new ClassFieldExtractorCache() ) );           
        final InstrumentedBuildContent context = new InstrumentedBuildContent( pkg,
                                                                               ruleDescr,
                                                                               registry );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();

        final ObjectType cheeseObjeectType = new ClassObjectType( Cheese.class );

        final Pattern pattern = new Pattern( 0,
                                    cheeseObjeectType );

        final PatternExtractor extractor = new PatternExtractor( cheeseObjeectType );

        final Declaration declaration = new Declaration( "cheese",
                                                   extractor,
                                                   pattern );
        final Map map = new HashMap();
        map.put( "cheese",
                 declaration );
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        final MVELConsequenceBuilder builder = new MVELConsequenceBuilder();
        builder.build( context );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final WorkingMemory wm = ruleBase.newStatefulSession();

        final Cheese cheddar = new Cheese( "cheddar",
                                     10 );
        final InternalFactHandle f0 = (InternalFactHandle) wm.assertObject( cheddar );
        final ReteTuple tuple = new ReteTuple( f0 );

        final AgendaItem item = new AgendaItem( 0,
                                          tuple,
                                          null,
                                          context.getRule(),
                                          null );
        final DefaultKnowledgeHelper kbHelper = new DefaultKnowledgeHelper( item,
                                                                      wm );
        context.getRule().getConsequence().evaluate( kbHelper,
                                                     wm );

        assertEquals( 5,
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
    public void testLineSpanOptionalSemis() throws Exception {
        
        String simpleEx = "foo\nbar\nbaz";
        MVELConsequenceBuilder cons = new MVELConsequenceBuilder();
        assertEquals("foo;\nbar;\nbaz", cons.delimitExpressions(simpleEx));
        
        String ex = "foo (\n bar \n)\nbar;\nyeah;\nman\nbaby";
        assertEquals("foo (\n bar \n);\nbar;\nyeah;\nman;\nbaby", cons.delimitExpressions(ex));

        ex = "foo {\n bar \n}\nbar;   \nyeah;\nman\nbaby";
        assertEquals("foo {\n bar \n};\nbar;   \nyeah;\nman;\nbaby", cons.delimitExpressions(ex));

        ex = "foo [\n bar \n]\nbar;  x\nyeah();\nman[42]\nbaby;ca chiga;\nend";
        assertEquals("foo [\n bar \n];\nbar;  x;\nyeah();\nman[42];\nbaby;ca chiga;\nend", cons.delimitExpressions(ex));

        
    }
}
