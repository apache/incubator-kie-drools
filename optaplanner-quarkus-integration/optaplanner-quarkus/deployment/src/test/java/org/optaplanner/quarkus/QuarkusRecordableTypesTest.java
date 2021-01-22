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

import java.util.Collections;
import java.util.function.Supplier;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.Type;
import org.jboss.jandex.WildcardType;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableAnnotations;
import org.optaplanner.quarkus.gizmo.types.QuarkusRecordableArrayType;
import org.optaplanner.quarkus.gizmo.types.QuarkusRecordableParameterizedType;
import org.optaplanner.quarkus.gizmo.types.QuarkusRecordableTypeVariable;
import org.optaplanner.quarkus.gizmo.types.QuarkusRecordableTypes;
import org.optaplanner.quarkus.gizmo.types.QuarkusRecordableWildcardType;
import org.optaplanner.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.quarkus.testdata.normal.domain.TestdataQuarkusSolution;

import io.quarkus.test.QuarkusUnitTest;

public class QuarkusRecordableTypesTest {
    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class));

    IndexView indexView;

    @BeforeEach
    public void setup() {
        indexView = Mockito.mock(IndexView.class);
    }

    @Test
    public void testPrimitiveTypeWrappers() {
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(PrimitiveType.BOOLEAN, indexView))
                .isEqualTo(boolean.class);
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(PrimitiveType.BYTE, indexView)).isEqualTo(byte.class);
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(PrimitiveType.CHAR, indexView)).isEqualTo(char.class);
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(PrimitiveType.SHORT, indexView))
                .isEqualTo(short.class);
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(PrimitiveType.INT, indexView)).isEqualTo(int.class);
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(PrimitiveType.LONG, indexView)).isEqualTo(long.class);
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(PrimitiveType.FLOAT, indexView))
                .isEqualTo(float.class);
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(PrimitiveType.DOUBLE, indexView))
                .isEqualTo(double.class);
    }

    @Test
    public void testArrayTypeWrappers() {
        QuarkusRecordableArrayType expected = new QuarkusRecordableArrayType();
        expected.setGenericComponentType(Integer.class);

        ArrayType arrayType = ArrayType.create(
                Type.create(DotName.createSimple(Integer.class.getName()), Type.Kind.CLASS), 1);
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(arrayType, indexView)).isEqualTo(expected);
    }

    @Test
    public void testVoidTypeWrapper() {
        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(Type.create(DotName.createSimple("java.lang.void"),
                Type.Kind.VOID), indexView)).isEqualTo(void.class);
    }

    @Test
    @Disabled("No way to create jandex TypeVariable, and cannot inject IndexView to fetch an existing one")
    public void testTypeVariableWrapper() {
        java.lang.reflect.Type[] bounds = { Supplier.class };
        QuarkusRecordableTypeVariable expected = new QuarkusRecordableTypeVariable();
        AnnotationInstance annotationInstance = AnnotationInstance.create(DotName.createSimple(PlanningScore.class.getName()),
                null,
                Collections.emptyList());
        expected.setName("MyTypeVariable");
        expected.setBounds(bounds);
        expected.setAnnotationList(Collections.singletonList(
                QuarkusRecordableAnnotations.getQuarkusRecorderFriendlyAnnotation(annotationInstance, indexView)));

        // TODO: Create a Jandex TypeVariable
    }

    @Test
    public void testWildcardTypeWrapper() {
        QuarkusRecordableWildcardType expected = new QuarkusRecordableWildcardType();
        expected.setLowerBounds(new java.lang.reflect.Type[] {});
        expected.setUpperBounds(new java.lang.reflect.Type[] { String.class });

        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(WildcardType.create(
                Type.create(DotName.createSimple("java.lang.String"),
                        Type.Kind.CLASS),
                true), indexView)).isEqualTo(expected);

        expected = new QuarkusRecordableWildcardType();
        expected.setLowerBounds(new java.lang.reflect.Type[] { String.class });
        expected.setUpperBounds(new java.lang.reflect.Type[] { Object.class });

        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(WildcardType.create(
                Type.create(DotName.createSimple("java.lang.String"),
                        Type.Kind.CLASS),
                false), indexView)).isEqualTo(expected);
    }

    @Test
    public void testParameterizedTypeWrapper() {
        QuarkusRecordableParameterizedType expected = new QuarkusRecordableParameterizedType();
        expected.setOwnerType(String.class);
        expected.setRawTypeName("java.util.Collection");
        expected.setActualTypeArguments(new java.lang.reflect.Type[] { String.class });

        assertThat(QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(ParameterizedType.create(
                DotName.createSimple("java.util.Collection"),
                new Type[] { Type.create(DotName.createSimple("java.lang.String"), Type.Kind.CLASS) },
                Type.create(DotName.createSimple("java.lang.String"), Type.Kind.CLASS)), indexView)).isEqualTo(expected);
    }
}
