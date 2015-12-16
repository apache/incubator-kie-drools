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
import org.drools.core.factmodel.DefaultBeanClassBuilder;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.rule.builder.dialect.asm.ClassGenerator;
import org.kie.api.definition.type.FactField;
import org.mvel2.asm.AnnotationVisitor;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;
import org.mvel2.asm.Type;

import java.beans.IntrospectionException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.drools.core.rule.builder.dialect.asm.ClassGenerator.createClassWriter;

public class TraitCoreWrapperClassBuilderImpl implements TraitCoreWrapperClassBuilder, Serializable {


    public byte[] buildClass( ClassDefinition core, ClassLoader classLoader ) throws IOException,
            IntrospectionException,
            SecurityException,
            IllegalArgumentException,
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            NoSuchFieldException {


        Class coreKlazz = core.getDefinedClass();
        String coreName = coreKlazz.getName();
        String wrapperName = coreName + "Wrapper";

        FieldVisitor fv;
        MethodVisitor mv;

        ClassWriter cw = createClassWriter( classLoader,
                                            ACC_PUBLIC + ACC_SUPER,
                                            BuildUtils.getInternalType( wrapperName ),
                                            BuildUtils.getTypeDescriptor( coreName ) +
                                                    "Lorg/drools/factmodel/traits/CoreWrapper<" + BuildUtils.getTypeDescriptor( coreName ) + ">;",
                                            BuildUtils.getInternalType( coreName ),
                                            new String[] { Type.getInternalName( CoreWrapper.class ), Type.getInternalName( Externalizable.class ) } );

        {
            AnnotationVisitor av0 = cw.visitAnnotation( Type.getDescriptor( Traitable.class ), true );
            av0.visit( "logical", core.isFullTraiting() );
        }

        {
            fv = cw.visitField( ACC_PRIVATE, "core", BuildUtils.getTypeDescriptor( coreName ), null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField( ACC_PRIVATE, TraitableBean.MAP_FIELD_NAME, Type.getDescriptor( Map.class ), "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null );
            fv.visitEnd();
        }
        {
            fv = cw.visitField( ACC_PRIVATE, TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ), "Ljava/util/Map<Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;>;", null );
            fv.visitEnd();
        }
        {
            fv = cw.visitField( ACC_PRIVATE, TraitableBean.FIELDTMS_FIELD_NAME, Type.getDescriptor( TraitFieldTMS.class ), null, null );
            fv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "<init>", "()V", null, null );
            mv.visitCode();

            try {
                coreKlazz.getConstructor();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn( INVOKESPECIAL, BuildUtils.getInternalType( coreName ), "<init>", "()V" );
            } catch ( NoSuchMethodException nsme ) {
                Constructor con = coreKlazz.getConstructors()[ 0 ];
                Class[] params = con.getParameterTypes();

                mv.visitVarInsn( ALOAD, 0 );
                for ( Class param : params ) {
                    mv.visitInsn( BuildUtils.zero( param.getName() ) );
                }
                mv.visitMethodInsn( INVOKESPECIAL,
                                    BuildUtils.getInternalType( coreName ),
                                    "<init>",
                                    Type.getConstructorDescriptor( con ) );
            }


//            mv.visitVarInsn( ALOAD, 0 );
//            mv.visitTypeInsn( NEW, Type.getInternalName( HashMap.class ) );
//            mv.visitInsn( DUP );
//            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashMap.class ), "<init>", "()V" );
//            mv.visitFieldInsn( PUTFIELD,
//                    BuildUtils.getInternalType( wrapperName ),
//                    TraitableBean.MAP_FIELD_NAME,
//                    Type.getDescriptor( Map.class ) );

//            mv.visitVarInsn( ALOAD, 0 );
//            mv.visitTypeInsn( NEW, Type.getInternalName( VetoableTypedMap.class ) );
//            mv.visitInsn( DUP );
//            mv.visitTypeInsn( NEW, Type.getInternalName( HashMap.class ) );
//            mv.visitInsn( DUP );
//            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashMap.class ), "<init>", "()V" );
//            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( VetoableTypedMap.class ), "<init>", "(" + Type.getDescriptor( Map.class ) + ")V" );
//            mv.visitFieldInsn( PUTFIELD,
//                    BuildUtils.getInternalType( wrapperName ),
//                    TraitableBean.TRAITSET_FIELD_NAME,
//                    Type.getDescriptor( Map.class ) );

            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "getCore" ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "getCore", "()" + Type.getDescriptor( Object.class ), "()"+BuildUtils.getTypeDescriptor( coreName ), null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD,
                        BuildUtils.getInternalType( wrapperName ),
                        "core",
                        BuildUtils.getTypeDescriptor( coreName ));
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "_getDynamicProperties" ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "_getDynamicProperties", "()" + Type.getDescriptor( Map.class ), "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD,
                        BuildUtils.getInternalType( wrapperName ),
                        TraitableBean.MAP_FIELD_NAME,
                        Type.getDescriptor( Map.class ) );
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();

                mv = cw.visitMethod( ACC_PUBLIC,
                        "_setDynamicProperties",
                        "(" + Type.getDescriptor( Map.class ) + ")V",
                        "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V",
                        null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.MAP_FIELD_NAME, Type.getDescriptor( Map.class ) );
                mv.visitInsn( RETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "_getTraitMap" ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "_getTraitMap", "()" + Type.getDescriptor( Map.class ),
                        "()Ljava/util/Map<Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;>;", null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
                Label l0 = new Label();
                mv.visitJumpInsn( IFNULL, l0 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
                mv.visitFieldInsn( GETSTATIC, Type.getInternalName( Collections.class ), "EMPTY_MAP", Type.getDescriptor( Map.class ) );
                Label l1 = new Label();
                mv.visitJumpInsn( IF_ACMPNE, l1 );
                mv.visitLabel( l0 );

                mv.visitVarInsn( ALOAD, 0 );
                mv.visitTypeInsn( NEW, Type.getInternalName( TraitTypeMap.class ) );
                mv.visitInsn( DUP );
                mv.visitTypeInsn( NEW, Type.getInternalName( HashMap.class ) );
                mv.visitInsn( DUP );
                mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashMap.class ), "<init>", "()V" );
                mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TraitTypeMap.class ), "<init>", "(" + Type.getDescriptor( Map.class ) + ")V" );
                mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
                mv.visitLabel( l1 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "setTraitMap", Map.class ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "setTraitMap", "(Ljava/util/Map;)V", null, null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitTypeInsn( NEW, Type.getInternalName( TraitTypeMap.class ) );
                mv.visitInsn( DUP );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TraitTypeMap.class ), "<init>", "(" + Type.getDescriptor( Map.class ) + ")V" );
                mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
                mv.visitInsn( RETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "addTrait", String.class, Thing.class ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "addTrait",
                        "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Thing.class ) + ")V",
                        "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Thing.class ) + ")V",
                        null);
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "_getTraitMap", "()" + Type.getDescriptor( Map.class ) );
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitTypeMap.class ) );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitVarInsn( ALOAD, 2 );
                mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TraitTypeMap.class ), "putSafe",
                        "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Thing.class ) + ")" + Type.getDescriptor( Thing.class ) );
                mv.visitInsn( POP );
                mv.visitInsn( RETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "getTrait", String.class ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "getTrait",
                        "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( Thing.class ),
                        "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( Thing.class ),
                        null );
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType(wrapperName), "_getTraitMap", "()" + Type.getDescriptor(Map.class));
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Map.class), "get",
                        "(" + Type.getDescriptor(Object.class) + ")" + Type.getDescriptor(Object.class));
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Thing.class));
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "hasTraits" ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC,
                                     "hasTraits",
                                     Type.getMethodDescriptor( Type.getType( boolean.class ), new Type[] {} ),
                                     null,
                                     null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType(wrapperName), "_getTraitMap", "()" + Type.getDescriptor(Map.class));
                Label l5 = new Label();
                mv.visitJumpInsn( IFNULL, l5 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType(wrapperName), "_getTraitMap", "()" + Type.getDescriptor(Map.class));
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "isEmpty", Type.getMethodDescriptor( Type.BOOLEAN_TYPE, new Type[] {} ) );
                mv.visitJumpInsn( IFNE, l5 );
                mv.visitInsn( ICONST_1 );
                Label l4 = new Label();
                mv.visitJumpInsn( GOTO, l4 );
                mv.visitLabel( l5 );
                mv.visitInsn( ICONST_0 );
                mv.visitLabel( l4 );
                mv.visitInsn( IRETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "hasTrait", String.class ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "hasTrait", "(" + Type.getDescriptor( String.class )+ ")Z", null, null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "_getTraitMap", "()" + Type.getDescriptor( Map.class ) );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "containsKey", "(" + Type.getDescriptor( Object.class ) + ")Z" );
                mv.visitInsn( IRETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "removeTrait", String.class ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "removeTrait",
                        Type.getMethodDescriptor( Type.getType( Collection.class ), new Type[] { Type.getType( String.class ) } ),
                        Type.getMethodDescriptor( Type.getType( Collection.class ), new Type[] { Type.getType( String.class ) } ),
                        null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "_getTraitMap", Type.getMethodDescriptor( Type.getType( Map.class ), new Type[] {} ) );
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitTypeMap.class ) );
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TraitTypeMap.class ), "removeCascade",
                        Type.getMethodDescriptor( Type.getType( Collection.class ), new Type[] { Type.getType( String.class )} ) );
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();

                mv = cw.visitMethod( ACC_PUBLIC, "removeTrait",
                        Type.getMethodDescriptor( Type.getType( Collection.class ), new Type[] { Type.getType( BitSet.class ) } ),
                        Type.getMethodDescriptor( Type.getType( Collection.class ), new Type[] { Type.getType( BitSet.class ) } ),
                        null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "_getTraitMap", Type.getMethodDescriptor( Type.getType( Map.class ), new Type[] {} ) );
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitTypeMap.class ) );
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TraitTypeMap.class ), "removeCascade",
                        Type.getMethodDescriptor( Type.getType( Collection.class ), new Type[] { Type.getType( BitSet.class )} ) );
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();

            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "getTraits" ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "getTraits", "()" + Type.getDescriptor( Collection.class ), "()Ljava/util/Collection<Ljava/lang/String;>;", null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "_getTraitMap", "()" + Type.getDescriptor( Map.class ) );
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "keySet", "()" + Type.getDescriptor( Set.class ) );
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }
        if ( coreKlazz == null || needsMethod( coreKlazz, "_setBottomTypeCode" ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC, "_setBottomTypeCode", "(" + Type.getDescriptor( BitSet.class )+ ")V", null, null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME , Type.getDescriptor( Map.class ) );
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitTypeMap.class ) );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( TraitTypeMap.class ), "setBottomCode", "(" + Type.getDescriptor( BitSet.class ) + ")V");
                mv.visitInsn( RETURN );
                mv.visitMaxs( 0,0 );
                mv.visitEnd();
            }
        }

        if ( coreKlazz == null || needsMethod( coreKlazz, "getCurrentTypeCode" ) ) {

            {
                mv = cw.visitMethod( ACC_PUBLIC, "getCurrentTypeCode", "()" + Type.getDescriptor( BitSet.class ), null, null );
                mv.visitCode();

                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD,
                                   BuildUtils.getInternalType( wrapperName ),
                                   TraitableBean.TRAITSET_FIELD_NAME,
                                   Type.getDescriptor( Map.class ) );
                Label l3 = new Label();
                mv.visitJumpInsn( IFNONNULL, l3 );
                mv.visitInsn( ACONST_NULL );
                mv.visitInsn( ARETURN );
                mv.visitLabel( l3 );

                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD,
                        BuildUtils.getInternalType( wrapperName ),
                        TraitableBean.TRAITSET_FIELD_NAME,
                        Type.getDescriptor( Map.class ) );
                mv.visitTypeInsn( CHECKCAST,
                        Type.getInternalName( TraitTypeMap.class ) );
                mv.visitMethodInsn( INVOKEVIRTUAL,
                        Type.getInternalName( TraitTypeMap.class ),
                        "getCurrentTypeCode",
                        "()" + Type.getDescriptor( BitSet.class ) );
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }

        if ( coreKlazz == null || needsMethod( coreKlazz, "getMostSpecificTraits" ) ) {

            {
                mv = cw.visitMethod( ACC_PUBLIC,
                        "getMostSpecificTraits",
                        "()" + Type.getDescriptor( Collection.class ),
                        "()Ljava/util/Collection<Lorg/drools/factmodel/traits/Thing;>;",
                        null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ),
                        TraitableBean.TRAITSET_FIELD_NAME ,
                        Type.getDescriptor( Map.class ) );
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitTypeMap.class ) );
                mv.visitMethodInsn( INVOKEVIRTUAL,
                        Type.getInternalName( TraitTypeMap.class ),
                        "getMostSpecificTraits",
                        "()" + Type.getDescriptor( Collection.class ) );
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();

            }
        }


        if ( coreKlazz == null || needsMethod( coreKlazz, "_getFieldTMS", TraitFieldTMS.class ) ) {
            {
                mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                                     "_getFieldTMS",
                                     Type.getMethodDescriptor( Type.getType( TraitFieldTMS.class ), new Type[] {} ),
                                     null,
                                     null);
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.FIELDTMS_FIELD_NAME, Type.getDescriptor( TraitFieldTMS.class ) );
                mv.visitInsn( ARETURN );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();

            }
        }

        if ( coreKlazz == null || needsMethod( coreKlazz, "_setFieldTMS", TraitFieldTMS.class ) ) {
            {
                mv = cw.visitMethod( ACC_PUBLIC,
                                     "_setFieldTMS",
                                     Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.getType( TraitFieldTMS.class ) } ),
                                     null,
                                     null);
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitFieldInsn ( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.FIELDTMS_FIELD_NAME, Type.getDescriptor( TraitFieldTMS.class ) );
                mv.visitInsn( RETURN) ;
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }
        }



        {
            mv = cw.visitMethod( ACC_PUBLIC, "writeExternal", "(" + Type.getDescriptor( ObjectOutput.class ) + ")V", null, new String[] { Type.getInternalName( IOException.class ) } );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getCore", "()" + Type.getDescriptor( Object.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectOutput.class ), "writeObject", "(" + Type.getDescriptor( Object.class ) + ")V" );


            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.MAP_FIELD_NAME, Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectOutput.class ), "writeObject", "(" + Type.getDescriptor( Object.class ) + ")V" );

            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectOutput.class ), "writeObject", "(" + Type.getDescriptor( Object.class ) + ")V" );

            if ( core.isFullTraiting() ) {
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.FIELDTMS_FIELD_NAME, Type.getDescriptor( Map.class ) );
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectOutput.class ), "writeObject", "(" + Type.getDescriptor( Object.class ) + ")V" );
            }

            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "readExternal", "(" + Type.getDescriptor( ObjectInput.class ) + ")V", null,
                    new String[] { Type.getInternalName( IOException.class ), Type.getInternalName( ClassNotFoundException.class ) } );
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectInput.class ), "readObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( coreName ) );
            mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), "core", BuildUtils.getTypeDescriptor( coreName ) );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectInput.class ), "readObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( Map.class ) );
            mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.MAP_FIELD_NAME, Type.getDescriptor( Map.class ) );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectInput.class ), "readObject", "()" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( Map.class ) );
            mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );

            if ( core.isFullTraiting() ) {
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( ObjectInput.class ), "readObject", "()" + Type.getDescriptor( Object.class ) );
                mv.visitTypeInsn( CHECKCAST, Type.getInternalName( TraitFieldTMS.class ) );
                mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.FIELDTMS_FIELD_NAME, Type.getDescriptor( TraitFieldTMS.class ) );
            }

            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC, "init", "("+ BuildUtils.getTypeDescriptor( coreName ) +")V", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitFieldInsn( PUTFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    "core",
                    BuildUtils.getTypeDescriptor( coreName ) );

            initializeDynamicTypeStructures( mv, wrapperName, core );

            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }


        Method[] ms = coreKlazz.getMethods();
        for ( Method method : ms ) {
            if ( Modifier.isFinal( method.getModifiers() ) ) {
                continue;
            }

            String signature = TraitFactory.buildSignature( method );
            {
                mv = cw.visitMethod( ACC_PUBLIC,
                        method.getName(),
                        signature,
                        null,
                        null );
                mv.visitCode();
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), "core", BuildUtils.getTypeDescriptor( coreName ) );

                Label l0 = new Label();
                    mv.visitJumpInsn( IFNONNULL, l0 );
                    if ( method.getReturnType() == void.class ) {
                        mv.visitInsn( RETURN );
                    } else {
                        mv.visitInsn( BuildUtils.zero( method.getReturnType().getName() ) );
                        mv.visitInsn( BuildUtils.returnType( method.getReturnType().getName() ) );
                    }
                mv.visitLabel( l0 );

                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), "core", BuildUtils.getTypeDescriptor( coreName ) );

                int j = 1;
                for ( Class arg : method.getParameterTypes() ) {
                    mv.visitVarInsn( BuildUtils.varType( arg.getName() ), j++ );
                }
                mv.visitMethodInsn( INVOKEVIRTUAL,
                        BuildUtils.getInternalType( coreName ),
                        method.getName(),
                        signature );

                mv.visitInsn( BuildUtils.returnType( method.getReturnType().getName() ) );
                int stack = TraitFactory.getStackSize( method );
                mv.visitMaxs( 0, 0 );
                mv.visitEnd();
            }

        }

        {
            mv = cw.visitMethod( ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "init", "(" + Type.getDescriptor( Object.class ) + ")V", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( coreName ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( wrapperName ),
                    "init",
                    "(" + BuildUtils.getTypeDescriptor( coreName ) + ")V" );
            mv.visitInsn( RETURN );
            mv.visitMaxs( 0, 0 );
            mv.visitEnd();
        }

        cw.visitEnd();

        return cw.toByteArray();
    }


    protected void initializeDynamicTypeStructures( MethodVisitor mv, String wrapperName, ClassDefinition coreDef ) {
        if ( coreDef.isFullTraiting() ) {

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitTypeInsn( NEW, Type.getInternalName( TraitFieldTMSImpl.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( TraitFieldTMSImpl.class ), "<init>", "()V" );
            mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.FIELDTMS_FIELD_NAME, Type.getDescriptor( TraitFieldTMS.class ) );

            for ( FactField hardField : coreDef.getFields() ) {
                FieldDefinition fld = (FieldDefinition) hardField;
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.FIELDTMS_FIELD_NAME, Type.getDescriptor( TraitFieldTMS.class ) );

                mv.visitLdcInsn( Type.getType( Type.getDescriptor( coreDef.getDefinedClass() ) ) );

                mv.visitLdcInsn( fld.resolveAlias() );

                if ( BuildUtils.isPrimitive( fld.getTypeName() ) ) {
//                    mv.visitFieldInsn( GETSTATIC, BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ), "TYPE", Type.getDescriptor( Class.class ) );
                    mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( BuildUtils.box( fld.getTypeName() ) ) ) );
                } else {
                    mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( fld.getTypeName() ) ) );
                }

                mv.visitVarInsn( ALOAD, 0 );
                mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), "core", Type.getDescriptor( coreDef.getDefinedClass() ) );
                mv.visitMethodInsn( INVOKEVIRTUAL, Type.getInternalName( coreDef.getDefinedClass() ), BuildUtils.getterName( fld.getName(), fld.getTypeName() ), "()" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) );
                if ( BuildUtils.isPrimitive( fld.getTypeName() ) ) {
                    mv.visitMethodInsn( INVOKESTATIC, BuildUtils.getInternalType( BuildUtils.box( fld.getTypeName() ) ), "valueOf", "(" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + ")" + BuildUtils.getTypeDescriptor( BuildUtils.box( fld.getTypeName() ) ) );
                }

                if ( fld.getInitExpr() != null ) {
                    mv.visitLdcInsn( fld.getInitExpr() );
                } else {
                    mv.visitInsn( ACONST_NULL );
                }

                mv.visitMethodInsn( INVOKEINTERFACE,
                                    Type.getInternalName( TraitFieldTMS.class ),
                                    "registerField",
                                    Type.getMethodDescriptor( Type.VOID_TYPE, new Type[] { Type.getType( Class.class ), Type.getType( String.class ), Type.getType( Class.class ), Type.getType( Object.class ), Type.getType( String.class ) } ) );
            }
        }
    }

    protected boolean needsMethod( Class coreKlazz, String methodName, Class... args ) {
        try {
            return coreKlazz.getMethod( methodName, args ) == null;
        } catch ( NoSuchMethodException e ) {
            return true;
        }
    }
}
