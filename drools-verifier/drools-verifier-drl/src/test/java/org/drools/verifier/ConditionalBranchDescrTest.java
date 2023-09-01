package org.drools.verifier;

import org.drools.io.ClassPathResource;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;

import static org.assertj.core.api.Assertions.assertThat;

public class ConditionalBranchDescrTest {

    @Test
    void testVerifier() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(new ClassPathResource( "ConditionalBranchDescrTest.drl",
                        Verifier.class ),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis();

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();
//        assertEquals( 0,
//                      result.getBySeverity( Severity.ERROR ).size() );
//        assertEquals( 6,
//                      result.getBySeverity( Severity.WARNING ).size() );
//        assertEquals( 1,
//                      result.getBySeverity( Severity.NOTE ).size() );

    }

}
