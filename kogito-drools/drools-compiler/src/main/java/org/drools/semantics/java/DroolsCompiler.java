package org.drools.semantics.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.stores.ResourceStoreClassLoader;
import org.drools.CheckedDroolsException;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.TypeResolver;

public class DroolsCompiler {
    private final MemoryResourceReader     src;
    private final MemoryResourceStore      dst;
    private final ResourceStoreClassLoader classLoader;

    private  JavaCompiler compiler = JavaCompilerFactory.getInstance().createCompiler( JavaCompilerFactory.ECLIPSE );
    
    private int                            counter;

    private Map                            packages;
    
    private Map                            errors = Collections.EMPTY_MAP;

    public DroolsCompiler() {
        this( null );
    }

    public DroolsCompiler(ClassLoader parentClassLoader) {
        this.src = new MemoryResourceReader();
        this.dst = new MemoryResourceStore();

        this.packages = new HashMap();

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
    
    void compile(String className, Map ruleClasses, Map invokerClasses) {
        
    }
    
    CompilationResult compile(String className, String text) {              
      src.addFile( className.replace( '.', '/' ) + ".java", text.toCharArray() );                
      CompilationResult result = compiler.compile( new String[] { className }, src, dst );
      if ( result.getErrors().length > 0 ) {
          if ( this.errors == Collections.EMPTY_MAP ) {
              this.errors = new HashMap();
          }
          for ( int i =0, size = result.getErrors().length; i < size; i++ ) {
              System.out.println(result.getErrors()[i]);
          }
          
          this.errors.put( className, result );
      }
     
      return result;
    }

    public void addFunction(PackageDescr packageDescr,
                            FunctionDescr rule) {
        //@todo        
    }

    public void addRule(Package pkg,
                        RuleDescr ruleDescr) throws CheckedDroolsException {
        String ruleClassName = getUniqueLegalName( pkg.getName(),
                                                   ruleDescr.getName(),
                                                   "java" );
        ruleDescr.SetClassName( ucFirst( ruleClassName ) );        
        
//        this.ruleCompiler.compile( pkg,
//                                   ruleDescr );
        
        RuleBuilder builder = new RuleBuilder();
        builder.build( pkg, ruleDescr );
        Rule rule = builder.getRule();
//        
//        for( Iterator it = methods.iterator(); it.hasNext(); it.next() ) {            
//            compile(  pkg.getName() + "." + ucFirst( ruleDescr.getClassName() ), (String) it.next() );              
//        }
        
        System.out.println( ruleDescr.getClassName() + ":\n" + builder.getRuleClass() );
        
        compile(  pkg.getName() + "." + ruleDescr.getClassName(), builder.getRuleClass() );
        
        for( Iterator it = builder.getInvokers().keySet().iterator(); it.hasNext(); ) {  
            String className = (String) it.next();
            String text = (String) builder.getInvokers().get( className );
            
            System.out.println( className + ":\n" + text );
            
            compile(  pkg.getName() + "." + className, text );              
        }        
                
        //this.compiler.compile(  this.pkg.getName() + "." + ucFirst( this.ruleDescr.getClassName() ), string.toString() );
        //this.compiler.compile( pkg.getName() + "." + ruleDescr.getClassName(), this.methods, this.invokers);
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
    
    public Map getErrors() {
        return this.errors;
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
