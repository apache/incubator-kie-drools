package org.drools.base;

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

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.asm.ClassWriter;
import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;
import org.drools.base.extractors.BaseBooleanClassFieldExtractor;
import org.drools.base.extractors.BaseByteClassFieldExtractor;
import org.drools.base.extractors.BaseCharClassFieldExtractor;
import org.drools.base.extractors.BaseDoubleClassFieldExtractor;
import org.drools.base.extractors.BaseFloatClassFieldExtractor;
import org.drools.base.extractors.BaseIntClassFieldExtractor;
import org.drools.base.extractors.BaseLongClassFieldExtractors;
import org.drools.base.extractors.BaseObjectClassFieldExtractor;
import org.drools.base.extractors.BaseShortClassFieldExtractor;
import org.drools.base.extractors.MVELClassFieldExtractor;
import org.drools.base.extractors.SelfReferenceClassFieldExtractor;
import org.drools.util.asm.ClassFieldInspector;

/**
 * This generates subclasses of BaseClassFieldExtractor to provide field extractors.
 * This should not be used directly, but via ClassFieldExtractor (which ensures that it is 
 * all nicely serializable).
 * 
 * @author Alexander Bagerman
 * @author Michael Neale
 */

public class ClassFieldExtractorFactory {

    private static final String           BASE_PACKAGE         = "org/drools/base";

    private static final String           SELF_REFERENCE_FIELD = "this";

    private static final ProtectionDomain PROTECTION_DOMAIN;

    private static final Map              inspectors           = new HashMap();
    
    private static ByteArrayClassLoader   byteArrayClassLoader;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {
            public Object run() {
                return ClassFieldExtractorFactory.class.getProtectionDomain();
            }
        } );
    }

    public static BaseClassFieldExtractor getClassFieldExtractor(final Class clazz,
                                                                 final String fieldName) {
        return getClassFieldExtractor( clazz,
                                       fieldName,
                                       null );
    }

    public static BaseClassFieldExtractor getClassFieldExtractor(final Class clazz,
                                                                 final String fieldName,
                                                                 final ClassLoader classLoader) {
        if ( byteArrayClassLoader == null ) {
            byteArrayClassLoader = new ByteArrayClassLoader( (classLoader != null) ? classLoader : Thread.currentThread().getContextClassLoader() );            
        }
        try {
            // if it is a self reference
            if ( SELF_REFERENCE_FIELD.equals( fieldName ) ) {
                // then just create an instance of the special class field extractor
                return new SelfReferenceClassFieldExtractor( clazz,
                                                             fieldName );
            } else if( fieldName.indexOf( '.' ) > -1 || fieldName.indexOf( '[' ) > -1 ) {
                // we need MVEL extractor for expressions
                return new MVELClassFieldExtractor( clazz,
                                                    fieldName );
            } else {
                // otherwise, bytecode generate a specific extractor
                ClassFieldInspector inspector = (ClassFieldInspector) inspectors.get( clazz );
                if ( inspector == null ) {
                    inspector = new ClassFieldInspector( clazz );
                    inspectors.put( clazz,
                                    inspector );
                }
                final Class fieldType = (Class) inspector.getFieldTypes().get( fieldName );
                final Method getterMethod = (Method) inspector.getGetterMethods().get( fieldName );
                final String className = ClassFieldExtractorFactory.BASE_PACKAGE + "/" + Type.getInternalName( clazz ) + "$" + getterMethod.getName();

                // generating byte array to create target class
                final byte[] bytes = dump( clazz,
                                           className,
                                           getterMethod,
                                           fieldType,
                                           clazz.isInterface() );
                // use bytes to get a class 

                final Class newClass = byteArrayClassLoader.defineClass( className.replace( '/',
                                                                                            '.' ),
                                                                         bytes,
                                                                         PROTECTION_DOMAIN );
                // instantiating target class
                final Integer index = (Integer) inspector.getFieldNames().get( fieldName );
                final ValueType valueType = ValueType.determineValueType( fieldType );
                final Object[] params = {index, fieldType, valueType};
                return (BaseClassFieldExtractor) newClass.getConstructors()[0].newInstance( params );
            }
        } catch ( final Exception e ) {
            System.out.println( e );
            throw new RuntimeDroolsException( e );
        }
    }

    private static byte[] dump(final Class originalClass,
                               final String className,
                               final Method getterMethod,
                               final Class fieldType,
                               final boolean isInterface) throws Exception {

        final ClassWriter cw = new ClassWriter( true );

        final Class superClass = getSuperClassFor( fieldType );
        buildClassHeader( superClass,
                          className,
                          cw );

        //        buildConstructor( superClass,
        //                          className,
        //                          cw );

        build3ArgConstructor( superClass,
                              className,
                              cw );

        buildGetMethod( originalClass,
                        className,
                        superClass,
                        getterMethod,
                        cw );

        cw.visitEnd();

        return cw.toByteArray();
    }

    /**
     * Builds the class header
     *  
     * @param clazz The class to build the extractor for
     * @param className The extractor class name
     * @param cw
     */
    protected static void buildClassHeader(final Class superClass,
                                           final String className,
                                           final ClassWriter cw) {
        cw.visit( Opcodes.V1_2,
                  Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                  className,
                  null,
                  Type.getInternalName( superClass ),
                  null );

        cw.visitSource( null,
                        null );
    }

    //    /**
    //     * Creates a constructor for the field extractor receiving
    //     * the class instance and field name
    //     * 
    //     * @param originalClassName
    //     * @param className
    //     * @param cw
    //     */
    //    private static void buildConstructor(final Class superClazz,
    //                                         final String className,
    //                                         final ClassWriter cw) {
    //        MethodVisitor mv;
    //        {
    //            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
    //                                 "<init>",
    //                                 Type.getMethodDescriptor( Type.VOID_TYPE,
    //                                                           new Type[]{Type.getType( Class.class ), Type.getType( String.class )} ),
    //                                 null,
    //                                 null );
    //            mv.visitCode();
    //            final Label l0 = new Label();
    //            mv.visitLabel( l0 );
    //            mv.visitVarInsn( Opcodes.ALOAD,
    //                             0 );
    //            mv.visitVarInsn( Opcodes.ALOAD,
    //                             1 );
    //            mv.visitVarInsn( Opcodes.ALOAD,
    //                             2 );
    //            mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
    //                                Type.getInternalName( superClazz ),
    //                                "<init>",
    //                                Type.getMethodDescriptor( Type.VOID_TYPE,
    //                                                          new Type[]{Type.getType( Class.class ), Type.getType( String.class )} ) );
    //            final Label l1 = new Label();
    //            mv.visitLabel( l1 );
    //            mv.visitInsn( Opcodes.RETURN );
    //            final Label l2 = new Label();
    //            mv.visitLabel( l2 );
    //            mv.visitLocalVariable( "this",
    //                                   "L" + className + ";",
    //                                   null,
    //                                   l0,
    //                                   l2,
    //                                   0 );
    //            mv.visitLocalVariable( "clazz",
    //                                   Type.getDescriptor( Class.class ),
    //                                   null,
    //                                   l0,
    //                                   l2,
    //                                   1 );
    //            mv.visitLocalVariable( "fieldName",
    //                                   Type.getDescriptor( String.class ),
    //                                   null,
    //                                   l0,
    //                                   l2,
    //                                   2 );
    //            mv.visitMaxs( 0,
    //                          0 );
    //            mv.visitEnd();
    //        }
    //    }

    /**
     * Creates a constructor for the field extractor receiving
     * the index, field type and value type
     * 
     * @param originalClassName
     * @param className
     * @param cw
     */
    private static void build3ArgConstructor(final Class superClazz,
                                             final String className,
                                             final ClassWriter cw) {
        MethodVisitor mv;
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "<init>",
                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                           new Type[]{Type.getType( int.class ), Type.getType( Class.class ), Type.getType( ValueType.class )} ),
                                 null,
                                 null );
            mv.visitCode();
            final Label l0 = new Label();
            mv.visitLabel( l0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ILOAD,
                             1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             2 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             3 );
            mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                Type.getInternalName( superClazz ),
                                "<init>",
                                Type.getMethodDescriptor( Type.VOID_TYPE,
                                                          new Type[]{Type.getType( int.class ), Type.getType( Class.class ), Type.getType( ValueType.class )} ) );
            final Label l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitInsn( Opcodes.RETURN );
            final Label l2 = new Label();
            mv.visitLabel( l2 );
            mv.visitLocalVariable( "this",
                                   "L" + className + ";",
                                   null,
                                   l0,
                                   l2,
                                   0 );
            mv.visitLocalVariable( "index",
                                   Type.getDescriptor( int.class ),
                                   null,
                                   l0,
                                   l2,
                                   1 );
            mv.visitLocalVariable( "fieldType",
                                   Type.getDescriptor( Class.class ),
                                   null,
                                   l0,
                                   l2,
                                   2 );
            mv.visitLocalVariable( "valueType",
                                   Type.getDescriptor( ValueType.class ),
                                   null,
                                   l0,
                                   l2,
                                   3 );
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
    protected static void buildGetMethod(final Class originalClass,
                                         final String className,
                                         final Class superClass,
                                         final Method getterMethod,
                                         final ClassWriter cw) {

        final Class fieldType = getterMethod.getReturnType();
        Method overridingMethod;
        try {
            overridingMethod = superClass.getMethod( getOverridingMethodName( fieldType ),
                                                     new Class[]{Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report back to JBoss Rules team.",
                                              e );
        }
        final MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                                 overridingMethod.getName(),
                                                 Type.getMethodDescriptor( overridingMethod ),
                                                 null,
                                                 null );

        mv.visitCode();

        final Label l0 = new Label();
        mv.visitLabel( l0 );
        mv.visitVarInsn( Opcodes.ALOAD,
                         1 );
        mv.visitTypeInsn( Opcodes.CHECKCAST,
                          Type.getInternalName( originalClass ) );

        if ( originalClass.isInterface() ) {
            mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                Type.getInternalName( originalClass ),
                                getterMethod.getName(),
                                Type.getMethodDescriptor( getterMethod ) );
        } else {
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( originalClass ),
                                getterMethod.getName(),
                                Type.getMethodDescriptor( getterMethod ) );
        }
        mv.visitInsn( Type.getType( fieldType ).getOpcode( Opcodes.IRETURN ) );
        final Label l1 = new Label();
        mv.visitLabel( l1 );
        mv.visitLocalVariable( "this",
                               "L" + className + ";",
                               null,
                               l0,
                               l1,
                               0 );
        mv.visitLocalVariable( "object",
                               Type.getDescriptor( Object.class ),
                               null,
                               l0,
                               l1,
                               1 );
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    private static String getOverridingMethodName(final Class fieldType) {
        String ret = null;
        if ( fieldType.isPrimitive() ) {
            if ( fieldType == char.class ) {
                ret = "getCharValue";
            } else if ( fieldType == byte.class ) {
                ret = "getByteValue";
            } else if ( fieldType == short.class ) {
                ret = "getShortValue";
            } else if ( fieldType == int.class ) {
                ret = "getIntValue";
            } else if ( fieldType == long.class ) {
                ret = "getLongValue";
            } else if ( fieldType == float.class ) {
                ret = "getFloatValue";
            } else if ( fieldType == double.class ) {
                ret = "getDoubleValue";
            } else if ( fieldType == boolean.class ) {
                ret = "getBooleanValue";
            }
        } else {
            ret = "getValue";
        }
        return ret;
    }

    /**
     * Returns the appropriate Base class field extractor class
     * for the given fieldType
     * 
     * @param fieldType
     * @return
     */
    private static Class getSuperClassFor(final Class fieldType) {
        Class ret = null;
        if ( fieldType.isPrimitive() ) {
            if ( fieldType == char.class ) {
                ret = BaseCharClassFieldExtractor.class;
            } else if ( fieldType == byte.class ) {
                ret = BaseByteClassFieldExtractor.class;
            } else if ( fieldType == short.class ) {
                ret = BaseShortClassFieldExtractor.class;
            } else if ( fieldType == int.class ) {
                ret = BaseIntClassFieldExtractor.class;
            } else if ( fieldType == long.class ) {
                ret = BaseLongClassFieldExtractors.class;
            } else if ( fieldType == float.class ) {
                ret = BaseFloatClassFieldExtractor.class;
            } else if ( fieldType == double.class ) {
                ret = BaseDoubleClassFieldExtractor.class;
            } else if ( fieldType == boolean.class ) {
                ret = BaseBooleanClassFieldExtractor.class;
            }
        } else {
            ret = BaseObjectClassFieldExtractor.class;
        }
        return ret;
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
                                 final ProtectionDomain domain) {
            return defineClass( name,
                                bytes,
                                0,
                                bytes.length,
                                domain );
        }
    }
}