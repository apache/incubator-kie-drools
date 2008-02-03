package org.drools.rule.builder.dialect.mvel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.PredicateConstraint.PredicateContextEntry;
import org.drools.spi.FieldExtractor;

public class MVELPredicateBuilderTest extends TestCase {

    private ClassFieldExtractorCache cache = ClassFieldExtractorCache.getInstance();

    public void setUp() {
    }

    public void testSimpleExpression() {
        final Package pkg = new Package( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
        MVELDialect mvelDialect = ( MVELDialect ) pkgBuilder.getDialectRegistry().getDialect( "mvel" );

        final InstrumentedBuildContent context = new InstrumentedBuildContent( conf,
                                                                               pkg,
                                                                               ruleDescr,
                                                                               pkgBuilder.getDialectRegistry(),
                                                                               mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();
        final FieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                             "price",
                                                             getClass().getClassLoader() );

        final Pattern patternA = new Pattern( 0,
                                              new ClassObjectType( Cheese.class ) );

        final Pattern patternB = new Pattern( 1,
                                              new ClassObjectType( Cheese.class ) );

        final Declaration a = new Declaration( "a",
                                               extractor,
                                               patternA );
        final Declaration b = new Declaration( "b",
                                               extractor,
                                               patternB );

        final Map map = new HashMap();
        map.put( "a",
                 a );
        map.put( "b",
                 b );
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        final PredicateDescr predicateDescr = new PredicateDescr();
        predicateDescr.setContent( "a == b" );

        final MVELPredicateBuilder builder = new MVELPredicateBuilder();

        final List[] usedIdentifiers = new ArrayList[2];
        final List list = new ArrayList();
        usedIdentifiers[1] = list;

        final Declaration[] previousDeclarations = new Declaration[]{a};
        final Declaration[] localDeclarations = new Declaration[]{b};

        final PredicateConstraint predicate = new PredicateConstraint( null,
                                                                       localDeclarations );

        builder.build( context,
                       usedIdentifiers,
                       previousDeclarations,
                       localDeclarations,
                       predicate,
                       predicateDescr );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory wm = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final Cheese stilton = new Cheese( "stilton",
                                           10 );

        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle f0 = (InternalFactHandle) wm.insert( cheddar );
        final InternalFactHandle f1 = (InternalFactHandle) wm.insert( stilton );
        final ReteTuple tuple = new ReteTuple( f0 );

        final PredicateContextEntry predicateContext = (PredicateContextEntry) predicate.createContextEntry();
        predicateContext.leftTuple = tuple;

        assertTrue( predicate.isAllowedCachedLeft( predicateContext,
                                                   f1 ) );

        cheddar.setPrice( 9 );
        wm.update( f0,
                   cheddar );

        assertFalse( predicate.isAllowedCachedLeft( predicateContext,
                                                    f1 ) );
    }

}
