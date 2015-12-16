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

package org.drools.verifier.report.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.core.io.impl.ClassPathResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.io.ResourceType;

public class CauseTest {

    @Test
    @Ignore("08-APR-2011 temporally ignoring -Rikkola-")
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
