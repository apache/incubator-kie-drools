package org.drools.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.Person;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.SalienceBuilder;
import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DefaultKnowledgeHelper;
import org.drools.core.base.mvel.MVELSalienceExpression;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.AgendaItemImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PatternExtractor;
import org.drools.core.spi.Salience;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.KnowledgeBaseFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MVELSalienceBuilderTest {
    private InstrumentedBuildContent context;
    private InternalKnowledgeBase kBase ;

    @Before
    public void setUp() throws Exception {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.addAttribute( new AttributeDescr( "salience",
                                                    "(p.age + 20)/2" ) );
        ruleDescr.setConsequence( "" );

        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl( pkg );
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( "mvel" );

        context = new InstrumentedBuildContent( pkgBuilder,
                                                ruleDescr,
                                                dialectRegistry,
                                                pkg,
                                                mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();

        final ObjectType personObjeectType = new ClassObjectType( Person.class );

        final Pattern pattern = new Pattern( 0,
                                             personObjeectType );

        final PatternExtractor extractor = new PatternExtractor( personObjeectType );

        final Declaration declaration = new Declaration( "p",
                                                         extractor,
                                                         pattern );
        final Map<String, Declaration> map = new HashMap<String, Declaration>();
        map.put( "p",
                 declaration );
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        SalienceBuilder salienceBuilder = new MVELSalienceBuilder();
        salienceBuilder.build( context );

        
        ((MVELSalienceExpression) context.getRule().getSalience()).compile( (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" ) );

    }

    @Test
    public void testSimpleExpression() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final Person p = new Person( "mark",
                                     "",
                                     31 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( p );
        final LeftTupleImpl tuple = new LeftTupleImpl( f0,
                                                       null,
                                                       true );

        RuleTerminalNode rtn = new RuleTerminalNode();
        rtn.setSalienceDeclarations( context.getDeclarationResolver().getDeclarations( context.getRule() ).values().toArray( new Declaration[1] ) );
        AgendaItem item = new AgendaItemImpl(0, tuple, 0, null, rtn, null);


        assertEquals( 25,
                      context.getRule().getSalience().getValue( new DefaultKnowledgeHelper( item, ksession ),
                                                                context.getRule(),
                                                                ksession ) );

    }

    @Test
    public void testMultithreadSalienceExpression() {
        final int tcount = 10;
        final SalienceEvaluator[] evals = new SalienceEvaluator[tcount];
        final Thread[] threads = new Thread[tcount];
        for ( int i = 0; i < evals.length; i++ ) {
                        
            evals[i] = new SalienceEvaluator( kBase,
                                              context,
                                              context.getRule(),
                                              context.getRule().getSalience(),
                                              new Person( "bob" + i,
                                                          30 + (i * 3) ) );
            threads[i] = new Thread( evals[i] );
        }
        for ( int i = 0; i < threads.length; i++ ) {
            threads[i].start();
        }
        for ( int i = 0; i < threads.length; i++ ) {
            try {
                threads[i].join();
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        int errors = 0;
        for ( int i = 0; i < evals.length; i++ ) {
            if ( evals[i].isError() ) {
                errors++;
            }
        }
        assertEquals( "There shouldn't be any threads in error: ",
                      0,
                      errors );

    }

    public static class SalienceEvaluator
        implements
        Runnable {
        public static final int          iterations = 1000;

        private Salience                 salience;
        private Rule                     rule;
        private LeftTupleImpl            tuple;
        private WorkingMemory            wm;
        private final int                result;
        private transient boolean        halt;
        private InstrumentedBuildContent context;
        private AgendaItem               item;

        private boolean                  error;

        public SalienceEvaluator(InternalKnowledgeBase kBase,
                                 InstrumentedBuildContent context,
                                 Rule rule,
                                 Salience salience,
                                 Person person) {
            wm = ((StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession());

            this.context = context;
            final InternalFactHandle f0 = (InternalFactHandle) wm.insert( person );
            tuple = new LeftTupleImpl( f0,
                                   null,
                                   true );
            this.salience = salience;
            this.halt = false;
            this.error = false;
            this.result = (person.getAge() + 20) / 2;
            
            RuleTerminalNode rtn = new RuleTerminalNode();
            rtn.setSalienceDeclarations( context.getDeclarationResolver().getDeclarations( context.getRule() ).values().toArray( new Declaration[1] ) );
            item = new AgendaItemImpl(0, tuple, 0, null, rtn, null);
        }

        public void run() {
            try {
                Thread.sleep( 1000 );
                for ( int i = 0; i < iterations && !halt; i++ ) {
                    assertEquals( result,
                                  salience.getValue( new DefaultKnowledgeHelper( item, wm ),
                                                     rule,
                                                     wm ) );
                    Thread.currentThread().yield();
                }
            } catch ( Throwable e ) {
                e.printStackTrace();
                this.error = true;
            }
        }

        public void halt() {
            this.halt = true;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

    }

}
