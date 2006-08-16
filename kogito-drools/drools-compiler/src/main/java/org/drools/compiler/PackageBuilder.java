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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.EclipseJavaCompiler;
import org.apache.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.problems.CompilationProblem;
import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.readers.ResourceReader;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateImpl;
import org.drools.facttemplates.FieldTemplate;
import org.drools.facttemplates.FieldTemplateImpl;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.semantics.java.ClassTypeResolver;
import org.drools.semantics.java.FunctionBuilder;
import org.drools.semantics.java.PackageStore;
import org.drools.semantics.java.RuleBuilder;
import org.drools.spi.TypeResolver;
import org.drools.xml.XmlPackageReader;
import org.xml.sax.SAXException;

/**
 * This is the main compiler class for parsing and compiling rules and assembling or merging them into a
 * binary Package instance.
 * This can be done by merging into existing binary packages, or totally from source.
 */
public class PackageBuilder {

    private JavaCompiler                compiler;
    private Package                     pkg;
    private List                        results;
    private PackageStore                packageStoreWrapper;
    private MemoryResourceReader        src;
    private PackageBuilderConfiguration configuration;
    private Map                         errorHandlers;
    private List                        generatedClassList;
    private ClassTypeResolver           typeResolver;
    private ClassFieldExtractorCache    classFieldExtractorCache;

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

    public PackageBuilder(PackageBuilderConfiguration configuration) {
        this( null,
              configuration );
    }

    /**
     * This allows you to pass in a pre existing package, and a configuration (for instance to set the classloader).
     * @param pkg A pre existing package (can be null if none exists)
     * @param configuration Optional configuration for this builder.
     */
    public PackageBuilder(final Package pkg,
                          PackageBuilderConfiguration configuration) {
        if ( configuration == null ) {
            configuration = new PackageBuilderConfiguration();
        }

        this.configuration = configuration;
        loadCompiler();
        this.src = new MemoryResourceReader();
        this.results = new ArrayList();
        this.errorHandlers = new HashMap();
        this.pkg = pkg;
        this.generatedClassList = new ArrayList();
        this.classFieldExtractorCache = new ClassFieldExtractorCache();

        if ( pkg != null ) {
            this.packageStoreWrapper = new PackageStore( pkg.getPackageCompilationData() );
        }
    }

    /**
     * Load a rule package from DRL source.
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
     * @param reader
     * @throws DroolsParserException
     * @throws IOException
     */
    public void addPackageFromXml(final Reader reader) throws DroolsParserException,
                                                      IOException {
        final XmlPackageReader xmlReader = new XmlPackageReader();

        try {
            xmlReader.read( reader );
        } catch ( final SAXException e ) {
            throw new DroolsParserException( e.getCause() );
        }

        addPackage( xmlReader.getPackageDescr() );
    }

    /**
     * Load a rule package from DRL source using the supplied DSL configuration.
     * @param source The source of the rules.
     * @param dsl The source of the domain specific language configuration.
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
     * This adds a package from a Descr/AST 
     * This will also trigger a compile, if there are any generated classes to compile
     * of course.
     */
    public void addPackage(final PackageDescr packageDescr) {

        validatePackageName( packageDescr );
        validateUniqueRuleNames( packageDescr );

        if ( this.pkg != null ) {
            //mergePackage( packageDescr ) ;
            mergePackage( this.pkg,
                          packageDescr );
        } else {
            this.pkg = newPackage( packageDescr );
        }

        //only try to compile if there are no parse errors
        if ( !hasErrors() ) {
            for ( final Iterator it = packageDescr.getFactTemplates().iterator(); it.hasNext(); ) {
                addFactTemplate( (FactTemplateDescr) it.next() );
            }

            //iterate and compile
            for ( final Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
                addFunction( (FunctionDescr) it.next() );
            }

            //iterate and compile
            for ( final Iterator it = packageDescr.getRules().iterator(); it.hasNext(); ) {
                addRule( (RuleDescr) it.next() );
            }
        }

        if ( this.generatedClassList.size() > 0 ) {
            this.compileAll();
        }
    }

    private void validatePackageName(final PackageDescr packageDescr) {
        if ( packageDescr.getName() == null || "".equals( packageDescr.getName() ) ) {

            throw new MissingPackageNameException( "Missing package name for rule package." );
        }
    }

    private void validateUniqueRuleNames(final PackageDescr packageDescr) {
        Set names = new HashSet();
        for ( Iterator iter = packageDescr.getRules().iterator(); iter.hasNext(); ) {
            RuleDescr rule = (RuleDescr) iter.next();
            String name = rule.getName();
            if ( names.contains( name ) ) {
                this.results.add( new ParserError( "Duplicate rule name: " + name,
                                                   rule.getLine(),
                                                   rule.getColumn() ) );
            }
            names.add( name );
        }
    }

    private Package newPackage(final PackageDescr packageDescr) {
        final Package pkg = new Package( packageDescr.getName(),
                                         this.configuration.getClassLoader() );

        this.packageStoreWrapper = new PackageStore( pkg.getPackageCompilationData() );

        mergePackage( pkg,
                      packageDescr );

        return pkg;
    }

    private void mergePackage(final Package pkg,
                              final PackageDescr packageDescr) {
        final List imports = packageDescr.getImports();
        for ( final Iterator it = imports.iterator(); it.hasNext(); ) {
            pkg.addImport( (String) it.next() );
        }

        final TypeResolver typeResolver = new ClassTypeResolver( imports,
                                                                 pkg.getPackageCompilationData().getClassLoader() );

        final Map globals = packageDescr.getGlobals();
        for ( final Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            final String identifier = (String) it.next();
            final String className = (String) globals.get( identifier );

            Class clazz;
            try {
                clazz = typeResolver.resolveType( className );
                pkg.addGlobal( identifier,
                               clazz );
            } catch ( final ClassNotFoundException e ) {
                new GlobalError( identifier );
            }
        }
    }

    /** 
     * This adds a compile "task" for when the compiler of 
     * semantics (JCI) is called later on with compileAll()\
     * which actually does the compiling.
     * The ErrorHandler is required to map the errors back to the 
     * element that caused it.
     */
    private void addClassCompileTask(final String className,
                                     final String text,
                                     final MemoryResourceReader src,
                                     ErrorHandler handler) {

        String fileName = className.replace( '.',
                                             '/' ) + ".java";
        src.add( fileName,
                 text.getBytes() );

        this.errorHandlers.put( fileName,
                                handler );
        this.generatedClassList.add( className );
    }

    private void addFunction(final FunctionDescr functionDescr) {
        final FunctionBuilder buidler = new FunctionBuilder();
        addClassCompileTask( this.pkg.getName() + "." + ucFirst( functionDescr.getName() ),
                             buidler.build( this.pkg,
                                            functionDescr ),
                             this.src,
                             new FunctionErrorHandler( functionDescr,
                                                       "Function Compilation error" ) );

    }

    private void addFactTemplate(final FactTemplateDescr factTemplateDescr) {
        List fields = new ArrayList();
        int index = 0;
        for ( final Iterator it = factTemplateDescr.getFields().iterator(); it.hasNext(); ) {
            FieldTemplateDescr fieldTemplateDescr = (FieldTemplateDescr) it.next();
            FieldTemplate fieldTemplate = null;
            try {
                fieldTemplate = new FieldTemplateImpl( fieldTemplateDescr.getName(),
                                                       index++,
                                                       getTypeResolver().resolveType( fieldTemplateDescr.getClassType() ) );
            } catch ( ClassNotFoundException e ) {
                this.results.add( new FieldTemplateError( this.pkg,
                                                          fieldTemplateDescr,
                                                          null,
                                                          "Unable to resolve Class '" + fieldTemplateDescr.getClassType() + "'" ) );
            }
            fields.add( fieldTemplate );
        }

        FactTemplate factTemplate = new FactTemplateImpl( this.pkg,
                                                          factTemplateDescr.getName(),
                                                          (FieldTemplate[]) fields.toArray( new FieldTemplate[fields.size()] ) );
    }

    private void addRule(final RuleDescr ruleDescr) {

        final String ruleClassName = getUniqueLegalName( this.pkg.getName(),
                                                         ruleDescr.getName(),
                                                         "java",
                                                         this.src );
        ruleDescr.setClassName( ucFirst( ruleClassName ) );

        final RuleBuilder builder = new RuleBuilder( getTypeResolver(),
                                                     classFieldExtractorCache );

        builder.build( this.pkg,
                       ruleDescr );

        this.results.addAll( builder.getErrors() );

        final Rule rule = builder.getRule();

        // Check if there is any code to compile. If so compile it.       
        if ( builder.getRuleClass() != null ) {
            addRuleSemantics( builder,
                              rule,
                              ruleDescr );
        }

        this.pkg.addRule( rule );
    }

    /**
     * @return a Type resolver, lazily. 
     * If one does not exist yet, it will be initialised.
     */
    private TypeResolver getTypeResolver() {
        if ( this.typeResolver == null ) {
            typeResolver = new ClassTypeResolver( pkg.getImports(),
                                                  pkg.getPackageCompilationData().getClassLoader() );
            // make an automatic import for the current package
            typeResolver.addImport( pkg.getName() + ".*" );
            typeResolver.addImport( "java.lang.*" );
        }
        return this.typeResolver;
    }

    /**
     * This will setup the semantic components of the rule for compiling later on.
     * It will not actually call the compiler
     */
    private void addRuleSemantics(final RuleBuilder builder,
                                  final Rule rule,
                                  final RuleDescr ruleDescr) {
        // The compilation result is for th entire rule, so difficult to associate with any descr
        addClassCompileTask( this.pkg.getName() + "." + ruleDescr.getClassName(),
                             builder.getRuleClass(),
                             this.src,
                             new RuleErrorHandler( ruleDescr,
                                                   rule,
                                                   "Rule Compilation error" ) );

        for ( final Iterator it = builder.getInvokers().keySet().iterator(); it.hasNext(); ) {
            final String className = (String) it.next();

            // Check if an invoker - returnvalue, predicate, eval or consequence has been associated
            // If so we add it to the PackageCompilationData as it will get wired up on compilation
            final Object invoker = builder.getInvokerLookups().get( className );
            if ( invoker != null ) {
                this.pkg.getPackageCompilationData().putInvoker( className,
                                                                 invoker );
            }
            final String text = (String) builder.getInvokers().get( className );

            //System.out.println( className + ":\n" + text );
            final PatternDescr descr = (PatternDescr) builder.getDescrLookups().get( className );
            addClassCompileTask( className,
                                 text,
                                 this.src,
                                 new RuleInvokerErrorHandler( descr,
                                                              rule,
                                                              "Unable to generate rule invoker." ) );

        }
    }

    /**
     * @return The compiled package. The package may contain errors, which you can report on
     * by calling getErrors or printErrors. If you try to add an invalid package (or rule)
     * to a RuleBase, you will get a runtime exception.
     * 
     * Compiled packages are serializable.
     */
    public Package getPackage() {

        if ( hasErrors() ) {
            this.pkg.setError( this.printErrors() );
        }
        return this.pkg;
    }

    /**
     * This actually triggers the compiling of all the resources.
     * Errors are mapped back to the element that originally generated the semantic
     * code.
     */
    private void compileAll() {
        String[] classes = new String[this.generatedClassList.size()];
        this.generatedClassList.toArray( classes );

        final CompilationResult result = this.compiler.compile( classes,
                                                                src,
                                                                this.packageStoreWrapper,
                                                                this.pkg.getPackageCompilationData().getClassLoader() );

        //this will sort out the errors based on what class/file they happened in
        if ( result.getErrors().length > 0 ) {
            for ( int i = 0; i < result.getErrors().length; i++ ) {
                CompilationProblem err = result.getErrors()[i];

                ErrorHandler handler = (ErrorHandler) this.errorHandlers.get( err.getFileName() );
                if ( handler instanceof RuleErrorHandler ) {
                    RuleErrorHandler rh = (RuleErrorHandler) handler;

                }
                handler.addError( err );
            }

            Collection errors = this.errorHandlers.values();
            for ( Iterator iter = errors.iterator(); iter.hasNext(); ) {
                ErrorHandler handler = (ErrorHandler) iter.next();
                if ( handler.isInError() ) {
                    if ( !(handler instanceof RuleInvokerErrorHandler) ) {
                        this.results.add( handler.getError() );
                    } else {
                        //we don't really want to report invoker errors.
                        //mostly as they can happen when there is a syntax error in the RHS
                        //and otherwise, it is a programmatic error in drools itself.
                        System.err.println( "Warning: An error occurred compiling a semantic invoker. Errors should have been reported elsewhere." );
                    }
                }
            }
        }
    }

    /** This will return true if there were errors in the package building and compiling phase */
    public boolean hasErrors() {
        return this.results.size() > 0;
    }

    /**
     * @return A list of Error objects that resulted from building and compiling the package. 
     */
    public DroolsError[] getErrors() {
        return (DroolsError[]) this.results.toArray( new DroolsError[this.results.size()] );
    }

    /**
     * This will pretty print the errors (from getErrors())
     * into lines.
     */
    public String printErrors() {
        final StringBuffer buf = new StringBuffer();
        for ( final Iterator iter = this.results.iterator(); iter.hasNext(); ) {
            final DroolsError err = (DroolsError) iter.next();
            buf.append( err.getMessage() );
            buf.append( "\n" );
        }
        return buf.toString();
    }

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     * 
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    private String getUniqueLegalName(final String packageName,
                                      final String name,
                                      final String ext,
                                      final ResourceReader src) {
        // replaces all non alphanumeric or $ chars with _
        String newName = "Rule_" + name.replaceAll( "[^\\w$]",
                                                    "_" );

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        boolean exists = true;
        while ( exists ) {

            counter++;
            final String fileName = packageName.replaceAll( "\\.",
                                                            "/" ) + newName + "_" + counter + ext;

            exists = src.isAvailable( fileName );
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 ) {
            newName = newName + "_" + counter;
        }

        return newName;
    }

    private void loadCompiler() {
        switch ( configuration.getCompiler() ) {
            case PackageBuilderConfiguration.JANINO : {
                if ( !"1.4".intern().equals( configuration.getJavaLanguageLevel() ) ) throw new RuntimeDroolsException( "Incompatible Java language level with selected compiler" );
                compiler = JavaCompilerFactory.getInstance().createCompiler( "janino" );
                break;
            }
            case PackageBuilderConfiguration.ECLIPSE :
            default : {
                EclipseJavaCompilerSettings eclipseSettings = new EclipseJavaCompilerSettings();
                eclipseSettings.getMap().put( "org.eclipse.jdt.core.compiler.codegen.targetPlatform",
                                              configuration.getJavaLanguageLevel() );
                eclipseSettings.getMap().put( "org.eclipse.jdt.core.compiler.source",
                                              configuration.getJavaLanguageLevel() );
                compiler = new EclipseJavaCompiler( eclipseSettings );
                break;
            }
        }
    }

    private String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    public static class MissingPackageNameException extends IllegalArgumentException {
        private static final long serialVersionUID = 4056984379574366454L;

        public MissingPackageNameException(final String message) {
            super( message );
        }

    }

    /**
     * This is the super of the error handlers.
     * Each error handler knows how to report a compile error of its type, should it happen.
     * This is needed, as the compiling is done as one
     * hit at the end, and we need to be able to work out what rule/ast element
     * caused the error.
     * 
     * An error handler it created for each class task that is queued to be compiled.
     * This doesn't mean an error has occurred, it just means it *may* occur
     * in the future and we need to be able to map it back to the AST element
     * that originally spawned the code to be compiled.
     */
    public abstract static class ErrorHandler {
        private List     errors  = new ArrayList();
        protected String message;
        private boolean  inError = false;

        /** This needes to be checked if there is infact an error */
        public boolean isInError() {
            return inError;
        }

        public void addError(CompilationProblem err) {
            this.errors.add( err );
            this.inError = true;
        }

        /**
         * 
         * @return A DroolsError object populated as appropriate,
         * should the unthinkable happen and this need to be reported.
         */
        public abstract DroolsError getError();

        /**
         * We must use an error of JCI problem objects.
         * If there are no problems, null is returned.
         * These errors are placed in the DroolsError instances.
         * Its not 1 to 1 with reported errors.
         */
        protected CompilationProblem[] collectCompilerProblems() {
            if ( errors.size() == 0 ) {
                return null;
            } else {
                CompilationProblem[] list = new CompilationProblem[errors.size()];
                errors.toArray( list );
                return list;
            }
        }
    }

    public static class RuleErrorHandler extends ErrorHandler {

        private PatternDescr descr;
        private Rule         rule;

        public RuleErrorHandler(PatternDescr ruleDescr,
                                Rule rule,
                                String message) {
            this.descr = ruleDescr;
            this.rule = rule;
            this.message = message;
        }

        public DroolsError getError() {
            return new RuleError( rule,
                                  descr,
                                  collectCompilerProblems(),
                                  message );
        }

    }

    /**
     * There isn't much point in reporting invoker errors, as
     * they are no help. 
     */
    public static class RuleInvokerErrorHandler extends RuleErrorHandler {

        public RuleInvokerErrorHandler(PatternDescr ruleDescr,
                                       Rule rule,
                                       String message) {
            super( ruleDescr,
                   rule,
                   message );
        }
    }

    public static class FunctionErrorHandler extends ErrorHandler {

        private FunctionDescr descr;

        public FunctionErrorHandler(FunctionDescr functionDescr,
                                    String message) {
            this.descr = functionDescr;
            this.message = message;
        }

        public DroolsError getError() {
            return new FunctionError( descr,
                                      collectCompilerProblems(),
                                      message );
        }

    }

    private static JavaCompiler cachedJavaCompiler = null;

}