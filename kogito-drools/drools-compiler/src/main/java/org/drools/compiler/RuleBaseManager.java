package org.drools.compiler;

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

public class RuleBaseManager {
    private JavaCompiler         compiler = JavaCompilerFactory.getInstance().createCompiler( JavaCompilerFactory.ECLIPSE );

    private Package              pkg;

    private List                 results;

    private PackageStore         packageStoreWrapper;

    private MemoryResourceReader src;

    public RuleBaseManager() {
        this( null,
              null );
    }

    public RuleBaseManager(Package pkg) {
        this( pkg,
              null );
    }

    public RuleBaseManager(ClassLoader parentClassLoader) {
        this( null,
              parentClassLoader );
    }

    public RuleBaseManager(Package pkg,
                           ClassLoader parentClassLoader) {
        this.src = new MemoryResourceReader();

        this.results = new ArrayList();

        this.pkg = pkg;

        if ( pkg != null) {
            this.packageStoreWrapper = new PackageStore( pkg.getPackageCompilationData() );
        }

        if ( parentClassLoader == null ) {
            parentClassLoader = Thread.currentThread().getContextClassLoader();
            if ( parentClassLoader == null ) {
                parentClassLoader = this.getClass().getClassLoader();
            }
        }
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
        Package pkg = new Package( packageDescr.getName() );

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

    CompilationResult compile(String className,
                              String text,
                              MemoryResourceReader src,
                              ResourceStore dst) {
        src.addFile( className.replace( '.',
                                        '/' ) + ".java",
                     text.toCharArray() );
        CompilationResult result = compiler.compile( new String[]{className},
                                                     src,
                                                     dst );

        return result;
    }

    void addFunction(FunctionDescr rule) {
        //@todo        
    }

    void addRule(RuleDescr ruleDescr) {

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

        //System.out.println( ruleDescr.getClassName() + ":\n" + builder.getRuleClass() );

        // The compilation result is for th entire rule, so difficult to associate with any descr
        CompilationResult result = compile( pkg.getName() + "." + ruleDescr.getClassName(),
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
                pkg.getPackageCompilationData().putInvoker( className,
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
    }

    public Package getPackage() {
        return this.pkg;
    }

    public DroolsError[] getErrors() {
        return (DroolsError[]) this.results.toArray( new DroolsError[this.results.size()] );
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
