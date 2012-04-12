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
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
                BuildUtils.getInternalType(wrapperName),
                BuildUtils.getTypeDescriptor( coreName) +
                        "Lorg/drools/factmodel/traits/CoreWrapper<" + BuildUtils.getTypeDescriptor( coreName ) + ">;",
                BuildUtils.getInternalType( coreName ),
                new String[]{"org/drools/factmodel/traits/CoreWrapper", "java/io/Externalizable" });

        {
            fv = cw.visitField(ACC_PRIVATE, "core", BuildUtils.getTypeDescriptor( coreName ), null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, TraitableBean.MAP_FIELD_NAME, "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;>;", null);
            fv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, BuildUtils.getInternalType( coreName ), "<init>", "()V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
            mv.visitFieldInsn( PUTFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    TraitableBean.MAP_FIELD_NAME,
                    "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
            mv.visitFieldInsn( PUTFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    TraitableBean.TRAITSET_FIELD_NAME,
                    "Ljava/util/Map;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getCore", "()Ljava/lang/Object;", "()"+BuildUtils.getTypeDescriptor( coreName ), null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    "core",
                    BuildUtils.getTypeDescriptor( coreName ));
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getDynamicProperties", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    TraitableBean.MAP_FIELD_NAME,
                    "Ljava/util/Map;");
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
            mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.MAP_FIELD_NAME, "Ljava/util/Map;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PROTECTED, "getTraitMap", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;>;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
            Label l0 = new Label();
            mv.visitJumpInsn(IFNONNULL, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
            mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "setTraitMap", "(Ljava/util/Map;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "addTrait", "(Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;)V", "(Ljava/lang/String;Lorg/drools/factmodel/traits/Thing;)V", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getTraitMap", "()Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getTrait", "(Ljava/lang/String;)Lorg/drools/factmodel/traits/Thing;", "(Ljava/lang/String;)Lorg/drools/factmodel/traits/Thing;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getTraitMap", "()Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/factmodel/traits/Thing");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "hasTrait", "(Ljava/lang/String;)Z", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getTraitMap", "()Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z");
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "removeTrait", "(Ljava/lang/String;)Lorg/drools/factmodel/traits/Thing;", "(Ljava/lang/String;)Lorg/drools/factmodel/traits/Thing;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getTraitMap", "()Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/factmodel/traits/Thing");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getTraits", "()Ljava/util/Collection;", "()Ljava/util/Collection<Ljava/lang/String;>;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getTraitMap", "()Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "keySet", "()Ljava/util/Set;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC, "writeExternal", "(Ljava/io/ObjectOutput;)V", null, new String[]{"java/io/IOException"});
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "getCore", "()Ljava/lang/Object;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/io/ObjectOutput", "writeObject", "(Ljava/lang/Object;)V");


            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.MAP_FIELD_NAME, "Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/io/ObjectOutput", "writeObject", "(Ljava/lang/Object;)V");

            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/io/ObjectOutput", "writeObject", "(Ljava/lang/Object;)V");

            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "readExternal", "(Ljava/io/ObjectInput;)V", null, new String[]{"java/io/IOException", "java/lang/ClassNotFoundException"});
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/io/ObjectInput", "readObject", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( coreName ) );
            mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( wrapperName ), "core", BuildUtils.getTypeDescriptor( coreName ) );


            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/io/ObjectInput", "readObject", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "java/util/Map");
            mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.MAP_FIELD_NAME, "Ljava/util/Map;");

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/io/ObjectInput", "readObject", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "java/util/Map");
            mv.visitFieldInsn(PUTFIELD, BuildUtils.getInternalType( wrapperName ), TraitableBean.TRAITSET_FIELD_NAME, "Ljava/util/Map;");


            mv.visitInsn( RETURN );
            mv.visitMaxs( 3, 2 );
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC, "init", "("+ BuildUtils.getTypeDescriptor( coreName ) +")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn( PUTFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    "core",
                    BuildUtils.getTypeDescriptor( coreName ) );
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }


        Method[] ms = coreKlazz.getMethods();
        for ( Method method : ms ) {
            if ( Modifier.isFinal(method.getModifiers()) ) {
                continue;
            }

            String signature = TraitFactory.buildSignature(method);
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
                int stack = TraitFactory.getStackSize(method);
                mv.visitMaxs(stack, stack);
                mv.visitEnd();
            }

        }

        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "init", "(Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( coreName ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( wrapperName ),
                    "init",
                    "(" + BuildUtils.getTypeDescriptor( coreName ) + ")V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
