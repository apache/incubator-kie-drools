/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.opposites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Opposites;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OppositePatternsTest extends OppositesBase {

    @Test
    public void testPatternsPossibilitiesOpposite() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Patterns.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Opposite Patterns" ) );

        Collection<Object> data = new ArrayList<Object>();

        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        /*
         * Working pair
         */
        SubPattern pp1 = new SubPattern( pattern,
                                         0 );
        SubPattern pp2 = new SubPattern( pattern,
                                         1 );

        Restriction r1 = LiteralRestriction.createRestriction( pattern,
                                                               "" );
        pp1.add( r1 );

        Restriction r2 = LiteralRestriction.createRestriction( pattern,
                                                               "" );
        pp2.add( r2 );

        Restriction r3 = LiteralRestriction.createRestriction( pattern,
                                                               "" );
        pp1.add( r3 );

        Restriction r4 = LiteralRestriction.createRestriction( pattern,
                                                               "" );
        pp2.add( r4 );

        Opposites o1 = new Opposites( r1,
                                      r2 );
        Opposites o2 = new Opposites( r3,
                                      r4 );

        /*
         * Pair that doesn't work.
         */
        SubPattern pp3 = new SubPattern( pattern,
                                         2 );
        SubPattern pp4 = new SubPattern( pattern,
                                         3 );

        Restriction r5 = LiteralRestriction.createRestriction( pattern,
                                                               "" );
        pp3.add( r5 );

        Restriction r6 = LiteralRestriction.createRestriction( pattern,
                                                               "" );
        pp4.add( r6 );

        Restriction r7 = LiteralRestriction.createRestriction( pattern,
                                                               "" );
        pp3.add( r7 );

        Restriction r8 = LiteralRestriction.createRestriction( pattern,
                                                               "" );
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
