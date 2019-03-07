package org.kie.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.dmn.api.core.AfterGeneratingSourcesListener;
import org.kie.dmn.api.core.GeneratedSource;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.execmodelbased.DMNRuleClassFile;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceWithConfigurationImpl;

@Mojo(name = "generateDMNModel",
        requiresDependencyResolution = ResolutionScope.NONE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class GenerateDMNModelMojo extends AbstractKieMojo {

    @Parameter(required = true, defaultValue = "${project.build.directory}")
    private File targetDirectory;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    private File projectDir;

    @Parameter
    private Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(property = "generateDMNModel", defaultValue = "no")
    private String generateDMNModel;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (DMNModelMode.shouldGenerateDMNModel(generateDMNModel)) {
            generateDMNModel();
        }
    }

    private void generateDMNModel() throws MojoExecutionException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        KieServices ks = KieServices.Factory.get();

        try {
            setSystemProperties(properties);

            final KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(projectDir);

            DMNCompilerConfigurationImpl dmnCompilerConfiguration = (DMNCompilerConfigurationImpl) DMNFactory.newCompilerConfiguration();

            final List<String> compiledClassNames = new ArrayList<>();
            dmnCompilerConfiguration.setDeferredCompilation(true);
            dmnCompilerConfiguration.addListener(generatedSource -> {
                final String droolsModelCompilerPath = "/generated-sources/dmn/main/java";
                addNewCompileRoot(droolsModelCompilerPath);

                for (GeneratedSource generatedFile : generatedSource) {
                    final Path fileNameRelative = transformPathToMavenPath(generatedFile);

                    compiledClassNames.add(getCompiledClassName(fileNameRelative));

                    final Path newFile = Paths.get(targetDirectory.getPath(),
                                                   droolsModelCompilerPath,
                                                   fileNameRelative.toString());

                    createInvokerSourceFile(newFile, generatedFile.getSourceContent());
                }
            });

            InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModuleIgnoringErrors();
            List<String> dmnFiles = getDMNFIles(kieModule);
            getLog().info("dmnFiles to process: " + dmnFiles);

            DMNAssemblerService assemblerService = new DMNAssemblerService(dmnCompilerConfiguration);
            KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

            for (String dmnFile : dmnFiles) {
                compileDMNFile(kieModule, assemblerService, knowledgeBuilder, dmnFile);
            }
            createDMNFile(compiledClassNames);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

        getLog().info("DMN Model successfully generated");
    }

    private void createDMNFile(List<String> compiledClassNames) {
        final Path dmnCompiledClassFile = Paths.get(targetDirectory.getPath(), "classes", DMNRuleClassFile.RULE_CLASS_FILE_NAME);

        try {
            if (!Files.exists(dmnCompiledClassFile)) {
                Files.createDirectories(dmnCompiledClassFile.getParent());
            }
            Files.write(dmnCompiledClassFile, compiledClassNames);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write file", e);
        }
    }

    private List<String> getDMNFIles(InternalKieModule kieModule) {
        return kieModule.getFileNames()
                        .stream()
                        .filter(f -> f.endsWith("dmn"))
                        .collect(Collectors.toList());
    }

    private void compileDMNFile(InternalKieModule kieModule, DMNAssemblerService assemblerService, KnowledgeBuilder knowledgeBuilder, String dmnFile) throws Exception {
        Resource resource = kieModule.getResource(dmnFile);
        ResourceConfiguration resourceConfiguration = kieModule.getResourceConfiguration(dmnFile);

        ResourceWithConfiguration resourceWithConfiguration =
                new ResourceWithConfigurationImpl(resource, resourceConfiguration, a -> {
                }, b -> {
                });

        assemblerService.addResources(knowledgeBuilder, Collections.singletonList(resourceWithConfiguration), ResourceType.DMN);
    }

    private void createInvokerSourceFile(Path newFile, String sourceContent) {
        try {
            Files.deleteIfExists(newFile);
            Files.createDirectories(newFile.getParent());
            Path newFilePath = Files.createFile(newFile);
            Files.write(newFilePath, sourceContent.getBytes());
            getLog().info("Generating new DMN file" + newFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write file", e);
        }
    }

    private String getCompiledClassName(Path fileNameRelative) {
        return fileNameRelative.toString()
                                .replace("/", ".")
                                .replace(".java", "");
    }

    private Path transformPathToMavenPath(GeneratedSource generatedFile) {
        Path fileName = Paths.get(generatedFile.getFileName());
        Path originalFilePath = Paths.get("src/main/java");
        final Path fileNameRelative;
        if(fileName.startsWith(originalFilePath)) {
            fileNameRelative = originalFilePath.relativize(fileName);
        } else {
            fileNameRelative = fileName;
        }
        return fileNameRelative;
    }

    private void addNewCompileRoot(String droolsModelCompilerPath) {
        final String newCompileSourceRoot = targetDirectory.getPath() + droolsModelCompilerPath;
        project.addCompileSourceRoot(newCompileSourceRoot);
    }
}

