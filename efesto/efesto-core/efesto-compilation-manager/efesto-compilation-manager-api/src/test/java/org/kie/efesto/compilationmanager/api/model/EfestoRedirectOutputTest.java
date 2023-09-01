package org.kie.efesto.compilationmanager.api.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EfestoRedirectOutputTest {

    private static ModelLocalUriId modelLocalUriId;

    @BeforeAll
    public static void setup() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        modelLocalUriId = new ModelLocalUriId(parsed);
    }

    @Test
    void constructor() {
        String targetEngine = "targetEngine";
        EfestoRedirectOutput retrieved = new EfestoRedirectOutput(modelLocalUriId, targetEngine, null) {};
        assertThat(retrieved.getModelLocalUriId()).isEqualTo(modelLocalUriId);
        assertThat(retrieved.getFullClassNames()).isNull();
    }
    @Test
    void constructorMissingRequiredTarget() {
        KieEfestoCommonException thrown = assertThrows(
                KieEfestoCommonException.class,
                () -> new EfestoRedirectOutput(modelLocalUriId, null, "content") {},
                "Expected constructor to throw, but it didn't"
        );
        String expectedMessage = "Missing required target";
        assertThat(thrown.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void constructorEmptyTarget() {
        KieEfestoCommonException thrown = assertThrows(
                KieEfestoCommonException.class,
                () -> new EfestoRedirectOutput(modelLocalUriId, "", "content") {},
                "Expected constructor to throw, but it didn't"
        );
        String expectedMessage = "Missing required target";
        assertThat(thrown.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void getTargetEngine() {
        String targetEngine = "targetEngine";
        EfestoRedirectOutput retrieved = new EfestoRedirectOutput(modelLocalUriId, targetEngine, null) {};
        assertThat(retrieved.getTargetEngine()).isEqualTo(targetEngine);
    }

    @Test
    void getContent() {
        String targetEngine = "targetEngine";
        Object content = "content";
        EfestoRedirectOutput retrieved = new EfestoRedirectOutput(modelLocalUriId, targetEngine, content) {};
        assertThat(retrieved.getContent()).isEqualTo(content);
    }

}