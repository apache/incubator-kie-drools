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

import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClassUtils {
    private static Map          classes = Collections.synchronizedMap( new HashMap() );

    private static final String STAR    = "*";

    public static boolean areNullSafeEquals(Object obj1, Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }

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
        return pResourceName.substring( 0, i );
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
        final String relFileName = absFileName.substring( rootLength + 1, p );
        return relFileName.replace( File.separatorChar, '.' );
    }

    public static String relative(final File base,
                                  final File file) {
        final int rootLength = base.getAbsolutePath().length();
        final String absFileName = file.getAbsolutePath();
        return absFileName.substring( rootLength + 1 );
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

        Object object;
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
        for (String item : items) {
            String qualifiedNamespace = item.substring(0,
                    item.lastIndexOf('.')).trim();
            String name = item.substring(item.lastIndexOf('.') + 1).trim();
            Object object = patterns.get(qualifiedNamespace);
            if (object == null) {
                if (STAR.equals(name)) {
                    patterns.put(qualifiedNamespace,
                            STAR);
                } else {
                    // create a new list and add it
                    List list = new ArrayList();
                    list.add(name);
                    patterns.put(qualifiedNamespace,
                            list);
                }
            } else if (name.equals(STAR)) {
                // if its a STAR now add it anyway, we don't care if it was a STAR or a List before
                patterns.put(qualifiedNamespace,
                        STAR);
            } else {
                // its a list so add it if it doesn't already exist
                List list = (List) object;
                if (!list.contains(name)) {
                    list.add(name);
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
        // Array [] object class names are "[x", where x is the first letter of the array type
        // -> NO '.' in class name, thus!
        // see http://download.oracle.com/javase/6/docs/api/java/lang/Class.html#getName%28%29
        String qualifiedNamespace = className;
        String name = className;
        if( className.indexOf('.') > 0 ) { 
            qualifiedNamespace = className.substring( 0, className.lastIndexOf( '.' ) ).trim();
            name = className.substring( className.lastIndexOf( '.' ) + 1 ).trim();
        }
        else if( className.indexOf('[') == 0 ) { 
           qualifiedNamespace = className.substring(0, className.lastIndexOf('[') );
        }
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
            int dotPos;
            int dolPos = cls.getName().indexOf( '$' );
            if ( dolPos > 0 ) {
                // we have nested classes, so adjust dotpos to before first $
                dotPos = cls.getName().substring( 0, dolPos ).lastIndexOf( '.' );
            } else {
                dotPos = cls.getName().lastIndexOf( '.' );
            }
                
            if ( dotPos > 0 ) {
                return cls.getName().substring( 0,
                                                dotPos );
            } else {
                // must be default package.
                return "";
            }
        } else {
            return pkg.getName();
        }
    }

    public static Class<?> findClass(String name, Collection<String> availableImports, ClassLoader cl) {
        Class<?> clazz = null;
        for (String imp : availableImports) {
            try {
                String className = imp.endsWith(name) ? imp : imp + "." + name;
                clazz = Class.forName(className, false, cl);
            } catch (ClassNotFoundException e) {
                continue;
            }
            break;
        }
        return clazz;
    }

    public static List<String> getSettableProperties(Class<?> clazz) {
        List<String> settableProperties = new ArrayList<String>();
        for (Method m : clazz.getMethods()) {
            if (m.getParameterTypes().length == 1) {
                String propName = setter2property(m.getName());
                if (propName != null) {
                    settableProperties.add(propName);
                }
            }
        }
        for (Field f : clazz.getFields()) {
            String fieldName = f.getName();
            if (!settableProperties.contains(fieldName)) {
                settableProperties.add(fieldName);
            }
        }
        Collections.sort(settableProperties);
        return settableProperties;
    }

    public static String getter2property(String methodName) {
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        }
        if (methodName.startsWith("is") && methodName.length() > 2) {
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        }
        return null;
    }

    public static String setter2property(String methodName) {
        if (!methodName.startsWith("set") || methodName.length() < 4) {
            return null;
        }
        return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
    }

    public static <T extends Externalizable> T deepClone(T origin) {
        return origin == null ? null : deepClone(origin, origin.getClass().getClassLoader());
    }

    public static <T extends Externalizable> T deepClone(T origin, ClassLoader classLoader) {
        if (origin == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new DroolsObjectOutputStream(baos);
            oos.writeObject(origin);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new DroolsObjectInputStream(bais, classLoader);
            Object deepCopy = ois.readObject();
            return (T)deepCopy;
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }
    }

    public static final Class<?> convertFromPrimitiveType(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        if (type == char.class) return Character.class;
        if (type == boolean.class) return Boolean.class;
        throw new RuntimeException("Class not convertible from primitive: " + type.getName());
    }

    public static Class<?> convertToPrimitiveType(Class<?> type) {
        if (type.isPrimitive()) return type;
        if (type == Integer.class) return int.class;
        if (type == Long.class) return long.class;
        if (type == Float.class) return float.class;
        if (type == Double.class) return double.class;
        if (type == Short.class) return short.class;
        if (type == Byte.class) return byte.class;
        if (type == Character.class) return char.class;
        if (type == Boolean.class) return boolean.class;
        if (type == BigInteger.class) return long.class;
        if (type == BigDecimal.class) return double.class;
        if (type == Number.class) return double.class;
        throw new RuntimeException("Class not convertible to primitive: " + type.getName());
    }
    
    public static boolean isWindows() {
        String os =  System.getProperty("os.name");
        return os.toUpperCase().contains( "WINDOWS" );
       
    }

    public static boolean isOSX() {
        String os =  System.getProperty("os.name");
        return os.toUpperCase().contains( "MAC OS X" );

    }
}
