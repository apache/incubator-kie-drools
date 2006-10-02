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
import java.util.Iterator;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.asm.ClassWriter;
import org.drools.asm.FieldVisitor;
import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;
import org.drools.util.asm.ClassFieldInspector;

/**
 * A factory for ShadowProxy classes
 */
public class ShadowProxyFactory {
    private static final String BASE_INTERFACE      = Type.getInternalName( ShadowProxy.class );

    private static final String FIELD_NAME_PREFIX   = "__";

    private static final String FIELD_SET_FLAG      = "IsSet";

    private static final String DELEGATE_FIELD_NAME = "__delegate";

    public static Class getProxy(final Class clazz) {
        try {
            final ClassFieldInspector inspector = new ClassFieldInspector( clazz, false );
            final String className = Type.getInternalName( clazz ) + "ShadowProxy";
            // generating byte array to create target class
            final byte[] bytes = dump( clazz,
                                       inspector,
                                       className,
                                       clazz.isInterface() );
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

    protected static byte[] dump(final Class clazz,
                                 final ClassFieldInspector inspector,
                                 final String className,
                                 final boolean isInterface) throws Exception {

        final ClassWriter cw = new ClassWriter( true );

        buildClassHeader( clazz,
                          className,
                          cw );

        buildField( DELEGATE_FIELD_NAME,
                    Type.getDescriptor( clazz ),
                    cw );
        Map fieldNames = inspector.getFieldNames();
        Map fieldTypes = inspector.getFieldTypes();
        Map fieldGetters = inspector.getGetterMethods();
        for ( Iterator it = fieldNames.keySet().iterator(); it.hasNext(); ) {
            String fieldName = (String) it.next();
            buildField( FIELD_NAME_PREFIX + fieldName,
                        Type.getDescriptor( (Class) fieldTypes.get( fieldName ) ),
                        cw );
            buildField( FIELD_NAME_PREFIX + fieldName + FIELD_SET_FLAG,
                        Type.BOOLEAN_TYPE.getDescriptor(),
                        cw );
            buildGetMethod( FIELD_NAME_PREFIX + fieldName,
                            (Class) fieldTypes.get( fieldName ),
                            FIELD_NAME_PREFIX + fieldName + FIELD_SET_FLAG,
                            (Method) fieldGetters.get( fieldName ),
                            className,
                            clazz,
                            cw );
        }

        buildConstructor( clazz,
                          className,
                          cw );

        buildResetProxyMethod( fieldTypes,
                               className,
                               cw );

        return cw.toByteArray();
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
        if( clazz.isInterface() ) {
            cw.visit( Opcodes.V1_2,
                      Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                      className,
                      null,
                      Type.getInternalName( Object.class ),
                      new String[]{BASE_INTERFACE, Type.getInternalName( clazz )} );
        } else {
            cw.visit( Opcodes.V1_2,
                      Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                      className,
                      null,
                      Type.getInternalName( clazz ),
                      new String[]{BASE_INTERFACE} );
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
    protected static void buildField(String name,
                                     String type,
                                     ClassWriter cw) {
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
            Label l0 = new Label();
            mv.visitLabel( l0 );
            mv.visitLineNumber( 41,
                                l0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            if(clazz.isInterface()) {
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
            Label l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitLineNumber( 42,
                                l1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               className,
                               DELEGATE_FIELD_NAME,
                               Type.getDescriptor( clazz ) );

            // return
            Label l2 = new Label();
            mv.visitLabel( l2 );
            mv.visitLineNumber( 43,
                                l2 );
            mv.visitInsn( Opcodes.RETURN );

            Label l3 = new Label();
            mv.visitLabel( l3 );
            mv.visitLocalVariable( "this",
                                   "L" + className + ";",
                                   null,
                                   l0,
                                   l3,
                                   0 );
            mv.visitLocalVariable( DELEGATE_FIELD_NAME,
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
    protected static void buildGetMethod(String fieldName,
                                         Class fieldType,
                                         String fieldFlag,
                                         Method method,
                                         String className,
                                         Class clazz,
                                         ClassWriter cw) {
        // method signature 
        Class[] exceptionTypes = method.getExceptionTypes();
        String[] exceptions = new String[exceptionTypes.length];
        for ( int i = 0; i < exceptions.length; i++ ) {
            exceptions[i] = Type.getInternalName( exceptionTypes[i] );
        }
        MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                           method.getName(),
                                           Type.getMethodDescriptor( method ),
                                           null,
                                           exceptions );
        mv.visitCode();

        // if ( ! _fieldIsSet ) {
        Label l0 = new Label();
        mv.visitLabel( l0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitFieldInsn( Opcodes.GETFIELD,
                           className,
                           fieldFlag,
                           Type.BOOLEAN_TYPE.getDescriptor() );
        Label l1 = new Label();
        mv.visitJumpInsn( Opcodes.IFNE,
                          l1 );

        //     __field = this.delegate.method();
        Label l2 = new Label();
        mv.visitLabel( l2 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitFieldInsn( Opcodes.GETFIELD,
                           className,
                           DELEGATE_FIELD_NAME,
                           Type.getDescriptor( clazz ) );
        if(clazz.isInterface()) {
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
        Label l3 = new Label();
        mv.visitLabel( l3 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         0 );
        mv.visitInsn( Opcodes.ICONST_1 );
        mv.visitFieldInsn( Opcodes.PUTFIELD,
                           className,
                           fieldFlag,
                           Type.BOOLEAN_TYPE.getDescriptor() );

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
        Label l4 = new Label();
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

    protected static void buildResetProxyMethod(Map fieldTypes,
                                                String className,
                                                ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                           "resetProxy",
                                           Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                     new Type[] { } ),
                                           null,
                                           null );
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel( l0 );
        for ( Iterator it = fieldTypes.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String fieldName = FIELD_NAME_PREFIX + entry.getKey();
            String fieldFlag = fieldName + FIELD_SET_FLAG;
            Class fieldType = (Class) entry.getValue();
            Label l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            if( fieldType.isPrimitive() ) {
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
            Label l2 = new Label();
            mv.visitLabel( l2 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitInsn( Opcodes.ICONST_0 );
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               className,
                               fieldFlag,
                               Type.BOOLEAN_TYPE.getDescriptor() );
        }
        Label l4 = new Label();
        mv.visitLabel( l4 );
        mv.visitInsn( Opcodes.RETURN );
        Label l5 = new Label();
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
