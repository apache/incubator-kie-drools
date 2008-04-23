package org.drools.factmodel;


import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


import org.drools.asm.ClassWriter;
import org.drools.asm.FieldVisitor;
import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;

/**
 * <p><b>Title:</b> ClassBuilder</p>
 * <p><b>Description:</b> A builder to dinamically build simple Value Object classes</p>
 * <p><b>Copyright:</b> Copyright (c) 2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: ClassBuilder.java 241 2006-05-11 19:48:25Z framos $
 */
public class ClassBuilder {
    private boolean debug = false;

    public ClassBuilder() {
        this.debug = "false".equalsIgnoreCase( System.getProperty( "br.com.auster.common.asm.debug" ) );
    }

    /**
     * Dinamically builds, defines and loads a class based on the given class definition
     *
     * @param classDef the class definition object structure
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
     * @throws InstantiationException
     */
    public Class buildAndLoadClass(ClassDefinition classDef) throws IOException,
                                                            IntrospectionException,
                                                            SecurityException,
                                                            IllegalArgumentException,
                                                            ClassNotFoundException,
                                                            NoSuchMethodException,
                                                            IllegalAccessException,
                                                            InvocationTargetException,
                                                            InstantiationException,
                                                            NoSuchFieldException {
        try {
            Class clazz = Class.forName( classDef.getClassName() );

            classDef.setDefinedClass( clazz );

            return clazz;

        } catch ( ClassNotFoundException e ) {
            // class not loaded, so create and load it
            byte[] serializedClazz = this.buildClass( classDef );

            Class clazz = this.loadClass( classDef.getClassName(),
                                          serializedClazz );
            classDef.setDefinedClass( clazz );

            return clazz;
        }
    }

    /**
     * Dinamically builds, defines and loads a class based on the given class definition
     *
     * @param classDef the class definition object structure
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
     * @throws InstantiationException
     */
    public byte[] buildClass(ClassDefinition classDef) throws IOException,
                                                      IntrospectionException,
                                                      SecurityException,
                                                      IllegalArgumentException,
                                                      ClassNotFoundException,
                                                      NoSuchMethodException,
                                                      IllegalAccessException,
                                                      InvocationTargetException,
                                                      InstantiationException,
                                                      NoSuchFieldException {

        ClassWriter cw = new ClassWriter( true );

        this.buildClassHeader( cw,
                               classDef );

        // Building fields
        for ( FieldDefinition fieldDef : classDef.getFields() ) {
            this.buildField( cw,
                             fieldDef );
        }

        // Building default constructor
        this.buildDefaultConstructor( cw,
                                      classDef );

        // Building methods
        for ( FieldDefinition fieldDef : classDef.getFields() ) {
            this.buildGetMethod( cw,
                                 classDef,
                                 fieldDef );
            this.buildSetMethod( cw,
                                 classDef,
                                 fieldDef );
        }

        this.buildEquals( cw,
                          classDef );
        this.buildHashCode( cw,
                            classDef );

        this.buildToString( cw,
                            classDef );

        cw.visitEnd();

        return cw.toByteArray();
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
     * Creates the field defined by the given FieldDefinition
     *
     * @param cw
     * @param fieldDef
     */
    private void buildField(ClassWriter cw,
                            FieldDefinition fieldDef) {
        FieldVisitor fv;
        fv = cw.visitField( Opcodes.ACC_PRIVATE,
                            fieldDef.getName(),
                            fieldDef.getInternalType(),
                            null,
                            null );
        fv.visitEnd();
    }

    /**
     * Creates a default constructor for the class
     *
     * @param cw
     */
    private void buildDefaultConstructor(ClassWriter cw,
                                         ClassDefinition classDef) {
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
            Label l0 = null;
            if ( this.debug ) {
                l0 = new Label();
                mv.visitLabel( l0 );
            }
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                Type.getInternalName( Object.class ),
                                "<init>",
                                Type.getMethodDescriptor( Type.VOID_TYPE,
                                                          new Type[]{} ) );
            mv.visitInsn( Opcodes.RETURN );
            Label l1 = null;
            if ( this.debug ) {
                l1 = new Label();
                mv.visitLabel( l1 );
                mv.visitLocalVariable( "this",
                                       classDef.getInternalName(),
                                       null,
                                       l0,
                                       l1,
                                       0 );
            }
            mv.visitMaxs( 0,
                          0 );
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
                                FieldDefinition fieldDef) {
        MethodVisitor mv;
        // set method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 fieldDef.getWriteMethod(),
                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                           new Type[]{Type.getType( fieldDef.getInternalType() )} ),
                                 null,
                                 null );
            mv.visitCode();
            Label l0 = null;
            if ( this.debug ) {
                l0 = new Label();
                mv.visitLabel( l0 );
            }
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Type.getType( fieldDef.getInternalType() ).getOpcode( Opcodes.ILOAD ),
                             1 );
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               classDef.getClassNameAsInternal(),
                               fieldDef.getName(),
                               fieldDef.getInternalType() );

            mv.visitInsn( Opcodes.RETURN );
            Label l1 = null;
            if ( this.debug ) {
                l1 = new Label();
                mv.visitLabel( l1 );
                mv.visitLocalVariable( "this",
                                       classDef.getInternalName(),
                                       null,
                                       l0,
                                       l1,
                                       0 );
            }
            mv.visitMaxs( 0,
                          0 );
            mv.visitEnd();
        }
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
                                FieldDefinition fieldDef) {
        MethodVisitor mv;
        // Get method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 fieldDef.getReadMethod(),
                                 Type.getMethodDescriptor( Type.getType( fieldDef.getInternalType() ),
                                                           new Type[]{} ),
                                 null,
                                 null );
            mv.visitCode();
            Label l0 = null;
            if ( this.debug ) {
                l0 = new Label();
                mv.visitLabel( l0 );
            }
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitFieldInsn( Opcodes.GETFIELD,
                               classDef.getClassNameAsInternal(),
                               fieldDef.getName(),
                               fieldDef.getInternalType() );
            mv.visitInsn( Type.getType( fieldDef.getInternalType() ).getOpcode( Opcodes.IRETURN ) );
            Label l1 = null;
            if ( this.debug ) {
                l1 = new Label();
                mv.visitLabel( l1 );
                mv.visitLocalVariable( "this",
                                       classDef.getInternalName(),
                                       null,
                                       l0,
                                       l1,
                                       0 );
            }
            mv.visitMaxs( 0,
                          0 );
            mv.visitEnd();
        }
    }

    private void buildEquals(ClassWriter cw,
                             ClassDefinition classDef) {
        MethodVisitor mv;
        // Building equals method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "equals",
                                 "(Ljava/lang/Object;)Z",
                                 null,
                                 null );
            mv.visitCode();
            Label l0 = null;
            if ( this.debug ) {
                l0 = new Label();
                mv.visitLabel( l0 );
            }
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            Label l1 = new Label();
            mv.visitJumpInsn( Opcodes.IF_ACMPNE,
                              l1 );
            mv.visitInsn( Opcodes.ICONST_1 );
            mv.visitInsn( Opcodes.IRETURN );
            mv.visitLabel( l1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            Label l3 = new Label();
            mv.visitJumpInsn( Opcodes.IFNULL,
                              l3 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.INSTANCEOF,
                              classDef.getClassNameAsInternal() );
            Label l4 = new Label();
            mv.visitJumpInsn( Opcodes.IFNE,
                              l4 );
            mv.visitLabel( l3 );
            mv.visitInsn( Opcodes.ICONST_0 );
            mv.visitInsn( Opcodes.IRETURN );
            mv.visitLabel( l4 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.CHECKCAST,
                              classDef.getClassNameAsInternal() );
            mv.visitVarInsn( Opcodes.ASTORE,
                             2 );

            int count = 0;
            for ( FieldDefinition field : classDef.getFields() ) {
                if ( field.isKey() ) {
                    count++;
                    Label goNext = new Label();
                    if ( field.isPrimitive() ) {
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           classDef.getClassNameAsInternal(),
                                           field.getName(),
                                           field.getInternalType() );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         2 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           classDef.getClassNameAsInternal(),
                                           field.getName(),
                                           field.getInternalType() );
                        if ( field.getType().equals( "long" ) ) {
                            mv.visitInsn( Opcodes.LCMP );
                            mv.visitJumpInsn( Opcodes.IFEQ,
                                              goNext );
                        } else if ( field.getType().equals( "double" ) ) {
                            mv.visitInsn( Opcodes.DCMPL );
                            mv.visitJumpInsn( Opcodes.IFEQ,
                                              goNext );
                        } else if ( field.getType().equals( "float" ) ) {
                            mv.visitInsn( Opcodes.FCMPL );
                            mv.visitJumpInsn( Opcodes.IFEQ,
                                              goNext );
                        } else {
                            mv.visitJumpInsn( Opcodes.IF_ICMPEQ,
                                              goNext );
                        }
                        mv.visitInsn( Opcodes.ICONST_0 );
                        mv.visitInsn( Opcodes.IRETURN );
                    } else {
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           classDef.getClassNameAsInternal(),
                                           field.getName(),
                                           field.getInternalType() );
                        Label secondIfPart = new Label();
                        mv.visitJumpInsn( Opcodes.IFNONNULL,
                                          secondIfPart );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         2 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           classDef.getClassNameAsInternal(),
                                           field.getName(),
                                           field.getInternalType() );
                        Label returnFalse = new Label();
                        mv.visitJumpInsn( Opcodes.IFNONNULL,
                                          returnFalse );
                        mv.visitLabel( secondIfPart );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           classDef.getClassNameAsInternal(),
                                           field.getName(),
                                           field.getInternalType() );
                        mv.visitJumpInsn( Opcodes.IFNULL,
                                          goNext );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           classDef.getClassNameAsInternal(),
                                           field.getName(),
                                           field.getInternalType() );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         2 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           classDef.getClassNameAsInternal(),
                                           field.getName(),
                                           field.getInternalType() );
                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                            field.getBoxTypeName(),
                                            "equals",
                                            "(Ljava/lang/Object;)Z" );
                        mv.visitJumpInsn( Opcodes.IFNE,
                                          goNext );
                        mv.visitLabel( returnFalse );
                        mv.visitInsn( Opcodes.ICONST_0 );
                        mv.visitInsn( Opcodes.IRETURN );
                    }
                    mv.visitLabel( goNext );
                }
            }
            if ( count > 0 ) {
                mv.visitInsn( Opcodes.ICONST_1 );
            } else {
                mv.visitInsn( Opcodes.ICONST_0 );
            }
            mv.visitInsn( Opcodes.IRETURN );
            Label lastLabel = null;
            if ( this.debug ) {
                lastLabel = new Label();
                mv.visitLabel( lastLabel );
                mv.visitLocalVariable( "this",
                                       classDef.getInternalName(),
                                       null,
                                       l0,
                                       lastLabel,
                                       0 );
                mv.visitLocalVariable( "obj",
                                       Type.getDescriptor( Object.class ),
                                       null,
                                       l0,
                                       lastLabel,
                                       1 );
                mv.visitLocalVariable( "other",
                                       classDef.getInternalName(),
                                       null,
                                       l0,
                                       lastLabel,
                                       2 );
            }
            mv.visitMaxs( 0,
                          0 );
            mv.visitEnd();
        }
    }

    private void buildHashCode(ClassWriter cw,
                               ClassDefinition classDef) {

        MethodVisitor mv;
        // Building hashCode() method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "hashCode",
                                 "()I",
                                 null,
                                 null );
            mv.visitCode();
            Label l0 = null;
            if ( this.debug ) {
                l0 = new Label();
                mv.visitLabel( l0 );
            }
            mv.visitInsn( Opcodes.ICONST_0 );
            mv.visitVarInsn( Opcodes.ISTORE,
                             1 );

            for ( FieldDefinition field : classDef.getFields() ) {
                if ( field.isKey() ) {
                    mv.visitVarInsn( Opcodes.ILOAD,
                                     1 );
                    if ( field.isPrimitive() ) {
                        mv.visitTypeInsn( Opcodes.NEW,
                                          field.getBoxTypeName() );
                        mv.visitInsn( Opcodes.DUP );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           classDef.getClassNameAsInternal(),
                                           field.getName(),
                                           field.getInternalType() );
                        mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                            field.getBoxTypeName(),
                                            "<init>",
                                            Type.getMethodDescriptor( Type.VOID_TYPE,
                                                                      new Type[]{Type.getType( field.getInternalType() )} ) );
                    } else {
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           classDef.getClassNameAsInternal(),
                                           field.getName(),
                                           field.getInternalType() );

                    }
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                        field.getBoxTypeName(),
                                        "hashCode",
                                        "()I" );
                    mv.visitInsn( Opcodes.IADD );
                    mv.visitVarInsn( Opcodes.ISTORE,
                                     1 );
                }
            }
            mv.visitVarInsn( Opcodes.ILOAD,
                             1 );
            mv.visitIntInsn( Opcodes.SIPUSH,
                             9653 );
            mv.visitInsn( Opcodes.IREM );
            mv.visitInsn( Opcodes.IRETURN );

            Label lastLabel = null;
            if ( this.debug ) {
                lastLabel = new Label();
                mv.visitLabel( lastLabel );
                mv.visitLocalVariable( "this",
                                       classDef.getInternalName(),
                                       null,
                                       l0,
                                       lastLabel,
                                       0 );
                mv.visitLocalVariable( "hash",
                                       Type.getDescriptor( int.class ),
                                       null,
                                       l0,
                                       lastLabel,
                                       1 );
            }
            mv.visitMaxs( 0,
                          0 );
            mv.visitEnd();
        }
    }

    private void buildToString(ClassWriter cw,
                               ClassDefinition classDef) {
        MethodVisitor mv;
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "toString",
                                 "()Ljava/lang/String;",
                                 null,
                                 null );
            mv.visitCode();

            Label l0 = null;
            if ( this.debug ) {
                l0 = new Label();
                mv.visitLabel( l0 );
            }

            // StringBuffer buf = new StringBuffer();
            mv.visitTypeInsn( Opcodes.NEW,
                              "java/lang/StringBuffer" );
            mv.visitInsn( Opcodes.DUP );
            mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                "java/lang/StringBuffer",
                                "<init>",
                                "()V" );
            mv.visitVarInsn( Opcodes.ASTORE,
                             1 );

            // buf.append(this.getClass().getSimpleName())
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                classDef.getClassNameAsInternal(),
                                "getClass",
                                "()Ljava/lang/Class;" );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                "java/lang/Class",
                                "getSimpleName",
                                "()Ljava/lang/String;" );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                "java/lang/StringBuffer",
                                "append",
                                "(Ljava/lang/String;)Ljava/lang/StringBuffer;" );

            // buf.append("( ");
            mv.visitLdcInsn( "( " );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                "java/lang/StringBuffer",
                                "append",
                                "(Ljava/lang/String;)Ljava/lang/StringBuffer;" );

            boolean previous = false;
            for ( FieldDefinition field : classDef.getFields() ) {
                if ( previous ) {
                    // buf.append(", ");
                    mv.visitLdcInsn( ", " );
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                        "java/lang/StringBuffer",
                                        "append",
                                        "(Ljava/lang/String;)Ljava/lang/StringBuffer;" );
                }
                // buf.append(attrName)
                mv.visitLdcInsn( field.getName() );
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuffer",
                                    "append",
                                    "(Ljava/lang/String;)Ljava/lang/StringBuffer;" );

                // buf.append("=");
                mv.visitLdcInsn( "=" );
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    "java/lang/StringBuffer",
                                    "append",
                                    "(Ljava/lang/String;)Ljava/lang/StringBuffer;" );

                // buf.append(attrValue)
                mv.visitVarInsn( Opcodes.ALOAD,
                                 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD,
                                   classDef.getClassNameAsInternal(),
                                   field.getName(),
                                   field.getInternalType() );

                if ( field.isPrimitive() ) {
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                        "java/lang/StringBuffer",
                                        "append",
                                        Type.getMethodDescriptor( Type.getType( StringBuffer.class ),
                                                                  new Type[]{Type.getType( field.getInternalType() )} ) );
                } else {
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                        "java/lang/StringBuffer",
                                        "append",
                                        Type.getMethodDescriptor( Type.getType( StringBuffer.class ),
                                                                  new Type[]{Type.getType( Object.class )} ) );
                }
                previous = true;
            }

            mv.visitLdcInsn( " )" );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                "java/lang/StringBuffer",
                                "append",
                                "(Ljava/lang/String;)Ljava/lang/StringBuffer;" );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                "java/lang/StringBuffer",
                                "toString",
                                "()Ljava/lang/String;" );
            mv.visitInsn( Opcodes.ARETURN );

            Label lastLabel = null;
            if ( this.debug ) {
                lastLabel = new Label();
                mv.visitLabel( lastLabel );
                mv.visitLocalVariable( "this",
                                       classDef.getInternalName(),
                                       null,
                                       l0,
                                       lastLabel,
                                       0 );
                mv.visitLocalVariable( "buf",
                                       Type.getDescriptor( StringBuffer.class ),
                                       null,
                                       l0,
                                       lastLabel,
                                       1 );
            }
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

}
