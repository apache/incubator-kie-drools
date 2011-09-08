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
import org.drools.factmodel.ClassBuilder;
import org.drools.factmodel.ClassDefinition;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
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
                new String[]{"org/drools/factmodel/traits/CoreWrapper"});

        {
            fv = cw.visitField(ACC_PRIVATE, "core", BuildUtils.getTypeDescriptor( coreName ), null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, ITraitable.MAP_FIELD_NAME, "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, ITraitable.TRAITSET_FIELD_NAME, "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/Class;>;", null);
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
                    ITraitable.MAP_FIELD_NAME,
                    "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
            mv.visitFieldInsn( PUTFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    ITraitable.TRAITSET_FIELD_NAME,
                    "Ljava/util/Map;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getDynamicProperties", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    ITraitable.MAP_FIELD_NAME,
                    "Ljava/util/Map;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getTraits", "()Ljava/util/Map;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( wrapperName ),
                    ITraitable.TRAITSET_FIELD_NAME,
                    "Ljava/util/Map;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
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
