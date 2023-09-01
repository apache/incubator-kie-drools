package org.kie.pmml.compiler.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;

class KieCompilerServicePMMLInputStreamTest {

    private static KieCompilerService kieCompilerService;

    @BeforeAll
    static void setUp() {
        kieCompilerService = new KieCompilerServicePMMLInputStream();
    }

    @Test
    void canManageResource() throws IOException {
        String fileName = "LinearRegressionSample.pmml";
        File pmmlFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get pmmlFIle"));
        EfestoInputStreamResource toProcess = new EfestoInputStreamResource(Files.newInputStream(pmmlFile.toPath()), fileName);
        assertThat(kieCompilerService.canManageResource(toProcess)).isTrue();
        EfestoFileResource notToProcess = new EfestoFileResource(pmmlFile);
        assertThat(kieCompilerService.canManageResource(notToProcess)).isFalse();
    }

}