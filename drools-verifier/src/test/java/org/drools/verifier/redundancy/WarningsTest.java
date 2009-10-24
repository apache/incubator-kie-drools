package org.drools.verifier.redundancy;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.RedundancyType;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

public class WarningsTest extends TestBase {

    public void testRedundantRules() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Warnings.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find redundant rule possibilities from different rules" ) );

        Collection<Object> objects = new ArrayList<Object>();

        VerifierRule rule1 = new VerifierRule();
        VerifierRule rule2 = new VerifierRule();

        Redundancy ruleRedundancy = new Redundancy( RedundancyType.STRONG,
                                                    rule1,
                                                    rule2 );

        SubRule rp1 = new SubRule();
        rp1.setRuleGuid( rule1.getGuid() );

        SubRule rp2 = new SubRule();
        rp2.setRuleGuid( rule2.getGuid() );

        Redundancy rulePossibilityRedundancy1 = new Redundancy( RedundancyType.STRONG,
                                                                rp1,
                                                                rp2 );

        Redundancy rulePossibilityRedundancy2 = new Redundancy( RedundancyType.STRONG,
                                                                rp2,
                                                                rp1 );

        objects.add( rule1 );
        objects.add( rule2 );
        objects.add( ruleRedundancy );
        objects.add( rp1 );
        objects.add( rp2 );
        objects.add( rulePossibilityRedundancy1 );
        objects.add( rulePossibilityRedundancy2 );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        session.executeWithResults( objects );

        Collection<VerifierMessageBase> notes = result.getBySeverity( Severity.WARNING );

        // Has at least one item.
        assertEquals( 1,
                      notes.size() );

        VerifierMessageBase warning = notes.iterator().next();
        assertTrue( warning.getFaulty().equals( rulePossibilityRedundancy1 ) );
    }

    public void testSubsumptantRules() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Warnings.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find subsumptant rule possibilities from different rules" ) );

        Collection<Object> objects = new ArrayList<Object>();

        VerifierRule rule1 = new VerifierRule();
        VerifierRule rule2 = new VerifierRule();

        Redundancy ruleRedundancy = new Redundancy( RedundancyType.STRONG,
                                                    rule1,
                                                    rule2 );

        SubRule rp1 = new SubRule();
        rp1.setRuleGuid( rule1.getGuid() );

        SubRule rp2 = new SubRule();
        rp2.setRuleGuid( rule2.getGuid() );

        Redundancy rulePossibilityRedundancy1 = new Redundancy( RedundancyType.STRONG,
                                                                rp1,
                                                                rp2 );

        objects.add( rule1 );
        objects.add( rule2 );
        objects.add( ruleRedundancy );
        objects.add( rp1 );
        objects.add( rp2 );
        objects.add( rulePossibilityRedundancy1 );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        session.executeWithResults( objects );

        Collection<VerifierMessageBase> notes = result.getBySeverity( Severity.WARNING );

        // Has at least one item.
        assertEquals( 1,
                      notes.size() );

        VerifierMessageBase warning = notes.iterator().next();
        assertTrue( warning.getFaulty().equals( rulePossibilityRedundancy1 ) );
    }
}
