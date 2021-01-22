/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.deployment;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberAccessorImplementor;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberDescriptor;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableAnnotatedElement;
import org.optaplanner.quarkus.gizmo.types.QuarkusRecordableTypes;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.gizmo.DescriptorUtils;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.Gizmo;
import io.quarkus.gizmo.MethodDescriptor;

public class GizmoMemberAccessorEntityEnhancer {

    private static Set<FieldInfo> visitedFields = new HashSet<>();
    private static Set<MethodInfo> visitedMethods = new HashSet<>();

    public static void addVirtualFieldGetter(ClassInfo classInfo, FieldInfo fieldInfo,
            BuildProducer<BytecodeTransformerBuildItem> transformers) {
        if (!visitedFields.contains(fieldInfo)) {
            transformers.produce(new BytecodeTransformerBuildItem(classInfo.name().toString(),
                    (className, classVisitor) -> new OptaPlannerFieldEnhancingClassVisitor(classInfo, classVisitor,
                            fieldInfo)));
            visitedFields.add(fieldInfo);
        }
    }

    public static Optional<MethodDescriptor> addVirtualMethodGetter(ClassInfo classInfo, MethodInfo methodInfo, String name,
            Optional<MethodDescriptor> setterDescriptor,
            BuildProducer<BytecodeTransformerBuildItem> transformers) {
        if (!visitedMethods.contains(methodInfo)) {
            transformers.produce(new BytecodeTransformerBuildItem(classInfo.name().toString(),
                    (className, classVisitor) -> new OptaPlannerMethodEnhancingClassVisitor(classInfo, classVisitor, methodInfo,
                            name, setterDescriptor)));
            visitedMethods.add(methodInfo);
        }
        return setterDescriptor.map(md -> MethodDescriptor
                .ofMethod(classInfo.name().toString(), getVirtualSetterName(name),
                        md.getReturnType(), md.getParameterTypes()));
    }

    public static String getVirtualGetterName(String name) {
        return "$get$optaplanner$__" + name;
    }

    public static String getVirtualSetterName(String name) {
        return "$set$optaplanner$__" + name;
    }

    /**
     * Generates the bytecode for the member accessor for the specified field.
     * Additionally enhances the class that declares the field with public simple
     * getters/setters methods for the field if the field is private.
     *
     * @param annotationInstance The annotations on the field
     * @param indexView The index view (needed to get default values of annotations)
     * @param classOutput Where to output the bytecode
     * @param classInfo The declaring class for the field
     * @param fieldInfo The field to generate the MemberAccessor for
     * @param transformers BuildProducer of BytecodeTransformers
     */
    public static String generateFieldAccessor(AnnotationInstance annotationInstance, IndexView indexView,
            ClassOutput classOutput, ClassInfo classInfo,
            FieldInfo fieldInfo, BuildProducer<BytecodeTransformerBuildItem> transformers) throws ClassNotFoundException {
        String generatedClassName = classInfo.name().prefix().toString() + ".$optaplanner$__"
                + classInfo.name().withoutPackagePrefix() + "$__" + fieldInfo.name();
        ClassCreator classCreator = ClassCreator
                .builder()
                .className(generatedClassName)
                .interfaces(MemberAccessor.class)
                .classOutput(classOutput)
                .build();

        GizmoMemberDescriptor member;
        Class<?> declaringClass = Class.forName(fieldInfo.declaringClass().name().toString(), false,
                Thread.currentThread().getContextClassLoader());
        FieldDescriptor memberDescriptor = FieldDescriptor.of(fieldInfo);
        String name = fieldInfo.name();
        AnnotatedElement annotatedElement = new QuarkusRecordableAnnotatedElement(fieldInfo, indexView);
        java.lang.reflect.Type type = QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(fieldInfo.type(), indexView);

        if (Modifier.isPublic(fieldInfo.flags())) {
            member = new GizmoMemberDescriptor(name, memberDescriptor, declaringClass,
                    annotatedElement, type);
        } else {
            addVirtualFieldGetter(classInfo, fieldInfo, transformers);
            String methodName = getVirtualGetterName(fieldInfo.name());
            MethodDescriptor getterDescriptor = MethodDescriptor.ofMethod(fieldInfo.declaringClass().name().toString(),
                    methodName,
                    fieldInfo.type().name().toString());
            MethodDescriptor setterDescriptor = MethodDescriptor.ofMethod(fieldInfo.declaringClass().name().toString(),
                    getVirtualSetterName(fieldInfo.name()),
                    "void",
                    fieldInfo.type().name().toString());
            member = new GizmoMemberDescriptor(name, getterDescriptor, declaringClass,
                    annotatedElement, type, setterDescriptor);
        }
        GizmoMemberAccessorImplementor.defineAccessorFor(classCreator, member,
                (Class<? extends Annotation>) Class.forName(annotationInstance.name().toString(), false,
                        Thread.currentThread().getContextClassLoader()));
        classCreator.close();
        return generatedClassName;
    }

    private static String getMemberName(MethodInfo methodInfo) {
        if (methodInfo.name().startsWith("get")) { // Case 1: Getter method
            return methodInfo.name().substring(3, 4).toLowerCase(Locale.ROOT) + methodInfo.name().substring(4);
        } else if (methodInfo.name().startsWith("is")) { // Case 2: Getter method for boolean
            return methodInfo.name().substring(2, 3).toLowerCase(Locale.ROOT) + methodInfo.name().substring(3);
        } else { // Case 3: Read method
            return methodInfo.name();
        }
    }

    private static Optional<MethodDescriptor> getSetterDescriptor(ClassInfo classInfo, MethodInfo methodInfo, String name) {
        if (methodInfo.name().startsWith("get") || methodInfo.name().startsWith("is")) {
            // ex: for methodInfo = Integer getValue(), name = value,
            // return void setValue(Integer value)
            // i.e. capitalize first letter of name, and take a parameter
            // of the getter return type.
            return Optional.ofNullable(classInfo.method("set" + name.substring(0, 1)
                    .toUpperCase(Locale.ROOT) +
                    name.substring(1),
                    methodInfo.returnType())).map(MethodDescriptor::of);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Generates the bytecode for the member accessor for the specified method.
     * Additionally enhances the class that declares the method with public simple
     * read/(optionally write if getter method and setter present) methods for the method
     * if the method is private.
     *
     * @param annotationInstance The annotations on the field
     * @param indexView The index view (needed to get default values of annotations)
     * @param classOutput Where to output the bytecode
     * @param classInfo The declaring class for the field
     * @param methodInfo The method to generate the MemberAccessor for
     * @param transformers BuildProducer of BytecodeTransformers
     */
    public static String generateMethodAccessor(AnnotationInstance annotationInstance, IndexView indexView,
            ClassOutput classOutput, ClassInfo classInfo,
            MethodInfo methodInfo, BuildProducer<BytecodeTransformerBuildItem> transformers) throws ClassNotFoundException {
        String generatedClassName = classInfo.name().prefix().toString() + ".$optaplanner$__"
                + classInfo.name().withoutPackagePrefix() + "$__" + methodInfo.name();
        ClassCreator classCreator = ClassCreator
                .builder()
                .className(generatedClassName)
                .interfaces(MemberAccessor.class)
                .classOutput(classOutput)
                .build();

        GizmoMemberDescriptor member;
        String name = getMemberName(methodInfo);
        Optional<MethodDescriptor> setterDescriptor = getSetterDescriptor(classInfo, methodInfo, name);

        Class<?> declaringClass = Class.forName(methodInfo.declaringClass().name().toString(), false,
                Thread.currentThread().getContextClassLoader());
        MethodDescriptor memberDescriptor = MethodDescriptor.of(methodInfo);
        AnnotatedElement annotatedElement = new QuarkusRecordableAnnotatedElement(methodInfo, indexView);
        java.lang.reflect.Type type = QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(methodInfo.returnType(), indexView);

        if (Modifier.isPublic(methodInfo.flags())) {
            member = new GizmoMemberDescriptor(name, memberDescriptor, declaringClass,
                    annotatedElement, type, setterDescriptor.orElse(null));
        } else {
            setterDescriptor = addVirtualMethodGetter(classInfo, methodInfo, name, setterDescriptor, transformers);
            String methodName = getVirtualGetterName(name);
            MethodDescriptor newMethodDescriptor =
                    MethodDescriptor.ofMethod(declaringClass, methodName, getTypeDescriptor(type));
            member = new GizmoMemberDescriptor(name, newMethodDescriptor, declaringClass,
                    annotatedElement, type, setterDescriptor.orElse(null));
        }
        GizmoMemberAccessorImplementor.defineAccessorFor(classCreator, member,
                (Class<? extends Annotation>) Class.forName(annotationInstance.name().toString(), false,
                        Thread.currentThread().getContextClassLoader()));
        classCreator.close();
        return generatedClassName;
    }

    private static String getTypeDescriptor(java.lang.reflect.Type type) throws ClassNotFoundException {
        String typeName = type.getTypeName();
        int genericStart = typeName.indexOf('<');
        boolean isGeneric = genericStart != -1;
        if (isGeneric) {
            int genericEnd = typeName.lastIndexOf('>');
            return Type.getDescriptor(Class.forName(typeName.substring(0, genericStart) + typeName.substring(genericEnd + 1)));
        } else {
            return Type.getDescriptor(Class.forName(typeName));
        }
    }

    private static class OptaPlannerFieldEnhancingClassVisitor extends ClassVisitor {
        private final FieldInfo fieldInfo;
        private final Class<?> clazz;
        private final String fieldTypeDescriptor;

        public OptaPlannerFieldEnhancingClassVisitor(ClassInfo classInfo, ClassVisitor outputClassVisitor,
                FieldInfo fieldInfo) {
            super(Gizmo.ASM_API_VERSION, outputClassVisitor);
            this.fieldInfo = fieldInfo;
            try {
                clazz = Class.forName(classInfo.name().toString(), false, Thread.currentThread().getContextClassLoader());
                fieldTypeDescriptor = DescriptorUtils.typeToString(fieldInfo.type());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            addGetter(this.cv);
            addSetter(this.cv);
        }

        private void addSetter(ClassVisitor classWriter) {
            String methodName = getVirtualSetterName(fieldInfo.name());
            MethodVisitor mv;
            mv = classWriter.visitMethod(ACC_PUBLIC, methodName, "(" + fieldTypeDescriptor + ")V",
                    null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(Type.getType(fieldTypeDescriptor).getOpcode(ILOAD), 1);
            mv.visitFieldInsn(PUTFIELD, Type.getInternalName(clazz), fieldInfo.name(), fieldTypeDescriptor);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
        }

        private void addGetter(ClassVisitor classWriter) {
            String methodName = getVirtualGetterName(fieldInfo.name());
            MethodVisitor mv;
            mv = classWriter.visitMethod(ACC_PUBLIC, methodName, "()" + fieldTypeDescriptor,
                    null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, Type.getInternalName(clazz), fieldInfo.name(), fieldTypeDescriptor);
            mv.visitInsn(Type.getType(fieldTypeDescriptor).getOpcode(IRETURN));
            mv.visitMaxs(0, 0);
        }
    }

    private static class OptaPlannerMethodEnhancingClassVisitor extends ClassVisitor {
        private final MethodInfo methodInfo;
        private final Class<?> clazz;
        private final String returnTypeDescriptor;
        private final Optional<MethodDescriptor> maybeSetter;
        private final String name;

        public OptaPlannerMethodEnhancingClassVisitor(ClassInfo classInfo, ClassVisitor outputClassVisitor,
                MethodInfo methodInfo, String name, Optional<MethodDescriptor> maybeSetter) {
            super(Gizmo.ASM_API_VERSION, outputClassVisitor);
            this.methodInfo = methodInfo;
            this.name = name;
            this.maybeSetter = maybeSetter;
            try {
                clazz = Class.forName(classInfo.name().toString(), false, Thread.currentThread().getContextClassLoader());
                returnTypeDescriptor = DescriptorUtils.typeToString(methodInfo.returnType());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            addGetter(this.cv);
            if (maybeSetter.isPresent()) {
                addSetter(this.cv);
            }
        }

        private void addGetter(ClassVisitor classWriter) {
            String methodName = getVirtualGetterName(name);
            MethodVisitor mv;
            mv = classWriter.visitMethod(ACC_PUBLIC, methodName, "()" + returnTypeDescriptor,
                    null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(clazz), methodInfo.name(),
                    "()" + returnTypeDescriptor, false);
            mv.visitInsn(Type.getType(returnTypeDescriptor).getOpcode(IRETURN));
            mv.visitMaxs(0, 0);
        }

        private void addSetter(ClassVisitor classWriter) {
            if (!maybeSetter.isPresent()) {
                return;
            }
            MethodDescriptor setter = maybeSetter.get();
            String methodName = getVirtualSetterName(name);
            MethodVisitor mv;
            mv = classWriter.visitMethod(ACC_PUBLIC, methodName, setter.getDescriptor(),
                    null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(clazz), setter.getName(),
                    setter.getDescriptor(), false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
        }
    }
}
