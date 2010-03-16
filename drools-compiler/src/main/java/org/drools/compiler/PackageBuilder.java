package org.drools.compiler;

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

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.ChangeSet;
import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassFieldAccessor;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.evaluators.TimeIntervalParser;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.common.InternalRuleBase;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.compiler.xml.XmlPackageReader;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.process.Process;
import org.drools.factmodel.ClassBuilder;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateImpl;
import org.drools.facttemplates.FieldTemplate;
import org.drools.facttemplates.FieldTemplateImpl;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.server.util.BRDRLPersistence;
import org.drools.guvnor.server.util.BRXMLPersistence;
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.ReaderResource;
import org.drools.io.internal.InternalResource;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.rule.DroolsCompositeClassLoader;
import org.drools.rule.Function;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleBuilder;
import org.drools.spi.InternalReadAccessor;
import org.drools.type.DateFormats;
import org.drools.type.DateFormatsImpl;
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
 */
public class PackageBuilder {

    private Map<String, PackageRegistry>      pkgRegistryMap;

    private List<DroolsError>                 results;

    private final PackageBuilderConfiguration configuration;

    public static final RuleBuilder           ruleBuilder = new RuleBuilder();

    /**
     * Optional RuleBase for incremental live building
     */
    private ReteooRuleBase                    ruleBase;

    /**
     * default dialect
     */
    private final String                      defaultDialect;

    private DroolsCompositeClassLoader              rootClassLoader;

    private Map<String, Class< ? >>           globals;

    private Resource                          resource;

    private List<DSLTokenizedMappingFile>     dslFiles;

    private TimeIntervalParser                timeParser;
    
    protected DateFormats                     dateFormats;    

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

    public PackageBuilder(final Package pkg) {
        this( pkg,
              null );
    }

    public PackageBuilder(final RuleBase ruleBase) {
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
    public PackageBuilder(final PackageBuilderConfiguration configuration) {
        this( (RuleBase) null,
              configuration );
    }

    public PackageBuilder(Package pkg,
                          PackageBuilderConfiguration configuration) {
        if ( configuration == null ) {
            this.configuration = new PackageBuilderConfiguration();
        } else {
            this.configuration = configuration;
        }
        
        this.dateFormats = null;//(DateFormats) this.environment.get( EnvironmentName.DATE_FORMATS );
        if ( this.dateFormats == null ) {
            this.dateFormats = new DateFormatsImpl();
            //this.environment.set( EnvironmentName.DATE_FORMATS , this.dateFormats );
        }        

        this.rootClassLoader = new DroolsCompositeClassLoader( this.configuration.getClassLoader() );

        this.defaultDialect = this.configuration.getDefaultDialect();

        this.pkgRegistryMap = new HashMap<String, PackageRegistry>();
        this.results = new ArrayList<DroolsError>();

        PackageRegistry pkgRegistry = new PackageRegistry( this,
                                                           pkg );
        pkgRegistry.setDialect( this.defaultDialect );
        this.pkgRegistryMap.put( pkg.getName(),
                                 pkgRegistry );

        globals = new HashMap<String, Class< ? >>();
    }

    public PackageBuilder(RuleBase ruleBase,
                          PackageBuilderConfiguration configuration) {
        if ( configuration == null ) {
            this.configuration = new PackageBuilderConfiguration();
        } else {
            this.configuration = configuration;
        }

        if ( ruleBase != null ) {
            this.rootClassLoader = ((InternalRuleBase) ruleBase).getRootClassLoader();
        } else {
            this.rootClassLoader = new DroolsCompositeClassLoader( this.configuration.getClassLoader() );
        }
        
        this.dateFormats = null;//(DateFormats) this.environment.get( EnvironmentName.DATE_FORMATS );
        if ( this.dateFormats == null ) {
            this.dateFormats = new DateFormatsImpl();
            //this.environment.set( EnvironmentName.DATE_FORMATS , this.dateFormats );
        }                

        // FIXME, we need to get drools to support "default" namespace.
        //this.defaultNamespace = pkg.getName();        
        this.defaultDialect = this.configuration.getDefaultDialect();

        this.pkgRegistryMap = new HashMap<String, PackageRegistry>();
        this.results = new ArrayList<DroolsError>();

        this.ruleBase = (ReteooRuleBase) ruleBase;

        globals = new HashMap<String, Class< ? >>();
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
        this.resource = new ReaderResource( reader );
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( reader );
        this.results.addAll( parser.getErrors() );
        if ( !parser.hasErrors() ) {
            addPackage( pkg );
        }
        this.resource = null;
    }

    public void addPackageFromDrl(Resource resource) throws DroolsParserException,
                                                    IOException {
        this.resource = resource;
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( resource.getInputStream() );
        this.results.addAll( parser.getErrors() );
        if ( !parser.hasErrors() ) {
            addPackage( pkg );
        }
        this.resource = null;
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
        this.resource = new ReaderResource( reader );
        final XmlPackageReader xmlReader = new XmlPackageReader( this.configuration.getSemanticModules() );

        try {
            xmlReader.read( reader );
        } catch ( final SAXException e ) {
            throw new DroolsParserException( e.toString(),
                                             e.getCause() );
        }

        addPackage( xmlReader.getPackageDescr() );
        this.resource = null;
    }

    public void addPackageFromXml(final Resource resource) throws DroolsParserException,
                                                          IOException {
        this.resource = resource;

        final XmlPackageReader xmlReader = new XmlPackageReader( this.configuration.getSemanticModules() );

        try {
            xmlReader.read( resource.getReader() );
        } catch ( final SAXException e ) {
            throw new DroolsParserException( e.toString(),
                                             e.getCause() );
        }

        addPackage( xmlReader.getPackageDescr() );
        this.resource = null;
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
        this.resource = new ReaderResource( source );

        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( source,
                                               dsl );
        this.results.addAll( parser.getErrors() );
        if ( !parser.hasErrors() ) {
            addPackage( pkg );
        }
        this.resource = null;
    }

    public void addPackageFromDslr(final Resource resource) throws DroolsParserException,
                                                           IOException {
        this.resource = resource;

        final DrlParser parser = new DrlParser();
        DefaultExpander expander = getDslExpander();

        try {
        	if (expander == null) {
        		expander = new DefaultExpander();
        	}
            String str = expander.expand( resource.getReader() );
            if ( expander.hasErrors() ) {
                this.results.addAll( expander.getErrors() );
            }

            final PackageDescr pkg = parser.parse( str );
            this.results.addAll( parser.getErrors() );
            if ( !parser.hasErrors() ) {
                addPackage( pkg );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        this.resource = null;
    }

    public void addPackageFromBrl(final Resource resource) throws DroolsParserException,
                                                          IOException {
        this.resource = resource;

        String brl = loadBrlFile( resource.getReader() );
        RuleModel model = BRXMLPersistence.getInstance().unmarshal( brl );
        String drl = BRDRLPersistence.getInstance().marshal( model );
        final DrlParser parser = new DrlParser();
        DefaultExpander expander = getDslExpander();

        try {
        	String str;
        	if (expander != null) {
        		str = expander.expand( new StringReader( drl ) );
                if ( expander.hasErrors() ) {
                    this.results.addAll( expander.getErrors() );
                }
        	} else {
        		str = drl;
        	}

            final PackageDescr pkg = parser.parse( str );
            this.results.addAll( parser.getErrors() );
            if ( !parser.hasErrors() ) {
                addPackage( pkg );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        this.resource = null;
    }

    private String loadBrlFile(final Reader drl) throws IOException {
        final StringBuilder buf = new StringBuilder();
        final BufferedReader input = new BufferedReader( drl );
        String line = null;
        while ( (line = input.readLine()) != null ) {
            buf.append( line );
            buf.append( "\n" );
        }
        return buf.toString();
    }

    public void addDsl(Resource resource) throws IOException {
        this.resource = resource;

        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        file.parseAndLoad( resource.getReader() );
        if ( this.dslFiles == null ) {
            this.dslFiles = new ArrayList<DSLTokenizedMappingFile>();
        }
        this.dslFiles.add( file );

        this.resource = null;
    }

    /**
     * Add a ruleflow (.rfm) asset to this package.
     */
    public void addRuleFlow(Reader processSource) {
        addProcessFromXml( processSource );
    }

    public void addProcessFromXml(Resource resource) throws IOException {
        this.resource = resource;

        ProcessBuilder processBuilder = new ProcessBuilder( this );
        try {
            processBuilder.addProcessFromFile( resource.getReader(),
                                               resource );
            this.results.addAll( processBuilder.getErrors() );
        } catch ( Exception e ) {
            if ( e instanceof RuntimeException ) {
                throw (RuntimeException) e;
            }
            this.results.add( new RuleFlowLoadError( "Unable to load the rule flow.",
                                                     e ) );
        }

        this.results = getResults( this.results );
        this.resource = null;
    }

    public void addProcessFromXml(Reader processSource) {
        this.resource = new ReaderResource( processSource );

        ProcessBuilder processBuilder = new ProcessBuilder( this );
        try {
            processBuilder.addProcessFromFile( processSource,
                                               this.resource );
            this.results.addAll( processBuilder.getErrors() );
        } catch ( Exception e ) {
            if ( e instanceof RuntimeException ) {
                throw (RuntimeException) e;
            }
            this.results.add( new RuleFlowLoadError( "Unable to load the rule flow.",
                                                     e ) );
        }

        this.results = getResults( this.results );
        this.resource = null;
    }

    public void addKnowledgeResource(Resource resource,
                                     ResourceType type,
                                     ResourceConfiguration configuration) {
        try {
            if ( ResourceType.DRL.equals( type ) ) {
                ((InternalResource) resource).setResourceType( type );
                addPackageFromDrl( resource );
            } else if ( ResourceType.DSLR.equals( type ) ) {
                ((InternalResource) resource).setResourceType( type );
                addPackageFromDslr( resource );
            } else if ( ResourceType.DSL.equals( type ) ) {
                ((InternalResource) resource).setResourceType( type );
                addDsl( resource );
            } else if ( ResourceType.XDRL.equals( type ) ) {
                ((InternalResource) resource).setResourceType( type );
                addPackageFromXml( resource );
            } else if ( ResourceType.BRL.equals( type ) ) {
                ((InternalResource) resource).setResourceType( type );
                addPackageFromBrl( resource );
            } else if ( ResourceType.DRF.equals( type ) ) {
                ((InternalResource) resource).setResourceType( type );
                addProcessFromXml( resource );
            } else if ( ResourceType.BPMN2.equals( type ) ) {
                ((InternalResource) resource).setResourceType( type );
                BPMN2ProcessFactory.configurePackageBuilder(this);
                addProcessFromXml( resource );
            } else if ( ResourceType.DTABLE.equals( type ) ) {
                ((InternalResource) resource).setResourceType( type );
                DecisionTableConfiguration dtableConfiguration = (DecisionTableConfiguration) configuration;

                String string = DecisionTableFactory.loadFromInputStream( resource.getInputStream(),
                                                                          dtableConfiguration );
                addPackageFromDrl( new StringReader( string ) );
            } else if ( ResourceType.PKG.equals( type ) ) {
                InputStream is = resource.getInputStream();
                Package pkg = (Package) DroolsStreamUtils.streamIn( is, this.configuration.getClassLoader() );
                is.close();
                addPackage( pkg );
            } else if ( ResourceType.CHANGE_SET.equals( type ) ) {
                ((InternalResource) resource).setResourceType( type );
                XmlChangeSetReader reader = new XmlChangeSetReader( this.configuration.getSemanticModules() );
                if ( resource instanceof ClassPathResource ) {
                    reader.setClassLoader( ((ClassPathResource) resource).getClassLoader(),  ((ClassPathResource) resource).getClazz() );
                } else {
                    reader.setClassLoader( this.configuration.getClassLoader() , null);
                }
                ChangeSet changeSet = reader.read( resource.getReader() );
                if ( changeSet == null ) {
                    // @TODO should log an error
                }
                for ( Resource nestedResource : changeSet.getResourcesAdded() ) {
                    InternalResource iNestedResourceResource = (InternalResource) nestedResource;
                    if ( iNestedResourceResource.isDirectory() ) {
                        this.resourceDirectories.add( iNestedResourceResource );
                        for ( Resource childResource : iNestedResourceResource.listResources() ) {
                            if ( ((InternalResource) childResource).isDirectory() ) {
                                continue; // ignore sub directories
                            }
                            ((InternalResource) childResource).setResourceType( iNestedResourceResource.getResourceType() );
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
            } else {
                ResourceTypeBuilder builder = ResourceTypeBuilderRegistry.getInstance().getResourceTypeBuilder( type );
                if ( builder != null ) {
                    builder.setPackageBuilder( this );
                    builder.addKnowledgeResource( resource,
                                                  type,
                                                  configuration );
                } else {
                    throw new RuntimeException( "Unknown resource type: " + type );
                }
            }
        } catch ( RuntimeException e ) {
            throw e;
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    private Set<Resource> resourceDirectories = new HashSet<Resource>();

    /**
     * This adds a package from a Descr/AST This will also trigger a compile, if
     * there are any generated classes to compile of course.
     */
    public void addPackage(final PackageDescr packageDescr) {
        validateUniqueRuleNames( packageDescr );

        String dialectName = this.defaultDialect;
        // see if this packageDescr overrides the current default dialect
        for ( Iterator it = packageDescr.getAttributes().iterator(); it.hasNext(); ) {
            AttributeDescr value = (AttributeDescr) it.next();
            if ( "dialect".equals( value.getName() ) ) {
                dialectName = value.getValue();
                break;
            }
        }

        if ( isEmpty( packageDescr.getNamespace() ) ) {
            packageDescr.setNamespace( this.configuration.getDefaultPackageName() );
        }
        if ( !checkNamespace( packageDescr.getNamespace() ) ) {
            return;
        }

        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( packageDescr.getNamespace() );
        if ( pkgRegistry == null ) {
            // initialise the package and namespace if it hasn't been used before
            pkgRegistry = newPackage( packageDescr );
        } else {
            // merge into existing package
            mergePackage( packageDescr );
        }

        // set the default dialect for this package
        pkgRegistry.setDialect( dialectName );

        // only try to compile if there are no parse errors
        if ( !hasErrors() ) {
            for ( final FactTemplateDescr factTempl : packageDescr.getFactTemplates() ) {
                addFactTemplate( packageDescr,
                                 factTempl );
            }

            if ( !packageDescr.getFunctions().isEmpty() ) {

                for ( final Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
                    FunctionDescr functionDescr = (FunctionDescr) it.next();
                    if ( isEmpty( functionDescr.getNamespace() ) ) {
                        // make sure namespace is set on components
                        functionDescr.setNamespace( packageDescr.getNamespace() );
                    }
                    if ( isEmpty( functionDescr.getDialect() ) ) {
                        // make sure namespace is set on components
                        functionDescr.setDialect( pkgRegistry.getDialect() );
                    }
                    preCompileAddFunction( functionDescr );
                }

                // iterate and compile
                for ( final Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
                    // inherit the dialect from the package
                    FunctionDescr functionDescr = (FunctionDescr) it.next();
                    addFunction( functionDescr );
                }

                // We need to compile all the functions now, so scripting
                // languages like mvel can find them
                compileAll();

                for ( final Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
                    FunctionDescr functionDescr = (FunctionDescr) it.next();
                    postCompileAddFunction( functionDescr );
                }
            }

            // iterate and compile
            for ( final Iterator it = packageDescr.getRules().iterator(); it.hasNext(); ) {
                RuleDescr ruleDescr = (RuleDescr) it.next();
                if ( isEmpty( ruleDescr.getNamespace() ) ) {
                    // make sure namespace is set on components
                    ruleDescr.setNamespace( packageDescr.getNamespace() );
                }
                if ( isEmpty( ruleDescr.getDialect() ) ) {
                    ruleDescr.addAttribute( new AttributeDescr( "dialect",
                                                                pkgRegistry.getDialect() ) );
                }
                addRule( ruleDescr );
            }
        }

        compileAll();
        reloadAll();
        updateResults();

        // iterate and compile
        if ( this.ruleBase != null ) {
            for ( final Iterator it = packageDescr.getRules().iterator(); it.hasNext(); ) {
                RuleDescr ruleDescr = (RuleDescr) it.next();
                pkgRegistry = this.pkgRegistryMap.get( ruleDescr.getNamespace() );
                this.ruleBase.addRule( pkgRegistry.getPackage(),
                                       pkgRegistry.getPackage().getRule( ruleDescr.getName() ) );
            }
        }
    }

    /**
     * This checks to see if it should all be in the one namespace.
     */
    private boolean checkNamespace(String newName) {
        if ( this.configuration == null ) return true;
        if ( (!this.pkgRegistryMap.isEmpty()) && (!this.pkgRegistryMap.containsKey( newName )) ) {
            return this.configuration.isAllowMultipleNamespaces();
        }
        return true;
    }

    public boolean isEmpty(String string) {
        return (string == null || string.trim().length() == 0);
    }

    public void updateResults() {
        // some of the rules and functions may have been redefined     
        this.results = getResults( this.results );
    }

    public void compileAll() {
        for ( PackageRegistry pkgRegistry : this.pkgRegistryMap.values() ) {
            pkgRegistry.compileAll();
        }
    }

    public void reloadAll() {
        for ( PackageRegistry pkgRegistry : this.pkgRegistryMap.values() ) {
            pkgRegistry.getDialectRuntimeRegistry().onBeforeExecute();
        }
    }

    private List getResults(List results) {
        for ( PackageRegistry pkgRegistry : this.pkgRegistryMap.values() ) {
            results = pkgRegistry.getDialectCompiletimeRegistry().addResults( results );
        }
        return results;
    }

    public synchronized void addPackage(final Package newPkg) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( newPkg.getName() );
        Package pkg = null;
        if ( pkgRegistry != null ) {
            pkg = pkgRegistry.getPackage();
        }

        //            // create new base package if it doesn't exist, as we always merge the newPkg into the existing one, 
        //            // to isolate the base package from further possible changes to newPkg.
        //            if ( pkg == null ) {
        //                // create a new package, use the same parent classloader as the incoming new package
        //                pkg = new Package( newPkg.getName(),
        //                                   new MapBackedClassLoader( newPkg.getPackageScopeClassLoader().getParent() ) );
        //                //newPkg.getPackageScopeClassLoader() );
        //                pkgs.put( pkg.getName(),
        //                          pkg );
        //                // add the dialect registry composite classloader (which uses the above classloader as it's parent)
        //                this.packageClassLoader.addClassLoader( pkg.getDialectRuntimeRegistry().getClassLoader() );
        //            }

        if ( pkg == null ) {
            pkg = newPackage( new PackageDescr( newPkg.getName() ) ).getPackage();
        }

        // first merge anything related to classloader re-wiring
        pkg.getDialectRuntimeRegistry().merge( newPkg.getDialectRuntimeRegistry(),
                                               this.rootClassLoader );
        if ( newPkg.getFunctions() != null ) {
            for ( Map.Entry<String, Function> entry : newPkg.getFunctions().entrySet() ) {
                pkg.addFunction( entry.getValue() );
            }
        }
        pkg.getClassFieldAccessorStore().merge( newPkg.getClassFieldAccessorStore() );
        pkg.getDialectRuntimeRegistry().onBeforeExecute();

        // we have to do this before the merging, as it does some classloader resolving
        TypeDeclaration lastType = null;
        try {
            // Add the type declarations to the RuleBase
            if ( newPkg.getTypeDeclarations() != null ) {
                // add type declarations
                for ( TypeDeclaration type : newPkg.getTypeDeclarations().values() ) {
                    lastType = type;
                    type.setTypeClass( this.rootClassLoader.loadClass( type.getTypeClassName() ) );
                }
            }
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeDroolsException( "unable to resolve Type Declaration class '" + lastType.getTypeName() + "'" );
        }

        // now merge the new package into the existing one
        mergePackage( pkg,
                      newPkg );

    }

    /**
     * Merge a new package with an existing package.
     * Most of the work is done by the concrete implementations,
     * but this class does some work (including combining imports, compilation data, globals,
     * and the actual Rule objects into the package).
     */
    private void mergePackage(final Package pkg,
                              final Package newPkg) {
        // Merge imports
        final Map<String, ImportDeclaration> imports = pkg.getImports();
        imports.putAll( newPkg.getImports() );

        String lastType = null;
        try {
            // merge globals
            if ( newPkg.getGlobals() != null && newPkg.getGlobals() != Collections.EMPTY_MAP ) {
                Map<String, String> globals = pkg.getGlobals();
                // Add globals
                for ( final Map.Entry<String, String> entry : newPkg.getGlobals().entrySet() ) {
                    final String identifier = entry.getKey();
                    final String type = entry.getValue();
                    lastType = type;
                    if ( globals.containsKey( identifier ) && !globals.get( identifier ).equals( type ) ) {
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
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeDroolsException( "Unable to resolve class '" + lastType + "'" );
        }

        // merge the type declarations
        if ( newPkg.getTypeDeclarations() != null ) {
            // add type declarations
            for ( TypeDeclaration type : newPkg.getTypeDeclarations().values() ) {
                // @TODO should we allow overrides? only if the class is not in use.
                if ( !pkg.getTypeDeclarations().containsKey( type.getTypeName() ) ) {
                    // add to package list of type declarations
                    pkg.addTypeDeclaration( type );
                }
            }
        }

        final Rule[] newRules = newPkg.getRules();
        for ( int i = 0; i < newRules.length; i++ ) {
            final Rule newRule = newRules[i];

            pkg.addRule( newRule );
        }

        //Merge The Rule Flows
        if ( newPkg.getRuleFlows() != null ) {
            final Map flows = newPkg.getRuleFlows();
            for ( final Iterator iter = flows.values().iterator(); iter.hasNext(); ) {
                final Process flow = (Process) iter.next();
                pkg.addProcess( flow );
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
        for ( final RuleDescr rule : packageDescr.getRules() ) {
            final String name = rule.getName();
            if ( names.contains( name ) ) {
                this.results.add( new ParserError( "Duplicate rule name: " + name,
                                                   rule.getLine(),
                                                   rule.getColumn() ) );
            }
            names.add( name );
        }
    }

    private PackageRegistry newPackage(final PackageDescr packageDescr) {
        Package pkg;
        if ( this.ruleBase != null && this.ruleBase.getPackage( packageDescr.getName() ) != null ) {
            // there is a rulebase and it already defines this package so use it.
            pkg = this.ruleBase.getPackage( packageDescr.getName() );
        } else {
            // define a new package
            pkg = new Package( packageDescr.getName() );
            pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( this.rootClassLoader ) );

            // if there is a rulebase then add the package.
            if ( this.ruleBase != null ) {
                this.ruleBase.addPackage( pkg );
                pkg = this.ruleBase.getPackage( packageDescr.getName() );
            } else {
                // the RuleBase will also initialise the 
                pkg.getDialectRuntimeRegistry().onAdd( this.rootClassLoader );
            }
        }

        PackageRegistry pkgRegistry = new PackageRegistry( this,
                                                           pkg );

        // add default import for this namespace
        pkgRegistry.addImport( packageDescr.getNamespace() + ".*" );

        this.pkgRegistryMap.put( packageDescr.getName(),
                                 pkgRegistry );

        mergePackage( packageDescr );

        return pkgRegistry;
    }

    private void mergePackage(final PackageDescr packageDescr) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( packageDescr.getNamespace() );

        for ( final ImportDescr importEntry : packageDescr.getImports() ) {
            pkgRegistry.addImport( importEntry.getTarget() );
        }

        processTypeDeclarations( packageDescr );

        for ( final FunctionImportDescr functionImport : packageDescr.getFunctionImports() ) {
            String importEntry = functionImport.getTarget();
            pkgRegistry.addStaticImport( importEntry );
            pkgRegistry.getPackage().addStaticImport( importEntry );
        }

        for ( final GlobalDescr global : packageDescr.getGlobals() ) {
            final String identifier = global.getIdentifier();
            final String className = global.getType();

            Class< ? > clazz;
            try {
                clazz = pkgRegistry.getTypeResolver().resolveType( className );
                pkgRegistry.getPackage().addGlobal( identifier,
                                                    clazz );
                this.globals.put( identifier,
                                  clazz );
            } catch ( final ClassNotFoundException e ) {
                this.results.add( new GlobalError( identifier,
                                                   global.getLine() ) );
                e.printStackTrace();
            }
        }

    }

    /**
     * @param packageDescr
     */
    private void processTypeDeclarations(final PackageDescr packageDescr) {
        PackageRegistry defaultRegistry = this.pkgRegistryMap.get( packageDescr.getNamespace() );

        PackageRegistry pkgRegistry = null;
        for ( TypeDeclarationDescr typeDescr : packageDescr.getTypeDeclarations() ) {
            // make sure namespace is set on components
            if ( isEmpty( typeDescr.getNamespace() ) ) {
                typeDescr.setNamespace( packageDescr.getNamespace() );
            }
            pkgRegistry = this.pkgRegistryMap.get( typeDescr.getNamespace() );

            TypeDeclaration type = new TypeDeclaration( typeDescr.getTypeName() );
            if ( resource != null && ((InternalResource) resource).hasURL() ) {
                type.setResource( this.resource );
            }

            // is it a regular fact or an event?
            String role = typeDescr.getMetaAttribute( TypeDeclaration.Role.ID );
            if ( role != null ) {
                type.setRole( TypeDeclaration.Role.parseRole( role ) );
            }

            // is it a POJO or a template?
            String templateName = typeDescr.getMetaAttribute( TypeDeclaration.ATTR_TEMPLATE );
            if ( templateName != null ) {
                type.setFormat( TypeDeclaration.Format.TEMPLATE );
                FactTemplate template = pkgRegistry.getPackage().getFactTemplate( templateName );
                if ( template != null ) {
                    type.setTypeTemplate( template );
                } else {
                    this.results.add( new TypeDeclarationError( "Template not found '" + template + "' for type '" + type.getTypeName() + "'",
                                                                typeDescr.getLine() ) );
                    continue;
                }
            } else {
                String className = typeDescr.getMetaAttribute( TypeDeclaration.ATTR_CLASS );
                if ( className == null ) {
                    className = type.getTypeName();
                }
                type.setFormat( TypeDeclaration.Format.POJO );
                Class clazz;
                try {
                    if ( typeDescr.getFields().size() > 0 ) {
                        // generate the bean if its needed
                        generateDeclaredBean( typeDescr,
                                              type,
                                              pkgRegistry );
                    }
                    clazz = pkgRegistry.getTypeResolver().resolveType( className );
                    type.setTypeClass( clazz );
                    if ( type.getTypeClassDef() != null ) {
                        try {
                            buildFieldAccessors( type,
                                                 pkgRegistry );
                        } catch ( Exception e ) {
                            this.results.add( new TypeDeclarationError( "Error creating field accessors for '" + className + "' for type '" + type.getTypeName() + "'",
                                                                        typeDescr.getLine() ) );
                            continue;
                        }
                    }
                } catch ( final ClassNotFoundException e ) {

                    this.results.add( new TypeDeclarationError( "Class not found '" + className + "' for type '" + type.getTypeName() + "'",
                                                                typeDescr.getLine() ) );
                    continue;
                }
            }

            String timestamp = typeDescr.getMetaAttribute( TypeDeclaration.ATTR_TIMESTAMP );
            if ( timestamp != null ) {
                type.setTimestampAttribute( timestamp );
                ClassDefinition cd = type.getTypeClassDef();
                ClassFieldAccessorStore store = pkgRegistry.getPackage().getClassFieldAccessorStore();
                InternalReadAccessor extractor = store.getReader( type.getTypeClass().getName(),
                                                                  timestamp,
                                                                  type.new TimestampAccessorSetter() );
            }
            String duration = typeDescr.getMetaAttribute( TypeDeclaration.ATTR_DURATION );
            if ( duration != null ) {
                type.setDurationAttribute( duration );
                ClassDefinition cd = type.getTypeClassDef();
                ClassFieldAccessorStore store = pkgRegistry.getPackage().getClassFieldAccessorStore();
                InternalReadAccessor extractor = store.getReader( type.getTypeClass().getName(),
                                                                  duration,
                                                                  type.new DurationAccessorSetter() );
            }
            String expiration = typeDescr.getMetaAttribute( TypeDeclaration.ATTR_EXPIRE );
            if ( expiration != null ) {
                if ( timeParser == null ) {
                    timeParser = new TimeIntervalParser();
                }
                type.setExpirationOffset( timeParser.parse( expiration )[0].longValue() );
            }

            boolean dynamic = typeDescr.getMetaAttributes().containsKey( TypeDeclaration.ATTR_PROP_CHANGE_SUPPORT );
            type.setDynamic( dynamic );

            pkgRegistry.getPackage().addTypeDeclaration( type );
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
    private final void buildFieldAccessors(final TypeDeclaration type,
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
        for ( FieldDefinition attrDef : cd.getFieldsDefinitions() ) {
            ClassFieldAccessor accessor = store.getAccessor( cd.getDefinedClass().getName(),
                                                             attrDef.getName() );
            attrDef.setReadWriteAccessor( accessor );
        }
    }

    /**
     * Generates a bean, and adds it to the composite class loader that
     * everything is using.
     * 
     */
    private void generateDeclaredBean(TypeDeclarationDescr typeDescr,
                                      TypeDeclaration type,
                                      PackageRegistry pkgRegistry) {
        // need to fix classloader?
        ClassBuilder cb = new ClassBuilder();
        String fullName = typeDescr.getNamespace() + "." + typeDescr.getTypeName();
        // generated beans should be serializable
        ClassDefinition def = new ClassDefinition( fullName,
                                                   Object.class.getName(),
                                                   new String[]{Serializable.class.getName()} );
        Map<String, TypeFieldDescr> flds = typeDescr.getFields();
        try {
            for ( TypeFieldDescr field : flds.values() ) {
                String fullFieldType = pkgRegistry.getTypeResolver().resolveType( field.getPattern().getObjectType() ).getName();
                FieldDefinition fieldDef = new FieldDefinition( field.getFieldName(),
                                                                fullFieldType );
                // field is marked as PK
                boolean isKey = field.getMetaAttributes().containsKey( "key" );
                fieldDef.setKey( isKey );
                def.addField( fieldDef );
            }

            byte[] d = cb.buildClass( def );

            JavaDialectRuntimeData dialect = (JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData( "java" );

            dialect.write( JavaDialectRuntimeData.convertClassToResourcePath( fullName ),
                           d );

            type.setTypeClassDef( def );
        } catch ( Exception e ) {
            e.printStackTrace();
            this.results.add( new TypeDeclarationError( "Unable to create a class for declared type " + fullName + ": " + e.getMessage() + ";",
                                                        typeDescr.getLine() ) );
        }
    }

    private void addFunction(final FunctionDescr functionDescr) {
        functionDescr.setResource( this.resource );
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( functionDescr.getNamespace() );
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect( functionDescr.getDialect() );
        dialect.addFunction( functionDescr,
                             pkgRegistry.getTypeResolver(),
                             this.resource );
    }

    private void preCompileAddFunction(final FunctionDescr functionDescr) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( functionDescr.getNamespace() );
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect( functionDescr.getDialect() );
        dialect.preCompileAddFunction( functionDescr,
                                       pkgRegistry.getTypeResolver() );
    }

    private void postCompileAddFunction(final FunctionDescr functionDescr) {
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( functionDescr.getNamespace() );
        Dialect dialect = pkgRegistry.getDialectCompiletimeRegistry().getDialect( functionDescr.getDialect() );
        dialect.postCompileAddFunction( functionDescr,
                                        pkgRegistry.getTypeResolver() );
    }

    private void addFactTemplate(final PackageDescr pkgDescr,
                                 final FactTemplateDescr factTemplateDescr) {
        final List fields = new ArrayList();
        int index = 0;
        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( pkgDescr.getNamespace() );
        for ( final Iterator it = factTemplateDescr.getFields().iterator(); it.hasNext(); ) {
            final FieldTemplateDescr fieldTemplateDescr = (FieldTemplateDescr) it.next();
            FieldTemplate fieldTemplate = null;
            try {
                fieldTemplate = new FieldTemplateImpl( fieldTemplateDescr.getName(),
                                                       index++,
                                                       pkgRegistry.getTypeResolver().resolveType( fieldTemplateDescr.getClassType() ) );
            } catch ( final ClassNotFoundException e ) {
                this.results.add( new FieldTemplateError( pkgRegistry.getPackage(),
                                                          fieldTemplateDescr,
                                                          null,
                                                          "Unable to resolve Class '" + fieldTemplateDescr.getClassType() + "'" ) );
            }
            fields.add( fieldTemplate );
        }

        final FactTemplate factTemplate = new FactTemplateImpl( pkgRegistry.getPackage(),
                                                                factTemplateDescr.getName(),
                                                                (FieldTemplate[]) fields.toArray( new FieldTemplate[fields.size()] ) );
    }

    private void addRule(final RuleDescr ruleDescr) {
        ruleDescr.setResource( resource );

        PackageRegistry pkgRegistry = this.pkgRegistryMap.get( ruleDescr.getNamespace() );

        DialectCompiletimeRegistry ctr = pkgRegistry.getDialectCompiletimeRegistry();
        RuleBuildContext context = new RuleBuildContext( this,
                                                         ruleDescr,
                                                         ctr,
                                                         pkgRegistry.getPackage(),
                                                         ctr.getDialect( pkgRegistry.getDialect() ) );
        this.ruleBuilder.build( context );

        this.results.addAll( context.getErrors() );

        if ( resource != null && ((InternalResource) resource).hasURL() ) {
            context.getRule().setResource( resource );
        }

        context.getDialect().addRule( context );

        if ( this.ruleBase != null ) {
            if ( pkgRegistry.getPackage().getRule( ruleDescr.getName() ) != null ) {
                this.ruleBase.removeRule( pkgRegistry.getPackage(),
                                          pkgRegistry.getPackage().getRule( ruleDescr.getName() ) );
            }
        }

        pkgRegistry.getPackage().addRule( context.getRule() );
    }

    /**
     * @return The compiled package. The package may contain errors, which you
     *         can report on by calling getErrors or printErrors. If you try to
     *         add an invalid package (or rule) to a RuleBase, you will get a
     *         runtime exception.
     * 
     * Compiled packages are serializable.
     */
    public Package getPackage() {
        PackageRegistry pkgRegistry = null;
        if ( !this.pkgRegistryMap.isEmpty() ) {
            pkgRegistry = (PackageRegistry) this.pkgRegistryMap.values().toArray()[0];
        }
        Package pkg = null;
        if ( pkgRegistry != null ) {
            pkg = pkgRegistry.getPackage();
        }
        if ( hasErrors() && pkg != null ) {
            pkg.setError( getErrors().toString() );
        }
        return pkg;
    }

    public Package[] getPackages() {
        Package[] pkgs = new Package[this.pkgRegistryMap.size()];
        int i = 0;
        String errors = null;
        if ( !getErrors().isEmpty() ) {
            errors = getErrors().toString();
        }
        for ( PackageRegistry pkgRegistry : this.pkgRegistryMap.values() ) {
            Package pkg = pkgRegistry.getPackage();
            pkg.getDialectRuntimeRegistry().onBeforeExecute();
            if ( errors != null ) {
                pkg.setError( errors );
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
        return this.pkgRegistryMap.get( name );
    }

    public Map<String, PackageRegistry> getPackageRegistry() {
        return this.pkgRegistryMap;
    }
    
    public DateFormats getDateFormats() {
        return this.dateFormats;
    }    

    /**
     * Returns an expander for DSLs (only if there is a DSL configured for this package).
     */
    public DefaultExpander getDslExpander() {
        DefaultExpander expander = new DefaultExpander();
        if ( this.dslFiles == null || this.dslFiles.isEmpty() ) {
            return null;
        }
        for ( DSLMappingFile file : this.dslFiles ) {
            expander.addDSLMapping( file.getMapping() );
        }
        return expander;
    }

    public Map<String, Class< ? >> getGlobals() {
        return this.globals;
    }

    /**
     * This will return true if there were errors in the package building and
     * compiling phase
     */
    public boolean hasErrors() {
        return !this.results.isEmpty();
    }

    /**
     * @return A list of Error objects that resulted from building and compiling
     *         the package.
     */
    public PackageBuilderErrors getErrors() {
        return new PackageBuilderErrors( this.results.toArray( new DroolsError[this.results.size()] ) );
    }

    /**
     * Reset the error list. This is useful when incrementally building
     * packages. Care should be used when building this, if you clear this when
     * there were errors on items that a rule depends on (eg functions), then
     * you will get spurious errors which will not be that helpful.
     */
    protected void resetErrors() {
        this.results.clear();
    }

    public String getDefaultDialect() {
        return this.defaultDialect;
    }

    public static class MissingPackageNameException extends IllegalArgumentException {
        private static final long serialVersionUID = 400L;

        public MissingPackageNameException(final String message) {
            super( message );
        }

    }

    public static class PackageMergeException extends IllegalArgumentException {
        private static final long serialVersionUID = 400L;

        public PackageMergeException(final String message) {
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

        public void addError(final CompilationProblem err) {
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
            if ( this.errors.size() == 0 ) {
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

        public RuleErrorHandler(final BaseDescr ruleDescr,
                                final Rule rule,
                                final String message) {
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

    public static class ProcessErrorHandler extends ErrorHandler {

        private BaseDescr descr;

        private Process   process;

        public ProcessErrorHandler(final BaseDescr ruleDescr,
                                   final Process process,
                                   final String message) {
            this.descr = ruleDescr;
            this.process = process;
            this.message = message;
        }

        public DroolsError getError() {
            return new ProcessBuildError( this.process,
                                          this.descr,
                                          collectCompilerProblems(),
                                          this.message );
        }

    }

    /**
     * There isn't much point in reporting invoker errors, as they are no help.
     */
    public static class RuleInvokerErrorHandler extends RuleErrorHandler {

        public RuleInvokerErrorHandler(final BaseDescr ruleDescr,
                                       final Rule rule,
                                       final String message) {
            super( ruleDescr,
                   rule,
                   message );
        }
    }

    public static class ProcessInvokerErrorHandler extends ProcessErrorHandler {

        public ProcessInvokerErrorHandler(final BaseDescr processDescr,
                                          final Process process,
                                          final String message) {
            super( processDescr,
                   process,
                   message );
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
            return new FunctionError( this.descr,
                                      collectCompilerProblems(),
                                      this.message );
        }

    }

    public static class SrcErrorHandler extends ErrorHandler {

        public SrcErrorHandler(final String message) {
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

        public SrcError(Object object,
                        String message) {
            this.object = object;
            this.message = message;
        }

        public Object getObject() {
            return this.object;
        }

        public int[] getErrorLines() {
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
            if ( this.object instanceof CompilationProblem[] ) {
                final CompilationProblem[] problem = (CompilationProblem[]) this.object;
                for ( int i = 0; i < problem.length; i++ ) {
                    buf.append( "\t" );
                    buf.append( problem[i] );
                    buf.append( "\n" );
                }
            } else if ( this.object != null ) {
                buf.append( this.object );
            }
            return buf.toString();
        }
    }

    private String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    public DroolsCompositeClassLoader getRootClassLoader() {
        return this.rootClassLoader;
    }
}