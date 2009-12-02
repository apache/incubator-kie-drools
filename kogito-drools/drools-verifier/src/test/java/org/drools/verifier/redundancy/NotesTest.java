package org.drools.verifier.redundancy;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

public class NotesTest extends TestBase {

    public void testRedundantRestrictionsInPatternPossibilities() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Notes.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find redundant restrictions from pattern possibilities" ) );

        Collection<Object> objects = new ArrayList<Object>();
        LiteralRestriction left = new LiteralRestriction();

        LiteralRestriction right = new LiteralRestriction();

        Redundancy redundancy = new Redundancy( left,
                                                right );

        SubPattern possibility = new SubPattern();
        possibility.add( left );
        possibility.add( right );

        objects.add( left );
        objects.add( right );
        objects.add( redundancy );
        objects.add( possibility );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        session.executeWithResults( objects );

        Collection<VerifierMessageBase> notes = result.getBySeverity( Severity.NOTE );

        // Has at least one item.
        assertEquals( 1,
                      notes.size() );

        VerifierMessageBase note = notes.iterator().next();
        assertTrue( note.getFaulty().equals( redundancy ) );
    }

    public void testRedundantPatternPossibilitiesInRulePossibilities() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Notes.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find redundant pattern possibilities from rule possibilities" ) );

        Collection<Object> objects = new ArrayList<Object>();
        SubPattern left = new SubPattern();

        SubPattern right = new SubPattern();

        Redundancy redundancy = new Redundancy( left,
                                                right );

        SubRule possibility = new SubRule();
        possibility.add( left );
        possibility.add( right );

        objects.add( left );
        objects.add( right );
        objects.add( redundancy );
        objects.add( possibility );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        session.executeWithResults( objects );

        Collection<VerifierMessageBase> notes = result.getBySeverity( Severity.NOTE );

        // Has at least one item.
        assertEquals( 1,
                      notes.size() );

        VerifierMessageBase note = notes.iterator().next();
        assertTrue( note.getFaulty().equals( redundancy ) );
    }
}
