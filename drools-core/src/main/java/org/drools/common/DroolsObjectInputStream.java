/**
 * 
 */
package org.drools.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.rule.DialectDatas;
import org.drools.rule.Package;

public class DroolsObjectInputStream extends ObjectInputStream {
    private final ClassLoader        classLoader;
    private InternalRuleBase         ruleBase;
    private InternalWorkingMemory    workingMemory;
    private Package                  pkg;
    private DialectDatas             dialectDatas;
    private ClassFieldExtractorCache extractorFactory;

    /** table mapping primitive type names to corresponding class objects */
    private static final HashMap     primClasses = new HashMap( 8,
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

    public DroolsObjectInputStream(final InputStream in) throws IOException {
        this( in,
              null );
    }

    public DroolsObjectInputStream(final InputStream in,
                                   ClassLoader classLoader) throws IOException {
        super( in );
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = this.getClass().getClassLoader();
            }
        }

        this.classLoader = classLoader;
        this.extractorFactory = ClassFieldExtractorCache.getInstance();
        enableResolveObject( true );
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    protected Class resolveClass(final ObjectStreamClass desc) throws IOException,
                                                              ClassNotFoundException {
        if ( this.classLoader == null ) {
            return super.resolveClass( desc );
        } else {
            final String name = desc.getName();
            Class clazz = (Class) primClasses.get( name );
            if ( clazz == null ) {
                try {
                    clazz = this.classLoader.loadClass( name );
                } catch ( final ClassNotFoundException cnf ) {
                    clazz = super.resolveClass( desc );
                }
            }
            return clazz;
        }
    }

    public InternalRuleBase getRuleBase() {
        return ruleBase;
    }

    public void setRuleBase(InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public void setWorkingMemory(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    public Package getPackage() {
        return pkg;
    }

    public void setPackage(Package pkg) {
        this.pkg = pkg;
    }      

    public DialectDatas getDialectDatas() {
        return this.dialectDatas;
    }

    public void setDialectDatas(DialectDatas dialectDatas) {
        this.dialectDatas = dialectDatas;
    }

    public ClassFieldExtractorCache getExtractorFactory() {
        return extractorFactory;
    }

    public void setExtractorFactory(ClassFieldExtractorCache extractorFactory) {
        this.extractorFactory = extractorFactory;
    }

}