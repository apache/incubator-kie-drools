package org.drools.compiler;

import java.io.IOException;
import java.io.InputStreamReader;
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
import org.drools.semantics.java.PackageStore;
import org.drools.semantics.java.RuleBuilder;
import org.drools.spi.TypeResolver;

public class PackageBuilder {
    private JavaCompiler         compiler = JavaCompilerFactory.getInstance().createCompiler( JavaCompilerFactory.ECLIPSE );

    private Package              pkg;

    private List                 results;

    private PackageStore         packageStoreWrapper;

    private MemoryResourceReader src;

    private ClassLoader          classLoader;

    public PackageBuilder() {
        this( null,
              null );
    }

    public PackageBuilder(Package pkg) {
        this( pkg,
              null );
    }

    public PackageBuilder(ClassLoader parentClassLoader) {
        this( null,
              parentClassLoader );
    }

    public PackageBuilder(Package pkg,
                          ClassLoader classLoader) {
        this.src = new MemoryResourceReader();

        this.results = new ArrayList();

        this.pkg = pkg;

        if ( pkg != null ) {
            this.packageStoreWrapper = new PackageStore( pkg.getPackageCompilationData() );
        }

        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = this.getClass().getClassLoader();
            }
            this.classLoader = classLoader;
        } else {
            this.classLoader = classLoader;
        }
    }

    /**
     * Load a rule package from DRL source.
     * @param reader
     * @throws DroolsParserException
     * @throws IOException
     */
    public void addPackageFromDrl(Reader reader) throws DroolsParserException, IOException {
        DrlParser parser = new DrlParser();
        addPackage( parser.parse( reader ) );        
    }
    
    /**
     * Load a rule package from DRL source using the supplied DSL configuration.
     * @param source The source of the rules.
     * @param dsl The source of the domain specific language configuration.
     * @throws DroolsParserException
     * @throws IOException
     */
    public void addPackageFromDrl(Reader source,
                                  Reader dsl) throws DroolsParserException, IOException {
        DrlParser parser = new DrlParser();
        addPackage( parser.parse( source, dsl ) );
    }    
    
    public void addPackage(PackageDescr packageDescr) {

        if ( this.pkg != null ) {
            //mergePackage( packageDescr ) ;
            mergePackage( this.pkg,
                          packageDescr );
        } else {
            this.pkg = newPackage( packageDescr );
        }

        //iterate and compile
        for ( Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
            addFunction( (FunctionDescr) it.next() );
        }

        //iterate and compile
        for ( Iterator it = packageDescr.getRules().iterator(); it.hasNext(); ) {
            addRule( (RuleDescr) it.next() );
        }
    }

    private Package newPackage(PackageDescr packageDescr) {
        Package pkg = new Package( packageDescr.getName(),
                                   this.classLoader );
        
        this.packageStoreWrapper = new PackageStore( pkg.getPackageCompilationData() );

        mergePackage( pkg,
                      packageDescr );

        return pkg;
    }

    private void mergePackage(Package pkg,
                              PackageDescr packageDescr) {
        List imports = packageDescr.getImports();
        for ( Iterator it = imports.iterator(); it.hasNext(); ) {
            pkg.addImport( (String) it.next() );
        }

        TypeResolver typeResolver = new ClassTypeResolver( imports,
                                                           pkg.getPackageCompilationData().getClassLoader() );

        Map globals = packageDescr.getGlobals();
        for ( Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            String className = (String) globals.get( identifier );

            Class clazz;
            try {
                clazz = typeResolver.resolveType( className );
                pkg.addGlobalDeclaration( identifier,
                                          clazz );
            } catch ( ClassNotFoundException e ) {
                new GlobalError( identifier );
            }
        }
    }

    private  CompilationResult compile(String className,
                              String text,
                              MemoryResourceReader src,
                              ResourceStore dst) {
        src.addFile( className.replace( '.',
                                        '/' ) + ".java",
                     text.toCharArray() );
        CompilationResult result = compiler.compile( new String[]{className},
                                                     src,
                                                     dst,
                                                     pkg.getPackageCompilationData().getClassLoader() );

        return result;
    }

    private void addFunction(FunctionDescr rule) {
        //@todo        
    }

    private void addRule(RuleDescr ruleDescr) {

        String ruleClassName = getUniqueLegalName( this.pkg.getName(),
                                                   ruleDescr.getName(),
                                                   "java",
                                                   src );
        ruleDescr.SetClassName( ucFirst( ruleClassName ) );

        RuleBuilder builder = new RuleBuilder();

        builder.build( this.pkg,
                       ruleDescr );

        this.results.addAll( builder.getErrors() );

        Rule rule = builder.getRule();

        // The compilation result is for th entire rule, so difficult to associate with any descr
        CompilationResult result = compile( this.pkg.getName() + "." + ruleDescr.getClassName(),
                                            builder.getRuleClass(),
                                            src,
                                            this.packageStoreWrapper );

        if ( result.getErrors().length > 0 ) {
            this.results.add( new RuleError( rule,
                                             null,
                                             result.getErrors(),
                                             "Compilation error" ) );
        }

        for ( Iterator it = builder.getInvokers().keySet().iterator(); it.hasNext(); ) {
            String className = (String) it.next();

            // Check if an invoker - returnvalue, predicate, eval or consequence has been associated
            // If so we add it to the PackageCompilationData as it will get wired up on compilation
            Object invoker = builder.getInvokerLookups().get( className );
            if ( invoker != null ) {
                this.pkg.getPackageCompilationData().putInvoker( className,
                                                                 invoker );
            }
            String text = (String) builder.getInvokers().get( className );

            //System.out.println( className + ":\n" + text );

            result = compile( className,
                              text,
                              src,
                              this.packageStoreWrapper );

            if ( result.getErrors().length > 0 ) {
                PatternDescr descr = (PatternDescr) builder.getDescrLookups().get( className );
                this.results.add( new RuleError( rule,
                                                 descr,
                                                 result.getErrors(),
                                                 "Compilation error for Invoker" ) );
            }
        }
        try {
            this.pkg.addRule( rule );
        } catch ( Exception e ) {
            
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
        if (hasErrors()) {
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
        StringBuffer buf = new StringBuffer();
        for ( Iterator iter = this.results.iterator(); iter.hasNext(); ) {
            DroolsError err = (DroolsError) iter.next();
            if (err instanceof RuleError) {
                RuleError ruleErr = (RuleError) err;
                buf.append(ruleErr.getMessage());
                buf.append( "\n" );
            } else {
                buf.append( err );
                buf.append( "\n" );
            }
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
    private String getUniqueLegalName(String packageName,
                                      String name,
                                      String ext,
                                      ResourceReader src) {
        // replaces the first char if its a number and after that all non
        // alphanumeric or $ chars with _
        String newName = name.replaceAll( "(^[0-9]|[^\\w$])",
                                          "_" );

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        boolean exists = true;
        while ( exists ) {

            counter++;
            String fileName = packageName.replaceAll( "\\.",
                                                      "/" ) + newName + "_" + counter + ext;

            exists = src.isAvailable( fileName );
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 ) {
            newName = newName + "_" + counter;
        }

        return newName;
    }

    private String ucFirst(String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }



}
