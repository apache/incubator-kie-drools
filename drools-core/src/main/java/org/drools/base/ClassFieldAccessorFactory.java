/**
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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassFieldAccessorCache.ByteArrayClassLoader;
import org.drools.base.ClassFieldAccessorCache.CacheEntry;
import org.drools.base.extractors.BaseBooleanClassFieldReader;
import org.drools.base.extractors.BaseBooleanClassFieldWriter;
import org.drools.base.extractors.BaseByteClassFieldReader;
import org.drools.base.extractors.BaseByteClassFieldWriter;
import org.drools.base.extractors.BaseCharClassFieldReader;
import org.drools.base.extractors.BaseCharClassFieldWriter;
import org.drools.base.extractors.BaseDoubleClassFieldReader;
import org.drools.base.extractors.BaseDoubleClassFieldWriter;
import org.drools.base.extractors.BaseFloatClassFieldReader;
import org.drools.base.extractors.BaseFloatClassFieldWriter;
import org.drools.base.extractors.BaseIntClassFieldReader;
import org.drools.base.extractors.BaseIntClassFieldWriter;
import org.drools.base.extractors.BaseLongClassFieldReader;
import org.drools.base.extractors.BaseLongClassFieldWriter;
import org.drools.base.extractors.BaseObjectClassFieldReader;
import org.drools.base.extractors.BaseObjectClassFieldWriter;
import org.drools.base.extractors.BaseShortClassFieldReader;
import org.drools.base.extractors.BaseShortClassFieldWriter;
import org.drools.base.extractors.MVELClassFieldReader;
import org.drools.base.extractors.SelfReferenceClassFieldReader;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.asm.ClassFieldInspector;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;
import org.mvel2.asm.Type;

/**
 * This generates subclasses of BaseClassFieldExtractor to provide field extractors.
 * This should not be used directly, but via ClassFieldExtractor (which ensures that it is 
 * all nicely serializable).
 * 
 * @author Alexander Bagerman
 * @author Michael Neale
 * @author Edson Tirelli
 */

public class ClassFieldAccessorFactory {

    private static final String                        BASE_PACKAGE         = "org/drools/base";

    private static final String                        SELF_REFERENCE_FIELD = "this";

    private static final ProtectionDomain              PROTECTION_DOMAIN;
//
//    private final Map<Class< ? >, ClassFieldInspector> inspectors           = new HashMap<Class< ? >, ClassFieldInspector>();
//
//    private ByteArrayClassLoader                       byteArrayClassLoader;

    static {
        PROTECTION_DOMAIN = AccessController.doPrivileged( new PrivilegedAction<ProtectionDomain>() {
            public ProtectionDomain run() {
                return ClassFieldAccessorFactory.class.getProtectionDomain();
            }
        } );
    }
    
    private static ClassFieldAccessorFactory instance = new ClassFieldAccessorFactory();
    
    public static ClassFieldAccessorFactory getInstance() {
        return instance;
    }

    public BaseClassFieldReader getClassFieldReader(final Class< ? > clazz,
                                                    final String fieldName,
                                                    CacheEntry cache) {
        ByteArrayClassLoader byteArrayClassLoader = cache.getByteArrayClassLoader();
        Map<Class< ? >, ClassFieldInspector> inspectors = cache.getInspectors();
//        if ( byteArrayClassLoader == null || byteArrayClassLoader.getParent() != classLoader ) {
//            if ( classLoader == null ) {
//                throw new RuntimeDroolsException( "ClassFieldAccessorFactory cannot have a null parent ClassLoader" );
//            }
//            byteArrayClassLoader = new ByteArrayClassLoader( classLoader );
//        }
        try {
            // if it is a self reference
            if ( SELF_REFERENCE_FIELD.equals( fieldName ) ) {
                // then just create an instance of the special class field extractor
                return new SelfReferenceClassFieldReader( clazz,
                                                          fieldName );
            } else if ( fieldName.indexOf( '.' ) > -1 || fieldName.indexOf( '[' ) > -1 ) {
                // we need MVEL extractor for expressions
                return new MVELClassFieldReader( clazz,
                                                 fieldName,
                                                 cache );
            } else {
                // otherwise, bytecode generate a specific extractor
                ClassFieldInspector inspector = inspectors.get( clazz );
                if ( inspector == null ) {
                    inspector = new ClassFieldInspector( clazz );
                    inspectors.put( clazz,
                                    inspector );
                }
                final Class< ? > fieldType = (Class< ? >) inspector.getFieldTypes().get( fieldName );
                final Method getterMethod = (Method) inspector.getGetterMethods().get( fieldName );
                if ( fieldType != null && getterMethod != null ) {
                    final String className = ClassFieldAccessorFactory.BASE_PACKAGE + "/" + Type.getInternalName( clazz ) + Math.abs( System.identityHashCode( clazz ) ) + "$" + getterMethod.getName();

                    // generating byte array to create target class
                    final byte[] bytes = dumpReader( clazz,
                                                     className,
                                                     getterMethod,
                                                     fieldType,
                                                     clazz.isInterface() );
                    // use bytes to get a class 

                    final Class< ? > newClass = byteArrayClassLoader.defineClass( className.replace( '/',
                                                                                                     '.' ),
                                                                                  bytes,
                                                                                  PROTECTION_DOMAIN );
                    // instantiating target class
                    final Integer index = (Integer) inspector.getFieldNames().get( fieldName );
                    final ValueType valueType = ValueType.determineValueType( fieldType );
                    final Object[] params = {index, fieldType, valueType};
                    return (BaseClassFieldReader) newClass.getConstructors()[0].newInstance( params );
                } else {
                    throw new RuntimeDroolsException( "Field/method '" + fieldName + "' not found for class '" + clazz.getName() + "'\n" );
                }
            }
        } catch ( final RuntimeDroolsException e ) {
            throw e;
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public BaseClassFieldWriter getClassFieldWriter(final Class< ? > clazz,
                                                    final String fieldName,
                                                    final CacheEntry cache) {
        ByteArrayClassLoader byteArrayClassLoader = cache.getByteArrayClassLoader();
        Map<Class< ? >, ClassFieldInspector> inspectors = cache.getInspectors();
        
        try {
            // otherwise, bytecode generate a specific extractor
            ClassFieldInspector inspector = inspectors.get( clazz );
            if ( inspector == null ) {
                inspector = new ClassFieldInspector( clazz );
                inspectors.put( clazz,
                                inspector );
            }
            final Method setterMethod = (Method) inspector.getSetterMethods().get( fieldName );
            if ( setterMethod != null ) {
                final Class< ? > fieldType = setterMethod.getParameterTypes()[0];
                final String className = ClassFieldAccessorFactory.BASE_PACKAGE + "/" + Type.getInternalName( clazz ) + Math.abs( System.identityHashCode( clazz ) ) + "$" + setterMethod.getName();

                // generating byte array to create target class
                final byte[] bytes = dumpWriter( clazz,
                                                 className,
                                                 setterMethod,
                                                 fieldType,
                                                 clazz.isInterface() );
                // use bytes to get a class 

                final Class< ? > newClass = byteArrayClassLoader.defineClass( className.replace( '/',
                                                                                                 '.' ),
                                                                              bytes,
                                                                              PROTECTION_DOMAIN );
                // instantiating target class
                final Integer index = (Integer) inspector.getFieldNames().get( fieldName );
                final ValueType valueType = ValueType.determineValueType( fieldType );
                final Object[] params = {index, fieldType, valueType};
                return (BaseClassFieldWriter) newClass.getConstructors()[0].newInstance( params );
            } else {
                throw new RuntimeDroolsException( "Field/method '" + fieldName + "' not found for class '" + clazz.getName() + "'" );
            }
        } catch ( final RuntimeDroolsException e ) {
            throw e;
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    private byte[] dumpReader(final Class< ? > originalClass,
                              final String className,
                              final Method getterMethod,
                              final Class< ? > fieldType,
                              final boolean isInterface) throws Exception {

        final ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS );

        final Class< ? > superClass = getReaderSuperClassFor( fieldType );
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

    private byte[] dumpWriter(final Class< ? > originalClass,
                              final String className,
                              final Method getterMethod,
                              final Class< ? > fieldType,
                              final boolean isInterface) throws Exception {

        final ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS );

        final Class< ? > superClass = getWriterSuperClassFor( fieldType );
        buildClassHeader( superClass,
                          className,
                          cw );

        build3ArgConstructor( superClass,
                              className,
                              cw );

        buildSetMethod( originalClass,
                        className,
                        superClass,
                        getterMethod,
                        fieldType,
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
    protected void buildClassHeader(final Class< ? > superClass,
                                    final String className,
                                    final ClassWriter cw) {
        cw.visit( Opcodes.V1_5,
                  Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                  className,
                  null,
                  Type.getInternalName( superClass ),
                  null );

        cw.visitSource( null,
                        null );
    }

    /**
     * Creates a constructor for the field extractor receiving
     * the index, field type and value type
     * 
     * @param originalClassName
     * @param className
     * @param cw
     */
    private void build3ArgConstructor(final Class< ? > superClazz,
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
    protected void buildGetMethod(final Class< ? > originalClass,
                                  final String className,
                                  final Class< ? > superClass,
                                  final Method getterMethod,
                                  final ClassWriter cw) {

        final Class< ? > fieldType = getterMethod.getReturnType();
        Method overridingMethod;
        try {
            overridingMethod = superClass.getMethod( getOverridingGetMethodName( fieldType ),
                                                     new Class[]{InternalWorkingMemory.class, Object.class} );
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
                         2 );
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
        mv.visitLocalVariable( "workingMemory",
                               Type.getDescriptor( InternalWorkingMemory.class ),
                               null,
                               l0,
                               l1,
                               1 );
        mv.visitLocalVariable( "object",
                               Type.getDescriptor( Object.class ),
                               null,
                               l0,
                               l1,
                               2 );
        mv.visitMaxs( 0,
                      0 );
        mv.visitEnd();
    }

    /**
     * Creates the set method for the given field definition
     *
     * @param cw
     * @param classDef
     * @param fieldDef
     */
    protected void buildSetMethod(final Class< ? > originalClass,
                                  final String className,
                                  final Class< ? > superClass,
                                  final Method setterMethod,
                                  final Class< ? > fieldType,
                                  final ClassWriter cw) {
        MethodVisitor mv;
        // set method
        {
            Method overridingMethod;
            try {
                overridingMethod = superClass.getMethod( getOverridingSetMethodName( fieldType ),
                                                         new Class[]{Object.class, fieldType.isPrimitive() ? fieldType : Object.class} );
            } catch ( final Exception e ) {
                throw new RuntimeDroolsException( "This is a bug. Please report back to JBoss Rules team.",
                                                  e );
            }

            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
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

            mv.visitVarInsn( Type.getType( fieldType ).getOpcode( Opcodes.ILOAD ),
                             2 );

            if ( !fieldType.isPrimitive() ) {
                mv.visitTypeInsn( Opcodes.CHECKCAST,
                                  Type.getInternalName( fieldType ) );
            }

            if ( originalClass.isInterface() ) {
                mv.visitMethodInsn( Opcodes.INVOKEINTERFACE,
                                    Type.getInternalName( originalClass ),
                                    setterMethod.getName(),
                                    Type.getMethodDescriptor( setterMethod ) );
            } else {
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    Type.getInternalName( originalClass ),
                                    setterMethod.getName(),
                                    Type.getMethodDescriptor( setterMethod ) );
            }

            mv.visitInsn( Opcodes.RETURN );

            final Label l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitLocalVariable( "this",
                                   "L" + className + ";",
                                   null,
                                   l0,
                                   l1,
                                   0 );
            mv.visitLocalVariable( "bean",
                                   Type.getDescriptor( Object.class ),
                                   null,
                                   l0,
                                   l1,
                                   1 );
            mv.visitLocalVariable( "value",
                                   Type.getDescriptor( fieldType ),
                                   null,
                                   l0,
                                   l1,
                                   2 );
            mv.visitMaxs( 0,
                          0 );
            mv.visitEnd();

        }
    }

    private String getOverridingGetMethodName(final Class< ? > fieldType) {
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

    private String getOverridingSetMethodName(final Class< ? > fieldType) {
        String ret = null;
        if ( fieldType.isPrimitive() ) {
            if ( fieldType == char.class ) {
                ret = "setCharValue";
            } else if ( fieldType == byte.class ) {
                ret = "setByteValue";
            } else if ( fieldType == short.class ) {
                ret = "setShortValue";
            } else if ( fieldType == int.class ) {
                ret = "setIntValue";
            } else if ( fieldType == long.class ) {
                ret = "setLongValue";
            } else if ( fieldType == float.class ) {
                ret = "setFloatValue";
            } else if ( fieldType == double.class ) {
                ret = "setDoubleValue";
            } else if ( fieldType == boolean.class ) {
                ret = "setBooleanValue";
            }
        } else {
            ret = "setValue";
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
    private Class< ? > getReaderSuperClassFor(final Class< ? > fieldType) {
        Class< ? > ret = null;
        if ( fieldType.isPrimitive() ) {
            if ( fieldType == char.class ) {
                ret = BaseCharClassFieldReader.class;
            } else if ( fieldType == byte.class ) {
                ret = BaseByteClassFieldReader.class;
            } else if ( fieldType == short.class ) {
                ret = BaseShortClassFieldReader.class;
            } else if ( fieldType == int.class ) {
                ret = BaseIntClassFieldReader.class;
            } else if ( fieldType == long.class ) {
                ret = BaseLongClassFieldReader.class;
            } else if ( fieldType == float.class ) {
                ret = BaseFloatClassFieldReader.class;
            } else if ( fieldType == double.class ) {
                ret = BaseDoubleClassFieldReader.class;
            } else if ( fieldType == boolean.class ) {
                ret = BaseBooleanClassFieldReader.class;
            }
        } else {
            ret = BaseObjectClassFieldReader.class;
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
    private Class< ? > getWriterSuperClassFor(final Class< ? > fieldType) {
        Class< ? > ret = null;
        if ( fieldType.isPrimitive() ) {
            if ( fieldType == char.class ) {
                ret = BaseCharClassFieldWriter.class;
            } else if ( fieldType == byte.class ) {
                ret = BaseByteClassFieldWriter.class;
            } else if ( fieldType == short.class ) {
                ret = BaseShortClassFieldWriter.class;
            } else if ( fieldType == int.class ) {
                ret = BaseIntClassFieldWriter.class;
            } else if ( fieldType == long.class ) {
                ret = BaseLongClassFieldWriter.class;
            } else if ( fieldType == float.class ) {
                ret = BaseFloatClassFieldWriter.class;
            } else if ( fieldType == double.class ) {
                ret = BaseDoubleClassFieldWriter.class;
            } else if ( fieldType == boolean.class ) {
                ret = BaseBooleanClassFieldWriter.class;
            }
        } else {
            ret = BaseObjectClassFieldWriter.class;
        }
        return ret;
    }

}
