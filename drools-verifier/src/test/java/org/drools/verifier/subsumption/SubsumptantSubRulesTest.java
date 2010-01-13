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
import org.drools.verifier.report.components.CauseType;
import org.drools.verifier.report.components.Subsumption;

public class SubsumptantSubRulesTest extends TestCase {

    public void testSubruleSubsumption1() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantSubRules1.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        for ( VerifierError error : verifier.getErrors() ) {
            System.out.println( error.getMessage() );
        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        int count = 0;
        for ( Object object : subsumptionList ) {
            //                        System.out.println( " * " + ((Subsumption) object) );
            if ( ((Subsumption) object).getLeft().getCauseType().equals( CauseType.SUB_RULE ) ) {
                //                System.out.println( " ** " + ((SubRule) ((Subsumption) object).getLeft()).getItems() + " - " + ((SubRule) ((Subsumption) object).getRight()).getItems() );
                count++;
            }
        }
        assertEquals( 2,
                      count );

        verifier.dispose();
    }

    //    /**
    //     * Empty pattern
    //     * @throws Exception
    //     */
    //    public void testSubpatternSubsumption2() throws Exception {
    //
    //        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
    //
    //        Verifier verifier = vBuilder.newVerifier();
    //
    //        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantSubPatterns2.drl",
    //                                                                             getClass() ),
    //                                       ResourceType.DRL );
    //
    //        for ( VerifierError error : verifier.getErrors() ) {
    //            System.out.println( error.getMessage() );
    //        }
    //
    //        assertFalse( verifier.hasErrors() );
    //
    //        boolean noProblems = verifier.fireAnalysis();
    //        assertTrue( noProblems );
    //
    //        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );
    //
    //        int count = 0;
    //        for ( Object object : subsumptionList ) {
    //            //            System.out.println( " * " + ((Subsumption) object) );
    //            if ( ((Subsumption) object).getLeft().getCauseType().equals( CauseType.SUB_PATTERN ) ) {
    //                count++;
    //            }
    //        }
    //        assertEquals( 1,
    //                      count );
    //
    //        verifier.dispose();
    //    }
    //
    //    /**
    //     * Different sources
    //     * @throws Exception
    //     */
    //    public void testSubpatternSubsumption3() throws Exception {
    //
    //        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
    //
    //        Verifier verifier = vBuilder.newVerifier();
    //
    //        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantSubPatterns3.drl",
    //                                                                             getClass() ),
    //                                       ResourceType.DRL );
    //
    //        for ( VerifierError error : verifier.getErrors() ) {
    //            System.out.println( error.getMessage() );
    //        }
    //
    //        assertFalse( verifier.hasErrors() );
    //
    //        boolean noProblems = verifier.fireAnalysis();
    //        assertTrue( noProblems );
    //
    //        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );
    //
    //        int count = 0;
    //        for ( Object object : subsumptionList ) {
    //            //            System.out.println( " * " + ((Subsumption) object) );
    //            if ( ((Subsumption) object).getLeft().getCauseType().equals( CauseType.SUB_PATTERN ) ) {
    //                count++;
    //            }
    //        }
    //        assertEquals( 1,
    //                      count );
    //
    //        verifier.dispose();
    //    }
    //
    //    /**
    //     * Patterns that use from
    //     * @throws Exception
    //     */
    //    public void FIXMEtestSubpatternSubsumption4() throws Exception {
    //
    //        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
    //
    //        Verifier verifier = vBuilder.newVerifier();
    //
    //        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantSubPatterns4.drl",
    //                                                                             getClass() ),
    //                                       ResourceType.DRL );
    //
    //        for ( VerifierError error : verifier.getErrors() ) {
    //            System.out.println( error.getMessage() );
    //        }
    //
    //        assertFalse( verifier.hasErrors() );
    //
    //        boolean noProblems = verifier.fireAnalysis();
    //        assertTrue( noProblems );
    //
    //        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );
    //
    //        int count = 0;
    //        for ( Object object : subsumptionList ) {
    //            System.out.println( " * " + ((Subsumption) object) );
    //            if ( ((Subsumption) object).getLeft().getCauseType().equals( CauseType.SUB_PATTERN ) ) {
    //                count++;
    //            }
    //        }
    //        assertEquals( 1,
    //                      count );
    //
    //        verifier.dispose();
    //    }
}
