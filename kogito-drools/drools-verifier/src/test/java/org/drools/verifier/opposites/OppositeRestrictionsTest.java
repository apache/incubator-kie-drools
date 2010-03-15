package org.drools.verifier.opposites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.base.evaluators.MatchesEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.report.components.Cause;

public class OppositeRestrictionsTest extends OppositesBase {

    public void testLiteralRestrictionOpposite() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Opposite LiteralRestrictions" ) );

        Collection<Object> data = new ArrayList<Object>();

        LiteralRestriction r1 = new LiteralRestriction();
        r1.setFieldGuid( "0" );
        r1.setOperator( Operator.EQUAL );
        r1.setValue( "1" );

        LiteralRestriction r2 = new LiteralRestriction();
        r2.setFieldGuid( "0" );
        r2.setOperator( Operator.NOT_EQUAL );
        r2.setValue( "1" );

        LiteralRestriction r3 = new LiteralRestriction();
        r3.setFieldGuid( "0" );
        r3.setOperator( Operator.EQUAL );
        r3.setValue( "1.0" );

        LiteralRestriction r4 = new LiteralRestriction();
        r4.setFieldGuid( "0" );
        r4.setOperator( Operator.NOT_EQUAL );
        r4.setValue( "1.0" );

        LiteralRestriction r5 = new LiteralRestriction();
        r5.setFieldGuid( "0" );
        r5.setOperator( MatchesEvaluatorsDefinition.MATCHES );
        r5.setValue( "foo" );

        LiteralRestriction r6 = new LiteralRestriction();
        r6.setFieldGuid( "0" );
        r6.setOperator( MatchesEvaluatorsDefinition.NOT_MATCHES );
        r6.setValue( "foo" );

        data.add( r1 );
        data.add( r2 );
        data.add( r3 );
        data.add( r4 );
        data.add( r5 );
        data.add( r6 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createOppositesMap( VerifierComponentType.RESTRICTION,
                                                         sessionResult.iterateObjects() );

        assertTrue( (TestBase.causeMapContains( map,
                                                r1,
                                                r2 ) ^ TestBase.causeMapContains( map,
                                                                                  r2,
                                                                                  r1 )) );
        assertTrue( (TestBase.causeMapContains( map,
                                                r3,
                                                r4 ) ^ TestBase.causeMapContains( map,
                                                                                  r4,
                                                                                  r3 )) );
        assertTrue( (TestBase.causeMapContains( map,
                                                r5,
                                                r6 ) ^ TestBase.causeMapContains( map,
                                                                                  r6,
                                                                                  r5 )) );

        if ( !map.isEmpty() ) {
            fail( "More opposites than was expected." );
        }
    }

    public void testLiteralRestrictionOppositeWithRangesGreaterOrEqualAndLess() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Opposite LiteralRestrictions with ranges, greater or equal - less" ) );

        Collection<Object> data = new ArrayList<Object>();

        LiteralRestriction r1 = new LiteralRestriction();
        r1.setFieldGuid( "0" );
        r1.setOperator( Operator.GREATER_OR_EQUAL );
        r1.setValue( "1" );

        LiteralRestriction r2 = new LiteralRestriction();
        r2.setFieldGuid( "0" );
        r2.setOperator( Operator.LESS );
        r2.setValue( "1" );

        data.add( r1 );
        data.add( r2 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createOppositesMap( VerifierComponentType.RESTRICTION,
                                                         sessionResult.iterateObjects() );

        assertTrue( (TestBase.causeMapContains( map,
                                                r1,
                                                r2 ) ^ TestBase.causeMapContains( map,
                                                                                  r2,
                                                                                  r1 )) );

        if ( !map.isEmpty() ) {
            fail( "More opposites than was expected." );
        }
    }

    public void testLiteralRestrictionOppositeWithRangesGreaterAndLessOrEqual() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Opposite LiteralRestrictions with ranges, greater - less or equal" ) );

        Collection<Object> data = new ArrayList<Object>();

        LiteralRestriction r1 = new LiteralRestriction();
        r1.setFieldGuid( "0" );
        r1.setOperator( Operator.GREATER );
        r1.setValue( "1" );

        LiteralRestriction r2 = new LiteralRestriction();
        r2.setFieldGuid( "0" );
        r2.setOperator( Operator.LESS_OR_EQUAL );
        r2.setValue( "1" );

        data.add( r1 );
        data.add( r2 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createOppositesMap( VerifierComponentType.RESTRICTION,
                                                         sessionResult.iterateObjects() );

        assertTrue( (TestBase.causeMapContains( map,
                                                r1,
                                                r2 ) ^ TestBase.causeMapContains( map,
                                                                                  r2,
                                                                                  r1 )) );

        if ( !map.isEmpty() ) {
            fail( "More opposites than was expected." );
        }
    }

    public void testLiteralRestrictionOppositeWithRangesLessAndGreaterForIntsAndDates() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Opposite LiteralRestrictions with ranges, less - greater for ints and dates" ) );

        Collection<Object> data = new ArrayList<Object>();

        LiteralRestriction r1 = new LiteralRestriction();
        r1.setFieldGuid( "0" );
        r1.setOperator( Operator.GREATER );
        r1.setValue( "0" );

        LiteralRestriction r2 = new LiteralRestriction();
        r2.setFieldGuid( "0" );
        r2.setOperator( Operator.LESS );
        r2.setValue( "1" );

        data.add( r1 );
        data.add( r2 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createOppositesMap( VerifierComponentType.RESTRICTION,
                                                         sessionResult.iterateObjects() );

        assertTrue( (TestBase.causeMapContains( map,
                                                r1,
                                                r2 ) ^ TestBase.causeMapContains( map,
                                                                                  r2,
                                                                                  r1 )) );

        if ( !map.isEmpty() ) {
            fail( "More opposites than was expected." );
        }
    }

    public void testLiteralRestrictionOppositeWithRangesLessOrEqualAndGreaterOrEqualForIntsAndDates() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Opposite LiteralRestrictions with ranges, less or equal - greater or equal for ints and dates" ) );

        Collection<Object> data = new ArrayList<Object>();

        LiteralRestriction r1 = new LiteralRestriction();
        r1.setFieldGuid( "0" );
        r1.setOperator( Operator.GREATER_OR_EQUAL );
        r1.setValue( "1" );

        LiteralRestriction r2 = new LiteralRestriction();
        r2.setFieldGuid( "0" );
        r2.setOperator( Operator.LESS_OR_EQUAL );
        r2.setValue( "0" );

        data.add( r1 );
        data.add( r2 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createOppositesMap( VerifierComponentType.RESTRICTION,
                                                         sessionResult.iterateObjects() );

        assertTrue( (TestBase.causeMapContains( map,
                                                r1,
                                                r2 ) ^ TestBase.causeMapContains( map,
                                                                                  r2,
                                                                                  r1 )) );

        if ( !map.isEmpty() ) {
            fail( "More opposites than was expected." );
        }
    }

    public void testVariableRestrictionOpposite() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Opposite VariableRestrictions" ) );

        Collection<Object> data = new ArrayList<Object>();

        /*
         * Working pair
         */
        Variable variable1 = new Variable();
        variable1.setObjectTypeGuid( "1" );
        variable1.setObjectTypeType( VerifierComponentType.FIELD.getType() );

        VariableRestriction r1 = new VariableRestriction();
        r1.setPatternGuid( "0" );
        r1.setFieldGuid( "0" );
        r1.setOperator( Operator.GREATER_OR_EQUAL );
        r1.setVariable( variable1 );

        VariableRestriction r2 = new VariableRestriction();
        r1.setPatternGuid( "0" );
        r2.setFieldGuid( "0" );
        r2.setOperator( Operator.LESS );
        r2.setVariable( variable1 );

        String containsOperator = "contains";

        Variable variable2 = new Variable();
        variable2.setObjectTypeGuid( "2" );
        variable2.setObjectTypeType( VerifierComponentType.FIELD.getType() );

        VariableRestriction r3 = new VariableRestriction();
        r3.setPatternGuid( "1" );
        r3.setFieldGuid( "1" );
        r3.setOperator( Operator.determineOperator( containsOperator,
                                                    false ) );
        r3.setVariable( variable2 );

        VariableRestriction r4 = new VariableRestriction();
        r4.setPatternGuid( "1" );
        r4.setFieldGuid( "1" );
        r4.setOperator( Operator.determineOperator( containsOperator,
                                                    true ) );
        r4.setVariable( variable2 );

        /*
         * Pair that doesn't work.
         */
        Variable variable3 = new Variable();
        variable3.setObjectTypeGuid( "3" );
        variable3.setObjectTypeType( VerifierComponentType.FIELD.getType() );

        VariableRestriction r5 = new VariableRestriction();
        r5.setPatternGuid( "2" );
        r5.setFieldGuid( "1" );
        r5.setOperator( Operator.GREATER_OR_EQUAL );
        r5.setVariable( variable3 );

        VariableRestriction r6 = new VariableRestriction();
        r6.setPatternGuid( "2" );
        r6.setFieldGuid( "1" );
        r6.setOperator( Operator.EQUAL );
        r6.setVariable( variable3 );

        data.add( r1 );
        data.add( r2 );
        data.add( r3 );
        data.add( r4 );
        data.add( r5 );
        data.add( r6 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createOppositesMap( VerifierComponentType.RESTRICTION,
                                                         sessionResult.iterateObjects() );

        assertTrue( (TestBase.causeMapContains( map,
                                                r1,
                                                r2 ) ^ TestBase.causeMapContains( map,
                                                                                  r2,
                                                                                  r1 )) );
        assertTrue( (TestBase.causeMapContains( map,
                                                r3,
                                                r4 ) ^ TestBase.causeMapContains( map,
                                                                                  r4,
                                                                                  r3 )) );

        if ( !map.isEmpty() ) {
            fail( "More opposites than was expected." );
        }
    }
}
