package org.drools.verifier.redundancy;

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
import org.drools.verifier.report.components.CauseType;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.RedundancyType;

public class RedundantPossibilitiesTest extends RedundancyTestBase {

    public void testSubPatternRedundancy() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Possibilities.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find pattern possibility redundancy" ) );

        Collection<Object> data = new ArrayList<Object>();

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        String ruleName1 = "Rule 1";
        String ruleName2 = "Rule 2";

        Pattern p1 = new Pattern();
        p1.setRuleName( ruleName1 );
        Pattern p2 = new Pattern();
        p2.setRuleName( ruleName2 );

        LiteralRestriction lr1 = new LiteralRestriction();
        lr1.setRuleName( ruleName1 );
        LiteralRestriction lr2 = new LiteralRestriction();
        lr2.setRuleName( ruleName2 );

        SubPattern pp1 = new SubPattern();
        pp1.setPatternGuid( p1.getGuid() );
        pp1.setRuleName( ruleName1 );
        pp1.add( lr1 );

        SubPattern pp2 = new SubPattern();
        pp2.setPatternGuid( p2.getGuid() );
        pp2.setRuleName( ruleName2 );
        pp2.add( lr2 );

        Redundancy r1 = new Redundancy( lr1,
                                        lr2 );
        Redundancy r2 = new Redundancy( p1,
                                        p2 );

        data.add( p1 );
        data.add( p2 );
        data.add( lr1 );
        data.add( lr2 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( r1 );
        data.add( r2 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<String, Set<String>> map = createRedundancyMap( sessionResult.iterateObjects() );

        assertTrue( TestBase.mapContains( map,
                                          ruleName1,
                                          ruleName2 ) );
        assertTrue( TestBase.mapContains( map,
                                          ruleName2,
                                          ruleName1 ) );

        if ( !map.isEmpty() ) {
            fail( "More redundancies than was expected." );
        }
    }

    public void testSubRuleRedundancy() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Possibilities.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Find rule possibility redundancy" ) );

        Collection<Object> data = new ArrayList<Object>();

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        session.setGlobal( "result",
                           result );

        /*
         * First rules. These are redundant,
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

        SubRule rp1 = new SubRule();
        rp1.setRuleGuid( r1.getGuid() );
        rp1.setRuleName( ruleName1 );
        rp1.add( pp1 );

        SubRule rp2 = new SubRule();
        rp2.setRuleGuid( r2.getGuid() );
        rp2.setRuleName( ruleName2 );
        rp2.add( pp2 );

        Redundancy possibilityredundancy = new Redundancy( RedundancyType.STRONG,
                                                           pp1,
                                                           pp2 );
        Redundancy ruleRedundancy = new Redundancy( r1,
                                                    r2 );

        data.add( r1 );
        data.add( r2 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( possibilityredundancy );
        data.add( ruleRedundancy );
        data.add( rp1 );
        data.add( rp2 );

        /*
         * These two rules are not redundant
         */
        String ruleName3 = "Rule 3";
        String ruleName4 = "Rule 4";

        VerifierRule r3 = new VerifierRule();
        r3.setRuleName( ruleName3 );
        VerifierRule r4 = new VerifierRule();
        r4.setRuleName( ruleName4 );

        SubPattern pp3 = new SubPattern();
        pp3.setRuleGuid( r3.getGuid() );
        pp3.setRuleName( ruleName3 );
        SubPattern pp4 = new SubPattern();
        pp4.setRuleGuid( r4.getGuid() );
        pp4.setRuleName( ruleName4 );
        // This possibility makes them different
        SubPattern pp5 = new SubPattern();
        pp5.setRuleGuid( r4.getGuid() );
        pp5.setRuleName( ruleName4 );

        SubRule rp3 = new SubRule();
        rp3.setRuleGuid( r3.getGuid() );
        rp3.setRuleName( ruleName3 );
        rp3.add( pp3 );

        SubRule rp4 = new SubRule();
        rp4.setRuleGuid( r4.getGuid() );
        rp4.setRuleName( ruleName4 );
        rp4.add( pp4 );
        rp4.add( pp5 );

        Redundancy possibilityredundancy2 = new Redundancy( RedundancyType.STRONG,
                                                            pp3,
                                                            pp4 );
        Redundancy ruleRedundancy2 = new Redundancy( r3,
                                                     r4 );

        data.add( r3 );
        data.add( r4 );
        data.add( pp3 );
        data.add( pp4 );
        data.add( pp5 );
        data.add( possibilityredundancy2 );
        data.add( ruleRedundancy2 );
        data.add( rp3 );
        data.add( rp4 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createRedundancyCauseMap( CauseType.RULE_POSSIBILITY,
                                                               sessionResult.iterateObjects() );

        assertTrue( TestBase.causeMapContains( map,
                                               rp1,
                                               rp2 ) );
        assertFalse( TestBase.causeMapContains( map,
                                                rp3,
                                                rp4 ) );

        if ( !map.isEmpty() ) {
            fail( "More redundancies than was expected." );
        }
    }
}
