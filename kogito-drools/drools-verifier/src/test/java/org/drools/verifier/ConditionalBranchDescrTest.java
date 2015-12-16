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

import org.drools.core.io.impl.ClassPathResource;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Test;
import org.kie.api.io.ResourceType;

import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarInputStream;

import static org.junit.Assert.*;

public class ConditionalBranchDescrTest {

    @Test
    public void testVerifier() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( new ClassPathResource( "ConditionalBranchDescrTest.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        assertTrue( works );

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
//        assertEquals( 0,
//                      result.getBySeverity( Severity.ERROR ).size() );
//        assertEquals( 6,
//                      result.getBySeverity( Severity.WARNING ).size() );
//        assertEquals( 1,
//                      result.getBySeverity( Severity.NOTE ).size() );

    }

}
