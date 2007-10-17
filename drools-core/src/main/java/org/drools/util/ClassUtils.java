package org.drools.util;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ClassUtils {
    private static Map classes = Collections.synchronizedMap( new HashMap() );
    
    /**
     * Please do not use - internal
     * org/my/Class.xxx -> org.my.Class
     */
    public static String convertResourceToClassName(final String pResourceName) {
        return ClassUtils.stripExtension( pResourceName ).replace( '/',
                                                                   '.' );
    }

    /**
     * Please do not use - internal
     * org.my.Class -> org/my/Class.class
     */
    public static String convertClassToResourcePath(final String pName) {
        return pName.replace( '.',
                              '/' ) + ".class";
    }

    /**
     * Please do not use - internal
     * org/my/Class.xxx -> org/my/Class
     */
    public static String stripExtension(final String pResourceName) {
        final int i = pResourceName.lastIndexOf( '.' );
        final String withoutExtension = pResourceName.substring( 0,
                                                                 i );
        return withoutExtension;
    }

    public static String toJavaCasing(final String pName) {
        final char[] name = pName.toLowerCase().toCharArray();
        name[0] = Character.toUpperCase( name[0] );
        return new String( name );
    }

    public static String clazzName(final File base,
                                   final File file) {
        final int rootLength = base.getAbsolutePath().length();
        final String absFileName = file.getAbsolutePath();
        final int p = absFileName.lastIndexOf( '.' );
        final String relFileName = absFileName.substring( rootLength + 1,
                                                          p );
        final String clazzName = relFileName.replace( File.separatorChar,
                                                      '.' );
        return clazzName;
    }

    public static String relative(final File base,
                                  final File file) {
        final int rootLength = base.getAbsolutePath().length();
        final String absFileName = file.getAbsolutePath();
        final String relFileName = absFileName.substring( rootLength + 1 );
        return relFileName;
    }
    
    public static String canonicalName( Class clazz ) {
        StringBuffer name = new StringBuffer();
        
        if( clazz.isArray() ) {
            name.append( canonicalName( clazz.getComponentType() ) );
            name.append( "[]" );
        } else if( clazz.getDeclaringClass() == null ) {
            name.append( clazz.getName() );
        } else {
            name.append( canonicalName( clazz.getDeclaringClass() ) );
            name.append( "." );
            name.append( clazz.getName().substring( clazz.getDeclaringClass().getName().length() + 1 ) );
        }
        
        return name.toString();
    }
    
    
    /**
     * This method will attempt to create an instance of the specified Class. It uses
     * a syncrhonized HashMap to cache the reflection Class lookup.
     * @param className
     * @return
     */
    public static Object instantiateObject(String className) {
        Class cls = (Class) ClassUtils.classes.get( className );
        if ( cls == null ) {
            try {
                cls = Class.forName( className );
                ClassUtils.classes.put(  className, cls );
            } catch ( Throwable e ) {
                throw new RuntimeException("Unable to load class '" + className + "'", e );
            }            
        }
        
        Object object = null;
        try {
            object = cls.newInstance();            
        } catch ( Throwable e ) {
            throw new RuntimeException("Unable to instantiate object for class '" + className + "'", e );
        }  
        return object;
    }    

}
