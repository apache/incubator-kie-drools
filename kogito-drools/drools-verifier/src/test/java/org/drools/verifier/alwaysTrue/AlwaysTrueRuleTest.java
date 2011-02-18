/*
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
import org.junit.Test;
import static org.junit.Assert.*;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.AlwaysTrue;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class AlwaysTrueRuleTest extends TestBase {

    @Test
    public void testPatternPossibilities() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Rules.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Rule possibility that is always true" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        // This rule is always true.
        VerifierRule rule1 = VerifierComponentMockFactory.createRule1();
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();

        SubRule rp1 = new SubRule( rule1,
                                   0 );
        SubPattern pp1 = new SubPattern( pattern1,
                                         0 );
        AlwaysTrue alwaysTrue1 = new AlwaysTrue( pp1 );
        SubPattern pp2 = new SubPattern( pattern1,
                                         1 );
        AlwaysTrue alwaysTrue2 = new AlwaysTrue( pp2 );

        rp1.add( pp1 );
        rp1.add( pp2 );

        // This rule is okay.
        VerifierRule rule2 = VerifierComponentMockFactory.createRule2();
        Pattern pattern2 = VerifierComponentMockFactory.createPattern2();

        SubRule rp2 = new SubRule( rule2,
                                   0 );
        SubPattern pp3 = new SubPattern( pattern2,
                                         0 );
        SubPattern pp4 = new SubPattern( pattern2,
                                         1 );
        AlwaysTrue alwaysTrue4 = new AlwaysTrue( pp4 );

        rp2.add( pp3 );
        rp2.add( pp4 );

        data.add( rule1 );
        data.add( rp1 );
        data.add( pp1 );
        data.add( pp2 );
        data.add( alwaysTrue1 );
        data.add( alwaysTrue2 );

        data.add( rule2 );
        data.add( rp2 );
        data.add( pp3 );
        data.add( pp4 );
        data.add( alwaysTrue4 );

        StatelessSessionResult sessionResult = session.executeWithResults( data );
        Iterator iter = sessionResult.iterateObjects();

        boolean rp1true = false;
        boolean rp2true = false;
        boolean rp3true = false;
        boolean rp4true = false;
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof AlwaysTrue ) {
                AlwaysTrue alwaysTrue = (AlwaysTrue) o;
                if ( !rp1true ) {
                    rp1true = alwaysTrue.getCause().equals( pp1 );
                }
                if ( !rp2true ) {
                    rp2true = alwaysTrue.getCause().equals( pp2 );
                }
                if ( !rp3true ) {
                    rp3true = alwaysTrue.getCause().equals( pp3 );
                }
                if ( !rp4true ) {
                    rp4true = alwaysTrue.getCause().equals( pp4 );
                }
            }
        }

        assertTrue( rp1true );
        assertTrue( rp2true );
        assertFalse( rp3true );
        assertTrue( rp4true );
    }

    @Test
    public void testPatterns() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "Rules.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Rule that is always true" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<Object> data = new ArrayList<Object>();

        session.setGlobal( "result",
                           result );

        // This rule is always true.
        VerifierRule rule1 = VerifierComponentMockFactory.createRule1();

        SubRule rp1 = new SubRule( rule1,
                                   0 );
        AlwaysTrue alwaysTrue1 = new AlwaysTrue( rp1 );

        SubRule rp2 = new SubRule( rule1,
                                   1 );
        AlwaysTrue alwaysTrue2 = new AlwaysTrue( rp2 );

        // This rule is okay.
        VerifierRule rule2 = VerifierComponentMockFactory.createRule2();

        SubRule rp3 = new SubRule( rule2,
                                   0 );

        SubRule rp4 = new SubRule( rule2,
                                   1 );
        AlwaysTrue alwaysTrue4 = new AlwaysTrue( rp4 );

        data.add( rule1 );
        data.add( rp1 );
        data.add( rp2 );
        data.add( alwaysTrue1 );
        data.add( alwaysTrue2 );

        data.add( rule2 );
        data.add( rp3 );
        data.add( rp4 );
        data.add( alwaysTrue4 );

        session.executeWithResults( data );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.WARNING ).iterator();

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

        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 1,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.NOTE ).size() );
        assertTrue( works );
    }
}
