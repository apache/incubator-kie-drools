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

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.base.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.MockLeftTupleSink;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.builder.MVELDialect;
import org.drools.mvel.builder.MVELEvalBuilder;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.expr.MVELEvalExpression;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MVELEvalBuilderTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testSimpleExpression() {
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "pkg1" );
        final RuleDescr ruleDescr = new RuleDescr( "rule 1" );

        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl( pkg );
        final KnowledgeBuilderConfigurationImpl conf = pkgBuilder.getBuilderConfiguration();
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = ( MVELDialect ) dialectRegistry.getDialect( "mvel" );

        final RuleBuildContext context = new RuleBuildContext( pkgBuilder,
                                                               ruleDescr,
                                                               dialectRegistry,
                                                               pkg,
                                                               mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();

        final ReadAccessor extractor = store.getReader( Cheese.class,
                                                             "price" );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( int.class ) );
        final Declaration declaration = new Declaration( "a",
                                                         extractor,
                                                         pattern );
        final Map map = new HashMap();
        map.put( "a",
                 declaration );
        declarationResolver.setDeclarations( map );
        context.setDeclarationResolver( declarationResolver );

        final EvalDescr evalDescr = new EvalDescr();
        evalDescr.setContent( "a == 10" );

        final MVELEvalBuilder builder = new MVELEvalBuilder();
        final EvalCondition eval = (EvalCondition) builder.build( context,
                                                                  evalDescr );
        (( MVELEvalExpression ) eval.getEvalExpression()).compile( ( MVELDialectRuntimeData ) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ) );

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        BuildContext                             buildContext = new BuildContext(kBase, Collections.emptyList());
        org.drools.core.reteoo.MockLeftTupleSink sink         = new MockLeftTupleSink(buildContext);
        MockTupleSource                          source       = new MockTupleSource(1, buildContext);
        source.setObjectCount(1);
        sink.setLeftTupleSource(source);

        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( cheddar );

        final LeftTuple tuple = new LeftTuple( f0, sink, true );
        f0.removeLeftTuple(tuple);
        
        Object evalContext = eval.createContext();

        assertThat(eval.isAllowed(tuple,
                ksession,
                evalContext)).isTrue();

        cheddar.setPrice( 9 );
        ksession.update( f0,
                   cheddar );
        assertThat(eval.isAllowed(tuple,
                ksession,
                evalContext)).isFalse();
    }

}
