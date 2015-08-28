/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.Cheese;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.reteoo.MockLeftTupleSink;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.mvel.MVELEvalExpression;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.InternalReadAccessor;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MVELEvalBuilderTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testSimpleExpression() {
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "pkg1" );
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

        final InternalReadAccessor extractor = store.getReader( Cheese.class,
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
        ((MVELEvalExpression) eval.getEvalExpression()).compile( (MVELDialectRuntimeData) pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ) );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        MockLeftTupleSink sink = new MockLeftTupleSink();
        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( cheddar );

        final LeftTupleImpl tuple = new LeftTupleImpl( f0, sink, true );
        f0.removeLeftTuple(tuple);
        
        Object evalContext = eval.createContext();

        assertTrue( eval.isAllowed( tuple,
                                    ksession,
                                    evalContext ) );

        cheddar.setPrice( 9 );
        ksession.update( f0,
                   cheddar );
        assertFalse( eval.isAllowed( tuple,
                                     ksession,
                                     evalContext ) );
    }

}
