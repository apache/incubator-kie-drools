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
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.report.components.Cause;

public class OppositeRestrictionsTest extends OppositesBase {

    public void testLiteralRestrictionOpposite() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Opposite LiteralRestrictions" ) );

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction r1 = LiteralRestriction.createRestriction( pattern,
                                                                      "1" );
        r1.setFieldPath( "0" );
        r1.setOperator( Operator.EQUAL );
        r1.setOrderNumber( 0 );

        LiteralRestriction r2 = LiteralRestriction.createRestriction( pattern,
                                                                      "1" );
        r2.setFieldPath( "0" );
        r2.setOperator( Operator.NOT_EQUAL );
        r2.setOrderNumber( 1 );

        LiteralRestriction r3 = LiteralRestriction.createRestriction( pattern,
                                                                      "1.0" );
        r3.setFieldPath( "0" );
        r3.setOperator( Operator.EQUAL );
        r3.setOrderNumber( 2 );

        LiteralRestriction r4 = LiteralRestriction.createRestriction( pattern,
                                                                      "1.0" );
        r4.setFieldPath( "0" );
        r4.setOperator( Operator.NOT_EQUAL );
        r4.setOrderNumber( 3 );

        LiteralRestriction r5 = LiteralRestriction.createRestriction( pattern,
                                                                      "foo" );
        r5.setFieldPath( "0" );
        r5.setOperator( MatchesEvaluatorsDefinition.MATCHES );
        r5.setOrderNumber( 4 );

        LiteralRestriction r6 = LiteralRestriction.createRestriction( pattern,
                                                                      "foo" );
        r6.setFieldPath( "0" );
        r6.setOperator( MatchesEvaluatorsDefinition.NOT_MATCHES );
        r6.setOrderNumber( 5 );

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

        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction r1 = LiteralRestriction.createRestriction( pattern,
                                                                      "1" );
        r1.setFieldPath( "0" );
        r1.setOperator( Operator.GREATER_OR_EQUAL );
        r1.setOrderNumber( 0 );

        LiteralRestriction r2 = LiteralRestriction.createRestriction( pattern,
                                                                      "1" );
        r2.setFieldPath( "0" );
        r2.setOperator( Operator.LESS );
        r2.setOrderNumber( 1 );

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

        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction r1 = LiteralRestriction.createRestriction( pattern,
                                                                      "1" );
        r1.setFieldPath( "0" );
        r1.setOperator( Operator.GREATER );
        r1.setOrderNumber( 0 );

        LiteralRestriction r2 = LiteralRestriction.createRestriction( pattern,
                                                                      "1" );
        r2.setFieldPath( "0" );
        r2.setOperator( Operator.LESS_OR_EQUAL );
        r2.setOrderNumber( 1 );

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

        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction r1 = LiteralRestriction.createRestriction( pattern,
                                                                      "0" );
        r1.setFieldPath( "0" );
        r1.setOperator( Operator.GREATER );
        r1.setOrderNumber( 0 );

        LiteralRestriction r2 = LiteralRestriction.createRestriction( pattern,
                                                                      "1" );
        r2.setFieldPath( "0" );
        r2.setOperator( Operator.LESS );
        r2.setOrderNumber( 1 );

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

        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        LiteralRestriction r1 = LiteralRestriction.createRestriction( pattern,
                                                                      "1" );
        r1.setFieldPath( "0" );
        r1.setOperator( Operator.GREATER_OR_EQUAL );
        r1.setOrderNumber( 0 );

        LiteralRestriction r2 = LiteralRestriction.createRestriction( pattern,
                                                                      "0" );
        r2.setFieldPath( "0" );
        r2.setOperator( Operator.LESS_OR_EQUAL );
        r2.setOrderNumber( 1 );

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

        VerifierRule rule = VerifierComponentMockFactory.createRule1();

        Pattern pattern1 = VerifierComponentMockFactory.createPattern( 1 );
        Pattern pattern2 = VerifierComponentMockFactory.createPattern( 2 );
        Pattern pattern3 = VerifierComponentMockFactory.createPattern( 3 );

        /*
         * Working pair
         */
        Variable variable1 = new Variable( rule );
        variable1.setObjectTypePath( "1" );
        variable1.setObjectTypeType( VerifierComponentType.FIELD.getType() );
        variable1.setOrderNumber( -1 );

        VariableRestriction r1 = new VariableRestriction( pattern1 );
        r1.setFieldPath( "0" );
        r1.setOperator( Operator.GREATER_OR_EQUAL );
        r1.setVariable( variable1 );
        r1.setOrderNumber( 0 );

        VariableRestriction r2 = new VariableRestriction( pattern1 );
        r2.setFieldPath( "0" );
        r2.setOperator( Operator.LESS );
        r2.setVariable( variable1 );
        r2.setOrderNumber( 1 );

        String containsOperator = "contains";

        Variable variable2 = new Variable( rule );
        variable2.setObjectTypePath( "2" );
        variable2.setObjectTypeType( VerifierComponentType.FIELD.getType() );
        variable2.setOrderNumber( 3 );

        VariableRestriction r3 = new VariableRestriction( pattern2 );
        r3.setFieldPath( "1" );
        r3.setOperator( Operator.determineOperator( containsOperator,
                                                    false ) );
        r3.setVariable( variable2 );
        r3.setOrderNumber( 4 );

        VariableRestriction r4 = new VariableRestriction( pattern2 );
        r4.setFieldPath( "1" );
        r4.setOperator( Operator.determineOperator( containsOperator,
                                                    true ) );
        r4.setVariable( variable2 );
        r4.setOrderNumber( 5 );

        /*
         * Pair that doesn't work.
         */
        Variable variable3 = new Variable( rule );
        variable3.setObjectTypePath( "3" );
        variable3.setObjectTypeType( VerifierComponentType.FIELD.getType() );
        variable3.setOrderNumber( 6 );

        VariableRestriction r5 = new VariableRestriction( pattern3 );
        r5.setFieldPath( "1" );
        r5.setOperator( Operator.GREATER_OR_EQUAL );
        r5.setVariable( variable3 );
        r5.setOrderNumber( 7 );

        VariableRestriction r6 = new VariableRestriction( pattern3 );
        r6.setFieldPath( "1" );
        r6.setOperator( Operator.EQUAL );
        r6.setVariable( variable3 );
        r6.setOrderNumber( 8 );

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
