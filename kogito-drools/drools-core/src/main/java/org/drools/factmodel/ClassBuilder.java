/*
 * Copyright 2008 JBoss Inc
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

package org.drools.factmodel;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.drools.definition.type.FactField;
import org.mvel2.asm.*;

/**
 * A builder to dynamically build simple Javabean(TM) classes
 * 
 */
public class ClassBuilder {
    private boolean     debug  = false;

    public ClassBuilder() {
        this( "true".equalsIgnoreCase( System.getProperty( "org.drools.classbuilder.debug" ) ) );
    }

    public ClassBuilder(final boolean debug) {
        this.debug = debug;
    }


    /**
     * Dynamically builds, defines and loads a class based on the given class definition
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

        ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS );
        //ClassVisitor cw = new CheckClassAdapter(cwr);

        this.buildClassHeader( cw,
                               classDef );

        // Building fields
        for ( FieldDefinition fieldDef : classDef.getFieldsDefinitions() ) {
            this.buildField( cw,
                             fieldDef );
        }

        // Building default constructor
        this.buildDefaultConstructor( cw,
                                      classDef );

        // Building constructor with all fields
        this.buildConstructorWithFields( cw,
                                         classDef,
                                         classDef.getFieldsDefinitions() );

        // Building constructor with key fields only
        List<FieldDefinition> keys = new LinkedList<FieldDefinition>();
        for ( FieldDefinition fieldDef : classDef.getFieldsDefinitions() ) {
            if ( fieldDef.isKey() ) {
                keys.add( fieldDef );
            }
        }
        if ( !keys.isEmpty() && keys.size() != classDef.getFieldsDefinitions().size() ) {
            this.buildConstructorWithFields( cw,
                                             classDef,
                                             keys );
        }

        // Building methods
        for ( FieldDefinition fieldDef : classDef.getFieldsDefinitions() ) {
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

        byte[] serializedClass = cw.toByteArray();

        return serializedClass;
    }

    /**
     * Defines the class header for the given class definition
     *
     * @param cw
     * @param classDef
     */
    private void buildClassHeader(ClassVisitor cw,
                                  ClassDefinition classDef) {
        String[] original = classDef.getInterfaces();
        String[] interfaces = new String[original.length];
        for ( int i = 0; i < original.length; i++ ) {
            interfaces[i] = getInternalType( original[i] );
        }
        // Building class header
        cw.visit( Opcodes.V1_5,
                  Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                  getInternalType( classDef.getClassName() ),
                  null,
                  getInternalType( classDef.getSuperClass() ),
                  interfaces );

        cw.visitSource( classDef.getClassName() + ".java",
                        null );
    }

    /**
     * Creates the field defined by the given FieldDefinition
     *
     * @param cw
     * @param fieldDef
     */
    private void buildField(ClassVisitor cw,
                            FieldDefinition fieldDef) {
        FieldVisitor fv;
        fv = cw.visitField( Opcodes.ACC_PRIVATE,
                            fieldDef.getName(),
                            getTypeDescriptor( fieldDef.getTypeName() ),
                            null,
                            null );
        fv.visitEnd();
    }

    /**
     * Creates a default constructor for the class
     *
     * @param cw
     */
    private void buildDefaultConstructor(ClassVisitor cw,
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
            try {
                mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                    //Type.getInternalName( Object.class ),
                                    Type.getInternalName(Class.forName(classDef.getSuperClass())),
                                    "<init>",
                                    Type.getMethodDescriptor( Type.VOID_TYPE,
                                                              new Type[]{} ) );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            mv.visitInsn(Opcodes.RETURN);
            Label l1 = null;
            if ( this.debug ) {
                l1 = new Label();
                mv.visitLabel( l1 );
                mv.visitLocalVariable( "this",
                                       getTypeDescriptor( classDef.getClassName() ),
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
     * Creates a constructor that takes and assigns values to all 
     * fields in the order they are declared.
     *
     * @param cw
     * @param classDef
     */
    private void buildConstructorWithFields(ClassVisitor cw,
                                            ClassDefinition classDef,
                                            Collection<FieldDefinition> fieldDefs) {
        MethodVisitor mv;
        // Building  constructor
        {
            Type[] params = new Type[fieldDefs.size()];
            int index = 0;
            for ( FieldDefinition field : fieldDefs ) {
                params[index++] = Type.getType( getTypeDescriptor( field.getTypeName() ) );
            }

            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 "<init>",
                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                           params ),
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
            try {
                mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                    Type.getInternalName(Class.forName(classDef.getSuperClass())),
                                    //Type.getInternalName( Object.class ),
                                    "<init>",
                                    Type.getMethodDescriptor( Type.VOID_TYPE,
                                                              new Type[]{} ) );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            index = 1; // local vars start at 1, as 0 is "this"
            for ( FieldDefinition field : fieldDefs ) {
                if ( this.debug ) {
                    Label l11 = new Label();
                    mv.visitLabel( l11 );
                }
                mv.visitVarInsn( Opcodes.ALOAD,
                                 0 );
                mv.visitVarInsn( Type.getType( getTypeDescriptor( field.getTypeName() ) ).getOpcode( Opcodes.ILOAD ),
                                 index++ );
                if ( field.getTypeName().equals( "long" ) || field.getTypeName().equals( "double" ) ) {
                    // long and double variables use 2 words on the variables table
                    index++;
                }
                mv.visitFieldInsn( Opcodes.PUTFIELD,
                                   getInternalType( classDef.getClassName() ),
                                   field.getName(),
                                   getTypeDescriptor( field.getTypeName() ) );

            }

            mv.visitInsn( Opcodes.RETURN );
            Label l1 = null;
            if ( this.debug ) {
                l1 = new Label();
                mv.visitLabel( l1 );
                mv.visitLocalVariable( "this",
                                       getTypeDescriptor( classDef.getClassName() ),
                                       null,
                                       l0,
                                       l1,
                                       0 );
                for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                    Label l11 = new Label();
                    mv.visitLabel( l11 );
                    mv.visitLocalVariable( field.getName(),
                                           getTypeDescriptor( field.getTypeName() ),
                                           null,
                                           l0,
                                           l1,
                                           0 );
                }
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
    private void buildSetMethod(ClassVisitor cw,
                                ClassDefinition classDef,
                                FieldDefinition fieldDef) {
        MethodVisitor mv;
        // set method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 fieldDef.getWriteMethod(),
                                 Type.getMethodDescriptor( Type.VOID_TYPE,
                                                           new Type[]{Type.getType( getTypeDescriptor( fieldDef.getTypeName() ) )} ),
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
            mv.visitVarInsn( Type.getType( getTypeDescriptor( fieldDef.getTypeName() ) ).getOpcode( Opcodes.ILOAD ),
                             1 );
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                               getInternalType( classDef.getClassName() ),
                               fieldDef.getName(),
                               getTypeDescriptor( fieldDef.getTypeName() ) );

            mv.visitInsn( Opcodes.RETURN );
            Label l1 = null;
            if ( this.debug ) {
                l1 = new Label();
                mv.visitLabel( l1 );
                mv.visitLocalVariable( "this",
                                       getTypeDescriptor( classDef.getClassName() ),
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
    private void buildGetMethod(ClassVisitor cw,
                                ClassDefinition classDef,
                                FieldDefinition fieldDef) {
        MethodVisitor mv;
        // Get method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                 fieldDef.getReadMethod(),
                                 Type.getMethodDescriptor( Type.getType( getTypeDescriptor( fieldDef.getTypeName() ) ),
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
                               getInternalType( classDef.getClassName() ),
                               fieldDef.getName(),
                               getTypeDescriptor( fieldDef.getTypeName() ) );
            mv.visitInsn( Type.getType( getTypeDescriptor( fieldDef.getTypeName() ) ).getOpcode( Opcodes.IRETURN ) );
            Label l1 = null;
            if ( this.debug ) {
                l1 = new Label();
                mv.visitLabel( l1 );
                mv.visitLocalVariable( "this",
                                       getTypeDescriptor( classDef.getClassName() ),
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

    private void buildEquals(ClassVisitor cw,
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

            // if ( this == obj ) return true;
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            Label l1 = new Label();
            mv.visitJumpInsn( Opcodes.IF_ACMPNE,
                              l1 );
            mv.visitInsn( Opcodes.ICONST_1 );
            mv.visitInsn( Opcodes.IRETURN );

            // if ( obj == null ) return false;
            mv.visitLabel( l1 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            Label l2 = new Label();
            mv.visitJumpInsn( Opcodes.IFNONNULL,
                              l2 );
            mv.visitInsn( Opcodes.ICONST_0 );
            mv.visitInsn( Opcodes.IRETURN );

            // if ( getClass() != obj.getClass() ) return false;
            mv.visitLabel( l2 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             0 );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( Object.class ),
                                "getClass",
                                Type.getMethodDescriptor( Type.getType( Class.class ),
                                                          new Type[]{} ) );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( Object.class ),
                                "getClass",
                                Type.getMethodDescriptor( Type.getType( Class.class ),
                                                          new Type[]{} ) );
            Label l3 = new Label();
            mv.visitJumpInsn( Opcodes.IF_ACMPEQ,
                              l3 );
            mv.visitInsn( Opcodes.ICONST_0 );
            mv.visitInsn( Opcodes.IRETURN );

            // final <classname> other = (<classname>) obj;
            mv.visitLabel( l3 );
            mv.visitVarInsn( Opcodes.ALOAD,
                             1 );
            mv.visitTypeInsn( Opcodes.CHECKCAST,
                              getInternalType( classDef.getClassName() ) );
            mv.visitVarInsn( Opcodes.ASTORE,
                             2 );

            // for each key field
            int count = 0;
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                if ( field.isKey() ) {
                    count++;

                    Label goNext = new Label();

                    if ( isPrimitive( field.getTypeName() ) ) {
                        // if attr is primitive 

                        // if ( this.<attr> != other.<booleanAttr> ) return false;
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           getInternalType( classDef.getClassName() ),
                                           field.getName(),
                                           getTypeDescriptor( field.getTypeName() ) );

                        mv.visitVarInsn( Opcodes.ALOAD,
                                         2 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           getInternalType( classDef.getClassName() ),
                                           field.getName(),
                                           getTypeDescriptor( field.getTypeName() ) );

                        if ( field.getTypeName().equals( "long" ) ) {
                            mv.visitInsn( Opcodes.LCMP );
                            mv.visitJumpInsn( Opcodes.IFEQ,
                                              goNext );
                        } else if ( field.getTypeName().equals( "double" ) ) {
                            mv.visitInsn( Opcodes.DCMPL );
                            mv.visitJumpInsn( Opcodes.IFEQ,
                                              goNext );
                        } else if ( field.getTypeName().equals( "float" ) ) {
                            mv.visitInsn( Opcodes.FCMPL );
                            mv.visitJumpInsn( Opcodes.IFEQ,
                                              goNext );
                        } else {
                            // boolean, byte, char, short, int
                            mv.visitJumpInsn( Opcodes.IF_ICMPEQ,
                                              goNext );
                        }
                        mv.visitInsn( Opcodes.ICONST_0 );
                        mv.visitInsn( Opcodes.IRETURN );
                    } else {
                        // if attr is not a primitive

                        // if ( this.<attr> == null && other.<attr> != null ||
                        //      this.<attr> != null && ! this.<attr>.equals( other.<attr> ) ) return false;
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           getInternalType( classDef.getClassName() ),
                                           field.getName(),
                                           getTypeDescriptor( field.getTypeName() ) );
                        Label secondIfPart = new Label();
                        mv.visitJumpInsn( Opcodes.IFNONNULL,
                                          secondIfPart );

                        // if ( other.objAttr != null ) return false;
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         2 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           getInternalType( classDef.getClassName() ),
                                           field.getName(),
                                           getTypeDescriptor( field.getTypeName() ) );
                        Label returnFalse = new Label();
                        mv.visitJumpInsn( Opcodes.IFNONNULL,
                                          returnFalse );

                        mv.visitLabel( secondIfPart );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           getInternalType( classDef.getClassName() ),
                                           field.getName(),
                                           getTypeDescriptor( field.getTypeName() ) );
                        mv.visitJumpInsn( Opcodes.IFNULL,
                                          goNext );

                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           getInternalType( classDef.getClassName() ),
                                           field.getName(),
                                           getTypeDescriptor( field.getTypeName() ) );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         2 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           getInternalType( classDef.getClassName() ),
                                           field.getName(),
                                           getTypeDescriptor( field.getTypeName() ) );
                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                            getInternalType( field.getTypeName() ),
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
                                       getTypeDescriptor( classDef.getClassName() ),
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
                                       getTypeDescriptor( classDef.getClassName() ),
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

    private void buildHashCode(ClassVisitor cw,
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

            // int result = 1;
            mv.visitInsn( Opcodes.ICONST_1 );
            mv.visitVarInsn( Opcodes.ISTORE,
                             1 );

            // for each key field
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                if ( field.isKey() ) {

                    // result = result * 31 + <attr_hash>
                    mv.visitVarInsn( Opcodes.ILOAD,
                                     1 );
                    mv.visitIntInsn( Opcodes.BIPUSH,
                                     31 );
                    mv.visitVarInsn( Opcodes.ILOAD,
                                     1 );
                    mv.visitInsn( Opcodes.IMUL );

                    mv.visitVarInsn( Opcodes.ALOAD,
                                     0 );
                    mv.visitFieldInsn( Opcodes.GETFIELD,
                                       getInternalType( classDef.getClassName() ),
                                       field.getName(),
                                       getTypeDescriptor( field.getTypeName() ) );

                    if ( "boolean".equals( field.getTypeName() ) ) {
                        // attr_hash ::== <boolean_attr> ? 1231 : 1237;
                        Label blabel1 = new Label();
                        mv.visitJumpInsn( Opcodes.IFEQ,
                                          blabel1 );
                        mv.visitIntInsn( Opcodes.SIPUSH,
                                         1231 );
                        Label blabel2 = new Label();
                        mv.visitJumpInsn( Opcodes.GOTO,
                                          blabel2 );
                        mv.visitLabel( blabel1 );
                        mv.visitIntInsn( Opcodes.SIPUSH,
                                         1237 );
                        mv.visitLabel( blabel2 );
                    } else if ( "long".equals( field.getTypeName() ) ) {
                        // attr_hash ::== (int) (longAttr ^ (longAttr >>> 32))
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           getInternalType( classDef.getClassName() ),
                                           field.getName(),
                                           getTypeDescriptor( field.getTypeName() ) );
                        mv.visitIntInsn( Opcodes.BIPUSH,
                                         32 );
                        mv.visitInsn( Opcodes.LUSHR );
                        mv.visitInsn( Opcodes.LXOR );
                        mv.visitInsn( Opcodes.L2I );

                    } else if ( "float".equals( field.getTypeName() ) ) {
                        // attr_hash ::== Float.floatToIntBits( floatAttr );
                        mv.visitMethodInsn( Opcodes.INVOKESTATIC,
                                            Type.getInternalName( Float.class ),
                                            "floatToIntBits",
                                            "(F)I" );
                    } else if ( "double".equals( field.getTypeName() ) ) {
                        // attr_hash ::== (int) (Double.doubleToLongBits( doubleAttr ) ^ (Double.doubleToLongBits( doubleAttr ) >>> 32));
                        mv.visitMethodInsn( Opcodes.INVOKESTATIC,
                                            Type.getInternalName( Double.class ),
                                            "doubleToLongBits",
                                            "(D)J" );
                        mv.visitInsn( Opcodes.DUP2 );
                        mv.visitIntInsn( Opcodes.BIPUSH,
                                         32 );
                        mv.visitInsn( Opcodes.LUSHR );
                        mv.visitInsn( Opcodes.LXOR );
                        mv.visitInsn( Opcodes.L2I );
                    } else if ( !isPrimitive( field.getTypeName() ) ) {
                        // attr_hash ::== ((objAttr == null) ? 0 : objAttr.hashCode());
                        Label olabel1 = new Label();
                        mv.visitJumpInsn( Opcodes.IFNONNULL,
                                          olabel1 );
                        mv.visitInsn( Opcodes.ICONST_0 );
                        Label olabel2 = new Label();
                        mv.visitJumpInsn( Opcodes.GOTO,
                                          olabel2 );
                        mv.visitLabel( olabel1 );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                         0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD,
                                           getInternalType( classDef.getClassName() ),
                                           field.getName(),
                                           getTypeDescriptor( field.getTypeName() ) );
                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                            getInternalType( field.getTypeName() ),
                                            "hashCode",
                                            "()I" );
                        mv.visitLabel( olabel2 );
                    }

                    mv.visitInsn( Opcodes.IADD );
                    mv.visitVarInsn( Opcodes.ISTORE,
                                     1 );
                }
            }
            mv.visitVarInsn( Opcodes.ILOAD,
                             1 );
            mv.visitInsn( Opcodes.IRETURN );

            Label lastLabel = null;
            if ( this.debug ) {
                lastLabel = new Label();
                mv.visitLabel( lastLabel );
                mv.visitLocalVariable( "this",
                                       getTypeDescriptor( classDef.getClassName() ),
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

    private void buildToString(ClassVisitor cw,
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

            // StringBuilder buf = new StringBuilder();
            mv.visitTypeInsn( Opcodes.NEW,
                              Type.getInternalName( StringBuilder.class ) );
            mv.visitInsn( Opcodes.DUP );
            mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                                Type.getInternalName( StringBuilder.class ),
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
                                getInternalType( classDef.getClassName() ),
                                "getClass",
                                "()Ljava/lang/Class;" );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( Class.class ),
                                "getSimpleName",
                                "()Ljava/lang/String;" );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( StringBuilder.class ),
                                "append",
                                "(Ljava/lang/String;)Ljava/lang/StringBuilder;" );

            // buf.append("( ");
            mv.visitLdcInsn( "( " );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( StringBuilder.class ),
                                "append",
                                "(Ljava/lang/String;)Ljava/lang/StringBuilder;" );

            boolean previous = false;
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                if ( previous ) {
                    // buf.append(", ");
                    mv.visitLdcInsn( ", " );
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                        Type.getInternalName( StringBuilder.class ),
                                        "append",
                                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;" );
                }
                // buf.append(attrName)
                mv.visitLdcInsn( field.getName() );
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    Type.getInternalName( StringBuilder.class ),
                                    "append",
                                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;" );

                // buf.append("=");
                mv.visitLdcInsn( "=" );
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    Type.getInternalName( StringBuilder.class ),
                                    "append",
                                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;" );

                // buf.append(attrValue)
                mv.visitVarInsn( Opcodes.ALOAD,
                                 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD,
                                   getInternalType( classDef.getClassName() ),
                                   field.getName(),
                                   getTypeDescriptor( field.getTypeName() ) );

                if ( isPrimitive( field.getTypeName() ) ) {
                    String type = field.getTypeName().matches( "(byte|short)" ) ? "int" : field.getTypeName();
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                        Type.getInternalName( StringBuilder.class ),
                                        "append",
                                        Type.getMethodDescriptor( Type.getType( StringBuilder.class ),
                                                                  new Type[]{Type.getType( getTypeDescriptor( type ) )} ) );
                } else {
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                        Type.getInternalName( StringBuilder.class ),
                                        "append",
                                        Type.getMethodDescriptor( Type.getType( StringBuilder.class ),
                                                                  new Type[]{Type.getType( Object.class )} ) );
                }
                previous = true;
            }

            mv.visitLdcInsn( " )" );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( StringBuilder.class ),
                                "append",
                                "(Ljava/lang/String;)Ljava/lang/StringBuilder;" );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                Type.getInternalName( StringBuilder.class ),
                                "toString",
                                "()Ljava/lang/String;" );
            mv.visitInsn( Opcodes.ARETURN );

            Label lastLabel = null;
            if ( this.debug ) {
                lastLabel = new Label();
                mv.visitLabel( lastLabel );
                mv.visitLocalVariable( "this",
                                       getTypeDescriptor( classDef.getClassName() ),
                                       null,
                                       l0,
                                       lastLabel,
                                       0 );
                mv.visitLocalVariable( "buf",
                                       Type.getDescriptor( StringBuilder.class ),
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

    /**
     * Returns the corresponding internal type representation for the
     * given type.
     * 
     * I decided to not use the ASM Type class methods because they require
     * resolving the actual type into a Class instance and at this point,
     * I think it is best to delay type resolution until it is really needed.
     * 
     * @param type
     * @return
     */
    private String getInternalType(String type) {
        String internalType = null;
        if ( "byte".equals( type ) ) {
            internalType = "B";
        } else if ( "char".equals( type ) ) {
            internalType = "C";
        } else if ( "double".equals( type ) ) {
            internalType = "D";
        } else if ( "float".equals( type ) ) {
            internalType = "F";
        } else if ( "int".equals( type ) ) {
            internalType = "I";
        } else if ( "long".equals( type ) ) {
            internalType = "J";
        } else if ( "short".equals( type ) ) {
            internalType = "S";
        } else if ( "boolean".equals( type ) ) {
            internalType = "Z";
        } else if ( "void".equals( type ) ) {
            internalType = "V";
        } else if ( type != null ) {
            // I think this will fail for inner classes, but we don't really 
            // support inner class generation at the moment
            internalType = type.replace( '.',
                                         '/' );
        }
        return internalType;
    }

    /**
     * Returns the corresponding type descriptor for the
     * given type.
     * 
     * I decided to not use the ASM Type class methods because they require
     * resolving the actual type into a Class instance and at this point,
     * I think it is best to delay type resolution until it is really needed.
     * 
     * @param type
     * @return
     */
    private String getTypeDescriptor(String type) {
        String internalType = null;
        if ( "byte".equals( type ) ) {
            internalType = "B";
        } else if ( "char".equals( type ) ) {
            internalType = "C";
        } else if ( "double".equals( type ) ) {
            internalType = "D";
        } else if ( "float".equals( type ) ) {
            internalType = "F";
        } else if ( "int".equals( type ) ) {
            internalType = "I";
        } else if ( "long".equals( type ) ) {
            internalType = "J";
        } else if ( "short".equals( type ) ) {
            internalType = "S";
        } else if ( "boolean".equals( type ) ) {
            internalType = "Z";
        } else if ( "void".equals( type ) ) {
            internalType = "V";
        } else if ( type != null ) {
            // I think this will fail for inner classes, but we don't really 
            // support inner class generation at the moment
            internalType = "L" + type.replace( '.',
                                               '/' ) + ";";
        }
        return internalType;
    }

    /**
     * Returns true if the provided type is a primitive type
     *  
     * @param type
     * @return
     */
    private boolean isPrimitive(String type) {
        boolean isPrimitive = false;
        if ( "byte".equals( type ) || "char".equals( type ) || "double".equals( type ) || "float".equals( type ) || "int".equals( type ) || "long".equals( type ) || "short".equals( type ) || "boolean".equals( type ) || "void".equals( type ) ) {
            isPrimitive = true;
        }
        return isPrimitive;
    }

}
