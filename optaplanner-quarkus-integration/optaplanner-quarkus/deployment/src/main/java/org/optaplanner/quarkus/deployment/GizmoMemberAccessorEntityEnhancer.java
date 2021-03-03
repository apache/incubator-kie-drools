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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.ObjectUtils;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberAccessorFactory;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberAccessorImplementor;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberDescriptor;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionClonerFactory;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionClonerImplementor;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionOrEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.quarkus.gizmo.OptaPlannerGizmoInitializer;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.gizmo.DescriptorUtils;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.Gizmo;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;

public class GizmoMemberAccessorEntityEnhancer {

    private static Set<Field> visitedFields = new HashSet<>();
    private static Set<MethodInfo> visitedMethods = new HashSet<>();

    public static void addVirtualFieldGetter(ClassInfo classInfo, FieldInfo fieldInfo,
            BuildProducer<BytecodeTransformerBuildItem> transformers) throws ClassNotFoundException, NoSuchFieldException {
        Class<?> clazz = Class.forName(classInfo.name().toString(), false,
                Thread.currentThread().getContextClassLoader());
        Field field = clazz.getDeclaredField(fieldInfo.name());
        addVirtualFieldGetter(clazz, field, transformers);
    }

    public static void addVirtualFieldGetter(Class<?> classInfo, Field fieldInfo,
            BuildProducer<BytecodeTransformerBuildItem> transformers) {
        if (!visitedFields.contains(fieldInfo)) {
            transformers.produce(new BytecodeTransformerBuildItem(classInfo.getName(),
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
                .ofMethod(classInfo.name().toString(), getVirtualSetterName(false, name),
                        md.getReturnType(), md.getParameterTypes()));
    }

    public static String getVirtualGetterName(boolean isField, String name) {
        return "$get$optaplanner$__" + ((isField) ? "field$__" : "method$__") + name;
    }

    public static String getVirtualSetterName(boolean isField, String name) {
        return "$set$optaplanner$__" + ((isField) ? "field$__" : "method$__") + name;
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
            FieldInfo fieldInfo, BuildProducer<BytecodeTransformerBuildItem> transformers)
            throws ClassNotFoundException, NoSuchFieldException {
        Class<?> declaringClass = Class.forName(fieldInfo.declaringClass().name().toString(), false,
                Thread.currentThread().getContextClassLoader());
        Field fieldMember = declaringClass.getDeclaredField(fieldInfo.name());
        String generatedClassName = GizmoMemberAccessorFactory.getGeneratedClassName(fieldMember);
        try (ClassCreator classCreator = ClassCreator
                .builder()
                .className(generatedClassName)
                .interfaces(MemberAccessor.class)
                .classOutput(classOutput)
                .setFinal(true)
                .build()) {

            GizmoMemberDescriptor member;

            FieldDescriptor memberDescriptor = FieldDescriptor.of(fieldInfo);
            String name = fieldInfo.name();

            if (Modifier.isPublic(fieldInfo.flags())) {
                member = new GizmoMemberDescriptor(name, memberDescriptor, memberDescriptor, declaringClass);
            } else {
                addVirtualFieldGetter(classInfo, fieldInfo, transformers);
                String methodName = getVirtualGetterName(true, fieldInfo.name());
                MethodDescriptor getterDescriptor = MethodDescriptor.ofMethod(fieldInfo.declaringClass().name().toString(),
                        methodName,
                        fieldInfo.type().name().toString());
                MethodDescriptor setterDescriptor = MethodDescriptor.ofMethod(fieldInfo.declaringClass().name().toString(),
                        getVirtualSetterName(true, fieldInfo.name()),
                        "void",
                        fieldInfo.type().name().toString());
                member = new GizmoMemberDescriptor(name, getterDescriptor, memberDescriptor, declaringClass, setterDescriptor);
            }
            GizmoMemberAccessorImplementor.defineAccessorFor(classCreator, member,
                    (Class<? extends Annotation>) Class.forName(annotationInstance.name().toString(), false,
                            Thread.currentThread().getContextClassLoader()));
        }
        return generatedClassName;
    }

    private static String getMemberName(Member member) {
        return ObjectUtils.defaultIfNull(ReflectionHelper.getGetterPropertyName(member),
                member.getName());
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
            MethodInfo methodInfo, BuildProducer<BytecodeTransformerBuildItem> transformers)
            throws ClassNotFoundException, NoSuchMethodException {
        Class<?> declaringClass = Class.forName(methodInfo.declaringClass().name().toString(), false,
                Thread.currentThread().getContextClassLoader());
        Method methodMember = declaringClass.getDeclaredMethod(methodInfo.name());
        String generatedClassName = GizmoMemberAccessorFactory.getGeneratedClassName(methodMember);
        try (ClassCreator classCreator = ClassCreator
                .builder()
                .className(generatedClassName)
                .interfaces(MemberAccessor.class)
                .classOutput(classOutput)
                .setFinal(true)
                .build()) {

            GizmoMemberDescriptor member;
            String name = getMemberName(methodMember);
            Optional<MethodDescriptor> setterDescriptor = getSetterDescriptor(classInfo, methodInfo, name);

            MethodDescriptor memberDescriptor = MethodDescriptor.of(methodInfo);

            if (Modifier.isPublic(methodInfo.flags())) {
                member = new GizmoMemberDescriptor(name, memberDescriptor, memberDescriptor, declaringClass,
                        setterDescriptor.orElse(null));
            } else {
                setterDescriptor = addVirtualMethodGetter(classInfo, methodInfo, name, setterDescriptor, transformers);
                String methodName = getVirtualGetterName(false, name);
                MethodDescriptor newMethodDescriptor =
                        MethodDescriptor.ofMethod(declaringClass, methodName, memberDescriptor.getReturnType());
                member = new GizmoMemberDescriptor(name, newMethodDescriptor, memberDescriptor, declaringClass,
                        setterDescriptor.orElse(null));
            }
            GizmoMemberAccessorImplementor.defineAccessorFor(classCreator, member,
                    (Class<? extends Annotation>) Class.forName(annotationInstance.name().toString(), false,
                            Thread.currentThread().getContextClassLoader()));
        }
        return generatedClassName;
    }

    public static String generateSolutionCloner(SolutionDescriptor solutionDescriptor,
            ClassOutput classOutput,
            IndexView indexView,
            BuildProducer<BytecodeTransformerBuildItem> transformers) throws ClassNotFoundException, NoSuchFieldException {
        String generatedClassName = GizmoSolutionClonerFactory.getGeneratedClassName(solutionDescriptor);
        try (ClassCreator classCreator = ClassCreator
                .builder()
                .className(generatedClassName)
                .interfaces(SolutionCloner.class)
                .classOutput(classOutput)
                .setFinal(true)
                .build()) {

            Map<Class<?>, GizmoSolutionOrEntityDescriptor> memoizedGizmoSolutionOrEntityDescriptorForClassMap = new HashMap<>();

            List<Class<?>> solutionSubclasses =
                    indexView.getAllKnownSubclasses(DotName.createSimple(solutionDescriptor.getSolutionClass().getName()))
                            .stream().map(classInfo -> {
                                try {
                                    return Class.forName(classInfo.name().toString(), false,
                                            Thread.currentThread().getContextClassLoader());
                                } catch (ClassNotFoundException e) {
                                    throw new IllegalStateException("Unable to find class (" + classInfo.name() +
                                            "), which is a known subclass of the solution class (" +
                                            solutionDescriptor.getSolutionClass() + ").", e);
                                }
                            }).collect(Collectors.toCollection(ArrayList::new));
            solutionSubclasses.add(solutionDescriptor.getSolutionClass());

            for (Class<?> solutionSubclass : solutionSubclasses) {
                getGizmoSolutionOrEntityDescriptorForEntity(solutionDescriptor,
                        solutionSubclass,
                        memoizedGizmoSolutionOrEntityDescriptorForClassMap,
                        transformers);
            }

            // IDEA gave error on entityClass being a Class...
            for (Object entityClass : solutionDescriptor.getEntityClassSet()) {
                getGizmoSolutionOrEntityDescriptorForEntity(solutionDescriptor,
                        (Class<?>) entityClass,
                        memoizedGizmoSolutionOrEntityDescriptorForClassMap,
                        transformers);
            }

            GizmoSolutionClonerImplementor.defineClonerFor(classCreator, solutionDescriptor, solutionSubclasses,
                    memoizedGizmoSolutionOrEntityDescriptorForClassMap);
        }

        return generatedClassName;
    }

    private static GizmoSolutionOrEntityDescriptor getGizmoSolutionOrEntityDescriptorForEntity(
            SolutionDescriptor solutionDescriptor,
            Class<?> entityClass,
            Map<Class<?>, GizmoSolutionOrEntityDescriptor> memoizedMap,
            BuildProducer<BytecodeTransformerBuildItem> transformers) throws NoSuchFieldException, ClassNotFoundException {
        Map<Field, GizmoMemberDescriptor> solutionFieldToMemberDescriptor = new HashMap<>();

        Class<?> currentClass = entityClass;
        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    GizmoMemberDescriptor member;
                    Class<?> declaringClass = field.getDeclaringClass();
                    FieldDescriptor memberDescriptor = FieldDescriptor.of(field);
                    String name = field.getName();

                    // Not being recorded, so can use Type and annotated element directly
                    if (Modifier.isPublic(field.getModifiers())) {
                        member = new GizmoMemberDescriptor(name, memberDescriptor, memberDescriptor, declaringClass);
                    } else {
                        addVirtualFieldGetter(declaringClass, field, transformers);
                        String methodName = getVirtualGetterName(true, field.getName());
                        MethodDescriptor getterDescriptor = MethodDescriptor.ofMethod(field.getDeclaringClass().getName(),
                                methodName,
                                field.getType());
                        MethodDescriptor setterDescriptor = MethodDescriptor.ofMethod(field.getDeclaringClass().getName(),
                                getVirtualSetterName(true, field.getName()),
                                "void",
                                field.getType());
                        member = new GizmoMemberDescriptor(name, getterDescriptor, memberDescriptor, declaringClass,
                                setterDescriptor);
                    }
                    solutionFieldToMemberDescriptor.put(field, member);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        GizmoSolutionOrEntityDescriptor out =
                new GizmoSolutionOrEntityDescriptor(solutionDescriptor, entityClass, solutionFieldToMemberDescriptor);
        memoizedMap.put(entityClass, out);
        return out;
    }

    public static String generateGizmoInitializer(ClassOutput classOutput, Set<String> generatedMemberAccessorsClassNames,
            Set<String> generatedSolutionClonersClassNames) {
        String generatedClassName = OptaPlannerGizmoInitializer.class.getName() + "$Implementation";
        ClassCreator classCreator = ClassCreator
                .builder()
                .className(generatedClassName)
                .interfaces(OptaPlannerGizmoInitializer.class)
                .classOutput(classOutput)
                .build();

        classCreator.addAnnotation(ApplicationScoped.class);
        MethodCreator methodCreator = classCreator.getMethodCreator(MethodDescriptor.ofMethod(OptaPlannerGizmoInitializer.class,
                "setup", void.class));
        ResultHandle memberAccessorMap = methodCreator.newInstance(MethodDescriptor.ofConstructor(HashMap.class));
        for (String generatedMemberAccessor : generatedMemberAccessorsClassNames) {
            ResultHandle generatedMemberAccessorResultHandle = methodCreator.load(generatedMemberAccessor);
            ResultHandle memberAccessorInstance =
                    methodCreator.newInstance(MethodDescriptor.ofConstructor(generatedMemberAccessor));
            methodCreator.invokeInterfaceMethod(
                    MethodDescriptor.ofMethod(Map.class, "put", Object.class, Object.class, Object.class),
                    memberAccessorMap, generatedMemberAccessorResultHandle, memberAccessorInstance);
        }
        methodCreator.invokeStaticMethod(
                MethodDescriptor.ofMethod(GizmoMemberAccessorFactory.class, "usePregeneratedMemberAccessorMap",
                        void.class, Map.class),
                memberAccessorMap);

        ResultHandle solutionClonerMap = methodCreator.newInstance(MethodDescriptor.ofConstructor(HashMap.class));
        for (String generatedSolutionCloner : generatedSolutionClonersClassNames) {
            ResultHandle generatedMemberAccessorResultHandle = methodCreator.load(generatedSolutionCloner);
            ResultHandle memberAccessorInstance =
                    methodCreator.newInstance(MethodDescriptor.ofConstructor(generatedSolutionCloner));
            methodCreator.invokeInterfaceMethod(
                    MethodDescriptor.ofMethod(Map.class, "put", Object.class, Object.class, Object.class),
                    solutionClonerMap, generatedMemberAccessorResultHandle, memberAccessorInstance);
        }

        methodCreator.invokeStaticMethod(
                MethodDescriptor.ofMethod(GizmoSolutionClonerFactory.class, "useSolutionClonerMap",
                        void.class, Map.class),
                solutionClonerMap);
        methodCreator.returnValue(null);

        classCreator.close();
        return generatedClassName;
    }

    private static class OptaPlannerFieldEnhancingClassVisitor extends ClassVisitor {
        private final Field fieldInfo;
        private final Class<?> clazz;
        private final String fieldTypeDescriptor;

        public OptaPlannerFieldEnhancingClassVisitor(Class<?> classInfo, ClassVisitor outputClassVisitor,
                Field fieldInfo) {
            super(Gizmo.ASM_API_VERSION, outputClassVisitor);
            this.fieldInfo = fieldInfo;
            clazz = classInfo;
            fieldTypeDescriptor = Type.getDescriptor(fieldInfo.getType());
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            addGetter(this.cv);
            addSetter(this.cv);
        }

        private void addSetter(ClassVisitor classWriter) {
            String methodName = getVirtualSetterName(true, fieldInfo.getName());
            MethodVisitor mv;
            mv = classWriter.visitMethod(ACC_PUBLIC, methodName, "(" + fieldTypeDescriptor + ")V",
                    null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(Type.getType(fieldTypeDescriptor).getOpcode(ILOAD), 1);
            mv.visitFieldInsn(PUTFIELD, Type.getInternalName(clazz), fieldInfo.getName(), fieldTypeDescriptor);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
        }

        private void addGetter(ClassVisitor classWriter) {
            String methodName = getVirtualGetterName(true, fieldInfo.getName());
            MethodVisitor mv;
            mv = classWriter.visitMethod(ACC_PUBLIC, methodName, "()" + fieldTypeDescriptor,
                    null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, Type.getInternalName(clazz), fieldInfo.getName(), fieldTypeDescriptor);
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
            String methodName = getVirtualGetterName(false, name);
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
            String methodName = getVirtualSetterName(false, name);
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
