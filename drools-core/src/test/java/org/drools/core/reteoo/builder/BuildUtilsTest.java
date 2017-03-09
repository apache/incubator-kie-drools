/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.builder;

import org.drools.core.base.ClassObjectType;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.AfterEvaluatorDefinition;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.test.model.StockTick;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.GroupElement.Type;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.constraint.EvaluatorConstraint;
import org.drools.core.time.Interval;
import org.drools.core.time.TemporalDependencyMatrix;
import org.junit.Before;
import org.junit.Test;

import static org.drools.core.time.Interval.MAX;
import static org.drools.core.time.Interval.MIN;
import static org.junit.Assert.assertEquals;

public class BuildUtilsTest {
    
    private BuildUtils utils;

    @Before
    public void setUp() throws Exception {
        utils = new BuildUtils();
    }

    /**
     * Test method for {@link org.drools.core.reteoo.builder.BuildUtils#calculateTemporalDistance(org.drools.core.rule.GroupElement)}.
     */
    @Test
    public void testCalculateTemporalDistance() {
        // input is here just for "documentation" purposes
        Interval[][] input = new Interval[][] {
                                                { new Interval(0,0), new Interval(-2,2), new Interval(-3, 4), new Interval(MIN, MAX), new Interval(MIN, MAX) },
                                                { new Interval(-2,2), new Interval(0,0), new Interval(MIN, MAX), new Interval(1,2), new Interval(MIN, MAX) },
                                                { new Interval(-4,3), new Interval(MIN,MAX), new Interval(0, 0), new Interval(2, 3), new Interval(MIN, MAX) },
                                                { new Interval(MIN,MAX), new Interval(-2,-1), new Interval(-3, -2), new Interval(0, 0), new Interval(1, 10) },
                                                { new Interval(MIN,MAX), new Interval(MIN,MAX), new Interval(MIN,MAX), new Interval(-10, -1), new Interval(0,0) }
                                        };
        Interval[][] expected = new Interval[][] {
                                                { new Interval(0,0), new Interval(-2,2), new Interval(-3, 2), new Interval(-1, 4), new Interval(0, 14) },
                                                { new Interval(-2,2), new Interval(0,0), new Interval(-2, 0), new Interval(1,2), new Interval(2, 12) },
                                                { new Interval(-2,3), new Interval(0,2), new Interval(0, 0), new Interval(2, 3), new Interval(3, 13) },
                                                { new Interval(-4,1), new Interval(-2,-1), new Interval(-3, -2), new Interval(0, 0), new Interval(1, 10) },
                                                { new Interval(-14,0), new Interval(-12,-2), new Interval(-13,-3), new Interval(-10, -1), new Interval(0,0) }
                                        };

        AfterEvaluatorDefinition evals = new AfterEvaluatorDefinition();
        ClassObjectType ot = new ClassObjectType(StockTick.class, true);
        
        Pattern a = new Pattern( 0, ot, "$a" );
        Pattern b = new Pattern( 1, ot, "$b" );

        b.addConstraint( new EvaluatorConstraint( new Declaration[] { a.getDeclaration() },
                                                  evals.getEvaluator( ValueType.OBJECT_TYPE,
                                                                      AfterEvaluatorDefinition.AFTER,
                                                                      "-2,2"),
                                                  new SelfReferenceClassFieldReader( StockTick.class ) ) );

        Pattern c = new Pattern( 2, ot, "$c" );
        c.addConstraint( new EvaluatorConstraint( new Declaration[] { a.getDeclaration() },
                                                  evals.getEvaluator( ValueType.OBJECT_TYPE,
                                                                      AfterEvaluatorDefinition.AFTER,
                                                                      "-3,4"),
                                                  new SelfReferenceClassFieldReader( StockTick.class ) ) );

        Pattern d = new Pattern( 3, ot, "$d" );
        d.addConstraint( new EvaluatorConstraint( new Declaration[] { b.getDeclaration() },
                                                  evals.getEvaluator( ValueType.OBJECT_TYPE,
                                                                      AfterEvaluatorDefinition.AFTER,
                                                                      "1,2"),
                                                  new SelfReferenceClassFieldReader( StockTick.class ) ) );

        d.addConstraint( new EvaluatorConstraint( new Declaration[] { c.getDeclaration() },
                                                  evals.getEvaluator( ValueType.OBJECT_TYPE,
                                                                      AfterEvaluatorDefinition.AFTER,
                                                                      "2,3"),
                                                  new SelfReferenceClassFieldReader( StockTick.class ) ) );

        Pattern e = new Pattern( 4, ot, "$e" );
        e.addConstraint(new EvaluatorConstraint(new Declaration[]{d.getDeclaration()},
                evals.getEvaluator(ValueType.OBJECT_TYPE,
                        AfterEvaluatorDefinition.AFTER,
                        "1,10"),
                new SelfReferenceClassFieldReader(StockTick.class)));

        GroupElement not = new GroupElement( Type.NOT );
        not.addChild( e );
        GroupElement and = new GroupElement( Type.AND );
        and.addChild( a );
        and.addChild( b );
        and.addChild( c );
        and.addChild( d );
        and.addChild( not );
        
        TemporalDependencyMatrix matrix = utils.calculateTemporalDistance( and );
        //printMatrix( matrix.getMatrix() );
        assertEqualsMatrix( expected, matrix.getMatrix() );
        
        assertEquals( 15, matrix.getExpirationOffset( a ) );
        assertEquals( 11, matrix.getExpirationOffset( d ) );
        assertEquals( 1, matrix.getExpirationOffset( e ) );
        
    }

    public void assertEqualsMatrix( Interval[][] expected, Interval[][] matrix ) {
        for( int i = 0; i < matrix.length; i++ ) {
            for( int j = 0; j < matrix[i].length; j++ ) {
                assertEquals( "Wrong value at ("+i+", "+j, expected[i][j], matrix[i][j] );
            }
        }
    }

    public void printMatrix( Interval[][] matrix ) {
        System.out.println("------------------------------------------------------------------");
        for( int i = 0; i < matrix.length; i++ ) {
            System.out.print("|  ");
            for( int j = 0; j < matrix[i].length; j++ ) {
                System.out.print( matrix[i][j] + "  ");
            }
            System.out.println("|");
        }
        System.out.println("------------------------------------------------------------------");
    }
    
}
