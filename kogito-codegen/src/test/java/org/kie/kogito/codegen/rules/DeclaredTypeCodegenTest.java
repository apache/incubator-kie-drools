package org.kie.kogito.codegen.rules;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.kogito.codegen.GeneratedFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DeclaredTypeCodegenTest {

    @Test
    void ofPath() {
        DeclaredTypeCodegen incrementalRuleCodegen =
                DeclaredTypeCodegen.ofFiles(
                        Collections.singleton(
                                new File("src/test/resources/org/kie/kogito/codegen/declared/declared.drl")));
        incrementalRuleCodegen.setPackageName("com.acme");

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.generate();

        assertThat(generatedFiles).hasSize(1);

    }
}