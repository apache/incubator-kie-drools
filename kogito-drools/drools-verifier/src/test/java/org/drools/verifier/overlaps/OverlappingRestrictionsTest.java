package org.drools.verifier.overlaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.base.evaluators.Operator;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.RuleComponent;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.Subsumption;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

public class OverlappingRestrictionsTest extends TestBase {

    public void testOverlap() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Restrictions.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find overlapping restrictions" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();

        Collection<Object> data = new ArrayList<Object>();

        /*
         * There restrictions overlap
         */
        String ruleName1 = "Rule 1";

        Field field1 = new Field();

        LiteralRestriction lr1 = new LiteralRestriction();
        lr1.setRuleName( ruleName1 );
        lr1.setFieldGuid( field1.getGuid() );
        lr1.setValue( "1.0" );
        lr1.setOperator( Operator.GREATER );

        LiteralRestriction lr2 = new LiteralRestriction();
        lr2.setRuleName( ruleName1 );
        lr2.setFieldGuid( field1.getGuid() );
        lr2.setValue( "2.0" );
        lr2.setOperator( Operator.GREATER );

        Subsumption s1 = new Subsumption( lr1,
                                          lr2 );

        /*
         * There restrictions do not overlap
         */
        String ruleName2 = "Rule 2";

        Field field2 = new Field();

        LiteralRestriction lr3 = new LiteralRestriction();
        lr3.setRuleName( ruleName2 );
        lr3.setFieldGuid( field2.getGuid() );
        lr3.setValue( "1.0" );
        lr3.setOperator( Operator.GREATER );

        LiteralRestriction lr4 = new LiteralRestriction();
        lr4.setRuleName( ruleName2 );
        lr4.setFieldGuid( field2.getGuid() );
        lr4.setValue( "2.0" );
        lr4.setOperator( Operator.GREATER );

        LiteralRestriction lr5 = new LiteralRestriction();
        lr5.setRuleName( ruleName2 );
        lr5.setFieldGuid( field2.getGuid() );
        lr5.setValue( "1.5" );
        lr5.setOperator( Operator.LESS );

        Subsumption s2 = new Subsumption( lr3,
                                          lr4 );

        data.add( lr1 );
        data.add( lr2 );
        data.add( lr3 );
        data.add( lr4 );
        data.add( lr5 );
        data.add( s1 );
        data.add( s2 );

        session.setGlobal( "result",
                           result );

        session.executeWithResults( data );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.NOTE ).iterator();

        Collection<String> ruleNames = new ArrayList<String>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof VerifierMessage ) {
                String name = ((VerifierMessage) o).getCauses().toArray( new RuleComponent[2] )[0].getRuleName();

                ruleNames.add( name );
            }
        }

        assertTrue( ruleNames.remove( ruleName1 ) );
        assertFalse( ruleNames.remove( ruleName2 ) );

        if ( !ruleNames.isEmpty() ) {
            for ( String string : ruleNames ) {
                fail( "Rule " + string + " caused an error." );
            }
        }
    }
}
