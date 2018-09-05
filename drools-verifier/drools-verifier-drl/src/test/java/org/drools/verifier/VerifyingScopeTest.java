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

package org.drools.verifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.drools.core.io.impl.ClassPathResource;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.junit.Test;
import org.kie.api.io.ResourceType;

public class VerifyingScopeTest {

    @Test
    public void testSingleRule() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        vConfiguration.getVerifyingResources().put( new ClassPathResource( "VerifyingScope.drl",
                                                                           Verifier.class ),
                                                    ResourceType.DRL );

        Verifier verifier = vBuilder.newVerifier( vConfiguration );

        verifier.addResourcesToVerify( new ClassPathResource( "Misc3.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis( new ScopesAgendaFilter( true,
                                                                       ScopesAgendaFilter.VERIFYING_SCOPE_SINGLE_RULE ) );

        if ( !works ) {
            for ( VerifierError error : verifier.getErrors() ) {
                System.out.println( error.getMessage() );
            }

            fail( "Error when building in verifier" );
        }

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 6,
                      result.getBySeverity( Severity.NOTE ).size() );

    }

    @Test
    public void testNothing() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        vConfiguration.getVerifyingResources().put( new ClassPathResource( "VerifyingScope.drl",
                                                                           Verifier.class ),
                                                    ResourceType.DRL );

        Verifier verifier = vBuilder.newVerifier( vConfiguration );

        verifier.addResourcesToVerify( new ClassPathResource( "Misc3.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis( new ScopesAgendaFilter( true,
                                                                       Collections.EMPTY_LIST));

        assertTrue( works );

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 2,
                      result.getBySeverity( Severity.NOTE ).size() );

    }

    @Test
    public void testDecisionTable() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        vConfiguration.getVerifyingResources().put( new ClassPathResource( "VerifyingScope.drl",
                                                                           Verifier.class ),
                                                    ResourceType.DRL );

        Verifier verifier = vBuilder.newVerifier( vConfiguration );

        verifier.addResourcesToVerify( new ClassPathResource( "Misc3.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis( new ScopesAgendaFilter( false,
                                                                       ScopesAgendaFilter.VERIFYING_SCOPE_DECISION_TABLE ) );

        assertTrue( works );

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 2,
                      result.getBySeverity( Severity.NOTE ).size() );

    }
}
