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

package org.drools.verifier.alwaysTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.AlwaysTrue;
import org.drools.verifier.report.components.Opposites;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class AlwaysTruePatternTest extends TestBase {

    @Test @Ignore
    public void testPatternPossibilities() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Patterns.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Pattern possibility that is always true" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        // This pattern is always true.
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        Restriction r1 = LiteralRestriction.createRestriction( pattern1,
                                                               "" );
        Restriction r2 = LiteralRestriction.createRestriction( pattern1,
                                                               "" );
        Opposites o1 = new Opposites( r1,
                                      r2 );
        SubPattern pp1 = new SubPattern( pattern1,
                                         0 );
        pp1.add( r1 );
        pp1.add( r2 );

        Restriction r3 = new VariableRestriction( pattern1 );
        Restriction r4 = new VariableRestriction( pattern1 );
        Opposites o2 = new Opposites( r1,
                                      r2 );
        SubPattern pp2 = new SubPattern( pattern1,
                                         1 );
        pp2.add( r1 );
        pp2.add( r2 );

        // This pattern is okay.
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        Restriction r5 = LiteralRestriction.createRestriction( pattern2,
                                                               "" );
        Restriction r6 = LiteralRestriction.createRestriction( pattern2,
                                                               "" );
        SubPattern pp3 = new SubPattern( pattern2,
                                         0 );
        pp3.add( r5 );
        pp3.add( r6 );

        Restriction r7 = new VariableRestriction( pattern2 );
        Restriction r8 = new VariableRestriction( pattern2 );
        Opposites o4 = new Opposites( r7,
                                      r8 );
        SubPattern pp4 = new SubPattern( pattern2,
                                         1 );
        pp4.add( r7 );
        pp4.add( r8 );

        data.add( pattern1 );
        data.add( r1 );
        data.add( r2 );
        data.add( r3 );
        data.add( r4 );
        data.add( o1 );
        data.add( o2 );
        data.add( pp1 );
        data.add( pp2 );

        data.add( pattern2 );
        data.add( r5 );
        data.add( r6 );
        data.add( r7 );
        data.add( r8 );
        data.add( o4 );
        data.add( pp3 );
        data.add( pp4 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );
        Iterator iter = sessionResult.iterateObjects();

        boolean pp1true = false;
        boolean pp2true = false;
        boolean pp3true = false;
        boolean pp4true = false;
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof AlwaysTrue ) {
                AlwaysTrue alwaysTrue = (AlwaysTrue) o;
                if ( !pp1true ) {
                    pp1true = alwaysTrue.getCause().equals( pp1 );
                }
                if ( !pp2true ) {
                    pp2true = alwaysTrue.getCause().equals( pp2 );
                }
                if ( !pp3true ) {
                    pp3true = alwaysTrue.getCause().equals( pp3 );
                }
                if ( !pp4true ) {
                    pp4true = alwaysTrue.getCause().equals( pp4 );
                }
            }
        }

        assertTrue( pp1true );
        assertTrue( pp2true );
        assertFalse( pp3true );
        assertTrue( pp4true );
    }

    @Test @Ignore
    public void testPatterns() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Patterns.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Pattern that is always true" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        VerifierRule rule1 = VerifierComponentMockFactory.createRule1();

        // This pattern is always true.
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        SubPattern pp1 = new SubPattern( pattern1,
                                         0 );
        AlwaysTrue alwaysTrue1 = new AlwaysTrue( pp1 );

        SubPattern pp2 = new SubPattern( pattern1,
                                         1 );
        AlwaysTrue alwaysTrue2 = new AlwaysTrue( pp2 );

        // This pattern is okay.
        Pattern pattern2 = new Pattern( rule1 );
        pattern2.setName( "testPattern2" );

        SubPattern pp3 = new SubPattern( pattern2,
                                         0 );

        SubPattern pp4 = new SubPattern( pattern2,
                                         1 );
        AlwaysTrue alwaysTrue4 = new AlwaysTrue( pp4 );

        data.add( rule1 );

        data.add( pattern1 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( alwaysTrue1 );
        data.add( alwaysTrue2 );

        data.add( pattern2 );
        data.add( pp3 );
        data.add( pp4 );
        data.add( alwaysTrue4 );

        session.executeWithResults( data );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.NOTE ).iterator();

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

        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 1,
                      result.getBySeverity( Severity.NOTE ).size() );
        assertTrue( works );
    }
}
