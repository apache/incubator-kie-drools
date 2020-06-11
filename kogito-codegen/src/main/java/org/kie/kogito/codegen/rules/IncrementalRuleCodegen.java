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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.core.io.internal.InternalResource;
import org.drools.modelcompiler.builder.GeneratedFile;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.SessionsPoolOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratorContext;
import org.kie.kogito.codegen.KogitoPackageSources;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.rules.config.NamedRuleUnitConfig;
import org.kie.kogito.codegen.rules.config.RuleConfigGenerator;
import org.kie.kogito.conf.ClockType;
import org.kie.kogito.conf.EventProcessingType;
import org.kie.kogito.grafana.GrafanaConfigurationWriter;
import org.kie.kogito.rules.RuleUnitConfig;
import org.kie.kogito.rules.units.AssignableChecker;
import org.kie.kogito.rules.units.ReflectiveRuleUnitDescription;

import static java.util.stream.Collectors.toList;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.drools.core.util.IoUtils.readBytesFromInputStream;
import static org.kie.api.io.ResourceType.determineResourceType;
import static org.kie.kogito.codegen.ApplicationGenerator.log;
import static org.kie.kogito.codegen.ApplicationGenerator.logger;

public class IncrementalRuleCodegen extends AbstractGenerator {

    public static IncrementalRuleCodegen ofJar(Path jarPath) {
        Collection<Resource> resources = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile( jarPath.toFile() )) {
            Enumeration< ? extends ZipEntry> entries = zipFile.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                ResourceType resourceType = determineResourceType(entry.getName());
                if (resourceType != null) {
                    InternalResource resource = new ByteArrayResource( readBytesFromInputStream( zipFile.getInputStream( entry ) ) );
                    resource.setResourceType( resourceType );
                    resource.setSourcePath( entry.getName() );
                    resources.add( resource );
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return new IncrementalRuleCodegen(resources);
    }

    public static IncrementalRuleCodegen ofPath(Path... paths) {
        Set<Resource> resources = new HashSet<>();
        for (Path path : paths) {
            try (Stream<File> files = Files.walk( path ).map( Path::toFile )) {
                resources.addAll( toResources( files ) );
            } catch (IOException e) {
                throw new UncheckedIOException( e );
            }
        }
        return new IncrementalRuleCodegen(resources);
    }

    public static IncrementalRuleCodegen ofPath(Path basePath, ResourceType resourceType) {
        try (Stream<File> files = Files.walk(basePath).map(Path::toFile)) {
            Set<Resource> resources = toResources(files, resourceType);
            return new IncrementalRuleCodegen(resources);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static IncrementalRuleCodegen ofFiles(Collection<File> files, ResourceType resourceType) {
        return new IncrementalRuleCodegen(toResources(files.stream(), resourceType));
    }

    public static IncrementalRuleCodegen ofJavaFiles(Collection<File> files) {
        List<Resource> generatedRules =
                AnnotatedClassPostProcessor.scan(files.stream().map(File::toPath)).generate();
        return ofResources(generatedRules);
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
    private static final String operationalDashboardDmnTemplate = "/grafana-dashboard-template/operational-dashboard-template.json";
    private final Collection<Resource> resources;
    private RuleUnitContainerGenerator moduleGenerator;

    private DependencyInjectionAnnotator annotator;
    /**
     * used for type-resolving during codegen/type-checking
     */
    private ClassLoader contextClassLoader;

    private KieModuleModel kieModuleModel;
    private boolean hotReloadMode = false;
    private boolean useMonitoring = false;
    private boolean useRestServices = true;
    private String packageName;
    private final boolean decisionTableSupported;
    private final Map<String, RuleUnitConfig> configs;


    @Deprecated
    public IncrementalRuleCodegen(Path basePath, Collection<File> files, ResourceType resourceType) {
        this(toResources(files.stream(), resourceType));
    }

    private IncrementalRuleCodegen(Collection<Resource> resources) {
        this.resources = resources;
        this.kieModuleModel = new KieModuleModelImpl();
        setDefaultsforEmptyKieModule(kieModuleModel);
        this.contextClassLoader = getClass().getClassLoader();
        this.decisionTableSupported = DecisionTableFactory.getDecisionTableProvider() != null;
        this.configs = new HashMap<>();
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
    }

    @Override
    public void setContext(GeneratorContext context) {
        super.setContext(context);
        this.configs.clear();
        for (NamedRuleUnitConfig cfg : NamedRuleUnitConfig.fromContext(context)) {
            this.configs.put(cfg.getCanonicalName(), cfg.getConfig());
        }
    }

    @Override
    public ApplicationSection section() {
        return moduleGenerator;
    }

    public List<org.kie.kogito.codegen.GeneratedFile> generate() {
        ReleaseIdImpl dummyReleaseId = new ReleaseIdImpl("dummy:dummy:0.0.0");
        if (!decisionTableSupported &&
                resources.stream().anyMatch(r -> r.getResourceType() == ResourceType.DTABLE)) {
            throw new MissingDecisionTableDependencyError();
        }

        moduleGenerator = new RuleUnitContainerGenerator();
        moduleGenerator.withDependencyInjection(annotator);

        KnowledgeBuilderConfigurationImpl configuration =
                new KnowledgeBuilderConfigurationImpl(contextClassLoader);

        ModelBuilderImpl<KogitoPackageSources> modelBuilder = new ModelBuilderImpl<>( KogitoPackageSources::dumpSources, configuration, dummyReleaseId, true, hotReloadMode );

        CompositeKnowledgeBuilder batch = modelBuilder.batch();
        resources.forEach(f -> batch.add(f, f.getResourceType()));

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

        Map<String, String> unitsMap = new HashMap<>();
        List<org.drools.modelcompiler.builder.GeneratedFile> modelFiles = new ArrayList<>();
        Map<String, String> modelsByUnit = new HashMap<>();

        boolean hasRuleUnits = generateModels( modelBuilder, unitsMap, modelFiles, modelsByUnit );

        List<org.kie.kogito.codegen.GeneratedFile> generatedFiles =
                modelFiles.stream().map(f -> new org.kie.kogito.codegen.GeneratedFile(
                        org.kie.kogito.codegen.GeneratedFile.Type.RULE,
                        f.getPath(), f.getData())).collect(toList());

        List<DroolsError> errors = new ArrayList<>();

        if (hasRuleUnits) {
            generateRuleUnits( errors, generatedFiles );
        } else if (annotator != null && !hotReloadMode) {
            generateSessionUnits( generatedFiles );
        }

        generateProject( dummyReleaseId, modelsByUnit, generatedFiles );

        if (!errors.isEmpty()) {
            throw new RuleCodegenError(errors);
        }

        return generatedFiles;
    }

    private boolean generateModels( ModelBuilderImpl<KogitoPackageSources> modelBuilder, Map<String, String> unitsMap, List<GeneratedFile> modelFiles, Map<String, String> modelsByUnit ) {
        boolean hasRuleUnits = false;
        for (KogitoPackageSources pkgSources : modelBuilder.getPackageSources()) {
            pkgSources.getModelsByUnit().forEach( (unit, model) -> modelsByUnit.put( ruleUnit2KieBaseName( unit ), model ) );

            pkgSources.collectGeneratedFiles( modelFiles );

            GeneratedFile reflectConfigSource = pkgSources.getReflectConfigSource();
            if (reflectConfigSource != null) {
                modelFiles.add(new GeneratedFile(GeneratedFile.Type.RULE, "../../classes/" + reflectConfigSource.getPath(), new String(reflectConfigSource.getData(), StandardCharsets.UTF_8)));
            }

            Collection<RuleUnitDescription> ruleUnits = pkgSources.getRuleUnits();
            if (!ruleUnits.isEmpty()) {
                hasRuleUnits = true;
                for (RuleUnitDescription ruleUnit : ruleUnits) {
                    RuleUnitGenerator ruSource = new RuleUnitGenerator(ruleUnit, pkgSources.getRulesFileName())
                            .withDependencyInjection(annotator)
                            .withQueries( pkgSources.getQueriesInRuleUnit( ruleUnit.getCanonicalName() ) )
                            .withMonitoring(useMonitoring);

                    moduleGenerator.addRuleUnit(ruSource);
                    unitsMap.put(ruleUnit.getCanonicalName(), ruSource.targetCanonicalName());
                    // only Class<?> has config for now
                    addUnitConfToKieModule( ruleUnit );
                }
            }
        }
        return hasRuleUnits;
    }

    private void generateProject( ReleaseIdImpl dummyReleaseId, Map<String, String> modelsByUnit, List<org.kie.kogito.codegen.GeneratedFile> generatedFiles ) {
        KieModuleModelMethod modelMethod = new KieModuleModelMethod( kieModuleModel.getKieBaseModels() );
        ModelSourceClass modelSourceClass = new ModelSourceClass( dummyReleaseId, modelMethod, modelsByUnit );

        generatedFiles.add(new org.kie.kogito.codegen.GeneratedFile(
                org.kie.kogito.codegen.GeneratedFile.Type.RULE,
                modelSourceClass.getName(),
                modelSourceClass.generate()));

        ProjectSourceClass projectSourceClass = new ProjectSourceClass(modelMethod);
        if (annotator != null) {
            projectSourceClass.withDependencyInjection("@" + annotator.applicationComponentType());
        }

        generatedFiles.add(new org.kie.kogito.codegen.GeneratedFile(
                org.kie.kogito.codegen.GeneratedFile.Type.RULE,
                projectSourceClass.getName(),
                projectSourceClass.generate()));
    }

    private void generateRuleUnits( List<DroolsError> errors, List<org.kie.kogito.codegen.GeneratedFile> generatedFiles ) {
        RuleUnitHelper ruleUnitHelper = new RuleUnitHelper();

        if (annotator != null) {
            generatedFiles.add( new org.kie.kogito.codegen.GeneratedFile( org.kie.kogito.codegen.GeneratedFile.Type.JSON_MAPPER,
                    packageName.replace('.', '/') + "/KogitoObjectMapper.java", annotator.objectMapperInjectorSource(packageName) ) );
        }

        for (RuleUnitGenerator ruleUnit : moduleGenerator.getRuleUnits()) {
            initRuleUnitHelper( ruleUnitHelper, ruleUnit.getRuleUnitDescription() );

            // add the label id of the rule unit with value set to `rules` as resource type
            this.addLabel(ruleUnit.label(), "rules");
            ruleUnit.setApplicationPackageName(packageName);

            generatedFiles.add( ruleUnit.generateFile( org.kie.kogito.codegen.GeneratedFile.Type.RULE) );

            RuleUnitInstanceGenerator ruleUnitInstance = ruleUnit.instance(ruleUnitHelper);
            generatedFiles.add( ruleUnitInstance.generateFile( org.kie.kogito.codegen.GeneratedFile.Type.RULE) );

            ruleUnit.pojo(ruleUnitHelper).ifPresent(p -> generatedFiles.add(p.generateFile( org.kie.kogito.codegen.GeneratedFile.Type.RULE)));

            if ( useRestServices ) {
                generateQueriesEndpoint( errors, generatedFiles, ruleUnitHelper, ruleUnit );
            }
        }
    }

    private void generateQueriesEndpoint( List<DroolsError> errors, List<org.kie.kogito.codegen.GeneratedFile> generatedFiles, RuleUnitHelper ruleUnitHelper, RuleUnitGenerator ruleUnit ) {
        List<QueryEndpointGenerator> queries = ruleUnit.queries();
        if (!queries.isEmpty()) {
            if (annotator == null) {
                generatedFiles.add( new RuleUnitDTOSourceClass( ruleUnit.getRuleUnitDescription(), ruleUnitHelper ).generateFile( org.kie.kogito.codegen.GeneratedFile.Type.DTO) );
            }

            for (QueryEndpointGenerator query : queries) {
                generateQueryEndpoint( errors, generatedFiles, query );
            }
        }
    }

    private void initRuleUnitHelper( RuleUnitHelper ruleUnitHelper, RuleUnitDescription ruleUnitDesc ) {
        if (ruleUnitDesc instanceof ReflectiveRuleUnitDescription ) {
            ruleUnitHelper.setAssignableChecker( ( ( ReflectiveRuleUnitDescription ) ruleUnitDesc).getAssignableChecker() );
        } else {
            if (ruleUnitHelper.getAssignableChecker() == null) {
                ruleUnitHelper.setAssignableChecker( AssignableChecker.create(contextClassLoader, hotReloadMode) );
            }
        }
    }

    private void generateQueryEndpoint( List<DroolsError> errors, List<org.kie.kogito.codegen.GeneratedFile> generatedFiles, QueryEndpointGenerator query ) {
        if (useMonitoring){
            String dashboard = GrafanaConfigurationWriter.generateOperationalDashboard(operationalDashboardDmnTemplate, query.getEndpointName());

            generatedFiles.add(new org.kie.kogito.codegen.GeneratedFile( org.kie.kogito.codegen.GeneratedFile.Type.RESOURCE,
                                                                        "dashboards/operational-dashboard-" + query.getEndpointName() + ".json",
                                                                        dashboard));
        }

        if (query.validate()) {
            generatedFiles.add( query.generateFile( org.kie.kogito.codegen.GeneratedFile.Type.QUERY ) );
        } else {
            errors.add( query.getError() );
        }
    }

    private void generateSessionUnits( List<org.kie.kogito.codegen.GeneratedFile> generatedFiles ) {
        for (KieBaseModel kBaseModel : kieModuleModel.getKieBaseModels().values()) {
            for (String sessionName : kBaseModel.getKieSessionModels().keySet()) {
                CompilationUnit cu = parse( getClass().getResourceAsStream( "/class-templates/SessionRuleUnitTemplate.java" ) );
                ClassOrInterfaceDeclaration template = cu.findFirst( ClassOrInterfaceDeclaration.class ).get();
                annotator.withNamedSingletonComponent(template, "$SessionName$");
                template.setName( "SessionRuleUnit_" + sessionName );

                template.findAll( FieldDeclaration.class).stream().filter( fd -> fd.getVariable(0).getNameAsString().equals("runtimeBuilder")).forEach( fd -> annotator.withInjection(fd));

                template.findAll( StringLiteralExpr.class ).forEach( s -> s.setString( s.getValue().replace( "$SessionName$", sessionName ) ) );
                generatedFiles.add(new org.kie.kogito.codegen.GeneratedFile(
                        org.kie.kogito.codegen.GeneratedFile.Type.RULE,
                        "org/drools/project/model/SessionRuleUnit_" + sessionName + ".java",
                        log( cu.toString() ) ));
            }
        }
    }

    private void addUnitConfToKieModule(RuleUnitDescription ruleUnitDescription) {
        KieBaseModel unitKieBaseModel = kieModuleModel.newKieBaseModel(ruleUnit2KieBaseName(ruleUnitDescription.getCanonicalName()));
        unitKieBaseModel.setEventProcessingMode(org.kie.api.conf.EventProcessingOption.CLOUD);
        unitKieBaseModel.addPackage(ruleUnitDescription.getPackageName());

        // merge config from the descriptor with configs from application.conf
        // application.conf overrides any other config
        RuleUnitConfig config =
                ruleUnitDescription.getConfig()
                        .merged(configs.get(ruleUnitDescription.getCanonicalName()));

        OptionalInt sessionsPool = config.getSessionPool();
        if (sessionsPool.isPresent()) {
            unitKieBaseModel.setSessionsPool(SessionsPoolOption.get(sessionsPool.getAsInt()));
        }
        EventProcessingType eventProcessingType = config.getDefaultedEventProcessingType();
        if (eventProcessingType == EventProcessingType.STREAM) {
            unitKieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
        }

        KieSessionModel unitKieSessionModel = unitKieBaseModel.newKieSessionModel(ruleUnit2KieSessionName(ruleUnitDescription.getCanonicalName()));
        unitKieSessionModel.setType(KieSessionModel.KieSessionType.STATEFUL);
        ClockType clockType = config.getDefaultedClockType();
        if (clockType == ClockType.PSEUDO) {
            unitKieSessionModel.setClockType(ClockTypeOption.PSEUDO);
        }
    }

    private String ruleUnit2KieBaseName(String ruleUnit) {
        return ruleUnit.replace( '.', '$' )  + "KieBase";
    }

    private String ruleUnit2KieSessionName(String ruleUnit) {
        return ruleUnit.replace( '.', '$' )  + "KieSession";
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
        cfg.withRuleConfig(new RuleConfigGenerator());
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

    public IncrementalRuleCodegen withMonitoring(boolean useMonitoring) {
        this.useMonitoring = useMonitoring;
        return this;
    }

    public IncrementalRuleCodegen withRestServices(boolean useRestServices) {
        this.useRestServices = useRestServices;
        return this;
    }
}