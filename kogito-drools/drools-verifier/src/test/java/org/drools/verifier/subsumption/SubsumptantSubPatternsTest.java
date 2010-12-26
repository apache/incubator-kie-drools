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
import org.junit.Ignore;
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
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.report.components.Subsumption;

public class SubsumptantSubPatternsTest {

    @Test
    public void testSubpatternSubsumption1() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantSubPatterns1.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

//        for ( VerifierError error : verifier.getErrors() ) {
//            System.out.println( error.getMessage() );
//        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        if ( !noProblems ) {
            for ( VerifierError error : verifier.getErrors() ) {
                System.out.println( error.getMessage() );
            }
        }

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        int count = 0;
        for ( Object object : subsumptionList ) {
            //            System.out.println( " * " + ((Subsumption) object) );
            if ( ((VerifierComponent) ((Subsumption) object).getLeft()).getVerifierComponentType().equals( VerifierComponentType.SUB_PATTERN ) ) {
                //                System.out.println( " ** " + ((SubPattern) ((Subsumption) object).getLeft()).getItems() + " - " + ((SubPattern) ((Subsumption) object).getRight()).getItems() );
                count++;
            }
        }
        assertEquals( 1,
                      count );

        verifier.dispose();
    }

    /**
     * Empty pattern
     * @throws Exception
     */
    @Test
    public void testSubpatternSubsumption2() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantSubPatterns2.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

//        for ( VerifierError error : verifier.getErrors() ) {
//            System.out.println( error.getMessage() );
//        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        int count = 0;
        for ( Object object : subsumptionList ) {
            //            System.out.println( " * " + ((Subsumption) object) );
            if ( ((VerifierComponent) ((Subsumption) object).getLeft()).getVerifierComponentType().equals( VerifierComponentType.SUB_PATTERN ) ) {
                count++;
            }
        }
        assertEquals( 1,
                      count );

        verifier.dispose();
    }

    /**
     * Different sources
     * @throws Exception
     */
    @Test
    public void testSubpatternSubsumption3() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantSubPatterns3.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

//        for ( VerifierError error : verifier.getErrors() ) {
//            System.out.println( error.getMessage() );
//        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        int count = 0;
        for ( Object object : subsumptionList ) {
            //            System.out.println( " * " + ((Subsumption) object) );
            if ( ((VerifierComponent) ((Subsumption) object).getLeft()).getVerifierComponentType().equals( VerifierComponentType.SUB_PATTERN ) ) {
                count++;
            }
        }
        assertEquals( 1,
                      count );

        verifier.dispose();
    }

    /**
     * Patterns that use from
     * @throws Exception
     */
    @Test @Ignore
    public void testSubpatternSubsumption4() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantSubPatterns4.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

//        for ( VerifierError error : verifier.getErrors() ) {
//            System.out.println( error.getMessage() );
//        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        int count = 0;
        for ( Object object : subsumptionList ) {
//            System.out.println( " * " + ((Subsumption) object) );
            if ( ((VerifierComponent) ((Subsumption) object).getLeft()).getVerifierComponentType().equals( VerifierComponentType.SUB_PATTERN ) ) {
                count++;
            }
        }
        assertEquals( 1,
                      count );

        verifier.dispose();
    }

    /**
     * Different sources
     * @throws Exception
     */
    @Test
    public void testSubpatternSubsumption5() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantSubPatterns5.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

//        for ( VerifierError error : verifier.getErrors() ) {
//            System.out.println( error.getMessage() );
//        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        int count = 0;
        for ( Object object : subsumptionList ) {
            //            System.out.println( " * " + ((Subsumption) object) );
            if ( ((VerifierComponent) ((Subsumption) object).getLeft()).getVerifierComponentType().equals( VerifierComponentType.SUB_PATTERN ) ) {
                count++;
            }
        }
        assertEquals( 8,
                      count );

        verifier.dispose();
    }
}
