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



import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.EvaluatorFactory;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Query;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.MockField;

public class QueryTerminalNodeTest extends TestCase {
    public void testQueryTerminalNode() {
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        Rete rete = ruleBase.getRete();

        ClassObjectType queryObjectType = new ClassObjectType( DroolsQuery.class );
        ObjectTypeNode queryObjectTypeNode = new ObjectTypeNode( 1,
                                                                 queryObjectType,
                                                                 rete );
        queryObjectTypeNode.attach();

        ClassFieldExtractor extractor = new ClassFieldExtractor( DroolsQuery.class,
                                                                 "name" );

        FieldValue field = new MockField( "query-1" );

        Evaluator evaluator = EvaluatorFactory.getEvaluator( Evaluator.STRING_TYPE,
                                                             Evaluator.EQUAL );
        LiteralConstraint constraint = new LiteralConstraint( field,
                                                              extractor,
                                                              evaluator );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint,
                                             queryObjectTypeNode );
        alphaNode.attach();

        LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 3,
                                                                 alphaNode );
        liaNode.attach();

        ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        ObjectTypeNode cheeseObjectTypeNode = new ObjectTypeNode( 4,
                                                                  cheeseObjectType,
                                                                  rete );
        cheeseObjectTypeNode.attach();

        extractor = new ClassFieldExtractor( Cheese.class,
                                             "type" );

        field = new MockField( "stilton" );

        constraint = new LiteralConstraint( field,
                                            extractor,
                                            evaluator );

        alphaNode = new AlphaNode( 5,
                                   constraint,
                                   cheeseObjectTypeNode );
        alphaNode.attach();

        JoinNode joinNode = new JoinNode( 6,
                                          liaNode,
                                          alphaNode );
        joinNode.attach();

        Query query = new Query( "query-1" );

        QueryTerminalNode queryNode = new QueryTerminalNode( 7,
                                                             joinNode,
                                                             query );

        queryNode.attach();

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        QueryResults results = workingMemory.getQueryResults( "query-1" );

        assertNull( results );

        Cheese stilton1 = new Cheese( "stilton",
                                     100 );
        FactHandle handle1 = workingMemory.assertObject( stilton1 );

        results = workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        Cheese cheddar = new Cheese( "cheddar",
                                     55 );
        workingMemory.assertObject( cheddar );

        results = workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        Cheese stilton2 = new Cheese( "stilton",
                              5 );

        FactHandle handle2 = workingMemory.assertObject( stilton2 );

        results = workingMemory.getQueryResults( "query-1" );

        assertEquals( 2,
                      results.size() );
        
        QueryResult result = results.get( 0 );
        assertTrue( result.get( 0 ) instanceof DroolsQuery );        
        assertSame( stilton1, result.get( 1 ) );

        result = results.get( 1 );
        assertTrue( result.get( 0 ) instanceof DroolsQuery );        
        assertSame( stilton2, result.get( 1 ) ); 
        
        int i = 0;
        for ( Iterator it = results.iterator(); it.hasNext(); ) {
           result = ( QueryResult ) it.next();
           assertTrue( result.get( 0 ) instanceof DroolsQuery );
           if ( i == 0 ) {
               assertSame( stilton1, result.get( 1 ) );
           } else {
               assertSame( stilton2, result.get( 1 ) );
           }
           i++;
        }
        
        
        workingMemory.retractObject( handle1 );
        results = workingMemory.getQueryResults( "query-1" );
        

        assertEquals( 1,
                      results.size() );

        workingMemory.retractObject( handle2 );
        results = workingMemory.getQueryResults( "query-1" );

        assertNull( results );

    }

    public class Cheese {
        private String type;
        private int    price;

        public Cheese(String type,
                      int price) {
            super();
            this.type = type;
            this.price = price;
        }

        public int getPrice() {
            return price;
        }

        public String getType() {
            return type;
        }
        
        public String toString() {
            return "[Cheese type='" + this.type + "' price='" + this.price + "']";
        }

    }
}