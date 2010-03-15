package org.drools.verifier;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;

public class VerifyingScopeTest extends TestCase {

    public void testSingleRule() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        vConfiguration.getVerifyingResources().put( new ClassPathResource( "VerifyingScope.drl",
                                                                           Verifier.class ),
                                                    ResourceType.DRL );

        vConfiguration.addVerifyingScopes( VerifierConfiguration.VERIFYING_SCOPE_SINGLE_RULE );

        Verifier verifier = vBuilder.newVerifier( vConfiguration );

        verifier.addResourcesToVerify( new ClassPathResource( "Misc3.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        assertFalse( verifier.hasErrors() );
        assertEquals( 0,
                      verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        if ( !works ) {
            for ( VerifierError error : verifier.getErrors() ) {
                System.out.println( error.getMessage() );
            }

            fail( "Error when building in verifier" );
        }

        VerifierReport result = verifier.getResult();
        assertNotNull( result );
        assertEquals( 0,
                      result.getBySeverity( Severity.ERROR ).size() );
        assertEquals( 0,
                      result.getBySeverity( Severity.WARNING ).size() );
        assertEquals( 6,
                      result.getBySeverity( Severity.NOTE ).size() );

    }

    public void testNothing() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        vConfiguration.getVerifyingResources().put( new ClassPathResource( "VerifyingScope.drl",
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
        assertEquals( 2,
                      result.getBySeverity( Severity.NOTE ).size() );

    }

    public void testDecisionTable() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        vConfiguration.getVerifyingResources().put( new ClassPathResource( "VerifyingScope.drl",
                                                                           Verifier.class ),
                                                    ResourceType.DRL );

        vConfiguration.addVerifyingScopes( VerifierConfiguration.VERIFYING_SCOPE_DECISION_TABLE );
        vConfiguration.setAcceptRulesWithoutVerifiyingScope( false );

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
        assertEquals( 2,
                      result.getBySeverity( Severity.NOTE ).size() );

    }
}
