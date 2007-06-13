package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.base.ClassTypeResolver;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELSalienceExpression;
import org.drools.common.AgendaItem;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.DialectRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.builder.SalienceBuilder;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.drools.spi.ObjectType;
import org.drools.spi.PatternExtractor;
import org.mvel.MVEL;

public class MVELSalienceBuilderTest extends TestCase {
    public void testSimpleExpression() {        
        final Package pkg = new Package( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.setSalience( "p.age + 20" );
        ruleDescr.setConsequence( "" );

        MVELDialect mvelDialect = new MVELDialect( new PackageBuilder( pkg ) );
        DialectRegistry registry = new DialectRegistry();
        registry.addDialect( "mvel",
                             mvelDialect );
        final InstrumentedBuildContent context = new InstrumentedBuildContent( pkg,
                                                                               ruleDescr,
                                                                               registry,
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
        final ReteTuple tuple = new ReteTuple( f0 );

        SalienceBuilder salienceBuilder = new MVELSalienceBuilder();
        salienceBuilder.build( context );
                
        assertEquals( 51, context.getRule().getSalience().getValue( tuple, wm ) );
      
    }
}
