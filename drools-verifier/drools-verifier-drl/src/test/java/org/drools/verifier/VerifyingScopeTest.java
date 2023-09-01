package org.drools.verifier;

import java.util.Collections;

import org.drools.io.ClassPathResource;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class VerifyingScopeTest {

    @Test
    void testSingleRule() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        vConfiguration.getVerifyingResources().put(new ClassPathResource( "VerifyingScope.drl",
                        Verifier.class ),
                ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vConfiguration);

        verifier.addResourcesToVerify(new ClassPathResource( "Misc3.drl",
                        Verifier.class ),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis(new ScopesAgendaFilter( true,
                ScopesAgendaFilter.VERIFYING_SCOPE_SINGLE_RULE ));

        if (!works) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }

            fail("Error when building in verifier");
        }

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();
        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.WARNING).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(6);

    }

    @Test
    void testNothing() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        vConfiguration.getVerifyingResources().put(new ClassPathResource( "VerifyingScope.drl",
                        Verifier.class ),
                ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vConfiguration);

        verifier.addResourcesToVerify(new ClassPathResource( "Misc3.drl",
                        Verifier.class ),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis(new ScopesAgendaFilter( true,
                Collections.EMPTY_LIST));

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();
        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.WARNING).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(2);

    }

    @Test
    void testDecisionTable() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        vConfiguration.getVerifyingResources().put(new ClassPathResource( "VerifyingScope.drl",
                        Verifier.class ),
                ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vConfiguration);

        verifier.addResourcesToVerify(new ClassPathResource( "Misc3.drl",
                        Verifier.class ),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis(new ScopesAgendaFilter( false,
                ScopesAgendaFilter.VERIFYING_SCOPE_DECISION_TABLE ));

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();
        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.WARNING).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(2);

    }
}
