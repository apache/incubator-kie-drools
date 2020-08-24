/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.prediction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.DescrResource;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.core.io.internal.InternalResource;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.KogitoPackageSources;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.codegen.prediction.config.PredictionConfigGenerator;
import org.kie.kogito.codegen.rules.RuleCodegenError;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLFactoryModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;

import static java.util.stream.Collectors.toList;
import static org.drools.core.util.IoUtils.readBytesFromInputStream;
import static org.kie.api.io.ResourceType.determineResourceType;
import static org.kie.kogito.codegen.ApplicationGenerator.log;
import static org.kie.kogito.codegen.ApplicationGenerator.logger;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsFromResourceFromPlugin;

public class PredictionCodegen extends AbstractGenerator {

    private final List<PMMLResource> resources;
    private final List<GeneratedFile> generatedFiles = new ArrayList<>();
    private String packageName;
    private String applicationCanonicalName;
    private DependencyInjectionAnnotator annotator;
    private PredictionContainerGenerator moduleGenerator;
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    public PredictionCodegen(List<PMMLResource> resources) {
        this.resources = resources;

        // set default package name
        setPackageName(ApplicationGenerator.DEFAULT_PACKAGE_NAME);
        this.moduleGenerator = new PredictionContainerGenerator(applicationCanonicalName, resources);
    }

    public static PredictionCodegen ofCollectedResources(Collection<CollectedResource> resources) {
        List<PMMLResource> dmnResources = resources.stream()
                .filter(r -> r.resource().getResourceType() == ResourceType.PMML)
                .flatMap(r -> parsePredictions(r.basePath(), Collections.singletonList(r.resource())).stream())
                .collect(toList());
        return ofPredictions(dmnResources);
    }

    public static PredictionCodegen ofJar(Path... jarPaths) throws IOException {
        List<PMMLResource> pmmlResources = new ArrayList<>();
        for (Path jarPath : jarPaths) {
            List<Resource> resources = new ArrayList<>();
            try (ZipFile zipFile = new ZipFile(jarPath.toFile())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    ResourceType resourceType = determineResourceType(entry.getName());
                    if (resourceType == ResourceType.PMML) {
                        InternalResource resource =
                                new ByteArrayResource(readBytesFromInputStream(zipFile.getInputStream(entry)));
                        resource.setResourceType(resourceType);
                        resource.setSourcePath(entry.getName());
                        resources.add(resource);
                    }
                }
            }
            pmmlResources.addAll(parsePredictions(jarPath, resources));
        }
        return ofPredictions(pmmlResources);
    }

    public static PredictionCodegen ofPath(Path... paths) throws IOException {
        List<PMMLResource> resources = new ArrayList<>();
        for (Path path : paths) {
            Path srcPath = Paths.get(path.toString());
            try (Stream<Path> filesStream = Files.walk(srcPath)) {
                List<File> files = filesStream.filter(p -> p.toString().endsWith(".pmml"))
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                resources.addAll(parseFiles(srcPath, files));
            }
        }
        return ofPredictions(resources);
    }

    public static PredictionCodegen ofFiles(Path basePath, List<File> files) {
        return ofPredictions(parseFiles(basePath, files));
    }

    private static PredictionCodegen ofPredictions(List<PMMLResource> resources) {
        return new PredictionCodegen(resources);
    }

    private static List<PMMLResource> parseFiles(Path path, List<File> files) {
        return parsePredictions(path, files.stream().map(FileSystemResource::new).collect(toList()));
    }

    private static List<PMMLResource> parsePredictions(Path path, List<Resource> resources) {
        final InternalKnowledgeBase knowledgeBase = new KnowledgeBaseImpl("PMML", null);
        KnowledgeBuilderImpl kbuilderImpl = new KnowledgeBuilderImpl(knowledgeBase);
        List<PMMLResource> toReturn = new ArrayList<>();
        resources.forEach(resource -> {
            List<KiePMMLModel> kiePMMLModels = getKiePMMLModelsFromResourceFromPlugin(kbuilderImpl, resource);
            String modelPath = resource.getSourcePath();
            PMMLResource toAdd = new PMMLResource(kiePMMLModels, path, modelPath);
            toReturn.add(toAdd);
        });
        return toReturn;
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
        if (!resources.isEmpty()) {
            cfg.withPredictionConfig(new PredictionConfigGenerator(packageName));
        }
    }

    @Override
    public ApplicationSection section() {
        return moduleGenerator;
    }

    public List<GeneratedFile> getGeneratedFiles() {
        return generatedFiles;
    }

    public PredictionCodegen withAddons(AddonsConfig addonsConfig) {
        this.moduleGenerator.withAddons(addonsConfig);
        this.addonsConfig = addonsConfig;
        return this;
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
        this.applicationCanonicalName = packageName + ".Application";
    }

    public void setDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
    }

    public PredictionContainerGenerator moduleGenerator() {
        return moduleGenerator;
    }

    public List<GeneratedFile> generate() {
        if (resources.isEmpty()) {
            return Collections.emptyList();
        }
        ModelBuilderImpl<KogitoPackageSources> modelBuilder = new ModelBuilderImpl<>( KogitoPackageSources::dumpSources,
                new KnowledgeBuilderConfigurationImpl(getClass().getClassLoader()), new ReleaseIdImpl("dummy:dummy:0.0.0"), true, false );
        CompositeKnowledgeBuilder batch = modelBuilder.batch();
        for (PMMLResource resource : resources) {
            List<KiePMMLModel> kiepmmlModels = resource.getKiePmmlModels();
            for (KiePMMLModel model : kiepmmlModels) {
                if (model.getName() == null || model.getName().isEmpty()) {
                    String errorMessage = String.format("Model name should not be empty inside %s", resource.getModelPath());
                    throw new RuntimeException(errorMessage);
                }
                if (!(model instanceof HasSourcesMap)) {
                    String errorMessage = String.format("Expecting HasSourcesMap instance, retrieved %s inside %s",
                                                        model.getClass().getName(),
                                                        resource.getModelPath());
                    throw new RuntimeException(errorMessage);
                }
                Map<String, String> sourceMap = ((HasSourcesMap) model).getSourcesMap();
                for (Map.Entry<String, String> sourceMapEntry : sourceMap.entrySet()) {
                    String path = sourceMapEntry.getKey().replace('.', File.separatorChar) + ".java";
                    storeFile(GeneratedFile.Type.PMML, path, sourceMapEntry.getValue());
                }
                if (model instanceof KiePMMLDroolsModelWithSources) {
                    PackageDescr packageDescr = ((KiePMMLDroolsModelWithSources)model).getPackageDescr();
                    batch.add( new DescrResource( packageDescr ), ResourceType.DESCR );
                }
                if (!(model instanceof KiePMMLFactoryModel)) {
                    PMMLRestResourceGenerator resourceGenerator = new PMMLRestResourceGenerator(model, applicationCanonicalName)
                            .withDependencyInjection(annotator);
                    storeFile(GeneratedFile.Type.PMML, resourceGenerator.generatedFilePath(), resourceGenerator.generate());
                }
            }
        }

        generatedFiles.addAll( generateRules(modelBuilder, batch) );

        return generatedFiles;
    }

    private List<GeneratedFile> generateRules(ModelBuilderImpl<KogitoPackageSources> modelBuilder, CompositeKnowledgeBuilder batch) {
        try {
            batch.build();
        } catch (RuntimeException e) {
            for (DroolsError error : modelBuilder.getErrors().getErrors()) {
                logger.error(error.toString());
            }
            logger.error(e.getMessage());
            throw new RuleCodegenError(e, modelBuilder.getErrors().getErrors());
        }

        if (modelBuilder.hasErrors()) {
            for (DroolsError error : modelBuilder.getErrors().getErrors()) {
                logger.error(error.toString());
            }
            throw new RuleCodegenError(modelBuilder.getErrors().getErrors());
        }

        return generateModels( modelBuilder ).stream().map(f -> new org.kie.kogito.codegen.GeneratedFile(
                        org.kie.kogito.codegen.GeneratedFile.Type.RULE,
                        f.getPath(), f.getData())).collect(toList());
    }

    private List<org.drools.modelcompiler.builder.GeneratedFile> generateModels( ModelBuilderImpl<KogitoPackageSources> modelBuilder) {
        List<org.drools.modelcompiler.builder.GeneratedFile> toReturn = new ArrayList<>();
        for (KogitoPackageSources pkgSources : modelBuilder.getPackageSources()) {

            pkgSources.collectGeneratedFiles( toReturn );
            toReturn.add(getRuleMapperClass(pkgSources));
            org.drools.modelcompiler.builder.GeneratedFile reflectConfigSource = pkgSources.getReflectConfigSource();
            if (reflectConfigSource != null) {
                toReturn.add(new org.drools.modelcompiler.builder.GeneratedFile( org.drools.modelcompiler.builder.GeneratedFile.Type.RULE, "../../classes/" + reflectConfigSource.getPath(), new String(reflectConfigSource.getData(), StandardCharsets.UTF_8)));
            }

        }
        return toReturn;
    }

    private org.drools.modelcompiler.builder.GeneratedFile getRuleMapperClass(KogitoPackageSources pkgSources) {
        final String rulesFileName = pkgSources.getRulesFileName();
        final String fullRuleName =
                pkgSources.getModelsByUnit().values().stream().filter(i -> i.endsWith("." + rulesFileName))
                .findFirst().orElseThrow(() -> new RuntimeException("Failed to find mapped Rule file " + rulesFileName));
        final String predictionRuleMapperPath =  fullRuleName.substring(0, fullRuleName.lastIndexOf('.')) + File.separator + "PredictionRuleMapperImpl.java";
        final String predictionRuleMapperSource =
                PredictionRuleMapperGenerator.getPredictionRuleMapperSource(fullRuleName);
        return new org.drools.modelcompiler.builder.GeneratedFile( org.drools.modelcompiler.builder.GeneratedFile.Type.CLASS, predictionRuleMapperPath, predictionRuleMapperSource);
    }

    private void storeFile(GeneratedFile.Type type, String path, String source) {
        generatedFiles.add(new GeneratedFile(type, path, log(source).getBytes(StandardCharsets.UTF_8)));
    }
}