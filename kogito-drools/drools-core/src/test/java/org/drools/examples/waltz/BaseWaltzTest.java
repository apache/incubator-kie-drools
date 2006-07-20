package org.drools.examples.waltz;

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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.DroolsTestCase;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.ClassObjectType;
import org.drools.base.EvaluatorFactory;
import org.drools.rule.VariableConstraint;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Or;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.MockField;
import org.drools.spi.Tuple;

/**
 * @author Alexander Bagerman
 * 
 */
public abstract class BaseWaltzTest extends DroolsTestCase {
    private ClassObjectType stageType;

    private ClassObjectType lineType;

    private ClassObjectType edgeType;

    private ClassObjectType junctionType;

    private Evaluator       objectEqualEvaluator;

    private Evaluator       objectNotEqualEvaluator;

    private Evaluator       integerEqualEvaluator;

    private Evaluator       integerNotEqualEvaluator;

    private Evaluator       integerGreaterEvaluator;

    private Evaluator       integerLessEvaluator;

    private Evaluator       booleanEqualEvaluator;

    private Evaluator       booleanNotEqualEvaluator;

    protected Package       pkg;

    protected void setUp() throws Exception {
        // types
        this.stageType = new ClassObjectType( Stage.class );
        this.lineType = new ClassObjectType( Line.class );
        this.edgeType = new ClassObjectType( Edge.class );
        this.junctionType = new ClassObjectType( Junction.class );
        // evaluators
        this.integerEqualEvaluator = EvaluatorFactory.getEvaluator( Evaluator.INTEGER_TYPE,
                                                                    Evaluator.EQUAL );
        this.integerNotEqualEvaluator = EvaluatorFactory.getEvaluator( Evaluator.INTEGER_TYPE,
                                                                       Evaluator.NOT_EQUAL );
        this.integerGreaterEvaluator = EvaluatorFactory.getEvaluator( Evaluator.INTEGER_TYPE,
                                                                      Evaluator.GREATER );
        this.integerLessEvaluator = EvaluatorFactory.getEvaluator( Evaluator.INTEGER_TYPE,
                                                                   Evaluator.LESS );

        this.objectEqualEvaluator = EvaluatorFactory.getEvaluator( Evaluator.OBJECT_TYPE,
                                                                   Evaluator.EQUAL );
        this.objectNotEqualEvaluator = EvaluatorFactory.getEvaluator( Evaluator.OBJECT_TYPE,
                                                                      Evaluator.NOT_EQUAL );

        this.booleanEqualEvaluator = EvaluatorFactory.getEvaluator( Evaluator.BOOLEAN_TYPE,
                                                                    Evaluator.EQUAL );
        this.booleanNotEqualEvaluator = EvaluatorFactory.getEvaluator( Evaluator.BOOLEAN_TYPE,
                                                                       Evaluator.NOT_EQUAL );

        // rules
        this.pkg = new Package( "Waltz" );
        this.pkg.addRule( getBeginRule() );
        this.pkg.addRule( getReverseEdgesRule() );
        this.pkg.addRule( getDoneReversingRule() );
        this.pkg.addRule( getMake3JunctionRule() );
        this.pkg.addRule( getMakeLRule() );
        this.pkg.addRule( getDoneDetectingRule() );
        this.pkg.addRule( getInitialBoundaryJunctionLRule() );
        this.pkg.addRule( getInitialBoundaryJunctionArrowRule() );
        this.pkg.addRule( getSecondBoundaryJunctionLRule() );
        this.pkg.addRule( getSecondBoundaryJunctionArrowRule() );
        this.pkg.addRule( getMatchEdgeRule() );
        this.pkg.addRule( getLabelLRule() );
        this.pkg.addRule( getLabelTeeARule() );
        this.pkg.addRule( getLabelTeeBRule() );
        this.pkg.addRule( getLabelFork1Rule() );
        this.pkg.addRule( getLabelFork2Rule() );
        this.pkg.addRule( getLabelFork3Rule() );
        this.pkg.addRule( getLabelFork4Rule() );
        this.pkg.addRule( getLabelArrow1ARule() );
        this.pkg.addRule( getLabelArrow1BRule() );
        this.pkg.addRule( getLabelArrow2ARule() );
        this.pkg.addRule( getLabelArrow2BRule() );
        this.pkg.addRule( getLabelArrow3ARule() );
        this.pkg.addRule( getLabelArrow3BRule() );
        this.pkg.addRule( getLabelArrow4ARule() );
        this.pkg.addRule( getLabelArrow4BRule() );
        this.pkg.addRule( getLabelArrow5ARule() );
        this.pkg.addRule( getLabelArrow5BRule() );
        this.pkg.addRule( getDoneLabelingRule() );
        this.pkg.addRule( getPlotRemainingRule() );
        this.pkg.addRule( getPlotBoudariesRule() );
        this.pkg.addRule( getDonePlotingRule() );
        this.pkg.addRule( getDoneRule() );

    }

    private Rule getBeginRule() throws IntrospectionException,
                               InvalidRuleException {
        final Rule rule = new Rule( "begin" );
        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.START ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );
        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Stage stage = (Stage) drools.get( stageDeclaration );
                    stage.setValue( Stage.DUPLICATE );
                    drools.modifyObject( tuple.get( stageDeclaration ),
                                         stage );

                    drools.assertObject( new Line( 122,
                                                   107 ) );
                    drools.assertObject( new Line( 107,
                                                   2207 ) );
                    drools.assertObject( new Line( 2207,
                                                   3204 ) );
                    drools.assertObject( new Line( 3204,
                                                   6404 ) );
                    drools.assertObject( new Line( 2216,
                                                   2207 ) );
                    drools.assertObject( new Line( 3213,
                                                   3204 ) );
                    drools.assertObject( new Line( 2216,
                                                   3213 ) );
                    drools.assertObject( new Line( 107,
                                                   2601 ) );
                    drools.assertObject( new Line( 2601,
                                                   7401 ) );
                    drools.assertObject( new Line( 6404,
                                                   7401 ) );
                    drools.assertObject( new Line( 3213,
                                                   6413 ) );
                    drools.assertObject( new Line( 6413,
                                                   6404 ) );
                    drools.assertObject( new Line( 7416,
                                                   7401 ) );
                    drools.assertObject( new Line( 5216,
                                                   6413 ) );
                    drools.assertObject( new Line( 2216,
                                                   5216 ) );
                    drools.assertObject( new Line( 122,
                                                   5222 ) );
                    drools.assertObject( new Line( 5222,
                                                   7416 ) );
                    drools.assertObject( new Line( 5222,
                                                   5216 ) );
                    System.out.println( "Started waltz..." );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    // ;If the duplicate flag is set, and there is still a line in WM, delete
    // the line
    // ;and add two edges. One edge runs from p1 to p2 and the other runs from
    // p2 to
    // ;p1. We then plot the edge.
    // (p reverse_edges
    // (stage ^value duplicate)
    // (line ^p1 <p1> ^p2 <p2>)
    // -->
    // ; (write draw <p1> <p2> (crlf))
    // (make edge ^p1 <p1> ^p2 <p2> ^joined false)
    // (make edge ^p1 <p2> ^p2 <p1> ^joined false)
    // (remove 2))
    private Rule getReverseEdgesRule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "reverse_edges" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.DUPLICATE ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column lineColumn = new Column( 1,
                                              this.lineType,
                                              "line" );
        rule.addPattern( lineColumn );
        final Declaration lineDeclaration = rule.getDeclaration( "line" );

        final Consequence consequence = new Consequence() {

            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Line line = (Line) drools.get( lineDeclaration );
                    drools.assertObject( new Edge( line.getP1(),
                                                   line.getP2(),
                                                   false,
                                                   Edge.NIL,
                                                   Edge.NIL ) );
                    drools.assertObject( new Edge( line.getP2(),
                                                   line.getP1(),
                                                   false,
                                                   Edge.NIL,
                                                   Edge.NIL ) );
                    drools.retractObject( tuple.get( lineDeclaration ) );

                    System.out.println( "draw " + line.getP1() + " " + line.getP2() );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }

        };
        rule.setConsequence( consequence );
        return rule;
    }

    // ;If the duplicating flag is set, and there are no more lines, then remove
    // the
    // ;duplicating flag and set the make junctions flag.
    // (p done_reversing
    // (stage ^value duplicate)
    // - (line)
    // -->
    // (modify 1 ^value detect_junctions))
    private Rule getDoneReversingRule() throws IntrospectionException,
                                       InvalidRuleException {
        final Rule rule = new Rule( "done_reversing" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.DUPLICATE ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Column notLineColumn = new Column( 1,
                                                 this.lineType );
        final Not notLine = new Not();
        notLine.addChild( notLineColumn );
        rule.addPattern( notLine );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Stage stage = (Stage) drools.get( stageDeclaration );
                    stage.setValue( Stage.DETECT_JUNCTIONS );
                    drools.modifyObject( tuple.get( stageDeclaration ),
                                         stage );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    // ;If three edges meet at a point and none of them have already been joined
    // in
    // ;a junction, then make the corresponding type of junction and label the
    // ;edges joined. This production calls make-3_junction to determine
    // ;what type of junction it is based on the angles inscribed by the
    // ;intersecting edges
    // (p make-3_junction
    // (stage ^value detect_junctions)
    // (edge ^p1 <base_point> ^p2 <p1> ^joined false)
    // (edge ^p1 <base_point> ^p2 {<p2> <> <p1>} ^joined false)
    // (edge ^p1 <base_point> ^p2 {<p3> <> <p1> <> <p2>} ^joined false)
    // -->
    // (make junction
    // ^type (make_3_junction <base_point> <p1> <p2> <p3>)
    // ^base_point <base_point>)
    // (modify 2 ^joined true)
    // (modify 3 ^joined true)
    // (modify 4 ^joined true))
    private Rule getMake3JunctionRule() throws IntrospectionException,
                                       InvalidRuleException {
        final Rule rule = new Rule( "make-3_junction" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.DETECT_JUNCTIONS ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column edgeColumn1 = new Column( 1,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "joined",
                                                         new Boolean( false ),
                                                         this.booleanEqualEvaluator ) );
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
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "joined",
                                                         new Boolean( false ),
                                                         this.booleanEqualEvaluator ) );
        setFieldDeclaration( edgeColumn2,
                             "p2",
                             "edge2p2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        final Declaration edge2P2Declaration = rule.getDeclaration( "edge2p2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               edge1P1Declaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 3,
                                               this.edgeType,
                                               "edge3" );
        edgeColumn3.addConstraint( getLiteralConstraint( edgeColumn3,
                                                         "joined",
                                                         new Boolean( false ),
                                                         this.booleanEqualEvaluator ) );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               edge1P1Declaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge2P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    edge1.setJoined( true );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setJoined( true );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setJoined( true );

                    drools.assertObject( WaltzUtil.make_3_junction( edge1.getP1(),
                                                                    edge1.getP2(),
                                                                    edge2.getP2(),
                                                                    edge3.getP2() ) );

                    drools.modifyObject( tuple.get( edge1Declaration ),
                                         edge1 );
                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );

                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    // ;If two, and only two, edges meet that have not already been joined, then
    // ;the junction is an "L"
    // (p make_L
    // (stage ^value detect_junctions)
    // (edge ^p1 <base_point> ^p2 <p2> ^joined false)
    // (edge ^p1 <base_point> ^p2 {<p3> <> <p2>} ^joined false)
    // - (edge ^p1 <base_point> ^p2 {<> <p2> <> <p3>})
    // -->
    // (make junction
    // ^type L
    // ^base_point <base_point>
    // ^p1 <p2>
    // ^p2 <p3>)
    // (modify 2 ^joined true)
    // (modify 3 ^joined true))
    private Rule getMakeLRule() throws IntrospectionException,
                               InvalidRuleException {
        final Rule rule = new Rule( "make_L" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.DETECT_JUNCTIONS ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column edgeColumn1 = new Column( 1,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "joined",
                                                         new Boolean( false ),
                                                         this.booleanEqualEvaluator ) );
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
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "joined",
                                                         new Boolean( false ),
                                                         this.booleanEqualEvaluator ) );
        setFieldDeclaration( edgeColumn2,
                             "p2",
                             "edge2p2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        final Declaration edge2P2Declaration = rule.getDeclaration( "edge2p2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               edge1P1Declaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 3,
                                               this.edgeType,
                                               "edge3" );
        //      final Declaration edge3Declaration = rule.getDeclaration("edge3");
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               edge1P1Declaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge2P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Not notEdge = new Not();
        notEdge.addChild( edgeColumn3 );
        rule.addPattern( notEdge );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    edge1.setJoined( true );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setJoined( true );

                    drools.assertObject( new Junction( edge1.getP2(),
                                                       edge2.getP2(),
                                                       0,
                                                       edge1.getP1(),
                                                       Junction.L ) );
                    drools.modifyObject( tuple.get( edge1Declaration ),
                                         edge1 );
                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );

                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };

        rule.setConsequence( consequence );

        return rule;
    }

    // ;If the detect junctions flag is set, and there are no more un_joined
    // edges,
    // ;set the find_initial_boundary flag
    // (p done_detecting
    // (stage ^value detect_junctions)
    // - (edge ^joined false)
    // -->
    // (modify 1 ^value find_initial_boundary))
    //   
    private Rule getDoneDetectingRule() throws IntrospectionException,
                                       InvalidRuleException {
        final Rule rule = new Rule( "done_detecting" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.DETECT_JUNCTIONS ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Column notEdgeColumn = new Column( 1,
                                                 this.edgeType );
        notEdgeColumn.addConstraint( getLiteralConstraint( notEdgeColumn,
                                                           "joined",
                                                           new Boolean( false ),
                                                           this.booleanEqualEvaluator ) );
        final Not notEdge = new Not();
        notEdge.addChild( notEdgeColumn );
        rule.addPattern( notEdge );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Stage stage = (Stage) drools.get( stageDeclaration );
                    stage.setValue( Stage.FIND_INITIAL_BOUNDARY );
                    drools.modifyObject( tuple.get( stageDeclaration ),
                                         stage );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // ;If the initial boundary junction is an L, then we know it's labelling
    // (p initial_boundary_junction_L
    // (stage ^value find_initial_boundary)
    // (junction ^type L ^base_point <base_point> ^p1 <p1> ^p2 <p2>)
    // (edge ^p1 <base_point> ^p2 <p1>)
    // (edge ^p1 <base_point> ^p2 <p2>)
    // - (junction ^base_point > <base_point>)
    // -->
    // (modify 3 ^label B)
    // (modify 4 ^label B)
    // (modify 1 ^value find_second_boundary))
    private Rule getInitialBoundaryJunctionLRule() throws IntrospectionException,
                                                  InvalidRuleException {
        final Rule rule = new Rule( "initial_boundary_junction_L" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.FIND_INITIAL_BOUNDARY ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.L,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        rule.addPattern( edgeColumn1 );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column notJunctionColumn = new Column( 4,
                                                     this.junctionType );
        notJunctionColumn.addConstraint( getBoundVariableConstraint( notJunctionColumn,
                                                                     "basePoint",
                                                                     junctionBasePointDeclaration,
                                                                     this.integerGreaterEvaluator ) );
        final Not notJunction = new Not();
        notJunction.addChild( notJunctionColumn );
        rule.addPattern( notJunction );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    edge1.setLabel( Edge.B );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.B );
                    Stage stage = (Stage) drools.get( stageDeclaration );
                    stage.setValue( Stage.FIND_SECOND_BOUNDARY );

                    drools.modifyObject( tuple.get( stageDeclaration ),
                                         stage );
                    drools.modifyObject( tuple.get( edge1Declaration ),
                                         edge1 );
                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    // ;Ditto for an arrow
    // (p initial_boundary_junction_arrow
    // (stage ^value find_initial_boundary)
    // (junction ^type arrow ^base_point <bp> ^p1 <p1> ^p2 <p2> ^p3 <p3>)
    // (edge ^p1 <bp> ^p2 <p1>)
    // (edge ^p1 <bp> ^p2 <p2>)
    // (edge ^p1 <bp> ^p2 <p3>)
    // - (junction ^base_point > <bp>)
    // -->
    // (modify 3 ^label B)
    // (modify 4 ^label +)
    // (modify 5 ^label B)
    // (modify 1 ^value find_second_boundary))
    private Rule getInitialBoundaryJunctionArrowRule() throws IntrospectionException,
                                                      InvalidRuleException {
        final Rule rule = new Rule( "initial_boundary_junction_arrow" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.FIND_INITIAL_BOUNDARY ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        rule.addPattern( edgeColumn1 );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column notJunctionColumn = new Column( 5,
                                                     this.junctionType );
        notJunctionColumn.addConstraint( getBoundVariableConstraint( notJunctionColumn,
                                                                     "basePoint",
                                                                     junctionBasePointDeclaration,
                                                                     this.integerGreaterEvaluator ) );
        final Not notJunction = new Not();
        notJunction.addChild( notJunctionColumn );
        rule.addPattern( notJunction );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Stage stage = (Stage) drools.get( stageDeclaration );
                    stage.setValue( Stage.FIND_SECOND_BOUNDARY );
                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    edge1.setLabel( Edge.B );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.PLUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.B );

                    drools.modifyObject( tuple.get( stageDeclaration ),
                                         stage );
                    drools.modifyObject( tuple.get( edge1Declaration ),
                                         edge1 );
                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // ;If we have already found the first boundary point, then find the second
    // ;boundary point, and label it.
    //   
    // (defrule second_boundary_junction_L
    // ?f1 <- (stage (value find_second_boundary))
    // (junction (type L) (base_point ?base_point) (p1 ?p1) (p2 ?p2))
    // ?f3 <- (edge (p1 ?base_point) (p2 ?p1))
    // ?f4 <- (edge (p1 ?base_point) (p2 ?p2))
    // (not (junction (base_point ?bp&:(< ?bp ?base_point))))
    // =>
    // (modify ?f3 (label B))
    // (modify ?f4 (label B))
    // (modify ?f1 (value labeling)))
    private Rule getSecondBoundaryJunctionLRule() throws IntrospectionException,
                                                 InvalidRuleException {
        final Rule rule = new Rule( "second_boundary_junction_L" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.FIND_SECOND_BOUNDARY ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.L,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        rule.addPattern( edgeColumn1 );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column notJunctionColumn = new Column( 4,
                                                     this.junctionType );
        notJunctionColumn.addConstraint( getBoundVariableConstraint( notJunctionColumn,
                                                                     "basePoint",
                                                                     junctionBasePointDeclaration,
                                                                     this.integerLessEvaluator ) );
        final Not notJunction = new Not();
        notJunction.addChild( notJunctionColumn );
        rule.addPattern( notJunction );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    edge1.setLabel( Edge.B );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.B );
                    Stage stage = (Stage) drools.get( stageDeclaration );
                    stage.setValue( Stage.LABELING );

                    drools.modifyObject( tuple.get( stageDeclaration ),
                                         stage );
                    drools.modifyObject( tuple.get( edge1Declaration ),
                                         edge1 );
                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (defrule second_boundary_junction_arrow
    // ?f1 <- (stage (value find_second_boundary))
    // (junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
    // ?f3 <- (edge (p1 ?bp) (p2 ?p1))
    // ?f4 <- (edge (p1 ?bp) (p2 ?p2))
    // ?f5 <- (edge (p1 ?bp) (p2 ?p3))
    // (not (junction (base_point ?b&:(< ?b ?bp))))
    // =>
    // (modify ?f3 (label B))
    // (modify ?f4 (label +))
    // (modify ?f5 (label B))
    // (modify ?f1 (value labeling)))
    private Rule getSecondBoundaryJunctionArrowRule() throws IntrospectionException,
                                                     InvalidRuleException {
        final Rule rule = new Rule( "second_boundary_junction_arrow" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.FIND_INITIAL_BOUNDARY ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        rule.addPattern( edgeColumn1 );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column notJunctionColumn = new Column( 5,
                                                     this.junctionType );
        notJunctionColumn.addConstraint( getBoundVariableConstraint( notJunctionColumn,
                                                                     "basePoint",
                                                                     junctionBasePointDeclaration,
                                                                     this.integerLessEvaluator ) );
        final Not notJunction = new Not();
        notJunction.addChild( notJunctionColumn );
        rule.addPattern( notJunction );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    edge1.setLabel( Edge.B );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.PLUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.B );
                    Stage stage = (Stage) drools.get( stageDeclaration );
                    stage.setValue( Stage.LABELING );

                    drools.modifyObject( tuple.get( stageDeclaration ),
                                         stage );
                    drools.modifyObject( tuple.get( edge1Declaration ),
                                         edge1 );
                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    //   
    // ;If we have an edge whose label we already know definitely, then
    // ;label the corresponding edge in the other direction
    // (defrule match_edge
    // (stage (value labeling))
    // ?f2 <- (edge (p1 ?p1) (p2 ?p2) (label ?label& + | - | B ))
    // ?f3 <- (edge (p1 ?p2) (p2 ?p1) (label nil))
    // =>
    // (modify ?f2 (plotted t))
    // (modify ?f3 (label ?label) (plotted t))
    // ; (write plot ?label ?p1 ?p2 (crlf))
    // )
    private Rule getMatchEdgeRule() throws IntrospectionException,
                                   InvalidRuleException {
        final Rule rule = new Rule( "match_edge" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column edgeColumn1plus = new Column( 1,
                                                   this.edgeType,
                                                   "edge1" );
        edgeColumn1plus.addConstraint( getLiteralConstraint( edgeColumn1plus,
                                                             "label",
                                                             Edge.PLUS,
                                                             this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1plus,
                             "p1",
                             "edge1p1" );
        setFieldDeclaration( edgeColumn1plus,
                             "p2",
                             "edge1p2" );

        final Column edgeColumn1minus = new Column( 1,
                                                    this.edgeType,
                                                    "edge1" );
        edgeColumn1minus.addConstraint( getLiteralConstraint( edgeColumn1minus,
                                                              "label",
                                                              Edge.MINUS,
                                                              this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1minus,
                             "p1",
                             "edge1p1" );
        setFieldDeclaration( edgeColumn1minus,
                             "p2",
                             "edge1p2" );

        final Column edgeColumn1b = new Column( 1,
                                                this.edgeType,
                                                "edge1" );
        edgeColumn1b.addConstraint( getLiteralConstraint( edgeColumn1b,
                                                          "label",
                                                          Edge.B,
                                                          this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1b,
                             "p1",
                             "edge1p1" );
        setFieldDeclaration( edgeColumn1b,
                             "p2",
                             "edge1p2" );

        final Or or = new Or();
        or.addChild( edgeColumn1plus );
        or.addChild( edgeColumn1minus );
        or.addChild( edgeColumn1b );
        rule.addPattern( or );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        final Declaration edge1P1Declaration = rule.getDeclaration( "edge1p1" );
        final Declaration edge1P2Declaration = rule.getDeclaration( "edge1p2" );

        final Column edgeColumn2 = new Column( 2,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               edge1P2Declaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    edge1.setPlotted( Edge.TRUE );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( edge1.getLabel() );
                    edge2.setPlotted( Edge.TRUE );

                    drools.modifyObject( tuple.get( edge1Declaration ),
                                         edge1 );
                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );

                    System.out.println( "plot " + edge1.getLabel() + " " + edge1.getP1() + " " + edge1.getP2() );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    // ;The following productions propogate the possible labellings of the edges
    // ;based on the labellings of edges incident on adjacent junctions. Since
    // ;from the initial boundary productions, we have determined the labellings
    // of
    // ;of atleast two junctions, this propogation will label all of the
    // junctions
    // ;with the possible labellings. The search space is pruned due to
    // filtering,
    // ;i.e.(not only label a junction in the ways physically possible based on
    // the
    // ;labellings of adjacent junctions.
    //   
    //   
    // (defrule label_L
    // (stage (value labeling))
    // (junction (type L) (base_point ?p1))
    // (edge (p1 ?p1) (p2 ?p2) (label + | - ))
    // ?f4 <- (edge (p1 ?p1) (p2 ~?p2) (label nil))
    // =>
    // (modify ?f4 (label B)))
    private Rule getLabelLRule() throws IntrospectionException,
                                InvalidRuleException {
        final Rule rule = new Rule( "label_L" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.L,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );

        final Column edgeColumn1plus = new Column( 2,
                                                   this.edgeType,
                                                   "edge1" );
        edgeColumn1plus.addConstraint( getLiteralConstraint( edgeColumn1plus,
                                                             "label",
                                                             Edge.PLUS,
                                                             this.objectEqualEvaluator ) );
        edgeColumn1plus.addConstraint( getBoundVariableConstraint( edgeColumn1plus,
                                                                   "p1",
                                                                   junctionBasePointDeclaration,
                                                                   this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1plus,
                             "p2",
                             "edge1p2" );

        final Column edgeColumn1minus = new Column( 2,
                                                    this.edgeType,
                                                    "edge1" );
        edgeColumn1minus.addConstraint( getLiteralConstraint( edgeColumn1minus,
                                                              "label",
                                                              Edge.MINUS,
                                                              this.objectEqualEvaluator ) );
        edgeColumn1minus.addConstraint( getBoundVariableConstraint( edgeColumn1minus,
                                                                    "p1",
                                                                    junctionBasePointDeclaration,
                                                                    this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1minus,
                             "p2",
                             "edge1p2" );

        final Or or = new Or();
        or.addChild( edgeColumn1plus );
        or.addChild( edgeColumn1minus );
        rule.addPattern( or );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        final Declaration edge1P2Declaration = rule.getDeclaration( "edge1p2" );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.B );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    //   
    //  (p label_tee_A
    //          (stage ^value labeling)
    //          (junction ^type tee ^base_point <bp> ^p1 <p1> ^p2 <p2> ^p3 <p3>)
    //          (edge ^p1 <bp> ^p2 <p1> ^label nil)
    //          (edge ^p1 <bp> ^p2 <p3>)
    //          -->
    //          (modify 3 ^label B)
    //          (modify 4 ^label B))
    private Rule getLabelTeeARule() throws IntrospectionException,
                                   InvalidRuleException {
        final Rule rule = new Rule( "label_tee_A" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.TEE,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn1 );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    edge1.setLabel( Edge.B );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.B );

                    drools.modifyObject( tuple.get( edge1Declaration ),
                                         edge1 );
                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    //   
    // (defrule label_tee_B
    // (stage (value labeling))
    // (junction (type tee) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
    // ?f3 <- (edge (p1 ?bp) (p2 ?p1))
    // ?f4 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
    // =>
    // (modify ?f3 (label B))
    // (modify ?f4 (label B)))
    private Rule getLabelTeeBRule() throws IntrospectionException,
                                   InvalidRuleException {
        final Rule rule = new Rule( "label_tee_B" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.TEE,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        rule.addPattern( edgeColumn1 );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    edge1.setLabel( Edge.B );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.B );

                    drools.modifyObject( tuple.get( edge1Declaration ),
                                         edge1 );
                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (defrule label_fork-1
    // (stage (value labeling))
    // (junction (type fork) (base_point ?bp))
    // (edge (p1 ?bp) (p2 ?p1) (label +))
    // ?f4 <- (edge (p1 ?bp) (p2 ?p2&~?p1) (label nil))
    // ?f5 <- (edge (p1 ?bp) (p2 ~?p2 &~?p1))
    // =>
    // (modify ?f4 (label +))
    // (modify ?f5 (label +)))
    private Rule getLabelFork1Rule() throws IntrospectionException,
                                    InvalidRuleException {
        final Rule rule = new Rule( "label_fork-1" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.FORK,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.PLUS,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1,
                             "p2",
                             "edge1p2" );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        final Declaration edge1P2Declaration = rule.getDeclaration( "edge1p2" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn2,
                             "p2",
                             "edge2p2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        final Declaration edge2P2Declaration = rule.getDeclaration( "edge2p2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge2P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.PLUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.PLUS );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (defrule label_fork-2
    // (stage (value labeling))
    // (junction (type fork) (base_point ?bp))
    // (edge (p1 ?bp) (p2 ?p1) (label B))
    // (edge (p1 ?bp) (p2 ?p2&~?p1) (label - ))
    // ?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1) (label nil))
    // =>
    // (modify ?f5 (label B)))
    private Rule getLabelFork2Rule() throws IntrospectionException,
                                    InvalidRuleException {
        final Rule rule = new Rule( "label_fork-2" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.FORK,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.B,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1,
                             "p2",
                             "edge1p2" );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        final Declaration edge1P2Declaration = rule.getDeclaration( "edge1p2" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.MINUS,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn2,
                             "p2",
                             "edge2p2" );
        rule.addPattern( edgeColumn2 );
        //      final Declaration edge2Declaration = rule.getDeclaration("edge2");
        final Declaration edge2P2Declaration = rule.getDeclaration( "edge2p2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        edgeColumn3.addConstraint( getLiteralConstraint( edgeColumn3,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge2P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.B );

                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    //   
    // (defrule label_fork-3
    // (stage (value labeling))
    // (junction (type fork) (base_point ?bp))
    // (edge (p1 ?bp) (p2 ?p1) (label B))
    // (edge (p1 ?bp) (p2 ?p2&~?p1) (label B))
    // ?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1) (label nil))
    // =>
    // (modify ?f5 (label -)))
    private Rule getLabelFork3Rule() throws IntrospectionException,
                                    InvalidRuleException {
        final Rule rule = new Rule( "label_fork-3" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.FORK,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.B,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1,
                             "p2",
                             "edge1p2" );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        final Declaration edge1P2Declaration = rule.getDeclaration( "edge1p2" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.B,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn2,
                             "p2",
                             "edge2p2" );
        rule.addPattern( edgeColumn2 );
        //      final Declaration edge2Declaration = rule.getDeclaration("edge2");
        final Declaration edge2P2Declaration = rule.getDeclaration( "edge2p2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        edgeColumn3.addConstraint( getLiteralConstraint( edgeColumn3,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge2P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.MINUS );

                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (defrule label_fork-4
    // (stage (value labeling))
    // (junction (type fork) (base_point ?bp))
    // (edge (p1 ?bp) (p2 ?p1) (label -))
    // (edge (p1 ?bp) (p2 ?p2&~?p1) (label -))
    // ?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1) (label nil))
    // =>
    // (modify ?f5 (label -)))
    private Rule getLabelFork4Rule() throws IntrospectionException,
                                    InvalidRuleException {
        final Rule rule = new Rule( "label_fork-4" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.FORK,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.MINUS,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1,
                             "p2",
                             "edge1p2" );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        final Declaration edge1P2Declaration = rule.getDeclaration( "edge1p2" );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.MINUS,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn2,
                             "p2",
                             "edge2p2" );
        rule.addPattern( edgeColumn2 );
        //      final Declaration edge2Declaration = rule.getDeclaration("edge2");
        final Declaration edge2P2Declaration = rule.getDeclaration( "edge2p2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        edgeColumn3.addConstraint( getLiteralConstraint( edgeColumn3,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge1P2Declaration,
                                                               this.integerNotEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               edge2P2Declaration,
                                                               this.integerNotEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.MINUS );

                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (p label_arrow-1A
    // (stage ^value labeling)
    // (junction ^type arrow ^base_point <bp> ^p1 <p1> ^p2 <p2> ^p3 <p3>)
    // (edge ^p1 <bp> ^p2 <p1> ^label {<label> << B - >>})
    // (edge ^p1 <bp> ^p2 <p2> ^label nil)
    // (edge ^p1 <bp> ^p2 <p3>)
    // -->
    // (modify 4 ^label +)
    // (modify 5 ^label <label>))
    private Rule getLabelArrow1ARule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-1A" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1minus = new Column( 2,
                                                    this.edgeType,
                                                    "edge1" );
        edgeColumn1minus.addConstraint( getLiteralConstraint( edgeColumn1minus,
                                                              "label",
                                                              Edge.MINUS,
                                                              this.objectEqualEvaluator ) );
        edgeColumn1minus.addConstraint( getBoundVariableConstraint( edgeColumn1minus,
                                                                    "p1",
                                                                    junctionBasePointDeclaration,
                                                                    this.integerEqualEvaluator ) );
        edgeColumn1minus.addConstraint( getBoundVariableConstraint( edgeColumn1minus,
                                                                    "p2",
                                                                    junctionP1Declaration,
                                                                    this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1minus,
                             "label",
                             "edge1label" );

        final Column edgeColumn1b = new Column( 2,
                                                this.edgeType,
                                                "edge1" );
        edgeColumn1b.addConstraint( getLiteralConstraint( edgeColumn1b,
                                                          "label",
                                                          Edge.B,
                                                          this.objectEqualEvaluator ) );
        edgeColumn1b.addConstraint( getBoundVariableConstraint( edgeColumn1b,
                                                                "p1",
                                                                junctionBasePointDeclaration,
                                                                this.integerEqualEvaluator ) );
        edgeColumn1b.addConstraint( getBoundVariableConstraint( edgeColumn1b,
                                                                "p2",
                                                                junctionP1Declaration,
                                                                this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1b,
                             "label",
                             "edge1label" );

        final Or or = new Or();
        or.addChild( edgeColumn1minus );
        or.addChild( edgeColumn1b );
        rule.addPattern( or );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        //      final Declaration edge2P2Declaration = rule.getDeclaration("edge2p2");
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.PLUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( edge1.getLabel() );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (defrule label_arrow-1B
    // (stage (value labeling))
    // (junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
    // (edge (p1 ?bp) (p2 ?p1) (label ?label & B | - ))
    // ?f4 <- (edge (p1 ?bp) (p2 ?p2))
    // ?f5 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
    // =>
    // (modify ?f4 (label +))
    // (modify ?f5 (label ?label)))
    private Rule getLabelArrow1BRule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-1B" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1minus = new Column( 2,
                                                    this.edgeType,
                                                    "edge1" );
        edgeColumn1minus.addConstraint( getLiteralConstraint( edgeColumn1minus,
                                                              "label",
                                                              Edge.MINUS,
                                                              this.objectEqualEvaluator ) );
        edgeColumn1minus.addConstraint( getBoundVariableConstraint( edgeColumn1minus,
                                                                    "p1",
                                                                    junctionBasePointDeclaration,
                                                                    this.integerEqualEvaluator ) );
        edgeColumn1minus.addConstraint( getBoundVariableConstraint( edgeColumn1minus,
                                                                    "p2",
                                                                    junctionP1Declaration,
                                                                    this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1minus,
                             "label",
                             "edge1label" );

        final Column edgeColumn1b = new Column( 2,
                                                this.edgeType,
                                                "edge1" );
        edgeColumn1b.addConstraint( getLiteralConstraint( edgeColumn1b,
                                                          "label",
                                                          Edge.B,
                                                          this.objectEqualEvaluator ) );
        edgeColumn1b.addConstraint( getBoundVariableConstraint( edgeColumn1b,
                                                                "p1",
                                                                junctionBasePointDeclaration,
                                                                this.integerEqualEvaluator ) );
        edgeColumn1b.addConstraint( getBoundVariableConstraint( edgeColumn1b,
                                                                "p2",
                                                                junctionP1Declaration,
                                                                this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1b,
                             "label",
                             "edge1label" );

        final Or or = new Or();
        or.addChild( edgeColumn1minus );
        or.addChild( edgeColumn1b );
        rule.addPattern( or );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        edgeColumn3.addConstraint( getLiteralConstraint( edgeColumn3,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.PLUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( edge1.getLabel() );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (p label_arrow-2A
    // (stage ^value labeling)
    // (junction ^type arrow ^base_point <bp> ^p1 <p1> ^p2 <p2> ^p3 <p3>)
    // (edge ^p1 <bp> ^p2 <p3> ^label {<label> << B - >>})
    // (edge ^p1 <bp> ^p2 <p2> ^label nil)
    // (edge ^p1 <bp> ^p2 <p1>)
    // -->
    // (modify 4 ^label +)
    // (modify 5 ^label <label>))
    private Rule getLabelArrow2ARule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-2A" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1minus = new Column( 2,
                                                    this.edgeType,
                                                    "edge1" );
        edgeColumn1minus.addConstraint( getLiteralConstraint( edgeColumn1minus,
                                                              "label",
                                                              Edge.MINUS,
                                                              this.objectEqualEvaluator ) );
        edgeColumn1minus.addConstraint( getBoundVariableConstraint( edgeColumn1minus,
                                                                    "p1",
                                                                    junctionBasePointDeclaration,
                                                                    this.integerEqualEvaluator ) );
        edgeColumn1minus.addConstraint( getBoundVariableConstraint( edgeColumn1minus,
                                                                    "p2",
                                                                    junctionP3Declaration,
                                                                    this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1minus,
                             "label",
                             "edge1label" );

        final Column edgeColumn1b = new Column( 2,
                                                this.edgeType,
                                                "edge1" );
        edgeColumn1b.addConstraint( getLiteralConstraint( edgeColumn1b,
                                                          "label",
                                                          Edge.B,
                                                          this.objectEqualEvaluator ) );
        edgeColumn1b.addConstraint( getBoundVariableConstraint( edgeColumn1b,
                                                                "p1",
                                                                junctionBasePointDeclaration,
                                                                this.integerEqualEvaluator ) );
        edgeColumn1b.addConstraint( getBoundVariableConstraint( edgeColumn1b,
                                                                "p2",
                                                                junctionP3Declaration,
                                                                this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1b,
                             "label",
                             "edge1label" );

        final Or or = new Or();
        or.addChild( edgeColumn1minus );
        or.addChild( edgeColumn1b );
        rule.addPattern( or );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        //      final Declaration edge1LabelDeclaration = rule.getDeclaration("edge1label");

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.PLUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( edge1.getLabel() );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (defrule label_arrow-2B
    // (stage (value labeling))
    // (junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
    // (edge (p1 ?bp) (p2 ?p3) (label ?label & B | - ))
    // ?f4 <- (edge (p1 ?bp) (p2 ?p2))
    // ?f5 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
    // =>
    // (modify ?f4 (label +))
    // (modify ?f5 (label ?label)))
    private Rule getLabelArrow2BRule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-2B" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1minus = new Column( 2,
                                                    this.edgeType,
                                                    "edge1" );
        edgeColumn1minus.addConstraint( getLiteralConstraint( edgeColumn1minus,
                                                              "label",
                                                              Edge.MINUS,
                                                              this.objectEqualEvaluator ) );
        edgeColumn1minus.addConstraint( getBoundVariableConstraint( edgeColumn1minus,
                                                                    "p1",
                                                                    junctionBasePointDeclaration,
                                                                    this.integerEqualEvaluator ) );
        edgeColumn1minus.addConstraint( getBoundVariableConstraint( edgeColumn1minus,
                                                                    "p2",
                                                                    junctionP3Declaration,
                                                                    this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1minus,
                             "label",
                             "edge1label" );

        final Column edgeColumn1b = new Column( 2,
                                                this.edgeType,
                                                "edge1" );
        edgeColumn1b.addConstraint( getLiteralConstraint( edgeColumn1b,
                                                          "label",
                                                          Edge.B,
                                                          this.objectEqualEvaluator ) );
        edgeColumn1b.addConstraint( getBoundVariableConstraint( edgeColumn1b,
                                                                "p1",
                                                                junctionBasePointDeclaration,
                                                                this.integerEqualEvaluator ) );
        edgeColumn1b.addConstraint( getBoundVariableConstraint( edgeColumn1b,
                                                                "p2",
                                                                junctionP3Declaration,
                                                                this.integerEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1b,
                             "label",
                             "edge1label" );

        final Or or = new Or();
        or.addChild( edgeColumn1minus );
        or.addChild( edgeColumn1b );
        rule.addPattern( or );
        final Declaration edge1Declaration = rule.getDeclaration( "edge1" );
        //      final Declaration edge1LabelDeclaration = rule.getDeclaration("edge1label");

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        edgeColumn3.addConstraint( getLiteralConstraint( edgeColumn3,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge1 = (Edge) drools.get( edge1Declaration );
                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.PLUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( edge1.getLabel() );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (p label_arrow-3A
    // (stage ^value labeling)
    // (junction ^type arrow ^base_point <bp> ^p1 <p1> ^p2 <p2> ^p3 <p3>)
    // (edge ^p1 <bp> ^p2 <p1> ^label +)
    // (edge ^p1 <bp> ^p2 <p2> ^label nil)
    // (edge ^p1 <bp> ^p2 <p3>)
    // -->
    // (modify 4 ^label -)
    // (modify 5 ^label +))
    private Rule getLabelArrow3ARule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-3A" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.PLUS,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1,
                             "p2",
                             "edge1p2" );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.MINUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.PLUS );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (defrule label_arrow-3B
    // (stage (value labeling))
    // (junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
    // (edge (p1 ?bp) (p2 ?p1) (label +))
    // ?f4 <- (edge (p1 ?bp) (p2 ?p2))
    // ?f5 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
    // =>
    // (modify ?f4 (label -))
    // (modify ?f5 (label +)))
    private Rule getLabelArrow3BRule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-3B" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.PLUS,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        edgeColumn3.addConstraint( getLiteralConstraint( edgeColumn3,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.MINUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.PLUS );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (p label_arrow-4A
    // (stage ^value labeling)
    // (junction ^type arrow ^base_point <bp> ^p1 <p1> ^p2 <p2> ^p3 <p3>)
    // (edge ^p1 <bp> ^p2 <p3> ^label +)
    // (edge ^p1 <bp> ^p2 <p2> ^label nil)
    // (edge ^p1 <bp> ^p2 <p1>)
    // -->
    // (modify 4 ^label -)
    // (modify 5 ^label +))
    private Rule getLabelArrow4ARule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-4A" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.PLUS,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1,
                             "p2",
                             "edge1p2" );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.MINUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.PLUS );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (defrule label_arrow-4B
    // (stage (value labeling))
    // (junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
    // (edge (p1 ?bp) (p2 ?p3) (label +))
    // ?f4 <- (edge (p1 ?bp) (p2 ?p2))
    // ?f5 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
    // =>
    // (modify ?f4 (label -))
    // (modify ?f5 (label +)))
    private Rule getLabelArrow4BRule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-4B" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.PLUS,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        edgeColumn3.addConstraint( getLiteralConstraint( edgeColumn3,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.MINUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.PLUS );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // (p label_arrow-5A
    // (stage ^value labeling)
    // (junction ^type arrow ^base_point <bp> ^p1 <p1> ^p2 <p2> ^p3 <p3>)
    // (edge ^p1 <bp> ^p2 <p2> ^label -)
    // (edge ^p1 <bp> ^p2 <p1>)
    // (edge ^p1 <bp> ^p2 <p3> ^label nil)
    // -->
    // (modify 4 ^label +)
    // (modify 5 ^label +))
    private Rule getLabelArrow5ARule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-5A" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.MINUS,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn1,
                             "p2",
                             "edge1p2" );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        edgeColumn3.addConstraint( getLiteralConstraint( edgeColumn3,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.PLUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.PLUS );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    //   
    // (defrule label_arrow-5B
    // (stage (value labeling))
    // (junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
    // (edge (p1 ?bp) (p2 ?p2) (label -))
    // ?f4 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
    // ?f5 <- (edge (p1 ?bp) (p2 ?p3))
    // =>
    // (modify ?f4 (label +))
    // (modify ?f5 (label +)))
    //   
    //   
    private Rule getLabelArrow5BRule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "label_arrow-5B" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column junctionColumn = new Column( 1,
                                                  this.junctionType );
        junctionColumn.addConstraint( getLiteralConstraint( junctionColumn,
                                                            "type",
                                                            Junction.ARROW,
                                                            this.objectEqualEvaluator ) );
        setFieldDeclaration( junctionColumn,
                             "basePoint",
                             "junctionBasePoint" );
        setFieldDeclaration( junctionColumn,
                             "p1",
                             "junctionP1" );
        setFieldDeclaration( junctionColumn,
                             "p2",
                             "junctionP2" );
        setFieldDeclaration( junctionColumn,
                             "p3",
                             "junctionP3" );
        rule.addPattern( junctionColumn );
        final Declaration junctionBasePointDeclaration = rule.getDeclaration( "junctionBasePoint" );
        final Declaration junctionP1Declaration = rule.getDeclaration( "junctionP1" );
        final Declaration junctionP2Declaration = rule.getDeclaration( "junctionP2" );
        final Declaration junctionP3Declaration = rule.getDeclaration( "junctionP3" );

        final Column edgeColumn1 = new Column( 2,
                                               this.edgeType,
                                               "edge1" );
        edgeColumn1.addConstraint( getLiteralConstraint( edgeColumn1,
                                                         "label",
                                                         Edge.MINUS,
                                                         this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn1 );
        //      final Declaration edge1Declaration = rule.getDeclaration("edge1");
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn1.addConstraint( getBoundVariableConstraint( edgeColumn1,
                                                               "p2",
                                                               junctionP2Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn2 = new Column( 3,
                                               this.edgeType,
                                               "edge2" );
        edgeColumn2.addConstraint( getLiteralConstraint( edgeColumn2,
                                                         "label",
                                                         Edge.NIL,
                                                         this.objectEqualEvaluator ) );
        setFieldDeclaration( edgeColumn2,
                             "p2",
                             "edge2p2" );
        rule.addPattern( edgeColumn2 );
        final Declaration edge2Declaration = rule.getDeclaration( "edge2" );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn2.addConstraint( getBoundVariableConstraint( edgeColumn2,
                                                               "p2",
                                                               junctionP1Declaration,
                                                               this.integerEqualEvaluator ) );

        final Column edgeColumn3 = new Column( 4,
                                               this.edgeType,
                                               "edge3" );
        rule.addPattern( edgeColumn3 );
        final Declaration edge3Declaration = rule.getDeclaration( "edge3" );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p1",
                                                               junctionBasePointDeclaration,
                                                               this.integerEqualEvaluator ) );
        edgeColumn3.addConstraint( getBoundVariableConstraint( edgeColumn3,
                                                               "p2",
                                                               junctionP3Declaration,
                                                               this.integerEqualEvaluator ) );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge2 = (Edge) drools.get( edge2Declaration );
                    edge2.setLabel( Edge.PLUS );
                    Edge edge3 = (Edge) drools.get( edge3Declaration );
                    edge3.setLabel( Edge.PLUS );

                    drools.modifyObject( tuple.get( edge2Declaration ),
                                         edge2 );
                    drools.modifyObject( tuple.get( edge3Declaration ),
                                         edge3 );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    // ;The conflict resolution mechanism will onle execute a production if no
    // ;productions that are more complicated are satisfied. This production is
    // ;simple, so all of the above dictionary productions will fire before this
    // ;change of state production
    // (p done_labeling
    // (stage ^value labeling)
    // -->
    // (modify 1 ^value plot_remaining_edges))
    private Rule getDoneLabelingRule() throws IntrospectionException,
                                      InvalidRuleException {
        final Rule rule = new Rule( "done_labeling" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.LABELING ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Stage stage = (Stage) drools.get( stageDeclaration );
                    stage.setValue( Stage.PLOT_REMAINING_EDGES );
                    drools.modifyObject( tuple.get( stageDeclaration ),
                                         stage );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    // ;At this point, some labellings may have not been plotted, so plot them
    // (defrule plot_remaining
    // (stage (value plot_remaining_edges))
    // ?f2 <- (edge (plotted nil) (label ?label&~nil) (p1 ?p1) (p2 ?p2))
    // =>
    // ; (write plot ?label ?p1 ?p2 (crlf))
    // (modify ?f2 (plotted t)))
    private Rule getPlotRemainingRule() throws IntrospectionException,
                                       InvalidRuleException {
        final Rule rule = new Rule( "plot_remaining" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.PLOT_REMAINING_EDGES ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column edgeColumn = new Column( 1,
                                              this.edgeType,
                                              "edge" );
        edgeColumn.addConstraint( getLiteralConstraint( edgeColumn,
                                                        "plotted",
                                                        Edge.NIL,
                                                        this.objectEqualEvaluator ) );
        edgeColumn.addConstraint( getLiteralConstraint( edgeColumn,
                                                        "label",
                                                        Edge.NIL,
                                                        this.objectNotEqualEvaluator ) );
        rule.addPattern( edgeColumn );
        final Declaration edgeDeclaration = rule.getDeclaration( "edge" );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge = (Edge) drools.get( edgeDeclaration );
                    System.out.println( "plot " + edge.getLabel() + " " + edge.getP1() + " " + edge.getP2() );
                    edge.setPlotted( Edge.TRUE );
                    drools.modifyObject( tuple.get( edgeDeclaration ),
                                         edge );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    //   
    //   
    // ;If we have been un able to label an edge, assume that it is a boundary.
    // ;This is a total Kludge, but what the hell. (if we assume only valid
    // drawings
    // ;will be given for labeling, this assumption generally is true!)
    // (defrule plot_boundaries
    // (stage (value plot_remaining_edges))
    // ?f2 <- (edge (plotted nil) (label nil) (p1 ?p1) (p2 ?p2))
    // =>
    // ; (write plot B ?p1 ?p2 (crlf))
    // (modify ?f2 (plotted t)))
    //   
    private Rule getPlotBoudariesRule() throws IntrospectionException,
                                       InvalidRuleException {
        final Rule rule = new Rule( "plot_boundaries" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.PLOT_REMAINING_EDGES ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Column edgeColumn = new Column( 1,
                                              this.edgeType,
                                              "edge" );
        edgeColumn.addConstraint( getLiteralConstraint( edgeColumn,
                                                        "plotted",
                                                        Edge.NIL,
                                                        this.objectEqualEvaluator ) );
        edgeColumn.addConstraint( getLiteralConstraint( edgeColumn,
                                                        "label",
                                                        Edge.NIL,
                                                        this.objectEqualEvaluator ) );
        rule.addPattern( edgeColumn );
        final Declaration edgeDeclaration = rule.getDeclaration( "edge" );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Edge edge = (Edge) drools.get( edgeDeclaration );
                    System.out.println( "plot B " + edge.getP1() + " " + edge.getP2() );
                    edge.setPlotted( Edge.TRUE );
                    drools.modifyObject( tuple.get( edgeDeclaration ),
                                         edge );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    // ;If there is no more work to do, then we are done and flag it.
    // (p done_plotting
    // (stage ^value plot_remaining_edges)
    // - (edge ^plotted nil)
    // -->
    // (modify 1 ^value done))
    private Rule getDonePlotingRule() throws IntrospectionException,
                                     InvalidRuleException {
        final Rule rule = new Rule( "done_plotting" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.PLOT_REMAINING_EDGES ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        final Declaration stageDeclaration = rule.getDeclaration( "stage" );

        final Column notEdgeColumn = new Column( 1,
                                                 this.edgeType );
        notEdgeColumn.addConstraint( getLiteralConstraint( notEdgeColumn,
                                                           "plotted",
                                                           Edge.NIL,
                                                           this.objectEqualEvaluator ) );
        final Not notEdge = new Not();
        notEdge.addChild( notEdgeColumn );
        rule.addPattern( notEdge );

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    Rule rule = drools.getRule();
                    Tuple tuple = drools.getTuple();

                    Stage stage = (Stage) drools.get( stageDeclaration );
                    stage.setValue( Stage.DONE );
                    drools.modifyObject( tuple.get( stageDeclaration ),
                                         stage );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    // ;Prompt the user as to where he can see a trace of the OPS5
    // ;execution
    // (defrule done
    // (stage (value done))
    // =>
    // ; (write see trace.waltz for description of execution- hit CR to end
    // (crlf))
    // )
    //
    private Rule getDoneRule() throws IntrospectionException,
                              InvalidRuleException {
        final Rule rule = new Rule( "done" );

        final Column stageColumn = new Column( 0,
                                               this.stageType,
                                               "stage" );
        stageColumn.addConstraint( getLiteralConstraint( stageColumn,
                                                         "value",
                                                         new Integer( Stage.DONE ),
                                                         this.integerEqualEvaluator ) );
        rule.addPattern( stageColumn );
        //      final Declaration stageDeclaration = rule.getDeclaration("stage");

        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper drools,
                                 WorkingMemory workingMemory) throws ConsequenceException {
                try {
                    System.out.println( "done." );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new ConsequenceException( e );
                }
            }
        };
        rule.setConsequence( consequence );
        return rule;
    }

    /**
     * Convert the facts from the <code>InputStream</code> to a list of
     * objects.
     */
    protected List getInputObjects(final InputStream inputStream) throws IOException {
        final List list = new ArrayList();

        final BufferedReader br = new BufferedReader( new InputStreamReader( inputStream ) );

        String line;
        while ( (line = br.readLine()) != null ) {
            if ( line.trim().length() == 0 || line.trim().startsWith( ";" ) ) {
                continue;
            }
            final StringTokenizer st = new StringTokenizer( line,
                                                            "() " );
            final String type = st.nextToken();

            if ( "line".equals( type ) ) {
                if ( !"p1".equals( st.nextToken() ) ) {
                    throw new IOException( "expected 'p1' in: " + line );
                }
                final int p1 = Integer.parseInt( st.nextToken(),
                                                 10 );
                if ( !"p2".equals( st.nextToken() ) ) {
                    throw new IOException( "expected 'p2' in: " + line );
                }
                final int p2 = Integer.parseInt( st.nextToken(),
                                                 10 );

                list.add( new Line( p1,
                                    p2 ) );
            }

            if ( "stage".equals( type ) ) {
                if ( !"value".equals( st.nextToken() ) ) {
                    throw new IOException( "expected 'seat' in: " + line );
                }
                list.add( new Stage( Stage.resolveStageValue( st.nextToken() ) ) );
            }
        }
        inputStream.close();

        return list;
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

    private FieldConstraint getLiteralConstraint(final Column column,
                                                 final String fieldName,
                                                 final Object fieldValue,
                                                 final Evaluator evaluator) throws IntrospectionException {
        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = ClassFieldExtractorFactory.getClassFieldExtractor( clazz,
                                                                                            fieldName );

        final MockField field = new MockField( fieldValue );

        return new LiteralConstraint( extractor,
                                      evaluator,
                                      field );
    }

    private void setFieldDeclaration(final Column column,
                                     final String fieldName,
                                     final String identifier) throws IntrospectionException {
        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = ClassFieldExtractorFactory.getClassFieldExtractor( clazz,
                                                                                            fieldName );

        column.addDeclaration( identifier,
                               extractor );
    }

    private FieldConstraint getBoundVariableConstraint(final Column column,
                                                       final String fieldName,
                                                       final Declaration declaration,
                                                       final Evaluator evaluator) throws IntrospectionException {
        final Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        final FieldExtractor extractor = ClassFieldExtractorFactory.getClassFieldExtractor( clazz,
                                                                                            fieldName );

        return new VariableConstraint( extractor,
                                            declaration,
                                            evaluator );
    }
}