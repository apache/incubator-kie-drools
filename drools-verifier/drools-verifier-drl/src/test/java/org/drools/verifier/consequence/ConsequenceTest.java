/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class ConsequenceTest extends TestBaseOld {

    @Test
    public void testMissingConsequence() throws Exception {

        InputStream in = getClass().getResourceAsStream( "Consequence.drl" );

        KieSession session = getStatelessKieSession( in );

        VerifierReport result = VerifierReportFactory.newVerifierReport();

        Collection< ? extends Object> testData = getTestData( this.getClass().getResourceAsStream( "ConsequenceTest2.drl" ),
                                                              result.getVerifierData() );

        session.setGlobal( "result",
                           result );

        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("No action - possibly commented out"));

        Iterator<VerifierMessageBase> iterator = result.getBySeverity( Severity.WARNING ).iterator();

        Set<String> rulesThatHadErrors = new HashSet<String>();
        while ( iterator.hasNext() ) {
            Object o = iterator.next();
            if ( o instanceof VerifierMessage ) {
                VerifierMessage message = (VerifierMessage) o;
                rulesThatHadErrors.addAll( message.getImpactedRules().values() );
            }
        }

        assertFalse( rulesThatHadErrors.contains( "Has a consequence 1" ) );
        assertTrue( rulesThatHadErrors.remove( "Missing consequence 1" ) );
        assertTrue( rulesThatHadErrors.remove( "Missing consequence 2" ) );

        if ( !rulesThatHadErrors.isEmpty() ) {
            for ( String string : rulesThatHadErrors ) {
                fail( "Rule " + string + " caused an error." );
            }
        }
    }
}
