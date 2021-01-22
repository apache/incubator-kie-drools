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

package org.optaplanner.quarkus.gizmo.annotations;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.Function;

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
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

enum AllOptaPlannerAnnotationEnum {
    PLANNING_SCORE(PlanningScore.class, QuarkusRecordablePlanningScore::new),
    PLANNING_ENTITY_COLLECTION_PROPERTY(PlanningEntityCollectionProperty.class,
            QuarkusRecordablePlanningEntityCollectionProperty::new),
    PLANNING_ENTITY_PROPERTY(PlanningEntityProperty.class,
            QuarkusRecordablePlanningEntityProperty::new),
    PROBLEM_FACT_COLLECTION_PROPERTY(ProblemFactCollectionProperty.class,
            QuarkusRecordableProblemFactCollectionProperty::new),
    PROBLEM_FACT_PROPERTY(ProblemFactProperty.class, QuarkusRecordableProblemFactProperty::new),
    CONSTRAINT_CONFIGURATION_PROVIDER(ConstraintConfigurationProvider.class,
            QuarkusRecordableConstraintConfigurationProvider::new),
    CONSTRAINT_WEIGHT(ConstraintWeight.class, QuarkusRecordableConstraintWeight::new),
    PLANNING_PIN(PlanningPin.class, QuarkusRecordablePlanningPin::new),
    PLANNING_ID(PlanningId.class, QuarkusRecordablePlanningId::new),
    PLANNING_VARIABLE(PlanningVariable.class, QuarkusRecordablePlanningVariable::new),
    PLANNING_VARIABLE_REFERENCE(PlanningVariableReference.class, QuarkusRecordablePlanningVariableReference::new),
    VALUE_RANGE_PROVIDER(ValueRangeProvider.class, QuarkusRecordableValueRangeProvider::new),
    ANCHOR_SHADOW_VARIABLE(AnchorShadowVariable.class, QuarkusRecordableAnchorShadowVariable::new),
    CUSTOM_SHADOW_VARIABLE(CustomShadowVariable.class, QuarkusRecordableCustomShadowVariable::new),
    INVERSE_RELATION_SHADOW_VARIABLE(InverseRelationShadowVariable.class,
            QuarkusRecordableInverseRelationShadowVariable::new);

    Class<?> annotationClass;
    Function<Map<String, Object>, Annotation> mapper;

    @SuppressWarnings("unchecked")
    <T extends Annotation> AllOptaPlannerAnnotationEnum(Class<? extends T> annotationClass,
            Function<Map<String, Object>, T> mapper) {
        this.annotationClass = annotationClass;
        this.mapper = (Function<Map<String, Object>, Annotation>) mapper;
    }

    public Annotation get(Map<String, Object> values) {
        return mapper.apply(values);
    }

    public static Annotation getForClass(Class<? extends Annotation> annotationClass, Map<String, Object> values) {
        for (AllOptaPlannerAnnotationEnum annotationType : AllOptaPlannerAnnotationEnum.values()) {
            if (annotationClass.equals(annotationType.annotationClass)) {
                return annotationType.get(values);
            }
        }
        throw new IllegalArgumentException("Missing case for " + annotationClass);
    }

    public static boolean isOptaPlannerAnnotation(Class<? extends Annotation> annotationClass) {
        return isOptaPlannerAnnotation(annotationClass.getName());
    }

    public static boolean isOptaPlannerAnnotation(String annotationClass) {
        return annotationClass.startsWith("org.optaplanner.");
    }
}
