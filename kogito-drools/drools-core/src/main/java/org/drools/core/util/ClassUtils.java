/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClassUtils {
    private static Map          classes = Collections.synchronizedMap( new HashMap() );

    private static final String STAR    = "*";

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

    public static String canonicalName(Class clazz) {
        StringBuilder name = new StringBuilder();

        if ( clazz.isArray() ) {
            name.append( canonicalName( clazz.getComponentType() ) );
            name.append( "[]" );
        } else if ( clazz.getDeclaringClass() == null ) {
            name.append( clazz.getName() );
        } else {
            name.append( canonicalName( clazz.getDeclaringClass() ) );
            name.append( "." );
            name.append( clazz.getName().substring( clazz.getDeclaringClass().getName().length() + 1 ) );
        }

        return name.toString();
    }

    public static Object instantiateObject(String className) {
        return instantiateObject( className,
                                  null );
    }

    /**
     * This method will attempt to create an instance of the specified Class. It uses
     * a syncrhonized HashMap to cache the reflection Class lookup.
     * @param className
     * @return
     */
    public static Object instantiateObject(String className,
                                           ClassLoader classLoader) {
        Class cls = (Class) ClassUtils.classes.get( className );
        if ( cls == null ) {
            try {
                cls = Class.forName( className );
            } catch ( Exception e ) {
                //swallow
            }

            //ConfFileFinder
            if ( cls == null && classLoader != null ) {
                try {
                    cls = classLoader.loadClass( className );
                } catch ( Exception e ) {
                    //swallow
                }
            }

            if ( cls == null ) {
                try {
                    cls = ClassUtils.class.getClassLoader().loadClass( className );
                } catch ( Exception e ) {
                    //swallow
                }
            }

            if ( cls == null ) {
                try {
                    cls = Thread.currentThread().getContextClassLoader().loadClass( className );
                } catch ( Exception e ) {
                    //swallow
                }
            }

            if ( cls == null ) {
                try {
                    cls = ClassLoader.getSystemClassLoader().loadClass( className );
                } catch ( Exception e ) {
                    //swallow
                }
            }

            if ( cls != null ) {
                ClassUtils.classes.put( className,
                                        cls );
            } else {
                throw new RuntimeException( "Unable to load class '" + className + "'" );
            }
        }

        Object object = null;
        try {
            object = cls.newInstance();
        } catch ( Throwable e ) {
            throw new RuntimeException( "Unable to instantiate object for class '" + className + "'",
                                        e );
        }
        return object;
    }

    /**
     * Populates the import style pattern map from give comma delimited string
     * @param patterns
     * @param str
     */
    public static void addImportStylePatterns(Map<String, Object> patterns,
                                              String str) {
        if ( str == null || "".equals( str.trim() ) ) {
            return;
        }

        String[] items = str.split( " " );
        for ( int i = 0; i < items.length; i++ ) {
            String qualifiedNamespace = items[i].substring( 0,
                                                            items[i].lastIndexOf( '.' ) ).trim();
            String name = items[i].substring( items[i].lastIndexOf( '.' ) + 1 ).trim();
            Object object = patterns.get( qualifiedNamespace );
            if ( object == null ) {
                if ( STAR.equals( name ) ) {
                    patterns.put( qualifiedNamespace,
                                  STAR );
                } else {
                    // create a new list and add it
                    List list = new ArrayList();
                    list.add( name );
                    patterns.put( qualifiedNamespace,
                                  list );
                }
            } else if ( name.equals( STAR ) ) {
                // if its a STAR now add it anyway, we don't care if it was a STAR or a List before
                patterns.put( qualifiedNamespace,
                              STAR );
            } else {
                // its a list so add it if it doesn't already exist
                List list = (List) object;
                if ( !list.contains( object ) ) {
                    list.add( name );
                }
            }
        }
    }

    /**
     * Determines if a given full qualified class name matches any import style patterns.
     * @param patterns
     * @param className
     * @return
     */
    public static boolean isMatched(Map<String, Object> patterns,
                                    String className) {
        String qualifiedNamespace = className.substring( 0,
                                                         className.lastIndexOf( '.' ) ).trim();
        String name = className.substring( className.lastIndexOf( '.' ) + 1 ).trim();
        Object object = patterns.get( qualifiedNamespace );
        if ( object == null ) {
            return true;
        } else if ( STAR.equals( object ) ) {
            return false;
        } else if ( patterns.containsKey( "*" ) ) {
            // for now we assume if the name space is * then we have a catchall *.* pattern
            return true;
        } else {
            List list = (List) object;
            return !list.contains( name );
        }
    }

    /**
     * Extracts the package name from the given class object
     * @param cls
     * @return
     */
    public static String getPackage(Class<?> cls) {
        // cls.getPackage() sometimes returns null, in which case fall back to string massaging.
        java.lang.Package pkg = cls.isArray() ? cls.getComponentType().getPackage() : cls.getPackage();
        if ( pkg == null ) {
            int dotPos = cls.getName().lastIndexOf( '.' );
            if ( dotPos > 0 ) {
                return cls.getName().substring( 0,
                                                dotPos - 1 );
            } else {
                // must be default package.
                return "";
            }
        } else {
            return pkg.getName();
        }
    }
    
}
