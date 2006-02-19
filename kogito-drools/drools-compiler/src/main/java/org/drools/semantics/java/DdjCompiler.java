package org.drools.semantics.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.stores.ResourceStoreClassLoader;
import org.drools.CheckedDroolsException;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.spi.TypeResolver;

public class DdjCompiler {
    private final MemoryResourceReader     src;
    private final MemoryResourceStore      dst;
    private final ResourceStoreClassLoader classLoader;

    private int                            counter;

    private Map                            packages;

    private RuleCompiler                   ruleCompiler;

    public DdjCompiler() {
        this( null );
    }

    public DdjCompiler(ClassLoader parentClassLoader) {
        this.src = new MemoryResourceReader();
        this.dst = new MemoryResourceStore();

        this.packages = new HashMap();

        this.ruleCompiler = new RuleCompiler( this );

        //this.packageName = packageName + "." + pkg.getName().replaceAll( "(^[0-9]|[^\\w$])", "_" ) + "_" + System.currentTimeMillis();

        if ( parentClassLoader == null ) {
            parentClassLoader = Thread.currentThread().getContextClassLoader();
            if ( parentClassLoader == null ) {
                parentClassLoader = this.getClass().getClassLoader();
            }
        }

        classLoader = new ResourceStoreClassLoader( parentClassLoader,
                                                    new ResourceStore[]{dst} );
    }

    public void addPackage(PackageDescr packageDescr) throws CheckedDroolsException, ClassNotFoundException {
        if ( this.packages == Collections.EMPTY_MAP ) {
            this.packages = new HashMap();
        }

        Package pkg = (Package) this.packages.get( packageDescr.getName() );
        if ( pkg != null ) {
            //mergePackage( packageDescr ) ;
            mergePackage(pkg, packageDescr);
        } else {
            pkg = newPackage(packageDescr);
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
        pkg.setDocumentation( packageDescr.getDocumentation() );

        List imports = packageDescr.getImports();
        for ( Iterator it = imports.iterator(); it.hasNext(); ) {
            pkg.addImport( (String) it.next() );
        }

        TypeResolver typeResolver = new ClassTypeResolver( imports,
                                                           this.classLoader );

        Map globals = packageDescr.getGlobals();
        for ( Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            String className = (String) globals.get( identifier );
            Class clazz = typeResolver.resolveType( className );
            pkg.addGlobalDeclaration( identifier, clazz );
        }

        List attributes = packageDescr.getAttributes();

        return pkg;
    }
    
    private void mergePackage(Package pkg, PackageDescr packageDescr) throws ClassNotFoundException {
        List imports = packageDescr.getImports();
        for ( Iterator it = imports.iterator(); it.hasNext(); ) {
            pkg.addImport( (String) it.next() );
        }

        TypeResolver typeResolver = new ClassTypeResolver( imports,
                                                           this.classLoader );

        Map globals = packageDescr.getGlobals();
        for ( Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            String className = (String) globals.get( identifier );
            Class clazz = typeResolver.resolveType( className );
            pkg.addGlobalDeclaration( identifier, clazz );
        }

        List attributes = packageDescr.getAttributes();
    }

    public void addFunction(PackageDescr packageDescr,
                            FunctionDescr rule) {
        //@todo        
    }

    public void addRule(Package pkg,
                        RuleDescr ruleDescr) throws CheckedDroolsException {
        this.ruleCompiler.compile( pkg,
                                   ruleDescr );
    }

    public List getPackages() {
        return new ArrayList( packages.values() );
    }

    public MemoryResourceReader getMemoryResourceReader() {
        return this.src;
    }

    public MemoryResourceStore getMemoryResourceStore() {
        return this.dst;
    }

    public ResourceStoreClassLoader getResourcStoreClassLoader() {
        return this.classLoader;
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

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     * 
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    public String generateUniqueLegalName(String packageName,
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

}
