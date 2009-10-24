package org.drools.verifier.subsumption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.base.evaluators.Operator;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Cause;

public class SubsumptantRestrictionsTest extends SubsumptionTestBase {

    public void testRestrictionRedundancyGreater() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find subsumptant restrictions, greater than" ) );

        Collection<Object> data = new ArrayList<Object>();

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        /*
         * Redundant restrictions
         */
        // Doubles
        Field field1 = new Field();

        LiteralRestriction lr1 = new LiteralRestriction();
        lr1.setOrderNumber( 0 );
        lr1.setFieldGuid( field1.getGuid() );
        lr1.setValue( "1.0" );
        lr1.setOperator( Operator.GREATER );

        LiteralRestriction lr2 = new LiteralRestriction();
        lr2.setOrderNumber( 1 );
        lr2.setFieldGuid( field1.getGuid() );
        lr2.setValue( "2.0" );
        lr2.setOperator( Operator.GREATER );

        // Integers
        Field field2 = new Field();

        LiteralRestriction lr3 = new LiteralRestriction();
        lr3.setOrderNumber( 0 );
        lr3.setFieldGuid( field2.getGuid() );
        lr3.setValue( "1" );
        lr3.setOperator( Operator.GREATER );

        LiteralRestriction lr4 = new LiteralRestriction();
        lr4.setOrderNumber( 1 );
        lr4.setFieldGuid( field2.getGuid() );
        lr4.setValue( "2" );
        lr4.setOperator( Operator.GREATER_OR_EQUAL );

        // Dates
        Field field3 = new Field();

        LiteralRestriction lr5 = new LiteralRestriction();
        lr5.setOrderNumber( 0 );
        lr5.setFieldGuid( field3.getGuid() );
        lr5.setValue( "10-dec-2005" );
        lr5.setOperator( Operator.GREATER );

        LiteralRestriction lr6 = new LiteralRestriction();
        lr6.setOrderNumber( 1 );
        lr6.setFieldGuid( field3.getGuid() );
        lr6.setValue( "10-dec-2008" );
        lr6.setOperator( Operator.EQUAL );

        data.add( lr1 );
        data.add( lr2 );
        data.add( lr3 );
        data.add( lr4 );
        data.add( lr5 );
        data.add( lr6 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createSubsumptionMap( sessionResult.iterateObjects() );

        assertTrue( TestBase.causeMapContains( map,
                                               lr1,
                                               lr2 ) );
        assertTrue( TestBase.causeMapContains( map,
                                               lr3,
                                               lr4 ) );
        assertTrue( TestBase.causeMapContains( map,
                                               lr5,
                                               lr6 ) );

        if ( !map.isEmpty() ) {
            fail( "More redundancies than was expected." );
        }
    }

    public void testRestrictionRedundancyLess() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find subsumptant restrictions, less than" ) );

        Collection<Object> data = new ArrayList<Object>();

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        /*
         * Redundant restrictions
         */
        // Doubles
        Field field1 = new Field();

        LiteralRestriction lr1 = new LiteralRestriction();
        lr1.setOrderNumber( 0 );
        lr1.setFieldGuid( field1.getGuid() );
        lr1.setValue( "2.0" );
        lr1.setOperator( Operator.LESS );

        LiteralRestriction lr2 = new LiteralRestriction();
        lr2.setOrderNumber( 1 );
        lr2.setFieldGuid( field1.getGuid() );
        lr2.setValue( "1.0" );
        lr2.setOperator( Operator.LESS );

        // Integers
        Field field2 = new Field();

        LiteralRestriction lr3 = new LiteralRestriction();
        lr3.setOrderNumber( 0 );
        lr3.setFieldGuid( field2.getGuid() );
        lr3.setValue( "2" );
        lr3.setOperator( Operator.LESS );

        LiteralRestriction lr4 = new LiteralRestriction();
        lr4.setOrderNumber( 1 );
        lr4.setFieldGuid( field2.getGuid() );
        lr4.setValue( "1" );
        lr4.setOperator( Operator.LESS_OR_EQUAL );

        // Dates
        Field field3 = new Field();

        LiteralRestriction lr5 = new LiteralRestriction();
        lr5.setOrderNumber( 0 );
        lr5.setFieldGuid( field3.getGuid() );
        lr5.setValue( "10-dec-2008" );
        lr5.setOperator( Operator.LESS );

        LiteralRestriction lr6 = new LiteralRestriction();
        lr6.setOrderNumber( 1 );
        lr6.setFieldGuid( field3.getGuid() );
        lr6.setValue( "10-dec-2005" );
        lr6.setOperator( Operator.EQUAL );

        data.add( lr1 );
        data.add( lr2 );
        data.add( lr3 );
        data.add( lr4 );
        data.add( lr5 );
        data.add( lr6 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createSubsumptionMap( sessionResult.iterateObjects() );

        assertTrue( TestBase.causeMapContains( map,
                                               lr1,
                                               lr2 ) );
        assertTrue( TestBase.causeMapContains( map,
                                               lr3,
                                               lr4 ) );
        assertTrue( TestBase.causeMapContains( map,
                                               lr5,
                                               lr6 ) );

        if ( !map.isEmpty() ) {
            fail( "More redundancies than was expected." );
        }
    }
}
