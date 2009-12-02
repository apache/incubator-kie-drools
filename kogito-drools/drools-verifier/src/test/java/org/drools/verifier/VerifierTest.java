package org.drools.verifier;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

public class VerifierTest extends TestCase {

    public void testVerifier() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( new ClassPathResource( "Misc3.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        assertTrue( works );

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 10,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 5,
                      result.getBySeverity( Severity.NOTE ).size() );

    }

    public void testCustomRule() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        vConfiguration.getVerifyingResources().put( new ClassPathResource( "FindPatterns.drl",
                                                                           Verifier.class ),
                                                    ResourceType.DRL );

        Verifier verifier = vBuilder.newVerifier( vConfiguration );

        verifier.addResourcesToVerify( new ClassPathResource( "Misc3.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        assertTrue( works );

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 6,
                      result.getBySeverity( Severity.NOTE ).size() );

        for ( VerifierMessageBase m : result.getBySeverity( Severity.NOTE ) ) {
            assertEquals( "This pattern was found.",
                          m.getMessage() );
        }
    }

}
