/*
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.incompatibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.base.evaluators.Operator;
import org.drools.verifier.TestBaseOld;
import org.junit.Test;
import static org.junit.Assert.*;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.report.components.Cause;

public class IncompatibilityRestrictionsTest extends IncompatibilityBase {

    @Test
    public void testLiteralRestrictionsIncompatibilityLessOrEqual() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Incompatible LiteralRestrictions with ranges in pattern possibility, impossible equality less or equal" ) );

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        /*
         * Working pair
         */
        LiteralRestriction r1 = LiteralRestriction.createRestriction( pattern1,
                                                                      "10" );
        r1.setOperator( Operator.EQUAL );
        r1.setFieldPath( "0" );
        r1.setOrderNumber( 0 );

        LiteralRestriction r2 = LiteralRestriction.createRestriction( pattern1,
                                                                      "1" );
        r2.setOperator( Operator.LESS );
        r2.setFieldPath( "0" );
        r2.setOrderNumber( 2 );

        /*
         * Pair that doesn't work.
         */
        LiteralRestriction r3 = LiteralRestriction.createRestriction( pattern2,
                                                                      "1" );
        r3.setOperator( Operator.GREATER_OR_EQUAL );
        r3.setFieldPath( "1" );
        r3.setOrderNumber( 0 );

        LiteralRestriction r4 = LiteralRestriction.createRestriction( pattern2,
                                                                      "10" );
        r4.setOperator( Operator.EQUAL );
        r4.setFieldPath( "1" );
        r4.setOrderNumber( 1 );

        data.add( r1 );
        data.add( r2 );
        data.add( r3 );
        data.add( r4 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createIncompatibilityMap( VerifierComponentType.RESTRICTION,
                                                               sessionResult.iterateObjects() );

        assertTrue( (TestBaseOld.causeMapContains(map,
                r1,
                r2) ^ TestBaseOld.causeMapContains(map,
                r2,
                r1)) );

        if ( !map.isEmpty() ) {
            fail( "More incompatibilities than was expected." );
        }
    }

    @Test
    public void testLiteralRestrictionsIncompatibilityGreater() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Incompatible LiteralRestrictions with ranges in pattern possibility, impossible equality greater" ) );

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        /*
         * Working pair
         */
        LiteralRestriction r1 = LiteralRestriction.createRestriction( pattern1,
                                                                      "10" );
        r1.setOperator( Operator.GREATER );
        r1.setFieldPath( "0" );
        r1.setOrderNumber( 0 );

        LiteralRestriction r2 = LiteralRestriction.createRestriction( pattern1,
                                                                      "1" );
        r2.setOperator( Operator.EQUAL );
        r2.setFieldPath( "0" );
        r2.setOrderNumber( 1 );

        /*
         * Pair that doesn't work.
         */
        LiteralRestriction r3 = LiteralRestriction.createRestriction( pattern2,
                                                                      "1" );
        r3.setOperator( Operator.GREATER_OR_EQUAL );
        r3.setFieldPath( "1" );
        r3.setOrderNumber( 0 );

        LiteralRestriction r4 = LiteralRestriction.createRestriction( pattern2,
                                                                      "10" );
        r4.setOperator( Operator.EQUAL );
        r4.setFieldPath( "1" );
        r4.setOrderNumber( 1 );

        data.add( r1 );
        data.add( r2 );
        data.add( r3 );
        data.add( r4 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createIncompatibilityMap( VerifierComponentType.RESTRICTION,
                                                               sessionResult.iterateObjects() );

        assertTrue( (TestBaseOld.causeMapContains(map,
                r1,
                r2) ^ TestBaseOld.causeMapContains(map,
                r2,
                r1)) );

        if ( !map.isEmpty() ) {
            fail( "More incompatibilities than was expected." );
        }
    }

    @Test
    public void testLiteralRestrictionsIncompatibilityImpossibleRange() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Incompatible LiteralRestrictions with ranges in pattern possibility, impossible range" ) );

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        /*
         * Working pair
         */
        LiteralRestriction r1 = LiteralRestriction.createRestriction( pattern1,
                                                                      "10" );
        r1.setOperator( Operator.GREATER );
        r1.setFieldPath( "0" );
        r1.setOrderNumber( 0 );

        LiteralRestriction r2 = LiteralRestriction.createRestriction( pattern1,
                                                                      "10" );
        r2.setOperator( Operator.LESS );
        r2.setFieldPath( "0" );
        r2.setOrderNumber( 1 );

        /*
         * Pair that doesn't work.
         */
        LiteralRestriction r3 = LiteralRestriction.createRestriction( pattern2,
                                                                      "1" );
        r3.setOperator( Operator.GREATER_OR_EQUAL );
        r3.setFieldPath( "1" );
        r3.setOrderNumber( 0 );

        LiteralRestriction r4 = LiteralRestriction.createRestriction( pattern2,
                                                                      "" );
        r4.setOperator( Operator.EQUAL );
        r4.setFieldPath( "1" );
        r4.setOrderNumber( 1 );

        data.add( r1 );
        data.add( r2 );
        data.add( r3 );
        data.add( r4 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createIncompatibilityMap( VerifierComponentType.RESTRICTION,
                                                               sessionResult.iterateObjects() );

        assertTrue( (TestBaseOld.causeMapContains(map,
                r1,
                r2) ^ TestBaseOld.causeMapContains(map,
                r2,
                r1)) );

        if ( !map.isEmpty() ) {
            fail( "More incompatibilities than was expected." );
        }
    }

    @Test
    public void testVariableRestrictionsIncompatibilityImpossibleRange() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Incoherent VariableRestrictions in pattern possibility, impossible range" ) );

        Collection<Object> data = new ArrayList<Object>();

        VerifierRule rule = VerifierComponentMockFactory.createRule1();

        ObjectType objectType = new ObjectType();
        objectType.setFullName( "org.test.Person" );

        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        /*
         * Working pair
         */
        Variable variable1 = new Variable( rule );
        variable1.setObjectTypePath( "0" );
        variable1.setObjectTypeType( VerifierComponentType.FIELD.getType() );
        variable1.setOrderNumber( 11 );

        VariableRestriction r1 = new VariableRestriction( pattern1 );
        r1.setOperator( Operator.GREATER );
        r1.setFieldPath( "0" );
        r1.setVariable( variable1 );
        r1.setOrderNumber( 0 );

        VariableRestriction r2 = new VariableRestriction( pattern1 );
        r2.setOperator( Operator.LESS );
        r2.setFieldPath( "0" );
        r2.setVariable( variable1 );
        r2.setOrderNumber( 1 );

        /*
         * Pair that doesn't work.
         */
        Variable variable2 = new Variable( rule );
        variable2.setObjectTypePath( "1" );
        variable2.setObjectTypeType( VerifierComponentType.FIELD.getType() );
        variable2.setOrderNumber( 10 );

        VariableRestriction r3 = new VariableRestriction( pattern2 );
        r3.setOperator( Operator.GREATER_OR_EQUAL );
        r3.setFieldPath( "1" );
        r3.setVariable( variable2 );
        r3.setOrderNumber( 0 );

        VariableRestriction r4 = new VariableRestriction( pattern2 );
        r4.setOperator( Operator.EQUAL );
        r4.setFieldPath( "1" );
        r4.setVariable( variable2 );
        r4.setOrderNumber( 1 );

        data.add( r1 );
        data.add( r2 );
        data.add( r3 );
        data.add( r4 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createIncompatibilityMap( VerifierComponentType.RESTRICTION,
                                                               sessionResult.iterateObjects() );

        assertTrue( (TestBaseOld.causeMapContains(map,
                r1,
                r2) ^ TestBaseOld.causeMapContains(map,
                r2,
                r1)) );

        if ( !map.isEmpty() ) {
            fail( "More incompatibilities than was expected." );
        }
    }
}
