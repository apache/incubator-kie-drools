package org.drools.verifier.subsumption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.RedundancyType;
import org.drools.verifier.report.components.Subsumption;

public class SubsumptantPossibilitiesTest extends SubsumptionTestBase {

    public void testSubPatternRedundancy1() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Possibilities.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find subsumptant pattern possibilities" ) );

        Collection<Object> data = new ArrayList<Object>();

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        /*
         * Redundant patterns
         */
        String ruleName1 = "Rule 1";
        String ruleName2 = "Rule 2";

        Pattern p1 = new Pattern();
        p1.setRuleName( ruleName1 );
        Pattern p2 = new Pattern();
        p2.setRuleName( ruleName2 );

        LiteralRestriction lr1 = new LiteralRestriction();
        lr1.setRuleName( ruleName1 );
        lr1.setOrderNumber( 0 );
        LiteralRestriction lr2 = new LiteralRestriction();
        lr2.setRuleName( ruleName2 );
        lr2.setOrderNumber( 0 );
        LiteralRestriction lr3 = new LiteralRestriction();
        lr3.setRuleName( ruleName2 );
        lr3.setOrderNumber( 1 );

        SubPattern pp1 = new SubPattern();
        pp1.setPatternGuid( p1.getGuid() );
        pp1.setRuleName( ruleName1 );
        pp1.add( lr1 );

        SubPattern pp2 = new SubPattern();
        pp2.setPatternGuid( p2.getGuid() );
        pp2.setRuleName( ruleName2 );
        pp2.add( lr2 );
        pp2.add( lr3 );

        Redundancy r1 = new Redundancy( lr1,
                                        lr2 );
        Redundancy r2 = new Redundancy( p1,
                                        p2 );

        data.add( p1 );
        data.add( p2 );
        data.add( lr1 );
        data.add( lr2 );
        data.add( lr3 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( r1 );
        data.add( r2 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createSubsumptionMap( sessionResult.iterateObjects() );

        assertTrue( TestBase.causeMapContains( map,
                                               pp1,
                                               pp2 ) );

        if ( !map.isEmpty() ) {
            fail( "More redundancies than was expected." );
        }
    }

    public void testSubPatternRedundancy2() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Possibilities.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find subsumptant pattern possibilities" ) );

        Collection<Object> data = new ArrayList<Object>();

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        /*
         * Not redundant patterns
         * 
         * For example: Pattern ( a==1, b==1, c==1) and Pattern ( a==1, c==1)
         */
        String ruleName1 = "Rule 1";
        String ruleName2 = "Rule 2";

        Pattern p1 = new Pattern();
        p1.setRuleName( ruleName1 );
        Pattern p2 = new Pattern();
        p2.setRuleName( ruleName2 );

        LiteralRestriction lr1 = new LiteralRestriction();
        lr1.setRuleName( ruleName1 );
        lr1.setOrderNumber( 0 );
        LiteralRestriction lr2 = new LiteralRestriction();
        lr2.setRuleName( ruleName2 );
        lr2.setOrderNumber( 0 );
        LiteralRestriction lr3 = new LiteralRestriction();
        lr3.setRuleName( ruleName2 );
        lr3.setOrderNumber( 1 );

        SubPattern pp1 = new SubPattern();
        pp1.setPatternGuid( p1.getGuid() );
        pp1.setRuleName( ruleName1 );
        pp1.add( lr1 );

        SubPattern pp2 = new SubPattern();
        pp2.setPatternGuid( p2.getGuid() );
        pp2.setRuleName( ruleName2 );
        pp2.add( lr2 );
        pp2.add( lr3 );

        Redundancy r1 = new Redundancy( lr1,
                                        lr3 );
        Redundancy r2 = new Redundancy( p1,
                                        p2 );

        data.add( p1 );
        data.add( p2 );
        data.add( lr1 );
        data.add( lr2 );
        data.add( lr3 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( r1 );
        data.add( r2 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createSubsumptionMap( sessionResult.iterateObjects() );

        assertFalse( TestBase.causeMapContains( map,
                                                pp1,
                                                pp2 ) );

        if ( !map.isEmpty() ) {
            fail( "More redundancies than was expected." );
        }
    }

    public void testSubRuleRedundancy1() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Possibilities.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find subsumptant rule possibilities" ) );

        Collection<Object> data = new ArrayList<Object>();

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        /*
         * First rules. These are subsumptant,
         */
        String ruleName1 = "Rule 1";
        String ruleName2 = "Rule 2";

        VerifierRule r1 = new VerifierRule();
        r1.setRuleName( ruleName1 );
        VerifierRule r2 = new VerifierRule();
        r2.setRuleName( ruleName2 );

        SubPattern pp1 = new SubPattern();
        pp1.setRuleName( ruleName1 );
        SubPattern pp2 = new SubPattern();
        pp2.setRuleName( ruleName2 );
        SubPattern pp3 = new SubPattern();
        pp3.setRuleName( ruleName2 );

        SubRule rp1 = new SubRule();
        rp1.setRuleGuid( r1.getGuid() );
        rp1.setRuleName( ruleName1 );
        rp1.add( pp1 );

        SubRule rp2 = new SubRule();
        rp2.setRuleGuid( r2.getGuid() );
        rp2.setRuleName( ruleName2 );
        rp2.add( pp2 );
        rp2.add( pp3 );

        Redundancy possibilityredundancy = new Redundancy( RedundancyType.STRONG,
                                                           pp1,
                                                           pp2 );
        Redundancy ruleRedundancy = new Redundancy( r1,
                                                    r2 );

        data.add( r1 );
        data.add( r2 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( pp3 );
        data.add( possibilityredundancy );
        data.add( ruleRedundancy );
        data.add( rp1 );
        data.add( rp2 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createSubsumptionMap( sessionResult.iterateObjects() );

        assertTrue( TestBase.causeMapContains( map,
                                               rp1,
                                               rp2 ) );

        if ( !map.isEmpty() ) {
            fail( "More redundancies than was expected." );
        }
    }

    public void testSubRuleRedundancy2() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Possibilities.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find subsumptant rule possibilities" ) );
        //		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
        //				"XXX: test rule"));

        Collection<Object> data = new ArrayList<Object>();

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        /*
         * First rules. These are subsumptant,
         */
        String ruleName1 = "Rule 1";
        String ruleName2 = "Rule 2";

        VerifierRule r1 = new VerifierRule();
        r1.setRuleName( ruleName1 );
        VerifierRule r2 = new VerifierRule();
        r2.setRuleName( ruleName2 );

        SubPattern pp1 = new SubPattern();
        pp1.setRuleName( ruleName1 );
        SubPattern pp2 = new SubPattern();
        pp2.setRuleName( ruleName1 );
        SubPattern pp3 = new SubPattern();
        pp3.setRuleName( ruleName2 );
        SubPattern pp4 = new SubPattern();
        pp4.setRuleName( ruleName2 );

        SubRule rp1 = new SubRule();
        rp1.setRuleGuid( r1.getGuid() );
        rp1.setRuleName( ruleName1 );
        rp1.add( pp1 );
        rp1.add( pp2 );

        SubRule rp2 = new SubRule();
        rp2.setRuleGuid( r2.getGuid() );
        rp2.setRuleName( ruleName2 );
        rp2.add( pp3 );
        rp2.add( pp4 );

        Redundancy possibilityredundancy = new Redundancy( RedundancyType.STRONG,
                                                           pp1,
                                                           pp3 );
        Subsumption possibilitysubsupmtion = new Subsumption( pp2,
                                                              pp4 );
        Redundancy ruleRedundancy = new Redundancy( r1,
                                                    r2 );

        data.add( r1 );
        data.add( r2 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( pp3 );
        data.add( possibilityredundancy );
        data.add( possibilitysubsupmtion );
        data.add( ruleRedundancy );
        data.add( rp1 );
        data.add( rp2 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createSubsumptionMap( sessionResult.iterateObjects() );

        assertTrue( TestBase.causeMapContains( map,
                                               rp1,
                                               rp2 ) );
        assertTrue( TestBase.causeMapContains( map,
                                               pp2,
                                               pp4 ) );

        if ( !map.isEmpty() ) {
            fail( "More redundancies than was expected." );
        }
    }
}
