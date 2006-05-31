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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.ResourceStore;
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

        this.compiler = getCompiler( configuration.getCompiler() );

        this.configuration = configuration;

        this.src = new MemoryResourceReader();

        this.results = new ArrayList();

        this.pkg = pkg;

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

    public void addPackage(final PackageDescr packageDescr) {

        if ( packageDescr.getName() == null || "".equals( packageDescr.getName() ) ) {

            throw new MissingPackageNameException( "Missing package name for rule package." );
        }

        if ( this.pkg != null ) {
            //mergePackage( packageDescr ) ;
            mergePackage( this.pkg,
                          packageDescr );
        } else {
            this.pkg = newPackage( packageDescr );
        }

        //only try to compile if there are no parse errors
        if ( !hasErrors() ) {
            //iterate and compile
            for ( final Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
                addFunction( (FunctionDescr) it.next() );
            }

            //iterate and compile
            for ( final Iterator it = packageDescr.getRules().iterator(); it.hasNext(); ) {
                addRule( (RuleDescr) it.next() );
            }
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

    private CompilationResult compile(final String className,
                                      final String text,
                                      final MemoryResourceReader src,
                                      final ResourceStore dst) {
        src.add( className.replace( '.',
                                    '/' ) + ".java",
                 text.getBytes() );
        final CompilationResult result = this.compiler.compile( new String[]{className},
                                                     src,
                                                     dst,
                                                     this.pkg.getPackageCompilationData().getClassLoader() );

        return result;
    }

    private void addFunction(final FunctionDescr functionDescr) {
        final FunctionBuilder buidler = new FunctionBuilder();
        final CompilationResult result = compile( this.pkg.getName() + "." + ucFirst( functionDescr.getName() ),
                                            buidler.build( this.pkg,
                                                           functionDescr ),
                                            this.src,
                                            this.packageStoreWrapper );

        if ( result.getErrors().length > 0 ) {
            this.results.add( new FunctionError( functionDescr,
                                                 result.getErrors(),
                                                 "Function Compilation error" ) );
        }
    }

    private void addRule(final RuleDescr ruleDescr) {

        final String ruleClassName = getUniqueLegalName( this.pkg.getName(),
                                                   ruleDescr.getName(),
                                                   "java",
                                                   this.src );
        ruleDescr.SetClassName( ucFirst( ruleClassName ) );

        final RuleBuilder builder = new RuleBuilder();

        builder.build( this.pkg,
                       ruleDescr );

        this.results.addAll( builder.getErrors() );

        final Rule rule = builder.getRule();

        // Check if there is any code to compile. If so compile it.       
        if ( builder.getRuleClass() != null ) {
            compileRule( builder,
                         rule,
                         ruleDescr );
        }

        this.pkg.addRule( rule );
    }

    public void compileRule(final RuleBuilder builder,
                            final Rule rule,
                            final RuleDescr ruleDescr) {
        // The compilation result is for th entire rule, so difficult to associate with any descr
        CompilationResult result = compile( this.pkg.getName() + "." + ruleDescr.getClassName(),
                                            builder.getRuleClass(),
                                            this.src,
                                            this.packageStoreWrapper );

        if ( result.getErrors().length > 0 ) {
            this.results.add( new RuleError( rule,
                                             ruleDescr,
                                             result.getErrors(),
                                             "Rule Compilation error" ) );
        } else {

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

                result = compile( className,
                                  text,
                                  this.src,
                                  this.packageStoreWrapper );

                if ( result.getErrors().length > 0 ) {
                    final PatternDescr descr = (PatternDescr) builder.getDescrLookups().get( className );
                    this.results.add( new RuleError( rule,
                                                     descr,
                                                     result.getErrors(),
                                                     "Rule Compilation error for Invoker" ) );
                }
            }
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

    private JavaCompiler getCompiler(final int compiler) {
        switch ( compiler ) {
            case PackageBuilderConfiguration.JANINO :
                return JavaCompilerFactory.getInstance().createCompiler( "janino" );
            case PackageBuilderConfiguration.ECLIPSE :
            default :
                return JavaCompilerFactory.getInstance().createCompiler( "eclipse" );
        }
    }

    private String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    public static class MissingPackageNameException extends IllegalArgumentException {

        /**
         * 
         */
        private static final long serialVersionUID = 4056984379574366454L;

        public MissingPackageNameException(final String message) {
            super( message );
        }

    }
}