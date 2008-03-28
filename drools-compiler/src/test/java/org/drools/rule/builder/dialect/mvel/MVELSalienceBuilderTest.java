package org.drools.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.builder.SalienceBuilder;
import org.drools.spi.ObjectType;
import org.drools.spi.PatternExtractor;

public class MVELSalienceBuilderTest extends TestCase {
    public void testSimpleExpression() {        
        final Package pkg = new Package( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.setSalience( "(p.age + 20)/2" );
        ruleDescr.setConsequence( "" );

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
        MVELDialect mvelDialect = ( MVELDialect ) pkgBuilder.getDialectRegistry().getDialect( "mvel" );

        final InstrumentedBuildContent context = new InstrumentedBuildContent( conf,
                                                                               pkg,
                                                                               ruleDescr,
                                                                               pkgBuilder.getDialectRegistry(),
                                                                               mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();

        final ObjectType personObjeectType = new ClassObjectType( Person.class );

        final Pattern pattern = new Pattern( 0,
                                             personObjeectType );

        final PatternExtractor extractor = new PatternExtractor( personObjeectType );

        final Declaration declaration = new Declaration( "p",
                                                         extractor,
                                                         pattern );
        final Map map = new HashMap();
        map.put( "p",
                 declaration );
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final WorkingMemory wm = ruleBase.newStatefulSession();

        final Person p = new Person("mark", "", 31);
        final InternalFactHandle f0 = (InternalFactHandle) wm.insert( p );
        final LeftTuple tuple = new LeftTuple( f0, null );

        SalienceBuilder salienceBuilder = new MVELSalienceBuilder();
        salienceBuilder.build( context );
                
        assertEquals( 25, context.getRule().getSalience().getValue( tuple, wm ) );
      
    }
}
