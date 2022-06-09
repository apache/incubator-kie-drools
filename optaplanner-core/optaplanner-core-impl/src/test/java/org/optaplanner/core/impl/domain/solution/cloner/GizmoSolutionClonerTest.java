package org.optaplanner.core.impl.domain.solution.cloner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

class GizmoSolutionClonerTest extends AbstractSolutionClonerTest {

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

        DeepCloningUtils deepCloningUtils = new DeepCloningUtils(solutionDescriptor);
        Set<Class<?>> deepClonedClassSet = deepCloningUtils.getDeepClonedClasses(Collections.emptyList());
        Stream.concat(Stream.of(solutionDescriptor.getSolutionClass()),
                Stream.concat(solutionDescriptor.getEntityClassSet().stream(),
                        deepClonedClassSet.stream()))
                .forEach(clazz -> {
                    memoizedSolutionOrEntityDescriptorMap.put(clazz,
                            generateGizmoSolutionOrEntityDescriptor(solutionDescriptor, clazz));
                });

        GizmoSolutionClonerImplementor.defineClonerFor(classCreator, solutionDescriptor,
                Arrays.asList(solutionDescriptor.getSolutionClass()),
                memoizedSolutionOrEntityDescriptorMap, deepClonedClassSet);
        classCreator.close();
        final byte[] byteCode = classBytecodeHolder[0];

        ClassLoader gizmoClassLoader = new ClassLoader() {
            // getName() is an abstract method in Java 11 but not in Java 8
            @Override
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
    // TODO: should this be another DomainAccessType? DomainAccessType.GIZMO_RELAXED_ACCESS?
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
                            throw new IllegalStateException("Failed to generate GizmoMemberDescriptor for (" + name + "): " +
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

    private interface Animal {
    }

    private interface Robot {
    }

    private interface Zebra extends Animal {
    }

    private interface RobotZebra extends Zebra, Robot {
    }

    // This test verifies the instanceof comparator works correctly
    @Test
    void instanceOfComparatorTest() {
        Set<Class<?>> classSet = new HashSet<>(Arrays.asList(
                Animal.class,
                Robot.class,
                Zebra.class,
                RobotZebra.class));

        Comparator<Class<?>> comparator = GizmoSolutionClonerImplementor.getInstanceOfComparator(classSet);

        // assert that the comparator works on equality
        assertThat(comparator.compare(Animal.class, Animal.class)).isEqualTo(0);
        assertThat(comparator.compare(Robot.class, Robot.class)).isEqualTo(0);
        assertThat(comparator.compare(Zebra.class, Zebra.class)).isEqualTo(0);
        assertThat(comparator.compare(RobotZebra.class, RobotZebra.class)).isEqualTo(0);

        // Zebra < Animal and Robot
        // Since Animal and Robot are base classes (i.e. not subclasses of anything in the set)
        // and Zebra is a subclass of Animal
        assertThat(comparator.compare(Zebra.class, Animal.class)).isLessThan(0);
        assertThat(comparator.compare(Zebra.class, Robot.class)).isLessThan(0);
        assertThat(comparator.compare(Animal.class, Zebra.class)).isGreaterThan(0);
        assertThat(comparator.compare(Robot.class, Zebra.class)).isGreaterThan(0);

        // RobotZebra < Animal and Robot and Zebra
        assertThat(comparator.compare(RobotZebra.class, Animal.class)).isLessThan(0);
        assertThat(comparator.compare(RobotZebra.class, Robot.class)).isLessThan(0);
        assertThat(comparator.compare(RobotZebra.class, Zebra.class)).isLessThan(0);
        assertThat(comparator.compare(Animal.class, RobotZebra.class)).isGreaterThan(0);
        assertThat(comparator.compare(Robot.class, RobotZebra.class)).isGreaterThan(0);
        assertThat(comparator.compare(Zebra.class, RobotZebra.class)).isGreaterThan(0);
    }

    // This test verifies a proper error message is thrown if an extended solution is passed.
    @Override
    @Test
    void cloneExtendedSolution() {
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
