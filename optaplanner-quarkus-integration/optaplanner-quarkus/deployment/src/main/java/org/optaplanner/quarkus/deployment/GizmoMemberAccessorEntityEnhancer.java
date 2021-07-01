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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.ObjectUtils;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.kogito.legacy.rules.KieRuntimeBuilder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberAccessorFactory;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberAccessorImplementor;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberDescriptor;
import org.optaplanner.core.impl.domain.solution.cloner.DeepCloningUtils;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionClonerFactory;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionClonerImplementor;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionOrEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.drools.KieRuntimeBuilderWrapper;
import org.optaplanner.quarkus.gizmo.OptaPlannerDroolsInitializer;
import org.optaplanner.quarkus.gizmo.OptaPlannerGizmoBeanFactory;

import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.recording.RecorderContext;
import io.quarkus.gizmo.BranchResult;
import io.quarkus.gizmo.BytecodeCreator;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.gizmo.DescriptorUtils;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.Gizmo;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;
import io.quarkus.runtime.RuntimeValue;

public class GizmoMemberAccessorEntityEnhancer {

    private final static String DROOLS_INITIALIZER_CLASS_NAME =
            OptaPlannerDroolsInitializer.class.getName() + "$Implementation";

    private static Set<Class<?>> visitedClasses = new HashSet<>();
    private static Set<Field> visitedFields = new HashSet<>();
    private static Set<MethodInfo> visitedMethods = new HashSet<>();

    public static void makeConstructorAccessible(Class<?> clazz, BuildProducer<BytecodeTransformerBuildItem> transformers) {
        try {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                return;
            }
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            if (!Modifier.isPublic(constructor.getModifiers()) && !visitedClasses.contains(clazz)) {
                transformers.produce(new BytecodeTransformerBuildItem(clazz.getName(),
                        (className, classVisitor) -> new OptaPlannerConstructorEnhancingClassVisitor(classVisitor)));
                visitedClasses.add(clazz);
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Class (" + clazz.getName() + ") must have a no-args constructor so it can be constructed by OptaPlanner.");
        }
    }

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
            BuildProducer<BytecodeTransformerBuildItem> transformers) {
        String generatedClassName = GizmoSolutionClonerFactory.getGeneratedClassName(solutionDescriptor);
        try (ClassCreator classCreator = ClassCreator
                .builder()
                .className(generatedClassName)
                .interfaces(SolutionCloner.class)
                .classOutput(classOutput)
                .setFinal(true)
                .build()) {

            Map<Class<?>, GizmoSolutionOrEntityDescriptor> memoizedGizmoSolutionOrEntityDescriptorForClassMap = new HashMap<>();

            List<Class<?>> solutionSubclassesList =
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
            solutionSubclassesList.add(solutionDescriptor.getSolutionClass());

            for (Class<?> solutionSubclass : solutionSubclassesList) {
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

            DeepCloningUtils deepCloningUtils = new DeepCloningUtils(solutionDescriptor);

            Set<Class<?>> solutionAndEntitySubclassSet = new HashSet<>(solutionSubclassesList);
            for (Object entityClassObject : solutionDescriptor.getEntityClassSet()) {
                Class<?> entityClass = (Class<?>) entityClassObject;
                Collection<ClassInfo> classInfoCollection =
                        indexView.getAllKnownSubclasses(DotName.createSimple(entityClass.getName()));
                classInfoCollection.stream().map(classInfo -> {
                    try {
                        return Class.forName(classInfo.name().toString(), false,
                                Thread.currentThread().getContextClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("Unable to find class (" + classInfo.name() +
                                "), which is a known subclass of the entity class (" +
                                entityClass + ").", e);
                    }
                }).forEach(solutionAndEntitySubclassSet::add);
            }
            Set<Class<?>> deepClonedClassSet = deepCloningUtils.getDeepClonedClasses(solutionAndEntitySubclassSet);

            for (Class<?> deepCloningClass : deepClonedClassSet) {
                makeConstructorAccessible(deepCloningClass, transformers);
                if (!memoizedGizmoSolutionOrEntityDescriptorForClassMap.containsKey(deepCloningClass)) {
                    getGizmoSolutionOrEntityDescriptorForEntity(solutionDescriptor,
                            deepCloningClass,
                            memoizedGizmoSolutionOrEntityDescriptorForClassMap,
                            transformers);
                }
            }

            GizmoSolutionClonerImplementor.defineClonerFor(classCreator, solutionDescriptor, solutionSubclassesList,
                    memoizedGizmoSolutionOrEntityDescriptorForClassMap, deepClonedClassSet);
        }

        return generatedClassName;
    }

    private static GizmoSolutionOrEntityDescriptor getGizmoSolutionOrEntityDescriptorForEntity(
            SolutionDescriptor solutionDescriptor,
            Class<?> entityClass,
            Map<Class<?>, GizmoSolutionOrEntityDescriptor> memoizedMap,
            BuildProducer<BytecodeTransformerBuildItem> transformers) {
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

    public static Map<String, RuntimeValue<MemberAccessor>> getGeneratedGizmoMemberAccessorMap(RecorderContext recorderContext,
            Set<String> generatedMemberAccessorsClassNames) {
        Map<String, RuntimeValue<MemberAccessor>> generatedGizmoMemberAccessorNameToInstanceMap = new HashMap<>();
        for (String className : generatedMemberAccessorsClassNames) {
            generatedGizmoMemberAccessorNameToInstanceMap.put(className, recorderContext.newInstance(className));
        }
        return generatedGizmoMemberAccessorNameToInstanceMap;
    }

    public static Map<String, RuntimeValue<SolutionCloner>> getGeneratedSolutionClonerMap(RecorderContext recorderContext,
            Set<String> generatedSolutionClonersClassNames) {
        Map<String, RuntimeValue<SolutionCloner>> generatedGizmoSolutionClonerNameToInstanceMap = new HashMap<>();
        for (String className : generatedSolutionClonersClassNames) {
            generatedGizmoSolutionClonerNameToInstanceMap.put(className, recorderContext.newInstance(className));
        }
        return generatedGizmoSolutionClonerNameToInstanceMap;
    }

    public static String generateGizmoBeanFactory(ClassOutput classOutput, Set<Class<?>> beanClasses,
            BuildProducer<BytecodeTransformerBuildItem> transformers) {
        String generatedClassName = OptaPlannerGizmoBeanFactory.class.getName() + "$Implementation";

        ClassCreator classCreator = ClassCreator
                .builder()
                .className(generatedClassName)
                .interfaces(OptaPlannerGizmoBeanFactory.class)
                .classOutput(classOutput)
                .build();

        classCreator.addAnnotation(ApplicationScoped.class);
        MethodCreator methodCreator = classCreator.getMethodCreator(MethodDescriptor.ofMethod(OptaPlannerGizmoBeanFactory.class,
                "newInstance", Object.class, Class.class));
        ResultHandle query = methodCreator.getMethodParam(0);
        BytecodeCreator currentBranch = methodCreator;

        for (Class<?> beanClass : beanClasses) {
            if (beanClass.isInterface() || Modifier.isAbstract(beanClass.getModifiers())) {
                continue;
            }
            makeConstructorAccessible(beanClass, transformers);
            ResultHandle beanClassHandle = currentBranch.loadClass(beanClass);
            ResultHandle isTarget = currentBranch.invokeVirtualMethod(
                    MethodDescriptor.ofMethod(Object.class, "equals", boolean.class, Object.class),
                    beanClassHandle, query);

            BranchResult isQueryBranchResult = currentBranch.ifTrue(isTarget);
            BytecodeCreator isQueryBranch = isQueryBranchResult.trueBranch();
            ResultHandle beanInstance =
                    isQueryBranch.newInstance(MethodDescriptor.ofConstructor(beanClass));
            isQueryBranch.returnValue(beanInstance);

            currentBranch = isQueryBranchResult.falseBranch();
        }
        currentBranch.returnValue(currentBranch.loadNull());

        classCreator.close();
        return generatedClassName;
    }

    public static String generateKieRuntimeBuilder(ClassOutput classOutput, SolverConfig config,
            BuildProducer<UnremovableBeanBuildItem> unremovableBeans,
            BuildProducer<BytecodeTransformerBuildItem> transformers) {
        String generatedClassName = DROOLS_INITIALIZER_CLASS_NAME;
        try (ClassCreator classCreator = ClassCreator
                .builder()
                .className(generatedClassName)
                .interfaces(OptaPlannerDroolsInitializer.class)
                .classOutput(classOutput)
                .build()) {

            classCreator.addAnnotation(ApplicationScoped.class);

            MethodCreator methodCreator;
            if (!ConfigUtils.isEmptyCollection(config.getScoreDirectorFactoryConfig().getScoreDrlList()) ||
                    !ConfigUtils.isEmptyCollection(config.getScoreDirectorFactoryConfig().getScoreDrlFileList())) {
                unremovableBeans.produce(UnremovableBeanBuildItem.beanTypes(KieRuntimeBuilder.class));
                methodCreator = classCreator.getMethodCreator(MethodDescriptor.ofMethod(OptaPlannerDroolsInitializer.class,
                        "setup", void.class, ScoreDirectorFactoryConfig.class));

                ResultHandle cdiResultHandle =
                        methodCreator.invokeStaticMethod(MethodDescriptor.ofMethod(CDI.class, "current", CDI.class));
                ResultHandle beanManagerResultHandle = methodCreator.invokeVirtualMethod(
                        MethodDescriptor.ofMethod(CDI.class, "getBeanManager", BeanManager.class),
                        cdiResultHandle);
                ResultHandle instanceResultHandle = methodCreator.invokeInterfaceMethod(
                        MethodDescriptor.ofMethod(BeanManager.class, "createInstance", Instance.class),
                        beanManagerResultHandle);
                ResultHandle kieRuntimeBuilderClass = methodCreator.loadClass(KieRuntimeBuilder.class);
                ResultHandle kieRuntimeBuilderInstanceResultHandle = methodCreator.invokeInterfaceMethod(
                        MethodDescriptor.ofMethod(Instance.class, "select", Instance.class, Class.class, Annotation[].class),
                        instanceResultHandle, kieRuntimeBuilderClass, methodCreator.newArray(Annotation.class, 0));
                ResultHandle kieRuntimeBuilder =
                        methodCreator.invokeInterfaceMethod(MethodDescriptor.ofMethod(Instance.class, "get", Object.class),
                                kieRuntimeBuilderInstanceResultHandle);
                ResultHandle kieBaseExtractor = methodCreator.newInstance(
                        MethodDescriptor.ofConstructor(KieRuntimeBuilderWrapper.class, KieRuntimeBuilder.class),
                        kieRuntimeBuilder);
                methodCreator.invokeVirtualMethod(
                        MethodDescriptor.ofMethod(ScoreDirectorFactoryConfig.class, "setGizmoKieRuntimeBuilderWrapper",
                                void.class,
                                KieRuntimeBuilderWrapper.class),
                        methodCreator.getMethodParam(0), kieBaseExtractor);

                // Workaround for https://issues.redhat.com/browse/KOGITO-5101
                transformers.produce(new BytecodeTransformerBuildItem(config.getSolutionClass().getName(),
                        (className, classVisitor) -> new OptaPlannerDroolsReactiveClassVisitor(config.getSolutionClass(),
                                classVisitor)));
                for (Class<?> entityClass : config.getEntityClassList()) {
                    transformers.produce(new BytecodeTransformerBuildItem(entityClass.getName(),
                            (className, classVisitor) -> new OptaPlannerDroolsReactiveClassVisitor(entityClass, classVisitor)));
                }
            } else {
                // No additional setup needed; Drools isn't used
                methodCreator = classCreator.getMethodCreator(MethodDescriptor.ofMethod(OptaPlannerDroolsInitializer.class,
                        "setup", void.class, ScoreDirectorFactoryConfig.class));
            }
            methodCreator.returnValue(null);
        }
        return generatedClassName;
    }

    public static RuntimeValue<OptaPlannerDroolsInitializer> getDroolsInitializer(RecorderContext recorderContext) {
        return recorderContext.newInstance(DROOLS_INITIALIZER_CLASS_NAME);
    }

    private static class OptaPlannerDroolsReactiveClassVisitor extends ClassVisitor {
        final Class<?> clazz;

        public OptaPlannerDroolsReactiveClassVisitor(Class<?> clazz, ClassVisitor outputClassVisitor) {
            super(Gizmo.ASM_API_VERSION, outputClassVisitor);
            this.clazz = clazz;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();

            if (clazz.getAnnotation(ClassReactive.class) == null && clazz.getAnnotation(PropertyReactive.class) == null) {
                this.cv.visitAnnotation(Type.getDescriptor(ClassReactive.class), true);
            }
        }

    }

    private static class OptaPlannerConstructorEnhancingClassVisitor extends ClassVisitor {
        public OptaPlannerConstructorEnhancingClassVisitor(ClassVisitor outputClassVisitor) {
            super(Gizmo.ASM_API_VERSION, outputClassVisitor);
        }

        @Override
        public MethodVisitor visitMethod(
                int access,
                String name,
                String desc,
                String signature,
                String[] exceptions) {
            if (name.equals("<init>")) {
                return cv.visitMethod(
                        ACC_PUBLIC,
                        name,
                        desc,
                        signature,
                        exceptions);
            }
            return cv.visitMethod(
                    access, name, desc, signature, exceptions);
        }
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
