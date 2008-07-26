package org.drools.verifier.alwaysFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternPossibility;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RulePossibility;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.dao.VerifierResultFactory;
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

        VerifierResult result = VerifierResultFactory.createVerifierResult();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        // This pattern has an error.
        VerifierRule rule1 = new VerifierRule();
        Pattern pattern1 = new Pattern();
        pattern1.setRuleId( rule1.getId() );

        Restriction r1 = new LiteralRestriction();
        Restriction r2 = new LiteralRestriction();
        Incompatibility i1 = new Incompatibility( r1,
                                                  r2 );
        PatternPossibility pp1 = new PatternPossibility();
        pp1.setPatternId( pattern1.getId() );
        pp1.add( r1 );
        pp1.add( r2 );

        Restriction r3 = new VariableRestriction();
        Restriction r4 = new VariableRestriction();
        Incompatibility i2 = new Incompatibility( r1,
                                                  r2 );
        PatternPossibility pp2 = new PatternPossibility();
        pp2.setPatternId( pattern1.getId() );
        pp2.add( r1 );
        pp2.add( r2 );

        // This pattern does not have an error.
        Pattern pattern2 = new Pattern();
        pattern2.setRuleId( rule1.getId() );

        Restriction r5 = new LiteralRestriction();
        Restriction r6 = new LiteralRestriction();
        PatternPossibility pp3 = new PatternPossibility();
        pp3.setPatternId( pattern2.getId() );
        pp3.add( r5 );
        pp3.add( r6 );

        Restriction r7 = new VariableRestriction();
        Restriction r8 = new VariableRestriction();
        Incompatibility i4 = new Incompatibility( r7,
                                                  r8 );
        PatternPossibility pp4 = new PatternPossibility();
        pp4.setPatternId( pattern2.getId() );
        pp4.add( r7 );
        pp4.add( r8 );

        data.add( rule1 );

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

        VerifierResult result = VerifierResultFactory.createVerifierResult();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        // This pattern has an error.
        VerifierRule rule1 = new VerifierRule();
        Pattern pattern1 = new Pattern();
        pattern1.setRuleId( rule1.getId() );

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

        VerifierResult result = VerifierResultFactory.createVerifierResult();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        // This rule has an error.
        VerifierRule rule1 = new VerifierRule();

        PatternPossibility pp1 = new PatternPossibility();
        PatternPossibility pp2 = new PatternPossibility();
        Incompatibility i1 = new Incompatibility( pp1,
                                                  pp2 );
        RulePossibility rp1 = new RulePossibility();
        rp1.setRuleId( rule1.getId() );
        rp1.add( pp1 );
        rp1.add( pp2 );

        PatternPossibility pp3 = new PatternPossibility();
        PatternPossibility pp4 = new PatternPossibility();
        Incompatibility i2 = new Incompatibility( pp1,
                                                  pp2 );
        RulePossibility rp2 = new RulePossibility();
        rp2.setRuleId( rule1.getId() );
        rp2.add( pp1 );
        rp2.add( pp2 );

        // This pattern does not have an error.
        VerifierRule rule2 = new VerifierRule();

        PatternPossibility pp5 = new PatternPossibility();
        PatternPossibility pp6 = new PatternPossibility();
        RulePossibility rp3 = new RulePossibility();
        rp3.setRuleId( rule2.getId() );
        rp3.add( pp5 );
        rp3.add( pp6 );

        PatternPossibility pp7 = new PatternPossibility();
        PatternPossibility pp8 = new PatternPossibility();
        Incompatibility i4 = new Incompatibility( pp7,
                                                  pp8 );
        RulePossibility rp4 = new RulePossibility();
        rp4.setRuleId( rule2.getId() );
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