package org.drools.verifier.redundancy;

import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.builder.VerifierImpl;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.Subsumption;
import org.junit.jupiter.api.Test;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class RedundancyTest {

    @Test
    void testVerifierLiteralRestrictionRedundancy() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("RedundantRestrictions.drl",
                        getClass()),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));
        Collection<? extends Object> redundancyList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Redundancy.class));

        assertThat(subsumptionList.size()).isEqualTo(2);
        assertThat(redundancyList.size()).isEqualTo(1);

        verifier.dispose();
    }
}
