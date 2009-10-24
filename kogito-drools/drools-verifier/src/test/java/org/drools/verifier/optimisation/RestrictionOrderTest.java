package org.drools.verifier.optimisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.base.evaluators.Operator;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RuleComponent;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

public class RestrictionOrderTest extends TestBase {

    public void testRestrictionOrderInsideOperator() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "RestrictionOrder.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Optimise restrictions inside operator" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection< ? extends Object> testData = getTestData( this.getClass().getResourceAsStream( "OptimisationRestrictionOrderTest.drl" ),
                                                              result.getVerifierData() );

        session.setGlobal( "result",
                           result );

        session.executeWithResults( testData );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.NOTE ).iterator();

        Collection<String> ruleNames = new ArrayList<String>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof VerifierMessage ) {
                String name = ((VerifierMessage) o).getCauses().toArray( new Restriction[2] )[0].getRuleName();

                ruleNames.add( name );
            }
        }

        assertTrue( ruleNames.remove( "Wrong descr order 1" ) );

        if ( !ruleNames.isEmpty() ) {
            for ( String string : ruleNames ) {
                fail( "Rule " + string + " caused an error." );
            }
        }
    }

    public void testRestrictionOrderInsideConstraint() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "RestrictionOrder.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Optimise restrictions inside constraint" ) );

        Collection<Object> testData = new ArrayList<Object>();

        /*
         * Case 1
         */
        Pattern pattern1 = new Pattern();
        testData.add( pattern1 );

        OperatorDescr parent1 = new OperatorDescr( OperatorDescr.Type.AND );

        LiteralRestriction r1 = new LiteralRestriction();
        r1.setPatternGuid( pattern1.getGuid() );
        r1.setParentGuid( parent1.getGuid() );
        r1.setOperator( Operator.GREATER );
        r1.setOrderNumber( 1 );
        testData.add( r1 );

        LiteralRestriction r2 = new LiteralRestriction();
        r2.setPatternGuid( pattern1.getGuid() );
        r2.setParentGuid( parent1.getGuid() );
        r2.setOperator( Operator.EQUAL );
        r2.setOrderNumber( 2 );
        testData.add( r2 );

        LiteralRestriction r3 = new LiteralRestriction();
        r3.setPatternGuid( pattern1.getGuid() );
        r3.setParentGuid( parent1.getGuid() );
        r3.setOperator( Operator.LESS );
        r3.setOrderNumber( 3 );
        testData.add( r3 );

        /*
         * Case 2
         */
        Pattern pattern2 = new Pattern();
        testData.add( pattern2 );

        OperatorDescr parent2 = new OperatorDescr( OperatorDescr.Type.OR );

        LiteralRestriction r4 = new LiteralRestriction();
        r4.setPatternGuid( pattern2.getGuid() );
        r4.setParentGuid( parent2.getGuid() );
        r4.setOperator( Operator.NOT_EQUAL );
        r4.setOrderNumber( 1 );
        testData.add( r4 );

        LiteralRestriction r5 = new LiteralRestriction();
        r5.setPatternGuid( pattern2.getGuid() );
        r5.setParentGuid( parent2.getGuid() );
        r5.setOperator( Operator.LESS_OR_EQUAL );
        r5.setOrderNumber( 2 );
        testData.add( r5 );

        LiteralRestriction r6 = new LiteralRestriction();
        r6.setPatternGuid( pattern2.getGuid() );
        r6.setParentGuid( parent2.getGuid() );
        r6.setOperator( Operator.NOT_EQUAL );
        r6.setOrderNumber( 3 );
        testData.add( r6 );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        session.executeWithResults( testData );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.NOTE ).iterator();

        Map<Cause, Cause> pairs = new HashMap<Cause, Cause>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof VerifierMessage ) {
                Cause left = ((VerifierMessage) o).getCauses().toArray( new Cause[2] )[0];
                Cause right = ((VerifierMessage) o).getCauses().toArray( new Cause[2] )[1];

                pairs.put( left,
                           right );
            }
        }

        // Check that case 1 is here.
        assertTrue( (pairs.containsKey( r1 ) && pairs.get( r1 ).equals( r2 )) || pairs.containsKey( r2 ) && pairs.get( r2 ).equals( r1 ) );

        // Check that case 2 is here.
        assertTrue( (pairs.containsKey( r4 ) && pairs.get( r4 ).equals( r5 )) || pairs.containsKey( r5 ) && pairs.get( r5 ).equals( r4 ) );

        // Check that there is only one pair.
        assertEquals( 2,
                      pairs.size() );
    }

    public void testPredicateOrderInsideOperator() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "RestrictionOrder.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Optimise predicates inside operator" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection< ? extends Object> testData = getTestData( this.getClass().getResourceAsStream( "OptimisationRestrictionOrderTest.drl" ),
                                                              result.getVerifierData() );

        session.setGlobal( "result",
                           result );

        session.executeWithResults( testData );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.NOTE ).iterator();

        Collection<String> ruleNames = new ArrayList<String>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof VerifierMessage ) {
                String name = ((VerifierMessage) o).getCauses().toArray( new RuleComponent[2] )[0].getRuleName();

                ruleNames.add( name );
            }
        }

        assertTrue( ruleNames.remove( "Wrong eval order 1" ) );

        if ( !ruleNames.isEmpty() ) {
            for ( String string : ruleNames ) {
                fail( "Rule " + string + " caused an error." );
            }
        }
    }
}
