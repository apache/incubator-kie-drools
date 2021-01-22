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

package org.optaplanner.quarkus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.util.Arrays;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Type;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.quarkus.gizmo.annotations.AbstractQuarkusRecordableAnnotation;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableAnchorShadowVariable;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableAnnotations;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableConstraintConfigurationProvider;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableConstraintWeight;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableCustomShadowVariable;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableInverseRelationShadowVariable;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordablePlanningEntityCollectionProperty;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordablePlanningEntityProperty;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordablePlanningId;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordablePlanningPin;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordablePlanningScore;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordablePlanningVariable;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordablePlanningVariableReference;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableProblemFactCollectionProperty;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableProblemFactProperty;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableValueRangeProvider;
import org.optaplanner.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

public class QuarkusRecordableAnnotationsTest {
    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class));

    IndexView indexView;

    // Cannot reuse DotNames here (different classloaders)
    static final DotName PLANNING_ENTITY_COLLECTION_PROPERTY =
            DotName.createSimple(PlanningEntityCollectionProperty.class.getName());
    static final DotName PLANNING_ENTITY_PROPERTY = DotName.createSimple(PlanningEntityProperty.class.getName());
    static final DotName PLANNING_SCORE = DotName.createSimple(PlanningScore.class.getName());
    static final DotName PROBLEM_FACT_COLLECTION_PROPERTY = DotName.createSimple(ProblemFactCollectionProperty.class.getName());
    static final DotName PROBLEM_FACT_PROPERTY = DotName.createSimple(ProblemFactProperty.class.getName());

    static final DotName CONSTRAINT_CONFIGURATION_PROVIDER =
            DotName.createSimple(ConstraintConfigurationProvider.class.getName());
    static final DotName CONSTRAINT_WEIGHT = DotName.createSimple(ConstraintWeight.class.getName());

    static final DotName PLANNING_PIN = DotName.createSimple(PlanningPin.class.getName());
    static final DotName PLANNING_ID = DotName.createSimple(PlanningId.class.getName());

    static final DotName PLANNING_VARIABLE = DotName.createSimple(PlanningVariable.class.getName());
    static final DotName VALUE_RANGE_PROVIDER = DotName.createSimple(ValueRangeProvider.class.getName());
    static final DotName PLANNING_VARIABLE_REFERENCE = DotName.createSimple(PlanningVariableReference.class.getName());

    static final DotName ANCHOR_SHADOW_VARIABLE = DotName.createSimple(AnchorShadowVariable.class.getName());
    static final DotName CUSTOM_SHADOW_VARIABLE = DotName.createSimple(CustomShadowVariable.class.getName());
    static final DotName INVERSE_RELATION_SHADOW_VARIABLE = DotName.createSimple(InverseRelationShadowVariable.class.getName());

    static final DotName[] GIZMO_MEMBER_ACCESSOR_ANNOTATIONS = {
            PLANNING_ENTITY_COLLECTION_PROPERTY,
            PLANNING_ENTITY_PROPERTY,
            PLANNING_SCORE,
            PROBLEM_FACT_COLLECTION_PROPERTY,
            PROBLEM_FACT_PROPERTY,
            CONSTRAINT_CONFIGURATION_PROVIDER,
            CONSTRAINT_WEIGHT,
            PLANNING_PIN,
            PLANNING_ID,
            PLANNING_VARIABLE,
            PLANNING_VARIABLE_REFERENCE,
            VALUE_RANGE_PROVIDER,
            ANCHOR_SHADOW_VARIABLE,
            CUSTOM_SHADOW_VARIABLE,
            INVERSE_RELATION_SHADOW_VARIABLE,
    };

    private ClassInfo createClassInfo(DotName annotationClass) {
        // Need to use deprecated methods cause ClassInfo is final class
        ClassInfo classInfo = ClassInfo.create(annotationClass,
                DotName.createSimple("java.lang.Object"),
                (short) 0,
                new DotName[] {},
                Collections.emptyMap(),
                false);
        try {
            // HACK: Use reflection to set method list as access is package-private for EVERYTHING in Jandex
            Method[] annotationParameters = Class.forName(annotationClass.toString()).getMethods();
            Class<?> methodInternalClass = Class.forName("org.jboss.jandex.MethodInternal");
            Object methodInternalArray = Array.newInstance(methodInternalClass, annotationParameters.length);
            Constructor<?> methodInternalCreator = methodInternalClass.getDeclaredConstructor(
                    byte[].class, byte[][].class, Type[].class, Type.class, short.class, Type.class, Type[].class,
                    Type[].class, AnnotationInstance[].class, AnnotationValue.class);
            methodInternalCreator.setAccessible(true);
            // byte[] name, byte[][] parameterNames, Type[] parameters, Type returnType, short flags,
            //                   Type receiverType, Type[] typeParameters, Type[] exceptions,
            //                   AnnotationInstance[] annotations, AnnotationValue defaultValue
            for (int i = 0; i < annotationParameters.length; i++) {
                if (annotationParameters[i].getDefaultValue() != null) {
                    ((Object[]) methodInternalArray)[i] = methodInternalCreator.newInstance(
                            annotationParameters[i].getName().getBytes(StandardCharsets.UTF_8),
                            new byte[][] {},
                            new Type[] {},
                            null, // return type
                            (short) 0,
                            null,
                            new Type[] {},
                            new Type[] {},
                            new AnnotationInstance[] {},
                            getAnnotationValue(annotationParameters[i].getName(), annotationParameters[i].getDefaultValue()));
                } else {
                    ((Object[]) methodInternalArray)[i] = methodInternalCreator.newInstance(
                            annotationParameters[i].getName().getBytes(StandardCharsets.UTF_8),
                            new byte[][] {},
                            new Type[] {},
                            null, // return type
                            (short) 0,
                            null,
                            new Type[] {},
                            new Type[] {},
                            new AnnotationInstance[] {},
                            null);
                }
            }
            Field methods = ClassInfo.class.getDeclaredField("methods");
            methods.setAccessible(true);
            methods.set(classInfo, methodInternalArray);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException
                | InvocationTargetException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        return classInfo;
    }

    private void mockGetClassByNameForAnnotation(DotName annotationClass) {
        Mockito.when(indexView.getClassByName(annotationClass))
                .thenReturn(createClassInfo(annotationClass));
    }

    @BeforeEach
    public void setup() {
        indexView = Mockito.mock(IndexView.class);
        for (DotName annotation : GIZMO_MEMBER_ACCESSOR_ANNOTATIONS) {
            mockGetClassByNameForAnnotation(annotation);
        }

    }

    private AnnotationInstance createAnnotationInstance(Annotation annotation) {
        List<AnnotationValue> annotationValuesList = new ArrayList<>();
        Set<String> ignoredMethods = new HashSet<>();
        ignoredMethods.add("toString");
        ignoredMethods.add("hashCode");
        ignoredMethods.add("annotationType");

        for (Method method : annotation.annotationType().getMethods()) {
            try {
                if (method.getParameterCount() == 0 && !ignoredMethods.contains(method.getName())) {
                    Object result = method.invoke(annotation);
                    if (result != null) {
                        annotationValuesList.add(getAnnotationValue(method.getName(), result));
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
        return AnnotationInstance.create(DotName.createSimple(annotation.annotationType().getName()), null,
                annotationValuesList);
    }

    private AnnotationValue getAnnotationValue(String name, Object value) {
        if (value instanceof Boolean) {
            return AnnotationValue.createBooleanValue(name, (boolean) value);
        } else if (value instanceof Byte) {
            return AnnotationValue.createByteValue(name, (byte) value);
        } else if (value instanceof Character) {
            return AnnotationValue.createCharacterValue(name, (char) value);
        } else if (value instanceof Short) {
            return AnnotationValue.createShortValue(name, (short) value);
        } else if (value instanceof Integer) {
            return AnnotationValue.createIntegerValue(name, (int) value);
        } else if (value instanceof Long) {
            return AnnotationValue.createLongValue(name, (long) value);
        } else if (value instanceof Float) {
            return AnnotationValue.createFloatValue(name, (float) value);
        } else if (value instanceof Double) {
            return AnnotationValue.createDoubleValue(name, (double) value);
        } else if (value instanceof String) {
            return AnnotationValue.createStringValue(name, (String) value);
        } else if (value instanceof Class) {
            return AnnotationValue.createClassValue(name,
                    Type.create(DotName.createSimple(((Class<?>) value).getName()), Type.Kind.CLASS));
        } else if (value instanceof Enum) {
            return AnnotationValue.createEnumValue(name, DotName.createSimple(value.getClass().getName()),
                    ((Enum<?>) value).name());
        } else if (value instanceof Annotation) {
            return AnnotationValue.createNestedAnnotationValue(name, createAnnotationInstance((Annotation) value));
        } else if (Arrays.isArray(value)) {
            AnnotationValue[] out = new AnnotationValue[Arrays.asList(value).size()];
            for (int i = 0; i < out.length; i++) {
                out[i] = getAnnotationValue(null, Arrays.asList(value).get(i));
            }
            return AnnotationValue.createArrayValue(name, out);
        } else {
            throw new IllegalStateException("Unrecongized class: " + value);
        }
    }

    private void assertConvertedAnnotationMatch(AbstractQuarkusRecordableAnnotation wrapper) {
        AnnotationInstance jandexAnnotation = createAnnotationInstance(wrapper);
        assertThat(QuarkusRecordableAnnotations.getQuarkusRecorderFriendlyAnnotation(jandexAnnotation, indexView))
                .isEqualTo(wrapper);
    }

    @Test
    public void testGetJavaObjectForJandexAnnotationValue() {
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("bool",
                        true),
                        null, indexView))
                                .isEqualTo(true);

        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("byte",
                        Byte.MAX_VALUE),
                        null, indexView))
                                .isEqualTo(Byte.MAX_VALUE);
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("char",
                        Character.MAX_VALUE),
                        null, indexView))
                                .isEqualTo(Character.MAX_VALUE);
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("short",
                        Short.MAX_VALUE),
                        null, indexView))
                                .isEqualTo(Short.MAX_VALUE);
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("int",
                        Integer.MAX_VALUE),
                        null, indexView))
                                .isEqualTo(Integer.MAX_VALUE);
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("long",
                        Long.MAX_VALUE),
                        null, indexView))
                                .isEqualTo(Long.MAX_VALUE);
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("float",
                        Float.MAX_VALUE),
                        null, indexView))
                                .isEqualTo(Float.MAX_VALUE);
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("double",
                        Double.MAX_VALUE),
                        null, indexView))
                                .isEqualTo(Double.MAX_VALUE);
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("string",
                        "String"),
                        null, indexView))
                                .isEqualTo("String");
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("enum",
                        PlanningVariableGraphType.CHAINED),
                        null, indexView))
                                .isEqualTo("CHAINED");
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("class",
                        Integer.class),
                        null, indexView))
                                .isEqualTo(Integer.class);

        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordablePlanningEntityCollectionProperty annotation =
                new QuarkusRecordablePlanningEntityCollectionProperty(annotationValues);
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("class",
                        annotation),
                        null, indexView))
                                .isEqualTo(annotation);

        // ****************************************
        // Now test arrays of the above
        // ****************************************
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("bool array",
                        new boolean[] { true }),
                        null, indexView))
                                .isEqualTo(new boolean[] { true });

        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("byte array",
                        new byte[] { Byte.MAX_VALUE }),
                        null, indexView))
                                .isEqualTo(new byte[] { Byte.MAX_VALUE });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("char array",
                        new char[] { Character.MAX_VALUE }),
                        null, indexView))
                                .isEqualTo(new char[] { Character.MAX_VALUE });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("short array",
                        new short[] { Short.MAX_VALUE }),
                        null, indexView))
                                .isEqualTo(new short[] { Short.MAX_VALUE });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("int array",
                        new int[] { Integer.MAX_VALUE }),
                        null, indexView))
                                .isEqualTo(new int[] { Integer.MAX_VALUE });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("long array",
                        new long[] { Long.MAX_VALUE }),
                        null, indexView))
                                .isEqualTo(new long[] { Long.MAX_VALUE });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("float array",
                        new float[] { Float.MAX_VALUE }),
                        null, indexView))
                                .isEqualTo(new float[] { Float.MAX_VALUE });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("double array",
                        new double[] { Double.MAX_VALUE }),
                        null, indexView))
                                .isEqualTo(new double[] { Double.MAX_VALUE });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("string array",
                        new String[] { "String" }),
                        null, indexView))
                                .isEqualTo(new String[] { "String" });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("enum array",
                        new PlanningVariableGraphType[] { PlanningVariableGraphType.CHAINED }),
                        null, indexView))
                                .isEqualTo(new String[] { "CHAINED" });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("class array",
                        new Class[] { Integer.class }),
                        null, indexView))
                                .isEqualTo(new Class[] { Integer.class });
        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("annotation array",
                        new Annotation[] { annotation }),
                        null, indexView))
                                .isEqualTo(new Annotation[] { annotation });

        assertThat(QuarkusRecordableAnnotations
                .getJavaObjectForJandexAnnotationValue(getAnnotationValue("unknown array",
                        new Object[] {}),
                        null, indexView))
                                .isEqualTo(new Object[] {});
    }

    @Test
    public void testPlanningEntityCollectionWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordablePlanningEntityCollectionProperty annotation =
                new QuarkusRecordablePlanningEntityCollectionProperty(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testPlanningEntityPropertyWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordablePlanningEntityProperty annotation = new QuarkusRecordablePlanningEntityProperty(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testPlanningScoreWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        annotationValues.put("bendableSoftLevelsSize", 1);
        annotationValues.put("bendableHardLevelsSize", 2);
        annotationValues.put("scoreDefinitionClass", ScoreDefinition.class);
        QuarkusRecordablePlanningScore annotation = new QuarkusRecordablePlanningScore(annotationValues);
        assertThat(annotation.bendableSoftLevelsSize()).isEqualTo(1);
        assertThat(annotation.bendableHardLevelsSize()).isEqualTo(2);
        assertThat(annotation.scoreDefinitionClass()).isEqualTo(ScoreDefinition.class);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testProblemFactCollectionPropertyWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordableProblemFactCollectionProperty annotation =
                new QuarkusRecordableProblemFactCollectionProperty(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testProblemFactPropertyWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordableProblemFactProperty annotation = new QuarkusRecordableProblemFactProperty(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testConstraintConfigurationProviderWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordableConstraintConfigurationProvider annotation =
                new QuarkusRecordableConstraintConfigurationProvider(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testConstraintWeightWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        annotationValues.put("constraintPackage", "org.optaplanner.constraints");
        QuarkusRecordableConstraintWeight annotation = new QuarkusRecordableConstraintWeight(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testPlanningPinWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordablePlanningPin annotation = new QuarkusRecordablePlanningPin(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testPlanningIdWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordablePlanningId annotation = new QuarkusRecordablePlanningId(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testPlanningVariableWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        annotationValues.put("nullable", false);
        annotationValues.put("graphType", PlanningVariableGraphType.NONE.name());
        annotationValues.put("strengthComparatorClass", PlanningVariable.NullStrengthComparator.class);
        annotationValues.put("strengthWeightFactoryClass", PlanningVariable.NullStrengthWeightFactory.class);
        annotationValues.put("valueRangeProviderRefs", new String[] { "valueRangeProvider" });
        QuarkusRecordablePlanningVariable annotation = new QuarkusRecordablePlanningVariable(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testValueRangeProviderWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordableValueRangeProvider annotation = new QuarkusRecordableValueRangeProvider(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testPlanningVariableReferenceWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        annotationValues.put("entityClass", TestdataQuarkusEntity.class);
        QuarkusRecordablePlanningVariableReference annotation =
                new QuarkusRecordablePlanningVariableReference(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testAnchorShadowVariableWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordableAnchorShadowVariable annotation = new QuarkusRecordableAnchorShadowVariable(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    @Disabled("Classloader issues with creating PlanningVariableReference array")
    public void testCustomShadowVariableWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        annotationValues.put("sources", new PlanningVariableReference[] {});
        annotationValues.put("variableListenerClass", CustomShadowVariable.NullVariableListener.class);
        annotationValues.put("variableListenerRef", new PlanningVariableReference[] {});
        QuarkusRecordableCustomShadowVariable annotation = new QuarkusRecordableCustomShadowVariable(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }

    @Test
    public void testInverseRelationShadowVariableWrapper() {
        Map<String, Object> annotationValues = new HashMap<>();
        QuarkusRecordableInverseRelationShadowVariable annotation =
                new QuarkusRecordableInverseRelationShadowVariable(annotationValues);
        assertConvertedAnnotationMatch(annotation);
    }
}
