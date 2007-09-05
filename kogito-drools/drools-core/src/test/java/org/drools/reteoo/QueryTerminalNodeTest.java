package org.drools.reteoo;

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

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.common.EmptyBetaConstraints;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Query;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;

public class QueryTerminalNodeTest extends TestCase {
    public void testQueryTerminalNode() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();

        final Rete rete = ruleBase.getRete();

        final ClassObjectType queryObjectType = new ClassObjectType( DroolsQuery.class );
        final ObjectTypeNode queryObjectTypeNode = new ObjectTypeNode( 1,
                                                                       queryObjectType,
                                                                       rete,
                                                                       3 );
        queryObjectTypeNode.attach();

        ClassFieldExtractor extractor = ClassFieldExtractorCache.getExtractor( DroolsQuery.class,
                                                                               "name",
                                                                               DroolsQuery.class.getClassLoader() );

        FieldValue field = FieldFactory.getFieldValue( "query-1" );

        final Evaluator evaluator = ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL );
        LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                              evaluator,
                                                              field );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint,
                                             queryObjectTypeNode,
                                             true,
                                             3  );
        alphaNode.attach();

        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 3,
                                                                       alphaNode );
        liaNode.attach();

        final ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        final ObjectTypeNode cheeseObjectTypeNode = new ObjectTypeNode( 4,
                                                                        cheeseObjectType,
                                                                        rete,
                                                                        3 );
        cheeseObjectTypeNode.attach();

        extractor = ClassFieldExtractorCache.getExtractor( Cheese.class,
                                                           "type",
                                                           getClass().getClassLoader() );

        field = FieldFactory.getFieldValue( "stilton" );

        constraint = new LiteralConstraint( extractor,
                                            evaluator,
                                            field );

        alphaNode = new AlphaNode( 5,
                                   constraint,
                                   cheeseObjectTypeNode,
                                   true,
                                   3  );
        alphaNode.attach();

        BuildContext buildContext = new BuildContext( ruleBase, ruleBase.getReteooBuilder().getIdGenerator() );
        
        final JoinNode joinNode = new JoinNode( 6,
                                                liaNode,
                                                alphaNode,
                                                EmptyBetaConstraints.getInstance(),
                                                buildContext );
        joinNode.attach();

        final Query query = new Query( "query-1" );

        final QueryTerminalNode queryNode = new QueryTerminalNode( 7,
                                                                   joinNode,
                                                                   query,
                                                                   query.getLhs() );

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
            Assert.fail( "Should not throw any exception: " + e.getMessage() );
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
        assertEquals( stilton2,
                      result.get( 0 ) );

        result = results.get( 1 );
        assertEquals( 1,
                      result.size() );
        assertEquals( stilton1,
                      result.get( 0 ) );

        int i = 0;
        for ( final Iterator it = results.iterator(); it.hasNext(); ) {
            result = (QueryResult) it.next();
            assertEquals( 1,
                          result.size() );
            if ( i == 1 ) {
                assertSame( stilton1,
                            result.get( 0 ) );
            } else {
                assertSame( stilton2,
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