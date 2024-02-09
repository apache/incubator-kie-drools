/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.rule.builder.dialect.mvel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.SalienceBuilder;
import org.drools.core.WorkingMemory;
import org.drools.base.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.MockLeftTupleSink;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TupleFactory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.Pattern;
import org.drools.base.base.ObjectType;
import org.drools.base.rule.accessor.PatternExtractor;
import org.drools.base.rule.accessor.Salience;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.builder.MVELDialect;
import org.drools.mvel.builder.MVELSalienceBuilder;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.expr.MVELSalienceExpression;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;

import static org.assertj.core.api.Assertions.assertThat;

public class MVELSalienceBuilderTest {
    private RuleBuildContext context;
    private InternalKnowledgeBase kBase ;
    private BuildContext buildContext;

    @Before
    public void setUp() throws Exception {
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );
        ruleDescr.addAttribute( new AttributeDescr( "salience",
                                                    "(p.age + 20)/2" ) );
        ruleDescr.setConsequence( "" );

        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl( pkg );
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( "mvel" );

        context = new RuleBuildContext( pkgBuilder,
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

        kBase = KnowledgeBaseFactory.newKnowledgeBase();
        buildContext = new BuildContext(kBase, Collections.emptyList());

        SalienceBuilder salienceBuilder = new MVELSalienceBuilder();
        salienceBuilder.build( context );

        
        (( MVELSalienceExpression ) context.getRule().getSalience()).compile( ( MVELDialectRuntimeData ) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" ) );

    }

    @Test
    public void testSimpleExpression() {
        StatefulKnowledgeSessionImpl ksession     = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final Person p = new Person( "mark",
                                     "",
                                     31 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( p );

        MockTupleSource source = new MockTupleSource(1, buildContext);
        source.setObjectCount(1);

        RuleTerminalNode rtn = new RuleTerminalNode(0, source, context.getRule(), new GroupElement(), 0, buildContext);

        final LeftTuple tuple = new LeftTuple( f0,
                                                               rtn,
                                                       true );

        rtn.setSalienceDeclarations( context.getDeclarationResolver().getDeclarations( context.getRule() ).values().toArray( new Declaration[1] ) );

        final RuleTerminalNodeLeftTuple item = (RuleTerminalNodeLeftTuple) TupleFactory.createLeftTuple(tuple, rtn, null, true);
        item.init(0, 0, item.getPropagationContext(), null, null);

        assertThat(context.getRule().getSalience().getValue(item, context.getRule(), ksession)).isEqualTo(25);

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
        assertThat(errors).as("There shouldn't be any threads in error: ").isEqualTo(0);

    }

    public static class SalienceEvaluator
        implements
        Runnable {
        public static final int           iterations = 1000;

        private Salience                  salience;
        private Rule                      rule;
        private LeftTuple                 tuple;
        private WorkingMemory             wm;
        private final int                 result;
        private transient boolean         halt;
        private RuleBuildContext          context;
        private RuleTerminalNodeLeftTuple item;

        private boolean                   error;

        public SalienceEvaluator(InternalKnowledgeBase kBase,
                                 RuleBuildContext context,
                                 Rule rule,
                                 Salience salience,
                                 Person person) {
            wm = ((StatefulKnowledgeSessionImpl)kBase.newKieSession());

            this.context = context;
            final InternalFactHandle f0 = (InternalFactHandle) wm.insert( person );

            BuildContext buildContext = new BuildContext(kBase, Collections.emptyList());
            MockLeftTupleSink sink = new MockLeftTupleSink(buildContext);
            MockTupleSource source = new MockTupleSource(1, buildContext);
            source.setObjectCount(1);
            sink.setLeftTupleSource(source);

            tuple = new LeftTuple(f0,
                                          sink,
                                          true );
            this.salience = salience;
            this.halt = false;
            this.error = false;
            this.result = (person.getAge() + 20) / 2;

            RuleTerminalNode rtn = new RuleTerminalNode(0, source, context.getRule(), new GroupElement(), 0, buildContext);
            rtn.setSalienceDeclarations( context.getDeclarationResolver().getDeclarations( context.getRule() ).values().toArray( new Declaration[1] ) );

            item = (RuleTerminalNodeLeftTuple) TupleFactory.createLeftTuple(tuple, rtn, null, true);
            item.init(0, 0, item.getPropagationContext(), null, null);
        }

        public void run() {
            try {
                Thread.sleep( 1000 );
                for ( int i = 0; i < iterations && !halt; i++ ) {
                    assertThat(salience.getValue(item, rule, wm)).isEqualTo(result);
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
