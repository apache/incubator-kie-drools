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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.impl.errors.MissingImplementationException;
import org.drools.compiler.compiler.AnnotationDeclarationError;
import org.drools.compiler.compiler.BaseKnowledgeBuilderResultImpl;
import org.drools.compiler.compiler.ConfigurableSeverityResult;
import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.DroolsErrorWrapper;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.DroolsWarning;
import org.drools.compiler.compiler.DroolsWarningWrapper;
import org.drools.compiler.compiler.DuplicateFunction;
import org.drools.compiler.compiler.DuplicateRule;
import org.drools.compiler.compiler.GlobalError;
import org.drools.compiler.compiler.GuidedDecisionTableFactory;
import org.drools.compiler.compiler.GuidedDecisionTableProvider;
import org.drools.compiler.compiler.GuidedRuleTemplateFactory;
import org.drools.compiler.compiler.GuidedRuleTemplateProvider;
import org.drools.compiler.compiler.GuidedScoreCardFactory;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.compiler.compiler.PackageBuilderResults;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.compiler.ProcessBuilder;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.compiler.compiler.ResourceConversionResult;
import org.drools.compiler.compiler.ResourceTypeDeclarationWarning;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.compiler.ScoreCardFactory;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.compiler.xml.XmlPackageReader;
import org.drools.compiler.lang.ExpanderException;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.AccumulateImportDescr;
import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.EntryPointDeclarationDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.FunctionImportDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternDestinationDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.compiler.lang.descr.WindowDeclarationDescr;
import org.drools.compiler.lang.dsl.DSLMappingFile;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleBuilder;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.dialect.DialectError;
import org.drools.compiler.runtime.pipeline.impl.DroolsJaxbHelperProviderImpl;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.BaseResource;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.io.impl.DescrResource;
import org.drools.core.io.impl.ReaderResource;
import org.drools.core.io.internal.InternalResource;
import org.drools.core.rule.Function;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.WindowDeclaration;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.drools.core.xml.XmlChangeSetReader;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.ChangeSet;
import org.kie.internal.builder.AssemblerContext;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.io.ResourceWithConfigurationImpl;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import static org.drools.core.impl.KnowledgeBaseImpl.registerFunctionClassAndInnerClasses;
import static org.drools.core.util.StringUtils.isEmpty;
import static org.drools.core.util.StringUtils.ucFirst;

public class KnowledgeBuilderImpl implements KnowledgeBuilder,
                                             DroolsAssemblerContext,
                                             AssemblerContext {

    protected static final transient Logger logger = LoggerFactory.getLogger(KnowledgeBuilderImpl.class);

    private final Map<String, PackageRegistry> pkgRegistryMap = new ConcurrentHashMap<>();

    private List<KnowledgeBuilderResult> results;

    private final KnowledgeBuilderConfigurationImpl configuration;

    /**
     * Optional RuleBase for incremental live building
     */
    private InternalKnowledgeBase kBase;

    /**
     * default dialect
     */
    private final String defaultDialect;

    private ClassLoader rootClassLoader;

    private int parallelRulesBuildThreshold;

    private final Map<String, Class<?>> globals = new HashMap<>();

    private Resource resource;

    private List<DSLTokenizedMappingFile> dslFiles;

    private final org.drools.compiler.compiler.ProcessBuilder processBuilder;


    //This list of package level attributes is initialised with the PackageDescr's attributes added to the assembler.
    //The package level attributes are inherited by individual rules not containing explicit overriding parameters.
    //The map is keyed on the PackageDescr's namespace and contains a map of AttributeDescr's keyed on the
    //AttributeDescr's name.
    private final Map<String, Map<String, AttributeDescr>> packageAttributes = new HashMap<>();

    //PackageDescrs' list of ImportDescrs are kept identical as subsequent PackageDescrs are added.
    private final Map<String, List<PackageDescr>> packages = new ConcurrentHashMap<>();

    private final Stack<List<Resource>> buildResources = new Stack<>();

    private AssetFilter assetFilter = null;

    private final TypeDeclarationBuilder typeBuilder;

    private Map<String, Object> builderCache;

    private ReleaseId releaseId;

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

        this.results = new ArrayList<>();

        PackageRegistry pkgRegistry = new PackageRegistry(rootClassLoader, this.configuration, pkg);
        pkgRegistry.setDialect(this.defaultDialect);
        this.pkgRegistryMap.put(pkg.getName(),
                                pkgRegistry);

        // add imports to pkg registry
        for (final ImportDeclaration implDecl : pkg.getImports().values()) {
            pkgRegistry.addImport(new ImportDescr(implDecl.getTarget()));
        }

        processBuilder = ProcessBuilderFactory.newProcessBuilder(this);
        typeBuilder = new TypeDeclarationBuilder(this);
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

        this.results = new ArrayList<>();

        this.kBase = kBase;

        processBuilder = ProcessBuilderFactory.newProcessBuilder(this);
        typeBuilder = new TypeDeclarationBuilder(this);
    }

    public void setReleaseId( ReleaseId releaseId ) {
        this.releaseId = releaseId;
    }

    Resource getCurrentResource() {
        return resource;
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    TypeDeclarationBuilder getTypeBuilder() {
        return typeBuilder;
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
        this.resource = sourceResource;
        final DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        final PackageDescr pkg = parser.parse(sourceResource, reader);
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            addBuilderResult(new ParserError(sourceResource, "Parser returned a null Package", 0, 0));
        }

        if (!parser.hasErrors()) {
            addPackage(pkg);
        }
        this.resource = null;
    }

    public void addPackageFromDecisionTable(Resource resource,
                                            ResourceConfiguration configuration) throws DroolsParserException,
            IOException {
        this.resource = resource;
        addPackage(decisionTableToPackageDescr(resource, configuration));
        this.resource = null;
    }

    PackageDescr decisionTableToPackageDescr(Resource resource,
                                             ResourceConfiguration configuration) throws DroolsParserException, IOException {
        DecisionTableConfiguration dtableConfiguration = configuration instanceof DecisionTableConfiguration ?
                (DecisionTableConfiguration) configuration :
                null;

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

    public void addPackageFromGuidedDecisionTable(Resource resource) throws DroolsParserException,
            IOException {
        this.resource = resource;
        addPackage(guidedDecisionTableToPackageDescr(resource));
        this.resource = null;
    }

    PackageDescr guidedDecisionTableToPackageDescr(Resource resource) throws DroolsParserException, IOException {
        GuidedDecisionTableProvider guidedDecisionTableProvider = GuidedDecisionTableFactory.getGuidedDecisionTableProvider();
        if (guidedDecisionTableProvider == null) {
            throw new MissingImplementationException(resource, "drools-workbench-models-guided-dtable");
        }
        ResourceConversionResult conversionResult = guidedDecisionTableProvider.loadFromInputStream(resource.getInputStream());
        return conversionResultToPackageDescr(resource, conversionResult);
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

    protected static File createDumpDrlFile(File dumpDir, String fileName, String extension) {
        return new File(dumpDir, fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]+", "_") + extension);
    }

    public void addPackageFromScoreCard(final Resource resource,
                                        final ResourceConfiguration configuration) throws DroolsParserException, IOException {
        this.resource = resource;
        final ScoreCardConfiguration scardConfiguration = configuration instanceof ScoreCardConfiguration ?
                (ScoreCardConfiguration) configuration :
                null;
        final String pmmlString = ScoreCardFactory.getPMMLStringFromInputStream(resource.getInputStream(), scardConfiguration);
        if (pmmlString != null) {
            addPackageFromScoreCard(pmmlString, "scorecard_generated.pmml");
        }
        this.resource = null;
    }

    public void addPackageFromGuidedScoreCard(final Resource resource) throws DroolsParserException, IOException {
        this.resource = resource;
        final String pmmlString = GuidedScoreCardFactory.getPMMLStringFromInputStream(resource.getInputStream());
        if (pmmlString != null) {
            addPackageFromScoreCard(pmmlString, "guided_scorecard_generated.pmml");
        }
        this.resource = null;
    }

    private void addPackageFromScoreCard(final String pmmlString, final String fileName) throws DroolsParserException, IOException  {
        final File dumpDir = this.configuration.getDumpDir();
        if (dumpDir != null) {
            final String dirName = dumpDir.getCanonicalPath().endsWith("/") ? dumpDir.getCanonicalPath() : dumpDir.getCanonicalPath() + "/";
            final String outputPath = dirName + fileName;
            try (final FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(pmmlString.getBytes());
            }
        }
        final Resource res = ResourceFactory.newByteArrayResource(pmmlString.getBytes());

        try {
            ResourceWithConfiguration resCon = new ResourceWithConfigurationImpl(res, null, null, null);
            addPackageForExternalType(ResourceType.PMML, Collections.singletonList(resCon));
        } catch (Exception e) {
            throw new DroolsParserException(e);
        }
    }

    public void addPackageFromTemplate(Resource resource) throws DroolsParserException,
            IOException {
        this.resource = resource;
        addPackage(templateToPackageDescr(resource));
        this.resource = null;
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
        this.resource = resource;
        addPackage(drlToPackageDescr(resource));
        this.resource = null;
    }

    PackageDescr drlToPackageDescr(Resource resource) throws DroolsParserException,
            IOException {
        PackageDescr pkg;
        boolean hasErrors = false;
        if (resource instanceof DescrResource) {
            pkg = (PackageDescr) ((DescrResource) resource).getDescr();
        } else {
            final DrlParser parser = new DrlParser(configuration.getLanguageLevel());
            pkg = parser.parse(resource);
            this.results.addAll(parser.getErrors());
            if (pkg == null) {
                addBuilderResult(new ParserError(resource, "Parser returned a null Package", 0, 0));
            }
            hasErrors = parser.hasErrors();
        }
        if (pkg != null) {
            pkg.setResource(resource);
        }
        return hasErrors ? null : pkg;
    }

    /**
     * Load a rule package from XML source.
     *
     * @param reader
     * @throws DroolsParserException
     * @throws IOException
     */
    public void addPackageFromXml(final Reader reader) throws DroolsParserException,
            IOException {
        this.resource = new ReaderResource(reader, ResourceType.XDRL);
        final XmlPackageReader xmlReader = new XmlPackageReader(this.configuration.getSemanticModules());
        xmlReader.getParser().setClassLoader(this.rootClassLoader);

        try {
            xmlReader.read(reader);
        } catch (final SAXException e) {
            throw new DroolsParserException(e.toString(),
                                            e.getCause());
        }

        addPackage(xmlReader.getPackageDescr());
        this.resource = null;
    }

    public void addPackageFromXml(final Resource resource) throws DroolsParserException,
            IOException {
        this.resource = resource;
        addPackage(xmlToPackageDescr(resource));
        this.resource = null;
    }

    PackageDescr xmlToPackageDescr(Resource resource) throws DroolsParserException,
            IOException {
        final XmlPackageReader xmlReader = new XmlPackageReader(this.configuration.getSemanticModules());
        xmlReader.getParser().setClassLoader(this.rootClassLoader);

        try (Reader reader = resource.getReader()) {
            xmlReader.read(reader);
        } catch (final SAXException e) {
            throw new DroolsParserException(e.toString(),
                                            e.getCause());
        }
        return xmlReader.getPackageDescr();
    }

    /**
     * Load a rule package from DRL source using the supplied DSL configuration.
     *
     * @param source The source of the rules.
     * @param dsl    The source of the domain specific language configuration.
     * @throws DroolsParserException
     * @throws IOException
     */
    public void addPackageFromDrl(final Reader source,
                                  final Reader dsl) throws DroolsParserException,
            IOException {
        this.resource = new ReaderResource(source, ResourceType.DSLR);

        final DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        final PackageDescr pkg = parser.parse(source, dsl);
        this.results.addAll(parser.getErrors());
        if (!parser.hasErrors()) {
            addPackage(pkg);
        }
        this.resource = null;
    }

    public void addPackageFromDslr(final Resource resource) throws DroolsParserException,
            IOException {
        this.resource = resource;
        addPackage(dslrToPackageDescr(resource));
        this.resource = null;
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
        this.resource = resource;
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();

        try (Reader reader = resource.getReader()) {
            if (!file.parseAndLoad(reader)) {
                this.results.addAll(file.getErrors());
            }
            if (this.dslFiles == null) {
                this.dslFiles = new ArrayList<>();
            }
            this.dslFiles.add(file);
        } finally {
            this.resource = null;
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
            } else if (ResourceType.XDRL.equals(type)) {
                addPackageFromXml(resource);
            } else if (ResourceType.DTABLE.equals(type)) {
                addPackageFromDecisionTable(resource, configuration);
            } else if (ResourceType.PKG.equals(type)) {
                addPackageFromInputStream(resource);
            } else if (ResourceType.CHANGE_SET.equals(type)) {
                addPackageFromChangeSet(resource);
            } else if (ResourceType.XSD.equals(type)) {
                addPackageFromXSD(resource, (JaxbConfigurationImpl) configuration);
            } else if (ResourceType.SCARD.equals(type)) {
                addPackageFromScoreCard(resource, configuration);
            } else if (ResourceType.TDRL.equals(type)) {
                addPackageFromDrl(resource);
            } else if (ResourceType.TEMPLATE.equals(type)) {
                addPackageFromTemplate(resource);
            } else if (ResourceType.GDST.equals(type)) {
                addPackageFromGuidedDecisionTable(resource);
            } else if (ResourceType.SCGD.equals(type)) {
                addPackageFromGuidedScoreCard(resource);
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
        KieAssemblers assemblers = ServiceRegistry.getInstance().get(KieAssemblers.class);

        assemblers.addResource(this,
                              resource,
                              type,
                              configuration);
    }

    @Deprecated
    void addPackageForExternalType(ResourceType type, List<ResourceWithConfiguration> resources) throws Exception {
        KieAssemblers assemblers = ServiceRegistry.getInstance().get(KieAssemblers.class);

        assemblers.addResources(this, resources, type);
    }

    void addPackageFromXSD(Resource resource,
                           JaxbConfigurationImpl configuration) throws IOException {
        if (configuration != null) {
            String[] classes = DroolsJaxbHelperProviderImpl.addXsdModel(resource,
                                                                        this,
                                                                        configuration.getXjcOpts(),
                                                                        configuration.getSystemId());
            for (String cls : classes) {
                configuration.getClasses().add(cls);
            }
        }
    }

    void addPackageFromChangeSet(Resource resource) throws SAXException,
            IOException {
        XmlChangeSetReader reader = new XmlChangeSetReader(this.configuration.getSemanticModules());
        if (resource instanceof ClassPathResource) {
            reader.setClassLoader(((ClassPathResource) resource).getClassLoader(),
                                  ((ClassPathResource) resource).getClazz());
        } else {
            reader.setClassLoader(this.configuration.getClassLoader(),
                                  null);
        }
        try (Reader resourceReader = resource.getReader()) {
            ChangeSet changeSet = reader.read(resourceReader);
            if (changeSet == null) {
                throw new RuntimeException("ChangeSet cannot be read! " + resource.toString());
            } else {
                for (Resource nestedResource : changeSet.getResourcesAdded()) {
                    InternalResource iNestedResourceResource = (InternalResource) nestedResource;
                    if (iNestedResourceResource.isDirectory()) {
                        for (Resource childResource : iNestedResourceResource.listResources()) {
                            if (((InternalResource) childResource).isDirectory()) {
                                continue; // ignore sub directories
                            }
                            ((InternalResource) childResource).setResourceType(iNestedResourceResource.getResourceType());
                            addKnowledgeResource(childResource,
                                                 iNestedResourceResource.getResourceType(),
                                                 iNestedResourceResource.getConfiguration());
                        }
                    } else {
                        addKnowledgeResource(iNestedResourceResource,
                                             iNestedResourceResource.getResourceType(),
                                             iNestedResourceResource.getConfiguration());
                    }
                }
            }
        }
    }

    void addPackageFromInputStream(final Resource resource) throws IOException,
            ClassNotFoundException {
        InputStream is = resource.getInputStream();
        Object object = DroolsStreamUtils.streamIn(is, this.configuration.getClassLoader());
        is.close();
        if (object instanceof Collection) {
            // KnowledgeBuilder API
            @SuppressWarnings("unchecked")
            Collection<KiePackage> pkgs = (Collection<KiePackage>) object;
            for (KiePackage kpkg : pkgs) {
                overrideReSource((KnowledgePackageImpl) kpkg, resource);
                addPackage((KnowledgePackageImpl) kpkg);
            }
        } else if (object instanceof KnowledgePackageImpl) {
            // KnowledgeBuilder API
            KnowledgePackageImpl kpkg = (KnowledgePackageImpl) object;
            overrideReSource(kpkg, resource);
            addPackage(kpkg);
        } else {
            results.add(new DroolsError(resource) {

                @Override
                public String getMessage() {
                    return "Unknown binary format trying to load resource " + resource.toString();
                }

                @Override
                public int[] getLines() {
                    return new int[0];
                }
            });
        }
    }

    private void overrideReSource(InternalKnowledgePackage pkg,
                                  Resource res) {
        for (org.kie.api.definition.rule.Rule r : pkg.getRules()) {
            if (isSwappable(((RuleImpl) r).getResource(), res)) {
                ((RuleImpl) r).setResource(res);
            }
        }
        for (TypeDeclaration d : pkg.getTypeDeclarations().values()) {
            if (isSwappable(d.getResource(), res)) {
                d.setResource(res);
            }
        }
        for (Function f : pkg.getFunctions().values()) {
            if (isSwappable(f.getResource(), res)) {
                f.setResource(res);
            }
        }
        for (org.kie.api.definition.process.Process p : pkg.getRuleFlows().values()) {
            if (isSwappable(p.getResource(), res)) {
                p.setResource(res);
            }
        }
    }

    private boolean isSwappable(Resource original,
                                Resource source) {
        return original == null
                || (original instanceof ReaderResource && ((ReaderResource) original).getReader() == null);
    }

    /**
     * This adds a package from a Descr/AST This will also trigger a compile, if
     * there are any generated classes to compile of course.
     */
    public void addPackage(final PackageDescr packageDescr) {
        PackageRegistry pkgRegistry = getOrCreatePackageRegistry(packageDescr);
        if (pkgRegistry == null) {
            return;
        }

        // merge into existing package
        mergePackage(pkgRegistry, packageDescr);

        compileKnowledgePackages(packageDescr, pkgRegistry);
        wireAllRules();
        compileRete(packageDescr);
    }

    protected void compileKnowledgePackages(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        pkgRegistry.setDialect(getPackageDialect(packageDescr));
        validateUniqueRuleNames(packageDescr);
        compileFunctions(packageDescr, pkgRegistry);
        compileRules(packageDescr, pkgRegistry);
    }

    protected void wireAllRules() {
        compileAll();
        try {
            reloadAll();
        } catch (Exception e) {
            addBuilderResult(new DialectError(null, "Unable to wire compiled classes, probably related to compilation failures:" + e.getMessage()));
        }
        updateResults();
    }

    protected void processKieBaseTypes() {
        if (!hasErrors() && this.kBase != null) {
            List<InternalKnowledgePackage> pkgs = new ArrayList<>();
            for (PackageRegistry pkgReg : pkgRegistryMap.values()) {
                pkgs.add(pkgReg.getPackage());
            }
            this.kBase.processAllTypesDeclaration(pkgs);
        }
    }

    protected void compileRete(PackageDescr packageDescr) {
        if (!hasErrors() && this.kBase != null) {
            Collection<RuleImpl> rulesToBeAdded = new ArrayList<>();
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                    InternalKnowledgePackage pkg = pkgRegistryMap.get(ruleDescr.getNamespace()).getPackage();
                    rulesToBeAdded.add(pkg.getRule(ruleDescr.getName()));
                }
            }
            if (!rulesToBeAdded.isEmpty()) {
                this.kBase.addRules(rulesToBeAdded);
            }
        }
    }

    public void addBuilderResult(KnowledgeBuilderResult result) {
        this.results.add(result);
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
        if (packageDescr == null) {
            return null;
        }
        if (isEmpty(packageDescr.getNamespace())) {
            packageDescr.setNamespace(this.configuration.getDefaultPackageName());
        }
        return pkgRegistryMap.computeIfAbsent(packageDescr.getName(), name -> createPackageRegistry(packageDescr));
    }

    private PackageRegistry createPackageRegistry(PackageDescr packageDescr) {
        initPackage(packageDescr);

        InternalKnowledgePackage pkg;
        if (this.kBase == null || (pkg = this.kBase.getPackage(packageDescr.getName())) == null) {
            // there is no rulebase or it does not define this package so define it
            pkg = new KnowledgePackageImpl(packageDescr.getName());
            pkg.setClassFieldAccessorCache(new ClassFieldAccessorCache(this.rootClassLoader));

            // if there is a rulebase then add the package.
            if (this.kBase != null) {
                try {
                    pkg = (InternalKnowledgePackage) this.kBase.addPackage(pkg).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // the RuleBase will also initialise the
                pkg.getDialectRuntimeRegistry().onAdd(this.rootClassLoader);
            }
        }

        PackageRegistry pkgRegistry = new PackageRegistry(rootClassLoader, configuration, pkg);

        // add default import for this namespace
        pkgRegistry.addImport(new ImportDescr(packageDescr.getNamespace() + ".*"));

        for (ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.registerImport(importDescr.getTarget());
        }

        return pkgRegistry;
    }

    private void initPackage(PackageDescr packageDescr) {
        //Gather all imports for all PackageDescrs for the current package and replicate into
        //all PackageDescrs for the current package, thus maintaining a complete list of
        //ImportDescrs for all PackageDescrs for the current package.
        List<PackageDescr> packageDescrsForPackage = packages.computeIfAbsent(packageDescr.getName(), k -> new ArrayList<>());
        packageDescrsForPackage.add(packageDescr);
        Set<ImportDescr> imports = new HashSet<>();
        for (PackageDescr pd : packageDescrsForPackage) {
            imports.addAll(pd.getImports());
        }
        for (PackageDescr pd : packageDescrsForPackage) {
            pd.getImports().clear();
            pd.addAllImports(imports);
        }

        //Copy package level attributes for inclusion on individual rules
        if (!packageDescr.getAttributes().isEmpty()) {
            Map<String, AttributeDescr> pkgAttributes = packageAttributes.get(packageDescr.getNamespace());
            if (pkgAttributes == null) {
                pkgAttributes = new HashMap<>();
                this.packageAttributes.put(packageDescr.getNamespace(),
                                           pkgAttributes);
            }
            for (AttributeDescr attr : packageDescr.getAttributes()) {
                pkgAttributes.put(attr.getName(),
                                  attr);
            }
        }
    }

    private void compileFunctions(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        List<FunctionDescr> functions = packageDescr.getFunctions();
        if (!functions.isEmpty()) {

            for (FunctionDescr functionDescr : functions) {
                if (isEmpty(functionDescr.getNamespace())) {
                    // make sure namespace is set on components
                    functionDescr.setNamespace(packageDescr.getNamespace());
                }

                // make sure functions are compiled using java dialect
                functionDescr.setDialect("java");

                preCompileAddFunction(functionDescr, pkgRegistry);
            }

            // iterate and compile
            for (FunctionDescr functionDescr : functions) {
                if (filterAccepts(ResourceChange.Type.FUNCTION, functionDescr.getNamespace(), functionDescr.getName())) {
                    // inherit the dialect from the package
                    addFunction(functionDescr, pkgRegistry);
                }
            }

            // We need to compile all the functions now, so scripting
            // languages like mvel can find them
            compileAll();

            for (FunctionDescr functionDescr : functions) {
                if (filterAccepts(ResourceChange.Type.FUNCTION, functionDescr.getNamespace(), functionDescr.getName())) {
                    postCompileAddFunction(functionDescr, pkgRegistry);
                }
            }
        }
    }

    private void compileRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        preProcessRules(packageDescr, pkgRegistry);

        // ensure that rules are ordered by dependency, so that dependent rules are built later
        SortedRules sortedRules = sortRulesByDependency(packageDescr, pkgRegistry);

        if (!sortedRules.queries.isEmpty()) {
            compileAllQueries(packageDescr, pkgRegistry, sortedRules.queries);
        }
        for (List<RuleDescr> rulesLevel : sortedRules.rules) {
            compileRulesLevel(packageDescr, pkgRegistry, rulesLevel);
        }
    }

    private void compileAllQueries(PackageDescr packageDescr, PackageRegistry pkgRegistry, List<RuleDescr> rules) {
        Map<String, RuleBuildContext> ruleCxts = buildRuleBuilderContexts(rules, pkgRegistry);
        for (RuleDescr ruleDescr : rules) {
            if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                initRuleDescr(packageDescr, pkgRegistry, ruleDescr);
                this.results.addAll(addRule(ruleCxts.get(ruleDescr.getName())));
            }
        }
    }

    private static class ForkJoinPoolHolder {
        private static ForkJoinPool COMPILER_POOL = new ForkJoinPool(); // avoid common pool
    }

    private void compileRulesLevel(PackageDescr packageDescr, PackageRegistry pkgRegistry, List<RuleDescr> rules) {
        boolean parallelRulesBuild = this.kBase == null && parallelRulesBuildThreshold != -1 && rules.size() > parallelRulesBuildThreshold;
        if (parallelRulesBuild) {
            Map<String, RuleBuildContext> ruleCxts = new ConcurrentHashMap<>();
            try {
                ForkJoinPoolHolder.COMPILER_POOL.submit(() ->
                rules.stream().parallel()
                        .filter(ruleDescr -> filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName()))
                        .forEach(ruleDescr -> {
                            initRuleDescr(packageDescr, pkgRegistry, ruleDescr);
                            RuleBuildContext context = buildRuleBuilderContext(pkgRegistry, ruleDescr);
                            ruleCxts.put(ruleDescr.getName(), context);
                            List<? extends KnowledgeBuilderResult> results = addRule(context);
                            if (!results.isEmpty()) {
                            synchronized (this.results) {
                                    this.results.addAll(results);
                                }
                            }
                        })
                ).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Rules compilation failed or interrupted", e);
            }
            for (RuleDescr ruleDescr : rules) {
                RuleBuildContext context = ruleCxts.get(ruleDescr.getName());
                if (context != null) {
                    pkgRegistry.getPackage().addRule(context.getRule());
                }
            }
        } else {
            for (RuleDescr ruleDescr : rules) {
                if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                    initRuleDescr(packageDescr, pkgRegistry, ruleDescr);
                    RuleBuildContext context = buildRuleBuilderContext(pkgRegistry, ruleDescr);
                    this.results.addAll(addRule(context));
                    pkgRegistry.getPackage().addRule(context.getRule());
                }
            }
        }
    }

    private void initRuleDescr(PackageDescr packageDescr, PackageRegistry pkgRegistry, RuleDescr ruleDescr) {
        if (isEmpty(ruleDescr.getNamespace())) {
            // make sure namespace is set on components
            ruleDescr.setNamespace(packageDescr.getNamespace());
        }

        inheritPackageAttributes(packageAttributes.get(packageDescr.getNamespace()), ruleDescr);

        if (isEmpty(ruleDescr.getDialect())) {
            ruleDescr.addAttribute(new AttributeDescr("dialect", pkgRegistry.getDialect()));
        }
    }

    private List<? extends KnowledgeBuilderResult> addRule(RuleBuildContext context) {
        return System.getSecurityManager() == null ?
                internalAddRule(context) :
                AccessController.<List<? extends KnowledgeBuilderResult>>doPrivileged((PrivilegedAction) () -> internalAddRule(context));
    }

    private List<? extends KnowledgeBuilderResult> internalAddRule(RuleBuildContext context) {
        RuleBuilder.build(context);

        context.getRule().setResource(context.getRuleDescr().getResource());

        context.getDialect().addRule(context);

        if (context.needsStreamMode()) {
            context.getPkg().setNeedStreamMode();
        }

        if (context.getErrors().isEmpty()) {
            return context.getWarnings();
        } else if (context.getWarnings().isEmpty()) {
            return context.getErrors();
        }

        List<KnowledgeBuilderResult> result = new ArrayList<>();
        result.addAll(context.getErrors());
        result.addAll(context.getWarnings());
        return result;
    }

    boolean filterAccepts(ResourceChange.Type type, String namespace, String name) {
        return assetFilter == null || !AssetFilter.Action.DO_NOTHING.equals(assetFilter.accept(type, namespace, name));
    }

    private boolean filterAcceptsRemoval(ResourceChange.Type type, String namespace, String name) {
        return assetFilter != null && AssetFilter.Action.REMOVE.equals(assetFilter.accept(type, namespace, name));
    }

    private void preProcessRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        if (this.kBase == null) {
            return;
        }

        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        boolean needsRemoval = false;

        // first, check if any rules no longer exist
        for (org.kie.api.definition.rule.Rule rule : pkg.getRules()) {
            if (filterAcceptsRemoval(ResourceChange.Type.RULE, rule.getPackageName(), rule.getName())) {
                needsRemoval = true;
                break;
            }
        }

        if (!needsRemoval) {
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                    if (pkg.getRule(ruleDescr.getName()) != null) {
                        needsRemoval = true;
                        break;
                    }
                }
            }
        }

        if (needsRemoval) {
            kBase.enqueueModification(() -> {
                Collection<RuleImpl> rulesToBeRemoved = new HashSet<>();

                for (org.kie.api.definition.rule.Rule rule : pkg.getRules()) {
                    if (filterAcceptsRemoval(ResourceChange.Type.RULE, rule.getPackageName(), rule.getName())) {
                        rulesToBeRemoved.add(((RuleImpl) rule));
                    }
                }

                rulesToBeRemoved.forEach(pkg::removeRule);

                for (RuleDescr ruleDescr : packageDescr.getRules()) {
                    if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                        RuleImpl rule = pkg.getRule(ruleDescr.getName());
                        if (rule != null) {
                            rulesToBeRemoved.add(rule);
                        }
                    }
                }

                if (!rulesToBeRemoved.isEmpty()) {
                    kBase.removeRules(rulesToBeRemoved);
                }
            });
        }
    }

    private Map<String, RuleBuildContext> buildRuleBuilderContexts(List<RuleDescr> rules, PackageRegistry pkgRegistry) {
        Map<String, RuleBuildContext> map = new HashMap<>();
        for (RuleDescr ruleDescr : rules) {
            RuleBuildContext context = buildRuleBuilderContext(pkgRegistry, ruleDescr);
            map.put(ruleDescr.getName(), context);
            pkgRegistry.getPackage().addRule(context.getRule());
        }
        return map;
    }

    private RuleBuildContext buildRuleBuilderContext(PackageRegistry pkgRegistry, RuleDescr ruleDescr) {
        if (ruleDescr.getResource() == null) {
            ruleDescr.setResource(resource);
        }

        DialectCompiletimeRegistry ctr = pkgRegistry.getDialectCompiletimeRegistry();
        RuleBuildContext context = new RuleBuildContext(this,
                                                        ruleDescr,
                                                        ctr,
                                                        pkgRegistry.getPackage(),
                                                        ctr.getDialect(pkgRegistry.getDialect()));
        RuleBuilder.preProcess(context);
        return context;
    }

    private SortedRules sortRulesByDependency(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        // Using a topological sorting algorithm
        // see http://en.wikipedia.org/wiki/Topological_sorting

        InternalKnowledgePackage pkg = pkgRegistry.getPackage();

        List<RuleDescr> roots = new LinkedList<>();
        Map<String, List<RuleDescr>> children = new HashMap<>();
        LinkedHashMap<String, RuleDescr> sorted = new LinkedHashMap<>();
        List<RuleDescr> queries = new ArrayList<>();
        Set<String> compiledRules = new HashSet<>();

        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            if (ruleDescr.isQuery()) {
                queries.add(ruleDescr);
            } else if (!ruleDescr.hasParent()) {
                roots.add(ruleDescr);
            } else {
                if (pkg.getRule(ruleDescr.getParentName()) != null) {
                    // The parent of this rule has been already compiled
                    compiledRules.add(ruleDescr.getParentName());
                }
                children.computeIfAbsent(ruleDescr.getParentName(), k -> new ArrayList<>()).add(ruleDescr);
            }
        }

        SortedRules sortedRules = new SortedRules();
        sortedRules.queries = queries;

        if (children.isEmpty()) { // Sorting not necessary
            if (!queries.isEmpty()) { // Build all queries first
                packageDescr.getRules().removeAll(queries);
                packageDescr.getRules().addAll(0, queries);
                sortedRules.rules.add(packageDescr.getRules().subList(queries.size(), packageDescr.getRules().size()));
            } else {
                sortedRules.rules.add(packageDescr.getRules());
            }
            return sortedRules;
        }

        for (String compiledRule : compiledRules) {
            List<RuleDescr> childz = children.remove(compiledRule);
            roots.addAll(childz);
        }

        List<RuleDescr> rulesLevel = roots;
        while (!rulesLevel.isEmpty()) {
            rulesLevel = sortRulesLevel(rulesLevel, sorted, sortedRules, children);
            sortedRules.newLevel();
        }

        reportHierarchyErrors(children, sorted);

        packageDescr.getRules().clear();
        packageDescr.getRules().addAll(queries);
        for (RuleDescr descr : sorted.values()) {
            packageDescr.getRules().add(descr);
        }
        return sortedRules;
    }

    private List<RuleDescr> sortRulesLevel(final List<RuleDescr> rulesLevel,
                                           final LinkedHashMap<String, RuleDescr> sorted, final SortedRules sortedRules,
                                           final Map<String, List<RuleDescr>> children) {
        final List<RuleDescr> nextLevel = new ArrayList<>();
        rulesLevel.forEach(ruleDescr -> {
            sortedRules.addRule(ruleDescr);
            sorted.put(ruleDescr.getName(), ruleDescr);
            final List<RuleDescr> childz = children.remove(ruleDescr.getName());
            if (childz != null) {
                nextLevel.addAll(childz);
            }
        });
        return nextLevel;
    }

    private static class SortedRules {

        List<RuleDescr> queries;
        final List<List<RuleDescr>> rules = new ArrayList<>();
        List<RuleDescr> current = new ArrayList<>();

        SortedRules() {
            newLevel();
        }

        void addRule(RuleDescr rule) {
            current.add(rule);
        }

        void newLevel() {
            current = new ArrayList<>();
            rules.add(current);
        }
    }

    private void reportHierarchyErrors(Map<String, List<RuleDescr>> parents,
                                       Map<String, RuleDescr> sorted) {
        boolean circularDep = false;
        for (List<RuleDescr> rds : parents.values()) {
            for (RuleDescr ruleDescr : rds) {
                if (parents.get(ruleDescr.getParentName()) != null
                        && (sorted.containsKey(ruleDescr.getName()) || parents.containsKey(ruleDescr.getName()))) {
                    circularDep = true;
                    results.add(new RuleBuildError(ruleDescr.toRule(), ruleDescr, null,
                                                   "Circular dependency in rules hierarchy"));
                    break;
                }
                manageUnresolvedExtension(ruleDescr, sorted.values());
            }
            if (circularDep) {
                break;
            }
        }
    }

    private void manageUnresolvedExtension(RuleDescr ruleDescr,
                                           Collection<RuleDescr> candidates) {
        List<String> candidateRules = new LinkedList<>();
        for (RuleDescr r : candidates) {
            if (StringUtils.stringSimilarity(ruleDescr.getParentName(), r.getName(), StringUtils.SIMILARITY_STRATS.DICE) >= 0.75) {
                candidateRules.add(r.getName());
            }
        }
        String msg = "Unresolved parent name " + ruleDescr.getParentName();
        if (!candidateRules.isEmpty()) {
            msg += " >> did you mean any of :" + candidateRules;
        }
        results.add(new RuleBuildError(ruleDescr.toRule(), ruleDescr, msg,
                                       "Unable to resolve parent rule, please check that both rules are in the same package"));
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
        updateResults(this.results);
    }

    public void updateResults(List<KnowledgeBuilderResult> results) {
        this.results = getResults(results);
    }

    public void compileAll() {
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
            pkgRegistry.compileAll();
        }
    }

    public void reloadAll() {
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
            pkgRegistry.getDialectRuntimeRegistry().onBeforeExecute();
        }
    }

    private List<KnowledgeBuilderResult> getResults(List<KnowledgeBuilderResult> results) {
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
            results = pkgRegistry.getDialectCompiletimeRegistry().addResults(results);
        }
        return results;
    }

    public synchronized void addPackage(InternalKnowledgePackage newPkg) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get(newPkg.getName());
        InternalKnowledgePackage pkg = null;
        if (pkgRegistry != null) {
            pkg = pkgRegistry.getPackage();
        }

        if (pkg == null) {
            PackageDescr packageDescr = new PackageDescr(newPkg.getName());
            pkgRegistry = getOrCreatePackageRegistry(packageDescr);
            mergePackage(this.pkgRegistryMap.get(packageDescr.getNamespace()), packageDescr);
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
        pkg.getClassFieldAccessorStore().merge(newPkg.getClassFieldAccessorStore());
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

        String lastType = null;
        try {
            // merge globals
            if (newPkg.getGlobals() != null && !newPkg.getGlobals().isEmpty()) {
                Map<String, String> pkgGlobals = pkg.getGlobals();
                // Add globals
                for (final Map.Entry<String, String> entry : newPkg.getGlobals().entrySet()) {
                    final String identifier = entry.getKey();
                    final String type = entry.getValue();
                    lastType = type;
                    if (pkgGlobals.containsKey(identifier) && !pkgGlobals.get(identifier).equals(type)) {
                        throw new RuntimeException(pkg.getName() + " cannot be integrated");
                    } else {
                        pkg.addGlobal(identifier,
                                      this.rootClassLoader.loadClass(type));
                        // this isn't a package merge, it's adding to the rulebase, but I've put it here for convenience
                        this.globals.put(identifier,
                                         this.rootClassLoader.loadClass(type));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to resolve class '" + lastType + "'");
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

    protected void validateUniqueRuleNames(final PackageDescr packageDescr) {
        final Set<String> names = new HashSet<>();
        PackageRegistry packageRegistry = this.pkgRegistryMap.get(packageDescr.getNamespace());
        InternalKnowledgePackage pkg = null;
        if (packageRegistry != null) {
            pkg = packageRegistry.getPackage();
        }
        for (final RuleDescr rule : packageDescr.getRules()) {
            validateRule(packageDescr, rule);

            final String name = rule.getName();
            if (names.contains(name)) {
                addBuilderResult(new ParserError(rule.getResource(),
                                                 "Duplicate rule name: " + name,
                                                 rule.getLine(),
                                                 rule.getColumn(),
                                                 packageDescr.getNamespace()));
            }
            if (pkg != null) {
                RuleImpl duplicatedRule = pkg.getRule(name);
                if (duplicatedRule != null) {
                    Resource resource = rule.getResource();
                    Resource duplicatedResource = duplicatedRule.getResource();
                    if (resource == null || duplicatedResource == null || duplicatedResource.getSourcePath() == null ||
                            duplicatedResource.getSourcePath().equals(resource.getSourcePath())) {
                        addBuilderResult(new DuplicateRule(rule,
                                                           packageDescr,
                                                           this.configuration));
                    } else {
                        addBuilderResult(new ParserError(rule.getResource(),
                                                         "Duplicate rule name: " + name,
                                                         rule.getLine(),
                                                         rule.getColumn(),
                                                         packageDescr.getNamespace()));
                    }
                }
            }
            names.add(name);
        }
    }

    private void validateRule(PackageDescr packageDescr,
                              RuleDescr rule) {
        if (rule.hasErrors()) {
            for (String error : rule.getErrors()) {
                addBuilderResult(new ParserError(rule.getResource(),
                                                 error + " in rule " + rule.getName(),
                                                 rule.getLine(),
                                                 rule.getColumn(),
                                                 packageDescr.getNamespace()));
            }
        }
    }

    void mergePackage(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        for (final ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.addImport(importDescr);
        }

        normalizeTypeDeclarationAnnotations(packageDescr, pkgRegistry.getTypeResolver());
        processAccumulateFunctions(pkgRegistry, packageDescr);
        processEntryPointDeclarations(pkgRegistry, packageDescr);

        Map<String, AbstractClassTypeDeclarationDescr> unprocesseableDescrs = new HashMap<>();
        List<TypeDefinition> unresolvedTypes = new ArrayList<>();
        List<AbstractClassTypeDeclarationDescr> unsortedDescrs = new ArrayList<>();
        unsortedDescrs.addAll(packageDescr.getTypeDeclarations());
        unsortedDescrs.addAll(packageDescr.getEnumDeclarations());

        typeBuilder.processTypeDeclarations(packageDescr, pkgRegistry, unsortedDescrs, unresolvedTypes, unprocesseableDescrs);
        for (AbstractClassTypeDeclarationDescr descr : unprocesseableDescrs.values()) {
            this.addBuilderResult(new TypeDeclarationError(descr, "Unable to process type " + descr.getTypeName()));
        }

        processOtherDeclarations(pkgRegistry, packageDescr);
        normalizeRuleAnnotations(packageDescr, pkgRegistry.getTypeResolver());
    }

    void processOtherDeclarations(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        processAccumulateFunctions(pkgRegistry, packageDescr);
        processWindowDeclarations(pkgRegistry, packageDescr);
        processFunctions(pkgRegistry, packageDescr);
        processGlobals(pkgRegistry, packageDescr);
    }

    private void processGlobals(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        Set<String> existingGlobals = new HashSet<>(pkg.getGlobals().keySet());

        for (final GlobalDescr global : packageDescr.getGlobals()) {
            final String identifier = global.getIdentifier();
            existingGlobals.remove(identifier);
            String className = global.getType();

            // JBRULES-3039: can't handle type name with generic params
            while (className.indexOf('<') >= 0) {
                className = className.replaceAll("<[^<>]+?>", "");
            }

            try {
                Class<?> clazz = pkgRegistry.getTypeResolver().resolveType(className);
                if (clazz.isPrimitive()) {
                    addBuilderResult(new GlobalError(global, " Primitive types are not allowed in globals : " + className));
                    return;
                }
                pkg.addGlobal(identifier, clazz);
                addGlobal(identifier, clazz);
                if (kBase != null) {
                    kBase.addGlobal(identifier, clazz);
                }
            } catch (final ClassNotFoundException e) {
                addBuilderResult(new GlobalError(global, e.getMessage()));
                logger.warn("ClassNotFoundException occured!", e);
            }
        }

        for (String toBeRemoved : existingGlobals) {
            if (filterAcceptsRemoval(ResourceChange.Type.GLOBAL, pkg.getName(), toBeRemoved)) {
                pkg.removeGlobal(toBeRemoved);
                if (kBase != null) {
                    kBase.removeGlobal(toBeRemoved);
                }
            }
        }
    }

    private void processAccumulateFunctions(PackageRegistry pkgRegistry,
                                            PackageDescr packageDescr) {
        for (final AccumulateImportDescr aid : packageDescr.getAccumulateImports()) {
            AccumulateFunction af = loadAccumulateFunction(pkgRegistry,
                                                           aid.getFunctionName(),
                                                           aid.getTarget());
            pkgRegistry.getPackage().addAccumulateFunction(aid.getFunctionName(), af);
        }
    }

    @SuppressWarnings("unchecked")
    private AccumulateFunction loadAccumulateFunction(PackageRegistry pkgRegistry,
                                                      String identifier,
                                                      String className) {
        try {
            Class<? extends AccumulateFunction> clazz = (Class<? extends AccumulateFunction>) pkgRegistry.getTypeResolver().resolveType(className);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Class " + className + " not found",
                                       e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Instantiation failed for class " + className,
                                       e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Illegal access to class " + className,
                                       e);
        }
    }

    private void processFunctions(PackageRegistry pkgRegistry,
                                  PackageDescr packageDescr) {
        for (FunctionDescr function : packageDescr.getFunctions()) {
            Function existingFunc = pkgRegistry.getPackage().getFunctions().get(function.getName());
            if (existingFunc != null && function.getNamespace().equals(existingFunc.getNamespace())) {
                addBuilderResult(
                        new DuplicateFunction(function,
                                              this.configuration));
            }
        }

        for (final FunctionImportDescr functionImport : packageDescr.getFunctionImports()) {
            String importEntry = functionImport.getTarget();
            pkgRegistry.addStaticImport(functionImport);
            pkgRegistry.getPackage().addStaticImport(importEntry);
        }
    }

    public TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String packageName) {
        if (kBase != null) {
            InternalKnowledgePackage pkg = kBase.getPackage(packageName);
            if (pkg != null) {
                TypeDeclaration typeDeclaration = pkg.getTypeDeclaration(cls);
                if (typeDeclaration != null) {
                    return typeDeclaration;
                }
            }
        }
        return typeBuilder.getAndRegisterTypeDeclaration(cls, packageName);
    }

    void processEntryPointDeclarations(PackageRegistry pkgRegistry,
                                       PackageDescr packageDescr) {
        for (EntryPointDeclarationDescr epDescr : packageDescr.getEntryPointDeclarations()) {
            pkgRegistry.getPackage().addEntryPointId(epDescr.getEntryPointId());
        }
    }

    private void processWindowDeclarations(PackageRegistry pkgRegistry,
                                           PackageDescr packageDescr) {
        for (WindowDeclarationDescr wd : packageDescr.getWindowDeclarations()) {
            WindowDeclaration window = new WindowDeclaration(wd.getName(), packageDescr.getName());
            // TODO: process annotations

            // process pattern
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();
            DialectCompiletimeRegistry ctr = pkgRegistry.getDialectCompiletimeRegistry();
            RuleDescr dummy = new RuleDescr(wd.getName() + " Window Declaration");
            dummy.setResource(packageDescr.getResource());
            dummy.addAttribute(new AttributeDescr("dialect", "java"));
            RuleBuildContext context = new RuleBuildContext(this,
                                                            dummy,
                                                            ctr,
                                                            pkg,
                                                            ctr.getDialect(pkgRegistry.getDialect()));
            final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder(wd.getPattern().getClass());
            if (builder != null) {
                final Pattern pattern = (Pattern) builder.build(context,
                                                                wd.getPattern(),
                                                                null);

                if (pattern.getXpathConstraint() != null) {
                    context.addError(new DescrBuildError(wd,
                                                         context.getParentDescr(),
                                                         null,
                                                         "OOpath expression " + pattern.getXpathConstraint() + " not allowed in window declaration\n"));
                }

                window.setPattern(pattern);
            } else {
                throw new RuntimeException(
                        "BUG: assembler not found for descriptor class " + wd.getPattern().getClass());
            }

            if (!context.getErrors().isEmpty()) {
                for (DroolsError error : context.getErrors()) {
                    addBuilderResult(error);
                }
            } else {
                pkgRegistry.getPackage().addWindowDeclaration(window);
            }
        }
    }

    private void addFunction(final FunctionDescr functionDescr, PackageRegistry pkgRegistry) {
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect(functionDescr.getDialect());
        dialect.addFunction(functionDescr,
                            pkgRegistry.getTypeResolver(),
                            this.resource);
    }

    private void preCompileAddFunction(final FunctionDescr functionDescr, PackageRegistry pkgRegistry) {
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect(functionDescr.getDialect());
        dialect.preCompileAddFunction(functionDescr,
                                      pkgRegistry.getTypeResolver());
    }

    private void postCompileAddFunction(final FunctionDescr functionDescr, PackageRegistry pkgRegistry) {
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect(functionDescr.getDialect());
        dialect.postCompileAddFunction(functionDescr, pkgRegistry.getTypeResolver());

        if (rootClassLoader instanceof ProjectClassLoader ) {
            String functionClassName = functionDescr.getClassName();
            JavaDialectRuntimeData runtime = ((JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData("java"));
            try {
                registerFunctionClassAndInnerClasses(functionClassName, runtime,
                                                     (name, bytes) -> ((ProjectClassLoader) rootClassLoader).storeClass(name, bytes));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public InternalKnowledgePackage[] getPackages() {
        InternalKnowledgePackage[] pkgs = new InternalKnowledgePackage[this.pkgRegistryMap.size()];
        String errors = null;
        if (!getErrors().isEmpty()) {
            errors = getErrors().toString();
        }
        int i = 0;
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
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
        return this.pkgRegistryMap.get(name);
    }

    public InternalKnowledgePackage getPackage(String name) {
        return this.pkgRegistryMap.get(name).getPackage();
    }

    public Map<String, PackageRegistry> getPackageRegistry() {
        return this.pkgRegistryMap;
    }

    public Collection<String> getPackageNames() {
        return pkgRegistryMap.keySet();
    }

    public List<PackageDescr> getPackageDescrs(String packageName) {
        return packages.get(packageName);
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
        return this.globals;
    }

    public void addGlobal(String name, Class<?> type) {
        globals.put(name, type);
    }

    /**
     * This will return true if there were errors in the package building and
     * compiling phase
     */
    public boolean hasErrors() {
        return !getErrorList().isEmpty();
    }

    public KnowledgeBuilderResults getResults(ResultSeverity... problemTypes) {
        List<KnowledgeBuilderResult> problems = getResultList(problemTypes);
        return new PackageBuilderResults(problems.toArray(new BaseKnowledgeBuilderResultImpl[problems.size()]));
    }

    private List<KnowledgeBuilderResult> getResultList(ResultSeverity... severities) {
        List<ResultSeverity> typesToFetch = Arrays.asList(severities);
        ArrayList<KnowledgeBuilderResult> problems = new ArrayList<>();
        for (KnowledgeBuilderResult problem : results) {
            if (typesToFetch.contains(problem.getSeverity())) {
                problems.add(problem);
            }
        }
        return problems;
    }

    public boolean hasResults(ResultSeverity... problemTypes) {
        return !getResultList(problemTypes).isEmpty();
    }

    private List<DroolsError> getErrorList() {
        List<DroolsError> errors = new ArrayList<>();
        for (KnowledgeBuilderResult problem : results) {
            if (problem.getSeverity() == ResultSeverity.ERROR) {
                if (problem instanceof ConfigurableSeverityResult) {
                    errors.add(new DroolsErrorWrapper(problem));
                } else {
                    errors.add((DroolsError) problem);
                }
            }
        }
        return errors;
    }

    public boolean hasWarnings() {
        return !getWarnings().isEmpty();
    }

    public boolean hasInfo() {
        return !getInfoList().isEmpty();
    }

    public List<DroolsWarning> getWarnings() {
        List<DroolsWarning> warnings = new ArrayList<>();
        for (KnowledgeBuilderResult problem : results) {
            if (problem.getSeverity() == ResultSeverity.WARNING) {
                if (problem instanceof ConfigurableSeverityResult) {
                    warnings.add(new DroolsWarningWrapper(problem));
                } else {
                    warnings.add((DroolsWarning) problem);
                }
            }
        }
        return warnings;
    }

    private List<KnowledgeBuilderResult> getInfoList() {
        return getResultList(ResultSeverity.INFO);
    }

    @Override
    public void reportError(KnowledgeBuilderError error) {
        getErrors().add(error);
    }

    /**
     * @return A list of Error objects that resulted from building and compiling
     * the package.
     */
    public PackageBuilderErrors getErrors() {
        List<DroolsError> errors = getErrorList();
        return new PackageBuilderErrors(errors.toArray(new DroolsError[errors.size()]));
    }

    /**
     * Reset the error list. This is useful when incrementally building
     * packages. Care should be used when building this, if you clear this when
     * there were errors on items that a rule depends on (eg functions), then
     * you will get spurious errors which will not be that helpful.
     */
    protected void resetErrors() {
        resetProblemType(ResultSeverity.ERROR);
    }

    protected void resetWarnings() {
        resetProblemType(ResultSeverity.WARNING);
    }

    private void resetProblemType(ResultSeverity problemType) {
        List<KnowledgeBuilderResult> toBeDeleted = new ArrayList<>();
        for (KnowledgeBuilderResult problem : results) {
            if (problemType != null && problemType.equals(problem.getSeverity())) {
                toBeDeleted.add(problem);
            }
        }
        this.results.removeAll(toBeDeleted);
    }

    protected void resetProblems() {
        this.results.clear();
        if (this.processBuilder != null) {
            this.processBuilder.getErrors().clear();
        }
    }

    public String getDefaultDialect() {
        return this.defaultDialect;
    }

    public static class MissingPackageNameException extends IllegalArgumentException {

        private static final long serialVersionUID = 510L;

        public MissingPackageNameException(final String message) {
            super(message);
        }
    }

    public static class PackageMergeException extends IllegalArgumentException {

        private static final long serialVersionUID = 400L;

        public PackageMergeException(final String message) {
            super(message);
        }
    }

    public ClassLoader getRootClassLoader() {
        return this.rootClassLoader;
    }

    //Entity rules inherit package attributes
    private void inheritPackageAttributes(Map<String, AttributeDescr> pkgAttributes,
                                          RuleDescr ruleDescr) {
        if (pkgAttributes == null) {
            return;
        }
        for (AttributeDescr attrDescr : pkgAttributes.values()) {
            ruleDescr.getAttributes().putIfAbsent(attrDescr.getName(), attrDescr);
        }
    }

    private ChangeSet parseChangeSet(Resource resource) throws IOException, SAXException {
        XmlChangeSetReader reader = new XmlChangeSetReader(this.configuration.getSemanticModules());
        if (resource instanceof ClassPathResource) {
            reader.setClassLoader(((ClassPathResource) resource).getClassLoader(),
                                  ((ClassPathResource) resource).getClazz());
        } else {
            reader.setClassLoader(this.configuration.getClassLoader(),
                                  null);
        }

        try (Reader resourceReader = resource.getReader()) {
            return reader.read(resourceReader);
        }
    }

    public void registerBuildResource(final Resource resource, ResourceType type) {
        InternalResource ires = (InternalResource) resource;
        if (ires.getResourceType() == null) {
            ires.setResourceType(type);
        } else if (ires.getResourceType() != type) {
            addBuilderResult(new ResourceTypeDeclarationWarning(resource, ires.getResourceType(), type));
        }
        if (ResourceType.CHANGE_SET == type) {
            try {
                ChangeSet changeSet = parseChangeSet(resource);
                List<Resource> resources = new ArrayList<>();
                resources.add(resource);
                resources.addAll(changeSet.getResourcesAdded());
                resources.addAll(changeSet.getResourcesModified());
                resources.addAll(changeSet.getResourcesRemoved());
                buildResources.push(resources);
            } catch (Exception e) {
                results.add(new DroolsError() {

                    public String getMessage() {
                        return "Unable to register changeset resource " + resource;
                    }

                    public int[] getLines() {
                        return new int[0];
                    }
                });
            }
        } else {
            buildResources.push(Collections.singletonList(resource));
        }
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
        for (PackageRegistry packageRegistry : pkgRegistryMap.values()) {
            modified = packageRegistry.removeObjectsGeneratedFromResource(resource) || modified;
        }

        if (results != null) {
            results.removeIf(knowledgeBuilderResult -> resource.equals(knowledgeBuilderResult.getResource()));
        }

        if (processBuilder != null && processBuilder.getErrors() != null) {
            processBuilder.getErrors().removeIf(knowledgeBuilderResult -> resource.equals(knowledgeBuilderResult.getResource()));
        }

        if (results != null && results.size() == 0) {
            // TODO Error attribution might be bugged
            for (PackageRegistry packageRegistry : pkgRegistryMap.values()) {
                packageRegistry.getPackage().resetErrors();
            }
        }

        Collection<String> removedTypes = typeBuilder.removeTypesGeneratedFromResource(resource);

        for (List<PackageDescr> pkgDescrs : packages.values()) {
            for (PackageDescr pkgDescr : pkgDescrs) {
                pkgDescr.removeObjectsGeneratedFromResource(resource);
            }
        }

        if (kBase != null) {
            modified = kBase.removeObjectsGeneratedFromResource(resource) || modified;
        }

        return new ResourceRemovalResult(modified, removedTypes);
    }


    public static class ResourceRemovalResult {
        private boolean modified;
        private Collection<String> removedTypes;

        public ResourceRemovalResult(  ) {
            this( false, Collections.emptyList() );
        }

        public ResourceRemovalResult( boolean modified, Collection<String> removedTypes ) {
            this.modified = modified;
            this.removedTypes = removedTypes;
        }

        public void add(ResourceRemovalResult other) {
            mergeModified( other.modified );
            if (this.removedTypes.isEmpty()) {
                this.removedTypes = other.removedTypes;
            } else {
                this.removedTypes.addAll( other.removedTypes );
            }
        }

        public void mergeModified( boolean otherModified ) {
            this.modified = this.modified || otherModified;
        }

        public boolean isModified() {
            return modified;
        }

        public Collection<String> getRemovedTypes() {
            return removedTypes;
        }
    }

    public void rewireAllClassObjectTypes() {
        if (kBase != null) {
            for (InternalKnowledgePackage pkg : kBase.getPackagesMap().values()) {
                pkg.getDialectRuntimeRegistry().getDialectData("java").setDirty(true);
                pkg.getClassFieldAccessorStore().wire();
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

    AssetFilter getAssetFilter() {
        return assetFilter;
    }

    public void setAssetFilter(AssetFilter assetFilter) {
        this.assetFilter = assetFilter;
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
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(conf);
        kbase.addPackages(Arrays.asList(getPackages()));
        return kbase;
    }

    public TypeDeclaration getTypeDeclaration(Class<?> cls) {
        return cls != null ? typeBuilder.getTypeDeclaration(cls) : null;
    }

    public void normalizeTypeDeclarationAnnotations(PackageDescr packageDescr, TypeResolver typeResolver) {
        boolean isStrict = configuration.getLanguageLevel().useJavaAnnotations();
        for (TypeDeclarationDescr typeDeclarationDescr : packageDescr.getTypeDeclarations()) {
            normalizeAnnotations(typeDeclarationDescr, typeResolver, isStrict);
            for (TypeFieldDescr typeFieldDescr : typeDeclarationDescr.getFields().values()) {
                normalizeAnnotations(typeFieldDescr, typeResolver, isStrict);
            }
        }

        for (EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations()) {
            normalizeAnnotations(enumDeclarationDescr, typeResolver, isStrict);
            for (TypeFieldDescr typeFieldDescr : enumDeclarationDescr.getFields().values()) {
                normalizeAnnotations(typeFieldDescr, typeResolver, isStrict);
            }
        }
    }

    public void normalizeRuleAnnotations(PackageDescr packageDescr, TypeResolver typeResolver) {
        boolean isStrict = configuration.getLanguageLevel().useJavaAnnotations();
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            normalizeAnnotations(ruleDescr, typeResolver, isStrict);
            traverseAnnotations(ruleDescr.getLhs(), typeResolver, isStrict);
        }
    }

    private void traverseAnnotations(BaseDescr descr, TypeResolver typeResolver, boolean isStrict) {
        if (descr instanceof AnnotatedBaseDescr) {
            normalizeAnnotations((AnnotatedBaseDescr) descr, typeResolver, isStrict);
        }
        if (descr instanceof ConditionalElementDescr) {
            for (BaseDescr baseDescr : ((ConditionalElementDescr) descr).getDescrs()) {
                traverseAnnotations(baseDescr, typeResolver, isStrict);
            }
        }
        if (descr instanceof PatternDescr && ((PatternDescr) descr).getSource() != null) {
            traverseAnnotations(((PatternDescr) descr).getSource(), typeResolver, isStrict);
        }
        if (descr instanceof PatternDestinationDescr) {
            traverseAnnotations(((PatternDestinationDescr) descr).getInputPattern(), typeResolver, isStrict);
        }
    }

    protected void normalizeAnnotations(AnnotatedBaseDescr annotationsContainer, TypeResolver typeResolver, boolean isStrict) {
        for (AnnotationDescr annotationDescr : annotationsContainer.getAnnotations()) {
            annotationDescr.setResource(annotationsContainer.getResource());
            annotationDescr.setStrict(isStrict);
            if (annotationDescr.isDuplicated()) {
                addBuilderResult(new AnnotationDeclarationError(annotationDescr,
                                                                "Duplicated annotation: " + annotationDescr.getName()));
            }
            if (isStrict) {
                normalizeStrictAnnotation(typeResolver, annotationDescr);
            } else {
                normalizeAnnotation(typeResolver, annotationDescr);
            }
        }
        annotationsContainer.indexByFQN(isStrict);
    }

    private AnnotationDescr normalizeAnnotation(TypeResolver typeResolver, AnnotationDescr annotationDescr) {
        Class<?> annotationClass = null;
        try {
            annotationClass = typeResolver.resolveType(annotationDescr.getName(), TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            String className = normalizeAnnotationNonStrictName(annotationDescr.getName());
            try {
                annotationClass = typeResolver.resolveType(className, TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
            } catch (ClassNotFoundException | NoClassDefFoundError e1) {
                // non-strict annotation, ignore error
            }
        }
        if (annotationClass != null) {
            annotationDescr.setFullyQualifiedName(annotationClass.getCanonicalName());

            for (String key : annotationDescr.getValueMap().keySet()) {
                try {
                    Method m = annotationClass.getMethod(key);
                    Object val = annotationDescr.getValue(key);
                    if (val instanceof Object[] && !m.getReturnType().isArray()) {
                        addBuilderResult(new AnnotationDeclarationError(annotationDescr,
                                                                        "Wrong cardinality on property " + key));
                        return annotationDescr;
                    }
                    if (m.getReturnType().isArray() && !(val instanceof Object[])) {
                        val = new Object[]{val};
                        annotationDescr.setKeyValue(key, val);
                    }

                    if (m.getReturnType().isArray()) {
                        int n = Array.getLength(val);
                        for (int j = 0; j < n; j++) {
                            if (Class.class.equals(m.getReturnType().getComponentType())) {
                                String className = Array.get(val, j).toString().replace(".class", "");
                                Array.set(val, j, typeResolver.resolveType(className).getName() + ".class");
                            } else if (m.getReturnType().getComponentType().isAnnotation()) {
                                Array.set(val, j, normalizeAnnotation(typeResolver,
                                                                      (AnnotationDescr) Array.get(val, j)));
                            }
                        }
                    } else {
                        if (Class.class.equals(m.getReturnType())) {
                            String className = annotationDescr.getValueAsString(key).replace(".class", "");
                            annotationDescr.setKeyValue(key, typeResolver.resolveType(className));
                        } else if (m.getReturnType().isAnnotation()) {
                            annotationDescr.setKeyValue(key,
                                                        normalizeAnnotation(typeResolver,
                                                                            (AnnotationDescr) annotationDescr.getValue(key)));
                        }
                    }
                } catch (NoSuchMethodException e) {
                    addBuilderResult(new AnnotationDeclarationError(annotationDescr,
                                                                    "Unknown annotation property " + key));
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    addBuilderResult(new AnnotationDeclarationError(annotationDescr,
                                                                    "Unknown class " + annotationDescr.getValue(key) + " used in property " + key +
                                                                            " of annotation " + annotationDescr.getName()));
                }
            }
        }
        return annotationDescr;
    }

    private String normalizeAnnotationNonStrictName(String name) {
        if ("typesafe".equalsIgnoreCase(name)) {
            return "TypeSafe";
        }
        return ucFirst(name);
    }

    private void normalizeStrictAnnotation(TypeResolver typeResolver, AnnotationDescr annotationDescr) {
        try {
            Class<?> annotationClass = typeResolver.resolveType(annotationDescr.getName(), TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
            annotationDescr.setFullyQualifiedName(annotationClass.getCanonicalName());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            addBuilderResult(new AnnotationDeclarationError(annotationDescr,
                                                            "Unknown annotation: " + annotationDescr.getName()));
        }
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

    // composite build lifecycle

    public void buildPackages( Collection<CompositePackageDescr> packages ) {
        buildPackagesWithoutRules(packages);
        buildRules(packages);
    }

    public void buildPackagesWithoutRules(Collection<CompositePackageDescr> packages ) {
        initPackageRegistries(packages);
        normalizeTypeAnnotations( packages );
        buildTypeDeclarations(packages);
        buildEntryPoints( packages );
        buildOtherDeclarations(packages);
        normalizeRuleAnnotations( packages );
    }

    protected void initPackageRegistries(Collection<CompositePackageDescr> packages) {
        for ( CompositePackageDescr packageDescr : packages ) {
            if ( StringUtils.isEmpty(packageDescr.getName()) ) {
                packageDescr.setName( getBuilderConfiguration().getDefaultPackageName() );
            }
            getOrCreatePackageRegistry( packageDescr );
        }
    }

    protected void normalizeTypeAnnotations( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            normalizeTypeDeclarationAnnotations( packageDescr, getOrCreatePackageRegistry( packageDescr ).getTypeResolver() );
        }
    }

    protected void normalizeRuleAnnotations( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            normalizeRuleAnnotations( packageDescr, getOrCreatePackageRegistry( packageDescr ).getTypeResolver() );
        }
    }

    protected void buildEntryPoints( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            processEntryPointDeclarations(getPackageRegistry( packageDescr.getNamespace() ), packageDescr);
        }
    }

    protected void buildTypeDeclarations( Collection<CompositePackageDescr> packages ) {
        Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs = new HashMap<>();
        List<TypeDefinition> unresolvedTypes = new ArrayList<>();
        List<AbstractClassTypeDeclarationDescr> unsortedDescrs = new ArrayList<>();
        for (CompositePackageDescr packageDescr : packages) {
            unsortedDescrs.addAll(packageDescr.getTypeDeclarations());
            unsortedDescrs.addAll(packageDescr.getEnumDeclarations());
        }

        getTypeBuilder().processTypeDeclarations( packages, unsortedDescrs, unresolvedTypes, unprocesseableDescrs );

        for ( CompositePackageDescr packageDescr : packages ) {
            for ( ImportDescr importDescr : packageDescr.getImports() ) {
                getPackageRegistry( packageDescr.getNamespace() ).addImport( importDescr );
            }
        }
    }

    protected void buildOtherDeclarations(Collection<CompositePackageDescr> packages) {
        for (CompositePackageDescr packageDescr : packages) {
            setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = getPackageRegistry(packageDescr.getNamespace());
            processOtherDeclarations( pkgRegistry, packageDescr );
            setAssetFilter(null);
        }
    }

    protected void buildRules(Collection<CompositePackageDescr> packages) {
        for (CompositePackageDescr packageDescr : packages) {
            setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = getPackageRegistry(packageDescr.getNamespace());
            compileKnowledgePackages(packageDescr, pkgRegistry);
            setAssetFilter(null);
        }

        wireAllRules();
        processKieBaseTypes();

        for (CompositePackageDescr packageDescr : packages) {
            setAssetFilter(packageDescr.getFilter());
            compileRete( packageDescr );
            setAssetFilter(null);
        }
    }
}
