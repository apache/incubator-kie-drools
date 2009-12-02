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
import org.drools.verifier.report.components.Subsumption;

public class SubsumptantRestrictionsTest extends TestCase {

    public void testVerifierLiteralRestrictionRedundancy1() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantRestriction1.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        for ( VerifierError error : verifier.getErrors() ) {
            System.out.println( error.getMessage() );
        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        assertEquals( 9,
                      subsumptionList.size() );

        verifier.dispose();
    }

    public void testVerifierLiteralRestrictionRedundancy2() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantRestriction2.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        for ( VerifierError error : verifier.getErrors() ) {
            System.out.println( error.getMessage() );
        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        assertEquals( 9,
                      subsumptionList.size() );

        verifier.dispose();
    }

    public void testVerifierLiteralRestrictionRedundancy3() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "SubsumptantRestriction3.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        for ( VerifierError error : verifier.getErrors() ) {
            System.out.println( error.getMessage() );
        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );

        assertEquals( 6,
                      subsumptionList.size() );

        verifier.dispose();
    }
}
