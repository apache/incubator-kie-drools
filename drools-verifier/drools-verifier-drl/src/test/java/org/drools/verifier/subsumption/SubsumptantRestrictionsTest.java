package org.drools.verifier.subsumption;

import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.builder.VerifierImpl;
import org.drools.verifier.report.components.Subsumption;
import org.junit.jupiter.api.Test;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class SubsumptantRestrictionsTest {

    @Test
    void testVerifierLiteralRestrictionRedundancy1() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("SubsumptantRestriction1.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));

        assertThat(subsumptionList.size()).isEqualTo(9);

        verifier.dispose();
    }

    @Test
    void testVerifierLiteralRestrictionRedundancy2() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("SubsumptantRestriction2.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));

        assertThat(subsumptionList.size()).isEqualTo(9);

        verifier.dispose();
    }

    @Test
    void testVerifierLiteralRestrictionRedundancy3() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("SubsumptantRestriction3.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));

        assertThat(subsumptionList.size()).isEqualTo(6);

        verifier.dispose();
    }

    @Test
    void testVerifierLiteralRestrictionRedundancy4() throws Exception {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("SubsumptantRestriction4.drl",
                        getClass()),
                ResourceType.DRL);

        //        for ( VerifierError error : verifier.getMissingClasses() ) {
        //            System.out.println( error.getMessage() );
        //        }

        assertThat(verifier.hasErrors()).isFalse();

        boolean noProblems = verifier.fireAnalysis();
        assertThat(noProblems).isTrue();

        Collection<? extends Object> subsumptionList = ((VerifierImpl) verifier).getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));

        //        for ( Object object : subsumptionList ) {
        //            System.out.println( object );
        //        }

        assertThat(subsumptionList.size()).isEqualTo(4);

        verifier.dispose();
    }
}
