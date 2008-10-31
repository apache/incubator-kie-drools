/**
 *
 */
package org.drools.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

import org.drools.rule.Package;

public class DroolsObjectInputStream extends ObjectInputStream
    implements
    DroolsObjectInput {
    private static final Map<String, Class> primClasses = new HashMap<String, Class>( 8,
                                                                                      1.0F );
    static {
        primClasses.put( "boolean",
                         boolean.class );
        primClasses.put( "byte",
                         byte.class );
        primClasses.put( "char",
                         char.class );
        primClasses.put( "short",
                         short.class );
        primClasses.put( "int",
                         int.class );
        primClasses.put( "long",
                         long.class );
        primClasses.put( "float",
                         float.class );
        primClasses.put( "double",
                         double.class );
        primClasses.put( "void",
                         void.class );
    }

    private ClassLoader                     parentClassLoader;
    private ClassLoader                     classLoader;
    private InternalRuleBase                ruleBase;
    private InternalWorkingMemory           workingMemory;
    private Package                         pkg;

    public DroolsObjectInputStream(InputStream inputStream) throws IOException {
        this( inputStream,
              null );
    }

    public DroolsObjectInputStream(InputStream inputStream,
                                   ClassLoader classLoader) throws IOException {
        super( inputStream );
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = getClass().getClassLoader();
            }
        }

        this.classLoader = classLoader;
        this.parentClassLoader = classLoader;

    }

    protected Class resolveClass(String className) throws ClassNotFoundException {
        try {
            Class clazz = primClasses.get( className );
            if ( clazz == null ) {
                clazz = Class.forName( className,
                                       true,
                                       this.classLoader );
            }
            return clazz;
        } catch ( ClassNotFoundException e ) {
            throw e;
        }
    }

    protected Class< ? > resolveClass(ObjectStreamClass desc) throws IOException,
                                                             ClassNotFoundException {
        return resolveClass( desc.getName() );
    }

    public static InvalidClassException newInvalidClassException(Class clazz,
                                                                 Throwable cause) {
        InvalidClassException exception = new InvalidClassException( clazz.getName() );
        exception.initCause( cause );
        return exception;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public InternalRuleBase getRuleBase() {
        return ruleBase;
    }

    public void setRuleBase(InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.classLoader = this.ruleBase.getRootClassLoader();
    }

    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    public void setWorkingMemory(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public Package getPackage() {
        return pkg;
    }

    public void setPackage(Package pkg) {
        this.pkg = pkg;
    }

    //    public ClassFieldAccessorCache getExtractorFactory() {
    //        return extractorFactory;
    //    }
    //
    //    public void setExtractorFactory(ClassFieldAccessorCache extractorFactory) {
    //        this.extractorFactory = extractorFactory;
    //    }

    public ClassLoader getParentClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = getClass().getClassLoader();
            }
        }
        this.classLoader = classLoader;
    }

}