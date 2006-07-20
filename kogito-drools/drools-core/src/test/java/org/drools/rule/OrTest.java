package org.drools.rule;

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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.EvaluatorFactory;
import org.drools.examples.waltz.Edge;
import org.drools.examples.waltz.Stage;
import org.drools.leaps.LeapsRuleBase;
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
 * This test demonstrate the following failures:
 * 1. Or constraint to produce matches when Or constraint is specified at the beging. 
 * I suspect it has something to do with regular columns having higher indexes
 * 
 * 2. Resolve an object that has Or condition defined for it in consequence 
 * 
 * 
 * @author Alexander Bagerman
 *
 */
public class OrTest extends TestCase {

    private Evaluator       integerEqualEvaluator;
    private Evaluator       integerNotEqualEvaluator;

    private ClassObjectType stageType;

    private ClassObjectType edgeType;

    protected Package       pkg;

    private Stage           markStage;

    protected void setUp() throws Exception {
        super.setUp();
        this.stageType = new ClassObjectType( Stage.class );
        this.edgeType = new ClassObjectType( Edge.class );
        this.integerEqualEvaluator = EvaluatorFactory.getEvaluator( Evaluator.INTEGER_TYPE,
                                                                    Evaluator.EQUAL );
        this.integerNotEqualEvaluator = EvaluatorFactory.getEvaluator( Evaluator.INTEGER_TYPE,
                                                                       Evaluator.NOT_EQUAL );
        this.pkg = new Package( "or" );
    }

    /*
     */
    public void testLeapsNeverGetsToConsequenceOrder() throws Exception {

        this.pkg.addRule( this.getNeverGetsToConsequenceRule() );

        final org.drools.leaps.LeapsRuleBase ruleBase = (LeapsRuleBase) RuleBaseFactory.newRuleBase( RuleBase.LEAPS );
        ruleBase.addPackage( this.pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.assertObject( new Stage( Stage.LABELING ) );
        workingMemory.assertObject( new Edge( 1000,
                                              1,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.assertObject( new Edge( 1000,
                                              2,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.assertObject( new Edge( 5555,
                                              3,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.fireAllRules();
        workingMemory.assertObject( new Stage( Stage.DETECT_JUNCTIONS ) );

        workingMemory.fireAllRules();
    }

    /*
     */
    public void testReteooNeverGetsToConsequenceOrder() throws Exception {

        this.pkg.addRule( this.getNeverGetsToConsequenceRule() );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO );
        ruleBase.addPackage( this.pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.assertObject( new Stage( Stage.LABELING ) );
        workingMemory.assertObject( new Edge( 1000,
                                              1,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.assertObject( new Edge( 1000,
                                              2,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.assertObject( new Edge( 5555,
                                              3,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.fireAllRules();
        workingMemory.assertObject( new Stage( Stage.DETECT_JUNCTIONS ) );

        workingMemory.fireAllRules();
    }

    /*
     */
    public void testLeapsWorkingButCanNotResolveOrObjectInConsequenceOrder() throws Exception {

        this.pkg.addRule( this.getWorkingButCanNotResolveOrObjectInConsequenceOrder() );

        final org.drools.leaps.LeapsRuleBase ruleBase =(LeapsRuleBase) RuleBaseFactory.newRuleBase( RuleBase.LEAPS );
        ruleBase.addPackage( this.pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.assertObject( new Stage( Stage.LABELING ) );
        workingMemory.assertObject( new Edge( 1000,
                                              1,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.assertObject( new Edge( 1000,
                                              2,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.assertObject( new Edge( 5555,
                                              3,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.fireAllRules();
        workingMemory.assertObject( new Stage( Stage.DETECT_JUNCTIONS ) );

        workingMemory.fireAllRules();
    }

    /*
     */
    public void testReteooWorkingButCanNotResolveOrObjectInConsequenceOrder() throws Exception {

        this.pkg.addRule( this.getWorkingButCanNotResolveOrObjectInConsequenceOrder() );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO );
        ruleBase.addPackage( this.pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.assertObject( new Stage( Stage.LABELING ) );
        workingMemory.assertObject( new Edge( 1000,
                                              1,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.assertObject( new Edge( 1000,
                                              2,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.assertObject( new Edge( 5555,
                                              3,
                                              false,
                                              Edge.NIL,
                                              Edge.NIL ) );
        workingMemory.fireAllRules();
        workingMemory.assertObject( new Stage( Stage.DETECT_JUNCTIONS ) );

        workingMemory.fireAllRules();
    }

    /*
     * Test method for
     * 'org.drools.leaps.ColumnConstraints.evaluateAlphas(FactHandleImpl, Token,
     * WorkingMemoryImpl)'
     */
    public Rule getNeverGetsToConsequenceRule() throws Exception {
        final Rule rule = new Rule( "NeverGetsToConsequence" );

        final Column stageColumn1 = new Column( 0,
                                                this.stageType,
                                                "stage" );
        stageColumn1.addConstraint( getLiteralConstraint( stageColumn1,
                                                          "value",
                                                          new Integer( Stage.DETECT_JUNCTIONS ),
                                                          this.integerEqualEvaluator ) );

        final Column stageColumn2 = new Column( 0,
                                                this.stageType,
                                                "stage" );
        stageColumn2.addConstraint( getLiteralConstraint( stageColumn2,
                                                          "value",
                                                          new Integer( Stage.LABELING ),
                                                          this.integerEqualEvaluator ) );
        final Or or = new Or();
        or.addChild( stageColumn1 );
        or.addChild( stageColumn2 );
        rule.addPattern( or );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Column edgeColumn1 = new Column( 1,
                                               this.edgeType,
                                               "edge1" );
        setFieldDeclaration( edgeColumn1,
                             "p1",
                             "edge1p1" );
        setFieldDeclaration( edgeColumn1,
                             "p2",
                             "edge1p2" );
        rule.addPattern( edgeColumn1 );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        final Declaration edge1P1Declaration = rule.getDeclaration( "edge1p1" );
        final Declaration edge1P2Declaration = rule.getDeclaration( "edge1p2" );

        final Column edgeColumn2 = new Column( 2,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               edge1P1Declaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -9201168516267126280L;

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Stage stage = (Stage) drools.get( stageDeclaration );

                    OrTest.this.markStage = stage;
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }

        };
        rule.setConsequence( consequence );

        return rule;
    }

    /*
     * Test method for
     * 'org.drools.leaps.ColumnConstraints.evaluateAlphas(FactHandleImpl, Token,
     * WorkingMemoryImpl)'
     */
    public Rule getWorkingButCanNotResolveOrObjectInConsequenceOrder() throws Exception {
        final Rule rule = new Rule( "WorkingButCanNotResolveOrObjectInConsequence" );

        final Column edgeColumn1 = new Column( 0,
                                               this.edgeType,
                                               "edge1" );
        setFieldDeclaration( edgeColumn1,
                             "p1",
                             "edge1p1" );
        setFieldDeclaration( edgeColumn1,
                             "p2",
                             "edge1p2" );
        rule.addPattern( edgeColumn1 );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        final Declaration edge1P1Declaration = rule.getDeclaration( "edge1p1" );
        final Declaration edge1P2Declaration = rule.getDeclaration( "edge1p2" );

        final Column edgeColumn2 = new Column( 1,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               edge1P1Declaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Column stageColumn1 = new Column( 2,
                                                this.stageType,
                                                "stage1" );
        stageColumn1.addConstraint( getLiteralConstraint( stageColumn1,
                                                          "value",
                                                          new Integer( Stage.DETECT_JUNCTIONS ),
                                                          this.integerEqualEvaluator ) );

        final Column stageColumn2 = new Column( 2,
                                                this.stageType,
                                                "stage" );
        stageColumn2.addConstraint( getLiteralConstraint( stageColumn2,
                                                          "value",
                                                          new Integer( Stage.LABELING ),
                                                          this.integerEqualEvaluator ) );
        final Or or = new Or();
        or.addChild( stageColumn1 );
        or.addChild( stageColumn2 );
        rule.addPattern( or );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -4956304333289545872L;

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Stage stage = (Stage) drools.get( stageDeclaration );
                    OrTest.this.markStage = stage;
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }

        };
        rule.setConsequence( consequence );

        return rule;
    }

    private void setFieldDeclaration(final Column column,
                                     final String fieldName,
                                     final String identifier) throws IntrospectionException {
        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                                  fieldName );

        column.addDeclaration( identifier,
                               extractor );
    }

    private FieldConstraint getLiteralConstraint(final Column column,
                                                 final String fieldName,
                                                 final Object fieldValue,
                                                 final Evaluator evaluator) throws IntrospectionException {
        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final int index = getIndex( clazz,
                                    fieldName );

        final FieldValue field = new MockField( fieldValue );

        final FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                                  fieldName );

        return new LiteralConstraint( extractor,
                                      evaluator,
                                      field );
    }

    private FieldConstraint getBoundVariableConstraint(final Column column,
                                                       final String fieldName,
                                                       final Declaration declaration,
                                                       final Evaluator evaluator) throws IntrospectionException {
        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = new ClassFieldExtractor( clazz,
                                                                  fieldName );

        return new VariableConstraint( extractor,
                                            declaration,
                                            evaluator );
    }

    public static int getIndex(final Class clazz,
                               final String name) throws IntrospectionException {
        final PropertyDescriptor[] descriptors = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0; i < descriptors.length; i++ ) {
            if ( descriptors[i].getName().equals( name ) ) {
                return i;
            }
        }
        return -1;
    }

}