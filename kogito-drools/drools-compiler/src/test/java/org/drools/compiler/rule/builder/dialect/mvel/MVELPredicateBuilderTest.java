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
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.reteoo.MockLeftTupleSink;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.mvel.MVELPredicateExpression;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.rule.PredicateConstraint.PredicateContextEntry;
import org.drools.core.spi.InternalReadAccessor;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MVELPredicateBuilderTest {


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
        PackageRegistry pkgRegistry = pkgBuilder.getPackageRegistry( pkg.getName() );
        MVELDialect mvelDialect = ( MVELDialect ) pkgRegistry.getDialectCompiletimeRegistry().getDialect( "mvel" );

        final InstrumentedBuildContent context = new InstrumentedBuildContent( pkgBuilder,
                                                                               ruleDescr,
                                                                               pkgRegistry.getDialectCompiletimeRegistry(),
                                                                               pkg,                                                                               
                                                                               mvelDialect );

        final InstrumentedDeclarationScopeResolver declarationResolver = new InstrumentedDeclarationScopeResolver();
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                             "price" );

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
        
        context.getBuildStack().add( patternA );
        context.getBuildStack().add( patternB );

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
        final Declaration[] previousDeclarations = new Declaration[]{a};
        final Declaration[] localDeclarations = new Declaration[]{b};

        final PredicateConstraint predicate = new PredicateConstraint( null,
                                                                       localDeclarations );
        
        AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                          predicateDescr,
                                                                          predicateDescr.getContent(),
                                                                          new BoundIdentifiers( declarationResolver.getDeclarationClasses( (RuleImpl) null ), new HashMap(), null, Cheese.class ) );

        builder.build( context,
                       new BoundIdentifiers( declarationResolver.getDeclarationClasses( (RuleImpl) null ), new HashMap() ),
                       previousDeclarations,
                       localDeclarations,
                       predicate,
                       predicateDescr,
                       analysis );
        
        ( (MVELPredicateExpression) predicate.getPredicateExpression()).compile( (MVELDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData( "mvel" ) );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
        InternalWorkingMemory wm = ksession;

        final Cheese stilton = new Cheese( "stilton",
                                           10 );

        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        
        MockLeftTupleSink sink = new MockLeftTupleSink();
        
        final InternalFactHandle f0 = (InternalFactHandle) wm.insert( cheddar );
        final InternalFactHandle f1 = (InternalFactHandle) wm.insert( stilton );
        final LeftTupleImpl tuple = new LeftTupleImpl( f0, sink, true );
        f0.removeLeftTuple(tuple);

        final PredicateContextEntry predicateContext = (PredicateContextEntry) predicate.createContextEntry();
        predicateContext.leftTuple = tuple;
        predicateContext.workingMemory = wm;

        assertTrue( predicate.isAllowedCachedLeft( predicateContext,
                                                   f1 ) );

        cheddar.setPrice( 9 );
        wm.update( f0,
                   cheddar );

        assertFalse( predicate.isAllowedCachedLeft( predicateContext,
                                                    f1 ) );
    }

}
