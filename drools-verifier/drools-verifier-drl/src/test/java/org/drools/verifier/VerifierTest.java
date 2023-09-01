package org.drools.verifier;

import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarInputStream;

import org.drools.io.ClassPathResource;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class VerifierTest {

    @Test
    void testVerifier() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        Verifier verifier = vBuilder.newVerifier();

        verifier.addResourcesToVerify(new ClassPathResource( "Misc3.drl",
                        Verifier.class ),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis();

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();
        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.WARNING).size()).isEqualTo(6);
        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(1);

    }

    @Test
    void testFactTypesFromJar() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        Verifier verifier = vBuilder.newVerifier();

        try {

            JarInputStream jar = new JarInputStream( this.getClass().getResourceAsStream("model.jar") );

            verifier.addObjectModel(jar);

        } catch (IOException e) {
            fail(e.getMessage());
        }

        verifier.addResourcesToVerify(new ClassPathResource( "imports.drl",
                        Verifier.class ),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis();

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();

        Collection<ObjectType> objectTypes = result.getVerifierData().getAll(VerifierComponentType.OBJECT_TYPE);

        assertThat(objectTypes).isNotNull();
        assertThat(objectTypes.size()).isEqualTo(3);

        Collection<Field> fields = result.getVerifierData().getAll(VerifierComponentType.FIELD);

        assertThat(fields).isNotNull();
        assertThat(fields.size()).isEqualTo(10);

    }

    @Test
    void testFactTypesFromJarAndDeclarations() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        Verifier verifier = vBuilder.newVerifier();

        try {

            JarInputStream jar = new JarInputStream( this.getClass().getResourceAsStream("model.jar") );

            verifier.addObjectModel(jar);

        } catch (IOException e) {
            fail(e.getMessage());
        }

        verifier.addResourcesToVerify(new ClassPathResource( "importsAndDeclarations.drl",
                        Verifier.class ),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis();

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();

        Collection<ObjectType> objectTypes = result.getVerifierData().getAll(VerifierComponentType.OBJECT_TYPE);

        for (ObjectType objectType : objectTypes) {
            if (objectType.getName().equals("VoiceCall")) {
                assertThat(objectType.getMetadata().keySet().size()).isEqualTo(4);
            }
        }

        assertThat(objectTypes).isNotNull();
        assertThat(objectTypes.size()).isEqualTo(4);

        Collection<Field> fields = result.getVerifierData().getAll(VerifierComponentType.FIELD);

        assertThat(fields).isNotNull();
        assertThat(fields.size()).isEqualTo(11);

    }

    @Test
    void testCustomRule() {

        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();

        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works.
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        vConfiguration.getVerifyingResources().put(new ClassPathResource( "FindPatterns.drl",
                        Verifier.class ),
                ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vConfiguration);

        verifier.addResourcesToVerify(new ClassPathResource( "Misc3.drl",
                        Verifier.class ),
                ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        boolean works = verifier.fireAnalysis();

        if (!works) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }
            fail("Could not run verifier");
        }
        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();
        assertThat(result.getBySeverity(Severity.ERROR).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.WARNING).size()).isEqualTo(0);
        assertThat(result.getBySeverity(Severity.NOTE).size()).isEqualTo(6);

        for (VerifierMessageBase m : result.getBySeverity(Severity.NOTE)) {
            assertThat(m.getMessage()).isEqualTo("This pattern was found.");
        }
    }
}
