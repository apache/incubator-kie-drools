package org.drools.factmodel;


import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.drools.asm.ClassWriter;
import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;

/**
 * <p><b>Title:</b> FieldAccessorBuilder</p>
 * <p><b>Description:</b> A builder for FieldAccessor instances</p>
 * <p><b>Copyright:</b> Copyright (c) 2004-2006</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: FieldAccessorBuilder.java 241 2006-05-11 19:48:25Z framos $
 */
public class FieldAccessorBuilder {
    private Map typeMap = null;

    public FieldAccessorBuilder() {
        typeMap = new HashMap();
        typeMap.put( Boolean.TYPE,
                     Boolean.class );
        typeMap.put( Byte.TYPE,
                     Byte.class );
        typeMap.put( Character.TYPE,
                     Character.class );
        typeMap.put( Double.TYPE,
                     Double.class );
        typeMap.put( Float.TYPE,
                     Float.class );
        typeMap.put( Integer.TYPE,
                     Integer.class );
        typeMap.put( Long.TYPE,
                     Long.class );
        typeMap.put( Short.TYPE,
                     Short.class );
        typeMap.put( Void.TYPE,
                     Void.class );
    }

    public Class buildAndLoadFieldAccessor(Class clazz,
                                           String fieldName) throws SecurityException,
                                                            IllegalArgumentException,
                                                            IOException,
                                                            IntrospectionException,
                                                            ClassNotFoundException,
                                                            NoSuchMethodException,
                                                            IllegalAccessException,
                                                            InvocationTargetException,
                                                            NoSuchFieldException {

        ClassDefinition accClass = new ClassDefinition( clazz.getName() + fieldName.substring( 0,
                                                                                               1 ).toUpperCase() + fieldName.substring( 1 ) + "FA" );
        try {
            return Class.forName( accClass.getClassName() );
        } catch ( ClassNotFoundException e ) {
            byte[] serializedClazz = this.buildFieldAccessor( clazz,
                                                              fieldName );
            Class newClazz = this.loadClass( accClass.getClassName(),
                                             serializedClazz );
            return newClazz;
        }
    }

    /**
     * Dinamically builds, defines and loads a field accessor class for the given field
     *
     * @param class the class to build the field accessor for
     * @param fieldName the name of the field to build the field accessor for
     *
     * @return the Class instance for the given class definition
     *
     * @throws IOException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    public byte[] buildFieldAccessor(Class clazz,
                                     String fieldName) throws IOException,
                                                      IntrospectionException,
                                                      SecurityException,
                                                      IllegalArgumentException,
                                                      ClassNotFoundException,
                                                      NoSuchMethodException,
                                                      IllegalAccessException,
                                                      InvocationTargetException,
                                                      NoSuchFieldException {

        ClassDefinition accClass = new ClassDefinition( clazz.getName() + fieldName.substring( 0,
                                                                                               1 ).toUpperCase() + fieldName.substring( 1 ) + "FA" );
        accClass.setInterfaces( new String[]{FieldAccessor.class.getName()} );
        accClass.setSuperClass( Object.class.getName() );

        PropertyDescriptor field = getPropertyDescriptor( clazz,
                                                          fieldName );

        ClassWriter cw = new ClassWriter( true );

        this.buildClassHeader( cw,
                               accClass );

        // Building default constructor
        this.buildDefaultConstructor( cw );

        // Building methods
        this.buildGetMethod( cw,
                             accClass,
                             clazz,
                             field );
        this.buildSetMethod( cw,
                             accClass,
                             clazz,
                             field );

        cw.visitEnd();

        return cw.toByteArray();
    }

    /**
     * @param clazz
     * @param fieldName
     * @param field
     * @return
     * @throws IntrospectionException
     * @throws NoSuchFieldException
     */
    private PropertyDescriptor getPropertyDescriptor(Class clazz,
                                                     String fieldName) throws IntrospectionException,
                                                                      NoSuchFieldException {
        PropertyDescriptor field = null;
        PropertyDescriptor[] propDescr = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0; i < propDescr.length; i++ ) {
            if ( fieldName.equals( propDescr[i].getName() ) ) {
                field = propDescr[i];
                break;
            }
        }
        if ( field == null ) {
            throw new NoSuchFieldException( "Field [" + fieldName + "] not found in class [" + clazz.getName() + "]" );
        }
        return field;
    }

    /**
     * Defines the class header for the given class definition
     *
     * @param cw
     * @param dimDef
     */
    private void buildClassHeader(ClassWriter cw,
                                  ClassDefinition classDef) {
        // Building class header
        cw.visit( Opcodes.V1_4,
                  Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                  classDef.getClassNameAsInternal(),
                  null,
                  classDef.getSuperClassAsInternal(),
                  classDef.getInterfacesAsInternal() );

        cw.visitSource( classDef.getClassName() + ".java",
                        null );
    }

    /**
     * Creates the get method for the given field definition
     *
     * @param cw
     * @param classDef
     * @param fieldDef
     */
    private void buildGetMethod(ClassWriter cw,
                                ClassDefinition classDef,
                                Class clazz,
                                PropertyDescriptor field) {
        MethodVisitor mv;
        // Get method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "getValue",
                                 Type.getMethodDescriptor( Type.getType( Object.class ),
                                                           new Type[]{Type.getType( Object.class )} ),
                                 null,
                                 null );
            mv.visitCode();
            if ( field.getPropertyType().isPrimitive() ) {
                mv.visitTypeInsn( Opcodes.NEW,
                                  Type.getInternalName( (Class) typeMap.get( field.getPropertyType() ) ) );
                mv.visitInsn( Opcodes.DUP );
            }
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.CHECKCAST,
                              Type.getInternalName( clazz ) );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( clazz ),
                                field.getReadMethod().getName(),
                                Type.getMethodDescriptor( Type.getType( field.getPropertyType() ),
                                                          new Type[]{} ) );

            if ( field.getPropertyType().isPrimitive() ) {
                mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                    Type.getInternalName( (Class) typeMap.get( field.getPropertyType() ) ),
                                    "<init>",
                                    Type.getMethodDescriptor( Type.VOID_TYPE,
                                                              new Type[]{Type.getType( field.getPropertyType() )} ) );
            }
            mv.visitInsn( Opcodes.ARETURN );
            mv.visitMaxs( 0,
                          0 ); // automatically calculated
            mv.visitEnd();
        }
    }

    /**
     * Creates the set method for the given field definition
     *
     * @param cw
     * @param classDef
     * @param fieldDef
     */
    private void buildSetMethod(ClassWriter cw,
                                ClassDefinition classDef,
                                Class clazz,
                                PropertyDescriptor field) {
        MethodVisitor mv;
        // set method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "setValue",
                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                           new Type[]{Type.getType( Object.class ), Type.getType( Object.class )} ),
                                 null,
                                 null );

            mv.visitCode();

            if ( field.getPropertyType().isPrimitive() ) {
                // value != null ?
                mv.visitVarInsn( Opcodes.ALOAD,
                                 2 );

                Label ifnull = new Label();
                mv.visitJumpInsn( Opcodes.IFNULL,
                                  ifnull );

                // ((PrimitiveWrapper)value).xxxValue() :
                mv.visitVarInsn( Opcodes.ALOAD,
                                 2 );
                mv.visitTypeInsn( Opcodes.CHECKCAST,
                                  Type.getInternalName( (Class) typeMap.get( field.getPropertyType() ) ) );
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    Type.getInternalName( (Class) typeMap.get( field.getPropertyType() ) ),
                                    field.getPropertyType().getName() + "Value",
                                    Type.getMethodDescriptor( Type.getType( field.getPropertyType() ),
                                                              new Type[]{} ) );

                Label afterif = new Label();
                mv.visitJumpInsn( Opcodes.GOTO,
                                  afterif );

                // 0
                mv.visitLabel( ifnull );
                if ( field.getPropertyType().isAssignableFrom( long.class ) ) {
                    mv.visitInsn( Opcodes.LCONST_0 );
                } else if ( field.getPropertyType().isAssignableFrom( double.class ) ) {
                    mv.visitInsn( Opcodes.DCONST_0 );
                } else if ( field.getPropertyType().isAssignableFrom( float.class ) ) {
                    mv.visitInsn( Opcodes.FCONST_0 );
                } else {
                    mv.visitInsn( Opcodes.ICONST_0 );
                }

                // localVar = pop()
                mv.visitLabel( afterif );
                mv.visitVarInsn( Type.getType( field.getPropertyType() ).getOpcode( Opcodes.ISTORE ),
                                 3 );
            } else {
                // localVar = (xxxClass) value
                mv.visitVarInsn( Opcodes.ALOAD,
                                 2 );
                mv.visitTypeInsn( Opcodes.CHECKCAST,
                                  Type.getInternalName( field.getPropertyType() ) );
                mv.visitVarInsn( Opcodes.ASTORE,
                                 3 );
            }

            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.CHECKCAST,
                              Type.getInternalName( clazz ) );
            mv.visitVarInsn( Type.getType( field.getPropertyType() ).getOpcode( Opcodes.ILOAD ),
                             3 );

            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( clazz ),
                                field.getWriteMethod().getName(),
                                Type.getMethodDescriptor( Type.VOID_TYPE,
                                                          new Type[]{Type.getType( field.getPropertyType() )} ) );

            mv.visitInsn( Opcodes.RETURN );
            mv.visitMaxs( 0,
                          0 ); // auto calculated
            mv.visitEnd();
        }
    }

    /**
     * Creates a default constructor for the class
     *
     * @param cw
     */
    private void buildDefaultConstructor(ClassWriter cw) {
        MethodVisitor mv;
        // Building default constructor
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "<init>",
                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                           new Type[]{} ),
                                 null,
                                 null );
            mv.visitCode();
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                Type.getInternalName( Object.class ),
                                "<init>",
                                Type.getMethodDescriptor( Type.VOID_TYPE,
                                                          new Type[]{} ) );
            mv.visitInsn( Opcodes.RETURN );
            mv.visitMaxs( 0,
                          0 );
            mv.visitEnd();
        }
    }

    private Class loadClass(String classname,
                            byte[] b) throws ClassNotFoundException,
                                     SecurityException,
                                     NoSuchMethodException,
                                     IllegalArgumentException,
                                     IllegalAccessException,
                                     InvocationTargetException {
        //override classDefine (as it is protected) and define the class.
        Class clazz = null;
        ClassLoader loader = ClassBuilder.class.getClassLoader();
        Class cls = Class.forName( "java.lang.ClassLoader" );
        java.lang.reflect.Method method = cls.getDeclaredMethod( "defineClass",
                                                                 new Class[]{String.class, byte[].class, int.class, int.class} );

        // protected method invocaton
        method.setAccessible( true );
        try {
            Object[] args = new Object[]{classname, b, new Integer( 0 ), new Integer( b.length )};
            clazz = (Class) method.invoke( loader,
                                           args );
        } finally {
            method.setAccessible( false );
        }
        return clazz;
    }

    /**
     * Creates the String name for the get method for a field with the given name and type
     * @param name
     * @param type
     * @return
     */
    public String getReadMethod(Field field) {
        String prefix = null;
        if ( Boolean.TYPE.equals( field.getType() ) ) {
            prefix = "is";
        } else {
            prefix = "get";
        }
        return prefix + field.getName().substring( 0,
                                                   1 ).toUpperCase() + field.getName().substring( 1 );
    }

    /**
     * Creates the String name for the set method for a field with the given name and type
     *
     * @param name
     * @param type
     * @return
     */
    public String getWriteMethod(Field field) {
        return "set" + field.getName().substring( 0,
                                                  1 ).toUpperCase() + field.getName().substring( 1 );
    }

}
