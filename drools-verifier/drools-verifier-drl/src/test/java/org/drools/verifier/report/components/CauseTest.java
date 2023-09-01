package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.io.ClassPathResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;

import static org.assertj.core.api.Assertions.assertThat;

public class CauseTest {

    @Test
    @Disabled("08-APR-2011 temporally ignoring -Rikkola-")
    void testCauseTrace() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(new ClassPathResource( "Causes.drl",
                        getClass() ),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis();

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();
        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(0);
        Collection<VerifierMessageBase> warnings = result.getBySeverity(Severity.WARNING);
        Collection<VerifierMessageBase> redundancyWarnings = new ArrayList<VerifierMessageBase>();

        for (VerifierMessageBase verifierMessageBase : warnings) {
            if (verifierMessageBase.getMessageType().equals(MessageType.REDUNDANCY)) {
                redundancyWarnings.add(verifierMessageBase);
            }
        }

        assertThat(redundancyWarnings.size()).isEqualTo(1);

        VerifierMessage message = (VerifierMessage) redundancyWarnings.toArray()[0];

        //        System.out.println( message );

        assertThat(message.getImpactedRules().size()).isEqualTo(2);

        assertThat(message.getImpactedRules().containsValue("Your First Rule")).isTrue();
        assertThat(message.getImpactedRules().containsValue("Your Second Rule")).isTrue();

        Cause[] causes = message.getCauses().toArray(new Cause[message.getCauses().size()]);

        assertThat(causes.length).isEqualTo(1);
        causes = causes[0].getCauses().toArray(new Cause[causes[0].getCauses().size()]);

        assertThat(causes.length).isEqualTo(2);

        causes = causes[0].getCauses().toArray(new Cause[causes[0].getCauses().size()]);

        assertThat(causes.length).isEqualTo(1);

        causes = causes[0].getCauses().toArray(new Cause[causes[0].getCauses().size()]);

        assertThat(causes.length).isEqualTo(1);

        causes = causes[0].getCauses().toArray(new Cause[causes[0].getCauses().size()]);

        assertThat(causes.length).isEqualTo(2);

        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(0);

    }
}
