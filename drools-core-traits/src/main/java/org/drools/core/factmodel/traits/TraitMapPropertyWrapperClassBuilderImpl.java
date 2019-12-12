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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Type;

import static org.drools.core.rule.builder.dialect.asm.ClassGenerator.createClassWriter;

public class TraitMapPropertyWrapperClassBuilderImpl extends AbstractPropertyWrapperClassBuilderImpl implements TraitPropertyWrapperClassBuilder, Serializable {
	
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
        BitSet mask = traitRegistry.getFieldMask( trait.getName(), core.getDefinedClass().getName() );

        String name = TraitFactory.getPropertyWrapperName(trait, core);


        String internalWrapper  = BuildUtils.getInternalType( name );
        String descrCore        = Type.getDescriptor( core.getDefinedClass() );
        String internalCore     = Type.getInternalName( core.getDefinedClass() );



        ClassWriter cw = createClassWriter( classLoader,
                                            ACC_PUBLIC + ACC_SUPER,
                                            internalWrapper,
                                            Type.getDescriptor( Object.class ) + Type.getDescriptor( Map.class ) + Type.getDescriptor( MapWrapper.class ),
                            //                "Ljava/lang/Object;Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Lorg/drools/factmodel/traits/MapWrapper;",
                                            Type.getInternalName( Object.class ),
                                            new String[]{ Type.getInternalName( Map.class ), Type.getInternalName( MapWrapper.class ), Type.getInternalName( Serializable.class ) } );

        cw.visitInnerClass( Type.getInternalName( Map.Entry.class ), Type.getInternalName( Map.class ), "Entry", ACC_PUBLIC + ACC_STATIC + ACC_ABSTRACT + ACC_INTERFACE );

        {
            fv = cw.visitField( 0, "object", descrCore, null, null );
            fv.visitEnd();
        }

        {
            fv = cw.visitField( 0, "map", Type.getDescriptor( Map.class ), "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null );
            fv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "<init>",                                        
                    "(" + descrCore + Type.getDescriptor( Map.class ) + ")V",
                    "(" + descrCore + "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V",
                    null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( Object.class ), "<init>", "()V", false );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitFieldInsn( PUTFIELD, internalWrapper, "object", descrCore );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitFieldInsn( PUTFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );

            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL, internalCore, "_setDynamicProperties", "(" + Type.getDescriptor( Map.class ) + ")V", false );

            initSoftFields( mv, trait, core, internalWrapper, mask, 2 );

            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();

        }


        buildSize( cw, name, core );

        buildIsEmpty( cw, name, core );

        buildGet( cw, name, core );

        buildPut( cw, name, core );

        buildClear( cw, name, trait, core, mask);

        buildRemove( cw, name, trait, core, mask);

        buildContainsKey( cw, name, core );

        buildContainsValue( cw, name, trait, core );

        buildKeyset( cw, name, core );

        buildValues( cw, name, core );

        buildEntryset( cw, name, core );

        buildCommonMethods( cw, name );


        cw.visitEnd();

        return cw.toByteArray();
    }










    protected void buildRemove( ClassWriter cw, String wrapperName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "remove", "(" + Type.getDescriptor( Object.class ) +")" + Type.getDescriptor( Object.class ), null, null );
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            invokeRemove( mv, wrapperName, core, field.getName(), field );
        }


        int j = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            if ( isSoftField ) {
                mv.visitLdcInsn( field.getName() );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitMethodInsn( INVOKEVIRTUAL,
                                    Type.getInternalName( String.class ),
                                    "equals",
                                    "(" + Type.getDescriptor( Object.class ) + ")Z",
                                    false );
                Label l2 = new Label();
                mv.visitJumpInsn( IFEQ, l2 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
                mv.visitLdcInsn( field.getName() );
                mv.visitMethodInsn( INVOKEINTERFACE,
                                    Type.getInternalName( Map.class ),
                                    "get",
                                    "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
                                    true );
                mv.visitVarInsn( ASTORE, 2 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
                mv.visitLdcInsn( field.getName() );
                mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    TraitFactory.valueOf( mv, field.getTypeName() );
                }
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "put", 
                                    "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), true );
                mv.visitInsn( POP );
                mv.visitVarInsn( ALOAD, 2 );
                mv.visitInsn( ARETURN );
                mv.visitLabel( l2 );
            }
        }


        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( Map.class ),
                            "remove",
                            "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
                            true );
        mv.visitVarInsn( ASTORE, 2 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }




    protected void initSoftFields( MethodVisitor mv, ClassDefinition trait, ClassDefinition core, String internalWrapper, BitSet mask, int varNum ) {
        int j = 0;
        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            boolean isSoftField = TraitRegistry.isSoftField( field, j++, mask );
            if ( isSoftField ) {

                mv.visitVarInsn( ALOAD, varNum );
                mv.visitLdcInsn( field.resolveAlias() );
                mv.visitMethodInsn( INVOKEINTERFACE,
                        Type.getInternalName( Map.class ), "containsKey",
                        Type.getMethodDescriptor( Type.getType( boolean.class ), Type.getType( Object.class ) ), true );
                Label l0 = new Label();
                mv.visitJumpInsn( IFNE, l0 );

                mv.visitVarInsn( ALOAD, varNum );
                mv.visitLdcInsn( field.resolveAlias() );
                mv.visitInsn( BuildUtils.zero( field.getTypeName() ) );
                if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                    TraitFactory.valueOf( mv, field.getTypeName() );
                }
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "put",
                                    Type.getMethodDescriptor( Type.getType( Object.class ), Type.getType( Object.class ), Type.getType( Object.class ) ), true );
                mv.visitInsn( POP );

                if ( core.isFullTraiting() ) {
                    registerLogicalField( mv, internalWrapper, field, core );
                }

                mv.visitLabel( l0 );
            }
        }
    }



    protected void buildClear( ClassWriter cw, String wrapperName, ClassDefinition trait, ClassDefinition core, BitSet mask ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "clear", "()V", null, null );
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            if ( field.isKey() ) continue;
            TraitFactory.invokeInjector( mv, wrapperName, core, field, true, 1 );
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "clear", "()V", true );

        initSoftFields( mv, trait, core, internalWrapper, mask, 0 );

        mv.visitInsn( RETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }





    protected void buildContainsValue( ClassWriter cw, String wrapperName, ClassDefinition trait, ClassDefinition core ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "containsValue", "(" + Type.getDescriptor( Object.class ) + ")Z", null, null );
        mv.visitCode();


        // null check
        mv.visitVarInsn( ALOAD, 1 );
        Label l99 = new Label();
        mv.visitJumpInsn( IFNONNULL, l99 );

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            if ( ! BuildUtils.isPrimitive( field.getTypeName() ) ) {
            	extractAndTestNotNull( mv, wrapperName, core, field );
            }
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitInsn( ACONST_NULL );
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( Map.class ),
                            "containsValue",
                            "(" + Type.getDescriptor( Object.class ) + ")Z",
                            true );
        mv.visitInsn( IRETURN );
        mv.visitLabel( l99 );

        // non-null values check
        for ( FieldDefinition field : core.getFieldsDefinitions() )   {

            mv.visitVarInsn( ALOAD, 1 );

            TraitFactory.invokeExtractor(mv, wrapperName, core, field );

            if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
                TraitFactory.valueOf( mv, field.getTypeName() );
            }
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                Type.getInternalName( Object.class ),
                                "equals",
                                "(" + Type.getDescriptor( Object.class ) + ")Z",
                                false );

            Label l0 = new Label();
            mv.visitJumpInsn( IFEQ, l0 );
            mv.visitInsn( ICONST_1 );
            mv.visitInsn( IRETURN );
            mv.visitLabel( l0 );

        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( Map.class ),
                            "containsValue",
                            "(" + Type.getDescriptor( Object.class ) + ")Z",
                            true );
        mv.visitInsn( IRETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }



	protected void buildContainsKey( ClassWriter cw, String name, ClassDefinition core ) {
        String internalWrapper = BuildUtils.getInternalType( name );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "containsKey", "(" + Type.getDescriptor( Object.class ) + ")Z", null, null );
        mv.visitCode();

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            invokeContainsKey( mv, field.getName() );
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( Map.class ),
                            "containsKey",
                            "(" + Type.getDescriptor( Object.class ) + ")Z",
                            true );
        mv.visitInsn( IRETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }


    protected void buildSize( ClassVisitor cw, String wrapperName, ClassDefinition core ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "size", "()I", null, null );
        mv.visitCode();
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( Map.class ),
                            "size",
                            "()I",
                            true );

        int n = core.getFieldsDefinitions().size();
        for ( int j = 0; j < n; j++ ) {
            mv.visitInsn( ICONST_1 );
            mv.visitInsn( IADD );
        }

        mv.visitInsn( IRETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }


    protected void buildIsEmpty( ClassVisitor cw, String wrapperName, ClassDefinition core ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        boolean hasHardFields = core.getFieldsDefinitions().size() > 0;

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "isEmpty", "()Z", null, null );
        mv.visitCode();

        if ( ! hasHardFields ) {
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "isEmpty", "()Z", true );
        } else {
            mv.visitInsn( ICONST_0 );
        }
        mv.visitInsn( IRETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }






    protected void invokeGet( MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field ) {
        mv.visitLdcInsn( fieldName );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                            Type.getInternalName( String.class ),
                            "equals",
                            "(" + Type.getDescriptor( Object.class ) + ")Z",
                            false );
        Label l0 = new Label();
        mv.visitJumpInsn( IFEQ, l0 );

        TraitFactory.invokeExtractor(mv, wrapperName, core, field );

        if ( BuildUtils.isPrimitive( field.getTypeName() ) ) {
            TraitFactory.valueOf( mv, field.getTypeName() );
        }
        mv.visitInsn( ARETURN );
        mv.visitLabel( l0 );
    }


    protected void buildGet( ClassVisitor cw, String wrapperName, ClassDefinition core ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "get", "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), null, null );
        mv.visitCode();


        if ( core.getFieldsDefinitions().size() > 0) {
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                invokeGet( mv, wrapperName, core, field.getName(), field );
            }
        }

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEINTERFACE,
                            Type.getInternalName( Map.class ),
                            "get",
                            "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
                            true );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }



    protected void buildPut( ClassVisitor cw, String wrapperName, ClassDefinition core ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "put", "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), null, null );
        mv.visitCode();

        if ( core.getFieldsDefinitions().size() > 0) {
            for ( FieldDefinition field : core.getFieldsDefinitions() ) {
                invokePut( mv, wrapperName, core, field.getName(), field );
            }
        }


        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "put", 
                            "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), true );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }




    protected void buildEntryset( ClassVisitor cw, String wrapperName, ClassDefinition core ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "entrySet", "()" + Type.getDescriptor( Set.class ),
                                          "()Ljava/util/Set<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;>;", null);
        mv.visitCode();
        mv.visitTypeInsn( NEW, Type.getInternalName( HashSet.class ) );
        mv.visitInsn( DUP );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashSet.class ), "<init>", "()V", false );
        mv.visitVarInsn( ASTORE, 1 );

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
	        buildEntry( mv, field, wrapperName, core );
        }

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "entrySet", "()" + Type.getDescriptor( Set.class ), true );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Set.class ), "addAll", "(" + Type.getDescriptor( Collection.class )+ ")Z", true );
        mv.visitInsn( POP );

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();

    }


    protected void buildKeyset( ClassVisitor cw, String wrapperName, ClassDefinition core ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "keySet", "()" + Type.getDescriptor( Set.class ), "()Ljava/util/Set<Ljava/lang/String;>;", null );
        mv.visitCode();
        mv.visitTypeInsn( NEW, Type.getInternalName( HashSet.class ) );
        mv.visitInsn( DUP );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashSet.class ), "<init>", "()V", false );
        mv.visitVarInsn( ASTORE, 1 );

        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitLdcInsn( field.getName() );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Set.class ), "add", "(" + Type.getDescriptor( Object.class ) + ")Z", true );
            mv.visitInsn( POP );
        }

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "keySet", "()" + Type.getDescriptor( Set.class ), true );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Set.class ), "addAll", "(" + Type.getDescriptor( Collection.class ) + ")Z", true );
        mv.visitInsn( POP );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ARETURN );
        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }



    protected void buildValues( ClassVisitor cw, String wrapperName, ClassDefinition core ) {
        String internalWrapper = BuildUtils.getInternalType( wrapperName );

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "values", "()" + Type.getDescriptor( Collection.class ), "()Ljava/util/Collection<Ljava/lang/Object;>;", null );
        mv.visitCode();

        mv.visitTypeInsn( NEW, Type.getInternalName( ArrayList.class ) );
        mv.visitInsn( DUP );
        mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( ArrayList.class ), "<init>", "()V", false );
        mv.visitVarInsn( ASTORE, 1 );


        for ( FieldDefinition field : core.getFieldsDefinitions() ) {
            extractAndCollect( mv, wrapperName, field, core );
        }

        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD, internalWrapper, "map", Type.getDescriptor( Map.class ) );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "values", "()" + Type.getDescriptor( Collection.class ), true );
        mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Collection.class ), "addAll", "(" + Type.getDescriptor( Collection.class ) + ")Z", true );
        mv.visitInsn( POP );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ARETURN );

        mv.visitMaxs( 0, 0 );
        mv.visitEnd();
    }




















    public void buildCommonMethods( ClassVisitor cw, String wrapper ) {

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "put", 
                                               "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ),
                                               null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( String.class ) );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "put",
                                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), false );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "equals", "(" + Type.getDescriptor( Object.class ) + ")Z", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            Label l0 = new Label();
            mv.visitJumpInsn( IF_ACMPNE, l0 );
            mv.visitInsn( ICONST_1 );
            mv.visitInsn( IRETURN );
            mv.visitLabel( l0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( MapWrapper.class ) );
            mv.visitVarInsn( ASTORE, 2 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "map", Type.getDescriptor( Map.class ) );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( MapWrapper.class ), "getInnerMap", "()" + Type.getDescriptor( Map.class ), true );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "equals", "(" + Type.getDescriptor( Object.class ) + ")Z", false );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "hashCode", "()I", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "map", Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( Object.class ), "hashCode", "()I", false );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "getInnerMap", "()" + Type.getDescriptor( Map.class ), 
                                               "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapper ), "map", Type.getDescriptor( Map.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }


        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "putAll", "(" + Type.getDescriptor( Map.class ) + ")V", 
                                               "(Ljava/util/Map<+Ljava/lang/String;+Ljava/lang/Object;>;)V", 
                                               null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "keySet", "()" + Type.getDescriptor( Set.class ), true );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Set.class ), "iterator", "()" + Type.getDescriptor( Iterator.class ), true );
            mv.visitVarInsn( ASTORE, 2 );
            Label l0 = new Label();
            mv.visitLabel( l0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Iterator.class ), "hasNext", "()Z", true );
            Label l1 = new Label();
            mv.visitJumpInsn( IFEQ, l1 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Iterator.class ), "next", "()" + Type.getDescriptor( Object.class ), true );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( String.class ) );
            mv.visitVarInsn( ASTORE, 3 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 3 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 3 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "get",
                                "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), true );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "put",
                                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ), false );
            mv.visitInsn( POP );
            mv.visitJumpInsn( GOTO, l0 );
            mv.visitLabel( l1 );
            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "toString", "()" + Type.getDescriptor( String.class ), null, null );
            mv.visitCode();
            mv.visitTypeInsn( NEW, Type.getInternalName( StringBuilder.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( StringBuilder.class ), "<init>", "()V", false );
            mv.visitLdcInsn("[[");
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
                    "(" + Type.getDescriptor( String.class ) +")" + Type.getDescriptor( StringBuilder.class ), false );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapper ), "entrySet", "()" + Type.getDescriptor( Set.class ), false );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
                    "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( StringBuilder.class ), false );
            mv.visitLdcInsn( "]]" );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "append",
                    "(" + Type.getDescriptor( String.class ) +")" + Type.getDescriptor( StringBuilder.class ), false );
            mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( StringBuilder.class ), "toString", "()" + Type.getDescriptor( String.class ), false );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }




    }








}

