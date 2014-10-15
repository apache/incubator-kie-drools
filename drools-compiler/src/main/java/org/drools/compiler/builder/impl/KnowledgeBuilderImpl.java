package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.AnnotationDeclarationError;
import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.compiler.compiler.BaseKnowledgeBuilderResultImpl;
import org.drools.compiler.compiler.ConfigurableSeverityResult;
import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DeprecatedResourceTypeWarning;
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
import org.drools.compiler.compiler.PMMLCompiler;
import org.drools.compiler.compiler.PMMLCompilerFactory;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.compiler.compiler.PackageBuilderResults;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.compiler.ProcessBuilder;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.compiler.compiler.ProcessLoadError;
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
import org.drools.compiler.lang.descr.EntryPointDeclarationDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.FunctionImportDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
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
import org.drools.core.base.TypeResolver;
import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
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
import org.drools.core.type.DateFormats;
import org.drools.core.type.DateFormatsImpl;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.drools.core.xml.XmlChangeSetReader;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.ChangeSet;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.assembler.KieAssemblerService;
import org.kie.internal.assembler.KieAssemblers;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import static org.drools.core.util.ClassUtils.convertClassToResourcePath;
import static org.drools.core.util.StringUtils.isEmpty;
import static org.drools.core.util.StringUtils.ucFirst;

public class KnowledgeBuilderImpl implements KnowledgeBuilder {

    protected static final transient Logger logger = LoggerFactory.getLogger(KnowledgeBuilderImpl.class);

    private final Map<String, PackageRegistry> pkgRegistryMap;

    private List<KnowledgeBuilderResult>                   results;

    private final KnowledgeBuilderConfigurationImpl configuration;

    public static final RuleBuilder ruleBuilder        = new RuleBuilder();

    /**
     * Optional RuleBase for incremental live building
     */
    private InternalKnowledgeBase kBase;

    /**
     * default dialect
     */
    private final String                                   defaultDialect;

    private ClassLoader                                    rootClassLoader;

    private final Map<String, Class<?>>                    globals  = new HashMap<String, Class<?>>();

    private Resource                                       resource;

    private List<DSLTokenizedMappingFile>                  dslFiles;

    protected DateFormats dateFormats;

    private final org.drools.compiler.compiler.ProcessBuilder processBuilder;

    private IllegalArgumentException                          processBuilderCreationFailure;

    private PMMLCompiler                                      pmmlCompiler;

    //This list of package level attributes is initialised with the PackageDescr's attributes added to the assembler.
    //The package level attributes are inherited by individual rules not containing explicit overriding parameters.
    //The map is keyed on the PackageDescr's namespace and contains a map of AttributeDescr's keyed on the
    //AttributeDescr's name.
    private final Map<String, Map<String, AttributeDescr>>    packageAttributes  = new HashMap<String, Map<String, AttributeDescr>>();

    //PackageDescrs' list of ImportDescrs are kept identical as subsequent PackageDescrs are added.
    private final Map<String, List<PackageDescr>>             packages           = new HashMap<String, List<PackageDescr>>();

    private final Stack<List<Resource>> buildResources     = new Stack<List<Resource>>();

    private int                                               currentRulePackage = 0;

    private AssetFilter                                       assetFilter        = null;

    private final TypeDeclarationBuilder                      typeBuilder;

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
     *
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

        this.dateFormats = null;//(DateFormats) this.environment.get( EnvironmentName.DATE_FORMATS );
        if (this.dateFormats == null) {
            this.dateFormats = new DateFormatsImpl();
            //this.environment.set( EnvironmentName.DATE_FORMATS , this.dateFormats );
        }

        this.rootClassLoader = this.configuration.getClassLoader();

        this.defaultDialect = this.configuration.getDefaultDialect();

        this.pkgRegistryMap = new LinkedHashMap<String, PackageRegistry>();
        this.results = new ArrayList<KnowledgeBuilderResult>();

        PackageRegistry pkgRegistry = new PackageRegistry(rootClassLoader, this.configuration, pkg);
        pkgRegistry.setDialect(this.defaultDialect);
        this.pkgRegistryMap.put(pkg.getName(),
                                pkgRegistry);

        // add imports to pkg registry
        for (final ImportDeclaration implDecl : pkg.getImports().values()) {
            pkgRegistry.addImport(new ImportDescr(implDecl.getTarget()));
        }

        processBuilder = createProcessBuilder();
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

        this.dateFormats = null;//(DateFormats) this.environment.get( EnvironmentName.DATE_FORMATS );
        if (this.dateFormats == null) {
            this.dateFormats = new DateFormatsImpl();
            //this.environment.set( EnvironmentName.DATE_FORMATS , this.dateFormats );
        }

        // FIXME, we need to get drools to support "default" namespace.
        //this.defaultNamespace = pkg.getName();
        this.defaultDialect = this.configuration.getDefaultDialect();

        this.pkgRegistryMap = new LinkedHashMap<String, PackageRegistry>();
        this.results = new ArrayList<KnowledgeBuilderResult>();

        this.kBase = kBase;

        processBuilder = createProcessBuilder();
        typeBuilder = new TypeDeclarationBuilder(this);
    }

    private ProcessBuilder createProcessBuilder() {
        try {
            return ProcessBuilderFactory.newProcessBuilder(this);
        } catch (IllegalArgumentException e) {
            processBuilderCreationFailure = e;
            return null;
        }
    }

    private PMMLCompiler getPMMLCompiler() {
        if (this.pmmlCompiler == null) {
            this.pmmlCompiler = PMMLCompilerFactory.getPMMLCompiler();
        }
        return this.pmmlCompiler;
    }

    Resource getCurrentResource() {
        return resource;
    }

    InternalKnowledgeBase getKnowledgeBase() {
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
     * @throws IOException
     */
    public void addPackageFromDrl(final Reader reader,
                                  final Resource sourceResource) throws DroolsParserException,
                                                                        IOException {
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
                                             ResourceConfiguration configuration) throws DroolsParserException,
                                                                                         IOException {
        DecisionTableConfiguration dtableConfiguration = configuration instanceof DecisionTableConfiguration ?
                                                         (DecisionTableConfiguration) configuration :
                                                         null;
        String generatedDrl = DecisionTableFactory.loadFromInputStream(resource.getInputStream(), dtableConfiguration);
        // dump the generated DRL if the dump dir was configured
        if (this.configuration.getDumpDir() != null) {
            dumpDrlGeneratedFromDTable(this.configuration.getDumpDir(), generatedDrl, resource.getSourcePath());
        }

        DrlParser parser = new DrlParser(this.configuration.getLanguageLevel());
        PackageDescr pkg = parser.parse(resource, new StringReader(generatedDrl));
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            addBuilderResult(new ParserError(resource, "Parser returned a null Package", 0, 0));
        }
        return parser.hasErrors() ? null : pkg;
    }

    private void dumpDrlGeneratedFromDTable(File dumpDir, String generatedDrl, String srcPath) {
        File dumpFile;
        if (srcPath != null) {
            dumpFile = new File(dumpDir, srcPath.replaceAll(File.separator, "_") + ".drl");
        } else {
            dumpFile = new File(dumpDir, "decision-table-" + UUID.randomUUID() + ".drl");
        }
        try {
            IoUtils.write(dumpFile, generatedDrl.getBytes(IoUtils.UTF8_CHARSET));
        } catch (IOException ex) {
            // nothing serious, just failure when writing the generated DRL to file, just log the exception and continue
            logger.warn("Can't write the DRL generated from decision table to file " + dumpFile.getAbsolutePath() + "!\n" +
                    Arrays.toString(ex.getStackTrace()));
        }
    }

    public void addPackageFromScoreCard(Resource resource,
                                        ResourceConfiguration configuration) throws DroolsParserException,
                                                                                    IOException {
        this.resource = resource;
        addPackage(scoreCardToPackageDescr(resource, configuration));
        this.resource = null;
    }

    PackageDescr scoreCardToPackageDescr(Resource resource,
                                         ResourceConfiguration configuration) throws DroolsParserException,
                                                                                     IOException {
        ScoreCardConfiguration scardConfiguration = configuration instanceof ScoreCardConfiguration ?
                                                    (ScoreCardConfiguration) configuration :
                                                    null;
        String string = ScoreCardFactory.loadFromInputStream(resource.getInputStream(), scardConfiguration);

        DrlParser parser = new DrlParser(this.configuration.getLanguageLevel());
        PackageDescr pkg = parser.parse(resource, new StringReader(string));
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            addBuilderResult(new ParserError(resource, "Parser returned a null Package", 0, 0));
        }
        return parser.hasErrors() ? null : pkg;
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

        Reader reader = null;
        try {
            reader = resource.getReader();
            xmlReader.read(reader);
        } catch (final SAXException e) {
            throw new DroolsParserException(e.toString(),
                                            e.getCause());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return xmlReader.getPackageDescr();
    }

    /**
     * Load a rule package from DRL source using the supplied DSL configuration.
     *
     * @param source
     *            The source of the rules.
     * @param dsl
     *            The source of the domain specific language configuration.
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

    PackageDescr dslrToPackageDescr(Resource resource) throws DroolsParserException {
        boolean hasErrors;
        PackageDescr pkg;

        DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        DefaultExpander expander = getDslExpander();

        Reader reader = null;
        try {
            if (expander == null) {
                expander = new DefaultExpander();
            }
            reader = resource.getReader();
            String str = expander.expand(reader);
            if (expander.hasErrors()) {
                for (ExpanderException error : expander.getErrors()) {
                    error.setResource(resource);
                    addBuilderResult(error);
                }
            }

            pkg = parser.parse(resource, str);
            this.results.addAll(parser.getErrors());
            hasErrors = parser.hasErrors();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return hasErrors ? null : pkg;
    }

    public void addDsl(Resource resource) throws IOException {
        this.resource = resource;
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();

        Reader reader = null;
        try {
            reader = resource.getReader();
            if (!file.parseAndLoad(reader)) {
                this.results.addAll(file.getErrors());
            }
            if (this.dslFiles == null) {
                this.dslFiles = new ArrayList<DSLTokenizedMappingFile>();
            }
            this.dslFiles.add(file);
        } finally {
            if (reader != null) {
                reader.close();
            }
            this.resource = null;
        }

    }

    /**
     * Add a ruleflow (.rfm) asset to this package.
     */
    public void addRuleFlow(Reader processSource) {
        addProcessFromXml(processSource);
    }

    public void addProcessFromXml(Resource resource) {
        if (processBuilder == null) {
            throw new RuntimeException("Unable to instantiate a process assembler", processBuilderCreationFailure);
        }

        if (ResourceType.DRF.equals(resource.getResourceType())) {
            addBuilderResult(new DeprecatedResourceTypeWarning(resource, "RF"));
        }

        this.resource = resource;

        try {
            List<Process> processes = processBuilder.addProcessFromXml(resource);
            List<BaseKnowledgeBuilderResultImpl> errors = processBuilder.getErrors();
            if ( errors.isEmpty() ) {
                if ( this.kBase != null && processes != null ) {
                    for (Process process : processes) {
                        if ( filterAccepts( process.getNamespace(), process.getId() ) ) {
                            this.kBase.addProcess(process);
                        }
                    }
                }
            } else {
                this.results.addAll(errors);
                errors.clear();
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            addBuilderResult(new ProcessLoadError(resource, "Unable to load process.", e));
        }
        this.results = getResults(this.results);
        this.resource = null;
    }

    public void addProcessFromXml(Reader processSource) {
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
            } else if (ResourceType.DRF.equals(type)) {
                addProcessFromXml(resource);
            } else if (ResourceType.BPMN2.equals(type)) {
                BPMN2ProcessFactory.configurePackageBuilder(this);
                addProcessFromXml(resource);
            } else if (ResourceType.DTABLE.equals(type)) {
                addPackageFromDecisionTable(resource, configuration);
            } else if (ResourceType.PKG.equals(type)) {
                addPackageFromInputStream(resource);
            } else if (ResourceType.CHANGE_SET.equals(type)) {
                addPackageFromChangeSet(resource);
            } else if (ResourceType.XSD.equals(type)) {
                addPackageFromXSD(resource, (JaxbConfigurationImpl) configuration);
            } else if (ResourceType.PMML.equals(type)) {
                addPackageFromPMML(resource, type, configuration);
            } else if (ResourceType.SCARD.equals(type)) {
                addPackageFromScoreCard(resource, configuration);
            } else if (ResourceType.TDRL.equals(type)) {
                addPackageFromDrl(resource);
            } else {
                addPackageForExternalType(resource, type, configuration);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void addPackageForExternalType(Resource resource,
                                   ResourceType type,
                                   ResourceConfiguration configuration) throws Exception {
        KieAssemblers assemblers = ServiceRegistryImpl.getInstance().get(KieAssemblers.class);

        KieAssemblerService assembler = assemblers.getAssemblers().get(type);


        if (assembler != null) {
            assembler.addResource(this,
                                  resource,
                                  type,
                                  configuration);
        } else {
            throw new RuntimeException("Unknown resource type: " + type);
        }
    }

    public void addPackageFromPMML(Resource resource,
                                   ResourceType type,
                                   ResourceConfiguration configuration) throws Exception {
        PMMLCompiler compiler = getPMMLCompiler();
        if (compiler != null) {
            if (compiler.getResults().isEmpty()) {
                this.resource = resource;
                PackageDescr descr = pmmlModelToPackageDescr(compiler, resource);
                if (descr != null) {
                    addPackage(descr);
                }
                this.resource = null;
            } else {
                this.results.addAll(compiler.getResults());
            }
            compiler.clearResults();
        } else {
            addPackageForExternalType(resource, type, configuration);
        }
    }

    PackageDescr pmmlModelToPackageDescr(PMMLCompiler compiler,
                                         Resource resource) throws DroolsParserException,
                                                                   IOException {
        String theory = compiler.compile(resource.getInputStream(),
                                         rootClassLoader);

        if (!compiler.getResults().isEmpty()) {
            this.results.addAll(compiler.getResults());
            return null;
        }

        DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        PackageDescr pkg = parser.parse(resource, new StringReader(theory));
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            addBuilderResult(new ParserError(resource, "Parser returned a null Package", 0, 0));
            return pkg;
        } else {
            return parser.hasErrors() ? null : pkg;
        }
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
        Reader resourceReader = null;
        try {
            resourceReader = resource.getReader();
            ChangeSet changeSet = reader.read(resourceReader);
            if (changeSet == null) {
                // @TODO should log an error
            }
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
        } finally {
            if (resourceReader != null) {
                resourceReader.close();
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
            Collection<KnowledgePackage> pkgs = (Collection<KnowledgePackage>) object;
            for (KnowledgePackage kpkg : pkgs) {
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
            if (isSwappable(((RuleImpl)r).getResource(), res)) {
                ((RuleImpl)r).setResource(res);
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
        PackageRegistry pkgRegistry = initPackageRegistry(packageDescr);
        if (pkgRegistry == null) {
            return;
        }

        currentRulePackage = pkgRegistryMap.size() - 1;

        // merge into existing package
        mergePackage(pkgRegistry, packageDescr);

        compileAllRules(packageDescr, pkgRegistry);
    }

    void compileAllRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        pkgRegistry.setDialect(getPackageDialect(packageDescr));

        validateUniqueRuleNames( packageDescr );
        compileRules(packageDescr, pkgRegistry);

        compileAll();
        try {
            reloadAll();
        } catch (Exception e) {
            addBuilderResult(new DialectError(null, "Unable to wire compiled classes, probably related to compilation failures:" + e.getMessage()));
        }
        updateResults();

        // iterate and compile
        if (!hasErrors() && this.kBase != null) {
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                if( filterAccepts( ruleDescr.getNamespace(), ruleDescr.getName() ) ) {
                    pkgRegistry = this.pkgRegistryMap.get(ruleDescr.getNamespace());
                    this.kBase.addRule(pkgRegistry.getPackage(), pkgRegistry.getPackage().getRule(ruleDescr.getName()));
                }
            }
        }
    }

    void addBuilderResult(KnowledgeBuilderResult result) {
        this.results.add(result);
    }

    PackageRegistry createPackageRegistry(PackageDescr packageDescr) {
        PackageRegistry pkgRegistry = initPackageRegistry(packageDescr);
        if (pkgRegistry == null) {
            return null;
        }
        for (ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.registerImport( importDescr.getTarget() );
        }
        return pkgRegistry;
    }

    private PackageRegistry initPackageRegistry(PackageDescr packageDescr) {
        if (packageDescr == null) {
            return null;
        }

        //Derive namespace
        if (isEmpty(packageDescr.getNamespace())) {
            packageDescr.setNamespace(this.configuration.getDefaultPackageName());
        }

        if (!checkNamespace(packageDescr.getNamespace())) {
            return null;
        }

        initPackage(packageDescr);

        PackageRegistry pkgRegistry = this.pkgRegistryMap.get(packageDescr.getNamespace());
        if (pkgRegistry == null) {
            // initialise the package and namespace if it hasn't been used before
            pkgRegistry = newPackage(packageDescr);
        }

        return pkgRegistry;
    }

    private void compileRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        List<FunctionDescr> functions = packageDescr.getFunctions();
        if (!functions.isEmpty()) {

            for (FunctionDescr functionDescr : functions) {
                if (filterAccepts(functionDescr.getNamespace(), functionDescr.getName()) ) {
                    if (isEmpty(functionDescr.getNamespace())) {
                        // make sure namespace is set on components
                        functionDescr.setNamespace(packageDescr.getNamespace());
                    }

                    // make sure functions are compiled using java dialect
                    functionDescr.setDialect("java");

                    preCompileAddFunction(functionDescr);
                }
            }

            // iterate and compile
            for (FunctionDescr functionDescr : functions) {
                if (filterAccepts(functionDescr.getNamespace(), functionDescr.getName()) ) {
                    // inherit the dialect from the package
                    addFunction(functionDescr);
                }
            }

            // We need to compile all the functions now, so scripting
            // languages like mvel can find them
            compileAll();

            for (FunctionDescr functionDescr : functions) {
                if (filterAccepts(functionDescr.getNamespace(), functionDescr.getName()) ) {
                    postCompileAddFunction(functionDescr);
                }
            }
        }

        // ensure that rules are ordered by dependency, so that dependent rules are built later
        sortRulesByDependency(packageDescr);

        // iterate and prepare RuleDescr
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            if (isEmpty(ruleDescr.getNamespace())) {
                // make sure namespace is set on components
                ruleDescr.setNamespace(packageDescr.getNamespace());
            }

            Map<String, AttributeDescr> pkgAttributes = packageAttributes.get(packageDescr.getNamespace());
            inheritPackageAttributes(pkgAttributes,
                                     ruleDescr);

            if (isEmpty(ruleDescr.getDialect())) {
                ruleDescr.addAttribute(new AttributeDescr("dialect",
                                                          pkgRegistry.getDialect()));
            }
        }

        // Build up map of contexts  and process all rules
        Map<String, RuleBuildContext> ruleCxts = preProcessRules(packageDescr, pkgRegistry);

        // iterate and compile
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            if (filterAccepts(ruleDescr.getNamespace(), ruleDescr.getName()) ) {
                addRule(ruleCxts.get(ruleDescr.getName()));
            }
        }
    }

    boolean filterAccepts( String namespace, String name ) {
        return assetFilter == null || ! AssetFilter.Action.DO_NOTHING.equals( assetFilter.accept( namespace, name ) );
    }

    private boolean filterAcceptsRemoval( String namespace, String name ) {
        return assetFilter != null && AssetFilter.Action.REMOVE.equals( assetFilter.accept( namespace, name ) );
    }

    private Map<String, RuleBuildContext> preProcessRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        Map<String, RuleBuildContext> ruleCxts = buildRuleBuilderContext(packageDescr.getRules());

        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        if (this.kBase != null) {
            boolean needsRemoval = false;

            // first, check if any rules no longer exist
            for( org.kie.api.definition.rule.Rule rule : pkg.getRules() ) {
                if (filterAcceptsRemoval( rule.getPackageName(), rule.getName() ) ) {
                    needsRemoval = true;
                    break;
                }
            }

            if( !needsRemoval ) {
                for (RuleDescr ruleDescr : packageDescr.getRules()) {
                    if (filterAccepts(ruleDescr.getNamespace(), ruleDescr.getName()) ) {
                        if (pkg.getRule(ruleDescr.getName()) != null) {
                            needsRemoval = true;
                            break;
                        }
                    }
                }
            }

            if (needsRemoval) {
                try {
                    this.kBase.lock();
                    for( org.kie.api.definition.rule.Rule rule : pkg.getRules() ) {
                        if (filterAcceptsRemoval( rule.getPackageName(), rule.getName() ) ) {
                            this.kBase.removeRule(pkg, pkg.getRule(rule.getName()));
                            pkg.removeRule(((RuleImpl)rule));
                        }
                    }
                    for (RuleDescr ruleDescr : packageDescr.getRules()) {
                        if (filterAccepts(ruleDescr.getNamespace(), ruleDescr.getName()) ) {
                            if (pkg.getRule(ruleDescr.getName()) != null) {
                                // XXX: this one notifies listeners
                                this.kBase.removeRule(pkg, pkg.getRule(ruleDescr.getName()));
                            }
                        }
                    }
                } finally {
                    this.kBase.unlock();
                }
            }
        }

        // Pre Process each rule, needed for Query signuture registration
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            if (filterAccepts(ruleDescr.getNamespace(), ruleDescr.getName()) ) {
                RuleBuildContext ruleBuildContext = ruleCxts.get(ruleDescr.getName());
                ruleBuilder.preProcess(ruleBuildContext);
                pkg.addRule(ruleBuildContext.getRule());
            }
        }
        return ruleCxts;
    }

    private void sortRulesByDependency(PackageDescr packageDescr) {
        // Using a topological sorting algorithm
        // see http://en.wikipedia.org/wiki/Topological_sorting

        PackageRegistry pkgRegistry = this.pkgRegistryMap.get(packageDescr.getNamespace());
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();

        List<RuleDescr> roots = new LinkedList<RuleDescr>();
        Map<String, List<RuleDescr>> children = new HashMap<String, List<RuleDescr>>();
        LinkedHashMap<String, RuleDescr> sorted = new LinkedHashMap<String, RuleDescr>();
        List<RuleDescr> queries = new ArrayList<RuleDescr>();

        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            if (ruleDescr.isQuery()) {
                queries.add(ruleDescr);
            } else if (!ruleDescr.hasParent()) {
                roots.add(ruleDescr);
            } else if (pkg.getRule(ruleDescr.getParentName()) != null) {
                // The parent of this rule has been already compiled
                sorted.put(ruleDescr.getName(), ruleDescr);
            } else {
                List<RuleDescr> childz = children.get(ruleDescr.getParentName());
                if (childz == null) {
                    childz = new ArrayList<RuleDescr>();
                    children.put(ruleDescr.getParentName(), childz);
                }
                childz.add(ruleDescr);
            }
        }

        if (children.isEmpty()) { // Sorting not necessary
            if (!queries.isEmpty()) { // Build all queries first
                packageDescr.getRules().removeAll(queries);
                packageDescr.getRules().addAll(0, queries);
            }
            return;
        }

        while (!roots.isEmpty()) {
            RuleDescr root = roots.remove(0);
            sorted.put(root.getName(), root);
            List<RuleDescr> childz = children.remove(root.getName());
            if (childz != null) {
                roots.addAll(childz);
            }
        }

        reportHierarchyErrors(children, sorted);

        packageDescr.getRules().clear();
        packageDescr.getRules().addAll(queries);
        for (RuleDescr descr : sorted.values()) {
            packageDescr.getRules().add(descr);
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
                    results.add(new RuleBuildError(new RuleImpl(ruleDescr.getName()), ruleDescr, null,
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
        List<String> candidateRules = new LinkedList<String>();
        for (RuleDescr r : candidates) {
            if (StringUtils.stringSimilarity(ruleDescr.getParentName(), r.getName(), StringUtils.SIMILARITY_STRATS.DICE) >= 0.75) {
                candidateRules.add(r.getName());
            }
        }
        String msg = "Unresolved parent name " + ruleDescr.getParentName();
        if (candidateRules.size() > 0) {
            msg += " >> did you mean any of :" + candidateRules;
        }
        results.add(new RuleBuildError(new RuleImpl(ruleDescr.getName()), ruleDescr, msg,
                                       "Unable to resolve parent rule, please check that both rules are in the same package"));
    }

    private void initPackage(PackageDescr packageDescr) {
        //Gather all imports for all PackageDescrs for the current package and replicate into
        //all PackageDescrs for the current package, thus maintaining a complete list of
        //ImportDescrs for all PackageDescrs for the current package.
        List<PackageDescr> packageDescrsForPackage = packages.get(packageDescr.getName());
        if (packageDescrsForPackage == null) {
            packageDescrsForPackage = new ArrayList<PackageDescr>();
            packages.put(packageDescr.getName(),
                         packageDescrsForPackage);
        }
        packageDescrsForPackage.add(packageDescr);
        Set<ImportDescr> imports = new HashSet<ImportDescr>();
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
                pkgAttributes = new HashMap<String, AttributeDescr>();
                this.packageAttributes.put(packageDescr.getNamespace(),
                                           pkgAttributes);
            }
            for (AttributeDescr attr : packageDescr.getAttributes()) {
                pkgAttributes.put(attr.getName(),
                                  attr);
            }
        }
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

    /**
     * This checks to see if it should all be in the one namespace.
     */
    private boolean checkNamespace(String newName) {
        return configuration == null ||
               !((!pkgRegistryMap.isEmpty()) && (!pkgRegistryMap.containsKey(newName))) ||
               configuration.isAllowMultipleNamespaces();
    }

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
            pkgRegistry = newPackage(packageDescr);
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
            if (newPkg.getGlobals() != null && newPkg.getGlobals() != Collections.EMPTY_MAP) {
                Map<String, String> globals = pkg.getGlobals();
                // Add globals
                for (final Map.Entry<String, String> entry : newPkg.getGlobals().entrySet()) {
                    final String identifier = entry.getKey();
                    final String type = entry.getValue();
                    lastType = type;
                    if (globals.containsKey(identifier) && !globals.get(identifier).equals(type)) {
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
            pkg.addRule(((RuleImpl)newRule));
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

    //
    //    private void validatePackageName(final PackageDescr packageDescr) {
    //        if ( (this.pkg == null || this.pkg.getName() == null || this.pkg.getName().equals( "" )) && (packageDescr.getName() == null || "".equals( packageDescr.getName() )) ) {
    //            throw new MissingPackageNameException( "Missing package name for rule package." );
    //        }
    //        if ( this.pkg != null && packageDescr.getName() != null && !"".equals( packageDescr.getName() ) && !this.pkg.getName().equals( packageDescr.getName() ) ) {
    //            throw new PackageMergeException( "Can't merge packages with different names. This package: " + this.pkg.getName() + " - New package: " + packageDescr.getName() );
    //        }
    //        return;
    //    }

    private void validateUniqueRuleNames(final PackageDescr packageDescr) {
        final Set<String> names = new HashSet<String>();
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

    public PackageRegistry newPackage(final PackageDescr packageDescr) {
        InternalKnowledgePackage pkg;
        if (this.kBase == null || (pkg = this.kBase.getPackage(packageDescr.getName())) == null) {
            // there is no rulebase or it does not define this package so define it
            pkg = new KnowledgePackageImpl(packageDescr.getName());
            pkg.setClassFieldAccessorCache(new ClassFieldAccessorCache(this.rootClassLoader));

            // if there is a rulebase then add the package.
            if (this.kBase != null) {
                // Must lock here, otherwise the assumption about addPackage/getPackage behavior below might be violated
                this.kBase.lock();
                try {
                    this.kBase.addPackage(pkg);
                    pkg = this.kBase.getPackage(packageDescr.getName());
                } finally {
                    this.kBase.unlock();
                }
            } else {
                // the RuleBase will also initialise the
                pkg.getDialectRuntimeRegistry().onAdd(this.rootClassLoader);
            }
        }

        PackageRegistry pkgRegistry = new PackageRegistry(rootClassLoader, configuration, pkg);

        // add default import for this namespace
        pkgRegistry.addImport(new ImportDescr(packageDescr.getNamespace() + ".*"));

        this.pkgRegistryMap.put(packageDescr.getName(), pkgRegistry);

        return pkgRegistry;
    }

    void mergePackage(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        for (final ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.addImport(importDescr);
        }

        normalizeTypeDeclarationAnnotations( packageDescr );
        processAccumulateFunctions(pkgRegistry, packageDescr);
        processEntryPointDeclarations(pkgRegistry, packageDescr);

        Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs = new HashMap<String,AbstractClassTypeDeclarationDescr>();
        List<TypeDefinition> unresolvedTypes = new ArrayList<TypeDefinition>();
        List<AbstractClassTypeDeclarationDescr> unsortedDescrs = new ArrayList<AbstractClassTypeDeclarationDescr>();
        for ( TypeDeclarationDescr typeDeclarationDescr : packageDescr.getTypeDeclarations() ) {
            unsortedDescrs.add( typeDeclarationDescr );
        }
        for ( EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations() ) {
            unsortedDescrs.add( enumDeclarationDescr );
        }

        typeBuilder.processTypeDeclarations( Arrays.asList( packageDescr ), unsortedDescrs, unresolvedTypes, unprocesseableDescrs );
        for ( AbstractClassTypeDeclarationDescr descr : unprocesseableDescrs.values() ) {
            this.addBuilderResult( new TypeDeclarationError( descr, "Unable to process type " + descr.getTypeName() ) );
        }

        processOtherDeclarations( pkgRegistry, packageDescr );
        normalizeRuleAnnotations( packageDescr );
    }

    void processOtherDeclarations(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        processAccumulateFunctions( pkgRegistry, packageDescr);
        processWindowDeclarations(pkgRegistry, packageDescr);
        processFunctions(pkgRegistry, packageDescr);
        processGlobals(pkgRegistry, packageDescr);

        // need to reinsert this to ensure that the package is the first/last one in the ordered map
        // this feature is exploited by the knowledgeAgent
        InternalKnowledgePackage current = getPackage();
        this.pkgRegistryMap.remove(packageDescr.getName());
        this.pkgRegistryMap.put(packageDescr.getName(), pkgRegistry);
        if (!current.getName().equals(packageDescr.getName())) {
            currentRulePackage = pkgRegistryMap.size() - 1;
        }
    }

    private void processGlobals(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        for (final GlobalDescr global : packageDescr.getGlobals()) {
            final String identifier = global.getIdentifier();
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
                pkgRegistry.getPackage().addGlobal(identifier,
                                                   clazz);
                this.globals.put(identifier,
                                 clazz);
                if (kBase != null) {
                    kBase.addGlobal(identifier, clazz);
                }
            } catch (final ClassNotFoundException e) {
                addBuilderResult(new GlobalError(global, e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    private void processAccumulateFunctions(PackageRegistry pkgRegistry,
                                            PackageDescr packageDescr) {
        for (final AccumulateImportDescr aid : packageDescr.getAccumulateImports() ) {
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
            Class< ? extends AccumulateFunction> clazz = (Class< ? extends AccumulateFunction>) pkgRegistry.getTypeResolver().resolveType(className);
            return clazz.newInstance();
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( "Error loading accumulate function for identifier " + identifier + ". Class " + className + " not found",
                                        e );
        } catch ( InstantiationException e ) {
            throw new RuntimeException( "Error loading accumulate function for identifier " + identifier + ". Instantiation failed for class " + className,
                                        e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( "Error loading accumulate function for identifier " + identifier + ". Illegal access to class " + className,
                                        e );
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
        return typeBuilder.getAndRegisterTypeDeclaration(cls, packageName);
    }

    /**
     * @param packageDescr
     */
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

    private void addFunction(final FunctionDescr functionDescr) {
        functionDescr.setResource(this.resource);
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get(functionDescr.getNamespace());
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect(functionDescr.getDialect());
        dialect.addFunction(functionDescr,
                            pkgRegistry.getTypeResolver(),
                            this.resource);
    }

    private void preCompileAddFunction(final FunctionDescr functionDescr) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get(functionDescr.getNamespace());
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect(functionDescr.getDialect());
        dialect.preCompileAddFunction(functionDescr,
                                      pkgRegistry.getTypeResolver());
    }

    private void postCompileAddFunction(final FunctionDescr functionDescr) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get(functionDescr.getNamespace());
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect(functionDescr.getDialect());
        dialect.postCompileAddFunction(functionDescr, pkgRegistry.getTypeResolver());

        if (rootClassLoader instanceof ProjectClassLoader) {
            String functionClassName = functionDescr.getClassName();
            JavaDialectRuntimeData runtime = ((JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData( "java" ));
            byte [] def = runtime.getStore().get(convertClassToResourcePath(functionClassName));
            if (def != null) {
                ((ProjectClassLoader)rootClassLoader).storeClass(functionClassName, def);
            }
        }
    }

    private Map<String, RuleBuildContext> buildRuleBuilderContext(List<RuleDescr> rules) {
        Map<String, RuleBuildContext> map = new HashMap<String, RuleBuildContext>();
        for (RuleDescr ruleDescr : rules) {
            if (ruleDescr.getResource() == null) {
                ruleDescr.setResource(resource);
            }

            PackageRegistry pkgRegistry = this.pkgRegistryMap.get(ruleDescr.getNamespace());

            InternalKnowledgePackage pkg = pkgRegistry.getPackage();
            DialectCompiletimeRegistry ctr = pkgRegistry.getDialectCompiletimeRegistry();
            RuleBuildContext context = new RuleBuildContext(this,
                                                            ruleDescr,
                                                            ctr,
                                                            pkg,
                                                            ctr.getDialect(pkgRegistry.getDialect()));
            map.put(ruleDescr.getName(), context);
        }

        return map;
    }

    private void addRule(RuleBuildContext context) {
        final RuleDescr ruleDescr = context.getRuleDescr();

        InternalKnowledgePackage pkg = context.getPkg();

        ruleBuilder.build(context);

        this.results.addAll(context.getErrors());
        this.results.addAll(context.getWarnings());

        context.getRule().setResource(ruleDescr.getResource());

        context.getDialect().addRule(context);

        if (context.needsStreamMode()) {
            pkg.setNeedStreamMode();
        }
    }

    /**
     * @return The compiled package. The package may contain errors, which you
     *         can report on by calling getErrors or printErrors. If you try to
     *         add an invalid package (or rule) to a RuleBase, you will get a
     *         runtime exception.
     *
     *         Compiled packages are serializable.
     */
    public InternalKnowledgePackage getPackage() {
        PackageRegistry pkgRegistry = null;
        if (!this.pkgRegistryMap.isEmpty()) {
            pkgRegistry = (PackageRegistry) this.pkgRegistryMap.values().toArray()[currentRulePackage];
        }
        InternalKnowledgePackage pkg = null;
        if (pkgRegistry != null) {
            pkg = pkgRegistry.getPackage();
        }
        if (hasErrors() && pkg != null) {
            pkg.setError(getErrors().toString());
        }
        return pkg;
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

    public Map<String, PackageRegistry> getPackageRegistry() {
        return this.pkgRegistryMap;
    }

    public DateFormats getDateFormats() {
        return this.dateFormats;
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

    /**
     * @param severities
     * @return
     */
    private List<KnowledgeBuilderResult> getResultList(ResultSeverity... severities) {
        List<ResultSeverity> typesToFetch = Arrays.asList(severities);
        ArrayList<KnowledgeBuilderResult> problems = new ArrayList<KnowledgeBuilderResult>();
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
        List<DroolsError> errors = new ArrayList<DroolsError>();
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
        List<DroolsWarning> warnings = new ArrayList<DroolsWarning>();
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

    /**
     * @return A list of Error objects that resulted from building and compiling
     *         the package.
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
        List<KnowledgeBuilderResult> toBeDeleted = new ArrayList<KnowledgeBuilderResult>();
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

        private static final long serialVersionUID = 510l;

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
            String name = attrDescr.getName();
            AttributeDescr ruleAttrDescr = ruleDescr.getAttributes().get(name);
            if (ruleAttrDescr == null) {
                ruleDescr.getAttributes().put(name,
                                              attrDescr);
            }
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
        Reader resourceReader = null;

        try {
            resourceReader = resource.getReader();
            ChangeSet changeSet = reader.read(resourceReader);
            return changeSet;
        } finally {
            if (resourceReader != null) {
                resourceReader.close();
            }
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
                List<Resource> resources = new ArrayList<Resource>();
                resources.add(resource);
                for (Resource addedRes : changeSet.getResourcesAdded()) {
                    resources.add(addedRes);
                }
                for (Resource modifiedRes : changeSet.getResourcesModified()) {
                    resources.add(modifiedRes);
                }
                for (Resource removedRes : changeSet.getResourcesRemoved()) {
                    resources.add(removedRes);
                }
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
            buildResources.push(Arrays.asList(resource));
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

    public boolean removeObjectsGeneratedFromResource(Resource resource) {
        boolean modified = false;
        if (pkgRegistryMap != null) {
            for (PackageRegistry packageRegistry : pkgRegistryMap.values()) {
                modified = packageRegistry.removeObjectsGeneratedFromResource(resource) || modified;
            }
        }

        if (results != null) {
            Iterator<KnowledgeBuilderResult> i = results.iterator();
            while (i.hasNext()) {
                if (resource.equals(i.next().getResource())) {
                    i.remove();
                }
            }
        }

        if (processBuilder != null && processBuilder.getErrors() != null) {
            Iterator<? extends KnowledgeBuilderResult> i = processBuilder.getErrors().iterator();
            while (i.hasNext()) {
                if (resource.equals(i.next().getResource())) {
                    i.remove();
                }
            }
        }

        if (results.size() == 0) {
            // TODO Error attribution might be bugged
            for (PackageRegistry packageRegistry : pkgRegistryMap.values()) {
                packageRegistry.getPackage().resetErrors();
            }
        }

        typeBuilder.removeTypesGeneratedFromResource(resource);

        for (List<PackageDescr> pkgDescrs : packages.values()) {
            for (PackageDescr pkgDescr : pkgDescrs) {
                pkgDescr.removeObjectsGeneratedFromResource(resource);
            }
        }

        if (kBase != null) {
            modified = kBase.removeObjectsGeneratedFromResource(resource) || modified;
        }

        return modified;
    }

    public void startPackageUpdate() {
        if (kBase != null) {
            kBase.lock();
        }
    }

    public void completePackageUpdate() {
        if (kBase != null) {
            kBase.unlock();
        }
    }

    public void setAllRuntimesDirty(Collection<String> packages) {
        if (kBase != null) {
            for (String pkgName : packages) {
                InternalKnowledgePackage pkg = kBase.getPackage(pkgName);
                if (pkg != null) {
                    pkg.getDialectRuntimeRegistry().getDialectData("java").setDirty(true);
                }
            }
        }
    }

    public void rewireClassObjectTypes(Collection<String> packages) {
        if (kBase != null) {
            for (String pkgName : packages) {
                InternalKnowledgePackage pkg = kBase.getPackage(pkgName);
                if (pkg != null) {
                    pkg.getClassFieldAccessorStore().wire();
                }
            }
        }
    }

    public boolean isClassInUse(String className) {
        return !(rootClassLoader instanceof ProjectClassLoader) || ((ProjectClassLoader) rootClassLoader).isClassInUse(className);
    }

    public static interface AssetFilter {
        public static enum Action {
            DO_NOTHING, ADD, REMOVE, UPDATE;
        }

        public Action accept(String pkgName, String assetName);
    }

    public AssetFilter getAssetFilter() {
        return assetFilter;
    }

    public void setAssetFilter(AssetFilter assetFilter) {
        this.assetFilter = assetFilter;
    }

    public void add(Resource resource, ResourceType type) {
        ResourceConfiguration resourceConfiguration = resource instanceof BaseResource ? ((BaseResource) resource).getConfiguration() : null;
        add(resource, type, resourceConfiguration)  ;
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

    public Collection<KnowledgePackage> getKnowledgePackages() {
        if ( hasErrors() ) {
            return new ArrayList<KnowledgePackage>( 0 );
        }

        InternalKnowledgePackage[] pkgs = getPackages();
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>( pkgs.length );

        for ( InternalKnowledgePackage pkg : pkgs ) {
            list.add( pkg );
        }

        return list;
    }

    public KnowledgeBase newKnowledgeBase() {
        KnowledgeBuilderErrors errors = getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                logger.error(error.toString());
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(getKnowledgePackages());
        return kbase;
    }

    public TypeDeclaration getTypeDeclaration(Class<?> cls) {
        return typeBuilder.getTypeDeclaration(cls);
    }

    public void normalizeTypeDeclarationAnnotations(PackageDescr packageDescr) {
        TypeResolver typeResolver = pkgRegistryMap.get(packageDescr.getName()).getTypeResolver();
        boolean isStrict = configuration.getLanguageLevel().useJavaAnnotations();
        for (TypeDeclarationDescr typeDeclarationDescr : packageDescr.getTypeDeclarations()) {
            normalizeAnnotations(typeDeclarationDescr, typeResolver, isStrict);
            for (TypeFieldDescr typeFieldDescr : typeDeclarationDescr.getFields().values()) {
                normalizeAnnotations(typeFieldDescr, typeResolver, isStrict);
            }
        }
    }

    public void normalizeRuleAnnotations(PackageDescr packageDescr) {
        TypeResolver typeResolver = pkgRegistryMap.get(packageDescr.getName()).getTypeResolver();
        boolean isStrict = configuration.getLanguageLevel().useJavaAnnotations();
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            normalizeAnnotations(ruleDescr, typeResolver, isStrict);
            for (BaseDescr baseDescr : ruleDescr.getLhs().getDescrs()) {
                if (baseDescr instanceof AnnotatedBaseDescr) {
                    normalizeAnnotations((AnnotatedBaseDescr)baseDescr, typeResolver, isStrict);
                }
            }
        }
    }

    private void normalizeAnnotations(AnnotatedBaseDescr annotationsContainer, TypeResolver typeResolver, boolean isStrict) {
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
        } catch (ClassNotFoundException e) {
            String className = normalizeAnnotationNonStrictName(annotationDescr.getName());
            try {
                annotationClass = typeResolver.resolveType(className, TypeResolver.ONLY_ANNOTATION_CLASS_FILTER);
            } catch (ClassNotFoundException e1) {
                // non-strict annotation, ignore error
            }
        }
        if (annotationClass != null) {
            annotationDescr.setFullyQualifiedName(annotationClass.getCanonicalName());

            for ( String key : annotationDescr.getValueMap().keySet() ) {
                try {
                    Method m = annotationClass.getMethod( key );
                    Object val = annotationDescr.getValue( key );
                    if ( val instanceof Object[] && ! m.getReturnType().isArray() ) {
                        addBuilderResult( new AnnotationDeclarationError( annotationDescr,
                                                                          "Wrong cardinality on property " + key ) );
                        return annotationDescr;
                    }
                    if ( m.getReturnType().isArray() && ! (val instanceof Object[]) ) {
                        val = new Object[] { val };
                        annotationDescr.setKeyValue( key, val );
                    }

                    if ( m.getReturnType().isArray() ) {
                        int n = Array.getLength( val );
                        Object ar = java.lang.reflect.Array.newInstance( m.getReturnType().getComponentType(), n );
                        for ( int j = 0; j < n; j++ ) {
                            if ( Class.class.equals( m.getReturnType().getComponentType() ) ) {
                                String className = Array.get( val, j ).toString().replace( ".class", "" );
                                Array.set( val, j, typeResolver.resolveType( className ).getName() + ".class" );
                            } else if ( m.getReturnType().getComponentType().isAnnotation() ) {
                                Array.set( val, j, normalizeAnnotation( typeResolver,
                                                                        (AnnotationDescr) Array.get( val, j ) ) );
                            }
                        }
                    } else {
                        if ( Class.class.equals( m.getReturnType() ) ) {
                            String className = annotationDescr.getValueAsString( key ).toString().replace( ".class", "" );
                            annotationDescr.setKeyValue( key, typeResolver.resolveType( className ).getName() + ".class" );
                        } else if ( m.getReturnType().isAnnotation() ) {
                            annotationDescr.setKeyValue( key,
                                                         normalizeAnnotation( typeResolver,
                                                                              (AnnotationDescr) annotationDescr.getValue( key ) ) );
                        }
                    }
                } catch ( NoSuchMethodException e ) {
                    addBuilderResult( new AnnotationDeclarationError( annotationDescr,
                                                                      "Unknown annotation property " + key ) );
                } catch ( ClassNotFoundException e ) {
                    addBuilderResult( new AnnotationDeclarationError( annotationDescr,
                                                                      "Unknown class " + annotationDescr.getValue( key ) +                                                                      " used in property " + key +
                                                                      " of annotation " + annotationDescr.getName() ) );
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
        } catch (ClassNotFoundException e) {
            addBuilderResult(new AnnotationDeclarationError(annotationDescr,
                                                            "Unknown annotation: " + annotationDescr.getName()));
        }
    }
}