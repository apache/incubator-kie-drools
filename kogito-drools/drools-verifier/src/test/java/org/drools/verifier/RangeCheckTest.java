package org.drools.verifier;

import junit.framework.Assert;

import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Test;

/**
 * 
 * @author rikkola
 *
 */
public class RangeCheckTest {

    @Test
    public void testVerifier() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        Assert.assertFalse( vBuilder.hasErrors() );
        Assert.assertEquals( 0,
                             vBuilder.getErrors().size() );

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( new ClassPathResource( "RangeTest.drl",
                                                              Verifier.class ),
                                       ResourceType.DRL );

        Assert.assertFalse( verifier.hasErrors() );
        Assert.assertEquals( 0,
                             verifier.getErrors().size() );

        boolean works = verifier.fireAnalysis();

        Assert.assertTrue( works );

        VerifierReport result = verifier.getResult();
        Assert.assertNotNull( result );

        for ( VerifierMessageBase message : result.getBySeverity( Severity.ERROR ) ) {
            System.out.println( message );
        }

        // This rule should not have errors, evereververevernever!
        Assert.assertEquals( 0,
                             result.getBySeverity( Severity.ERROR ).size() );

    }

}
