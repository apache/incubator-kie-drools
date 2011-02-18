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

package org.drools.verifier;

import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 * @author rikkola
 *
 */
public class RangeCheckTest {

    @Test
    public void testVerifier() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                             vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( new ClassPathResource( "RangeTest.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                             verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        assertTrue( works );

        VerifierReport result = verifier.getResult();
        assertNotNull( result );

        for ( VerifierMessageBase message : result.getBySeverity( Severity.ERROR ) ) {
            System.out.println( message );
        }

        // This rule should not have errors, evereververevernever!
        assertEquals( 0,
                             result.getBySeverity( Severity.ERROR ).size() );

    }

}
