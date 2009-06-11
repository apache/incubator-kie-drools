package org.drools.rule.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.base.mvel.MVELSalienceExpression;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.builder.SalienceBuilder;
import org.drools.spi.ObjectType;
import org.drools.spi.PatternExtractor;
import org.drools.spi.Salience;

public class MVELSalienceBuilderTest extends TestCase {
    private InstrumentedBuildContent context;
    private RuleBase                 ruleBase;

    protected void setUp() throws Exception {
        super.setUp();
        final Package pkg = new Package( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.addAttribute( new AttributeDescr( "salience",
                                                    "(p.age + 20)/2" ) );
        ruleDescr.setConsequence( "" );

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
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

        ruleBase = RuleBaseFactory.newRuleBase();
        SalienceBuilder salienceBuilder = new MVELSalienceBuilder();
        salienceBuilder.build( context );

        ((MVELSalienceExpression) context.getRule().getSalience()).compile( Thread.currentThread().getContextClassLoader() );

    }

    public void testSimpleExpression() {
        WorkingMemory wm = ruleBase.newStatefulSession();

        final Person p = new Person( "mark",
                                     "",
                                     31 );
        final InternalFactHandle f0 = (InternalFactHandle) wm.insert( p );
        final LeftTuple tuple = new LeftTuple( f0,
                                               null,
                                               true );

        assertEquals( 25,
                      context.getRule().getSalience().getValue( tuple,
                                                                wm ) );

    }

    public void testMultithreadSalienceExpression() {
        final int tcount = 10;
        final SalienceEvaluator[] evals = new SalienceEvaluator[tcount];
        final Thread[] threads = new Thread[tcount];
        for ( int i = 0; i < evals.length; i++ ) {
            evals[i] = new SalienceEvaluator( ruleBase,
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
        public static final int   iterations = 1000;

        private Salience          salience;
        private LeftTuple         tuple;
        private WorkingMemory     wm;
        private final int         result;
        private transient boolean halt;

        private boolean           error;

        public SalienceEvaluator(RuleBase ruleBase,
                                 Salience salience,
                                 Person person) {
            wm = ruleBase.newStatefulSession();
            final InternalFactHandle f0 = (InternalFactHandle) wm.insert( person );
            tuple = new LeftTuple( f0,
                                   null,
                                   true );
            this.salience = salience;
            this.halt = false;
            this.error = false;
            this.result = (person.getAge() + 20) / 2;
        }

        public void run() {
            try {
                Thread.sleep( 1000 );
                for ( int i = 0; i < iterations && !halt; i++ ) {
                    assertEquals( result,
                                  salience.getValue( tuple,
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
