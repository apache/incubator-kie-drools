/*
 * Copyright 2005 JBoss Inc
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

package org.drools.base;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.asm.ClassWriter;
import org.drools.asm.FieldVisitor;
import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;
import org.drools.util.ShadowProxyUtils;

/**
 * A factory for ShadowProxy classes
 */
public class ShadowProxyFactory {
    private static final String           UPDATE_PROXY         = "updateProxy";
    private static final String           SET_SHADOWED_OBJECT  = "setShadowedObject";
    private static final String           GET_SHADOWED_OBJECT  = "getShadowedObject";

    private static final String           BASE_INTERFACE       = Type.getInternalName( ShadowProxy.class );

    //private static final String FIELD_NAME_PREFIX   = "__";

    public static final String            FIELD_SET_FLAG       = "IsSet";

    public static final String            DELEGATE_FIELD_NAME  = "delegate";

    public static final String            HASHCACHE_FIELD_NAME = "__hashCache";

    private static final ProtectionDomain PROTECTION_DOMAIN;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {
            public Object run() {
                return ShadowProxyFactory.class.getProtectionDomain();
            }
        } );
    }

    public static Class getProxy(final Class clazz) {
        try {
            if ( !isPossibleToGenerateTheProxyFor( clazz ) ) {
                return null;
            }

            final String className = getInternalProxyClassNameForClass( clazz );
            // generating byte array to create target class
            final byte[] bytes = dump( clazz,
                                       className );
            // use bytes to get a class 
            final ByteArrayClassLoader classLoader = new ByteArrayClassLoader( Thread.currentThread().getContextClassLoader() );
            final Class newClass = classLoader.defineClass( className.replace( '/',
                                                                               '.' ),
                                                            bytes,
                                                            PROTECTION_DOMAIN );
            return newClass;
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public static byte[] getProxyBytes(final Class clazz) {
        try {
            if ( !isPossibleToGenerateTheProxyFor( clazz ) ) {
                return null;
            }

            final String className = getInternalProxyClassNameForClass( clazz );
            // generating byte array to create target class
            final byte[] bytes = dump( clazz,
                                       className );
            return bytes;
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    protected static boolean isPossibleToGenerateTheProxyFor(final Class clazz) throws Exception {
        if ( (clazz.getModifiers() & Modifier.FINAL) != 0 ) {
            return false;
        }
        try {
            Method equals = clazz.getMethod( "equals",
                                             new Class[]{Object.class} );
            if ( Modifier.isFinal( equals.getModifiers() ) ) {
                return false;
            }
        } catch ( NoSuchMethodException e ) {
            // that's fine
        }
        try {
            Method hashcode = clazz.getMethod( "hashCode",
                                               new Class[0] );
            if ( Modifier.isFinal( hashcode.getModifiers() ) ) {
                return false;
            }
        } catch ( NoSuchMethodException e ) {
            // that's fine
        }
        return true;
    }

    /**
     * @param clazz
     * @return
     */
    public static String getInternalProxyClassNameForClass(final Class clazz) {
        String className = null;
        if ( clazz.getPackage() != null && (clazz.getPackage().getName().startsWith( "java." ) || clazz.getPackage().getName().startsWith( "javax." )) ) {
            className = "org/drools/shadow/" + Type.getInternalName( clazz ) + "ShadowProxy";
        } else {
            className = Type.getInternalName( clazz ) + "ShadowProxy";
        }
        return className;
    }

    public static String getProxyClassNameForClass(final Class clazz) {
        String className = null;
        Package pkg = clazz.getPackage();
        if ( pkg != null && (pkg.getName().startsWith( "java." ) || pkg.getName().startsWith( "javax." )) ) {
            className = "org.drools.shadow." + clazz.getName() + "ShadowProxy";
        } else {
            className = clazz.getName() + "ShadowProxy";
        }
        return className;
    }

    protected static byte[] dump(final Class clazz,
                                 final String className) throws Exception {

        final ClassWriter cw = new ClassWriter( true );

        buildClassHeader( clazz,
                          className,
                          cw );

        buildConstructor( clazz,
                          className,
                          cw );

        buildField( ShadowProxyFactory.DELEGATE_FIELD_NAME,
                    Type.getDescriptor( clazz ),
                    cw );

        final Method getShadowed = ShadowProxy.class.getDeclaredMethod( GET_SHADOWED_OBJECT,
                                                                        new Class[]{} );
        final Method setShadowed = ShadowProxy.class.getDeclaredMethod( SET_SHADOWED_OBJECT,
                                                                        new Class[]{Object.class} );
        buildSimpleGetMethod( ShadowProxyFactory.DELEGATE_FIELD_NAME,
                              clazz,
                              getShadowed,
                              className,
                              clazz,
                              cw );

        buildSetShadowedObject( clazz,
                                className,
                                setShadowed,
                                cw );

        if ( Collection.class.isAssignableFrom( clazz ) ) {
            buildCollectionClass( clazz,
                                  className,
                                  cw );
        } else if ( Map.class.isAssignableFrom( clazz ) ) {
            buildMapClass( clazz,
                           className,
                           cw );
        } else {
            buildRegularClass( clazz,
                               className,
                               cw );
        }

        return cw.toByteArray();
    }

    private static void buildCollectionClass(final Class clazz,
                                             final String className,
                                             final ClassWriter cw) {

        buildCollectionUpdateProxyMethod( clazz,
                                          className,
                                          cw );

    }

    private static void buildMapClass(final Class clazz,
                                      final String className,
                                      final ClassWriter cw) {

        buildMapUpdateProxyMethod( clazz,
                                   className,
                                   cw );

    }

    private static void buildRegularClass(final Class clazz,
                                          final String className,
                                          final ClassWriter cw) {
        final Map fieldTypes = new HashMap();
        final Method[] methods = getMethods( clazz );
        for ( int i = 0; i < methods.length; i++ ) {
            if ( (!Modifier.isFinal( methods[i].getModifiers() )) && Modifier.isPublic( methods[i].getModifiers() ) && (!Modifier.isStatic( methods[i].getModifiers() )) ) {
                if ( (!methods[i].getReturnType().equals( Void.TYPE )) && (methods[i].getParameterTypes().length == 0) && (!methods[i].getName().equals( "hashCode" )) && (!methods[i].getName().equals( "toString" )) ) {

                    final String fieldName = methods[i].getName();

                    buildField( fieldName,
                                Type.getDescriptor( methods[i].getReturnType() ),
                                cw );
                    fieldTypes.put( fieldName,
                                    methods[i] );

                    buildField( fieldName + ShadowProxyFactory.FIELD_SET_FLAG,
                                Type.BOOLEAN_TYPE.getDescriptor(),
                                cw );
                    buildGetMethod( fieldName,
                                    methods[i].getReturnType(),
                                    fieldName + ShadowProxyFactory.FIELD_SET_FLAG,
                                    methods[i],
                                    className,
                                    clazz,
                                    cw );
                } else if ( (!methods[i].getName().equals( "hashCode" )) && (!methods[i].getName().equals( "equals" )) ) {
                    buildDelegateMethod( methods[i],
                                         clazz,
                                         className,
                                         cw );
                }
            }
        }

        buildUpdateProxyMethod( fieldTypes,
                                className,
                                cw );

        buildEquals( cw,
                     className,
                     clazz,
                     fieldTypes );

        buildField( ShadowProxyFactory.HASHCACHE_FIELD_NAME,
                    Type.getDescriptor( int.class ),
                    cw );

        buildHashCode( cw,
                       className,
                       clazz,
                       fieldTypes );
    }

    /**
     * Filter out any method we are not interested in
     * @param clazz
     * @return
     */
    private static Method[] getMethods(final Class clazz) {
        // to help filtering process, we will create a map of maps:
        // Map< String methodName, Map< Class[] parameterTypes, Method method > >
        final Map map = new HashMap();
        final List helperList = new ArrayList();
        final Method[] methods = clazz.getMethods();
        for ( int i = 0; i < methods.length; i++ ) {
            Method previous = null;
            Map signatures = (Map) map.get( methods[i].getName() );
            final ParametersWrapper key = new ParametersWrapper( methods[i].getParameterTypes() );
            if ( signatures != null ) {
                previous = (Method) signatures.get( key );
            }
            // if no previous method with the same name and parameter types is found
            // or if the previous method's return type is a super class of the 
            // current method's return type, add current to the map
            // overriding previous if it exists
            if ( (previous == null) || (previous.getReturnType().isAssignableFrom( methods[i].getReturnType() )) ) {
                if ( signatures == null ) {
                    signatures = new HashMap();
                    map.put( methods[i].getName(),
                             signatures );
                }
                if ( signatures.put( key,
                                     methods[i] ) != null ) {
                    helperList.remove( previous );
                }
                helperList.add( methods[i] );
            }
        }
        return (Method[]) helperList.toArray( new Method[helperList.size()] );
    }

    private static class ParametersWrapper {
        private Class[] parameters;

        public ParametersWrapper(final Class[] parameters) {
            this.parameters = parameters;
        }

        public int hashCode() {
            return this.parameters.length;
        }

        public boolean equals(final Object o) {
            if ( !(o instanceof ParametersWrapper) ) {
                return false;
            }
            final ParametersWrapper other = (ParametersWrapper) o;

            if ( this.parameters.length != other.parameters.length ) {
                return false;
            }

            for ( int i = 0; i < this.parameters.length; i++ ) {
                if ( !this.parameters[i].equals( other.parameters[i] ) ) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Builds the shadow proxy class header
     *  
     * @param clazz The class to build shadow proxy for
     * @param className The shadow proxy class name
     * @param cw
     */
    protected static void buildClassHeader(final Class clazz,
                                           final String className,
                                           final ClassWriter cw) {
        if ( clazz.isInterface() ) {
            cw.visit( Opcodes.V1_2,
                      Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                      className,
                      null,
                      Type.getInternalName( Object.class ),
                      new String[]{ShadowProxyFactory.BASE_INTERFACE, Type.getInternalName( clazz )} );
        } else {
            cw.visit( Opcodes.V1_2,
                      Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                      className,
                      null,
                      Type.getInternalName( clazz ),
                      new String[]{ShadowProxyFactory.BASE_INTERFACE} );
        }

        cw.visitSource( null,
                        null );
    }

    /**
     * Creates the field defined by the given FieldDefinition 
     * 
     * @param cw
     * @param fieldDef
     */
    protected static void buildField(final String name,
                                     final String type,
                                     final ClassWriter cw) {
        FieldVisitor fv;
        fv = cw.visitField( Opcodes.ACC_PRIVATE,
                            name,
                            type,
                            null,
                            null );
        fv.visitEnd();
    }

    /**
     * Creates a constructor for the shadow proxy receiving
     * the actual delegate class as parameter
     * 
     * @param originalClassName
     * @param className
     * @param cw
     */
    private static void buildConstructor(final Class clazz,
                                         final String className,
                                         final ClassWriter cw) {
        MethodVisitor mv;
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "<init>",
                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                           new Type[]{Type.getType( clazz )} ),
                                 null,
                                 null );
            mv.visitCode();

            // super();
            final Label l0 = new Label();
            mv.visitLabel( l0 );
            mv.visitLineNumber( 41,
                                l0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            if ( clazz.isInterface() ) {
                mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                    Type.getInternalName( Object.class ),
                                    "<init>",
                                    Type.getMethodDescriptor( Type.VOID_TYPE,
                                                              new Type[]{} ) );
            } else {
                mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                    Type.getInternalName( clazz ),
                                    "<init>",
                                    Type.getMethodDescriptor( Type.VOID_TYPE,
                                                              new Type[]{} ) );
            }

            // this.delegate = delegate
            final Label l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitLineNumber( 42,
                                l1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               className,
                               ShadowProxyFactory.DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );

            // return
            final Label l2 = new Label();
            mv.visitLabel( l2 );
            mv.visitLineNumber( 43,
                                l2 );
            mv.visitInsn( Opcodes.RETURN );

            final Label l3 = new Label();
            mv.visitLabel( l3 );
            mv.visitLocalVariable( "this",
                                   "L" + className + ";",
                                   null,
                                   l0,
                                   l3,
                                   0 );
            mv.visitLocalVariable( ShadowProxyFactory.DELEGATE_FIELD_NAME,
                                   Type.getDescriptor( clazz ),
                                   null,
                                   l0,
                                   l3,
                                   1 );
            mv.visitMaxs( 0,
                          0 );
            mv.visitEnd();
        }
    }

    /**
     * Creates the proxy reader method for the given method
     * 
     * @param fieldName
     * @param fieldFlag
     * @param method
     * @param cw
     */
    protected static void buildGetMethod(final String fieldName,
                                         final Class fieldType,
                                         final String fieldFlag,
                                         final Method method,
                                         final String className,
                                         final Class clazz,
                                         final ClassWriter cw) {
        // method signature 
        final Class[] exceptionTypes = method.getExceptionTypes();
        final String[] exceptions = getExceptionArrayAsString( exceptionTypes );
        final MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                                 method.getName(),
                                                 Type.getMethodDescriptor( method ),
                                                 null,
                                                 exceptions );
        mv.visitCode();

        // if ( ! _fieldIsSet ) {
        final Label l0 = new Label();
        mv.visitLabel( l0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitFieldInsn( Opcodes.GETFIELD,
                           className,
                           fieldFlag,
                           Type.BOOLEAN_TYPE.getDescriptor() );
        final Label l1 = new Label();
        mv.visitJumpInsn( Opcodes.IFNE,
                          l1 );

        //     __fieldIsSet = true;
        final Label l3 = new Label();
        mv.visitLabel( l3 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitInsn( Opcodes.ICONST_1 );
        mv.visitFieldInsn( Opcodes.PUTFIELD,
                           className,
                           fieldFlag,
                           Type.BOOLEAN_TYPE.getDescriptor() );

        if ( Map.class.isAssignableFrom( fieldType ) || Collection.class.isAssignableFrom( fieldType ) || fieldType.isArray() ) {

            // FieldType aux = this.delegate.getField();
            Label l01 = new Label();
            mv.visitLabel( l01 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );
            if ( clazz.isInterface() ) {
                mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                    Type.getInternalName( clazz ),
                                    method.getName(),
                                    Type.getMethodDescriptor( method ) );
            } else {
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    Type.getInternalName( clazz ),
                                    method.getName(),
                                    Type.getMethodDescriptor( method ) );
            }
            mv.visitVarInsn( Opcodes.ASTORE,
                             1 );

            // this.field = (FieldType) ShadoProxyUtils.clone( aux );
            Label l11 = new Label();
            mv.visitLabel( l11 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitMethodInsn( Opcodes.INVOKESTATIC,
                                Type.getInternalName( ShadowProxyUtils.class ),
                                "cloneObject",
                                "(Ljava/lang/Object;)Ljava/lang/Object;" );
            mv.visitTypeInsn( Opcodes.CHECKCAST,
                              Type.getInternalName( fieldType ) );
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               className,
                               fieldName,
                               Type.getDescriptor( fieldType ) );

        } else {
            //     __field = this.delegate.method();
            final Label l2 = new Label();
            mv.visitLabel( l2 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               ShadowProxyFactory.DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );
            if ( clazz.isInterface() ) {
                mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                    Type.getInternalName( clazz ),
                                    method.getName(),
                                    Type.getMethodDescriptor( method ) );
            } else {
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    Type.getInternalName( clazz ),
                                    method.getName(),
                                    Type.getMethodDescriptor( method ) );
            }
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               className,
                               fieldName,
                               Type.getDescriptor( fieldType ) );

        }

        // }
        // return __field;
        mv.visitLabel( l1 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitFieldInsn( Opcodes.GETFIELD,
                           className,
                           fieldName,
                           Type.getDescriptor( fieldType ) );
        mv.visitInsn( Type.getType( fieldType ).getOpcode( Opcodes.IRETURN ) );

        // local variables table
        final Label l4 = new Label();
        mv.visitLabel( l4 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l4,
                               0 );
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    /**
     * Creates the proxy reader method for the given method
     * 
     * @param fieldName
     * @param fieldFlag
     * @param method
     * @param cw
     */
    protected static void buildSimpleGetMethod(final String fieldName,
                                               final Class fieldType,
                                               final Method method,
                                               final String className,
                                               final Class clazz,
                                               final ClassWriter cw) {

        final Class[] exceptionTypes = method.getExceptionTypes();
        final String[] exceptions = getExceptionArrayAsString( exceptionTypes );
        // method signature 
        final MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                                 method.getName(),
                                                 Type.getMethodDescriptor( method ),
                                                 null,
                                                 exceptions );
        mv.visitCode();

        // return __field;
        final Label l0 = new Label();
        mv.visitLabel( l0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitFieldInsn( Opcodes.GETFIELD,
                           className,
                           fieldName,
                           Type.getDescriptor( fieldType ) );
        mv.visitInsn( Type.getType( fieldType ).getOpcode( Opcodes.IRETURN ) );

        // local variables table
        final Label l4 = new Label();
        mv.visitLabel( l4 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l4,
                               0 );
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    protected static void buildUpdateProxyMethod(final Map fieldTypes,
                                                 final String className,
                                                 final ClassWriter cw) {
        final MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                                 ShadowProxyFactory.UPDATE_PROXY,
                                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                           new Type[]{} ),
                                                 null,
                                                 null );
        mv.visitCode();
        final Label l0 = new Label();
        mv.visitLabel( l0 );
        for ( final Iterator it = fieldTypes.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry entry = (Map.Entry) it.next();
            final String fieldName = (String) entry.getKey();
            final String fieldFlag = fieldName + ShadowProxyFactory.FIELD_SET_FLAG;
            final Class fieldType = ((Method) entry.getValue()).getReturnType();
            final Label l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            if ( fieldType.isPrimitive() ) {
                if ( fieldType.equals( Long.TYPE ) ) {
                    mv.visitInsn( Opcodes.LCONST_0 );
                } else if ( fieldType.equals( Double.TYPE ) ) {
                    mv.visitInsn( Opcodes.DCONST_0 );
                } else if ( fieldType.equals( Float.TYPE ) ) {
                    mv.visitInsn( Opcodes.FCONST_0 );
                } else {
                    mv.visitInsn( Opcodes.ICONST_0 );
                }
            } else {
                mv.visitInsn( Opcodes.ACONST_NULL );
            }
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               className,
                               fieldName,
                               Type.getDescriptor( fieldType ) );
            final Label l2 = new Label();
            mv.visitLabel( l2 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitInsn( Opcodes.ICONST_0 );
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               className,
                               fieldFlag,
                               Type.BOOLEAN_TYPE.getDescriptor() );
        }

        //    this.__hashCache = 0;
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitInsn( Opcodes.ICONST_0 );
        mv.visitFieldInsn( Opcodes.PUTFIELD,
                           className,
                           ShadowProxyFactory.HASHCACHE_FIELD_NAME,
                           Type.getDescriptor( int.class ) );

        final Label l4 = new Label();
        mv.visitLabel( l4 );
        mv.visitInsn( Opcodes.RETURN );
        final Label l5 = new Label();
        mv.visitLabel( l5 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l5,
                               0 );
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    protected static void buildSetShadowedObject(final Class clazz,
                                                 final String className,
                                                 final Method setShadowed,
                                                 final ClassWriter cw) {
        final MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                                 setShadowed.getName(),
                                                 Type.getMethodDescriptor( setShadowed ),
                                                 null,
                                                 null );
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel( l0 );
        // this.delegate = (<clazz>) object;
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         1 );
        mv.visitTypeInsn( Opcodes.CHECKCAST,
                          Type.getInternalName( clazz ) );
        mv.visitFieldInsn( Opcodes.PUTFIELD,
                           className,
                           DELEGATE_FIELD_NAME,
                           Type.getDescriptor( clazz ) );
        if ( Collection.class.isAssignableFrom( clazz ) || Map.class.isAssignableFrom( clazz ) ) {
            Label l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                className,
                                UPDATE_PROXY,
                                Type.getMethodDescriptor( Type.VOID_TYPE,
                                                          new Type[0] ) );
        }
        Label l2 = new Label();
        mv.visitLabel( l2 );
        mv.visitInsn( Opcodes.RETURN );
        Label l3 = new Label();
        mv.visitLabel( l3 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l3,
                               0 );
        mv.visitLocalVariable( "object",
                               Type.getDescriptor( Object.class ),
                               null,
                               l0,
                               l3,
                               1 );
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    /**
     * Creates an update proxy method for Map classes
     * 
     * public void updateProxy() {
     *     this.clear();
     *     this.addAll( this.delegate );
     * }
     * 
     * @param clazz
     * @param className
     * @param cw
     */
    protected static void buildCollectionUpdateProxyMethod(final Class clazz,
                                                           final String className,
                                                           final ClassWriter cw) {
        final MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                                 ShadowProxyFactory.UPDATE_PROXY,
                                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                           new Type[]{} ),
                                                 null,
                                                 null );
        mv.visitCode();
        final Label l0 = new Label();
        mv.visitLabel( l0 );
        // this.clear();
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                            className,
                            "clear",
                            Type.getMethodDescriptor( Type.VOID_TYPE,
                                                      new Type[0] ) );
        Label l1 = new Label();
        mv.visitLabel( l1 );
        // this.addAll( this.delegate );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitFieldInsn( Opcodes.GETFIELD,
                           className,
                           DELEGATE_FIELD_NAME,
                           Type.getDescriptor( clazz ) );
        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                            className,
                            "addAll",
                            Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                      new Type[]{Type.getType( Collection.class )} ) );
        mv.visitInsn( Opcodes.POP );
        Label l2 = new Label();
        mv.visitLabel( l2 );
        mv.visitInsn( Opcodes.RETURN );
        Label l3 = new Label();
        mv.visitLabel( l3 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l3,
                               0 );
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    /**
     * Creates an update proxy method for Map classes
     * 
     * public void updateProxy() {
     *     this.clear();
     *     this.putAll( this.delegate );
     * }
     * 
     * @param clazz
     * @param className
     * @param cw
     */
    protected static void buildMapUpdateProxyMethod(final Class clazz,
                                                    final String className,
                                                    final ClassWriter cw) {
        final MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                                 ShadowProxyFactory.UPDATE_PROXY,
                                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                           new Type[]{} ),
                                                 null,
                                                 null );
        mv.visitCode();
        final Label l0 = new Label();
        mv.visitLabel( l0 );
        // this.clear();
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                            className,
                            "clear",
                            Type.getMethodDescriptor( Type.VOID_TYPE,
                                                      new Type[0] ) );
        Label l1 = new Label();
        mv.visitLabel( l1 );
        // this.putAll( this.delegate );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitFieldInsn( Opcodes.GETFIELD,
                           className,
                           DELEGATE_FIELD_NAME,
                           Type.getDescriptor( clazz ) );
        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                            className,
                            "putAll",
                            Type.getMethodDescriptor( Type.VOID_TYPE,
                                                      new Type[]{Type.getType( Map.class )} ) );
        Label l2 = new Label();
        mv.visitLabel( l2 );
        mv.visitInsn( Opcodes.RETURN );
        Label l3 = new Label();
        mv.visitLabel( l3 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l3,
                               0 );
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    protected static void buildDelegateMethod(final Method method,
                                              final Class clazz,
                                              final String className,
                                              final ClassWriter cw) {

        // creating method visitor
        final String[] exceptions = getExceptionArrayAsString( method.getExceptionTypes() );
        final MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                                 method.getName(),
                                                 Type.getMethodDescriptor( method ),
                                                 null,
                                                 exceptions );
        mv.visitCode();

        // return this.delegate.method(...);
        final Label l0 = new Label();
        mv.visitLabel( l0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitFieldInsn( Opcodes.GETFIELD,
                           className,
                           ShadowProxyFactory.DELEGATE_FIELD_NAME,
                           Type.getDescriptor( clazz ) );

        final Class[] parameters = method.getParameterTypes();
        for ( int i = 0, offset = 1; i < parameters.length; i++ ) {
            Type type = Type.getType( parameters[i] );
            mv.visitVarInsn( type.getOpcode( Opcodes.ILOAD ),
                             offset );
            offset += type.getSize();
        }
        if ( clazz.isInterface() ) {
            mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                Type.getInternalName( clazz ),
                                method.getName(),
                                Type.getMethodDescriptor( method ) );
        } else {
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( clazz ),
                                method.getName(),
                                Type.getMethodDescriptor( method ) );
        }
        mv.visitInsn( Type.getType( method.getReturnType() ).getOpcode( Opcodes.IRETURN ) );
        final Label l1 = new Label();
        mv.visitLabel( l1 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l1,
                               0 );
        for ( int i = 0, offset = 0; i < parameters.length; i++ ) {
            mv.visitLocalVariable( "arg" + i,
                                   Type.getDescriptor( parameters[i] ),
                                   null,
                                   l0,
                                   l1,
                                   offset );
            offset += Type.getType( parameters[i] ).getSize();
        }
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    protected static void buildEquals(final ClassWriter cw,
                                      final String className,
                                      final Class clazz,
                                      final Map fieldTypes) {
        MethodVisitor mv;
        // Building equals method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "equals",
                                 Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                           new Type[]{Type.getType( Object.class )} ),
                                 null,
                                 null );
            mv.visitCode();
            final Label l0 = new Label();
            mv.visitLabel( l0 );

            // if ( this == object || this.delegate == object || this.delegate.equals( object ) ) {
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            final Label l1 = new Label();
            mv.visitJumpInsn( Opcodes.IF_ACMPEQ,
                              l1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitJumpInsn( Opcodes.IF_ACMPEQ,
                              l1 );
            
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            if ( clazz.isInterface() ) {
                mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                    Type.getInternalName( clazz ),
                                    "equals",
                                    Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                              new Type[]{Type.getType( Object.class )} ) );
            } else {
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    Type.getInternalName( clazz ),
                                    "equals",
                                    Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                              new Type[]{Type.getType( Object.class )} ) );
            }
            Label l2 = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, l2);
            
            //      return true;
            mv.visitLabel( l1 );
            mv.visitInsn( Opcodes.ICONST_1 );
            mv.visitInsn( Opcodes.IRETURN );
            mv.visitLabel( l2 );

            // if (( object == null ) || ( ! ( object instanceof <class> ) ) ) 
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            final Label l3 = new Label();
            mv.visitJumpInsn( Opcodes.IFNULL,
                              l3 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.INSTANCEOF,
                              Type.getInternalName( clazz ) );
            final Label l4 = new Label();
            mv.visitJumpInsn( Opcodes.IFNE,
                              l4 );

            //       return false;
            mv.visitLabel( l3 );
            mv.visitInsn( Opcodes.ICONST_0 );
            mv.visitInsn( Opcodes.IRETURN );
            mv.visitLabel( l4 );

            // if( object instanceof ShadowProxy && 
            //     ( this.delegate == ((ShadowProxy)object).delegate ||
            //       this.delegate.equals( ((ShadowProxy)object).delegate ) ) ) {
            Label c0 = new Label();
            mv.visitLabel( c0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.INSTANCEOF,
                              className );
            Label c1 = new Label();
            mv.visitJumpInsn( Opcodes.IFEQ,
                              c1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.CHECKCAST,
                              className );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );
            Label c2 = new Label();
            mv.visitJumpInsn( Opcodes.IF_ACMPEQ,
                              c2 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.CHECKCAST,
                              className );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );
            if ( clazz.isInterface() ) {
                mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                    Type.getInternalName( clazz ),
                                    "equals",
                                    Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                              new Type[]{Type.getType( Object.class )} ) );
            } else {
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    Type.getInternalName( clazz ),
                                    "equals",
                                    Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                              new Type[]{Type.getType( Object.class )} ) );
            }
            mv.visitJumpInsn( Opcodes.IFEQ,
                              c1 );
            mv.visitLabel( c2 );
            //     return true;
            mv.visitInsn( Opcodes.ICONST_1 );
            mv.visitInsn( Opcodes.IRETURN );
            // }
            mv.visitLabel( c1 );
            

            // <class> other = (<class>) object;
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.CHECKCAST,
                              Type.getInternalName( clazz ) );
            mv.visitVarInsn( Opcodes.ASTORE,
                             2 );

            // for each field:
            int count = 0;
            for ( final Iterator it = fieldTypes.entrySet().iterator(); it.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) it.next();
                final String fieldName = (String) entry.getKey();
                final Method method = (Method) entry.getValue();
                final Class fieldType = method.getReturnType();
                final String fieldFlag = fieldName + ShadowProxyFactory.FIELD_SET_FLAG;
                count++;
                final Label goNext = new Label();

                // if ( ! _fieldIsSet ) {
                final Label l5 = new Label();
                mv.visitLabel( l5 );
                mv.visitVarInsn( Opcodes.ALOAD,
                                 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD,
                                   className,
                                   fieldFlag,
                                   Type.BOOLEAN_TYPE.getDescriptor() );
                final Label l6 = new Label();
                mv.visitJumpInsn( Opcodes.IFNE,
                                  l6 );

                //     __field = this.delegate.method();
                final Label l7 = new Label();
                mv.visitLabel( l7 );
                mv.visitVarInsn( Opcodes.ALOAD,
                                 0 );
                mv.visitVarInsn( Opcodes.ALOAD,
                                 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD,
                                   className,
                                   ShadowProxyFactory.DELEGATE_FIELD_NAME,
                                   Type.getDescriptor( clazz ) );
                if ( clazz.isInterface() ) {
                    mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                        Type.getInternalName( clazz ),
                                        method.getName(),
                                        Type.getMethodDescriptor( method ) );
                } else {
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                        Type.getInternalName( clazz ),
                                        method.getName(),
                                        Type.getMethodDescriptor( method ) );
                }
                mv.visitFieldInsn( Opcodes.PUTFIELD,
                                   className,
                                   fieldName,
                                   Type.getDescriptor( fieldType ) );

                //     __fieldIsSet = true;
                final Label l8 = new Label();
                mv.visitLabel( l8 );
                mv.visitVarInsn( Opcodes.ALOAD,
                                 0 );
                mv.visitInsn( Opcodes.ICONST_1 );
                mv.visitFieldInsn( Opcodes.PUTFIELD,
                                   className,
                                   fieldFlag,
                                   Type.BOOLEAN_TYPE.getDescriptor() );

                // }
                mv.visitLabel( l6 );
                if ( fieldType.isPrimitive() ) {
                    // for primitive types
                    // if ( this.field != other.field ) 
                    mv.visitVarInsn( Opcodes.ALOAD,
                                     0 );
                    mv.visitFieldInsn( Opcodes.GETFIELD,
                                       className,
                                       fieldName,
                                       Type.getDescriptor( fieldType ) );
                    mv.visitVarInsn( Opcodes.ALOAD,
                                     2 );
                    if ( clazz.isInterface() ) {
                        mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                            Type.getInternalName( clazz ),
                                            method.getName(),
                                            Type.getMethodDescriptor( method ) );
                    } else {
                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                            Type.getInternalName( clazz ),
                                            method.getName(),
                                            Type.getMethodDescriptor( method ) );
                    }

                    if ( fieldType.equals( Long.TYPE ) ) {
                        mv.visitInsn( Opcodes.LCMP );
                        mv.visitJumpInsn( Opcodes.IFEQ,
                                          goNext );
                    } else if ( fieldType.equals( Double.TYPE ) ) {
                        mv.visitInsn( Opcodes.DCMPL );
                        mv.visitJumpInsn( Opcodes.IFEQ,
                                          goNext );
                    } else if ( fieldType.equals( Float.TYPE ) ) {
                        mv.visitInsn( Opcodes.FCMPL );
                        mv.visitJumpInsn( Opcodes.IFEQ,
                                          goNext );
                    } else {
                        mv.visitJumpInsn( Opcodes.IF_ICMPEQ,
                                          goNext );
                    }
                    //     return false;
                    mv.visitInsn( Opcodes.ICONST_0 );
                    mv.visitInsn( Opcodes.IRETURN );
                } else {
                    // for non primitive types
                    // if( ( ( this.field == null ) && ( other.field != null ) ) ||
                    //     ( ( this.field != null ) && ( ! this.field.equals( other.field ) ) ) )
                    mv.visitVarInsn( Opcodes.ALOAD,
                                     0 );
                    mv.visitFieldInsn( Opcodes.GETFIELD,
                                       className,
                                       fieldName,
                                       Type.getDescriptor( fieldType ) );
                    final Label secondIfPart = new Label();
                    mv.visitJumpInsn( Opcodes.IFNONNULL,
                                      secondIfPart );
                    mv.visitVarInsn( Opcodes.ALOAD,
                                     2 );
                    if ( clazz.isInterface() ) {
                        mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                            Type.getInternalName( clazz ),
                                            method.getName(),
                                            Type.getMethodDescriptor( method ) );
                    } else {
                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                            Type.getInternalName( clazz ),
                                            method.getName(),
                                            Type.getMethodDescriptor( method ) );
                    }
                    final Label returnFalse = new Label();
                    mv.visitJumpInsn( Opcodes.IFNONNULL,
                                      returnFalse );
                    mv.visitLabel( secondIfPart );
                    mv.visitVarInsn( Opcodes.ALOAD,
                                     0 );
                    mv.visitFieldInsn( Opcodes.GETFIELD,
                                       className,
                                       fieldName,
                                       Type.getDescriptor( fieldType ) );
                    mv.visitJumpInsn( Opcodes.IFNULL,
                                      goNext );
                    mv.visitVarInsn( Opcodes.ALOAD,
                                     0 );
                    mv.visitFieldInsn( Opcodes.GETFIELD,
                                       className,
                                       fieldName,
                                       Type.getDescriptor( fieldType ) );
                    mv.visitVarInsn( Opcodes.ALOAD,
                                     2 );
                    if ( clazz.isInterface() ) {
                        mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                            Type.getInternalName( clazz ),
                                            method.getName(),
                                            Type.getMethodDescriptor( method ) );
                    } else {
                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                            Type.getInternalName( clazz ),
                                            method.getName(),
                                            Type.getMethodDescriptor( method ) );
                    }
                    if ( fieldType.isInterface() ) {
                        mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                            Type.getInternalName( fieldType ),
                                            "equals",
                                            Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                                      new Type[]{Type.getType( Object.class )} ) );
                    } else {
                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                            Type.getInternalName( fieldType ),
                                            "equals",
                                            Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                                      new Type[]{Type.getType( Object.class )} ) );
                    }
                    mv.visitJumpInsn( Opcodes.IFNE,
                                      goNext );
                    //       return false;
                    mv.visitLabel( returnFalse );
                    mv.visitInsn( Opcodes.ICONST_0 );
                    mv.visitInsn( Opcodes.IRETURN );
                }
                mv.visitLabel( goNext );
            }
            // if all fields were ok
            if ( count > 0 ) {
                // return true;
                mv.visitInsn( Opcodes.ICONST_1 );
                // if no fields exists
            } else {
                // return false;
                mv.visitInsn( Opcodes.ICONST_0 );
            }
            mv.visitInsn( Opcodes.IRETURN );
            final Label lastLabel = new Label();
            mv.visitLabel( lastLabel );

            mv.visitLocalVariable( "this",
                                   "L" + className + ";",
                                   null,
                                   l0,
                                   lastLabel,
                                   0 );
            mv.visitLocalVariable( "object",
                                   Type.getDescriptor( Object.class ),
                                   null,
                                   l0,
                                   lastLabel,
                                   1 );
            mv.visitLocalVariable( "other",
                                   Type.getDescriptor( clazz ),
                                   null,
                                   l0,
                                   lastLabel,
                                   2 );

            mv.visitMaxs( 0,
                          0 );
            mv.visitEnd();
        }
    }

    protected static void buildCollectionEquals(final ClassWriter cw,
                                                final String className,
                                                final Class clazz) {

        final MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                                 "equals",
                                                 Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                                           new Type[]{Type.getType( Object.class )} ),
                                                 null,
                                                 null );
        // if ( this == object ) {
        Label l0 = new Label();
        mv.visitLabel( l0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         1 );
        Label l1 = new Label();
        mv.visitJumpInsn( Opcodes.IF_ACMPNE,
                          l1 );
        //    return true;
        Label l2 = new Label();
        mv.visitLabel( l2 );
        mv.visitInsn( Opcodes.ICONST_1 );
        mv.visitInsn( Opcodes.IRETURN );
        // }
        mv.visitLabel( l1 );
        // return this.delegate.equals( object );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitFieldInsn( Opcodes.GETFIELD,
                           className,
                           DELEGATE_FIELD_NAME,
                           Type.getDescriptor( clazz ) );
        mv.visitVarInsn( Opcodes.ALOAD,
                         1 );
        if ( clazz.isInterface() ) {
            mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                Type.getInternalName( clazz ),
                                "equals",
                                Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                          new Type[]{Type.getType( Object.class )} ) );
        } else {
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( clazz ),
                                "equals",
                                Type.getMethodDescriptor( Type.BOOLEAN_TYPE,
                                                          new Type[]{Type.getType( Object.class )} ) );
        }
        mv.visitInsn( Opcodes.IRETURN );
        Label l3 = new Label();
        mv.visitLabel( l3 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l3,
                               0 );
        mv.visitLocalVariable( "object",
                               Type.getDescriptor( Object.class ),
                               null,
                               l0,
                               l3,
                               1 );
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    /**
     *  Sample of generated code for all primitive + object types
     *  
     *  public int hashCode() {
     *       if( ___hashCache == 0 ) {
     *           __hashCache = this.delegate.hashCode();
     *       }
     *       return this.__hashCache;
     *   }
     * 
     * @param cw
     * @param className
     * @param clazz
     * @param fieldTypes
     */
    protected static void buildHashCode(final ClassWriter cw,
                                        final String className,
                                        final Class clazz,
                                        final Map fieldTypes) {
        MethodVisitor mv;
        // Building hashcode method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "hashCode",
                                 Type.getMethodDescriptor( Type.INT_TYPE,
                                                           new Type[]{} ),
                                 null,
                                 null );
            mv.visitCode();

            // if( this.__hashCache == 0 ) {
            Label l0 = new Label();
            mv.visitLabel( l0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               HASHCACHE_FIELD_NAME,
                               Type.INT_TYPE.getDescriptor() );
            Label l1 = new Label();
            mv.visitJumpInsn( Opcodes.IFNE,
                              l1 );
            Label l2 = new Label();

            //    this.__hashCache = this.delegate.hashCode();
            mv.visitLabel( l2 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );
            if ( clazz.isInterface() ) {
                mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                    Type.getInternalName( clazz ),
                                    "hashCode",
                                    Type.getMethodDescriptor( Type.INT_TYPE,
                                                              new Type[0] ) );
            } else {
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    Type.getInternalName( clazz ),
                                    "hashCode",
                                    Type.getMethodDescriptor( Type.INT_TYPE,
                                                              new Type[0] ) );
            }
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               className,
                               HASHCACHE_FIELD_NAME,
                               Type.INT_TYPE.getDescriptor() );
            // }
            mv.visitLabel( l1 );

            // return this.__hashCache;
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               className,
                               HASHCACHE_FIELD_NAME,
                               Type.INT_TYPE.getDescriptor() );
            mv.visitInsn( Opcodes.IRETURN );
            Label l3 = new Label();
            mv.visitLabel( l3 );
            mv.visitLocalVariable( "this",
                                   "L" + className + ";",
                                   null,
                                   l0,
                                   l3,
                                   0 );

            mv.visitMaxs( 0,
                          0 );
            mv.visitEnd();
        }
    }

    /**
     * @param exceptionTypes
     * @return
     */
    private static String[] getExceptionArrayAsString(final Class[] exceptionTypes) {
        final String[] exceptions = new String[exceptionTypes.length];
        for ( int i = 0; i < exceptions.length; i++ ) {
            exceptions[i] = Type.getInternalName( exceptionTypes[i] );
        }
        return exceptions;
    }

    /**
     * Simple classloader
     * @author Michael Neale
     */
    static class ByteArrayClassLoader extends ClassLoader {
        public ByteArrayClassLoader(final ClassLoader parent) {
            super( parent );
        }

        public Class defineClass(final String name,
                                 final byte[] bytes,
                                 final ProtectionDomain PROTECTION_DOMAIN) {
            return defineClass( name,
                                bytes,
                                0,
                                bytes.length,
                                PROTECTION_DOMAIN );
        }
    }

}
