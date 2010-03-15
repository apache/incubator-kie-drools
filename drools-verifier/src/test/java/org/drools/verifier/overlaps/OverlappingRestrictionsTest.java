package org.drools.verifier.overlaps;

import java.util.Collection;

import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ClassObjectFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.builder.VerifierImpl;
import org.drools.verifier.report.components.Overlap;

public class OverlappingRestrictionsTest extends TestBase {

    // TODO: Add this feature
    public void testOverlap() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "RestrictionsTest.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        Collection<Object> overlaps = ((VerifierImpl) verifier).getKnowledgeSession().getObjects( new ClassObjectFilter( Overlap.class ) );

//        for ( Object object : overlaps ) {
//            System.out.println( object );
//        }

        assertEquals( 3,
                      overlaps.size() );

        verifier.dispose();

    }
}
