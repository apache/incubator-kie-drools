/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.FieldFactory;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.MvelConstraintTestUtil;
import org.drools.core.rule.QueryImpl;
import org.drools.core.rule.constraint.MvelConstraint;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.rule.Query;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.definition.KnowledgePackage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Ignore("phreak")
public class QueryTerminalNodeTest {
    private InternalKnowledgeBase kBase;
    private BuildContext     buildContext;
    private EntryPointNode   entryPoint;

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        this.buildContext = new BuildContext( kBase,
                                              kBase.getReteooBuilder().getIdGenerator() );
        this.entryPoint = new EntryPointNode( 0,
                                              this.kBase.getRete(),
                                              buildContext );
        this.entryPoint.attach(buildContext);
    }

    @Test
    public void testQueryTerminalNode() {
        final ClassObjectType queryObjectType = new ClassObjectType( DroolsQuery.class );
        final ObjectTypeNode queryObjectTypeNode = new ObjectTypeNode( this.buildContext.getNextId(),
                                                                       this.entryPoint,
                                                                       queryObjectType,
                                                                       buildContext );
        queryObjectTypeNode.attach(buildContext);

        ClassFieldReader extractor = store.getReader(DroolsQuery.class, "name" );

        MvelConstraint constraint = new MvelConstraintTestUtil( "name == \"query-1\"",
                                                                FieldFactory.getInstance().getFieldValue( "query-1" ),
                                                                extractor );

        final QueryImpl query = new QueryImpl( "query-1" );
        buildContext.setRule(query);
        AlphaNode alphaNode = new AlphaNode( this.buildContext.getNextId(),
                                             constraint,
                                             queryObjectTypeNode,
                                             buildContext );
        alphaNode.attach(buildContext);

        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( this.buildContext.getNextId(),
                                                                       alphaNode,
                                                                       this.buildContext );
        liaNode.attach(buildContext);

        final ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        final ObjectTypeNode cheeseObjectTypeNode = new ObjectTypeNode( this.buildContext.getNextId(),
                                                                        this.entryPoint,
                                                                        cheeseObjectType,
                                                                        buildContext );
        cheeseObjectTypeNode.attach(buildContext);

        extractor = store.getReader( Cheese.class, "type" );


        constraint = new MvelConstraintTestUtil( "type == \"stilton\"",
                                                 FieldFactory.getInstance().getFieldValue( "stilton" ),
                                                 extractor );

        alphaNode = new AlphaNode( this.buildContext.getNextId(),
                                   constraint,
                                   cheeseObjectTypeNode,
                                   buildContext );
        alphaNode.attach(buildContext);

        BuildContext buildContext = new BuildContext( kBase,
                                                      kBase.getReteooBuilder().getIdGenerator() );
        buildContext.setTupleMemoryEnabled( false );

        final JoinNode joinNode = new JoinNode( this.buildContext.getNextId(),
                                                liaNode,
                                                alphaNode,
                                                EmptyBetaConstraints.getInstance(),
                                                buildContext );
        joinNode.attach(buildContext);


        final QueryTerminalNode queryNode = new QueryTerminalNode( this.buildContext.getNextId(),
                                                                   joinNode,
                                                                   query,
                                                                   ((QueryImpl)query).getLhs(),
                                                                   0,
                                                                   buildContext );

        queryNode.attach(buildContext);

        final KnowledgePackageImpl pkg = new KnowledgePackageImpl( "com.drools.test" );
        pkg.addRule( query );
        ((KnowledgeBaseImpl) kBase).addPackages(Arrays.asList(new InternalKnowledgePackage[] { pkg }));


        KieSession kSession = kBase.newKieSession();
        QueryResults results = kSession.getQueryResults( "query-1" );

        assertEquals( 0,
                      results.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            100 );
        final FactHandle handle1 = kSession.insert( stilton1 );

        results = kSession.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        final Cheese cheddar = new Cheese( "cheddar",
                                           55 );
        kSession.insert( cheddar );

        results = kSession.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        final Cheese stilton2 = new Cheese( "stilton",
                                            5 );

        final FactHandle handle2 = kSession.insert( stilton2 );

        results = kSession.getQueryResults( "query-1" );

        assertEquals( 2,
                      results.size() );

        /**
        QueryResultsRow result = results.get( 0 );
        assertEquals( 1,
                      result.size() );
        assertEquals( stilton1,
                      result.get( 0 ) );

        result = results.get( 1 );
        assertEquals( 1,
                      result.size() );
        assertEquals( stilton2,
                      result.get( 0 ) );
         **/

        int i = 0;
        for ( final Iterator<QueryResultsRow> it = results.iterator(); it.hasNext(); ) {
            QueryResultsRow resultRow = it.next();
            if ( i == 1 ) {
//                assertSame( stilton2, result.g( 0 ) );
            } else {
//                assertSame( stilton1, result.get( 0 ) );
            }
            i++;
        }

        kSession.delete( handle1 );
        results = kSession.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        kSession.delete( handle2 );
        results = kSession.getQueryResults( "query-1" );

        assertEquals( 0,
                      results.size() );

    }
    public class Cheese {
        private String type;
        private int    price;

        public Cheese(final String type,
                      final int price) {
            super();
            this.type = type;
            this.price = price;
        }

        public int getPrice() {
            return this.price;
        }

        public String getType() {
            return this.type;
        }

        public String toString() {
            return "[Cheese type='" + this.type + "' price='" + this.price + "']";
        }

    }
}
