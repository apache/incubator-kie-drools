package org.drools.verifier.redundancy;

import org.drools.verifier.TestBaseOld;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.MessageType;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class WarningsTest extends TestBaseOld {

    @Test
    @Disabled("08-APR-2011 temporally ignoring -Rikkola-")
    void testRedundantRules() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("RedundantRules1.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        VerifierReport result = verifier.getResult();

        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.WARNING);

        int counter = 0;
        for (VerifierMessageBase message : warnings) {
            //            System.out.println( message );
            if (message.getMessageType().equals(MessageType.REDUNDANCY)) {
                //                System.out.println( message );
                counter++;
            }
        }

        assertThat(counter).isEqualTo(1);

        verifier.dispose();
    }
}
