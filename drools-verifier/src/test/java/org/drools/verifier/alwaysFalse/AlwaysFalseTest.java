package org.drools.verifier.alwaysFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Incompatibility;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class AlwaysFalseTest extends TestBase {

    public void testPatterns() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Patterns.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Pattern that is always false" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        // This pattern has an error.
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        Restriction r1 = new LiteralRestriction( pattern1 );
        Restriction r2 = new LiteralRestriction( pattern1 );
        Incompatibility i1 = new Incompatibility( r1,
                                                  r2 );
        SubPattern pp1 = new SubPattern( pattern1,
                                         0 );
        pp1.add( r1 );
        pp1.add( r2 );

        Restriction r3 = new VariableRestriction( pattern1 );
        Restriction r4 = new VariableRestriction( pattern1 );
        Incompatibility i2 = new Incompatibility( r1,
                                                  r2 );
        SubPattern pp2 = new SubPattern( pattern1,
                                         1 );
        pp2.add( r1 );
        pp2.add( r2 );

        // This pattern does not have an error.
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        Restriction r5 = new LiteralRestriction( pattern2 );
        Restriction r6 = new LiteralRestriction( pattern2 );
        SubPattern pp3 = new SubPattern( pattern2,
                                         0 );
        pp3.add( r5 );
        pp3.add( r6 );

        Restriction r7 = new VariableRestriction( pattern2 );
        Restriction r8 = new VariableRestriction( pattern2 );
        Incompatibility i4 = new Incompatibility( r7,
                                                  r8 );
        SubPattern pp4 = new SubPattern( pattern2,
                                         1 );
        pp4.add( r7 );
        pp4.add( r8 );

        data.add( VerifierComponentMockFactory.createRule1() );

        data.add( pattern1 );
        data.add( r1 );
        data.add( r2 );
        data.add( r3 );
        data.add( r4 );
        data.add( i1 );
        data.add( i2 );
        data.add( pp1 );
        data.add( pp2 );

        data.add( pattern2 );
        data.add( r5 );
        data.add( r6 );
        data.add( r7 );
        data.add( r8 );
        data.add( i4 );
        data.add( pp3 );
        data.add( pp4 );

        session.executeWithResults( data );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.ERROR ).iterator();

        boolean works = false;
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof VerifierMessage ) {
                VerifierMessage message = (VerifierMessage) o;
                if ( message.getFaulty().equals( pattern1 ) ) {
                    works = true;
                } else {
                    fail( "There can be only one. (And this is not the one)" );
                }
            }
        }

        assertEquals( 1,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.NOTE ).size() );
        assertTrue( works );
    }

    /**
     * 
     * rule "test"
     *     when
     *         TestPattern()
     *     then
     *         # Nothing
     * end   
     * 
     * Check that a pattern with out restrictions does not raise any notifications.
     * 
     * @throws Exception
     */
    public void testSinglePatternNoRestrictions() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Patterns.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Pattern that is always false" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        // This pattern has an error.
        VerifierRule rule1 = VerifierComponentMockFactory.createRule1();
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        data.add( rule1 );
        data.add( pattern1 );

        session.executeWithResults( data );

        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.NOTE ).size() );
    }

    public void testRules() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Rules.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Rule that is always false" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        // This rule has an error.
        VerifierRule rule1 = VerifierComponentMockFactory.createRule1();
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        SubPattern pp1 = new SubPattern( pattern1,
                                         0 );
        SubPattern pp2 = new SubPattern( pattern1,
                                         1 );
        Incompatibility i1 = new Incompatibility( pp1,
                                                  pp2 );
        SubRule rp1 = new SubRule( rule1,
                                   0 );
        rp1.add( pp1 );
        rp1.add( pp2 );

        SubPattern pp3 = new SubPattern( pattern1,
                                         2 );
        SubPattern pp4 = new SubPattern( pattern1,
                                         3 );
        Incompatibility i2 = new Incompatibility( pp1,
                                                  pp2 );
        SubRule rp2 = new SubRule( rule1,
                                   1 );
        rp2.add( pp1 );
        rp2.add( pp2 );

        // This pattern does not have an error.
        VerifierRule rule2 = VerifierComponentMockFactory.createRule2();
        Pattern pattern2= VerifierComponentMockFactory.createPattern2();

        SubPattern pp5 = new SubPattern( pattern2,
                                         0 );
        SubPattern pp6 = new SubPattern( pattern2,
                                         1 );
        SubRule rp3 = new SubRule( rule2,
                                   2 );
        rp3.add( pp5 );
        rp3.add( pp6 );

        SubPattern pp7 = new SubPattern( pattern2,
                                         2 );
        SubPattern pp8 = new SubPattern( pattern2,
                                         3 );
        Incompatibility i4 = new Incompatibility( pp7,
                                                  pp8 );
        SubRule rp4 = new SubRule( rule2,
                                   3 );
        rp4.add( pp7 );
        rp4.add( pp8 );

        data.add( rule1 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( pp3 );
        data.add( pp4 );
        data.add( i1 );
        data.add( i2 );
        data.add( rp1 );
        data.add( rp2 );

        data.add( rule2 );
        data.add( pp5 );
        data.add( pp6 );
        data.add( pp7 );
        data.add( pp8 );
        data.add( i4 );
        data.add( rp3 );
        data.add( rp4 );

        session.executeWithResults( data );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.ERROR ).iterator();

        boolean works = false;
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof VerifierMessage ) {
                VerifierMessage message = (VerifierMessage) o;
                if ( message.getFaulty().equals( rule1 ) ) {
                    works = true;
                } else {
                    fail( "There can be only one. (And this is not the one)" );
                }
            }
        }

        assertEquals( 1,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.NOTE ).size() );
        assertTrue( works );
    }
}