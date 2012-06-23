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
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TraitCoreWrapperClassBuilderImpl implements TraitCoreWrapperClassBuilder {


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


        Class coreKlazz = core.getDefinedClass();
        String coreName = coreKlazz.getName();
        String wrapperName = coreName + "Wrapper";

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,
                BuildUtils.getInternalType( wrapperName ),
                BuildUtils.getTypeDescriptor( coreName ) +
                        "Lorg/drools/factmodel/traits/CoreWrapper<" + BuildUtils.getTypeDescriptor( coreName ) + ">;",
                BuildUtils.getInternalType( coreName ),
                new String[] { Type.getInternalName( CoreWrapper.class ), Type.getInternalName( Externalizable.class ) } );

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
            mv = cw.visitMethod( ACC_PUBLIC, "<init>", "()V", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKESPECIAL, BuildUtils.getInternalType( coreName ), "<init>", "()V" );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitTypeInsn( NEW, Type.getInternalName( HashMap.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashMap.class ), "<init>", "()V" );
            mv.visitFieldInsn( PUTFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    TraitableBean.MAP_FIELD_NAME,
                    Type.getDescriptor( Map.class ) );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitTypeInsn( NEW, Type.getInternalName( HashMap.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashMap.class ), "<init>", "()V" );
            mv.visitFieldInsn( PUTFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    TraitableBean.TRAITSET_FIELD_NAME,
                    Type.getDescriptor( Map.class ) );
            mv.visitInsn( RETURN );
            mv.visitMaxs( 3, 1 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "getCore", "()" + Type.getDescriptor( Object.class ), "()"+BuildUtils.getTypeDescriptor( coreName ), null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    "core",
                    BuildUtils.getTypeDescriptor( coreName ));
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "getDynamicProperties", "()" + Type.getDescriptor( Map.class ), "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    TraitableBean.MAP_FIELD_NAME,
                    Type.getDescriptor( Map.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();

            mv = cw.visitMethod( ACC_PUBLIC,
                    "setDynamicProperties",
                    "(" + Type.getDescriptor( Map.class ) + ")V",
                    "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;)V",
                    null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.MAP_FIELD_NAME, Type.getDescriptor( Map.class ) );
            mv.visitInsn( RETURN );
            mv.visitMaxs( 2, 2 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PROTECTED, "getTraitMap", "()" + Type.getDescriptor( Map.class ), 
                                 "()Ljava/util/Map<Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;>;", null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
            Label l0 = new Label();
            mv.visitJumpInsn( IFNONNULL, l0 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitTypeInsn( NEW, Type.getInternalName( HashMap.class ) );
            mv.visitInsn( DUP );
            mv.visitMethodInsn( INVOKESPECIAL, Type.getInternalName( HashMap.class ), "<init>", "()V" );
            mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
            mv.visitLabel( l0 );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 3, 1 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "setTraitMap", "(Ljava/util/Map;)V", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitFieldInsn( PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, Type.getDescriptor( Map.class ) );
            mv.visitInsn( RETURN );
            mv.visitMaxs( 2, 2 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "addTrait",
                                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Thing.class ) + ")V",
                                "(" + Type.getDescriptor( String.class ) + Type.getDescriptor( Thing.class ) + ")V",
                                null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getTraitMap", "()" + Type.getDescriptor( Map.class ) );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "put",
                                "(" + Type.getDescriptor( Object.class ) + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
            mv.visitInsn( POP );
            mv.visitInsn( RETURN );
            mv.visitMaxs( 3, 3 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "getTrait",
                                "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( Thing.class ),
                                "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( Thing.class ), 
                                null );
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType(wrapperName), "getTraitMap", "()" + Type.getDescriptor(Map.class));
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Map.class), "get",
                    "(" + Type.getDescriptor(Object.class) + ")" + Type.getDescriptor(Object.class));
            mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Thing.class));
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 2, 2 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "hasTrait", "(" + Type.getDescriptor( String.class )+ ")Z", null, null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getTraitMap", "()" + Type.getDescriptor( Map.class ) );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "containsKey", "(" + Type.getDescriptor( Object.class ) + ")Z" );
            mv.visitInsn( IRETURN );
            mv.visitMaxs( 2, 2 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "removeTrait", 
                                "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( Thing.class ),
                                "(" + Type.getDescriptor( String.class ) + ")" + Type.getDescriptor( Thing.class ), 
                                null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getTraitMap", "()" + Type.getDescriptor( Map.class ) );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "remove", "(" + Type.getDescriptor( Object.class ) + ")" + Type.getDescriptor( Object.class ) );
            mv.visitTypeInsn( CHECKCAST, Type.getInternalName( Thing.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 2, 2 );
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC, "getTraits", "()" + Type.getDescriptor( Collection.class ), "()Ljava/util/Collection<Ljava/lang/String;>;", null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getTraitMap", "()" + Type.getDescriptor( Map.class ) );
            mv.visitMethodInsn( INVOKEINTERFACE, Type.getInternalName( Map.class ), "keySet", "()" + Type.getDescriptor( Set.class ) );
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
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

            mv.visitInsn( RETURN );
            mv.visitMaxs( 2, 2 );
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


            mv.visitInsn( RETURN );
            mv.visitMaxs( 3, 2 );
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
            mv.visitInsn( RETURN );
            mv.visitMaxs( 2, 2 );
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
                mv.visitMaxs( stack, stack );
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
            mv.visitMaxs( 2, 2 );
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
