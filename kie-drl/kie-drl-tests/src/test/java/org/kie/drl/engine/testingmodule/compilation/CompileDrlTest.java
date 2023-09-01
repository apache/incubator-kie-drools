package org.kie.drl.engine.testingmodule.compilation;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.io.FileSystemResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.drl.engine.compilation.model.DrlCompilationContext;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.drl.engine.compilation.model.DrlPackageDescrSetResource;
import org.kie.drl.engine.testingmodule.utils.DrlTestUtils;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.core.service.CompilationManagerImpl;
import org.kie.internal.builder.KnowledgeBuilderFactory;

import static org.assertj.core.api.Assertions.assertThat;

class CompileDrlTest {

    private static CompilationManager compilationManager;
    private static DrlCompilationContext context;

    private static Set<File> drlFiles;

    private static Set<PackageDescr> packageDescrs;

    @BeforeAll
    static void setUp() throws IOException, DroolsParserException {
        DrlTestUtils.refreshDrlIndexFile();
        compilationManager = new CompilationManagerImpl();
        context = DrlCompilationContext.buildWithParentClassLoader(CompilationManager.class.getClassLoader());
        drlFiles = DrlTestUtils.collectDrlFiles("src/test/resources/org/drools/model/project/codegen");
        KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        DrlResourceHandler drlResourceHandler =
                new DrlResourceHandler(knowledgeBuilderConfiguration);
        packageDescrs = new HashSet<>();
        for (File drlFile : drlFiles) {
            FileSystemResource fileSystemResource = new FileSystemResource(drlFile);
            PackageDescr process = drlResourceHandler.process(fileSystemResource);
            packageDescrs.add(process);
        }
    }

    @Test
    void compileDrlFromFile() {
        String basePath = UUID.randomUUID().toString();
        DrlFileSetResource toProcess = new DrlFileSetResource(drlFiles, basePath);
        compilationManager.processResource(context, toProcess);
        assertThat(context.getGeneratedResourcesMap()).hasSize(1);
    }

    @Test
    void compileDrlFromPackageDescr() {
        String basePath = UUID.randomUUID().toString();
        DrlPackageDescrSetResource toProcess = new DrlPackageDescrSetResource(packageDescrs, basePath);
        compilationManager.processResource(context, toProcess);
        assertThat(context.getGeneratedResourcesMap()).hasSize(1);
    }

}
