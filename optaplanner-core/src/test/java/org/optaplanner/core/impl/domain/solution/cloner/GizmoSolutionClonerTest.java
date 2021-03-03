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
package org.optaplanner.core.impl.domain.solution.cloner;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberDescriptor;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionClonerFactory;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionClonerImplementor;
import org.optaplanner.core.impl.domain.solution.cloner.gizmo.GizmoSolutionOrEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedEntity;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedSolution;

import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodDescriptor;

public class GizmoSolutionClonerTest extends AbstractSolutionClonerTest {

    @Override
    protected <Solution_> SolutionCloner<Solution_> createSolutionCloner(SolutionDescriptor<Solution_> solutionDescriptor) {
        String className = GizmoSolutionClonerFactory.getGeneratedClassName(solutionDescriptor);
        final byte[][] classBytecodeHolder = new byte[1][];
        ClassOutput classOutput = (path, byteCode) -> {
            classBytecodeHolder[0] = byteCode;
        };
        ClassCreator classCreator = ClassCreator.builder()
                .className(className)
                .interfaces(SolutionCloner.class)
                .superClass(Object.class)
                .classOutput(classOutput)
                .setFinal(true)
                .build();

        Map<Class<?>, GizmoSolutionOrEntityDescriptor> memoizedSolutionOrEntityDescriptorMap = new HashMap<>();

        Stream.concat(Stream.of(solutionDescriptor.getSolutionClass()),
                solutionDescriptor.getEntityClassSet().stream()).forEach(clazz -> {
                    memoizedSolutionOrEntityDescriptorMap.put(clazz,
                            generateGizmoSolutionOrEntityDescriptor(solutionDescriptor, clazz));
                });

        GizmoSolutionClonerImplementor.defineClonerFor(classCreator, solutionDescriptor,
                Arrays.asList(solutionDescriptor.getSolutionClass()),
                memoizedSolutionOrEntityDescriptorMap);
        classCreator.close();
        final byte[] byteCode = classBytecodeHolder[0];

        ClassLoader gizmoClassLoader = new ClassLoader() {
            // getName() is an abstract method in Java 11 but not in Java 8
            public String getName() {
                return "OptaPlanner Gizmo SolutionCloner Test ClassLoader";
            }

            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException {
                if (className.equals(name)) {
                    return defineClass(name, byteCode, 0, byteCode.length);
                } else {
                    // Not a Gizmo generated class; load from context class loader
                    return Thread.currentThread().getContextClassLoader().loadClass(name);
                }
            }
        };

        try {
            return (SolutionCloner<Solution_>) gizmoClassLoader.loadClass(className).getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed creating generated Gizmo Class (" + className + ").", e);
        }
    }

    // HACK: use public getters/setters of fields so test domain can remain private
    // TODO: should this another DomainAccessType? DomainAcessType.GIZMO_RELAXED_ACCESS?
    private GizmoSolutionOrEntityDescriptor generateGizmoSolutionOrEntityDescriptor(SolutionDescriptor solutionDescriptor,
            Class<?> entityClass) {
        Map<Field, GizmoMemberDescriptor> solutionFieldToMemberDescriptor = new HashMap<>();
        Class<?> currentClass = entityClass;

        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    GizmoMemberDescriptor member;
                    Class<?> declaringClass = field.getDeclaringClass();
                    FieldDescriptor memberDescriptor = FieldDescriptor.of(field);
                    String name = field.getName();

                    if (Modifier.isPublic(field.getModifiers())) {
                        member = new GizmoMemberDescriptor(name, memberDescriptor, memberDescriptor, declaringClass);
                    } else {
                        Method getter = ReflectionHelper.getGetterMethod(currentClass, field.getName());
                        Method setter = ReflectionHelper.getSetterMethod(currentClass, field.getName());
                        if (getter != null && setter != null) {
                            MethodDescriptor getterDescriptor = MethodDescriptor.ofMethod(field.getDeclaringClass().getName(),
                                    getter.getName(),
                                    field.getType());
                            MethodDescriptor setterDescriptor = MethodDescriptor.ofMethod(field.getDeclaringClass().getName(),
                                    setter.getName(),
                                    setter.getReturnType(),
                                    field.getType());
                            member = new GizmoMemberDescriptor(name, getterDescriptor, memberDescriptor, declaringClass,
                                    setterDescriptor);
                        } else {
                            throw new IllegalStateException("Fail to generate GizmoMemberDescriptor for (" + name + "): " +
                                    "Field is not public and does not have both a getter and a setter.");
                        }
                    }
                    solutionFieldToMemberDescriptor.put(field, member);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        GizmoSolutionOrEntityDescriptor out =
                new GizmoSolutionOrEntityDescriptor(solutionDescriptor, entityClass, solutionFieldToMemberDescriptor);
        return out;
    }

    // This test verify a proper error message is thrown if an extended solution is passed.
    @Override
    @Test
    public void cloneExtendedSolution() {
        SolutionDescriptor solutionDescriptor = TestdataUnannotatedExtendedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataUnannotatedExtendedSolution> cloner = createSolutionCloner(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataUnannotatedExtendedEntity a = new TestdataUnannotatedExtendedEntity("a", val1, null);
        TestdataUnannotatedExtendedEntity b = new TestdataUnannotatedExtendedEntity("b", val1, "extraObjectOnEntity");
        TestdataUnannotatedExtendedEntity c = new TestdataUnannotatedExtendedEntity("c", val3);
        TestdataUnannotatedExtendedEntity d = new TestdataUnannotatedExtendedEntity("d", val3, c);
        c.setExtraObject(d);

        TestdataUnannotatedExtendedSolution original = new TestdataUnannotatedExtendedSolution("solution",
                "extraObjectOnSolution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);

        assertThatCode(() -> cloner.cloneSolution(original))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Failed to create clone: encountered (" + original.getClass() + ") which is not a known subclass of " +
                                "the solution class (" + TestdataSolution.class + "). The known subclasses are " +
                                "[" + TestdataSolution.class.getName() + "].\nMaybe use DomainAccessType.REFLECTION?");
    }
}
