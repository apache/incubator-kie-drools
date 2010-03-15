package org.drools.verifier.opposites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Opposites;

public class OppositePatternsTest extends OppositesBase {

    public void testPatternsPossibilitiesOpposite() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Patterns.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Opposite Patterns" ) );

        Collection<Object> data = new ArrayList<Object>();

        /*
         * Working pair
         */
        SubPattern pp1 = new SubPattern();
        SubPattern pp2 = new SubPattern();

        Restriction r1 = new LiteralRestriction();
        pp1.add( r1 );

        Restriction r2 = new LiteralRestriction();
        pp2.add( r2 );

        Restriction r3 = new LiteralRestriction();
        pp1.add( r3 );

        Restriction r4 = new LiteralRestriction();
        pp2.add( r4 );

        Opposites o1 = new Opposites( r1,
                                      r2 );
        Opposites o2 = new Opposites( r3,
                                      r4 );

        /*
         * Pair that doesn't work.
         */
        SubPattern pp3 = new SubPattern();
        SubPattern pp4 = new SubPattern();

        Restriction r5 = new LiteralRestriction();
        pp3.add( r5 );

        Restriction r6 = new LiteralRestriction();
        pp4.add( r6 );

        Restriction r7 = new LiteralRestriction();
        pp3.add( r7 );

        Restriction r8 = new LiteralRestriction();
        pp4.add( r8 );

        Opposites o3 = new Opposites( r5,
                                      r6 );

        data.add( r1 );
        data.add( r2 );
        data.add( r3 );
        data.add( r4 );
        data.add( r5 );
        data.add( r6 );
        data.add( r7 );
        data.add( r8 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( pp3 );
        data.add( pp4 );
        data.add( o1 );
        data.add( o2 );
        data.add( o3 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );

        Map<Cause, Set<Cause>> map = createOppositesMap( VerifierComponentType.SUB_PATTERN,
                                                         sessionResult.iterateObjects() );

        assertTrue( (TestBase.causeMapContains( map,
                                                pp1,
                                                pp2 ) ^ TestBase.causeMapContains( map,
                                                                                   pp2,
                                                                                   pp1 )) );

        if ( !map.isEmpty() ) {
            fail( "More opposites than was expected." );
        }
    }
}
