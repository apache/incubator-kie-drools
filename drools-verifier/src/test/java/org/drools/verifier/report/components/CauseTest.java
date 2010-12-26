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

package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;

public class CauseTest {

    @Test
    public void testCauseTrace() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( new ClassPathResource( "Causes.drl",
                                                              getClass() ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        assertTrue( works );

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        Collection<VerifierMessageBase> warnings = result.getBySeverity( Severity.WARNING );
        Collection<VerifierMessageBase> redundancyWarnings = new ArrayList<VerifierMessageBase>();

        for ( VerifierMessageBase verifierMessageBase : warnings ) {
            if ( verifierMessageBase.getMessageType().equals( MessageType.REDUNDANCY ) ) {
                redundancyWarnings.add( verifierMessageBase );
            }
        }

        assertEquals( 1,
                      redundancyWarnings.size() );

        VerifierMessage message = (VerifierMessage) redundancyWarnings.toArray()[0];

        //        System.out.println( message );

        assertEquals( 2,
                      message.getImpactedRules().size() );

        assertTrue( message.getImpactedRules().values().contains( "Your First Rule" ) );
        assertTrue( message.getImpactedRules().values().contains( "Your Second Rule" ) );

        Cause[] causes = message.getCauses().toArray( new Cause[message.getCauses().size()] );

        assertEquals( 1,
                      causes.length );
        causes = causes[0].getCauses().toArray( new Cause[causes[0].getCauses().size()] );

        assertEquals( 2,
                      causes.length );

        causes = causes[0].getCauses().toArray( new Cause[causes[0].getCauses().size()] );

        assertEquals( 1,
                      causes.length );

        causes = causes[0].getCauses().toArray( new Cause[causes[0].getCauses().size()] );

        assertEquals( 1,
                      causes.length );

        causes = causes[0].getCauses().toArray( new Cause[causes[0].getCauses().size()] );

        assertEquals( 2,
                      causes.length );

        assertEquals( 0,
                      result.getBySeverity( Severity.NOTE ).size() );

    }
}
