package org.drools.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.Cheese;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.common.InternalFactHandle;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.reteoo.InitialFactImpl;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.compiler.reteoo.MockLeftTupleSink;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.kie.internal.KnowledgeBaseFactory;

public class MVELAccumulateBuilderTest {

    @Test
    public void testSimpleExpression() {
        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl();
        pkgBuilder.addPackage( new PackageDescr( "pkg1" ) );

        InternalKnowledgePackage pkg = pkgBuilder.getPackage();
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );

        final KnowledgeBuilderConfigurationImpl conf = pkgBuilder.getBuilderConfiguration();
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( "mvel" );

        final RuleBuildContext context = new RuleBuildContext( pkgBuilder,
                                                               ruleDescr,
                                                               dialectRegistry,
                                                               pkg,
                                                               mvelDialect );

        final AccumulateDescr accDescr = new AccumulateDescr();
        final PatternDescr inputPattern = new PatternDescr( "org.drools.compiler.Cheese",
                                                            "$cheese" );
        accDescr.setInputPattern( inputPattern );
        accDescr.setInitCode( "total = 0;" );
        accDescr.setActionCode( "total += $cheese.price;" );
        accDescr.setReverseCode( "total -= $cheese.price;" );
        accDescr.setResultCode( "new Integer(total)" );

        final MVELAccumulateBuilder builder = new MVELAccumulateBuilder();
        final Accumulate acc = (Accumulate) builder.build( context,
                                                           accDescr );

        ((MVELCompileable) acc.getAccumulators()[0]).compile( (MVELDialectRuntimeData) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ) );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        MockLeftTupleSink sink = new MockLeftTupleSink();
        final Cheese cheddar1 = new Cheese( "cheddar",
                                            10 );
        final Cheese cheddar2 = new Cheese( "cheddar",
                                            8 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( new InitialFactImpl() );
        final InternalFactHandle f1 = (InternalFactHandle) ksession.insert( cheddar1 );
        final InternalFactHandle f2 = (InternalFactHandle) ksession.insert( cheddar2 );
        final LeftTupleImpl tuple = new LeftTupleImpl( f0,
                                               sink,
                                               true );

        Object[] wmContext = acc.createWorkingMemoryContext();
        Object[] accContext = acc.createContext();
        acc.init( wmContext,
                  accContext,
                  tuple,
                  ksession );

        acc.accumulate( wmContext,
                        accContext,
                        tuple,
                        f1,
                        ksession );
        acc.accumulate( wmContext,
                        accContext,
                        tuple,
                        f2,
                        ksession );

        assertEquals( new Integer( 18 ),
                      acc.getResult( wmContext,
                                     accContext,
                                     tuple,
                                     ksession )[0] );

        acc.reverse( wmContext,
                     accContext,
                     tuple,
                     f1,
                     ksession );

        assertEquals( new Integer( 8 ),
                      acc.getResult( wmContext,
                                     accContext,
                                     tuple,
                                     ksession )[0] );
    }

}
