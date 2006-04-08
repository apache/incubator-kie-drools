package org.drools.compiler;

import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.drools.RuntimeDroolsException;

public class PackageBuilderConfiguration {    
    public static final int ECLIPSE = 0;
    public static final int JANINO  = 1;    
    
    private int compiler = JavaCompilerFactory.ECLIPSE;
    
    private ClassLoader          classLoader;
    
    public PackageBuilderConfiguration(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if ( classLoader == null ) {
            classLoader = this.getClass().getClassLoader();
        }
        this.classLoader = classLoader;     
    }        
    
    public int getCompiler() {
        return this.compiler;
    }
    
    public void setCompiler(int compiler) {
        switch ( compiler ) {
            case PackageBuilderConfiguration.ECLIPSE :
                this.compiler = JavaCompilerFactory.ECLIPSE;
                break;
            case PackageBuilderConfiguration.JANINO:
                this.compiler = JavaCompilerFactory.JANINO;
                break;
            default:
                throw new RuntimeDroolsException( "value '" + compiler + "' is not a valid compiler" );
        }
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public void setClassLoader( ClassLoader classLoader ) {
        if ( classLoader != null ) {
            this.classLoader = classLoader;
        }
    }    
}
