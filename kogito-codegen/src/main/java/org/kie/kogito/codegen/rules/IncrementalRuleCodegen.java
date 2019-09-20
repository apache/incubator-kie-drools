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

package org.kie.kogito.codegen.rules;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.io.impl.FileSystemResource;
import org.kie.kogito.codegen.GeneratedFile;
import org.drools.modelcompiler.builder.KieModuleModelMethod;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.ModelSourceClass;
import org.drools.modelcompiler.builder.PackageSources;
import org.drools.modelcompiler.builder.ProjectSourceClass;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.rules.config.RuleConfigGenerator;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.kie.kogito.codegen.ApplicationGenerator.log;

public class IncrementalRuleCodegen extends AbstractGenerator {

    private String packageName;

    public static IncrementalRuleCodegen ofPath( Path basePath) {
        try {
            Stream<File> files = Files.walk(basePath).map(Path::toFile);
            Set<Resource> resources = toResources(files);
            return new IncrementalRuleCodegen(resources);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static IncrementalRuleCodegen ofPath(Path basePath, ResourceType resourceType) {
        try {
            Stream<File> files = Files.walk(basePath).map(Path::toFile);
            Set<Resource> resources = toResources(files, resourceType);
            return new IncrementalRuleCodegen(resources);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static IncrementalRuleCodegen ofFiles(Collection<File> files, ResourceType resourceType) {
        return new IncrementalRuleCodegen(toResources(files.stream(), resourceType));
    }

    public static IncrementalRuleCodegen ofFiles(Collection<File> files) {
        return new IncrementalRuleCodegen(toResources(files.stream()));
    }

    public static IncrementalRuleCodegen ofResources(Collection<Resource> resources) {
        return new IncrementalRuleCodegen(resources);
    }

    private static Set<Resource> toResources(Stream<File> files, ResourceType resourceType) {
        return files.filter(f -> resourceType.matchesExtension(f.getName())).map(FileSystemResource::new).peek(r -> r.setResourceType(resourceType)).collect(Collectors.toSet());
    }

    private static Set<Resource> toResources(Stream<File> files) {
        return files.map(FileSystemResource::new).peek(r -> r.setResourceType(typeOf(r))).filter(r -> r.getResourceType() != null).collect(Collectors.toSet());
    }

    private static ResourceType typeOf(FileSystemResource r) {
        for (ResourceType rt : resourceTypes) {
            if (rt.matchesExtension(r.getFile().getName())) {
                return rt;
            }
        }
        return null;
    }


    private static final ResourceType[] resourceTypes = {
            ResourceType.DRL,
            ResourceType.DTABLE
    };
    private final Collection<Resource> resources;
    private RuleUnitContainerGenerator moduleGenerator;
    private final Map<String, String> labels;

    private boolean dependencyInjection;
    private DependencyInjectionAnnotator annotator;
    /**
     * used for type-resolving during codegen/type-checking
     */
    private ClassLoader contextClassLoader;

    private KieModuleModel kieModuleModel;
    private boolean hotReloadMode = false;

    @Deprecated
    public IncrementalRuleCodegen(Path basePath, Collection<File> files, ResourceType resourceType) {
        this(toResources(files.stream(), resourceType));
    }

    private IncrementalRuleCodegen(Collection<Resource> resources) {
        this.resources = resources;
        this.kieModuleModel = new KieModuleModelImpl();
        setDefaultsforEmptyKieModule(kieModuleModel);
        this.contextClassLoader = getClass().getClassLoader();
        this.labels = new HashMap<>();
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
    }

    @Override
    public ApplicationSection section() {
        return moduleGenerator;
    }

    public List<GeneratedFile> generate() {
        ReleaseIdImpl dummyReleaseId = new ReleaseIdImpl("dummy:dummy:0.0.0");

        moduleGenerator = new RuleUnitContainerGenerator();
        moduleGenerator.withDependencyInjection(annotator);

        KnowledgeBuilderConfigurationImpl configuration =
                new KnowledgeBuilderConfigurationImpl(contextClassLoader);

        ModelBuilderImpl modelBuilder = new ModelBuilderImpl( configuration, dummyReleaseId, true, hotReloadMode );

        CompositeKnowledgeBuilder batch = modelBuilder.batch();
        resources.forEach(f -> batch.add(f, f.getResourceType()));
        batch.build();

        if (modelBuilder.hasErrors()) {
            ApplicationGenerator.logger.error( modelBuilder.getErrors().toString() );
            return Collections.emptyList();
        }

        boolean hasRuleUnits = false;
        Map<Class<?>, String> unitsMap = new HashMap<>();

        ArrayList<GeneratedFile> generatedFiles = new ArrayList<>();
        List<String> fqn = new ArrayList<>();

        for (PackageSources pkgSources : modelBuilder.getPackageSources()) {
            fqn.addAll( pkgSources.getModelNames() );

            addGeneratedFiles( generatedFiles, pkgSources.getPojoSources() );
            addGeneratedFiles( generatedFiles, pkgSources.getAccumulateSources() );
            addGeneratedFile( generatedFiles, pkgSources.getMainSource() );
            addGeneratedFiles( generatedFiles, pkgSources.getRuleSources() );
            addGeneratedFile( generatedFiles, pkgSources.getDomainClassSource() );

            Collection<Class<?>> ruleUnits = pkgSources.getRuleUnits();
            if (!ruleUnits.isEmpty()) {
                hasRuleUnits = true;
                for (Class<?> ruleUnit : ruleUnits) {
                    RuleUnitSourceClass ruSource = new RuleUnitSourceClass(ruleUnit, pkgSources.getRulesFileName())
                            .withDependencyInjection(annotator)
                            .withQueries( pkgSources.getQueriesInRuleUnit( ruleUnit ) );
                    moduleGenerator.addRuleUnit(ruSource);
                    unitsMap.put(ruleUnit, ruSource.targetCanonicalName());
                }
            }
        }

        if (hasRuleUnits) {
            generatedFiles.add( new RuleUnitsRegisterClass(unitsMap).generateFile(GeneratedFile.Type.RULE) );

            for (RuleUnitSourceClass ruleUnit : moduleGenerator.getRuleUnits()) {
                // add the label id of the rule unit with value set to `rules` as resource type
                labels.put(ruleUnit.label(), "rules");
                ruleUnit.setApplicationPackageName(packageName);

                generatedFiles.add( ruleUnit.generateFile(GeneratedFile.Type.RULE) );

                RuleUnitInstanceSourceClass ruleUnitInstance = ruleUnit.instance(contextClassLoader);
                generatedFiles.add( ruleUnitInstance.generateFile(GeneratedFile.Type.RULE) );

                List<QueryEndpointSourceClass> queries = ruleUnit.queries();
                if (!queries.isEmpty()) {
                    generatedFiles.add( new RuleUnitDTOSourceClass( ruleUnit.getRuleUnitClass() ).generateFile(GeneratedFile.Type.RULE) );
                    for (QueryEndpointSourceClass query : queries) {
                        generatedFiles.add( query.generateFile( GeneratedFile.Type.QUERY ) );
                    }
                }
            }
        } else if (annotator != null && !hotReloadMode) {
            for (KieBaseModel kBaseModel : kieModuleModel.getKieBaseModels().values()) {
                for (String sessionName : kBaseModel.getKieSessionModels().keySet()) {
                    CompilationUnit cu = parse( getClass().getResourceAsStream( "/class-templates/SessionRuleUnitTemplate.java" ) );
                    ClassOrInterfaceDeclaration template = cu.findFirst( ClassOrInterfaceDeclaration.class ).get();
                    annotator.withNamedSingletonComponent(template, "$SessionName$");
                    template.setName( "SessionRuleUnit_" + sessionName );

                    template.findAll(FieldDeclaration.class).stream().filter(fd -> fd.getVariable(0).getNameAsString().equals("runtimeBuilder")).forEach(fd -> annotator.withInjection(fd));

                    template.findAll( StringLiteralExpr.class ).forEach( s -> s.setString( s.getValue().replace( "$SessionName$", sessionName ) ) );
                    generatedFiles.add(new GeneratedFile(
                            GeneratedFile.Type.RULE,
                            "org/drools/project/model/SessionRuleUnit_" + sessionName + ".java",
                            log( cu.toString() ).getBytes( StandardCharsets.UTF_8 ) ));
                }
            }
        }

        if (!hotReloadMode) {
            KieModuleModelMethod modelMethod = new KieModuleModelMethod(kieModuleModel.getKieBaseModels());
            ModelSourceClass modelSourceClass = new ModelSourceClass(
                    dummyReleaseId,
                    modelMethod,
                    fqn);

            generatedFiles.add(new GeneratedFile(
                    GeneratedFile.Type.RULE,
                    modelSourceClass.getName(),
                    modelSourceClass.generate()));

            ProjectSourceClass projectSourceClass = new ProjectSourceClass(modelMethod);
            if (annotator != null) {
                projectSourceClass.withDependencyInjection("@" + annotator.applicationComponentType());
            }

            generatedFiles.add(new GeneratedFile(
                    GeneratedFile.Type.RULE,
                    projectSourceClass.getName(),
                    projectSourceClass.generate()));
        }

        return generatedFiles;
    }

    private void addGeneratedFiles( List<GeneratedFile> generatedFiles, List<org.drools.modelcompiler.builder.GeneratedFile> source ) {
        source.forEach( s -> addGeneratedFile( generatedFiles, s ) );
    }

    private void addGeneratedFile( List<GeneratedFile> generatedFiles, org.drools.modelcompiler.builder.GeneratedFile source ) {
        ApplicationGenerator.log( source.getData() );
        generatedFiles.add( new GeneratedFile(GeneratedFile.Type.RULE, source.getPath(), source.getData()) );
    }

    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
        cfg.withRuleConfig(new RuleConfigGenerator());
    }

    public void setDependencyInjection(boolean di) {
        this.dependencyInjection = di;
    }

    public IncrementalRuleCodegen withKModule(KieModuleModel model) {
        kieModuleModel = model;
        setDefaultsforEmptyKieModule(kieModuleModel);
        return this;
    }

    public IncrementalRuleCodegen withClassLoader(ClassLoader projectClassLoader) {
        this.contextClassLoader = projectClassLoader;
        return this;
    }

    public IncrementalRuleCodegen withHotReloadMode() {
        this.hotReloadMode = true;
        return this;
    }

}
