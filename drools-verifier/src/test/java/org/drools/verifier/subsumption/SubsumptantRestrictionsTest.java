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

package org.drools.verifier.subsumption;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ClassObjectFilter;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.builder.VerifierImpl;
import org.drools.verifier.report.components.Subsumption;

public class SubsumptantRestrictionsTest {

    @Test
    public void testVerifierLiteralRestrictionRedundancy1() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantRestriction1.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        //        for ( VerifierError error : verifier.getErrors() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        assertEquals( 9,
                      subsumptionList.size() );

        verifier.dispose();
    }

    @Test
    public void testVerifierLiteralRestrictionRedundancy2() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantRestriction2.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        //        for ( VerifierError error : verifier.getErrors() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        assertEquals( 9,
                      subsumptionList.size() );

        verifier.dispose();
    }

    @Test
    public void testVerifierLiteralRestrictionRedundancy3() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantRestriction3.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        //        for ( VerifierError error : verifier.getErrors() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        assertEquals( 6,
                      subsumptionList.size() );

        verifier.dispose();
    }

    @Test
    public void testVerifierLiteralRestrictionRedundancy4() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantRestriction4.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        //        for ( VerifierError error : verifier.getErrors() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

//        for ( Object object : subsumptionList ) {
//            System.out.println( object );
//        }

        assertEquals( 4,
                      subsumptionList.size() );

        verifier.dispose();
    }
}
