package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;

public class CauseTest extends TestCase {

    public void testCauseTrace() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertFalse( vBuilder.hasErrors() );
        assertEquals( 0,
                      vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( new ClassPathResource( "Causes.drl",
                                                              getClass() ),
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
        Collection<VerifierMessageBase> warnings = result.getBySeverity( Severity.WARNING );
        Collection<VerifierMessageBase> redundancyWarnings = new ArrayList<VerifierMessageBase>();

        for ( VerifierMessageBase verifierMessageBase : warnings ) {
            if ( verifierMessageBase.getMessageType().equals( MessageType.REDUNDANCY ) ) {
                redundancyWarnings.add( verifierMessageBase );
            }
        }

        assertEquals( 1,
                      redundancyWarnings.size() );

        VerifierMessage message = (VerifierMessage) redundancyWarnings.toArray()[0];

        assertEquals( 2,
                      message.getImpactedRules().size() );

        assertTrue( message.getImpactedRules().values().contains( "Your First Rule" ) );
        assertTrue( message.getImpactedRules().values().contains( "Your Second Rule" ) );

        // TODO: Test causes
        Cause[] causes = message.getCauses().toArray( new Cause[message.getCauses().size()] );

        assertEquals( 1,
                      causes.length );
        causes = causes[0].getCauses().toArray( new Cause[message.getCauses().size()] );
        assertEquals( 2,
                      causes.length );

        assertEquals( 0,
                      result.getBySeverity( Severity.NOTE ).size() );

    }
}
