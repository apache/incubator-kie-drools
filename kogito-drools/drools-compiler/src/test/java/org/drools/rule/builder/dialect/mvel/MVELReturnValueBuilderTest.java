package org.drools.rule.builder.dialect.mvel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.base.mvel.MVELReturnValueExpression;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.Rule;
import org.drools.spi.InternalReadAccessor;

public class MVELReturnValueBuilderTest {


    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testSimpleExpression() {
        final Package pkg = new Package( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( "mvel" );

        final InstrumentedBuildContent context = new InstrumentedBuildContent( pkgBuilder,
                                                                               ruleDescr,
                                                                               dialectRegistry,
                                                                               pkg,
                                                                               mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "price",
                                                                getClass().getClassLoader() );

        final Pattern patternA = new Pattern( 0,
                                              new ClassObjectType( int.class ) );

        final Pattern patternB = new Pattern( 1,
                                              new ClassObjectType( int.class ) );

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

        final ReturnValueRestrictionDescr returnValueDescr = new ReturnValueRestrictionDescr( "=" );
        returnValueDescr.setContent( "a + b" );

        final MVELReturnValueBuilder builder = new MVELReturnValueBuilder();

        final Declaration[] previousDeclarations = new Declaration[]{a, b};
        final Declaration[] localDeclarations = new Declaration[]{};
        final String[] requiredGlobals = new String[]{};

        final ReturnValueRestriction returnValue = new ReturnValueRestriction( extractor,
                                                                               previousDeclarations,
                                                                               localDeclarations,
                                                                               requiredGlobals,
                                                                               context.getConfiguration().getEvaluatorRegistry().getEvaluator( ValueType.PINTEGER_TYPE,
                                                                                                                                               Operator.EQUAL ) );
        AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                          returnValueDescr,
                                                                          returnValueDescr.getContent(),
                                                                          new BoundIdentifiers( declarationResolver.getDeclarationClasses( (Rule) null ), new HashMap(), Cheese.class ) );        
        context.getBuildStack().push( patternB );
        builder.build( context,
                       analysis.getBoundIdentifiers(),
                       previousDeclarations,
                       localDeclarations,
                       returnValue,
                       returnValueDescr,
                       analysis );
        
        ((MVELReturnValueExpression)returnValue.getExpression()).compile( Thread.currentThread().getContextClassLoader() );

        ContextEntry retValContext = returnValue.createContextEntry();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory wm = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final Cheese stilton = new Cheese( "stilton",
                                           10 );

        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle f0 = (InternalFactHandle) wm.insert( cheddar );
        LeftTuple tuple = new LeftTuple( f0,
                                         null,
                                         true );

        final InternalFactHandle f1 = (InternalFactHandle) wm.insert( stilton );
        tuple = new LeftTuple( tuple,
                               new RightTuple( f1,
                                               null ),
                               null,
                               true );

        final Cheese brie = new Cheese( "brie",
                                        20 );
        final InternalFactHandle f2 = (InternalFactHandle) wm.insert( brie );

        assertTrue( returnValue.isAllowed( extractor,
                                           f2,
                                           tuple,
                                           wm,
                                           retValContext ) );

        brie.setPrice( 18 );
        wm.update( f2,
                   brie );
        assertFalse( returnValue.isAllowed( extractor,
                                            f2,
                                            tuple,
                                            wm,
                                            retValContext ) );
    }
}
