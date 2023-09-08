/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.traits.core.factmodel;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.builder.impl.classbuilder.BuildUtils;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.factmodel.FieldDefinition;
import org.drools.base.factmodel.traits.TraitFieldTMS;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.mvel.asm.AsmUtil;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Type;

public abstract class AbstractPropertyWrapperClassBuilderImpl implements TraitPropertyWrapperClassBuilder,
                                                                         Serializable {

    protected transient ClassDefinition trait;

    protected transient TraitRegistryImpl traitRegistryImpl;

    protected ClassDefinition getTrait() {
        return trait;
    }

    public void init(ClassDefinition trait, TraitRegistryImpl traitRegistryImpl) {
        this.trait = trait;
        this.traitRegistryImpl = traitRegistryImpl;
    }

    protected void invokePut(MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field) {
        mv.visitLdcInsn(fieldName);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(String.class), "equals", "(" + Type.getDescriptor(Object.class) + ")Z", false);
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);

        mv.visitVarInsn(ALOAD, 2);
        if (BuildUtils.isPrimitive(field.getTypeName())) {
            TraitFactoryImpl.primitiveValue(mv, field.getTypeName());
            mv.visitVarInsn( AsmUtil.storeType(field.getTypeName()), 3);
            TraitFactoryImpl.invokeInjector(mv, wrapperName, core, field, false, 3);
        } else {
            TraitFactoryImpl.invokeInjector(mv, wrapperName, core, field, false, 2);
        }

        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l1);
    }

    protected void invokeRemove(MethodVisitor mv, String wrapperName, ClassDefinition core, String fieldName, FieldDefinition field) {
        mv.visitLdcInsn(fieldName);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(String.class), "equals", "(" + Type.getDescriptor(Object.class) + ")Z", false);
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);

        TraitFactoryImpl.invokeExtractor(mv, wrapperName, core, field);

        if (BuildUtils.isPrimitive(field.getTypeName())) {
            TraitFactoryImpl.valueOf(mv, field.getTypeName());
        }
        mv.visitVarInsn(ASTORE, 2);

        TraitFactoryImpl.invokeInjector(mv, wrapperName, core, field, true, 1);

        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l1);
    }

    protected void registerLogicalField(MethodVisitor mv, String internalWrapper, FieldDefinition field, ClassDefinition core) {

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalWrapper, "object", Type.getDescriptor(core.getDefinedClass()));
        mv.visitTypeInsn(CHECKCAST, Type.getInternalName(TraitableBean.class));
        mv.visitMethodInsn(INVOKEINTERFACE,
                           Type.getInternalName(TraitableBean.class),
                           "_getFieldTMS",
                           Type.getMethodDescriptor(Type.getType(TraitFieldTMS.class)),
                           true);
        mv.visitVarInsn(ASTORE, 1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(field.resolveAlias());
        mv.visitMethodInsn(INVOKEINTERFACE,
                           Type.getInternalName(TraitFieldTMS.class),
                           "isManagingField",
                           Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(String.class)),
                           true);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(Type.getType(BuildUtils.getTypeDescriptor(core.getClassName())));
        mv.visitLdcInsn(field.resolveAlias());
        mv.visitMethodInsn(INVOKEINTERFACE,
                           Type.getInternalName(TraitFieldTMS.class),
                           "registerField",
                           Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Class.class), Type.getType(String.class)),
                           true);
        mv.visitLabel(l1);
    }

    protected void invokeContainsKey(MethodVisitor mv, String fieldName) {
        mv.visitLdcInsn(fieldName);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL,
                           Type.getInternalName(String.class),
                           "equals",
                           "(" + Type.getDescriptor(Object.class) + ")Z",
                           false);
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l0);
    }

    protected void buildEntry(MethodVisitor mv, FieldDefinition field, String wrapperName, ClassDefinition core) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(field.getName());

        TraitFactoryImpl.invokeExtractor(mv, wrapperName, core, field);

        if (BuildUtils.isPrimitive(field.getTypeName())) {
            TraitFactoryImpl.valueOf(mv, field.getTypeName());
        }

        mv.visitMethodInsn(INVOKESTATIC,
                           Type.getInternalName(TraitProxyImpl.class),
                           "buildEntry",
                           "(" + Type.getDescriptor(String.class) + Type.getDescriptor(Object.class) + ")" + Type.getDescriptor(Map.Entry.class),
                           false);
        mv.visitMethodInsn(INVOKEINTERFACE,
                           Type.getInternalName(Set.class),
                           "add",
                           "(" + Type.getDescriptor(Object.class) + ")Z",
                           true);
        mv.visitInsn(POP);
    }

    protected void extractAndCollect(MethodVisitor mv, String wrapperName, FieldDefinition field, ClassDefinition core) {
        mv.visitVarInsn(ALOAD, 1);

        TraitFactoryImpl.invokeExtractor(mv, wrapperName, core, field);

        if (BuildUtils.isPrimitive(field.getTypeName())) {
            TraitFactoryImpl.valueOf(mv, field.getTypeName());
        }

        mv.visitMethodInsn(INVOKEINTERFACE,
                           Type.getInternalName(Collection.class),
                           "add",
                           "(" + Type.getDescriptor(Object.class) + ")Z",
                           true);
        mv.visitInsn(POP);
    }

    protected void extractAndTestNotNull(MethodVisitor mv, String wrapperName, ClassDefinition core, FieldDefinition field) {
        TraitFactoryImpl.invokeExtractor(mv, wrapperName, core, field);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNONNULL, l1);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l1);
    }

    protected void collectFieldName(MethodVisitor mv, FieldDefinition field) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(field.getName());
        mv.visitMethodInsn(INVOKEINTERFACE,
                           Type.getInternalName(Set.class),
                           "add",
                           "(" + Type.getDescriptor(Object.class) + ")Z",
                           true);
        mv.visitInsn(POP);
    }
}

