/*
* Copyright 2005 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.drools.compiler.compiler;

import static org.drools.core.util.BitMaskUtil.isSet;
import static org.drools.core.util.ClassUtils.convertClassToResourcePath;

import java.beans.IntrospectionException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.compiler.PackageBuilder.AssetFilter.Action;
import org.drools.compiler.compiler.xml.XmlPackageReader;
import org.drools.compiler.lang.ExpanderException;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.AccumulateImportDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.EntryPointDeclarationDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.EnumLiteralDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.FunctionImportDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.QualifiedName;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.compiler.lang.descr.WindowDeclarationDescr;
import org.drools.compiler.lang.dsl.DSLMappingFile;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleBuilder;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.dialect.DialectError;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.compiler.runtime.pipeline.impl.DroolsJaxbHelperProviderImpl;
import org.drools.core.PackageIntegrationException;
import org.drools.core.RuleBase;
import org.drools.core.RuntimeDroolsException;
import org.drools.core.base.ClassFieldAccessor;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.TypeResolver;
import org.drools.core.base.evaluators.TimeIntervalParser;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.definitions.impl.KnowledgePackageImp;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.factmodel.ClassBuilder;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.EnumClassDefinition;
import org.drools.core.factmodel.EnumLiteralDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.GeneratedFact;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.io.impl.DescrResource;
import org.drools.core.io.impl.ReaderResource;
import org.drools.core.io.internal.InternalResource;
import org.drools.core.reteoo.ReteooRuleBase;
import org.drools.core.rule.Function;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Package;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.Rule;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.WindowDeclaration;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.type.DateFormats;
import org.drools.core.type.DateFormatsImpl;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.DeepCloneable;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.HierarchySorter;
import org.drools.core.util.StringUtils;
import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.core.xml.XmlChangeSetReader;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.ChangeSet;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.definition.KnowledgePackage;
import org.xml.sax.SAXException;

/**
 * This is the main compiler class for parsing and compiling rules and
 * assembling or merging them into a binary Package instance. This can be done
 * by merging into existing binary packages, or totally from source.
 *
 * If you are using the Java dialect the JavaDialectConfiguration will attempt
 * to validate that the specified compiler is in the classpath, using
 * ClassLoader.loasClass(String). If you intented to just Janino sa the compiler
 * you must either overload the compiler property before instantiating this
 * class or the PackageBuilder, or make sure Eclipse is in the classpath, as
 * Eclipse is the default.
 *
 * Normally, a complete package is built using one of the applicable
 * addPackageFromXXX methods. It is however possible to construct a package
 * incrementally by adding individual component parts. When a package is built
 * incrementally package level attributes are cached and applied to Rules
 * subsequently added. Caution should be exercised when using the same
 * PackageBuilder to construct packages from multiple sources as the cached
 * package level attributes will still apply even if the resource added to
 * PackageBuilder does not explicitly include package level attributes.
 */
public class PackageBuilder
        implements
        DeepCloneable<PackageBuilder> {

    private final Map<String, PackageRegistry>             pkgRegistryMap;

    private List<KnowledgeBuilderResult>                   results;

    private final PackageBuilderConfiguration              configuration;

    public static final RuleBuilder                        ruleBuilder        = new RuleBuilder();

    /**
     * Optional RuleBase for incremental live building
     */
    private ReteooRuleBase                                 ruleBase;

    /**
     * default dialect
     */
    private final String                                   defaultDialect;

    private ClassLoader                                    rootClassLoader;

    private final Map<String, Class<?>>                    globals;

    private Resource                                       resource;

    private List<DSLTokenizedMappingFile>                  dslFiles;

    private TimeIntervalParser                             timeParser;

    protected DateFormats                                  dateFormats;

    private final ProcessBuilder                           processBuilder;

    private IllegalArgumentException                       processBuilderCreationFailure;

    private PMMLCompiler                                   pmmlCompiler;

    private final Map<String, TypeDeclaration>             builtinTypes;

    private Map<String, TypeDeclaration>                   cacheTypes;

    //This list of package level attributes is initialised with the PackageDescr's attributes added to the builder.
    //The package level attributes are inherited by individual rules not containing explicit overriding parameters.
    //The map is keyed on the PackageDescr's namespace and contains a map of AttributeDescr's keyed on the
    //AttributeDescr's name.
    private final Map<String, Map<String, AttributeDescr>> packageAttributes  = new HashMap<String, Map<String, AttributeDescr>>();

    //PackageDescrs' list of ImportDescrs are kept identical as subsequent PackageDescrs are added.
    private final Map<String, List<PackageDescr>>          packages           = new HashMap<String, List<PackageDescr>>();

    private final Set<String>                              generatedTypes     = new HashSet<String>();

    private final Stack<List<Resource>>                    buildResources     = new Stack<List<Resource>>();

    private int                                            currentRulePackage = 0;

    private AssetFilter                                    assetFilter        = null;

    /**
     * Use this when package is starting from scratch.
     */
    public PackageBuilder() {
        this((RuleBase) null,
                null);
    }

    /**
     * This will allow you to merge rules into this pre existing package.
     */

    public PackageBuilder(final Package pkg) {
        this(pkg,
                null);
    }

    public PackageBuilder(final RuleBase ruleBase) {
        this(ruleBase,
                null);
    }

    /**
     * Pass a specific configuration for the PackageBuilder
     *
     * PackageBuilderConfiguration is not thread safe and it also contains
     * state. Once it is created and used in one or more PackageBuilders it
     * should be considered immutable. Do not modify its properties while it is
     * being used by a PackageBuilder.
     *
     * @param configuration
     */
    public PackageBuilder(final PackageBuilderConfiguration configuration) {
        this((RuleBase) null,
                configuration);
    }

    public PackageBuilder(Package pkg,
            PackageBuilderConfiguration configuration) {
        if (configuration == null) {
            this.configuration = new PackageBuilderConfiguration();
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

        PackageRegistry pkgRegistry = new PackageRegistry(this,
                pkg);
        pkgRegistry.setDialect(this.defaultDialect);
        this.pkgRegistryMap.put(pkg.getName(),
                pkgRegistry);

        // add imports to pkg registry
        for (final ImportDeclaration implDecl : pkg.getImports().values()) {
            pkgRegistry.addImport(new ImportDescr(implDecl.getTarget()));
        }

        globals = new HashMap<String, Class<?>>();

        processBuilder = createProcessBuilder();

        builtinTypes = new HashMap<String, TypeDeclaration>();
        initBuiltinTypeDeclarations();
    }

    public PackageBuilder(RuleBase ruleBase,
            PackageBuilderConfiguration configuration) {
        if (configuration == null) {
            this.configuration = new PackageBuilderConfiguration();
        } else {
            this.configuration = configuration;
        }

        if (ruleBase != null) {
            this.rootClassLoader = ((InternalRuleBase) ruleBase).getRootClassLoader();
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

        this.ruleBase = (ReteooRuleBase) ruleBase;

        globals = new HashMap<String, Class<?>>();

        processBuilder = createProcessBuilder();

        builtinTypes = new HashMap<String, TypeDeclaration>();
        initBuiltinTypeDeclarations();
    }

    public PackageBuilder deepClone() {
        PackageBuilder clone = new PackageBuilder(configuration);
        clone.rootClassLoader = rootClassLoader;

        for (Map.Entry<String, PackageRegistry> entry : pkgRegistryMap.entrySet()) {
            clone.pkgRegistryMap.put(entry.getKey(), entry.getValue().clonePackage(rootClassLoader));
        }
        clone.results.addAll(results);
        clone.ruleBase = ClassUtils.deepClone(ruleBase, rootClassLoader);
        clone.globals.putAll(globals);
        if (dslFiles != null) {
            clone.dslFiles = new ArrayList<DSLTokenizedMappingFile>();
            clone.dslFiles.addAll(dslFiles);
        }
        if (cacheTypes != null) {
            clone.cacheTypes = new HashMap<String, TypeDeclaration>();
            clone.cacheTypes.putAll(cacheTypes);
        }
        clone.packageAttributes.putAll(packageAttributes);
        for (Map.Entry<String, List<PackageDescr>> entry : packages.entrySet()) {
            clone.packages.put(entry.getKey(), new ArrayList<PackageDescr>(entry.getValue()));
        }
        clone.packages.putAll(packages);

        clone.currentRulePackage = currentRulePackage;
        return clone;
    }

    private void initBuiltinTypeDeclarations() {
        TypeDeclaration colType = new TypeDeclaration("Collection");
        colType.setTypesafe(false);
        colType.setTypeClass(Collection.class);
        builtinTypes.put("java.util.Collection",
                colType);

        TypeDeclaration mapType = new TypeDeclaration("Map");
        mapType.setTypesafe(false);
        mapType.setTypeClass(Map.class);
        builtinTypes.put("java.util.Map",
                mapType);

        TypeDeclaration activationType = new TypeDeclaration("Match");
        activationType.setTypesafe(false);
        activationType.setTypeClass(Match.class);
        builtinTypes.put(Match.class.getCanonicalName(),
                activationType);

        TypeDeclaration thingType = new TypeDeclaration(Thing.class.getSimpleName());
        thingType.setKind(TypeDeclaration.Kind.TRAIT);
        thingType.setTypeClass(Thing.class);
        builtinTypes.put(Thing.class.getCanonicalName(),
                thingType);
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

    /**
     * Load a rule package from DRL source.
     *
     * @param reader
     * @throws DroolsParserException
     * @throws IOException
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
            this.results.add(new ParserError(sourceResource, "Parser returned a null Package", 0, 0));
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
        String string = DecisionTableFactory.loadFromInputStream(resource.getInputStream(), dtableConfiguration);

        DrlParser parser = new DrlParser(this.configuration.getLanguageLevel());
        PackageDescr pkg = parser.parse(resource, new StringReader(string));
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
        }
        return parser.hasErrors() ? null : pkg;
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
            this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
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
                this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
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
                    this.results.add(error);
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
            throw new RuntimeException("Unable to instantiate a process builder", processBuilderCreationFailure);
        }

        if (ResourceType.DRF.equals(resource.getResourceType())) {
            this.results.add(new DeprecatedResourceTypeWarning(resource, "RF"));
        }

        this.resource = resource;

        try {
            this.results.addAll(processBuilder.addProcessFromXml(resource));
            processBuilder.getErrors().clear();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            this.results.add(new ProcessLoadError(resource, "Unable to load process.", e));
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
        ResourceTypeBuilder builder = ResourceTypeBuilderRegistry.getInstance().getResourceTypeBuilder(type);
        if (builder != null) {
            builder.setPackageBuilder(this);
            builder.addKnowledgeResource(resource,
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
                getPackageRegistry());

        if (!compiler.getResults().isEmpty()) {
            this.results.addAll(compiler.getResults());
            return null;
        }

        DrlParser parser = new DrlParser(configuration.getLanguageLevel());
        PackageDescr pkg = parser.parse(resource, new StringReader(theory));
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
            return pkg;
        } else {
            return parser.hasErrors() ? null : pkg;
        }
    }

    void addPackageFromXSD(Resource resource,
                           JaxbConfigurationImpl configuration) throws IOException {
        String[] classes = DroolsJaxbHelperProviderImpl.addXsdModel(resource,
                this,
                configuration.getXjcOpts(),
                configuration.getSystemId());
        for (String cls : classes) {
            configuration.getClasses().add(cls);
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
                overrideReSource(((KnowledgePackageImp) kpkg).pkg, resource);
                addPackage(((KnowledgePackageImp) kpkg).pkg);
            }
        } else if (object instanceof KnowledgePackageImp) {
            // KnowledgeBuilder API
            KnowledgePackageImp kpkg = (KnowledgePackageImp) object;
            overrideReSource(kpkg.pkg, resource);
            addPackage(kpkg.pkg);
        } else if (object instanceof Package) {
            // Old Drools 4 API
            Package pkg = (Package) object;
            overrideReSource(pkg, resource);
            addPackage(pkg);
        } else if (object instanceof Package[]) {
            // Old Drools 4 API
            Package[] pkgs = (Package[]) object;
            for (Package pkg : pkgs) {
                overrideReSource(pkg, resource);
                addPackage(pkg);
            }
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

    private void overrideReSource(Package pkg,
            Resource res) {
        for (Rule r : pkg.getRules()) {
            if (isSwappable(r.getResource(), res)) {
                r.setResource(res);
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
        for (Process p : pkg.getRuleFlows().values()) {
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

        // only try to compile if there are no parse errors
        if (!hasErrors()) {
            compileRules(packageDescr, pkgRegistry);
        }

        compileAll();
        try {
            reloadAll();
        } catch (Exception e) {
            this.results.add(new DialectError(null, "Unable to wire compiled classes, probably related to compilation failures:" + e.getMessage()));
        }
        updateResults();

        // iterate and compile
        if (!hasErrors() && this.ruleBase != null) {
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                if( filterAccepts( ruleDescr.getNamespace(), ruleDescr.getName() ) ) {
                    pkgRegistry = this.pkgRegistryMap.get(ruleDescr.getNamespace());
                    this.ruleBase.addRule(pkgRegistry.getPackage(), pkgRegistry.getPackage().getRule(ruleDescr.getName()));
                }
            }
        }
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
        validateUniqueRuleNames(packageDescr);
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

    private boolean filterAccepts( String namespace, String name ) {
        return assetFilter == null || ! Action.DO_NOTHING.equals( assetFilter.accept( namespace, name ) );
    }

    private boolean filterAcceptsRemoval( String namespace, String name ) {
        return assetFilter != null && Action.REMOVE.equals( assetFilter.accept( namespace, name ) );
    }

    private Map<String, RuleBuildContext> preProcessRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        Map<String, RuleBuildContext> ruleCxts = buildRuleBuilderContext(packageDescr.getRules());

        Package pkg = pkgRegistry.getPackage();
        if (this.ruleBase != null) {
            boolean needsRemoval = false;

            // first, check if any rules no longer exist
            for( Rule rule : pkg.getRules() ) {
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
                    this.ruleBase.lock();
                    for( Rule rule : pkg.getRules() ) {
                        if (filterAcceptsRemoval( rule.getPackageName(), rule.getName() ) ) {
                            this.ruleBase.removeRule(pkg, pkg.getRule(rule.getName()));
                            pkg.removeRule(rule);
                        }
                    }
                    for (RuleDescr ruleDescr : packageDescr.getRules()) {
                        if (filterAccepts(ruleDescr.getNamespace(), ruleDescr.getName()) ) {
                            if (pkg.getRule(ruleDescr.getName()) != null) {
                                // XXX: this one notifies listeners
                                this.ruleBase.removeRule(pkg, pkg.getRule(ruleDescr.getName()));
                            }
                        }
                    }
                } finally {
                    this.ruleBase.unlock();
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
        Package pkg = pkgRegistry.getPackage();

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
                    results.add(new RuleBuildError(new Rule(ruleDescr.getName()), ruleDescr, null,
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
        results.add(new RuleBuildError(new Rule(ruleDescr.getName()), ruleDescr, msg,
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
        if (this.configuration == null)
            return true;
        if ((!this.pkgRegistryMap.isEmpty()) && (!this.pkgRegistryMap.containsKey(newName))) {
            return this.configuration.isAllowMultipleNamespaces();
        }
        return true;
    }

    public boolean isEmpty(String string) {
        return (string == null || string.trim().length() == 0);
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

    public synchronized void addPackage(final Package newPkg) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get(newPkg.getName());
        Package pkg = null;
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
                    this.results.add(new DuplicateFunction(entry.getValue(),
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
            throw new RuntimeDroolsException("unable to resolve Type Declaration class '" + lastType.getTypeName() +
                    "'");
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
    private void mergePackage(final Package pkg,
            final Package newPkg) {
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
                        throw new PackageIntegrationException(pkg);
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
            throw new RuntimeDroolsException("Unable to resolve class '" + lastType + "'");
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

        final Rule[] newRules = newPkg.getRules();
        for (final Rule newRule : newRules) {
            pkg.addRule(newRule);
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
        Package pkg = null;
        if (packageRegistry != null) {
            pkg = packageRegistry.getPackage();
        }
        for (final RuleDescr rule : packageDescr.getRules()) {
            validateRule(packageDescr, rule);

            final String name = rule.getName();
            if (names.contains(name)) {
                this.results.add(new ParserError(rule.getResource(),
                        "Duplicate rule name: " + name,
                        rule.getLine(),
                        rule.getColumn(),
                        packageDescr.getNamespace()));
            }
            if (pkg != null) {
                Rule duplicatedRule = pkg.getRule(name);
                if (duplicatedRule != null) {
                    Resource resource = rule.getResource();
                    Resource duplicatedResource = duplicatedRule.getResource();
                    if (resource == null || duplicatedResource == null || duplicatedResource.getSourcePath() == null ||
                            duplicatedResource.getSourcePath().equals(resource.getSourcePath())) {
                        this.results.add(new DuplicateRule(rule,
                                packageDescr,
                                this.configuration));
                    } else {
                        this.results.add(new ParserError(rule.getResource(),
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
                this.results.add(new ParserError(rule.getResource(),
                        error + " in rule " + rule.getName(),
                        rule.getLine(),
                        rule.getColumn(),
                        packageDescr.getNamespace()));
            }
        }
    }

    private PackageRegistry newPackage(final PackageDescr packageDescr) {
        Package pkg;
        if (this.ruleBase == null || (pkg = this.ruleBase.getPackage(packageDescr.getName())) == null) {
            // there is no rulebase or it does not define this package so define it
            pkg = new Package(packageDescr.getName());
            pkg.setClassFieldAccessorCache(new ClassFieldAccessorCache(this.rootClassLoader));

            // if there is a rulebase then add the package.
            if (this.ruleBase != null) {
                // Must lock here, otherwise the assumption about addPackage/getPackage behavior below might be violated
                this.ruleBase.lock();
                try {
                    this.ruleBase.addPackage(pkg);
                    pkg = this.ruleBase.getPackage(packageDescr.getName());
                } finally {
                    this.ruleBase.unlock();
                }
            } else {
                // the RuleBase will also initialise the
                pkg.getDialectRuntimeRegistry().onAdd(this.rootClassLoader);
            }
        }

        PackageRegistry pkgRegistry = new PackageRegistry(this, pkg);

        // add default import for this namespace
        pkgRegistry.addImport(new ImportDescr(packageDescr.getNamespace() + ".*"));

        this.pkgRegistryMap.put(packageDescr.getName(), pkgRegistry);

        return pkgRegistry;
    }

    private void mergePackage(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        for (final ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.addImport(importDescr);
        }

        processAccumulateFunctions(pkgRegistry, packageDescr);

        processEntryPointDeclarations(pkgRegistry, packageDescr);

        // process types in 2 steps to deal with circular and recursive declarations
        processUnresolvedTypes(pkgRegistry, processTypeDeclarations(pkgRegistry, packageDescr, new ArrayList<TypeDefinition>()));

        processOtherDeclarations(pkgRegistry, packageDescr);
    }

    void processOtherDeclarations(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        processAccumulateFunctions( pkgRegistry, packageDescr);
        processWindowDeclarations(pkgRegistry, packageDescr);
        processFunctions(pkgRegistry, packageDescr);
        processGlobals(pkgRegistry, packageDescr);

        // need to reinsert this to ensure that the package is the first/last one in the ordered map
        // this feature is exploited by the knowledgeAgent
        Package current = getPackage();
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
                    this.results.add(new GlobalError(global, " Primitive types are not allowed in globals : " + className));
                    return;
                }
                pkgRegistry.getPackage().addGlobal(identifier,
                        clazz);
                this.globals.put(identifier,
                        clazz);
            } catch (final ClassNotFoundException e) {
                this.results.add(new GlobalError(global, e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    private void processAccumulateFunctions(PackageRegistry pkgRegistry,
            PackageDescr packageDescr) {
        for (final AccumulateImportDescr aid : packageDescr.getAccumulateImports() ) {
            AccumulateFunction af = loadAccumulateFunction( pkgRegistry, 
                    aid.getFunctionName(),
                    aid.getTarget() ); 
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
            throw new RuntimeDroolsException( "Error loading accumulate function for identifier " + identifier + ". Class " + className + " not found",
                                              e );
        } catch ( InstantiationException e ) {
            throw new RuntimeDroolsException( "Error loading accumulate function for identifier " + identifier + ". Instantiation failed for class " + className,
                                              e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeDroolsException( "Error loading accumulate function for identifier " + identifier + ". Illegal access to class " + className,
                                              e );
        }
    }

    private void processFunctions(PackageRegistry pkgRegistry,
            PackageDescr packageDescr) {
        for (FunctionDescr function : packageDescr.getFunctions()) {
            Function existingFunc = pkgRegistry.getPackage().getFunctions().get(function.getName());
            if (existingFunc != null && function.getNamespace().equals(existingFunc.getNamespace())) {
                this.results.add(
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

    void processUnresolvedTypes(PackageRegistry pkgRegistry, List<TypeDefinition> unresolvedTypeDefinitions) {
        if (unresolvedTypeDefinitions != null) {
            for (TypeDefinition typeDef : unresolvedTypeDefinitions) {
                processUnresolvedType(pkgRegistry, typeDef);
            }
        }
    }

    void processUnresolvedType(PackageRegistry pkgRegistry, TypeDefinition unresolvedTypeDefinition) {
        processTypeFields(pkgRegistry, unresolvedTypeDefinition.typeDescr, unresolvedTypeDefinition.type, false);
    }

    public TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String packageName) {
        if (cls.isPrimitive() || cls.isArray()) {
            return null;
        }
        TypeDeclaration typeDeclaration = getCachedTypeDeclaration(cls);
        if (typeDeclaration != null) {
            registerTypeDeclaration(packageName, typeDeclaration);
            return typeDeclaration;
        }
        typeDeclaration = getExistingTypeDeclaration(cls);
        if (typeDeclaration != null) {
            initTypeDeclaration(cls, typeDeclaration);
            return typeDeclaration;
        }

        typeDeclaration = createTypeDeclarationForBean(cls);
        initTypeDeclaration(cls, typeDeclaration);
        registerTypeDeclaration(packageName, typeDeclaration);
        return typeDeclaration;
    }

    private void registerTypeDeclaration(String packageName,
            TypeDeclaration typeDeclaration) {
        if (typeDeclaration.getNature() == TypeDeclaration.Nature.DECLARATION || packageName.equals(typeDeclaration.getTypeClass().getPackage().getName())) {
            PackageRegistry packageRegistry = pkgRegistryMap.get(packageName);
            if (packageRegistry != null) {
                packageRegistry.getPackage().addTypeDeclaration(typeDeclaration);
            } else {
                newPackage(new PackageDescr(packageName, ""));
                pkgRegistryMap.get(packageName).getPackage().addTypeDeclaration(typeDeclaration);
            }
        }
    }

    public TypeDeclaration getTypeDeclaration(Class<?> cls) {
        if (cls.isPrimitive() || cls.isArray())
            return null;

        // If this class has already been accessed, it'll be in the cache
        TypeDeclaration tdecl = getCachedTypeDeclaration(cls);
        return tdecl != null ? tdecl : createTypeDeclaration(cls);
    }

    private TypeDeclaration createTypeDeclaration(Class<?> cls) {
        TypeDeclaration typeDeclaration = getExistingTypeDeclaration(cls);

        if (typeDeclaration == null) {
            typeDeclaration = createTypeDeclarationForBean(cls);
        }

        initTypeDeclaration(cls, typeDeclaration);
        return typeDeclaration;
    }

    private TypeDeclaration getCachedTypeDeclaration(Class<?> cls) {
        if (this.cacheTypes == null) {
            this.cacheTypes = new HashMap<String, TypeDeclaration>();
            return null;
        } else {
            return cacheTypes.get(cls.getName());
        }
    }

    private TypeDeclaration getExistingTypeDeclaration(Class<?> cls) {
        // Check if we are in the built-ins
        TypeDeclaration typeDeclaration = this.builtinTypes.get((cls.getName()));
        if (typeDeclaration == null) {
            // No built-in
            // Check if there is a user specified typedeclr
            PackageRegistry pkgReg = this.pkgRegistryMap.get(ClassUtils.getPackage(cls));
            if (pkgReg != null) {
                String className = cls.getName();
                String typeName = className.substring(className.lastIndexOf(".") + 1);
                typeDeclaration = pkgReg.getPackage().getTypeDeclaration(typeName);
            }
        }
        return typeDeclaration;
    }

    private void initTypeDeclaration(Class<?> cls,
            TypeDeclaration typeDeclaration) {
        ClassDefinition clsDef = typeDeclaration.getTypeClassDef();
        if (clsDef == null) {
            clsDef = new ClassDefinition();
            typeDeclaration.setTypeClassDef(clsDef);
        }

        if (typeDeclaration.isPropertyReactive()) {
            processModifiedProps(cls, clsDef);
        }
        processFieldsPosition(cls, clsDef);

        // build up a set of all the super classes and interfaces
        Set<TypeDeclaration> tdecls = new LinkedHashSet<TypeDeclaration>();

        tdecls.add(typeDeclaration);
        buildTypeDeclarations(cls,
                tdecls);

        // Iterate and for each typedeclr assign it's value if it's not already set
        // We start from the rear as those are the furthest away classes and interfaces
        TypeDeclaration[] tarray = tdecls.toArray(new TypeDeclaration[tdecls.size()]);
        for (int i = tarray.length - 1; i >= 0; i--) {
            TypeDeclaration currentTDecl = tarray[i];
            if (!isSet(typeDeclaration.getSetMask(),
                    TypeDeclaration.ROLE_BIT) && isSet(currentTDecl.getSetMask(),
                    TypeDeclaration.ROLE_BIT)) {
                typeDeclaration.setRole(currentTDecl.getRole());
            }
            if (!isSet(typeDeclaration.getSetMask(),
                    TypeDeclaration.FORMAT_BIT) && isSet(currentTDecl.getSetMask(),
                    TypeDeclaration.FORMAT_BIT)) {
                typeDeclaration.setFormat(currentTDecl.getFormat());
            }
            if (!isSet(typeDeclaration.getSetMask(),
                    TypeDeclaration.TYPESAFE_BIT) && isSet(currentTDecl.getSetMask(),
                    TypeDeclaration.TYPESAFE_BIT)) {
                typeDeclaration.setTypesafe(currentTDecl.isTypesafe());
            }
        }

        this.cacheTypes.put(cls.getName(),
                typeDeclaration);
    }

    private TypeDeclaration createTypeDeclarationForBean(Class<?> cls) {
        TypeDeclaration typeDeclaration = new TypeDeclaration(cls);

        PropertySpecificOption propertySpecificOption = configuration.getOption(PropertySpecificOption.class);
        boolean propertyReactive = propertySpecificOption.isPropSpecific(cls.isAnnotationPresent(PropertyReactive.class),
                cls.isAnnotationPresent(ClassReactive.class));

        setPropertyReactive(null, typeDeclaration, propertyReactive);

        Role role = cls.getAnnotation(Role.class);
        if (role != null && role.value() == Role.Type.EVENT) {
            typeDeclaration.setRole(TypeDeclaration.Role.EVENT);
        }

        return typeDeclaration;
    }

    private void processModifiedProps(Class<?> cls,
            ClassDefinition clsDef) {
        for (Method method : cls.getDeclaredMethods()) {
            Modifies modifies = method.getAnnotation(Modifies.class);
            if (modifies != null) {
                String[] props = modifies.value();
                List<String> properties = new ArrayList<String>(props.length);
                for (String prop : props) {
                    properties.add(prop.trim());
                }
                clsDef.addModifiedPropsByMethod(method,
                        properties);
            }
        }
    }

    private void processFieldsPosition(Class<?> cls,
            ClassDefinition clsDef) {
        // it's a new type declaration, so generate the @Position for it
        Collection<Field> fields = new LinkedList<Field>();
        Class<?> tempKlass = cls;
        while (tempKlass != null && tempKlass != Object.class) {
            Collections.addAll(fields, tempKlass.getDeclaredFields());
            tempKlass = tempKlass.getSuperclass();
        }

        List<FieldDefinition> orderedFields = new ArrayList<FieldDefinition>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            // as these could be set in any order, initialise first, to allow setting later.
            orderedFields.add(null);
        }

        for (Field fld : fields) {
            Position pos = fld.getAnnotation(Position.class);
            if (pos != null) {
                FieldDefinition fldDef = clsDef.getField(fld.getName());
                if (fldDef == null) {
                    fldDef = new FieldDefinition(fld.getName(), fld.getType().getName());
                }
                fldDef.setIndex(pos.value());
                orderedFields.set(pos.value(), fldDef);
            }
        }
        for (FieldDefinition fld : orderedFields) {
            if (fld != null) {
                // it's null if there is no @Position
                clsDef.addField(fld);
            }
        }
    }

    public void buildTypeDeclarations(Class<?> cls,
            Set<TypeDeclaration> tdecls) {
        // Process current interfaces
        Class<?>[] intfs = cls.getInterfaces();
        for (Class<?> intf : intfs) {
            buildTypeDeclarationInterfaces(intf,
                    tdecls);
        }

        // Process super classes and their interfaces
        cls = cls.getSuperclass();
        while (cls != null && cls != Object.class) {
            if (!buildTypeDeclarationInterfaces(cls,
                    tdecls)) {
                break;
            }
            cls = cls.getSuperclass();
        }

    }

    public boolean buildTypeDeclarationInterfaces(Class cls,
            Set<TypeDeclaration> tdecls) {
        PackageRegistry pkgReg;

        TypeDeclaration tdecl = this.builtinTypes.get((cls.getName()));
        if (tdecl == null) {
            pkgReg = this.pkgRegistryMap.get(ClassUtils.getPackage(cls));
            if (pkgReg != null) {
                tdecl = pkgReg.getPackage().getTypeDeclaration(cls.getSimpleName());
            }
        }
        if (tdecl != null) {
            if (!tdecls.add(tdecl)) {
                return false; // the interface already exists, return to stop recursion
            }
        }

        Class<?>[] intfs = cls.getInterfaces();
        for (Class<?> intf : intfs) {
            pkgReg = this.pkgRegistryMap.get(ClassUtils.getPackage(intf));
            if (pkgReg != null) {
                tdecl = pkgReg.getPackage().getTypeDeclaration(intf.getSimpleName());
            }
            if (tdecl != null) {
                tdecls.add(tdecl);
            }
        }

        for (Class<?> intf : intfs) {
            if (!buildTypeDeclarationInterfaces(intf,
                    tdecls)) {
                return false;
            }
        }

        return true;

    }

    /**
     * Tries to determine the namespace (package) of a simple type chosen to be
     * the superclass of a declared bean. Looks among imports, local
     * declarations and previous declarations. Means that a class can't extend
     * another class declared in package that has not been loaded yet.
     *
     * @param sup
     *            the simple name of the superclass
     * @param packageDescr
     *            the descriptor of the package the base class is declared in
     * @param pkgRegistry
     *            the current package registry
     * @return the fully qualified name of the superclass
     */
    private String resolveType(String sup,
            PackageDescr packageDescr,
            PackageRegistry pkgRegistry) {

        //look among imports
        for (ImportDescr id : packageDescr.getImports()) {
            if (id.getTarget().endsWith("." + sup)) {
                //logger.info("Replace supertype " + sup + " with full name " + id.getTarget());
                return id.getTarget();

            }
        }

        //look among local declarations
        if (pkgRegistry != null) {
            for (String declaredName : pkgRegistry.getPackage().getTypeDeclarations().keySet()) {
                if (declaredName.equals(sup))
                    sup = pkgRegistry.getPackage().getTypeDeclaration(declaredName).getTypeClass().getName();
            }
        }

        if ((sup != null) && (!sup.contains(".")) && (packageDescr.getNamespace() != null && !packageDescr.getNamespace().isEmpty())) {
            for (AbstractClassTypeDeclarationDescr td : packageDescr.getClassAndEnumDeclarationDescrs()) {
                if ( sup.equals( td.getTypeName() ) ) {
                    if ( td.getType().getFullName().contains( "." ) ) {
                        sup = td.getType().getFullName();
                    } else {
                        sup = packageDescr.getNamespace() + "." + sup;
                    }
                }
            }

        }

        return sup;
    }

    /**
     * Resolves and sets the superclass (name and package) for a given type
     * declaration descriptor The declared supertype, if any, may be a simple
     * name or a fully qualified one. In the former case, the simple name could
     * be the local name of some f.q.n. which has to be resolved
     *
     * @param typeDescr
     *            the descriptor of the declared superclass whose superclass
     *            will be identified
     * @param packageDescr
     *            the descriptor of the package the class is declared in
     */
    private void fillSuperType(TypeDeclarationDescr typeDescr,
            PackageDescr packageDescr) {

        for (QualifiedName qname : typeDescr.getSuperTypes()) {
            String declaredSuperType = qname.getFullName();

            if (declaredSuperType != null) {
                int separator = declaredSuperType.lastIndexOf(".");
                boolean qualified = separator > 0;
                // check if a simple name corresponds to a f.q.n.
                if (!qualified) {
                    declaredSuperType =
                            resolveType(declaredSuperType,
                                    packageDescr,
                                    this.pkgRegistryMap.get(typeDescr.getNamespace()));

                    declaredSuperType = typeName2ClassName(declaredSuperType);

                    // sets supertype name and supertype package
                    separator = declaredSuperType.lastIndexOf(".");
                    if (separator < 0) {
                        this.results.add(new TypeDeclarationError(typeDescr,
                                "Cannot resolve supertype '" + declaredSuperType + "'"));
                        qname.setName(null);
                        qname.setNamespace(null);
                    } else {
                        qname.setName(declaredSuperType.substring(separator + 1));
                        qname.setNamespace(declaredSuperType.substring(0,
                                separator));
                    }
                }
            }
        }
    }

    private String typeName2ClassName(String type) {
        Class<?> cls = getClassForType(type);
        return cls != null ? cls.getName() : type;
    }

    private Class<?> getClassForType(String type) {
        Class<?> cls = null;
        String superType = type;
        while (true) {
            try {
                cls = Class.forName(superType, true, this.rootClassLoader);
                break;
            } catch (ClassNotFoundException e) {
            }
            int separator = superType.lastIndexOf('.');
            if (separator < 0) {
                break;
            }
            superType = superType.substring(0, separator) + "$" + superType.substring(separator + 1);
        }
        return cls;
    }

    private void fillFieldTypes(AbstractClassTypeDeclarationDescr typeDescr,
            PackageDescr packageDescr) {

        for (TypeFieldDescr field : typeDescr.getFields().values()) {
            String declaredType = field.getPattern().getObjectType();

            if (declaredType != null) {
                int separator = declaredType.lastIndexOf(".");
                boolean qualified = separator > 0;
                // check if a simple name corresponds to a f.q.n.
                if (!qualified) {
                    declaredType =
                            resolveType(declaredType,
                                    packageDescr,
                                    this.pkgRegistryMap.get(typeDescr.getNamespace()));

                    field.getPattern().setObjectType(declaredType);
                }
            }
        }
    }

    /**
     * In order to build a declared class, the fields inherited from its
     * superclass(es) are added to its declaration. Inherited descriptors are
     * marked as such to distinguish them from native ones. Various scenarioes
     * are possible. (i) The superclass has been declared in the DRL as well :
     * the fields are cloned as inherited (ii) The superclass is imported
     * (external), but some of its fields have been tagged with metadata (iii)
     * The superclass is imported.
     *
     * The search for field descriptors is carried out in the order. (i) and
     * (ii+iii) are mutually exclusive. The search is as such: (i) The
     * superclass' declared fields are used to build the base class additional
     * fields (iii) The superclass is inspected to discover its (public) fields,
     * from which descriptors are generated (ii) Both (i) and (iii) are applied,
     * but the declared fields override the inspected ones
     *
     * @param typeDescr
     *            The base class descriptor, to be completed with the inherited
     *            fields descriptors
     * @return true if all went well
     */
    private boolean mergeInheritedFields(TypeDeclarationDescr typeDescr) {

        if (typeDescr.getSuperTypes().isEmpty())
            return false;
        boolean merge = false;

        for (int j = typeDescr.getSuperTypes().size() - 1; j >= 0; j--) {
            QualifiedName qname = typeDescr.getSuperTypes().get(j);
            String simpleSuperTypeName = qname.getName();
            String superTypePackageName = qname.getNamespace();
            String fullSuper = qname.getFullName();

            merge = mergeInheritedFields(simpleSuperTypeName,
                    superTypePackageName,
                    fullSuper,
                    typeDescr) || merge;
        }

        return merge;
    }

    private boolean mergeInheritedFields(String simpleSuperTypeName,
            String superTypePackageName,
            String fullSuper,
            TypeDeclarationDescr typeDescr) {

        Map<String, TypeFieldDescr> fieldMap = new LinkedHashMap<String, TypeFieldDescr>();

        PackageRegistry registry = this.pkgRegistryMap.get(superTypePackageName);
        Package pack;
        if (registry != null) {
            pack = registry.getPackage();
        } else {
            // If there is no regisrty the type isn't a DRL-declared type, which is forbidden.
            // Avoid NPE JIRA-3041 when trying to access the registry. Avoid subsequent problems.
            this.results.add(new TypeDeclarationError(typeDescr, "Cannot extend supertype '" + fullSuper + "' (not a declared type)"));
            typeDescr.setType(null, null);
            return false;
        }

        // if a class is declared in DRL, its package can't be null? The default package is replaced by "defaultpkg"
        boolean isSuperClassTagged = false;
        boolean isSuperClassDeclared = true; //in the same package, or in a previous one

        if (pack != null) {

            // look for the supertype declaration in available packages
            TypeDeclaration superTypeDeclaration = pack.getTypeDeclaration(simpleSuperTypeName);

            if (superTypeDeclaration != null) {
                ClassDefinition classDef = superTypeDeclaration.getTypeClassDef();
                // inherit fields
                for (FactField fld : classDef.getFields()) {
                    TypeFieldDescr inheritedFlDescr = buildInheritedFieldDescrFromDefinition(fld, typeDescr);
                    fieldMap.put(inheritedFlDescr.getFieldName(),
                            inheritedFlDescr);
                }

                // new classes are already distinguished from tagged external classes
                isSuperClassTagged = !superTypeDeclaration.isNovel();
            } else {
                isSuperClassDeclared = false;
            }

        } else {
            isSuperClassDeclared = false;
        }

        // look for the class externally
        if (!isSuperClassDeclared || isSuperClassTagged) {
            try {
                Class superKlass = registry.getTypeResolver().resolveType(fullSuper);
                ClassFieldInspector inspector = new ClassFieldInspector(superKlass);
                for (String name : inspector.getGetterMethods().keySet()) {
                    // classFieldAccessor requires both getter and setter
                    if (inspector.getSetterMethods().containsKey(name)) {
                        if (!inspector.isNonGetter(name) && !"class".equals(name)) {
                            TypeFieldDescr inheritedFlDescr = new TypeFieldDescr(
                                    name,
                                    new PatternDescr(
                                            inspector.getFieldTypes().get(name).getName()));
                            inheritedFlDescr.setInherited(!Modifier.isAbstract(inspector.getGetterMethods().get(name).getModifiers()));

                            if (!fieldMap.containsKey(inheritedFlDescr.getFieldName()))
                                fieldMap.put(inheritedFlDescr.getFieldName(),
                                        inheritedFlDescr);
                        }
                    }
                }

            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeDroolsException("Unable to resolve Type Declaration superclass '" + fullSuper + "'");
            } catch (IOException e) {

            }
        }

        // finally, locally declared fields are merged. The map swap ensures that super-fields are added in order, before the subclass' ones
        // notice that it is not possible to override a field changing its type
        for (String fieldName : typeDescr.getFields().keySet()) {
            if (fieldMap.containsKey(fieldName)) {
                String type1 = fieldMap.get(fieldName).getPattern().getObjectType();
                String type2 = typeDescr.getFields().get(fieldName).getPattern().getObjectType();
                if (type2.lastIndexOf(".") < 0) {
                    try {
                        TypeResolver typeResolver = pkgRegistryMap.get(pack.getName()).getTypeResolver();
                        type1 = typeResolver.resolveType(type1).getName();
                        type2 = typeResolver.resolveType(type2).getName();
                        // now that we are at it... this will be needed later anyway
                        fieldMap.get(fieldName).getPattern().setObjectType(type1);
                        typeDescr.getFields().get(fieldName).getPattern().setObjectType(type2);
                    } catch (ClassNotFoundException cnfe) {
                        // will fail later
                    }
                }

                if (!type1.equals(type2)) {
                    this.results.add(new TypeDeclarationError(typeDescr,
                            "Cannot redeclare field '" + fieldName + " from " + type1 + " to " + type2));
                    typeDescr.setType(null,
                            null);
                    return false;
                } else {
                    String initVal = fieldMap.get(fieldName).getInitExpr();
                    if (typeDescr.getFields().get(fieldName).getInitExpr() == null) {
                        typeDescr.getFields().get(fieldName).setInitExpr(initVal);
                    }
                    typeDescr.getFields().get(fieldName).setInherited(fieldMap.get(fieldName).isInherited());

                    for (String key : fieldMap.get(fieldName).getAnnotationNames()) {
                        if (typeDescr.getFields().get(fieldName).getAnnotation(key) == null) {
                            typeDescr.getFields().get(fieldName).addAnnotation(fieldMap.get(fieldName).getAnnotation(key));
                        }
                    }

                    if (typeDescr.getFields().get(fieldName).getIndex() < 0) {
                        typeDescr.getFields().get(fieldName).setIndex(fieldMap.get(fieldName).getIndex());
                    }
                }
            }
            fieldMap.put(fieldName,
                    typeDescr.getFields().get(fieldName));
        }

        typeDescr.setFields(fieldMap);

        return true;
    }

    protected TypeFieldDescr buildInheritedFieldDescrFromDefinition(FactField fld, TypeDeclarationDescr typeDescr) {
        PatternDescr fldType = new PatternDescr();
        TypeFieldDescr inheritedFldDescr = new TypeFieldDescr();
        inheritedFldDescr.setFieldName(fld.getName());
        fldType.setObjectType(((FieldDefinition) fld).getFieldAccessor().getExtractToClassName());
        inheritedFldDescr.setPattern(fldType);
        if (fld.isKey()) {
            inheritedFldDescr.getAnnotations().put(TypeDeclaration.ATTR_KEY,
                    new AnnotationDescr(TypeDeclaration.ATTR_KEY));
        }
        inheritedFldDescr.setIndex(((FieldDefinition) fld).getDeclIndex());
        inheritedFldDescr.setInherited(true);

        String initExprOverride = ((FieldDefinition) fld).getInitExpr();
        int overrideCount = 0;
        // only @aliasing local fields may override defaults.
        for (TypeFieldDescr localField : typeDescr.getFields().values()) {
            AnnotationDescr ann = localField.getAnnotation("Alias");
            if (ann != null && fld.getName().equals(ann.getSingleValue().replaceAll("\"", "")) && localField.getInitExpr() != null) {
                overrideCount++;
                initExprOverride = localField.getInitExpr();
            }
        }
        if (overrideCount > 1) {
            // however, only one is allowed
            initExprOverride = null;
        }
        inheritedFldDescr.setInitExpr(initExprOverride);
        return inheritedFldDescr;
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
            Package pkg = pkgRegistry.getPackage();
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
                throw new RuntimeDroolsException(
                        "BUG: builder not found for descriptor class " + wd.getPattern().getClass());
            }

            if (!context.getErrors().isEmpty()) {
                for (DroolsError error : context.getErrors()) {
                    this.results.add(error);
                }
            } else {
                pkgRegistry.getPackage().addWindowDeclaration(window);
            }
        }
    }

    void registerGeneratedType(AbstractClassTypeDeclarationDescr typeDescr) {
        String fullName = typeDescr.getType().getFullName();
        generatedTypes.add(fullName);
    }

    /**
     * @param packageDescr
     */
    List<TypeDefinition> processTypeDeclarations(PackageRegistry pkgRegistry, PackageDescr packageDescr, List<TypeDefinition> unresolvedTypes) {

        Map<String, PackageDescr> foreignPackages = null;

        for (AbstractClassTypeDeclarationDescr typeDescr : packageDescr.getClassAndEnumDeclarationDescrs()) {
            if (filterAccepts(typeDescr.getNamespace(), typeDescr.getTypeName()) ) {

                String qName = typeDescr.getType().getFullName();
                Class<?> typeClass = getClassForType(qName);
                if (typeClass == null) {
                    typeClass = getClassForType(typeDescr.getTypeName());
                }
                if (typeClass == null) {
                    for (ImportDescr id : packageDescr.getImports()) {
                        String imp = id.getTarget();
                        int separator = imp.lastIndexOf('.');
                        String tail = imp.substring(separator + 1);
                        if (tail.equals(typeDescr.getTypeName())) {
                            typeDescr.setNamespace(imp.substring(0, separator));
                            typeClass = getClassForType(typeDescr.getType().getFullName());
                            break;
                        } else if (tail.equals("*")) {
                            typeClass = getClassForType(imp.substring(0, imp.length() - 1) + typeDescr.getType().getName());
                            if (typeClass != null) {
                                typeDescr.setNamespace(imp.substring(0, separator));
                                break;
                            }
                        }
                    }
                }
                String className = typeClass != null ? typeClass.getName() : qName;
                int dotPos = className.lastIndexOf('.');
                if (dotPos >= 0) {
                    typeDescr.setNamespace(className.substring(0, dotPos));
                    typeDescr.setTypeName(className.substring(dotPos + 1));
                }

                if (isEmpty(typeDescr.getNamespace()) && typeDescr.getFields().isEmpty()) {
                    // might be referencing a class imported with a package import (.*)
                    PackageRegistry pkgReg = this.pkgRegistryMap.get(packageDescr.getName());
                    if (pkgReg != null) {
                        try {
                            Class<?> clz = pkgReg.getTypeResolver().resolveType(typeDescr.getTypeName());
                            java.lang.Package pkg = clz.getPackage();
                            if (pkg != null) {
                                typeDescr.setNamespace(pkg.getName());
                                int index = typeDescr.getNamespace() != null && !typeDescr.getNamespace().isEmpty() ? typeDescr.getNamespace().length() + 1 : 0;
                                typeDescr.setTypeName(clz.getCanonicalName().substring(index));
                            }
                        } catch (Exception e) {
                            // intentionally eating the exception as we will fallback to default namespace
                        }
                    }
                }

                if (isEmpty(typeDescr.getNamespace())) {
                    typeDescr.setNamespace(packageDescr.getNamespace()); // set the default namespace
                }

                //identify superclass type and namespace
                if (typeDescr instanceof TypeDeclarationDescr) {
                    fillSuperType((TypeDeclarationDescr) typeDescr,
                            packageDescr);
                }

                //identify field types as well
                fillFieldTypes(typeDescr,
                        packageDescr);

                if (!typeDescr.getNamespace().equals(packageDescr.getNamespace())) {
                    // If the type declaration is for a different namespace, process that separately.
                    PackageDescr altDescr = null;

                    if ( foreignPackages == null ) {
                        foreignPackages = new HashMap<String, PackageDescr>(  );
                    }

                    if ( foreignPackages.containsKey( typeDescr.getNamespace() ) ) {
                        altDescr = foreignPackages.get( typeDescr.getNamespace() );
                    } else {
                        altDescr = new PackageDescr(typeDescr.getNamespace());
                        foreignPackages.put( typeDescr.getNamespace(), altDescr );
                    }

                    if (typeDescr instanceof TypeDeclarationDescr) {
                        altDescr.addTypeDeclaration((TypeDeclarationDescr) typeDescr);
                    } else if (typeDescr instanceof EnumDeclarationDescr) {
                        altDescr.addEnumDeclaration((EnumDeclarationDescr) typeDescr);
                    }

                    for (ImportDescr imp : packageDescr.getImports()) {
                        altDescr.addImport(imp);
                    }
                    if (!getPackageRegistry().containsKey(altDescr.getNamespace())) {
                        newPackage(altDescr);
                    }
                }
            }
        }

        if ( foreignPackages != null ) {
            for ( String ns : foreignPackages.keySet() ) {
                mergePackage( this.pkgRegistryMap.get( ns ), foreignPackages.get( ns ) );
            }
            foreignPackages.clear();
        }

        // sort declarations : superclasses must be generated first
        Collection<AbstractClassTypeDeclarationDescr> sortedTypeDescriptors = sortByHierarchy(packageDescr.getClassAndEnumDeclarationDescrs());

        for (AbstractClassTypeDeclarationDescr typeDescr : sortedTypeDescriptors) {
            registerGeneratedType(typeDescr);
        }

        if (hasErrors()) {
            return Collections.emptyList();
        }

        for (AbstractClassTypeDeclarationDescr typeDescr : sortedTypeDescriptors) {

            if (!typeDescr.getNamespace().equals(packageDescr.getNamespace())) {
                continue;
            }

            //descriptor needs fields inherited from superclass
            if (typeDescr instanceof TypeDeclarationDescr) {
                TypeDeclarationDescr tDescr = (TypeDeclarationDescr) typeDescr;
                for (QualifiedName qname : tDescr.getSuperTypes()) {
                    //descriptor needs fields inherited from superclass
                    if (mergeInheritedFields(tDescr)) {
                        //descriptor also needs metadata from superclass
                        for (AbstractClassTypeDeclarationDescr descr : sortedTypeDescriptors) {
                            // sortedTypeDescriptors are sorted by inheritance order, so we'll always find the superClass (if any) before the subclass
                            if (qname.equals(descr.getType())) {
                                typeDescr.getAnnotations().putAll(descr.getAnnotations());
                                break;
                            } else if (typeDescr.getType().equals(descr.getType())) {
                                break;
                            }

                        }
                    }
                }
            }

            // Go on with the build
            TypeDeclaration type = new TypeDeclaration(typeDescr.getTypeName());
            if (typeDescr.getResource() == null) {
                typeDescr.setResource(resource);
            }
            type.setResource(typeDescr.getResource());

            TypeDeclaration parent = null;
            if (!typeDescr.getSuperTypes().isEmpty()) {
                // parent might have inheritable properties
                PackageRegistry sup = pkgRegistryMap.get(typeDescr.getSuperTypeNamespace());
                if (sup != null) {
                    parent = sup.getPackage().getTypeDeclaration(typeDescr.getSuperTypeName());
                    if (parent == null) {
                        this.results.add(new TypeDeclarationError(typeDescr, "Declared class " + typeDescr.getTypeName() + " can't extend class " + typeDescr.getSuperTypeName() + ", it should be declared"));
                    } else {
                        if (parent.getNature() == TypeDeclaration.Nature.DECLARATION && ruleBase != null) {
                            // trying to find a definition
                            parent = ruleBase.getPackagesMap().get(typeDescr.getSuperTypeNamespace()).getTypeDeclaration(typeDescr.getSuperTypeName());
                        }
                    }
                }
            }

            // is it a regular fact or an event?
            AnnotationDescr annotationDescr = typeDescr.getAnnotation(TypeDeclaration.Role.ID);
            String role = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
            if (role != null) {
                type.setRole(TypeDeclaration.Role.parseRole(role));
            } else if (parent != null) {
                type.setRole(parent.getRole());
            }

            annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_TYPESAFE);
            String typesafe = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
            if (typesafe != null) {
                type.setTypesafe(Boolean.parseBoolean(typesafe));
            } else if (parent != null) {
                type.setTypesafe(parent.isTypesafe());
            }

            // is it a pojo or a template?
            annotationDescr = typeDescr.getAnnotation(TypeDeclaration.Format.ID);
            String format = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
            if (format != null) {
                type.setFormat(TypeDeclaration.Format.parseFormat(format));
            }

            // is it a class, a trait or an enum?
            annotationDescr = typeDescr.getAnnotation(TypeDeclaration.Kind.ID);
            String kind = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
            if (kind != null) {
                type.setKind(TypeDeclaration.Kind.parseKind(kind));
            }
            if (typeDescr instanceof EnumDeclarationDescr) {
                type.setKind(TypeDeclaration.Kind.ENUM);
            }

            annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_CLASS);
            String className = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
            if (StringUtils.isEmpty(className)) {
                className = type.getTypeName();
            }

            try {

                // the type declaration is generated in any case (to be used by subclasses, if any)
                // the actual class will be generated only if needed
                if (!hasErrors()) {
                    generateDeclaredBean(typeDescr,
                            type,
                            pkgRegistry,
                            unresolvedTypes);

                    Class<?> clazz = pkgRegistry.getTypeResolver().resolveType(typeDescr.getType().getFullName());
                    type.setTypeClass(clazz);
                }

            } catch (final ClassNotFoundException e) {
                this.results.add(new TypeDeclarationError(typeDescr,
                        "Class '" + className +
                                "' not found for type declaration of '" +
                                type.getTypeName() + "'"));
                continue;
            }

            if (!processTypeFields(pkgRegistry, typeDescr, type, true)) {
                unresolvedTypes.add(new TypeDefinition(type, typeDescr));
            }
        }

        return unresolvedTypes;
    }

    private boolean processTypeFields(PackageRegistry pkgRegistry,
            AbstractClassTypeDeclarationDescr typeDescr,
            TypeDeclaration type,
            boolean firstAttempt) {
        if (type.getTypeClassDef() != null) {
            try {
                buildFieldAccessors(type, pkgRegistry);
            } catch (Throwable e) {
                if (!firstAttempt) {
                    this.results.add(new TypeDeclarationError(typeDescr,
                            "Error creating field accessors for TypeDeclaration '" + type.getTypeName() +
                                    "' for type '" +
                                    type.getTypeName() +
                                    "'"));
                }
                return false;
            }
        }

        AnnotationDescr annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_TIMESTAMP);
        String timestamp = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (timestamp != null) {
            type.setTimestampAttribute(timestamp);
            Package pkg = pkgRegistry.getPackage();

            MVELDialect dialect = (MVELDialect) pkgRegistry.getDialectCompiletimeRegistry().getDialect("mvel");
            PackageBuildContext context = new PackageBuildContext();
            context.init(this, pkg, typeDescr, pkgRegistry.getDialectCompiletimeRegistry(), dialect, null);
            if (!type.isTypesafe()) {
                context.setTypesafe(false);
            }

            MVELAnalysisResult results = (MVELAnalysisResult)
                    context.getDialect().analyzeExpression(context,
                            typeDescr,
                            timestamp,
                            new BoundIdentifiers(Collections.EMPTY_MAP,
                                    Collections.EMPTY_MAP,
                                    Collections.EMPTY_MAP,
                                    type.getTypeClass()));

            if (results != null) {
                InternalReadAccessor reader = pkg.getClassFieldAccessorStore().getMVELReader(ClassUtils.getPackage(type.getTypeClass()),
                        type.getTypeClass().getName(),
                        timestamp,
                        type.isTypesafe(),
                        results.getReturnType());

                MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
                data.addCompileable((MVELCompileable) reader);
                ((MVELCompileable) reader).compile(data);
                type.setTimestampExtractor(reader);
            } else {
                this.results.add(new TypeDeclarationError(typeDescr,
                        "Error creating field accessors for timestamp field '" + timestamp +
                                "' for type '" +
                                type.getTypeName() +
                                "'"));
            }
        }

        annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_DURATION);
        String duration = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (duration != null) {
            type.setDurationAttribute(duration);
            Package pkg = pkgRegistry.getPackage();

            MVELDialect dialect = (MVELDialect) pkgRegistry.getDialectCompiletimeRegistry().getDialect("mvel");
            PackageBuildContext context = new PackageBuildContext();
            context.init(this, pkg, typeDescr, pkgRegistry.getDialectCompiletimeRegistry(), dialect, null);
            if (!type.isTypesafe()) {
                context.setTypesafe(false);
            }

            MVELAnalysisResult results = (MVELAnalysisResult)
                    context.getDialect().analyzeExpression(context,
                            typeDescr,
                            duration,
                            new BoundIdentifiers(Collections.EMPTY_MAP,
                                    Collections.EMPTY_MAP,
                                    Collections.EMPTY_MAP,
                                    type.getTypeClass()));

            if (results != null) {
                InternalReadAccessor reader = pkg.getClassFieldAccessorStore().getMVELReader(ClassUtils.getPackage(type.getTypeClass()),
                        type.getTypeClass().getName(),
                        duration,
                        type.isTypesafe(),
                        results.getReturnType());

                MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
                data.addCompileable((MVELCompileable) reader);
                ((MVELCompileable) reader).compile(data);
                type.setDurationExtractor(reader);
            } else {
                this.results.add(new TypeDeclarationError(typeDescr,
                        "Error processing @duration for TypeDeclaration '" + type.getFullName() +
                                "': cannot access the field '" + duration + "'"));
            }
        }

        annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_EXPIRE);
        String expiration = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (expiration != null) {
            if (timeParser == null) {
                timeParser = new TimeIntervalParser();
            }
            type.setExpirationOffset(timeParser.parse(expiration)[0]);
        }

        boolean dynamic = typeDescr.getAnnotationNames().contains(TypeDeclaration.ATTR_PROP_CHANGE_SUPPORT);
        type.setDynamic(dynamic);

        PropertySpecificOption propertySpecificOption = configuration.getOption(PropertySpecificOption.class);
        boolean propertyReactive = propertySpecificOption.isPropSpecific(typeDescr.getAnnotationNames().contains(TypeDeclaration.ATTR_PROP_SPECIFIC),
                typeDescr.getAnnotationNames().contains(TypeDeclaration.ATTR_NOT_PROP_SPECIFIC));

        setPropertyReactive(typeDescr.getResource(), type, propertyReactive);

        if (type.isValid()) {
            // prefer definitions where possible
            if (type.getNature() == TypeDeclaration.Nature.DEFINITION) {
                pkgRegistry.getPackage().addTypeDeclaration(type);
            } else {
                TypeDeclaration oldType = pkgRegistry.getPackage().getTypeDeclaration(type.getTypeName());
                if (oldType == null) {
                    pkgRegistry.getPackage().addTypeDeclaration(type);
                } else {
                    if (type.getRole() == TypeDeclaration.Role.EVENT) {
                        oldType.setRole(TypeDeclaration.Role.EVENT);
                        if ( type.getDurationAttribute() != null ) {
                            oldType.setDurationAttribute( type.getDurationAttribute() );
                            oldType.setDurationExtractor( type.getDurationExtractor() );
                        }
                        if ( type.getTimestampAttribute() != null ) {
                            oldType.setTimestampAttribute( type.getTimestampAttribute() );
                            oldType.setTimestampExtractor( type.getTimestampExtractor() );
                        }
                        if ( type.getExpirationOffset() >= 0 ) {
                            oldType.setExpirationOffset( type.getExpirationOffset() );
                        }
                    }
                    if (type.isPropertyReactive()) {
                        oldType.setPropertyReactive(true);
                    }
                }
            }
        }

        return true;
    }

    private void setPropertyReactive(Resource resource,
            TypeDeclaration type,
            boolean propertyReactive) {
        if (propertyReactive && type.getSettableProperties().size() >= 64) {
            this.results.add(new DisabledPropertyReactiveWarning(resource, type.getTypeName()));
            type.setPropertyReactive(false);
        } else {
            type.setPropertyReactive(propertyReactive);
        }
    }

    private void updateTraitDefinition(TypeDeclaration type,
            Class concrete) {
        try {

            ClassFieldInspector inspector = new ClassFieldInspector(concrete);
            Map<String, Method> methods = inspector.getGetterMethods();
            Map<String, Method> setters = inspector.getSetterMethods();
            int j = 0;
            for (String fieldName : methods.keySet()) {
                if ("core".equals(fieldName) || "fields".equals(fieldName)) {
                    continue;
                }
                if (!inspector.isNonGetter(fieldName) && setters.keySet().contains(fieldName)) {

                    Class ret = methods.get(fieldName).getReturnType();
                    FieldDefinition field = new FieldDefinition();
                    field.setName(fieldName);
                    field.setTypeName(ret.getName());
                    field.setIndex(j++);
                    type.getTypeClassDef().addField(field);
                }
            }

            Set<String> interfaces = new HashSet<String>();
            Collections.addAll(interfaces, type.getTypeClassDef().getInterfaces());
            for (Class iKlass : concrete.getInterfaces()) {
                interfaces.add(iKlass.getName());
            }
            type.getTypeClassDef().setInterfaces(interfaces.toArray(new String[interfaces.size()]));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks whether a declaration is novel, or is a retagging of an external
     * one
     *
     * @param typeDescr
     * @return
     */
    private boolean isNovelClass(AbstractClassTypeDeclarationDescr typeDescr) {
        return getExistingDeclarationClass(typeDescr) == null;
    }

    private Class<?> getExistingDeclarationClass(AbstractClassTypeDeclarationDescr typeDescr) {
        PackageRegistry reg = this.pkgRegistryMap.get(typeDescr.getNamespace());
        if (reg == null) {
            return null;
        }
        String availableName = typeDescr.getType().getFullName();
        try {
            return reg.getTypeResolver().resolveType(availableName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Tries to determine whether a given annotation is properly defined using a
     * java.lang.Annotation and can be resolved
     *
     * Proper annotations will be wired to dynamically generated beans
     *
     * @param annotation
     * @param resolver
     * @return
     */
    private Class resolveAnnotation(String annotation,
            TypeResolver resolver) {

        // do not waste time with @format
        if (TypeDeclaration.Format.ID.equals(annotation)) {
            return null;
        }
        // known conflicting annotation
        if (TypeDeclaration.ATTR_CLASS.equals(annotation)) {
            return null;
        }

        try {
            return resolver.resolveType(annotation.indexOf('.') < 0 ?
                    annotation.substring(0, 1).toUpperCase() + annotation.substring(1) :
                    annotation);
        } catch (ClassNotFoundException e) {
            // internal annotation, or annotation which can't be resolved.
            if (TypeDeclaration.Role.ID.equals(annotation)) {
                return Role.class;
            }
            if ("key".equals(annotation)) {
                return Key.class;
            }
            if ("position".equals(annotation)) {
                return Position.class;
            }
            return null;
        }
    }

    /**
     *
     * @param pkgRegistry
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws IntrospectionException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    private void buildFieldAccessors(final TypeDeclaration type,
            final PackageRegistry pkgRegistry) throws SecurityException,
            IllegalArgumentException,
            InstantiationException,
            IllegalAccessException,
            IOException,
            IntrospectionException,
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            NoSuchFieldException {
        ClassDefinition cd = type.getTypeClassDef();
        ClassFieldAccessorStore store = pkgRegistry.getPackage().getClassFieldAccessorStore();
        for (FieldDefinition attrDef : cd.getFieldsDefinitions()) {
            ClassFieldAccessor accessor = store.getAccessor(cd.getDefinedClass().getName(),
                    attrDef.getName());
            attrDef.setReadWriteAccessor(accessor);
        }
    }

    /**
     * Generates a bean, and adds it to the composite class loader that
     * everything is using.
     */
    private void generateDeclaredBean(AbstractClassTypeDeclarationDescr typeDescr,
            TypeDeclaration type,
            PackageRegistry pkgRegistry,
            List<TypeDefinition> unresolvedTypeDefinitions) {

        // extracts type, supertype and interfaces
        String fullName = typeDescr.getType().getFullName();

        if (type.getKind().equals(TypeDeclaration.Kind.CLASS)) {
            TypeDeclarationDescr tdescr = (TypeDeclarationDescr) typeDescr;
            if (tdescr.getSuperTypes().size() > 1) {
                this.results.add(new TypeDeclarationError(typeDescr, "Declared class " + fullName + "  - has more than one supertype;"));
                return;
            } else if (tdescr.getSuperTypes().isEmpty()) {
                tdescr.addSuperType("java.lang.Object");
            }
        }

        AnnotationDescr traitableAnn = typeDescr.getAnnotation(Traitable.class.getSimpleName());
        boolean traitable = traitableAnn != null;

        String[] fullSuperTypes = new String[typeDescr.getSuperTypes().size() + 1];
        int j = 0;
        for (QualifiedName qname : typeDescr.getSuperTypes()) {
            fullSuperTypes[j++] = qname.getFullName();
        }
        fullSuperTypes[j] = Thing.class.getName();

        List<String> interfaceList = new ArrayList<String>();
        interfaceList.add(traitable ? Externalizable.class.getName() : Serializable.class.getName());
        if (traitable) {
            interfaceList.add(TraitableBean.class.getName());
        }
        String[] interfaces = interfaceList.toArray(new String[interfaceList.size()]);

        // prepares a class definition
        ClassDefinition def;
        switch (type.getKind()) {
            case TRAIT:
                def = new ClassDefinition(fullName,
                        "java.lang.Object",
                        fullSuperTypes);
                break;
            case ENUM:
                def = new EnumClassDefinition(fullName,
                        fullSuperTypes[0],
                        null);
                break;
            case CLASS:
            default:
                def = new ClassDefinition(fullName,
                        fullSuperTypes[0],
                        interfaces);
                def.setTraitable(traitable, traitableAnn != null &&
                        traitableAnn.getValue("logical") != null &&
                        Boolean.valueOf(traitableAnn.getValue("logical")));
        }

        for (String annotationName : typeDescr.getAnnotationNames()) {
            Class annotation = resolveAnnotation(annotationName,
                    pkgRegistry.getTypeResolver());
            if (annotation != null) {
                try {
                    AnnotationDefinition annotationDefinition = AnnotationDefinition.build(annotation,
                            typeDescr.getAnnotations().get(annotationName).getValueMap(),
                            pkgRegistry.getTypeResolver());
                    def.addAnnotation(annotationDefinition);
                } catch (NoSuchMethodException nsme) {
                    this.results.add(new TypeDeclarationError(typeDescr,
                            "Annotated type " + fullName +
                                    "  - undefined property in @annotation " +
                                    annotationName + ": " +
                                    nsme.getMessage() + ";"));
                }
            }
            if (annotation == null || annotation == Role.class) {
                def.addMetaData(annotationName, typeDescr.getAnnotation(annotationName).getSingleValue());
            }
        }

        // add enum literals, if appropriate
        if (type.getKind() == TypeDeclaration.Kind.ENUM) {
            for (EnumLiteralDescr lit : ((EnumDeclarationDescr) typeDescr).getLiterals()) {
                ((EnumClassDefinition) def).addLiteral(
                        new EnumLiteralDefinition(lit.getName(), lit.getConstructorArgs())
                        );
            }
        }

        // fields definitions are created. will be used by subclasses, if any.
        // Fields are SORTED in the process
        if (!typeDescr.getFields().isEmpty()) {
            PriorityQueue<FieldDefinition> fieldDefs = sortFields(typeDescr.getFields(),
                    pkgRegistry);
            int n = fieldDefs.size();
            for (int k = 0; k < n; k++) {
                FieldDefinition fld = fieldDefs.poll();
                if (unresolvedTypeDefinitions != null) {
                    for (TypeDefinition typeDef : unresolvedTypeDefinitions) {
                        if (fld.getTypeName().equals(typeDef.getTypeClassName())) {
                            fld.setRecursive(true);
                            break;
                        }
                    }
                }
                fld.setIndex(k);
                def.addField(fld);
            }
        }

        // check whether it is necessary to build the class or not
        Class<?> existingDeclarationClass = getExistingDeclarationClass(typeDescr);
        type.setNovel(existingDeclarationClass == null);

        // attach the class definition, it will be completed later
        type.setTypeClassDef(def);

        //if is not new, search the already existing declaration and
        //compare them o see if they are at least compatibles
        if (!type.isNovel()) {
            TypeDeclaration previousTypeDeclaration = this.pkgRegistryMap.get(typeDescr.getNamespace()).getPackage().getTypeDeclaration(typeDescr.getTypeName());

            try {

                if (!type.getTypeClassDef().getFields().isEmpty()) {
                    //since the declaration defines one or more fields, it is a DEFINITION
                    type.setNature(TypeDeclaration.Nature.DEFINITION);
                } else {
                    //The declaration doesn't define any field, it is a DECLARATION
                    type.setNature(TypeDeclaration.Nature.DECLARATION);
                }

                //if there is no previous declaration, then the original declaration was a POJO
                //to the behavior previous these changes
                if (previousTypeDeclaration == null) {
                    // new declarations of a POJO can't declare new fields,
                    // except if the POJO was previously generated/compiled and saved into the kjar
                    if (!configuration.isPreCompiled() &&
                        !GeneratedFact.class.isAssignableFrom(existingDeclarationClass) && !type.getTypeClassDef().getFields().isEmpty()) {
                        try {
                            Class existingClass = pkgRegistry.getPackage().getTypeResolver().resolveType( typeDescr.getType().getFullName() );
                            ClassFieldInspector cfi = new ClassFieldInspector( existingClass );

                            int fieldCount = 0;
                            for ( String existingFieldName : cfi.getFieldTypesField().keySet() ) {
                                if ( ! cfi.isNonGetter( existingFieldName ) && ! "class".equals( existingFieldName ) && cfi.getSetterMethods().containsKey( existingFieldName ) ) {
                                    if ( ! typeDescr.getFields().containsKey( existingFieldName ) ) {
                                        type.setValid(false);
                                        this.results.add(new TypeDeclarationError(typeDescr, "New declaration of "+typeDescr.getType().getFullName() +
                                                                                             " does not include field " + existingFieldName ) );
                                    } else {
                                        String fldType = cfi.getFieldTypes().get( existingFieldName ).getName();
                                        TypeFieldDescr declaredField = typeDescr.getFields().get( existingFieldName );
                                        if ( ! fldType.equals( type.getTypeClassDef().getField( existingFieldName ).getTypeName() ) ) {
                                            type.setValid(false);
                                            this.results.add(new TypeDeclarationError(typeDescr, "New declaration of "+typeDescr.getType().getFullName() +
                                                                                                 " redeclared field " + existingFieldName + " : \n" +
                                                                                                 "existing : " + fldType + " vs declared : " + declaredField.getPattern().getObjectType() ) );
                                        } else {
                                            fieldCount++;
                                        }

                                    }
                                }
                            }

                            if ( fieldCount != typeDescr.getFields().size() ) {
                                this.results.add(new TypeDeclarationError(typeDescr, "New declaration of "+typeDescr.getType().getFullName()
                                                                                     +" can't declaredeclares a different set of fields \n" +
                                                                                     "existing : " + cfi.getFieldTypesField() + "\n" +
                                                                                     "declared : " + typeDescr.getFields() ));

                            }
                        } catch ( IOException e ) {
                            e.printStackTrace();
                            type.setValid(false);
                            this.results.add( new TypeDeclarationError( typeDescr, "Unable to redeclare " + typeDescr.getType().getFullName() + " : " + e.getMessage() ) );
                        } catch ( ClassNotFoundException e ) {
                            type.setValid(false);
                            this.results.add( new TypeDeclarationError( typeDescr, "Unable to redeclare " + typeDescr.getType().getFullName() + " : " + e.getMessage() ) );
                        }
                    }
                } else {

                    int typeComparisonResult = this.compareTypeDeclarations(previousTypeDeclaration, type);

                    if (typeComparisonResult < 0) {
                        //oldDeclaration is "less" than newDeclaration -> error
                        this.results.add(new TypeDeclarationError(typeDescr, typeDescr.getType().getFullName()
                                + " declares more fields than the already existing version"));
                        type.setValid(false);
                    } else if (typeComparisonResult > 0 && !type.getTypeClassDef().getFields().isEmpty()) {
                        //oldDeclaration is "grater" than newDeclaration -> error
                        this.results.add(new TypeDeclarationError(typeDescr, typeDescr.getType().getFullName()
                                + " declares less fields than the already existing version"));
                        type.setValid(false);
                    }

                    //if they are "equal" -> no problem

                    // in the case of a declaration, we need to copy all the
                    // fields present in the previous declaration
                    if (type.getNature() == TypeDeclaration.Nature.DECLARATION) {
                        this.mergeTypeDeclarations(previousTypeDeclaration, type);
                    }
                }

            } catch (IncompatibleClassChangeError error) {
                //if the types are incompatible -> error
                this.results.add(new TypeDeclarationError(typeDescr, error.getMessage()));
            }

        } else {
            //if the declaration is novel, then it is a DEFINITION
            type.setNature(TypeDeclaration.Nature.DEFINITION);
        }

        generateDeclaredBean(typeDescr,
                type,
                pkgRegistry,
                expandImportsInFieldInitExpr(def, pkgRegistry));
    }

    private ClassDefinition expandImportsInFieldInitExpr(ClassDefinition def,
            PackageRegistry pkgRegistry) {
        TypeResolver typeResolver = pkgRegistry.getPackage().getTypeResolver();
        for (FieldDefinition field : def.getFieldsDefinitions()) {
            field.setInitExpr(rewriteInitExprWithImports(field.getInitExpr(), typeResolver));
        }
        return def;
    }

    private String rewriteInitExprWithImports(String expr,
            TypeResolver typeResolver) {
        if (expr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        boolean inTypeName = false;
        boolean afterDot = false;
        int typeStart = 0;
        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);
            if (Character.isJavaIdentifierStart(ch)) {
                if (!inTypeName && !inQuotes && !afterDot) {
                    typeStart = i;
                    inTypeName = true;
                }
            } else if (!Character.isJavaIdentifierPart(ch)) {
                if (ch == '"') {
                    inQuotes = !inQuotes;
                } else if (ch == '.' && !inQuotes) {
                    afterDot = true;
                } else if (!Character.isSpaceChar(ch)) {
                    afterDot = false;
                }
                if (inTypeName) {
                    inTypeName = false;
                    String type = expr.substring(typeStart, i);
                    sb.append(getFullTypeName(type, typeResolver));
                }
            }
            if (!inTypeName) {
                sb.append(ch);
            }
        }
        if (inTypeName) {
            String type = expr.substring(typeStart);
            sb.append(getFullTypeName(type, typeResolver));
        }
        return sb.toString();
    }

    private String getFullTypeName(String type,
            TypeResolver typeResolver) {
        if (type.equals("new")) {
            return type;
        }
        try {
            return typeResolver.getFullTypeName(type);
        } catch (ClassNotFoundException e) {
            return type;
        }
    }

    private void generateDeclaredBean(AbstractClassTypeDeclarationDescr typeDescr,
            TypeDeclaration type,
            PackageRegistry pkgRegistry,
            ClassDefinition def) {

        if (typeDescr.getAnnotation(Traitable.class.getSimpleName()) != null
                || (!type.getKind().equals(TypeDeclaration.Kind.TRAIT) &&
                        pkgRegistryMap.containsKey(def.getSuperClass()) &&
                pkgRegistryMap.get(def.getSuperClass()).getTraitRegistry().getTraitables().containsKey(def.getSuperClass())
                )) {
            if (!isNovelClass(typeDescr)) {
                try {
                    PackageRegistry reg = this.pkgRegistryMap.get(typeDescr.getNamespace());
                    String availableName = typeDescr.getType().getFullName();
                    Class<?> resolvedType = reg.getTypeResolver().resolveType(availableName);
                    updateTraitDefinition(type,
                            resolvedType);
                } catch (ClassNotFoundException cnfe) {
                    // we already know the class exists
                }
            }
            pkgRegistry.getTraitRegistry().addTraitable(def);
        } else if (type.getKind().equals(TypeDeclaration.Kind.TRAIT)
                || typeDescr.getAnnotation(Trait.class.getSimpleName()) != null) {

            if (!type.isNovel()) {
                try {
                    PackageRegistry reg = this.pkgRegistryMap.get(typeDescr.getNamespace());
                    String availableName = typeDescr.getType().getFullName();
                    Class<?> resolvedType = reg.getTypeResolver().resolveType(availableName);
                    if (!Thing.class.isAssignableFrom(resolvedType)) {
                        updateTraitDefinition(type,
                                resolvedType);

                        String target = typeDescr.getTypeName() + TraitFactory.SUFFIX;
                        TypeDeclarationDescr tempDescr = new TypeDeclarationDescr();
                        tempDescr.setNamespace(typeDescr.getNamespace());
                        tempDescr.setFields(typeDescr.getFields());
                        tempDescr.setType(target,
                                typeDescr.getNamespace());
                        tempDescr.addSuperType(typeDescr.getType());
                        TypeDeclaration tempDeclr = new TypeDeclaration(target);
                        tempDeclr.setKind(TypeDeclaration.Kind.TRAIT);
                        tempDeclr.setTypesafe(type.isTypesafe());
                        tempDeclr.setNovel(true);
                        tempDeclr.setTypeClassName(tempDescr.getType().getFullName());
                        tempDeclr.setResource(type.getResource());

                        ClassDefinition tempDef = new ClassDefinition(target);
                        tempDef.setClassName(tempDescr.getType().getFullName());
                        tempDef.setTraitable(false);
                        for (FieldDefinition fld : def.getFieldsDefinitions()) {
                            tempDef.addField(fld);
                        }
                        tempDef.setInterfaces(def.getInterfaces());
                        tempDef.setSuperClass(def.getClassName());
                        tempDef.setDefinedClass(resolvedType);
                        tempDef.setAbstrakt(true);
                        tempDeclr.setTypeClassDef(tempDef);

                        type.setKind(TypeDeclaration.Kind.CLASS);

                        generateDeclaredBean(tempDescr,
                                tempDeclr,
                                pkgRegistry,
                                tempDef);
                        try {
                            Class<?> clazz = pkgRegistry.getTypeResolver().resolveType(tempDescr.getType().getFullName());
                            tempDeclr.setTypeClass(clazz);
                        } catch (ClassNotFoundException cnfe) {
                            this.results.add(new TypeDeclarationError(typeDescr,
                                    "Internal Trait extension Class '" + target +
                                            "' could not be generated correctly'"));
                        } finally {
                            pkgRegistry.getPackage().addTypeDeclaration(tempDeclr);
                        }

                    } else {
                        updateTraitDefinition(type,
                                resolvedType);
                        pkgRegistry.getTraitRegistry().addTrait(def);
                    }
                } catch (ClassNotFoundException cnfe) {
                    // we already know the class exists
                }
            } else {
                if (def.getClassName().endsWith(TraitFactory.SUFFIX)) {
                    pkgRegistry.getTraitRegistry().addTrait(def.getClassName().replace(TraitFactory.SUFFIX,
                            ""),
                            def);
                } else {
                    pkgRegistry.getTraitRegistry().addTrait(def);
                }
            }

        }

        if (type.isNovel()) {
            String fullName = typeDescr.getType().getFullName();
            JavaDialectRuntimeData dialect = (JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData("java");
            switch (type.getKind()) {
                case TRAIT:
                    try {
                        buildClass(def, fullName, dialect, configuration.getClassBuilderFactory().getTraitBuilder());
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.results.add(new TypeDeclarationError(typeDescr,
                                "Unable to compile declared trait " + fullName +
                                        ": " + e.getMessage() + ";"));
                    }
                    break;
                case ENUM:
                    try {
                        buildClass(def, fullName, dialect, configuration.getClassBuilderFactory().getEnumClassBuilder());
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.results.add(new TypeDeclarationError(typeDescr,
                                "Unable to compile declared enum " + fullName +
                                        ": " + e.getMessage() + ";"));
                    }
                    break;
                case CLASS:
                default:
                    try {
                        buildClass(def, fullName, dialect, configuration.getClassBuilderFactory().getBeanClassBuilder());
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.results.add(new TypeDeclarationError(typeDescr,
                                "Unable to create a class for declared type " + fullName +
                                        ": " + e.getMessage() + ";"));
                    }
                    break;
            }

        }

    }

    private void buildClass(ClassDefinition def, String fullName, JavaDialectRuntimeData dialect, ClassBuilder cb) throws Exception {
        byte[] bytecode = cb.buildClass(def, rootClassLoader);
        String resourceName = convertClassToResourcePath(fullName);
        dialect.putClassDefinition(resourceName, bytecode);
        if (ruleBase != null) {
            ruleBase.registerAndLoadTypeDefinition(fullName, bytecode);
        } else {
            if (rootClassLoader instanceof ProjectClassLoader) {
                ((ProjectClassLoader) rootClassLoader).defineClass(fullName, resourceName, bytecode);
            } else {
                dialect.write(resourceName, bytecode);
            }
        }
    }

    /**
     * Sorts a bean's fields according to the positional index metadata. The
     * order is as follows (i) as defined using the @position metadata (ii) as
     * resulting from the inspection of an external java superclass, if
     * applicable (iii) in declaration order, superclasses first
     *
     * @param flds
     * @param pkgRegistry
     * @return
     */
    private PriorityQueue<FieldDefinition> sortFields(Map<String, TypeFieldDescr> flds, PackageRegistry pkgRegistry) {
        PriorityQueue<FieldDefinition> queue = new PriorityQueue<FieldDefinition>(flds.size());
        int maxDeclaredPos = 0;
        int curr = 0;

        BitSet occupiedPositions = new BitSet(flds.size());
        for (TypeFieldDescr field : flds.values()) {
            int pos = field.getIndex();
            if (pos >= 0) {
                occupiedPositions.set(pos);
            }
            maxDeclaredPos = Math.max(maxDeclaredPos, pos);
        }

        for (TypeFieldDescr field : flds.values()) {

            try {
                String typeName = field.getPattern().getObjectType();
                String fullFieldType = generatedTypes.contains(typeName) ? typeName : pkgRegistry.getTypeResolver().resolveType(typeName).getName();

                FieldDefinition fieldDef = new FieldDefinition(field.getFieldName(),
                        fullFieldType);
                // field is marked as PK
                boolean isKey = field.getAnnotation(TypeDeclaration.ATTR_KEY) != null;
                fieldDef.setKey(isKey);

                fieldDef.setDeclIndex(field.getIndex());
                if (field.getIndex() < 0) {
                    int freePos = occupiedPositions.nextClearBit(0);
                    if (freePos < maxDeclaredPos) {
                        occupiedPositions.set(freePos);
                    } else {
                        freePos = maxDeclaredPos + 1;
                    }
                    fieldDef.setPriority(freePos * 256 + curr++);
                } else {
                    fieldDef.setPriority(field.getIndex() * 256 + curr++);
                }
                fieldDef.setInherited(field.isInherited());
                fieldDef.setInitExpr(field.getInitExpr());

                for (String annotationName : field.getAnnotationNames()) {
                    Class annotation = resolveAnnotation(annotationName,
                            pkgRegistry.getTypeResolver());
                    if (annotation != null) {
                        try {
                            AnnotationDefinition annotationDefinition = AnnotationDefinition.build(annotation,
                                    field.getAnnotations().get(annotationName).getValueMap(),
                                    pkgRegistry.getTypeResolver());
                            fieldDef.addAnnotation(annotationDefinition);
                        } catch (NoSuchMethodException nsme) {
                            this.results.add(new TypeDeclarationError(field,
                                    "Annotated field " + field.getFieldName() +
                                            "  - undefined property in @annotation " +
                                            annotationName + ": " + nsme.getMessage() + ";"));
                        }
                    }
                    if (annotation == null || annotation == Key.class || annotation == Position.class) {
                        fieldDef.addMetaData(annotationName, field.getAnnotation(annotationName).getSingleValue());
                    }
                }

                queue.add(fieldDef);
            } catch (ClassNotFoundException cnfe) {
                this.results.add(new TypeDeclarationError(field, cnfe.getMessage()));
            }

        }

        return queue;
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

            Package pkg = pkgRegistry.getPackage();
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

        Package pkg = context.getPkg();

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
    public Package getPackage() {
        PackageRegistry pkgRegistry = null;
        if (!this.pkgRegistryMap.isEmpty()) {
            pkgRegistry = (PackageRegistry) this.pkgRegistryMap.values().toArray()[currentRulePackage];
        }
        Package pkg = null;
        if (pkgRegistry != null) {
            pkg = pkgRegistry.getPackage();
        }
        if (hasErrors() && pkg != null) {
            pkg.setError(getErrors().toString());
        }
        return pkg;
    }

    public Package[] getPackages() {
        Package[] pkgs = new Package[this.pkgRegistryMap.size()];
        String errors = null;
        if (!getErrors().isEmpty()) {
            errors = getErrors().toString();
        }
        int i = 0;
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
            Package pkg = pkgRegistry.getPackage();
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
    public PackageBuilderConfiguration getPackageBuilderConfiguration() {
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

    public KnowledgeBuilderResults getProblems(ResultSeverity... problemTypes) {
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

    public boolean hasProblems(ResultSeverity... problemTypes) {
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
        return !getWarningList().isEmpty();
    }

    public boolean hasInfo() {
        return !getInfoList().isEmpty();
    }

    public List<DroolsWarning> getWarningList() {
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

    /**
     * This is the super of the error handlers. Each error handler knows how to
     * report a compile error of its type, should it happen. This is needed, as
     * the compiling is done as one hit at the end, and we need to be able to
     * work out what rule/ast element caused the error.
     *
     * An error handler it created for each class task that is queued to be
     * compiled. This doesn't mean an error has occurred, it just means it *may*
     * occur in the future and we need to be able to map it back to the AST
     * element that originally spawned the code to be compiled.
     */
    public abstract static class ErrorHandler {

        private final List errors  = new ArrayList();

        protected String   message;

        private boolean    inError = false;

        /** This needes to be checked if there is infact an error */
        public boolean isInError() {
            return this.inError;
        }

        public void addError(final CompilationProblem err) {
            this.errors.add(err);
            this.inError = true;
        }

        /**
         *
         * @return A DroolsError object populated as appropriate, should the
         *         unthinkable happen and this need to be reported.
         */
        public abstract DroolsError getError();

        /**
         * We must use an error of JCI problem objects. If there are no
         * problems, null is returned. These errors are placed in the
         * DroolsError instances. Its not 1 to 1 with reported errors.
         */
        protected CompilationProblem[] collectCompilerProblems() {
            if (this.errors.isEmpty()) {
                return null;
            } else {
                final CompilationProblem[] list = new CompilationProblem[this.errors.size()];
                this.errors.toArray(list);
                return list;
            }
        }
    }

    public static class RuleErrorHandler extends ErrorHandler {

        private BaseDescr descr;

        private Rule      rule;

        public RuleErrorHandler(final BaseDescr ruleDescr,
                final Rule rule,
                final String message) {
            this.descr = ruleDescr;
            this.rule = rule;
            this.message = message;
        }

        public DroolsError getError() {
            return new RuleBuildError(this.rule,
                    this.descr,
                    collectCompilerProblems(),
                    this.message);
        }

    }

    /**
     * There isn't much point in reporting invoker errors, as they are no help.
     */
    public static class RuleInvokerErrorHandler extends RuleErrorHandler {

        public RuleInvokerErrorHandler(final BaseDescr ruleDescr,
                final Rule rule,
                final String message) {
            super(ruleDescr,
                    rule,
                    message);
        }
    }

    public static class FunctionErrorHandler extends ErrorHandler {

        private FunctionDescr descr;

        public FunctionErrorHandler(final FunctionDescr functionDescr,
                final String message) {
            this.descr = functionDescr;
            this.message = message;
        }

        public DroolsError getError() {
            return new FunctionError(this.descr,
                    collectCompilerProblems(),
                    this.message);
        }

    }

    public static class SrcErrorHandler extends ErrorHandler {

        public SrcErrorHandler(final String message) {
            this.message = message;
        }

        public DroolsError getError() {
            return new SrcError(collectCompilerProblems(),
                    this.message);
        }

    }

    public static class SrcError extends DroolsError {

        private Object object;
        private String message;
        private int[]  errorLines = new int[0];

        public SrcError(Object object,
                String message) {
            super(null);
            this.object = object;
            this.message = message;
        }

        public Object getObject() {
            return this.object;
        }

        public int[] getLines() {
            return this.errorLines;
        }

        public String getMessage() {
            return this.message;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append(this.message);
            buf.append(" : ");
            buf.append("\n");
            if (this.object instanceof CompilationProblem[]) {
                final CompilationProblem[] problem = (CompilationProblem[]) this.object;
                for (CompilationProblem aProblem : problem) {
                    buf.append("\t");
                    buf.append(aProblem);
                    buf.append("\n");
                }
            } else if (this.object != null) {
                buf.append(this.object);
            }
            return buf.toString();
        }
    }

    public ClassLoader getRootClassLoader() {
        return this.rootClassLoader;
    }

    /**
     * Utility method to sort declared beans. Linearizes the hierarchy,
     * i.e.generates a sequence of declaration such that, if Sub is subclass of
     * Sup, then the index of Sub will be > than the index of Sup in the
     * resulting collection. This ensures that superclasses are processed before
     * their subclasses
     *
     * @param typeDeclarations
     * @return
     */
    public Collection<AbstractClassTypeDeclarationDescr> sortByHierarchy(List<AbstractClassTypeDeclarationDescr> typeDeclarations) {

        HierarchySorter<QualifiedName> sorter = new HierarchySorter<QualifiedName>();
        Map<QualifiedName, Collection<QualifiedName>> taxonomy = new HashMap<QualifiedName, Collection<QualifiedName>>();
        Map<QualifiedName, AbstractClassTypeDeclarationDescr> cache = new HashMap<QualifiedName, AbstractClassTypeDeclarationDescr>();

        for (AbstractClassTypeDeclarationDescr tdescr : typeDeclarations) {
            QualifiedName name = tdescr.getType();

            cache.put(name, tdescr);

            if (taxonomy.get(name) == null) {
                taxonomy.put(name, new ArrayList<QualifiedName>());
            } else {
                this.results.add(new TypeDeclarationError(tdescr,
                        "Found duplicate declaration for type " + tdescr.getType()));
            }

            Collection<QualifiedName> supers = taxonomy.get(name);

            boolean circular = false;
            for (QualifiedName sup : tdescr.getSuperTypes()) {
                if (!Object.class.getName().equals(name.getFullName())) {
                    if (!hasCircularDependency(tdescr.getType(), sup, taxonomy)) {
                        supers.add(sup);
                    } else {
                        circular = true;
                        this.results.add(new TypeDeclarationError(tdescr,
                                "Found circular dependency for type " + tdescr.getTypeName()));
                        break;
                    }
                }
            }
            if (circular) {
                tdescr.getSuperTypes().clear();
            }

            for (TypeFieldDescr field : tdescr.getFields().values()) {
                QualifiedName typeName = new QualifiedName(field.getPattern().getObjectType());
                if (!hasCircularDependency(name, typeName, taxonomy)) {
                    supers.add(typeName);
                }

            }

        }
        List<QualifiedName> sorted = sorter.sort(taxonomy);
        ArrayList list = new ArrayList(sorted.size());
        for (QualifiedName name : sorted) {
            list.add(cache.get(name));
        }

        return list;
    }

    private boolean hasCircularDependency(QualifiedName name,
            QualifiedName typeName,
            Map<QualifiedName, Collection<QualifiedName>> taxonomy) {
        if (name.equals(typeName)) {
            return true;
        }
        if (taxonomy.containsKey(typeName)) {
            Collection<QualifiedName> parents = taxonomy.get(typeName);
            if (parents.contains(name)) {
                return true;
            } else {
                for (QualifiedName ancestor : parents) {
                    if (hasCircularDependency(name, ancestor, taxonomy)) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    private int compareTypeDeclarations(TypeDeclaration oldDeclaration,
            TypeDeclaration newDeclaration) throws IncompatibleClassChangeError {

        //different formats -> incompatible
        if (!oldDeclaration.getFormat().equals(newDeclaration.getFormat())) {
            throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " has a different"
                    + " format that its previous definition: " + newDeclaration.getFormat() + "!=" + oldDeclaration.getFormat());
        }

        //different superclasses -> Incompatible (TODO: check for hierarchy)
        if (!oldDeclaration.getTypeClassDef().getSuperClass().equals(newDeclaration.getTypeClassDef().getSuperClass())) {
            if (oldDeclaration.getNature() == TypeDeclaration.Nature.DEFINITION
                    && newDeclaration.getNature() == TypeDeclaration.Nature.DECLARATION
                    && Object.class.getName().equals(newDeclaration.getTypeClassDef().getSuperClass())) {
                // actually do nothing. The new declaration just recalls the previous definition, probably to extend it.
            } else {
                throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " has a different"
                        + " superclass that its previous definition: " + newDeclaration.getTypeClassDef().getSuperClass()
                        + " != " + oldDeclaration.getTypeClassDef().getSuperClass());
            }
        }

        //different duration -> Incompatible
        if (!this.nullSafeEqualityComparison(oldDeclaration.getDurationAttribute(), newDeclaration.getDurationAttribute())) {
            throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " has a different"
                    + " duration: " + newDeclaration.getDurationAttribute()
                    + " != " + oldDeclaration.getDurationAttribute());
        }

        //        //different masks -> incompatible
        if (newDeclaration.getNature().equals(TypeDeclaration.Nature.DEFINITION)) {
            if (oldDeclaration.getSetMask() != newDeclaration.getSetMask()) {
                throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " is incompatible with"
                        + " the previous definition: " + newDeclaration
                        + " != " + oldDeclaration);
            }
        }

        //TODO: further comparison?

        //Field comparison
        List<FactField> oldFields = oldDeclaration.getTypeClassDef().getFields();
        Map<String, FactField> newFieldsMap = new HashMap<String, FactField>();
        for (FactField factField : newDeclaration.getTypeClassDef().getFields()) {
            newFieldsMap.put(factField.getName(), factField);
        }

        //each of the fields in the old definition that are also present in the
        //new definition must have the same type. If not -> Incompatible
        boolean allFieldsInOldDeclarationAreStillPresent = true;
        for (FactField oldFactField : oldFields) {
            FactField newFactField = newFieldsMap.get(oldFactField.getName());

            if (newFactField != null) {
                //we can't use newFactField.getType() since it throws a NPE at this point.
                String newFactType = ((FieldDefinition) newFactField).getTypeName();

                if (!newFactType.equals( ((FieldDefinition) oldFactField).getTypeName())) {
                    throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + "." + newFactField.getName() + " has a different"
                            + " type that its previous definition: " + newFactType
                            + " != " + oldFactField.getType().getCanonicalName());
                }
            } else {
                allFieldsInOldDeclarationAreStillPresent = false;
            }

        }

        //If the old declaration has less fields than the new declaration, oldDefinition < newDefinition
        if (oldFields.size() < newFieldsMap.size()) {
            return -1;
        }

        //If the old declaration has more fields than the new declaration, oldDefinition > newDefinition
        if (oldFields.size() > newFieldsMap.size()) {
            return 1;
        }

        //If the old declaration has the same fields as the new declaration,
        //and all the fieds present in the old declaration are also present in
        //the new declaration, then they are considered "equal", otherwise
        //they are incompatible
        if (allFieldsInOldDeclarationAreStillPresent) {
            return 0;
        }

        //Both declarations have the same number of fields, but not all the
        //fields in the old declaration are present in the new declaration.
        throw new IncompatibleClassChangeError(newDeclaration.getTypeName() + " introduces"
                + " fields that are not present in its previous version.");

    }

    /**
     * Merges all the missing FactFields from oldDefinition into newDeclaration.
     * @param oldDeclaration
     * @param newDeclaration
     */
    private void mergeTypeDeclarations(TypeDeclaration oldDeclaration,
            TypeDeclaration newDeclaration) {
        if (oldDeclaration == null) {
            return;
        }

        //add the missing fields (if any) to newDeclaration
        for (FieldDefinition oldFactField : oldDeclaration.getTypeClassDef().getFieldsDefinitions()) {
            FieldDefinition newFactField = newDeclaration.getTypeClassDef().getField(oldFactField.getName());
            if (newFactField == null) {
                newDeclaration.getTypeClassDef().addField(oldFactField);
            }
        }

        //copy the defined class
        newDeclaration.setTypeClass(oldDeclaration.getTypeClass());
    }

    private boolean nullSafeEqualityComparison(Comparable c1,
            Comparable c2) {
        if (c1 == null) {
            return c2 == null;
        }
        return c2 != null && c1.compareTo(c2) == 0;
    }

    static class TypeDefinition {

        private final AbstractClassTypeDeclarationDescr typeDescr;
        private final TypeDeclaration                   type;

        private TypeDefinition(TypeDeclaration type,
                AbstractClassTypeDeclarationDescr typeDescr) {
            this.type = type;
            this.typeDescr = typeDescr;
        }

        public String getTypeClassName() {
            return type.getTypeClassName();
        }

        public String getNamespace() {
            return typeDescr.getNamespace();
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
            this.results.add(new ResourceTypeDeclarationWarning(resource, ires.getResourceType(), type));
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

        if (cacheTypes != null) {
            List<String> typesToBeRemoved = new ArrayList<String>();
            for (Map.Entry<String, TypeDeclaration> type : cacheTypes.entrySet()) {
                if (resource.equals(type.getValue().getResource())) {
                    typesToBeRemoved.add(type.getKey());
                }
            }
            for (String type : typesToBeRemoved) {
                cacheTypes.remove(type);
            }
        }

        for (List<PackageDescr> pkgDescrs : packages.values()) {
            for (PackageDescr pkgDescr : pkgDescrs) {
                pkgDescr.removeObjectsGeneratedFromResource(resource);
            }
        }

        if (ruleBase != null) {
            ruleBase.removeObjectsGeneratedFromResource(resource);
        }

        return modified;
    }

    public void startPackageUpdate() {
        if (ruleBase != null) {
            ruleBase.lock();
        }
    }

    public void completePackageUpdate() {
        if (ruleBase != null) {
            ruleBase.unlock();
        }
    }

    public void setAllRuntimesDirty(Collection<String> packages) {
        if (ruleBase != null) {
            for (String pkgName : packages) {
                Package pkg = ruleBase.getPackage(pkgName);
                if (pkg != null) {
                    pkg.getDialectRuntimeRegistry().getDialectData("java").setDirty(true);
                }
            }
        }
    }

    public void rewireClassObjectTypes(Collection<String> packages) {
        if (ruleBase != null) {
            for (String pkgName : packages) {
                Package pkg = ruleBase.getPackage(pkgName);
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
}
