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

package org.drools.verifier.redundancy;

import java.util.Collection;

import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.verifier.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.MessageType;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

public class WarningsTest extends TestBase {

    @Test
    public void testRedundantRules() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "RedundantRules1.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

//        for ( VerifierError error : verifier.getMissingClasses() ) {
//            System.out.println( error.getMessage() );
//        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        VerifierReport result = verifier.getResult();

        Collection<VerifierMessageBase> warnings = result.getBySeverity( Severity.WARNING );

        int counter = 0;
        for ( VerifierMessageBase message : warnings ) {
            //            System.out.println( message );
            if ( message.getMessageType().equals( MessageType.REDUNDANCY ) ) {
                //                System.out.println( message );
                counter++;
            }
        }

        assertEquals( 1,
                      counter );

        verifier.dispose();
    }
}
