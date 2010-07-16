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

package org.drools.verifier.optimisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RuleComponent;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

public class RestrictionOrderTest extends TestBase {

    public void testRestrictionOrderInsideOperator() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "RestrictionOrder.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Optimise restrictions inside operator" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection< ? extends Object> testData = getTestData( this.getClass().getResourceAsStream( "OptimisationRestrictionOrderTest.drl" ),
                                                              result.getVerifierData() );

        //        for ( Object object : testData ) {
        //            if ( object instanceof SubPattern ) {
        //                SubPattern s = (SubPattern) object;
        //                System.out.println( " - " + s );
        //                for ( PatternComponent o : s.getItems() ) {
        //                    System.out.println( " -- " + o + " : " + o.getOrderNumber() );
        //                }
        //            }
        //        }

        session.setGlobal( "result",
                           result );

        session.executeWithResults( testData );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.NOTE ).iterator();

        Collection<String> ruleNames = new ArrayList<String>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof VerifierMessage ) {
                String name = ((VerifierMessage) o).getCauses().toArray( new Restriction[2] )[0].getRuleName();

                ruleNames.add( name );
            }
        }

        assertTrue( ruleNames.remove( "Wrong descr order 1" ) );

        if ( !ruleNames.isEmpty() ) {
            for ( String string : ruleNames ) {
                fail( "Rule " + string + " caused an error." );
            }
        }
    }

    public void testPredicateOrderInsideOperator() throws Exception {
        StatelessSession session = getStatelessSession( this.getClass().getResourceAsStream( "RestrictionOrder.drl" ) );

        session.setAgendaFilter( new RuleNameMatchesAgendaFilter( "Optimise predicates inside operator" ) );

        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection< ? extends Object> testData = getTestData( this.getClass().getResourceAsStream( "OptimisationRestrictionOrderTest.drl" ),
                                                              result.getVerifierData() );

        session.setGlobal( "result",
                           result );

        session.executeWithResults( testData );

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.NOTE ).iterator();

        Collection<String> ruleNames = new ArrayList<String>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof VerifierMessage ) {
                String name = ((VerifierMessage) o).getCauses().toArray( new RuleComponent[2] )[0].getRuleName();

                ruleNames.add( name );
            }
        }

        assertTrue( ruleNames.remove( "Wrong eval order 1" ) );

        if ( !ruleNames.isEmpty() ) {
            for ( String string : ruleNames ) {
                fail( "Rule " + string + " caused an error." );
            }
        }
    }
}
