package org.drools.verifier.redundancy;

import java.util.Collection;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ClassObjectFilter;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.builder.VerifierImpl;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.Subsumption;

/**
 * 
 * @author rikkola
 *
 */
public class RedundancyTest extends TestCase {

    public void testVerifierLiteralRestrictionRedundancy() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "RedundantRestrictions.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Subsumption.class ) );
        Collection<Object> redundancyList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Redundancy.class ) );

        assertEquals( 2,
                      subsumptionList.size() );
        assertEquals( 1,
                      redundancyList.size() );

        verifier.dispose();
    }
}