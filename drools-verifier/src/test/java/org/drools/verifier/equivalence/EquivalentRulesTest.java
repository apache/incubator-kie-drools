package org.drools.verifier.equivalence;

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
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.MessageType;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.Subsumption;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 *
 */
public class EquivalentRulesTest extends TestCase {

    public void testVerifierLiteralRestrictionRedundancy() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( "EquivalentRules.drl",
                                                                             getClass() ),
                                       ResourceType.DRL );

        for ( VerifierError error : verifier.getErrors() ) {
            System.out.println( error.getMessage() );
        }

        assertFalse( verifier.hasErrors() );

        boolean noProblems = verifier.fireAnalysis();
        assertTrue( noProblems );

        VerifierReport result = verifier.getResult();

        Collection<VerifierMessageBase> warnings = result.getBySeverity( Severity.WARNING );

        int counter = 0;
        for ( VerifierMessageBase message : warnings ) {
            //            System.out.println( message );
            if ( message.getMessageType().equals( MessageType.EQUIVALANCE ) ) {
//                                System.out.println( message );
                counter++;
            }
        }

        // Has at least one item.
        assertEquals( 1,
                      counter );

        verifier.dispose();
    }

}
