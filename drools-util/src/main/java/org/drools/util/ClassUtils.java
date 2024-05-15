/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Character.toUpperCase;
import static java.lang.System.arraycopy;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;
import static java.util.Arrays.asList;
import static java.util.stream.Stream.of;
import static org.drools.util.MethodUtils.getMethod;
import static org.drools.util.StringUtils.ucFirst;

public final class ClassUtils {
    private static final Map<String, Class<?>> classes = Collections.synchronizedMap( new HashMap() );

    private static final Map<String, Constructor<?>> constructors = Collections.synchronizedMap( new HashMap() );

    private static final String STAR    = "*";

    private static final Map<String, String> abbreviationMap;

    private static final Map<String, Class<?>> primitiveNameToType;

    private static final Set<Class<?>> numericClasses = new HashSet<>(asList(int.class, long.class, double.class, float.class, short.class, char.class, byte.class,
                                                                             Integer.class, Long.class, Double.class, Float.class, Short.class, Character.class, Byte.class,
                                                                             BigInteger.class, BigDecimal.class));

    static {
        final Map<String, String> m = new HashMap<>();
        m.put("int", "I");
        m.put("boolean", "Z");
        m.put("float", "F");
        m.put("long", "J");
        m.put("short", "S");
        m.put("byte", "B");
        m.put("double", "D");
        m.put("char", "C");
        m.put("void", "V");
        abbreviationMap = Collections.unmodifiableMap(m);

        final Map<String, Class<?>> m2 = new HashMap<>();
        m2.put("int", int.class);
        m2.put("boolean", boolean.class);
        m2.put("float", float.class);
        m2.put("long", long.class);
        m2.put("short", short.class);
        m2.put("byte", byte.class);
        m2.put("double", double.class);
        m2.put("char", char.class);
        primitiveNameToType = Collections.unmodifiableMap(m2);
    }

    public static Class<?> toNonPrimitiveType(Class<?> c) {
        if (!c.isPrimitive()) return c;
        if (c == int.class) return Integer.class;
        if (c == long.class) return Long.class;
        if (c == double.class) return Double.class;
        if (c == float.class) return Float.class;
        if (c == short.class) return Short.class;
        if (c == byte.class) return Byte.class;
        if (c == char.class) return Character.class;
        if (c == boolean.class) return Boolean.class;
        return c;
    }


    /**
     * Please do not use - internal
     * org/my/Class.xxx -> org.my.Class
     */
    public static String convertResourceToClassName(final String pResourceName) {
        return stripExtension(pResourceName).replace('/', '.');
    }

    /**
     * Please do not use - internal
     * org.my.Class -> org/my/Class.class
     */
    public static String convertClassToResourcePath(final Class cls) {
        return convertClassToResourcePath(cls.getName());
    }

    public static String convertClassToResourcePath(final String pName) {
        return pName.replace( '.', '/' ) + ".class";
    }

    /**
     * Please do not use - internal
     * org/my/Class.xxx -> org/my/Class
     */
    public static String stripExtension(final String pResourceName) {
        final int i = pResourceName.lastIndexOf('.');
        return pResourceName.substring( 0, i );
    }

    public static String relative(final File base,
                                  final File file) {
        final int rootLength = base.getAbsolutePath().length();
        final String absFileName = file.getAbsolutePath();
        return absFileName.substring(rootLength + 1);
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

    /**
     * This method will attempt to load the specified Class. It uses
     * a syncrhonized HashMap to cache the reflection Class lookup.
     */
    public static Class<?> loadClass(String className,
                                     ClassLoader classLoader) {
        Class cls = classes.get(className );
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
                classes.put( className, cls );
            } else {
                throw new RuntimeException( "Unable to load class '" + className + "'" );
            }
        }
        return cls;
    }

    public static Object instantiateObject(String className) {
        return instantiateObject(className,
                (ClassLoader)null);
    }

    /**
     * This method will attempt to create an instance of the specified Class. It uses
     * a syncrhonized HashMap to cache the reflection Class lookup.
     */
    public static Object instantiateObject(String className,
                                           ClassLoader classLoader) {
        Object object;
        try {
            object = loadClass(className, classLoader).newInstance();
        } catch ( Throwable e ) {
            throw new RuntimeException( "Unable to instantiate object for class '" + className + "'",
                                        e );
        }
        return object;
    }

    /**
     * This method will attempt to create an instance of the specified Class. It uses
     * a synchronized HashMap to cache the reflection Class lookup.  It will execute the default
     * constructor with the passed in arguments
     * @param className the name of the class
     * @param args  arguments to default constructor
     */
    public static Object instantiateObject(String className,
                                           ClassLoader classLoader, Object...args) {
        Constructor c = constructors.computeIfAbsent(className, n -> loadClass(n, classLoader).getConstructors()[0]);

        Object object;
        try {
            object = c.newInstance(args);
        } catch ( Throwable e ) {
            throw new RuntimeException( "Unable to instantiate object for class '" + className +
                    "' with constructor " + c, e );
        }
        return object;
    }

    /**
     * This method will attempt to create an instance of the specified Class. It uses
     * a synchronized HashMap to cache the reflection Class lookup.  It will execute the default
     * constructor with the passed in arguments
     * @param className teh name of the class
     * @param args  arguments to default constructor
     */
    public static Object instantiateObject(String className, Object...args) {
        return instantiateObject(className, null, args);
    }

    /**
     * Populates the import style pattern map from give comma delimited string
     */
    public static void addImportStylePatterns(Map<String, Object> patterns, String str) {
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
                    List<String> list = new ArrayList<>();
                    list.add(name);
                    patterns.put(qualifiedNamespace, list);
                }
            } else if (name.equals(STAR)) {
                // if its a STAR now add it anyway, we don't care if it was a STAR or a List before
                patterns.put(qualifiedNamespace, STAR);
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
            if (imp.endsWith(".*")) {
                imp = imp.substring(0, imp.length()-2);
            }
            String className = imp.endsWith(name) ? imp : imp + "." + name;
            clazz = findClass(className, cl);
            if (clazz != null) {
                break;
            }
        }
        return clazz;
    }

    public static Class<?> findClass(String className, ClassLoader cl) {
        try {
            return Class.forName(className, false, cl);
        } catch (ClassNotFoundException e) {
            int lastDot = className.lastIndexOf('.');
            className = className.substring(0, lastDot) + "$" + className.substring(lastDot+1);
            try {
                return Class.forName(className, false, cl);
            } catch (ClassNotFoundException e1) { }
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String field) {
        try {
            return clazz.getDeclaredField( field );
        } catch (NoSuchFieldException e) {
            return clazz.getSuperclass() != null ? getField(clazz.getSuperclass(), field) : null;
        }
    }

    public static Method getAccessor(Class<?> clazz, String field) {
        return getAccessor(clazz, field, false);
    }

    public static Method getAccessor(Class<?> clazz, String field, boolean exceptionIfIncompatible) {
        Map<String, Integer> accessorPriorityMap = accessorPriorityMap(field);
        List<Method> accessors = accessorPriorityMap.keySet()
                                                    .stream()
                                                    .map(methodName -> getMethod(clazz, methodName))
                                                    .filter(Optional::isPresent)
                                                    .map(Optional::get)
                                                    .filter(method -> !(method.getName().startsWith("is") && !method.getReturnType().equals(boolean.class)))
                                                    .distinct()
                                                    .collect(Collectors.toList());
        return bestCandidateAccessor(clazz, accessors, accessorPriorityMap, exceptionIfIncompatible);
    }

    public static Map<String, Integer> accessorPriorityMap(String field) {
        Map<String, Integer> accessorPriorityMap = new HashMap<>();
        accessorPriorityMap.put("is" + ucFirst(field), 4);
        accessorPriorityMap.put("is" + field, 3);
        accessorPriorityMap.put("get" + ucFirst(field), 2);
        accessorPriorityMap.put("get" + field, 1);
        accessorPriorityMap.put(field, 0);
        return accessorPriorityMap;
    }

    private static Method bestCandidateAccessor(Class<?> clazz, List<Method> accessors, Map<String, Integer> accessorPriorityMap, boolean exceptionIfIncompatible) {
        Method bestCandidate = null;
        for (Method method : accessors) {
            if (bestCandidate != null && !MethodUtils.isOverride(bestCandidate, method)) {
                if (method.getReturnType() != bestCandidate.getReturnType()) {
                    if (method.getReturnType().isAssignableFrom(bestCandidate.getReturnType())) {
                        // a more specialized getter (covariant overload) has been already indexed, so skip this one
                        continue;
                    } else if (bestCandidate.getReturnType().isAssignableFrom(method.getReturnType())) {
                        // this method is a more specialized getter. Overwrite with this one
                    } else {
                        // returnType is different so it would likely produce a wrong result
                        if (exceptionIfIncompatible) {
                            throw new IncompatibleGetterOverloadException(clazz,
                                                                          bestCandidate.getName(), bestCandidate.getReturnType(),
                                                                          method.getName(), method.getReturnType());
                        }
                    }
                } else if (Modifier.isAbstract(method.getModifiers()) && Modifier.isAbstract(bestCandidate.getModifiers())) {
                    // If both are abstract, no need of Warning
                } else if (accessorPriorityMap.get(bestCandidate.getName()) > accessorPriorityMap.get(method.getName())) {
                    // bestCandidate has higher priority
                    continue;
                }
            }
            bestCandidate = method;
        }
        return bestCandidate;
    }

    public static Method getSetter(Class<?> clazz, String field, Class<?> parameterType) {
        return Stream.<Supplier<String>>of(
                () -> "set" + ucFirst(field),
                () -> field,
                () -> "set" + field
        )
                .map( f -> getMethod(clazz, f.get(), parameterType) )
                .filter( Optional::isPresent )
                .findFirst()
                .flatMap( Function.identity() )
                .orElse( parameterType.isPrimitive() ? getSetter(clazz, field, convertFromPrimitiveType(parameterType)) : null );
    }

    public static boolean isReadableProperty( Class clazz, String property ) {
        return getFieldOrAccessor( clazz, property ) != null;
    }

    public static Member getFieldOrAccessor( Class clazz, String property ) {
        for (Field f : clazz.getFields()) {
            if (property.equals(f.getName())) {
                if ((f.getModifiers() & PUBLIC) != 0) return f;
                break;
            }
        }
        return getGetterMethod(clazz, property);
    }

    public static Method getGetterMethod(Class clazz, String property ) {
        String simple = "get" + property;
        String simpleIsGet = "is" + property;
        String isGet = getIsGetter(property);
        String getter = getGetterMethod(property);

        Method candidate = null;

        if ( Collection.class.isAssignableFrom(clazz) && "isEmpty".equals(isGet)) {
            try {
                return Collection.class.getMethod("isEmpty");
            } catch (NoSuchMethodException ignore) {}
        }

        for (Method meth : clazz.getMethods()) {
            if ((meth.getModifiers() & PUBLIC) != 0 && (meth.getModifiers() & STATIC) == 0 && meth.getParameterTypes().length == 0
                    && (getter.equals(meth.getName()) || property.equals(meth.getName()) || ((isGet.equals(meth.getName()) || simpleIsGet.equals(meth.getName())) && meth.getReturnType() == boolean.class)
                    || simple.equals(meth.getName()))) {
                if (candidate == null || candidate.getReturnType().isAssignableFrom(meth.getReturnType())) {
                    candidate = meth;
                }
            }
        }
        return candidate;
    }

    public static String getGetterMethod(String s) {
        char[] c = s.toCharArray();
        char[] chars = new char[c.length + 3];

        chars[0] = 'g';
        chars[1] = 'e';
        chars[2] = 't';

        chars[3] = toUpperCase(c[0]);

        arraycopy(c, 1, chars, 4, c.length - 1);

        return new String(chars);
    }


    private static String getIsGetter(String s) {
        char[] c = s.toCharArray();
        char[] chars = new char[c.length + 2];

        chars[0] = 'i';
        chars[1] = 's';

        chars[2] = toUpperCase(c[0]);

        arraycopy(c, 1, chars, 3, c.length - 1);

        return new String(chars);
    }

    public static Class extractGenericType(Class<?> clazz, final String methodName) {
        Method method = ClassUtils.getAccessor(clazz, methodName);
        if (method == null) {
            throw new RuntimeException(String.format("Unknown accessor %s on %s", methodName, clazz));
        }

        java.lang.reflect.Type returnType = method.getGenericReturnType();

        if (returnType instanceof ParameterizedType type) {
            java.lang.reflect.Type[] typeArguments = type.getActualTypeArguments();
            if (typeArguments.length > 0) {
                return typeArguments[0] instanceof ParameterizedType ? (Class) ((ParameterizedType)typeArguments[0]).getRawType() : (Class) typeArguments[0];
            }
        }
        throw new RuntimeException("No generic type");
    }


    public static Type getTypeArgument(Type genericType, int index) {
        return genericType instanceof ParameterizedType ? (( ParameterizedType ) genericType).getActualTypeArguments()[index] : Object.class;
    }

    public static boolean isAssignableFrom(Type from, Type to) {
        Class<?> fromClass = toRawClass( from );
        Class<?> toClass = toRawClass( to );
        return fromClass.isAssignableFrom(toClass) || MethodUtils.areBoxingCompatible(fromClass, toClass);
    }


    public static boolean isCollection(Type t) {
        return of(List.class, Map.class).anyMatch(cls -> {
            Class<?> clazz = classFromType(t);
            return cls.isAssignableFrom(clazz);
        });
    }

    public static Class<?> classFromType(Type t) {
        return classFromType(t, null);
    }

    public static Class<?> classFromType(Type t, Type scope) {
        if (t instanceof Class<?>) {
            return (Class<?>) t;
        }
        if (t instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType)t).getRawType();
        }
        if (t instanceof TypeVariable && scope != null) {
            return classFromType( actualTypeFromGenerics(scope, t) );
        }
        throw new UnsupportedOperationException("Unable to parse type");
    }

    public static Class<?> toRawClass(Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Class<?>) {
            return ( Class ) type;
        }
        if (type instanceof ParameterizedType) {
            return toRawClass( (( ParameterizedType ) type).getRawType() );
        }
        if (type instanceof TypeVariable typeVar) {
            return typeVar.getBounds().length == 1 ? toRawClass(typeVar.getBounds()[0]) : Object.class;
        }
        throw new UnsupportedOperationException( "Unknown type " + type );
    }

    public static Class<?> rawType(Type type) {
        if (type == null) {
            return null;
        }
        return type instanceof Class ? (Class<?>) type : rawType( ((ParameterizedType) type).getRawType() );
    }

    public static boolean isTypeCompatibleWithArgumentType( Class<?> actual, Class<?> formal ) {
        if ( actual.isPrimitive() && formal.isPrimitive() ) {
            return isConvertible( actual, formal );
        } else if ( actual.isPrimitive() ) {
            return isConvertible( actual, convertToPrimitiveType( formal ) );
        } else if ( formal.isPrimitive() ) {
            return isConvertible( convertToPrimitiveType( actual ), formal );
        } else {
            return formal.isAssignableFrom( actual );
        }
    }

    public static boolean isAssignable( Class<?> type, Object obj ) {
        return type.isInstance( obj ) || (type.isPrimitive() && convertFromPrimitiveType( type ).isInstance( obj ));
    }

    public static boolean isConvertible( Class<?> srcPrimitive, Class<?> tgtPrimitive ) {
        if ( Boolean.TYPE.equals( srcPrimitive ) ) {
            return Boolean.TYPE.equals( tgtPrimitive );
        } else if ( Byte.TYPE.equals( srcPrimitive ) ) {
            return Byte.TYPE.equals( tgtPrimitive )
                   || Short.TYPE.equals( tgtPrimitive )
                   || Integer.TYPE.equals( tgtPrimitive )
                   || Long.TYPE.equals( tgtPrimitive )
                   || Float.TYPE.equals( tgtPrimitive )
                   || Double.TYPE.equals( tgtPrimitive );
        } else if ( Character.TYPE.equals( srcPrimitive ) ) {
            return Character.TYPE.equals( tgtPrimitive )
                   || Integer.TYPE.equals( tgtPrimitive )
                   || Long.TYPE.equals( tgtPrimitive )
                   || Float.TYPE.equals( tgtPrimitive )
                   || Double.TYPE.equals( tgtPrimitive );
        } else if ( Double.TYPE.equals( srcPrimitive ) ) {
            return Double.TYPE.equals( tgtPrimitive );
        } else if ( Float.TYPE.equals( srcPrimitive ) ) {
            return Float.TYPE.equals( tgtPrimitive )
                   || Double.TYPE.equals( tgtPrimitive );
        } else if ( Integer.TYPE.equals( srcPrimitive ) ) {
            return Integer.TYPE.equals( tgtPrimitive )
                   || Long.TYPE.equals( tgtPrimitive )
                   || Float.TYPE.equals( tgtPrimitive )
                   || Double.TYPE.equals( tgtPrimitive );
        } else if ( Long.TYPE.equals( srcPrimitive ) ) {
            return Long.TYPE.equals( tgtPrimitive )
                   || Float.TYPE.equals( tgtPrimitive )
                   || Double.TYPE.equals( tgtPrimitive );
        } else if ( Short.TYPE.equals( srcPrimitive ) ) {
            return Short.TYPE.equals( tgtPrimitive )
                   || Integer.TYPE.equals( tgtPrimitive )
                   || Long.TYPE.equals( tgtPrimitive )
                   || Float.TYPE.equals( tgtPrimitive )
                   || Double.TYPE.equals( tgtPrimitive );
        }
        return false;
    }

    public static boolean isFinal(Class<?> clazz) {
        return Modifier.isFinal( clazz.getModifiers() );
    }

    public static boolean isInterface(Class<?> clazz) {
        return Modifier.isInterface( clazz.getModifiers() );
    }

    public static String getter2property(String methodName) {
        if (methodName.startsWith("get") && methodName.length() > 3) {
            if (methodName.length() > 4 && Character.isUpperCase( methodName.charAt( 4 ) )) {
                return methodName.substring(3);
            }
            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
        }
        if (methodName.startsWith("is") && methodName.length() > 2) {
            if (methodName.length() > 3 && Character.isUpperCase( methodName.charAt( 3 ) )) {
                return methodName.substring(2);
            }
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        }
        return null;
    }

    public static String setter2property(String methodName) {
        if (!methodName.startsWith("set") || methodName.length() < 4) {
            return null;
        }
        if (methodName.length() > 4 && Character.isUpperCase( methodName.charAt( 4 ) )) {
            return methodName.substring(3);
        }
        return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
    }

    public static boolean isGetter(String methodName) {
        return (methodName.startsWith("get") && methodName.length() > 3) || (methodName.startsWith("is") && methodName.length() > 2);
    }

    public static boolean isSetter(String methodName) {
        return (methodName.startsWith("set") && methodName.length() > 3);
    }

    public static Class<?> convertFromPrimitiveType(Class<?> type) {
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

    public static Class<?> convertPrimitiveNameToType( String typeName ) {
        return primitiveNameToType.get(typeName);
    }

    public static Set<Class<?>> getAllImplementedInterfaceNames( Class<?> klass ) {
        Set<Class<?>> interfaces = new HashSet<>();
        while( klass != null ) {
            Class<?>[] localInterfaces = klass.getInterfaces();
            for ( Class<?> intf : localInterfaces ) {
                interfaces.add( intf );
                exploreSuperInterfaces( intf, interfaces );
            }
            klass = klass.getSuperclass();
        }
        return interfaces;
    }

    private static void exploreSuperInterfaces( Class<?> intf, Set<Class<?>> traitInterfaces ) {
        for ( Class<?> sup : intf.getInterfaces() ) {
            traitInterfaces.add( sup );
            exploreSuperInterfaces( sup, traitInterfaces );
        }
    }

    public static Set<Class<?>> getMinimalImplementedInterfaceNames( Class<?> klass ) {
        Set<Class<?>> interfaces = new HashSet<>();
        while( klass != null ) {
            Class<?>[] localInterfaces = klass.getInterfaces();
            for ( Class<?> intf : localInterfaces ) {
                boolean subsumed = false;
                for ( Class<?> i : new ArrayList<>(interfaces) ) {
                    if ( intf.isAssignableFrom( i ) ) {
                        subsumed = true;
                        break;
                    } else if ( i.isAssignableFrom( intf ) ) {
                        interfaces.remove( i );
                    }
                }
                if ( subsumed ) {
                    continue;
                }
                interfaces.add( intf );
            }
            klass = klass.getSuperclass();
        }
        return interfaces;
    }

    public static boolean isCaseSenstiveOS() {
        String os =  System.getProperty("os.name").toUpperCase();
        return os.contains( "WINDOWS" ) || os.contains( "MAC OS X" );
    }

    public static boolean isWindows() {
        String os =  System.getProperty("os.name");
        return os.toUpperCase().contains( "WINDOWS" );
    }

    public static boolean isOSX() {
        String os =  System.getProperty("os.name");
        return os.toUpperCase().contains( "MAC OS X" );
    }

    public static boolean isJboss() {
        return System.getProperty("jboss.server.name") != null;
    }

    public static Class<?> findCommonSuperClass(Class<?> c1, Class<?> c2) {
        if (c1 == null) {
            return c2;
        }
        if (c2 == null) {
            return c1;
        }
        if (c1.isAssignableFrom( c2 )) {
            return c1;
        }
        if (c2.isAssignableFrom( c1 )) {
            return c2;
        }
        for (Class<?> parent = c1.getSuperclass(); parent != null; parent = parent.getSuperclass()) {
            if (parent.isAssignableFrom(c2)) {
                return parent;
            }
        }
        return c1;
    }

    public static Class<?> getClassFromName(String className) throws ClassNotFoundException {
        return getClassFromName( className, true, ClassUtils.class.getClassLoader() );
    }

    public static Class<?> getClassFromName(String className, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        try {
            Class<?> clazz;
            if (abbreviationMap.containsKey(className)) {
                final String clsName = "[" + abbreviationMap.get(className);
                clazz = Class.forName(clsName, initialize, classLoader).getComponentType();
            } else {
                clazz = Class.forName(toCanonicalName( className ), initialize, classLoader);
            }
            return clazz;
        } catch (final ClassNotFoundException ex) {
            // allow path separators (.) as inner class name separators
            final int lastDotIndex = className.lastIndexOf('.');

            if (lastDotIndex != -1) {
                try {
                    return getClassFromName( className.substring( 0, lastDotIndex ) + '$' + className.substring( lastDotIndex + 1 ),
                                             initialize, classLoader);
                } catch (final ClassNotFoundException ex2) { // NOPMD
                    // ignore exception
                }
            }

            throw ex;
        }
    }

    private static String toCanonicalName(String className) {
        if (className == null) {
            throw new NullPointerException("className must not be null.");
        } else if (className.endsWith("[]")) {
            final StringBuilder classNameBuffer = new StringBuilder();
            while (className.endsWith("[]")) {
                className = className.substring(0, className.length() - 2);
                classNameBuffer.append("[");
            }
            final String abbreviation = abbreviationMap.get(className);
            if (abbreviation != null) {
                classNameBuffer.append(abbreviation);
            } else {
                classNameBuffer.append("L").append(className).append(";");
            }
            className = classNameBuffer.toString();
        }
        return className;
    }

    public static Class<?> safeLoadClass(ClassLoader cl, String name) {
        try {
            return cl.loadClass( name );
        }
        catch ( final ClassNotFoundException | NoClassDefFoundError cnfe ) {
            // class doesn't exist
            // potential mis-match induced by Mac/OSX
        }
        return null;
    }

    public static String getSimpleName(Class<?> c) {
        return getCanonicalSimpleName( c, '$' );
    }

    public static String getCanonicalSimpleName(Class<?> c) {
        return getCanonicalSimpleName( c, '.' );
    }

    public static String getCanonicalSimpleName(Class<?> c, char separator) {
        Class<?> enclosingClass = c.getEnclosingClass();
        return enclosingClass != null ?
               getCanonicalSimpleName(enclosingClass) + separator + c.getSimpleName() :
               c.getSimpleName();
    }

    public static ClassLoader findParentClassLoader(Class<?> invokingClass) {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        return parent != null ? parent : invokingClass.getClassLoader();
    }

    public static boolean isNumericClass(Class<?> clazz) {
        return numericClasses.contains(clazz);
    }

    public static Type actualTypeFromGenerics(Type scope, Type genericType) {
        return actualTypeFromGenerics(scope, genericType, toRawClass(scope));
    }

    public static Type actualTypeFromGenerics(Type scope, Type genericType, Class<?> rawClassCursor) {
        if (genericType instanceof Class || genericType instanceof ParameterizedType) {
            return genericType;
        }
        if (genericType instanceof TypeVariable typeVar) {
            if (scope instanceof ParameterizedType paramType) {
                return actualTypeFromGenerics(rawClassCursor, paramType, typeVar);
            }
            if (scope instanceof Class classCursor && classCursor.getSuperclass() != null && classCursor.getSuperclass() != Object.class) {
                return actualTypeFromGenerics(classCursor.getGenericSuperclass(), genericType, classCursor.getSuperclass());
            }
            if (typeVar.getBounds().length == 1 && typeVar.getBounds()[0] instanceof Class) {
                return typeVar.getBounds()[0];
            }
        }
        return Object.class;
    }

    private static Type actualTypeFromGenerics(Class<?> rawClassCursor, ParameterizedType originalTypeCursor, TypeVariable genericType) {
        int genericPos = 0;
        for (TypeVariable typeVar : rawClassCursor.getTypeParameters()) {
            if (typeVar.equals( genericType )) {
                return originalTypeCursor.getActualTypeArguments()[genericPos];
            }
            genericPos++;
        }
        throw new RuntimeException( "Unknonw generic type " + genericType + " for type " + originalTypeCursor );
    }
}
