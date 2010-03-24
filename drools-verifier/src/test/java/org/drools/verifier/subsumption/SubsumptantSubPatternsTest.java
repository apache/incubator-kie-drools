package org.drools.verifier.subsumption;

import java.util.Collection;

import junit.framework.TestCase;

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

public class SubsumptantSubPatternsTest extends TestCase {

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
    public void FIXMEtestSubpatternSubsumption4() throws Exception {

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
