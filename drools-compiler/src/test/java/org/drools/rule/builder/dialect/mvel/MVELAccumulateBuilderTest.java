package org.drools.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.InitialFactImpl;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.MockLeftTupleSink;
import org.drools.rule.Accumulate;
import org.drools.rule.Package;

public class MVELAccumulateBuilderTest extends TestCase {

    public void setUp() {
    }

    public void testSimpleExpression() {
        final Package pkg = new Package( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( "mvel" );

        final InstrumentedBuildContent context = new InstrumentedBuildContent( conf,
                                                                               ruleDescr,
                                                                               dialectRegistry,
                                                                               pkg,
                                                                               mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();

        final Map map = new HashMap();
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        final AccumulateDescr accDescr = new AccumulateDescr();
        final PatternDescr inputPattern = new PatternDescr( "org.drools.Cheese",
                                                            "$cheese" );
        accDescr.setInputPattern( inputPattern );
        accDescr.setInitCode( "total = 0;" );
        accDescr.setActionCode( "total += $cheese.price;" );
        accDescr.setReverseCode( "total -= $cheese.price;" );
        accDescr.setResultCode( "new Integer(total)" );

        final MVELAccumulateBuilder builder = new MVELAccumulateBuilder();
        final Accumulate acc = (Accumulate) builder.build( context,
                                                           accDescr );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final WorkingMemory wm = ruleBase.newStatefulSession();

        MockLeftTupleSink sink = new MockLeftTupleSink();
        final Cheese cheddar1 = new Cheese( "cheddar",
                                            10 );
        final Cheese cheddar2 = new Cheese( "cheddar",
                                            8 );
        final InternalFactHandle f0 = (InternalFactHandle) wm.insert( new InitialFactImpl() );
        final InternalFactHandle f1 = (InternalFactHandle) wm.insert( cheddar1 );
        final InternalFactHandle f2 = (InternalFactHandle) wm.insert( cheddar2 );
        final LeftTuple tuple = new LeftTuple( f0,
                                               sink,
                                               true );

        Object wmContext = acc.createWorkingMemoryContext();
        Object accContext = acc.createContext();
        acc.init( wmContext, accContext, tuple, wm );
        
        acc.accumulate( wmContext, accContext, tuple, f1, wm );
        acc.accumulate( wmContext, accContext, tuple, f2, wm );

        assertEquals( new Integer(18), acc.getResult( wmContext, accContext, tuple, wm ) );
        
        acc.reverse( wmContext, accContext, tuple, f1, wm );

        assertEquals( new Integer(8), acc.getResult( wmContext, accContext, tuple, wm ) );
    }

}
