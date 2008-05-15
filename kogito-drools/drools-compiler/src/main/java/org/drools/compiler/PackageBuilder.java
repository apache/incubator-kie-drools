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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.factmodel.ClassBuilder;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateImpl;
import org.drools.facttemplates.FieldTemplate;
import org.drools.facttemplates.FieldTemplateImpl;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.process.core.Process;
import org.drools.rule.CompositePackageClassLoader;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.MapBackedClassLoader;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleBuilder;
import org.drools.spi.FieldExtractor;
import org.drools.xml.XmlPackageReader;
import org.drools.xml.XmlProcessReader;
import org.xml.sax.SAXException;

/**
 * This is the main compiler class for parsing and compiling rules and
 * assembling or merging them into a binary Package instance. This can be done
 * by merging into existing binary packages, or totally from source.
 *
 * If you are using the Java dialect the JavaDialectConfiguration will attempt to
 * validate that the specified compiler is in the classpath, using ClassLoader.loasClass(String).
 * If you intented to just Janino sa the compiler you must either overload the compiler
 * property before instantiating this class or the PackageBuilder, or make sure Eclipse is in the
 * classpath, as Eclipse is the default.
 */
public class PackageBuilder {

    private Package                     pkg;

    private List                        results;

    private PackageBuilderConfiguration configuration;

    private TypeResolver                typeResolver;

    private ClassFieldExtractorCache    classFieldExtractorCache;

    private RuleBuilder                 ruleBuilder;

    private Dialect                     dialect;

    private DialectRegistry             dialectRegistry;

	private MapBackedClassLoader generatedBeanClassLoader;

    /**
     * Use this when package is starting from scratch.
     */
    public PackageBuilder() {
        this( null,
              null );
    }

    /**
     * This will allow you to merge rules into this pre existing package.
     */

    public PackageBuilder(final Package pkg) {
        this( pkg,
              null );
    }

    /**
     * Pass a specific configuration for the PackageBuilder
     *
     * PackageBuilderConfiguration is not thread safe and it also contains state. Once it is created and used
     * in one or more PackageBuilders it should be considered immutable. Do not modify its
     * properties while it is being used by a PackageBuilder.
     *
     * @param configuration
     */
    public PackageBuilder(final PackageBuilderConfiguration configuration) {
        this( null,
              configuration );
    }

    /**
     * This allows you to pass in a pre existing package, and a configuration
     * (for instance to set the classloader).
     *
     * @param pkg
     *            A pre existing package (can be null if none exists)
     * @param configuration
     *            Optional configuration for this builder.
     */
    public PackageBuilder(final Package pkg,
                          PackageBuilderConfiguration configuration) {
        if ( configuration == null ) {
            configuration = new PackageBuilderConfiguration();
        }

        this.configuration = configuration;
        this.results = new ArrayList();
        this.pkg = pkg;
        this.classFieldExtractorCache = ClassFieldExtractorCache.getInstance();

        if ( this.pkg != null ) {
        	ClassLoader cl = this.pkg.getDialectDatas().getClassLoader();
            this.typeResolver = new ClassTypeResolver( new HashSet<String>( this.pkg.getImports().keySet() ), cl );
            // make an automatic import for the current package
            this.typeResolver.addImport( this.pkg.getName() + ".*" );
        } else {
//            this.typeResolver = new ClassTypeResolver( new HashSet<String>(),
//                                                       this.configuration.getClassLoader() );
        }

        this.dialectRegistry = this.configuration.buildDialectRegistry();



        this.dialect = this.dialectRegistry.getDialect( this.configuration.getDefaultDialect() );

        if ( this.pkg != null ) {
            this.dialectRegistry.initAll( this );
        }

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
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( reader );
        this.results.addAll( parser.getErrors() );
        addPackage( pkg );
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
        final XmlPackageReader xmlReader = new XmlPackageReader( this.configuration.getSemanticModules() );

        try {
            xmlReader.read( reader );
        } catch ( final SAXException e ) {
            throw new DroolsParserException( e.toString(),
                                             e.getCause() );
        }

        addPackage( xmlReader.getPackageDescr() );
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
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( source,
                                               dsl );
        this.results.addAll( parser.getErrors() );
        addPackage( pkg );
    }

    /**
     * Add a ruleflow (.rfm) asset to this package.
     */
    public void addRuleFlow(Reader processSource) {
        ProcessBuilder processBuilder = new ProcessBuilder( this );

        try {
            processBuilder.addProcessFromFile( processSource );
            this.results.addAll( processBuilder.getErrors() );
        } catch ( Exception e ) {
            if ( e instanceof RuntimeException ) {
                throw (RuntimeException) e;
            }
            this.results.add( new RuleFlowLoadError( "Unable to load the rule flow.",
                                                     e ) );
        }

        this.results = this.dialectRegistry.addResults( this.results );
    }

    public void addProcessFromXml(Reader reader) {
        ProcessBuilder processBuilder = new ProcessBuilder( this );
        XmlProcessReader xmlReader = new XmlProcessReader( this.configuration.getSemanticModules() );
        try {
            Process process = xmlReader.read( reader );
            processBuilder.buildProcess( process );
            this.results.addAll( processBuilder.getErrors() );
        } catch ( Exception e ) {
            if ( e instanceof RuntimeException ) {
                throw (RuntimeException) e;
            }
            this.results.add( new RuleFlowLoadError( "Unable to load the rule flow.",
                                                     e ) );
        }

        this.results = this.dialectRegistry.addResults( this.results );
    }

    private void addSemanticModules() {
        //this.configuration.getSemanticModules();
    }

    /**
     * This adds a package from a Descr/AST This will also trigger a compile, if
     * there are any generated classes to compile of course.
     */
    public void addPackage(final PackageDescr packageDescr) {
        validatePackageName( packageDescr );
        validateUniqueRuleNames( packageDescr );

        String dialectName = null;

        for ( Iterator it = packageDescr.getAttributes().iterator(); it.hasNext(); ) {
            AttributeDescr value = (AttributeDescr) it.next();
            if ( "dialect".equals( value.getName() ) ) {
                dialectName = value.getValue();
                break;
            }
        }

        // If the PackageDescr specifies a dialect then set it.
        if ( dialectName != null ) {
            this.dialect = this.dialectRegistry.getDialect( dialectName );
        } else if ( this.dialect == null ) {
            // If a dialect is not specified and one is not set, then set from the configuration
            this.dialect = this.dialectRegistry.getDialect( this.configuration.getDefaultDialect() );
        }

        if ( this.pkg != null ) {
            mergePackage( packageDescr );
        } else {
            newPackage( packageDescr );
        }

        this.ruleBuilder = new RuleBuilder();

        // only try to compile if there are no parse errors
        if ( !hasErrors() ) {
            for ( final Iterator it = packageDescr.getFactTemplates().iterator(); it.hasNext(); ) {
                addFactTemplate( (FactTemplateDescr) it.next() );
            }

            if ( !packageDescr.getFunctions().isEmpty() ) {

                for ( final Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
                    FunctionDescr functionDescr = (FunctionDescr) it.next();
                    preCompileAddFunction( functionDescr );
                }

                // iterate and compile
                for ( final Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
                    // inherit the dialect from the package
                    FunctionDescr functionDescr = (FunctionDescr) it.next();
                    functionDescr.setDialect( this.dialect.getId() );
                    addFunction( functionDescr );
                }

                // We need to compile all the functions now, so scripting languages like mvel can find them
                this.dialectRegistry.compileAll();

                for ( final Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
                    FunctionDescr functionDescr = (FunctionDescr) it.next();
                    postCompileAddFunction( functionDescr );
                }
            }

            // iterate and compile
            for ( final Iterator it = packageDescr.getRules().iterator(); it.hasNext(); ) {
                addRule( (RuleDescr) it.next() );
            }
        }

        this.dialectRegistry.compileAll();

        // some of the rules and functions may have been redefined
        this.pkg.getDialectDatas().reloadDirty();
        this.results = this.dialectRegistry.addResults( this.results );
    }

    private void validatePackageName(final PackageDescr packageDescr) {
        if ( (this.pkg == null || this.pkg.getName() == null || this.pkg.getName().equals( "" )) && (packageDescr.getName() == null || "".equals( packageDescr.getName() )) ) {
            throw new MissingPackageNameException( "Missing package name for rule package." );
        }
        if ( this.pkg != null && packageDescr.getName() != null && !"".equals( packageDescr.getName() ) && !this.pkg.getName().equals( packageDescr.getName() ) ) {
            throw new PackageMergeException( "Can't merge packages with different names. This package: " + this.pkg.getName() + " - New package: " + packageDescr.getName() );
        }
        return;
    }

    private void validateUniqueRuleNames(final PackageDescr packageDescr) {
        final Set names = new HashSet();
        for ( final Iterator iter = packageDescr.getRules().iterator(); iter.hasNext(); ) {
            final RuleDescr rule = (RuleDescr) iter.next();
            final String name = rule.getName();
            if ( names.contains( name ) ) {
                this.results.add( new ParserError( "Duplicate rule name: " + name,
                                                   rule.getLine(),
                                                   rule.getColumn() ) );
            }
            names.add( name );
        }
    }

    private void newPackage(final PackageDescr packageDescr) {
        this.pkg = new Package( packageDescr.getName(),
                                this.configuration.getClassLoader() );

       ClassLoader cl = this.pkg.getDialectDatas().getClassLoader();


       this.typeResolver = new ClassTypeResolver( new HashSet<String>(), cl );

        this.typeResolver.addImport( this.pkg.getName() + ".*" );

        this.dialectRegistry.initAll( this );

        mergePackage( packageDescr );
    }

    private void mergePackage(final PackageDescr packageDescr) {
        final List imports = packageDescr.getImports();
        for ( final Iterator it = imports.iterator(); it.hasNext(); ) {
            ImportDescr importEntry = (ImportDescr) it.next();
            ImportDeclaration importDecl = new ImportDeclaration( importEntry.getTarget() );
            pkg.addImport( importDecl );
            this.typeResolver.addImport( importDecl.getTarget() );
            this.dialectRegistry.addImport( importDecl.getTarget() );
        }

        processTypeDeclarations( packageDescr );

        for ( final Iterator it = packageDescr.getFunctionImports().iterator(); it.hasNext(); ) {
            String importEntry = ((FunctionImportDescr) it.next()).getTarget();
            this.dialectRegistry.addStaticImport( importEntry );
            this.pkg.addStaticImport( importEntry );
        }

        final List globals = packageDescr.getGlobals();
        for ( final Iterator it = globals.iterator(); it.hasNext(); ) {
            final GlobalDescr global = (GlobalDescr) it.next();
            final String identifier = global.getIdentifier();
            final String className = global.getType();

            Class clazz;
            try {
                clazz = typeResolver.resolveType( className );
                this.pkg.addGlobal( identifier,
                                    clazz );
            } catch ( final ClassNotFoundException e ) {
                this.results.add( new GlobalError( identifier,
                                                   global.getLine() ) );
            }
        }

    }

    /**
     * @param packageDescr
     */
    private void processTypeDeclarations(final PackageDescr packageDescr) {
        for ( TypeDeclarationDescr typeDescr : packageDescr.getTypeDeclarations() ) {
            TypeDeclaration type = new TypeDeclaration( typeDescr.getTypeName() );

            // is it a regular fact or an event?
            String role = typeDescr.getMetaAttribute( TypeDeclaration.Role.ID );
            if ( role != null ) {
                type.setRole( TypeDeclaration.Role.parseRole( role ) );
            }

            // is it a POJO or a template?
            String templateName = typeDescr.getMetaAttribute( TypeDeclaration.ATTR_TEMPLATE );
            if ( templateName != null ) {
                type.setFormat( TypeDeclaration.Format.TEMPLATE );
                FactTemplate template = this.pkg.getFactTemplate( templateName );
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
                	if (typeDescr.getFields().size() > 0) {
                		//generate the bean if its needed
                		generateDeclaredBean(typeDescr);
                	}
                    clazz = typeResolver.resolveType( className );
                    type.setTypeClass( clazz );
                } catch ( final ClassNotFoundException e ) {

	                    this.results.add( new TypeDeclarationError( "Class not found '" + className + "' for type '" + type.getTypeName() + "'",
	                                                                typeDescr.getLine() ) );
	                    continue;
                }
            }

            String timestamp = typeDescr.getMetaAttribute( TypeDeclaration.ATTR_TIMESTAMP );
            if ( timestamp != null ) {
                type.setTimestampAttribute( timestamp );
            }
            String duration = typeDescr.getMetaAttribute( TypeDeclaration.ATTR_DURATION );
            if ( duration != null ) {
                type.setDurationAttribute( duration );
                FieldExtractor extractor = ClassFieldExtractorCache.getInstance().getExtractor( type.getTypeClass(),
                                                                                                duration,
                                                                                                this.configuration.getClassLoader() );
                type.setDurationExtractor( extractor );
            }

            this.pkg.addTypeDeclaration( type );
        }
    }

    /**
     * Generates a bean, and adds it to the composite class loader that
     * everything is using.
     *
     */
    private void generateDeclaredBean(TypeDeclarationDescr typeDescr) {
    	ClassBuilder cb = new ClassBuilder();
    	String fullName = this.pkg.getName() + "." + typeDescr.getTypeName();
    	ClassDefinition def = new ClassDefinition(fullName);
    	Map<String, TypeFieldDescr> flds = typeDescr.getFields();
    	try {
        	for (TypeFieldDescr field : flds.values()) {
        		String fullFieldType = typeResolver.resolveType(field.getPattern().getObjectType()).getName();
    			def.addField(new FieldDefinition(field.getFieldName(), fullFieldType));
    		}

	    	byte[] d = cb.buildClass(def);
	    	if (this.generatedBeanClassLoader == null) {
	    		this.generatedBeanClassLoader = new MapBackedClassLoader(this.configuration.getClassLoader());
	    		CompositePackageClassLoader ccl = (CompositePackageClassLoader) this.pkg.getDialectDatas().getClassLoader();
	    		ccl.addClassLoader(this.generatedBeanClassLoader);
	    	}
	    	generatedBeanClassLoader.addClass(fullName, d);
    	} catch (Exception e) {
    		this.results.add(new TypeDeclarationError("Unable to create a class for declared type " + typeDescr.getTypeName(), typeDescr.getLine()));
    	}
	}

	private void addFunction(final FunctionDescr functionDescr) {
        this.dialect.addFunction( functionDescr,
                                  getTypeResolver() );
    }

    private void preCompileAddFunction(final FunctionDescr functionDescr) {
        this.dialect.preCompileAddFunction( functionDescr,
                                            getTypeResolver() );
    }

    private void postCompileAddFunction(final FunctionDescr functionDescr) {
        this.dialect.postCompileAddFunction( functionDescr,
                                             getTypeResolver() );
    }

    private void addFactTemplate(final FactTemplateDescr factTemplateDescr) {
        final List fields = new ArrayList();
        int index = 0;
        for ( final Iterator it = factTemplateDescr.getFields().iterator(); it.hasNext(); ) {
            final FieldTemplateDescr fieldTemplateDescr = (FieldTemplateDescr) it.next();
            FieldTemplate fieldTemplate = null;
            try {
                fieldTemplate = new FieldTemplateImpl( fieldTemplateDescr.getName(),
                                                       index++,
                                                       getTypeResolver().resolveType( fieldTemplateDescr.getClassType() ) );
            } catch ( final ClassNotFoundException e ) {
                this.results.add( new FieldTemplateError( this.pkg,
                                                          fieldTemplateDescr,
                                                          null,
                                                          "Unable to resolve Class '" + fieldTemplateDescr.getClassType() + "'" ) );
            }
            fields.add( fieldTemplate );
        }

        final FactTemplate factTemplate = new FactTemplateImpl( this.pkg,
                                                                factTemplateDescr.getName(),
                                                                (FieldTemplate[]) fields.toArray( new FieldTemplate[fields.size()] ) );
    }

    private void addRule(final RuleDescr ruleDescr) {
        //this.dialect.init( ruleDescr );

        if ( ruleDescr instanceof QueryDescr ) {
            //ruleDescr.getLhs().insertDescr( 0, baseDescr );
        }

        RuleBuildContext context = new RuleBuildContext( this.configuration,
                                                         pkg,
                                                         ruleDescr,
                                                         this.dialectRegistry,
                                                         this.dialect );
        this.ruleBuilder.build( context );

        this.results.addAll( context.getErrors() );

        context.getDialect().addRule( context );

        this.pkg.addRule( context.getRule() );
    }

    /**
     * @return a Type resolver, lazily. If one does not exist yet, it will be
     *         initialised.
     */
    public TypeResolver getTypeResolver() {
        return this.typeResolver;
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
        if ( this.pkg != null ) {
            this.pkg.getDialectDatas().reloadDirty();
        }
        if ( hasErrors() && this.pkg != null ) {
            this.pkg.setError( getErrors().toString() );
        }
        return this.pkg;
    }

    /**
     * Return the PackageBuilderConfiguration for this PackageBuilder session
     * @return
     *      The PackageBuilderConfiguration
     */
    public PackageBuilderConfiguration getPackageBuilderConfiguration() {
        return this.configuration;
    }

    public DialectRegistry getDialectRegistry() {
        return this.dialectRegistry;
    }

    public Dialect getDefaultDialect() {
        return this.dialect;
    }

    /**
     * Return the ClassFieldExtractorCache, this should only be used internally, and is subject to change
     * @return
     *      the ClsasFieldExtractorCache
     */
    public ClassFieldExtractorCache getClassFieldExtractorCache() {
        return this.classFieldExtractorCache;
    }

    /**
     * This will return true if there were errors in the package building and
     * compiling phase
     */
    public boolean hasErrors() {
        return this.results.size() > 0;
    }

    /**
     * @return A list of Error objects that resulted from building and compiling
     *         the package.
     */
    public PackageBuilderErrors getErrors() {
        return new PackageBuilderErrors( (DroolsError[]) this.results.toArray( new DroolsError[this.results.size()] ) );
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

    private String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }
}