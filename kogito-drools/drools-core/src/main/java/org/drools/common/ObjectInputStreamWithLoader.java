/**
 * 
 */
package org.drools.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;

public class ObjectInputStreamWithLoader extends ObjectInputStream {
    private final ClassLoader classLoader;
    
    /** table mapping primitive type names to corresponding class objects */
    private static final HashMap primClasses = new HashMap(8, 1.0F);
    static {
    primClasses.put("boolean", boolean.class);
    primClasses.put("byte", byte.class);
    primClasses.put("char", char.class);
    primClasses.put("short", short.class);
    primClasses.put("int", int.class);
    primClasses.put("long", long.class);
    primClasses.put("float", float.class);
    primClasses.put("double", double.class);
    primClasses.put("void", void.class);
    }

    public ObjectInputStreamWithLoader(final InputStream in,
                                       final ClassLoader classLoader) throws IOException {
        super( in );
        this.classLoader = classLoader;
        enableResolveObject( true );
    }

    protected Class resolveClass(final ObjectStreamClass desc) throws IOException,
                                                              ClassNotFoundException {
        if ( this.classLoader == null ) {
            return super.resolveClass( desc );
        } else {
            final String name = desc.getName();
            Class clazz = (Class) primClasses.get( name );
            if( clazz == null ) {
                try{
                    clazz = this.classLoader.loadClass( name );
                } catch (ClassNotFoundException cnf) {
                    clazz = super.resolveClass( desc );
                }
            }
            return clazz;
        }
    }
}