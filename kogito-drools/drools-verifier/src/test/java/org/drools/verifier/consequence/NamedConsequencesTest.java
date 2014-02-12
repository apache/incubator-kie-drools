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

package org.drools.verifier.consequence;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

public class NamedConsequencesTest extends TestBaseOld {

    @Test
    public void testMissingConsequence() throws Exception {

        InputStream in = getClass().getResourceAsStream( "NamedConsequences.drl" );

        KieSession session = getStatelessKieSession(in);

        VerifierReport result = VerifierReportFactory.newVerifierReport();

        Collection< ? extends Object> testData = getTestData( this.getClass().getResourceAsStream( "ConsequenceTest.drl" ),
                                                              result.getVerifierData() );

        session.setGlobal( "result",
                           result );

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("No action - possibly commented out"));

        Iterator<VerifierMessageBase> iter = result.getBySeverity( Severity.NOTE ).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof VerifierMessage ) {
                VerifierMessage message = (VerifierMessage) o;
                rulesThatHadErrors.addAll( message.getImpactedRules().values() );
            }
        }

        assertFalse( rulesThatHadErrors.contains( "This one is has an unused named consequence" ) );

        if ( !rulesThatHadErrors.isEmpty() ) {
            for ( String string : rulesThatHadErrors ) {
                fail( "Rule " + string + " caused an error." );
            }
        }
    }
}
