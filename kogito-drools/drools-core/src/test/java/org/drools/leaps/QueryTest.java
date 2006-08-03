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

import java.util.ArrayList;

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.examples.manners.Context;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.MockField;
import org.drools.spi.Tuple;

/**
 * 
 * @author Alexander Bagerman
 *
 */
public class QueryTest extends DroolsTestCase {
    LeapsRuleBase   ruleBase;
    LeapsRuleBase   ruleBaseAddRule;

    WorkingMemory   wm1;

    // leaps add rule objects
    final String    handle1Rule1    = "11";

    final String    handle1Rule2    = "12";

    final String    handle2Rule1    = "21";

    final String    handle2Rule2    = "22";

    final ArrayList handlesForRules = new ArrayList();

    WorkingMemory   workingMemory;

    Rule            rule1;

    Rule            rule2;

    final Context   context1        = new Context( 1 );

    final Context   context2        = new Context( 1 );

    public void setUp() throws Exception {
        this.ruleBase = (LeapsRuleBase) RuleBaseFactory.newRuleBase( RuleBase.LEAPS );

        this.wm1 = this.ruleBase.newWorkingMemory();
        // add rules section
        this.ruleBaseAddRule = (LeapsRuleBase) RuleBaseFactory.newRuleBase( RuleBase.LEAPS );

        this.workingMemory = this.ruleBaseAddRule.newWorkingMemory();
        // rules
        final ClassObjectType contextType = new ClassObjectType( Context.class );
        final Evaluator integerEqualEvaluator = ValueType.INTEGER_TYPE.getEvaluator( Operator.EQUAL );
        // rule 1
        // fires on context.state == integer(1)
        this.rule1 = new Query( "query-to-execute" );
        final Column contextColumnRule1 = new Column( 0,
                                                      contextType,
                                                      "context1" );
        contextColumnRule1.addConstraint( getLiteralConstraint( contextColumnRule1,
                                                                "state",
                                                                new Integer( 1 ),
                                                                integerEqualEvaluator ) );
        this.rule1.addPattern( contextColumnRule1 );

        this.rule2 = new Rule( "rule2" );
        final Column contextColumnRule2 = new Column( 0,
                                                      contextType,
                                                      "context2" );
        contextColumnRule2.addConstraint( getLiteralConstraint( contextColumnRule2,
                                                                "state",
                                                                new Integer( 1 ),
                                                                integerEqualEvaluator ) );
        this.rule2.addPattern( contextColumnRule2 );
        final Declaration contextRule2Declaration = this.rule2.getDeclaration( "context2" );
        this.rule2.setConsequence( new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -5839841224218714327L;

            public void evaluate(final KnowledgeHelper drools,
                                 final WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    final Rule rule = drools.getRule();
                    final Tuple tuple = drools.getTuple();

                    final Context dummy = (Context) drools.get( contextRule2Declaration );
                    if ( dummy == QueryTest.this.context1 ) {
                        QueryTest.this.handlesForRules.add( QueryTest.this.handle1Rule2 );
                    } else if ( dummy == QueryTest.this.context2 ) {
                        QueryTest.this.handlesForRules.add( QueryTest.this.handle2Rule2 );
                    }

                } catch ( final Exception e ) {
                    throw new ConsequenceException( e );
                }
            }

        } );
    }

    public void testAddRuleBeforeFacts() throws Exception {

        assertEquals( 0,
                      this.handlesForRules.size() );

        this.ruleBaseAddRule.addRule( this.rule1 );
        this.ruleBaseAddRule.addRule( this.rule2 );
        this.workingMemory.assertObject( this.context1 );
        this.workingMemory.assertObject( this.context2 );
        // firing
        this.workingMemory.fireAllRules();
        // finally everything should be filled
        assertEquals( 2,
                      this.handlesForRules.size() );
        assertEquals( 2,
                      this.workingMemory.getQueryResults( "query-to-execute" ).size() );
        assertTrue( this.handlesForRules.contains( this.handle1Rule2 ) );
        assertTrue( this.handlesForRules.contains( this.handle2Rule2 ) );
    }

    private FieldConstraint getLiteralConstraint(final Column column,
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