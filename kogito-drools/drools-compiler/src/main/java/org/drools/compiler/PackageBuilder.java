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

package org.drools.compiler;

import static org.drools.core.util.BitMaskUtil.isSet;

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

import org.drools.ChangeSet;
import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassFieldAccessor;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.TypeResolver;
import org.drools.base.evaluators.TimeIntervalParser;
import org.drools.base.mvel.MVELCompileable;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.KnowledgeBuilderResult;
import org.drools.builder.KnowledgeBuilderResults;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.builder.ResultSeverity;
import org.drools.builder.conf.PropertySpecificOption;
import org.drools.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.common.InternalRuleBase;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.compiler.xml.XmlPackageReader;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.DeepCloneable;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.StringUtils;
import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definition.type.ClassReactive;
import org.drools.definition.type.FactField;
import org.drools.definition.type.Modifies;
import org.drools.definition.type.Position;
import org.drools.definition.type.PropertyReactive;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.factmodel.AnnotationDefinition;
import org.drools.factmodel.ClassBuilder;
import org.drools.factmodel.ClassBuilderFactory;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.EnumClassDefinition;
import org.drools.factmodel.EnumLiteralDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.factmodel.traits.Thing;
import org.drools.factmodel.traits.Trait;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.factmodel.traits.TraitRegistry;
import org.drools.factmodel.traits.Traitable;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.facttemplates.FactTemplateImpl;
import org.drools.facttemplates.FieldTemplate;
import org.drools.facttemplates.FieldTemplateImpl;
import org.drools.io.Resource;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.DescrResource;
import org.drools.io.impl.ReaderResource;
import org.drools.io.internal.InternalResource;
import org.drools.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.lang.descr.AnnotationDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EnumDeclarationDescr;
import org.drools.lang.descr.EnumLiteralDescr;
import org.drools.lang.descr.EntryPointDeclarationDescr;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.lang.descr.WindowDeclarationDescr;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.lang.descr.QualifiedName;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.rule.Function;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.WindowDeclaration;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.dialect.DialectError;
import org.drools.runtime.pipeline.impl.DroolsJaxbHelperProviderImpl;
import org.drools.runtime.rule.Activation;
import org.drools.spi.InternalReadAccessor;
import org.drools.type.DateFormats;
import org.drools.type.DateFormatsImpl;
import org.drools.util.CompositeClassLoader;
import org.drools.xml.XmlChangeSetReader;
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
public class PackageBuilder implements DeepCloneable<PackageBuilder> {

    private final Map<String, PackageRegistry>       pkgRegistryMap;

    private List<KnowledgeBuilderResult>             results;

    private final PackageBuilderConfiguration        configuration;

    public static final RuleBuilder                  ruleBuilder       = new RuleBuilder();

    /**
     * Optional RuleBase for incremental live building
     */
    private ReteooRuleBase                           ruleBase;

    /**
     * default dialect
     */
    private final String                             defaultDialect;

    private CompositeClassLoader                     rootClassLoader;

    private final Map<String, Class<?>>              globals;

    private Resource                                 resource;

    private List<DSLTokenizedMappingFile>            dslFiles;

    private TimeIntervalParser                       timeParser;

    protected DateFormats                            dateFormats;

    private final ProcessBuilder                     processBuilder;

    private IllegalArgumentException                 processBuilderCreationFailure;

    private PMMLCompiler                             pmmlCompiler;

    private final Map<String, TypeDeclaration>       builtinTypes;

    private Map<String, TypeDeclaration>             cacheTypes;

    //This list of package level attributes is initialised with the PackageDescr's attributes added to the builder.
    //The package level attributes are inherited by individual rules not containing explicit overriding parameters.
    //The map is keyed on the PackageDescr's namespace and contains a map of AttributeDescr's keyed on the
    //AttributeDescr's name.
    private final Map<String, Map<String, AttributeDescr>> packageAttributes = new HashMap<String, Map<String, AttributeDescr>>();

    //PackageDescrs' list of ImportDescrs are kept identical as subsequent PackageDescrs are added.
    private final Map<String, List<PackageDescr>>    packages          = new HashMap<String, List<PackageDescr>>();

    private final Set<String>                        generatedTypes    = new HashSet<String>();

    private final Stack<List<Resource>>              buildResources    = new Stack<List<Resource>>();

    /**
     * Use this when package is starting from scratch.
     */
    public PackageBuilder() {
        this( (RuleBase) null,
              null );
    }

    /**
     * This will allow you to merge rules into this pre existing package.
     */

    public PackageBuilder( final Package pkg ) {
        this( pkg,
              null );
    }

    public PackageBuilder( final RuleBase ruleBase ) {
        this( ruleBase,
              null );
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
    public PackageBuilder( final PackageBuilderConfiguration configuration ) {
        this( (RuleBase) null,
              configuration );
    }

    public PackageBuilder( Package pkg,
            PackageBuilderConfiguration configuration ) {
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
        this.rootClassLoader.addClassLoader( getClass().getClassLoader() );

        this.defaultDialect = this.configuration.getDefaultDialect();

        this.pkgRegistryMap = new LinkedHashMap<String, PackageRegistry>();
        this.results = new ArrayList<KnowledgeBuilderResult>();

        PackageRegistry pkgRegistry = new PackageRegistry( this,
                                                           pkg );
        pkgRegistry.setDialect( this.defaultDialect );
        this.pkgRegistryMap.put( pkg.getName(),
                                 pkgRegistry );

        globals = new HashMap<String, Class<?>>();

        processBuilder = createProcessBuilder();

        builtinTypes = new HashMap<String, TypeDeclaration>();
        initBuiltinTypeDeclarations();
    }

    public PackageBuilder( RuleBase ruleBase,
            PackageBuilderConfiguration configuration ) {
        if (configuration == null) {
            this.configuration = new PackageBuilderConfiguration();
        } else {
            this.configuration = configuration;
        }

        if (ruleBase != null) {
            this.rootClassLoader = ( (InternalRuleBase) ruleBase ).getRootClassLoader();
        } else {
            this.rootClassLoader = this.configuration.getClassLoader();
        }

        this.rootClassLoader.addClassLoader(getClass().getClassLoader());

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

        return clone;
    }

    private void initBuiltinTypeDeclarations() {
        TypeDeclaration colType = new TypeDeclaration( "Collection" );
        colType.setTypesafe( false );
        colType.setTypeClass( Collection.class );
        builtinTypes.put( "java.util.Collection",
                          colType );

        TypeDeclaration mapType = new TypeDeclaration( "Map" );
        mapType.setTypesafe( false );
        mapType.setTypeClass( Map.class );
        builtinTypes.put( "java.util.Map",
                          mapType );

        TypeDeclaration activationType = new TypeDeclaration( "Activation" );
        activationType.setTypesafe( false );
        activationType.setTypeClass( Activation.class );
        builtinTypes.put( Activation.class.getCanonicalName(),
                          activationType );

        TypeDeclaration thingType = new TypeDeclaration( Thing.class.getName() );
        thingType.setKind( TypeDeclaration.Kind.TRAIT );
        thingType.setTypeClass( Thing.class );
        builtinTypes.put( Thing.class.getCanonicalName(),
                          thingType );
        ClassDefinition def = new ClassDefinition();
        def.setClassName( thingType.getTypeClass().getName() );
        def.setDefinedClass( Thing.class );
        TraitRegistry.getInstance().addTrait( def );

    }

    private ProcessBuilder createProcessBuilder() {
        try {
            return ProcessBuilderFactory.newProcessBuilder( this );
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
    public void addPackageFromDrl( final Reader reader ) throws DroolsParserException, IOException {
        addPackageFromDrl( reader, new ReaderResource( reader, ResourceType.DRL ) );
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
    public void addPackageFromDrl( final Reader reader, final Resource sourceResource ) throws DroolsParserException, IOException {
        this.resource = sourceResource;
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( reader );
        this.results.addAll( parser.getErrors() );
        if (pkg == null) {
            this.results.add( new ParserError( sourceResource, "Parser returned a null Package", 0, 0 ) );
        }

        if (!parser.hasErrors()) {
            addPackage( pkg );
        }
        this.resource = null;
    }

    public void addPackageFromDecisionTable( Resource resource, ResourceConfiguration configuration ) throws DroolsParserException, IOException {
        this.resource = resource;
        addPackage( decisionTableToPackageDescr(resource, configuration) );
        this.resource = null;
    }

    PackageDescr decisionTableToPackageDescr(Resource resource, ResourceConfiguration configuration) throws DroolsParserException, IOException {
        DecisionTableConfiguration dtableConfiguration = (DecisionTableConfiguration) configuration;
        String string = DecisionTableFactory.loadFromInputStream( resource.getInputStream(), dtableConfiguration );

        DrlParser parser = new DrlParser();
        PackageDescr pkg = parser.parse( new StringReader( string ) );
        this.results.addAll( parser.getErrors() );
        if (pkg == null) {
            this.results.add( new ParserError( resource, "Parser returned a null Package", 0, 0 ) );
        }
        return parser.hasErrors() ? null : pkg;
    }

    public void addPackageFromDrl( Resource resource ) throws DroolsParserException, IOException {
        this.resource = resource;
        addPackage( drlToPackageDescr(resource) );
        this.resource = null;
    }

    PackageDescr drlToPackageDescr(Resource resource) throws DroolsParserException, IOException {
        PackageDescr pkg;
        boolean hasErrors = false;
        if (resource instanceof DescrResource) {
            pkg = (PackageDescr) ( (DescrResource) resource ).getDescr();
        } else {
            final DrlParser parser = new DrlParser();
            pkg = parser.parse( resource );
            this.results.addAll( parser.getErrors() );
            if (pkg == null) {
                this.results.add( new ParserError( resource, "Parser returned a null Package", 0, 0 ) );
            }
            hasErrors = parser.hasErrors();
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
    public void addPackageFromXml( final Reader reader ) throws DroolsParserException, IOException {
        this.resource = new ReaderResource( reader, ResourceType.XDRL );
        final XmlPackageReader xmlReader = new XmlPackageReader( this.configuration.getSemanticModules() );
        xmlReader.getParser().setClassLoader( this.rootClassLoader );

        try {
            xmlReader.read( reader );
        } catch (final SAXException e) {
            throw new DroolsParserException( e.toString(),
                                             e.getCause() );
        }

        addPackage( xmlReader.getPackageDescr() );
        this.resource = null;
    }

    public void addPackageFromXml( final Resource resource ) throws DroolsParserException, IOException {
        this.resource = resource;
        addPackage( xmlToPackageDescr(resource) );
        this.resource = null;
    }

    PackageDescr xmlToPackageDescr(Resource resource) throws DroolsParserException, IOException {
        final XmlPackageReader xmlReader = new XmlPackageReader( this.configuration.getSemanticModules() );
        xmlReader.getParser().setClassLoader( this.rootClassLoader );

        try {
            xmlReader.read( resource.getReader() );
        } catch (final SAXException e) {
            throw new DroolsParserException( e.toString(),
                    e.getCause() );
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
    public void addPackageFromDrl( final Reader source, final Reader dsl ) throws DroolsParserException, IOException {
        this.resource = new ReaderResource( source, ResourceType.DSLR );

        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( source, dsl );
        this.results.addAll( parser.getErrors() );
        if (!parser.hasErrors()) {
            addPackage( pkg );
        }
        this.resource = null;
    }

    public void addPackageFromDslr( final Resource resource ) throws DroolsParserException, IOException {
        this.resource = resource;
        addPackage( dslrToPackageDescr(resource) );
        this.resource = null;
    }

    PackageDescr dslrToPackageDescr(Resource resource) throws DroolsParserException {
        boolean hasErrors;
        PackageDescr pkg;

        DrlParser parser = new DrlParser();
        DefaultExpander expander = getDslExpander();

        try {
            if (expander == null) {
                expander = new DefaultExpander();
            }
            String str = expander.expand( resource.getReader() );
            if (expander.hasErrors()) {
                this.results.addAll( expander.getErrors() );
            }

            pkg = parser.parse( str );
            this.results.addAll( parser.getErrors() );
            hasErrors = parser.hasErrors();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        return hasErrors ? null : pkg;
    }

    public void addPackageFromBrl( final Resource resource ) throws DroolsParserException {
        this.resource = resource;
        try {
            addPackage( brlToPackageDescr(resource) );
        } catch (Exception e) {
            throw new DroolsParserException( e );
        } finally {
            this.resource = null;
        }
    }

    PackageDescr brlToPackageDescr(Resource resource) throws Exception {
        BusinessRuleProvider provider = BusinessRuleProviderFactory.getInstance().getProvider();
        Reader knowledge = provider.getKnowledgeReader( resource );

        DrlParser parser = new DrlParser();

        if (provider.hasDSLSentences()) {
            DefaultExpander expander = getDslExpander();

            if (null != expander) {
                knowledge = new StringReader( expander.expand( knowledge ) );
                if (expander.hasErrors())
                    this.results.addAll( expander.getErrors() );
            }
        }

        PackageDescr pkg = parser.parse( knowledge );
        if (parser.hasErrors()) {
            this.results.addAll( parser.getErrors() );
            return null;
        }
        return pkg;
    }

    public void addDsl( Resource resource ) throws IOException {
        this.resource = resource;

        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        if (!file.parseAndLoad( resource.getReader() )) {
            this.results.addAll( file.getErrors() );
        }
        if (this.dslFiles == null) {
            this.dslFiles = new ArrayList<DSLTokenizedMappingFile>();
        }
        this.dslFiles.add( file );

        this.resource = null;
    }

    /**
     * Add a ruleflow (.rfm) asset to this package.
     */
    public void addRuleFlow( Reader processSource ) {
        addProcessFromXml( processSource );
    }

    public void addProcessFromXml( Resource resource ) {
        if (processBuilder == null) {
            throw new RuntimeException( "Unable to instantiate a process builder", processBuilderCreationFailure );
        }

        if ( ResourceType.DRF.equals( ( (InternalResource) resource ).getResourceType() ) ) {
            this.results.add( new DeprecatedResourceTypeWarning(resource, "RF") );
        }

        this.resource = resource;

        try {
            this.results.addAll( processBuilder.addProcessFromXml( resource ) );
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            this.results.add( new ProcessLoadError( resource, "Unable to load process.", e ) );
        }
        this.results = getResults( this.results );
        this.resource = null;
    }

    public void addProcessFromXml( Reader processSource ) {
        addProcessFromXml( new ReaderResource( processSource, ResourceType.DRF ) );
    }

    public void addKnowledgeResource( Resource resource,
            ResourceType type,
            ResourceConfiguration configuration ) {
        try {
            ( (InternalResource) resource ).setResourceType( type );
            if (ResourceType.DRL.equals( type )) {
                addPackageFromDrl( resource );
            } else if (ResourceType.DESCR.equals( type )) {
                addPackageFromDrl( resource );
            } else if (ResourceType.DSLR.equals( type )) {
                addPackageFromDslr( resource );
            } else if (ResourceType.DSL.equals( type )) {
                addDsl( resource );
            } else if (ResourceType.XDRL.equals( type )) {
                addPackageFromXml( resource );
            } else if (ResourceType.BRL.equals( type )) {
                addPackageFromBrl( resource );
            } else if (ResourceType.DRF.equals( type )) {
                addProcessFromXml( resource );
            } else if (ResourceType.BPMN2.equals( type )) {
                BPMN2ProcessFactory.configurePackageBuilder( this );
                addProcessFromXml( resource );
            } else if (ResourceType.DTABLE.equals( type )) {
                addPackageFromDecisionTable( resource, configuration );
            } else if (ResourceType.PKG.equals( type )) {
                addPackageFromInputStream(resource);
            } else if (ResourceType.CHANGE_SET.equals( type )) {
                addPackageFromChangeSet(resource);
            } else if (ResourceType.XSD.equals( type )) {
                addPackageFromXSD(resource, (JaxbConfigurationImpl) configuration);
            } else if (ResourceType.PMML.equals( type )) {
                addPackageFromPMML(resource, type, configuration);
            } else {
                addPackageForExternalType(resource, type, configuration);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    void addPackageForExternalType(Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        ResourceTypeBuilder builder = ResourceTypeBuilderRegistry.getInstance().getResourceTypeBuilder( type );
        if (builder != null) {
            builder.setPackageBuilder( this );
            builder.addKnowledgeResource( resource,
                                          type,
                                          configuration );
        } else {
            throw new RuntimeException( "Unknown resource type: " + type );
        }
    }

    void addPackageFromPMML(Resource resource, ResourceType type, ResourceConfiguration configuration) throws IOException {
        PMMLCompiler compiler = getPMMLCompiler();
        if (compiler != null) {

            String theory = compiler.compile( resource.getInputStream(),
                                              getPackageRegistry() );

            addKnowledgeResource( new ByteArrayResource( theory.getBytes() ),
                                  ResourceType.DRL,
                                  configuration );
        } else {
            throw new RuntimeException( "Unknown resource type: " + type );
        }
    }

    void addPackageFromXSD(Resource resource, JaxbConfigurationImpl configuration) throws IOException {
        String[] classes = DroolsJaxbHelperProviderImpl.addXsdModel(resource,
                this,
                configuration.getXjcOpts(),
                configuration.getSystemId());
        for (String cls : classes) {
            configuration.getClasses().add( cls );
        }
    }

    void addPackageFromChangeSet(Resource resource) throws SAXException, IOException {
        XmlChangeSetReader reader = new XmlChangeSetReader( this.configuration.getSemanticModules() );
        if (resource instanceof ClassPathResource) {
            reader.setClassLoader( ( (ClassPathResource) resource ).getClassLoader(),
                                   ( (ClassPathResource) resource ).getClazz() );
        } else {
            reader.setClassLoader( this.configuration.getClassLoader(),
                                   null );
        }
        ChangeSet changeSet = reader.read( resource.getReader() );
        if (changeSet == null) {
            // @TODO should log an error
        }
        for (Resource nestedResource : changeSet.getResourcesAdded()) {
            InternalResource iNestedResourceResource = (InternalResource) nestedResource;
            if (iNestedResourceResource.isDirectory()) {
                for (Resource childResource : iNestedResourceResource.listResources()) {
                    if (( (InternalResource) childResource ).isDirectory()) {
                        continue; // ignore sub directories
                    }
                    ( (InternalResource) childResource ).setResourceType( iNestedResourceResource.getResourceType() );
                    addKnowledgeResource( childResource,
                                          iNestedResourceResource.getResourceType(),
                                          iNestedResourceResource.getConfiguration() );
                }
            } else {
                addKnowledgeResource( iNestedResourceResource,
                                      iNestedResourceResource.getResourceType(),
                                      iNestedResourceResource.getConfiguration() );
            }
        }
    }

    void addPackageFromInputStream(final Resource resource) throws IOException, ClassNotFoundException {
        InputStream is = resource.getInputStream();
        Object object = DroolsStreamUtils.streamIn(is, this.configuration.getClassLoader());
        is.close();
        if( object instanceof Collection ) {
            // KnowledgeBuilder API
            @SuppressWarnings("unchecked")
            Collection<KnowledgePackage> pkgs = (Collection<KnowledgePackage>) object;             
            for( KnowledgePackage kpkg : pkgs ) {
                addPackage( ((KnowledgePackageImp)kpkg).pkg );
            }
        } else if( object instanceof KnowledgePackageImp ) {
            // KnowledgeBuilder API
            KnowledgePackageImp kpkg = (KnowledgePackageImp) object;
            addPackage( kpkg.pkg );
        } else if( object instanceof Package ) {
            // Old Drools 4 API
            Package pkg = (Package) object;             
            addPackage( pkg );
        } else if( object instanceof Package[] )  {
            // Old Drools 4 API
            Package[] pkgs = (Package[]) object;             
            for( Package pkg : pkgs ) {
                addPackage( pkg );
            }
        } else {
            results.add( new DroolsError( resource ) {
                @Override
                public String getMessage() {
                    return "Unknown binary format trying to load resource "+resource.toString();
                }
                @Override
                public int[] getLines() {
                    return new int[0];
                }
            } );
        }
    }

    /**
     * This adds a package from a Descr/AST This will also trigger a compile, if
     * there are any generated classes to compile of course.
     */
    public void addPackage( final PackageDescr packageDescr ) {
        PackageRegistry pkgRegistry = initPackageRegistry(packageDescr);
        if (pkgRegistry == null) {
            return;
        }

        // merge into existing package
        mergePackage(pkgRegistry, packageDescr);

        compileAllRules(packageDescr, pkgRegistry);
    }

    void compileAllRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        pkgRegistry.setDialect( getPackageDialect(packageDescr) );

        // only try to compile if there are no parse errors
        if (!hasErrors()) {
            compileRules(packageDescr, pkgRegistry);
        }

        compileAll();
        try {
            reloadAll();
        } catch (Exception e) {
            this.results.add( new DialectError( null, "Unable to wire compiled classes, probably related to compilation failures:" + e.getMessage() ) );
        }
        updateResults();

        // iterate and compile
        if (! hasErrors() && this.ruleBase != null) {
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                pkgRegistry = this.pkgRegistryMap.get(ruleDescr.getNamespace());
                this.ruleBase.addRule(pkgRegistry.getPackage(), pkgRegistry.getPackage().getRule(ruleDescr.getName()));
            }
        }
    }

    PackageRegistry initPackageRegistry(PackageDescr packageDescr) {
        if (packageDescr == null) {
            return null;
        }

        //Derive namespace
        if (isEmpty( packageDescr.getNamespace() )) {
            packageDescr.setNamespace( this.configuration.getDefaultPackageName() );
        }
        validateUniqueRuleNames( packageDescr );
        if (!checkNamespace( packageDescr.getNamespace() )) {
            return null;
        }

        initPackage(packageDescr);

        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( packageDescr.getNamespace() );
        if (pkgRegistry == null) {
            // initialise the package and namespace if it hasn't been used before
            pkgRegistry = newPackage( packageDescr );
        }

        return pkgRegistry;
    }

    private void compileRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        List<FunctionDescr> functions = packageDescr.getFunctions();
        if (!functions.isEmpty()) {

            for (FunctionDescr functionDescr : functions) {
                if (isEmpty(functionDescr.getNamespace())) {
                    // make sure namespace is set on components
                    functionDescr.setNamespace(packageDescr.getNamespace());
                }

                // make sure functions are compiled using java dialect
                functionDescr.setDialect("java");

                preCompileAddFunction(functionDescr);
            }

            // iterate and compile
            for (FunctionDescr functionDescr : functions) {
                // inherit the dialect from the package
                addFunction(functionDescr);
            }

            // We need to compile all the functions now, so scripting
            // languages like mvel can find them
            compileAll();

            for (FunctionDescr functionDescr : functions) {
                postCompileAddFunction( functionDescr );
            }
        }

        // iterate and compile
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
            addRule(ruleDescr);
        }
    }

    private void initPackage(PackageDescr packageDescr) {
        //Gather all imports for all PackageDescrs for the current package and replicate into
        //all PackageDescrs for the current package, thus maintaining a complete list of
        //ImportDescrs for all PackageDescrs for the current package.
        List<PackageDescr> packageDescrsForPackage = packages.get( packageDescr.getName() );
        if (packageDescrsForPackage == null) {
            packageDescrsForPackage = new ArrayList<PackageDescr>();
            packages.put( packageDescr.getName(),
                          packageDescrsForPackage );
        }
        packageDescrsForPackage.add( packageDescr );
        Set<ImportDescr> imports = new HashSet<ImportDescr>();
        for (PackageDescr pd : packageDescrsForPackage) {
            imports.addAll( pd.getImports() );
        }
        for (PackageDescr pd : packageDescrsForPackage) {
            pd.getImports().clear();
            pd.addAllImports(imports);
        }

        //Copy package level attributes for inclusion on individual rules
        if (packageDescr.getAttributes().size() > 0) {
            Map<String, AttributeDescr> pkgAttributes = packageAttributes.get( packageDescr.getNamespace() );
            if (pkgAttributes == null) {
                pkgAttributes = new HashMap<String, AttributeDescr>();
                this.packageAttributes.put( packageDescr.getNamespace(),
                                            pkgAttributes );
            }
            for (AttributeDescr attr : packageDescr.getAttributes()) {
                pkgAttributes.put( attr.getName(),
                                   attr );
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
    private boolean checkNamespace( String newName ) {
        if (this.configuration == null)
            return true;
        if (( !this.pkgRegistryMap.isEmpty() ) && ( !this.pkgRegistryMap.containsKey( newName ) )) {
            return this.configuration.isAllowMultipleNamespaces();
        }
        return true;
    }

    public boolean isEmpty( String string ) {
        return ( string == null || string.trim().length() == 0 );
    }

    public void updateResults() {
        // some of the rules and functions may have been redefined
        this.results = getResults( this.results );
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

    private List<KnowledgeBuilderResult> getResults( List<KnowledgeBuilderResult> results ) {
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
            results = pkgRegistry.getDialectCompiletimeRegistry().addResults( results );
        }
        return results;
    }

    public synchronized void addPackage( final Package newPkg ) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( newPkg.getName() );
        Package pkg = null;
        if (pkgRegistry != null) {
            pkg = pkgRegistry.getPackage();
        }

        if (pkg == null) {
            PackageDescr packageDescr = new PackageDescr( newPkg.getName() );
            pkgRegistry = newPackage( packageDescr );
            mergePackage( this.pkgRegistryMap.get( packageDescr.getNamespace() ), packageDescr );
            pkg = pkgRegistry.getPackage();
        }

        // first merge anything related to classloader re-wiring
        pkg.getDialectRuntimeRegistry().merge( newPkg.getDialectRuntimeRegistry(),
                                               this.rootClassLoader );
        if (newPkg.getFunctions() != null) {
            for (Map.Entry<String, Function> entry : newPkg.getFunctions().entrySet()) {
                if (pkg.getFunctions().containsKey( entry.getKey() )) {
                    this.results.add( new DuplicateFunction( entry.getValue(),
                                                             this.configuration ) );
                }
                pkg.addFunction( entry.getValue() );
            }
        }
        pkg.getClassFieldAccessorStore().merge( newPkg.getClassFieldAccessorStore() );
        pkg.getDialectRuntimeRegistry().onBeforeExecute();

        // we have to do this before the merging, as it does some classloader resolving
        TypeDeclaration lastType = null;
        try {
            // Resolve the class for the type declaation
            if (newPkg.getTypeDeclarations() != null) {
                // add type declarations
                for (TypeDeclaration type : newPkg.getTypeDeclarations().values()) {
                    lastType = type;
                    type.setTypeClass( this.rootClassLoader.loadClass( type.getTypeClassName() ) );
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeDroolsException( "unable to resolve Type Declaration class '" + lastType.getTypeName() +
                                              "'" );
        }

        // now merge the new package into the existing one
        mergePackage( pkg,
                      newPkg );

    }

    /**
     * Merge a new package with an existing package. Most of the work is done by
     * the concrete implementations, but this class does some work (including
     * combining imports, compilation data, globals, and the actual Rule objects
     * into the package).
     */
    private void mergePackage( final Package pkg,
            final Package newPkg ) {
        // Merge imports
        final Map<String, ImportDeclaration> imports = pkg.getImports();
        imports.putAll( newPkg.getImports() );

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
                    if (globals.containsKey( identifier ) && !globals.get( identifier ).equals( type )) {
                        throw new PackageIntegrationException( pkg );
                    } else {
                        pkg.addGlobal( identifier,
                                       this.rootClassLoader.loadClass( type ) );
                        // this isn't a package merge, it's adding to the rulebase, but I've put it here for convenience
                        this.globals.put( identifier,
                                          this.rootClassLoader.loadClass( type ) );
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeDroolsException( "Unable to resolve class '" + lastType + "'" );
        }

        // merge the type declarations
        if (newPkg.getTypeDeclarations() != null) {
            // add type declarations
            for (TypeDeclaration type : newPkg.getTypeDeclarations().values()) {
                // @TODO should we allow overrides? only if the class is not in use.
                if (!pkg.getTypeDeclarations().containsKey( type.getTypeName() )) {
                    // add to package list of type declarations
                    pkg.addTypeDeclaration( type );
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

    private void validateUniqueRuleNames( final PackageDescr packageDescr ) {
        final Set<String> names = new HashSet<String>();
        PackageRegistry packageRegistry = this.pkgRegistryMap.get( packageDescr.getNamespace() );
        Package pkg = null;
        if (packageRegistry != null) {
            pkg = packageRegistry.getPackage();
        }
        for (final RuleDescr rule : packageDescr.getRules()) {
            final String name = rule.getName();
            if (names.contains( name )) {
                this.results.add( new ParserError( rule.getResource(),
                                                   "Duplicate rule name: " + name,
                                                   rule.getLine(),
                                                   rule.getColumn(),
                                                   packageDescr.getNamespace() ) );
            }
            if (pkg != null && pkg.getRule( name ) != null) {
                this.results.add( new DuplicateRule( rule,
                                                     packageDescr,
                                                     this.configuration ) );
            }
            names.add( name );
        }
    }

    private PackageRegistry newPackage( final PackageDescr packageDescr ) {
        Package pkg;
        if (this.ruleBase == null || ( pkg = this.ruleBase.getPackage( packageDescr.getName() ) ) == null) {
            // there is no rulebase or it does not define this package so define it
            pkg = new Package( packageDescr.getName() );
            pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( this.rootClassLoader ) );

            // if there is a rulebase then add the package.
            if (this.ruleBase != null) {
                // Must lock here, otherwise the assumption about addPackage/getPackage behavior below might be violated
                this.ruleBase.lock();
                try {
                    this.ruleBase.addPackage( pkg );
                    pkg = this.ruleBase.getPackage( packageDescr.getName() );
                } finally {
                    this.ruleBase.unlock();
                }
            } else {
                // the RuleBase will also initialise the
                pkg.getDialectRuntimeRegistry().onAdd( this.rootClassLoader );
            }
        }

        PackageRegistry pkgRegistry = new PackageRegistry( this,
                                                           pkg );

        // add default import for this namespace
        pkgRegistry.addImport( new ImportDescr( packageDescr.getNamespace() + ".*" ) );

        this.pkgRegistryMap.put( packageDescr.getName(),
                                 pkgRegistry );

        return pkgRegistry;
    }

    private void mergePackage( PackageRegistry pkgRegistry, PackageDescr packageDescr ) {
        for (final ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.addImport( importDescr );
        }

        processEntryPointDeclarations(pkgRegistry, packageDescr);

        // process types in 2 steps to deal with circular and recursive declarations
        processUnresolvedTypes( pkgRegistry, processTypeDeclarations( pkgRegistry, packageDescr ) );

        processOtherDeclarations( pkgRegistry, packageDescr );
    }

    void processOtherDeclarations(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        processWindowDeclarations(pkgRegistry, packageDescr);
        processFunctions(pkgRegistry, packageDescr);
        processGlobals(pkgRegistry, packageDescr);

        // need to reinsert this to ensure that the package is the first/last one in the ordered map
        // this feature is exploited by the knowledgeAgent
        this.pkgRegistryMap.remove( packageDescr.getName() );
        this.pkgRegistryMap.put( packageDescr.getName(), pkgRegistry );
    }

    private void processGlobals(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        for (final GlobalDescr global : packageDescr.getGlobals()) {
            final String identifier = global.getIdentifier();
            String className = global.getType();

            // JBRULES-3039: can't handle type name with generic params
            while (className.indexOf( '<' ) >= 0) {
                className = className.replaceAll( "<[^<>]+?>", "" );
            }

            Class<?> clazz;
            try {
                clazz = pkgRegistry.getTypeResolver().resolveType( className );
                pkgRegistry.getPackage().addGlobal( identifier,
                                                    clazz );
                this.globals.put( identifier,
                                  clazz );
            } catch (final ClassNotFoundException e) {
                this.results.add( new GlobalError( global ) );
                e.printStackTrace();
            }
        }
    }

    private void processFunctions(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        for (FunctionDescr function : packageDescr.getFunctions()) {
            Function existingFunc = pkgRegistry.getPackage().getFunctions().get( function.getName() );
            if (existingFunc != null && function.getNamespace().equals( existingFunc.getNamespace() )) {
                this.results.add(
                        new DuplicateFunction( function,
                                               this.configuration ) );
            }
        }

        for (final FunctionImportDescr functionImport : packageDescr.getFunctionImports()) {
            String importEntry = functionImport.getTarget();
            pkgRegistry.addStaticImport( functionImport );
            pkgRegistry.getPackage().addStaticImport( importEntry );
        }
    }

    void processUnresolvedTypes(PackageRegistry pkgRegistry, List<TypeDefinition> unresolvedTypeDefinitions) {
        if (unresolvedTypeDefinitions != null) {
            for (TypeDefinition typeDef : unresolvedTypeDefinitions) {
                processTypeFields(pkgRegistry, typeDef.typeDescr, typeDef.type, false);
            }
        }
    }

    public TypeDeclaration getAndRegisterTypeDeclaration( Class<?> cls, String packageName ) {
        if (cls.isPrimitive() || cls.isArray())
            return null;
        TypeDeclaration typeDeclaration = getCachedTypeDeclaration( cls );
        if (typeDeclaration != null) {
            return typeDeclaration;
        }
        typeDeclaration = getExistingTypeDeclaration( cls );
        if (typeDeclaration != null) {
            initTypeDeclaration( cls, typeDeclaration );
            return typeDeclaration;
        }

        typeDeclaration = createTypeDeclarationForBean( cls );
        initTypeDeclaration( cls,
                             typeDeclaration );
        PackageRegistry packageRegistry = pkgRegistryMap.get( packageName );
        if (packageRegistry != null) {
            packageRegistry.getPackage().addTypeDeclaration( typeDeclaration );
        }
        return typeDeclaration;
    }

    public TypeDeclaration getTypeDeclaration( Class<?> cls ) {
        if (cls.isPrimitive() || cls.isArray())
            return null;

        // If this class has already been accessed, it'll be in the cache
        TypeDeclaration tdecl = getCachedTypeDeclaration( cls );
        return tdecl != null ? tdecl : createTypeDeclaration( cls );
    }

    private TypeDeclaration createTypeDeclaration( Class<?> cls ) {
        TypeDeclaration typeDeclaration = getExistingTypeDeclaration( cls );

        if (typeDeclaration == null) {
            typeDeclaration = createTypeDeclarationForBean( cls );
        }

        initTypeDeclaration( cls,
                             typeDeclaration );
        return typeDeclaration;
    }

    private TypeDeclaration getCachedTypeDeclaration( Class<?> cls ) {
        if (this.cacheTypes == null) {
            this.cacheTypes = new HashMap<String, TypeDeclaration>();
            return null;
        } else {
            return cacheTypes.get( cls.getName() );
        }
    }

    private TypeDeclaration getExistingTypeDeclaration( Class<?> cls ) {
        // Check if we are in the built-ins
        TypeDeclaration typeDeclaration = this.builtinTypes.get( ( cls.getName() ) );
        if (typeDeclaration == null) {
            // No built-in
            // Check if there is a user specified typedeclr
            PackageRegistry pkgReg = this.pkgRegistryMap.get( ClassUtils.getPackage( cls ) );
            if (pkgReg != null) {
                String className = cls.getName();
                String typeName = className.substring( className.lastIndexOf(".") + 1 );
                typeDeclaration = pkgReg.getPackage().getTypeDeclaration( typeName );
            }
        }
        return typeDeclaration;
    }

    private void initTypeDeclaration( Class<?> cls, TypeDeclaration typeDeclaration ) {
        // build up a set of all the super classes and interfaces
        Set<TypeDeclaration> tdecls = new LinkedHashSet<TypeDeclaration>();

        tdecls.add( typeDeclaration );
        buildTypeDeclarations( cls,
                               tdecls );

        // Iterate and for each typedeclr assign it's value if it's not already set
        // We start from the rear as those are the furthest away classes and interfaces
        TypeDeclaration[] tarray = tdecls.toArray( new TypeDeclaration[tdecls.size()] );
        for (int i = tarray.length - 1; i >= 0; i--) {
            TypeDeclaration currentTDecl = tarray[i];
            if (!isSet( typeDeclaration.getSetMask(),
                        TypeDeclaration.ROLE_BIT ) && isSet( currentTDecl.getSetMask(),
                                                             TypeDeclaration.ROLE_BIT )) {
                typeDeclaration.setRole( currentTDecl.getRole() );
            }
            if (!isSet( typeDeclaration.getSetMask(),
                        TypeDeclaration.FORMAT_BIT ) && isSet( currentTDecl.getSetMask(),
                                                               TypeDeclaration.FORMAT_BIT )) {
                typeDeclaration.setFormat( currentTDecl.getFormat() );
            }
            if (!isSet( typeDeclaration.getSetMask(),
                        TypeDeclaration.TYPESAFE_BIT ) && isSet( currentTDecl.getSetMask(),
                                                                 TypeDeclaration.TYPESAFE_BIT )) {
                typeDeclaration.setTypesafe( currentTDecl.isTypesafe() );
            }
        }

        this.cacheTypes.put( cls.getName(),
                             typeDeclaration );
    }

    private TypeDeclaration createTypeDeclarationForBean( Class<?> cls ) {
        String typeName = cls.getName();
        int lastDot = typeName.lastIndexOf( '.' );
        typeName = lastDot >= 0 ? typeName.substring( lastDot + 1 ) : typeName;
        TypeDeclaration typeDeclaration = new TypeDeclaration( typeName );
        typeDeclaration.setTypeClass( cls );

        PropertySpecificOption propertySpecificOption = configuration.getOption(PropertySpecificOption.class);
        boolean propertySpecific = propertySpecificOption.isPropSpecific(cls.isAnnotationPresent(PropertyReactive.class),
                                                                         cls.isAnnotationPresent(ClassReactive.class));
        typeDeclaration.setPropertyReactive(propertySpecific);

        ClassDefinition clsDef = typeDeclaration.getTypeClassDef();
        if (clsDef == null) {
            clsDef = new ClassDefinition();
            if (typeDeclaration.isPropertyReactive()) {
                processModifiedProps( cls,
                                      clsDef );
            }
            processFieldsPosition( cls,
                                   clsDef );
            typeDeclaration.setTypeClassDef( clsDef );
        }

        return typeDeclaration;
    }

    private void processModifiedProps( Class<?> cls, ClassDefinition clsDef ) {
        for (Method method : cls.getDeclaredMethods()) {
            Modifies modifies = method.getAnnotation( Modifies.class );
            if (modifies != null) {
                String[] props = modifies.value();
                List<String> properties = new ArrayList<String>( props.length );
                for (String prop : props) {
                    properties.add( prop.trim() );
                }
                clsDef.addModifiedPropsByMethod( method,
                                                 properties );
            }
        }
    }

    private void processFieldsPosition( Class<?> cls, ClassDefinition clsDef ) {
        // it's a new type declaration, so generate the @Position for it
        Collection<Field> fields = new LinkedList<Field>();
        Class<?> tempKlass = cls;
        while (tempKlass != null && tempKlass != Object.class) {
            Collections.addAll(fields, tempKlass.getDeclaredFields());
            tempKlass = tempKlass.getSuperclass();
        }

        List<FieldDefinition> orderedFields = new ArrayList<FieldDefinition>( fields.size() );
        for (int i = 0; i < fields.size(); i++) {
            // as these could be set in any order, initialise first, to allow setting later.
            orderedFields.add( null );
        }

        for (Field fld : fields) {
            Position pos = fld.getAnnotation( Position.class );
            if (pos != null) {
                FieldDefinition fldDef = new FieldDefinition( fld.getName(),
                                                              fld.getType().getName() );
                fldDef.setIndex( pos.value() );
                orderedFields.set( pos.value(),
                                   fldDef );
            }
        }
        for (FieldDefinition fld : orderedFields) {
            if (fld != null) {
                // it's null if there is no @Position
                clsDef.addField( fld );
            }
        }
    }

    public void buildTypeDeclarations( Class<?> cls, Set<TypeDeclaration> tdecls ) {
        // Process current interfaces
        Class<?>[] intfs = cls.getInterfaces();
        for (Class<?> intf : intfs) {
            buildTypeDeclarationInterfaces( intf,
                                            tdecls );
        }

        // Process super classes and their interfaces
        cls = cls.getSuperclass();
        while ( cls != null && cls != Object.class ) {
            if (!buildTypeDeclarationInterfaces( cls,
                                                 tdecls )) {
                break;
            }
            cls = cls.getSuperclass();
        }

    }

    public boolean buildTypeDeclarationInterfaces( Class cls,
            Set<TypeDeclaration> tdecls ) {
        PackageRegistry pkgReg;
        TypeDeclaration tdecl;

        tdecl = this.builtinTypes.get( ( cls.getName() ) );
        if (tdecl == null) {
            pkgReg = this.pkgRegistryMap.get( ClassUtils.getPackage( cls ) );
            if (pkgReg != null) {
                tdecl = pkgReg.getPackage().getTypeDeclaration( cls.getSimpleName() );
            }
        }
        if (tdecl != null) {
            if (!tdecls.add( tdecl )) {
                return false; // the interface already exists, return to stop recursion
            }
        }

        Class<?>[] intfs = cls.getInterfaces();
        for (Class<?> intf : intfs) {
            pkgReg = this.pkgRegistryMap.get( ClassUtils.getPackage( intf ) );
            if (pkgReg != null) {
                tdecl = pkgReg.getPackage().getTypeDeclaration( intf.getSimpleName() );
            }
            if (tdecl != null) {
                tdecls.add( tdecl );
            }
        }

        for (Class<?> intf : intfs) {
            if (!buildTypeDeclarationInterfaces( intf,
                                                 tdecls )) {
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
    private String resolveType( String sup,
            PackageDescr packageDescr,
            PackageRegistry pkgRegistry ) {

        //look among imports
        for (ImportDescr id : packageDescr.getImports()) {
            if (id.getTarget().endsWith( "." + sup )) {
                //System.out.println("Replace supertype " + sup + " with full name " + id.getTarget());
                return id.getTarget();

            }
        }

        //look among local declarations
        if (pkgRegistry != null) {
            for (String declaredName : pkgRegistry.getPackage().getTypeDeclarations().keySet()) {
                if (declaredName.equals( sup ))
                    sup = pkgRegistry.getPackage().getTypeDeclaration( declaredName ).getTypeClass().getName();
            }
        }

        if ( (sup != null) && (!sup.contains( "." )) && (packageDescr.getNamespace() != null && packageDescr.getNamespace().length() > 0) ) {
            for ( AbstractClassTypeDeclarationDescr td : packageDescr.getClassAndEnumDeclarationDescrs() ) {
                if ( sup.equals( td.getTypeName() ) ) sup = packageDescr.getNamespace() + "." + sup;
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
    private void fillSuperType( TypeDeclarationDescr typeDescr,
            PackageDescr packageDescr ) {

        for ( QualifiedName qname : typeDescr.getSuperTypes() ) {
            String declaredSuperType = qname.getFullName();

            if (declaredSuperType != null) {
                int separator = declaredSuperType.lastIndexOf( "." );
                boolean qualified = separator > 0;
                // check if a simple name corresponds to a f.q.n.
                if (!qualified) {
                    declaredSuperType =
                                        resolveType( declaredSuperType,
                                                     packageDescr,
                                                     this.pkgRegistryMap.get( typeDescr.getNamespace() ) );

                    // sets supertype name and supertype package
                    separator = declaredSuperType.lastIndexOf( "." );
                    if (separator < 0) {
                        this.results.add( new TypeDeclarationError( typeDescr,
                                                                    "Cannot resolve supertype '" + declaredSuperType + "'") );
                        qname.setName( null );
                        qname.setNamespace( null );
                    } else {
                        qname.setName( declaredSuperType.substring( separator + 1 ) );
                        qname.setNamespace( declaredSuperType.substring( 0,
                                                                         separator ) );
                    }
                }
            }
        }
    }

    private void fillFieldTypes( AbstractClassTypeDeclarationDescr typeDescr,
                                 PackageDescr packageDescr ) {

        for (TypeFieldDescr field : typeDescr.getFields().values()) {
            String declaredType = field.getPattern().getObjectType();

            if (declaredType != null) {
                int separator = declaredType.lastIndexOf( "." );
                boolean qualified = separator > 0;
                // check if a simple name corresponds to a f.q.n.
                if (!qualified) {
                    declaredType =
                                   resolveType( declaredType,
                                                packageDescr,
                                                this.pkgRegistryMap.get( typeDescr.getNamespace() ) );

                    field.getPattern().setObjectType( declaredType );
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
    private boolean mergeInheritedFields( TypeDeclarationDescr typeDescr ) {

        if (typeDescr.getSuperTypes().isEmpty())
            return false;
        boolean merge = false;

        for ( QualifiedName qname : typeDescr.getSuperTypes() ) {
            String simpleSuperTypeName = qname.getName();
            String superTypePackageName = qname.getNamespace();
            String fullSuper = qname.getFullName();

            merge = merge || mergeInheritedFields( simpleSuperTypeName,
                                                   superTypePackageName,
                                                   fullSuper,
                                                   typeDescr );
        }

        return merge;
    }

    private boolean mergeInheritedFields( String simpleSuperTypeName,
            String superTypePackageName,
            String fullSuper,
            TypeDeclarationDescr typeDescr ) {

        Map<String, TypeFieldDescr> fieldMap = new LinkedHashMap<String, TypeFieldDescr>();

        boolean isSuperClassDeclared = true; //in the same package, or in a previous one
        boolean isSuperClassTagged = false;

        PackageRegistry registry = this.pkgRegistryMap.get( superTypePackageName );
        Package pack;
        if (registry != null) {
            pack = registry.getPackage();
        } else {
            // If there is no regisrty the type isn't a DRL-declared type, which is forbidden.
            // Avoid NPE JIRA-3041 when trying to access the registry. Avoid subsequent problems.
            this.results.add( new TypeDeclarationError( typeDescr, "Cannot extend supertype '" + fullSuper + "' (not a declared type)" ) );
            typeDescr.setType( null, null );
            return false;
        }

        // if a class is declared in DRL, its package can't be null? The default package is replaced by "defaultpkg"
        if (pack != null) {

            // look for the supertype declaration in available packages
            TypeDeclaration superTypeDeclaration = pack.getTypeDeclaration( simpleSuperTypeName );

            if (superTypeDeclaration != null) {
                ClassDefinition classDef = superTypeDeclaration.getTypeClassDef();
                // inherit fields
                for (FactField fld : classDef.getFields()) {
                    TypeFieldDescr inheritedFlDescr = buildInheritedFieldDescrFromDefinition( fld );
                    fieldMap.put( inheritedFlDescr.getFieldName(),
                                  inheritedFlDescr );
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
                Class superKlass = registry.getTypeResolver().resolveType( fullSuper );
                ClassFieldInspector inspector = new ClassFieldInspector( superKlass );
                for (String name : inspector.getGetterMethods().keySet()) {
                    // classFieldAccessor requires both getter and setter
                    if (inspector.getSetterMethods().containsKey( name )) {
                        if (!inspector.isNonGetter( name ) && !"class".equals( name )) {
                            TypeFieldDescr inheritedFlDescr = new TypeFieldDescr(
                                                                                  name,
                                                                                  new PatternDescr(
                                                                                                    inspector.getFieldTypes().get( name ).getName() ) );
                            inheritedFlDescr.setInherited( !Modifier.isAbstract( inspector.getGetterMethods().get( name ).getModifiers() ) );
                            inheritedFlDescr.setIndex( inspector.getFieldNames().size() + inspector.getFieldNames().get( name ) );

                            if (!fieldMap.containsKey( inheritedFlDescr.getFieldName() ))
                                fieldMap.put( inheritedFlDescr.getFieldName(),
                                              inheritedFlDescr );
                        }
                    }
                }

            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeDroolsException( "Unable to resolve Type Declaration superclass '" + fullSuper + "'" );
            } catch (IOException e) {

            }
        }

        // finally, locally declared fields are merged. The map swap ensures that super-fields are added in order, before the subclass' ones
        // notice that it is not possible to override a field changing its type
        for (String fieldName : typeDescr.getFields().keySet()) {
            if ( fieldMap.containsKey( fieldName ) ) {
                String type1 = fieldMap.get( fieldName ).getPattern().getObjectType();
                String type2 = typeDescr.getFields().get( fieldName ).getPattern().getObjectType();
                if (type2.lastIndexOf( "." ) < 0) {
                    try {
                        TypeResolver typeResolver = pkgRegistryMap.get( pack.getName() ).getTypeResolver();
                        type1 = typeResolver.resolveType(type1).getName();
                        type2 = typeResolver.resolveType(type2).getName();
                        // now that we are at it... this will be needed later anyway
                        fieldMap.get( fieldName ).getPattern().setObjectType( type1 );
                        typeDescr.getFields().get( fieldName ).getPattern().setObjectType( type2 );
                    } catch ( ClassNotFoundException cnfe ) {
                        // will fail later
                    }
                }

                if (!type1.equals( type2 )) {
                    this.results.add( new TypeDeclarationError( typeDescr,
                                                                "Cannot redeclare field '" + fieldName + " from " + type1 + " to " + type2 ) );
                    typeDescr.setType( null,
                                       null );
                    return false;
                } else {
                    String initVal = fieldMap.get( fieldName ).getInitExpr();
                    if (typeDescr.getFields().get( fieldName ).getInitExpr() == null) {
                        typeDescr.getFields().get( fieldName ).setInitExpr( initVal );
                    }
                    typeDescr.getFields().get( fieldName ).setInherited( fieldMap.get( fieldName ).isInherited() );

                    for (String key : fieldMap.get( fieldName ).getAnnotationNames()) {
                        if (typeDescr.getFields().get( fieldName ).getAnnotation( key ) == null) {
                            typeDescr.getFields().get( fieldName ).addAnnotation( fieldMap.get( fieldName ).getAnnotation( key ) );
                        }
                    }

                    if (typeDescr.getFields().get( fieldName ).getIndex() < 0) {
                        typeDescr.getFields().get( fieldName ).setIndex( fieldMap.get( fieldName ).getIndex() );
                    }
                }
            }
            fieldMap.put( fieldName,
                          typeDescr.getFields().get( fieldName ) );
        }

        typeDescr.setFields( fieldMap );

        return true;
    }

    protected TypeFieldDescr buildInheritedFieldDescrFromDefinition(FactField fld) {
        PatternDescr fldType = new PatternDescr();
        TypeFieldDescr inheritedFldDescr = new TypeFieldDescr();
            inheritedFldDescr.setFieldName( fld.getName() );
            fldType.setObjectType( ( (FieldDefinition) fld ).getFieldAccessor().getExtractToClassName() );
            inheritedFldDescr.setPattern( fldType );
        if ( fld.isKey() ) {
            inheritedFldDescr.getAnnotations().put( TypeDeclaration.ATTR_KEY,
                                                    new AnnotationDescr( TypeDeclaration.ATTR_KEY ) );
        }
            inheritedFldDescr.setIndex( fld.getIndex() );
            inheritedFldDescr.setInherited( true );
            inheritedFldDescr.setInitExpr( ( (FieldDefinition) fld ).getInitExpr() );
        return inheritedFldDescr;
    }


    /**
     * @param packageDescr
     */
    void processEntryPointDeclarations( PackageRegistry pkgRegistry, PackageDescr packageDescr ) {
        for (EntryPointDeclarationDescr epDescr : packageDescr.getEntryPointDeclarations()) {
            pkgRegistry.getPackage().addEntryPointId( epDescr.getEntryPointId() );
        }
    }

    private void processWindowDeclarations( PackageRegistry pkgRegistry, PackageDescr packageDescr ) {
        for (WindowDeclarationDescr wd : packageDescr.getWindowDeclarations()) {
            WindowDeclaration window = new WindowDeclaration( wd.getName(), packageDescr.getName() );
            // TODO: process annotations

            // process pattern
            Package pkg = pkgRegistry.getPackage();
            DialectCompiletimeRegistry ctr = pkgRegistry.getDialectCompiletimeRegistry();
            RuleDescr dummy = new RuleDescr( wd.getName() + " Window Declaration" );
            dummy.addAttribute( new AttributeDescr( "dialect", "java" ) );
            RuleBuildContext context = new RuleBuildContext( this,
                                                             dummy,
                                                             ctr,
                                                             pkg,
                                                             ctr.getDialect( pkgRegistry.getDialect() ) );
            final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( wd.getPattern().getClass() );
            if (builder != null) {
                final Pattern pattern = (Pattern) builder.build( context,
                                                                 wd.getPattern(),
                                                                 null );

                window.setPattern( pattern );
            } else {
                throw new RuntimeDroolsException(
                                                  "BUG: builder not found for descriptor class " + wd.getPattern().getClass() );
            }

            if( context.getErrors().size() > 0 ) {
                for( DroolsError error : context.getErrors() ) {
                    this.results.add( error );
                }
            } else {
                pkgRegistry.getPackage().addWindowDeclaration( window );
            }
        }
    }

    void registerGeneratedType(AbstractClassTypeDeclarationDescr typeDescr) {
        String fullName = typeDescr.getType().getFullName();
        generatedTypes.add( fullName );
    }

    /**
     * @param packageDescr
     */
    List<TypeDefinition> processTypeDeclarations(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        for ( AbstractClassTypeDeclarationDescr typeDescr : packageDescr.getClassAndEnumDeclarationDescrs() ) {

            if ( isEmpty( typeDescr.getNamespace() ) ) {
                for ( ImportDescr id : packageDescr.getImports() ) {
                    String imp = id.getTarget();
                    int separator = imp.lastIndexOf( '.' );
                    String tail = imp.substring( separator + 1 );
                    if (tail.equals( typeDescr.getTypeName() )) {
                        typeDescr.setNamespace( imp.substring( 0,
                                                               separator ) );
                    }
                }
            }
            String qName = typeDescr.getType().getFullName();

            int dotPos = qName.lastIndexOf( '.' );
            if (dotPos >= 0) {
                // see if this overwrites an existing bean, which also could be a nested class.
                Class cls = null;
                try {
                    cls = Class.forName( typeDescr.getTypeName(),
                                         true,
                                         this.rootClassLoader );
                } catch (ClassNotFoundException e) {
                }

                String qualifiedClass = qName;
                int lastIndex;
                while (cls == null && ( lastIndex = qualifiedClass.lastIndexOf( '.' ) ) != -1) {
                    try {

                        qualifiedClass = qualifiedClass.substring( 0,
                                                                   lastIndex ) + "$" +
                                         qualifiedClass.substring( lastIndex + 1 );
                        cls = Class.forName( qualifiedClass,
                                             true,
                                             this.rootClassLoader );
                    } catch (final ClassNotFoundException e) {
                        cls = null;
                    }
                }

                if (cls != null) {
                    String str = ClassUtils.getPackage( cls );
                    typeDescr.setNamespace( str );
                    dotPos = cls.getName().lastIndexOf( '.' ); // reget dotPos, incase there were nested classes
                    typeDescr.setTypeName( cls.getName().substring( dotPos + 1 ) );
                } else {
                    typeDescr.setNamespace( qName.substring( 0,
                                                             dotPos ) );
                    typeDescr.setTypeName( qName.substring( dotPos + 1 ) );
                }
            }

            if ( isEmpty( typeDescr.getNamespace() ) && typeDescr.getFields().isEmpty() ) {
                // might be referencing a class imported with a package import (.*)
                PackageRegistry pkgReg = this.pkgRegistryMap.get( packageDescr.getName() );
                if ( pkgReg != null ) {
                    try {
                        Class<?> clz = pkgReg.getTypeResolver().resolveType( typeDescr.getTypeName() );
                        java.lang.Package pkg = clz.getPackage();
                        if (pkg != null) {
                            typeDescr.setNamespace( pkg.getName() );
                            int index = typeDescr.getNamespace() != null && typeDescr.getNamespace().length() > 0 ? typeDescr.getNamespace().length() + 1
                                                                                                                 : 0;
                            typeDescr.setTypeName( clz.getCanonicalName().substring( index ) );
                        }
                    } catch (Exception e) {
                        // intentionally eating the exception as we will fallback to default namespace
                    }
                }
            }

            if ( isEmpty( typeDescr.getNamespace() ) ) {
                typeDescr.setNamespace( packageDescr.getNamespace() ); // set the default namespace
            }

            //identify superclass type and namespace
            if ( typeDescr instanceof TypeDeclarationDescr ) {
                fillSuperType( (TypeDeclarationDescr) typeDescr,
                               packageDescr );
            }

            //identify field types as well
            fillFieldTypes( typeDescr,
                            packageDescr );

            if ( !typeDescr.getNamespace().equals( packageDescr.getNamespace() ) ) {
                // If the type declaration is for a different namespace, process that separately.
                PackageDescr altDescr = new PackageDescr( typeDescr.getNamespace() );
                if ( typeDescr instanceof TypeDeclarationDescr ) {
                    altDescr.addTypeDeclaration( (TypeDeclarationDescr) typeDescr );
                } else if ( typeDescr instanceof EnumDeclarationDescr) {
                    altDescr.addEnumDeclaration( (EnumDeclarationDescr) typeDescr );
                }

                for ( ImportDescr imp : packageDescr.getImports() ) {
                    altDescr.addImport( imp );
                }
                if (!getPackageRegistry().containsKey( altDescr.getNamespace() )) {
                    newPackage(altDescr);
                }
                mergePackage( this.pkgRegistryMap.get( altDescr.getNamespace() ), altDescr );
            }

        }

        // sort declarations : superclasses must be generated first
        Collection<AbstractClassTypeDeclarationDescr> sortedTypeDescriptors = sortByHierarchy( packageDescr.getClassAndEnumDeclarationDescrs() );

        for ( AbstractClassTypeDeclarationDescr typeDescr : sortedTypeDescriptors ) {
            registerGeneratedType( typeDescr );
        }

        List<TypeDefinition> unresolvedTypeDefinitions = null;

        for ( AbstractClassTypeDeclarationDescr typeDescr : sortedTypeDescriptors ) {

            if (!typeDescr.getNamespace().equals( packageDescr.getNamespace() )) {
                continue;
            }

            //descriptor needs fields inherited from superclass
            if ( typeDescr instanceof TypeDeclarationDescr ) {
                TypeDeclarationDescr tDescr = (TypeDeclarationDescr) typeDescr;
                for ( QualifiedName qname : tDescr.getSuperTypes() ) {
                    //descriptor needs fields inherited from superclass
                    if ( mergeInheritedFields( tDescr ) ) {
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
            TypeDeclaration type = new TypeDeclaration( typeDescr.getTypeName() );
            if (typeDescr.getResource() == null) {
                typeDescr.setResource(resource);
            }
            type.setResource( typeDescr.getResource() );

            // is it a regular fact or an event?
            AnnotationDescr annotationDescr = typeDescr.getAnnotation( TypeDeclaration.Role.ID );
            String role = ( annotationDescr != null ) ? annotationDescr.getSingleValue() : null;
            if (role != null) {
                type.setRole( TypeDeclaration.Role.parseRole( role ) );
            }

            annotationDescr = typeDescr.getAnnotation( TypeDeclaration.ATTR_TYPESAFE );
            String typesafe = ( annotationDescr != null ) ? annotationDescr.getSingleValue() : null;
            if (typesafe != null) {
                type.setTypesafe( Boolean.parseBoolean( typesafe ) );
            }

            // is it a pojo or a template?
            annotationDescr = typeDescr.getAnnotation( TypeDeclaration.Format.ID );
            String format = ( annotationDescr != null ) ? annotationDescr.getSingleValue() : null;
            if (format != null) {
                type.setFormat( TypeDeclaration.Format.parseFormat( format ) );
            }

            // is it a class, a trait or an enum?
            annotationDescr = typeDescr.getAnnotation( TypeDeclaration.Kind.ID );
            String kind = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
            if ( kind != null ) {
                type.setKind( TypeDeclaration.Kind.parseKind( kind ) );
            }
            if ( typeDescr instanceof EnumDeclarationDescr ) {
                type.setKind( TypeDeclaration.Kind.ENUM );
            }


            annotationDescr = typeDescr.getAnnotation( TypeDeclaration.ATTR_CLASS );
            String className = ( annotationDescr != null ) ? annotationDescr.getSingleValue() : null;
            if (StringUtils.isEmpty( className )) {
                className = type.getTypeName();
            }

            Class clazz;
            try {

                // the type declaration is generated in any case (to be used by subclasses, if any)
                // the actual class will be generated only if needed

                generateDeclaredBean( typeDescr,
                                      type,
                                      pkgRegistry );

                clazz = pkgRegistry.getTypeResolver().resolveType( typeDescr.getType().getFullName() );
                type.setTypeClass( clazz );


            } catch (final ClassNotFoundException e) {
                this.results.add( new TypeDeclarationError( typeDescr,
                                                            "Class '" + className +
                                                            "' not found for type declaration of '" +
                                                            type.getTypeName() + "'" ) );
                continue;
            }

            if ( ! processTypeFields( pkgRegistry, typeDescr, type, true ) ) {
                if (unresolvedTypeDefinitions == null) {
                    unresolvedTypeDefinitions = new ArrayList<TypeDefinition>();
                }
                unresolvedTypeDefinitions.add( new TypeDefinition( type, typeDescr ) );
            }
        }

        return unresolvedTypeDefinitions;
    }

    private boolean processTypeFields(PackageRegistry pkgRegistry, AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type, boolean firstAttempt) {
        if (type.getTypeClassDef() != null) {
            try {
                buildFieldAccessors( type, pkgRegistry );
            } catch (Throwable e) {
                if (!firstAttempt) {
                    this.results.add( new TypeDeclarationError(typeDescr,
                            "Error creating field accessors for TypeDeclaration '" + type.getTypeName() +
                                    "' for type '" +
                                    type.getTypeName() +
                                    "'") );
                }
                return false;
            }
        }

        AnnotationDescr annotationDescr = typeDescr.getAnnotation( TypeDeclaration.ATTR_TIMESTAMP );
        String timestamp = ( annotationDescr != null ) ? annotationDescr.getSingleValue() : null;
        if (timestamp != null) {
            type.setTimestampAttribute( timestamp );
            Package pkg = pkgRegistry.getPackage();
            InternalReadAccessor reader = pkg.getClassFieldAccessorStore().getMVELReader( ClassUtils.getPackage(type.getTypeClass()),
                                                                                          type.getTypeClass().getName(),
                                                                                          timestamp,
                                                                                          type.isTypesafe() );
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( (MVELCompileable) reader );
            ( (MVELCompileable) reader ).compile( data );
            type.setTimestampExtractor( reader );
        }

        annotationDescr = typeDescr.getAnnotation( TypeDeclaration.ATTR_DURATION );
        String duration = ( annotationDescr != null ) ? annotationDescr.getSingleValue() : null;
        if (duration != null) {
            type.setDurationAttribute( duration );
            Package pkg = pkgRegistry.getPackage();
            InternalReadAccessor reader = pkg.getClassFieldAccessorStore().getMVELReader( ClassUtils.getPackage( type.getTypeClass() ),
                                                                                          type.getTypeClass().getName(),
                                                                                          duration,
                                                                                          type.isTypesafe() );
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( (MVELCompileable) reader );
            ( (MVELCompileable) reader ).compile( data );
            type.setDurationExtractor( reader );
        }

        annotationDescr = typeDescr.getAnnotation( TypeDeclaration.ATTR_EXPIRE );
        String expiration = ( annotationDescr != null ) ? annotationDescr.getSingleValue() : null;
        if (expiration != null) {
            if (timeParser == null) {
                timeParser = new TimeIntervalParser();
            }
            type.setExpirationOffset( timeParser.parse( expiration )[0] );
        }

        boolean dynamic = typeDescr.getAnnotationNames().contains( TypeDeclaration.ATTR_PROP_CHANGE_SUPPORT );
        type.setDynamic( dynamic );

        PropertySpecificOption propertySpecificOption = configuration.getOption(PropertySpecificOption.class);
        boolean propertySpecific = propertySpecificOption.isPropSpecific(typeDescr.getAnnotationNames().contains(TypeDeclaration.ATTR_PROP_SPECIFIC),
                typeDescr.getAnnotationNames().contains(TypeDeclaration.ATTR_NOT_PROP_SPECIFIC));
        type.setPropertyReactive(propertySpecific);

        if ( type.isValid() ) {
            pkgRegistry.getPackage().addTypeDeclaration( type );
        }

        return true;
    }

    private void updateTraitDefinition( TypeDeclaration type, Class concrete ) {
        try {

            ClassFieldInspector inspector = new ClassFieldInspector( concrete );
            Map<String, Method> methods = inspector.getGetterMethods();
            Map<String, Method> setters = inspector.getSetterMethods();
            int j = 0;
            for (String fieldName : methods.keySet()) {
                if ("core".equals( fieldName ) || "fields".equals( fieldName )) {
                    continue;
                }
                if (!inspector.isNonGetter( fieldName ) && setters.keySet().contains( fieldName )) {

                    Class ret = methods.get( fieldName ).getReturnType();
                    FieldDefinition field = new FieldDefinition();
                    field.setName( fieldName );
                    field.setTypeName( ret.getName() );
                    field.setIndex( j++ );
                    type.getTypeClassDef().addField( field );
                }
            }

            Set<String> interfaces = new HashSet<String>();
            Collections.addAll(interfaces, type.getTypeClassDef().getInterfaces());
            for (Class iKlass : concrete.getInterfaces()) {
                interfaces.add( iKlass.getName() );
            }
            type.getTypeClassDef().setInterfaces( interfaces.toArray( new String[interfaces.size()] ) );

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
    private boolean isNovelClass( AbstractClassTypeDeclarationDescr typeDescr ) {
        try {
            PackageRegistry reg = this.pkgRegistryMap.get( typeDescr.getNamespace() );
            if ( reg != null ) {
                String availableName = typeDescr.getType().getFullName();
                Class< ? > resolvedType = reg.getTypeResolver().resolveType( availableName );
                return false;
            } else {
                return false;
            }
        } catch (ClassNotFoundException cnfe) {
            return true;
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
    private Class resolveAnnotation( String annotation,
            TypeResolver resolver ) {
        // do not waste time with @role and @format
        if (TypeDeclaration.Role.ID.equals( annotation )
            || TypeDeclaration.Format.ID.equals( annotation )) {
            return null;
        }
        // known conflicting annotation
        if (TypeDeclaration.ATTR_CLASS.equals( annotation )) {
            return null;
        }

        try {
            return resolver.resolveType( annotation.substring( 0,
                                                               1 ).toUpperCase() + annotation.substring( 1 ) );
        } catch (ClassNotFoundException e) {
            // internal annotation, or annotation which can't be resolved.
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
    private void buildFieldAccessors( final TypeDeclaration type,
            final PackageRegistry pkgRegistry ) throws SecurityException,
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
        for ( FieldDefinition attrDef : cd.getFieldsDefinitions() ) {
            ClassFieldAccessor accessor = store.getAccessor( cd.getDefinedClass().getName(),
                                                             attrDef.getName() );
            attrDef.setReadWriteAccessor( accessor );
        }
    }

    /**
     * Generates a bean, and adds it to the composite class loader that
     * everything is using.
     */
    private void generateDeclaredBean( AbstractClassTypeDeclarationDescr typeDescr,
                                       TypeDeclaration type,
                                       PackageRegistry pkgRegistry ) {

        // extracts type, supertype and interfaces
        String fullName = typeDescr.getType().getFullName();

        if ( type.getKind().equals( TypeDeclaration.Kind.CLASS ) ) {
            TypeDeclarationDescr tdescr = (TypeDeclarationDescr) typeDescr;
            if ( tdescr.getSuperTypes().size() > 1 ) {
                this.results.add( new TypeDeclarationError( typeDescr, "Declared class " + fullName + "  - has more than one supertype;" ) );
                return;
            } else if ( tdescr.getSuperTypes().size() == 0 ) {
                tdescr.addSuperType( "java.lang.Object" );
            }
        }

        boolean traitable = typeDescr.getAnnotation( Traitable.class.getSimpleName() ) != null;

        String[] fullSuperTypes = new String[typeDescr.getSuperTypes().size() + 1];
        int j = 0;
        for ( QualifiedName qname : typeDescr.getSuperTypes() ) {
            fullSuperTypes[j++] = qname.getFullName();
        }
        fullSuperTypes[j] = Thing.class.getName();

        List<String> interfaceList = new ArrayList<String>();
        interfaceList.add(  traitable ? Externalizable.class.getName() : Serializable.class.getName() );
        if (traitable) {
            interfaceList.add( TraitableBean.class.getName() );
        }
        String[] interfaces = interfaceList.toArray( new String[interfaceList.size()] );

        // prepares a class definition
        ClassDefinition def;
        switch ( type.getKind() ) {
            case TRAIT :
                def = new ClassDefinition( fullName,
                        "java.lang.Object",
                        fullSuperTypes );
                break;
            case ENUM :
                def = new EnumClassDefinition( fullName,
                        fullSuperTypes[0],
                        null );
                break;
            case CLASS :
            default :
                def = new ClassDefinition( fullName,
                        fullSuperTypes[0],
                        interfaces );
                def.setTraitable( traitable );
        }

        for (String annotationName : typeDescr.getAnnotationNames()) {
            Class annotation = resolveAnnotation( annotationName,
                                                  pkgRegistry.getTypeResolver() );
            if (annotation != null) {
                try {
                    AnnotationDefinition annotationDefinition = AnnotationDefinition.build( annotation,
                                                                                            typeDescr.getAnnotations().get( annotationName ).getValueMap(),
                                                                                            pkgRegistry.getTypeResolver() );
                    def.addAnnotation( annotationDefinition );
                } catch (NoSuchMethodException nsme) {
                    this.results.add( new TypeDeclarationError( typeDescr,
                                                                "Annotated type " + fullName +
                                                                "  - undefined property in @annotation " +
                                                                annotationName + ": " +
                                                                nsme.getMessage() + ";" ) );
                }
            }
        }

        // add enum literals, if appropriate
        if ( type.getKind() == TypeDeclaration.Kind.ENUM ) {
            for ( EnumLiteralDescr lit : ((EnumDeclarationDescr) typeDescr).getLiterals() ) {
                ((EnumClassDefinition) def).addLiteral(
                        new EnumLiteralDefinition( lit.getName(), lit.getConstructorArgs() )
                );
            }
        }

        // fields definitions are created. will be used by subclasses, if any.
        // Fields are SORTED in the process
        if (typeDescr.getFields().size() > 0) {
            PriorityQueue<FieldDefinition> fieldDefs = sortFields( typeDescr.getFields(),
                                                                   pkgRegistry );
            while (fieldDefs.size() > 0) {
                FieldDefinition fld = fieldDefs.poll();
                def.addField( fld );
            }
        }

        // check whether it is necessary to build the class or not
        type.setNovel( isNovelClass( typeDescr ) );

        // attach the class definition, it will be completed later
        type.setTypeClassDef( def );

        //if is not new, search the already existing declaration and
        //compare them o see if they are at least compatibles
        if ( ! type.isNovel() ) {
            TypeDeclaration previousTypeDeclaration = this.pkgRegistryMap.get( typeDescr.getNamespace() ).getPackage().getTypeDeclaration( typeDescr.getTypeName() );

            try {

                if ( type.getTypeClassDef().getFields().size() > 0 ){
                    //since the declaration defines one or more fields, it is a DEFINITION
                    type.setNature( TypeDeclaration.Nature.DEFINITION );
                } else{
                    //The declaration doesn't define any field, it is a DECLARATION
                    type.setNature( TypeDeclaration.Nature.DECLARATION );
                }

                //if there is no previous declaration, then the original declaration was a POJO
                //to the behavior previous these changes
                if ( previousTypeDeclaration == null ) {
                    // new declarations of a POJO can't declare new fields
                    if (type.getTypeClassDef().getFields().size() > 0 ){
                        type.setValid(false);
                        this.results.add(new TypeDeclarationError(typeDescr, "New declaration of "+typeDescr.getType().getFullName()
                                +" can't declare new fields"));
                    }
                } else {

                    int typeComparisonResult = this.compareTypeDeclarations(previousTypeDeclaration, type);

                    if ( typeComparisonResult < 0 ) {
                        //oldDeclaration is "less" than newDeclaration -> error
                        this.results.add( new TypeDeclarationError(typeDescr, typeDescr.getType().getFullName()
                                +" declares more fields than the already existing version") );
                        type.setValid( false );
                    } else if ( typeComparisonResult > 0 && ! type.getTypeClassDef().getFields().isEmpty() ) {
                        //oldDeclaration is "grater" than newDeclaration -> error
                        this.results.add( new TypeDeclarationError( typeDescr, typeDescr.getType().getFullName()
                                +" declares less fields than the already existing version") );
                        type.setValid( false );
                    }

                    //if they are "equal" -> no problem

                    // in the case of a declaration, we need to copy all the
                    // fields present in the previous declaration
                    if ( type.getNature() == TypeDeclaration.Nature.DECLARATION ) {
                        this.mergeTypeDeclarations( previousTypeDeclaration, type );
                    }
                }

            } catch ( IncompatibleClassChangeError error ) {
                //if the types are incompatible -> error
                this.results.add( new TypeDeclarationError( typeDescr, error.getMessage() ) );
            }

        } else {
            //if the declaration is novel, then it is a DEFINITION
            type.setNature( TypeDeclaration.Nature.DEFINITION );
        }

        generateDeclaredBean( typeDescr,
                              type,
                              pkgRegistry,
                              def );
    }

    private void generateDeclaredBean( AbstractClassTypeDeclarationDescr typeDescr,
                                       TypeDeclaration type,
                                       PackageRegistry pkgRegistry,
                                       ClassDefinition def ) {

        if ( typeDescr.getAnnotation( Traitable.class.getSimpleName() ) != null
                || ( ! type.getKind().equals( TypeDeclaration.Kind.TRAIT ) && TraitRegistry.getInstance().getTraitables().containsKey( def.getSuperClass() ) ) ) {
            if (!isNovelClass( typeDescr )) {
                try {
                    PackageRegistry reg = this.pkgRegistryMap.get( typeDescr.getNamespace() );
                    String availableName = typeDescr.getType().getFullName();
                    Class<?> resolvedType = reg.getTypeResolver().resolveType( availableName );
                    updateTraitDefinition( type,
                                           resolvedType );
                } catch (ClassNotFoundException cnfe) {
                    // we already know the class exists
                }
            }
            TraitRegistry.getInstance().addTraitable( def );
        } else if ( type.getKind().equals( TypeDeclaration.Kind.TRAIT )
                    || typeDescr.getAnnotation( Trait.class.getSimpleName() ) != null ) {

            if ( !type.isNovel() ) {
                try {
                    PackageRegistry reg = this.pkgRegistryMap.get( typeDescr.getNamespace() );
                    String availableName = typeDescr.getType().getFullName();
                    Class<?> resolvedType = reg.getTypeResolver().resolveType( availableName );
                    if ( ! Thing.class.isAssignableFrom( resolvedType ) ) {
                        updateTraitDefinition( type,
                                               resolvedType );

                        String target = typeDescr.getTypeName() + TraitFactory.SUFFIX;
                        TypeDeclarationDescr tempDescr = new TypeDeclarationDescr();
                        tempDescr.setNamespace( typeDescr.getNamespace() );
                        tempDescr.setFields( typeDescr.getFields() );
                        tempDescr.setType( target,
                                           typeDescr.getNamespace() );
                        tempDescr.addSuperType( typeDescr.getType() );
                        TypeDeclaration tempDeclr = new TypeDeclaration( target );
                        tempDeclr.setKind( TypeDeclaration.Kind.TRAIT );
                        tempDeclr.setTypesafe( type.isTypesafe() );
                        tempDeclr.setNovel( true );
                        tempDeclr.setTypeClassName( tempDescr.getType().getFullName() );
                        tempDeclr.setResource( type.getResource() );

                        ClassDefinition tempDef = new ClassDefinition( target );
                        tempDef.setClassName( tempDescr.getType().getFullName() );
                        tempDef.setTraitable( false );
                        for (FieldDefinition fld : def.getFieldsDefinitions()) {
                            tempDef.addField( fld );
                        }
                        tempDef.setInterfaces( def.getInterfaces() );
                        tempDef.setSuperClass( def.getClassName() );
                        tempDef.setDefinedClass( resolvedType );
                        tempDef.setAbstrakt( true );
                        tempDeclr.setTypeClassDef( tempDef );

                        type.setKind( TypeDeclaration.Kind.CLASS );

                        generateDeclaredBean( tempDescr,
                                              tempDeclr,
                                              pkgRegistry,
                                              tempDef );
                        try {
                            Class<?> clazz = pkgRegistry.getTypeResolver().resolveType( tempDescr.getType().getFullName() );
                            tempDeclr.setTypeClass( clazz );
                        } catch ( ClassNotFoundException cnfe ) {
                            this.results.add( new TypeDeclarationError( typeDescr,
                                                                        "Internal Trait extension Class '" + target +
                                                                        "' could not be generated correctly'" ) );
                        } finally {
                            pkgRegistry.getPackage().addTypeDeclaration( tempDeclr );
                        }

                    } else {
                        updateTraitDefinition( type,
                                               resolvedType );
                        TraitRegistry.getInstance().addTrait( def );
                    }
                } catch (ClassNotFoundException cnfe) {
                    // we already know the class exists
                }
            } else {
                if (def.getClassName().endsWith( "_Trait__Extension" )) {
                    TraitRegistry.getInstance().addTrait( def.getClassName().replace( "_Trait__Extension",
                                                                                      "" ),
                                                          def );
                } else {
                    TraitRegistry.getInstance().addTrait( def );
                }
            }

        }

        if ( type.isNovel() ) {
            String fullName = typeDescr.getType().getFullName();
            JavaDialectRuntimeData dialect = (JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData( "java" );
            switch ( type.getKind() ) {
                case TRAIT :
                    try {
                        ClassBuilder tb = this.configuration.getClassBuilderFactory().getTraitBuilder();
                        byte[] d = tb.buildClass( def );
                        String resourceName = JavaDialectRuntimeData.convertClassToResourcePath( fullName );
                        dialect.putClassDefinition( resourceName, d );
                        dialect.write( resourceName, d );
                    } catch ( Exception e ) {
                        this.results.add( new TypeDeclarationError( typeDescr,
                                                                    "Unable to compile declared trait " + fullName +
                                                                    ": " + e.getMessage() + ";" ) );
                    }
                    break;
                case ENUM :
                    try {
                        ClassBuilder eb = this.configuration.getClassBuilderFactory().getEnumClassBuilder();
                        byte[] d = eb.buildClass( def );
                        String resourceName = JavaDialectRuntimeData.convertClassToResourcePath( fullName );
                        dialect.putClassDefinition( resourceName, d );
                        dialect.write( resourceName, d );
                    } catch ( Exception e ) {
                        e.printStackTrace();
                        this.results.add( new TypeDeclarationError( typeDescr,
                                                                    "Unable to compile declared enum " + fullName +
                                                                    ": " + e.getMessage() + ";" ) );
                    }
                    break;
                case CLASS :
                default :
                    try {
                        ClassBuilder cb = this.configuration.getClassBuilderFactory().getBeanClassBuilder();
                        byte[] d = cb.buildClass( def );
                        String resourceName = JavaDialectRuntimeData.convertClassToResourcePath( fullName );
                        dialect.putClassDefinition( resourceName, d );
                        dialect.write( resourceName, d );
                    } catch ( Exception e ) {
                        this.results.add( new TypeDeclarationError( typeDescr,
                                                                    "Unable to create a class for declared type " + fullName +
                                                                    ": " + e.getMessage() + ";" ) );
                    }
                    break;
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
    private PriorityQueue<FieldDefinition> sortFields( Map<String, TypeFieldDescr> flds,
            PackageRegistry pkgRegistry ) {
        PriorityQueue<FieldDefinition> queue = new PriorityQueue<FieldDefinition>();
        int last = 0;

        for (TypeFieldDescr field : flds.values()) {
            last = Math.max( last,
                             field.getIndex() );
        }

        for (TypeFieldDescr field : flds.values()) {
            if (field.getIndex() < 0) {
                field.setIndex( ++last );
            }

            String fullFieldType;
            try {
                String typeName = field.getPattern().getObjectType();
                fullFieldType = generatedTypes.contains(typeName) ? typeName : pkgRegistry.getTypeResolver().resolveType(typeName).getName();

                FieldDefinition fieldDef = new FieldDefinition( field.getFieldName(),
                                                                fullFieldType );
                // field is marked as PK
                boolean isKey = field.getAnnotation( TypeDeclaration.ATTR_KEY ) != null;
                fieldDef.setKey( isKey );

                fieldDef.setIndex( field.getIndex() );
                fieldDef.setInherited( field.isInherited() );
                fieldDef.setInitExpr( field.getInitExpr() );

                for (String annotationName : field.getAnnotationNames()) {
                    Class annotation = resolveAnnotation( annotationName,
                                                          pkgRegistry.getTypeResolver() );
                    if (annotation != null) {
                        try {
                            AnnotationDefinition annotationDefinition = AnnotationDefinition.build( annotation,
                                                                                                    field.getAnnotations().get( annotationName ).getValueMap(),
                                                                                                    pkgRegistry.getTypeResolver() );
                            fieldDef.addAnnotation( annotationDefinition );
                        } catch (NoSuchMethodException nsme) {
                            this.results.add( new TypeDeclarationError( field,
                                                                        "Annotated field " + field.getFieldName() +
                                                                        "  - undefined property in @annotation " +
                                                                        annotationName + ": " + nsme.getMessage() + ";" ) );
                        }
                    }
                }

                queue.add( fieldDef );
            } catch (ClassNotFoundException cnfe) {
                this.results.add( new TypeDeclarationError( field, cnfe.getMessage() ) );
            }

        }

        return queue;
    }

    private void addFunction( final FunctionDescr functionDescr ) {
        functionDescr.setResource( this.resource );
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( functionDescr.getNamespace() );
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect( functionDescr.getDialect() );
        dialect.addFunction( functionDescr,
                             pkgRegistry.getTypeResolver(),
                             this.resource );
    }

    private void preCompileAddFunction( final FunctionDescr functionDescr ) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( functionDescr.getNamespace() );
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect( functionDescr.getDialect() );
        dialect.preCompileAddFunction( functionDescr,
                                       pkgRegistry.getTypeResolver() );
    }

    private void postCompileAddFunction( final FunctionDescr functionDescr ) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( functionDescr.getNamespace() );
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect( functionDescr.getDialect() );
        dialect.postCompileAddFunction( functionDescr,
                                        pkgRegistry.getTypeResolver() );
    }

    private void addFactTemplate( final PackageDescr pkgDescr,
                                  final FactTemplateDescr factTemplateDescr ) {
        List<FieldTemplate> fields = new ArrayList<FieldTemplate>();
        int index = 0;
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( pkgDescr.getNamespace() );
        for (FieldTemplateDescr fieldTemplateDescr : factTemplateDescr.getFields()) {
            FieldTemplate fieldTemplate = null;
            try {
                fieldTemplate = new FieldTemplateImpl( fieldTemplateDescr.getName(),
                                                       index++,
                                                       pkgRegistry.getTypeResolver().resolveType(fieldTemplateDescr.getClassType()) );
            } catch (final ClassNotFoundException e) {
                this.results.add( new FieldTemplateError( pkgRegistry.getPackage(),
                                                          fieldTemplateDescr,
                                                          null,
                                                          "Unable to resolve Class '" + fieldTemplateDescr.getClassType() + "'"));
            }
            fields.add(fieldTemplate);
        }

        new FactTemplateImpl( pkgRegistry.getPackage(),
                              factTemplateDescr.getName(),
                              fields.toArray( new FieldTemplate[fields.size()] ) );
    }

    private void addRule( final RuleDescr ruleDescr ) {
        if (ruleDescr.getResource() == null) {
            ruleDescr.setResource( resource );
        }

        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( ruleDescr.getNamespace() );

        Package pkg = pkgRegistry.getPackage();
        DialectCompiletimeRegistry ctr = pkgRegistry.getDialectCompiletimeRegistry();
        RuleBuildContext context = new RuleBuildContext( this,
                                                         ruleDescr,
                                                         ctr,
                                                         pkg,
                                                         ctr.getDialect( pkgRegistry.getDialect() ) );
        ruleBuilder.build( context );

        this.results.addAll( context.getErrors() );

        context.getRule().setResource( ruleDescr.getResource() );

        context.getDialect().addRule( context );

        if (this.ruleBase != null) {
            if (pkg.getRule( ruleDescr.getName() ) != null) {
                this.ruleBase.lock();
                try {
                    // XXX: this one notifies listeners
                    this.ruleBase.removeRule( pkg,
                                              pkg.getRule( ruleDescr.getName() ) );
                } finally {
                    this.ruleBase.unlock();
                }
            }
        }

        pkg.addRule( context.getRule() );
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
            pkgRegistry = (PackageRegistry) this.pkgRegistryMap.values().toArray()[this.pkgRegistryMap.size() - 1];
        }
        Package pkg = null;
        if (pkgRegistry != null) {
            pkg = pkgRegistry.getPackage();
        }
        if (hasErrors() && pkg != null) {
            pkg.setError( getErrors().toString() );
        }
        return pkg;
    }

    public Package[] getPackages() {
        Package[] pkgs = new Package[this.pkgRegistryMap.size()];
//        int i = pkgs.length;
        int i = 0 ;
        String errors = null;
        if (!getErrors().isEmpty()) {
            errors = getErrors().toString();
        }
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
            Package pkg = pkgRegistry.getPackage();
            pkg.getDialectRuntimeRegistry().onBeforeExecute();
            if (errors != null) {
                pkg.setError( errors );
            }
//            pkgs[--i] = pkg;
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

    public PackageRegistry getPackageRegistry( String name ) {
        return this.pkgRegistryMap.get( name );
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
            expander.addDSLMapping( file.getMapping() );
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

    public KnowledgeBuilderResults getProblems( ResultSeverity... problemTypes ) {
        List<KnowledgeBuilderResult> problems = getResultList( problemTypes );
        return new PackageBuilderResults( problems.toArray( new BaseKnowledgeBuilderResultImpl[problems.size()] ) );
    }

    /**
     * @param severities
     * @return
     */
    private List<KnowledgeBuilderResult> getResultList( ResultSeverity... severities ) {
        List<ResultSeverity> typesToFetch = Arrays.asList( severities );
        ArrayList<KnowledgeBuilderResult> problems = new ArrayList<KnowledgeBuilderResult>();
        for (KnowledgeBuilderResult problem : results) {
            if (typesToFetch.contains( problem.getSeverity() )) {
                problems.add( problem );
            }
        }
        return problems;
    }

    public boolean hasProblems( ResultSeverity... problemTypes ) {
        return !getResultList( problemTypes ).isEmpty();
    }

    private List<DroolsError> getErrorList() {
        List<KnowledgeBuilderResult> list = getResultList( ResultSeverity.ERROR );
        List<DroolsError> errors = new ArrayList<DroolsError>();
        for (KnowledgeBuilderResult p : list) {
            if (p instanceof ConfigurableSeverityResult) {
                errors.add( new DroolsErrorWrapper( p ) );
            } else {
                errors.add( (DroolsError) p );
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

    private List<KnowledgeBuilderResult> getWarningList() {
        return getResultList( ResultSeverity.WARNING );
    }

    private List<KnowledgeBuilderResult> getInfoList() {
        return getResultList( ResultSeverity.INFO );
    }

    /**
     * @return A list of Error objects that resulted from building and compiling
     *         the package.
     */
    public PackageBuilderErrors getErrors() {
        List<DroolsError> errors = getErrorList();
        return new PackageBuilderErrors( errors.toArray( new DroolsError[errors.size()] ) );
    }

    /**
     * Reset the error list. This is useful when incrementally building
     * packages. Care should be used when building this, if you clear this when
     * there were errors on items that a rule depends on (eg functions), then
     * you will get spurious errors which will not be that helpful.
     */
    protected void resetErrors() {
        resetProblemType( ResultSeverity.ERROR );
    }

    protected void resetWarnings() {
        resetProblemType( ResultSeverity.WARNING );
    }

    private void resetProblemType( ResultSeverity problemType ) {
        List<KnowledgeBuilderResult> toBeDeleted = new ArrayList<KnowledgeBuilderResult>();
        for (KnowledgeBuilderResult problem : results) {
            if (problemType != null && problemType.equals( problem.getSeverity() )) {
                toBeDeleted.add( problem );
            }
        }
        this.results.removeAll( toBeDeleted );

    }

    protected void resetProblems() {
        this.results.clear();
    }

    public String getDefaultDialect() {
        return this.defaultDialect;
    }

    public static class MissingPackageNameException extends IllegalArgumentException {

        private static final long serialVersionUID = 510l;

        public MissingPackageNameException( final String message ) {
            super( message );
        }

    }

    public static class PackageMergeException extends IllegalArgumentException {

        private static final long serialVersionUID = 400L;

        public PackageMergeException( final String message ) {
            super( message );
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

        public void addError( final CompilationProblem err ) {
            this.errors.add( err );
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
            if (this.errors.size() == 0) {
                return null;
            } else {
                final CompilationProblem[] list = new CompilationProblem[this.errors.size()];
                this.errors.toArray( list );
                return list;
            }
        }
    }

    public static class RuleErrorHandler extends ErrorHandler {

        private BaseDescr descr;

        private Rule      rule;

        public RuleErrorHandler( final BaseDescr ruleDescr,
                final Rule rule,
                final String message ) {
            this.descr = ruleDescr;
            this.rule = rule;
            this.message = message;
        }

        public DroolsError getError() {
            return new RuleBuildError( this.rule,
                                       this.descr,
                                       collectCompilerProblems(),
                                       this.message );
        }

    }

    /**
     * There isn't much point in reporting invoker errors, as they are no help.
     */
    public static class RuleInvokerErrorHandler extends RuleErrorHandler {

        public RuleInvokerErrorHandler( final BaseDescr ruleDescr,
                final Rule rule,
                final String message ) {
            super( ruleDescr,
                   rule,
                   message );
        }
    }

    public static class FunctionErrorHandler extends ErrorHandler {

        private FunctionDescr descr;

        public FunctionErrorHandler( final FunctionDescr functionDescr,
                final String message ) {
            this.descr = functionDescr;
            this.message = message;
        }

        public DroolsError getError() {
            return new FunctionError( this.descr,
                                      collectCompilerProblems(),
                                      this.message );
        }

    }

    public static class SrcErrorHandler extends ErrorHandler {

        public SrcErrorHandler( final String message ) {
            this.message = message;
        }

        public DroolsError getError() {
            return new SrcError( collectCompilerProblems(),
                                 this.message );
        }

    }

    public static class SrcError extends DroolsError {

        private Object object;
        private String message;
        private int[]  errorLines = new int[0];

        public SrcError( Object object, String message ) {
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
            buf.append( this.message );
            buf.append( " : " );
            buf.append( "\n" );
            if (this.object instanceof CompilationProblem[]) {
                final CompilationProblem[] problem = (CompilationProblem[]) this.object;
                for (CompilationProblem aProblem : problem) {
                    buf.append("\t");
                    buf.append(aProblem);
                    buf.append("\n");
                }
            } else if (this.object != null) {
                buf.append( this.object );
            }
            return buf.toString();
        }
    }

    public CompositeClassLoader getRootClassLoader() {
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
    public Collection<AbstractClassTypeDeclarationDescr> sortByHierarchy( List<AbstractClassTypeDeclarationDescr> typeDeclarations ) {

        Node<AbstractClassTypeDeclarationDescr> root = new Node<AbstractClassTypeDeclarationDescr>( null );
        Map<String, Node<AbstractClassTypeDeclarationDescr>> map = new HashMap<String, Node<AbstractClassTypeDeclarationDescr>>();
        for ( AbstractClassTypeDeclarationDescr tdescr : typeDeclarations ) {
            String typeName = tdescr.getType().getFullName();

            Node<AbstractClassTypeDeclarationDescr> node = map.get( typeName );
            if ( node == null ) {
                node = new Node( typeName,
                                 tdescr );
                map.put( typeName,
                         node );
            } else if ( node.getData() == null ) {
                node.setData( tdescr );
            } else {
                this.results.add( new TypeDeclarationError( tdescr,
                                       "Found duplicate declaration for type " + tdescr.getTypeName() ) );
            }

                if ( tdescr.getSuperTypes().isEmpty() ) {
                    root.addChild( node );
                } else {
                    for ( QualifiedName qname : tdescr.getSuperTypes() ) {
                        String superTypeName = qname.getFullName();

                        Node<AbstractClassTypeDeclarationDescr> superNode = map.get( superTypeName );
                        if ( superNode == null ) {
                            superNode = new Node<AbstractClassTypeDeclarationDescr>( superTypeName );
                            map.put( superTypeName,
                                    superNode );
                        }
                        superNode.addChild( node );
                    }
                }
            for ( TypeFieldDescr field : tdescr.getFields().values() ) {
                String fieldTypeName = field.getPattern().getObjectType();

                Node<AbstractClassTypeDeclarationDescr> superNode = map.get( fieldTypeName );
                if ( superNode == null ) {
                    superNode = new Node<AbstractClassTypeDeclarationDescr>( fieldTypeName );
                    map.put( fieldTypeName,
                             superNode );
                }
                superNode.addChild( node );
            }

        }


        for ( Node<AbstractClassTypeDeclarationDescr> n : map.values() ) {
            if ( n.getData() == null ) {
                root.addChild(n);
            }
        }



        List<AbstractClassTypeDeclarationDescr> sortedList = new LinkedList<AbstractClassTypeDeclarationDescr>();
        root.accept( sortedList );

        return sortedList;
    }

    /**
     * Utility class for the sorting algorithm
     *
     * @param <T>
     */
    private static class Node<T> {

        private String        key;
        private T             data;
        private List<Node<T>> children;

        public Node( String key ) {
            this.key = key;
            this.children = new LinkedList<Node<T>>();
        }

        public Node( String key,
                T content ) {
            this( key );
            this.data = content;
        }

        public void addChild( Node<T> child ) {
            this.children.add( child );
        }

        public String getKey() {
            return key;
        }

        public T getData() {
            return data;
        }

        public void setData( T content ) {
            this.data = content;
        }

        public void accept( List<T> list ) {
            accept( list, new Stack<T>() );
        }

        private void accept( List<T> list, Stack<T> stack ) {
            if (this.data != null) {
                list.remove( this.data );
                list.add( this.data );
                stack.push(this.data);
            }

            for (Node<T> child : children) {
                if (!stack.contains(child.data)) {
                    child.accept( list, stack );
                }
            }

            if (this.data != null) {
                stack.pop();
            }
        }
    }

    //Entity rules inherit package attributes
    private void inheritPackageAttributes( Map<String, AttributeDescr> pkgAttributes,
            RuleDescr ruleDescr ) {
        if (pkgAttributes == null) {
            return;
        }
        for (AttributeDescr attrDescr : pkgAttributes.values()) {
            String name = attrDescr.getName();
            AttributeDescr ruleAttrDescr = ruleDescr.getAttributes().get( name );
            if (ruleAttrDescr == null) {
                ruleDescr.getAttributes().put( name,
                                               attrDescr );
            }
        }
    }

    private int compareTypeDeclarations(TypeDeclaration oldDeclaration, TypeDeclaration newDeclaration) throws IncompatibleClassChangeError{

        //different formats -> incompatible
        if (!oldDeclaration.getFormat().equals(newDeclaration.getFormat())){
            throw new IncompatibleClassChangeError("Type Declaration "+newDeclaration.getTypeName()+" has a different"
                    + " format that its previous definition: "+newDeclaration.getFormat()+"!="+oldDeclaration.getFormat());
        }

        //different superclasses -> Incompatible (TODO: check for hierarchy)
        if ( !oldDeclaration.getTypeClassDef().getSuperClass().equals(newDeclaration.getTypeClassDef().getSuperClass()) ){
            if ( oldDeclaration.getNature() == TypeDeclaration.Nature.DEFINITION
                 && newDeclaration.getNature() == TypeDeclaration.Nature.DECLARATION
                 && Object.class.getName().equals( newDeclaration.getTypeClassDef().getSuperClass() )
                    ) {
                // actually do nothing. The new declaration just recalls the previous definition, probably to extend it.
            } else {
                throw new IncompatibleClassChangeError("Type Declaration "+newDeclaration.getTypeName()+" has a different"
                        + " superclass that its previous definition: "+newDeclaration.getTypeClassDef().getSuperClass()
                        +" != "+oldDeclaration.getTypeClassDef().getSuperClass());
            }
        }

        //different duration -> Incompatible
        if (!this.nullSafeEqualityComparison(oldDeclaration.getDurationAttribute(),newDeclaration.getDurationAttribute())){
            throw new IncompatibleClassChangeError("Type Declaration "+newDeclaration.getTypeName()+" has a different"
                    + " duration: "+newDeclaration.getDurationAttribute()
                    +" != "+oldDeclaration.getDurationAttribute());
        }

//        //different masks -> incompatible
        if ( newDeclaration.getNature().equals( TypeDeclaration.Nature.DEFINITION ) ) {
            if (oldDeclaration.getSetMask() != newDeclaration.getSetMask() ){
                throw new IncompatibleClassChangeError("Type Declaration "+newDeclaration.getTypeName()+" is incompatible with"
                        + " the previous definition: "+newDeclaration
                        +" != "+oldDeclaration);
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

            if (newFactField != null){
                //we can't use newFactField.getType() since it throws a NPE at this point.
                String newFactType = ((FieldDefinition)newFactField).getTypeName();

                if (!newFactType.equals(oldFactField.getType().getCanonicalName())){
                    throw new IncompatibleClassChangeError("Type Declaration "+newDeclaration.getTypeName()+"."+newFactField.getName()+" has a different"
                        + " type that its previous definition: "+newFactType
                        +" != "+oldFactField.getType().getCanonicalName());
                }
            }else{
                allFieldsInOldDeclarationAreStillPresent = false;
            }

        }


        //If the old declaration has less fields than the new declaration, oldDefinition < newDefinition
        if (oldFields.size() < newFieldsMap.size()){
            return -1;
        }

        //If the old declaration has more fields than the new declaration, oldDefinition > newDefinition
        if (oldFields.size() > newFieldsMap.size()){
            return 1;
        }

        //If the old declaration has the same fields as the new declaration,
        //and all the fieds present in the old declaration are also present in
        //the new declaration, then they are considered "equal", otherwise
        //they are incompatible
        if (allFieldsInOldDeclarationAreStillPresent){
            return 0;
        }

        //Both declarations have the same number of fields, but not all the
        //fields in the old declaration are present in the new declaration.
        throw new IncompatibleClassChangeError(newDeclaration.getTypeName()+" introduces"
            + " fields that are not present in its previous version.");


    }

    /**
     * Merges all the missing FactFields from oldDefinition into newDeclaration.
     * @param oldDeclaration
     * @param newDeclaration
     */
    private void mergeTypeDeclarations( TypeDeclaration oldDeclaration, TypeDeclaration newDeclaration ) {
        if ( oldDeclaration == null ){
            return;
        }

        //add the missing fields (if any) to newDeclaration
        for ( FieldDefinition oldFactField : oldDeclaration.getTypeClassDef().getFieldsDefinitions() ) {
            FieldDefinition newFactField = newDeclaration.getTypeClassDef().getField( oldFactField.getName() );
            if ( newFactField == null){
                newDeclaration.getTypeClassDef().addField( oldFactField );
            }
        }

        //copy the defined class
        newDeclaration.setTypeClass( oldDeclaration.getTypeClass() );
    }


    private boolean nullSafeEqualityComparison( Comparable c1, Comparable c2 ){
        if ( c1 == null ) {
            return c2 == null;
        }
        return c2 == null ? false : c1.compareTo( c2 ) == 0;
    }

    static class TypeDefinition {
        private final AbstractClassTypeDeclarationDescr typeDescr;
        private final TypeDeclaration type;

        private TypeDefinition( TypeDeclaration type, AbstractClassTypeDeclarationDescr typeDescr ) {
            this.type = type;
            this.typeDescr = typeDescr;
        }
    }

    public void registerBuildResource(final Resource resource) {
        buildResources.push( new ArrayList<Resource>() {{ add(resource); }} );
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

    private void removeObjectsGeneratedFromResource(Resource resource) {
        if (pkgRegistryMap != null) {
            for (PackageRegistry packageRegistry : pkgRegistryMap.values()) {
                packageRegistry.removeObjectsGeneratedFromResource(resource);
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
    }

}
