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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KogitoKnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.drools.modelcompiler.builder.GeneratedFile;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.SessionsPoolOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.internal.builder.RuleTemplateConfiguration;
import org.kie.internal.io.ResourceTypeImpl;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratorContext;
import org.kie.kogito.codegen.KogitoPackageSources;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.io.CollectedResource;
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
import static org.kie.kogito.codegen.ApplicationGenerator.log;
import static org.kie.kogito.codegen.ApplicationGenerator.logger;

public class IncrementalRuleCodegen extends AbstractGenerator {

    public static IncrementalRuleCodegen ofCollectedResources(Collection<CollectedResource> resources) {
        List<Resource> dmnResources = resources.stream()
                .map(CollectedResource::resource)
                .filter(r -> r.getResourceType() == ResourceType.DRL || r.getResourceType() == ResourceType.DTABLE || r.getResourceType() == ResourceType.PROPERTIES)
                .collect(toList());
        return ofResources(dmnResources);
    }

    public static IncrementalRuleCodegen ofJavaResources(Collection<CollectedResource> resources) {
        List<Resource> generatedRules =
                AnnotatedClassPostProcessor.scan(
                        resources.stream()
                                .filter(r -> r.resource().getResourceType() == ResourceType.JAVA)
                                .map(r -> new File(r.resource().getSourcePath()))
                                .map(File::toPath)).generate();
        return ofResources(generatedRules);
    }

    public static IncrementalRuleCodegen ofResources(Collection<Resource> resources) {
        return new IncrementalRuleCodegen(resources);
    }

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
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;
    private boolean useRestServices = true;
    private String packageName = KnowledgeBuilderConfigurationImpl.DEFAULT_PACKAGE;
    private final boolean decisionTableSupported;
    private final Map<String, RuleUnitConfig> configs;


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

        moduleGenerator = new RuleUnitContainerGenerator(packageName);
        moduleGenerator.withDependencyInjection(annotator);

        KnowledgeBuilderConfigurationImpl configuration =
                new KogitoKnowledgeBuilderConfigurationImpl(contextClassLoader);

        ModelBuilderImpl<KogitoPackageSources> modelBuilder = new ModelBuilderImpl<>( KogitoPackageSources::dumpSources, configuration, dummyReleaseId, true, hotReloadMode );

        CompositeKnowledgeBuilder batch = modelBuilder.batch();
        resources.forEach(f -> addResource( batch, f ) );

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

    private void addResource( CompositeKnowledgeBuilder batch, Resource resource ) {
        if (resource.getResourceType() == ResourceType.PROPERTIES) {
            return;
        }
        if (resource.getResourceType() == ResourceType.DTABLE) {
            Resource resourceProps = findPropertiesResource(resource);
            if (resourceProps != null) {
                // TODO delete this method and use the one in AbstractKieModule when it will be available since drools 7.44
                ResourceConfiguration conf = loadResourceConfiguration( resource.getSourcePath(), x -> true, x -> {
                    try {
                        return resourceProps.getInputStream();
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                } );
                if  (conf instanceof DecisionTableConfiguration ) {
                    // TODO delete this method and use the one in AbstractKieModule when it will be available since drools 7.44
                    addDTableToCompiler( batch, resource, (( DecisionTableConfiguration ) conf) );
                    return;
                }
            }
        }
        batch.add( resource, resource.getResourceType() );
    }

    private Resource findPropertiesResource(Resource resource) {
        return resources.stream().filter( r -> r.getSourcePath().equals( resource.getSourcePath() + ".properties" ) ).findFirst().orElse( null );
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
                    String canonicalName = ruleUnit.getCanonicalName();
                    RuleUnitGenerator ruSource = new RuleUnitGenerator(ruleUnit, pkgSources.getRulesFileName())
                            .withDependencyInjection(annotator)
                            .withQueries(pkgSources.getQueriesInRuleUnit(canonicalName))
                            .withAddons(addonsConfig)
                            .mergeConfig(configs.get(canonicalName));

                    moduleGenerator.addRuleUnit(ruSource);
                    unitsMap.put(canonicalName, ruSource.targetCanonicalName());
                    // only Class<?> has config for now
                    addUnitConfToKieModule(ruleUnit);
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

            List<String> queryClasses = useRestServices ? generateQueriesEndpoint( errors, generatedFiles, ruleUnitHelper, ruleUnit ) : Collections.emptyList();

            generatedFiles.add( ruleUnit.generateFile( org.kie.kogito.codegen.GeneratedFile.Type.RULE) );

            RuleUnitInstanceGenerator ruleUnitInstance = ruleUnit.instance(ruleUnitHelper, queryClasses);
            generatedFiles.add( ruleUnitInstance.generateFile( org.kie.kogito.codegen.GeneratedFile.Type.RULE) );

            ruleUnit.pojo(ruleUnitHelper).ifPresent(p -> generatedFiles.add(p.generateFile( org.kie.kogito.codegen.GeneratedFile.Type.RULE)));
        }
    }

    private List<String> generateQueriesEndpoint( List<DroolsError> errors, List<org.kie.kogito.codegen.GeneratedFile> generatedFiles, RuleUnitHelper ruleUnitHelper, RuleUnitGenerator ruleUnit ) {
        List<QueryEndpointGenerator> queries = ruleUnit.queries();
        if (queries.isEmpty()) {
            return Collections.emptyList();
        }

        if (annotator == null) {
            generatedFiles.add( new RuleUnitDTOSourceClass( ruleUnit.getRuleUnitDescription(), ruleUnitHelper ).generateFile( org.kie.kogito.codegen.GeneratedFile.Type.DTO) );
        }

        return queries.stream().map( q -> generateQueryEndpoint( errors, generatedFiles, q ) )
                .flatMap( o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty() ).collect( toList() );
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

    private Optional<String> generateQueryEndpoint( List<DroolsError> errors, List<org.kie.kogito.codegen.GeneratedFile> generatedFiles, QueryEndpointGenerator query ) {
        if (addonsConfig.useMonitoring()){
            String dashboard = GrafanaConfigurationWriter.generateOperationalDashboard(operationalDashboardDmnTemplate, query.getEndpointName(), addonsConfig.useTracing());

            generatedFiles.add(new org.kie.kogito.codegen.GeneratedFile( org.kie.kogito.codegen.GeneratedFile.Type.RESOURCE,
                                                                        "dashboards/operational-dashboard-" + query.getEndpointName() + ".json",
                                                                        dashboard));
        }

        if (query.validate()) {
            generatedFiles.add( query.generateFile( org.kie.kogito.codegen.GeneratedFile.Type.QUERY ) );
            QueryGenerator queryGenerator = query.getQueryGenerator();
            generatedFiles.add( query.getQueryGenerator().generateFile( org.kie.kogito.codegen.GeneratedFile.Type.QUERY ) );
            return Optional.of( queryGenerator.getQueryClassName() );
        }

        errors.add( query.getError() );
        return Optional.empty();
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
        cfg.withRuleConfig(new RuleConfigGenerator(packageName));
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

    public IncrementalRuleCodegen withAddons(AddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
        return this;
    }

    public IncrementalRuleCodegen withRestServices(boolean useRestServices) {
        this.useRestServices = useRestServices;
        return this;
    }

    // --- TODO all the code below can be deleted when the corresponging methods will be available in AbstractKieModule since drools 7.44

    public static ResourceConfiguration loadResourceConfiguration( String fileName, Predicate<String> fileAvailable, Function<String, InputStream> fileProvider ) {
        ResourceConfiguration conf;
        Properties prop = new Properties();
        if ( fileAvailable.test( fileName + ".properties") ) {
            try ( InputStream input = fileProvider.apply( fileName + ".properties") ) {
                prop.load(input);
            } catch (IOException e) {
                throw new RuntimeException(String.format("Error loading resource configuration from file: %s.properties", fileName ), e);
            }
        }
        if (ResourceType.DTABLE.matchesExtension( fileName )) {
            int lastDot = fileName.lastIndexOf( '.' );
            if (lastDot >= 0 && fileName.length() > lastDot+1) {
                String extension = fileName.substring( lastDot+1 );
                Object confClass = prop.get( ResourceTypeImpl.KIE_RESOURCE_CONF_CLASS);
                if (confClass == null || confClass.toString().equals( ResourceConfigurationImpl.class.getCanonicalName() )) {
                    prop.setProperty( ResourceTypeImpl.KIE_RESOURCE_CONF_CLASS, DecisionTableConfigurationImpl.class.getName() );
                }
                prop.setProperty(DecisionTableConfigurationImpl.DROOLS_DT_TYPE, DecisionTableInputType.valueOf( extension.toUpperCase() ).toString());
            }
        }
        conf = prop.isEmpty() ? null : ResourceTypeImpl.fromProperties(prop);
        if (conf instanceof DecisionTableConfiguration && (( DecisionTableConfiguration ) conf).getWorksheetName() == null) {
            (( DecisionTableConfiguration ) conf).setWorksheetName( prop.getProperty( "sheets" ) );
        }
        return conf;
    }

    public static void addDTableToCompiler( CompositeKnowledgeBuilder ckbuilder, Resource resource, DecisionTableConfiguration dtableConf ) {
        addDTableToCompiler( ckbuilder, resource, dtableConf, null );
    }

    private static void addDTableToCompiler( CompositeKnowledgeBuilder ckbuilder, Resource resource, DecisionTableConfiguration dtableConf, ResourceChangeSet rcs ) {
        String sheetNames = dtableConf.getWorksheetName();
        if (sheetNames == null || sheetNames.indexOf( ',' ) < 0) {
            ckbuilder.add( resource, ResourceType.DTABLE, dtableConf, rcs );
        } else {
            for (String sheetName : sheetNames.split( "\\," ) ) {
                ckbuilder.add( resource, ResourceType.DTABLE, new DecisionTableConfigurationDelegate( dtableConf, sheetName), rcs );
            }
        }
    }

    static class DecisionTableConfigurationDelegate implements DecisionTableConfiguration {

        private final DecisionTableConfiguration delegate;
        private final String sheetName;

        DecisionTableConfigurationDelegate( DecisionTableConfiguration delegate, String sheetName ) {
            this.delegate = delegate;
            this.sheetName = sheetName;
        }

        @Override
        public void setInputType( DecisionTableInputType inputType ) {
            delegate.setInputType( inputType );

        }

        @Override
        public DecisionTableInputType getInputType() {
            return delegate.getInputType();
        }

        @Override
        public void setWorksheetName( String name ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getWorksheetName() {
            return sheetName;
        }

        @Override
        public void addRuleTemplateConfiguration( Resource template, int row, int col ) {
            delegate.addRuleTemplateConfiguration( template, row, col );
        }

        @Override
        public List<RuleTemplateConfiguration> getRuleTemplateConfigurations() {
            return delegate.getRuleTemplateConfigurations();
        }

        @Override
        public boolean isTrimCell() {
            return delegate.isTrimCell();
        }

        @Override
        public void setTrimCell( boolean trimCell ) {
            delegate.setTrimCell( trimCell );
        }

        @Override
        public Properties toProperties() {
            return delegate.toProperties();
        }

        @Override
        public ResourceConfiguration fromProperties( Properties prop ) {
            return delegate.fromProperties( prop );
        }
    }
}