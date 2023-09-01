package org.kie.drl.engine.compilation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.drl.engine.compilation.model.DrlCompilationContext;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.drl.engine.compilation.service.KieCompilerServiceDrl;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

import static org.assertj.core.api.Assertions.assertThat;

class KieCompilerServiceDrlTest {

    private static KieCompilerService kieCompilerService;
    private static DrlCompilationContext context;

    @BeforeAll
    static void setUp() {
        kieCompilerService = new KieCompilerServiceDrl();
        context = DrlCompilationContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
    }


    @Test
    void canManageResource() throws IOException {
        Set<File> files = Files.list(Paths.get("src/test/resources"))
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toSet());
        EfestoResource<Set<File>> toProcess = new DrlFileSetResource(files, "BasePath");
        // this is really only testing the constant field "drl" so it is always true...
        assertThat(kieCompilerService.canManageResource(toProcess)).isTrue();
        EfestoResource<String> toProcess2 = () -> "EfestoResource";
        assertThat(kieCompilerService.canManageResource(toProcess2)).isFalse();
    }


    @Test
    void processResource() throws IOException {
        Set<File> files = Files.walk(Paths.get("src/test/resources"))
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toSet());
        EfestoResource<Set<File>> toProcess = new DrlFileSetResource(files, "BasePath");
        List<EfestoCompilationOutput> retrieved = kieCompilerService.processResource(toProcess, context);
        assertThat(retrieved).isNotNull().hasSize(1);
    }

}