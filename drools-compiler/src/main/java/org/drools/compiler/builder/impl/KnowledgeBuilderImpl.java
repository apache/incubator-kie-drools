/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.conf.DecisionTableConfigurationImpl;
import org.drools.compiler.builder.impl.errors.MissingImplementationException;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.builder.impl.processors.CompositePackageCompilationPhase;
import org.drools.compiler.builder.impl.processors.ConsequenceCompilationPhase;
import org.drools.compiler.builder.impl.processors.FunctionCompiler;
import org.drools.compiler.builder.impl.processors.PackageCompilationPhase;
import org.drools.compiler.builder.impl.processors.ReteCompiler;
import org.drools.compiler.builder.impl.processors.RuleCompiler;
import org.drools.compiler.builder.impl.processors.RuleValidator;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.compiler.DroolsWarning;
import org.drools.compiler.compiler.DuplicateFunction;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.ProcessBuilder;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.compiler.compiler.ResourceTypeDeclarationWarning;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.core.base.ObjectType;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.RuleBase;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.rule.Function;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.rule.TypeDeclaration;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.drl.extensions.GuidedRuleTemplateFactory;
import org.drools.drl.extensions.GuidedRuleTemplateProvider;
import org.drools.drl.extensions.ResourceConversionResult;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.ParserError;
import org.drools.drl.parser.lang.ExpanderException;
import org.drools.drl.parser.lang.dsl.DSLMappingFile;
import org.drools.drl.parser.lang.dsl.DSLTokenizedMappingFile;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.util.IoUtils;
import org.drools.util.io.BaseResource;
import org.drools.util.io.InternalResource;
import org.drools.util.io.ReaderResource;
import org.drools.wiring.api.ComponentsFactory;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResultSeverity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class KnowledgeBuilderImpl implements InternalKnowledgeBuilder, TypeDeclarationContext, GlobalVariableContext {

    protected static final transient Logger logger = LoggerFactory.getLogger(KnowledgeBuilderImpl.class);

    private final PackageRegistryManagerImpl pkgRegistryManager;

    private final BuildResultCollectorImpl results;

    private final KnowledgeBuilderConfigurationImpl configuration;

    /**
     * Optional RuleBase for incremental live building
     */
    private InternalKnowledgeBase kBase;

    /**
     * default dialect
     */
    private final String defaultDialect;

    private final ClassLoader rootClassLoader;

    private int parallelRulesBuildThreshold;

    private final GlobalVariableContext globals = new GlobalVariableContextImpl();

    private List<DSLTokenizedMappingFile> dslFiles;

    private final org.drools.compiler.compiler.ProcessBuilder processBuilder;

    private final Stack<List<Resource>> buildResources = new Stack<>();

    private AssetFilter assetFilter = null;

    private TypeDeclarationManagerImpl typeDeclarationManager;

    private Map<String, Object> builderCache;

    private ReleaseId releaseId;

    private BuildContext buildContext;

    /**
     * Use this when package is starting from scratch.
     */
    public KnowledgeBuilderImpl() {
        this((InternalKnowledgeBase) null,
             null);
    }

    /**
     * This will allow you to merge rules into this pre existing package.
     */

    public KnowledgeBuilderImpl(final InternalKnowledgePackage pkg) {
        this(pkg,
             null);
    }

    public KnowledgeBuilderImpl(final InternalKnowledgeBase kBase) {
        this(kBase,
             null);
    }

    /**
     * Pass a specific configuration for the PackageBuilder
     * <p>
     * PackageBuilderConfiguration is not thread safe and it also contains
     * state. Once it is created and used in one or more PackageBuilders it
     * should be considered immutable. Do not modify its properties while it is
     * being used by a PackageBuilder.
     */
    public KnowledgeBuilderImpl(final KnowledgeBuilderConfigurationImpl configuration) {
        this((InternalKnowledgeBase) null,
             configuration);
    }

    public KnowledgeBuilderImpl(InternalKnowledgePackage pkg,
                                KnowledgeBuilderConfigurationImpl configuration) {
        if (configuration == null) {
            this.configuration = new KnowledgeBuilderConfigurationImpl();
        } else {
            this.configuration = configuration;
        }

        this.rootClassLoader = this.configuration.getClassLoader();

        this.defaultDialect = this.configuration.getDefaultDialect();

        this.parallelRulesBuildThreshold = this.configuration.getParallelRulesBuildThreshold();

        this.results = new BuildResultCollectorImpl();

        this.pkgRegistryManager =
                new PackageRegistryManagerImpl(
                        this.configuration, this, this);

        PackageRegistry pkgRegistry = new PackageRegistry(rootClassLoader, this.configuration, pkg);
        pkgRegistry.setDialect(this.defaultDialect);
        this.pkgRegistryManager.getPackageRegistry().put(pkg.getName(),
                                pkgRegistry);

        // add imports to pkg registry
        for (final ImportDeclaration implDecl : pkg.getImports().values()) {
            pkgRegistry.addImport(new ImportDescr(implDecl.getTarget()));
        }

        processBuilder = ProcessBuilderFactory.newProcessBuilder(this);
        this.typeDeclarationManager = new TypeDeclarationManagerImpl(createTypeDeclarationBuilder(), this.kBase);
    }

    public KnowledgeBuilderImpl(InternalKnowledgeBase kBase,
                                KnowledgeBuilderConfigurationImpl configuration) {
        if (configuration == null) {
            this.configuration = new KnowledgeBuilderConfigurationImpl();
        } else {
            this.configuration = configuration;
        }

        if (kBase != null) {
            this.rootClassLoader = kBase.getRootClassLoader();
        } else {
            this.rootClassLoader = this.configuration.getClassLoader();
        }

        this.defaultDialect = this.configuration.getDefaultDialect();

        this.parallelRulesBuildThreshold = this.configuration.getParallelRulesBuildThreshold();

        this.results = new BuildResultCollectorImpl();

        this.kBase = kBase;

        this.pkgRegistryManager =
                new PackageRegistryManagerImpl(
                        this.configuration, this, this);

        processBuilder = ProcessBuilderFactory.newProcessBuilder(this);

        this.typeDeclarationManager = new TypeDeclarationManagerImpl(createTypeDeclarationBuilder(), this.kBase);
    }

    private TypeDeclarationBuilder createTypeDeclarationBuilder() {
        TypeDeclarationBuilderFactory typeDeclarationBuilderFactory =
                Optional.ofNullable(KieService.load(TypeDeclarationBuilderFactory.class))
                        .orElse(new DefaultTypeDeclarationBuilderFactory());

        return typeDeclarationBuilderFactory.createTypeDeclarationBuilder(this);
    }

    public ReleaseId getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(ReleaseId releaseId ) {
        this.releaseId = releaseId;
    }

    public BuildContext getBuildContext() {
        if (buildContext == null) {
            buildContext = createBuildContext();
        }
        return buildContext;
    }

    protected BuildContext createBuildContext() {
        return new BuildContext();
    }

    public void setBuildContext(BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    public TypeDeclarationBuilder getTypeBuilder() {
        return typeDeclarationManager.getTypeDeclarationBuilder();
    }

    /**
     * Load a rule package from DRL source.
     *
     * @throws DroolsParserException
     * @throws java.io.IOException
     */
    public void addPackageFromDrl(final Reader reader) throws DroolsParserException,
            IOException {
        addPackageFromDrl(reader, new ReaderResource(reader, ResourceType.DRL));
    }

    /**
     * Load a rule package from DRL source and associate all loaded artifacts
     * with the given resource.
     *
     * @param reader
     * @param sourceResource the source resource for the read artifacts
     * @throws DroolsParserException
     */
    public void addPackageFromDrl(final Reader reader,
                                  final Resource sourceResource) throws DroolsParserException, IOException {
        final DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        final PackageDescr pkg = parser.parse(sourceResource, reader);
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            addBuilderResult(new ParserError(sourceResource, "Parser returned a null Package", 0, 0));
        }

        if (!parser.hasErrors()) {
            addPackageWithResource(pkg, sourceResource);
        }
    }

    public void addPackageFromDecisionTable(Resource resource,
                                            ResourceConfiguration configuration) throws DroolsParserException,
            IOException {
        addPackageWithResource(decisionTableToPackageDescr(resource, configuration), resource);
    }

    PackageDescr decisionTableToPackageDescr(Resource resource,
                                             ResourceConfiguration configuration) throws DroolsParserException {
        DecisionTableConfiguration dtableConfiguration = configuration instanceof DecisionTableConfiguration ?
                (DecisionTableConfiguration) configuration :
                new DecisionTableConfigurationImpl();

        if (!dtableConfiguration.getRuleTemplateConfigurations().isEmpty()) {
            List<String> generatedDrls = DecisionTableFactory.loadFromInputStreamWithTemplates(resource, dtableConfiguration);
            if (generatedDrls.size() == 1) {
                return generatedDrlToPackageDescr(resource, generatedDrls.get(0));
            }
            CompositePackageDescr compositePackageDescr = null;
            for (String generatedDrl : generatedDrls) {
                PackageDescr packageDescr = generatedDrlToPackageDescr(resource, generatedDrl);
                if (packageDescr != null) {
                    if (compositePackageDescr == null) {
                        compositePackageDescr = new CompositePackageDescr(resource, packageDescr);
                    } else {
                        compositePackageDescr.addPackageDescr(resource, packageDescr);
                    }
                }
            }
            return compositePackageDescr;
        }

        dtableConfiguration.setTrimCell( this.configuration.isTrimCellsInDTable() );

        String generatedDrl = DecisionTableFactory.loadFromResource(resource, dtableConfiguration);
        return generatedDrlToPackageDescr(resource, generatedDrl);
    }

    private PackageDescr generatedDrlToPackageDescr(Resource resource, String generatedDrl) throws DroolsParserException {
        // dump the generated DRL if the dump dir was configured
        if (this.configuration.getDumpDir() != null) {
            dumpDrlGeneratedFromDTable(this.configuration.getDumpDir(), generatedDrl, resource.getSourcePath());
        }

        DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        PackageDescr pkg = parser.parse(resource, new StringReader(generatedDrl));
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            addBuilderResult(new ParserError(resource, "Parser returned a null Package", 0, 0));
        } else {
            pkg.setResource(resource);
        }
        return parser.hasErrors() ? null : pkg;
    }

    PackageDescr generatedDslrToPackageDescr(Resource resource, String dslr) throws DroolsParserException {
        return dslrReaderToPackageDescr(resource, new StringReader(dslr));
    }

    private void dumpDrlGeneratedFromDTable(File dumpDir, String generatedDrl, String srcPath) {
        String fileName = srcPath != null ? srcPath : "decision-table-" + UUID.randomUUID();
        if (releaseId != null) {
            fileName = releaseId.getGroupId() + "_" + releaseId.getArtifactId() + "_" + fileName;
        }
        File dumpFile = createDumpDrlFile(dumpDir, fileName, ".drl");
        try {
            IoUtils.write(dumpFile, generatedDrl.getBytes(IoUtils.UTF8_CHARSET));
        } catch (IOException ex) {
            // nothing serious, just failure when writing the generated DRL to file, just log the exception and continue
            logger.warn("Can't write the DRL generated from decision table to file " + dumpFile.getAbsolutePath() + "!\n" +
                                Arrays.toString(ex.getStackTrace()));
        }
    }

    public static File createDumpDrlFile(File dumpDir, String fileName, String extension) {
        return new File(dumpDir, fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]+", "_") + extension);
    }

    public void addPackageFromTemplate(Resource resource) throws DroolsParserException,
            IOException {
        addPackageWithResource(templateToPackageDescr(resource), resource);
    }

    PackageDescr templateToPackageDescr(Resource resource) throws DroolsParserException, IOException {
        GuidedRuleTemplateProvider guidedRuleTemplateProvider = GuidedRuleTemplateFactory.getGuidedRuleTemplateProvider();
        if (guidedRuleTemplateProvider == null) {
            throw new MissingImplementationException(resource, "drools-workbench-models-guided-template");
        }
        ResourceConversionResult conversionResult = guidedRuleTemplateProvider.loadFromInputStream(resource.getInputStream());
        return conversionResultToPackageDescr(resource, conversionResult);
    }

    private PackageDescr conversionResultToPackageDescr(Resource resource, ResourceConversionResult resourceConversionResult)
            throws DroolsParserException {
        ResourceType resourceType = resourceConversionResult.getType();
        if (ResourceType.DSLR.equals(resourceType)) {
            return generatedDslrToPackageDescr(resource, resourceConversionResult.getContent());
        } else if (ResourceType.DRL.equals(resourceType)) {
            return generatedDrlToPackageDescr(resource, resourceConversionResult.getContent());
        } else {
            throw new RuntimeException("Converting generated " + resourceType + " into PackageDescr is not supported!");
        }
    }

    public void addPackageFromDrl(Resource resource) throws DroolsParserException,
            IOException {
        addPackageWithResource(new DrlResourceHandler(configuration).process(resource), resource);
    }

    public void addPackageFromDslr(final Resource resource) throws DroolsParserException,
            IOException {
        addPackageWithResource(dslrToPackageDescr(resource), resource);
    }

    PackageDescr dslrToPackageDescr(Resource resource) throws DroolsParserException,
            IOException {
        return dslrReaderToPackageDescr(resource, resource.getReader());
    }

    private PackageDescr dslrReaderToPackageDescr(Resource resource, Reader dslrReader) throws DroolsParserException {
        boolean hasErrors;
        PackageDescr pkg;

        DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        DefaultExpander expander = getDslExpander();

        try {
            try {
                if (expander == null) {
                    expander = new DefaultExpander();
                }
                String str = expander.expand(dslrReader);
                if (expander.hasErrors()) {
                    for (ExpanderException error : expander.getErrors()) {
                        error.setResource(resource);
                        addBuilderResult(error);
                    }
                }

                pkg = parser.parse(resource, str);
                this.results.addAll(parser.getErrors());
                hasErrors = parser.hasErrors();
            } finally {
                if (dslrReader != null) {
                    dslrReader.close();
                }
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return hasErrors ? null : pkg;
    }

    public void addDsl(Resource resource) throws IOException {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();

        try (Reader reader = resource.getReader()) {
            if (!file.parseAndLoad(reader)) {
                this.results.addAll(file.getErrors());
            }
            if (this.dslFiles == null) {
                this.dslFiles = new ArrayList<>();
            }
            this.dslFiles.add(file);
        }
    }

    /**
     * Add a ruleflow (.rfm) asset to this package.
     */
    public void addRuleFlow(Reader processSource) {
        addKnowledgeResource(
                new ReaderResource(processSource, ResourceType.DRF),
                ResourceType.DRF,
                null);
    }

    @Deprecated
    public void addProcessFromXml(Resource resource) {
        addKnowledgeResource(
                resource,
                resource.getResourceType(),
                resource.getConfiguration());
    }

    public ProcessBuilder getProcessBuilder() {
        return processBuilder;
    }

    @Deprecated
    public void addProcessFromXml( Reader processSource) {
        addProcessFromXml(new ReaderResource(processSource, ResourceType.DRF));
    }

    public void addKnowledgeResource(Resource resource,
                                     ResourceType type,
                                     ResourceConfiguration configuration) {
        try {
            ((InternalResource) resource).setResourceType(type);
            if (ResourceType.DRL.equals(type)) {
                addPackageFromDrl(resource);
            } else if (ResourceType.GDRL.equals(type)) {
                addPackageFromDrl(resource);
            } else if (ResourceType.RDRL.equals(type)) {
                addPackageFromDrl(resource);
            } else if (ResourceType.DESCR.equals(type)) {
                addPackageFromDrl(resource);
            } else if (ResourceType.DSLR.equals(type)) {
                addPackageFromDslr(resource);
            } else if (ResourceType.RDSLR.equals(type)) {
                addPackageFromDslr(resource);
            } else if (ResourceType.DSL.equals(type)) {
                addDsl(resource);
            } else if (ResourceType.DTABLE.equals(type)) {
                addPackageFromDecisionTable(resource, configuration);
            } else if (ResourceType.XSD.equals(type)) {
                addPackageFromXSD(resource, configuration);
            } else if (ResourceType.TDRL.equals(type)) {
                addPackageFromDrl(resource);
            } else if (ResourceType.TEMPLATE.equals(type)) {
                addPackageFromTemplate(resource);
            } else {
                addPackageForExternalType(resource, type, configuration);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    void addPackageForExternalType(Resource resource,
                                   ResourceType type,
                                   ResourceConfiguration configuration) throws Exception {
        KieAssemblers assemblers = KieService.load(KieAssemblers.class);

        assemblers.addResourceAfterRules(this,
                               resource,
                               type,
                               configuration);
    }

    @Deprecated
    void addPackageForExternalType(ResourceType type, List<ResourceWithConfiguration> resources) throws Exception {
        KieAssemblers assemblers = KieService.load(KieAssemblers.class);

        assemblers.addResourcesAfterRules(this, resources, type);
    }

    void addPackageFromXSD(Resource resource, ResourceConfiguration configuration) throws IOException {
        if (configuration != null) {
            ComponentsFactory.addPackageFromXSD( this, resource, configuration );
        }
    }

    /**
     * Adds a package from a Descr/AST also triggering its compilation
     * and the generation of the corresponding rete/phreak network
     */
    @Override
    public final void addPackage(final PackageDescr packageDescr) {
        addPackageWithResource(packageDescr, null);
    }

    // this is only overridden by org.drools.verifier.builder.VerifierPackageBuilder.InnerBuilder
    protected void addPackageWithResource(PackageDescr packageDescr, Resource resource) {
        PackageRegistry pkgRegistry = getOrCreatePackageRegistry(packageDescr);
        if (pkgRegistry == null) {
            return;
        }

        // merge into existing package
        PackageCompilationPhase packageProcessor =
                new PackageCompilationPhase(this,
                        kBase,
                        configuration,
                        typeDeclarationManager.getTypeDeclarationBuilder(),
                        assetFilter,
                        pkgRegistry,
                        packageDescr,
                        resource);
        packageProcessor.process();
        this.results.addAll(packageProcessor.getResults());

        compileKnowledgePackages(packageDescr, pkgRegistry, resource);
        wireAllRules();
        compileRete(pkgRegistry, packageDescr);
    }

    private void compileKnowledgePackages(PackageDescr packageDescr, PackageRegistry pkgRegistry, Resource resource) {
        pkgRegistry.setDialect(getPackageDialect(packageDescr));
        PackageRegistry packageRegistry = this.pkgRegistryManager.getPackageRegistry(packageDescr.getNamespace());
        Map<String, AttributeDescr> packageAttributes = this.pkgRegistryManager.getPackageAttributes().get(packageDescr.getNamespace());

        List<CompilationPhase> phases = asList(
                new RuleValidator(packageRegistry, packageDescr, configuration), // validateUniqueRuleNames
                new FunctionCompiler(packageDescr, pkgRegistry, assetFilter, rootClassLoader),
                new RuleCompiler(pkgRegistry, packageDescr, kBase, parallelRulesBuildThreshold,
                        assetFilter, packageAttributes, resource, this));
        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> this.results.addAll(p.getResults()));
    }

    protected void wireAllRules() {
        ConsequenceCompilationPhase compilationPhase = new ConsequenceCompilationPhase(pkgRegistryManager);
        compilationPhase.process();
        results.addAll(compilationPhase.getResults());
    }

    protected void processKieBaseTypes() {
        if (!hasErrors() && this.kBase != null) {
            List<InternalKnowledgePackage> pkgs = new ArrayList<>();
            for (PackageRegistry pkgReg : pkgRegistryManager.getPackageRegistry().values()) {
                pkgs.add(pkgReg.getPackage());
            }
            this.kBase.processAllTypesDeclaration(pkgs);
        }
    }

    private void compileRete(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        if (!hasErrors() && this.kBase != null) {
            ReteCompiler reteCompiler = new ReteCompiler(pkgRegistry, packageDescr, kBase, assetFilter);
            reteCompiler.process();
        }
    }

    public void addBuilderResult(KnowledgeBuilderResult result) {
        this.results.addBuilderResult(result);
    }

    protected BuildResultCollector getBuildResultCollector() {
        return this.results;
    }

    @Override
    public <T extends ResourceTypePackage<?>> T computeIfAbsent(
            ResourceType resourceType,
            String namespace, java.util.function.Function<? super ResourceType, T> mappingFunction) {

        PackageRegistry pkgReg = getOrCreatePackageRegistry(new PackageDescr(namespace));
        InternalKnowledgePackage kpkgs = pkgReg.getPackage();
        return kpkgs.getResourceTypePackages()
                .computeIfAbsent(
                        resourceType,
                        mappingFunction);
    }

    public PackageRegistry getOrCreatePackageRegistry(PackageDescr packageDescr) {
        return this.pkgRegistryManager.getOrCreatePackageRegistry(packageDescr);
    }

    public void registerPackage(PackageDescr packageDescr) {
        this.pkgRegistryManager.registerPackage(packageDescr);
    }

    protected PackageRegistryManager getPackageRegistryManager() {
        return pkgRegistryManager;
    }

    public static class ForkJoinPoolHolder {
        public static final ForkJoinPool COMPILER_POOL = new ForkJoinPool(); // avoid common pool
    }


    public boolean filterAccepts(ResourceChange.Type type, String namespace, String name) {
        return assetFilter == null || !AssetFilter.Action.DO_NOTHING.equals(assetFilter.accept(type, namespace, name));
    }

    private String getPackageDialect(PackageDescr packageDescr) {
        String dialectName = this.defaultDialect;
        // see if this packageDescr overrides the current default dialect
        for (AttributeDescr value : packageDescr.getAttributes()) {
            if ("dialect".equals(value.getName())) {
                dialectName = value.getValue();
                break;
            }
        }
        return dialectName;
    }

    //  test

    public void updateResults() {
        // some of the rules and functions may have been redefined
        updateResults(new ArrayList<>(this.results.getAllResults()));
    }

    public void updateResults(List<KnowledgeBuilderResult> results) {
        this.results.addAll(getResults(results));
    }

    public void compileAll() {
        this.pkgRegistryManager.compileAll();
    }

    public void reloadAll() {
        this.pkgRegistryManager.reloadAll();
    }

    private List<KnowledgeBuilderResult> getResults(List<KnowledgeBuilderResult> results) {
        results.addAll(this.pkgRegistryManager.getResults());
        return results;
    }

    public synchronized void addPackage(InternalKnowledgePackage newPkg) {
        PackageRegistry pkgRegistry = this.pkgRegistryManager.getPackageRegistry(newPkg.getName());
        InternalKnowledgePackage pkg = null;
        if (pkgRegistry != null) {
            pkg = pkgRegistry.getPackage();
        }

        if (pkg == null) {
            PackageDescr packageDescr = new PackageDescr(newPkg.getName());
            pkgRegistry = getOrCreatePackageRegistry(packageDescr);
            PackageCompilationPhase packageProcessor =
                    new PackageCompilationPhase(this,
                            kBase,
                            configuration,
                            typeDeclarationManager.getTypeDeclarationBuilder(),
                            assetFilter,
                            this.pkgRegistryManager.getPackageRegistry(packageDescr.getNamespace()),
                            packageDescr,
                            null);
            packageProcessor.process();
            this.results.addAll(packageProcessor.getResults());
            pkg = pkgRegistry.getPackage();
        }

        // first merge anything related to classloader re-wiring
        pkg.getDialectRuntimeRegistry().merge(newPkg.getDialectRuntimeRegistry(),
                                              this.rootClassLoader);
        if (newPkg.getFunctions() != null) {
            for (Map.Entry<String, Function> entry : newPkg.getFunctions().entrySet()) {
                if (pkg.getFunctions().containsKey(entry.getKey())) {
                    addBuilderResult(new DuplicateFunction(entry.getValue(),
                                                           this.configuration));
                }
                pkg.addFunction(entry.getValue());
            }
        }
        pkg.mergeStore(newPkg);
        pkg.getDialectRuntimeRegistry().onBeforeExecute();

        // we have to do this before the merging, as it does some classloader resolving
        TypeDeclaration lastType = null;
        try {
            // Resolve the class for the type declaation
            if (newPkg.getTypeDeclarations() != null) {
                // add type declarations
                for (TypeDeclaration type : newPkg.getTypeDeclarations().values()) {
                    lastType = type;
                    type.setTypeClass(this.rootClassLoader.loadClass(type.getTypeClassName()));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("unable to resolve Type Declaration class '" + lastType.getTypeName() + "'");
        }

        // now merge the new package into the existing one
        mergePackage(pkg,
                     newPkg);
    }

    /**
     * Merge a new package with an existing package. Most of the work is done by
     * the concrete implementations, but this class does some work (including
     * combining imports, compilation data, globals, and the actual Rule objects
     * into the package).
     */
    private void mergePackage(InternalKnowledgePackage pkg,
                              InternalKnowledgePackage newPkg) {
        // Merge imports
        final Map<String, ImportDeclaration> imports = pkg.getImports();
        imports.putAll(newPkg.getImports());

        // merge globals
        if (newPkg.getGlobals() != null && !newPkg.getGlobals().isEmpty()) {
            Map<String, Class<?>> pkgGlobals = pkg.getGlobals();
            // Add globals
            for (final Map.Entry<String, Class<?>> entry : newPkg.getGlobals().entrySet()) {
                final String identifier = entry.getKey();
                final Class<?> type = entry.getValue();
                if (pkgGlobals.containsKey(identifier) && !pkgGlobals.get(identifier).equals(type)) {
                    throw new RuntimeException(pkg.getName() + " cannot be integrated");
                } else {
                    pkg.addGlobal(identifier, type);
                    // this isn't a package merge, it's adding to the rulebase, but I've put it here for convenience
                    this.globals.addGlobal(identifier, type );
                }
            }
        }

        // merge the type declarations
        if (newPkg.getTypeDeclarations() != null) {
            // add type declarations
            for (TypeDeclaration type : newPkg.getTypeDeclarations().values()) {
                // @TODO should we allow overrides? only if the class is not in use.
                if (!pkg.getTypeDeclarations().containsKey(type.getTypeName())) {
                    // add to package list of type declarations
                    pkg.addTypeDeclaration(type);
                }
            }
        }

        for (final org.kie.api.definition.rule.Rule newRule : newPkg.getRules()) {
            pkg.addRule(((RuleImpl) newRule));
        }

        //Merge The Rule Flows
        if (newPkg.getRuleFlows() != null) {
            final Map flows = newPkg.getRuleFlows();
            for (Object o : flows.values()) {
                final Process flow = (Process) o;
                pkg.addProcess(flow);
            }
        }
    }

    public TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String packageName) {
        return typeDeclarationManager.getAndRegisterTypeDeclaration(cls, packageName);
    }

    public InternalKnowledgePackage[] getPackages() {
        InternalKnowledgePackage[] pkgs = new InternalKnowledgePackage[this.pkgRegistryManager.getPackageRegistry().size()];
        String errors = null;
        if (!getErrors().isEmpty()) {
            errors = getErrors().toString();
        }
        int i = 0;
        for (PackageRegistry pkgRegistry : this.pkgRegistryManager.getPackageRegistry().values()) {
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();
            pkg.getDialectRuntimeRegistry().onBeforeExecute();
            if (errors != null) {
                pkg.setError(errors);
            }
            pkgs[i++] = pkg;
        }

        return pkgs;
    }

    /**
     * Return the PackageBuilderConfiguration for this PackageBuilder session
     *
     * @return The PackageBuilderConfiguration
     */
    public KnowledgeBuilderConfigurationImpl getBuilderConfiguration() {
        return this.configuration;
    }

    public PackageRegistry getPackageRegistry(String name) {
        return this.pkgRegistryManager.getPackageRegistry(name);
    }

    @Override
    public InternalKnowledgePackage getPackage(String name) {
        PackageRegistry registry = this.getPackageRegistry(name);
        return registry == null ? null : registry.getPackage();
    }

    public Map<String, PackageRegistry> getPackageRegistry() {
        return this.pkgRegistryManager.getPackageRegistry();
    }

    public Collection<String> getPackageNames() {
        return this.pkgRegistryManager.getPackageNames();
    }

    public List<PackageDescr> getPackageDescrs(String packageName) {
        return pkgRegistryManager.getPackageDescrs(packageName);
    }

    /**
     * Returns an expander for DSLs (only if there is a DSL configured for this
     * package).
     */
    public DefaultExpander getDslExpander() {
        DefaultExpander expander = new DefaultExpander();
        if (this.dslFiles == null || this.dslFiles.isEmpty()) {
            return null;
        }
        for (DSLMappingFile file : this.dslFiles) {
            expander.addDSLMapping(file.getMapping());
        }
        return expander;
    }

    public Map<String, Class<?>> getGlobals() {
        return this.globals.getGlobals();
    }

    public void addGlobal(String name, Class<?> type) {
        this.globals.addGlobal(name, type);
    }

    protected GlobalVariableContext getGlobalVariableContext() {
        return globals;
    }

    /**
     * This will return true if there were errors in the package building and
     * compiling phase
     */
    public boolean hasErrors() {
        return results.hasErrors();
    }

    public KnowledgeBuilderResults getResults(ResultSeverity... problemTypes) {
        return results.getResults(problemTypes);
    }

    public boolean hasResults(ResultSeverity... problemTypes) {
        return results.hasResults(problemTypes);
    }

    public boolean hasWarnings() {
        return results.hasWarnings();
    }

    public boolean hasInfo() {
        return results.hasInfo();
    }

    public List<DroolsWarning> getWarnings() {
        return results.getWarnings();
    }

    @Override
    public void reportError(KnowledgeBuilderError error) {
        results.reportError(error);
    }

    /**
     * @return A list of Error objects that resulted from building and compiling
     * the package.
     */
    public PackageBuilderErrors getErrors() {
        return results.getErrors();
    }

    /**
     * Reset the error list. This is useful when incrementally building
     * packages. Care should be used when building this, if you clear this when
     * there were errors on items that a rule depends on (eg functions), then
     * you will get spurious errors which will not be that helpful.
     */
    public void resetErrors() {
        results.resetErrors();
    }

    public void resetWarnings() {
        results.resetWarnings();
    }

    public void resetProblems() {
        this.results.resetProblems();
        if (this.processBuilder != null) {
            this.processBuilder.getErrors().clear();
        }
    }

    public ClassLoader getRootClassLoader() {
        return this.rootClassLoader;
    }

    public void registerBuildResource(final Resource resource, ResourceType type) {
        InternalResource ires = (InternalResource) resource;
        if (ires.getResourceType() == null) {
            ires.setResourceType(type);
        } else if (ires.getResourceType() != type) {
            addBuilderResult(new ResourceTypeDeclarationWarning(resource, ires.getResourceType(), type));
        }
        buildResources.push(Collections.singletonList(resource));
    }

    public void registerBuildResources(List<Resource> resources) {
        buildResources.push(resources);
    }

    public void undo() {
        if (buildResources.isEmpty()) {
            return;
        }
        for (Resource resource : buildResources.pop()) {
            removeObjectsGeneratedFromResource(resource);
        }
    }

    public ResourceRemovalResult removeObjectsGeneratedFromResource(Resource resource) {
        boolean modified = false;
        for (PackageRegistry packageRegistry : this.pkgRegistryManager.getPackageRegistry().values()) {
            modified = packageRegistry.removeObjectsGeneratedFromResource(resource) || modified;
        }

        if (results != null) {
            results.getAllResults().removeIf(knowledgeBuilderResult -> resource.equals(knowledgeBuilderResult.getResource()));
        }

        if (processBuilder != null && processBuilder.getErrors() != null) {
            processBuilder.getErrors().removeIf(knowledgeBuilderResult -> resource.equals(knowledgeBuilderResult.getResource()));
        }

        if (results != null && results.getAllResults().size() == 0) {
            // TODO Error attribution might be bugged
            for (PackageRegistry packageRegistry : this.pkgRegistryManager.getPackageRegistry().values()) {
                packageRegistry.getPackage().resetErrors();
            }
        }

        Collection<String> removedTypes = typeDeclarationManager.getTypeDeclarationBuilder().removeTypesGeneratedFromResource(resource);

        for (List<PackageDescr> pkgDescrs : pkgRegistryManager.getPackageDescrs()) {
            for (PackageDescr pkgDescr : pkgDescrs) {
                pkgDescr.removeObjectsGeneratedFromResource(resource);
            }
        }

        if (kBase != null) {
            modified = kBase.removeObjectsGeneratedFromResource(resource, kBase.getWorkingMemories()) || modified;
        }

        return new ResourceRemovalResult(modified, removedTypes);
    }

    @Override
    public void rewireAllClassObjectTypes() {
        if (kBase != null) {
            for (InternalKnowledgePackage pkg : kBase.getPackagesMap().values()) {
                pkg.getDialectRuntimeRegistry().getDialectData("java").setDirty(true);
                pkg.wireStore();
            }
        }
    }

    public interface AssetFilter {

        enum Action {
            DO_NOTHING,
            ADD,
            REMOVE,
            UPDATE
        }

        Action accept(ResourceChange.Type type, String pkgName, String assetName);
    }

    public void setAssetFilter(AssetFilter assetFilter) {
        this.assetFilter = assetFilter;
    }

    protected AssetFilter getAssetFilter() {
        return this.assetFilter;
    }

    public void add(Resource resource, ResourceType type) {
        ResourceConfiguration resourceConfiguration = resource instanceof BaseResource ? resource.getConfiguration() : null;
        add(resource, type, resourceConfiguration);
    }

    public CompositeKnowledgeBuilder batch() {
        return new CompositeKnowledgeBuilderImpl(this);
    }

    public void add(Resource resource,
                    ResourceType type,
                    ResourceConfiguration configuration) {
        registerBuildResource(resource, type);
        addKnowledgeResource(resource, type, configuration);
    }

    @Override
    public Collection<KiePackage> getKnowledgePackages() {
        if (hasErrors()) {
            return new ArrayList<>(0);
        }

        InternalKnowledgePackage[] pkgs = getPackages();
        List<KiePackage> list = new ArrayList<>(pkgs.length);

        Collections.addAll(list, pkgs);

        return list;
    }

    public KieBase newKieBase() {
        return newKnowledgeBase(null);
    }

    public KieBase newKnowledgeBase(KieBaseConfiguration conf) {
        KnowledgeBuilderErrors errors = getErrors();
        if (!errors.isEmpty()) {
            for (KnowledgeBuilderError error : errors) {
                logger.error(error.toString());
            }
            throw new IllegalArgumentException("Could not parse knowledge. See the logs for details.");
        }
        RuleBase kbase = RuleBaseFactory.newRuleBase(conf);
        kbase.addPackages(asList(getPackages()));
        return KnowledgeBaseFactory.newKnowledgeBase(kbase);
    }

    public TypeDeclaration getTypeDeclaration(Class<?> cls) {
        return typeDeclarationManager.getTypeDeclaration(cls);
    }

    public TypeDeclaration getTypeDeclaration(ObjectType objectType) {
        return typeDeclarationManager.getTypeDeclaration(objectType);
    }

    private Map<String, Object> getBuilderCache() {
        if (builderCache == null) {
            builderCache = new HashMap<>();
        }
        return builderCache;
    }

    public <T> T getCachedOrCreate(String key, Supplier<T> creator) {
        final Map<String, Object> builderCache = getBuilderCache();
        final T cachedValue = (T) builderCache.get(key);
        if (cachedValue == null) {
            final T newValue = creator.get();
            builderCache.put(key, newValue);
            return newValue;
        } else {
            return cachedValue;
        }
    }

    public final void buildPackages( Collection<CompositePackageDescr> packages ) {
        // this 2 build steps are called in sequence here, but are interleaved by processes and assemblers compilation
        // during the build lifecycle of the CompositeKnowledgeBuilderImpl
        doFirstBuildStep(packages);
        doSecondBuildStep(packages);
    }

    // composite build lifecycle

    /**
     * Performs the actual building of rules, but may be empty in subclasses
     */
    protected void doFirstBuildStep( Collection<CompositePackageDescr> packages ) {
        buildPackagesWithoutRules(packages);
        buildRules(packages);
    }

    /**
     * Used by subclasses that need to perform the build after the assemblers
     */
    protected void doSecondBuildStep( Collection<CompositePackageDescr> packages ) { }

    public void buildPackagesWithoutRules(Collection<CompositePackageDescr> packages ) {
        CompositePackageCompilationPhase compositePackageCompilationPhase =
                new CompositePackageCompilationPhase(
                        packages,
                        pkgRegistryManager,
                        typeDeclarationManager.getTypeDeclarationBuilder(),
                        globals,
                        this, // as DroolsAssemblerContext
                        results,
                        kBase,
                        configuration);
        compositePackageCompilationPhase.process();
    }

    private void buildRules(Collection<CompositePackageDescr> packages) {
        for (CompositePackageDescr packageDescr : packages) {
            setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = getPackageRegistry(packageDescr.getNamespace());
            compileKnowledgePackages(packageDescr, pkgRegistry, null);
            setAssetFilter(null);
        }

        wireAllRules();
        processKieBaseTypes();

        for (CompositePackageDescr packageDescr : packages) {
            setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = getPackageRegistry(packageDescr.getNamespace());
            compileRete(pkgRegistry, packageDescr);
            setAssetFilter(null);
        }
    }
}
