/*
 * Copyright 2005 JBoss Inc
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

package org.drools.reteoo;

import org.drools.FactHandle;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassFieldReader;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.FieldFactory;
import org.drools.common.EmptyBetaConstraints;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.MvelConstraintTestUtil;
import org.drools.rule.Query;
import org.drools.rule.constraint.MvelConstraint;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class QueryTerminalNodeTest {
    private ReteooRuleBase   ruleBase;
    private BuildContext     buildContext;
    private EntryPointNode   entryPoint;

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        this.buildContext = new BuildContext( ruleBase,
                                              ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
        this.entryPoint = new EntryPointNode( 0,
                                              this.ruleBase.getRete(),
                                              buildContext );
        this.entryPoint.attach();
    }

    @Test
    public void testQueryTerminalNode() {
        final ClassObjectType queryObjectType = new ClassObjectType( DroolsQuery.class );
        final ObjectTypeNode queryObjectTypeNode = new ObjectTypeNode( this.buildContext.getNextId(),
                                                                       this.entryPoint,
                                                                       queryObjectType,
                                                                       buildContext );
        queryObjectTypeNode.attach();

        ClassFieldReader extractor = store.getReader(DroolsQuery.class,
                "name",
                DroolsQuery.class.getClassLoader());

        MvelConstraint constraint = new MvelConstraintTestUtil( "name == \"query-1\"",
                                                                FieldFactory.getInstance().getFieldValue( "query-1" ),
                                                                extractor );

        AlphaNode alphaNode = new AlphaNode( this.buildContext.getNextId(),
                                             constraint,
                                             queryObjectTypeNode,
                                             buildContext );
        alphaNode.attach();

        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( this.buildContext.getNextId(),
                                                                       alphaNode,
                                                                       this.buildContext );
        liaNode.attach();

        final ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        final ObjectTypeNode cheeseObjectTypeNode = new ObjectTypeNode( this.buildContext.getNextId(),
                                                                        this.entryPoint,
                                                                        cheeseObjectType,
                                                                        buildContext );
        cheeseObjectTypeNode.attach();

        extractor = store.getReader( Cheese.class,
                                        "type",
                                        getClass().getClassLoader() );

        constraint = new MvelConstraintTestUtil( "type == \"stilton\"",
                                                 FieldFactory.getInstance().getFieldValue( "stilton" ),
                                                 extractor );

        alphaNode = new AlphaNode( this.buildContext.getNextId(),
                                   constraint,
                                   cheeseObjectTypeNode,
                                   buildContext );
        alphaNode.attach();

        BuildContext buildContext = new BuildContext( ruleBase,
                                                      ruleBase.getReteooBuilder().getIdGenerator() );
        buildContext.setTupleMemoryEnabled( false );

        final JoinNode joinNode = new JoinNode( this.buildContext.getNextId(),
                                                liaNode,
                                                alphaNode,
                                                EmptyBetaConstraints.getInstance(),
                                                buildContext );
        joinNode.attach();

        final Query query = new Query( "query-1" );

        final QueryTerminalNode queryNode = new QueryTerminalNode( this.buildContext.getNextId(),
                                                                   joinNode,
                                                                   query,
                                                                   query.getLhs(),
                                                                   0,
                                                                   buildContext );

        queryNode.attach();

        final org.drools.rule.Package pkg = new org.drools.rule.Package( "com.drools.test" );
        pkg.addRule( query );

        try {
            final Field pkgField = ruleBase.getClass().getSuperclass().getDeclaredField( "pkgs" );
            pkgField.setAccessible( true );
            final Map pkgs = (Map) pkgField.get( ruleBase );
            pkgs.put( pkg.getName(),
                      pkg );
        } catch ( final Exception e ) {
            fail( "Should not throw any exception: " + e.getMessage() );
        }

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        QueryResults results = workingMemory.getQueryResults( "query-1" );

        assertEquals( 0,
                      results.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            100 );
        final FactHandle handle1 = workingMemory.insert( stilton1 );

        results = workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        final Cheese cheddar = new Cheese( "cheddar",
                                           55 );
        workingMemory.insert( cheddar );

        results = workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        final Cheese stilton2 = new Cheese( "stilton",
                                            5 );

        final FactHandle handle2 = workingMemory.insert( stilton2 );

        results = workingMemory.getQueryResults( "query-1" );

        assertEquals( 2,
                      results.size() );

        QueryResult result = results.get( 0 );
        assertEquals( 1,
                      result.size() );
        assertEquals( stilton1,
                      result.get( 0 ) );

        result = results.get( 1 );
        assertEquals( 1,
                      result.size() );
        assertEquals( stilton2,
                      result.get( 0 ) );

        int i = 0;
        for ( final Iterator it = results.iterator(); it.hasNext(); ) {
            result = (QueryResult) it.next();
            assertEquals( 1,
                          result.size() );
            if ( i == 1 ) {
                assertSame( stilton2,
                            result.get( 0 ) );
            } else {
                assertSame( stilton1,
                            result.get( 0 ) );
            }
            i++;
        }

        workingMemory.retract( handle1 );
        results = workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        workingMemory.retract( handle2 );
        results = workingMemory.getQueryResults( "query-1" );

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
