/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.factmodel.traits;

import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TraitTripleWrapperClassBuilderImpl implements TraitProxyClassBuilder {


    private ClassDefinition trait;

    public void init(ClassDefinition trait) {
        this.trait = trait;
    }



    public byte[] buildClass( ClassDefinition core ) throws IOException,
            IntrospectionException,
            SecurityException,
            IllegalArgumentException,
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            NoSuchFieldException {


        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        // get the method bitmask
        long mask = TraitRegistry.getInstance().getFieldMask(trait.getName(), core.getDefinedClass().getName());

        String name = TraitFactory.getPropertyWrapperName( trait, core );
        String masterName = TraitFactory.getProxyName(trait, core);


        String internalWrapper  = BuildUtils.getInternalType(name);
        String internalProxy    = BuildUtils.getInternalType(masterName);
        String descrWrapper     = BuildUtils.getTypeDescriptor(name);
        String descrProxy       = BuildUtils.getTypeDescriptor(masterName);

        String internalCore     = BuildUtils.getInternalType(core.getClassName());
        String descrCore        = BuildUtils.getTypeDescriptor(core.getClassName());
        String internalTrait    = BuildUtils.getInternalType(trait.getClassName());
        String descrTrait       = BuildUtils.getTypeDescriptor(trait.getClassName());


        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,
                internalWrapper,
                null,
                "org/drools/factmodel/traits/TripleBasedStruct",
                null );

        cw.visitInnerClass("java/util/Map$Entry", "java/util/Map", "Entry", ACC_PUBLIC + ACC_STATIC + ACC_ABSTRACT + ACC_INTERFACE);



        for ( FieldDefinition fld : core.getFieldsDefinitions() ) {
            fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, fld.getName()+"_reader", "Lorg/drools/spi/InternalReadAccessor;", null, null);
            fv.visitEnd();
            fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, fld.getName()+"_writer", "Lorg/drools/spi/WriteAccessor;", null, null);
            fv.visitEnd();
        }


        {
            fv = cw.visitField(0, "object", descrCore, null, null);
            fv.visitEnd();
        }


        {
            mv = cw.visitMethod(ACC_PUBLIC,
                    "<init>",
                    "(" +
                    descrCore +
                    "Lorg/drools/core/util/TripleStore;)V",
                    null,
                    null);


            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);

            mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "<init>", "()V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, internalWrapper, "object", descrCore );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(PUTFIELD, internalWrapper, "store", "Lorg/drools/core/util/TripleStore;");

            int stackSize = initSoftFields( mv, internalWrapper, trait, mask );

            mv.visitInsn(RETURN);
            mv.visitMaxs( 4 + stackSize,
                    3 );
            mv.visitEnd();

        }


        buildSize( cw, name, core.getClassName(), trait, core, mask );

        buildIsEmpty( cw, name, core.getClassName(), trait, core, mask );

        buildGet( cw, name, core.getClassName(), trait, core, mask );

        buildPut( cw, name, core.getClassName(), trait, core, mask );

        buildClear(cw, name, core.getClassName(), trait, core, mask);

        buildRemove(cw, name, core.getClassName(), trait, core, mask);

        buildContainsKey(cw, name, core.getClassName(), trait, core, mask);

        buildContainsValue(cw, name, core.getClassName(), trait, core, mask);

        buildKeyset(cw, name, core.getClassName(), trait, core, mask);

        buildValues(cw, name, core.getClassName(), trait, core, mask);

        buildEntryset(cw, name, core.getClassName(), trait, core, mask);

        buildCommonMethods( cw, name );

        buildSpecificMethods( cw, name, core );

        cw.visitEnd();

        return cw.toByteArray();
    }



    private void buildRemove(ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        boolean hasPrimitiveFields = false;
        boolean hasObjectFields = false;

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "remove", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();

        int stack = 0;
        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            stack = Math.max( stack, BuildUtils.sizeOf( field.getTypeName() ) );
            mv.visitLdcInsn( field.getName() );
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);

            TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.valueOf( mv, field.getTypeName() );
            }
            mv.visitVarInsn(ASTORE, 2);

            TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, true, 1);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l1);
        }

        int j = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = (mask & (1 << j++)) == 0;
            if ( isSoftField ) {
                stack = Math.max( stack, BuildUtils.sizeOf( field.getTypeName() ) );

                mv.visitLdcInsn( field.getName() );
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                Label l2 = new Label();
                mv.visitJumpInsn(IFEQ, l2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, internalWrapper, "store", "Lorg/drools/core/util/TripleStore;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitLdcInsn( field.getName() );
                mv.visitMethodInsn(INVOKEVIRTUAL, internalWrapper, "key", "(Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/core/util/TripleStore", "get", "(Lorg/drools/core/util/Triple;)Lorg/drools/core/util/Triple;");

                mv.visitVarInsn(ASTORE, 2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitLdcInsn( field.getName() );
                mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );

                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    TraitFactory.valueOf( mv, field.getTypeName() );
                }
                mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "put", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;");
                mv.visitInsn(POP);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l2);
            }
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");

        mv.visitVarInsn(ASTORE, 2);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitMaxs( 2+stack, 3 );
        mv.visitEnd();
    }




    private int initSoftFields( MethodVisitor mv, String wrapperName, ClassDefinition trait, long mask ) {

        int j = 0;
        int nonPrimitiveFields = 0;
        int stackSize = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = (mask & (1 << j++)) == 0;
            if ( isSoftField ) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, wrapperName, "store", "Lorg/drools/core/util/TripleStore;");
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitLdcInsn( field.getName() );
                mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    TraitFactory.valueOf( mv, field.getTypeName() );
                    int size = BuildUtils.sizeOf( field.getTypeName() );
                    stackSize = Math.max( stackSize, size );
                } else {
                    stackSize = Math.max( stackSize, 2 );
                }
                mv.visitMethodInsn( INVOKEVIRTUAL,
                                    wrapperName,
                                    "property",
                                    "(Ljava/lang/String;Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;");
                mv.visitMethodInsn( INVOKEVIRTUAL,
                                    "org/drools/core/util/TripleStore",
                                    "put",
                                    "(Lorg/drools/core/util/Triple;)Z");
                mv.visitInsn(POP);

            }
        }
        return stackSize;
    }



    private void buildClear(ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );


        boolean hasPrimitiveFields = false;
        boolean hasObjectFields = false;
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "clear", "()V", null, null);
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            if ( field.isKey() ) continue;
            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                hasPrimitiveFields = true;
            } else {
                hasObjectFields = true;
            }
            TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, true, 1 );
        }

        int stack = 2;
        if ( hasPrimitiveFields ) {
            stack++;
        }
        if ( hasObjectFields ) {
            stack++;
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "clear", "()V");

        int num = initSoftFields(mv, internalWrapper, trait, mask);
        stack += num;


        mv.visitInsn(RETURN);
        mv.visitMaxs( stack , 1 );
        mv.visitEnd();


    }










    private void buildContainsValue(ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "containsValue", "(Ljava/lang/Object;)Z", null, null);
        mv.visitCode();

        // null check
        mv.visitVarInsn(ALOAD, 1);
        Label l99 = new Label();
        mv.visitJumpInsn(IFNONNULL, l99);

        int j = 0;
        int N = core.getFieldsDefinitions().size();
        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            j++;
            if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );
                if ( j != N ) {
                    Label l1 = new Label();
                    mv.visitJumpInsn(IFNONNULL, l1);
                    mv.visitInsn(ICONST_1);
                    mv.visitInsn(IRETURN);
                    mv.visitLabel( l1 );
                } else {
                    mv.visitJumpInsn(IFNONNULL, l99);
                    mv.visitInsn(ICONST_1);
                    mv.visitInsn(IRETURN);
                    mv.visitLabel( l99 );
                }

            }
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "containsValue", "(Ljava/lang/Object;)Z");

        mv.visitInsn(IRETURN);
        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 3 : 2
                , 2);
        mv.visitEnd();

    }

    private void buildContainsKey(ClassWriter cw, String name, String className, ClassDefinition trait, ClassDefinition core, long mask) {
        String internalWrapper = BuildUtils.getInternalType( name );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "containsKey", "(Ljava/lang/Object;)Z", null, null);
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitLdcInsn( field.getName() );
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l0);
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "containsKey", "(Ljava/lang/Object;)Z");

        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }


    private void buildSize( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "size", "()I", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "size", "()I");

        int n = core.getFieldsDefinitions().size();
        for ( int j = 0; j < n; j++ ) {
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IADD);
        }

        mv.visitInsn(IRETURN);
        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 2 : 1,
                1 );
        mv.visitEnd();

    }


    private void buildIsEmpty( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        boolean hasHardFields = core.getFieldsDefinitions().size() > 0;

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "isEmpty", "()Z", null, null);
        mv.visitCode();

        if ( ! hasHardFields ) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn( INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "isEmpty", "()Z" );
        } else {
            mv.visitInsn( ICONST_0 );
        }
        mv.visitInsn( IRETURN );
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }








    private void buildGet( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();


        if ( core.getFieldsDefinitions().size() > 0) {
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                mv.visitLdcInsn( field.getName() );
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                Label l0 = new Label();
                mv.visitJumpInsn(IFEQ, l0);

                TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    TraitFactory.valueOf( mv, field.getTypeName() );
                }
                mv.visitInsn(ARETURN);
                mv.visitLabel(l0);
            }

        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");

        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }



    private void buildPut( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "put", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();

        if ( core.getFieldsDefinitions().size() > 0) {
            int j = 0;
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                mv.visitLdcInsn( field.getName() );
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                Label l1 = new Label();
                mv.visitJumpInsn(IFEQ, l1);


                mv.visitVarInsn(ALOAD, 2);
                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    TraitFactory.promote( mv, field.getTypeName() );
                    mv.visitVarInsn( BuildUtils.storeType( field.getTypeName() ), 3 );
                    TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, false, 3 );
                } else {
                    TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, false, 2 );
                }

                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l1);
            }

        }


        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "put", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(4,5);
        mv.visitEnd();
    }




    private void buildEntryset( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "entrySet", "()Ljava/util/Set;", "()Ljava/util/Set<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;>;", null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/util/HashSet");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V");
        mv.visitVarInsn(ASTORE, 1);

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn( field.getName() );

            TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.valueOf( mv, field.getTypeName() );
            }

            mv.visitMethodInsn(INVOKESTATIC, "org/drools/factmodel/traits/TraitProxy", "buildEntry", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/util/Map$Entry;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z");
            mv.visitInsn(POP);
        }

        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "entrySet", "()Ljava/util/Set;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "addAll", "(Ljava/util/Collection;)Z");                    
        mv.visitInsn(POP);

        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARETURN);
        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ?  4 : 2,
                2);
        mv.visitEnd();


    }


    private void buildKeyset( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "keySet", "()Ljava/util/Set;", "()Ljava/util/Set<Ljava/lang/String;>;", null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/util/HashSet");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V");
        mv.visitVarInsn(ASTORE, 1);

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn( field.getName() );
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z");
            mv.visitInsn(POP);
        }

        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "keySet", "()Ljava/util/Set;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "addAll", "(Ljava/util/Collection;)Z");
        mv.visitInsn(POP);
        
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

            
         



    private void buildValues( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, long mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );
        String internalCore = BuildUtils.getInternalType( coreName );
        String descrCore = BuildUtils.getTypeDescriptor( coreName );

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "values", "()Ljava/util/Collection;", "()Ljava/util/Collection<Ljava/lang/Object;>;", null);
        mv.visitCode();

        mv.visitTypeInsn(NEW, "java/util/ArrayList");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
        mv.visitVarInsn(ASTORE, 1);


        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn(ALOAD, 1);

            TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.valueOf( mv, field.getTypeName() );
            }

            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "add", "(Ljava/lang/Object;)Z");
            mv.visitInsn(POP);
        }

        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "org/drools/factmodel/traits/TripleBasedStruct", "values", "()Ljava/util/Collection;");
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "addAll", "(Ljava/util/Collection;)Z");
        mv.visitInsn(POP);
        
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARETURN);

        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 3 : 2,
                2);
        mv.visitEnd();
    }



      
    

            















    public void buildCommonMethods( ClassVisitor cw, String wrapper ) {
        MethodVisitor mv;
        

        {
            mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
            mv.visitLdcInsn("[[");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "entrySet", "()Ljava/util/Set;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
            mv.visitLdcInsn("]]");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "put", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }

    }



    private void buildSpecificMethods(ClassWriter cw, String wrapper, ClassDefinition core) {
        MethodVisitor mv;

         {
            mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapper ), "object", BuildUtils.getTypeDescriptor( core.getName() ));
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "getTriplesForSubject", "(Ljava/lang/Object;)Ljava/util/Collection;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I");
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PROTECTED, "getObject", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapper ), "object", BuildUtils.getTypeDescriptor( core.getName() ));
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

    }









}

