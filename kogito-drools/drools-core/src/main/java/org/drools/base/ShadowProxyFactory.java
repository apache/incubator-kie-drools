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
import java.util.ArrayList;
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

/**
 * A factory for ShadowProxy classes
 */
public class ShadowProxyFactory {
    private static final String UPDATE_PROXY        = "updateProxy";

    private static final String BASE_INTERFACE      = Type.getInternalName( ShadowProxy.class );

    //private static final String FIELD_NAME_PREFIX   = "__";

    private static final String FIELD_SET_FLAG      = "IsSet";

    private static final String DELEGATE_FIELD_NAME = "delegate";

    public static Class getProxy(final Class clazz) {
        try {
            if(( clazz.getModifiers() & Modifier.FINAL ) != 0 ) {
                return null;
            }

            String className = getInternalProxyClassNameForClass( clazz );
            // generating byte array to create target class
            final byte[] bytes = dump( clazz,
                                       className );
            // use bytes to get a class 
            final ByteArrayClassLoader classLoader = new ByteArrayClassLoader( Thread.currentThread().getContextClassLoader() );
            final Class newClass = classLoader.defineClass( className.replace( '/',
                                                                               '.' ),
                                                            bytes );
            return newClass;
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public static byte[] getProxyBytes(final Class clazz) {
        try {
            if(( clazz.getModifiers() & Modifier.FINAL ) != 0 ) {
                return null;
            }

            String className = getInternalProxyClassNameForClass( clazz );
            // generating byte array to create target class
            final byte[] bytes = dump( clazz,
                                       className );
            return bytes;
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    /**
     * @param clazz
     * @return
     */
    public static String getInternalProxyClassNameForClass(final Class clazz) {
        String className = null;
        if ( clazz.getPackage().getName().startsWith( "java." ) || clazz.getPackage().getName().startsWith( "javax." ) ) {
            className = "org/drools/shadow/" + Type.getInternalName( clazz ) + "ShadowProxy";
        } else {
            className = Type.getInternalName( clazz ) + "ShadowProxy";
        }
        return className;
    }

    public static String getProxyClassNameForClass(final Class clazz) {
        String className = null;
        if ( clazz.getPackage().getName().startsWith( "java." ) || clazz.getPackage().getName().startsWith( "javax." ) ) {
            className = "org.drools.shadow." + clazz.getName() + "ShadowProxy";
        } else {
            className =  clazz.getName() + "ShadowProxy";
        }
        return className;
    }

    protected static byte[] dump(final Class clazz,
                                 final String className) throws Exception {

        final ClassWriter cw = new ClassWriter( true );

        buildClassHeader( clazz,
                          className,
                          cw );

        buildField( ShadowProxyFactory.DELEGATE_FIELD_NAME,
                    Type.getDescriptor( clazz ),
                    cw );

        Method getShadowed = ShadowProxy.class.getDeclaredMethod( "getShadowedObject",
                                                                  new Class[]{} );
        buildSimpleGetMethod( ShadowProxyFactory.DELEGATE_FIELD_NAME,
                              clazz,
                              getShadowed,
                              className,
                              clazz,
                              cw );

        final Map fieldTypes = new HashMap();
        final Method[] methods = getMethods( clazz );
        for ( int i = 0; i < methods.length; i++ ) {
            if ( (!Modifier.isFinal( methods[i].getModifiers() )) && Modifier.isPublic( methods[i].getModifiers() ) ) {
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

        buildConstructor( clazz,
                          className,
                          cw );

        buildUpdateProxyMethod( fieldTypes,
                                className,
                                cw );

        buildEquals( cw,
                     className,
                     clazz,
                     fieldTypes );

        buildHashCode( cw,
                       className,
                       clazz,
                       fieldTypes );

        return cw.toByteArray();
    }

    /**
     * Filter out any method we are not interested in
     * @param clazz
     * @return
     */
    private static Method[] getMethods(final Class clazz) {
        // to help filtering process, we will create a map of maps:
        // Map< String methodName, Map< Class[] parameterTypes, Method method > >
        Map map = new HashMap();
        List helperList = new ArrayList();
        final Method[] methods = clazz.getMethods();
        for( int i = 0; i < methods.length; i++ ) {
            Method previous = null; 
            Map signatures = (Map) map.get( methods[i].getName() );
            ParametersWrapper key = new ParametersWrapper( methods[i].getParameterTypes() );
            if( signatures != null ) {
                previous = (Method) signatures.get( key );
            }
            // if no previous method with the same name and parameter types is found
            // or if the previous method's return type is a super class of the 
            // current method's return type, add current to the map
            // overriding previous if it exists
            if( ( previous == null ) ||
                ( previous.getReturnType().isAssignableFrom( methods[i].getReturnType() ) ) ) {
                if( signatures == null ) {
                    signatures = new HashMap();
                    map.put( methods[i].getName(), signatures );
                }
                if( signatures.put( key, methods[i] ) != null ) {
                    helperList.remove( previous );
                }
                helperList.add( methods[i] );
            }
        }
        return (Method[]) helperList.toArray( new Method[helperList.size()] );
    }
    
    private static class ParametersWrapper {
        private Class[] parameters;
        public ParametersWrapper( Class[] parameters ) {
            this.parameters = parameters;
        }
        
        public int hashCode() {
            return this.parameters.length;
        }
        
        public boolean equals( Object o ) {
            if( !( o instanceof ParametersWrapper ) ) {
                return false;
            }
            ParametersWrapper other = (ParametersWrapper) o;
            
            if( this.parameters.length != other.parameters.length ) {
                return false;
            }
            
            for( int i = 0; i < this.parameters.length; i++ ) {
                if( ! this.parameters[i].equals( other.parameters[i] )) {
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
        String[] exceptions = getExceptionArrayAsString( exceptionTypes );
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
            final Class fieldType = (Class) ((Method) entry.getValue()).getReturnType();
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
        for ( int i = 0; i < parameters.length; i++ ) {
            mv.visitVarInsn( Type.getType( parameters[i] ).getOpcode( Opcodes.ILOAD ),
                             i + 1 );
        }
        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                            Type.getInternalName( clazz ),
                            method.getName(),
                            Type.getMethodDescriptor( method ) );
        mv.visitInsn( Type.getType( method.getReturnType() ).getOpcode( Opcodes.IRETURN ) );
        final Label l1 = new Label();
        mv.visitLabel( l1 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l1,
                               0 );
        for ( int i = 0; i < parameters.length; i++ ) {
            mv.visitLocalVariable( "arg" + i,
                                   Type.getDescriptor( parameters[i] ),
                                   null,
                                   l0,
                                   l1,
                                   i + 1 );
        }
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    protected static void buildEquals(ClassWriter cw,
                                    String className,
                                    final Class clazz,
                                    Map fieldTypes) {
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
            Label l0 = new Label();
            mv.visitLabel( l0 );

            // if ( this == object )
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            Label l1 = new Label();
            mv.visitJumpInsn( Opcodes.IF_ACMPNE,
                              l1 );
            //      return true;
            mv.visitInsn( Opcodes.ICONST_1 );
            mv.visitInsn( Opcodes.IRETURN );

            // if (( object == null ) || ( ! ( object instanceof <class> ) ) ) 
            mv.visitLabel( l1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            Label l3 = new Label();
            mv.visitJumpInsn( Opcodes.IFNULL,
                              l3 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.INSTANCEOF,
                              Type.getInternalName( clazz ) );
            Label l4 = new Label();
            mv.visitJumpInsn( Opcodes.IFNE,
                              l4 );

            //       return false;
            mv.visitLabel( l3 );
            mv.visitInsn( Opcodes.ICONST_0 );
            mv.visitInsn( Opcodes.IRETURN );

            // <class> other = (<class>) object;
            mv.visitLabel( l4 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.CHECKCAST,
                              Type.getInternalName( clazz ) );
            mv.visitVarInsn( Opcodes.ASTORE,
                             2 );

            // for each field:
            int count = 0;
            for ( Iterator it = fieldTypes.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String fieldName = (String) entry.getKey();
                Method method = (Method) entry.getValue();
                Class fieldType = method.getReturnType();
                String fieldFlag = fieldName + ShadowProxyFactory.FIELD_SET_FLAG;
                count++;
                Label goNext = new Label();

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

                    //                    mv.visitFieldInsn( Opcodes.GETFIELD,
                    //                                       className,
                    //                                       name,
                    //                                       Type.getDescriptor( fieldType ) );
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
                    Label secondIfPart = new Label();
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
                    Label returnFalse = new Label();
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
            Label lastLabel = new Label();
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

    /**
     *  Sample of generated code for all primitive + object types
     *  
     *  public int hashCode() {
     *       final int PRIME = 31;
     *       int result = 1;
     *       result = PRIME * result + (booleanAttr ? 1231 : 1237);
     *       result = PRIME * result + charAttr;
     *       long temp = Double.doubleToLongBits( doubleAttr );
     *       result = PRIME * result + (int) (temp ^ (temp >>> 32));
     *       result = PRIME * result + Float.floatToIntBits( floatAttr );
     *       result = PRIME * result + intAttr;
     *       result = PRIME * result + ((listAttr == null) ? 0 : listAttr.hashCode());
     *       result = PRIME * result + (int) (longAttr ^ (longAttr >>> 32));
     *       result = PRIME * result + shortAttr;
     *       return result;
     *   }
     * 
     * @param cw
     * @param className
     * @param clazz
     * @param fieldTypes
     */
    protected static void buildHashCode(ClassWriter cw,
                                      String className,
                                      final Class clazz,
                                      Map fieldTypes) {
        MethodVisitor mv;
        boolean hasDoubleAttr = false;
        // Building hashcode method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "hashCode",
                                 Type.getMethodDescriptor( Type.INT_TYPE,
                                                           new Type[]{} ),
                                 null,
                                 null );
            mv.visitCode();

            // final int PRIME = 31;
            Label l0 = new Label();
            mv.visitLabel( l0 );
            mv.visitIntInsn( Opcodes.BIPUSH,
                             31 );
            mv.visitVarInsn( Opcodes.ISTORE,
                             1 );

            // int result = 1;
            Label l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitInsn( Opcodes.ICONST_1 );
            mv.visitVarInsn( Opcodes.ISTORE,
                             2 );

            // for each field:
            int count = 0;
            for ( Iterator it = fieldTypes.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String fieldName = (String) entry.getKey();
                Method method = (Method) entry.getValue();
                Class fieldType = method.getReturnType();
                String fieldFlag = fieldName + ShadowProxyFactory.FIELD_SET_FLAG;
                count++;
                Label goNext = new Label();

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
                    // result = PRIME * result + <att hashcode>
                    Label l2 = new Label();
                    if ( fieldType == Double.TYPE ) {
                        hasDoubleAttr = true;
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           className,
                                           fieldName,
                                           Type.getDescriptor( fieldType ) );
                        mv.visitMethodInsn( Opcodes.INVOKESTATIC,
                                            Type.getInternalName( Double.class ),
                                            "doubleToLongBits",
                                            "(D)J" );
                        mv.visitVarInsn( Opcodes.LSTORE,
                                         3 );
                    }

                    mv.visitLabel( l2 );
                    mv.visitIntInsn( Opcodes.BIPUSH,
                                     31 );
                    mv.visitVarInsn( Opcodes.ILOAD,
                                     2 );
                    mv.visitInsn( Opcodes.IMUL );

                    if ( fieldType != Double.TYPE ) {
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           className,
                                           fieldName,
                                           Type.getDescriptor( fieldType ) );
                    }

                    if ( fieldType == Boolean.TYPE ) {
                        // att_hashcode ::= ( boolean_attribute ) ? 1231 : 1237;
                        Label z1 = new Label();
                        mv.visitJumpInsn( Opcodes.IFEQ,
                                          z1 );
                        mv.visitIntInsn( Opcodes.SIPUSH,
                                         1231 );
                        Label z2 = new Label();
                        mv.visitJumpInsn( Opcodes.GOTO,
                                          z2 );
                        mv.visitLabel( z1 );
                        mv.visitIntInsn( Opcodes.SIPUSH,
                                         1237 );
                        mv.visitLabel( z2 );
                    } else if ( fieldType == Double.TYPE ) {
                        // long temp = Double.doubleToLongBits( doubleAttr );
                        // att_hashcode ::= (int) (temp ^ ( temp >>> 32 ) );
                        Label d1 = new Label();
                        mv.visitLabel( d1 );
                        mv.visitVarInsn( Opcodes.LLOAD,
                                         3 );
                        mv.visitVarInsn( Opcodes.LLOAD,
                                         3 );
                        mv.visitIntInsn( Opcodes.BIPUSH,
                                         32 );
                        mv.visitInsn( Opcodes.LUSHR );
                        mv.visitInsn( Opcodes.LXOR );
                        mv.visitInsn( Opcodes.L2I );
                    } else if ( fieldType == Float.TYPE ) {
                        // att_hashcode ::= Float.floatToIntBits( floatAttr );
                        mv.visitMethodInsn( Opcodes.INVOKESTATIC,
                                            Type.getInternalName( Float.class ),
                                            "floatToIntBits",
                                            "(F)I" );

                    } else if ( fieldType == Long.TYPE ) {
                        // att_hashcode ::= (int) (lontattr ^( longattr >>> 32 ) );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           className,
                                           fieldName,
                                           Type.getDescriptor( fieldType ) );
                        mv.visitIntInsn( Opcodes.BIPUSH,
                                         32 );
                        mv.visitInsn( Opcodes.LUSHR );
                        mv.visitInsn( Opcodes.LXOR );
                        mv.visitInsn( Opcodes.L2I );

                    }
                    mv.visitInsn( Opcodes.IADD );
                    mv.visitVarInsn( Opcodes.ISTORE,
                                     2 );

                } else {
                    // for non primitive types
                    // result = PRIME * result + <att hashcode>
                    Label l2 = new Label();
                    mv.visitLabel( l2 );
                    mv.visitIntInsn( Opcodes.BIPUSH,
                                     31 );
                    mv.visitVarInsn( Opcodes.ILOAD,
                                     2 );
                    mv.visitInsn( Opcodes.IMUL );

                    mv.visitVarInsn( Opcodes.ALOAD,
                                     0 );
                    mv.visitFieldInsn( Opcodes.GETFIELD,
                                       className,
                                       fieldName,
                                       Type.getDescriptor( fieldType ) );

                    Label np1 = new Label();
                    mv.visitJumpInsn( Opcodes.IFNONNULL,
                                      np1 );
                    mv.visitInsn( Opcodes.ICONST_0 );
                    Label np2 = new Label();
                    mv.visitJumpInsn( Opcodes.GOTO,
                                      np2 );
                    mv.visitLabel( np1 );
                    mv.visitVarInsn( Opcodes.ALOAD,
                                     0 );
                    mv.visitFieldInsn( Opcodes.GETFIELD,
                                       className,
                                       fieldName,
                                       Type.getDescriptor( fieldType ) );
                    if ( fieldType.isInterface() ) {
                        mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                            Type.getInternalName( fieldType ),
                                            "hashCode",
                                            "()I" );
                    } else {
                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                            Type.getInternalName( fieldType ),
                                            "hashCode",
                                            "()I" );
                    }
                    mv.visitLabel( np2 );
                    mv.visitInsn( Opcodes.IADD );
                    mv.visitVarInsn( Opcodes.ISTORE,
                                     2 );
                }
                mv.visitLabel( goNext );
            }
            mv.visitVarInsn( Opcodes.ILOAD,
                             2 );
            mv.visitInsn( Opcodes.IRETURN );
            Label lastLabel = new Label();
            mv.visitLabel( lastLabel );

            mv.visitLocalVariable( "this",
                                   "L" + className + ";",
                                   null,
                                   l0,
                                   lastLabel,
                                   0 );
            mv.visitLocalVariable( "PRIME",
                                   Type.INT_TYPE.getDescriptor(),
                                   null,
                                   l0,
                                   lastLabel,
                                   1 );
            mv.visitLocalVariable( "result",
                                   Type.INT_TYPE.getDescriptor(),
                                   null,
                                   l1,
                                   lastLabel,
                                   2 );
            if ( hasDoubleAttr ) {
                mv.visitLocalVariable( "temp",
                                       Type.LONG_TYPE.getDescriptor(),
                                       null,
                                       l1,
                                       lastLabel,
                                       3 );
            }
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
                                 final byte[] bytes) {
            return defineClass( name,
                                bytes,
                                0,
                                bytes.length );
        }
    }

}
