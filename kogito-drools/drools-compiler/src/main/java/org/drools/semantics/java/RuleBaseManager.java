package org.drools.semantics.java;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.stores.ResourceStoreClassLoader;
import org.drools.CheckedDroolsException;
import org.drools.RuleBase;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.EvalCondition;
import org.drools.rule.Package;
import org.drools.rule.PackageCompilationData;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.EvalExpression;
import org.drools.spi.PredicateExpression;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.TypeResolver;

public class RuleBaseManager {
    private final MemoryResourceReader src;

    private JavaCompiler               compiler            = JavaCompilerFactory.getInstance().createCompiler( JavaCompilerFactory.ECLIPSE );

    private int                        counter;

    private Map                        packages;

    private CompilationResult          results;

    private PackageStore               packageStoreWrapper = new PackageStore();

    public RuleBaseManager() {
        this( null );
    }

    public RuleBaseManager(ClassLoader parentClassLoader) {
        this.src = new MemoryResourceReader();

        this.packages = new HashMap();

        if ( parentClassLoader == null ) {
            parentClassLoader = Thread.currentThread().getContextClassLoader();
            if ( parentClassLoader == null ) {
                parentClassLoader = this.getClass().getClassLoader();
            }
        }

        //        this.classLoader = new ResourceStoreClassLoader( parentClassLoader,
        //                                                         new ResourceStore[]{dst} );
    }

    public void addPackage(PackageDescr packageDescr) throws CheckedDroolsException,
                                                     ClassNotFoundException,
                                                     InstantiationException,
                                                     IllegalAccessException {
        if ( this.packages == Collections.EMPTY_MAP ) {
            this.packages = new HashMap();
        }

        Package pkg = (Package) this.packages.get( packageDescr.getName() );
        if ( pkg != null ) {
            //mergePackage( packageDescr ) ;
            mergePackage( pkg,
                          packageDescr );
        } else {
            pkg = newPackage( packageDescr );
            this.packages.put( pkg.getName(),
                               pkg );
        }

        //iterate and compile
        for ( Iterator it = packageDescr.getFunctions().iterator(); it.hasNext(); ) {
            addFunction( packageDescr,
                         (FunctionDescr) it.next() );
        }

        //iterate and compile
        for ( Iterator it = packageDescr.getRules().iterator(); it.hasNext(); ) {
            addRule( pkg,
                     (RuleDescr) it.next() );
        }
    }

    private Package newPackage(PackageDescr packageDescr) throws ClassNotFoundException {
        Package pkg = new Package( packageDescr.getName() );

        mergePackage( pkg,
                      packageDescr );

        return pkg;
    }

    private void mergePackage(Package pkg,
                              PackageDescr packageDescr) throws ClassNotFoundException {
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
            Class clazz = typeResolver.resolveType( className );
            pkg.addGlobalDeclaration( identifier,
                                      clazz );
        }
    }

    CompilationResult compile(String className,
                              String text,
                              ResourceStore dst) {
        src.addFile( className.replace( '.',
                                        '/' ) + ".java",
                     text.toCharArray() );
        this.results = compiler.compile( new String[]{className},
                                         src,
                                         dst );
        
        return results;
    }

    public void addFunction(PackageDescr packageDescr,
                            FunctionDescr rule) {
        //@todo        
    }

    public void addRule(Package pkg,
                        RuleDescr ruleDescr) throws CheckedDroolsException,
                                            ClassNotFoundException,
                                            InstantiationException,
                                            IllegalAccessException {
        String ruleClassName = getUniqueLegalName( pkg.getName(),
                                                   ruleDescr.getName(),
                                                   "java" );
        ruleDescr.SetClassName( ucFirst( ruleClassName ) );

        RuleBuilder builder = new RuleBuilder();
        builder.build( pkg,
                       ruleDescr );
        Rule rule = builder.getRule();

        //System.out.println( ruleDescr.getClassName() + ":\n" + builder.getRuleClass() );

        this.packageStoreWrapper.setPackageCompilationData( pkg.getPackageCompilationData() );
        compile( pkg.getName() + "." + ruleDescr.getClassName(),
                 builder.getRuleClass(),
                 this.packageStoreWrapper );

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

            compile( className,
                     text,
                     this.packageStoreWrapper );
        }

        pkg.addRule( rule );
    }

    public Package getPackag(String packageName) {
        return (Package) this.packages.get( packageName );
    }

    public Map getPackages() {
        return this.packages;
    }

    public MemoryResourceReader getMemoryResourceReader() {
        return this.src;
    }

    public byte[] getSrcJar() {
        return null;
    }

    public byte[] getBinJar() {
        return getBinJar( Collections.EMPTY_LIST );
    }

    public byte[] getBinJar(List objects) {
        return null;
    }

    public int getNextInt() {
        return this.counter++;
    }

    public CompilationResult getCompilationResults() {
        return this.results;
    }

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     * 
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    public String getUniqueLegalName(String packageName,
                                     String name,
                                     String ext) {
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

            exists = this.src.isAvailable( fileName );
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
