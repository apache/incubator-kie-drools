/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.factmodel.traits;

import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Type;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.drools.core.rule.builder.dialect.asm.ClassGenerator.createClassWriter;

public class TraitTriplePropertyWrapperClassBuilderImpl implements TraitPropertyWrapperClassBuilder, Serializable {


    private transient ClassDefinition trait;

    private transient TraitRegistry traitRegistry;

    protected ClassDefinition getTrait() {
        return trait;
    }

    public void init( ClassDefinition trait, TraitRegistry traitRegistry ) {
        this.trait = trait;
        this.traitRegistry = traitRegistry;
    }


    public byte[] buildClass( ClassDefinition core, ClassLoader classLoader ) throws IOException,
            SecurityException,
            IllegalArgumentException,
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            NoSuchFieldException {


        FieldVisitor fv;
        MethodVisitor mv;

        // get the method bitmask
        BitSet mask = traitRegistry.getFieldMask(trait.getName(), core.getDefinedClass().getName());

        String name = TraitFactory.getPropertyWrapperName( trait, core );
        String masterName = TraitFactory.getProxyName(trait, core);


        String internalWrapper  = BuildUtils.getInternalType( name );
        String descrCore        = Type.getDescriptor( core.getDefinedClass() );
        String internalCore     = Type.getInternalName( core.getDefinedClass() );


        ClassWriter cw = createClassWriter( classLoader,
                                            ACC_PUBLIC + ACC_SUPER,
                                            internalWrapper,
                                            null,
                                            Type.getInternalName( TripleBasedStruct.class ),
                                            new String[] { Type.getInternalName( Serializable.class ) } );

        cw.visitInnerClass( Type.getInternalName( Map.Entry.class ),
                            Type.getInternalName( Map.class ), 
                            "Entry", 
                            ACC_PUBLIC + ACC_STATIC + ACC_ABSTRACT + ACC_INTERFACE );


        {
            fv = cw.visitField(0, "object", descrCore, null, null);
            fv.visitEnd();
        }


        {
            mv = cw.visitMethod(ACC_PUBLIC,
                    "<init>",
                    "()V",
                    null,
                    null);

            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL, 
                                Type.getInternalName( TripleBasedStruct.class ),
                                "<init>", 
                                "()V" );
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitMethodInsn(INVOKESPECIAL, internalWrapper, "initSoftFields", "()V");
            mv.visitInsn( RETURN );
//            mv.visitMaxs( 1, 1 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();


        }


        {
            mv = cw.visitMethod(ACC_PUBLIC,
                    "<init>",
                    "(" + 
                            descrCore + 
                            Type.getDescriptor( TripleStore.class ) +
                            Type.getDescriptor( TripleFactory.class ) +
                    ")V",
                    null,
                    null);


            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );

            mv.visitMethodInsn( INVOKESPECIAL, 
                                Type.getInternalName( TripleBasedStruct.class ),
                                "<init>", 
                                "()V" );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitFieldInsn( PUTFIELD, internalWrapper, "object", descrCore );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitFieldInsn( PUTFIELD, internalWrapper, "store", Type.getDescriptor( TripleStore.class ) );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 3 );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    Type.getInternalName( TripleBasedStruct.class ),
                    "setTripleFactory",
                    "(" + Type.getDescriptor( TripleFactory.class ) + ")V" );
            

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL, 
                                Type.getInternalName( TripleStore.class ),
                                "getId", 
                                "()" + Type.getDescriptor( String.class ) );
            mv.visitFieldInsn( PUTFIELD, 
                               internalWrapper, 
                               "storeId", 
                               Type.getDescriptor( String.class ) );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL, internalWrapper, "initSoftFields", "()V" );
            mv.visitInsn( RETURN );
//            mv.visitMaxs( 2, 4 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();


        }

        buildInitSoftFields( cw, internalWrapper, trait, core, mask );

        buildClearSoftFields(cw, internalWrapper, trait, mask);

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

        buildExtensionMethods( cw, name, core );

        cw.visitEnd();

        return cw.toByteArray();
    }




    protected void invokeRemove( MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field ) {
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, 
                            Type.getInternalName( String.class ), 
                            "equals", 
                            "("+ Type.getDescriptor( Object.class ) + ")Z" );
        Label l1 = new Label();
        mv.visitJumpInsn( IFEQ, l1 );

        TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

        if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
            TraitFactory.valueOf( mv, field.getTypeName() );
        }
        mv.visitVarInsn(ASTORE, 2);

        TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, true, 1);

        mv.visitVarInsn( ALOAD, 2 );
        mv.visitInsn( ARETURN );
        mv.visitLabel( l1 );
    }
    
    
    protected void buildRemove( ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, 
                                           "remove", 
                                           "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), 
                                           null, 
                                           null );
        mv.visitCode();

        int stack = 0;
        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            stack = Math.max( stack, BuildUtils.sizeOf( field.getTypeName() ) );
            invokeRemove( mv, wrapperName, core, field.getName(), field );
        }


        int j = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            if ( isSoftField ) {
                stack = Math.max( stack, BuildUtils.sizeOf( field.getTypeName() ) );

                mv.visitLdcInsn( field.getName() );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( String.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
                Label l2 = new Label();
                mv.visitJumpInsn( IFEQ, l2 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, internalWrapper, "store", Type.getDescriptor( TripleStore.class ) );
                mv.visitVarInsn( ALOAD, 0);
                mv.visitLdcInsn( field.getName() );
                mv.visitMethodInsn( INVOKEVIRTUAL, 
                                    internalWrapper, 
                                    "propertyKey", 
                                    "(" + Type.getDescriptor( Object.class ) + ")" +  Type.getDescriptor( Triple.class ) );
                mv.visitMethodInsn( INVOKEVIRTUAL, 
                                    Type.getInternalName( TripleStore.class ),
                                    "get", 
                                    "(" + Type.getDescriptor( Triple.class ) + ")" + Type.getDescriptor( Triple.class ) );

                mv.visitVarInsn( ASTORE, 2 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitLdcInsn( field.getName() );
                mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );

                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    TraitFactory.valueOf( mv, field.getTypeName() );
                }
                mv.visitMethodInsn( INVOKESPECIAL, 
                                    Type.getInternalName( TripleBasedStruct.class ),
                                    "put", 
                                    "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
                mv.visitInsn( POP );
                mv.visitVarInsn( ALOAD, 2 );
                mv.visitInsn( ARETURN );
                mv.visitLabel( l2 );
            }
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKESPECIAL, 
                            Type.getInternalName( TripleBasedStruct.class ),
                            "remove", 
                            "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );

        mv.visitVarInsn( ASTORE, 2 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitInsn( ARETURN );
//        mv.visitMaxs( 2 + stack, 3 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }




    protected boolean mustSkip( FieldDefinition field ) {
        return false;
    }

    protected void buildInitSoftFields( ClassWriter cw, String wrapperName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {

        MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "initSoftFields", "()V", null, null);
        mv.visitCode();

        int stackSize = initSoftFields( mv, wrapperName, trait, core, mask );

        mv.visitInsn(RETURN);
//        mv.visitMaxs(4 + stackSize, 2);
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }

    protected int initSoftFields( MethodVisitor mv, String wrapperName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
        int j = 0;
        int stackSize = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            if ( mustSkip( field ) ) continue;
            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            if ( isSoftField ) {
                int size = initSoftField( mv, wrapperName, field, core, wrapperName );
                stackSize = Math.max( stackSize, size );
            }
        }
        return stackSize;
    }



    protected int initSoftField(MethodVisitor mv, String wrapperName, FieldDefinition field, ClassDefinition core, String internalWrapper ) {
        int size = 0;

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, 
                           wrapperName, 
                           "store", 
                           Type.getDescriptor( TripleStore.class ) );
        mv.visitVarInsn( ALOAD, 0);
        mv.visitLdcInsn( field.resolveAlias() );
        mv.visitMethodInsn( INVOKEVIRTUAL, 
                            wrapperName, 
                            "propertyKey", 
                            "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Triple.class ) );
        mv.visitMethodInsn( INVOKEVIRTUAL, 
                            Type.getInternalName(TripleStore.class),
                            "contains", 
                            "(" + Type.getDescriptor( Triple.class ) + ")Z" );
        Label l0 = new Label();
        mv.visitJumpInsn( IFNE, l0 );

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn(GETFIELD, wrapperName, "store", Type.getDescriptor( TripleStore.class ) );

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitLdcInsn( field.resolveAlias() );


        mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
        if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
            TraitFactory.valueOf( mv, field.getTypeName() );
            size = BuildUtils.sizeOf( field.getTypeName() );

        } else {
            size = 2;
        }
        mv.visitMethodInsn( INVOKEVIRTUAL,
                wrapperName,
                "property",
                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Triple.class ) );
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn( INVOKEVIRTUAL,
                Type.getInternalName( TripleStore.class ),
                "put",
                "(" + Type.getDescriptor( Triple.class ) + "Z)Z" );


        if ( core.isFullTraiting() ) {
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalWrapper, "object", Type.getDescriptor( core.getDefinedClass() ) );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitableBean.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( TraitableBean.class ), "_getFieldTMS", Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ), new Type[] {} ) );
            mv.visitVarInsn( ASTORE, 1 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitLdcInsn( field.resolveAlias() );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( TraitFieldTMS.class ), "isManagingField", Type.getMethodDescriptor( Type.BOOLEAN_TYPE, new Type[] { Type.getType( String.class ) } ) );
            Label l1 = new Label();
            mv.visitJumpInsn( IFNE, l1 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( core.getClassName() ) ) );
            mv.visitLdcInsn( field.resolveAlias() );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( TraitFieldTMS.class ), "registerField", Type.getMethodDescriptor( Type.VOID_TYPE, new Type[]{ Type.getType( Class.class ), Type.getType( String.class ) } ) );
            mv.visitLabel( l1 );
        }

        mv.visitInsn( POP );
        mv.visitLabel( l0 );

        return size;
    }

    
    protected void buildClear( ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        boolean hasPrimitiveFields = false;
        boolean hasObjectFields = false;
        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "clear", "()V", null, null );
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

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TripleBasedStruct.class ), "clear", "()V" );


        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKESPECIAL, internalWrapper, "clearSoftFields", "()V" );

        mv.visitInsn( RETURN );
//        mv.visitMaxs( stack , 1 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();


    }





    protected void buildClearSoftFields( ClassWriter cw, String wrapperName, ClassDefinition trait, BitSet mask ) {

        MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, "clearSoftFields", "()V", null, null );
        mv.visitCode();

        int j = 0;
        int stackSize = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            if ( isSoftField ) {
                int size = clearSoftField(mv, wrapperName, field);
                stackSize = Math.max( stackSize, size );
            }
        }
        mv.visitInsn( RETURN );
//        mv.visitMaxs( 4 + stackSize, 2 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }

    protected int clearSoftField(MethodVisitor mv, String wrapperName, FieldDefinition field ) {
        int size = 0;

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, wrapperName, "store", Type.getDescriptor( TripleStore.class ) );

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitLdcInsn( field.getName() );


        mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
        if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
            TraitFactory.valueOf( mv, field.getTypeName() );
            size = BuildUtils.sizeOf( field.getTypeName() );

        } else {
            size = 2;
        }
        mv.visitMethodInsn( INVOKEVIRTUAL,
                wrapperName,
                "property",
                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Triple.class ) );
        mv.visitInsn( ICONST_1 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                Type.getInternalName( TripleStore.class ),
                "put",
                "(" + Type.getDescriptor( Triple.class ) + "Z)Z" );
        mv.visitInsn( POP );

        return size;
    }









    protected void buildContainsValue( ClassWriter cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "containsValue", "(" + Type.getDescriptor( Object.class ) + ")Z", null, null );
        mv.visitCode();

        boolean hasNillable = false;
        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
                hasNillable = true;
            }
        }
        Label l99 = null;
        if ( hasNillable ) {
            mv.visitVarInsn(ALOAD, 1);
            l99 = new Label();
            mv.visitJumpInsn(IFNONNULL, l99);
        }

        int j = 0;
        int N = core.getFieldsDefinitions().size();
        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            j++;
            if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );
                    Label l1 = new Label();
                    mv.visitJumpInsn( IFNONNULL, l1 );
                    mv.visitInsn( ICONST_1 );
                    mv.visitInsn( IRETURN );
                    mv.visitLabel( l1 );

            }
        }
        if ( hasNillable ) {
            mv.visitLabel( l99 );
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKESPECIAL,
                            Type.getInternalName( TripleBasedStruct.class ), 
                            "containsValue", 
                            "(" + Type.getDescriptor( Object.class ) + ")Z" );

        mv.visitInsn( IRETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }
    
    
    protected void invokeContainsKey( MethodVisitor mv, String fieldName ) {
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                            Type.getInternalName( String.class ),
                            "equals", 
                            "(" + Type.getDescriptor( Object.class ) + ")Z" );
        Label l0 = new Label();
        mv.visitJumpInsn( IFEQ, l0 );
        mv.visitInsn( ICONST_1 );
        mv.visitInsn( IRETURN );
        mv.visitLabel( l0 );
    }

    protected void buildContainsKey(ClassWriter cw, String name, String className, ClassDefinition trait, ClassDefinition core, BitSet mask) {

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, 
                                           "containsKey", 
                                           "(" + Type.getDescriptor( Object.class ) + ")Z", 
                                           null, 
                                           null );
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            invokeContainsKey( mv, field.getName() );
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKESPECIAL, 
                            Type.getInternalName( TripleBasedStruct.class ),
                            "containsKey",
                            "(" + Type.getDescriptor( Object.class ) + ")Z" );

        mv.visitInsn( IRETURN );
//        mv.visitMaxs( 2, 2 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }


    protected void buildSize( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "size", "()I", null, null);
        mv.visitCode();
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKESPECIAL, 
                            Type.getInternalName( TripleBasedStruct.class ), 
                            "size", 
                            "()I" );

        int n = core.getFieldsDefinitions().size();
        for ( int j = 0; j < n; j++ ) {
            mv.visitInsn( ICONST_1 );
            mv.visitInsn( IADD );
        }

        mv.visitInsn(IRETURN);
//        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 2 : 1,
//                1 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }


    protected void buildIsEmpty( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {

        boolean hasHardFields = core.getFieldsDefinitions().size() > 0;

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "isEmpty", "()Z", null, null );
        mv.visitCode();

        if ( ! hasHardFields ) {
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL,
                                Type.getInternalName( TripleBasedStruct.class ), 
                                "isEmpty", 
                                "()Z" );
        } else {
            mv.visitInsn( ICONST_0 );
        }
        mv.visitInsn( IRETURN );
//        mv.visitMaxs( 1, 1 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }





    
    protected void invokeGet( MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field) {
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( ALOAD, 1);
        mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/String", "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
        Label l0 = new Label();
        mv.visitJumpInsn( IFEQ, l0 );

        TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

        if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
            TraitFactory.valueOf( mv, field.getTypeName() );
        }
        mv.visitInsn( ARETURN );
        mv.visitLabel( l0 );
    }



    protected void buildGet( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, 
                                           "get",
                                            "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
                                            null, 
                                            null );
        mv.visitCode();

        if ( core.getFieldsDefinitions().size() > 0 ) {
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                invokeGet( mv, wrapperName, core, field.getName(), field );
            }
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKESPECIAL, 
                            Type.getInternalName( TripleBasedStruct.class ),
                            "get", 
                            "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );

        mv.visitInsn( ARETURN );
//        mv.visitMaxs( 2, 2 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }



    protected void invokePut( MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field ) {
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( String.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z" );
        Label l1 = new Label();
        mv.visitJumpInsn( IFEQ, l1 );
        
        mv.visitVarInsn( ALOAD, 2 );
        if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
            TraitFactory.primitiveValue( mv, field.getTypeName() );
            mv.visitVarInsn( BuildUtils.storeType( field.getTypeName() ), 3 );
            TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, false, 3 );
        } else {
            TraitFactory.invokeInjector( mv, wrapperName, trait, core, field, false, 2 );
        }

        mv.visitVarInsn( ALOAD, 2 );
        mv.visitInsn( ARETURN );
        mv.visitLabel( l1 );
    }
    
    protected void buildPut( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, 
                                           "put", 
                                            "(" + Type.getDescriptor( String.class) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
                                            null, 
                                            null );
        mv.visitCode();

        if ( core.getFieldsDefinitions().size() > 0) {
            int j = 0;
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                invokePut( mv, wrapperName, core, field.getName(), field );   
            }
        }


        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitMethodInsn( INVOKESPECIAL,
                            Type.getInternalName( TripleBasedStruct.class ),
                            "put",
                            "(" + Type.getDescriptor( String.class) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
        mv.visitInsn( ARETURN );
//        mv.visitMaxs( 4, 5 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }
    


    protected void buildEntryset( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, 
                                           "entrySet", 
                                           "()" + Type.getDescriptor( Set.class ),
                                           "()Ljava/util/Set<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;>;", 
                                           null );
        mv.visitCode();
        mv.visitTypeInsn( NEW, Type.getInternalName( HashSet.class ) );
        mv.visitInsn( DUP );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashSet.class ), "<init>", "()V" );
        mv.visitVarInsn( ASTORE, 1 );

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitLdcInsn( field.getName() );

            TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.valueOf( mv, field.getTypeName() );
            }

            mv.visitMethodInsn( INVOKESTATIC, 
                                Type.getInternalName( TraitProxy.class ), 
                                "buildEntry", 
                                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Map.Entry.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE,
                                Type.getInternalName( Set.class ),
                                "add", 
                                "(" + Type.getDescriptor( Object.class ) + ")Z" );
            mv.visitInsn(POP);
        }

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKESPECIAL,
                            Type.getInternalName( TripleBasedStruct.class ), 
                            "entrySet", 
                            "()" + Type.getDescriptor( Set.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( Set.class ),
                            "addAll", 
                            "(" + Type.getDescriptor( Collection.class ) + ")Z" );                       
        mv.visitInsn( POP );

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ARETURN );
//        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ?  4 : 2, 2 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }


    protected void buildKeyset( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, 
                                           "keySet", 
                                           "()" + Type.getDescriptor( Set.class ), 
                                           "()Ljava/util/Set<Ljava/lang/String;>;", 
                                           null);
        mv.visitCode();
        mv.visitTypeInsn( NEW, Type.getInternalName( HashSet.class ) );
        mv.visitInsn( DUP );
        mv.visitMethodInsn( INVOKESPECIAL,
                            Type.getInternalName( HashSet.class ),
                            "<init>", 
                             "()V" );
        mv.visitVarInsn( ASTORE, 1 );

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitLdcInsn( field.getName() );
            mv.visitMethodInsn( INVOKEINTERFACE, 
                                Type.getInternalName( Set.class ), 
                                "add", 
                                "(" + Type.getDescriptor( Object.class ) + ")Z" );
            mv.visitInsn( POP );
        }

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKESPECIAL, 
                            Type.getInternalName( TripleBasedStruct.class ), 
                            "keySet", 
                            "()" + Type.getDescriptor( Set.class ));
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( Set.class ),
                            "addAll", 
                            "(" + Type.getDescriptor( Collection.class ) + ")Z" );
        mv.visitInsn( POP );

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ARETURN );
//        mv.visitMaxs( 2, 2 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }






    protected void buildValues( ClassVisitor cw, String wrapperName, String coreName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, 
                                           "values", 
                                           "()" + Type.getDescriptor( Collection.class ), 
                                           "()Ljava/util/Collection<Ljava/lang/Object;>;", 
                                           null );
        mv.visitCode();

        mv.visitTypeInsn( NEW, Type.getInternalName( ArrayList.class ) );
        mv.visitInsn( DUP );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( ArrayList.class ), "<init>", "()V" );
        mv.visitVarInsn( ASTORE, 1 );


        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn( ALOAD, 1 );

            TraitFactory.invokeExtractor( mv, wrapperName, trait, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.valueOf( mv, field.getTypeName() );
            }

            mv.visitMethodInsn( INVOKEINTERFACE,
                                Type.getInternalName( Collection.class ),
                                "add", 
                                "(" + Type.getDescriptor( Object.class ) + ")Z" );
            mv.visitInsn( POP );
        }

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKESPECIAL, 
                            Type.getInternalName( TripleBasedStruct.class ), 
                            "values", 
                            "()" + Type.getDescriptor( Collection.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, 
                            Type.getInternalName( Collection.class ),
                            "addAll", 
                            "(" + Type.getDescriptor( Collection.class ) + ")Z" );
        mv.visitInsn( POP );

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ARETURN );

//        mv.visitMaxs( core.getFieldsDefinitions().size() > 0 ? 3 : 2, 2 );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }






















    public void buildCommonMethods( ClassVisitor cw, String wrapper ) {
        MethodVisitor mv;

        {
            mv = cw.visitMethod( ACC_PUBLIC, "toString", "()" + Type.getDescriptor( String.class ), null, null );
            mv.visitCode();
            mv.visitTypeInsn( NEW, Type.getInternalName( StringBuilder.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( StringBuilder.class ), "<init>", "()V" );
            mv.visitLdcInsn("[[");
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
                                "(" + Type.getDescriptor( String.class ) +")" + Type.getDescriptor( StringBuilder.class ) );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "entrySet", "()" + Type.getDescriptor( Set.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
                                "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( StringBuilder.class ) );
            mv.visitLdcInsn( "]]" );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
                                "(" + Type.getDescriptor( String.class ) +")" + Type.getDescriptor( StringBuilder.class ));
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "toString", "()" + Type.getDescriptor( String.class ) );
            mv.visitInsn( ARETURN );
//            mv.visitMaxs( 2, 1 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "put", 
                                "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), 
                                null, null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( String.class ) );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "put", 
                                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
            mv.visitInsn( ARETURN);
//            mv.visitMaxs( 3, 3 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }


    }



    protected void buildSpecificMethods(ClassWriter cw, String wrapper, ClassDefinition core) {
        MethodVisitor mv;

        {
            mv = cw.visitMethod( ACC_PUBLIC, "hashCode", "()I", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "object", BuildUtils.getTypeDescriptor( core.getName() ));
            mv.visitMethodInsn( INVOKEVIRTUAL, 
                                BuildUtils.getInternalType( wrapper ), 
                                "getTriplesForSubject", 
                                "(" + Type.getDescriptor( Object.class ) + ")" +  Type.getDescriptor( Collection.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "hashCode", "()I" );
            mv.visitInsn( IRETURN );
//            mv.visitMaxs( 2, 1 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PROTECTED, "getObject", "()" + Type.getDescriptor( Object.class ), null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "object", BuildUtils.getTypeDescriptor( core.getName() ) );
            mv.visitInsn( ARETURN );
//            mv.visitMaxs( 1, 1 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "setObject", "(" + Type.getDescriptor( Object.class ) + ")V", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( core.getName() ) );
            mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapper ), "object", BuildUtils.getTypeDescriptor( core.getName() ) );
            mv.visitInsn( RETURN );
//            mv.visitMaxs( 2, 2 );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

    }


    protected void buildExtensionMethods(ClassWriter cw, String name, ClassDefinition core) {

    }




}

