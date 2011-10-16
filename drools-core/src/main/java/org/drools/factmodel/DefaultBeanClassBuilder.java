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

import org.drools.factmodel.traits.TraitableBean;
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A builder to dynamically build simple Javabean(TM) classes
 */
public class DefaultBeanClassBuilder implements Opcodes, BeanClassBuilder {
    protected boolean     debug  = false;

    public DefaultBeanClassBuilder() {
        this( "true".equalsIgnoreCase( System.getProperty( "org.drools.classbuilder.debug" ) ) );
    }

    public DefaultBeanClassBuilder(final boolean debug) {
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
    public byte[] buildClass( ClassDefinition classDef ) throws IOException,
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

        this.buildFields( cw,
                classDef );

        if ( classDef.isTraitable() ) {
            this.buildDynamicPropertyMap( cw, classDef );
            this.buildTraitMap(cw, classDef);
        }

        this.buildConstructors( cw,
                classDef );

        this.buildGettersAndSetters( cw,
                classDef );

        this.buildEqualityMethods( cw,
                classDef );

        this.buildToString( cw,
                classDef );

        cw.visitEnd();

        byte[] serializedClass = cw.toByteArray();

        return serializedClass;
    }

    protected void buildGettersAndSetters(ClassWriter cw, ClassDefinition classDef) {
        // Building methods
        for ( FieldDefinition fieldDef : classDef.getFieldsDefinitions() ) {
            if (! fieldDef.isInherited()) {
                this.buildGetMethod( cw,
                        classDef,
                        fieldDef );
                this.buildSetMethod( cw,
                        classDef,
                        fieldDef );
            }
        }
    }

    protected void buildEqualityMethods(ClassWriter cw, ClassDefinition classDef) {
        this.buildEquals( cw,
                classDef );
        this.buildHashCode( cw,
                classDef );
    }

    protected void buildFields(ClassWriter cw, ClassDefinition classDef) {
        // Building fields
        for ( FieldDefinition fieldDef : classDef.getFieldsDefinitions() ) {
            if (! fieldDef.isInherited())
                this.buildField( cw,
                        fieldDef );
        }
    }


    protected void buildConstructors(ClassWriter cw, ClassDefinition classDef) {
        // Building default constructor
        try {
        this.buildDefaultConstructor( cw,
                classDef );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Building constructor with all fields
        if (classDef.getFieldsDefinitions().size() > 0) {
            this.buildConstructorWithFields( cw,
                    classDef,
                    classDef.getFieldsDefinitions() );
        }

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
    }


    /**
     * A traitable class is a special class with support for dynamic properties and types.
     *
     * This method builds the trait map, containing the references to the proxies
     * for each trait carried by an object at a given time.
     *
     * @param cw
     * @param classDef
     */
    protected void buildTraitMap(ClassWriter cw, ClassDefinition classDef) {

//        FieldVisitor fv = cw.visitField( ACC_PRIVATE,
//                                         TraitableBean.TRAITSET_FIELD_NAME,
//                                         "Ljava/util/Map;",
//                                         "Ljava/util/Map<Ljava/lang/String;+Lorg/drools/factmodel/traits/Thing;>;",
//                                         null);
//        fv.visitEnd();
//
//
//        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC,
//                "getTraits",
//                "()Ljava/util/Map;",
//                "()Ljava/util/Map<Ljava/lang/String;+Lorg/drools/factmodel/traits/Thing;>;",
//                null);
//
//        mv.visitCode();
//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( classDef.getName() ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
//        mv.visitInsn(ARETURN);
//        mv.visitMaxs(1, 1);
//        mv.visitEnd();


        FieldVisitor fv = cw.visitField( ACC_PRIVATE,
                TraitableBean.TRAITSET_FIELD_NAME,
                "Ljava/util/Map;",
                "Ljava/util/Map<Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;>;",
                null );
        fv.visitEnd();

        MethodVisitor mv;

        mv = cw.visitMethod(ACC_PROTECTED, "getTraitMap", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;>;", null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( classDef.getName() ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
        Label l0 = new Label();
        mv.visitJumpInsn(IFNONNULL, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, "java/util/HashMap");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
        mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( classDef.getName() ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( classDef.getName() ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(3, 1);
        mv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "setTraitMap", "(Ljava/util/Map;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( classDef.getName() ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "addTrait", "(Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;)V", "(Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;)V", null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( classDef.getName() ), "getTraitMap", "()Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(POP);
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "getTrait", "(Ljava/lang/String;)Lorg/drools/factmodel/traits/Thing;", "(Ljava/lang/String;)Lorg/drools/factmodel/traits/Thing;", null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( classDef.getName() ), "getTraitMap", "()Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, "org/drools/factmodel/traits/Thing");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "hasTrait", "(Ljava/lang/String;)Z", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( classDef.getName() ), "getTraitMap", "()Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z");
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "removeTrait", "(Ljava/lang/String;)Lorg/drools/factmodel/traits/Thing;", "(Ljava/lang/String;)Lorg/drools/factmodel/traits/Thing;", null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( classDef.getName() ), "getTraitMap", "()Ljava/util/Map;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, "org/drools/factmodel/traits/Thing");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();


        mv = cw.visitMethod(ACC_PUBLIC, "getTraits", "()Ljava/util/Collection;", "()Ljava/util/Collection<Ljava/lang/String;>;", null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( classDef.getName() ), "getTraitMap", "()Ljava/util/Map;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "keySet", "()Ljava/util/Set;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();



    }


    /**
     * A traitable class is a special class with support for dynamic properties and types.
     *
     * This method builds the property map, containing the key/values pairs to implement
     * any property defined in a trait interface but not supported by the traited class
     * fields.
     *
     * @param cw
     * @param def
     */
    protected void buildDynamicPropertyMap( ClassWriter cw, ClassDefinition def ) {

        FieldVisitor fv = cw.visitField( Opcodes.ACC_PRIVATE,
                TraitableBean.MAP_FIELD_NAME,
                "Ljava/util/Map;",
                "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;",
                null);
        fv.visitEnd();

        MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                "getDynamicProperties",
                "()Ljava/util/Map;",
                "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;",
                null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType(def.getName()), TraitableBean.MAP_FIELD_NAME, "Ljava/util/Map;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();



        mv = cw.visitMethod( ACC_PUBLIC,
                "setDynamicProperties",
                "(Ljava/util/Map;)V",
                "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V",
                null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType(def.getName()), TraitableBean.MAP_FIELD_NAME, "Ljava/util/Map;");
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

    }

    /**
     * Defines the class header for the given class definition
     *
     * @param cw
     * @param classDef
     */
    protected void buildClassHeader(ClassVisitor cw,
                                  ClassDefinition classDef) {
        String[] original = classDef.getInterfaces();
        String[] interfaces = new String[original.length];
        for ( int i = 0; i < original.length; i++ ) {
            interfaces[i] = BuildUtils.getInternalType( original[i] );
        }
        // Building class header
        cw.visit( Opcodes.V1_5,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                BuildUtils.getInternalType( classDef.getClassName() ),
                null,
                BuildUtils.getInternalType( classDef.getSuperClass() ),
                interfaces );

        buildClassAnnotations(classDef, cw);



        cw.visitSource( classDef.getClassName() + ".java",
                null );
    }



    /**
     * Creates the field defined by the given FieldDefinition
     *
     * @param cw
     * @param fieldDef
     */
    protected void buildField(ClassVisitor cw,
                            FieldDefinition fieldDef) {
        FieldVisitor fv;
        fv = cw.visitField( Opcodes.ACC_PRIVATE,
                fieldDef.getName(),
                BuildUtils.getTypeDescriptor( fieldDef.getTypeName() ),
                null,
                null );


        buildFieldAnnotations(fieldDef, fv);

        fv.visitEnd();
    }



    /**
     * Creates a default constructor for the class
     *
     * @param cw
     */
    protected void buildDefaultConstructor(ClassVisitor cw,
                                           ClassDefinition classDef) {


        MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
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

        boolean hasObjects = defaultConstructorStart( mv, classDef );

        mv.visitInsn(Opcodes.RETURN);
        Label l1 = null;
        if ( this.debug ) {
            l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitLocalVariable( "this",
                    BuildUtils.getTypeDescriptor( classDef.getClassName() ),
                    null,
                    l0,
                    l1,
                    0 );
        }
        mv.visitMaxs( hasObjects ? 3 : 0,
                hasObjects ? 1 : 0 );
        mv.visitEnd();

    }


    protected boolean defaultConstructorStart( MethodVisitor mv, ClassDefinition classDef ) {
        // Building default constructor

            mv.visitVarInsn( Opcodes.ALOAD,
                    0 );

            String sup = "";
            try {
                sup = Type.getInternalName(Class.forName(classDef.getSuperClass()));
            } catch (ClassNotFoundException e) {
                sup = BuildUtils.getInternalType( classDef.getSuperClass() );
            }
            mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                    sup,
                    "<init>",
                    Type.getMethodDescriptor( Type.VOID_TYPE,
                            new Type[]{} ) );

            boolean hasObjects = false;
            for (FieldDefinition field : classDef.getFieldsDefinitions()) {

                    Object val = BuildUtils.getDefaultValue(field);

                    if (val != null) {
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        if ( BuildUtils.isPrimitive( field.getTypeName() )
                             || BuildUtils.isBoxed( field.getTypeName() )
                             || String.class.getName().equals( field.getTypeName() ) ) {
                            mv.visitLdcInsn(val);
                            if ( BuildUtils.isBoxed(field.getTypeName()) ) {
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    BuildUtils.getInternalType(field.getTypeName()),
                                    "valueOf",
                                    "("+BuildUtils.unBox(field.getTypeName())+")"+BuildUtils.getTypeDescriptor(field.getTypeName()));
                            }
                        } else {
                            hasObjects = true;
                            String type = BuildUtils.getInternalType( val.getClass().getName() );
                            mv.visitTypeInsn( NEW, type );
                            mv.visitInsn(DUP);
                            mv.visitMethodInsn( INVOKESPECIAL,
                                                type,
                                                "<init>",
                                                "()V");
                        }


                        if (! field.isInherited()) {
                            mv.visitFieldInsn( Opcodes.PUTFIELD,
                                    BuildUtils.getInternalType( classDef.getClassName() ),
                                    field.getName(),
                                    BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                        } else {
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                    BuildUtils.getInternalType(classDef.getClassName()),
                                    field.getWriteMethod(),
                                    Type.getMethodDescriptor(Type.VOID_TYPE,
                                            new Type[]{Type.getType(BuildUtils.getTypeDescriptor(field.getTypeName()))}
                                    ));
                        }

                    }
            }


            if ( classDef.isTraitable() ) {
                initializeDynamicTypeStructures( mv, classDef );
            }

        return hasObjects;
    }


    /**
     * Initializes the trait map and dynamic property map to empty values
     * @param mv
     * @param classDef
     */
    protected void initializeDynamicTypeStructures( MethodVisitor mv, ClassDefinition classDef) {
        mv.visitVarInsn(ALOAD, 0);
                        mv.visitTypeInsn(NEW, "java/util/HashMap");
                        mv.visitInsn(DUP);
                        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
                        mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( classDef.getName() ), TraitableBean.MAP_FIELD_NAME, "Ljava/util/Map;");
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitTypeInsn(NEW, "java/util/HashMap");
                        mv.visitInsn(DUP);
                        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
                        mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( classDef.getName() ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");

    }

    /**
     * Creates a constructor that takes and assigns values to all
     * fields in the order they are declared.
     *
     * @param cw
     * @param classDef
     */
    protected void buildConstructorWithFields(ClassVisitor cw,
                                              ClassDefinition classDef,
                                              Collection<FieldDefinition> fieldDefs) {


        Type[] params = new Type[fieldDefs.size()];
        int index = 0;
        for ( FieldDefinition field : fieldDefs ) {
            params[index++] = Type.getType( BuildUtils.getTypeDescriptor( field.getTypeName() ) );
        }

        MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
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

        fieldConstructorStart( mv, classDef, fieldDefs );

        mv.visitInsn( Opcodes.RETURN );
        Label l1 = null;
        if ( this.debug ) {
            l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitLocalVariable( "this",
                    BuildUtils.getTypeDescriptor( classDef.getClassName() ),
                    null,
                    l0,
                    l1,
                    0 );
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                Label l11 = new Label();
                mv.visitLabel( l11 );
                mv.visitLocalVariable( field.getName(),
                        BuildUtils.getTypeDescriptor( field.getTypeName() ),
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

    protected void fieldConstructorStart(MethodVisitor mv, ClassDefinition classDef, Collection<FieldDefinition> fieldDefs) {
        mv.visitVarInsn( Opcodes.ALOAD,
                0 );

        String sup = "";
        try {
            sup = Type.getInternalName(Class.forName(classDef.getSuperClass()));
        } catch (ClassNotFoundException e) {
            sup = BuildUtils.getInternalType( classDef.getSuperClass() );
        }

        mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                sup,
                "<init>",
                Type.getMethodDescriptor( Type.VOID_TYPE,
                        new Type[]{} ) );

        int index = 1; // local vars start at 1, as 0 is "this"
        for ( FieldDefinition field : fieldDefs ) {
            if ( this.debug ) {
                Label l11 = new Label();
                mv.visitLabel( l11 );
            }
            mv.visitVarInsn( Opcodes.ALOAD,
                    0 );
            mv.visitVarInsn( Type.getType( BuildUtils.getTypeDescriptor( field.getTypeName() ) ).getOpcode( Opcodes.ILOAD ),
                    index++ );
            if ( field.getTypeName().equals( "long" ) || field.getTypeName().equals( "double" ) ) {
                // long and double variables use 2 words on the variables table
                index++;
            }

            if (! field.isInherited()) {
                mv.visitFieldInsn( Opcodes.PUTFIELD,
                        BuildUtils.getInternalType( classDef.getClassName() ),
                        field.getName(),
                        BuildUtils.getTypeDescriptor( field.getTypeName() ) );
            } else {
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        BuildUtils.getInternalType(classDef.getClassName()),
                        field.getWriteMethod(),
                        Type.getMethodDescriptor(Type.VOID_TYPE,
                                new Type[]{Type.getType(BuildUtils.getTypeDescriptor(field.getTypeName()))}
                        ));
            }

        }

        if ( classDef.isTraitable() ) {
            initializeDynamicTypeStructures( mv, classDef );
        }

    }




    /**
     * Creates the set method for the given field definition
     *
     * @param cw
     * @param classDef
     * @param fieldDef
     */
    protected void buildSetMethod(ClassVisitor cw,
                                ClassDefinition classDef,
                                FieldDefinition fieldDef) {
        MethodVisitor mv;
        // set method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                    fieldDef.getWriteMethod(),
                    Type.getMethodDescriptor( Type.VOID_TYPE,
                            new Type[]{Type.getType( BuildUtils.getTypeDescriptor( fieldDef.getTypeName() ) )} ),
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
            mv.visitVarInsn( Type.getType( BuildUtils.getTypeDescriptor( fieldDef.getTypeName() ) ).getOpcode( Opcodes.ILOAD ),
                    1 );
            mv.visitFieldInsn( Opcodes.PUTFIELD,
                    BuildUtils.getInternalType( classDef.getClassName() ),
                    fieldDef.getName(),
                    BuildUtils.getTypeDescriptor( fieldDef.getTypeName() ) );

            mv.visitInsn( Opcodes.RETURN );
            Label l1 = null;
            if ( this.debug ) {
                l1 = new Label();
                mv.visitLabel( l1 );
                mv.visitLocalVariable( "this",
                        BuildUtils.getTypeDescriptor( classDef.getClassName() ),
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
    protected void buildGetMethod(ClassVisitor cw,
                                ClassDefinition classDef,
                                FieldDefinition fieldDef) {
        MethodVisitor mv;
        // Get method
        {
            mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                    fieldDef.getReadMethod(),
                    Type.getMethodDescriptor( Type.getType( BuildUtils.getTypeDescriptor( fieldDef.getTypeName() ) ),
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
                    BuildUtils.getInternalType( classDef.getClassName() ),
                    fieldDef.getName(),
                    BuildUtils.getTypeDescriptor( fieldDef.getTypeName() ) );
            mv.visitInsn( Type.getType( BuildUtils.getTypeDescriptor( fieldDef.getTypeName() ) ).getOpcode( Opcodes.IRETURN ) );
            Label l1 = null;
            if ( this.debug ) {
                l1 = new Label();
                mv.visitLabel( l1 );
                mv.visitLocalVariable( "this",
                        BuildUtils.getTypeDescriptor( classDef.getClassName() ),
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

    protected void buildEquals(ClassVisitor cw,
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
                    BuildUtils.getInternalType( classDef.getClassName() ) );
            mv.visitVarInsn( Opcodes.ASTORE,
                    2 );

            // for each key field
            int count = 0;
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                if ( field.isKey() ) {
                    count++;

                    Label goNext = new Label();

                    if ( BuildUtils.isPrimitive(field.getTypeName()) ) {
                        // if attr is primitive

                        // if ( this.<attr> != other.<booleanAttr> ) return false;
                        mv.visitVarInsn( Opcodes.ALOAD,
                                0 );


                        visitFieldOrGetter(mv, classDef, field);

                        mv.visitVarInsn(Opcodes.ALOAD,
                                2);

                        visitFieldOrGetter(mv, classDef, field);

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

                        visitFieldOrGetter(mv, classDef, field);

                        Label secondIfPart = new Label();
                        mv.visitJumpInsn( Opcodes.IFNONNULL,
                                secondIfPart );

                        // if ( other.objAttr != null ) return false;
                        mv.visitVarInsn( Opcodes.ALOAD,
                                2 );

                        visitFieldOrGetter(mv, classDef, field);

                        Label returnFalse = new Label();
                        mv.visitJumpInsn( Opcodes.IFNONNULL,
                                returnFalse );

                        mv.visitLabel( secondIfPart );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                0 );

                        visitFieldOrGetter(mv, classDef, field);

                        mv.visitJumpInsn( Opcodes.IFNULL,
                                goNext );

                        mv.visitVarInsn( Opcodes.ALOAD,
                                0 );

                        visitFieldOrGetter(mv, classDef, field);

                        mv.visitVarInsn( Opcodes.ALOAD,
                                2 );

                        visitFieldOrGetter(mv, classDef, field);

                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                BuildUtils.getInternalType( field.getTypeName() ),
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
                        BuildUtils.getTypeDescriptor( classDef.getClassName() ),
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
                        BuildUtils.getTypeDescriptor( classDef.getClassName() ),
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

    protected void buildHashCode(ClassVisitor cw,
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

                    mv.visitVarInsn(Opcodes.ALOAD,
                            0);

                    visitFieldOrGetter(mv, classDef, field);

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

                        visitFieldOrGetter(mv, classDef, field);

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
                    } else if ( !BuildUtils.isPrimitive(field.getTypeName()) ) {
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

                        visitFieldOrGetter(mv, classDef, field);

                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                BuildUtils.getInternalType( field.getTypeName() ),
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
                        BuildUtils.getTypeDescriptor( classDef.getClassName() ),
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

    protected void buildToString(ClassVisitor cw,
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
                    BuildUtils.getInternalType( classDef.getClassName() ),
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
                mv.visitVarInsn(Opcodes.ALOAD,
                        0);

                visitFieldOrGetter(mv,classDef,field);


                if ( BuildUtils.isPrimitive(field.getTypeName()) ) {
                    String type = field.getTypeName().matches( "(byte|short)" ) ? "int" : field.getTypeName();
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                            Type.getInternalName( StringBuilder.class ),
                            "append",
                            Type.getMethodDescriptor( Type.getType( StringBuilder.class ),
                                    new Type[]{Type.getType( BuildUtils.getTypeDescriptor( type ) )} ) );
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
                        BuildUtils.getTypeDescriptor( classDef.getClassName() ),
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




    protected void buildClassAnnotations(ClassDefinition classDef, ClassVisitor cw) {
        if (classDef.getAnnotations() != null) {
            for (AnnotationDefinition ad : classDef.getAnnotations()) {
                AnnotationVisitor av = cw.visitAnnotation("L"+BuildUtils.getInternalType(ad.getName())+";", true);
                for (String key : ad.getValues().keySet()) {
                    AnnotationDefinition.AnnotationPropertyVal apv = ad.getValues().get(key);

                    switch (apv.getValType()) {
                        case STRINGARRAY:
                            AnnotationVisitor subAv = av.visitArray(apv.getProperty());
                            Object[] array = (Object[]) apv.getValue();
                            for (Object o : array) {
                                subAv.visit(null,o);
                            }
                            subAv.visitEnd();
                            break;
                        case PRIMARRAY:
                            av.visit(apv.getProperty(),apv.getValue());
                            break;
                        case ENUMARRAY:
                            AnnotationVisitor subEnav = av.visitArray(apv.getProperty());
                            Enum[] enArray = (Enum[]) apv.getValue();
                            String aenumType = "L" + BuildUtils.getInternalType(enArray[0].getClass().getName()) + ";";
                            for (Enum enumer : enArray) {
                                subEnav.visitEnum(null,aenumType,enumer.name());
                            }
                            subEnav.visitEnd();
                            break;
                        case CLASSARRAY:
                            AnnotationVisitor subKlav = av.visitArray(apv.getProperty());
                            Class[] klarray = (Class[]) apv.getValue();
                            for (Class klass : klarray) {
                                subKlav.visit(null,Type.getType("L"+BuildUtils.getInternalType(klass.getName())+";"));
                            }
                            subKlav.visitEnd();
                            break;
                        case ENUMERATION:
                            String enumType = "L" + BuildUtils.getInternalType(apv.getType().getName()) + ";";
                            av.visitEnum(apv.getProperty(),enumType,((Enum) apv.getValue()).name());
                            break;
                        case KLASS:
                            String klassName = BuildUtils.getInternalType(((Class) apv.getValue()).getName());
                            av.visit(apv.getProperty(),Type.getType("L"+klassName+";"));
                            break;
                        case PRIMITIVE:
                            av.visit(apv.getProperty(),apv.getValue());
                            break;
                        case STRING:
                            av.visit(apv.getProperty(),apv.getValue());
                            break;
                    }

                }
                av.visitEnd();
            }
        }
    }




    protected void buildFieldAnnotations(FieldDefinition fieldDef, FieldVisitor fv) {
        if (fieldDef.getAnnotations() != null) {
            for (AnnotationDefinition ad : fieldDef.getAnnotations()) {
                AnnotationVisitor av = fv.visitAnnotation("L"+BuildUtils.getInternalType(ad.getName())+";", true);
                for (String key : ad.getValues().keySet()) {
                    AnnotationDefinition.AnnotationPropertyVal apv = ad.getValues().get(key);

                    switch (apv.getValType()) {
                        case STRINGARRAY:
                            AnnotationVisitor subAv = av.visitArray(apv.getProperty());
                            Object[] array = (Object[]) apv.getValue();
                            for (Object o : array) {
                                subAv.visit(null,o);
                            }
                            subAv.visitEnd();
                            break;
                        case PRIMARRAY:
                            av.visit(apv.getProperty(),apv.getValue());
                            break;
                        case ENUMARRAY:
                            AnnotationVisitor subEnav = av.visitArray(apv.getProperty());
                            Enum[] enArray = (Enum[]) apv.getValue();
                            String aenumType = "L" + BuildUtils.getInternalType(enArray[0].getClass().getName()) + ";";
                            for (Enum enumer : enArray) {
                                subEnav.visitEnum(null,aenumType,enumer.name());
                            }
                            subEnav.visitEnd();
                            break;
                        case CLASSARRAY:
                            AnnotationVisitor subKlav = av.visitArray(apv.getProperty());
                            Class[] klarray = (Class[]) apv.getValue();
                            for (Class klass : klarray) {
                                subKlav.visit(null,Type.getType("L"+BuildUtils.getInternalType(klass.getName())+";"));
                            }
                            subKlav.visitEnd();
                            break;
                        case ENUMERATION:
                            String enumType = "L" + BuildUtils.getInternalType(apv.getType().getName()) + ";";
                            av.visitEnum(apv.getProperty(),enumType,((Enum) apv.getValue()).name());
                            break;
                        case KLASS:
                            String klassName = BuildUtils.getInternalType(((Class) apv.getValue()).getName());
                            av.visit(apv.getProperty(),Type.getType("L"+klassName+";"));
                            break;
                        case PRIMITIVE:
                            av.visit(apv.getProperty(),apv.getValue());
                            break;
                        case STRING:
                            av.visit(apv.getProperty(),apv.getValue());
                            break;
                    }
                }
                av.visitEnd();
            }
        }
    }





    protected void visitFieldOrGetter(MethodVisitor mv, ClassDefinition classDef, FieldDefinition field) {
        if (! field.isInherited()) {
            mv.visitFieldInsn( Opcodes.GETFIELD,
                    BuildUtils.getInternalType( classDef.getClassName() ),
                    field.getName(),
                    BuildUtils.getTypeDescriptor( field.getTypeName() ) );
        } else {
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                    BuildUtils.getInternalType( classDef.getClassName()),
                    field.getReadMethod(),
                    Type.getMethodDescriptor(Type.getType(BuildUtils.getTypeDescriptor(field.getTypeName())),
                            new Type[]{})
            );
        }
    }
   










   
}

