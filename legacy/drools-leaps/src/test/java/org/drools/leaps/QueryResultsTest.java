package org.drools.leaps;

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

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.rule.Column;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Query;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.MockField;

/** 
 *  
 * @author Alexander Bagerman
 * 
 */
public class QueryResultsTest extends TestCase {
    public void testQueryTerminalNode() throws PackageIntegrationException {
        final LeapsRuleBase ruleBase = (LeapsRuleBase) RuleBaseFactory.newRuleBase( RuleBase.LEAPS );

        final ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        final Evaluator evaluator = ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL );
        // fires on context.state == integer(1)
        final Query query = new Query( "query-1" );

        final Column cheeseColumn = new Column( 0,
                                                cheeseObjectType,
                                                "cheese" );
        cheeseColumn.addConstraint( getLiteralConstraint( cheeseColumn,
                                                          "type",
                                                          "stilton",
                                                          evaluator ) );
        query.addPattern( cheeseColumn );

        ruleBase.addRule( query );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.fireAllRules();

        LeapsQueryResults results = (LeapsQueryResults) workingMemory.getQueryResults( "query-1" );

        assertNull( results );

        final Cheese stilton1 = new Cheese( "stilton",
                                            100 );

        final FactHandle handle1 = workingMemory.assertObject( stilton1 );

        workingMemory.fireAllRules();

        results = (LeapsQueryResults) workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        final Cheese cheddar = new Cheese( "cheddar",
                                           55 );
        workingMemory.assertObject( cheddar );

        workingMemory.fireAllRules();

        results = (LeapsQueryResults) workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        final Cheese stilton2 = new Cheese( "stilton",
                                            5 );

        final FactHandle handle2 = workingMemory.assertObject( stilton2 );

        workingMemory.fireAllRules();

        results = (LeapsQueryResults) workingMemory.getQueryResults( "query-1" );

        assertEquals( 2,
                      results.size() );

        LeapsQueryResult result = (LeapsQueryResult) results.get( 0 );

        assertEquals( 1,
                      result.size() );

        boolean wasStilton1 = (stilton1 == result.get( 0 ));

        // assertSame( stilton1, result.get( 0 ) );

        result = (LeapsQueryResult) results.get( 1 );

        boolean wasStilton2 = (stilton2 == result.get( 0 ));

        assertEquals( 1,
                      result.size() );

        // assertSame( stilton2, result.get( 0 ) );
        assertTrue( (wasStilton1 && wasStilton2) || (!wasStilton1 && !wasStilton2) );
        Object result1 = null, result2 = null;
        int i = 0;
        for ( final Iterator it = results.iterator(); it.hasNext(); ) {
            result = (LeapsQueryResult) it.next();
            assertEquals( 1,
                          result.size() );
            if ( i == 0 ) {
                result1 = result.get( 0 );
            } else {
                result2 = result.get( 0 );
            }
            i++;
        }
        wasStilton1 = (stilton1 == result1);
        wasStilton2 = (stilton2 == result2);
        assertTrue( (wasStilton1 && wasStilton2) || (!wasStilton1 && !wasStilton2) );

        workingMemory.retractObject( handle1 );

        workingMemory.fireAllRules();

        results = (LeapsQueryResults) workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      results.size() );

        workingMemory.retractObject( handle2 );

        workingMemory.fireAllRules();

        results = (LeapsQueryResults) workingMemory.getQueryResults( "query-1" );

        assertNull( results );
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

    private AlphaNodeFieldConstraint getLiteralConstraint(final Column column,
                                                          final String fieldName,
                                                          final Object fieldValue,
                                                          final Evaluator evaluator) {
        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                                  fieldName );

        final FieldValue field = new MockField( fieldValue );

        return new LiteralConstraint( extractor,
                                      evaluator,
                                      field );
    }
}
