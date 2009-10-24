package org.drools.verifier.optimisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.RuleComponent;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

public class PatternOrderTest extends TestBase {

    public void testEvalOrderInsideOperator() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "PatternOrder.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Optimise evals inside pattern" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection< ? extends Object> testData = getTestData( this.getClass().getResourceAsStream( "OptimisationPatternOrderTest.drl" ),
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
